package io.transwarp.servlet.hdfsCheck;

import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import io.transwarp.bean.LoginInfoBean;
import io.transwarp.connTool.MyThreadPool;
import io.transwarp.connTool.ShellUtil;
import io.transwarp.information.ClusterInformation;
import io.transwarp.information.PropertiesInfo;
import io.transwarp.util.UtilTool;

public class HdfsCheckRunnable extends ClusterInformation implements Runnable{
	
	private static final Logger LOG = Logger.getLogger(HdfsCheckRunnable.class);
	
	private String namenodeIP;
	private LoginInfoBean loginInfo;
	
	public HdfsCheckRunnable(String namenodeIP, LoginInfoBean loginInfo) {
		this.namenodeIP = namenodeIP;
		this.loginInfo = loginInfo;
	}
	
	@Override
	public void run() {
		LOG.info("begin check hdfs report/fsck");
		try {
			checkReport();
			MyThreadPool.threadSuccess();
			LOG.info("hdfs check completed");
		}catch(Exception e) {
			e.printStackTrace();
			MyThreadPool.threadFailure();
			LOG.info("hdfs check error");
		}
	}
	
	protected void checkReport() throws Exception {
		List<Element> configs = PropertiesInfo.prop_cluster.getAll();
		for(Element config : configs) {
			String itemName = config.elementText("name");
			String execCommand = config.elementText("command");
			try {
				execCommand = UtilTool.getCmdOfSecurity(execCommand, loginInfo);
				String result = ShellUtil.executeDist(execCommand, namenodeIP);
				ClusterInformation.hdfsInfos.put(itemName, result);
			}catch(Exception e) {
				LOG.error("check hdfs error, exec command is : " + execCommand);
			}
		}
	}
	


}
