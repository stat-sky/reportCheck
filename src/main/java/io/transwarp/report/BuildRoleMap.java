package io.transwarp.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import io.transwarp.bean.restapiInfo.NodeBean;
import io.transwarp.bean.restapiInfo.RoleBean;
import io.transwarp.bean.restapiInfo.ServiceBean;
import io.transwarp.information.CheckInfos;
import io.transwarp.information.ClusterInformation;
import io.transwarp.information.Constant;
import io.transwarp.util.UtilTool;

public class BuildRoleMap extends ClusterInformation {
	
	private static final Logger LOG = Logger.getLogger(BuildRoleMap.class);
	
	private Map<String, List<String>> roleMaps;
	private int totalServiceRole;
	private Map<String, List<String>> rackLists;
	private Map<String, Double> allocatedMemory;
	private Map<String, Integer> allocatedCore;
	private Map<String, Double> totalMemory;
	private Map<String, String> totalCore;
	
	public void getBuildRoleMap() {
		init();
		putServiceRoleInfoToRoleMap();
		List<List<String>> mergeResult = mergeNodeLists();
		ClusterInformation.roleMapTable = changeToForm(mergeResult);
	}
	
	protected void init() {
		roleMaps = new HashMap<String, List<String>>();
		//添加服务列
		List<String> serviceList = new ArrayList<String>();
		serviceList.add("rack");
		serviceList.add("node");
		serviceList.add("title");
		roleMaps.put("service", serviceList);
		//添加角色列
		List<String> roleList = new ArrayList<String>();
		roleList.add(null);
		roleList.add(null);
		roleList.add(null);
		roleMaps.put("role", roleList);
		//添加节点列
		rackLists = new HashMap<String, List<String>>();
		allocatedMemory = new HashMap<String, Double>();
		allocatedCore = new HashMap<String, Integer>();
		totalMemory = new HashMap<String, Double>();
		totalCore = new HashMap<String, String>();
		for(Iterator<String> hostnames = ClusterInformation.nodeInfoByRestAPIs.keySet().iterator(); hostnames.hasNext(); ) {
			String hostname = hostnames.next();
			NodeBean node = ClusterInformation.nodeInfoByRestAPIs.get(hostname);
			List<String> nodeList = new ArrayList<String>();
			nodeList.add(node.getRackName());
			nodeList.add(null);
			nodeList.add("status,resource");
			roleMaps.put(hostname, nodeList);
			//记录节点所在机柜
			String rackname = node.getRackName();
			List<String> rackList = rackLists.get(rackname);
			if(rackList == null) {
				rackList = new ArrayList<String>();
			}
			rackList.add(hostname);
			rackLists.put(rackname, rackList);
			//添加资源统计
			allocatedMemory.put(hostname, 0.0);
			allocatedCore.put(hostname, 0);
			double totalPhysMemBytes = Double.valueOf(node.getTotalPhysMemBytes());
			totalMemory.put(hostname, totalPhysMemBytes / 1024 / 1024);
			totalCore.put(hostname, node.getCpu());
		}
		totalServiceRole = 3;
	}
	
