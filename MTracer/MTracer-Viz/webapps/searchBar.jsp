<HTML>
<form action="" method="get" name="searchForm">
	<B>Search by:</B>
	Title <input title = "use ',' as delimiter,use '*' as wildcard. e.g. '*ls*,*rm *'"style="width:180" name="title" value= "<%= request.getParameter("title")==null ? "*" : request.getParameter("title")%>" /> 
	Receive time from <input style="width:150" name="timeFrom" value= "<%= request.getParameter("timeFrom")==null ? "1970-01-01 00:00:00" : request.getParameter("timeFrom")%>" />
	to <input style="width:150" name="timeTo" value= "<%= request.getParameter("timeTo")==null ? "*" : request.getParameter("timeTo")%>" />
	Delay from <input style="width:80" name="delayFrom" value="<%= request.getParameter("delayFrom")==null ? "0" : request.getParameter("delayFrom")%>" />
	to <input style="width:80" name="delayTo" value="<%= request.getParameter("delayTo")==null ? "*" : request.getParameter("delayTo")%>" />
	| <input style="width:80" type="button" value="reset" onclick="window.location='/'"/><p>
	TaskID: <input title = "use ',' as delimiter,use '*' as wildcard. e.g. 'CC81DC28AAD1936F,*8A*D1936F'" style="width:157" name="taskID" value="<%= request.getParameter("taskID")==null ? "*" : request.getParameter("taskID")%>" />
	Reports number from <input style="width:50" name="numReportsFrom" value="<%= request.getParameter("numReportsFrom")==null ? "1" : request.getParameter("numReportsFrom")%>" />
	to <input style="width:50" name="numReportsTo" value="<%= request.getParameter("numReportsTo")==null ? "*" : request.getParameter("numReportsTo")%>" />
	Edges number from <input style="width:50" name="numEdgesFrom" value="<%= request.getParameter("numEdgesFrom")==null ? "0" : request.getParameter("numEdgesFrom")%>" />
	to <input style="width:50" name="numEdgesTo" value="<%= request.getParameter("numEdgesTo")==null ? "*" : request.getParameter("numEdgesTo")%>" />
	<input type=hidden name="sessionKey" value="<%= request.getParameter("sessionKey")==null ? "*" : request.getParameter("sessionKey")%>" />
	sorted by 
	<%
	session.setAttribute("is_select_time","");
	session.setAttribute("is_select_delay","");
	session.setAttribute("is_select_numReport","");
	session.setAttribute("is_select_numEdges","");
	session.setAttribute("is_select_title","");
	session.setAttribute("is_select_taskID","");
	if(request.getParameter("sortValue")==null)
		session.setAttribute("is_select_time", "selected");
	else
		session.setAttribute("is_select_"+request.getParameter("sortValue"), "selected");
	%>
	<select name="sortValue">
		<option <%= session.getAttribute("is_select_time") %> value="time">FirstSeen</option>
		<option <%= session.getAttribute("is_select_delay") %> value="delay">Delay</option>
		<option <%= session.getAttribute("is_select_numReport") %> value="numReport">NumReports</option>
		<option <%= session.getAttribute("is_select_numEdges") %> value="numEdges">NumEdges</option>
		<option <%= session.getAttribute("is_select_title") %> value="title">Title</option>
		<option <%= session.getAttribute("is_select_taskID") %> value="taskID">TaskID</option>
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
