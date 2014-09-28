package edu.berkeley.xtrace.server;

public class MT_ReportRecord extends MT_TableRecord{
	private String TaskID;
	private String TID;
	private String OpName;
	private long StartTime;
	private long EndTime;
	private String HostAddress;
	private String HostName;
	private String Agent;
	private String Description;
	
	public String getTaskID(){return TaskID;}
	public String getTID(){return TID;}
	public String getOpName(){return OpName;}
	public long getStartTime(){return StartTime;}
	public long getEndTime(){return EndTime;}
	public String getHostAddress(){return HostAddress;}
	public String getHostName(){return HostName;}
	public String getAgent(){return Agent;}
	public String getDescription(){return Description;}
	
	public MT_ReportRecord(String TaskID, String TID, String OpName, long StartTime, long EndTime, String HostAddress, String HostName, String Agent, String Description){
		this.TaskID = TaskID;
		this.TID = TID;
		this.OpName = OpName;
		this.StartTime = StartTime;
		this.EndTime = EndTime;
		this.HostAddress = HostAddress;
		this.HostName = HostName;
		this.Agent = Agent;
		this.Description = Description;
	}
	
	public String toString(){
		String ret = "TaskID="+TaskID;
		ret += ", TID="+TID;
		ret += ", OpName="+OpName;
		ret += ", StartTime="+StartTime;
		ret += ", EndTime="+EndTime;
		ret += ", HostAddress="+HostAddress;
		ret += ", HostName="+HostName;
		ret += ", Agent"+Agent;
		ret += ", Description"+Description;
		return ret;
	}
}
