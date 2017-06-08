package io.transwarp.servlet;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import io.transwarp.bean.CheckItemBean;
import io.transwarp.bean.ConfigBean;
import io.transwarp.bean.LoginInfoBean;
import io.transwarp.bean.TableBean;
import io.transwarp.bean.restapiInfo.NodeBean;
import io.transwarp.bean.restapiInfo.RoleBean;
import io.transwarp.bean.restapiInfo.ServiceBean;
import io.transwarp.connTool.HdfsPool;
import io.transwarp.connTool.MyThreadPool;
import io.transwarp.connTool.ShellUtil;
import io.transwarp.information.CheckInfos;
import io.transwarp.information.ClusterInformation;
import io.transwarp.information.Constant;
import io.transwarp.information.PropertiesInfo;
import io.transwarp.servlet.configCheck.ServiceConfigCheckRunnable;
import io.transwarp.servlet.hdfsCheck.HdfsCheckRunnable;
import io.transwarp.servlet.hdfsCheck.HdfsMetaDataCheckRunnable;
import io.transwarp.servlet.hdfsCheck.HdfsSpaceCheckRunnable;
import io.transwarp.servlet.logCheck.LogCheckRunnable;
import io.transwarp.servlet.nodeCheck.NodeCheckRunnable;
import io.transwarp.servlet.processCheck.ProcessCheckRunnable;
import io.transwarp.servlet.resource.ExecutorCheckRunnable;
import io.transwarp.servlet.resource.ResourceCheckRunnable;
import io.transwarp.servlet.restapiCheck.RestapiCheckImpl;
import io.transwarp.servlet.restapiCheck.RestapiCheckV45;
import io.transwarp.servlet.restapiCheck.RestapiCheckV46;
import io.transwarp.servlet.tableCheck.TableCheckRunnable;
import io.transwarp.servlet.tableCheck.TableInfoByJDBC;
import io.transwarp.util.UtilTool;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.dom4j.Element;

public class ClusterCheck extends ClusterInformation {

	private static final Logger LOG = Logger.getLogger(ClusterCheck.class);
	
	private Map<String, String> serviceConfigMap;
	private LoginInfoBean loginInfo;
	private String commandOfCheckNamenode;
	private String activeNamenodeIP = null;
	private String activeNamenodeHost = null;
	private String managerHost = null;
	private String[] hdfsPaths;
	private Vector<String> datanodes = new Vector<String>();
	private String checkLog;
	
	public ClusterCheck() {
		super();
	}
	
	public void checkClusterInfo() {
		Date nowDate = new Date(System.currentTimeMillis());
		CheckInfos.beginTime = Constant.DATE_FORMAT.format(nowDate);
		getCheckItem();
		getLoginInfoBean();
		getInfoByRestAPI();
		getServiceConfigMap();
		checkByNodes();
		checkByServices();
		getHdfsInfo();
		if(PropertiesInfo.checkItems.isExcetor()) {
			getExecutorInfo();
		}
		MyThreadPool.closeWhenCompleted(5000);
		closeHdfsPool();
		nowDate = new Date(System.currentTimeMillis());
		CheckInfos.endTime = Constant.DATE_FORMAT.format(nowDate);
	}
	
	protected void getCheckItem() {
		String outputSheet = PropertiesInfo.prop_env.getProperty("outputSheet", "license,roleMap,nodeInfo");
		String[] items = outputSheet.split(",");
		PropertiesInfo.checkItems = new CheckItemBean(items);
	}
	
	protected void getLoginInfoBean() {
		loginInfo = new LoginInfoBean();
		String managerIP = PropertiesInfo.prop_env.getProperty("managerIP", "127.0.0.0");
		String managerUser = PropertiesInfo.prop_env.getProperty("username", "admin");
		String managerPwd = PropertiesInfo.prop_env.getProperty("password", "admin");
		this.checkLog = PropertiesInfo.prop_env.getProperty("checkLog", "false");
		
		loginInfo.setManagerUrl("http://" + managerIP + ":8180");
		loginInfo.setManagerUser(managerUser);
		loginInfo.setManagerPwd(managerPwd);	
		loginInfo.setHdfsUser("hdfs");
		
		commandOfCheckNamenode = "hdfs haadmin -getServiceState ";
	}
	
