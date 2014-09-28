package edu.berkeley.xtrace.server;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import org.apache.log4j.Logger;
import edu.berkeley.xtrace.XTraceException;
/**
 * @author zjw
 *
 */
public final class MT_MySQLXTraceServer {
	private static final Logger LOG = Logger.getLogger(MT_MySQLXTraceServer.class);

	private static ReportSource[] sources;//接收器
	
	private static BlockingQueue<String> incomingReportQueue, reportsToStorageQueue;//接受队列，待写入队列

	private static ThreadPerTaskExecutor sourcesExecutor;//接收器线程池

	private static MT_MySQLReportStore reportstore;//数据写入器
	
	public static void main(String[] args) {
		System.setProperty("xtrace.server.sources", "edu.berkeley.xtrace.server.UdpReportSource");
		System.setProperty("xtrace.server.store","edu.berkeley.xtrace.server.MT_MySQLReportStore");
				
		LOG.info("===========================starting MT_MySQLXTraceServer================================");
		
		LOG.info("--------------------starting setup ReportSources-------------------");
		setupReportSources();
		LOG.info("-------------------finishing setup ReportSources-------------------");
		
		LOG.info("--------------------starting setup ReportStore-------------------");
		setupReportStore();
		LOG.info("--------------------finishing setup ReportStore---------------------");
		
		LOG.info("--------------------starting setup Backplane----------------------");
		setupBackplane();
		LOG.info("--------------------finishing setup Backplane----------------------");
		
		LOG.info("===========================finishing MT_MySQLXTraceServer================================");
		
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
		
		String storeStr = "edu.berkeley.xtrace.server.MT_MySQLReportStore";
		if (System.getProperty("xtrace.server.store") != null) {
			storeStr = System.getProperty("xtrace.server.store");
		} else {
			LOG.warn("No server report store specified... using default (MySQLReportStore)");
		}
		
		reportstore = null;
		try {
			reportstore = (MT_MySQLReportStore) Class.forName(storeStr).newInstance();
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
		
		try {
			reportstore.initialize();
		} catch (XTraceException e) {
			LOG.fatal("Unable to start report store", e);
			System.exit(-1);
		}
		
		reportstore.run(reportsToStorageQueue);
	}
	public static long timeTransferReport;//4// time of reports transfered from incomingReportQueue to reportsToStorageQueue
	public static MT_QueueMonitor Q2Monitor;//4
	
	private static void setupBackplane() {
		new Thread(new Runnable() {
			public void run() {
				LOG.info("Backplane waiting for packets");
				timeTransferReport=0;//4
				Q2Monitor = new MT_QueueMonitor("Q2");//4
				
				while (true) {
					long st= System.nanoTime();//4
					String msg = null;
					try {
						msg = incomingReportQueue.take();
					} catch (InterruptedException e) {
						LOG.warn("Interrupted", e);
						continue;
					}
					
					Q2Monitor.monitorAll(reportsToStorageQueue);//4			
					reportsToStorageQueue.offer(msg);
					long et= System.nanoTime();//4
					Q2Monitor.monitorMaxLength(reportsToStorageQueue);
					Q2Monitor.monitorCountElement();
					timeTransferReport+=et-st;//4
				}
			}
		}).start();
	}
	
	private static class ThreadPerTaskExecutor implements Executor {
	     public void execute(Runnable r) {
	         new Thread(r).start();
	     }
	 }
}
