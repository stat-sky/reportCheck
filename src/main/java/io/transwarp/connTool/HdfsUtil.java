package io.transwarp.connTool;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;

import io.transwarp.bean.LoginInfoBean;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.log4j.Logger;

public class HdfsUtil {

	private static final Logger LOG = Logger.getLogger(HdfsUtil.class);
	
	
	public static FileSystem getHdfsFileSystem(String[] configPaths, String ipAddress, LoginInfoBean loginInfo) throws Exception {
		Configuration configuration = loadConfiguration(configPaths);
		String enableKerberos = loginInfo.getEnableKerberos();
		if(enableKerberos.equals("true")) {
			configuration = certificateSafety(configuration, loginInfo);
			return FileSystem.get(configuration);
		}
		return FileSystem.get(URI.create("hdfs://" + ipAddress + ":8020"), configuration, loginInfo.getHdfsUser());
	}
	
	private static Configuration loadConfiguration(String[] configPaths) throws Exception {
//		LOG.info("load hdfs config file");
		Configuration configuration = new Configuration();
		for(String configPath : configPaths) {
			File file = new File(configPath);
			if(!file.exists())
				throw new RuntimeException("the file : " + configPath + " is no find");
			InputStream fileInput = new FileInputStream(file);
			configuration.addResource(fileInput);
		}
		return configuration;
	}
	
	private static Configuration certificateSafety(Configuration configuration, LoginInfoBean loginInfo) throws Exception{
		UserGroupInformation.setConfiguration(configuration);
		UserGroupInformation.loginUserFromKeytab(loginInfo.getHdfsUser(), loginInfo.getHdfsKeytab());
		return configuration;
	}
	
	public static String getHdfsPath(String path) {
		if(path.indexOf("hdfs://") == -1) {
			return path;
		}else {
			LOG.debug("get file path of hdfs : " + path);
			StringBuffer truePath = new StringBuffer();
			String[] items = path.split("/");
			for(int i = 3; i < items.length; i++) {
				truePath.append("/").append(items[i]);
			}
			if(truePath.length() == 0)
				truePath.append("/");
			return truePath.toString();
		}
	}
	
	/*public static void main(String[] args) {
		String[] paths = new String[]{"/etc/hdfs1/conf/hdfs-site.xml", "/etc/hdfs1/conf/core-site.xml"};
		try {
			LoginInfoBean loginInfo = new LoginInfoBean();
			loginInfo.setSecurityType("simple");
			loginInfo.setUsername("hdfs");
			FileSystem fs = HdfsUtil.getHdfsFileSystem(paths, "172.16.1.109", loginInfo);
			FileStatus[] list = fs.listStatus(new Path("/"));
			for(FileStatus file : list) {
				System.out.println(HdfsUtil.getHdfsPath(file.getPath().toString()));
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}*/
}
