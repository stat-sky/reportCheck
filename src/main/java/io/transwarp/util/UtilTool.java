package io.transwarp.util;

import io.transwarp.bean.LoginInfoBean;
import io.transwarp.information.Constant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UtilTool {

	public static String buildURL(String originalURL, Map<String, Object> urlParam) throws Exception {
		if(originalURL.indexOf("{") == -1) {
			return originalURL;
		}else if(originalURL.indexOf("[") == -1) {
			return buildURLWithRequiredParam(originalURL, urlParam);
		}else {
			return buildURLWithOptionalParam(originalURL, urlParam);
		}
	}
	
	protected static String buildURLWithRequiredParam(String originalURL, Map<String, Object> urlParam) throws Exception {
		StringBuffer urlBuild = new StringBuffer();
		String[] splitByLeftParenthesis = originalURL.split("\\{");
		urlBuild.append(splitByLeftParenthesis[0]);
		int size = splitByLeftParenthesis.length;
		for(int i = 1; i < size; i++) {
			String[] splitByRightParenthesis = splitByLeftParenthesis[i].split("\\}");
			Object value = urlParam.get(splitByRightParenthesis[0]);
			if(value == null || value.equals("")) {
				throw new RuntimeException("there is not this param : " + splitByRightParenthesis[0]);
			}
			urlBuild.append(value);
			if(splitByRightParenthesis.length > 1) {
				urlBuild.append(splitByRightParenthesis[1]);
			}
			
		}
		return urlBuild.toString();
	}
	
	protected static String buildURLWithOptionalParam(String originalURL, Map<String, Object> urlParam) throws Exception {
		StringBuffer urlBuild = new StringBuffer();
		String[] splitByLeftParenthesis = originalURL.split("\\[");
		urlBuild.append(buildURL(splitByLeftParenthesis[0], urlParam));
		boolean hasParamOnURL = (urlBuild.toString().indexOf("?") == -1) ? false : true;

		int numberOfSplit = splitByLeftParenthesis.length;
		for(int i = 1; i < numberOfSplit; i++) {
			String buffer = splitByLeftParenthesis[i].substring(1, splitByLeftParenthesis[i].length() - 1);
			String[] items = buffer.split("\\&");
			for(String item : items) {
				try {
					String urlFragment = buildURLWithRequiredParam(item, urlParam); 
					if(hasParamOnURL) {
						urlBuild.append("&").append(urlFragment);
					}else {
						hasParamOnURL = true;
						urlBuild.append("?").append(urlFragment);
					}
				}catch(Exception e) {
					//构建不成功说明没有该参数，跳过
				}
			}
		}
		return urlBuild.toString();
	}
	
	public static String getInteger(String oldstr) {
		return getNumber(oldstr, "[-0-9]+");
	}
	
	public static String getDouble(String oldstr) {
		return getNumber(oldstr, "[-0-9.]+");	
	}
	
	protected static String getNumber(String oldstr, String regex) {
		StringBuffer buffer = new StringBuffer();
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(oldstr);
		while(matcher.find()) {
			buffer.append(matcher.group());
		}
		return buffer.toString();			
	}
	
	public static void checkAndBuildDir(String path) throws Exception {
		File file = new File(path);
		if(!file.exists()) {
			file.mkdirs();
		}
	}
	
	public static String readFile(String path) throws Exception {
		StringBuffer fileValue = new StringBuffer();
		FileReader fileReader = new FileReader(path);
		BufferedReader reader = new BufferedReader(fileReader);
		String line = null;
		while(true) {
			line = reader.readLine();
			if(line == null)
				break;
			fileValue.append(line);
		}
		reader.close();
		fileReader.close();
		return fileValue.toString();
	}
	
	public static String getFileName(String filePath) throws Exception {
		if(filePath == null) {
			throw new RuntimeException("this path is null");
		}
		String[] items = filePath.split("/");
		return items[items.length - 1];
	}
	
	public static String getCmdOfSecurity(String command, LoginInfoBean loginInfo) {
		String enableKerberos = loginInfo.getEnableKerberos();
		if(enableKerberos.equals("false")) {
			return "sudo -u " + loginInfo.getHdfsUser() + " " + command;
		}else {
			return "kinit -kt " + loginInfo.getHdfsKeytab() + " " + loginInfo.getHdfsUser() + ";" + command;
		}
	}
	
	public static String getSize(double number) {
		int carry = 0;
		while(number >= 1024) {
			number /= 1024;
			carry += 1;
		}
		return Constant.DECIMAL_FORMAT.format(number) + " " + Constant.UNITS[carry];
	}
	

}
