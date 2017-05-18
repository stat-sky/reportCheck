package io.transwarp.bean.restapiInfo;

import io.transwarp.util.TypeChangeUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.sf.json.JSONObject;

public class ComponentBean {

	private String compType;
	private String compTypeFriendly;
	private String compSize;
	private String licenseType;
	private String expiredDate;
	private String supportExpiration;
	
	public ComponentBean(String json) {
		this(JSONObject.fromObject(json));
	}
	
	public ComponentBean(JSONObject json) {
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
	
	public void setCompType(Object compType) {
		if(compType != null) {
			this.compType = compType.toString();
		}else {
			this.compType = "";
		}
	}
	
	public String getCompType() {
		return this.compType;
	}
	
	public void setCompTypeFriendly(Object compTypeFriendly) {
		if(compTypeFriendly != null) {
			this.compTypeFriendly = compTypeFriendly.toString();
		}else {
			this.compTypeFriendly = "";
		}
	}
	
	public String getCompTypeFriendly() {
		return this.compTypeFriendly;
	}
	
	public void setCompSize(Object compSize) {
		if(compSize != null) {
			this.compSize = compSize.toString();
		}else {
			this.compSize = "";
		}
	}
	
	public String getCompSize() {
		return this.compSize;
	}
	
	public void setLicenseType(Object licenseType) {
		if(licenseType != null) {
			this.licenseType = licenseType.toString();
		}else {
			this.licenseType = "";
		}
	}
	
	public String getLicenseType() {
		return this.licenseType;
	}
	
	public void setExpiredDate(Object expiredDate) {
		if(expiredDate != null) {
			this.expiredDate = expiredDate.toString();
		}else {
			this.expiredDate = "";
		}
	}
	
	public String getExpiredDate() {
		return this.expiredDate;
	}
	
	public void setSupportExpiration(Object supportExpiration) {
		if(supportExpiration != null) {
			this.supportExpiration = supportExpiration.toString();
		}else {
			this.supportExpiration = "";
		}
	}
	
	public String getSupportExpiration() {
		return this.supportExpiration;
	}
}
