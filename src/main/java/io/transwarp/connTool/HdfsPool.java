package io.transwarp.connTool;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.hadoop.fs.FileSystem;

import io.transwarp.bean.LoginInfoBean;
import io.transwarp.information.PropertiesInfo;

public class HdfsPool {
	
	public static boolean buildPool = false;
	private static int totalHdfsConn;
	private static Queue<FileSystem> fsPool;
	private static final Lock queueLock = new ReentrantLock();
	
	public static void openPool(String activeNamenodeIP, LoginInfoBean loginInfo, String[] hdfsPaths, String managerHost) throws Exception {
		fsPool = new LinkedList<FileSystem>();
		totalHdfsConn = Integer.valueOf(PropertiesInfo.prop_env.getProperty("threadNumber", "10"));
		for(int i = 0; i < totalHdfsConn; i++) {
			fsPool.add(HdfsUtil.getHdfsFileSystem(hdfsPaths, activeNamenodeIP, loginInfo, managerHost));
		}
		buildPool = true;
		
	}
	
	public static FileSystem getHdfsConn() throws Exception {
		boolean getConn = false;
		FileSystem fs = null;
		while(!getConn) {
			queueLock.lock();
			if(totalHdfsConn == 0) {
				queueLock.unlock();
				Thread.sleep(1000);
			}else {
				totalHdfsConn -= 1;
				fs = fsPool.poll();
				queueLock.unlock();
				break;
			}
		}
		return fs;

	}
	
	public static void putHdfsConn(FileSystem fs) throws Exception {
		queueLock.lock();
		totalHdfsConn += 1;
		fsPool.offer(fs);
		queueLock.unlock();
	}
	
	public static void closePool() throws Exception {
		while(!fsPool.isEmpty()) {
			FileSystem fs = fsPool.poll();
			fs.close();
		}
	}
}
