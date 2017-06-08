package io.transwarp.servlet.configCheck;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import io.transwarp.bean.ConfigBean;
import io.transwarp.bean.restapiInfo.NodeBean;
import io.transwarp.connTool.MyThreadPool;
import io.transwarp.connTool.ShellUtil;
import io.transwarp.information.ClusterInformation;
import io.transwarp.util.ReadXmlUtil;
import io.transwarp.util.UtilTool;

public class ServiceConfigCheckRunnable extends ClusterInformation implements Runnable {
	
	private static final Logger LOG = Logger.getLogger(ServiceConfigCheckRunnable.class);
	
	private String hostname;
	private String ipAddress;
	private String nodeUser;
	private Map<String, String> serviceConfigMap;
	private String goalPath;
	
	public ServiceConfigCheckRunnable(NodeBean node, Map<String, String> serviceConfigMap, 
			String goalPath, String nodeUser) {
		this.hostname = node.getHostName();
		this.ipAddress = node.getIpAddress();
		this.serviceConfigMap = serviceConfigMap;
		this.goalPath = goalPath;
		this.nodeUser = nodeUser;
	}
	
	@Override
	public void run() {
		try {
			checkMysqlHA();
			getConfigByService();
			MyThreadPool.threadSuccess();
			LOG.info("service config check of node : " + hostname + " is success");
		}catch(Exception e) {
			e.printStackTrace();
			ClusterInformation.errorInfos.add("get service config of node : " + hostname + " is error|" + e.getMessage());
			LOG.error("service config check error, hostname is : " + hostname);
			MyThreadPool.threadFailure();
		}finally {
			ClusterInformation.completedOfConfigCheck.incrementAndGet();
		}
	}
	
	protected void checkMysqlHA() throws Exception {
		boolean hasMysql = false;
		String shellCmd = "ps -ef | grep mysql";
		String result = ShellUtil.executeDist(shellCmd, ipAddress);
		String[] lines = result.split("\n");
		for(String line : lines) {
			if(line.startsWith("mysql")) {
				hasMysql = true;
				break;
			}
		}
		if(!hasMysql) {
			return;
		}
		String configPath = getMysqlConfig();
		String configValue = UtilTool.readFile(configPath);
		if(configValue.indexOf("server_id") != -1) {
			if(configValue.indexOf("read_only") != -1) {
				ClusterInformation.mysqlHAInfo.put(hostname, "2");
			}else {
				ClusterInformation.mysqlHAInfo.put(hostname, "1");
			}
		}else {
			ClusterInformation.mysqlHAInfo.put(hostname, "0");
		}
		
	}
	
	private String getMysqlConfig() throws Exception {
		StringBuffer getPath = new StringBuffer();
		getPath.append(nodeUser).append("@").append(ipAddress).append(":/etc/my.cnf");
		StringBuffer savePath = new StringBuffer();
		savePath.append(goalPath).append("mysql/").append(ipAddress).append("/");
		UtilTool.checkAndBuildDir(savePath.toString());
		ShellUtil.scpFiler(getPath.toString(), savePath.toString());
		return savePath.toString() + "my.cnf";
	}
	
	protected void getConfigByService() throws Exception {
		Map<String, ConfigBean> serviceConfigs = new HashMap<String, ConfigBean>();
		for(Iterator<String> servicenames = ClusterInformation.serviceInfoByRestAPIs.keySet().iterator(); 
				servicenames.hasNext(); ) {
			String servicename = servicenames.next();
			String sid = serviceConfigMap.get(servicename);
			String savePath = getconfigFiles(sid, servicename);
			
			ConfigBean config = serviceConfigs.get(servicename);
			if(config == null) {
				config = new ConfigBean(servicename);
			}
			
			Queue<String> queue = new LinkedList<String>();
			queue.offer(savePath);
			while(!queue.isEmpty()) {
				String path = queue.poll();
				File file = new File(path);
				if(file.isDirectory()) {
					File[] children = file.listFiles();
					for(File child : children) {
						queue.offer(child.getAbsolutePath());
					}
				}else {
					Map<String, String> configValue = null;
					if(path.endsWith(".xml")) {
						configValue = getConfigValueOfXml(path);
					}else if(path.endsWith(".sh") || path.endsWith("-env")) {
						configValue = getConfigValueOfShell(path);
					}
					if(configValue == null || configValue.size() == 0) {
						continue;
					}
					config.addConfigFile(UtilTool.getFileName(path), configValue);
				}
			}
			serviceConfigs.put(servicename, config);
		}
		ClusterInformation.configInfos.put(hostname, serviceConfigs);
	}
	
	private String getconfigFiles(String sid, String servicename) throws Exception {
		StringBuffer getPath = new StringBuffer();
		getPath.append(nodeUser).append("@").append(ipAddress)
				.append(":/etc/").append(sid).append("/conf/*");
		StringBuffer savePath = new StringBuffer();
		savePath.append(goalPath).append(servicename).append("/").append(ipAddress).append("/");
		UtilTool.checkAndBuildDir(savePath.toString());
		ShellUtil.scpFiler(getPath.toString(), savePath.toString());
		LOG.info(getPath.toString());
		return savePath.toString();
	}
	
	private Map<String, String> getConfigValueOfXml(String path) throws Exception {
		Map<String, String> configValue = new HashMap<String, String>();
		ReadXmlUtil xmlRead = new ReadXmlUtil(path);
		List<Element> properties = xmlRead.getAll();
		for(Element property : properties) {
			String name = property.elementText("name");
			String value = property.elementText("value");
			configValue.put(name, value);
		}
		return configValue;
	}
	
	private Map<String, String> getConfigValueOfShell(String path) throws Exception {
		Map<String, String> configValue = new HashMap<String, String>();
		BufferedReader reader = new BufferedReader(new FileReader(path));
		while(true) {
			String line = reader.readLine();
			if(line == null) {
				break;
			}
			int equalsID = line.indexOf("=", 1);
			if(equalsID == -1) {
				continue;
			}
			String name = line.substring(0, equalsID);
			if(name.indexOf("export") != -1) {
				name = name.substring(7);
			}
			String value = line.substring(equalsID + 1).replaceAll("\"", "");
			String hasValue = configValue.get(name);
			if(hasValue != null) {
				value = hasValue + " " + value;
			}
			configValue.put(name, value);
		}
		reader.close();
		return configValue;
	}
}
