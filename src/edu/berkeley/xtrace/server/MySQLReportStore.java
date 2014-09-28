package edu.berkeley.xtrace.server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import edu.berkeley.xtrace.TaskID;
import edu.berkeley.xtrace.XTraceException;
import edu.berkeley.xtrace.XTraceMetadata;
import edu.berkeley.xtrace.reporting.Report;

public class MySQLReportStore implements QueryableReportStore{
	private static final Logger LOG = Logger.getLogger(MySQLReportStore.class);
	
	private BlockingQueue<String> incomingReports;//待写入Report队列
	private boolean shouldOperate = false;
	private boolean databaseInitialized = false;
	
	private String userName = "root";//mysql用户名
	private String password = "root";//mysql密码
	
	private Connection conn;//产生的数据库连接
	
	/*用于写task表*/
	private PreparedStatement getTaskByTaskID;//根据TaskID查询Task表中的task
	private PreparedStatement updateTask;//更新Task表，主要是更新NumReports和LastUpdated字段
	private PreparedStatement insertTask;//往Task表中写入一条记录
	
	/*用于写Report和Edge表*/
	private PreparedStatement insertReport;//往Report表中写入一条记录
	private PreparedStatement insertEdge;//往Edge表中写入一条
	
	/*用于写Operation表*/
	private PreparedStatement getOperationByName;//根据OpName查询Operation表中的Operation
	private PreparedStatement updateOperation;//更新Operation表
	private PreparedStatement insertOperation;//往Operation表中写入一条记录
	
	/*用于查询*/
	private PreparedStatement totalNumReports;//总共的报文数,对应numReports()函数，用在http://localhost:8080/底部”xx reports"
	private PreparedStatement totalNumTasks;//总共的task数,对应numTasks()函数，用在http://localhost:8080/底部”xx tasks"
	private PreparedStatement totalNumEdges;
	private PreparedStatement lastTasks;//最近的task，对应getLatestTasks()函数，用于显示整个task列表
	private PreparedStatement getReportsByTaskID;//根据TaskID查找所有的report，对应getReportsByTask()函数，用在task列表中的Reports列
	private PreparedStatement getEdgesByTaskID;//根据TaskID查找所有的Edge，对应getReportsByTask()函数，用在task列表中的Reports列
	private PreparedStatement getTasksByTitle;//根据title查找task，对应getTasksByTitle()函数，用在task列表中的Trace Title列
	
	public void setReportQueue(BlockingQueue<String> q)
	{
		this.incomingReports = q;
	}
	
	public void initialize() throws XTraceException
	{
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (InstantiationException e) {
			throw new XTraceException(
					"Unable to instantiate mysql database", e);
		} catch (IllegalAccessException e) {
			throw new XTraceException(
					"Unable to access mysql database class", e);
		} catch (ClassNotFoundException e) {
			throw new XTraceException(
					"Unable to locate mysql database class", e);
		}
		try {
			try {
				// Connect to existing DB
				conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/xtrace" +
						"?user=" + this.userName + "&password=" + this.password +
                     "&useUnicode=true&characterEncoding=UTF-8");
			} catch (SQLException e) {//没有建立数据库xtrace
				createDatabase();
			}
			createTables();//创建表，如果已经存在就不用创建了
			conn.setAutoCommit(false);
			conn.commit();
			
		} catch (SQLException e) {
			throw new XTraceException("Unable to connect to mysql database: " + e.getSQLState(), e);
		}
		LOG.info("Successfully connected to the mysql database");
		try {
			createPreparedStatements();
		} catch (SQLException e) {
			throw new XTraceException("Unable to setup prepared statements", e);
		}
		
