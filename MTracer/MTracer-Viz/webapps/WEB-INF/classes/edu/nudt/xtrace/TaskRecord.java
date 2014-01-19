package edu.nudt.xtrace;

import java.util.Date;

public class TaskRecord {
	private String taskId;
	private Date firstSeen;
	private Date lastUpdated;
	private int numReports;
	private int numEdges;
	private double delay;//整个trace的延迟
	private String title;
		
	public TaskRecord()
	{}

	public TaskRecord(String taskId, Date firstSeen, Date lastUpdated,
			int numReports, int numEdges, double delay, String title) {
		super();
		this.taskId = taskId;
		this.firstSeen = firstSeen;
		this.lastUpdated = lastUpdated;
		this.numReports = numReports;
		this.numEdges = numEdges;
		this.delay = delay;
		this.title = title;
	}
	public String getTaskId() {
		return taskId;
	}
	public Date getFirstSeen() {
		return firstSeen;
	}
	public Date getLastUpdated() {
		return lastUpdated;
	}
	public int getNumReports() {
		return numReports;
	}
	public int getNumEdges() {
		return numEdges;
	}
	public double getDelay() {
		return delay;
	}
	public String getTitle() {
		return title;
	}
}


