<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="edu.nudt.xtrace.*"%>
<%@ page import="java.util.*"%>

<HTML>
<head>
<title>X-Trace Viewer</title>
</head>
<BODY>
<FONT style="FONT-FAMILY: sans-serif">
<div style="line-height:1"> 
<a href="/">Return Home</a> &gt;&gt;<B>Tasks</B> | <a href="op_index.jsp">Operations</a> | 
&gt;&gt;<a href="database_list.jsp">Databases</a>&lt;&lt;
<hr/><%@include file = "searchBar.jsp"%><hr/>
</div>
<jsp:useBean id="accessor" class="edu.nudt.xtrace.MySQLAccessor" scope="session"/>
<%@include file = "taskTable.jsp"%>
<%
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
%>
<B>Tools:</B>
<input title = "see one kind trace classification" style="width:100" type="button" value="classify" 
onclick="window.location='/taskClassify.jsp?<%=request.getQueryString()==null?"title=*":request.getQueryString()%>'">
<input title = "find the abnormal tasks" style="width:100" type="button" value="diagnose" 
onclick="window.location='/abnormalDiagnosis.jsp?<%=partUrl%>'">
<script language="javascript">
function onDeleteTasks(conditionStr){
	if(conditionStr.length==0)
		alert("No task selected, Please select tasks");
	else{
		var r=confirm("Delete these tasks?");
		if (r==true)
		{
			var r2=confirm("You really sure delete all these tasks?\n It cann't be recovery");
			if (r2==true)
			{
				window.location.href="deleteTask.jsp?"+conditionStr;
			}
		}
	}
}
</script>
|  <input title = "delete selected tasks" style="width:100" type="button" value="delete" onClick = "onDeleteTasks('<%=partUrl%>'); return false;"/>
<p>

<%@include file = "navbar.jsp"%>
<%
String isAbnormalList = request.getParameter("abnormalList")==null?"false":request.getParameter("abnormalList");
ArrayList<DiagnosisResult> drList=(ArrayList<DiagnosisResult>)session.getAttribute("abnormalTaskInfo");
if(isAbnormalList.equals("true"))
	out.print(showTasks(tasks, drList, true));
else
	out.print(showTasks(tasks));
%>
<%@include file = "navbar.jsp"%>
<hr/>
Database size: <%= accessor.numTasks()%> tasks, <%= accessor.numReports()%> reports.</p>
</FONT>
</BODY>
</HTML>

