<%@ page import="edu.nudt.xtrace.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.io.File" %>

<jsp:useBean id="accessor" class="edu.nudt.xtrace.MySQLAccessor" scope="session"/>
<%
Report condition = new Report();
Report title = new Report();
int paraNum=0;
Enumeration para=request.getParameterNames();
while(para.hasMoreElements()){
	paraNum++;
	String key=(String)para.nextElement();
	String value=request.getParameter(key);
	if(!key.equals("sortValue")&&!key.equals("sortStyle"))
		condition.put(key, value);
}
if(paraNum != 0){
	String temp=application.getRealPath(request.getRequestURI());  
	String fileDir=new File(temp).getParent();
	List<TaskRecord> tasks = null;
	int len =50;

	String taskIDType=condition.get("taskID")==null ? "useUrl" : condition.get("taskID").get(0);
	ArrayList<String> selectedTaskIDsList = null;
	int taskIDsListIndex = 0;
	if(taskIDType.equals("usesession")){//通过list传递taskid的值
		String sessionKey=condition.get("sessionKey").get(0);
		selectedTaskIDsList=(ArrayList<String>)session.getAttribute(sessionKey);
		String taskIDs="";
		condition.remove("taskID");
		int num = (taskIDsListIndex+len)>selectedTaskIDsList.size()?selectedTaskIDsList.size():(taskIDsListIndex+len);
		for(int i=taskIDsListIndex;i<num;i++){
			taskIDs += selectedTaskIDsList.get(i)+",";
		}
		taskIDs = taskIDs.substring(0,taskIDs.length()-1);//remove the last ','
		condition.put("taskID",taskIDs);
		taskIDsListIndex = num;
		tasks=accessor.getTasksBySearch(condition, title, 0, len);
	}else{
		tasks=accessor.getTasksBySearch(condition, title, 0, len);
	}
	
	while(tasks.size()>0){
		Iterator<TaskRecord> it = tasks.iterator();
		while (it.hasNext()) {
			TaskRecord task = it.next();
			String taskID = task.getTaskId();
			if(taskID!=null)
				accessor.deleteTask(taskID);
			File file = new File(fileDir+"/image/" + taskID);
			String[] filelist = file.list();
			if(filelist!=null){
				for(int i = 0; i < filelist.length; i++){
					File delfile = new File(fileDir+"/image/" + taskID + "/" + filelist[i]);
					if (!delfile.isDirectory())
						delfile.delete();
				}
			}
			file.delete();
		}
		if(taskIDType.equals("usesession")){//通过list传递taskid的值
			String taskIDs="";
			condition.remove("taskID");
			int num = (taskIDsListIndex+len)>selectedTaskIDsList.size()?selectedTaskIDsList.size():(taskIDsListIndex+len);
			for(int i=taskIDsListIndex;i<num;i++){
				taskIDs += selectedTaskIDsList.get(i)+",";
			}
			if(taskIDs.equals(""))
				taskIDs = "notask";
			taskIDs = taskIDs.substring(0,taskIDs.length()-1);//remove the last ','
			condition.put("taskID",taskIDs);
			taskIDsListIndex = num;
			tasks=accessor.getTasksBySearch(condition, title, 0, len);
		}else{
			tasks=accessor.getTasksBySearch(condition, title, 0, len);
		}
	}
}
String url=request.getHeader("Referer");
response.sendRedirect(url);
%>

