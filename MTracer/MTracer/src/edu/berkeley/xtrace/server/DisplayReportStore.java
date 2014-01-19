package edu.berkeley.xtrace.server;


import java.util.List;
import java.util.concurrent.BlockingQueue;
import org.apache.log4j.Logger;
import edu.berkeley.xtrace.XTraceException;
import edu.berkeley.xtrace.reporting.Report;

public class DisplayReportStore implements ReportStore{
	private static final Logger LOG = Logger.getLogger(DisplayReportStore.class);

	private BlockingQueue<String> incomingReports;
	
	String rootID="";

	public synchronized void setReportQueue(BlockingQueue<String> q) {
		this.incomingReports = q;
	}

	public synchronized void initialize() throws XTraceException {
	}

	public void sync() {		
	}

	public synchronized void shutdown() {
		LOG.info("Shutting down the DisplayReportStore");
		sync();
	}

	void receiveReport(String msg) {
			Report r = Report.createFromString(msg);
			System.out.println(r.toString());
	}
	
	void receiveReport2(String msg){
		Report r=Report.createFromString(msg);	
		System.out.print(r.get("TaskID").get(0).toString()+"\t");
		System.out.print(r.get("TID").get(0).toString()+"\t");
		System.out.print(r.get("HostAddress").get(0).toString()+"\t");
		System.out.print(r.get("HostName").get(0).toString()+"\t");
		System.out.print(r.get("Agent").get(0).toString()+"\t");
		System.out.print(r.get("OpName").get(0).toString()+"\t");
		System.out.print(r.get("StartTime").get(0).toString()+"\t");
		System.out.print(r.get("EndTime").get(0).toString()+"\t");
		System.out.print(r.get("Description").get(0).toString()+"\n");
		
		List<String> fid=r.get("FatherTID");
		List<String> ft=r.get("FatherStartTime");
		if(fid!=null && ft!=null)
		{
			System.out.print(fid.get(0).toString()+"\t");
			System.out.print(ft.get(0).toString()+"\t");
			System.out.print(r.get("TID").get(0).toString()+"\n");
		}
		System.out.print("\n");
	}

	public void run() {
		LOG.info("DisplayReportStore running");

		while (true) {
			String msg;
			try {
				msg = incomingReports.take();
			} catch (InterruptedException e1) {
				continue;
			}
			//receiveReport(msg);
			receiveReport2(msg);
		}
	}
}
