package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import edu.nudt.xtrace.*;
import java.util.*;
import java.io.File;

public final class taskClassify_jsp extends org.apache.jasper.runtime.HttpJspBase
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
      out.write("<HTML>\n");
      out.write("<head>\n");
      out.write("<title>Task Classify</title>\n");
      out.write("</head>\n");
      out.write("<BODY>\n");
      out.write("<FONT style=\"FONT-FAMILY: sans-serif\">\n");
      out.write("<div style=\"line-height:1\"> \n");
      out.write("<a href=\"/\">Return Home</a> &gt;&gt;<a href=\"/\">Tasks</a>&gt;&gt;<B>Task classify</B> | <a href=\"op_index.jsp\">Operations</a><hr/>\n");
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
      edu.nudt.xtrace.TaskClassifier classifier = null;
      synchronized (session) {
        classifier = (edu.nudt.xtrace.TaskClassifier) _jspx_page_context.getAttribute("classifier", PageContext.SESSION_SCOPE);
        if (classifier == null){
          classifier = new edu.nudt.xtrace.TaskClassifier();
          _jspx_page_context.setAttribute("classifier", classifier, PageContext.SESSION_SCOPE);
        }
      }
      out.write('\n');

String title = request.getParameter("title");//只能对同一title的task进行操作
if(title == null) title = "*";
if(title.indexOf(",")!=-1 || title.indexOf("*")!=-1){
	out.print("<font color = red>WARNING: There may be more than 1 kind of tasks(Title = \"" + title + "\"), try to remove '*' and ',' in the title string</font><p>");
	return;
}
String oldTitle = title;
title = title.replaceAll("-","");
title = title.replaceAll(" ","_");
Report condition = new Report();
Enumeration para=request.getParameterNames();
while(para.hasMoreElements()){
	String key=(String)para.nextElement();
	String value=request.getParameter(key);
	condition.put(key, value);
}
String taskIDType=condition.get("taskID")==null ? "useUrl" : condition.get("taskID").get(0);
if(taskIDType.equals("usesession")){//通过list传递taskid的值
	condition.remove("taskID");
	String sessionKey=condition.get("sessionKey").get(0);
	ArrayList<String> selectedTaskIDsList=(ArrayList<String>)session.getAttribute(sessionKey);
	String taskIDs="";
	for(int i=0;i<selectedTaskIDsList.size();i++)
		taskIDs += selectedTaskIDsList.get(i)+",";
	taskIDs = taskIDs.substring(0,taskIDs.length()-1);//remove the last ','
	condition.put("taskID",taskIDs);
}
String temp = application.getRealPath(request.getRequestURI());
String basePath = new File(temp).getParent();
basePath += "/classification";
int numTasks = classifier.classify(accessor.getConn(),basePath,condition);
Map<String, ArrayList<String>> results = classifier.getResults();
classifier.genGraph();//生成图像

out.print("<table border=1 cellspacing=0 cellpadding=3>");
out.print("<tr>");
out.print("<th width=60>Type</th>");
out.print("<th width=60>Num</th>");
out.print("<th width=380>Rate graph</th>");
out.print("<th width=60>Rate</th>");
out.print("<th width=100>Tools</th>");
out.print("</tr>");
Set<String> topoids = results.keySet();

for (Iterator it = topoids.iterator(); it.hasNext();) {
	String topoid = (String) it.next();
	String dotPath = basePath + "/"+title + "/type_"+topoid+".dot";
	String svgPath = basePath + "/"+title + "/type_"+topoid+".svg";

	ArrayList<String> tasks=results.get(topoid);
	/*String taskIDs = "";
	for(int i=0;i<tasks.size();i++)
		taskIDs += tasks.get(i)+",";
	taskIDs = taskIDs.substring(0,taskIDs.length()-1);//remove the last ','*/
	session.setAttribute(topoid,tasks);
	out.print("<tr>");
	out.print("<td align = center width = 400 height=100>");
	out.print("<embed width=400 height=120 src=\"classification/"+title +"/type_"+topoid+".svg"+"\" type=\"image/svg+xml\" />");
        out.print("<a title = \"see the graph of this type\" href=\"/topoGraph.jsp?topoID="+topoid+"&title="+oldTitle+"\">");
	out.print(">>Detail<<");
	out.print("</a>");
	out.print("</td>");
	//out.print("<td align = center>" +"<embed width = 400 height=100 src=\"classification/"+title + "/type_"+topoid+".svg"+"\" type=\"image/svg+xml\" />"+ "</td>");
	//out.print("<td align = center><a title = \"see all tasks of this type\" href=\"/?title="+oldTitle+"&taskID="+taskIDs+"\">" + tasks.size() + "</a></td>");
	//url不能太长，所以taskids不能通过url传送，而将其存储在session中，告诉下一个页面通过该session访问
	out.print("<td align = center><a onClick = \"storeTaskIds(); return false;\" title = \"see all tasks of this type\" href=\"/?title="+oldTitle+"&sortValue=delay&sortStyle=descend&taskID=usesession&sessionKey="+topoid+"\">" + tasks.size() + "</a></td>");
	String tmp = "";
	for(int t=0;t<tasks.size()*100/numTasks;t++)tmp+="|";
	out.print("<td>"+tmp+"&nbsp;</td>");
	//out.print("<td><img width=200 height=120 src=\"/home/hadoop/XTraceServer/xtrace-display/webapps/image/color/1.gif\" type=\"image/svg+xml\" />");
	//out.print("</td>");
	//out.print("<td><svg id=\"svgid\" width=\"400\" height=\"200\" xmlns=\"http://www.w3.ort/2000/svg\">");
	//out.print("<rect id=\"rectid\" width=\"400\" height=\"200\" stroke=\"#17301D\" stroke-width=\"2\" fill=\"#0E4E75\" fill-opacity=\"0.5\"/>");
	//out.print("</svg></td>");
	out.print("<td align = center>" +Math.round((double)tasks.size()*100/numTasks)  + "%</td>");
	out.print("<td align = \"center\">");
	out.print("<input title = \"find the abnormal tasks\" style=\"width:80\" type=\"button\" value=\"diagnose\" onclick=\"window.location='/abnormalDiagnosis.jsp?taskID="+"usesession&sessionKey="+topoid+"'\">");
	out.print("</td>");
	out.print("</tr>");
}


      out.write("\n");
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
