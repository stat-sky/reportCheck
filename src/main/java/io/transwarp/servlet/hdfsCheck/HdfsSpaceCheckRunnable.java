package io.transwarp.servlet.hdfsCheck;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

import io.transwarp.connTool.HdfsPool;
import io.transwarp.connTool.HdfsUtil;
import io.transwarp.connTool.MyThreadPool;
import io.transwarp.information.ClusterInformation;

public class HdfsSpaceCheckRunnable extends ClusterInformation implements Runnable{

	private static final Logger LOG = Logger.getLogger(HdfsSpaceCheckRunnable.class);
	private FileSystem fs;
	
	@Override
	public void run() {
		LOG.info("begin check hdfs space");
		try {
			checkFileSpace();
			MyThreadPool.threadSuccess();
			LOG.info("hdfs check completed");
		}catch(Exception e) {
			e.printStackTrace();
			MyThreadPool.threadFailure();
			ClusterInformation.errorInfos.add("hdfs space check error|" + e.getMessage());
			LOG.error("hdfs check error");
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
	
	protected void checkFileSpace() throws Exception {
		if(!HdfsPool.buildPool) {
			LOG.error("hdfs connection pool open error");
			throw new Exception("get hdfs connection faild");
		}
		fs = HdfsPool.getHdfsConn();
		deepSearchFile(1, "/");
	}
	
	private long deepSearchFile(int floor, String filePath) throws Exception {
		long dirSize = 0;
		Path filePath1 = new Path(filePath);
		FileStatus[] childrenFile = fs.listStatus(filePath1);
		for(FileStatus childFile : childrenFile) {
			String filename = childFile.getPath().toString();
			if(floor == 2 && filename.indexOf("inceptorsql") != -1 && filename.indexOf("user") != -1) {
				continue;
			}
			if(childFile.isFile()) {
				dirSize += childFile.getLen();
			}else {
				dirSize += deepSearchFile(floor + 1, filename);
			}
		}
		if(floor > 1 && floor < 4 && (floor != 2 || filePath.indexOf("inceptorsql") == -1)) {
			ClusterInformation.hdfsSpaceSizeInfos.add(HdfsUtil.getHdfsPath(filePath) + ":" + dirSize);
		}
		return dirSize;
	}
}
