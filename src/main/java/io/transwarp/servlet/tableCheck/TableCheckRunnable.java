package io.transwarp.servlet.tableCheck;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

import io.transwarp.bean.LoginInfoBean;
import io.transwarp.bean.TableBean;
import io.transwarp.connTool.HdfsUtil;
import io.transwarp.connTool.MyThreadPool;
import io.transwarp.information.ClusterInformation;

public class TableCheckRunnable extends ClusterInformation implements Runnable{

	private static final Logger LOG = Logger.getLogger(TableCheckRunnable.class);
	
	private TableBean table;
	private String namenodeIP;
	private String[] configPaths;
	private LoginInfoBean loginInfo;
	private FileSystem fs;
	
	public TableCheckRunnable(String namenodeIP, String[] configPaths, TableBean table, LoginInfoBean loginInfo) {
		this.table = table;
		this.namenodeIP = namenodeIP;
		this.configPaths = configPaths;
		this.loginInfo = loginInfo;
	}
	
	@Override
	public void run() {
//		LOG.info("begin check table : " + table.getTable_name());
		try {
			checkTableSpace();
			MyThreadPool.threadSuccess();
		}catch(FileNotFoundException fileNoFound) {
			LOG.info(fileNoFound.getMessage());
			MyThreadPool.threadSuccess();
			LOG.error("table check error, table name is : " + table.getTable_name());
		}catch(Exception e) {
			e.printStackTrace();
			MyThreadPool.threadFailure();
			LOG.error("table check error, table name is : " + table.getTable_name());
		}finally {
			if(fs != null) {
				try {
					fs.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	protected void checkTableSpace() throws Exception {
		fs = HdfsUtil.getHdfsFileSystem(configPaths, namenodeIP, loginInfo);
		Queue<String> queueOfPath = new LinkedList<String>();
		String tableLocation = table.getTable_location();
		queueOfPath.offer(tableLocation);
		while(!queueOfPath.isEmpty()) {
			String checkPath = queueOfPath.poll();
			boolean isLastDir = true;
			if(checkPath == null || checkPath.equals("")) {
				continue;
			}
			Path hdfsPath = new Path(checkPath);
			long dirSize = 0;
			FileStatus[] childrenFile = fs.listStatus(hdfsPath);
			for(FileStatus childFile : childrenFile) {
				if(childFile.isDirectory()) {
					isLastDir = false;
					queueOfPath.offer(childFile.getPath().toString());
				}else {
					long fileSize = childFile.getLen();
					dirSize += fileSize;
					table.addOneFileSize(fileSize);
				}
			}
			if(isLastDir && !checkPath.equals(tableLocation)) {
				table.addOneDirSize(dirSize);
			}
		}
	}
}
