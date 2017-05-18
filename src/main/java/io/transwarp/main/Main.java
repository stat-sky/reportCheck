package io.transwarp.main;

import java.io.FileInputStream;

import io.transwarp.connTool.JDBCUtil;
import io.transwarp.connTool.MyThreadPool;
import io.transwarp.connTool.ShellUtil;
import io.transwarp.information.ClusterInformation;
import io.transwarp.information.PropertiesInfo;
import io.transwarp.report.TotalReport;
import io.transwarp.util.ReadXmlUtil;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Main {

	private static final Logger LOG = Logger.getLogger(Main.class);
	
	public static void main(String[] args) {
		init();
		TotalReport report = new TotalReport();
		report.getReport();
	}
	
	private static void init() {
		try {
			loadConfig();
			initValue();
		}catch(Exception e) {
			e.printStackTrace();
			LOG.error("init project error");
			System.exit(1);
		}
	}
	
	private static void loadConfig() throws Exception {
		LOG.info("load config file");
		PropertyConfigurator.configure("config/log4j.properties");
		PropertiesInfo.prop_env.load(new FileInputStream("config/env.properties"));
		PropertiesInfo.prop_restapi = new ReadXmlUtil("config/restapi/restapiURL.xml");
		PropertiesInfo.prop_resource = new ReadXmlUtil("config/restapi/resource.xml");
		PropertiesInfo.prop_serviceConfig = new ReadXmlUtil("config/restapi/serviceConfig.xml");
		PropertiesInfo.prop_metric = new ReadXmlUtil("config/restapi/loadMetric.xml");
		PropertiesInfo.prop_port = new ReadXmlUtil("config/shell/portCheck.xml");
		PropertiesInfo.prop_process = new ReadXmlUtil("config/shell/processCheck.xml");
		PropertiesInfo.prop_cluster = new ReadXmlUtil("config/shell/clusterCheck.xml");
		PropertiesInfo.prop_logCheck = new ReadXmlUtil("config/logCheck.xml");
		
		String osType = PropertiesInfo.prop_env.getProperty("os");
		LOG.info("system is : " + osType);
		if(osType == null) {
			PropertiesInfo.prop_nodeCheckOfShell = new ReadXmlUtil("config/shell/CentOS/nodeCheck.xml");
		}else if(osType.equalsIgnoreCase("centos")) {
			PropertiesInfo.prop_nodeCheckOfShell = new ReadXmlUtil("config/shell/CentOS/nodeCheck.xml");
		}else if(osType.equalsIgnoreCase("suse")) {
			PropertiesInfo.prop_nodeCheckOfShell = new ReadXmlUtil("config/shell/Suse/nodeCheck.xml");
		}else {
			PropertiesInfo.prop_nodeCheckOfShell = new ReadXmlUtil("config/shell/CentOS/nodeCheck.xml");
		}
	}
	
	private static void initValue() throws Exception {
		LOG.info("initialize");
		String tdh_version = PropertiesInfo.prop_env.getProperty("tdh_version", "4.6");
		ClusterInformation.setTdhVersion(tdh_version);
		String rootKey = PropertiesInfo.prop_env.getProperty("rootKey", "/etc/transwarp/transwarp-id_rsa");
		String nodeUser = PropertiesInfo.prop_env.getProperty("nodeUser", "root");
		ShellUtil.initShell(rootKey, nodeUser);
		String threadNumber = PropertiesInfo.prop_env.getProperty("threadNumber", "5");
		int threadNum = Integer.valueOf(threadNumber);
		MyThreadPool.init(threadNum);
		String className = PropertiesInfo.prop_env.getProperty("className", "com.mysql.jdbc.Driver");
		JDBCUtil.initJDBCUtil(className);
	}
}
