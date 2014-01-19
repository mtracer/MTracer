<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="edu.nudt.xtrace.*"%>
<%@ page import="java.util.*"%>

<HTML>
<head>
<title>Delay Trend Graph</title>
</head>
<BODY>
<FONT style="FONT-FAMILY: sans-serif">
<div style="line-height:1"> 
<jsp:useBean id="accessor" class="edu.nudt.xtrace.MySQLAccessor" scope="session"/>
<%
int num = 0;
double maxDelay = -1;
String partUrl="";
Report condition = new Report();
Report title = new Report();
Enumeration para=request.getParameterNames();
while(para.hasMoreElements()){
	String key=(String)para.nextElement();
	String value=request.getParameter(key);
	if(!key.equals("sortValue")&&!key.equals("sortStyle"))
		condition.put(key, value);
	partUrl += key + "=" + value + "&";
}
if(!partUrl.isEmpty())
	partUrl = partUrl.substring(0,partUrl.length()-1);
condition.put("sortValue","FirstSeen");
condition.put("sortStyle","ascend");
%>
<a href="/">Return Home</a> &gt;&gt;<a href="op_index.jsp">Operations</a> &gt;&gt;<a href="op_tasksWithOp.jsp?<%=partUrl%>">Operation Detail</a>&gt;&gt;
<a href="op_delayDistributeGraph.jsp?<%=partUrl%>">DDG</a> <a href="op_hostDistributeGraph.jsp?<%=partUrl%>">HDG</a> <B>DTG</B> | <a href="index.jsp">Tasks</a>
</div><hr/>
<form action="" method="get" name="changeForm">
	<B>Time from</B>
	<input type=hidden name="opName" value= "<%= request.getParameter("opName")==null ? "*" : request.getParameter("opName")%>" /> 
	<input type=hidden name="delayFrom" value= "<%= request.getParameter("delayFrom")==null ? "0" : request.getParameter("delayFrom")%>" />
	<input type=hidden name="delayTo" value= "<%= request.getParameter("delayTo")==null ? "*" : request.getParameter("delayTo")%>" />
	<input type=hidden name="address" value= "<%= request.getParameter("address")==null ? "*" : request.getParameter("address")%>" />
	<input type=hidden name="name" value= "<%= request.getParameter("name")==null ? "*" : request.getParameter("name")%>" />
	<input type=hidden name="agent" value= "<%= request.getParameter("agent")==null ? "*" : request.getParameter("agent")%>" />
	<input type=hidden name="title" value= "<%= request.getParameter("title")==null ? "*" : request.getParameter("title")%>" /> 
	<input type=hidden name="taskID" value="<%= request.getParameter("taskID")==null ? "*" : request.getParameter("taskID")%>" />
	<input style="width:180" name="timeFrom" value= "<%= request.getParameter("timeFrom")==null ? "1970-01-01 00:00:00" : request.getParameter("timeFrom")%>" /> 
	to <input style="width:180" name="timeTo" value= "<%= request.getParameter("timeTo")==null ? "*" : request.getParameter("timeTo")%>" /> 
	<input style="width:80" type="submit" value="change" />
</form>

<%
String opName = request.getParameter("opName")==null?"*":request.getParameter("opName");
if(opName.indexOf(",")!=-1 || opName.indexOf("*")!=-1) 
	out.print("<font color = red>WARNING: There may be more than 1 kind of OPs(OpName = " + opName + "), the DTG maybe meanless</font><p>");
int off = 0;int len =50;
List<Report> taskWithOp=accessor.getTasksByOp(condition, title, off, len);
while(taskWithOp.size()>0)
{
	int currentRecordNum=0;
	//taskWithOp=accessor.getTasksByOp(condition, title, off, len);
	Iterator<Report> it = taskWithOp.iterator();
	while (it.hasNext()) {
		currentRecordNum++;
		Report rep = it.next();
		double delay=-1;
		try{delay=Double.parseDouble(rep.get("Delay").get(0));}catch(Exception e){continue;}
        	if(delay>maxDelay)
			maxDelay=delay;
		num++;
        }
	off = off + currentRecordNum;
	taskWithOp=accessor.getTasksByOp(condition, title, off, len);
}

double factor= (double)100/maxDelay;
String table;
table = "<table border=1 cellspacing=0 cellpadding=3>";
table += "<tr>";
table += "<th width=200>Time</th>";
table += "<th>Delay</th>";
table += "<th width=400>Delay graph</th>";
table += "</tr>";
String nextTask = "notask";
String color = "blue";
off=0;len=25;
while(off<num)
{
	taskWithOp=accessor.getTasksByOp(condition, title, off, len);
	Iterator<Report> it = taskWithOp.iterator();
	while (it.hasNext()) {
        	Report rep = it.next();
		double delay;
		try{delay=Double.parseDouble(rep.get("Delay").get(0));}catch(Exception e){continue;}
		String timestamp;
		try{timestamp=rep.get("FirstSeen").get(0);}catch(Exception e){continue;}
		String taskID;
		try{taskID=rep.get("TaskID").get(0);}catch(Exception e){taskID="*";}
		String curTask="";
		try{curTask=rep.get("TaskID").get(0);}catch(Exception e){color=color=="blue"?"black":"blue";}
		if(!curTask.equals(nextTask))color=color=="blue"?"black":"blue";
		table += "<tr>";
		table += "<td align = center><a title = \"see all related tasks\" href=\"/op_tasksWithOp.jsp?opName="+opName+"&delayFrom="+delay+"&delayTo="+delay+"&timeFrom="+timestamp+"&timeTo="+timestamp+"&taskID="+taskID+"\">" + timestamp + "</td>";
		table += "<td align = right><font color="+color+">" + delay + "ms</td>";
		table += "<td><font color="+color+">";
		for(int j=0;j<(int)(factor*delay);j++)
			table+="\\"; 
		table += "<font></td>";
		table += "</tr>";
		nextTask=curTask;
        }
	off = off + len;
}

out.print(table);
%>
</FONT>

</BODY>
<HTML>

