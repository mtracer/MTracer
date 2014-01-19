package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import java.io.File;

public final class database_005flist_jsp extends org.apache.jasper.runtime.HttpJspBase
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
      out.write("<HTML>\n");
      out.write("<head>\n");
      out.write("<title>change database</title>\n");
      out.write("</head>\n");
      out.write("<BODY>\n");
      out.write("<a href=\"/\">Return Home</a>&gt;&gt;<B>change database</B> | <a href=\"index.jsp\">Tasks</a> | <a href=\"op_index.jsp\">Operations</a> \n");
      out.write("<hr/>\n");
      out.write("\n");
      out.write("<script language=\"javascript\">\n");
      out.write("function onChangeDB(dbname){\n");
      out.write("        if(dbname.length==0)\n");
      out.write("                alert(\"No database selected\");\n");
      out.write("        else{\n");
      out.write("                var r=confirm(\"this operation will drop current database, \\nsave current database first!\");\n");
      out.write("                if (r==true)\n");
      out.write("                {\n");
      out.write("                        var r2=confirm(\"You really sure you saved current database?\\n It cann't be recovery\");\n");
      out.write("                        if (r2==true)\n");
      out.write("                        {\n");
      out.write("                                window.location.href=\"database_manager.jsp?OpType=change&dbname=\"+dbname;\n");
      out.write("                        }\n");
      out.write("                }\n");
      out.write("        }\n");
      out.write("}\n");
      out.write("function onSaveCurrentDB(){\n");
      out.write("\tvar d=new Date();\n");
      out.write("\tvar defaultName=\"\"+d.getFullYear()+\"-\"+d.getMonth()+\"-\"+d.getDate()+\"-\"+d.getHours()+\"-\"+d.getMinutes()+\"-\"+d.getSeconds()+\".sql\";\n");
      out.write("\tvar dbname=prompt(\"input name:\",defaultName);\n");
      out.write("\tif(dbname!=null && dbname!=\"\")\n");
      out.write("\t\twindow.location.href=\"database_manager.jsp?OpType=saveCurrent&dbname=\"+dbname;\n");
      out.write("}\n");
      out.write("\n");
      out.write("function onRenameDB(dbname){\n");
      out.write("\tvar newName=prompt(\"new name:\",dbname);\n");
      out.write("\tif(newName!=null && newName!=\"\")\n");
      out.write("\t\twindow.location.href=\"database_manager.jsp?OpType=rename&dbname=\"+dbname+\"&newName=\"+newName;\n");
      out.write("}\n");
      out.write("\n");
      out.write("function onDeleteDB(dbname){\n");
      out.write("\tvar r=confirm(\"You sure?\");\n");
      out.write("\tif(r==true)\n");
      out.write("\t\twindow.location.href=\"database_manager.jsp?OpType=delete&dbname=\"+dbname;\n");
      out.write("}\n");
      out.write("function onOpenFile(name){\n");
      out.write("        window.location.href=\"database_manager.jsp?OpType=open&dbname=\"+name;\n");
      out.write("}\n");
      out.write("\n");
      out.write("</script>\n");
      out.write("\n");
      out.write("<input title = \"save current database\" style=\"width:200\" type=\"button\" value=\"save current database\" onClick = \"onSaveCurrentDB(); return false;\"/><p>\n");
      out.write("\n");
      out.write("<table border=1 cellspacing=0 cellpadding=3>\n");
      out.write("<tr>\n");
      out.write("<th width=500>database</th>\n");
      out.write("<th width=250>tool</th>\n");
      out.write("</tr>\n");
      out.write("\n");

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
		
      out.write("\n");
      out.write("\t\t<td>\n");
      out.write("\t\t");

		if(files[i].endsWith(".sql"))
			out.println("<input title = \"use this one as current database\" style=\"width:80\" type=\"button\" value=\"enable\" onClick = \"onChangeDB('"+files[i]+"'); return false;\"/>");
		else
			out.println("<input title = \"read this file\" style=\"width:80\" type=\"button\" value=\"open\" onClick = \"onOpenFile('"+files[i]+"'); return false;\"/>");
		
      out.write("\n");
      out.write("\t\t\t\n");
      out.write("\t\t<input title = \"rename this database\" style=\"width:80\" type=\"button\" value=\"rename\" onClick = \"onRenameDB('");
      out.print(files[i]);
      out.write("'); return false;\"/>\n");
      out.write("\t\t<input title = \"delete this database\" style=\"width:80\" type=\"button\" value=\"delete\" onClick = \"onDeleteDB('");
      out.print(files[i]);
      out.write("'); return false;\"/>\n");
      out.write("\t\t</td>\n");
      out.write("\t\t");

		out.println("</tr>");
	}
}

      out.write("\n");
      out.write("</table>\n");
      out.write("</BODY>\n");
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
