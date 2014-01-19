/*
 *written by zjw
*/
package edu.berkeley.xtrace;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.lang.Math;

//import edu.berkeley.xtrace.*;
import edu.berkeley.xtrace.reporting.*;
import edu.berkeley.xtrace.IdInfo;

public class MyXTraceContext {
	//public static void main(String[] args){
		/*startTrace("trace");
		for(int i=0;i<100;i++){
			
			//genNewID();
			Report rep = logStart("safdadsf","asdfasdfsadf");
			if(rep == null)continue;
			long st = System.nanoTime();
			logEnd(rep,"success");
			long et = System.nanoTime();
			System.out.println(et-st);
		}*/
		
		/*Report rep = startTrace("trace");
		String msg = rep.toString();
		
		long total1=0,total2=0;
		for(int i=0;i<10;i++){
			BlockingQueue<String> q1;
			q1 = new ArrayBlockingQueue<String>(1024);
			//try{Thread.sleep(1);}catch(Exception e){}
			long st = System.nanoTime();
			//double x=Math.sqrt(1023234324);
			q1.offer(msg);
			long mt = System.nanoTime();
			try{q1.take();}catch(Exception e){}
			long et = System.nanoTime();
			total1+=mt-st;total2+=et-mt;
			System.out.println((mt-st)+"\t"+(et-mt));
		}System.out.println(total1+"\t"+total2);
		System.out.println(total1+total2);
		*/
		
		/*new Thread(new Runnable() {
			public void run() {
				System.setProperty("xtrace.udpdest", "10.0.1.5:7831");
				Report rep = startTrace("task");
				while(true){
					logEnd(rep,"Successful");
					//System.out.println("dd");
				}
			}
		}).start();*/
		
		/*
		System.setProperty("xtrace.udpdest", "10.0.1.3:7831");
		//Report rep = startTrace("task");
		DatagramSocket socket;
		InetAddress addr;
		BlockingQueue<String> q1 = new ArrayBlockingQueue<String>(1024);
		
		try {
			addr = InetAddress.getByName("10.0.1.3");
		} catch (UnknownHostException e) {
			System.out.println("Error1");
			return;
		}
		try {
			socket = new DatagramSocket(7831, addr);
		} catch (SocketException e) {
			System.out.println("Error2");
			return;
		}
		int count=0;
		long t1=0,t2=0,t3=0,t=0;
		System.out.println("start");
		while(count<20000){
			System.out.println(count);
			Report rep = startTrace("task");
			logEnd(rep,"Successful");
			long st = System.nanoTime();
			byte[] buf = new byte[4096];
			DatagramPacket p = new DatagramPacket(buf, buf.length);
			long mt1 = System.nanoTime();
			try {
				socket.receive(p);
				long mt2 = System.nanoTime();
				q1.offer(new String(p.getData(), 0, p.getLength(), "UTF-8"));
				long et = System.nanoTime();
				try{q1.take();}catch(Exception e){}
				t1+=mt1-st;t2+=mt2-mt1;t3+=et-mt2;t+=et-st;
				count++;
				
			} catch (IOException e) {
				System.out.println("Error");
			}
		}
		System.out.println(t1+"\t"+t2+"\t"+t3+"\t"+t);*/
	//}
	
	
	public static final int ID_LENGTH=16;
	
	/****************本机相关信息****************/
	//本机信息存储结构
	private static class LocalInfo{//记录本机信息
		public final String hostName;//本机名字
		public final String hostAddress;//本机IP
		
		public LocalInfo()
		{
			String name="unknow";
			String address="unknown";
			try {
				InetAddress add = InetAddress.getLocalHost();
				name = add.getHostName();
				address = add.getHostAddress();
			} catch (UnknownHostException e) {}
			hostName = name;
			hostAddress = address;			
		}
	}
	
	//本机信息存储变量
	private static ThreadLocal<LocalInfo> localInfo//本机信息
	= new ThreadLocal<LocalInfo>() {
		@Override
		protected LocalInfo initialValue() {
			return new LocalInfo();
		}
	};
	/****************本机相关信息****************/
	
