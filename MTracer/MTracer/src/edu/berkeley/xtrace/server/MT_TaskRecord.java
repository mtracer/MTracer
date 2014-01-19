/** written by zjw
 * info stored in QTask */

package edu.berkeley.xtrace.server;

import java.sql.Timestamp;

public class MT_TaskRecord extends MT_TableRecord{
/*	public static void main(String[] args){
		MT_TaskRecord r = new MT_TaskRecord("1","1",1,1,1);
		System.out.println(r.FirstSeen);
	}
	*/
	private String TaskID;
	private String Title;
	private int nEdge,nReport;
	private long StartTime,EndTime;
	private Timestamp FirstSeen,LastUpdated;
	
	public String getTaskID(){return TaskID;}
	public String getTitle(){return Title;}
	public int getNEdge(){return nEdge;}
	public int getNReport(){return nReport;}
	public long getStartTime(){return StartTime;}
	public long getEndTime(){return EndTime;}
	public Timestamp getFirstSeen(){return FirstSeen;}
	public Timestamp getLastUpdated(){return LastUpdated;}
	
	/**constuctor for root node**/
	public MT_TaskRecord(String TaskID, String Title, int nEdge,long StartTime, long EndTime){
		this.TaskID = TaskID;
		this.Title = Title;
		this.nEdge = nEdge;
		this.nReport = 1;
		this.StartTime = StartTime;
		this.EndTime = EndTime;
		this.FirstSeen = new Timestamp(System.currentTimeMillis());//the temp first seen
		this.LastUpdated = new Timestamp(System.currentTimeMillis());//the temp last updated
	}
	
	/**constuctor for non-root node with edge**/
	public MT_TaskRecord(String TaskID, int nEdge){
		this(TaskID, TaskID, nEdge, 0, Long.MAX_VALUE);
	}
	
	/**constuctor for non-root node without edge**/
	public MT_TaskRecord(String TaskID){
		this(TaskID, 0);
	}
	
	public MT_TaskRecord combineRecords(MT_TaskRecord record){
		if(!this.TaskID.equals(record.getTaskID()))//tasks with diff task id shouldn't be combined
			return null;
		
		if(this.Title.equals(this.TaskID) && !record.getTitle().equals(this.TaskID))
			this.Title = record.getTitle();
		
		this.nEdge += record.getNEdge();
		this.nReport += record.getNReport();
		
		if(this.StartTime == 0 && record.getStartTime() != 0)
			this.StartTime = record.getStartTime();
		
		if(this.EndTime == Long.MAX_VALUE && record.getEndTime() != Long.MAX_VALUE)
			this.EndTime = record.getEndTime();
		
		if(this.FirstSeen.compareTo(record.getFirstSeen()) > 0)
			this.FirstSeen = record.getFirstSeen();
		
		if(this.LastUpdated.compareTo(record.getLastUpdated()) < 0)
			this.LastUpdated = record.getLastUpdated();
		
		return this;
	}
	
	public String toString(){
		String ret = "TaskID="+TaskID;
		ret += ", Title="+Title;
		ret += ", nEdge="+nEdge;
		ret += ", nReport="+nReport;
		ret += ", StartTime="+StartTime;
		ret += ", EndTime="+EndTime;
		ret += ", FirstSeen="+FirstSeen;
		ret += ", LastUpdated"+LastUpdated;
		return ret;
	}
}
