package edu.berkeley.xtrace.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.DefaultServlet;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.servlet.CGI;

import edu.berkeley.xtrace.TaskID;
import edu.berkeley.xtrace.XTraceException;
import edu.berkeley.xtrace.reporting.Report;
/**
 * @author zjw
 *
 */
public final class MySQLXTraceServer {
	private static final Logger LOG = Logger.getLogger(XTraceServer.class);

	private static ReportSource[] sources;//接收器
	
	private static BlockingQueue<String> incomingReportQueue, reportsToStorageQueue;//接受队列，待写入队列

	private static ThreadPerTaskExecutor sourcesExecutor;//接收器线程池

	private static ExecutorService storeExecutor;//数据写入器线程池

	private static MySQLReportStore reportstore;//数据写入器
	
	private static final DateFormat JSON_DATE_FORMAT =
		new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
	//private static final DateFormat HTML_DATE_FORMAT =
		//new SimpleDateFormat("MM dd yyyy, HH:mm:ss"); 
	private static final DateFormat HTML_DATE_FORMAT =
			new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
	
	// Default number of results to show per page for web UI
	private static final int PAGE_LENGTH = 25;
	
	public static void main(String[] args) {
		System.setProperty("xtrace.server.sources", "edu.berkeley.xtrace.server.UdpReportSource");
		//System.setProperty("xtrace.udpsource","127.0.0.1:7831");
		//System.setProperty("xtrace.udpsource","10.107.100.39:7831");
		System.setProperty("xtrace.server.store","edu.berkeley.xtrace.server.MySQLReportStore");
		System.setProperty("xtrace.server.syncinterval", "5");
		//System.setProperty("xtrace.backend.webui.dir", "/home/zjw8612/workspace/logging/XTrace/webui");
		System.setProperty("xtrace.backend.httpport","8888");
		
		System.out.println("beginning...");
		
		System.out.println("beginning setupReportSources...");
		setupReportSources();
		System.out.println("setupReportSources done!\n");
		
		System.out.println("\nbeginning setupReportStore...");
		setupReportStore();
		System.out.println("setupReportStore done!\n");
		
		System.out.println("\nbeginning setupBackplane...");
		setupBackplane();
		System.out.println("setupBackplane done!\n");
		
		//System.out.println("\nbeginning setupWebInterface...");
		//setupWebInterface();
		//System.out.println("setupWebInterface done!\n");
		
		System.out.println("done!");
	}

	private static void setupReportSources() {
		
		incomingReportQueue = new ArrayBlockingQueue<String>(1024, true);
		sourcesExecutor = new ThreadPerTaskExecutor();
		
		// Default input sources
		String sourcesStr = "edu.berkeley.xtrace.server.UdpReportSource";
		
		if (System.getProperty("xtrace.server.sources") != null) {
			sourcesStr = System.getProperty("xtrace.server.sources");
		} else {
			LOG.warn("No server report sources specified... using defaults (Udp)");
		}
		String[] sourcesLst = sourcesStr.split(",");
		
		sources = new ReportSource[sourcesLst.length];
		for (int i = 0; i < sourcesLst.length; i++) {
			try {
				LOG.info("Starting report source '" + sourcesLst[i] + "'");
				sources[i] = (ReportSource) Class.forName(sourcesLst[i]).newInstance();
			} catch (InstantiationException e1) {
				LOG.fatal("Could not instantiate report source", e1);
				System.exit(-1);
			} catch (IllegalAccessException e1) {
				LOG.fatal("Could not access report source", e1);
				System.exit(-1);
			} catch (ClassNotFoundException e1) {
				LOG.fatal("Could not find report source class", e1);
				System.exit(-1);
			}
			sources[i].setReportQueue(incomingReportQueue);
			try {
				sources[i].initialize();
			} catch (XTraceException e) {
				LOG.warn("Unable to initialize report source", e);
				// TODO: gracefully shutdown any previously started threads?
				System.exit(-1);
			}
			sourcesExecutor.execute((Runnable) sources[i]);
			
			Runtime.getRuntime().addShutdownHook(new Thread() {
				  public void run() {
					  sources[0].shutdown();
				  }
				});
		}
	}
	
