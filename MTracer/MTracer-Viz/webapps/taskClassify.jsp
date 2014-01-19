<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="edu.nudt.xtrace.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.io.File" %>

<HTML>
<head>
<title>Task Classify</title>
</head>
<BODY>
<FONT style="FONT-FAMILY: sans-serif">
<div style="line-height:1"> 
<a href="/">Return Home</a> &gt;&gt;<a href="/">Tasks</a>&gt;&gt;<B>Task classify</B> | <a href="op_index.jsp">Operations</a><hr/>
</div>
<jsp:useBean id="accessor" class="edu.nudt.xtrace.MySQLAccessor" scope="session"/>
<jsp:useBean id="classifier" class="edu.nudt.xtrace.TaskClassifier" scope="session"/>
<%
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

%>
</FONT>
</BODY>
</HTML>

