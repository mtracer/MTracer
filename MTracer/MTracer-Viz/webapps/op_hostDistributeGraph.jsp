<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="edu.nudt.xtrace.*"%>
<%@ page import="java.util.*"%>

<HTML>
<head>
<title>Host Distribute Graph</title>
</head>
<BODY>
<FONT style="FONT-FAMILY: sans-serif">
<div style="line-height:1"> 
<jsp:useBean id="accessor" class="edu.nudt.xtrace.MySQLAccessor" scope="session"/>
<%
int num = 0;
Report condition = new Report();
Report title = new Report();
String partUrl="";
Enumeration para=request.getParameterNames();
while(para.hasMoreElements()){
	String key=(String)para.nextElement();
	String value=request.getParameter(key);
	if(!key.equals("sortValue")&&!key.equals("keyStyle"))
		condition.put(key, value);
	partUrl += key + "=" + value + "&";
}
if(!partUrl.isEmpty())
	partUrl = partUrl.substring(0,partUrl.length()-1);
condition.put("sortValue","FirstSeen");
condition.put("sortStyle","ascend");
%>
<a href="/">Return Home</a> &gt;&gt;<a href="op_index.jsp">Operations</a> &gt;&gt;<a href="op_tasksWithOp.jsp?<%=partUrl%>">Operation Detail</a>&gt;&gt; 
<a href="op_delayDistributeGraph.jsp?<%=partUrl%>">DDG</a> <B>HDG</B> <a href="op_delayTrendGraph.jsp?<%=partUrl%>">DTG</a> | <a href="index.jsp">Tasks</a>
</div><hr/>
<%
String opName = request.getParameter("opName")==null?"*":request.getParameter("opName");
if(opName.indexOf(",")!=-1 || opName.indexOf("*")!=-1) 
	out.print("<font color = red>WARNING: There may be more than 1 kind of OPs(OpName = " + opName + "), the HDG maybe meanless</font><p>");
int off = 0;int len =50;
HashMap<String,Integer> count = new HashMap<String,Integer>();
List<Report> taskWithOp=accessor.getTasksByOp(condition, title, off, len);
while(taskWithOp.size()>0)
{
	Iterator<Report> it = taskWithOp.iterator();
	while (it.hasNext()) {
        	Report rep = it.next();
		String host;
		if(rep.get("HostName")==null)continue;else host=rep.get("HostName").get(0);
		if(count.get(host)==null)
			count.put(host, 1);
		else
			count.put(host, count.get(host)+1);
        }
	off = off + len;
	taskWithOp=accessor.getTasksByOp(condition, title, off, len);
}
Iterator<Map.Entry<String, Integer>>  it = count.entrySet().iterator();
int maxCount=1;
while (it.hasNext())
{
	Map.Entry entry = (Map.Entry) it.next();
	Integer times = (Integer)entry.getValue();
	num+=times;
	if(maxCount<times)
		maxCount=times;
}
double factor= (double)100/maxCount;

String table;
table = "<table border=1 cellspacing=0 cellpadding=3>";
table += "<tr>";
table += "<th width=200>Host</th>";
table += "<th>Count</th>";
table += "<th width=400>Count graph</th>";
table += "<th width=80>Rate</th>";
table += "</tr>";
it = count.entrySet().iterator();
while (it.hasNext())
{
	Map.Entry entry = (Map.Entry) it.next();
	String host = (String)entry.getKey();
	Integer times = (Integer)entry.getValue();
	table += "<tr>";
	table += "<td align = center><a title = \"see all related tasks\" href=\"/op_tasksWithOp.jsp?opName="+opName+"&name="+host+"\">" + host + "</td>";
	table += "<td>" + times + "</a></td>";
	table += "<td>";
	for(int j=0;j<(int)(factor*times);j++)
		table+="\\";
	table += "</td>";
	table += "<td align = center>" + (int)Math.round((double)100*times/num)+"%" + "</a></td>";
	table += "</tr>";
}
out.print(table);
%>
</FONT>

</BODY>
<HTML>

