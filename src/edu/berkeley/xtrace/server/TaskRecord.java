package edu.berkeley.xtrace.server;

import java.util.Date;
import java.util.List;

import edu.berkeley.xtrace.TaskID;

public class TaskRecord {
	private TaskID taskId;
	private Date firstSeen;
	private Date lastUpdated;
	private int numReports;
	private int numEdges;//added by zjw
	private double delay;//added by zjw整个trace的延迟
	private String title;
	private List<String> tags;
	
	public TaskRecord(TaskID taskId, Date firstSeen, Date lastUpdated,
			int numReports, int numEdges, double delay, String title, List<String> tags) {//modified by zjw
		super();
		this.taskId = taskId;
		this.firstSeen = firstSeen;
		this.lastUpdated = lastUpdated;
		this.numReports = numReports;
		this.numEdges = numEdges;//added by zjw
		this.delay = delay;//added by zjw
		this.title = title;
		this.tags = tags;
	}

	public TaskID getTaskId() {
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
	
	/*added by zjw*/
	public int getNumEdges() {
		return numEdges;
	}
	
	public double getDelay() {
		return delay;
	}
	/*added by zjw*/

	public String getTitle() {
		return title;
	}

	public List<String> getTags() {
		return tags;
	}
}
