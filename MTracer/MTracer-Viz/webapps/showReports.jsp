<% out.print("Reports of Task '"+request.getParameter("taskid")+"'"); %>
<jsp:useBean id="accessor" class="edu.nudt.xtrace.MySQLAccessor" scope="session"/>
<%
Iterator<Report> it = accessor.getReportsByTask(request.getParameter("taskid")==null? "##" : request.getParameter("taskid"));
while (it.hasNext()) {
	Report r = it.next();
	out.print(r.toString());
	out.print("\n");
}
%>

<%@ page contentType="text/plain;charset=UTF-8" %>
<%@ page import="edu.nudt.xtrace.*"%>
<%@ page import="java.util.*"%>

