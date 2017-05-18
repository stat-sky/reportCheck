package io.transwarp.report.outputBytext;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;

import io.transwarp.bean.ConfigBean;
import io.transwarp.bean.restapiInfo.MetricBean;
import io.transwarp.bean.restapiInfo.NodeBean;
import io.transwarp.information.ClusterInformation;
import io.transwarp.information.Constant;
import io.transwarp.information.PropertiesInfo;
import io.transwarp.util.UtilTool;

public class NodeReport extends ClusterInformation {
	
	public void outputToFile(String dirPath) throws Exception {
		UtilTool.checkAndBuildDir(dirPath);
		for(Iterator<String> hostnames = ClusterInformation.nodeInfoByRestAPIs.keySet().iterator(); hostnames.hasNext(); ) {
			String hostname = hostnames.next();
			NodeBean node = ClusterInformation.nodeInfoByRestAPIs.get(hostname);
			Map<String, String> nodeInfos = ClusterInformation.nodeInfoByShells.get(hostname);
			Map<String, ConfigBean> configs = ClusterInformation.configInfos.get(hostname);
			Map<String, String> portChecks = ClusterInformation.portInfos.get(hostname);
			Map<String, MetricBean> metrics = ClusterInformation.metricByRestAPIs.get(hostname);
			
			FileWriter write = new FileWriter(dirPath + hostname + ".txt");
			try {
				write.write(getNodeInfo(node, nodeInfos));
			}catch(Exception e) {
				e.printStackTrace();
			}
			try {
				write.write(getNtpInfo(nodeInfos));
			}catch(Exception e) {
				e.printStackTrace();
			}
			try {
				write.write(getJavaPathInfo(nodeInfos));
			}catch(Exception e) {
				e.printStackTrace();
			}
			try {
				write.write(getJDKInfo(nodeInfos));
			}catch(Exception e) {
				e.printStackTrace();
			}
			try {
				write.write(getDNSInfo(nodeInfos));
			}catch(Exception e) {
				e.printStackTrace();
			}
			try {
				write.write(getIptablesInfo(nodeInfos));
			}catch(Exception e) {
				e.printStackTrace();
			}
			try {
				write.write(getNetWorkInfo(nodeInfos));
			}catch(Exception e) {
				e.printStackTrace();
			}
			try {
				write.write(getHostsInfo(nodeInfos));
			}catch(Exception e) {
				e.printStackTrace();
			}
			try {
				write.write(getMemoryInfo(nodeInfos));
			}catch(Exception e) {
				e.printStackTrace();
			}
			try {
				write.write(getMountInfo(nodeInfos));
			}catch(Exception e) {
				e.printStackTrace();
			}
			try {
				write.write(getPortInfo(portChecks));
			}catch(Exception e) {
				e.printStackTrace();
			}
			try {
				write.write(getServiceConfigInfo(configs));
			}catch(Exception e) {
				e.printStackTrace();
			}
			try {
				write.write(getMetricInfo(metrics));
			}catch(Exception e) {
				e.printStackTrace();
			}
			write.flush();
			write.close();
		}
	}
	
	protected String getNodeInfo(NodeBean node, Map<String, String> nodeInfos) throws Exception {
		StringBuffer buffer = new StringBuffer("节点信息:\n");
		List<String[]> maps = new ArrayList<String[]>();
		maps.add(new String[]{"检测项", "值"});
		maps.add(new String[]{"isManager", node.getIsManaged()});
		maps.add(new String[]{"hostname", node.getHostName()});
		maps.add(new String[]{"ipAddress", node.getIpAddress()});
		maps.add(new String[]{"clusterName", node.getClusterName()});
		maps.add(new String[]{"rackName", node.getRackName()});
		maps.add(new String[]{"status", node.getStatus()});
		maps.add(new String[]{"numCores", node.getNumCores()});
		maps.add(new String[]{"totalPhysMemBytes", node.getTotalPhysMemBytes()});
		maps.add(new String[]{"cpu", node.getCpu()});
		maps.add(new String[]{"osType", node.getOsType()});
		Element config = PropertiesInfo.prop_nodeCheckOfShell.getElementByKeyValue("topic", "OS");
		Element properties = config.element("properties");
		List<?> props = properties.elements();
		for(Object prop : props) {
			Element item = (Element)prop;
			String parameter = item.elementText("parameter");
			String value = nodeInfos.get(parameter).trim();
			maps.add(new String[]{parameter, value});
		}
		buffer.append(PrintToTable.changeToTable(maps, 50));
		return buffer.toString();
	}
	
	protected String getNtpInfo(Map<String, String> nodeInfos) throws Exception {
		List<String[]> maps = new ArrayList<String[]>();
		String ntpCheckResult = nodeInfos.get("NTP");
		String[] lines = ntpCheckResult.split("\n");
		for(String line : lines) {
			line = line.trim();
			if(line.equals("") || (line.startsWith("=") && line.endsWith("="))) {
				continue;
			}
			String[] items = line.trim().split("\\s+");
			maps.add(items);
		}
		StringBuffer buffer = new StringBuffer("NTP检测:\n");
		buffer.append(PrintToTable.changeToTable(maps, 10));
		return buffer.toString();
	}
	
	protected String getJavaPathInfo(Map<String, String> nodeInfos) throws Exception {
		StringBuffer buffer = new StringBuffer("java 路径:\n");
		String checkResult = nodeInfos.get("JAVA_HOME");
		buffer.append(checkResult).append("\n\n");
		return buffer.toString();
	}
	
