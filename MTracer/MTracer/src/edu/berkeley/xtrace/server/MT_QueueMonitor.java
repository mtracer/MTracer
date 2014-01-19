package edu.berkeley.xtrace.server;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.management.monitor.Monitor;

import org.apache.log4j.Logger;

public class MT_QueueMonitor {
	private static final Logger LOG = Logger.getLogger(MT_QueueMonitor.class);
	
	String queueName = "";
	int maxQueueLength;//max length of incomingReportQueue reached;
	int countQueueLengthLT100;//times of incomingReportQueue's length is less than 100
	int countQueueLengthLT500;//times of incomingReportQueue's length is among 100 and 500
	int countQueueLengthLT800;//times of incomingReportQueue's length is among 500 and 800
	int countQueueLengthLT1000;//times of incomingReportQueue's length is among 800 and 1000
	int countQueueLengthMT1000;//times of incomingReportQueue's length is more than 1000
	int countQueueLengthEQ1024;//times of incomingReportQueue's length is 1024
	int countElement;//times element has been written to this queue
	
	public MT_QueueMonitor(String name){
		queueName = name;
		maxQueueLength = 0;
		countQueueLengthLT100 = 0;
		countQueueLengthLT500 = 0;
		countQueueLengthLT800 = 0;
		countQueueLengthLT1000 = 0;
		countQueueLengthMT1000 = 0;
		countQueueLengthEQ1024 = 0;
		countElement = 0;
	}
	
	public void monitorAll(BlockingQueue queue){
		int size=queue.size();
		if(size<100){
			countQueueLengthLT100++;
		}else if(size<500){
			countQueueLengthLT500++;
		}else if(size<800){
			countQueueLengthLT800++;
		}else if(size<1000){
			countQueueLengthLT1000++;
		}else{
			countQueueLengthMT1000++;
		}
		if(size==1024)
			countQueueLengthEQ1024++;
		if(maxQueueLength<size)
			maxQueueLength=size;
	}
	public void monitorMaxLength(BlockingQueue queue){
		int size=queue.size();
		if(maxQueueLength<size)
			maxQueueLength=size;
	}
	public void monitorCountElement(){
		countElement++;
	}
	
	public void print(){
		LOG.info(queueName+".maxQueueLength = "+maxQueueLength);
		LOG.info(queueName+".countQueueLengthLT100 = "+countQueueLengthLT100);
		LOG.info(queueName+".countQueueLengthLT500 = "+countQueueLengthLT500);
		LOG.info(queueName+".countQueueLengthLT800 = "+countQueueLengthLT800);
		LOG.info(queueName+".countQueueLengthLT1000 = "+countQueueLengthLT1000);
		LOG.info(queueName+".countQueueLengthMT1000 = "+countQueueLengthMT1000);
		LOG.info(queueName+".countQueueLengthEQ1024 = "+countQueueLengthEQ1024);
		LOG.info(queueName+".countElement = "+countElement);
	}
}
