package io.transwarp.servlet.hdfsCheck;

import java.io.IOException;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

import io.transwarp.bean.LoginInfoBean;
import io.transwarp.connTool.HdfsUtil;
import io.transwarp.connTool.MyThreadPool;
import io.transwarp.information.ClusterInformation;

public class HdfsSpaceCheckRunnable extends ClusterInformation implements Runnable{

	private static final Logger LOG = Logger.getLogger(HdfsSpaceCheckRunnable.class);
	private String namenodeIP;
	private LoginInfoBean loginInfo;
	private String[] configPaths;
	private FileSystem fs;
	
	public HdfsSpaceCheckRunnable(String namenodeIP, LoginInfoBean loginInfo, String[] configPaths) {
		this.namenodeIP = namenodeIP;
		this.loginInfo = loginInfo;
		this.configPaths = configPaths;
	}
	
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
			LOG.info("hdfs check error");
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
	
	protected void checkFileSpace() throws Exception {
		fs = HdfsUtil.getHdfsFileSystem(configPaths, namenodeIP, loginInfo);
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
