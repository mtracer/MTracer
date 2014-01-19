<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="edu.nudt.xtrace.*"%>
<%@ page import="java.util.*"%>

<HTML>
<head>
<title>Operation Detail</title>
</head>
<BODY>
<FONT style="FONT-FAMILY: sans-serif">
<div style="line-height:1"> 
<a href="/">Return Home</a> &gt;&gt;<a href="op_index.jsp">Operations</a> &gt;&gt;<B>Operation Detail</B> | <a href="index.jsp">Tasks</a>
<hr/><%@include file = "op_taskWithOpsearchBar.jsp"%><hr/>
</div>
<jsp:useBean id="accessor" class="edu.nudt.xtrace.MySQLAccessor" scope="session"/>
<%@include file = "op_taskWithOpTable.jsp"%>
<%
int off = 0;
int len = 25;
if(request.getParameter("offset")!=null)
{
	try{off = Integer.parseInt(request.getParameter("offset"));}
	catch(Exception e){off =0;}
}
if(request.getParameter("length")!=null)
{
	try{len = Integer.parseInt(request.getParameter("length"));}
	catch(Exception e){len =25;}
}
String partUrl="";
Report condition = new Report();
Report title = new Report();
Enumeration para=request.getParameterNames();
while(para.hasMoreElements()){
	String key=(String)para.nextElement();
	String value=request.getParameter(key);
	condition.put(key, value);
	partUrl += key + "=" + value + "&";
}
if(!partUrl.isEmpty())
	partUrl = partUrl.substring(0,partUrl.length()-1);
List<Report> taskWithOp=accessor.getTasksByOp(condition, title, off, len);

if(!request.getParameterNames().hasMoreElements()){
	out.print("<B>X-Trace Latest Tasks</B><p>");
}else{
	out.print("<B>" + title.get("title")==null?"Operations":title.get("title").get(0) + "</B><p>");
}
%>
<B>Tools:</B>
<input title = "Delay Distribution Graph" style="width:120" type="button" value="DDG" onclick="window.location='/op_delayDistributeGraph.jsp?<%=partUrl%>'">
<input title = "Host Distribution Graph" style="width:120" type="button" value="HDG" onclick="window.location='/op_hostDistributeGraph.jsp?<%=partUrl%>'">
<input title = "Delay Trend Graph" style="width:120" type="button" value="DTG" onclick="window.location='/op_delayTrendGraph.jsp?<%=partUrl%>'"><p>
<%@include file = "navbar.jsp"%>
<% out.print(showTaskWithOp(taskWithOp));%>
<%@include file = "navbar.jsp"%>
<hr/>

</FONT>
</BODY>
<HTML>