	protected String getJDKInfo(Map<String, String> nodeInfos) {
		StringBuffer buffer = new StringBuffer("jdk version :\n");
		String checkResult = nodeInfos.get("jdk_version");
		buffer.append(checkResult).append("\n\n");
		return buffer.toString();
	}
	
	protected String getDNSInfo(Map<String, String> nodeInfos) {
		StringBuffer buffer = new StringBuffer("DNS :\n");
		String checkResult = nodeInfos.get("DNS");
		buffer.append(checkResult).append("\n\n");
		return buffer.toString();
	}
	
	protected String getIptablesInfo(Map<String, String> nodeInfos) {
		StringBuffer buffer = new StringBuffer("防火墙信息:\n");
		String checkResult = nodeInfos.get("iptables");
		buffer.append(checkResult).append("\n\n");
		return buffer.toString();
	}
	
	protected String getNetWorkInfo(Map<String, String> nodeInfos) {
		StringBuffer buffer = new StringBuffer("网络信息:\n");
		String checkResult = nodeInfos.get("ip");
		buffer.append(checkResult);
		return buffer.toString();
	}
	
	protected String getHostsInfo(Map<String, String> nodeInfos) {
		StringBuffer buffer = new StringBuffer("hosts:\n");
		String checkResult = nodeInfos.get("hosts");
		buffer.append(checkResult);
		return buffer.toString();
	}
	
	protected String getMemoryInfo(Map<String, String> nodeInfos) {
		List<String[]> maps = new ArrayList<String[]>();
		String checkResult = nodeInfos.get("memory");
		String[] lines = checkResult.split("\n");
		for(String line : lines) {
			String[] items = line.trim().split("\\s+");
			maps.add(items);
		}
		StringBuffer buffer = new StringBuffer("节点内存检测:\n");
		buffer.append(PrintToTable.changeToTable(maps, 40));
		return buffer.toString();
	}
	
	protected String getMountInfo(Map<String, String> nodeInfos) {
		List<String[]> maps = new ArrayList<String[]>();
		String checkResult = nodeInfos.get("mount");
		String[] lines = checkResult.split("\n");
		for(String line : lines) {
			line = line.trim();
			if(line.startsWith("#") || line.equals("")) {
				continue;
			}
			String[] items = line.split(" \\s*");
			maps.add(items);
		}
		StringBuffer buffer = new StringBuffer("磁盘挂载检测:\n");
		buffer.append(PrintToTable.changeToTable(maps, 40));
		return buffer.toString();
	}
	
	protected String getPortInfo(Map<String, String> portChecks) {
		List<String[]> maps = new ArrayList<String[]>();
		for(Iterator<String> keys = portChecks.keySet().iterator(); keys.hasNext(); ) {
			String key = keys.next();
			String result = portChecks.get(key).trim();
			maps.add(new String[]{key, result});
		}
		StringBuffer buffer = new StringBuffer("端口检测:\n");
		buffer.append(PrintToTable.changeToTable(maps, 60));
		return buffer.toString();
	}
	
	protected String getServiceConfigInfo(Map<String, ConfigBean> configs) throws Exception {
		StringBuffer buffer = new StringBuffer("服务配置检测:\n");
		List<String[]> maps = new ArrayList<String[]>();
		for(Iterator<String> servicenames = configs.keySet().iterator(); servicenames.hasNext(); ) {
			String servicename = servicenames.next();
			ConfigBean config = configs.get(servicename);
			Map<String, Map<String, String>> configFiles = config.getAllFiles();
			for(Iterator<String> filenames = configFiles.keySet().iterator(); filenames.hasNext(); ) {
				String filename = filenames.next();
				Map<String, String> values = configFiles.get(filename);
				Element propConfig = null;
				try {
					 propConfig = PropertiesInfo.prop_serviceConfig.getElementByKeyValue("topic", filename);
				}catch(RuntimeException e) {
					continue;
				}
				String properties = propConfig.elementText("property");
				String[] checkItems = properties.split(";");
				for(String checkItem : checkItems) {
					String value = values.get(checkItem);
					if(value == null) {
						continue;
					}
					String[] items = value.split(",");
					for(int i = 0; i < items.length; i++) {
						if(i == 0) {
							maps.add(new String[]{checkItem, items[i]});
						}else {
							maps.add(new String[]{null, items[i]});
						}
					}
				}
				if(maps.size() > 0) {
					buffer.append("  ").append(servicename).append(": ").append(filename).append(":\n");
					buffer.append(PrintToTable.changeToTable(maps, 50));
					maps.clear();
				}
			}
		}
		return buffer.toString();
	}
	
	protected String getMetricInfo(Map<String, MetricBean> metrics) throws Exception {
		StringBuffer buffer = new StringBuffer("指标检测:\n");
		List<String[]> maps = new ArrayList<String[]>();
		for(Iterator<String> metricnames = metrics.keySet().iterator(); metricnames.hasNext(); ) {
			String metricname = metricnames.next();
			MetricBean metric = metrics.get(metricname);
			String unit = metric.getUnit();
			List<String> values = metric.getMetricValues();
			for(String value : values) {
				String[] items = value.split(":");
				String timestamp = items[0];
				Date date = new Date(Long.valueOf(timestamp));
				items[0] = Constant.DATE_FORMAT.format(date);
				items[1] += " " + unit;
				maps.add(items);
			}
			if(maps.size() > 0) {
				String name = metric.getMetricName();
				buffer.append("  ").append(name).append(":\n");
				buffer.append(PrintToTable.changeToTable(maps, 50));
				maps.clear();
			}
		}
		return buffer.toString();
	}
}
