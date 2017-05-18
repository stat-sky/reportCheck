package io.transwarp.bean;

public class LoginInfoBean {
	
	private String managerUrl;
	private String jdbcIP;
	private String enableKerberos;
	private String managerUser;
	private String hdfsUser;
	private String managerPwd;
	private String hdfsKeytab;
	
	public LoginInfoBean() {
		super();
	}

	public String getManagerUrl() {
		return managerUrl;
	}

	public void setManagerUrl(String managerUrl) {
		this.managerUrl = managerUrl;
	}

	public String getEnableKerberos() {
		return this.enableKerberos;
	}
	
	public void setEnableKerberos(String enableKerberos) {
		this.enableKerberos = enableKerberos;
	}

	public String getManagerUser() {
		return managerUser;
	}

	public void setManagerUser(String managerUser) {
		this.managerUser = managerUser;
	}

	public String getHdfsUser() {
		return hdfsUser;
	}

	public void setHdfsUser(String hdfsUser) {
		this.hdfsUser = hdfsUser;
	}

	public String getManagerPwd() {
		return managerPwd;
	}

	public void setManagerPwd(String managerPwd) {
		this.managerPwd = managerPwd;
	}
	public String getHdfsKeytab() {
		return hdfsKeytab;
	}

	public void setHdfsKeytab(String hdfsKeytab) {
		this.hdfsKeytab = hdfsKeytab;
	}

	public String getJdbcIP() {
		return jdbcIP;
	}

	public void setJdbcIP(String jdbcIP) {
		this.jdbcIP = jdbcIP;
	}
	
}
