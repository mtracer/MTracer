<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="edu.nudt.xtrace.*"%>
<%@ page import="java.util.*"%>

<HTML>
<head>
<title>Delay Distribute Graph'</title>
</head>
<BODY>
<FONT style="FONT-FAMILY: sans-serif">
<div style="line-height:1"> 
<jsp:useBean id="accessor" class="edu.nudt.xtrace.MySQLAccessor" scope="session"/>
<%
int num = 0;
int N;
try{N = Integer.parseInt(request.getParameter("n"));}catch(Exception e){N=8;}
double maxDelay = -1;
double minDelay = Double.MAX_VALUE;
Report condition = new Report();
Report title = new Report();
String partUrl="";
Enumeration para=request.getParameterNames();
while(para.hasMoreElements()){
	String key=(String)para.nextElement();
	String value=request.getParameter(key);
	condition.put(key, value);
	partUrl += key + "=" + value + "&";
}
if(!partUrl.isEmpty())
	partUrl = partUrl.substring(0,partUrl.length()-1);
%>
<a href="/">Return Home</a> &gt;&gt;<a href="op_index.jsp">Operations</a> &gt;&gt;<a href="op_tasksWithOp.jsp?<%=partUrl%>">Operation Detail</a>&gt;&gt;
<B>DDG</B> <a href="op_hostDistributeGraph.jsp?<%=partUrl%>">HDG</a> <a href="op_delayTrendGraph.jsp?<%=partUrl%>">DTG</a> | <a href="index.jsp">Tasks</a>
</div><hr/>
<%
int off = 0;int len =50;
List<Report> taskWithOp=accessor.getTasksByOp(condition, title, off, len);
while(taskWithOp.size()>0)
{
	int currentRecordNum=0;
	taskWithOp=accessor.getTasksByOp(condition, title, off, len);
	Iterator<Report> it = taskWithOp.iterator();
	while (it.hasNext()) {
		currentRecordNum++;
		Report rep = it.next();
		double delay=-1;
		try{delay=Double.parseDouble(rep.get("Delay").get(0));}catch(Exception e){continue;}
        	if(delay>maxDelay)
			maxDelay=delay;
		if(delay<minDelay)
			minDelay=delay;
		num++;
        }
	off = off + currentRecordNum;
	taskWithOp=accessor.getTasksByOp(condition, title, off, len);
}
%>
<form action="" method="get" name="changeForm">
	<B>Change N: </B>
	<input type=hidden name="opName" value= "<%= request.getParameter("opName")==null ? "*" : request.getParameter("opName")%>" /> 
	<input type=hidden name="delayFrom" value= "<%= request.getParameter("delayFrom")==null ? "0" : request.getParameter("delayFrom")%>" />
	<input type=hidden name="delayTo" value= "<%= request.getParameter("delayTo")==null ? "*" : request.getParameter("delayTo")%>" />
	<input type=hidden name="address" value= "<%= request.getParameter("address")==null ? "*" : request.getParameter("address")%>" />
	<input type=hidden name="name" value= "<%= request.getParameter("name")==null ? "*" : request.getParameter("name")%>" />
	<input type=hidden name="agent" value= "<%= request.getParameter("agent")==null ? "*" : request.getParameter("agent")%>" />
	<input type=hidden name="title" value= "<%= request.getParameter("title")==null ? "*" : request.getParameter("title")%>" /> 
	<input type=hidden name="timeFrom" value= "<%= request.getParameter("timeFrom")==null ? "1970-01-01 00:00:00" : request.getParameter("timeFrom")%>" />
	<input type=hidden name="timeTo" value= "<%= request.getParameter("timeTo")==null ? "*" : request.getParameter("timeTo")%>" />
	<input type=hidden name="taskID" value="<%= request.getParameter("taskID")==null ? "*" : request.getParameter("taskID")%>" />
	<input style="width:50" name="n" value="<%= request.getParameter("n")==null ? "8" : request.getParameter("n")%>" />
	<input style="width:80" type="submit" value="change" />
</form>
<%
String opName = request.getParameter("opName")==null?"*":request.getParameter("opName");
if(opName.indexOf(",")!=-1 || opName.indexOf("*")!=-1) 
	out.print("<font color = red>WARNING: There may be more than 1 kind of OPs(OpName = " + opName + "), the DDG maybe meanless</font><p>");
off = 0;len =50;
if(maxDelay == minDelay){
	N=1;
	out.print("<font color = red>WARNING: N is too big, change to "+N+"</font><p>");
	
}else if((maxDelay - minDelay)/N<0.01)
{
	N=(int)Math.round((maxDelay-minDelay)*100);
	out.print("<font color = red>WARNING: N is too big, change to "+N+"</font><p>");
}
double interval = (maxDelay - minDelay)/N;

int [] count = new int[N];
for(int i=0; i<N; i++)
	count[i]=0;
while(off<num)
{
	taskWithOp=accessor.getTasksByOp(condition, title, off, len);
	Iterator<Report> it = taskWithOp.iterator();
	while (it.hasNext()) {
        	Report rep = it.next();
		double delay;
		try{delay=Double.parseDouble(rep.get("Delay").get(0));}catch(Exception e){continue;}
		if(delay == minDelay)
			count[0]++;
		else{
			int index = (int)Math.ceil((delay - minDelay)/interval)-1;
			if(index>=N) 
				count[N-1]++;
			else
				count[index]++;
		}
        }
	off = off + len;
}
int maxCount=count[0];
for(int i=1;i<N;i++)
{
	if(count[i]>maxCount)
		maxCount=count[i];
}
double factor= (double)100/maxCount;
String table;
table = "<table border=1 cellspacing=0 cellpadding=3>";
table += "<tr>";
table += "<th width=200>Delay</th>";
table += "<th>Count</th>";
table += "<th width=400>Count graph</th>";
table += "<th width=80>Rate</th>";
table += "</tr>";

String tmpUrl="";
para=request.getParameterNames();
while(para.hasMoreElements()){
	String key=(String)para.nextElement();
	String value=request.getParameter(key);
	if(!key.equals("delayFrom")&&!key.equals("delayTo"))
		tmpUrl += key + "=" + value + "&";
}
for(int i=0;i<N;i++)
{
	table += "<tr>";
	String df=String.format("%.2f",minDelay+i*interval);
	String dt=String.format("%.2f",minDelay+(i+1)*interval);
	table += "<td align = center>";
	if(count[i]>0)
		table+="<a title = \"see all related tasks\" href=\"/op_tasksWithOp.jsp?"+tmpUrl+"delayFrom="+df+"&delayTo="+dt+"\">";
	if(i==0)table+="[";else table+="(";
	table += df + ", "+ dt +"]";
	if(count[i]>0) table+="</a>";
	table+="</td>";
	table += "<td>" + count[i] + "</td>";
	table += "<td>";
	for(int j=0;j<(int)(factor*count[i]);j++)
		table+="\\";
	table += "&nbsp;</td>";
	table += "<td align = center>" + (int)Math.round((double)100*count[i]/num)+"%" + "</td>";
	table += "</tr>";
}
out.print(table);
%>
</FONT>

</BODY>
<HTML>