		databaseInitialized = true;
		shouldOperate = true;
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
                "&useUnicode=true&characterEncoding=UTF-8");
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
					+ "Num integer default 1 not null, "
					+ "MaxDelay BIGINT not null, "//ns级
					+ "MinDelay BIGINT not null, "
					+ "AverageDelay double not null)");
		}
		
		rs.close();
		createStatemenet.close();
		queryStatemenet.close();
	}
	private void createPreparedStatements() throws SQLException {
		getTaskByTaskID = conn.prepareStatement("select * from Task where TaskID = ?");
		updateTask = conn.prepareStatement("update Task set Title = ?, LastUpdated = current_timestamp, NumReports = NumReports + 1, NumEdges = NumEdges + ?, StartTime = ?, EndTime = ? where TaskID = ?");
		insertTask = conn.prepareStatement("insert into Task (TaskID, Title, NumEdges, LastUpdated, StartTime, EndTime) values (?, ?, ?, current_timestamp, ?, ?)");

		insertReport = conn.prepareStatement("insert into Report (TaskID, TId, OpName, StartTime, EndTime, HostAddress, HostName, Agent, Description) values (?, ?, ?, ?, ?, ?, ?, ?, ?)");
		insertEdge = conn.prepareStatement("insert into Edge (TaskID, FatherTID, FatherStartTime, ChildTID) values (?, ?, ?, ?)");
		
		getOperationByName = conn.prepareStatement("select * from Operation where OpName = ?");
		updateOperation = conn.prepareStatement("update Operation set Num = Num + 1, MaxDelay = ?, MinDelay = ?, AverageDelay = ? where OpName = ?");
		insertOperation = conn.prepareStatement("insert into Operation (OpName, MaxDelay, MinDelay, AverageDelay) values (?, ?, ?, ?)");
		
		totalNumReports = conn.prepareStatement("select sum(NumReports) as totalreports from Task");
		totalNumTasks = conn.prepareStatement("select count(distinct TaskID) as numtasks from Task");
		totalNumEdges = conn.prepareStatement("select sum(NumEdges) as totalEdges from Task");
		lastTasks = conn.prepareStatement("select * from Task order by LastUpdated desc");
		getReportsByTaskID = conn.prepareStatement("select * from Report where TaskID = ?");
		getEdgesByTaskID = conn.prepareStatement("select * from Edge where TaskID = ?");
		getTasksByTitle = conn.prepareStatement("select * from Task where upper(Title) = upper(?) order by LastUpdated desc");
	}
	
	int repNumToStore=0;//3
	int numRepStored=0;//3
	
	long timeReceiveReport=0;//3
	long timeExtractReport=0;//3
	long timeStoreRepToMysql=0;//3
	public void run() 
	{
		LOG.info("MySQLReportStore running ");
		
		while (true) {
			if (shouldOperate) {
				String msg;
				
				try {
					msg = incomingReports.take();
					
					repNumToStore++;//3
				} catch (InterruptedException e1) {
					continue;
				}
				long  st=System.nanoTime();//3
				try{
					receiveReport(msg);
					numRepStored++;//3
				}catch(Exception e){}
				long et= System.nanoTime();//3
				timeReceiveReport+=et-st;//3
			}
		}
	}
	public void shutdown()
	{
		LOG.info("Shutting down the FileTreeReportStore");//3
		LOG.info("ReportStore.repNumToStore = "+repNumToStore);//3
		LOG.info("ReportStore.numRepStored = "+ numRepStored);//3
		LOG.info("ReportStore.timeReceiveReport = "+timeReceiveReport);//3
		LOG.info("ReportStore.timeExtractReport = "+timeExtractReport);//3
		LOG.info("ReportStore.timeStoreRepToMysql = "+timeStoreRepToMysql);//3
		
		LOG.info("MySQLXTraceServer.maxQueueLength = "+MySQLXTraceServer.maxQueueLength);//3
		LOG.info("MySQLXTraceServer.countQueueLengthLT100 = "+MySQLXTraceServer.countQueueLengthLT100);//3
		LOG.info("MySQLXTraceServer.countQueueLengthLT500 = "+MySQLXTraceServer.countQueueLengthLT500);//3
		LOG.info("MySQLXTraceServer.countQueueLengthLT800 = "+MySQLXTraceServer.countQueueLengthLT800);//3
		LOG.info("MySQLXTraceServer.countQueueLengthLT1000 = "+MySQLXTraceServer.countQueueLengthLT1000);//3
		LOG.info("MySQLXTraceServer.countQueueLengthMT1000 = "+MySQLXTraceServer.countQueueLengthMT1000);//3
		LOG.info("MySQLXTraceServer.countQueueLengthEQ1024 = "+MySQLXTraceServer.countQueueLengthEQ1024);//3
		LOG.info("MySQLXTraceServer.countQueueIncrease = "+MySQLXTraceServer.countQueueIncrease);//3
		

		if (databaseInitialized) {
			try {
				conn.close();
			} catch (SQLException e) {
				if (!e.getSQLState().equals("08006")) {
					LOG.warn("Unable to shutdown mysql database", e);
				}
			}
			databaseInitialized = false;
		}
	}
	
	
	void receiveReport(String msg) {
		long  st=System.nanoTime();//3
		Report r = Report.createFromString(msg);
		String TaskID = r.get("TaskID") != null ? r.get("TaskID").get(0).toString() : "null";
		String TID = r.get("TID") != null ? r.get("TID").get(0).toString() : "null";
		String OpName = r.get("OpName") != null ? r.get("OpName").get(0).toString() : "null";
		long StartTime = r.get("StartTime") != null ? Long.parseLong(r.get("StartTime").get(0).toString()) : 0L;
		long EndTime = r.get("EndTime") != null ? Long.parseLong(r.get("EndTime").get(0).toString()) : 0L;
		String HostAddress = r.get("HostAddress") != null ? r.get("HostAddress").get(0).toString() : "null";
		String HostName =r.get("HostName") != null ? r.get("HostName").get(0).toString() : "null";
		String Agent = r.get("Agent") != null ? r.get("Agent").get(0).toString() : "null";
		String Description = r.get("Description") != null ? r.get("Description").get(0).toString() : "null";
		if(Description.length()>255)
			Description=Description.substring(0, 255);
		
		List<String> fid=r.get("FatherTID");
		List<String> ft=r.get("FatherStartTime");
		long  et=System.nanoTime();//3
		timeExtractReport+=et-st;//3
		
		try{
			long  st2=System.nanoTime();//3
			//写Task
			int nedge = 0;//是否要增加边的数量
			if(fid != null && ft != null)
				nedge = 1;
			getTaskByTaskID.setString(1, TaskID);
			ResultSet rs = getTaskByTaskID.executeQuery();
			List<String> t = r.get("Title");
			if(!rs.next())//还没有记录
			{
				String Title = t != null ? t.get(0).toString() : TaskID;//如果没有title信息，则将TaskID设为Title
				insertTask.setString(1, TaskID);
				insertTask.setString(2, Title);
				insertTask.setInt(3, nedge);
				/*modified*/
				/*insertTask.setLong(4, StartTime);
				long end = EndTime;
				if(EndTime == Long.MAX_VALUE)
					end = StartTime;
				*/
				
				long start=0;
				long end=Long.MAX_VALUE;
				if(Description.equals("A user task")){
					start=StartTime;
					end=EndTime;
				}
				insertTask.setLong(4, start);
				/*modified*/
				insertTask.setLong(5, end);
				insertTask.executeUpdate();
			}else{//已经有记录了，只要更新
				String Title = rs.getString("Title");
				if(t != null)
					Title = t.get(0).toString();
				long start = rs.getLong("StartTime");
				long end = rs.getLong("EndTime");
				/*modified*/
				/*if(StartTime < start)
					start = StartTime;
				if(EndTime > end && EndTime != Long.MAX_VALUE)
					end = EndTime;
					*/
				if(Description.equals("A user task")){
					start=StartTime;
					end=EndTime;
				}
				/*modified*/
				updateTask.setString(1,Title);
				updateTask.setInt(2,nedge);
				updateTask.setLong(3,start);
				updateTask.setLong(4,end);
				updateTask.setString(5,TaskID);
				updateTask.executeUpdate();
			}
			rs.close();
			
			//写Report
			insertReport.setString(1, TaskID);
			insertReport.setString(2, TID);
			insertReport.setString(3, OpName);
			insertReport.setLong(4, StartTime);
			insertReport.setLong(5, EndTime);
			insertReport.setString(6, HostAddress);
			insertReport.setString(7, HostName);
			insertReport.setString(8, Agent);
			insertReport.setString(9, Description);
			insertReport.executeUpdate();
			
			//写Edge
			if(fid!=null && ft!=null){
				String FatherTID = fid.get(0).toString();
				long FatherStartTime = Long.parseLong(ft.get(0).toString());
				
				insertEdge.setString(1, TaskID);
				insertEdge.setString(2, FatherTID);
				insertEdge.setLong(3, FatherStartTime);
				insertEdge.setString(4, TID);
				insertEdge.executeUpdate();
			}
			
			//写Operation
			/*getOperationByName;//根据OpName查询Operation表中的Operation
			updateOperation;//更新Operation表 MaxDelay = ?, MinDelay = ?, AverageDelay = ? where OpName = ?"
			insertOperation;//往Operation表中写入一条记录*/
			if(!Description.equals("A user task")){//不记录根节点
				getOperationByName.setString(1, OpName);
				rs = getOperationByName.executeQuery();
				if(!rs.next())//还没有记录
				{
					long delay = EndTime - StartTime;
					insertOperation.setString(1, OpName);
					insertOperation.setLong(2, delay);
					insertOperation.setLong(3, delay);
					insertOperation.setDouble(4, (double)delay);
					insertOperation.executeUpdate();
				}else{//已经有记录了，只要更新
					long MaxDelay = rs.getLong("Maxdelay");
					long MinDelay = rs.getLong("MinDelay");
					Double AverageDelay = rs.getDouble("AverageDelay");
					int numOp = rs.getInt("Num");
					long delay = EndTime - StartTime;
					updateOperation.setLong(1,delay > MaxDelay ? delay : MaxDelay);
					updateOperation.setLong(2,delay < MinDelay ? delay : MinDelay);
					updateOperation.setDouble(3, AverageDelay+(delay-AverageDelay)/(numOp+1));
					updateOperation.setString(4,OpName);
					updateOperation.executeUpdate();
				}
			}
			rs.close();
			
			conn.commit();
			long  et2=System.nanoTime();//3
			timeStoreRepToMysql+=et2-st2;//3
		} catch (SQLException e) {
			LOG.warn("Unable to update metadata about task "	+ TaskID.toString(), e);
		}
	}
	public void sync()
	{
		
	}

	public Iterator<Report> getReportsByTask(TaskID taskid) throws XTraceException
	{
		ArrayList<Report> lst = new ArrayList<Report>();
		try{
			getReportsByTaskID.setString(1, taskid.toString());
			ResultSet rs = getReportsByTaskID.executeQuery();
			ResultSetMetaData meta = rs.getMetaData();
			int column1 = meta.getColumnCount();
			while(rs.next())
			{
				Report r = new Report();
				for(int i =1;i<=column1; i++)
					r.put(meta.getColumnName(i), rs.getString(i));
				lst.add(r);
			}
			rs.close();
		}catch (SQLException e) {
			LOG.warn("mysql error", e);
		}
		return lst.iterator();
	}
	
	public Iterator<Report> getEdgesByTask(TaskID taskid) throws XTraceException
	{
		ArrayList<Report> lst = new ArrayList<Report>();
		try{
			getEdgesByTaskID.setString(1, taskid.toString());
			ResultSet rs = getEdgesByTaskID.executeQuery();
			ResultSetMetaData meta = rs.getMetaData();
			int column = meta.getColumnCount();
			while(rs.next())
			{
				Report r = new Report();
				for(int i =1;i<=column; i++)
					r.put(meta.getColumnName(i), rs.getString(i));
				lst.add(r);
			}
			rs.close();
		}catch (SQLException e) {
			LOG.warn("mysql error", e);
		}
		return lst.iterator();
	}
	
	public List<TaskRecord> getTasksSince(long milliSecondsSince1970,
			int offset, int limit) {
		return null;
	}
	
	public List<TaskRecord> getLatestTasks(int offset, int limit)
	{
		int numToFetch = offset + limit;
		List<TaskRecord> lst = new ArrayList<TaskRecord>();
		try {
			if (offset + limit + 1 < 0) {
				lastTasks.setMaxRows(Integer.MAX_VALUE);
			} else {
				lastTasks.setMaxRows(offset + limit + 1);
			}
			ResultSet rs = lastTasks.executeQuery();
			int i = 0;
			while (rs.next() && numToFetch > 0) {
				if (i >= offset && i < offset + limit)
					lst.add(readTaskRecord(rs));
				numToFetch -= 1;
				i++;
			}
			rs.close();
		} catch (SQLException e) {
			LOG.warn("mysql error", e);
		}
		return lst;
	}
	
	public List<TaskRecord> getTasksByTag(String tag, int offset, int limit)//没有tag了
	{
		return null;
	}
	
	public List<TaskRecord> getTasksByTitle(String title, int offset, int limit)
	{
		List<TaskRecord> lst = new ArrayList<TaskRecord>();
		try {
			if (offset + limit + 1 < 0) {
				getTasksByTitle.setMaxRows(Integer.MAX_VALUE);
			} else {
				getTasksByTitle.setMaxRows(offset + limit + 1);
			}
			getTasksByTitle.setString(1, title);
			lst = createRecordList(getTasksByTitle.executeQuery(), offset, limit);
		} catch (SQLException e) {
			LOG.warn("mysql error", e);
		}
		return lst;
	}

	public List<TaskRecord> getTasksByTitleSubstring(String title, int offset, int limit)
	{
		return null;
	}
	
	public List<TaskRecord> getTasksBySearch(Report report, Report display, int offset,int limit)
	{
		List<TaskRecord> lst = new ArrayList<TaskRecord>();
		if(report == null)
			return lst;
		
		List<String> titleList, timeFromList, timeToList, delayFromList, delayToList, taskIDList;
		List<String> numReportsFromList, numReportsToList, numEdgesFromList, numEdgesToList, sortValueList, sortStyleList;
		String title, timeFrom, timeTo, delayFrom, delayTo, taskID, numReportsFrom, numReportsTo, numEdgesFrom, numEdgesTo, sortValue, sortStyle;
		
		titleList = report.get("title");
		timeFromList= report.get("timeFrom");
		timeToList= report.get("timeTo");
		delayFromList= report.get("delayFrom");
		delayToList= report.get("delayTo");
		taskIDList= report.get("taskID");
		numReportsFromList= report.get("numReportsFrom");
		numReportsToList= report.get("numReportsTo");
		numEdgesFromList= report.get("numEdgesFrom");
		numEdgesToList= report.get("numEdgesTo");
		sortValueList= report.get("sortValue");
		sortStyleList= report.get("sortStyle");
		
		title = titleList == null ? "*" : titleList.get(0);
		timeFrom = timeFromList == null ? "*" : timeFromList.get(0);
		timeTo = timeToList == null ? "*" : timeToList.get(0);
		delayFrom = delayFromList == null ? "*" : delayFromList.get(0);
		delayTo = delayToList == null ? "*" : delayToList.get(0);
		taskID = taskIDList == null ? "*" : taskIDList.get(0);
		numReportsFrom = numReportsFromList == null ? "*" : numReportsFromList.get(0);
		numReportsTo = numReportsToList == null ? "*" : numReportsToList.get(0);
		numEdgesFrom = numEdgesFromList == null ? "*" : numEdgesFromList.get(0);
		numEdgesTo = numEdgesToList == null ? "*" : numEdgesToList.get(0);
		sortValue = sortValueList == null ? "*" : sortValueList.get(0);
		sortStyle = sortStyleList == null ? "*" : sortStyleList.get(0);
		
		String sql = "select * from Task where ";
		
		title = title.isEmpty() ? "*" : title;
		if(!title.equals("*")) sql = sql + "upper(Title) like upper('%" + title +"%') and "; 
		
		timeFrom = timeFrom.isEmpty() ? "*" : timeFrom;
		timeTo = timeTo.isEmpty() ? "*" : timeTo;
		//参考http://blog.csdn.net/lxcnn/article/details/4362500
		Pattern timePattem = Pattern.compile("(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]");
		Matcher matcher = timePattem.matcher(timeFrom);
		if (!matcher.find()) 
			timeFrom = "*";
		else
			timeFrom = matcher.group(0);
		matcher = timePattem.matcher(timeTo);
		if (!matcher.find()) 
			timeTo = "*";
		else
			timeTo = matcher.group(0);
		if(!timeFrom.equals("*") && !timeFrom.equals("1970-01-01 00:00:00"))
			sql = sql + "LastUpdated >= '" + timeFrom + "' and ";
		if(!timeTo.equals("*"))
			sql = sql + "FirstSeen <= '" + timeTo + "' and ";
		
		delayFrom = delayFrom.isEmpty() ? "*" : delayFrom;
		delayTo = delayTo.isEmpty() ? "*" : delayTo;
		Pattern floatPattem = Pattern.compile("(\\d*\\.)?\\d+");
		matcher = floatPattem.matcher(delayFrom);
		if (!matcher.find()) 
			delayFrom = "*";
		else
			delayFrom = matcher.group(0);
		matcher = floatPattem.matcher(delayTo);
		if (!matcher.find()) 
			delayTo = "*";
		else
			delayTo = matcher.group(0);
		if(!delayFrom.equals("*") && Double.parseDouble(delayFrom) > 0)
			sql = sql + "EndTime-StartTime >= " + delayFrom + "*1000000 and ";
		if(!delayTo.equals("*"))
			sql = sql + "EndTime-StartTime <= " + delayTo + "*1000000 and ";
		
		numReportsFrom = numReportsFrom.isEmpty() ? "*" : numReportsFrom;
		numReportsTo = numReportsTo.isEmpty() ? "*" : numReportsTo;
		Pattern intPattem = Pattern.compile("\\d+");
		matcher = intPattem.matcher(numReportsFrom);
		if (!matcher.find()) 
			numReportsFrom = "*";
		else
			numReportsFrom = matcher.group(0);
		matcher = intPattem.matcher(numReportsTo);
		if (!matcher.find()) 
			numReportsTo = "*";
		else
			numReportsTo = matcher.group(0);
		if(!numReportsFrom.equals("*") && Integer.parseInt(numReportsFrom) > 1)
			sql = sql + "NumReports >= " + numReportsFrom + " and ";
		if(!numReportsTo.equals("*"))
			sql = sql + "NumReports <= " + numReportsTo + " and ";
		
		numEdgesFrom = numEdgesFrom.isEmpty() ? "*" : numEdgesFrom;
		numEdgesTo = numEdgesTo.isEmpty() ? "*" : numEdgesTo;
		matcher = intPattem.matcher(numEdgesFrom);
		if (!matcher.find()) 
			numEdgesFrom = "*";
		else
			numEdgesFrom = matcher.group(0);
		matcher = intPattem.matcher(numEdgesTo);
		if (!matcher.find()) 
			numEdgesTo = "*";
		else
			numEdgesTo = matcher.group(0);
		if(!numEdgesFrom.equals("*") && Integer.parseInt(numEdgesFrom) > 1)
			sql = sql + "NumEdges >= " + numEdgesFrom + " and ";
		if(!numEdgesTo.equals("*"))
			sql = sql + "NumEdges <= " + numEdgesTo + " and ";
		
		taskID = taskID.isEmpty() ? "*" : taskID;
		if(!taskID.equals("*"))
			sql = sql + "TaskID like upper('%" + taskID +"%')";
		else
			sql = sql + "TaskID like '%' ";
		
		sortValue = sortValue.isEmpty() ? "*" : sortValue;
		sortStyle = sortStyle.isEmpty() ? "*" : sortStyle;
		if(sortValue.equals("taskid")){
			if(sortStyle.equals("descend"))
				sql = sql + "order by TaskID desc";
			else
				sql = sql + "order by TaskID asc";
		}else if(sortValue.equals("title")){
			if(sortStyle.equals("descend"))
				sql = sql + "order by Title desc";
			else
				sql = sql + "order by Title asc";
		}else if(sortValue.equals("edgeNum")){
			if(sortStyle.equals("descend"))
				sql = sql + "order by NumEdges desc";
			else
				sql = sql + "order by NumEdges asc";
		}else if(sortValue.equals("reportNum")){
			if(sortStyle.equals("descend"))
				sql = sql + "order by NumReports desc";
			else
				sql = sql + "order by NumReports asc";
		}else if(sortValue.equals("delay")){
			if(sortStyle.equals("descend"))
				sql = sql + "order by EndTime-StartTime desc";
			else
				sql = sql + "order by EndTime-StartTime asc";
		}else{
			if(sortStyle.equals("ascend"))
				sql = sql + "order by FirstSeen asc";
			else
				sql = sql + "order by FirstSeen desc";
		}
		
		String disStr = "Found ";
		PreparedStatement search;
		try {
			search = conn.prepareStatement(sql);
			if (offset + limit + 1 < 0) {
				search.setMaxRows(Integer.MAX_VALUE);
			} else {
				search.setMaxRows(offset + limit + 1);
			}
			ResultSet rs = search.executeQuery();
			rs.last(); 
			disStr = disStr + rs.getRow() + " results (" + sql +")";
			display.put("title", disStr);
			rs.beforeFirst();
			lst = createRecordList(rs, offset, limit);
		} catch (SQLException e) {
			LOG.warn("mysql error", e);
		}
		return lst;
	}
	
	public int numTasks()
	{
		int total = 0;
		try {
			ResultSet rs = totalNumTasks.executeQuery();
			rs.next();
			total = rs.getInt("numtasks");
			rs.close();
		} catch (SQLException e) {
			LOG.warn("mysql error", e);
		}
		return total;
	}
	
	public int numReports()
	{
		int total = 0;
		try {
			ResultSet rs = totalNumReports.executeQuery();
			rs.next();
			total = rs.getInt("totalreports");
			rs.close();
		} catch (SQLException e) {
			LOG.warn("mysql error", e);
		}
		return total;
	}
	
	public int numEdges()
	{
		int total = 0;
		try {
			ResultSet rs = totalNumEdges.executeQuery();
			rs.next();
			total = rs.getInt("totalEdges");
			rs.close();
		} catch (SQLException e) {
			LOG.warn("mysql error", e);
		}
		return total;
	}
	
	public long dataAsOf()
	{
		return 0L;
	}
	
	private TaskRecord readTaskRecord(ResultSet rs) throws SQLException {
		TaskID taskId = TaskID.createFromString(rs.getString("TaskID"));
		Date firstSeen = new Date(rs.getTimestamp("FirstSeen").getTime());
		Date lastUpdated = new Date(rs.getTimestamp("LastUpdated").getTime());
		String title = rs.getString("Title");
		int numReports = rs.getInt("NumReports");
		int numEdges = rs.getInt("NumEdges");
		long et = rs.getLong("EndTime");
		long st = rs.getLong("StartTime");
		double delay = (double)(et - st)/1000000;
		String tmp = String.format("%.2f",delay);
		return new TaskRecord(taskId, firstSeen, lastUpdated, numReports, numEdges, Double.parseDouble(tmp),
				title, null);
	}
	
	public List<TaskRecord> createRecordList(ResultSet rs, int offset, int limit)
			throws SQLException {
		List<TaskRecord> lst = new ArrayList<TaskRecord>();
		int i = 0;
		while (rs.next()) {
			if (i >= offset && i < offset + limit)
				lst.add(readTaskRecord(rs));
			i++;
		}
		rs.close();
		return lst;
	}

}
