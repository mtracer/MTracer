<%
String topoID = request.getParameter("topoID");
String title = request.getParameter("title");
if(topoID == null || title == null){
	out.print("<font color = red>WARNING: some mistakes occurred</font><p>");
}else{
	title = title.replaceAll("-","");
	title = title.replaceAll(" ","_");
	out.println("<p><embed src=\"classification/"+title + "/type_"+topoID+".svg" +"\" type=\"image/svg+xml\" />");
}
%>
