package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import edu.nudt.xtrace.*;
import java.util.*;

public final class op_005fdelayTrendGraph_jsp extends org.apache.jasper.runtime.HttpJspBase
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
      out.write("<HTML>\n");
      out.write("<head>\n");
      out.write("<title>Delay Trend Graph</title>\n");
      out.write("</head>\n");
      out.write("<BODY>\n");
      out.write("<FONT style=\"FONT-FAMILY: sans-serif\">\n");
      out.write("<div style=\"line-height:1\"> \n");
      edu.nudt.xtrace.MySQLAccessor accessor = null;
      synchronized (session) {
        accessor = (edu.nudt.xtrace.MySQLAccessor) _jspx_page_context.getAttribute("accessor", PageContext.SESSION_SCOPE);
        if (accessor == null){
          accessor = new edu.nudt.xtrace.MySQLAccessor();
          _jspx_page_context.setAttribute("accessor", accessor, PageContext.SESSION_SCOPE);
        }
      }
      out.write('\n');

int num = 0;
double maxDelay = -1;
String partUrl="";
Report condition = new Report();
Report title = new Report();
Enumeration para=request.getParameterNames();
while(para.hasMoreElements()){
	String key=(String)para.nextElement();
	String value=request.getParameter(key);
	if(!key.equals("sortValue")&&!key.equals("sortStyle"))
		condition.put(key, value);
	partUrl += key + "=" + value + "&";
}
if(!partUrl.isEmpty())
	partUrl = partUrl.substring(0,partUrl.length()-1);
condition.put("sortValue","FirstSeen");
condition.put("sortStyle","ascend");

      out.write("\n");
      out.write("<a href=\"/\">Return Home</a> &gt;&gt;<a href=\"op_index.jsp\">Operations</a> &gt;&gt;<a href=\"op_tasksWithOp.jsp?");
      out.print(partUrl);
      out.write("\">Operation Detail</a>&gt;&gt;\n");
      out.write("<a href=\"op_delayDistributeGraph.jsp?");
      out.print(partUrl);
      out.write("\">DDG</a> <a href=\"op_hostDistributeGraph.jsp?");
      out.print(partUrl);
      out.write("\">HDG</a> <B>DTG</B> | <a href=\"index.jsp\">Tasks</a>\n");
      out.write("</div><hr/>\n");
      out.write("<form action=\"\" method=\"get\" name=\"changeForm\">\n");
      out.write("\t<B>Time from</B>\n");
      out.write("\t<input type=hidden name=\"opName\" value= \"");
      out.print( request.getParameter("opName")==null ? "*" : request.getParameter("opName"));
      out.write("\" /> \n");
      out.write("\t<input type=hidden name=\"delayFrom\" value= \"");
      out.print( request.getParameter("delayFrom")==null ? "0" : request.getParameter("delayFrom"));
      out.write("\" />\n");
      out.write("\t<input type=hidden name=\"delayTo\" value= \"");
      out.print( request.getParameter("delayTo")==null ? "*" : request.getParameter("delayTo"));
      out.write("\" />\n");
      out.write("\t<input type=hidden name=\"address\" value= \"");
      out.print( request.getParameter("address")==null ? "*" : request.getParameter("address"));
      out.write("\" />\n");
      out.write("\t<input type=hidden name=\"name\" value= \"");
      out.print( request.getParameter("name")==null ? "*" : request.getParameter("name"));
      out.write("\" />\n");
      out.write("\t<input type=hidden name=\"agent\" value= \"");
      out.print( request.getParameter("agent")==null ? "*" : request.getParameter("agent"));
      out.write("\" />\n");
      out.write("\t<input type=hidden name=\"title\" value= \"");
      out.print( request.getParameter("title")==null ? "*" : request.getParameter("title"));
      out.write("\" /> \n");
      out.write("\t<input type=hidden name=\"taskID\" value=\"");
      out.print( request.getParameter("taskID")==null ? "*" : request.getParameter("taskID"));
      out.write("\" />\n");
      out.write("\t<input style=\"width:180\" name=\"timeFrom\" value= \"");
      out.print( request.getParameter("timeFrom")==null ? "1970-01-01 00:00:00" : request.getParameter("timeFrom"));
      out.write("\" /> \n");
      out.write("\tto <input style=\"width:180\" name=\"timeTo\" value= \"");
      out.print( request.getParameter("timeTo")==null ? "*" : request.getParameter("timeTo"));
      out.write("\" /> \n");
      out.write("\t<input style=\"width:80\" type=\"submit\" value=\"change\" />\n");
      out.write("</form>\n");
      out.write("\n");

String opName = request.getParameter("opName")==null?"*":request.getParameter("opName");
if(opName.indexOf(",")!=-1 || opName.indexOf("*")!=-1) 
	out.print("<font color = red>WARNING: There may be more than 1 kind of OPs(OpName = " + opName + "), the DTG maybe meanless</font><p>");
int off = 0;int len =50;
List<Report> taskWithOp=accessor.getTasksByOp(condition, title, off, len);
while(taskWithOp.size()>0)
{
	int currentRecordNum=0;
	//taskWithOp=accessor.getTasksByOp(condition, title, off, len);
	Iterator<Report> it = taskWithOp.iterator();
	while (it.hasNext()) {
		currentRecordNum++;
		Report rep = it.next();
		double delay=-1;
		try{delay=Double.parseDouble(rep.get("Delay").get(0));}catch(Exception e){continue;}
        	if(delay>maxDelay)
			maxDelay=delay;
		num++;
        }
	off = off + currentRecordNum;
	taskWithOp=accessor.getTasksByOp(condition, title, off, len);
}

double factor= (double)100/maxDelay;
String table;
table = "<table border=1 cellspacing=0 cellpadding=3>";
table += "<tr>";
table += "<th width=200>Time</th>";
table += "<th>Delay</th>";
table += "<th width=400>Delay graph</th>";
table += "</tr>";
String nextTask = "notask";
String color = "blue";
off=0;len=25;
while(off<num)
{
	taskWithOp=accessor.getTasksByOp(condition, title, off, len);
	Iterator<Report> it = taskWithOp.iterator();
	while (it.hasNext()) {
        	Report rep = it.next();
		double delay;
		try{delay=Double.parseDouble(rep.get("Delay").get(0));}catch(Exception e){continue;}
		String timestamp;
		try{timestamp=rep.get("FirstSeen").get(0);}catch(Exception e){continue;}
		String taskID;
		try{taskID=rep.get("TaskID").get(0);}catch(Exception e){taskID="*";}
		String curTask="";
		try{curTask=rep.get("TaskID").get(0);}catch(Exception e){color=color=="blue"?"black":"blue";}
		if(!curTask.equals(nextTask))color=color=="blue"?"black":"blue";
		table += "<tr>";
		table += "<td align = center><a title = \"see all related tasks\" href=\"/op_tasksWithOp.jsp?opName="+opName+"&delayFrom="+delay+"&delayTo="+delay+"&timeFrom="+timestamp+"&timeTo="+timestamp+"&taskID="+taskID+"\">" + timestamp + "</td>";
		table += "<td align = right><font color="+color+">" + delay + "ms</td>";
		table += "<td><font color="+color+">";
		for(int j=0;j<(int)(factor*delay);j++)
			table+="\\"; 
		table += "<font></td>";
		table += "</tr>";
		nextTask=curTask;
        }
	off = off + len;
}

out.print(table);

      out.write("\n");
      out.write("</FONT>\n");
      out.write("\n");
      out.write("</BODY>\n");
      out.write("<HTML>\n");
      out.write("\n");
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
