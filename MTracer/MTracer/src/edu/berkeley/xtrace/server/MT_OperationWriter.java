package edu.berkeley.xtrace.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.log4j.Logger;

import edu.berkeley.xtrace.XTraceException;


public class MT_OperationWriter extends MT_TableWriter{
	
	private static final Logger LOG = Logger.getLogger(MT_OperationWriter.class);
	
	private BlockingQueue<MT_OperationRecord> QOperation;
	private String userName = "root";
	private String password = "root";
	private HashMap<String, MT_OperationRecord> cache;
	private int CACHE_MAX = 15;//10
	private int FAILED_COUNT_MAX = 100;
	private long QUEUE_BLOCKING_TIMEOUT = 10;//ms
	
	private Connection conn;
	
	public void setOperationQueue(BlockingQueue<MT_OperationRecord> QOperation)
	{
		this.QOperation = QOperation;
	}
	
	public void initialize()throws XTraceException
	{
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/xtrace" +
					"?user=" + this.userName + "&password=" + this.password +
                 "&useUnicode=true&rewriteBatchedStatements=true&characterEncoding=UTF-8");
			conn.setAutoCommit(false);
			conn.commit();
			
		} catch (SQLException e) {
			try{conn.close();}catch(SQLException e1){LOG.info("Error when closing conn");}
			throw new XTraceException("Error when connected to mysql",e);
		}
		LOG.info("Successfully connected to the mysql database"+"with CACHE_MAX = "+CACHE_MAX);
		try {
			createPreparedStatements();
		} catch (SQLException e) {
			try{conn.close();}catch(SQLException e1){LOG.info("Error when closing conn");}
			throw new XTraceException("Error when create PreparedStatement",e);
		}
		
		cache = new HashMap<String, MT_OperationRecord>(CACHE_MAX);
	}
	
	private PreparedStatement getOperationByName;
	private PreparedStatement updateOperation;
	private PreparedStatement insertOperation;
	private void createPreparedStatements() throws SQLException {
		getOperationByName = conn.prepareStatement("select * from Operation where OpName = ?");
		updateOperation = conn.prepareStatement("update Operation set Num = Num + ?, MaxDelay = ?, MinDelay = ?, AverageDelay = ? where OpName = ?");
		insertOperation = conn.prepareStatement("insert into Operation (OpName, Num, MaxDelay, MinDelay, AverageDelay) values (?, ?, ?, ?, ?)");
	}
	
	long timeWriteToCache = 0;//4
	int numWriteToCache = 0;//4
	long timeWriteToMySql = 0;//4
	int numWriteToMySql = 0;//4
	public void run() {
		//initialize();
		LOG.info("MT_OperationWriter start running ");
		int failedCount = 0;
		while(true){
			MT_OperationRecord record;
			try{ record= QOperation.poll(QUEUE_BLOCKING_TIMEOUT, TimeUnit.MILLISECONDS);}catch(InterruptedException e){record = null;}
			
			if(record != null){
				long st= System.nanoTime();//4
				writeToCache(record);
				long et= System.nanoTime();//4
				timeWriteToCache += et - st;//4
				numWriteToCache++;//4
			}else{
				failedCount++;
			}
			
			if(cache.size() >= CACHE_MAX){//write to DB when CACHE_MAX OPs prepared
				writeToMySQL();
			}
			
			if(failedCount >= FAILED_COUNT_MAX)//write to DB when no OPs received after QUEUE_BLOCKING_TIMEOUT*FAILED_COUNT_MAX (1) seconds
			{
				writeToMySQL();
				failedCount = 0;
			}
		}
	}
	
	public void shutdown(){
		try {
			writeToMySQL();
			conn.close();
		} catch (SQLException e) {
			if (!e.getSQLState().equals("08006")) {
				LOG.warn("Unable to shutdown mysql database", e);
			}
		}
		LOG.info("MT_OperationWriter stop running ");
		LOG.info("MT_OperationWriter.timeWriteToCache = " + timeWriteToCache);//4
		LOG.info("MT_OperationWriter.numWriteToCache = " + numWriteToCache);//4
		LOG.info("MT_OperationWriter.timeWriteToMySql = " + timeWriteToMySql);//4
		LOG.info("MT_OperationWriter.numWriteToMySql = " + numWriteToMySql);//4
	}
	
	private void writeToCache(MT_OperationRecord record){
		String name = record.getOpName();
		MT_OperationRecord op = cache.get(name);
		if(op == null){
			cache.put(name, record);
		}else{
			op.combineRecords(record);
		}
	}
	
	private void writeToMySQL(){
		if(cache.size() == 0)
			return;
		long st= System.nanoTime();//4
		Iterator<Map.Entry<String, MT_OperationRecord>> iter = cache.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, MT_OperationRecord> entry = iter.next();
			MT_OperationRecord record = entry.getValue();
			write(record);
		}
		try{insertOperation.executeBatch();}catch(SQLException e){LOG.warn("Exception when insertOperation", e);}
		try{updateOperation.executeBatch();}catch(SQLException e){LOG.warn("Exception when updateOperation", e);}
		try{conn.commit();}catch(SQLException e){LOG.warn("Exception when commit", e);}
		cache.clear();
		long et= System.nanoTime();//4
		timeWriteToMySql += et - st;//4
		numWriteToMySql++;//4
	}
	
	private void write(MT_OperationRecord record){
		try{
			getOperationByName.setString(1, record.getOpName());
			ResultSet rs = getOperationByName.executeQuery();
			if(!rs.next())
			{
				insertOperation.setString(1, record.getOpName());
				insertOperation.setLong(2, record.getNum());
				insertOperation.setLong(3, record.getMaxDelay());
				insertOperation.setLong(4, record.getMinDelay());
				insertOperation.setDouble(5, record.getAverageDelay());
				//insertOperation.executeUpdate();
				insertOperation.addBatch();
			}else{
				long MaxDelay = rs.getLong("Maxdelay");
				long MinDelay = rs.getLong("MinDelay");
				Double AverageDelay = rs.getDouble("AverageDelay");
				long Num = rs.getLong("Num");
				if(record.getMaxDelay() > MaxDelay)
					MaxDelay = record.getMaxDelay();
				if(record.getMinDelay() < MinDelay)
					MinDelay = record.getMinDelay();
				AverageDelay = (AverageDelay*Num+record.getAverageDelay()*record.getNum())/(Num+record.getNum());
				updateOperation.setLong(1,record.getNum());
				updateOperation.setLong(2,MaxDelay);
				updateOperation.setLong(3,MinDelay);
				updateOperation.setDouble(4, AverageDelay);
				updateOperation.setString(5, record.getOpName());
				//updateOperation.executeUpdate();
				updateOperation.addBatch();
			}
			rs.close();
		}catch(SQLException e){
			LOG.warn("Exception when write", e);
		}
	}
}
