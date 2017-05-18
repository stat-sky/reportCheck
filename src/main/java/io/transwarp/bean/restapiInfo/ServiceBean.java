package io.transwarp.bean.restapiInfo;

import io.transwarp.util.TypeChangeUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

public class ServiceBean {

	private String id = null; 						//服务编号
	private String name = null; 					//服务名称
	private String dependencies = null; 			//服务依赖的服务编号
	private String status = null; 					//服务状态
	private String installed = null; 				//服务是否安装
	private String health = null;					//服务健康状态
	private String configStatus = null;				//配置状况
	private String enableKerberos = null;			//是否开启kerberos
	private String type = null;						//服务类型
	private String clusterId = null;				//集群编号
	private String clusterName = null;				//集群名称
	private List<RoleBean> roles = null;			//属于该服务的角色
	
	public ServiceBean(String jsonString) throws Exception {
		this(JSONObject.fromObject(jsonString));
	}
	
	public ServiceBean(JSONObject json) throws Exception {
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
	
	public String getId() {
		return id;
	}
	public void setId(Object id) {
		if(id == null)
			id = "";
		this.id = id.toString();
	}
	public String getName() {
		return name;
	}
	public void setName(Object name) {
		if(name == null)
			name = "";
		this.name = name.toString();
	}
	public String getDependencies() {
		return dependencies;
	}
	public void setDependencies(Object dependencies) {
		if(dependencies == null)
			dependencies = "";
		this.dependencies = dependencies.toString();
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(Object status) {
		if(status == null)
			status = "";
		this.status = status.toString();
	}
	public String getInstalled() {
		return installed;
	}
	public void setInstalled(Object installed) {
		if(installed == null)
			installed = "";
		this.installed = installed.toString();
	}
	public String getHealth() {
		return health;
	}
	public void setHealth(Object health) {
		if(health == null)
			health = "";
		this.health = health.toString();
	}
	public String getConfigStatus() {
		return configStatus;
	}
	public void setConfigStatus(Object configStatus) {
		if(configStatus == null)
			configStatus = "";
		this.configStatus = configStatus.toString();
	}
	public String getEnableKerberos() {
		return enableKerberos;
	}
	public void setEnableKerberos(Object enableKerberos) {
		if(enableKerberos == null)
			enableKerberos = "";
		this.enableKerberos = enableKerberos.toString();
	}
	public String getType() {
		return type;
	}
	public void setType(Object type) {
		if(type == null)
			type = "";
		this.type = type.toString();
	}
	public String getClusterId() {
		return clusterId;
	}
	public void setClusterId(Object clusterId) {
		if(clusterId == null)
			clusterId = "";
		this.clusterId = clusterId.toString();
	}
	public String getClusterName() {
		return clusterName;
	}
	public void setClusterName(Object clusterName) {
		if(clusterName == null)
			clusterName = "";
		this.clusterName = clusterName.toString();
	}
	public List<RoleBean> getRoles() {
		return roles;
	}
	public void setRoles(Object roles) {
		this.roles = new ArrayList<RoleBean>();
		if(roles != null) { 
			try {
				List<String> roleJsons = TypeChangeUtil.changeJsonToList(roles.toString());
				for(String roleJson : roleJsons) {
					this.roles.add(new RoleBean(roleJson));
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	public void addRole(RoleBean role) {
		this.roles.add(role);
	}
	
}
