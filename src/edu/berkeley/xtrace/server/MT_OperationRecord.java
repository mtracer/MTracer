/** written by zjw
 * info stored in QTask */

package edu.berkeley.xtrace.server;


public class MT_OperationRecord extends MT_TableRecord{
	private String OpName;
	private long Num;
	private long MaxDelay,MinDelay;
	private double AverageDelay;
	
	public String getOpName(){return OpName;}
	public long getNum(){return Num;}
	public long getMaxDelay(){return MaxDelay;}
	public long getMinDelay(){return MinDelay;}
	public double getAverageDelay(){return AverageDelay;}
	
	public MT_OperationRecord(String OpName, long delay){
		this.OpName = OpName;
		this.Num = 1;
		this.MaxDelay = delay;
		this.MinDelay = delay;
		this.AverageDelay = (double)delay;
	}
	
	public MT_OperationRecord(String OpName, long StartTime, long EndTime){
		this(OpName, EndTime-StartTime);
	}
	
	public MT_OperationRecord combineRecords(MT_OperationRecord record){
		if(!this.OpName.equals(record.getOpName()))//OP with diff title shouldn't be combined
			return null;
		
		this.Num += record.getNum();
				
		if(this.MaxDelay < record.getMaxDelay())
			this.MaxDelay = record.getMaxDelay();
		
		if(this.MinDelay > record.getMinDelay())
			this.MinDelay = record.getMinDelay();
		
		this.AverageDelay = ((this.AverageDelay*this.Num)+(record.AverageDelay*record.Num))/(this.Num+record.Num);
		
		return this;
	}
	
	public String toString(){
		String ret = "OpName="+OpName;
		ret += ", Num="+Num;
		ret += ", MaxDelay="+MaxDelay;
		ret += ", MinDelay="+MinDelay;
		ret += ", AverageDelay="+AverageDelay;
		return ret;
	}
}
