package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import edu.nudt.xtrace.*;
import java.util.*;
import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.IOException;

public final class callTreeGraph_jsp extends org.apache.jasper.runtime.HttpJspBase
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
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("<HTML>\n");
      out.write("<head>\n");
      out.write("<title>Call Tree Graph of ");
      out.print( request.getParameter("taskid") );
      out.write("</title>\n");
      out.write("</head>\n");
      out.write("<BODY>\n");
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
 
String direction = request.getParameter("direction");
if(!direction.equals("TB") && !direction.equals("LR"))direction="TB";
String shape = request.getParameter("shape");
if(!shape.equals("ellipse") && !shape.equals("box"))shape="ellipse";
String delay = request.getParameter("delay");
String taskID = request.getParameter("taskid");
String temp=application.getRealPath(request.getRequestURI());  
String fileDir=new File(temp).getParent();
String fileName = taskID + "_" + direction + "_" + shape;
String imagePath=fileDir+"/image/" + taskID + "/" + fileName + ".svg";
String dotPath = fileDir+"/image/" + taskID + "/" + fileName + ".dot";

      out.write("\n");
      out.write("<a href=\"/\">Return Home</a>&gt;&gt;<a href=\"/\">Tasks</a>&gt;&gt;<B>CTG</B> \n");
      out.write("<a href=\"/delayRateGraph.jsp?taskid=");
      out.print( taskID );
      out.write("&delay=");
      out.print( delay);
      out.write("\">DRG</a> | <a href=\"op_index.jsp\">Operations</a><hr> \n");
      out.write("<input style=\"width:120\" type=\"button\" value=\"change direction\" \n");
      out.write("onclick=\"window.location='/callTreeGraph.jsp?taskid=");
      out.print( taskID );
      out.write("&delay=");
      out.print( delay);
      out.write("&direction=");
      out.print( direction.equals("LR")?"TB":"LR");
      out.write("&shape=");
      out.print( shape);
      out.write("'\"/>\n");
      out.write("<input style=\"width:120\" type=\"button\" value=\"change shape\"\n");
      out.write("onclick=\"window.location='/callTreeGraph.jsp?taskid=");
      out.print( taskID );
      out.write("&delay=");
      out.print( delay);
      out.write("&direction=");
      out.print(direction);
      out.write("&shape=");
      out.print( shape.equals("box")?"ellipse":"box");
      out.write("'\"/>\n");
      out.write("<input style=\"width:80\" type=\"button\" value=\"reset\" \n");
      out.write("onclick=\"window.location='/callTreeGraph.jsp?taskid=");
      out.print( taskID );
      out.write("&delay=");
      out.print( delay);
      out.write("&direction=TB&shape=ellipse'\"/>\n");
      out.write("<input style=\"width:80\" type=\"button\" value=\"clear cache\" title=\"regenerate image\" \n");
      out.write("onclick=\"window.location='/callTreeGraph.jsp?taskid=");
      out.print( taskID );
      out.write("&delay=");
      out.print( delay);
      out.write("&direction=");
      out.print( direction);
      out.write("&shape=");
      out.print( shape );
      out.write("&refresh=true'\"/>\n");
      out.write("<br>\n");

String refresh = request.getParameter("refresh");
String infoString = "";
if(refresh != null)//点击刷新时删除缓存文件
	if(refresh.equals("true"))
	{
		File file = new File(fileDir+"/image/" + taskID);
		if(file.exists()){
			String[] filelist = file.list();
			for(int i = 0; i < filelist.length; i++){
				File delfile = new File(fileDir+"/image/" + taskID + "/" + filelist[i]);
				if (!delfile.isDirectory())
					delfile.delete();
			}
			file.delete();
		}
		String url = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getRequestURI()+"?";
		if(request.getQueryString() != null)
		{
			Enumeration paras=request.getParameterNames();
			while(paras.hasMoreElements()){
				String key=(String)paras.nextElement();
				String value=request.getParameter(key);
				if(!key.equals("refresh"))
					url += key + "=" + value + "&";
			}
		}
		url += "refresh=false";
		response.sendRedirect(url);
	}

File image = new File(imagePath); 
if(!image.exists())//图片不存在
{
	File  dot = new File(dotPath);
	long st,et;
	if(!dot.exists()){//dot文件不存在,则查找相关dot文件
		st = System.currentTimeMillis();
		File existFile=null;
		String tempPath = fileDir+"/image/" + taskID + "/" + taskID;
		String existDirection=direction;
		String existShape=shape;
		File tmpFile = new File(tempPath + "_TB_ellipse.dot");
		if(tmpFile.exists()){
			existDirection = "TB";existShape="ellipse";
			existFile = new File(tempPath + "_TB_ellipse.dot");
		}else{
			tmpFile = new File(tempPath + "_LR_ellipse.dot");
			if(tmpFile.exists()){
				existDirection = "LR";existShape="ellipse";
				existFile = new File(tempPath + "_LR_ellipse.dot");
			}else{
				tmpFile = new File(tempPath + "_TB_box.dot");
				if(tmpFile.exists()){
					existDirection = "TB";existShape="box";
					existFile = new File(tempPath + "_TB_box.dot");
				}else{
					tmpFile = new File(tempPath + "_LR_box.dot");
					if(tmpFile.exists()){
						existDirection = "LR";existShape="box";
						existFile = new File(tempPath + "_LR_box.dot");
					}
				}
			}
		}
		et = System.currentTimeMillis();
		infoString += "find related .dot file: "+(et - st)+"ms;";
		if(existFile == null){//相关文件不存在，只好重新生成
			st = System.currentTimeMillis();
			if(trace.findTopology(accessor.getConn(), taskID, delay) == null)
				out.print("error in construct topology");
			else if(trace.genTxt(dotPath, direction, shape)==false)
				out.print("error in display");
			et = System.currentTimeMillis();
			infoString += "gen .dot file use bean: "+(et - st)+"ms;";
		}else{//相关文件存在，则利用相关文件来生成需要的dot文件
			st = System.currentTimeMillis();
			try {
				BufferedReader bufReader =  new BufferedReader(new InputStreamReader(new FileInputStream(existFile)));
				StringBuffer strBuf = new StringBuffer();  
				for (String tmp = null; (tmp = bufReader.readLine()) != null; tmp = null) {
					tmp = tmp.replaceAll("rankdir = "+existDirection, "rankdir = "+direction);
					tmp = tmp.replaceAll("shape = "+existShape, "shape = "+shape);  
					strBuf.append(tmp);  
					strBuf.append(System.getProperty("line.separator"));  
				    }  
			    bufReader.close();  
		  
			    PrintWriter printWriter = new PrintWriter(dotPath);  
			    printWriter.write(strBuf.toString().toCharArray());  
			    printWriter.flush();  
			    printWriter.close();  
			} catch (IOException e) {  
			    e.printStackTrace();  
			}  
			et = System.currentTimeMillis();
			infoString += "gen .dot file use related file: "+(et - st)+"ms;";
		}
	}
	//有了dot文件后，生成图片
	st = System.currentTimeMillis();
	String cmd = "dot -Tsvg "+ dotPath + " -o " +  imagePath;
	Process process = Runtime.getRuntime().exec(cmd); 
	process.waitFor();
	et = System.currentTimeMillis();
	infoString += "gen image file use .dot file: "+(et - st)+"ms;";
}
//out.println(infoString);
out.println("<p><embed src=\"image/" + taskID + "/" + fileName + ".svg" +"\" type=\"image/svg+xml\" />");

      out.write("\n");
      out.write("</BODY>\n");
      out.write("<HTML>\n");
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
