package io.transwarp.information;

import io.transwarp.bean.ConfigBean;
import io.transwarp.bean.TableBean;
import io.transwarp.bean.restapiInfo.LicenseBean;
import io.transwarp.bean.restapiInfo.MetricBean;
import io.transwarp.bean.restapiInfo.NodeBean;
import io.transwarp.bean.restapiInfo.ServiceBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ClusterInformation {
	
	protected static Map<String, NodeBean> nodeInfoByRestAPIs = new ConcurrentHashMap<String, NodeBean>();
	protected static Map<String, Map<String, String>> nodeInfoByShells = new ConcurrentHashMap<String, Map<String, String>>();
	
	protected static Map<String, ServiceBean> serviceInfoByRestAPIs = new ConcurrentHashMap<String, ServiceBean>();
	
	protected static Map<String, Map<String, ConfigBean>> configInfos = new ConcurrentHashMap<String, Map<String, ConfigBean>>();
	protected static AtomicInteger completedOfConfigCheck = new AtomicInteger(0);
	
	protected static Map<String, Map<String, String>> portInfos = new ConcurrentHashMap<String, Map<String, String>>();
	
	protected static Map<String, Map<String, String>> serviceProcessInfos = new ConcurrentHashMap<String, Map<String, String>>();
	
	protected static Map<String, Map<String, MetricBean>> metricByRestAPIs = new ConcurrentHashMap<String, Map<String, MetricBean>>();
	
	protected static Map<String, String> hdfsInfos = new ConcurrentHashMap<String, String>();
	
	protected static Stack<String> hdfsSpaceSizeInfos = new Stack<String>();
	
	protected static Map<String, String> mysqlHAInfo = new ConcurrentHashMap<String, String>();
	
	protected static Map<String, List<String>> editLogContinuity = new ConcurrentHashMap<String, List<String>>();
	
	protected static Map<String, String> fsimageIDAndTimestamp = new ConcurrentHashMap<String, String>();
	
	protected static Map<String, Vector<TableBean>> tableInfos = new ConcurrentHashMap<String, Vector<TableBean>>();
	
	protected static Map<String, Map<String, String>> executorInfos = new ConcurrentHashMap<String, Map<String, String>>();
	
	protected static Map<String, String> roleResources = new ConcurrentHashMap<String, String>();
	protected static AtomicInteger completedOfRoleResourceCheck = new AtomicInteger(0);
	
	protected static List<String[]> roleMapTable = new ArrayList<String[]>();
	protected static Map<String, String> roleMapOfNodes = new HashMap<String, String>();
	
	protected static LicenseBean licenseInfo;
	
	protected static int roleNumbers = 0;
	
	protected static String tdh_version;
	
	protected static Vector<String> errorInfos = new Vector<String>();
	
	public static void setTdhVersion(String version) {
		tdh_version = version;
	}

}
