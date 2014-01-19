package edu.berkeley.xtrace.server;

public class MT_EdgeRecord extends MT_TableRecord{
	private String TaskID;
	private String FatherTID;
	private long FatherStartTime;
	private String ChildTID;
	
	public String getTaskID(){return TaskID;}
	public String getFatherTID(){return FatherTID;}
	public long getFatherStartTime(){return FatherStartTime;}
	public String getChildTID(){return ChildTID;}
	
	public MT_EdgeRecord(String TaskID, String FatherTID, long FatherStartTime,  String ChildTID){
		this.TaskID = TaskID;
		this.FatherTID = FatherTID;
		this.FatherStartTime = FatherStartTime;
		this.ChildTID = ChildTID;
	}
	
	public String toString(){
		String ret = "TaskID="+TaskID;
		ret += ", FatherTID="+FatherTID;
		ret += ", FatherStartTime="+FatherStartTime;
		ret += ", ChildTID="+ChildTID;
		return ret;
	}
}
