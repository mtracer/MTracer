<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="edu.nudt.xtrace.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.io.File" %>
<%@ page import="java.io.BufferedReader" %>
<%@ page import="java.io.InputStreamReader" %>
<%@ page import="java.io.FileInputStream" %>
<%@ page import="java.io.PrintWriter" %>
<%@ page import="java.io.IOException" %>

<HTML>
<head>
<title>abnormal Call Tree Graph of <%= request.getParameter("taskid") %></title>
</head>
<BODY>
<jsp:useBean id="accessor" class="edu.nudt.xtrace.MySQLAccessor" scope="session"/>
<jsp:useBean id="trace" class="edu.nudt.xtrace.Trace" scope="session"/>
<%
String delay = request.getParameter("delay");
String taskID = request.getParameter("taskid");
String temp=application.getRealPath(request.getRequestURI());  
String fileDir=new File(temp).getParent();
String fileName = "abnormal_CTG_tmp";
String imagePath=fileDir+"/tmp/" + fileName + ".svg";
String dotPath = fileDir+"/tmp/" + fileName + ".dot";
%>
<a href="/">Return Home</a>&gt;&gt;<a href="/">Tasks</a>&gt;&gt;<B>abnormalCTG</B> | <a href="op_index.jsp">Operations</a><hr> 
<br>
<%
File image = new File(imagePath); 
File dot = new File(dotPath);
if(image.exists())image.delete();
if(dot.exists())dot.delete();
ArrayList<DiagnosisResult> drList=(ArrayList<DiagnosisResult>)session.getAttribute("abnormalTaskInfo");
if(drList == null)
	response.sendRedirect("/callTreeGraph.jsp?taskid="+taskID+"&delay="+delay+"&direction=TB&shape=ellipse");
DiagnosisResult dr=null;
for(int i=0;i<drList.size();i++){
	dr = drList.get(i);
	if(taskID.equals(dr.getTaskID()))
		break;
}
if(dr == null)
	response.sendRedirect("/callTreeGraph.jsp?taskid="+taskID+"&delay="+delay+"&direction=TB&shape=ellipse");
if(trace.findTopology(accessor.getConn(), taskID, delay) == null)
	response.sendRedirect("/callTreeGraph.jsp?taskid="+taskID+"&delay="+delay+"&direction=TB&shape=ellipse");
if(trace.genAbnormalTxt(dotPath, dr)==false)
	response.sendRedirect("/callTreeGraph.jsp?taskid="+taskID+"&delay="+delay+"&direction=TB&shape=ellipse");

String cmd = "dot -Tsvg "+ dotPath + " -o " +  imagePath;
Process process = Runtime.getRuntime().exec(cmd); 
process.waitFor();
out.println("<p><embed src=\"tmp/" + fileName + ".svg" +"\" type=\"image/svg+xml\" />");
%>
</BODY>
<HTML>
