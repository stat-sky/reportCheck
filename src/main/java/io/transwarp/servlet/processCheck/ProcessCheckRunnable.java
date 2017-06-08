package io.transwarp.servlet.processCheck;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import io.transwarp.connTool.MyThreadPool;
import io.transwarp.connTool.ShellUtil;
import io.transwarp.information.ClusterInformation;

public class ProcessCheckRunnable extends ClusterInformation implements Runnable {
	
	private static final Logger LOG = Logger.getLogger(ProcessCheckRunnable.class);
	
	private String ipAddress;
	private String topic;
	private Element config;
	
	public ProcessCheckRunnable(String ipAddress, String topic, Element config) {
		this.ipAddress = ipAddress;
		this.topic = topic;
		this.config = config;
	}
	
	@Override
	public void run() {
		LOG.info("begin check process of " + topic + " on " + ipAddress);
		try {
			checkProcess();
			MyThreadPool.threadSuccess();
			LOG.info("process of service role : " + topic + " is success");
		}catch(Exception e) {
			e.printStackTrace();
			MyThreadPool.threadFailure();
			ClusterInformation.errorInfos.add("process check of service : " + topic + " is error|" + e.getMessage());
			LOG.error("process check is error, service role is : " + topic);
		}
	}

	
	protected void checkProcess() throws Exception {
		Map<String, String> processCheckResult = new HashMap<String, String>();
		List<?> properties = config.elements();
		for(Object property : properties) {
			Element prop = (Element)property;
			String name = prop.elementText("name");
			String command = prop.elementText("command");
			String result = ShellUtil.executeDist(command, ipAddress);
			processCheckResult.put(name, result.trim());
		}
		ClusterInformation.serviceProcessInfos.put(topic, processCheckResult);
	}
}
