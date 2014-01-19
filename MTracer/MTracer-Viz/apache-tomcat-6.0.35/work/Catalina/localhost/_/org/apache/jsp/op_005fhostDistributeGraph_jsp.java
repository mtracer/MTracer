package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import edu.nudt.xtrace.*;
import java.util.*;

public final class op_005fhostDistributeGraph_jsp extends org.apache.jasper.runtime.HttpJspBase
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
      out.write("<title>Host Distribute Graph</title>\n");
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
Report condition = new Report();
Report title = new Report();
String partUrl="";
Enumeration para=request.getParameterNames();
while(para.hasMoreElements()){
	String key=(String)para.nextElement();
	String value=request.getParameter(key);
	if(!key.equals("sortValue")&&!key.equals("keyStyle"))
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
      out.write("\">Operation Detail</a>&gt;&gt; \n");
      out.write("<a href=\"op_delayDistributeGraph.jsp?");
      out.print(partUrl);
      out.write("\">DDG</a> <B>HDG</B> <a href=\"op_delayTrendGraph.jsp?");
      out.print(partUrl);
      out.write("\">DTG</a> | <a href=\"index.jsp\">Tasks</a>\n");
      out.write("</div><hr/>\n");

String opName = request.getParameter("opName")==null?"*":request.getParameter("opName");
if(opName.indexOf(",")!=-1 || opName.indexOf("*")!=-1) 
	out.print("<font color = red>WARNING: There may be more than 1 kind of OPs(OpName = " + opName + "), the HDG maybe meanless</font><p>");
int off = 0;int len =50;
HashMap<String,Integer> count = new HashMap<String,Integer>();
List<Report> taskWithOp=accessor.getTasksByOp(condition, title, off, len);
while(taskWithOp.size()>0)
{
	Iterator<Report> it = taskWithOp.iterator();
	while (it.hasNext()) {
        	Report rep = it.next();
		String host;
		if(rep.get("HostName")==null)continue;else host=rep.get("HostName").get(0);
		if(count.get(host)==null)
			count.put(host, 1);
		else
			count.put(host, count.get(host)+1);
        }
	off = off + len;
	taskWithOp=accessor.getTasksByOp(condition, title, off, len);
}
Iterator<Map.Entry<String, Integer>>  it = count.entrySet().iterator();
int maxCount=1;
while (it.hasNext())
{
	Map.Entry entry = (Map.Entry) it.next();
	Integer times = (Integer)entry.getValue();
	num+=times;
	if(maxCount<times)
		maxCount=times;
}
double factor= (double)100/maxCount;

String table;
table = "<table border=1 cellspacing=0 cellpadding=3>";
table += "<tr>";
table += "<th width=200>Host</th>";
table += "<th>Count</th>";
table += "<th width=400>Count graph</th>";
table += "<th width=80>Rate</th>";
table += "</tr>";
it = count.entrySet().iterator();
while (it.hasNext())
{
	Map.Entry entry = (Map.Entry) it.next();
	String host = (String)entry.getKey();
	Integer times = (Integer)entry.getValue();
	table += "<tr>";
	table += "<td align = center><a title = \"see all related tasks\" href=\"/op_tasksWithOp.jsp?opName="+opName+"&name="+host+"\">" + host + "</td>";
	table += "<td>" + times + "</a></td>";
	table += "<td>";
	for(int j=0;j<(int)(factor*times);j++)
		table+="\\";
	table += "</td>";
	table += "<td align = center>" + (int)Math.round((double)100*times/num)+"%" + "</a></td>";
	table += "</tr>";
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