	private static void setupReportStore() {
		reportsToStorageQueue = new ArrayBlockingQueue<String>(1024);
		
		String storeStr = "edu.berkeley.xtrace.server.MySQLReportStore";
		if (System.getProperty("xtrace.server.store") != null) {
			storeStr = System.getProperty("xtrace.server.store");
		} else {
			LOG.warn("No server report store specified... using default (MySQLReportStore)");
		}
		
		reportstore = null;
		try {
			reportstore = (MySQLReportStore) Class.forName(storeStr).newInstance();
		} catch (InstantiationException e1) {
			LOG.fatal("Could not instantiate report store", e1);
			System.exit(-1);
		} catch (IllegalAccessException e1) {
			LOG.fatal("Could not access report store class", e1);
			System.exit(-1);
		} catch (ClassNotFoundException e1) {
			LOG.fatal("Could not find report store class", e1);
			System.exit(-1);
		}
		
		reportstore.setReportQueue(reportsToStorageQueue);
		try {
			reportstore.initialize();
		} catch (XTraceException e) {
			LOG.fatal("Unable to start report store", e);
			System.exit(-1);
		}
		
		storeExecutor = Executors.newSingleThreadExecutor();
		storeExecutor.execute(reportstore);
		
		/* Every N seconds we should sync the report store */
		String syncIntervalStr = System.getProperty("xtrace.server.syncinterval", "5");
		long syncInterval = Integer.parseInt(syncIntervalStr);
		Timer timer= new Timer();
		timer.schedule(new SyncTimer(reportstore), syncInterval*1000, syncInterval*1000);//这里没有用了
		
		/* Add a shutdown hook to flush and close the report store */
		Runtime.getRuntime().addShutdownHook(new Thread() {
		  public void run() {
			  reportstore.shutdown();
		  }
		});
	}
	
	public static int countTransferReport;//3// number of reports transfered from incomingReportQueue to reportsToStorageQueue
	public static long timeTransferReport;//3// time of reports transfered from incomingReportQueue to reportsToStorageQueue
	public static int countQueueLengthLT100;//3
	public static int countQueueLengthLT500;//3
	public static int countQueueLengthLT800;//3
	public static int countQueueLengthLT1000;//3
	public static int countQueueLengthMT1000;//3
	public static int countQueueLengthEQ1024;//3
	public static int maxQueueLength;//3
	public static int countQueueIncrease;//3
	
	private static void setupBackplane() {
		new Thread(new Runnable() {
			public void run() {
				LOG.info("Backplane waiting for packets");
				countTransferReport=0;//3
				timeTransferReport=0;//3
				countQueueLengthLT100=0;//3
				countQueueLengthLT500=0;//3
				countQueueLengthLT800=0;//3
				countQueueLengthLT1000=0;//3
				countQueueLengthMT1000=0;//3
				countQueueLengthEQ1024=0;//3
				maxQueueLength=0;//3
				countQueueIncrease=0;//3
				int oldQueueLength=0;//3
				
				while (true) {
					long st= System.nanoTime();//3
					String msg = null;
					try {
						msg = incomingReportQueue.take();
					} catch (InterruptedException e) {
						LOG.warn("Interrupted", e);
						continue;
					}
					
					int size=reportsToStorageQueue.size();//3
					if(size<100){//3
						countQueueLengthLT100++;//3
					}else if(size<500){//3
						countQueueLengthLT500++;//3
					}else if(size<800){//3
						countQueueLengthLT800++;//3
					}else if(size<1000){//3
						countQueueLengthLT1000++;//3
					}else{//3
						countQueueLengthMT1000++;//3
					}//3
					if(size==1024)//3
						countQueueLengthEQ1024++;//3
					if(maxQueueLength<size)//3
						maxQueueLength=size;//3
					
					reportsToStorageQueue.offer(msg);
					long et= System.nanoTime();//3
					size=reportsToStorageQueue.size();//3
					if(size>=oldQueueLength || size==1024)//3
						countQueueIncrease++;//3
					oldQueueLength=size;//3
					if(maxQueueLength<size)//3
						maxQueueLength=size;//3
					
					countTransferReport++;//3
					timeTransferReport+=et-st;//3
				}
			}
		}).start();
	}
	
	private static class ThreadPerTaskExecutor implements Executor {
	     public void execute(Runnable r) {
	         new Thread(r).start();
	     }
	 }
	
