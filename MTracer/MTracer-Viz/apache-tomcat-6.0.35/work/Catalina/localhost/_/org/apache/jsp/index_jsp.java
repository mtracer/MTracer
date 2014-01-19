package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import edu.nudt.xtrace.*;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public final class index_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {


public String showTasks(List<TaskRecord> tasks, ArrayList<DiagnosisResult> drList, boolean isAbnormalList)
{
	DateFormat HTML_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
	String table;
	table = "<table border=1 cellspacing=0 cellpadding=3>";
	table += "<tr>";
	table += "<th width=250>Trace Title</th>";
	table += "<th width=60>Reports</th>";
	table += "<th width=60>Edges</th>";
	table += "<th width=100>Delay</th>";
	table += "<th width=170>FirstSeen</th>";
	table += "<th width=170>LastUpdated</th>";
	table += "<th width=160>TaskID</th>";
	if(isAbnormalList==true)
		table += "<th width=160>AbnormalOperations</th>";
	table += "<th width=140>Tool</th>";
	table += "</tr>";
	Iterator<TaskRecord> it = tasks.iterator();
	while (it.hasNext()) {
        	TaskRecord task = it.next();
		table += "<tr>";
		table += "<td><a title = \"see tasks with the same title\" href=\"/?title=" +task.getTitle()+ "\">" +task.getTitle()+ "</a></td>";
		table += "<td><a title = \"see all reports of this task\" href=\"/showReports.jsp?taskid="+task.getTaskId()+"\">" + task.getNumReports() + "</a></td>";
		table += "<td><a title = \"see all edges of this task\" href=\"/showEdges.jsp?taskid="+task.getTaskId()+"\">" + task.getNumEdges() + "</a></td>";
		table += "<td align = \"right\">" + task.getDelay() + "ms</td>";
		table += "<td align = \"center\">" + HTML_DATE_FORMAT.format(task.getFirstSeen()) + "</td>";
		table += "<td align = \"center\">" + HTML_DATE_FORMAT.format(task.getLastUpdated()) + "</td>";
		table += "<td align = \"center\"><tt>" + task.getTaskId() + "</tt></td>";
		if(isAbnormalList==true){
			String abop = "";				
			DiagnosisResult dr=null;
			for(int i=0;i<drList.size();i++){
				dr = drList.get(i);
				if(task.getTaskId().equals(dr.getTaskID()))
					break;
			}
			if(dr!=null){
				ArrayList<Integer> ops = dr.getAbnormalOperations();
				abop += ops.size()+": ";
				for(int i=0;i<ops.size();i++)
					abop += "["+ops.get(i)+"] ";
			}
			table += "<td title=\"the dfs index of abnormal operations \" align = \"left\">" + abop + "</td>";
		}
		table += "<td align = \"center\">";
		if(isAbnormalList == true)
			table += "<a title = \"abnormal Call Tree Graph\" href=\"/callTreeGraph_abnormal.jsp?taskid="+task.getTaskId()+"&delay="+task.getDelay()+"\">CTG</a>";
		else
			table += "<a title = \"Call Tree Graph\" href=\"/callTreeGraph.jsp?taskid="+task.getTaskId()+"&delay="+task.getDelay()+"&direction=TB&shape=ellipse\">CTG</a>";
		table += "     ";
		if(isAbnormalList == true)
			table += "<a title = \"abnormal Delay Rate Graph\" href=\"/delayRateGraph.jsp?taskid="+task.getTaskId()+"&delay="+task.getDelay()+"&isAbnormalTask=true\">DRG</a>";
		else
			table += "<a title = \"Delay Rate Graph\" href=\"/delayRateGraph.jsp?taskid="+task.getTaskId()+"&delay="+task.getDelay()+"\">DRG</a>";
		table += "     ";
		table += "<input title = \"delete this task\" style=\"width:50\" type=\"button\" value=\"delete\" onClick = \"onDelete('"+task.getTaskId()+"'); return false;\"/>";
		table += "</td>";
		table += "</tr>";
        }
	table += "</table>";
	return table;
}

public String showTasks(List<TaskRecord> tasks){
	return showTasks(tasks, null, false);
}

  private static final JspFactory _jspxFactory = JspFactory.getDefaultFactory();

  private static java.util.List _jspx_dependants;

  static {
    _jspx_dependants = new java.util.ArrayList(3);
    _jspx_dependants.add("/searchBar.jsp");
    _jspx_dependants.add("/taskTable.jsp");
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
      out.write("<title>X-Trace Viewer</title>\n");
      out.write("</head>\n");
      out.write("<BODY>\n");
      out.write("<FONT style=\"FONT-FAMILY: sans-serif\">\n");
      out.write("<div style=\"line-height:1\"> \n");
      out.write("<a href=\"/\">Return Home</a> &gt;&gt;<B>Tasks</B> | <a href=\"op_index.jsp\">Operations</a> | \n");
      out.write("&gt;&gt;<a href=\"database_list.jsp\">Databases</a>&lt;&lt;\n");
      out.write("<hr/>");
      out.write("<HTML>\n");
      out.write("<form action=\"\" method=\"get\" name=\"searchForm\">\n");
      out.write("\t<B>Search by:</B>\n");
      out.write("\tTitle <input title = \"use ',' as delimiter,use '*' as wildcard. e.g. '*ls*,*rm *'\"style=\"width:180\" name=\"title\" value= \"");
      out.print( request.getParameter("title")==null ? "*" : request.getParameter("title"));
      out.write("\" /> \n");
      out.write("\tReceive time from <input style=\"width:150\" name=\"timeFrom\" value= \"");
      out.print( request.getParameter("timeFrom")==null ? "1970-01-01 00:00:00" : request.getParameter("timeFrom"));
      out.write("\" />\n");
      out.write("\tto <input style=\"width:150\" name=\"timeTo\" value= \"");
      out.print( request.getParameter("timeTo")==null ? "*" : request.getParameter("timeTo"));
      out.write("\" />\n");
      out.write("\tDelay from <input style=\"width:80\" name=\"delayFrom\" value=\"");
      out.print( request.getParameter("delayFrom")==null ? "0" : request.getParameter("delayFrom"));
      out.write("\" />\n");
      out.write("\tto <input style=\"width:80\" name=\"delayTo\" value=\"");
      out.print( request.getParameter("delayTo")==null ? "*" : request.getParameter("delayTo"));
      out.write("\" />\n");
      out.write("\t| <input style=\"width:80\" type=\"button\" value=\"reset\" onclick=\"window.location='/'\"/><p>\n");
      out.write("\tTaskID: <input title = \"use ',' as delimiter,use '*' as wildcard. e.g. 'CC81DC28AAD1936F,*8A*D1936F'\" style=\"width:157\" name=\"taskID\" value=\"");
      out.print( request.getParameter("taskID")==null ? "*" : request.getParameter("taskID"));
      out.write("\" />\n");
      out.write("\tReports number from <input style=\"width:50\" name=\"numReportsFrom\" value=\"");
      out.print( request.getParameter("numReportsFrom")==null ? "1" : request.getParameter("numReportsFrom"));
      out.write("\" />\n");
      out.write("\tto <input style=\"width:50\" name=\"numReportsTo\" value=\"");
      out.print( request.getParameter("numReportsTo")==null ? "*" : request.getParameter("numReportsTo"));
      out.write("\" />\n");
      out.write("\tEdges number from <input style=\"width:50\" name=\"numEdgesFrom\" value=\"");
      out.print( request.getParameter("numEdgesFrom")==null ? "0" : request.getParameter("numEdgesFrom"));
      out.write("\" />\n");
      out.write("\tto <input style=\"width:50\" name=\"numEdgesTo\" value=\"");
      out.print( request.getParameter("numEdgesTo")==null ? "*" : request.getParameter("numEdgesTo"));
      out.write("\" />\n");
      out.write("\t<input type=hidden name=\"sessionKey\" value=\"");
      out.print( request.getParameter("sessionKey")==null ? "*" : request.getParameter("sessionKey"));
      out.write("\" />\n");
      out.write("\tsorted by \n");
      out.write("\t");

	session.setAttribute("is_select_time","");
	session.setAttribute("is_select_delay","");
	session.setAttribute("is_select_numReport","");
	session.setAttribute("is_select_numEdges","");
	session.setAttribute("is_select_title","");
	session.setAttribute("is_select_taskID","");
	if(request.getParameter("sortValue")==null)
		session.setAttribute("is_select_time", "selected");
	else
		session.setAttribute("is_select_"+request.getParameter("sortValue"), "selected");
	
      out.write("\n");
      out.write("\t<select name=\"sortValue\">\n");
      out.write("\t\t<option ");
      out.print( session.getAttribute("is_select_time") );
      out.write(" value=\"time\">FirstSeen</option>\n");
      out.write("\t\t<option ");
      out.print( session.getAttribute("is_select_delay") );
      out.write(" value=\"delay\">Delay</option>\n");
      out.write("\t\t<option ");
      out.print( session.getAttribute("is_select_numReport") );
      out.write(" value=\"numReport\">NumReports</option>\n");
      out.write("\t\t<option ");
      out.print( session.getAttribute("is_select_numEdges") );
      out.write(" value=\"numEdges\">NumEdges</option>\n");
      out.write("\t\t<option ");
      out.print( session.getAttribute("is_select_title") );
      out.write(" value=\"title\">Title</option>\n");
      out.write("\t\t<option ");
      out.print( session.getAttribute("is_select_taskID") );
      out.write(" value=\"taskID\">TaskID</option>\n");
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
      out.write("\n");
      out.write("\n");
      out.write("<form action=\"\" method=\"get\" name=\"hiddenForm\">\n");
      out.write("\t<input type=hidden name=\"taskID\" /> \n");
      out.write("</form>\n");
      out.write("<script language=\"javascript\">\n");
      out.write("function onDelete(taskID){\n");
      out.write("\tvar r=confirm(\"Delete this task?\");\n");
      out.write("\tif (r==true)\n");
      out.write("\t{\n");
      out.write("\t\twindow.location.href=\"deleteTask.jsp?\"+\"taskID=\"+taskID; \n");
      out.write("\t}\n");
      out.write("}\n");
      out.write("</script>\n");
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
String partUrl="";
Enumeration para=request.getParameterNames();
while(para.hasMoreElements()){
	String key=(String)para.nextElement();
	String value=request.getParameter(key);
	condition.put(key, value);
	partUrl += key + "=" + value + "&";
}
if(!partUrl.isEmpty())
	partUrl = partUrl.substring(0,partUrl.length()-1);
List<TaskRecord> tasks=new ArrayList<TaskRecord>();
String taskIDType=condition.get("taskID")==null ? "useUrl" : condition.get("taskID").get(0);
/*if(taskIDType.equals("usesession")){//通过list传递taskid的值
	String sessionKey=condition.get("sessionKey").get(0);
	ArrayList<String> selectedTaskIDsList=(ArrayList<String>)session.getAttribute(sessionKey);
	String taskIDs="";
	condition.remove("taskID");
	if(selectedTaskIDsList == null){//防止用户在搜索栏中的taskid字段手动输入usesession
		taskIDs="*";
	}else{
		if(selectedTaskIDsList.size()==0){//空列表
			taskIDs = "notask";
		}else{
			int num = (off+len)>selectedTaskIDsList.size()?selectedTaskIDsList.size():(off+len);
			for(int i=off;i<num;i++)
				taskIDs += selectedTaskIDsList.get(i)+",";
			taskIDs = taskIDs.substring(0,taskIDs.length()-1);//remove the last ','
		}
	}
	condition.put("taskID",taskIDs);
	tasks=accessor.getTasksBySearch(condition, title, 0, len);
	if(selectedTaskIDsList != null){
		String tmp=title.get("title").get(0);
		title.remove("title");
		int start = tmp.indexOf("(TaskID");
		int end =tmp.indexOf(")",start);
		tmp=tmp.substring(0,start)+"(taskID in $list)"+tmp.substring(end+1,tmp.length())+"("+selectedTaskIDsList.size()+" results)";
		title.put("title",tmp);
	}
}*/
if(taskIDType.equals("usesession")){//通过list传递taskid的值
	String sessionKey=condition.get("sessionKey").get(0);
	ArrayList<String> selectedTaskIDsList=(ArrayList<String>)session.getAttribute(sessionKey);
	String taskIDs="";
	condition.remove("taskID");
	if(selectedTaskIDsList == null){//防止用户在搜索栏中的taskid字段手动输入usesession
		taskIDs="*";
	}else{
		if(selectedTaskIDsList.size()==0){//空列表
			taskIDs = "notask";
		}else{
			for(int i=0;i<selectedTaskIDsList.size();i++)
				taskIDs += selectedTaskIDsList.get(i)+",";
			taskIDs = taskIDs.substring(0,taskIDs.length()-1);//remove the last ','
		}
	}
	condition.put("taskID",taskIDs);
	tasks=accessor.getTasksBySearch(condition, title, off, len);
	if(selectedTaskIDsList != null){
		String tmp=title.get("title").get(0);
		title.remove("title");
		int start = tmp.indexOf("(TaskID");
		int end =tmp.indexOf(")",start);
		tmp=tmp.substring(0,start)+"(taskID in $list)"+tmp.substring(end+1,tmp.length());
		title.put("title",tmp);
	}
}else{
	tasks=accessor.getTasksBySearch(condition, title, off, len);
}

if(request.getQueryString() == null){
	out.print("<B>X-Trace Latest Tasks</B><p>");
}else{
	if(title!=null)
		if(title.get("title")!=null)
			out.print("<B>" + title.get("title").get(0) + "</B><p>");
}

      out.write("\n");
      out.write("<B>Tools:</B>\n");
      out.write("<input title = \"see one kind trace classification\" style=\"width:100\" type=\"button\" value=\"classify\" \n");
      out.write("onclick=\"window.location='/taskClassify.jsp?");
      out.print(request.getQueryString()==null?"title=*":request.getQueryString());
      out.write("'\">\n");
      out.write("<input title = \"find the abnormal tasks\" style=\"width:100\" type=\"button\" value=\"diagnose\" \n");
      out.write("onclick=\"window.location='/abnormalDiagnosis.jsp?");
      out.print(partUrl);
      out.write("'\">\n");
      out.write("<script language=\"javascript\">\n");
      out.write("function onDeleteTasks(conditionStr){\n");
      out.write("\tif(conditionStr.length==0)\n");
      out.write("\t\talert(\"No task selected, Please select tasks\");\n");
      out.write("\telse{\n");
      out.write("\t\tvar r=confirm(\"Delete these tasks?\");\n");
      out.write("\t\tif (r==true)\n");
      out.write("\t\t{\n");
      out.write("\t\t\tvar r2=confirm(\"You really sure delete all these tasks?\\n It cann't be recovery\");\n");
      out.write("\t\t\tif (r2==true)\n");
      out.write("\t\t\t{\n");
      out.write("\t\t\t\twindow.location.href=\"deleteTask.jsp?\"+conditionStr;\n");
      out.write("\t\t\t}\n");
      out.write("\t\t}\n");
      out.write("\t}\n");
      out.write("}\n");
      out.write("</script>\n");
      out.write("|  <input title = \"delete selected tasks\" style=\"width:100\" type=\"button\" value=\"delete\" onClick = \"onDeleteTasks('");
      out.print(partUrl);
      out.write("'); return false;\"/>\n");
      out.write("<p>\n");
      out.write("\n");
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

String isAbnormalList = request.getParameter("abnormalList")==null?"false":request.getParameter("abnormalList");
ArrayList<DiagnosisResult> drList=(ArrayList<DiagnosisResult>)session.getAttribute("abnormalTaskInfo");
if(isAbnormalList.equals("true"))
	out.print(showTasks(tasks, drList, true));
else
	out.print(showTasks(tasks));

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
      out.print( accessor.numTasks());
      out.write(" tasks, ");
      out.print( accessor.numReports());
      out.write(" reports.</p>\n");
      out.write("</FONT>\n");
      out.write("</BODY>\n");
      out.write("</HTML>\n");
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
