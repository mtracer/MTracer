/*
 * MyXTraceServer.java
 * Using my default configuration:
 * UdpReportSource
 * Display in screen or store in mysql later
 * Writer by Zhou Jingwen
 */

package edu.berkeley.xtrace.server;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.log4j.Logger;

import edu.berkeley.xtrace.MyXTraceContext;
import edu.berkeley.xtrace.XTraceException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class MyXTraceServer {
	private static final Logger LOG = Logger.getLogger(MyXTraceServer.class);

	private static ReportSource[] sources;
	
	private static BlockingQueue<String> incomingReportQueue, reportsToStorageQueue;

	private static ThreadPerTaskExecutor sourcesExecutor;

	private static ExecutorService storeExecutor;

	private static ReportStore reportstore;
	
	private static String sourcesStr="edu.berkeley.xtrace.server.UdpReportSource";//设置接收器为UDP接收,可有多个接收器，用“,”分开
	private static String storeStr="edu.berkeley.xtrace.server.DisplayReportStore";//设置存储器为存储到屏幕，即显示
	private static long syncInterval=5;//存储器写间隔,单位为秒
	
	public static void main(String[] args) {
		System.out.println("beginning...");
		
		/*setupReportSources();
		setupReportStore();
		setupBackplane();*/
		System.out.println("done!");
	}
	public void mysqlConnect(){  
        String dbDriver = "com.mysql.jdbc.Driver";  
        String url = "jdbc:mysql://localhost/top800";  
        String username = "root";  
        String password = "root";  
        		
        Statement mStatement = null;  
        ResultSet mResultSet = null;  
        Connection mConnection = null;  
          
        String sql = "select top, id, name, country, dtime from gametop800 where top<=20";  
          
        try{  
            Class.forName(dbDriver).newInstance();  
            mConnection = DriverManager.getConnection(url, username, password);  
            mStatement = mConnection.createStatement();  
            mResultSet = mStatement.executeQuery(sql);  
              
            try{  
                while(mResultSet.next()){  
                    System.out.print(mResultSet.getInt(1) + "\t");  
                    System.out.print(mResultSet.getString(2) + "\t");  
                    System.out.print(mResultSet.getString(3) + "\t");  
                    System.out.println(mResultSet.getString(4));  
                }  
            }catch (Exception e){  
                System.out.println("数据库读取错误！ \n" + e.getMessage());  
            }  
        }catch (SQLException e){  
            System.out.println("连接数据库错误: \n" + url + "\n" + e.getMessage());  
        }catch (Exception e){  
            e.printStackTrace();  
        }finally{  
//          mStatement.close();  
//          mConnection.close();  
        }  
    }  
	private static void setupReportSources() {
		incomingReportQueue = new ArrayBlockingQueue<String>(1024, true);
		sourcesExecutor = new ThreadPerTaskExecutor();
		
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
		}
		System.out.println("setupReportSources done!\n");
	}
	
	private static void setupReportStore() {
		System.out.println("\nbeginning setupReportStore...");
		
		reportsToStorageQueue = new ArrayBlockingQueue<String>(1024);
		reportstore = null;
		try {
			reportstore = (ReportStore) Class.forName(storeStr).newInstance();
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
		Timer timer= new Timer();
		timer.schedule(new SyncTimer(reportstore), syncInterval*1000, syncInterval*1000);
		
		/* Add a shutdown hook to flush and close the report store */
		Runtime.getRuntime().addShutdownHook(new Thread() {
		  public void run() {
			  reportstore.shutdown();
		  }
		});
		System.out.println("setupReportStore done!\n");
	}
	
	private static void setupBackplane() {//将收到的报文放到数据库写队列中
		System.out.println("\nbeginning setupBackplane...");
		new Thread(new Runnable() {
			public void run() {
				LOG.info("Backplane waiting for packets");
				
				while (true) {
					String msg = null;
					try {
						msg = incomingReportQueue.take();
					} catch (InterruptedException e) {
						LOG.warn("Interrupted", e);
						continue;
					}
					reportsToStorageQueue.offer(msg);
				}
			}
		}).start();
		System.out.println("setupBackplane done!\n");
	}
	
	private static class ThreadPerTaskExecutor implements Executor {
	     public void execute(Runnable r) {
	         new Thread(r).start();
	     }
	 }
  
	private static final class SyncTimer extends TimerTask {
		private ReportStore reportstore;

		public SyncTimer(ReportStore reportstore) {
			this.reportstore = reportstore;
		}

		public void run() {
			reportstore.sync();
		}
	}
}
