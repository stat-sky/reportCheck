package io.transwarp.connTool;

import java.sql.Connection;
import java.sql.DriverManager;

public class JDBCUtil {
	
	public static void initJDBCUtil(String className) {
		try {
			Class.forName(className);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Connection getConnection(String url, String username, String password) throws Exception {
		return DriverManager.getConnection(url, username, password);
	}
	/*
	public static Connection getConnection(String url, LoginInfoBean loginInfo) throws Exception {
		String securityType = loginInfo.getSecurityType();
		if(securityType.equals("simple")) {
			return DriverManager.getConnection(url);
		}else if(securityType.equals("kerberos")) {
			return getConnectionByKerberos(url, loginInfo);
		}else {
			return getConnectionByLdap(url, loginInfo);
		}
	}
	
	public static Connection getConnectionByKerberos(String url, LoginInfoBean loginInfo) throws Exception {
		StringBuffer urlBuild = new StringBuffer(url);
		urlBuild.append(";principal=").append(loginInfo.getPrincipal())
				.append(";authentication=kerberos")
				.append(";kuser=").append(loginInfo.getJdbcUser())
				.append(";keytab=").append(loginInfo.getJdbcKeytab())
				.append(";krb5conf=").append(loginInfo.getKrb5conf());
		return DriverManager.getConnection(urlBuild.toString());
	}
	
	public static Connection getConnectionByLdap(String url, LoginInfoBean loginInfo) throws Exception {
		return DriverManager.getConnection(url, loginInfo.getJdbcUser(), loginInfo.getJdbcPwd());
	}
*/
	
}
