package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import edu.nudt.xtrace.*;
import java.util.*;
import java.io.File;

public final class deleteTask_jsp extends org.apache.jasper.runtime.HttpJspBase
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
      response.setContentType("text/html");
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
      edu.nudt.xtrace.MySQLAccessor accessor = null;
      synchronized (session) {
        accessor = (edu.nudt.xtrace.MySQLAccessor) _jspx_page_context.getAttribute("accessor", PageContext.SESSION_SCOPE);
        if (accessor == null){
          accessor = new edu.nudt.xtrace.MySQLAccessor();
          _jspx_page_context.setAttribute("accessor", accessor, PageContext.SESSION_SCOPE);
        }
      }
      out.write('\n');

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
	if(taskIDType.equals("usesession")){//éè¿listä¼ étaskidçå¼
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
		if(taskIDType.equals("usesession")){//éè¿listä¼ étaskidçå¼
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

      out.write('\n');
      out.write('\n');
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