	/****************编号相关信息****************/
	private static ThreadLocal<IdInfo> idInfo//报文编号信息
	= new ThreadLocal<IdInfo>() {
		@Override
		protected IdInfo initialValue() {
			return null;
		}
	};
	
	public static IdInfo getIdInfo()
	{
		return idInfo.get();
	}
	
	public static void setIdInfo(IdInfo info)
	{
		idInfo.set(info);
	}
	
	private static IdInfo storeInfo = new IdInfo();//主要用于同机进程间的共享
	public static void setStoreInfo(IdInfo info)
	{
		if(info != null) storeInfo = info;
	}
	public static IdInfo getStoreInfo()
	{
		return storeInfo;
	}
	/****************编号相关信息****************/
	
	/****************编号生成相关****************/
	private static ThreadLocal<Random> random//参考了XTraceEvent中的编号生成方法
	= new ThreadLocal<Random>() {
		@Override
		protected Random initialValue() {
			//System.out.println("start new random");//4
			//long st= System.nanoTime();//4
			int processId = ManagementFactory.getRuntimeMXBean().getName().hashCode();
			try {
				Random rnd = new Random(++threadId
						+ processId
						+ System.nanoTime()
						+ Thread.currentThread().getId()
						+ InetAddress.getLocalHost().getHostName().hashCode() );
				//long et= System.nanoTime();//4
				//System.out.println("end new Random try: "+(et-st)+" ns");//4
				return rnd;
			} catch (UnknownHostException e) {
				// Failed to get local host name; just use the other pieces
				Random rnd = new Random(++threadId
						+ processId
						+ System.nanoTime()
						+ Thread.currentThread().getId());
				//long et= System.nanoTime();//4
				//System.out.println("end new Random catch: "+(et-st)+" ns");//4
				return rnd;
			}
		}
	};
	private static volatile long threadId = 0;
	
	private static String genNewID()
	{
		//System.out.println("start genNewID");//4
		//long st= System.nanoTime();//4
		String id = genNewID(ID_LENGTH);
		//long et= System.nanoTime();//4
		//System.out.println("end genNewID("+id+"): "+(et-st)+" ns");//4
		return id;
	}
	
	private static String genNewID(int length)//生成新ID
	{
		
		byte[] id=new byte[length/2];
		//System.out.println("start random.get");//4
		//long st= System.nanoTime();//4
		random.get().nextBytes(id);
		//long et= System.nanoTime();//4
		//System.out.println("end random.get: "+(et-st)+" ns");//4
		try {
			return IoUtil.bytesToString(id);
		} catch (IOException e) {
		}
		return null;
	}
	/****************编号生成相关****************/
	
	/****************对外接口****************/
	//timestamp为父节点的开始时间，慎用latestStartTime，一般要直接指明是哪个report的开始时间，如writeIdInfo(out, report.get("StartTime").get(0));
	public static void writeIdInfo(DataOutput out, long timestamp) throws IOException {
		if(timestamp<0){
			out.writeInt(-1);
			return;
		}
		IdInfo info=getIdInfo();
		if(info == null)
			info=new IdInfo();
		
		byte[] taskid=IoUtil.stringToBytes(info.taskID);
		out.writeInt(taskid.length);
		out.write(taskid);
		
		byte[] rid=IoUtil.stringToBytes(info.TID);
		out.writeInt(rid.length);
		out.write(rid);
		
		out.writeLong(timestamp);
		
	}
	public static IdInfo readIdInfo(DataInput in) throws IOException {
		IdInfo info=new IdInfo();
		int len;
		
		len=in.readInt();
		if(len<0){
			return info;
		}
		byte[] buf = new byte[len];
		in.readFully(buf);
		info.taskID=IoUtil.bytesToString(buf);
		
		len=in.readInt();
		in.readFully(buf);
		info.fatherTID=IoUtil.bytesToString(buf);
		
		info.fatherStartTime=in.readLong();
		
		info.TID=genNewID();//这会生成TID
		
		info.isTransferred=false;
		setIdInfo(info);
		setStoreInfo(info);
		return info;
	}
	
