<%@ page import="java.text.DateFormat"%>
<%@ page import="java.text.SimpleDateFormat"%>
<form action="" method="get" name="hiddenForm">
	<input type=hidden name="taskID" /> 
</form>
<script language="javascript">
function onDelete(taskID){
	var r=confirm("Delete this task?");
	if (r==true)
	{
		window.location.href="deleteTask.jsp?"+"taskID="+taskID; 
	}
}
</script>
<%!
public String showTasks(List<TaskRecord> tasks, ArrayList<DiagnosisResult> drList, boolean isAbnormalList)
{
	DateFormat HTML_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
	String table;
	table = "<table border=1 cellspacing=0 cellpadding=3>";
	table += "<tr>";
	table += "<th width=250>Trace Title</th>";
	table += "<th width=60>Reports</th>";
	table += "<th width=60>Edges</th>";
	table += "<th width=100>Delay</th>";
	table += "<th width=170>FirstSeen</th>";
	table += "<th width=170>LastUpdated</th>";
	table += "<th width=160>TaskID</th>";
	if(isAbnormalList==true)
		table += "<th width=160>AbnormalOperations</th>";
	table += "<th width=140>Tool</th>";
	table += "</tr>";
	Iterator<TaskRecord> it = tasks.iterator();
	while (it.hasNext()) {
        	TaskRecord task = it.next();
		table += "<tr>";
		table += "<td><a title = \"see tasks with the same title\" href=\"/?title=" +task.getTitle()+ "\">" +task.getTitle()+ "</a></td>";
		table += "<td><a title = \"see all reports of this task\" href=\"/showReports.jsp?taskid="+task.getTaskId()+"\">" + task.getNumReports() + "</a></td>";
		table += "<td><a title = \"see all edges of this task\" href=\"/showEdges.jsp?taskid="+task.getTaskId()+"\">" + task.getNumEdges() + "</a></td>";
		table += "<td align = \"right\">" + task.getDelay() + "ms</td>";
		table += "<td align = \"center\">" + HTML_DATE_FORMAT.format(task.getFirstSeen()) + "</td>";
		table += "<td align = \"center\">" + HTML_DATE_FORMAT.format(task.getLastUpdated()) + "</td>";
		table += "<td align = \"center\"><tt>" + task.getTaskId() + "</tt></td>";
		if(isAbnormalList==true){
			String abop = "";				
			DiagnosisResult dr=null;
			for(int i=0;i<drList.size();i++){
				dr = drList.get(i);
				if(task.getTaskId().equals(dr.getTaskID()))
					break;
			}
			if(dr!=null){
				ArrayList<Integer> ops = dr.getAbnormalOperations();
				abop += ops.size()+": ";
				for(int i=0;i<ops.size();i++)
					abop += "["+ops.get(i)+"] ";
			}
			table += "<td title=\"the dfs index of abnormal operations \" align = \"left\">" + abop + "</td>";
		}
		table += "<td align = \"center\">";
		if(isAbnormalList == true)
			table += "<a title = \"abnormal Call Tree Graph\" href=\"/callTreeGraph_abnormal.jsp?taskid="+task.getTaskId()+"&delay="+task.getDelay()+"\">CTG</a>";
		else
			table += "<a title = \"Call Tree Graph\" href=\"/callTreeGraph.jsp?taskid="+task.getTaskId()+"&delay="+task.getDelay()+"&direction=TB&shape=ellipse\">CTG</a>";
		table += "     ";
		if(isAbnormalList == true)
			table += "<a title = \"abnormal Delay Rate Graph\" href=\"/delayRateGraph.jsp?taskid="+task.getTaskId()+"&delay="+task.getDelay()+"&isAbnormalTask=true\">DRG</a>";
		else
			table += "<a title = \"Delay Rate Graph\" href=\"/delayRateGraph.jsp?taskid="+task.getTaskId()+"&delay="+task.getDelay()+"\">DRG</a>";
		table += "     ";
		table += "<input title = \"delete this task\" style=\"width:50\" type=\"button\" value=\"delete\" onClick = \"onDelete('"+task.getTaskId()+"'); return false;\"/>";
		table += "</td>";
		table += "</tr>";
        }
	table += "</table>";
	return table;
}

public String showTasks(List<TaskRecord> tasks){
	return showTasks(tasks, null, false);
}
%>
