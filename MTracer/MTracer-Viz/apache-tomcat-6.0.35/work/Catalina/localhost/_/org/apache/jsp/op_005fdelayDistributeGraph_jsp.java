package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import edu.nudt.xtrace.*;
import java.util.*;

public final class op_005fdelayDistributeGraph_jsp extends org.apache.jasper.runtime.HttpJspBase
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
      out.write("<HTML>\n");
      out.write("<head>\n");
      out.write("<title>Delay Distribute Graph'</title>\n");
      out.write("</head>\n");
      out.write("<BODY>\n");
      out.write("<FONT style=\"FONT-FAMILY: sans-serif\">\n");
      out.write("<div style=\"line-height:1\"> \n");
      edu.nudt.xtrace.MySQLAccessor accessor = null;
      synchronized (session) {
        accessor = (edu.nudt.xtrace.MySQLAccessor) _jspx_page_context.getAttribute("accessor", PageContext.SESSION_SCOPE);
        if (accessor == null){
          accessor = new edu.nudt.xtrace.MySQLAccessor();
          _jspx_page_context.setAttribute("accessor", accessor, PageContext.SESSION_SCOPE);
        }
      }
      out.write('\n');

int num = 0;
int N;
try{N = Integer.parseInt(request.getParameter("n"));}catch(Exception e){N=8;}
double maxDelay = -1;
double minDelay = Double.MAX_VALUE;
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

      out.write("\n");
      out.write("<a href=\"/\">Return Home</a> &gt;&gt;<a href=\"op_index.jsp\">Operations</a> &gt;&gt;<a href=\"op_tasksWithOp.jsp?");
      out.print(partUrl);
      out.write("\">Operation Detail</a>&gt;&gt;\n");
      out.write("<B>DDG</B> <a href=\"op_hostDistributeGraph.jsp?");
      out.print(partUrl);
      out.write("\">HDG</a> <a href=\"op_delayTrendGraph.jsp?");
      out.print(partUrl);
      out.write("\">DTG</a> | <a href=\"index.jsp\">Tasks</a>\n");
      out.write("</div><hr/>\n");

int off = 0;int len =50;
List<Report> taskWithOp=accessor.getTasksByOp(condition, title, off, len);
while(taskWithOp.size()>0)
{
	int currentRecordNum=0;
	taskWithOp=accessor.getTasksByOp(condition, title, off, len);
	Iterator<Report> it = taskWithOp.iterator();
	while (it.hasNext()) {
		currentRecordNum++;
		Report rep = it.next();
		double delay=-1;
		try{delay=Double.parseDouble(rep.get("Delay").get(0));}catch(Exception e){continue;}
        	if(delay>maxDelay)
			maxDelay=delay;
		if(delay<minDelay)
			minDelay=delay;
		num++;
        }
	off = off + currentRecordNum;
	taskWithOp=accessor.getTasksByOp(condition, title, off, len);
}

      out.write("\n");
      out.write("<form action=\"\" method=\"get\" name=\"changeForm\">\n");
      out.write("\t<B>Change N: </B>\n");
      out.write("\t<input type=hidden name=\"opName\" value= \"");
      out.print( request.getParameter("opName")==null ? "*" : request.getParameter("opName"));
      out.write("\" /> \n");
      out.write("\t<input type=hidden name=\"delayFrom\" value= \"");
      out.print( request.getParameter("delayFrom")==null ? "0" : request.getParameter("delayFrom"));
      out.write("\" />\n");
      out.write("\t<input type=hidden name=\"delayTo\" value= \"");
      out.print( request.getParameter("delayTo")==null ? "*" : request.getParameter("delayTo"));
      out.write("\" />\n");
      out.write("\t<input type=hidden name=\"address\" value= \"");
      out.print( request.getParameter("address")==null ? "*" : request.getParameter("address"));
      out.write("\" />\n");
      out.write("\t<input type=hidden name=\"name\" value= \"");
      out.print( request.getParameter("name")==null ? "*" : request.getParameter("name"));
      out.write("\" />\n");
      out.write("\t<input type=hidden name=\"agent\" value= \"");
      out.print( request.getParameter("agent")==null ? "*" : request.getParameter("agent"));
      out.write("\" />\n");
      out.write("\t<input type=hidden name=\"title\" value= \"");
      out.print( request.getParameter("title")==null ? "*" : request.getParameter("title"));
      out.write("\" /> \n");
      out.write("\t<input type=hidden name=\"timeFrom\" value= \"");
      out.print( request.getParameter("timeFrom")==null ? "1970-01-01 00:00:00" : request.getParameter("timeFrom"));
      out.write("\" />\n");
      out.write("\t<input type=hidden name=\"timeTo\" value= \"");
      out.print( request.getParameter("timeTo")==null ? "*" : request.getParameter("timeTo"));
      out.write("\" />\n");
      out.write("\t<input type=hidden name=\"taskID\" value=\"");
      out.print( request.getParameter("taskID")==null ? "*" : request.getParameter("taskID"));
      out.write("\" />\n");
      out.write("\t<input style=\"width:50\" name=\"n\" value=\"");
      out.print( request.getParameter("n")==null ? "8" : request.getParameter("n"));
      out.write("\" />\n");
      out.write("\t<input style=\"width:80\" type=\"submit\" value=\"change\" />\n");
      out.write("</form>\n");

