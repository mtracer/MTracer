<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="edu.nudt.xtrace.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.io.File" %>
<%@ page import="java.io.BufferedReader" %>
<%@ page import="java.io.InputStreamReader" %>
<%@ page import="java.io.FileInputStream" %>
<%@ page import="java.io.PrintWriter" %>
<%@ page import="java.io.IOException" %>

<HTML>
<head>
<title>Call Tree Graph of <%= request.getParameter("taskid") %></title>
</head>
<BODY>
<jsp:useBean id="accessor" class="edu.nudt.xtrace.MySQLAccessor" scope="session"/>
<jsp:useBean id="trace" class="edu.nudt.xtrace.Trace" scope="session"/>
<% 
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
%>
<a href="/">Return Home</a>&gt;&gt;<a href="/">Tasks</a>&gt;&gt;<B>CTG</B> 
<a href="/delayRateGraph.jsp?taskid=<%= taskID %>&delay=<%= delay%>">DRG</a> | <a href="op_index.jsp">Operations</a><hr> 
<input style="width:120" type="button" value="change direction" 
onclick="window.location='/callTreeGraph.jsp?taskid=<%= taskID %>&delay=<%= delay%>&direction=<%= direction.equals("LR")?"TB":"LR"%>&shape=<%= shape%>'"/>
<input style="width:120" type="button" value="change shape"
onclick="window.location='/callTreeGraph.jsp?taskid=<%= taskID %>&delay=<%= delay%>&direction=<%=direction%>&shape=<%= shape.equals("box")?"ellipse":"box"%>'"/>
<input style="width:80" type="button" value="reset" 
onclick="window.location='/callTreeGraph.jsp?taskid=<%= taskID %>&delay=<%= delay%>&direction=TB&shape=ellipse'"/>
<input style="width:80" type="button" value="clear cache" title="regenerate image" 
onclick="window.location='/callTreeGraph.jsp?taskid=<%= taskID %>&delay=<%= delay%>&direction=<%= direction%>&shape=<%= shape %>&refresh=true'"/>
<br>
<%
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
%>
</BODY>
<HTML>
