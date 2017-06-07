package io.transwarp.information;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckInfos {
	
	public static String beginTime;
	public static String endTime;

	public static Map<String, List<String>> diskOfLessMemory = new HashMap<String, List<String>>();
	public static Map<String, List<String>> diskOfLessInodes = new HashMap<String, List<String>>();
	public static Map<String, String> cpuUsed = new HashMap<String, String>();
	public static Map<String, String> memUsed = new HashMap<String, String>();
}
