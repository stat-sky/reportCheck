package io.transwarp.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class TypeChangeUtil {

	public static String changeFirstCharToCapital(String str) {
		byte[] items = str.getBytes();
		int ch = items[0];
		if(ch > 'a' && ch < 'z') {
			ch = ch - 'a' + 'A';
			items[0] = (byte)ch;
		}
		return new String(items);
	}
	
	public static List<String> changeJsonToList(String json) throws Exception {
		List<String> result = new ArrayList<String>();
		JSONArray array = JSONArray.fromObject(json);
		for(int i = 0; i < array.size(); i++) {
			result.add(array.getString(i));
		}
		return result;
	}
	
	public static String changeMapToJson(Map<String, String> maps) {
		JSONObject json = new JSONObject();
		json.putAll(maps);
		return json.toString();
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> changeJsonObjectToMap(String json) throws Exception {
		Map<String, Object> maps = new HashMap<String, Object>();
		JSONObject jsonObject = JSONObject.fromObject(json);
		for(Iterator<String> keys = jsonObject.keySet().iterator(); keys.hasNext(); ) {
			String key = keys.next();
			Object value = jsonObject.get(key);
			if(value.getClass().equals(JSONObject.class)) {
				maps.put(key, changeJsonObjectToMap(value.toString()));
			}else if(value.getClass().equals(JSONArray.class)) {
				maps.put(key, changeJsonArrayToList(value.toString()));
			}else {
				maps.put(key, value);
			}
		}
		return maps;
	}
	
	public static List<Object> changeJsonArrayToList(String json) throws Exception {
		List<Object> list = new ArrayList<Object>();
		JSONArray array = JSONArray.fromObject(json);
		for(int i = 0; i < array.size(); i++) {
			Object value = array.get(i);
			if(value.getClass().equals(JSONObject.class)) {
				list.add(changeJsonObjectToMap(value.toString()));
			}else if(value.getClass().equals(JSONArray.class)) {
				list.add(changeJsonArrayToList(value.toString()));
			}else {
				list.add(value);
			}
		}
		return null;
	}
	
	public static String changeInputStreamToString(InputStream inputStream) throws Exception {
		byte[] buffer = new byte[1024];
		StringBuffer answer = new StringBuffer();
		int len = -1;
		while(true) {
			len = inputStream.read(buffer);
			if(len == -1) break;
			String str = new String(buffer, 0, len);
			answer.append(str);
		}
		return answer.toString();
	}
}
