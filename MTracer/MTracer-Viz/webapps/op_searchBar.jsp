<HTML>
<form action="" method="get" name="searchForm">
	<B>Search by:</B>
	OpName <input title = "use ',' as delimiter,use '*' as wildcard. e.g. 'RPC*,*write*'"style="width:180" name="opName" value= "<%= request.getParameter("opName")==null ? "*" : request.getParameter("opName")%>" /> 
	Num from <input style="width:78" name="numFrom" value= "<%= request.getParameter("numFrom")==null ? "1" : request.getParameter("numFrom")%>" />
	to <input style="width:78" name="numTo" value= "<%= request.getParameter("numTo")==null ? "*" : request.getParameter("numTo")%>" />
	MaxDelay from <input style="width:80" name="maxDelayFrom" value="<%= request.getParameter("maxDelayFrom")==null ? "0" : request.getParameter("maxDelayFrom")%>" />
	to <input style="width:80" name="maxDelayTo" value="<%= request.getParameter("maxDelayTo")==null ? "*" : request.getParameter("maxDelayTo")%>" />
	| <input style="width:80" type="button" value="reset" onclick="window.location='http://localhost:8080/op_index.jsp'"/><p>
	MinDelay from <input style="width:80" name="minDelayFrom" value="<%= request.getParameter("minDelayFrom")==null ? "0" : request.getParameter("minDelayFrom")%>" />
	to <input style="width:80" name="minDelayTo" value="<%= request.getParameter("minDelayTo")==null ? "*" : request.getParameter("minDelayTo")%>" />
	AverageDelay from <input style="width:80" name="averageDelayFrom" value="<%= request.getParameter("averageDelayFrom")==null ? "0" : request.getParameter("averageDelayFrom")%>" />
	to <input style="width:80" name="averageDelayTo" value="<%= request.getParameter("averageDelayTo")==null ? "*" : request.getParameter("averageDelayTo")%>" />
	sorted by 
	<%
	session.setAttribute("is_select_opName","");
	session.setAttribute("is_select_num","");
	session.setAttribute("is_select_maxDelay","");
	session.setAttribute("is_select_minDelay","");
	session.setAttribute("is_select_averageDelay","");
	if(request.getParameter("sortValue")==null)
		session.setAttribute("is_select_opName", "selected");
	else
		session.setAttribute("is_select_"+request.getParameter("sortValue"), "selected");
	%>
	<select name="sortValue">
		<option <%= session.getAttribute("is_select_opName") %> value="opName">OpName</option>
		<option <%= session.getAttribute("is_select_num") %> value="num">Num</option>
		<option <%= session.getAttribute("is_select_maxDelay") %> value="maxDelay">MaxDelay</option>
		<option <%= session.getAttribute("is_select_minDelay") %> value="minDelay">MinDelay</option>
		<option <%= session.getAttribute("is_select_averageDelay") %> value="averageDelay">AverageDelay</option>
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