	private static void setupWebInterface() {
		String webDir = System.getProperty("xtrace.backend.webui.dir");
		if (webDir == null) {
			LOG.warn("No webui directory specified... using default (./src/webui)");
			webDir = "./src/webui";
		}
    
    int httpPort =
      Integer.parseInt(System.getProperty("xtrace.backend.httpport", "8080"));

		// Initialize Velocity template engine
		try {
			Velocity.setProperty(Velocity.RUNTIME_LOG_LOGSYSTEM_CLASS,
					"org.apache.velocity.runtime.log.Log4JLogChute");
			Velocity.setProperty("runtime.log.logsystem.log4j.logger",
					"edu.berkeley.xtrace.server.XTraceServer");
			Velocity.setProperty("file.resource.loader.path", webDir + "/templates");
			Velocity.setProperty("file.resource.loader.cache", "true");
			Velocity.init();
		} catch (Exception e) {
			LOG.warn("Failed to initialize Velocity", e);
		}
		
		// Create Jetty server
    Server server = new Server(httpPort);
    Context context = new Context(server, "/");
    
    // Create a CGI servlet for scripts in webui/cgi-bin 
    ServletHolder cgiHolder = new ServletHolder(new CGI());
    cgiHolder.setInitParameter("cgibinResourceBase", webDir + "/cgi-bin");
    if (System.getenv("PATH") != null) {
    	// Pass any special PATH setting on to the execution environment
    	cgiHolder.setInitParameter("Path", System.getenv("PATH"));
    }
    context.addServlet(cgiHolder, "*.cgi");
    context.addServlet(cgiHolder, "*.pl");
    context.addServlet(cgiHolder, "*.py");
    context.addServlet(cgiHolder, "*.rb");
    context.addServlet(cgiHolder, "*.tcl");

    context.addServlet(new ServletHolder(
        new GetReportsServlet()), "/reports/*");
    context.addServlet(new ServletHolder(//added by zjw
            new GetEdgesServlet()), "/edges/*");
    context.addServlet(new ServletHolder(
        new GetLatestTaskServlet()), "/latestTask");
    //没有tag了
  //  context.addServlet(new ServletHolder(
   //     new TagServlet()), "/tag/*");
    context.addServlet(new ServletHolder(
        new TitleServlet()), "/title/*");
    
    context.addServlet(new ServletHolder(//added by zjw
            new searchServlet()), "/search/*");
    //context.addServlet(new ServletHolder(
     //   new TitleLikeServlet()), "/titleLike/*");
    
    // Add an IndexServlet as the default servlet. This servlet will serve
    // a human-readable (HTML) latest tasks page for "/" and serve static
    // content for any other URL. Being the default servlet, it will get
    // invoked only for URLs that does not match the other patterns where we
    // have registered servlets above.
    context.setResourceBase(webDir + "/html");
    context.addServlet(new ServletHolder(new IndexServlet()), "/");//初始页面
    
    try {
      server.start();
    } catch (Exception e) {
      LOG.warn("Unable to start web interface", e);
    }
	}
	
