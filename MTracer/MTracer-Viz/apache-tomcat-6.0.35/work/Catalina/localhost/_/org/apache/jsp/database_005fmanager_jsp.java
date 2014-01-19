package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import java.util.*;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;

public final class database_005fmanager_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static final JspFactory _jspxFactory = JspFactory.getDefaultFactory();

  private static java.util.List _jspx_dependants;

  private javax.el.ExpressionFactory _el_expressionfactory;
  private org.apache.AnnotationProcessor _jsp_annotationprocessor;

  public Object getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _el_expressionfactory = _jspxFactory.getJspApplicationContext(getServletConfig().getServletContext()).getExpressionFactory();
    _jsp_annotationprocessor = (org.apache.AnnotationProcessor) getServletConfig().getServletContext().getAttribute(org.apache.AnnotationProcessor.class.getName());
  }

  public void _jspDestroy() {
  }

  public void _jspService(HttpServletRequest request, HttpServletResponse response)
        throws java.io.IOException, ServletException {

    PageContext pageContext = null;
    HttpSession session = null;
    ServletContext application = null;
    ServletConfig config = null;
    JspWriter out = null;
    Object page = this;
    JspWriter _jspx_out = null;
    PageContext _jspx_page_context = null;


    try {
      response.setContentType("text/html;charset=UTF-8");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;

      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");


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

      out.write('\n');
      out.write('\n');
    } catch (Throwable t) {
      if (!(t instanceof SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          try { out.clearBuffer(); } catch (java.io.IOException e) {}
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
      }
    } finally {
      _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }
}
