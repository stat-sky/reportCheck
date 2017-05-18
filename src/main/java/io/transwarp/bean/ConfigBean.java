package io.transwarp.bean;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

public class ConfigBean {

	private String serviceName;
	private Map<String, Map<String, String>> configFileValues;
	
	public ConfigBean(String serviceName) {
		this.serviceName = serviceName;
		this.configFileValues = new HashMap<String, Map<String, String>>();
	}
	
	public String getServiceName() {
		return serviceName;
	}
	
	public Map<String, String> getConfigFileValue(String fileName) {
		Map<String, String> fileValue = this.configFileValues.get(fileName);
		if(fileValue == null) {
			throw new RuntimeException("config file : " + fileName + " is no find");
		}
		return fileValue;
	}
	
	public Map<String, Map<String, String>> getAllFiles() {
		return configFileValues;
	}
	
	public void addConfigFile(String fileName, Map<String, String> configValue) {
		this.configFileValues.put(fileName, configValue);
	}
	
	@Override
	public String toString() {
		JSONObject json = new JSONObject();
		json.putAll(configFileValues);
		json.put("serviceName", serviceName);
		return json.toString();
	}
}
