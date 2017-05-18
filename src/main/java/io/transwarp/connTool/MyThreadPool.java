package io.transwarp.connTool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

public class MyThreadPool {

	private static final Logger LOG = Logger.getLogger(MyThreadPool.class);
	
	private static AtomicInteger successTask;
	private static AtomicInteger failureTask;
	private static AtomicInteger totalTask;
	private static ExecutorService threadPool;
	
	public static void init(int threadNumber) {
		successTask = new AtomicInteger(0);
		failureTask = new AtomicInteger(0);
		totalTask = new AtomicInteger(0);
		threadPool = Executors.newFixedThreadPool(threadNumber);
	}
	
	public static void addNewThread(Runnable thread) {
		threadPool.execute(thread);
		totalTask.incrementAndGet();
	}
	
	public static void closeWhenCompleted(long waitTime) {
		int count = 0;
		int oldCompleteNumber = 0;
		int total = totalTask.intValue();
		while(true) {
			int newSuccessNumber = successTask.intValue();
			int newFailureNumber = failureTask.intValue();
			if(newSuccessNumber + newFailureNumber >= total || count > 60) {
				threadPool.shutdown();
				break;
			}
			if(oldCompleteNumber == newSuccessNumber + newFailureNumber) {
				count += 1;
			}else {
				oldCompleteNumber = newSuccessNumber + newFailureNumber;
				count = 0;
			}
			LOG.info("success task : " + newSuccessNumber + ", failure task : " + newFailureNumber + ", total task : " + total);
			try {
				Thread.sleep(waitTime);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		int completedNum = successTask.intValue() + failureTask.intValue();
		if(completedNum == 0) {
			LOG.info("all task is completed, total task is " + total + ", success task is " + successTask.intValue() + ", failure task is " + failureTask.intValue());
		}else {
			LOG.info("all task is completed, total task is " + total + ", success task is " + successTask.intValue() + ", failure task is " + failureTask.intValue() + ", no completed task is " + (total - completedNum));
		}
	}
	
	public static void threadSuccess() {
		successTask.incrementAndGet();
	}
	
	public static void threadFailure() {
		failureTask.incrementAndGet();
	}
}
