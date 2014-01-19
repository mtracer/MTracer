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
import java.sql.Timestamp;
import org.apache.log4j.Logger;

import edu.berkeley.xtrace.XTraceException;


public class MT_TaskWriter extends MT_TableWriter{
	
	private static final Logger LOG = Logger.getLogger( MT_TaskWriter.class);
	
	private BlockingQueue<MT_TaskRecord> QTask;
	private String userName = "root";
	private String password = "root";
	private HashMap<String, MT_TaskRecord> cache;
	private int CACHE_MAX = 15;//10
	
	private int FAILED_COUNT_MAX = 100;
	private long QUEUE_BLOCKING_TIMEOUT = 10;//ms
	
	private Connection conn;
	
	public void setTaskQueue(BlockingQueue<MT_TaskRecord> QTask)
	{
		this.QTask = QTask;
	}
	
	public void initialize() throws XTraceException
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
		
		cache = new HashMap<String, MT_TaskRecord>(CACHE_MAX);
	}
	
	private PreparedStatement getTaskByTaskID;
	private PreparedStatement updateTask;
	private PreparedStatement insertTask;
	private void createPreparedStatements() throws SQLException {
		getTaskByTaskID = conn.prepareStatement("select * from Task where TaskID = ?");
		insertTask = conn.prepareStatement("insert into Task (TaskID, Title, NumReports, NumEdges, FirstSeen, LastUpdated, StartTime, EndTime) values (?, ?, ?, ?, ?, ?, ?, ?)");
		updateTask = conn.prepareStatement("update Task set Title = ?, FirstSeen = ?, LastUpdated = ?, NumReports = NumReports + ?, NumEdges = NumEdges + ?, StartTime = ?, EndTime = ? where TaskID = ?");
	}
	
	long timeWriteToCache = 0;//4
	int numWriteToCache = 0;//4
	long timeWriteToMySql = 0;//4
	int numWriteToMySql = 0;//4
	public void run() {
		LOG.info("MT_TaskWriter start running ");
		int failedCount = 0;
		while(true){
			MT_TaskRecord record;
			try{ record= QTask.poll(QUEUE_BLOCKING_TIMEOUT, TimeUnit.MILLISECONDS);}catch(InterruptedException e){record = null;}
			
			if(record != null){
				long st= System.nanoTime();//4
				writeToCache(record);
				long et= System.nanoTime();//4
				timeWriteToCache += et - st;//4
				numWriteToCache++;//4
			}else{
				failedCount++;
			}
			
			if(cache.size() >= CACHE_MAX){//write to DB when CACHE_MAX tasks prepared
				writeToMySQL();
			}
			
			if(failedCount >= FAILED_COUNT_MAX)//write to DB when no tasks received after QTASK_BLOCKING_TIMEOUT*FAILED_COUNT_MAX (1) seconds
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
		LOG.info("MT_TaskWriter stop running ");
		LOG.info("MT_TaskWriter.timeWriteToCache = " + timeWriteToCache);//4
		LOG.info("MT_TaskWriter.numWriteToCache = " + numWriteToCache);//4
		LOG.info("MT_TaskWriter.timeWriteToMySql = " + timeWriteToMySql);//4
		LOG.info("MT_TaskWriter.numWriteToMySql = " + numWriteToMySql);//4
	}
	
	private void writeToCache(MT_TaskRecord record){
		String id = record.getTaskID();
		MT_TaskRecord task = cache.get(id);
		if(task == null){
			cache.put(id, record);
		}else{
			task.combineRecords(record);
		}
	}
	
	private void writeToMySQL(){
		if(cache.size() == 0)
			return;
		
		long st= System.nanoTime();//4
		Iterator<Map.Entry<String, MT_TaskRecord>> iter = cache.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, MT_TaskRecord> entry = iter.next();
			MT_TaskRecord record = entry.getValue();
			write(record);
		}
		try{insertTask.executeBatch();}catch(SQLException e){LOG.warn("Exception when insertTask", e);}
		try{updateTask.executeBatch();}catch(SQLException e){LOG.warn("Exception when updateTask", e);}
		try{conn.commit();}catch(SQLException e){LOG.warn("Exception when commit", e);}
		
		cache.clear();
		long et= System.nanoTime();//4
		timeWriteToMySql += et - st;//4
		numWriteToMySql++;//4
	}
	
	private void write(MT_TaskRecord record){
		try{
			getTaskByTaskID.setString(1, record.getTaskID());
			ResultSet rs = getTaskByTaskID.executeQuery();
			if(!rs.next())//还没有记录
			{
				insertTask.setString(1, record.getTaskID());
				insertTask.setString(2, record.getTitle());
				insertTask.setInt(3, record.getNReport());
				insertTask.setInt(4, record.getNEdge());
				insertTask.setTimestamp(5, record.getFirstSeen());
				insertTask.setTimestamp(6, record.getLastUpdated());
				insertTask.setLong(7, record.getStartTime());
				insertTask.setLong(8, record.getEndTime());
				//insertTask.executeUpdate();
				insertTask.addBatch();
			}else{//已经有记录了，只要更新
				String title = rs.getString("Title");
				long start = rs.getLong("StartTime");
				long end = rs.getLong("EndTime");
				Timestamp first = rs.getTimestamp("FirstSeen");
				Timestamp last = rs.getTimestamp("LastUpdated");
				if(!record.getTitle().equals(record.getTaskID()))
					title = record.getTitle();
				if(record.getStartTime() != 0 && record.getEndTime() != Long.MAX_VALUE){
					start = record.getStartTime();
					end = record.getEndTime();
				}
				if(record.getFirstSeen().compareTo(first) < 0)
					first = record.getFirstSeen();
				if(record.getLastUpdated().compareTo(last) > 0)
					last = record.getLastUpdated();
				updateTask.setString(1,title);
				updateTask.setTimestamp(2, first);
				updateTask.setTimestamp(3, last);
				updateTask.setInt(4,record.getNReport());
				updateTask.setInt(5,record.getNEdge());
				updateTask.setLong(6, start);
				updateTask.setLong(7,end);
				updateTask.setString(8,record.getTaskID());
				//updateTask.executeUpdate();
				updateTask.addBatch();
			}
			rs.close();
		}catch(SQLException e){
			LOG.warn("Exception when write", e);
		}
	}
}