String opName = request.getParameter("opName")==null?"*":request.getParameter("opName");
if(opName.indexOf(",")!=-1 || opName.indexOf("*")!=-1) 
	out.print("<font color = red>WARNING: There may be more than 1 kind of OPs(OpName = " + opName + "), the DDG maybe meanless</font><p>");
off = 0;len =50;
if(maxDelay == minDelay){
	N=1;
	out.print("<font color = red>WARNING: N is too big, change to "+N+"</font><p>");
	
}else if((maxDelay - minDelay)/N<0.01)
{
	N=(int)Math.round((maxDelay-minDelay)*100);
	out.print("<font color = red>WARNING: N is too big, change to "+N+"</font><p>");
}
double interval = (maxDelay - minDelay)/N;

int [] count = new int[N];
for(int i=0; i<N; i++)
	count[i]=0;
while(off<num)
{
	taskWithOp=accessor.getTasksByOp(condition, title, off, len);
	Iterator<Report> it = taskWithOp.iterator();
	while (it.hasNext()) {
        	Report rep = it.next();
		double delay;
		try{delay=Double.parseDouble(rep.get("Delay").get(0));}catch(Exception e){continue;}
		if(delay == minDelay)
			count[0]++;
		else{
			int index = (int)Math.ceil((delay - minDelay)/interval)-1;
			if(index>=N) 
				count[N-1]++;
			else
				count[index]++;
		}
        }
	off = off + len;
}
int maxCount=count[0];
for(int i=1;i<N;i++)
{
	if(count[i]>maxCount)
		maxCount=count[i];
}
double factor= (double)100/maxCount;
String table;
table = "<table border=1 cellspacing=0 cellpadding=3>";
table += "<tr>";
table += "<th width=200>Delay</th>";
table += "<th>Count</th>";
table += "<th width=400>Count graph</th>";
table += "<th width=80>Rate</th>";
table += "</tr>";

String tmpUrl="";
para=request.getParameterNames();
while(para.hasMoreElements()){
	String key=(String)para.nextElement();
	String value=request.getParameter(key);
	if(!key.equals("delayFrom")&&!key.equals("delayTo"))
		tmpUrl += key + "=" + value + "&";
}
for(int i=0;i<N;i++)
{
	table += "<tr>";
	String df=String.format("%.2f",minDelay+i*interval);
	String dt=String.format("%.2f",minDelay+(i+1)*interval);
	table += "<td align = center>";
	if(count[i]>0)
		table+="<a title = \"see all related tasks\" href=\"/op_tasksWithOp.jsp?"+tmpUrl+"delayFrom="+df+"&delayTo="+dt+"\">";
	if(i==0)table+="[";else table+="(";
	table += df + ", "+ dt +"]";
	if(count[i]>0) table+="</a>";
	table+="</td>";
	table += "<td>" + count[i] + "</td>";
	table += "<td>";
	for(int j=0;j<(int)(factor*count[i]);j++)
		table+="\\";
	table += "&nbsp;</td>";
	table += "<td align = center>" + (int)Math.round((double)100*count[i]/num)+"%" + "</td>";
	table += "</tr>";
}
out.print(table);

      out.write("\n");
      out.write("</FONT>\n");
      out.write("\n");
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
