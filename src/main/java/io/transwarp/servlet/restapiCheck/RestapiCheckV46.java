package io.transwarp.servlet.restapiCheck;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import io.transwarp.bean.LoginInfoBean;
import io.transwarp.bean.restapiInfo.LicenseBean;
import io.transwarp.bean.restapiInfo.MetricBean;
import io.transwarp.bean.restapiInfo.NodeBean;
import io.transwarp.bean.restapiInfo.RoleBean;
import io.transwarp.bean.restapiInfo.ServiceBean;
import io.transwarp.connTool.RestApiUtil;
import io.transwarp.information.ClusterInformation;
import io.transwarp.information.Constant;
import io.transwarp.information.PropertiesInfo;
import io.transwarp.util.UtilTool;

public class RestapiCheckV46 extends ClusterInformation implements RestapiCheckImpl{
	
	private static final Logger LOG = Logger.getLogger(RestapiCheckV46.class);
	
	protected RestApiUtil method;
	protected LoginInfoBean loginInfo;
	protected String[] getInfoItems = new String[]{"node", "service", "serviceRole", "metric", "version", "license"};

	public RestapiCheckV46(LoginInfoBean loginInfo) {
		this.loginInfo = loginInfo;
	}
	
	@Override
	public void getInfoByRestAPI() {
		LOG.info("begin get cluster information by rest api");
		try {
			method = RestApiUtil.getRestApiUtil(loginInfo);
			if(method == null) {
				LOG.error("get rest api error");
				System.exit(1);
			}
			for(String getInfoItem : getInfoItems) {
				LOG.info("begin check " + getInfoItem);
				try {
					getInfoChoose(getInfoItem);
				}catch(Exception e) {
					LOG.error("get info of " + getInfoItem + " error");
					e.printStackTrace();
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			if(method != null) {
				try {
					method.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	protected void getInfoChoose(String item) throws Exception {
		if(item.equals("node")) {
			getNodeInfo();
		}else if(item.equals("service")) {
			getServiceInfo();
		}else if(item.equals("serviceRole")) {
			getServiceRoleInfo();
		}else if(item.equals("metric")) {
			getMetricsInfo();
		}else if(item.equals("version")) {
			getTdhVersion();
		}else if(item.equals("license")) {
			getTdhLicenseInfo();
		}else {
			throw new RuntimeException("no find this item");
		}
	}
	
	protected void getNodeInfo() throws Exception {
		Element config = PropertiesInfo.prop_restapi.getElementByKeyValue("purpose", Constant.FIND_MORE_NODE);
		Map<String, Object> urlParam = new HashMap<String, Object>();
		urlParam.put("viewType", "summary");
		String url = UtilTool.buildURL(config.elementText("url"), urlParam);
		String nodeInfoOfJson = method.executeRestApi(url, config.elementText("http-method"), null);
		
		JSONArray nodeArray = JSONArray.fromObject(nodeInfoOfJson);
		for(int i = 0; i < nodeArray.size(); i++) {
			Object value = nodeArray.get(i);
			NodeBean node = new NodeBean(value.toString());
			ClusterInformation.nodeInfoByRestAPIs.put(node.getHostName(), node);
		}
	}
	
	protected void getServiceInfo() throws Exception {
		Element config = PropertiesInfo.prop_restapi.getElementByKeyValue("purpose", Constant.FIND_MORE_SERVICE);
		Map<String, Object> urlParam = new HashMap<String, Object>();
		String url = UtilTool.buildURL(config.elementText("url"), urlParam);
		String serviceInfoOfJson = method.executeRestApi(url, config.elementText("http-method"), null);
		
		JSONArray serviceArray = JSONArray.fromObject(serviceInfoOfJson);
		for(int i = 0; i < serviceArray.size(); i++) {
			Object value = serviceArray.get(i);
			ServiceBean service = new ServiceBean(value.toString());
			ClusterInformation.serviceInfoByRestAPIs.put(service.getName(), service);
		}
	}
	
	protected void getServiceRoleInfo() throws Exception {
		Element config = PropertiesInfo.prop_restapi.getElementByKeyValue("purpose", Constant.FIND_MORE_SERVICE_ROLE);
		Map<String, Object> urlParam = new HashMap<String, Object>();
		String url = UtilTool.buildURL(config.elementText("url"), urlParam);
		String roleInfoOfJson = method.executeRestApi(url, config.elementText("http-method"), null);
		
		JSONArray roleArray = JSONArray.fromObject(roleInfoOfJson);
		for(int i = 0; i < roleArray.size(); i++) {
			Object value = roleArray.get(i);
			RoleBean role = new RoleBean(value.toString());
			ServiceBean service = role.getService();
			if(service != null) {
				ServiceBean hasService = ClusterInformation.serviceInfoByRestAPIs.get(service.getName());
				if(hasService == null) {
					hasService = role.getService();
				}
				hasService.addRole(role);
				ClusterInformation.serviceInfoByRestAPIs.put(hasService.getName(), hasService);
			}
			ClusterInformation.roleNumbers += 1;
		}
	}
	
	protected void getMetricsInfo() throws Exception {
		Element config = PropertiesInfo.prop_restapi.getElementByKeyValue("purpose", Constant.NODE_METRIC);
		long metricRange = Long.valueOf(PropertiesInfo.prop_env.getProperty("metricRange", "24")) * 60 * 60 * 1000;
		long endTime = System.currentTimeMillis();
		long startTime = endTime - metricRange;
		Map<String, Object> urlParam = new HashMap<String, Object>();
		urlParam.put("startTimeStamp", startTime);
		urlParam.put("endTimeStamp", endTime);
		List<Element> metricItems = PropertiesInfo.prop_metric.getAll();
		for(Iterator<String> hostnames = ClusterInformation.nodeInfoByRestAPIs.keySet().iterator(); hostnames.hasNext(); ) {
			String hostname = hostnames.next();
			NodeBean node = ClusterInformation.nodeInfoByRestAPIs.get(hostname);
			urlParam.put("nodeId", node.getId());
			Map<String, MetricBean> metricValue = getMetricFromNode(metricItems, config.elementText("url"), urlParam, config.elementText("http-method"));
			ClusterInformation.metricByRestAPIs.put(hostname, metricValue);
		}
		
	}
	
	private Map<String, MetricBean> getMetricFromNode(List<Element> metricItems, 
							String originalURL, Map<String, Object> urlParam, String httpMethod) throws Exception {
		Map<String, MetricBean> metricValue = new HashMap<String, MetricBean>();
		for(Element metricItem : metricItems) {
			String metricName = metricItem.elementText("metric");
			urlParam.put("metricsName", metricName);
			String url = UtilTool.buildURL(originalURL, urlParam);
			String metricOfJson = method.executeRestApi(url, httpMethod, null);
			MetricBean metric = analysisMetric(metricOfJson, metricItem.elementText("name"));
			metricValue.put(metricName, metric);
		}
		return metricValue;
	}
	
	private MetricBean analysisMetric(String metricOfJson, String name) {
		MetricBean metric = new MetricBean();
		
		JSONArray array = JSONArray.fromObject(metricOfJson);
		for(int i = 0; i < array.size(); i++) {
			JSONObject item = array.getJSONObject(i);
			String unit = item.getString("unit");
			String timestamp = item.getString("timestamp");
			JSONObject values = item.getJSONObject("metricValue");
			String value = values.getString("value");
			metric.setMetricName(name);
			metric.setUnit(unit);
			metric.addMetricValue(timestamp + ":" + value); 
		}
		return metric;
	}
	
	protected void getTdhVersion() throws Exception {
		String versionOnJson = method.executeRestApi("/manager/version", "get", null);
		
		JSONObject versionOfObject = JSONObject.fromObject(versionOnJson);
		String version = versionOfObject.getString("version");
		if(version != null) {
			ClusterInformation.tdh_version = version;
		}
	}
	
	protected void getTdhLicenseInfo() throws Exception {
		String licenseOnJson = method.executeRestApi("/manager/license", "get", null);
		if(licenseOnJson != null) {
			ClusterInformation.licenseInfo = new LicenseBean(licenseOnJson);
		}
	}
}
