package edu.berkeley.xtrace.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;

import edu.berkeley.xtrace.XTraceException;


public class MT_EdgeWriter extends MT_TableWriter{
	
	private static final Logger LOG = Logger.getLogger(MT_EdgeWriter.class);
	
	private BlockingQueue<MT_EdgeRecord> QEdge;
	private String userName = "root";
	private String password = "root";
	private int CACHE_MAX = 15;//10
	private int FAILED_COUNT_MAX = 100;
	private long QUEUE_BLOCKING_TIMEOUT = 10;//ms
	
	private Connection conn;
	
	public void setEdgeQueue(BlockingQueue<MT_EdgeRecord> QEdge)
	{
		this.QEdge = QEdge;
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
			throw new XTraceException("Error when connected to mysql", e);
		}
		LOG.info("Successfully connected to the mysql database"+"with CACHE_MAX = "+CACHE_MAX);
		try {
			createPreparedStatements();
		} catch (SQLException e) {
			try{conn.close();}catch(SQLException e1){LOG.info("Error when closing conn");}
			throw new XTraceException("Error when create PreparedStatement",e);
		}
	}
	
	private PreparedStatement insertEdge;
	private void createPreparedStatements() throws SQLException {
		insertEdge = conn.prepareStatement("insert into Edge (TaskID, FatherTID, FatherStartTime, ChildTID) values (?, ?, ?, ?)");
	}
	
	long timeWriteToCache = 0;//4
	int numWriteToCache = 0;//4
	long timeWriteToMySql = 0;//4
	int numWriteToMySql = 0;//4
	public void run() {
		//initialize();
		LOG.info("MT_EdgeWriter start running ");
		int failedCount = 0;
		while(true){
			MT_EdgeRecord record;
			try{ record= QEdge.poll(QUEUE_BLOCKING_TIMEOUT, TimeUnit.MILLISECONDS);}catch(InterruptedException e){record = null;}
			
			if(record != null){
				long st= System.nanoTime();//4
				writeToCache(record);
				long et= System.nanoTime();//4
				timeWriteToCache += et - st;//4
				numWriteToCache++;//4
			}else{
				failedCount++;
			}
			
			if(cacheSize >= CACHE_MAX){//write to DB when CACHE_MAX edges prepared
				writeToMySQL();
			}
			
			if(failedCount >= FAILED_COUNT_MAX)//write to DB when no edges received after QUEUE_BLOCKING_TIMEOUT*FAILED_COUNT_MAX (1) seconds
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
		LOG.info("MT_EdgeWriter stop running ");
		LOG.info("MT_EdgeWriter.timeWriteToCache = " + timeWriteToCache);//4
		LOG.info("MT_EdgeWriter.numWriteToCache = " + numWriteToCache);//4
		LOG.info("MT_EdgeWriter.timeWriteToMySql = " + timeWriteToMySql);//4
		LOG.info("MT_EdgeWriter.numWriteToMySql = " + numWriteToMySql);//4
	}
	
	private int cacheSize = 0;
	private void writeToCache(MT_EdgeRecord record){
		write(record);
		cacheSize++;
	}
	private void writeToMySQL(){
		if(cacheSize == 0)
			return;
		long st= System.nanoTime();//4
		try{
			insertEdge.executeBatch();
			conn.commit();
		}catch(SQLException e){
			LOG.warn("Exception when commit: ", e);
		}
		cacheSize=0;
		long et= System.nanoTime();//4
		timeWriteToMySql += et - st;//4
		numWriteToMySql++;//4
	}
	
	private void write(MT_EdgeRecord record){
		try{
			insertEdge.setString(1, record.getTaskID());
			insertEdge.setString(2, record.getFatherTID());
			insertEdge.setLong(3, record.getFatherStartTime());
			insertEdge.setString(4, record.getChildTID());
			//insertEdge.executeUpdate();
			insertEdge.addBatch();
		}catch(SQLException e){
			LOG.warn("Exception when writing: ", e);
		}
	}
}
