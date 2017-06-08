package io.transwarp.servlet.hdfsCheck;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.transwarp.bean.restapiInfo.NodeBean;
import io.transwarp.connTool.MyThreadPool;
import io.transwarp.connTool.ShellUtil;
import io.transwarp.information.ClusterInformation;
import io.transwarp.util.UtilTool;

import org.apache.log4j.Logger;

public class HdfsMetaDataCheckRunnable extends ClusterInformation implements Runnable{

	private static final Logger LOG = Logger.getLogger(HdfsMetaDataCheckRunnable.class);
	
	private String[] paths;
	private String hostname;
	private String ipAddress;
	
	private List<String> editFiles = new ArrayList<String>();
	private List<String> fsimageFiles = new ArrayList<String>();
	
	public HdfsMetaDataCheckRunnable(NodeBean node, String[] paths) {
		this.paths = paths;
		this.hostname = node.getHostName();
		this.ipAddress = node.getIpAddress();
	}
	
	@Override
	public void run() {
		LOG.info("begin check hdfs meta data");
		try {
			for(String path : paths) {
				path = path + "/current/";
				getMetaDataFilesFromPath(path);
				analysisEditLogContinuity(path);
				analysisFsimageFile(path);
				editFiles.clear();
				fsimageFiles.clear();
			}			
			MyThreadPool.threadSuccess();
			LOG.info("hdfs meta data check is success");
		}catch(Exception e) {
			e.printStackTrace();
			MyThreadPool.threadFailure();
			ClusterInformation.errorInfos.add("hdfs meta data of node : " + hostname + " check error|" + e.getMessage());
			LOG.error("hdfs meta data check is error, hostname is : " + hostname);
		}
	}
	
	protected void getMetaDataFilesFromPath(String path) throws Exception {
		String fileList = ShellUtil.executeDist("ls " + path, ipAddress);
		String[] lines = fileList.split("\n");
		for(String line : lines) {
			if(line.startsWith("edit")) {
				editFiles.add(line);
			}else if(line.startsWith("fsimage")) {
				fsimageFiles.add(line);
			}
		}
	}
	
	protected void analysisEditLogContinuity(String path) throws Exception {
		List<String> errorFiles = new ArrayList<String>();
		List<Long> editIDs = new ArrayList<Long>();
		for(String filename : editFiles) {
			if(filename.endsWith("empty")) {
				continue;
			}
			String editID = UtilTool.getInteger(filename);
			String[] items = editID.split("-");
			for(String item : items) {
				editIDs.add(Long.valueOf(item));
			}
		}
		
		int size = editIDs.size();
		for(int i = 2; i < size; i += 2) {
			if(editIDs.get(i) - editIDs.get(i - 1) != 1) {
				errorFiles.add((editIDs.get(i - 1) + 1) + "-" + (editIDs.get(i) - 1));
			}
		}
		ClusterInformation.editLogContinuity.put(hostname + ":" + path, errorFiles);
	}
	
	protected void analysisFsimageFile(String path) throws Exception {
		Map<String, String> fsimageFileList = new HashMap<String, String>();
		Map<String, String> md5fileList = new HashMap<String, String>();
		for(String filename : fsimageFiles) {
			String fsimageID = UtilTool.getInteger(filename);
			if(filename.endsWith("md5")) {
				md5fileList.put(fsimageID, filename);
			}else {
				fsimageFileList.put(fsimageID, filename);
			}
		}
		StringBuffer fsimageInfo = new StringBuffer();
		for(Iterator<String> ids = fsimageFileList.keySet().iterator(); ids.hasNext(); ) {
			String fsimageID = ids.next();
			String filename = fsimageFileList.get(fsimageID);
			String md5file = md5fileList.get(fsimageID + "5");
			if(fsimageInfo.length() > 0) {
				fsimageInfo.append(";");
			}
			if(md5file != null) {
				String timestamp = getTimestampOfFile(path + filename);
				fsimageInfo.append("filename : ").append(filename).append(", timestamp : ").append(timestamp);
			}else {
				fsimageInfo.append("this file : ").append(filename).append(" has no md5 file");
			}
		}
		ClusterInformation.fsimageIDAndTimestamp.put(hostname + ":" + path, fsimageInfo.toString());
	}
	
	private String getTimestampOfFile(String path) throws Exception {
		String command = "stat " + path + " | grep -i Modify | awk -F. '{print $1}'";
		String timestamp = ShellUtil.executeDist(command, ipAddress);
		return timestamp;
	}
}