	private static class GetReportsServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
      response.setContentType("text/plain");
      response.setStatus(HttpServletResponse.SC_OK);
      String uri = request.getRequestURI();
      int pathLen = request.getServletPath().length() + 1;
      String taskId = uri.length() > pathLen ? uri.substring(pathLen) : null;
      Writer out = response.getWriter();
      if (taskId != null) {
        Iterator<Report> iter;
        try {
          iter = reportstore.getReportsByTask(TaskID.createFromString(taskId));
        } catch (XTraceException e) {
          throw new ServletException(e);
        }
        while (iter.hasNext()) {
        	Report r = iter.next();
          out.write(r.toString());
          out.write("\n");
        }
      }
    }
  }
	
	private static class GetEdgesServlet extends HttpServlet {//added by zjw
	    protected void doGet(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {
	      response.setContentType("text/plain");
	      response.setStatus(HttpServletResponse.SC_OK);
	      String uri = request.getRequestURI();
	      int pathLen = request.getServletPath().length() + 1;
	      String taskId = uri.length() > pathLen ? uri.substring(pathLen) : null;
	      Writer out = response.getWriter();
	      if (taskId != null) {
	        Iterator<Report> iter;
	        try {
	          iter = reportstore.getEdgesByTask(TaskID.createFromString(taskId));
	        } catch (XTraceException e) {
	          throw new ServletException(e);
	        }
	        while (iter.hasNext()) {
	        	Report r = iter.next();
	          out.write(r.toString());
	          out.write("\n");
	        }
	      }
	    }
	  }
	
	private static class GetLatestTaskServlet extends HttpServlet {
	  protected void doGet(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {
      response.setContentType("text/plain");
      response.setStatus(HttpServletResponse.SC_OK);
      Writer out = response.getWriter();
      
      List<TaskRecord> task = reportstore.getLatestTasks(0, 1);
      if (task.size() != 1) {
        LOG.warn("getLatestTasks(1) returned " + task.size() + " entries");
        return;
      }
      try {
        Iterator<Report> iter = reportstore.getReportsByTask(task.get(0).getTaskId());
        while (iter.hasNext()) {
          Report r = iter.next();
          out.write(r.toString());
          out.write("\n");
        }
      } catch (XTraceException e) {
        LOG.warn("Internal error", e);
        out.write("Internal error: " + e);
      }
	  }
	}
  /*
  private static class TagServlet extends HttpServlet {
		protected void doGet(HttpServletRequest request,
				HttpServletResponse response) throws ServletException, IOException {
			String tag = getUriPastServletName(request);
			if (tag == null || tag.equalsIgnoreCase("")) {
				response.sendError(505, "No tag given");
			} else {
				Collection<TaskRecord> taskInfos = reportstore.getTasksByTag(
						tag, getOffset(request), getLength(request));
				showTasks(request, response, taskInfos, "Tasks with tag: " + tag, false);
			}
		}
	}
  */
  private static class TitleServlet extends HttpServlet {
		protected void doGet(HttpServletRequest request,
				HttpServletResponse response) throws ServletException, IOException {
			String title = getUriPastServletName(request);
			if (title == null || title.equalsIgnoreCase("")) {
				response.sendError(505, "No title given");
			} else {
				Collection<TaskRecord> taskInfos = reportstore.getTasksByTitle(
						title, getOffset(request), getLength(request));
				showTasks(request, response, taskInfos, "Tasks with title: " + title, false);
			}
		}
	}
  
  private static class searchServlet extends HttpServlet {//added by zjw
		protected void doGet(HttpServletRequest request,
				HttpServletResponse response) throws ServletException, IOException {
			String s = getUriPastServletName(request);
			s = s.replaceAll("=", ": ");
			s = s.replace('&', '\n');
			s = "X-Trace Report ver 1.0\n" + s;
			Report r =Report.createFromString(s);//用于存储参数
			
			Report title = new Report();
			Collection<TaskRecord> taskInfos = reportstore.getTasksBySearch(r, title, getOffset(request), getLength(request));
			List<String> disList = title.get("title");
			String dis = new String();
			if(disList == null)
				dis = "Research result";
			else
				dis = disList.get(0);
			showTasks(request, response, taskInfos, dis, false);
		}
	}
	/*
  private static class TitleLikeServlet extends HttpServlet {
		protected void doGet(HttpServletRequest request,
				HttpServletResponse response) throws ServletException, IOException {
			String title = getUriPastServletName(request);
			if (title == null || title.equalsIgnoreCase("")) {
				response.sendError(505, "No title given");
			} else {
				Collection<TaskRecord> taskInfos = reportstore.getTasksByTitleSubstring(
						title, getOffset(request), getLength(request));
				showTasks(request, response, taskInfos, "Tasks with title like: " + title, false);
			}
		}
	}
  */
  private static class IndexServlet extends DefaultServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
      if(request.getRequestURI().equals("/")) {
        Collection<TaskRecord> tasks = reportstore.getLatestTasks(getOffset(request), getLength(request));
        showTasks(request, response, tasks, "X-Trace Latest Tasks", true);
      } else {
        super.doGet(request, response);
      }
    }
  }

	private static String getUriPastServletName(HttpServletRequest request) {
		String uri = request.getRequestURI();
		int pathLen = request.getServletPath().length() + 1;
		String text = uri.length() > pathLen ? uri.substring(pathLen) : null;
		if (text != null) {
			try {
				text = URLDecoder.decode(text, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return null;
			}
		}
		return text;
	}

  private static void showTasks(HttpServletRequest request,
			HttpServletResponse response, Collection<TaskRecord> tasks, String title, boolean showDbStats) throws IOException {
		if ("json".equals(request.getParameter("format"))) {
			response.setContentType("text/plain");
		}
		else {
			response.setContentType("text/html");
		}
	  	int offset = getOffset(request);
	  	int length = getLength(request);
	    // Create Velocity context
		VelocityContext context = new VelocityContext();
		context.put("tasks", tasks);
		context.put("title", title);
		context.put("reportStore", reportstore);
		context.put("request", request);
		context.put("offset", offset);
		context.put("length", length);
		context.put("lastResultNum", offset + length - 1);
		context.put("prevOffset", Math.max(0, offset - length));
		context.put("nextOffset", offset + length);
		context.put("showStats", showDbStats);
		context.put("JSON_DATE_FORMAT", JSON_DATE_FORMAT);
	   	context.put("HTML_DATE_FORMAT", HTML_DATE_FORMAT);
	   	context.put("PAGE_LENGTH", PAGE_LENGTH);
	    
	   	/*added by zjw for search where SC = Search Condition*/
	   	String SC_TaskID, SC_Title, SC_TimeFrom, SC_TimeTo, SC_DelayFrom, SC_DelayTo, SC_NumReportsFrom, SC_NumReportsTo;
	   	String SC_NumEdgesFrom, SC_NumEdgesTo, SC_SortValue, SC_SortStyle;
	   	String SC_SortValue_Time, SC_SortValue_Delay, SC_SortValue_ReportNum, SC_SortValue_EdgeNum, SC_SortValue_Title, SC_SortValue_TaskID;
	   	String SC_SortValue_Default, SC_SortValue_Descend, SC_SortValue_Ascend;
	   	SC_TaskID = "*"; SC_Title = "*"; SC_TimeFrom = "1970-01-01 00:00:00";
	   	SC_TimeTo = "*"; SC_DelayFrom = "0"; SC_DelayTo = "*";
	   	SC_NumReportsFrom = "1"; SC_NumReportsTo = "*";
	   	SC_NumEdgesFrom = "1"; SC_NumEdgesTo = "*";
	   	SC_SortValue = "time"; SC_SortStyle = "default";
	   	SC_SortValue_Time = "selected"; SC_SortValue_Delay = ""; SC_SortValue_ReportNum = "";
	   	SC_SortValue_EdgeNum = ""; SC_SortValue_Title = ""; SC_SortValue_TaskID = "";
	   	SC_SortValue_Default = "selected"; SC_SortValue_Descend = ""; SC_SortValue_Ascend = "";
	   	
	   	String s = getUriPastServletName(request);
	   	if(s != null)
	   	{
			s = s.replaceAll("=", ": ");
			s = s.replace('&', '\n');
			s = "X-Trace Report ver 1.0\n" + s;
			Report r =Report.createFromString(s);//用于存储参数
			if(r.get("taskID") != null) SC_TaskID = r.get("taskID").get(0);
			if(r.get("title") != null) SC_Title = r.get("title").get(0);
			if(r.get("timeFrom") != null) SC_TimeFrom = r.get("timeFrom").get(0);
			if(r.get("timeTo") != null) SC_TimeTo = r.get("timeTo").get(0);
			if(r.get("delayFrom") != null) SC_DelayFrom = r.get("delayFrom").get(0);
			if(r.get("delayTo") != null) SC_DelayTo = r.get("delayTo").get(0);
			if(r.get("numReportsFrom") != null) SC_NumReportsFrom = r.get("numReportsFrom").get(0);
			if(r.get("numReportsTo") != null) SC_NumReportsTo = r.get("numReportsTo").get(0);
			if(r.get("numEdgesFrom") != null) SC_NumEdgesFrom = r.get("numEdgesFrom").get(0);
			if(r.get("numEdgesTo") != null) SC_NumEdgesTo = r.get("numEdgesTo").get(0);
			if(r.get("sortValue") != null) 
			{
				SC_SortValue = r.get("sortValue").get(0);
				SC_SortValue_Time = ""; SC_SortValue_Delay = ""; SC_SortValue_ReportNum = "";
			   	SC_SortValue_EdgeNum = ""; SC_SortValue_Title = ""; SC_SortValue_TaskID = "";
			   	if(SC_SortValue.equals("delay")) SC_SortValue_Delay = "selected";
			   	else if(SC_SortValue.equals("reportNum")) SC_SortValue_ReportNum = "selected";
			   	else if(SC_SortValue.equals("edgeNum")) SC_SortValue_EdgeNum = "selected";
			   	else if(SC_SortValue.equals("title")) SC_SortValue_Title = "selected";
			   	else if(SC_SortValue.equals("taskid")) SC_SortValue_TaskID = "selected";
			   	else SC_SortValue_Time = "selected";
			}
			
			if(r.get("sortStyle") != null)
			{
				SC_SortStyle = r.get("sortStyle").get(0);
				SC_SortValue_Default = ""; SC_SortValue_Descend = ""; SC_SortValue_Ascend = "";
				if(SC_SortStyle.equals("ascend")) SC_SortValue_Ascend = "selected";
			   	else if(SC_SortStyle.equals("descend")) SC_SortValue_Descend = "selected";
			   	else SC_SortValue_Default = "selected";
			}
	   	}
	   	context.put("SC_TaskID", SC_TaskID);
	   	context.put("SC_Title", SC_Title);
	   	context.put("SC_TimeFrom", SC_TimeFrom);
	   	context.put("SC_TimeTo", SC_TimeTo);
	   	context.put("SC_DelayFrom", SC_DelayFrom);
	   	context.put("SC_DelayTo", SC_DelayTo);
	   	context.put("SC_NumReportsFrom", SC_NumReportsFrom);
	   	context.put("SC_NumReportsTo", SC_NumReportsTo);
	   	context.put("SC_NumEdgesFrom", SC_NumEdgesFrom);
	   	context.put("SC_NumEdgesTo", SC_NumEdgesTo);
	   	context.put("SC_SortValue", SC_SortValue);
	   	context.put("SC_SortStyle", SC_SortStyle);
		context.put("SC_SortValue_Time", SC_SortValue_Time);
	   	context.put("SC_SortValue_Delay", SC_SortValue_Delay);
	   	context.put("SC_SortValue_ReportNum", SC_SortValue_ReportNum);
		context.put("SC_SortValue_EdgeNum", SC_SortValue_EdgeNum);
	   	context.put("SC_SortValue_Title", SC_SortValue_Title);
	   	context.put("SC_SortValue_TaskID", SC_SortValue_TaskID);
		context.put("SC_SortValue_Default", SC_SortValue_Default);
	   	context.put("SC_SortValue_Descend", SC_SortValue_Descend);
	   	context.put("SC_SortValue_Ascend", SC_SortValue_Ascend);
	   	/*added by zjw for search where SC = Search Condition*/
	   	
	    // Return Velocity results
		try {
			Velocity.mergeTemplate("tasks.vm", "UTF-8", context, response.getWriter());
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (Exception e) {
			LOG.warn("Failed to display tasks.vm", e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						"Failed to display tasks.vm");
		}
	}

	/**
	 * Get the length GET parameter from a HTTP request, or return the default
	 * (of PAGE_LENGTH) when it is not specified or invalid.
	 * @param request
	 * @return
	 */
	private static int getLength(HttpServletRequest request) {
		int length = getIntParam(request, "length", PAGE_LENGTH);
		return Math.max(length, 0); // Don't allow negative
	}

	/**
	 * Get the offset HTTP parameter from a request, or return the default
	 * (of 0) when it is not specified.
	 * @param request
	 * @return
	 */
	private static int getOffset(HttpServletRequest request) {
		int offset = getIntParam(request, "offset", 0);
		return Math.max(offset, 0); // Don't allow negative
	}
	
	/**
	 * Read an integer parameter from a HTTP request, or return a default value
	 * if the parameter is not specified.
	 * @param request
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	private static final int getIntParam(
			HttpServletRequest request, String name, int defaultValue) {
	    int value;
	    try {
	    	return Integer.parseInt(request.getParameter(name));
	    } catch(Exception ex) {
	      return defaultValue;
    }
	}
  
	private static final class SyncTimer extends TimerTask {
		private QueryableReportStore reportstore;

		public SyncTimer(QueryableReportStore reportstore) {
			this.reportstore = reportstore;
		}

		public void run() {
			reportstore.sync();
		}
	}
}
