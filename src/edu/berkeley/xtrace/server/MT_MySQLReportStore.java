package edu.berkeley.xtrace.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import edu.berkeley.xtrace.XTraceException;
import java.util.concurrent.Executor;



public class MT_MySQLReportStore {
	private static final Logger LOG = Logger.getLogger(MT_MySQLReportStore.class);
	
	private Connection conn;
	private String userName = "root";
	private String password = "root";
	
	public void initialize() throws XTraceException{
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (InstantiationException e) {
			throw new XTraceException("Unable to instantiate mysql database" + e);
		} catch (IllegalAccessException e) {
			throw new XTraceException("Unable to access mysql database class" + e);
		} catch (ClassNotFoundException e) {
			throw new XTraceException("Unable to locate mysql database class" + e);
		}
		try {
			try {
				conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/xtrace" +
						"?user=" + this.userName + "&password=" + this.password +
                     "&useUnicode=true&characterEncoding=UTF-8");
			} catch (SQLException e) {
				createDatabase();
			}
			createTables();
			conn.setAutoCommit(false);
			conn.commit();
			conn.close();
			
		} catch (SQLException e) {
			try{conn.close();}catch(SQLException e1){System.out.println("cann't close conn");}
			throw new XTraceException("Unable to connect to mysql database: " + e.getSQLState() + e);
		}
		LOG.info("mysql database prepared");
	}
	private void createDatabase() throws SQLException {
		LOG.info("create database xtrace");
		conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mysql" +
				"?user=" + this.userName + "&password=" + this.password +
             "&useUnicode=true&characterEncoding=UTF-8");
		Statement statement = conn.createStatement();
		statement.executeUpdate("CREATE DATABASE xtrace");
		statement.close();
		conn.close();
		conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/xtrace" +
				"?user=" + this.userName + "&password=" + this.password +
                "&useUnicode=true&rewriteBatchedStatements=true&characterEncoding=UTF-8");
	}
	private void createTables() throws SQLException {
		Statement queryStatemenet = conn.createStatement();//用于查询表是否存在
		Statement createStatemenet = conn.createStatement();//用来创建表
		ResultSet rs = queryStatemenet.executeQuery("select * from information_schema.TABLES " +
				"where table_schema ='xtrace' and table_name = 'Task';");//判断Task表是否存在
		if(!rs.next())//Task表不存在，则创建
		{
			LOG.info("create table Task");
			createStatemenet.executeUpdate("create table Task("//创建Task表
				+ "TaskID varchar(40) not null primary key, "
				+ "Title varchar(128), "
				+ "NumReports integer default 1 not null, "
				+ "NumEdges integer default 0 not null, "
				+ "FirstSeen timestamp default current_timestamp not null, "
				+ "LastUpdated timestamp default '0000-00-00 00:00:00' not null, "//mysql不能两个timestamp字段的默认值同时为CURRENT_TIMESTAMP 
				+ "StartTime BIGINT not null, "
				+ "EndTime BIGINT not null)");
			createStatemenet.executeUpdate("create index idx_tasks on Task(TaskID)");
			createStatemenet.executeUpdate("create index idx_title on Task(Title)");
			createStatemenet.executeUpdate("create index idx_firstseen on Task(FirstSeen)");
			createStatemenet.executeUpdate("create index idx_lastUpdated on Task(LastUpdated)");
			createStatemenet.executeUpdate("create index idx_startTime on Task(StartTime)");
			createStatemenet.executeUpdate("create index idx_endTime on Task(EndTime)");
		}
		
		rs = queryStatemenet.executeQuery("select * from information_schema.TABLES " +
				"where table_schema ='xtrace' and table_name = 'Report';");//判断Report表是否存在
		if(!rs.next())//Report表不存在，则创建
		{
			LOG.info("create table Report");
			createStatemenet.executeUpdate("create table Report("//创建Report表
				+ "TaskID varchar(40) not null, "
				+ "TID varchar(40) not null, "
				+ "OpName varchar(255) not null, "
				+ "StartTime BIGINT not null, "//记录ns，类型为long
				+ "EndTime BIGINT not null, "
				+ "HostAddress varchar(20) not null, "
				+ "HostName varchar(128) not null, "
				+ "Agent varchar(128), "
				+ "Description varchar(255))");
			createStatemenet.executeUpdate("create index idx_report_tasks on Task(TaskID)");
		}
		
		rs = queryStatemenet.executeQuery("select * from information_schema.TABLES " +
				"where table_schema ='xtrace' and table_name = 'Edge';");//判断Edge表是否存在
		if(!rs.next())//Edge表不存在，则创建
		{
			LOG.info("create table Edge");
			createStatemenet.executeUpdate("create table Edge("//创建Edge表
				+ "TaskID varchar(40) not null, "
				+ "FatherTID varchar(40) not null, "
				+ "FatherStartTime BIGINT not null, "//记录ns，类型为long
				+ "ChildTID varchar(40) not null)");
			createStatemenet.executeUpdate("create index idx_edge_tasks on Task(TaskID)");
		}
		
		rs = queryStatemenet.executeQuery("select * from information_schema.TABLES " +
				"where table_schema ='xtrace' and table_name = 'Operation';");//判断Operation表是否存在
		if(!rs.next())//Edge表不存在，则创建
		{
			LOG.info("create table Edge");
			createStatemenet.executeUpdate("create table Operation("//创建Operation表
					+ "OpName varchar(40) not null primary key, "
					+ "Num BIGINT default 1 not null, "
					+ "MaxDelay BIGINT not null, "//ns级
					+ "MinDelay BIGINT not null, "
					+ "AverageDelay double not null)");
		}
		
		rs.close();
		createStatemenet.close();
		queryStatemenet.close();
	}
	
	private MT_Extractor extractor;
	private MT_TaskWriter taskWriter;
	private MT_ReportWriter reportWriter;
	private MT_EdgeWriter edgeWriter;
	private MT_OperationWriter operationWriter;
	public void run(BlockingQueue<String> QIncome){
		newThreads();
		setQueues(QIncome);
		initThreads();
		runThreads();
		addShutdownHooks();
		LOG.info("MT_MySQLReportStore started");
	}
	
	private void newThreads(){
		extractor = null;
		taskWriter = null;
		reportWriter = null;
		edgeWriter = null;
		operationWriter = null;
		try {
			extractor = (MT_Extractor) Class.forName("edu.berkeley.xtrace.server.MT_Extractor").newInstance();
			taskWriter = (MT_TaskWriter) Class.forName("edu.berkeley.xtrace.server.MT_TaskWriter").newInstance();
			reportWriter = (MT_ReportWriter) Class.forName("edu.berkeley.xtrace.server.MT_ReportWriter").newInstance();
			edgeWriter = (MT_EdgeWriter) Class.forName("edu.berkeley.xtrace.server.MT_EdgeWriter").newInstance();
			operationWriter = (MT_OperationWriter) Class.forName("edu.berkeley.xtrace.server.MT_OperationWriter").newInstance();
		} catch (InstantiationException e) {
			LOG.fatal("Could not instantiate writer", e);
			System.exit(-1);
		} catch (IllegalAccessException e) {
			LOG.fatal("Could not access report writer", e);
			System.exit(-1);
		} catch (ClassNotFoundException e) {
			LOG.fatal("Could not find report writer", e);
			System.exit(-1);
		}
	}
	
	private void setQueues(BlockingQueue<String> QIncome){
		BlockingQueue<MT_TaskRecord> QTask = new ArrayBlockingQueue<MT_TaskRecord>(1024, true);
		BlockingQueue<MT_ReportRecord> QReport = new ArrayBlockingQueue<MT_ReportRecord>(1024, true);
		BlockingQueue<MT_EdgeRecord> QEdge = new ArrayBlockingQueue<MT_EdgeRecord>(1024, true);
		BlockingQueue<MT_OperationRecord> QOperation = new ArrayBlockingQueue<MT_OperationRecord>(1024, true);
		
		extractor.setIncomeQueue(QIncome);
		extractor.setTaskQueue(QTask);
		extractor.setReportQueue(QReport);
		extractor.setEdgeQueue(QEdge);
		extractor.setOperationQueue(QOperation);
		
		taskWriter.setTaskQueue(QTask);
		reportWriter.setReportQueue(QReport);
		edgeWriter.setEdgeQueue(QEdge);
		operationWriter.setOperationQueue(QOperation);
	}
	
	private void initThreads(){
		try{
			extractor.initialize();
			taskWriter.initialize();
			reportWriter.initialize();
			edgeWriter.initialize();
			operationWriter.initialize();
		}catch(XTraceException e){
			LOG.fatal("Could not access report writer", e);
			System.exit(-1);
		}
	}
	
	private static class ThreadPerTaskExecutor implements Executor {
	     public void execute(Runnable r) {
	         new Thread(r).start();
	     }
	}
	private ThreadPerTaskExecutor executor;
	private void runThreads(){
		executor = new ThreadPerTaskExecutor();
		executor.execute(extractor);
		executor.execute(taskWriter);
		executor.execute(reportWriter);
		executor.execute(edgeWriter);
		executor.execute(operationWriter);
	}
	
	private void addShutdownHooks(){
		Runtime.getRuntime().addShutdownHook(new Thread() {
			  public void run() {
				  extractor.shutdown();
				  taskWriter.shutdown();
				  reportWriter.shutdown();
				  edgeWriter.shutdown();
				  operationWriter.shutdown();
			  }
			});
	}
}



















