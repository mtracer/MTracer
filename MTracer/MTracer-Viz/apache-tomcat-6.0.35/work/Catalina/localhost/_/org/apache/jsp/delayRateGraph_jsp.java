package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import edu.nudt.xtrace.*;
import java.util.*;

public final class delayRateGraph_jsp extends org.apache.jasper.runtime.HttpJspBase
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
      out.write("<title>Delay Rate Graph of ");
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
      out.write('\n');
 
String delay = request.getParameter("delay");
String taskID = request.getParameter("taskid");

      out.write("\n");
      out.write("<a href=\"/\">Return Home</a>&gt;&gt;<a href=\"/\">Tasks</a>&gt;&gt;<a href=\"/callTreeGraph.jsp?taskid=");
      out.print( taskID );
      out.write("&delay=");
      out.print( delay);
      out.write("&direction=TB&shape=ellipse\">CTG</a> \n");
      out.write("<B>DRG</B> | <a href=\"op_index.jsp\">Operations</a>\n");
      out.write("<hr>\n");
      out.write("<br>\n");
      out.write("<table border=1 cellspacing=0 cellpadding=3>\n");
      out.write("<tr>\n");
      out.write("\t<th>OpName</th>\n");
      out.write("\t<th>Delay</th>\n");
      out.write("\t<th>Delay graph</th>\n");
      out.write("\t<th>Rate</th>\n");
      out.write("\t<th>Agent</th>\n");
      out.write("\t<th>HostName</th>\n");
      out.write("\t<th>Address</th>\n");
      out.write("\t<th>Description</th>\n");
      out.write("</tr>\n");

if(trace.findTopology(accessor.getConn(), taskID, delay) == null)
	out.print("error in construct topology");
else{
	/**  added for abnormal task**/
	String isAbnormalStr = request.getParameter("isAbnormalTask")==null?"false":request.getParameter("isAbnormalTask");
	boolean isAbnormal =  isAbnormalStr.equals("true")?true:false;
	ArrayList<DiagnosisResult> drList=null;
	DiagnosisResult dr=null;
	ArrayList<Integer> abnormalOp = null;
	if(isAbnormal==true){
		drList = (ArrayList<DiagnosisResult>)session.getAttribute("abnormalTaskInfo");
		for(int i=0;i<drList.size();i++){
			dr = drList.get(i);
			if(taskID.equals(dr.getTaskID()))
				break;
		}
		if(dr!=null)
			abnormalOp=dr.getAbnormalOperations();
	}
	/**  added for abnormal task**/
	trace.depthFirstSearchOrder();
	ArrayList<Report> reports = trace.getReports();
	HashMap<String, Report> sortedReports = new HashMap<String, Report>();
	for(int i=0; i<reports.size();i++)
	{
		Report rep = reports.get(i);
		if(rep.get("dfsorder")==null)continue;
		sortedReports.put(rep.get("dfsorder").get(0),rep);
	}
	for(int i=0; i<sortedReports.size();i++)
	{
		Report rep = sortedReports.get(String.valueOf(i));
		if(rep == null) continue;
		int depth = Integer.parseInt(rep.get("depth").get(0));
		String opname = rep.get("opname").get(0);
		String formattedOpname = opname;
		/**  added for abnormal task**/
		if(isAbnormal==true && abnormalOp!=null){
			if(abnormalOp.contains(i))
				formattedOpname = "<font color=red>"+formattedOpname+"</font>";
		}
		/**  added for abnormal task**/
		String time = rep.get("delay").get(0);
		out.print("<tr>");
		String space="";
		double rate;
		if(Double.parseDouble(delay)<=0)
			rate = 100;
		else
			rate = Double.parseDouble(time)*100/Double.parseDouble(delay);
		for(int s=0;s<depth;s++)
			for(int t=0;t<16;t++)
				space+="&nbsp;";
		if(depth==0)
			out.print("<td>"+space+"<a title = \"see all tasks of this type\" href=\"/?title="+opname+"&numReportsFrom="+sortedReports.size()+"&numReportsTo="+sortedReports.size()+"\">"+formattedOpname+"</td>");
		else
			out.print("<td>"+space+"\\_____<a title = \"see all the tasks with this operation\" href=\"/op_tasksWithOp.jsp?opName="+opname+"&sortValue=Delay&sortStyle=descend"+"\">"+formattedOpname+"</td>");
		String tmp = "";
		for(int t=0;t<Integer.valueOf(String.format("%.0f", rate));t++)tmp+="\\";
		out.print("<td>"+String.format("%.2f", Double.parseDouble(time))+"ms</td>");
		out.print("<td>"+tmp+"&nbsp;</td>");
		out.print("<td>"+String.format("%.0f", rate)+"%</td>");
		if(depth==0)
			out.print("<td>"+rep.get("agent").get(0)+"</td>");
		else
			out.print("<td><a title = \"see all the tasks with this OP and on this agent\" href=\"op_tasksWithOp.jsp?opName="+opname+"&agent="+rep.get("agent").get(0)+"&sortValue=Delay&sortStyle=descend"+"\">"+rep.get("agent").get(0)+"</td>");
		if(depth==0)
			out.print("<td>"+rep.get("hostname").get(0)+"</td>");
		else
			out.print("<td><a title = \"see all the tasks with this OP and on host\" href=\"op_tasksWithOp.jsp?opName="+opname+"&name="+rep.get("hostname").get(0)+"&sortValue=Delay&sortStyle=descend"+"\">"+rep.get("hostname").get(0)+"</td>");
		if(depth==0)
			out.print("<td>"+rep.get("hostaddress").get(0)+"</td>");
		else
			out.print("<td><a title = \"see all the tasks with this OP and on host\" href=\"op_tasksWithOp.jsp?opName="+opname+"&address="+rep.get("hostaddress").get(0)+"&sortValue=Delay&sortStyle=descend"+"\">"+rep.get("hostaddress").get(0)+"</td>");
		String description = rep.get("description")==null?"null":rep.get("description").get(0);
		out.print("<td title = \""+description+"\">"+description.substring(0,Math.min(50,description.length()))+"</td>");
		out.print("</tr>");
	}
}

      out.write("\n");
      out.write("</BODY>\n");
      out.write("<HTML>\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
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
