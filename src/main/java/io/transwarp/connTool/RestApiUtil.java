package io.transwarp.connTool;

import java.net.URI;
import java.util.Iterator;
import java.util.Map;

import io.transwarp.bean.LoginInfoBean;
import io.transwarp.util.TypeChangeUtil;
import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

public class RestApiUtil {

	private static final Logger LOG = Logger.getLogger(RestApiUtil.class);
	
	private static RestApiUtil method = null;
	private static String managerUrl;
	private static String username;
	
	private CloseableHttpClient httpClient;
	private CloseableHttpResponse response;
	
	public static RestApiUtil getRestApiUtil(LoginInfoBean loginInfo) throws Exception {
		if(method != null && username != null && loginInfo.getManagerUser().equals(username)) {
			return method;
		}
		method = new RestApiUtil();
		try {
			managerUrl = loginInfo.getManagerUrl();
			method.login(loginInfo);
		}catch(Exception e) {
			throw new RuntimeException("login error, " + e.getMessage());
		}
		return method;
	}	
	
	private RestApiUtil() {
		super();
		httpClient = HttpClients.createDefault();
	}
	
	private boolean login(LoginInfoBean loginInfo) throws Exception {
		String parameterOfJson = buildLoginParameter(loginInfo.getManagerUser(), loginInfo.getManagerPwd());
		String resultOfLoginOnJson = executeRestApi("/users/login", "post", parameterOfJson);
		return analysisResultOfLogin(resultOfLoginOnJson);
	}
	
	private String buildLoginParameter(String username, String password) throws Exception {
		JSONObject json = new JSONObject();
		json.put("userName", username);
		json.put("userPassword", password);
		return json.toString();
	}
	
	private boolean analysisResultOfLogin(String resultOfJson) throws Exception {
		Map<String, Object> resultOfMap = TypeChangeUtil.changeJsonObjectToMap(resultOfJson);
		for(Iterator<String> infoKeys = resultOfMap.keySet().iterator(); infoKeys.hasNext(); ) {
			String infoKey = infoKeys.next();
			if(infoKey.equals("messageKey")) {
				throw new RuntimeException(resultOfMap.get(infoKey).toString());
			}
		}
		return true;
	}
	
	public boolean close() throws Exception {
		try {
			method.logout();
			method = null;
			httpClient = null;
		}catch(Exception e) {
			throw new RuntimeException("logout error : " + e.getMessage());
		}
		return true;
	}
	
	private void logout() throws Exception {
		String resultOfLogout = method.executeRestApi("/users/logout", "get", "");
		if(!resultOfLogout.equals("success")) {
			throw new RuntimeException("logout faild");
		}
	}	
	
	public String executeRestApi(String url, String httpMethod, String parameterOfJson) throws Exception{
		if(url.indexOf("http://") == -1) {
			url = managerUrl + "/api"+ url;
		}
		LOG.debug("executor rest api url is \"" + url + "\", http method is \"" + httpMethod + "\"");
		HttpEntity entity = null;
		switch(httpMethod) {
		case "get" : entity = getMethodOfHttp(url); break;
		case "put" : entity = putMethodOfHttp(url, parameterOfJson); break;
		case "post" : entity = postMethodOfHttp(url, parameterOfJson); break;
		case "delete" : entity = deleteMethodOfHttp(url, parameterOfJson); break;
		default : throw new RuntimeException("this http method " + httpMethod + " is no find");
		}
		String executeResult = EntityUtils.toString(entity);
		response.close();
		return executeResult;
	}
	
	private HttpEntity getMethodOfHttp(String url) throws Exception {
		HttpGet getRequest = new HttpGet(url);
		response = httpClient.execute(getRequest);
		return response.getEntity();
	}
	
	private HttpEntity putMethodOfHttp(String url, String parameterOfJson) throws Exception {
		HttpPut putRequest = new HttpPut(url);
		if(!parameterOfJson.equals("")) {
			StringEntity stringEntity = new StringEntity(parameterOfJson);
			putRequest.setEntity(stringEntity);
		}
		response = httpClient.execute(putRequest);
		return response.getEntity();
	}
	
	private HttpEntity postMethodOfHttp(String url, String parameterOfJson) throws Exception {
		HttpPost postRequest = new HttpPost(url);
		if(!parameterOfJson.equals("")) {
			StringEntity stringEntity = new StringEntity(parameterOfJson);
			postRequest.setEntity(stringEntity);
		}
		response = httpClient.execute(postRequest);
		return response.getEntity();
	}
	
	private HttpEntity deleteMethodOfHttp(String url, String parameterOfJson) throws Exception {
		HttpDeleteWithBody deleteRequest = new HttpDeleteWithBody(url);
		if(!parameterOfJson.equals("")) {
			StringEntity stringEntity = new StringEntity(parameterOfJson);
			deleteRequest.setEntity(stringEntity);
		}
		response = httpClient.execute(deleteRequest);
		return response.getEntity();
	}
	
	private class HttpDeleteWithBody extends HttpEntityEnclosingRequestBase {
		public static final String METHOD_NAME = "DELETE";
		public String getMethod() {
			return METHOD_NAME;
		}
		public HttpDeleteWithBody(final String uri) {
			super();
			setURI(URI.create(uri));
		}
		@SuppressWarnings("unused")
		public HttpDeleteWithBody() {
			super();
		}
	}
}
