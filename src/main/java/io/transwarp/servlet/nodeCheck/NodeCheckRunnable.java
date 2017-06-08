package io.transwarp.servlet.nodeCheck;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.transwarp.bean.restapiInfo.NodeBean;
import io.transwarp.connTool.MyThreadPool;
import io.transwarp.connTool.ShellUtil;
import io.transwarp.information.ClusterInformation;
import io.transwarp.information.PropertiesInfo;

import org.apache.log4j.Logger;
import org.dom4j.Element;

public class NodeCheckRunnable extends ClusterInformation implements Runnable{

	private static final Logger LOG = Logger.getLogger(NodeCheckRunnable.class);
	
	private NodeBean node;
	
	public NodeCheckRunnable(NodeBean node) {
		this.node = node;
	}
	
	@Override
	public void run() {
		LOG.info("begin check node : " + node.getHostName());
		try {
			nodeBaseCheck();
			portCheck();
			MyThreadPool.threadSuccess();
			LOG.info("node " + node.getHostName() + " check is success");
		}catch(Exception e) {
			e.printStackTrace();
			MyThreadPool.threadFailure();
			ClusterInformation.errorInfos.add("node check of " + node.getHostName() + " error|" + e.getMessage());
			LOG.error("node info check error, hostname is : " + node.getHostName());
		}
	}
	
	protected void nodeBaseCheck() throws Exception {
		Map<String, String> nodeInfo = new HashMap<String, String>();
		List<Element> checkConfigs = PropertiesInfo.prop_nodeCheckOfShell.getAll();
		for(Element checkConfig : checkConfigs) {
			Element properties = checkConfig.element("properties");
			List<?> checkItems = properties.elements();
			for(Object checkItem : checkItems) {
				Element item = (Element)checkItem;
				String command = item.elementText("command");
				String itemName = item.elementText("parameter");
				try {
					String checkResult = ShellUtil.executeDist(command, node.getIpAddress());
					nodeInfo.put(itemName, checkResult);
				}catch(Exception e) {
					e.printStackTrace();
					LOG.error("check item error, item is : " + itemName + ", command is " + command);
				}
			}
		}
		if(nodeInfo.size() == 0) {
			throw new RuntimeException("this node " + node.getHostName() + " check is failure");
		}
		ClusterInformation.nodeInfoByShells.put(node.getHostName(), nodeInfo);
	}
	
	protected void portCheck() throws Exception { 
		Map<String, String> portInfo = new HashMap<String, String>();
		List<Element> checkConfigs = PropertiesInfo.prop_port.getAll();
		for(Element checkConfig : checkConfigs) {
			String topic = checkConfig.elementText("topic");
			String command = checkConfig.elementText("command");
			try {
				String result = ShellUtil.executeDist(command, node.getIpAddress());
				portInfo.put(topic, result);
			}catch(Exception e) {
				e.printStackTrace();
				LOG.error("check port error, topic " + topic);
			}
		}
		if(portInfo.size() == 0) {
			throw new RuntimeException("this port check of node : " + node.getHostName() + " is failure");
		}
		ClusterInformation.portInfos.put(node.getHostName(), portInfo);
	}
}
