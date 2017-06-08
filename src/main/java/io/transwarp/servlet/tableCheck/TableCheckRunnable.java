package io.transwarp.servlet.tableCheck;

import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

import io.transwarp.bean.TableBean;
import io.transwarp.connTool.HdfsPool;
import io.transwarp.connTool.MyThreadPool;
import io.transwarp.information.ClusterInformation;

public class TableCheckRunnable extends ClusterInformation implements Runnable{

	private static final Logger LOG = Logger.getLogger(TableCheckRunnable.class);
	
	private TableBean table;
	private FileSystem fs;
	
	public TableCheckRunnable(TableBean table) {
		this.table = table;
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
			ClusterInformation.errorInfos.add("table check error : " + table.getTable_name() + "|" + fileNoFound.getMessage());
		}catch(Exception e) {
			e.printStackTrace();
			MyThreadPool.threadFailure();
			ClusterInformation.errorInfos.add("table check error : " + table.getTable_name() + "|" + e.getMessage());
			LOG.error("table check error, table name is : " + table.getTable_name());
		}finally {
			if(fs != null) {
				try {
					HdfsPool.putHdfsConn(fs);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	protected void checkTableSpace() throws Exception {
		fs = HdfsPool.getHdfsConn();
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
