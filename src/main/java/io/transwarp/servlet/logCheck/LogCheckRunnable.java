package io.transwarp.servlet.logCheck;

import io.transwarp.bean.restapiInfo.NodeBean;
import io.transwarp.bean.restapiInfo.RoleBean;
import io.transwarp.bean.restapiInfo.ServiceBean;
import io.transwarp.connTool.MyThreadPool;
import io.transwarp.connTool.ShellUtil;
import io.transwarp.information.ClusterInformation;
import io.transwarp.information.PropertiesInfo;
import io.transwarp.util.UtilTool;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Element;

public class LogCheckRunnable extends ClusterInformation implements Runnable{
	
	private static final Logger LOG = Logger.getLogger(LogCheckRunnable.class);
	
	private String hostname;
	private String ipAddress;
	private String nodeUser;
	private String scriptSavePath;
	private Map<String, String> serviceConfigMap;
	
	public LogCheckRunnable(NodeBean node, String nodeUser, String scriptSavePath, Map<String, String> serviceConfigMap) {
		this.hostname = node.getHostName();
		this.ipAddress = node.getIpAddress();
		this.nodeUser = nodeUser;
		this.scriptSavePath = scriptSavePath;
		this.serviceConfigMap = serviceConfigMap;
	}
	
	@Override
	public void run() {
		LOG.info("begin check log");
		try {
			sendJarScript();
			long timestamp = System.currentTimeMillis();
			String resultDirName = "result-" + timestamp;
			runShellToCheckLog(resultDirName);
			getResult(resultDirName);
			MyThreadPool.threadSuccess();
			LOG.info("log check of node : " + hostname + " is success");
		}catch(Exception e) {
			e.printStackTrace();
			MyThreadPool.threadFailure();
			ClusterInformation.errorInfos.add("log check error|" + e.getMessage());
			LOG.error("log check is error, hostname is : " + hostname);
		}
	}
	
	protected void sendJarScript() throws Exception {
		StringBuffer savePath = new StringBuffer();
		savePath.append(nodeUser).append("@").append(ipAddress).append(":/").append(scriptSavePath);
		String[] scriptPaths = new String[]{"script/logCheck.jar", "config/logCheck.xml"};
		for(String scriptPath : scriptPaths) {
			ShellUtil.scpFiler(scriptPath, savePath.toString());
		}
		
	}
	
	protected void runShellToCheckLog(String resultDirName) throws Exception {
		StringBuffer commandBuffer = new StringBuffer();
		commandBuffer.append("java -jar ").append(scriptSavePath).append("logCheck.jar")
					.append(" ").append(hostname)
					.append(" ").append(scriptSavePath).append("logCheck.xml")
					.append(" ").append(scriptSavePath).append(resultDirName).append("/")
					.append(" ").append(PropertiesInfo.prop_env.getProperty("logCheckTimeRange", "24"));
		List<Element> logCheckConfigs = PropertiesInfo.prop_logCheck.getAll();
		for(Iterator<String> servicenames = ClusterInformation.serviceInfoByRestAPIs.keySet().iterator(); 
				servicenames.hasNext(); ) {
			String servicename = servicenames.next();
			ServiceBean service = ClusterInformation.serviceInfoByRestAPIs.get(servicename);
			List<RoleBean> roles = service.getRoles();
			for(RoleBean role : roles) {
				NodeBean node = role.getNode();
				String ipAddress = node.getIpAddress();
				if(ipAddress == null || !ipAddress.equals(this.ipAddress)) {
					continue;
				}
				String roleType = role.getRoleType();
				for(Element logCheckConfig : logCheckConfigs) {
					String serviceRoleType = logCheckConfig.elementText("serviceRole");
					if(roleType.matches("\\S*" + serviceRoleType + "\\S*")) {
						commandBuffer.append(" ").append(servicename).append("/").append(serviceRoleType).append(":")
									.append("/var/log/").append(serviceConfigMap.get(servicename)).append("/");
						break;
					}
				}
			}
		}
		commandBuffer.append(" > ").append(scriptSavePath).append("logCheck.log 2>&1");
		LOG.info("log check command is : " + commandBuffer.toString() + ", ip : " + ipAddress);
		ShellUtil.executeDist(commandBuffer.toString(), ipAddress);
	}
	
	protected void getResult(String resultDirName) throws Exception {
		String resultDirPath = nodeUser + "@" + ipAddress + ":" + scriptSavePath + resultDirName;
		String logOfCheckPath = nodeUser + "@" + ipAddress + ":" + scriptSavePath + "/logCheck.log";
		String localSave = PropertiesInfo.prop_env.getProperty("goalPath", "/home/") + "logCheck/" + hostname + "/";
		UtilTool.checkAndBuildDir(localSave);
		ShellUtil.scpDirector(resultDirPath, localSave);
		ShellUtil.scpFiler(logOfCheckPath, localSave);
	}

}
