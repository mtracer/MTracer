package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import edu.nudt.xtrace.*;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public final class op_005ftasksWithOp_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {


public String showTaskWithOp(List<Report> tasks)
{
	DateFormat HTML_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
	String table;
	table = "<table border=1 cellspacing=0 cellpadding=3>";
	table += "<tr>";
	table += "<th colspan=\"5\">Operation details</th>";
	table += "<th colspan=\"8\">Trace details</th>";
	table += "</tr>";
	
	table += "<tr>";
	table += "<th width=160>OpName</th>";
	table += "<th width=100>Delay</th>";
	table += "<th width=170>HostName</th>";
	table += "<th width=80>HostAddress</th>";
	table += "<th width=80>Agent</th>";
	
	table += "<th width=200>Trace Title</th>";
	table += "<th width=60>Reports</th>";
	table += "<th width=60>Edges</th>";
	table += "<th width=80>Delay</th>";
	table += "<th width=170>Created</th>";
	table += "<th width=170>Last Report</th>";
	table += "<th width=160>TaskID</th>";
	table += "<th width=80>Tool</th>";
	table += "</tr>";
	Iterator<Report> it = tasks.iterator();
	while (it.hasNext()) {
        	Report rep = it.next();
		String OpName = rep.get("OpName")==null?"":rep.get("OpName").get(0);
		String Delay = rep.get("Delay")==null?"":rep.get("Delay").get(0);
		String HostName = rep.get("HostName")==null?"":rep.get("HostName").get(0);
		String HostAddress = rep.get("HostAddress")==null?"":rep.get("HostAddress").get(0);
		String Agent = rep.get("Agent")==null?"":rep.get("Agent").get(0);
		String Title = rep.get("Title")==null?"":rep.get("Title").get(0);
		String NumReports = rep.get("NumReports")==null?"":rep.get("NumReports").get(0);
		String NumEdges = rep.get("NumEdges")==null?"":rep.get("NumEdges").get(0);
		String TaskDelay = rep.get("TaskDelay")==null?"":rep.get("TaskDelay").get(0);
		String FirstSeen = rep.get("FirstSeen")==null?"":rep.get("FirstSeen").get(0);
		String LastUpdated = rep.get("LastUpdated")==null?"":rep.get("LastUpdated").get(0);
		String TaskID = rep.get("TaskID")==null?"":rep.get("TaskID").get(0);

		table += "<tr>";
		table += "<td align = \"left\"><a title = \"see the OPs with this name\" href=\"/op_tasksWithOp.jsp?opName="+OpName+"\">" + OpName + "</td>";
		table += "<td align = \"right\">" + Delay + "ms</td>";
		table += "<td align = \"center\"><a title = \"see the OPs in the same host\" href=\"/op_tasksWithOp.jsp?name="+HostName+"\">" + HostName + "</td>";
		table += "<td align = \"center\"><a title = \"see the OPs in the same host\" href=\"/op_tasksWithOp.jsp?address="+HostAddress+"\">" + HostAddress + "</td>";
		table += "<td align = \"center\"><a title = \"see the OPs in the same agent\" href=\"/op_tasksWithOp.jsp?agent="+Agent+"\">" + Agent + "</td>";
		
		table += "<td><a title = \"see tasks with the same title\" href=\"/?title=" +Title+ "\">" +Title+ "</a></td>";
		table += "<td><a title = \"see all reports of this task\" href=\"/showReports.jsp?taskid="+TaskID+"\">" +NumReports+ "</a></td>";
		table += "<td><a title = \"see all edges of this task\" href=\"/showEdges.jsp?taskid="+TaskID+"\">" +NumEdges+ "</a></td>";
		table += "<td align = \"right\">" + TaskDelay + "ms</td>";
		table += "<td align = \"center\">" + FirstSeen + "</td>";
		table += "<td align = \"center\">" + LastUpdated + "</td>";
		table += "<td align = \"center\"><tt>" + TaskID + "</tt></td>";
		table += "<td align = \"center\">";
		table += "<a title = \"Call Tree Graph\" href=\"/callTreeGraph.jsp?taskid="+TaskID+"&delay="+TaskDelay+"&direction=TB&shape=ellipse\">CTG</a>";
		table += "     ";
		table += "<a title = \"Delay Rate Graph\" href=\"/delayRateGraph.jsp?taskid="+TaskID+"&delay="+TaskDelay+"\">DRG</a>";
		table += "</td>";
		table += "</tr>";
        }
	table += "</table>";
	return table;
}

  private static final JspFactory _jspxFactory = JspFactory.getDefaultFactory();

  private static java.util.List _jspx_dependants;

  static {
    _jspx_dependants = new java.util.ArrayList(3);
    _jspx_dependants.add("/op_taskWithOpsearchBar.jsp");
    _jspx_dependants.add("/op_taskWithOpTable.jsp");
    _jspx_dependants.add("/navbar.jsp");
  }

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
      out.write("<title>Operation Detail</title>\n");
      out.write("</head>\n");
      out.write("<BODY>\n");
      out.write("<FONT style=\"FONT-FAMILY: sans-serif\">\n");
      out.write("<div style=\"line-height:1\"> \n");
      out.write("<a href=\"/\">Return Home</a> &gt;&gt;<a href=\"op_index.jsp\">Operations</a> &gt;&gt;<B>Operation Detail</B> | <a href=\"index.jsp\">Tasks</a>\n");
      out.write("<hr/>");
      out.write("<HTML>\n");
      out.write("<form action=\"\" method=\"get\" name=\"searchForm\">\n");
      out.write("\t<B>Search by:</B>\n");
      out.write("\tOpName <input title = \"use ',' as delimiter,use '*' as wildcard. e.g. 'RPC*,*write*'\" style=\"width:180\" name=\"opName\" value= \"");
      out.print( request.getParameter("opName")==null ? "*" : request.getParameter("opName"));
      out.write("\" /> \n");
      out.write("\tDelay from <input style=\"width:78\" name=\"delayFrom\" value= \"");
      out.print( request.getParameter("delayFrom")==null ? "0" : request.getParameter("delayFrom"));
      out.write("\" />\n");
      out.write("\tto <input style=\"width:78\" name=\"delayTo\" value= \"");
      out.print( request.getParameter("delayTo")==null ? "*" : request.getParameter("delayTo"));
      out.write("\" />\n");
      out.write("\tHostAddress <input title = \"use ',' as delimiter,use '*' as wildcard. e.g. '127.*,192.168*'\" style=\"width:120\" name=\"address\" value= \"");
      out.print( request.getParameter("address")==null ? "*" : request.getParameter("address"));
      out.write("\" />\n");
      out.write("\tHostName <input title = \"use ',' as delimiter,use '*' as wildcard. e.g. 'zjw8612*, *W510'\" style=\"width:120\" name=\"name\" value= \"");
      out.print( request.getParameter("name")==null ? "*" : request.getParameter("name"));
      out.write("\" />\n");
      out.write("\tAgent <input title = \"use ',' as delimiter,use '*' as wildcard. e.g. '*node, RPC*'\" style=\"width:85\" name=\"agent\" value= \"");
      out.print( request.getParameter("agent")==null ? "*" : request.getParameter("agent"));
      out.write("\" />\n");
      out.write("\t| <input style=\"width:80\" type=\"button\" value=\"reset\" onclick=\"window.location='http://localhost:8080/op_tasksWithOp.jsp'\"/><p>\n");
      out.write("\tTrace Title <input title = \"use ',' as delimiter,use '*' as wildcard. e.g. '*ls*,*rm *'\"style=\"width:180\" name=\"title\" value= \"");
      out.print( request.getParameter("title")==null ? "*" : request.getParameter("title"));
      out.write("\" /> \n");
      out.write("\tTime from <input style=\"width:150\" name=\"timeFrom\" value= \"");
      out.print( request.getParameter("timeFrom")==null ? "1970-01-01 00:00:00" : request.getParameter("timeFrom"));
      out.write("\" />\n");
      out.write("\tto <input style=\"width:150\" name=\"timeTo\" value= \"");
      out.print( request.getParameter("timeTo")==null ? "*" : request.getParameter("timeTo"));
      out.write("\" />\n");
      out.write("\tTaskID: <input title = \"use ',' as delimiter,use '*' as wildcard. e.g. 'CC81DC28AAD1936F,*8A*D1936F'\" style=\"width:157\" name=\"taskID\" value=\"");
      out.print( request.getParameter("taskID")==null ? "*" : request.getParameter("taskID"));
      out.write("\" />\n");
      out.write("\t\n");
      out.write("\tsorted by \n");
      out.write("\t");

	session.setAttribute("is_select_OpName","");
	session.setAttribute("is_select_Delay","");
	session.setAttribute("is_select_HostName","");
	session.setAttribute("is_select_HostAddress","");
	session.setAttribute("is_select_Agent","");
	session.setAttribute("is_select_Title","");
	session.setAttribute("is_select_NumReports","");
	session.setAttribute("is_select_NumEdges","");
	session.setAttribute("is_select_TaskDelay","");
	session.setAttribute("is_select_FirstSeen","");
	session.setAttribute("is_select_LastUpdated","");
	session.setAttribute("is_select_TaskID","");
	if(request.getParameter("sortValue")==null)
		session.setAttribute("is_select_opName", "selected");
	else
		session.setAttribute("is_select_"+request.getParameter("sortValue"), "selected");
	
      out.write("\n");
      out.write("\t<select name=\"sortValue\">\n");
      out.write("\t\t<option ");
      out.print( session.getAttribute("is_select_Delay") );
      out.write(" value=\"Delay\">Delay</option>\n");
      out.write("\t\t<option ");
      out.print( session.getAttribute("is_select_OpName") );
      out.write(" value=\"OpName\">OpName</option>\n");
      out.write("\t\t<option ");
      out.print( session.getAttribute("is_select_HostName") );
      out.write(" value=\"HostName\">HostName</option>\n");
      out.write("\t\t<option ");
      out.print( session.getAttribute("is_select_HostAddress") );
      out.write(" value=\"HostAddress\">HostAddress</option>\n");
      out.write("\t\t<option ");
      out.print( session.getAttribute("is_select_Agent") );
      out.write(" value=\"Agent\">Agent</option>\n");
      out.write("\t\t<option ");
      out.print( session.getAttribute("is_select_Title") );
      out.write(" value=\"Title\">Title</option>\n");
      out.write("\t\t<option ");
      out.print( session.getAttribute("is_select_NumReports") );
      out.write(" value=\"NumReports\">NumReports</option>\n");
      out.write("\t\t<option ");
      out.print( session.getAttribute("is_select_NumEdges") );
      out.write(" value=\"NumEdges\">NumEdges</option>\n");
      out.write("\t\t<option ");
      out.print( session.getAttribute("is_select_TaskDelay") );
      out.write(" value=\"TaskDelay\">TaskDelay</option>\n");
      out.write("\t\t<option ");
      out.print( session.getAttribute("is_select_FirstSeen") );
      out.write(" value=\"FirstSeen\">FirstSeen</option>\n");
      out.write("\t\t<option ");
      out.print( session.getAttribute("is_select_LastUpdated") );
      out.write(" value=\"LastUpdated\">LastUpdated</option>\n");
      out.write("\t\t<option ");
      out.print( session.getAttribute("is_select_TaskID") );
      out.write(" value=\"TaskID\">TaskID</option>\n");
      out.write("\t</select>\n");
      out.write("\t");

	session.setAttribute("is_select_default","");
	session.setAttribute("is_select_descend","");
	session.setAttribute("is_select_ascend","");
	if(request.getParameter("sortStyle")==null)
		session.setAttribute("is_select_default", "selected");
	else
		session.setAttribute("is_select_"+request.getParameter("sortStyle"), "selected");
	
      out.write("\n");
      out.write("\t<select name=\"sortStyle\" index = 2>\n");
      out.write("\t\t<option ");
      out.print( session.getAttribute("is_select_default") );
      out.write(" value=\"default\">default</option>\n");
      out.write("\t\t<option ");
      out.print( session.getAttribute("is_select_descend") );
      out.write(" value=\"descend\">descend</option>\n");
      out.write("\t\t<option ");
      out.print( session.getAttribute("is_select_ascend") );
      out.write(" value=\"ascend\">ascend</option>\n");
      out.write("\t</select>\n");
      out.write("\t| <input style=\"width:80\" type=\"submit\" value=\"search\" />\n");
      out.write("</form>\n");
      out.write("</HTML>\n");
      out.write("<hr/>\n");
      out.write("</div>\n");
      edu.nudt.xtrace.MySQLAccessor accessor = null;
      synchronized (session) {
        accessor = (edu.nudt.xtrace.MySQLAccessor) _jspx_page_context.getAttribute("accessor", PageContext.SESSION_SCOPE);
        if (accessor == null){
          accessor = new edu.nudt.xtrace.MySQLAccessor();
          _jspx_page_context.setAttribute("accessor", accessor, PageContext.SESSION_SCOPE);
        }
      }
      out.write('\n');
      out.write('\n');
      out.write('\n');
      out.write('\n');
      out.write('\n');
      out.write('\n');

int off = 0;
int len = 25;
if(request.getParameter("offset")!=null)
{
	try{off = Integer.parseInt(request.getParameter("offset"));}
	catch(Exception e){off =0;}
}
if(request.getParameter("length")!=null)
{
	try{len = Integer.parseInt(request.getParameter("length"));}
	catch(Exception e){len =25;}
}
String partUrl="";
Report condition = new Report();
Report title = new Report();
Enumeration para=request.getParameterNames();
while(para.hasMoreElements()){
	String key=(String)para.nextElement();
	String value=request.getParameter(key);
	condition.put(key, value);
	partUrl += key + "=" + value + "&";
}
if(!partUrl.isEmpty())
	partUrl = partUrl.substring(0,partUrl.length()-1);
List<Report> taskWithOp=accessor.getTasksByOp(condition, title, off, len);

if(!request.getParameterNames().hasMoreElements()){
	out.print("<B>X-Trace Latest Tasks</B><p>");
}else{
	out.print("<B>" + title.get("title")==null?"Operations":title.get("title").get(0) + "</B><p>");
}

      out.write("\n");
      out.write("<B>Tools:</B>\n");
      out.write("<input title = \"Delay Distribution Graph\" style=\"width:120\" type=\"button\" value=\"DDG\" onclick=\"window.location='/op_delayDistributeGraph.jsp?");
      out.print(partUrl);
      out.write("'\">\n");
      out.write("<input title = \"Host Distribution Graph\" style=\"width:120\" type=\"button\" value=\"HDG\" onclick=\"window.location='/op_hostDistributeGraph.jsp?");
      out.print(partUrl);
      out.write("'\">\n");
      out.write("<input title = \"Delay Trend Graph\" style=\"width:120\" type=\"button\" value=\"DTG\" onclick=\"window.location='/op_delayTrendGraph.jsp?");
      out.print(partUrl);
      out.write("'\"><p>\n");
      out.write("<HTML>\n");

{
String url = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getRequestURI()+"?";
int offset = 0;
int length = 25;
if(request.getQueryString() != null)
{
	Enumeration paras=request.getParameterNames();
	while(paras.hasMoreElements()){
		String key=(String)paras.nextElement();
		String value=request.getParameter(key);
		if(key.equals("length")){
			try{length = Integer.parseInt(value);}
			catch(Exception e){length =25;}
		}
		else if(key.equals("offset")){
			try{offset = Integer.parseInt(value);}
			catch(Exception e){offset = 0;}
		}
		else url += key + "=" + value + "&";
	}
}

int prev = offset - length;
int next = offset + length;
if(prev < 0)
	prev = 0;

String prevUrl = url+"length="+length+"&offset="+prev;
String nextUrl = url+"length="+length+"&offset="+next;
String dis = "<a href=\""+prevUrl+"\">[&lt;&lt; Previous "+length+"]</a>";
dis += "- Showing Tasks "+offset+" - "+(next-1)+" -";
dis += "<a href=\""+nextUrl+"\">[Next "+length+" &gt;&gt;]</a>"+"   |";
out.print(dis);

      out.write("\n");
      out.write("\n");
      out.write("Start with: <input type=\"text\" name=\"offset\" value= \"");
      out.print( request.getParameter("offset")==null ? "0" : request.getParameter("offset"));
      out.write("\" size=\"3\"/>\n");
      out.write("Results per page: <input type=\"text\" name=\"length\" value=\"");
      out.print( request.getParameter("length")==null ? "25" : request.getParameter("length"));
      out.write("\" size=\"3\" />\n");
      out.write("<script language=\"javascript\">\n");
      out.write("function onChange(){\n");
      out.write("\tvar off = document.getElementsByName(\"offset\");\n");
      out.write("\tvar len = document.getElementsByName(\"length\");\n");
      out.write("\twindow.location.href=\"");
      out.print( url );
      out.write("\"+\"offset=\"+off[0].value+\"&length=\"+len[0].value; \n");
      out.write("}\n");
      out.write("</script>\n");
      out.write("<input type=\"button\" value=\"Change\" onClick = \"onChange(); return false;\"/>\n");
      out.write("\n");
}
      out.write("\n");
      out.write("</HTML>\n");
      out.write("\n");
      out.write("\n");
      out.write('\n');
 out.print(showTaskWithOp(taskWithOp));
      out.write('\n');
      out.write("<HTML>\n");

{
String url = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getRequestURI()+"?";
int offset = 0;
int length = 25;
if(request.getQueryString() != null)
{
	Enumeration paras=request.getParameterNames();
	while(paras.hasMoreElements()){
		String key=(String)paras.nextElement();
		String value=request.getParameter(key);
		if(key.equals("length")){
			try{length = Integer.parseInt(value);}
			catch(Exception e){length =25;}
		}
		else if(key.equals("offset")){
			try{offset = Integer.parseInt(value);}
			catch(Exception e){offset = 0;}
		}
		else url += key + "=" + value + "&";
	}
}

int prev = offset - length;
int next = offset + length;
if(prev < 0)
	prev = 0;

String prevUrl = url+"length="+length+"&offset="+prev;
String nextUrl = url+"length="+length+"&offset="+next;
String dis = "<a href=\""+prevUrl+"\">[&lt;&lt; Previous "+length+"]</a>";
dis += "- Showing Tasks "+offset+" - "+(next-1)+" -";
dis += "<a href=\""+nextUrl+"\">[Next "+length+" &gt;&gt;]</a>"+"   |";
out.print(dis);

      out.write("\n");
      out.write("\n");
      out.write("Start with: <input type=\"text\" name=\"offset\" value= \"");
      out.print( request.getParameter("offset")==null ? "0" : request.getParameter("offset"));
      out.write("\" size=\"3\"/>\n");
      out.write("Results per page: <input type=\"text\" name=\"length\" value=\"");
      out.print( request.getParameter("length")==null ? "25" : request.getParameter("length"));
      out.write("\" size=\"3\" />\n");
      out.write("<script language=\"javascript\">\n");
      out.write("function onChange(){\n");
      out.write("\tvar off = document.getElementsByName(\"offset\");\n");
      out.write("\tvar len = document.getElementsByName(\"length\");\n");
      out.write("\twindow.location.href=\"");
      out.print( url );
      out.write("\"+\"offset=\"+off[0].value+\"&length=\"+len[0].value; \n");
      out.write("}\n");
      out.write("</script>\n");
      out.write("<input type=\"button\" value=\"Change\" onClick = \"onChange(); return false;\"/>\n");
      out.write("\n");
}
      out.write("\n");
      out.write("</HTML>\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("<hr/>\n");
      out.write("\n");
      out.write("</FONT>\n");
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
