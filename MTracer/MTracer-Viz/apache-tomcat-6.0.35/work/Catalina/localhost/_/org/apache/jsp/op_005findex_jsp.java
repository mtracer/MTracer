package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import edu.nudt.xtrace.*;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public final class op_005findex_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {


public String showOperations(List<OperationRecord> operations)
{
	String table;
	table = "<table border=1 cellspacing=0 cellpadding=3>";
	table += "<tr>";
	table += "<th width=250>OpName</th>";
	table += "<th width=60>Num</th>";
	table += "<th width=110>MaxDelay</th>";
	table += "<th width=110>MinDelay</th>";
	table += "<th width=110>AverageDelay</th>";
	table += "<th width=120>Tool</th>";
	table += "</tr>";
	Iterator<OperationRecord> it = operations.iterator();
	while (it.hasNext()) {
        	OperationRecord operation = it.next();
		table += "<tr>";
		table += "<td>" + operation.getOpName()+ "</td>";
		table += "<td><a title = \"see all tasks with this operation\" href=\"/op_tasksWithOp.jsp?opName="+operation.getOpName()+"\">" + operation.getNum() + "</a></td>";
		table += "<td align = \"right\"><a title = \"see the task with maxDelay\" href=\"/op_tasksWithOp.jsp?opName="+operation.getOpName()+"&delayFrom="+operation.getMaxDelay()+"&delayTo="+operation.getMaxDelay()+"\">" + operation.getMaxDelay() + "ms</td>";
		table += "<td align = \"right\"><a title = \"see the task with minDelayy\" href=\"/op_tasksWithOp.jsp?opName="+operation.getOpName()+"&delayFrom="+operation.getMinDelay()+"&delayTo="+operation.getMinDelay()+"\">" + operation.getMinDelay() + "ms</td>";
		table += "<td align = \"right\">" + operation.getAverageDelay() + "ms</td>";
		table += "<td align = \"center\">";
		table += "<a title = \"see the Delay Distribute Graph of this operation\" href=\"/op_delayDistributeGraph.jsp?opName="+operation.getOpName()+"\">DDG</a>";
		table += "          ";
		table += "<a title = \"see the Host Distribute Graph of this operation\" href=\"/op_hostDistributeGraph.jsp?opName="+operation.getOpName()+"\">HDG</a>";
		table += "          ";
		table += "<a title = \"see the Delay Trend Graph of this operation\" href=\"/op_delayTrendGraph.jsp?opName="+operation.getOpName()+"\">DTG</a>";
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
    _jspx_dependants.add("/op_searchBar.jsp");
    _jspx_dependants.add("/op_table.jsp");
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
      out.write("<title>Operation Abstract Info</title>\n");
      out.write("</head>\n");
      out.write("<BODY>\n");
      out.write("<FONT style=\"FONT-FAMILY: sans-serif\">\n");
      out.write("<div style=\"line-height:1\"> \n");
      out.write("<a href=\"/\">Return Home</a> &gt;&gt;<B>Operations</B>&gt;&gt;<a href=\"op_tasksWithOp.jsp\">Operation Detail</a> | <a href=\"index.jsp\">Tasks</a>\n");
      out.write("<hr/>");
      out.write("<HTML>\n");
      out.write("<form action=\"\" method=\"get\" name=\"searchForm\">\n");
      out.write("\t<B>Search by:</B>\n");
      out.write("\tOpName <input title = \"use ',' as delimiter,use '*' as wildcard. e.g. 'RPC*,*write*'\"style=\"width:180\" name=\"opName\" value= \"");
      out.print( request.getParameter("opName")==null ? "*" : request.getParameter("opName"));
      out.write("\" /> \n");
      out.write("\tNum from <input style=\"width:78\" name=\"numFrom\" value= \"");
      out.print( request.getParameter("numFrom")==null ? "1" : request.getParameter("numFrom"));
      out.write("\" />\n");
      out.write("\tto <input style=\"width:78\" name=\"numTo\" value= \"");
      out.print( request.getParameter("numTo")==null ? "*" : request.getParameter("numTo"));
      out.write("\" />\n");
      out.write("\tMaxDelay from <input style=\"width:80\" name=\"maxDelayFrom\" value=\"");
      out.print( request.getParameter("maxDelayFrom")==null ? "0" : request.getParameter("maxDelayFrom"));
      out.write("\" />\n");
      out.write("\tto <input style=\"width:80\" name=\"maxDelayTo\" value=\"");
      out.print( request.getParameter("maxDelayTo")==null ? "*" : request.getParameter("maxDelayTo"));
      out.write("\" />\n");
      out.write("\t| <input style=\"width:80\" type=\"button\" value=\"reset\" onclick=\"window.location='http://localhost:8080/op_index.jsp'\"/><p>\n");
      out.write("\tMinDelay from <input style=\"width:80\" name=\"minDelayFrom\" value=\"");
      out.print( request.getParameter("minDelayFrom")==null ? "0" : request.getParameter("minDelayFrom"));
      out.write("\" />\n");
      out.write("\tto <input style=\"width:80\" name=\"minDelayTo\" value=\"");
      out.print( request.getParameter("minDelayTo")==null ? "*" : request.getParameter("minDelayTo"));
      out.write("\" />\n");
      out.write("\tAverageDelay from <input style=\"width:80\" name=\"averageDelayFrom\" value=\"");
      out.print( request.getParameter("averageDelayFrom")==null ? "0" : request.getParameter("averageDelayFrom"));
      out.write("\" />\n");
      out.write("\tto <input style=\"width:80\" name=\"averageDelayTo\" value=\"");
      out.print( request.getParameter("averageDelayTo")==null ? "*" : request.getParameter("averageDelayTo"));
      out.write("\" />\n");
      out.write("\tsorted by \n");
      out.write("\t");

	session.setAttribute("is_select_opName","");
	session.setAttribute("is_select_num","");
	session.setAttribute("is_select_maxDelay","");
	session.setAttribute("is_select_minDelay","");
	session.setAttribute("is_select_averageDelay","");
	if(request.getParameter("sortValue")==null)
		session.setAttribute("is_select_opName", "selected");
	else
		session.setAttribute("is_select_"+request.getParameter("sortValue"), "selected");
	
      out.write("\n");
      out.write("\t<select name=\"sortValue\">\n");
      out.write("\t\t<option ");
      out.print( session.getAttribute("is_select_opName") );
      out.write(" value=\"opName\">OpName</option>\n");
      out.write("\t\t<option ");
      out.print( session.getAttribute("is_select_num") );
      out.write(" value=\"num\">Num</option>\n");
      out.write("\t\t<option ");
      out.print( session.getAttribute("is_select_maxDelay") );
      out.write(" value=\"maxDelay\">MaxDelay</option>\n");
      out.write("\t\t<option ");
      out.print( session.getAttribute("is_select_minDelay") );
      out.write(" value=\"minDelay\">MinDelay</option>\n");
      out.write("\t\t<option ");
      out.print( session.getAttribute("is_select_averageDelay") );
      out.write(" value=\"averageDelay\">AverageDelay</option>\n");
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
Report condition = new Report();
Report title = new Report();
Enumeration para=request.getParameterNames();
while(para.hasMoreElements()){
	String key=(String)para.nextElement();
	String value=request.getParameter(key);
	condition.put(key, value);
}
List<OperationRecord> operations=accessor.getOperationsBySearch(condition, title, off, len);
if(request.getParameter("opName") == null){
	out.print("<B>Operations</B><p>");
}else{
	out.print("<B>" + title.get("title").get(0) + "</B><p>");
}

      out.write("\n");
      out.write("<input title = \"see all operation details\" style=\"width:120\" type=\"button\" value=\"Operation Detail\" onclick=\"window.location='/op_tasksWithOp.jsp?sortValue=OpName&sortStyle=default'\"><p>\n");
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
out.print(showOperations(operations));
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
      out.write("Database size: ");
      out.print( accessor.numOperations());
      out.write(" operations.</p>\n");
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
