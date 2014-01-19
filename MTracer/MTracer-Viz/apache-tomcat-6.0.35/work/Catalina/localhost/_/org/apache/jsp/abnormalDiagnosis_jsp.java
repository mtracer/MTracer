package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import edu.nudt.xtrace.*;
import java.util.*;
import java.io.*;

public final class abnormalDiagnosis_jsp extends org.apache.jasper.runtime.HttpJspBase
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
      edu.nudt.xtrace.Trace trace = null;
      synchronized (session) {
        trace = (edu.nudt.xtrace.Trace) _jspx_page_context.getAttribute("trace", PageContext.SESSION_SCOPE);
        if (trace == null){
          trace = new edu.nudt.xtrace.Trace();
          _jspx_page_context.setAttribute("trace", trace, PageContext.SESSION_SCOPE);
        }
      }
      out.write('\n');
      edu.nudt.xtrace.AbnormalDiagnosis diagnosisor = null;
      synchronized (session) {
        diagnosisor = (edu.nudt.xtrace.AbnormalDiagnosis) _jspx_page_context.getAttribute("diagnosisor", PageContext.SESSION_SCOPE);
        if (diagnosisor == null){
          diagnosisor = new edu.nudt.xtrace.AbnormalDiagnosis();
          _jspx_page_context.setAttribute("diagnosisor", diagnosisor, PageContext.SESSION_SCOPE);
        }
      }
      out.write('\n');

//è·åæ¥è¯¢æ¡ä»¶
Report condition = new Report();
Report title = new Report();
Enumeration para=request.getParameterNames();
while(para.hasMoreElements()){
	String key=(String)para.nextElement();
	String value=request.getParameter(key);
	if(!key.equals("sortValue")&&!key.equals("sortStyle"))
		condition.put(key, value);
}

String taskIDType=condition.get("taskID")==null ? "useUrl" : condition.get("taskID").get(0);
if(taskIDType.equals("usesession")){//éè¿listä¼ étaskidçå¼
	condition.remove("taskID");
	String sessionKey=condition.get("sessionKey").get(0);
	ArrayList<String> selectedTaskIDsList=(ArrayList<String>)session.getAttribute(sessionKey);
	String taskIDs="";
	for(int i=0;i<selectedTaskIDsList.size();i++)
		taskIDs += selectedTaskIDsList.get(i)+",";
	taskIDs = taskIDs.substring(0,taskIDs.length()-1);//remove the last ','
	condition.put("taskID",taskIDs);
}

//è·ååå§æ°æ®
int depth = 0;
ArrayList<double[]> metadata = new ArrayList<double[]>();//å­å¨åå§æ°æ®
ArrayList<String> taskids = new ArrayList<String>();
try {
	int off = 0;int len = 50;
	List<TaskRecord> tasks=accessor.getTasksBySearch(condition, title, off, len);
	while(tasks.size()>0)
	{
		int count = 0;
		Iterator<TaskRecord> it = tasks.iterator();
		while (it.hasNext()) {
			TaskRecord task = it.next();
			String taskID = task.getTaskId();
			if(trace.findTopology(accessor.getConn(), taskID, String.valueOf(task.getDelay())) != null){
				trace.depthFirstSearchOrder();
				ArrayList<Report> reports = trace.getReports();
				double [] row = new double[reports.size()];
				int k=0;
				for(k=0; k<reports.size();k++)
				{
					Report rep = reports.get(k);
					int index=-1;
					try{index = Integer.parseInt(rep.get("dfsorder").get(0));}catch(Exception e){break;}
					int tmpDepth=0;
					try{tmpDepth = Integer.parseInt(rep.get("depth").get(0));}catch(Exception e){break;}
					if(tmpDepth!=0 && tmpDepth+1>depth)
						depth = tmpDepth+1;
					double delay=-1;
					try{delay = Double.parseDouble(rep.get("delay").get(0));}catch(Exception e){break;}
					if(index<0 || index>=reports.size() || delay<0)break;
					row[index]=delay;
				}
				if(k==reports.size()){
					taskids.add(taskID);
					metadata.add(row);
				}
				count++;
			}
		}
		off=off+count;
		tasks=accessor.getTasksBySearch(condition, title, off, len);
	}
}catch(Exception e){}
if(metadata.size()<2){
	out.print("<font color = red>WARNING: You have selected only one trace - PCA is not meaningful</font><p>");
	return;
}
/*//original data
for(int i=0;i<metadata.size();i++){
	out.print(taskids.get(i)+"["+i+"]:");
	double[] tmp=metadata.get(i);
	for(int j=0;j<tmp.length;j++)
		out.print(tmp[j]+"  ");
	out.print("["+tmp.length+"]<p>");
}*/

