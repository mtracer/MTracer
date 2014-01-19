package edu.nudt.xtrace;

import java.util.List;
import java.util.ArrayList;

public class DiagnosisResult {
	private String taskID;//trace
	private ArrayList<Integer> abnormalOperations=new ArrayList<Integer>();//abnormal operation ids, where id is the deep first search id in the call tree of this trace
	private ArrayList<Double> delays=new ArrayList<Double>();//the delay corresponding to the abnormal operations
	private ArrayList<Double> minDelays=new ArrayList<Double>();//the min normal delay corresponding to the abnormal operations
	private ArrayList<Double> maxDelays=new ArrayList<Double>();//the max normal delay corresponding to the abnormal operations

	public DiagnosisResult(){};
	public DiagnosisResult(String taskID){this.taskID=taskID;};
	public String getTaskID(){return taskID;};
	public ArrayList<Integer> getAbnormalOperations(){return abnormalOperations;};
	public ArrayList<Double> getDelays(){return delays;};
	public ArrayList<Double> getMinDelays(){return minDelays;};
	public ArrayList<Double> getMaxDelays(){return maxDelays;};
	
	public void setTaskID(String taskID){this.taskID = taskID;};
	public void setAbnormalOperations(ArrayList<Integer> abnormalOperations){this.abnormalOperations=abnormalOperations;};
	public void setDelays(ArrayList<Double> delays){this.delays=delays;};
	public void setMinDelays(ArrayList<Double> minDelays){this.minDelays=minDelays;};
	public void setMaxDelays(ArrayList<Double> maxDelays){this.maxDelays=maxDelays;};

	public void addAbnormal(int op, double delay, double minDelay, double maxDelay){
		this.abnormalOperations.add(op);
		this.delays.add(delay);
		this.minDelays.add(minDelay);
		this.maxDelays.add(maxDelay);
	}
	private int getOpIdx(int op){
		for(int i=0;i<abnormalOperations.size();i++)
			if(abnormalOperations.get(i)==op)
				return i;
		return -1;
	}
	public double getDelay(int op){
		int idx=getOpIdx(op);
		if(idx==-1) return -1;
		return delays.get(idx);
	}
	public double getMaxDelay(int op){
		int idx=getOpIdx(op);
		if(idx==-1) return -1;
		return maxDelays.get(idx);
	}
	public double getMinDelay(int op){
		int idx=getOpIdx(op);
		if(idx==-1) return -1;
		return minDelays.get(idx);
	}
	public int getAbnormalOperationNum(){
		return abnormalOperations.size();
	}
	public String toString(){
		String str="";
		str += "["+this.taskID+"]: ";
		for(int i=0;i<getAbnormalOperationNum();i++){
			str += "(";
			str += "OP["+abnormalOperations.get(i)+"] ";
			str += "delay="+String.format("%.2f", delays.get(i))+"ms, ";
			str += "normalDelay=("+String.format("%.2f", minDelays.get(i))+"ms,"+String.format("%.2f", maxDelays.get(i))+"ms) ";
			str += "); ";
		}
		return str;
	}
}











