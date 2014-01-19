<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.io.File" %>

<HTML>
<head>
<title>change database</title>
</head>
<BODY>
<a href="/">Return Home</a>&gt;&gt;<B>change database</B> | <a href="index.jsp">Tasks</a> | <a href="op_index.jsp">Operations</a> 
<hr/>

<script language="javascript">
function onChangeDB(dbname){
        if(dbname.length==0)
                alert("No database selected");
        else{
                var r=confirm("this operation will drop current database, \nsave current database first!");
                if (r==true)
                {
                        var r2=confirm("You really sure you saved current database?\n It cann't be recovery");
                        if (r2==true)
                        {
                                window.location.href="database_manager.jsp?OpType=change&dbname="+dbname;
                        }
                }
        }
}
function onSaveCurrentDB(){
	var d=new Date();
	var defaultName=""+d.getFullYear()+"-"+d.getMonth()+"-"+d.getDate()+"-"+d.getHours()+"-"+d.getMinutes()+"-"+d.getSeconds()+".sql";
	var dbname=prompt("input name:",defaultName);
	if(dbname!=null && dbname!="")
		window.location.href="database_manager.jsp?OpType=saveCurrent&dbname="+dbname;
}

function onRenameDB(dbname){
	var newName=prompt("new name:",dbname);
	if(newName!=null && newName!="")
		window.location.href="database_manager.jsp?OpType=rename&dbname="+dbname+"&newName="+newName;
}

function onDeleteDB(dbname){
	var r=confirm("You sure?");
	if(r==true)
		window.location.href="database_manager.jsp?OpType=delete&dbname="+dbname;
}
function onOpenFile(name){
        window.location.href="database_manager.jsp?OpType=open&dbname="+name;
}

</script>

<input title = "save current database" style="width:200" type="button" value="save current database" onClick = "onSaveCurrentDB(); return false;"/><p>

<table border=1 cellspacing=0 cellpadding=3>
<tr>
<th width=500>database</th>
<th width=250>tool</th>
</tr>

<%
String temp=application.getRealPath(request.getRequestURI());
String fileDir=new File(temp).getParent();
File file = new File(fileDir+"/database");
if(file.exists()){
	String[] files = file.list();
	for(int i=0;i<files.length;i++){
		for(int j=0;j<i;j++){
			if(files[i].compareTo(files[j])<0){
				String tmpStr=files[i];
				files[i]=files[j];
				files[j]=tmpStr;
			}
		}
	}
	for(int i=0;i<files.length;i++){
		out.println("<tr>");
		out.println("<td>"+files[i]+"</td>");
		%>
		<td>
		<%
		if(files[i].endsWith(".sql"))
			out.println("<input title = \"use this one as current database\" style=\"width:80\" type=\"button\" value=\"enable\" onClick = \"onChangeDB('"+files[i]+"'); return false;\"/>");
		else
			out.println("<input title = \"read this file\" style=\"width:80\" type=\"button\" value=\"open\" onClick = \"onOpenFile('"+files[i]+"'); return false;\"/>");
		%>
			
		<input title = "rename this database" style="width:80" type="button" value="rename" onClick = "onRenameDB('<%=files[i]%>'); return false;"/>
		<input title = "delete this database" style="width:80" type="button" value="delete" onClick = "onDeleteDB('<%=files[i]%>'); return false;"/>
		</td>
		<%
		out.println("</tr>");
	}
}
%>
</table>
</BODY>
