<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="edu.nudt.xtrace.*"%>
<%@ page import="java.util.*"%>
<HTML>
<head>
<title>Delay Rate Graph of <%= request.getParameter("taskid") %></title>
</head>
<BODY>
<jsp:useBean id="accessor" class="edu.nudt.xtrace.MySQLAccessor" scope="session"/>
<jsp:useBean id="trace" class="edu.nudt.xtrace.Trace" scope="session"/>

<% 
String delay = request.getParameter("delay");
String taskID = request.getParameter("taskid");
%>
<a href="/">Return Home</a>&gt;&gt;<a href="/">Tasks</a>&gt;&gt;<a href="/callTreeGraph.jsp?taskid=<%= taskID %>&delay=<%= delay%>&direction=TB&shape=ellipse">CTG</a> 
<B>DRG</B> | <a href="op_index.jsp">Operations</a>
<hr>
<br>
<table border=1 cellspacing=0 cellpadding=3>
<tr>
	<th>OpName</th>
	<th>Delay</th>
	<th>Delay graph</th>
	<th>Rate</th>
	<th>Agent</th>
	<th>HostName</th>
	<th>Address</th>
	<th>Description</th>
</tr>
<%
if(trace.findTopology(accessor.getConn(), taskID, delay) == null)
	out.print("error in construct topology");
else{
	/**  added for abnormal task**/
	String isAbnormalStr = request.getParameter("isAbnormalTask")==null?"false":request.getParameter("isAbnormalTask");
	boolean isAbnormal =  isAbnormalStr.equals("true")?true:false;
	ArrayList<DiagnosisResult> drList=null;
	DiagnosisResult dr=null;
	ArrayList<Integer> abnormalOp = null;
	if(isAbnormal==true){
		drList = (ArrayList<DiagnosisResult>)session.getAttribute("abnormalTaskInfo");
		for(int i=0;i<drList.size();i++){
			dr = drList.get(i);
			if(taskID.equals(dr.getTaskID()))
				break;
		}
		if(dr!=null)
			abnormalOp=dr.getAbnormalOperations();
	}
	/**  added for abnormal task**/
	trace.depthFirstSearchOrder();
	ArrayList<Report> reports = trace.getReports();
	HashMap<String, Report> sortedReports = new HashMap<String, Report>();
	for(int i=0; i<reports.size();i++)
	{
		Report rep = reports.get(i);
		if(rep.get("dfsorder")==null)continue;
		sortedReports.put(rep.get("dfsorder").get(0),rep);
	}
	for(int i=0; i<sortedReports.size();i++)
	{
		Report rep = sortedReports.get(String.valueOf(i));
		if(rep == null) continue;
		int depth = Integer.parseInt(rep.get("depth").get(0));
		String opname = rep.get("opname").get(0);
		String formattedOpname = opname;
		/**  added for abnormal task**/
		if(isAbnormal==true && abnormalOp!=null){
			if(abnormalOp.contains(i))
				formattedOpname = "<font color=red>"+formattedOpname+"</font>";
		}
		/**  added for abnormal task**/
		String time = rep.get("delay").get(0);
		out.print("<tr>");
		String space="";
		double rate;
		if(Double.parseDouble(delay)<=0)
			rate = 100;
		else
			rate = Double.parseDouble(time)*100/Double.parseDouble(delay);
		for(int s=0;s<depth;s++)
			for(int t=0;t<16;t++)
				space+="&nbsp;";
		if(depth==0)
			out.print("<td>"+space+"<a title = \"see all tasks of this type\" href=\"/?title="+opname+"&numReportsFrom="+sortedReports.size()+"&numReportsTo="+sortedReports.size()+"\">"+formattedOpname+"</td>");
		else
			out.print("<td>"+space+"\\_____<a title = \"see all the tasks with this operation\" href=\"/op_tasksWithOp.jsp?opName="+opname+"&sortValue=Delay&sortStyle=descend"+"\">"+formattedOpname+"</td>");
		String tmp = "";
		for(int t=0;t<Integer.valueOf(String.format("%.0f", rate));t++)tmp+="\\";
		out.print("<td>"+String.format("%.2f", Double.parseDouble(time))+"ms</td>");
		out.print("<td>"+tmp+"&nbsp;</td>");
		out.print("<td>"+String.format("%.0f", rate)+"%</td>");
		if(depth==0)
			out.print("<td>"+rep.get("agent").get(0)+"</td>");
		else
			out.print("<td><a title = \"see all the tasks with this OP and on this agent\" href=\"op_tasksWithOp.jsp?opName="+opname+"&agent="+rep.get("agent").get(0)+"&sortValue=Delay&sortStyle=descend"+"\">"+rep.get("agent").get(0)+"</td>");
		if(depth==0)
			out.print("<td>"+rep.get("hostname").get(0)+"</td>");
		else
			out.print("<td><a title = \"see all the tasks with this OP and on host\" href=\"op_tasksWithOp.jsp?opName="+opname+"&name="+rep.get("hostname").get(0)+"&sortValue=Delay&sortStyle=descend"+"\">"+rep.get("hostname").get(0)+"</td>");
		if(depth==0)
			out.print("<td>"+rep.get("hostaddress").get(0)+"</td>");
		else
			out.print("<td><a title = \"see all the tasks with this OP and on host\" href=\"op_tasksWithOp.jsp?opName="+opname+"&address="+rep.get("hostaddress").get(0)+"&sortValue=Delay&sortStyle=descend"+"\">"+rep.get("hostaddress").get(0)+"</td>");
		String description = rep.get("description")==null?"null":rep.get("description").get(0);
		out.print("<td title = \""+description+"\">"+description.substring(0,Math.min(50,description.length()))+"</td>");
		out.print("</tr>");
	}
}
%>
</BODY>
<HTML>








