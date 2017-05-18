package io.transwarp.information;

import io.transwarp.bean.CheckItemBean;
import io.transwarp.util.ReadXmlUtil;

import java.util.Properties;

public class PropertiesInfo {

	public static Properties prop_env = new Properties();
	
	public static ReadXmlUtil prop_restapi;
	
	public static ReadXmlUtil prop_logCheck;
	
	public static ReadXmlUtil prop_process;
	
	public static ReadXmlUtil prop_port;
	
	public static ReadXmlUtil prop_nodeCheckOfShell;
	
	public static ReadXmlUtil prop_serviceConfig;
	
	public static ReadXmlUtil prop_cluster;
	
	public static ReadXmlUtil prop_metric;
	
	public static ReadXmlUtil prop_resource;
	
	public static CheckItemBean checkItems;
}