String[] taskIDs = new String[taskids.size()];
for(int i=0;i<taskIDs.length;i++)
	taskIDs[i]=taskids.get(i);
double[][] responses = new double[metadata.size()][metadata.get(0).length];
for(int i=0;i<metadata.size();i++)
	responses[i]=metadata.get(i);

//double[][] tmp={{12.5,586},{24,754},{15.3,850},{18,667},{31.2,750}};
//double[][] tmp={{1,0,0},{2,0,0},{2,0,0},{3,0,0},{1,0,0},{2,0,0},{2,0,0},{3,0,0},{2,10,0}};
//double[][] tmp={{12.5,0.01},{14,-0.01},{15.3,0},{11,0.05},{9.2,0.1},{10.5,-0.01},{11.5,-0.01},{9.3,-0.01},{11.8,0.05},{11.2,0.05},{14,-0.01},{15.3,0},{11,0.05},{9.2,0.1},{10.5,-0.01},{11.5,-0.01},{9.3,-0.01},{11.8,0.05},{11.2,0.05},{12,5}};

//diagnosisor.calAbnormalRows(responses, taskIDs);
//ArrayList<DiagnosisResult> result = diagnosisor.calAbnormalOperation(depth);
ArrayList<DiagnosisResult> result = diagnosisor.diagnosis(responses,taskIDs,depth);
/*//data after 0-means
double[][] tmp = diagnosisor.getDataMatrix();
for(int i=0;i<tmp.length;i++){
	for(int j=0;j<tmp[0].length;j++)
		out.print(tmp[i][j]);
	out.print("<p>");
}
*/
/*
if(result!=null){
	for(int i=0;i<result.size();i++)
		out.print(result.get(i)+"<p>");
}
int[] cols = diagnosisor.getPrincipalCols();
for(int i=0;i<cols.length;i++)
	out.print("["+cols[i]+"]  ");
out.print("<p>");
double[][] data = diagnosisor.getOriginalData();
for(int i=0;i<data.length;i++){
	out.print(taskIDs[i]+"::");
	for(int j=0;j<cols.length;j++){
		out.print(data[i][cols[j]]+"  ");
	}
	out.print("<p>");
}
*//*
//eigenvalues and eigenvectors
double[] val = diagnosisor.getEigenvalues();
double valsum=0;
for(int i=0;i<val.length;i++)
	valsum+=val[i];
double[][] vec=diagnosisor.getEigenvectors();
out.print("eigenvalues:<p>");
double partvalsum=0;
for(int i=0;i<val.length;i++){
	out.print(val[i]+"  ");
}
out.print("<p>");
for(int i=0;i<val.length;i++){
	partvalsum+=val[i];
	out.print(partvalsum*100/valsum+"%  ");
}
out.print("<p>");
out.print("eigenvectors:<p>");
for(int i=0;i<vec.length;i++){
	for(int j=0;j<vec[0].length;j++)
		out.print(vec[j][i]+" ");
	out.print("<p>");
}
out.print("selected eigenvectors:<p>");
out.print(diagnosisor.getPrincipalComponentNum());
out.print("<p>");

out.print("principal operations:<p>");
int[] principalOps=diagnosisor.getPrincipalCols();
for(int i=0;i<principalOps.length;i++)
	out.print("["+principalOps[i]+"],");
out.print("<p>");
//
String[] names = diagnosisor.getRowNames();
double[] projection = diagnosisor.getProjection();
for(int i=0;i<names.length;i++){
	out.print("["+names[i]+"]:");
	out.print(projection[i]+"<p>");
}
out.print("Q="+diagnosisor.getThreshold());
*/

ArrayList<String> abnormalTasks = new ArrayList<String>(); 
if(result!=null){
	for(int i=0;i<result.size();i++)
		abnormalTasks.add(result.get(i).getTaskID());
}
session.setAttribute("abnormalTasks",abnormalTasks);
session.setAttribute("abnormalTaskInfo",result);
String url="/?taskID=usesession&sessionKey=abnormalTasks&sortValue=delay&sortStyle=descend";
url+="&abnormalList=true";//refresh the call tree graphs(CTG)
response.sendRedirect(url);

      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
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
