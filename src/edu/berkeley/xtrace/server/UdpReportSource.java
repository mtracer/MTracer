package edu.berkeley.xtrace.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import edu.berkeley.xtrace.XTraceException;

public class UdpReportSource implements ReportSource {
	private static final Logger LOG = Logger.getLogger(UdpReportSource.class);

	private BlockingQueue<String> q;
	private DatagramSocket socket;
	
	int countReceive;//4//number of reports receive from network
	long timeReceive;//4//total time of receiving reports from network
	long timeStore;//4//total time of storing reports to incomingReportQueue
	MT_QueueMonitor Q1Monitor;//4//monitor queue info
	
	
	public void initialize() throws XTraceException {
		
		String udpSource = System.getProperty("xtrace.udpsource", "127.0.0.1:7831");
		countReceive=0;//4
		timeReceive=0;//4
		timeStore=0;//4
		Q1Monitor = new MT_QueueMonitor("Q1");
		
		
		InetAddress localAddr;
		try {
			localAddr = InetAddress.getByName(udpSource.split(":")[0]);
		} catch (UnknownHostException e) {
			throw new XTraceException("Unknown host: " + udpSource.split(":")[0], e);
		}
		int localPort = Integer.parseInt(udpSource.split(":")[1]);
		try {
			socket = new DatagramSocket(localPort, localAddr);
		} catch (SocketException e) {
			throw new XTraceException("Unable to open socket", e);
		}

		LOG.info("UDPReportSource initialized on " + localAddr + ":" + localPort);
	}
	public void setReportQueue(BlockingQueue<String> q) {
		this.q = q;
	}
	
	public void shutdown() {
		if (socket != null)
			socket.close();
		LOG.info("ReportSource.countReceive = "+countReceive);//4
		LOG.info("ReportSource.timeReceive = "+timeReceive);//4
		LOG.info("ReportSource.timeStore = "+timeStore);//4
		Q1Monitor.print();
	}

	public void run() {
		LOG.info("UDPReportSource listening for packets");
	
		while (true) {
			byte[] buf = new byte[4096];
			DatagramPacket p = new DatagramPacket(buf, buf.length);

		    try {
		    	long st= System.nanoTime();//4
				socket.receive(p);
				long et= System.nanoTime();//4
				countReceive++;//4
				timeReceive+=et-st;//4
				//LOG.info(System.currentTimeMillis());
			} catch (IOException e) {
				//LOG.warn("Unable to receive report", e);
				LOG.warn("Unable to receive report");
			}
			
			//LOG.debug("Received Report");
			
		    try {
		    	Q1Monitor.monitorAll(q);//4
		    	long st= System.nanoTime();//4
				q.offer(new String(p.getData(), 0, p.getLength(), "UTF-8"));
				long et= System.nanoTime();//4
				Q1Monitor.monitorMaxLength(q);
				Q1Monitor.monitorCountElement();//4
				timeStore+=et-st;//4
			} catch (UnsupportedEncodingException e) {
				LOG.warn("UTF-8 not available", e);
			}
		}
	}
	
	
}
