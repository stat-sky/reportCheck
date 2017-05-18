package io.transwarp.bean.restapiInfo;

import io.transwarp.util.TypeChangeUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

public class LicenseBean {

	private String clusterSize;
	private String supportStartDay;
	private String serverKey;
	private String serialNumber;
	List<ComponentBean> components;
	
	public LicenseBean(String json) {
		this(JSONObject.fromObject(json));
	}
	
	public LicenseBean(JSONObject json) {
		Class<?> clazz = this.getClass();
		Field[] fields = clazz.getDeclaredFields();
		for(Field field : fields) {
			String variableName = field.getName();
			Object variableValue = json.get(variableName);
			putThisVariableToClass(variableName, variableValue);
		}
	}
	
	private void putThisVariableToClass(String variableName, Object variableValue) {
		Class<?> clazz = this.getClass();
		try {
			Method setMethod = clazz.getDeclaredMethod("set" + TypeChangeUtil.changeFirstCharToCapital(variableName), Object.class);
			setMethod.invoke(this, new Object[]{variableValue});
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String toString() {
		JSONObject json = new JSONObject();
		Class<?> clazz = this.getClass();
		Field[] fields = clazz.getDeclaredFields();
		for(Field field : fields) {
			String name = field.getName();
			try {
				Method getMethod = clazz.getDeclaredMethod("get" + TypeChangeUtil.changeFirstCharToCapital(name));
				Object value = getMethod.invoke(this, new Object[]{});
				if(value == null) continue;
				json.put(name, value.toString());
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json.toString();
	}
	
	public void setClusterSize(Object clusterSize) {
		if(clusterSize != null) {
			this.clusterSize = clusterSize.toString();
		}else {
			this.clusterSize = "";
		}
	}
	
	public String getClusterSize() {
		return this.clusterSize;
	}
	
	public void setSupportStartDay(Object supportStartDay) {
		if(supportStartDay != null) {
			this.supportStartDay = supportStartDay.toString();
		}else {
			this.supportStartDay = "";
		}
	}
	
	public String getSupportStartDay() {
		return this.supportStartDay;
	}
	
	public void setComponents(Object components) throws Exception {
		this.components = new ArrayList<ComponentBean>();
		if(components != null) {
			List<String> componentJsons = TypeChangeUtil.changeJsonToList(components.toString());
			for(String componentJson : componentJsons) {
				this.components.add(new ComponentBean(componentJson));
			}
		}
	}
	
	public List<ComponentBean> getComponents() {
		return this.components;
	}
	
	public void setServerKey(Object serverKey) {
		if(serverKey != null) {
			this.serverKey = serverKey.toString();
		}else {
			this.serverKey = "";
		}
	}
	
	public String getServerKey() {
		return this.serverKey;
	}
	
	public void setSerialNumber(Object serialNumber) {
		if(serialNumber != null) {
			this.serialNumber = serialNumber.toString();
		}else {
			this.serialNumber = "";
		}
	}
	
	public String getSerialNumber() {
		return this.serialNumber;
	}
}
