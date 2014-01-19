package edu.nudt.xtrace;

import java.util.Date;

public class OperationRecord {
	private String opName;
	private int num;
	private double maxDelay;
	private double minDelay;
	private double averageDelay;
		
	public OperationRecord()
	{}

	public OperationRecord(String opName, int num, double maxDelay,
			double minDelay, double averageDelay) {
		super();
		this.opName = opName;
		this.num = num;
		this.maxDelay = maxDelay;
		this.minDelay = minDelay;
		this.averageDelay = averageDelay;
	}
	public String getOpName() {
		return opName;
	}
	public int getNum() {
		return num;
	}
	public double getMaxDelay() {
		return maxDelay;
	}
	public double getMinDelay() {
		return minDelay;
	}
	public double getAverageDelay() {
		return averageDelay;
	}
}


