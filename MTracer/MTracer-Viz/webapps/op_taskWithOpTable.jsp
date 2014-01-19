<%@ page import="java.text.DateFormat"%>
<%@ page import="java.text.SimpleDateFormat"%>

<%!
public String showTaskWithOp(List<Report> tasks)
{
	DateFormat HTML_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
	String table;
	table = "<table border=1 cellspacing=0 cellpadding=3>";
	table += "<tr>";
	table += "<th colspan=\"5\">Operation details</th>";
	table += "<th colspan=\"8\">Trace details</th>";
	table += "</tr>";
	
	table += "<tr>";
	table += "<th width=160>OpName</th>";
	table += "<th width=100>Delay</th>";
	table += "<th width=170>HostName</th>";
	table += "<th width=80>HostAddress</th>";
	table += "<th width=80>Agent</th>";
	
	table += "<th width=200>Trace Title</th>";
	table += "<th width=60>Reports</th>";
	table += "<th width=60>Edges</th>";
	table += "<th width=80>Delay</th>";
	table += "<th width=170>Created</th>";
	table += "<th width=170>Last Report</th>";
	table += "<th width=160>TaskID</th>";
	table += "<th width=80>Tool</th>";
	table += "</tr>";
	Iterator<Report> it = tasks.iterator();
	while (it.hasNext()) {
        	Report rep = it.next();
		String OpName = rep.get("OpName")==null?"":rep.get("OpName").get(0);
		String Delay = rep.get("Delay")==null?"":rep.get("Delay").get(0);
		String HostName = rep.get("HostName")==null?"":rep.get("HostName").get(0);
		String HostAddress = rep.get("HostAddress")==null?"":rep.get("HostAddress").get(0);
		String Agent = rep.get("Agent")==null?"":rep.get("Agent").get(0);
		String Title = rep.get("Title")==null?"":rep.get("Title").get(0);
		String NumReports = rep.get("NumReports")==null?"":rep.get("NumReports").get(0);
		String NumEdges = rep.get("NumEdges")==null?"":rep.get("NumEdges").get(0);
		String TaskDelay = rep.get("TaskDelay")==null?"":rep.get("TaskDelay").get(0);
		String FirstSeen = rep.get("FirstSeen")==null?"":rep.get("FirstSeen").get(0);
		String LastUpdated = rep.get("LastUpdated")==null?"":rep.get("LastUpdated").get(0);
		String TaskID = rep.get("TaskID")==null?"":rep.get("TaskID").get(0);

		table += "<tr>";
		table += "<td align = \"left\"><a title = \"see the OPs with this name\" href=\"/op_tasksWithOp.jsp?opName="+OpName+"\">" + OpName + "</td>";
		table += "<td align = \"right\">" + Delay + "ms</td>";
		table += "<td align = \"center\"><a title = \"see the OPs in the same host\" href=\"/op_tasksWithOp.jsp?name="+HostName+"\">" + HostName + "</td>";
		table += "<td align = \"center\"><a title = \"see the OPs in the same host\" href=\"/op_tasksWithOp.jsp?address="+HostAddress+"\">" + HostAddress + "</td>";
		table += "<td align = \"center\"><a title = \"see the OPs in the same agent\" href=\"/op_tasksWithOp.jsp?agent="+Agent+"\">" + Agent + "</td>";
		
		table += "<td><a title = \"see tasks with the same title\" href=\"/?title=" +Title+ "\">" +Title+ "</a></td>";
		table += "<td><a title = \"see all reports of this task\" href=\"/showReports.jsp?taskid="+TaskID+"\">" +NumReports+ "</a></td>";
		table += "<td><a title = \"see all edges of this task\" href=\"/showEdges.jsp?taskid="+TaskID+"\">" +NumEdges+ "</a></td>";
		table += "<td align = \"right\">" + TaskDelay + "ms</td>";
		table += "<td align = \"center\">" + FirstSeen + "</td>";
		table += "<td align = \"center\">" + LastUpdated + "</td>";
		table += "<td align = \"center\"><tt>" + TaskID + "</tt></td>";
		table += "<td align = \"center\">";
		table += "<a title = \"Call Tree Graph\" href=\"/callTreeGraph.jsp?taskid="+TaskID+"&delay="+TaskDelay+"&direction=TB&shape=ellipse\">CTG</a>";
		table += "     ";
		table += "<a title = \"Delay Rate Graph\" href=\"/delayRateGraph.jsp?taskid="+TaskID+"&delay="+TaskDelay+"\">DRG</a>";
		table += "</td>";
		table += "</tr>";
        }
	table += "</table>";
	return table;
}
%>
