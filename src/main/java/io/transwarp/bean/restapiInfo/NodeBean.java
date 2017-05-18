package io.transwarp.bean.restapiInfo;

import io.transwarp.bean.restapiInfo.ServiceBean;
import io.transwarp.util.TypeChangeUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

public class NodeBean {

	private String id;						//节点ID
	private String hostName;				//节点hostname
	private String ipAddress;				//节点IP
	private String clusterId;				//所属集群编号
	private String clusterName;				//所属集群名称
	private String sshConfigId;				//ssh配置编号
	private String rackId;					//所属机柜编号
	private String rackName;				//所属机柜名称
	private String isManaged;				//是否为manager节点
	private String expectedConfigVersion;	//最近一次配置修改的时间戳
	private String lastHeartbeat;			//最近一次心跳的时间戳
	private String numCores;				//core的数量
	private String totalPhysMemBytes;		//总的物理空间大小
	private String mounts;					//硬盘挂载点
	private String status;					//节点状态
	private String cpu;						//cpu信息
	private String disk;					//磁盘信息
	private String osType;					//操作系统
	private String serverKey;				//机器码
	private List<ServiceBean> roles;			//包含的服务角色
	
	public NodeBean(String jsonString) throws Exception{
		this(JSONObject.fromObject(jsonString));
	}
	
	public NodeBean(JSONObject json) throws Exception{
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
			this.id = "";
		else
			this.id = id.toString();
	}
	public String getHostName() {
		return hostName;
	}
	public void setHostName(Object hostName) {
		if(hostName == null) 
			this.hostName = "";
		else
			this.hostName = hostName.toString();
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(Object ipAddress) {
		if(ipAddress == null) 
			this.ipAddress = "";
		else
			this.ipAddress = ipAddress.toString();
	}
	public String getClusterId() {
		return clusterId;
	}
	public void setClusterId(Object clusterId) {
		if(clusterId == null)
			this.clusterId = "";
		else
			this.clusterId = clusterId.toString();
	}
	public String getClusterName() {
		return clusterName;
	}
	public void setClusterName(Object clusterName) {
		if(clusterName == null)
			this.clusterName = "";
		else	
			this.clusterName = clusterName.toString();
	}
	public String getSshConfigId() {
		return sshConfigId;
	}
	public void setSshConfigId(Object sshConfigId) {
		if(sshConfigId == null)
			this.sshConfigId = "";
		else
			this.sshConfigId = sshConfigId.toString();
	}
	public String getRackId() {
		return rackId;
	}
	public void setRackId(Object rackId) {
		if(rackId == null)
			this.rackId = "";
		else
			this.rackId = rackId.toString();
	}
	public String getRackName() {
		return rackName;
	}
	public void setRackName(Object rackName) {
		if(rackName == null)
			this.rackName = "";
		else	
			this.rackName = rackName.toString();
	}
	public String getIsManaged() {
		return isManaged;
	}
	public void setIsManaged(Object isManaged) {
		if(isManaged == null)
			this.isManaged = "";
		else
			this.isManaged = isManaged.toString();
	}
	public String getExpectedConfigVersion() {
		return expectedConfigVersion;
	}
	public void setExpectedConfigVersion(Object expectedConfigVersion) {
		if(expectedConfigVersion == null)
			this.expectedConfigVersion = "";
		else
			this.expectedConfigVersion = expectedConfigVersion.toString();
	}
	public String getLastHeartbeat() {
		return lastHeartbeat;
	}
	public void setLastHeartbeat(Object lastHeartbeat) {
		if(lastHeartbeat == null)
			this.lastHeartbeat = "";
		else
			this.lastHeartbeat = lastHeartbeat.toString();
	}
	public String getNumCores() {
		return numCores;
	}
	public void setNumCores(Object numCores) {
		if(numCores == null)
			this.numCores = "";
		else
			this.numCores = numCores.toString();
	}
	public String getTotalPhysMemBytes() {
		return totalPhysMemBytes;
	}
	public void setTotalPhysMemBytes(Object totalPhysMemBytes) {
		if(totalPhysMemBytes == null) 
			this.totalPhysMemBytes = "";
		else
			this.totalPhysMemBytes = totalPhysMemBytes.toString();
	}
	public String getMounts() {
		return mounts;
	}
	public void setMounts(Object mounts) {
		if(mounts == null)
			this.mounts = "";
		else
			this.mounts = mounts.toString();
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(Object status) {
		if(status == null)
			this.status = "";
		else
			this.status = status.toString();
	}
	public String getCpu() {
		return cpu;
	}
	public void setCpu(Object cpu) {
		if(cpu == null)
			this.cpu = "";
		else
			this.cpu = cpu.toString();
	}
	public String getDisk() {
		return disk;
	}
	public void setDisk(Object disk) {
		if(disk == null)
			this.disk = "";
		else
			this.disk = disk.toString();
	}
	public String getOsType() {
		return osType;
	}
	public void setOsType(Object osType) {
		if(osType == null) 
			this.osType = "";
		else
			this.osType = osType.toString();
	}
	public String getServerKey() {
		return serverKey;
	}
	public void setServerKey(Object serverKey) {
		if(serverKey == null)
			this.serverKey = "";
		else
			this.serverKey = serverKey.toString();
	}
	public List<ServiceBean> getRoles() {
		return roles;
	}
	public void setRoles(Object roles) {
		this.roles = new ArrayList<ServiceBean>();
		if(roles != null) {
			try {
				List<String> roleJsons = TypeChangeUtil.changeJsonToList(roles.toString());
				for(String roleJson : roleJsons) {
					this.roles.add(new ServiceBean(roleJson));
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
}
