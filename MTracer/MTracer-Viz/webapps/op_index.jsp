<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="edu.nudt.xtrace.*"%>
<%@ page import="java.util.*"%>

<HTML>
<head>
<title>Operation Abstract Info</title>
</head>
<BODY>
<FONT style="FONT-FAMILY: sans-serif">
<div style="line-height:1"> 
<a href="/">Return Home</a> &gt;&gt;<B>Operations</B>&gt;&gt;<a href="op_tasksWithOp.jsp">Operation Detail</a> | <a href="index.jsp">Tasks</a>
<hr/><%@include file = "op_searchBar.jsp"%><hr/>
</div>
<jsp:useBean id="accessor" class="edu.nudt.xtrace.MySQLAccessor" scope="session"/>
<%@include file = "op_table.jsp"%>
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
Report condition = new Report();
Report title = new Report();
Enumeration para=request.getParameterNames();
while(para.hasMoreElements()){
	String key=(String)para.nextElement();
	String value=request.getParameter(key);
	condition.put(key, value);
}
List<OperationRecord> operations=accessor.getOperationsBySearch(condition, title, off, len);
if(request.getParameter("opName") == null){
	out.print("<B>Operations</B><p>");
}else{
	out.print("<B>" + title.get("title").get(0) + "</B><p>");
}
%>
<input title = "see all operation details" style="width:120" type="button" value="Operation Detail" onclick="window.location='/op_tasksWithOp.jsp?sortValue=OpName&sortStyle=default'"><p>
<%@include file = "navbar.jsp"%>
<%out.print(showOperations(operations));%>
<%@include file = "navbar.jsp"%>
<hr/>
Database size: <%= accessor.numOperations()%> operations.</p>
</FONT>
</BODY>
<HTML>

