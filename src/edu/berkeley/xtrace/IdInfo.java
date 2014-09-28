package edu.berkeley.xtrace;

public class IdInfo{//报文编号信息
	public String taskID;//task id
	public String TID;//本机当前TID
	public String fatherTID;//父节点TID
	public long fatherStartTime;//转换时间
	public long latestStartTime;//最近一个report的开始时间
	public boolean isTransferred;//是否转换，一个《fatherRID，fatherStartTime》只要转换一次
	
	public IdInfo()
	{
		taskID = "FFFFFFFFFFFFFFFF";
		TID = "FFFFFFFFFFFFFFFF";
		fatherTID = "FFFFFFFFFFFFFFFF";
		fatherStartTime = -1;
		latestStartTime = -1;//该值不推荐使用
		isTransferred = false;
	}
}