	protected void putServiceRoleInfoToRoleMap() {
		for(Iterator<String> servicenames = ClusterInformation.serviceInfoByRestAPIs.keySet().iterator(); servicenames.hasNext(); ) {
			String servicename = servicenames.next();
			if(servicename.equals("transwarp_license_cluster") || servicename.equals("Transpedia1")) {
				continue;
			}
			ServiceBean service = ClusterInformation.serviceInfoByRestAPIs.get(servicename);
			List<RoleBean> roles = service.getRoles();
			boolean hasMetastore = false;
			for(RoleBean role : roles) {
				//查询行号
				String roleType = role.getRoleType();
				if(roleType.indexOf("WORKER") != -1) {
					continue;
				}
				int id = addRoleTopic(servicename, roleType);
				//查询所在节点
				NodeBean node = role.getNode();
				String hostname = node.getHostName();
				//查询角色信息
				String resource = getResource(service.getType(), roleType, hostname);
				String roleInfo = role.getHealth() + resource;
				//添加信息到指定节点列表
				addRoleInfoToNodeList(hostname, id, roleInfo);
				if(roleType.equals("INCEPTOR_METASTORE")) {
					hasMetastore = true;
				}
			}
			//若为inceptor或streamsql，则添加executor信息
			try {
				if(service.getType().equals("INCEPTOR_SQL")) {
					addExecutorInfo(servicename);
				}
				if(hasMetastore) {
					addMySqlInfo(servicename);
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		//添加节点的资源统计情况
		for(Iterator<String> hostnames = ClusterInformation.nodeInfoByRestAPIs.keySet().iterator(); hostnames.hasNext(); ) {
			String hostname = hostnames.next();
			int totalMemID = addRoleTopic("nodeResource", "Total Physics Memory");
			String totalMemValue = ",Mem : " + Constant.DECIMAL_FORMAT.format(totalMemory.get(hostname)) + " MB";
			addRoleInfoToNodeList(hostname, totalMemID, totalMemValue);
			int totalCoreID = addRoleTopic("nodeResource", "Total Physics Core");
			String totalCoreValue = ",Cpu : " + totalCore.get(hostname);
			addRoleInfoToNodeList(hostname, totalCoreID, totalCoreValue);
			int allocatedMemID = addRoleTopic("nodeResource", "Allocated Memory");
			String allocatedMemValue = ",Mem : " + Constant.DECIMAL_FORMAT.format(allocatedMemory.get(hostname)) + " MB";
			addRoleInfoToNodeList(hostname, allocatedMemID, allocatedMemValue);
			int allocatedCoreID = addRoleTopic("nodeResource", "Allocated Core");
			String allocatedCoreValue = ",Cpu : " + allocatedCore.get(hostname);
			addRoleInfoToNodeList(hostname, allocatedCoreID, allocatedCoreValue);
			
			//计算内存和cpu使用情况
			double memUsedPercent = allocatedMemory.get(hostname) / totalMemory.get(hostname) * 100;
			if(memUsedPercent > 65) {
				CheckInfos.memUsed.put(hostname, Constant.DECIMAL_FORMAT.format(memUsedPercent) + "%");
			}
			NodeBean node = ClusterInformation.nodeInfoByRestAPIs.get(hostname);
			double cpuUsedPercent = allocatedCore.get(hostname) / Double.valueOf(node.getNumCores()) * 100;
			if(cpuUsedPercent > 65) {
				CheckInfos.cpuUsed.put(hostname, Constant.DECIMAL_FORMAT.format(cpuUsedPercent) + "%");
			}
		}
		
	}
	
	private int addRoleTopic(String servicename, String roleType) {
		List<String> roleList = roleMaps.get("role");
		List<String> serviceList = roleMaps.get("service");
		int id = 0;
		for(; id < roleList.size(); id++) {
			String item = roleList.get(id);
			if(item != null && item.equals(roleType)) {
				String name = serviceList.get(id);
				if(name != null && name.equals(servicename)) {
					return id;
				}
			}
		}
		roleList.add(roleType);
		serviceList.add(servicename);
		totalServiceRole += 1;
		roleMaps.put("role", roleList);
		roleMaps.put("service", serviceList);
		return id;
	}
	
	private String getResource(String serviceType, String roleType, String hostname) {
		String topic = serviceType + ":" + roleType + ":" + hostname;
		String resource = ClusterInformation.roleResources.get(topic);
		if(resource == null) {
			resource = "";
		}
		return resource;
	}
	
	private void addRoleInfoToNodeList(String hostname, int id, String roleInfo) {
		List<String> nodeList = roleMaps.get(hostname);
		while(nodeList.size() <= id) {
			nodeList.add("");
		}
		nodeList.set(id, roleInfo);
		//记录该服务角色的占用资源
		if(roleInfo != null) {
			String[] items = roleInfo.split(",");
			double number = 1;
			double memory = 0;
			double core = 0;
			for(String item : items) {
				String value = UtilTool.getDouble(item);
				if(value.equals("")) {
					continue;
				}
				if(item.indexOf("number") != -1) {
					if(value.indexOf("/") != -1) {
						String[] nums = value.split("\\s*/\\s*");
						number = Double.valueOf(nums[0]) / Double.valueOf(nums[1]);
					}else {
						number = Integer.valueOf(value);
					}
				}else if(item.startsWith("memory")) {
					memory = Double.valueOf(value);
				}else if(item.startsWith("vcore")) {
					core = Double.valueOf(value);
				}
			}		
			memory = memory * number + allocatedMemory.get(hostname);
			allocatedMemory.put(hostname, memory);
			core += allocatedCore.get(hostname);
			allocatedCore.put(hostname, (int)core);
		}

	}
	
	private void addExecutorInfo(String servicename) {
		int id = addRoleTopic(servicename, "EXECUTOR");
		Map<String, String> executorInfo = ClusterInformation.executorInfos.get(servicename);
		if(executorInfo == null) {
			LOG.error("executor info is null, service is : " + servicename);
			return;
		}
		for(Iterator<String> hostnames = executorInfo.keySet().iterator(); hostnames.hasNext(); ) {
			String hostname = hostnames.next();
			addRoleInfoToNodeList(hostname, id, executorInfo.get(hostname));
		}
	}
	
	private void addMySqlInfo(String servicename) {
		int id = addRoleTopic(servicename, "MYSQL");
		for(Iterator<String> hostnames = ClusterInformation.mysqlHAInfo.keySet().iterator(); hostnames.hasNext(); ) {
			String hostname = hostnames.next();
			String value = ClusterInformation.mysqlHAInfo.get(hostname);
			if(value != null) {
				if(value.equals("1")) {
					addRoleInfoToNodeList(hostname, id, "active");
				}else if(value.equals("2")) {
					addRoleInfoToNodeList(hostname, id, "standby");
				}
			}
		}
	}
	
	protected List<List<String>> mergeNodeLists() {
		List<List<String>> mergeResult = new ArrayList<List<String>>();
		List<String> serviceList = roleMaps.get("service");
		for(int i = serviceList.size() - 1; i >= 0; i--) {
			if(i > 0 && serviceList.get(i).equals(serviceList.get(i - 1))) {
				serviceList.set(i, null);
			}
		}
		mergeResult.add(serviceList);
		mergeResult.add(roleMaps.get("role"));
		for(Iterator<String> racknames = rackLists.keySet().iterator(); racknames.hasNext(); ) {
			String rackname = racknames.next();
			List<String> topics = rackLists.get(rackname);
			for(String topic : topics) {
				List<String> nodeList = roleMaps.get(topic);
				boolean hasSame = false;
				for(List<String> item : mergeResult) {
					hasSame = comparedNodeList(item, nodeList);
					if(hasSame) {
						String hostname = item.get(1);
						hostname += "," + topic;
						item.set(1, hostname);
						break;
					}
				}
				if(!hasSame) {
					nodeList.set(1, topic);
					mergeResult.add(nodeList);
				}
			}
		}
		return mergeResult;
	}
	
	private boolean comparedNodeList(List<String> list1, List<String> list2) {
		int size1 = list1.size();
		int size2 = list2.size();
		int maxSize = size1 > size2 ? size1 : size2;
		for(int i = 0; i < maxSize; i++) {
			if(i == 1) {  //节点hostname一栏比较跳过
				continue;
			}
			String value1 = null;
			String value2 = null;
			if(i < size1) {
				value1 = list1.get(i);
			}
			if(value1 == null) {
				value1 = "";
			}
			if(i < size2) {
				value2 = list2.get(i);
			}
			if(value2 == null) {
				value2 = "";
			}
			if(!value1.equals(value2)) {
				return false;
			}
		}
		return true;
	}
	
	protected List<String[]> changeToForm(List<List<String>> mapOfColumn) {
		List<String[]> buffer = new ArrayList<String[]>();
		List<String[]> result = new ArrayList<String[]>();
		int sizeOfMap = mapOfColumn.size();
		int columnNumber = sizeOfMap * 2 - 2;  //每个节点列改为两列显示
		for(int row = 0; row < totalServiceRole; row++) {
			String[] line = createLine(columnNumber);
			for(int column = 0; column < sizeOfMap; column++) {
				List<String> items = mapOfColumn.get(column);
				String value = null;
				if(row < items.size()) {
					value = items.get(row);
				}
				if(row == 1 && column > 1) {   //节点hostname用代号
					roleMapOfNodes.put("node" + (column - 1), value);
					line[column * 2 - 2] = "node" + (column - 1);
					line[column * 2 - 1] = null;
				}else if(row > 1 && column > 1 && value != null) {  //节点上的角色信息分两列多行显示
					String[] roleInfos = value.split(",");
					if(roleInfos.length == 1) {
						line[column * 2 - 2] = value;
						line[column * 2 - 1] = "";
					}else {
						line[column * 2 - 2] = roleInfos[0];
						line[column * 2 - 1] = roleInfos[1];
						if(roleInfos.length > 2) {
							int diff = roleInfos.length - 2 - buffer.size();
							while(diff > 0) {
								buffer.add(createLine(columnNumber));
								diff -= 1;
							}
							for(int i = 0; i < roleInfos.length - 2; i++) {
								buffer.get(i)[column * 2 - 1] = roleInfos[i + 2];
							}
						}
					}
				}else if(column <= 1) {
					line[column] = value;
				}else if(row == 0) {
					line[column * 2 - 2] = value;
					line[column * 2 - 1] = null;
				}
			}
			result.add(line);
			if(buffer.size() > 0) {
				for(String[] bufLine : buffer) {
					result.add(bufLine);
				}
				buffer.clear();
			}
		}
		//合并机柜名称
		String[] line = result.get(0);
		for(int i = line.length - 2; i > 0; i -= 2) {
			if(i - 2 >= 0 && line[i] != null && line[i].equals(line[i - 2])) {
				line[i] = null;
			}
		}
		return result;
	}
	
	private String[] createLine(int length) {
		String[] line = new String[length];
		for(int i = 2; i < length; i++) {
			line[i] = "";
		}
		return line;
	}
	
}
