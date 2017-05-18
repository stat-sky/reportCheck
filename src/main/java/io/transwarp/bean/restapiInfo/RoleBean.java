package io.transwarp.bean.restapiInfo;

import io.transwarp.util.TypeChangeUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.sf.json.JSONObject;

public class RoleBean {
	
	private String roleId = null;			//角色编号
	private String name = null;				//角色名称
	private String roleType = null;			//角色类型
	private String status = null;			//角色状态
	private String health = null;			//健康状态
	private NodeBean node = null;			//所在节点
	private ServiceBean service = null;		//所属服务
	private String resource = null;			//角色资源

	public RoleBean(String jsonString) throws Exception {
		this(JSONObject.fromObject(jsonString));
	}
	
	public RoleBean(JSONObject json) throws Exception {
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
				if(name.equalsIgnoreCase("service")) {
					json.put(name, ((ServiceBean)value).getName());
				}else {
					json.put(name, value.toString());
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json.toString();
	}
	
	public String getRoleId() {
		return roleId;
	}
	public void setRoleId(Object roleId) {
		if(roleId == null)
			roleId = "";
		this.roleId = roleId.toString();
	}
	public String getName() {
		return name;
	}
	public void setName(Object name) {
		if(name == null)
			name = "";
		this.name = name.toString();
	}
	public String getRoleType() {
		return roleType;
	}
	public void setRoleType(Object roleType) {
		if(roleType == null)
			roleType = "";
		this.roleType = roleType.toString();
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(Object status) {
		if(status == null)
			status = "";
		this.status = status.toString();
	}
	public String getHealth() {
		return health;
	}
	public void setHealth(Object health) {
		if(health == null)
			health = "";
		this.health = health.toString();
	}
	public NodeBean getNode() {
		return node;
	}
	public void setNode(Object node) {
		if(node == null) 
			node = "{}";
		try {
			this.node = new NodeBean(node.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public ServiceBean getService() {
		return service;
	}
	public void setService(Object service) {
		if(service == null)
			service = "{}";
		try {
			this.service = new ServiceBean(service.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public String getResource() {
		return resource;
	}
	public void setResource(Object resource) {
		if(resource == null)
			resource = "";
		this.resource = resource.toString();
	}
}
