package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import edu.nudt.xtrace.*;
import java.util.*;
import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.IOException;

public final class callTreeGraph_005fabnormal_jsp extends org.apache.jasper.runtime.HttpJspBase
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
      out.write("\n");
      out.write("\n");
      out.write("<HTML>\n");
      out.write("<head>\n");
      out.write("<title>abnormal Call Tree Graph of ");
      out.print( request.getParameter("taskid") );
      out.write("</title>\n");
      out.write("</head>\n");
      out.write("<BODY>\n");
      edu.nudt.xtrace.MySQLAccessor accessor = null;
      synchronized (session) {
        accessor = (edu.nudt.xtrace.MySQLAccessor) _jspx_page_context.getAttribute("accessor", PageContext.SESSION_SCOPE);
        if (accessor == null){
          accessor = new edu.nudt.xtrace.MySQLAccessor();
          _jspx_page_context.setAttribute("accessor", accessor, PageContext.SESSION_SCOPE);
        }
      }
      out.write('\n');
      edu.nudt.xtrace.Trace trace = null;
      synchronized (session) {
        trace = (edu.nudt.xtrace.Trace) _jspx_page_context.getAttribute("trace", PageContext.SESSION_SCOPE);
        if (trace == null){
          trace = new edu.nudt.xtrace.Trace();
          _jspx_page_context.setAttribute("trace", trace, PageContext.SESSION_SCOPE);
        }
      }
      out.write('\n');

String delay = request.getParameter("delay");
String taskID = request.getParameter("taskid");
String temp=application.getRealPath(request.getRequestURI());  
String fileDir=new File(temp).getParent();
String fileName = "abnormal_CTG_tmp";
String imagePath=fileDir+"/tmp/" + fileName + ".svg";
String dotPath = fileDir+"/tmp/" + fileName + ".dot";

      out.write("\n");
      out.write("<a href=\"/\">Return Home</a>&gt;&gt;<a href=\"/\">Tasks</a>&gt;&gt;<B>abnormalCTG</B> | <a href=\"op_index.jsp\">Operations</a><hr> \n");
      out.write("<br>\n");

File image = new File(imagePath); 
File dot = new File(dotPath);
if(image.exists())image.delete();
if(dot.exists())dot.delete();
ArrayList<DiagnosisResult> drList=(ArrayList<DiagnosisResult>)session.getAttribute("abnormalTaskInfo");
if(drList == null)
	response.sendRedirect("/callTreeGraph.jsp?taskid="+taskID+"&delay="+delay+"&direction=TB&shape=ellipse");
DiagnosisResult dr=null;
for(int i=0;i<drList.size();i++){
	dr = drList.get(i);
	if(taskID.equals(dr.getTaskID()))
		break;
}
if(dr == null)
	response.sendRedirect("/callTreeGraph.jsp?taskid="+taskID+"&delay="+delay+"&direction=TB&shape=ellipse");
if(trace.findTopology(accessor.getConn(), taskID, delay) == null)
	response.sendRedirect("/callTreeGraph.jsp?taskid="+taskID+"&delay="+delay+"&direction=TB&shape=ellipse");
if(trace.genAbnormalTxt(dotPath, dr)==false)
	response.sendRedirect("/callTreeGraph.jsp?taskid="+taskID+"&delay="+delay+"&direction=TB&shape=ellipse");

String cmd = "dot -Tsvg "+ dotPath + " -o " +  imagePath;
Process process = Runtime.getRuntime().exec(cmd); 
process.waitFor();
out.println("<p><embed src=\"tmp/" + fileName + ".svg" +"\" type=\"image/svg+xml\" />");

      out.write("\n");
      out.write("</BODY>\n");
      out.write("<HTML>\n");
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
