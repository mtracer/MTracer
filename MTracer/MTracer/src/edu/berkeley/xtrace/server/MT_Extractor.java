package edu.berkeley.xtrace.server;

import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import edu.berkeley.xtrace.XTraceException;
import edu.berkeley.xtrace.reporting.Report;

public class MT_Extractor implements Runnable{
	private static final Logger LOG = Logger.getLogger( MT_Extractor.class);
	
	private BlockingQueue<String> QIncome;
	private BlockingQueue<MT_TaskRecord> QTask;
	private BlockingQueue<MT_ReportRecord> QReport;
	private BlockingQueue<MT_EdgeRecord> QEdge;
	private BlockingQueue<MT_OperationRecord> QOperation;
	
	public void setIncomeQueue(BlockingQueue<String> QIncome){this.QIncome = QIncome;}
	public void setTaskQueue(BlockingQueue<MT_TaskRecord> QTask){this.QTask = QTask;}
	public void setReportQueue(BlockingQueue<MT_ReportRecord> QReport){this.QReport = QReport;}
	public void setEdgeQueue(BlockingQueue<MT_EdgeRecord> QEdge){this.QEdge = QEdge;}
	public void setOperationQueue(BlockingQueue<MT_OperationRecord> QOperation){this.QOperation = QOperation;}
	
	
	public void initialize() throws XTraceException{}
	MT_QueueMonitor QTaskMonitor = new MT_QueueMonitor("QTask");//4
	MT_QueueMonitor QReportMonitor= new MT_QueueMonitor("QReport");//4
	MT_QueueMonitor QEdgeMonitor= new MT_QueueMonitor("QEdge");//4
	MT_QueueMonitor QOperationMonitor= new MT_QueueMonitor("QOperation");//4
	
	int numTakeFromQ2 = 0;//4
	long timeExtract = 0;//4
	public void run() 
	{
		LOG.info("MT_Extractor start running ");
		
		while (true) {
			String msg;
			try {msg = QIncome.take();} catch (InterruptedException e) {msg = null;}
			if(msg != null){
				numTakeFromQ2++;//4
				long st= System.nanoTime();//4
				extract(msg);
				long et= System.nanoTime();//4
				timeExtract += et - st;
			}
		}
	}
	
	public void shutdown(){
		LOG.info("MT_Extractor stop running ");
		
		QTaskMonitor.print();//4
		QReportMonitor.print();//4
		QEdgeMonitor.print();//4
		QOperationMonitor.print();//4
		MT_MySQLXTraceServer.Q2Monitor.print();//4
		LOG.info("MT_Extractor.timeExtract = " + timeExtract);//4
		LOG.info("MT_Extractor.numTakeFromQ2 = " + numTakeFromQ2);//4
		LOG.info("MT_MySQLXTraceServer.timeTransferReport = "+MT_MySQLXTraceServer.timeTransferReport);//4
	}
	
	private void extract(String msg){
		try{
			Report r = Report.createFromString(msg);
			String TaskID = r.get("TaskID") != null ? r.get("TaskID").get(0).toString() : "null";
			String TID = r.get("TID") != null ? r.get("TID").get(0).toString() : "null";
			String OpName = r.get("OpName") != null ? r.get("OpName").get(0).toString() : "null";
			String Title = r.get("Title") != null ? r.get("Title").get(0).toString() : "null";
			long StartTime = r.get("StartTime") != null ? Long.parseLong(r.get("StartTime").get(0).toString()) : 0L;
			long EndTime = r.get("EndTime") != null ? Long.parseLong(r.get("EndTime").get(0).toString()) : 0L;
			String HostAddress = r.get("HostAddress") != null ? r.get("HostAddress").get(0).toString() : "null";
			String HostName =r.get("HostName") != null ? r.get("HostName").get(0).toString() : "null";
			String Agent = r.get("Agent") != null ? r.get("Agent").get(0).toString() : "null";
			String Description = r.get("Description") != null ? r.get("Description").get(0).toString() : "null";
			if(Description.length()>255)
				Description=Description.substring(0, 255);
			String FatherTID = r.get("FatherTID") != null ? r.get("FatherTID").get(0).toString() : "null";
			long FatherStartTime = r.get("FatherStartTime") != null ? Long.parseLong(r.get("FatherStartTime").get(0).toString()) : -1L;
			
			MT_TaskRecord task = null;
			MT_ReportRecord report = null;
			MT_EdgeRecord edge = null;
			MT_OperationRecord operation = null;
			
			if(Description.equals("A user task")){
				if((!FatherTID.equals("null")) && (FatherStartTime !=-1L))
					task = new MT_TaskRecord(TaskID, Title, 1, StartTime, EndTime);
				else
					task = new MT_TaskRecord(TaskID, Title, 0, StartTime, EndTime);
			}else{
				if((!FatherTID.equals("null")) && (FatherStartTime !=-1L))
					task = new MT_TaskRecord(TaskID, 1);
				else
					task = new MT_TaskRecord(TaskID, 0);
			}
			
			report = new MT_ReportRecord(TaskID, TID, OpName, StartTime, EndTime, HostAddress, HostName, Agent, Description);
			
			if((!FatherTID.equals("null")) && (FatherStartTime !=-1L))
				edge = new MT_EdgeRecord(TaskID, FatherTID, FatherStartTime,  TID);
			
			if(!Description.equals("A user task"))
				operation = new MT_OperationRecord(OpName, StartTime,EndTime);
			
			
			if(task != null){
				QTaskMonitor.monitorAll(QTask);//4
				QTask.offer(task);
				QTaskMonitor.monitorMaxLength(QTask);//4
				QTaskMonitor.monitorCountElement();//4
			}
			if(report != null){
				QReportMonitor.monitorAll(QReport);//4
				QReport.offer(report);
				QReportMonitor.monitorMaxLength(QReport);//4
				QReportMonitor.monitorCountElement();//4
			}
			if(edge != null){
				QEdgeMonitor.monitorAll(QEdge);//4
				QEdge.offer(edge);
				QEdgeMonitor.monitorMaxLength(QEdge);//4
				QEdgeMonitor.monitorCountElement();//4
			}
			if(operation != null){
				QOperationMonitor.monitorAll(QOperation);//4
				QOperation.offer(operation);
				QOperationMonitor.monitorMaxLength(QOperation);//4
				QOperationMonitor.monitorCountElement();//4
			}
		}catch(Exception e){
			//LOG.warn("Exception in extract report", e);
			LOG.warn("Exception in extract report");
		}
	}

}
