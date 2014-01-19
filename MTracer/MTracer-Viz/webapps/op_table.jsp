<%@ page import="java.text.DateFormat"%>
<%@ page import="java.text.SimpleDateFormat"%>

<%!
public String showOperations(List<OperationRecord> operations)
{
	String table;
	table = "<table border=1 cellspacing=0 cellpadding=3>";
	table += "<tr>";
	table += "<th width=250>OpName</th>";
	table += "<th width=60>Num</th>";
	table += "<th width=110>MaxDelay</th>";
	table += "<th width=110>MinDelay</th>";
	table += "<th width=110>AverageDelay</th>";
	table += "<th width=120>Tool</th>";
	table += "</tr>";
	Iterator<OperationRecord> it = operations.iterator();
	while (it.hasNext()) {
        	OperationRecord operation = it.next();
		table += "<tr>";
		table += "<td>" + operation.getOpName()+ "</td>";
		table += "<td><a title = \"see all tasks with this operation\" href=\"/op_tasksWithOp.jsp?opName="+operation.getOpName()+"\">" + operation.getNum() + "</a></td>";
		table += "<td align = \"right\"><a title = \"see the task with maxDelay\" href=\"/op_tasksWithOp.jsp?opName="+operation.getOpName()+"&delayFrom="+operation.getMaxDelay()+"&delayTo="+operation.getMaxDelay()+"\">" + operation.getMaxDelay() + "ms</td>";
		table += "<td align = \"right\"><a title = \"see the task with minDelayy\" href=\"/op_tasksWithOp.jsp?opName="+operation.getOpName()+"&delayFrom="+operation.getMinDelay()+"&delayTo="+operation.getMinDelay()+"\">" + operation.getMinDelay() + "ms</td>";
		table += "<td align = \"right\">" + operation.getAverageDelay() + "ms</td>";
		table += "<td align = \"center\">";
		table += "<a title = \"see the Delay Distribute Graph of this operation\" href=\"/op_delayDistributeGraph.jsp?opName="+operation.getOpName()+"\">DDG</a>";
		table += "          ";
		table += "<a title = \"see the Host Distribute Graph of this operation\" href=\"/op_hostDistributeGraph.jsp?opName="+operation.getOpName()+"\">HDG</a>";
		table += "          ";
		table += "<a title = \"see the Delay Trend Graph of this operation\" href=\"/op_delayTrendGraph.jsp?opName="+operation.getOpName()+"\">DTG</a>";
		table += "</td>";
		table += "</tr>";
        }
	table += "</table>";
	return table;
}
%>
