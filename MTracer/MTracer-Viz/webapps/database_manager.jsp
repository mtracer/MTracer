<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="java.util.*"%>
<%@ page import="java.io.File" %>
<%@ page import="java.io.FileReader" %>
<%@ page import="java.io.BufferedReader" %>
<%@ page import="java.io.BufferedWriter" %>
<%@ page import="java.io.FileWriter" %>
<%

String OpType=request.getParameter("OpType");
if(OpType==null || OpType.equals("")){
	return;
}

String temp=application.getRealPath(request.getRequestURI());
String dir=new File(temp).getParent();

if(OpType.equals("saveCurrent"))
{
	String dbname=request.getParameter("dbname");
	if(dbname!=null || !dbname.equals("")){
		File file = new File(dir+"/database/"+dbname);
		if(file.exists()){
			out.println("file exists, use other name!");
			return;
		}
	}

	if(dbname!=null || !dbname.equals("")){
		
		File file = new File(dir+"/tmp/saveDBcmd.sh");
		if(file.exists())
			file.delete();
		BufferedWriter bw=new BufferedWriter(new FileWriter(file));
		bw.write("#!/bin/bash");
		bw.newLine();
		bw.write("mysqldump -u root -proot xtrace > "+dir+"/database/"+dbname);
		bw.flush();
		bw.close();
		
	      	Process process = Runtime.getRuntime().exec("chmod a+x "+dir+"/tmp/saveDBcmd.sh");
	       	process.waitFor();
		process = Runtime.getRuntime().exec(dir+"/tmp/saveDBcmd.sh");
	}
}

else if(OpType.equals("change"))
{
	String dbname=request.getParameter("dbname");
        if(dbname!=null || !dbname.equals("")){
		File file = new File(dir+"/tmp/changeDBcmd.sh");
		if(file.exists())
                        file.delete();
		BufferedWriter bw=new BufferedWriter(new FileWriter(file));
                bw.write("#!/bin/bash");
                bw.newLine();
		bw.write("mysql -u root -proot xtrace < "+dir+"/database/"+dbname);
                bw.flush();
                bw.close();
		
		Process process = Runtime.getRuntime().exec("chmod a+x "+dir+"/tmp/changeDBcmd.sh");
		process.waitFor();
                process = Runtime.getRuntime().exec(dir+"/tmp/changeDBcmd.sh");                
	}
}

else if(OpType.equals("rename"))
{
	String oldname=request.getParameter("dbname");
	String newname=request.getParameter("newName");
        if(newname!=null || !newname.equals("")){
                File file = new File(dir+"/database/"+newname);
                if(file.exists()){
                        out.println("file exists, use other name!");
                        return;
                }
        }

        if(oldname!=null || !oldname.equals("") || newname!=null || !newname.equals("")){
                String cmd = "mv "+dir+"/database/"+oldname+" "+dir+"/database/"+newname; 
                Process process = Runtime.getRuntime().exec(cmd);
                process.waitFor();
		out.println(cmd);
        }
}

else if(OpType.equals("delete"))
{
	String dbname=request.getParameter("dbname");
        if(dbname!=null || !dbname.equals("")){
		String cmd = "mv "+dir+"/database/"+dbname+" "+dir+"/deletedDB/"+dbname;
                out.println(cmd);
              	Process process = Runtime.getRuntime().exec(cmd);
		process.waitFor();
	}
}

else if(OpType.equals("open"))
{
	String dbname=request.getParameter("dbname");
	FileReader fr=new FileReader(dir+"/database/"+dbname);
	BufferedReader br=new BufferedReader(fr);
	String content=br.readLine();
	while(content!=null){
		out.print(content);
		out.print("<p>");
		content=br.readLine();
	}
	br.close();
	fr.close();
	return;
}
String url=request.getHeader("Referer");
if(OpType.equals("change"))
	response.sendRedirect("/");
else
	response.sendRedirect(url);
%>

