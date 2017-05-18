package io.transwarp.servlet.restapiCheck;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import io.transwarp.bean.LoginInfoBean;
import io.transwarp.bean.restapiInfo.RoleBean;
import io.transwarp.bean.restapiInfo.ServiceBean;
import io.transwarp.information.ClusterInformation;
import io.transwarp.information.Constant;
import io.transwarp.information.PropertiesInfo;
import io.transwarp.util.UtilTool;
import net.sf.json.JSONArray;

//import org.apache.log4j.Logger;
import org.dom4j.Element;

public class RestapiCheckV45 extends RestapiCheckV46{

//	private static final Logger LOG = Logger.getLogger(RestapiCheckRunnableV45.class);
	
	public RestapiCheckV45(LoginInfoBean loginInfo) {
		super(loginInfo);
	}
	
	@Override
	protected void getServiceRoleInfo() throws Exception {
		Element config = PropertiesInfo.prop_restapi.getElementByKeyValue("purpose", Constant.FIND_MORE_SERVICE_ROLE);
		String originalURL = config.elementText("url");
		String httpMethod = config.elementText("http-method");
		for(Iterator<String> servicenames = ClusterInformation.serviceInfoByRestAPIs.keySet().iterator(); 
				servicenames.hasNext(); ) {
			String servicename = servicenames.next();
			ServiceBean service = ClusterInformation.serviceInfoByRestAPIs.get(servicename);
			Map<String, Object> urlParam = new HashMap<String, Object>();
			urlParam.put("serviceId", service.getId());
			String url = UtilTool.buildURL(originalURL, urlParam);
			String roleInfoOfJson = method.executeRestApi(url, httpMethod, null);
			
			JSONArray array = JSONArray.fromObject(roleInfoOfJson);
			for(int i = 0; i < array.size(); i++) {
				Object value = array.get(i);
				RoleBean role = new RoleBean(value.toString());
				service.addRole(role);
				ClusterInformation.roleNumbers += 1;
			}
		}
	}
}
