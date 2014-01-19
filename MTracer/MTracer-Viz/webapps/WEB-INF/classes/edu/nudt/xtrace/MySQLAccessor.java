package edu.nudt.xtrace;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MySQLAccessor{
	private boolean databaseInitialized = false;
	
	private String userName = "root";//mysql用户名
	private String password = "root";//mysql密码
	
	public void setUserName(String name)
	{
		userName = name;
	}
	public void setPassword(String pw)
	{
		password = pw;
	}
	public String test()
	{return "hello world";}
	private Connection conn;//产生的数据库连接
	public Connection getConn(){return conn;}
	/*用于查询*/
	private PreparedStatement totalNumReports;//总共的报文数,对应numReports()函数
	private PreparedStatement totalNumTasks;//总共的task数,对应numTasks()函数
	private PreparedStatement totalNumEdges;
	private PreparedStatement totalNumOperations;//总共的operations
	private PreparedStatement lastTasks;//最近的task，对应getLatestTasks()函数
	private PreparedStatement getReportsByTaskID;//根据TaskID查找所有的report，对应getReportsByTask()函数
	private PreparedStatement getEdgesByTaskID;//根据TaskID查找所有的Edge，对应getReportsByTask()函数
	private PreparedStatement getTasksByTitle;//根据title查找task，对应getTasksByTitle()函数
	
	public MySQLAccessor() throws Exception
	{
		try{
			initialize();
		}catch(Exception e){
			throw new Exception(e);
		}
	}
	public MySQLAccessor(Connection conn) throws Exception
	{
		this.conn = conn;
		try{
			createPreparedStatements();
		}catch(Exception e){
			throw new Exception("Unable to setup prepared statements", e);
		}
	}
	public void initialize() throws Exception
	{
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (InstantiationException e) {
			throw new Exception(
					"Unable to instantiate mysql database", e);
		} catch (IllegalAccessException e) {
			throw new Exception(
					"Unable to access mysql database class", e);
		} catch (ClassNotFoundException e) {
			throw new Exception(
					"Unable to locate mysql database class", e);
		}
		try {
			// Connect to existing DB
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/xtrace" +
						"?user=" + this.userName + "&password=" + this.password +
                     "&useUnicode=true&characterEncoding=UTF-8");
			conn.setAutoCommit(false);
			conn.commit();
		} catch (SQLException e) {
			throw new Exception("Unable to connect to mysql database: " + e.getSQLState(), e);
		}
		try {
			createPreparedStatements();
		} catch (SQLException e) {
			throw new Exception("Unable to setup prepared statements", e);
		}
		
		databaseInitialized = true;
	}
	private void createPreparedStatements() throws SQLException {	
		totalNumReports = conn.prepareStatement("select sum(NumReports) as totalreports from Task");
		totalNumTasks = conn.prepareStatement("select count(distinct TaskID) as numtasks from Task");
		totalNumEdges = conn.prepareStatement("select sum(NumEdges) as totalEdges from Task");
		totalNumOperations = conn.prepareStatement("select count(OpName) as totalOperations from Operation");
		lastTasks = conn.prepareStatement("select * from Task order by LastUpdated desc");
		getReportsByTaskID = conn.prepareStatement("select * from Report where TaskID = ?");
		getEdgesByTaskID = conn.prepareStatement("select * from Edge where TaskID = ?");
		getTasksByTitle = conn.prepareStatement("select * from Task where upper(Title) = upper(?) order by LastUpdated desc");
	}
	public void shutdown()
	{
		if (databaseInitialized) {
			try {
				conn.close();
			} catch (SQLException e) {
			}
			databaseInitialized = false;
		}
	}
	protected void finalize()
    {
		if (databaseInitialized) {
			try {
				conn.close();
			} catch (SQLException e) {
			}
			databaseInitialized = false;
		}
     }
	public Iterator<Report> getReportsByTask(String taskid) throws Exception
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
		}
		return lst.iterator();
	}
	
	public Iterator<Report> getEdgesByTask(String taskid) throws Exception
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
		}
		return lst.iterator();
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
		}
		return lst;
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
		}
		return lst;
	}

	public List<Report> getTasksByOp(Report condition, Report display, int offset,int limit)
	{
		List<Report> lst = new ArrayList<Report>();

		List<String> opNameList, delayFromList, delayToList, addressList, nameList, agentList, timeFromList, timeToList,titleList, taskIDList, sortValueList, sortStyleList;
		String opName, delayFrom, delayTo, address,name,agent,timeFrom, timeTo,title,taskID,sortValue, sortStyle;
		opNameList = condition.get("opName");
		delayFromList = condition.get("delayFrom");
		delayToList = condition.get("delayTo");
		addressList = condition.get("address");
		nameList = condition.get("name");
		agentList = condition.get("agent");
		timeFromList= condition.get("timeFrom");
		timeToList= condition.get("timeTo");
		titleList = condition.get("title");
		taskIDList= condition.get("taskID");
		sortValueList= condition.get("sortValue");
		sortStyleList= condition.get("sortStyle");

		opName = opNameList == null ? "*" : opNameList.get(0);
		delayFrom = delayFromList==null ? "*" : delayFromList.get(0);
		delayTo = delayToList==null ? "*" : delayToList.get(0);
		address = addressList==null ? "*" : addressList.get(0);
		name = nameList==null ? "*" : nameList.get(0);
		agent = agentList==null ? "*" : agentList.get(0);
		timeFrom = timeFromList == null ? "*" : timeFromList.get(0);
		timeTo = timeToList == null ? "*" : timeToList.get(0);
		title = titleList == null ? "*" : titleList.get(0);
		taskID = taskIDList == null ? "*" : taskIDList.get(0);
		sortValue = sortValueList == null ? "Delay" : sortValueList.get(0);
		sortStyle = sortStyleList == null ? "ascend" : sortStyleList.get(0);

		String sql="select Report.TaskID as TaskID,Report.OpName as OpName,round((Report.EndTime-Report.StartTime)/1000000,2) as Delay,Report.HostAddress as HostAddress,Report.HostName as HostName, ";
		sql += "Report.Agent as Agent, Task.Title as Title, Task.NumReports as NumReports, Task.NumEdges as NumEdges, Task.FirstSeen as FirstSeen, Task.LastUpdated as LastUpdated, ";
		sql += "round((Task.EndTime-Task.StartTime)/1000000,2) as TaskDelay from Report, Task ";
		sql += "where Report.TaskID=Task.TaskID and ";
		
		//delay
		delayFrom = delayFrom.isEmpty() ? "*" : delayFrom;
		delayTo = delayTo.isEmpty() ? "*" : delayTo;
		Pattern floatPattem = Pattern.compile("(\\d*\\.)?\\d+");
		Matcher matcher = floatPattem.matcher(delayFrom);
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
			sql = sql + "round((Report.EndTime-Report.StartTime)/1000000,2) >= " + delayFrom +" and ";
		if(!delayTo.equals("*") && Double.parseDouble(delayTo) > 0)
			sql = sql + "round((Report.EndTime-Report.StartTime)/1000000,2) <= " + delayTo +" and ";

		//hostAddress
		address = address.replaceAll(" ","");
		address = address.isEmpty() ? "*" : address;
		if(!address.equals("*"))
		{
			String tmp = formatCondition("Report.HostAddress",address);
			if(!tmp.isEmpty())
				sql += " ("+ tmp +") and ";
		}

		//hostName
		name = name.isEmpty() ? "*" : name;
		if(!name.equals("*"))
		{
			String tmp = formatCondition("Report.HostName",name);
			if(!tmp.isEmpty())
				sql += " ("+ tmp +") and ";
		}

		//agent
		agent = agent.isEmpty() ? "*" : agent;
		if(!agent.equals("*"))
		{
			String tmp = formatCondition("Report.Agent",agent);
			if(!tmp.isEmpty())
				sql += " ("+ tmp +") and ";
		}

		//time
		timeFrom = timeFrom.isEmpty() ? "*" : timeFrom;
		timeTo = timeTo.isEmpty() ? "*" : timeTo;
		//参考http://blog.csdn.net/lxcnn/article/details/4362500
		Pattern timePattem = Pattern.compile("(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]");
		matcher = timePattem.matcher(timeFrom);
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
			sql = sql + "Task.LastUpdated >= '" + timeFrom + "' and ";
		if(!timeTo.equals("*"))
			sql = sql + "Task.FirstSeen <= '" + timeTo + "' and ";

		//title
		title = title.isEmpty() ? "*" : title;
		if(!title.equals("*"))
		{
			String tmp = formatCondition("Task.Title",title);
			if(!tmp.isEmpty())
				sql += " ("+ tmp +") and ";
		}

		//taskID
		taskID = taskID.replaceAll(" ","");
		taskID = taskID.isEmpty() ? "*" : taskID;
		if(!taskID.equals("*"))
		{
			String tmp = formatCondition("Task.TaskID",taskID);
			if(!tmp.isEmpty())
				sql += " ("+ tmp +") and ";
		}
		
		//description
		sql += "Report.Description != 'A user task' and ";

		//opName
		opName = opName.isEmpty() ? "*" : opName;
		if(opName.equals("*"))
			sql = sql + "Report.OpName like '%' ";
		else{
			String tmp = formatCondition("Report.OpName",opName);
			if(tmp.isEmpty())
				sql = sql + "Report.OpName like '%' ";
			else
				sql += " ("+ tmp +") ";
		}

		//sort rule
		if(sortValue.equals("OpName")||sortValue.equals("Delay")||sortValue.equals("HostName")||sortValue.equals("HostAddress")||sortValue.equals("Agent")||sortValue.equals("Title")||
				sortValue.equals("NumReports")||sortValue.equals("NumEdges")||sortValue.equals("TaskDelay")||sortValue.equals("LastUpdated")||sortValue.equals("TaskID")){
			sql += "order by "+sortValue;
			if(sortStyle.equals("descend"))
				sql = sql + " desc";
			else
				sql = sql + " asc";
		}else if(sortValue.equals("FirstSeen")){
			sql += "order by "+sortValue;
			if(sortStyle.equals("ascend"))
				sql = sql + " asc";
			else
				sql = sql + " desc";
		}else{
			sql += "order by Delay asc";
		}
		display.put("title", sql);
		PreparedStatement getTask;
		try {
			getTask = conn.prepareStatement(sql);
			if (offset + limit + 1 < 0) {
				getTask.setMaxRows(Integer.MAX_VALUE);
			} else {
				getTask.setMaxRows(offset + limit + 1);
			}
			ResultSet rs = getTask.executeQuery();

			int k = 0;
			ResultSetMetaData meta = rs.getMetaData();
			int column = meta.getColumnCount();
			while(rs.next())
			{
				if(k >= offset && k < offset + limit){
					Report r = new Report();
					for(int i =1;i<=column; i++)
						r.put(meta.getColumnName(i), rs.getString(i));
					lst.add(r);
				}
				k++;
			}
			rs.close();
			return lst;
		} catch (SQLException e) {}
		return lst;
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
		sortValue = sortValueList == null ? "FirstSeen" : sortValueList.get(0);
		sortStyle = sortStyleList == null ? "descend" : sortStyleList.get(0);
		
		String sql = "select * from Task where ";
		
		//title
		title = title.isEmpty() ? "*" : title;
		if(!title.equals("*"))
		{
			String tmp = formatCondition("Title",title);
			if(!tmp.isEmpty())
				sql += " ("+ tmp +") and ";
		}

		//time
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

		//delay
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
		
		//numReports
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
		
		//numEdges
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
		
		//TaskID
		taskID = taskID.isEmpty() ? "*" : taskID;
		if(taskID.equals("*"))
			sql = sql + "TaskID like '%' ";
		else{
			taskID = taskID.toUpperCase();//转换成大写
			taskID = taskID.replaceAll(" ","");//去除空格
			taskID = taskID.replaceAll("'","");//去除'
			String tmp = formatCondition("TaskID",taskID);
			if(tmp.isEmpty())
				sql = sql + "TaskID like '%' ";
			else
				sql += " ("+ tmp +") ";
		}
			
		
		//sort rule
		sortValue = sortValue.isEmpty() ? "FirstSeen" : sortValue;
		sortStyle = sortStyle.isEmpty() ? "descend" : sortStyle;
		if(sortValue.equals("taskID")){
			if(sortStyle.equals("descend"))
				sql = sql + "order by TaskID desc";
			else
				sql = sql + "order by TaskID asc";
		}else if(sortValue.equals("title")){
			if(sortStyle.equals("descend"))
				sql = sql + "order by Title desc";
			else
				sql = sql + "order by Title asc";
		}else if(sortValue.equals("numEdges")){
			if(sortStyle.equals("descend"))
				sql = sql + "order by NumEdges desc";
			else
				sql = sql + "order by NumEdges asc";
		}else if(sortValue.equals("numReport")){
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
		
		String disStr = sql;//"Found ";
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
			//disStr = disStr + rs.getRow() + " results (" + sql +")";
			display.put("title", disStr);
			rs.beforeFirst();
			lst = createRecordList(rs, offset, limit);
		} catch (SQLException e) {}
		
		return lst;
	}
	String formatCondition(String field, String value)
	{
		value = value.replaceAll("'","*");
		String formatted = "";
		int begin = 0;
		int end = value.indexOf(',')==-1?value.length():value.indexOf(',');
		String subValue = value.substring(begin,end);
		if(!subValue.isEmpty()){
			if(subValue.indexOf('*')!=-1){
				subValue = subValue.replaceAll("\\*","%");
				formatted = "lower("+field + ") like lower('" + subValue + "') ";
			}else
				formatted = field + "= '" + subValue + "' ";
		}
		while(end<value.length())
		{
			begin = end+1;
			end = value.indexOf(',',end+1)==-1?value.length():value.indexOf(',',end+1);
			subValue = value.substring(begin,end);
			if(!subValue.isEmpty()){
				if(subValue.indexOf('*')!=-1){
					subValue = subValue.replaceAll("\\*","%");
					formatted += "or lower(" + field + ") like lower('" + subValue + "') ";
				}else
					formatted += "or " + field + " = '" + subValue + "' ";
			}
		}
		if(formatted.startsWith("or "))
			formatted = formatted.substring(3);
		return formatted;
	}
	public List<OperationRecord> getOperationsBySearch(Report report, Report display, int offset,int limit)
	{
		List<OperationRecord> lst = new ArrayList<OperationRecord>();
		if(report == null)
			return lst;
		
		List<String> opNameList, numFromList, numToList, maxDelayFromList, maxDelayToList;
		List<String> minDelayFromList, minDelayToList, averageDelayFromList, averageDelayToList, sortValueList, sortStyleList;
		String opName, numFrom, numTo, maxDelayFrom, maxDelayTo, minDelayFrom, minDelayTo, averageDelayFrom, averageDelayTo, sortValue, sortStyle;
		
		opNameList = report.get("opName");
		numFromList= report.get("numFrom");
		numToList= report.get("numTo");
		maxDelayFromList= report.get("maxDelayFrom");
		maxDelayToList= report.get("maxDelayTo");
		minDelayFromList= report.get("minDelayFrom");
		minDelayToList= report.get("minDelayTo");
		averageDelayFromList= report.get("averageDelayFrom");
		averageDelayToList= report.get("averageDelayTo");
		sortValueList= report.get("sortValue");
		sortStyleList= report.get("sortStyle");
		
		opName = opNameList == null ? "*" : opNameList.get(0);
		numFrom = numFromList == null ? "*" : numFromList.get(0);
		numTo = numToList == null ? "*" : numToList.get(0);
		maxDelayFrom = maxDelayFromList == null ? "*" : maxDelayFromList.get(0);
		maxDelayTo = maxDelayToList == null ? "*" : maxDelayToList.get(0);
		minDelayFrom = minDelayFromList == null ? "*" : minDelayFromList.get(0);
		minDelayTo = minDelayToList == null ? "*" : minDelayToList.get(0);
		averageDelayFrom = averageDelayFromList == null ? "*" : averageDelayFromList.get(0);
		averageDelayTo = averageDelayToList == null ? "*" : averageDelayToList.get(0);
		sortValue = sortValueList == null ? "OpName" : sortValueList.get(0);
		sortStyle = sortStyleList == null ? "ascend" : sortStyleList.get(0);
		
		String sql = "select * from Operation where ";

		//num
		numFrom = numFrom.isEmpty() ? "*" : numFrom;
		numTo = numTo.isEmpty() ? "*" : numTo;
		Pattern intPattem = Pattern.compile("\\d+");
		Matcher matcher = intPattem.matcher(numFrom);
		if (!matcher.find()) 
			numFrom = "*";
		else
			numFrom = matcher.group(0);
		matcher = intPattem.matcher(numTo);
		if (!matcher.find()) 
			numTo = "*";
		else
			numTo = matcher.group(0);
		if(!numFrom.equals("*") && Integer.parseInt(numFrom) > 1)
			sql = sql + "Num >= " + numFrom + " and ";
		if(!numTo.equals("*"))
			sql = sql + "Num <= " + numTo + " and ";
		
		//maxDelay
		maxDelayFrom = maxDelayFrom.isEmpty() ? "*" : maxDelayFrom;
		maxDelayTo = maxDelayTo.isEmpty() ? "*" : maxDelayTo;
		Pattern floatPattem = Pattern.compile("(\\d*\\.)?\\d+");
		matcher = floatPattem.matcher(maxDelayFrom);
		if (!matcher.find()) 
			maxDelayFrom = "*";
		else
			maxDelayFrom = matcher.group(0);
		matcher = floatPattem.matcher(maxDelayTo);
		if (!matcher.find()) 
			maxDelayTo = "*";
		else
			maxDelayTo = matcher.group(0);
		if(!maxDelayFrom.equals("*") && Double.parseDouble(maxDelayFrom) > 0)
			sql = sql + "MaxDelay >= " + maxDelayFrom + "*1000000 and ";
		if(!maxDelayTo.equals("*"))
			sql = sql + "MaxDelay <= " + maxDelayTo + "*1000000 and ";

		//minDelay
		minDelayFrom = minDelayFrom.isEmpty() ? "*" : minDelayFrom;
		minDelayTo = minDelayTo.isEmpty() ? "*" : minDelayTo;
		matcher = floatPattem.matcher(minDelayFrom);
		if (!matcher.find()) 
			minDelayFrom = "*";
		else
			minDelayFrom = matcher.group(0);
		matcher = floatPattem.matcher(minDelayTo);
		if (!matcher.find()) 
			minDelayTo = "*";
		else
			minDelayTo = matcher.group(0);
		if(!minDelayFrom.equals("*") && Double.parseDouble(minDelayFrom) > 0)
			sql = sql + "MinDelay >= " + minDelayFrom + "*1000000 and ";
		if(!minDelayTo.equals("*"))
			sql = sql + "MinDelay <= " + minDelayTo + "*1000000 and ";
		
		//averageDelay
		averageDelayFrom = averageDelayFrom.isEmpty() ? "*" : averageDelayFrom;
		averageDelayTo = averageDelayTo.isEmpty() ? "*" : averageDelayTo;
		matcher = floatPattem.matcher(averageDelayFrom);
		if (!matcher.find()) 
			averageDelayFrom = "*";
		else
			averageDelayFrom = matcher.group(0);
		matcher = floatPattem.matcher(averageDelayTo);
		if (!matcher.find()) 
			averageDelayTo = "*";
		else
			averageDelayTo = matcher.group(0);
		if(!averageDelayFrom.equals("*") && Double.parseDouble(averageDelayFrom) > 0)
			sql = sql + "AverageDelay >= " + averageDelayFrom + "*1000000 and ";
		if(!averageDelayTo.equals("*"))
			sql = sql + "AverageDelay <= " + averageDelayTo + "*1000000 and ";

		//opName
		opName = opName.isEmpty() ? "*" : opName;
		if(opName.equals("*"))
			sql = sql + "OpName like '%' ";
		else{
			String tmp = formatCondition("OpName",opName);
			if(tmp.isEmpty())
				sql = sql + "OpName like '%' ";
			else
				sql += " ("+ tmp +") ";
		}
		
		//sort rule
		sortValue = sortValue.isEmpty() ? "OpName" : sortValue;
		sortStyle = sortStyle.isEmpty() ? "ascend" : sortStyle;
		sql = sql + "order by " + sortValue;
		if(sortStyle.equals("descend"))
			sql = sql + " desc";
		else 
			sql = sql + " asc";

		String disStr = sql;//"Found ";
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
			//disStr = disStr + rs.getRow() + " results (" + sql +")";
			display.put("title", disStr);
			rs.beforeFirst();
			lst = createOperationRecordList(rs, offset, limit);
		} catch (SQLException e) {}
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
		}
		return total;
	}
	
	public int numOperations()
	{
		int total = 0;
		try {
			ResultSet rs = totalNumOperations.executeQuery();
			rs.next();
			total = rs.getInt("totalOperations");
			rs.close();
		} catch (SQLException e) {
		}
		return total;
	}
	
	private TaskRecord readTaskRecord(ResultSet rs) throws SQLException {
		String taskId = rs.getString("TaskID");
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
				title);
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


	private OperationRecord readOperationRecord(ResultSet rs) throws SQLException {
		String OpName = rs.getString("OpName");
		int Num = rs.getInt("Num");
		double MaxDelay = (double)rs.getLong("MaxDelay")/1000000;
		double MinDelay = (double)rs.getLong("MinDelay")/1000000;
		double AverageDelay = rs.getDouble("AverageDelay")/1000000;
		return new OperationRecord(OpName, Num, Double.parseDouble(String.format("%.2f",MaxDelay)), 
			Double.parseDouble(String.format("%.2f",MinDelay)), Double.parseDouble(String.format("%.2f",AverageDelay)));
	}
	public List<OperationRecord> createOperationRecordList(ResultSet rs, int offset, int limit)
			throws SQLException {
		List<OperationRecord> lst = new ArrayList<OperationRecord>();
		int i = 0;
		while (rs.next()) {
			if (i >= offset && i < offset + limit)
				lst.add(readOperationRecord(rs));
			i++;
		}
		rs.close();
		return lst;
	}


	public void deleteTask(String taskID) throws SQLException
	{
		try{
			//查询包含的op
			PreparedStatement getOpNamesByTask = conn.prepareStatement("select OpName,EndTime,StartTime,TID from Report where TaskID = '"+taskID+"'");
			ResultSet rs = getOpNamesByTask.executeQuery();
			while(rs.next())
			{
				String opName = rs.getString("OpName");
				long startTime = rs.getLong("StartTime");
				long delay = rs.getLong("EndTime")-startTime;
				String TID = rs.getString("TID");
				PreparedStatement deleteRep = conn.prepareStatement("delete from Report where TID = '"+TID+"' and StartTime = "+startTime+" and OpName = '"+opName+"'");
				deleteRep.executeUpdate();
				PreparedStatement getOperationByOpName = conn.prepareStatement("select * from Operation where OpName = '"+opName+"'");
				ResultSet rs2 = getOperationByOpName.executeQuery();
				if(rs2.next())
				{
					int num = rs2.getInt("Num");
					long oldMaxDelay = rs2.getLong("MaxDelay");
					long oldMinDelay = rs2.getLong("MinDelay");
					double oldAverageDelay = rs2.getDouble("AverageDelay");
					if(num == 1){//Operation中对应op只有一个则删除记录
						PreparedStatement deleteOperation = conn.prepareStatement("delete from Operation where OpName = '"+opName+"'");
						deleteOperation.executeUpdate();
					}else{//否则更新
						long newMaxDelay = oldMaxDelay;
						long newMinDelay = oldMinDelay;
						if(delay == oldMaxDelay){
							PreparedStatement getMaxDelay = conn.prepareStatement("select (EndTime-StartTime) as Delay from Report where OpName ='"+opName+"' order by Delay desc");
							ResultSet rs3 = getMaxDelay.executeQuery();
							if(rs3.next())
								newMaxDelay = rs3.getLong("Delay");
							rs3.close();
						}
						if(delay == oldMinDelay){
							PreparedStatement getMinDelay = conn.prepareStatement("select (EndTime-StartTime) as Delay from Report where OpName ='"+opName+"' order by Delay asc");
							ResultSet rs3 = getMinDelay.executeQuery();
							if(rs3.next())
								newMinDelay = rs3.getLong("Delay");
							rs3.close();
						}
						double newAverageDelay = oldAverageDelay + (oldAverageDelay-delay)/(num-1);
						PreparedStatement UpdateOperation = conn.prepareStatement("update Operation set Num = Num-1, MaxDelay = "+newMaxDelay+",MinDelay="+newMinDelay+",AverageDelay = "+newAverageDelay+" where OpName = '"+opName+"'");
						UpdateOperation.executeUpdate();
					}
					rs2.close();
				}
			}
			//删除Report表
			PreparedStatement deleteReportByTaskID = conn.prepareStatement("delete from Report where TaskID = '"+taskID+"'");
			deleteReportByTaskID.executeUpdate();
			//删除Edge表
			PreparedStatement deleteEdgeByTaskID = conn.prepareStatement("delete from Edge where TaskID = '"+taskID+"'");
			deleteEdgeByTaskID.executeUpdate();
			//删除Task表
			PreparedStatement deleteTaskByTaskID = conn.prepareStatement("delete from Task where TaskID = '"+taskID+"'");
			deleteTaskByTaskID.executeUpdate();
			rs.close();
		}catch (SQLException e) {
			throw e;
		}
	}
}



