	public static Report logStart(String agent,String opname)
	{
		//System.out.println("start logStart");//4
		//long st= System.nanoTime();//4
		Report report = new Report();
		if(report == null)//若创建失败，则返回空，logEnd中对空报文不进行处理
			return null;
		IdInfo info=getIdInfo();
		if(info == null)
			return null;
		if(info.taskID.equals("FFFFFFFFFFFFFFFF") && info.fatherTID.equals("FFFFFFFFFFFFFFFF"))//这中情况只会出现在收到其他主机发送过来的空idInfo
			return null;

		report.put("TaskID", info.taskID);
		report.put("TID",info.TID);
		report.put("OpName",opname);
		report.put("HostAddress",localInfo.get().hostAddress);
		report.put("HostName", localInfo.get().hostName);
		report.put("Agent", agent);
		
		if(info.isTransferred == false)//ID转换
		{
			report.put("FatherTID", info.fatherTID);
			report.put("FatherStartTime", String.valueOf(info.fatherStartTime));
			info.isTransferred = true;
		}
		
		//将开始时间放在最后记录，尽量靠近要记录的操作，减小误差
		//long time = System.currentTimeMillis();
		long time = System.nanoTime();//计时更精确
		report.put("StartTime", String.valueOf(time));
		info.latestStartTime = time;
		setIdInfo(info);
		//long et= System.nanoTime();//4
		//System.out.println("end logStart: "+(et-st)+" ns");//4
		return report;
	}
	
	public static void logEnd(Report report,String description)
	{
		//System.out.println("start logEnd");//4
		//long st= System.nanoTime();//4
		if(report==null)
			return;
				
		//将结束时间放在最先记录，尽量靠近要记录的操作，减小误差
		//report.put("EndTime", String.valueOf(System.currentTimeMillis()));
		report.put("EndTime", String.valueOf(System.nanoTime()));//计时更精确
		report.put("Description",description);
		
		Reporter.getReporter().sendReport(report);
		//long et= System.nanoTime();//4
		//System.out.println("end logEnd: "+(et-st)+" ns");//4
	}
	
	public static void startTraceWithoutEnd()
	{
		startTraceWithoutEnd("task");
	}
	public static void startTraceWithoutEnd(String taskName)
	{
		Report report = startTrace(taskName);
		//task结束时间不好获取，因为其结束时间应该在所有操作完成之后，所以将其设为无穷大
		//这样对时间先后的逻辑关系没影响，其正确的结束值在后续处理中获取
		report.put("EndTime", String.valueOf(Long.MAX_VALUE));
		Reporter.getReporter().sendReport(report);
	}
	
	public static Report startTrace(String taskName)
	{
		//System.out.println("start startTrace");//4
		//long st= System.nanoTime();//4
		/*建立根节点*/
		IdInfo rootInfo = new IdInfo();//根节点的信息
		rootInfo.taskID = genNewID();
		rootInfo.fatherTID = "0000000000000000";//根节点的父节点为全0
		rootInfo.fatherStartTime = 0;
		rootInfo.TID = genNewID();
		rootInfo.isTransferred = false;
		setIdInfo(rootInfo);
		Report report = logStart("User",taskName);
		report.put("Title", taskName);
		report.put("Description", "A user task");
		
		/*修改当前idInfo*/
		IdInfo info = new IdInfo();
		info.taskID = rootInfo.taskID;
		info.fatherTID = rootInfo.TID;
		info.fatherStartTime = Long.parseLong(report.get("StartTime").get(0));
		info.TID = genNewID();
		info.isTransferred = false;
		setIdInfo(info);
		setStoreInfo(info);
		//long et= System.nanoTime();//4
		//System.out.println("end startTrace: "+(et-st)+" ns");//4
		return report;
	}
	
	public static void endTrace(Report report)
	{
		//System.out.println("start endTrace");//4
		//long st= System.nanoTime();//4
		if(report==null)
			return;
				
		//将结束时间放在最先记录，尽量靠近要记录的操作，减小误差
		//report.put("EndTime", String.valueOf(System.currentTimeMillis()));
		report.put("EndTime", String.valueOf(System.nanoTime()));//计时更精确
		
		Reporter.getReporter().sendReport(report);
		
		setIdInfo(null);
		//long et= System.nanoTime();//4
		//System.out.println("end endTrace: "+(et-st)+" ns\n");//4
	}
	/****************对外接口****************/
}