	protected void getInfoByRestAPI() {

		RestapiCheckImpl restapiCheck;
		if(tdh_version.startsWith("4.3") || tdh_version.startsWith("4.5")) {
			restapiCheck = new RestapiCheckV45(loginInfo);
		}else if(tdh_version.startsWith("4.6")) {
			restapiCheck = new RestapiCheckV46(loginInfo);
		}else {
			restapiCheck = new RestapiCheckV46(loginInfo);
		}
		restapiCheck.getInfoByRestAPI();
	}
	
	protected void getServiceConfigMap() {
		String serviceConfigPath = PropertiesInfo.prop_env.getProperty("configMap", "/var/lib/transwarp-manager/master/data/data/Service.json");
		try {
			String fileValue = UtilTool.readFile(serviceConfigPath);
			serviceConfigMap = new HashMap<String, String>();
			JSONArray array = JSONArray.fromObject(fileValue);
			for(int i = 0; i < array.size(); i++) {
				JSONObject json = array.getJSONObject(i);
				Object activeStatus = json.get("activeStatus");
				if(activeStatus == null || activeStatus.toString().equalsIgnoreCase("deleted")) {
					continue;
				}
				Object name = json.get("name");
				Object sid = json.get("sid");
				if(sid == null || name == null) {
					LOG.error("read Service.json error");
					continue;
				}
				serviceConfigMap.put(name.toString(), sid.toString());
				if(name.toString().startsWith("HDFS")) {
					String path = "/etc/" + sid.toString() + "/conf/";
					loginInfo.setHdfsKeytab("/etc/" + sid.toString() + "/hdfs.keytab");
					hdfsPaths = new String[]{path + "hdfs-site.xml", path + "core-site.xml"};
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
			ClusterInformation.errorInfos.add("get info from Service.json error" + "|" + e.getMessage());
		}
	}
	
	protected void checkByNodes() {
		String scriptSavePath = PropertiesInfo.prop_env.getProperty("scriptSavePath", "/tmp/");
		String nodeUser = PropertiesInfo.prop_env.getProperty("nodeUser", "root");
		String goalPath = PropertiesInfo.prop_env.getProperty("goalPath", "/home/");
		String managerIP = PropertiesInfo.prop_env.getProperty("managerIP", "");
		for(Iterator<String> hostnames = ClusterInformation.nodeInfoByRestAPIs.keySet().iterator(); 
				hostnames.hasNext(); ) {
			String hostname = hostnames.next();
			NodeBean node = ClusterInformation.nodeInfoByRestAPIs.get(hostname);
			if(node.getIpAddress().equals(managerIP)) {
				managerHost = hostname;
			}
			String nodeStatus = node.getStatus();
			if(nodeStatus.equals("Disasociated")) {
				continue;
			}
			if(!UtilTool.isConnectionBySsh(node.getIpAddress(), 22)) {
				ClusterInformation.errorInfos.add("connect to node " + node.getHostName() + " by port 22 is error|");
				CheckInfos.networkCheck.add("connect to node " + node.getHostName() + " by port 22 is error");
				LOG.error("connect to node " + node.getHostName() + " by port 22 is error");
			}
			if(checkLog.equals("true")) {
				MyThreadPool.addNewThread(new LogCheckRunnable(node, nodeUser, scriptSavePath, serviceConfigMap));
			}
			if(PropertiesInfo.checkItems.isNodeCheck()) {
				MyThreadPool.addNewThread(new NodeCheckRunnable(node));
			}
			if(PropertiesInfo.checkItems.isServiceConfig()) {
				MyThreadPool.addNewThread(new ServiceConfigCheckRunnable(node, serviceConfigMap, goalPath + "serviceConfigs/", nodeUser));
			}
		}
	}
	
	protected void getNamenodeHA(NodeBean node, String servicename) {
		if(activeNamenodeIP != null) {
			return;
		}
		try {
			Map<String, ConfigBean> serviceConfigs = ClusterInformation.configInfos.get(node.getHostName());
			ConfigBean config = serviceConfigs.get(servicename);
			Map<String, String> configFile = config.getConfigFileValue("hdfs-site.xml");
			String nameservice = configFile.get("dfs.ha.namenodes.nameservice1");
			LOG.info("namenode service is : " + nameservice);
			String[] items = nameservice.split(",");
			for(String item : items) {
				if(checkActiveNamenode(node.getIpAddress(), node.getHostName(), item) == true) {
					String nnPort = configFile.get("dfs.namenode.http-address.nameservice1." + item);
					LOG.info("hdfs namenode port : " + nnPort);
					String[] nnPortSplit = nnPort.split("\\:");
					NodeBean nodeOfActive = ClusterInformation.nodeInfoByRestAPIs.get(nnPortSplit[0]);
					activeNamenodeIP = nodeOfActive.getIpAddress();
					activeNamenodeHost = nodeOfActive.getHostName();
					return;
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}

	}
	
	protected boolean checkActiveNamenode(String ipAddress, String hostname, String namenodeservice) {
		String command = UtilTool.getCmdOfSecurity(commandOfCheckNamenode, loginInfo, hostname) + namenodeservice;
		try {
			String result = ShellUtil.executeDist(command, ipAddress);
			if(result != null && result.indexOf("active") != -1) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	protected void getHdfsInfo() {
		if(activeNamenodeIP == null && (PropertiesInfo.checkItems.isHdfsInfo() || PropertiesInfo.checkItems.isHdfsSpace())) {
			return;
		}
		if(PropertiesInfo.checkItems.isHdfsInfo()) {
			MyThreadPool.addNewThread(new HdfsCheckRunnable(activeNamenodeIP, activeNamenodeHost, loginInfo));
		}
		if(PropertiesInfo.checkItems.isHdfsSpace()) {
			MyThreadPool.addNewThread(new HdfsSpaceCheckRunnable());
		}
	}
	
	protected void checkByServices() {
		while(true) {
			int value = ClusterInformation.completedOfConfigCheck.intValue();
			if(value >= ClusterInformation.nodeInfoByRestAPIs.size()) {
				break;
			}
			LOG.info("wait service config check completed, completed task is : " + value + 
					", total task is " + ClusterInformation.nodeInfoByRestAPIs.size());
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		List<String> tableCheckInfos = new ArrayList<String>();
		for(Iterator<String> servicenames = ClusterInformation.serviceInfoByRestAPIs.keySet().iterator(); 
				servicenames.hasNext(); ) {
			String servicename = servicenames.next();
			ServiceBean service = ClusterInformation.serviceInfoByRestAPIs.get(servicename);
			if(service.getType().equals("HDFS")) {
				String enableKerberos = service.getEnableKerberos();
				if(enableKerberos.equals("")) {
					loginInfo.setEnableKerberos("false");
				}else {
					loginInfo.setEnableKerberos(enableKerberos);
				}
			}
			boolean hasMetastore = false;
			String inceptorHostname = null;
			List<RoleBean> roles = service.getRoles();
			for(RoleBean role : roles) {
				String roleType = role.getRoleType();
				NodeBean node = role.getNode();
				if(roleType.matches("\\S*NAMENODE\\S*")) {
					if(PropertiesInfo.checkItems.isHdfsMeta()) {
						getHdfsMetaDataInfo(role, servicename);
					}
					if(PropertiesInfo.checkItems.isNamenodeHA()) {
						getNamenodeHA(node, servicename);
					}
				}else if(roleType.matches("\\S*DATANODE\\S*")) {
					datanodes.add(node.getHostName());
				}else if(roleType.equals("INCEPTOR_METASTORE")) {
					hasMetastore = true;
				}else if(roleType.equals("INCEPTOR_SERVER")) {
					inceptorHostname = node.getHostName();
				}
				if(PropertiesInfo.checkItems.isProcess()) {
					getProcessInfo(role, servicename);
				}
				if(PropertiesInfo.checkItems.isExcetor()) {
					Map<String, ConfigBean> serviceConfigs = ClusterInformation.configInfos.get(node.getHostName());
					MyThreadPool.addNewThread(new ResourceCheckRunnable(role, service.getType(), serviceConfigs.get(servicename)));
				}
			}
			if(hasMetastore && inceptorHostname != null) {
				tableCheckInfos.add(inceptorHostname + "," + servicename);
			}
		}
		getHdfsPool();
		if(PropertiesInfo.checkItems.isTable()) {
			for(String tableCheckInfo : tableCheckInfos) {
				String[] items = tableCheckInfo.split(",");
				getTablesInfo(items[0], items[1]);
			}
		}
	}
	
	protected void getHdfsPool() {
		try {
			if(activeNamenodeIP == null || activeNamenodeIP.equals("")) {
				ClusterInformation.errorInfos.add("get active namenode ip error|");
				LOG.error("get active namenode ip error");
				return;
			}
			HdfsPool.openPool(activeNamenodeIP, loginInfo, hdfsPaths, managerHost);
		}catch(Exception e) {
			ClusterInformation.errorInfos.add("open hdfs connection pool error|" + e.getMessage());
			e.printStackTrace();
		}
	}
	
	protected void closeHdfsPool() {
		try {
			if(HdfsPool.buildPool) {
				HdfsPool.closePool();
			}
		}catch(Exception e) {
			ClusterInformation.errorInfos.add("close hdfs connection pool error|" + e.getMessage());
			e.printStackTrace();
		}
	}
	
	private void getTablesInfo(String hostname, String servicename) {
		if(!HdfsPool.buildPool) {
			LOG.error("hdfs connection pool open error");
			return;
		}
		try {
			Map<String, ConfigBean> configOfNode = ClusterInformation.configInfos.get(hostname);
			ConfigBean config = configOfNode.get(servicename);
			Map<String, String> configValue = config.getConfigFileValue("hive-site.xml");
			String url = configValue.get("javax.jdo.option.ConnectionURL");
			String username = configValue.get("javax.jdo.option.ConnectionUserName");
			String password = configValue.get("javax.jdo.option.ConnectionPassword");
			if(url == null || url.equals("") || username == null || username.equals("") || password == null || password.equals("")) {
				ClusterInformation.errorInfos.add("get connection of mysql error | ");
				LOG.error("get jdbc connection of mysql error");
			}
			TableInfoByJDBC tableCheck = new TableInfoByJDBC(servicename, url, username, password);
			tableCheck.getTableInfo();
			Vector<TableBean> tables = ClusterInformation.tableInfos.get(servicename);
			for(TableBean table : tables) {
				MyThreadPool.addNewThread(new TableCheckRunnable(table));
			}
		}catch(Exception e) {
			ClusterInformation.errorInfos.add("get table info error, service is : " + servicename + "|" + e.getMessage());
		}
	}
	
	protected void getHdfsMetaDataInfo(RoleBean role, String servicename) {
		NodeBean node = role.getNode();
		Map<String, ConfigBean> serviceConfigs = ClusterInformation.configInfos.get(node.getHostName());
		ConfigBean serviceConfig = serviceConfigs.get(servicename);
		Map<String, String> configValue = serviceConfig.getConfigFileValue("hdfs-site.xml");
		String path = configValue.get("dfs.namenode.name.dir");
		if(path == null) {
			LOG.error("dfs.namenode.name.dir is no found, ip : " + node.getIpAddress());
			return;
		}
		MyThreadPool.addNewThread(new HdfsMetaDataCheckRunnable(node, path.split(",")));
	}
	
	protected void getProcessInfo(RoleBean role, String servicename) {
		String health = role.getHealth();
		if(health.equals("DOWN")) {
			return;
		}
		String roleType = role.getRoleType();
		String topic = servicename + ":" + roleType;
		List<Element> processConfigs = PropertiesInfo.prop_process.getAll();
		for(Element processConfig : processConfigs) {
			String serviceRole = processConfig.elementText("serviceRoleType");
			if(roleType.equals(serviceRole)) {
				NodeBean node = role.getNode();
				String ipAddress = node.getIpAddress();
				MyThreadPool.addNewThread(new ProcessCheckRunnable(ipAddress, topic, processConfig.element("properties")));
			}
		}
	}
	
	protected void getExecutorInfo() {
		while(true) {
			int value = ClusterInformation.completedOfRoleResourceCheck.intValue();
			if(value >= ClusterInformation.roleNumbers) {
				break;
			}
			LOG.info("wait role resource check completed");
			try {
				Thread.sleep(2000);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		for(Iterator<String> hostnames = ClusterInformation.configInfos.keySet().iterator(); hostnames.hasNext(); ) {
			String hostname = hostnames.next();
			Map<String, ConfigBean> serviceConfigs = ClusterInformation.configInfos.get(hostname);
			for(Iterator<String> servicenames = serviceConfigs.keySet().iterator(); servicenames.hasNext(); ) {
				String servicename = servicenames.next();
				ConfigBean serviceConfig = serviceConfigs.get(servicename);
				Map<String, String> fileValue;
				try {
					fileValue = serviceConfig.getConfigFileValue("ngmr-context-env.sh");
				}catch(RuntimeException e) {
					continue;
				}
				if(fileValue.size() > 0) {
					MyThreadPool.addNewThread(new ExecutorCheckRunnable(servicename, datanodes, fileValue));
				}
			}
		}
	}
}
