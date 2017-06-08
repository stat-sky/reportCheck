package io.transwarp.servlet.resource;

import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import io.transwarp.bean.ConfigBean;
import io.transwarp.bean.restapiInfo.NodeBean;
import io.transwarp.bean.restapiInfo.RoleBean;
import io.transwarp.connTool.MyThreadPool;
import io.transwarp.information.ClusterInformation;
import io.transwarp.information.PropertiesInfo;
import io.transwarp.util.UtilTool;

public class ResourceCheckRunnable extends ClusterInformation implements Runnable{
	
	private static final Logger LOG = Logger.getLogger(ResourceCheckRunnable.class);
	
	private RoleBean role;
	private String serviceType;
	private ConfigBean configBean;
	
	public ResourceCheckRunnable(RoleBean role, String serviceType, ConfigBean configBean) {
		this.role = role;
		this.serviceType = serviceType;
		this.configBean = configBean;
	}
	
	@Override
	public void run() {
		LOG.info("begin get resource of service role : " + role.getName());
		try {
			getResource();
			MyThreadPool.threadSuccess();
			LOG.info("role resource check of role : " + role.getName() + " is success");
		}catch(Exception e) {
			e.printStackTrace();
			MyThreadPool.threadFailure();
			ClusterInformation.errorInfos.add("get resource of serviceType : " + serviceType + " is error|" + e.getMessage());
			LOG.error("role resource check is error, role is : " + role.getName());
		}finally {
			ClusterInformation.completedOfRoleResourceCheck.incrementAndGet();
		}
	}

	protected void getResource() throws Exception {
		StringBuffer roleResource = new StringBuffer();
		String topic = serviceType + ":" + role.getRoleType();
		Element config;
		try {
			config = PropertiesInfo.prop_resource.getElementByKeyValue("topic", topic);
		}catch(Exception e) {
			LOG.error(e.getMessage());
			return;
		}
		String resourceItem = config.elementText("item");
		String[] items = resourceItem.split(";");
		for(String item : items) {
			String[] configItems = item.split(":");
			if(configItems.length != 3) {
				LOG.error("resource config error of topic : " + topic + ", item : " + item);
				continue;
			}
			Map<String, String> configFileValue;
			try {
				configFileValue = configBean.getConfigFileValue(configItems[1]);
			}catch(RuntimeException e) {
				LOG.error(e.getMessage());
				continue;
			}
			String value = configFileValue.get(configItems[2]);
			if(configItems[0].equals("memory") && value != null) {
				value = getMemory(value);
			}
			if(value != null) {
				roleResource.append(",").append(configItems[0]).append(":").append(value);
				
			}
		}
		NodeBean node = role.getNode();
		ClusterInformation.roleResources.put(topic + ":" + node.getHostName(), roleResource.toString());
	}
	
	private String getMemory(String value) {
		String[] items = value.split(" ");
		if(items.length == 1) {
			return UtilTool.getInteger(value).replaceAll("-", "");
		}else {
			for(String item : items) {
				if(item.indexOf("-Xmx") != -1) {
					return UtilTool.getInteger(item).replaceAll("-", "");
				}
			}
		}
		return null;
	}
}
