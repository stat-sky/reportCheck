package io.transwarp.servlet.resource;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import io.transwarp.bean.restapiInfo.NodeBean;
import io.transwarp.connTool.MyThreadPool;
import io.transwarp.information.ClusterInformation;
import io.transwarp.information.Constant;
import io.transwarp.util.UtilTool;

import org.apache.log4j.Logger;

public class ExecutorCheckRunnable extends ClusterInformation implements Runnable{

	private static final Logger LOG = Logger.getLogger(ExecutorCheckRunnable.class);
	
	private String servicename;
	private Vector<String> datanodes;
	private Map<String, String> fileValue;
	Map<String, String> nodeList;
	
	public ExecutorCheckRunnable(String servicename, Vector<String> datanodes, Map<String, String> fileValue) {
		this.servicename = servicename;
		this.datanodes = datanodes;
		this.fileValue = fileValue;
	}
	
	@Override
	public void run() {
		LOG.info("begin calculate executor info");
		try {
			getNodeList();
			getExecutorInfos();
			MyThreadPool.threadSuccess();
			LOG.info("executor check of service : " + servicename + " is success");
		}catch(Exception e) {
			e.printStackTrace();
			MyThreadPool.threadFailure();
			LOG.error("executor check is error, service is : " + servicename);
		}
	}
	
	protected void getNodeList() throws Exception {
		nodeList = new HashMap<String, String>();
		String number_executors = fileValue.get("INCEPTOR_YARN_NUMBER_EXECUTORS");
		String number_workers = fileValue.get("INCEPTOR_YARN_NUMBER_WORKERS");
		String executor_list = fileValue.get("NGMR_YARN_EXECUTOR_LIST");
		if(number_workers.equals("-1")) {
			if(executor_list == null || executor_list.equals("")) {
				if(number_executors.equals("-1")) {  //每个计算节点一个executor
					addExecutorNumberToDatanodes(datanodes.toArray(), "1");
				}else {  //每个计算节点executor数：executor总数/计算节点数
					int nodeNumber = datanodes.size();
					addExecutorNumberToDatanodes(datanodes.toArray(), number_executors + "/" + nodeNumber);
				}
			}else {
				String[] hostnames = executor_list.split(",");
				if(number_executors.equals("-1")) {  //指定节点上有1个executor
					addExecutorNumberToDatanodes(hostnames, "1");
				}else {  //指定节点上executor数：executor总数/指定节点数
					int nodeNumber = hostnames.length;
					addExecutorNumberToDatanodes(hostnames, number_executors + "/" + nodeNumber);
				}
			}
		}else {
			if(number_executors.equals("-1")) {  //每个计算节点executor数：worker数/计算节点数
				int nodeNumber = datanodes.size();
				addExecutorNumberToDatanodes(datanodes.toArray(), number_workers + "/" + nodeNumber);
			}else {  //每个计算节点executor数：executor总数/worker总数
				addExecutorNumberToDatanodes(datanodes.toArray(), number_executors + "/" + number_workers);
			}
		}
	}
	
	private void addExecutorNumberToDatanodes(Object[] hostnames, String number) {
		for(Object hostname : hostnames) {
			nodeList.put(hostname.toString(), ",number : " + number);
		}
	}
	
	protected void getExecutorInfos() throws Exception {
		String scheduler_mode = fileValue.get("INCEPTOR_YARN_SCHEDULER_MODE");
		if(scheduler_mode.indexOf("Heterogeneous") != -1) {
			String mem_cpu_ratio = fileValue.get("INCEPTOR_YARN_EXECUTOR_MEM_CPU_RATIO");
			String used_resource_percent = fileValue.get("INCEPTOR_YARN_EXECUTOR_USED_RESOURCE_PERCENT");
			getExecutorResourceByRatio(mem_cpu_ratio, used_resource_percent);
		}else {
			String executor_cores = fileValue.get("INCEPTOR_YARN_EXECUTOR_CORES");
			String executor_memory = fileValue.get("INCEPTOR_YARN_EXECUTOR_MEMORY");
			getExecutorResourceByFixed(executor_cores, executor_memory);
		}
		ClusterInformation.executorInfos.put(servicename, nodeList);
	}
	
	private void getExecutorResourceByRatio(String mem_cpu_ratio, String used_resource_percent) throws Exception {
		double mem_cpu = Double.valueOf(mem_cpu_ratio);
		double used_resource = Double.valueOf(used_resource_percent);
		for(Iterator<String> hostnames = nodeList.keySet().iterator(); hostnames.hasNext(); ) {
			String hostname = hostnames.next();
			int totalCores = getTotalCores(hostname);
			int usedCore = (int)(totalCores * used_resource);
			double usedMemory = usedCore * mem_cpu * 1024;
			String resource = nodeList.get(hostname);
			resource += ",vcore : " + usedCore + ",memory : " + Constant.DECIMAL_FORMAT.format(usedMemory) + " MB";
			nodeList.put(hostname, resource);
		}
	}
	
	private int getTotalCores(String hostname) {
		//从yarn资源信息中获取总的core数目,资源格式为：,resource-memory:123,resource-cpu:123
		String topic = "YARN:YARN_NODEMANAGER" + ":" + hostname;
		String totalResource = ClusterInformation.roleResources.get(topic);
		String[] resourceItems = totalResource.split(",");
		for(String resourceItem : resourceItems) {
			String[] items = resourceItem.split(":");
			if(items.length < 2) {
				continue;
			}
			if(items[0].equals("resource-cpu")) {
				return Integer.valueOf(items[1]);
			}
		}
		NodeBean node = ClusterInformation.nodeInfoByRestAPIs.get(hostname);
		return Integer.valueOf(node.getNumCores());
	}
	
	private void getExecutorResourceByFixed(String executor_cores, String executor_memory) throws Exception {
		double cores = Double.valueOf(executor_cores);
		double memory = Double.valueOf(UtilTool.getInteger(executor_memory));
		for(Iterator<String> hostnames = nodeList.keySet().iterator(); hostnames.hasNext(); ) {
			String hostname = hostnames.next();
			String resource = nodeList.get(hostname);
			resource += ",vcore : " + cores + ",memory : " + Constant.DECIMAL_FORMAT.format(memory) + " MB";
			nodeList.put(hostname, resource);
		}
	}
}
