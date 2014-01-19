<HTML>
<form action="" method="get" name="searchForm">
	<B>Search by:</B>
	OpName <input title = "use ',' as delimiter,use '*' as wildcard. e.g. 'RPC*,*write*'" style="width:180" name="opName" value= "<%= request.getParameter("opName")==null ? "*" : request.getParameter("opName")%>" /> 
	Delay from <input style="width:78" name="delayFrom" value= "<%= request.getParameter("delayFrom")==null ? "0" : request.getParameter("delayFrom")%>" />
	to <input style="width:78" name="delayTo" value= "<%= request.getParameter("delayTo")==null ? "*" : request.getParameter("delayTo")%>" />
	HostAddress <input title = "use ',' as delimiter,use '*' as wildcard. e.g. '127.*,192.168*'" style="width:120" name="address" value= "<%= request.getParameter("address")==null ? "*" : request.getParameter("address")%>" />
	HostName <input title = "use ',' as delimiter,use '*' as wildcard. e.g. 'zjw8612*, *W510'" style="width:120" name="name" value= "<%= request.getParameter("name")==null ? "*" : request.getParameter("name")%>" />
	Agent <input title = "use ',' as delimiter,use '*' as wildcard. e.g. '*node, RPC*'" style="width:85" name="agent" value= "<%= request.getParameter("agent")==null ? "*" : request.getParameter("agent")%>" />
	| <input style="width:80" type="button" value="reset" onclick="window.location='http://localhost:8080/op_tasksWithOp.jsp'"/><p>
	Trace Title <input title = "use ',' as delimiter,use '*' as wildcard. e.g. '*ls*,*rm *'"style="width:180" name="title" value= "<%= request.getParameter("title")==null ? "*" : request.getParameter("title")%>" /> 
	Time from <input style="width:150" name="timeFrom" value= "<%= request.getParameter("timeFrom")==null ? "1970-01-01 00:00:00" : request.getParameter("timeFrom")%>" />
	to <input style="width:150" name="timeTo" value= "<%= request.getParameter("timeTo")==null ? "*" : request.getParameter("timeTo")%>" />
	TaskID: <input title = "use ',' as delimiter,use '*' as wildcard. e.g. 'CC81DC28AAD1936F,*8A*D1936F'" style="width:157" name="taskID" value="<%= request.getParameter("taskID")==null ? "*" : request.getParameter("taskID")%>" />
	
	sorted by 
	<%
	session.setAttribute("is_select_OpName","");
	session.setAttribute("is_select_Delay","");
	session.setAttribute("is_select_HostName","");
	session.setAttribute("is_select_HostAddress","");
	session.setAttribute("is_select_Agent","");
	session.setAttribute("is_select_Title","");
	session.setAttribute("is_select_NumReports","");
	session.setAttribute("is_select_NumEdges","");
	session.setAttribute("is_select_TaskDelay","");
	session.setAttribute("is_select_FirstSeen","");
	session.setAttribute("is_select_LastUpdated","");
	session.setAttribute("is_select_TaskID","");
	if(request.getParameter("sortValue")==null)
		session.setAttribute("is_select_opName", "selected");
	else
		session.setAttribute("is_select_"+request.getParameter("sortValue"), "selected");
	%>
	<select name="sortValue">
		<option <%= session.getAttribute("is_select_Delay") %> value="Delay">Delay</option>
		<option <%= session.getAttribute("is_select_OpName") %> value="OpName">OpName</option>
		<option <%= session.getAttribute("is_select_HostName") %> value="HostName">HostName</option>
		<option <%= session.getAttribute("is_select_HostAddress") %> value="HostAddress">HostAddress</option>
		<option <%= session.getAttribute("is_select_Agent") %> value="Agent">Agent</option>
		<option <%= session.getAttribute("is_select_Title") %> value="Title">Title</option>
		<option <%= session.getAttribute("is_select_NumReports") %> value="NumReports">NumReports</option>
		<option <%= session.getAttribute("is_select_NumEdges") %> value="NumEdges">NumEdges</option>
		<option <%= session.getAttribute("is_select_TaskDelay") %> value="TaskDelay">TaskDelay</option>
		<option <%= session.getAttribute("is_select_FirstSeen") %> value="FirstSeen">FirstSeen</option>
		<option <%= session.getAttribute("is_select_LastUpdated") %> value="LastUpdated">LastUpdated</option>
		<option <%= session.getAttribute("is_select_TaskID") %> value="TaskID">TaskID</option>
	</select>
	<%
	session.setAttribute("is_select_default","");
	session.setAttribute("is_select_descend","");
	session.setAttribute("is_select_ascend","");
	if(request.getParameter("sortStyle")==null)
		session.setAttribute("is_select_default", "selected");
	else
		session.setAttribute("is_select_"+request.getParameter("sortStyle"), "selected");
	%>
	<select name="sortStyle" index = 2>
		<option <%= session.getAttribute("is_select_default") %> value="default">default</option>
		<option <%= session.getAttribute("is_select_descend") %> value="descend">descend</option>
		<option <%= session.getAttribute("is_select_ascend") %> value="ascend">ascend</option>
	</select>
	| <input style="width:80" type="submit" value="search" />
</form>
</HTML>
