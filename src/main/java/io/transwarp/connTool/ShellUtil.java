package io.transwarp.connTool;

import io.transwarp.util.TypeChangeUtil;

import java.io.InputStream;

import org.apache.log4j.Logger;

public class ShellUtil {

	private static final Logger LOG = Logger.getLogger(ShellUtil.class);
	
	private static String distCmd = "";
	private static String distScp = "";
	
	public static void initShell(String userKeytab, String nodeUser) {
		ShellUtil.distCmd = "ssh -i " + userKeytab + " " + nodeUser + "@";
		ShellUtil.distScp = "scp -i " + userKeytab;
	}
	
	public static String executeDist(String command, String ipAddress) throws Exception {
		return executeDist(command, ipAddress, -1);
	}
	
	public static String executeDist(String command, String ipAddress, long waitTime) throws Exception {
		StringBuffer executeCmd = new StringBuffer();
		executeCmd.append(distCmd).append(ipAddress).append(" ").append(command);
		return executeLocal(executeCmd.toString(), waitTime);
	}
	
	public static String executeLocal(String command) throws Exception {
		return executeLocal(command, -1);
	}
	
	public static String executeLocal(String command, long waitTime) throws Exception {
		LOG.debug("execute command is \"" + command + "\"");
		Process process = Runtime.getRuntime().exec(command);
		if(waitTime != -1) {
			Thread.sleep(waitTime);
		}
		InputStream inputStream = process.getInputStream();
		String resultOfCmd = TypeChangeUtil.changeInputStreamToString(inputStream);
		return resultOfCmd;
	}
	
	public static void scpDirector(String path1, String path2) throws Exception {
		StringBuffer executeCmd = new StringBuffer();
		executeCmd.append(distScp).append(" -r ")
					.append(path1).append(" ")
					.append(path2);
		executeLocal(executeCmd.toString(), -1);
	}
	
	public static void scpFiler(String path1, String path2) throws Exception {
		StringBuffer executeCmd = new StringBuffer();
		executeCmd.append(distScp).append(" ")
					.append(path1).append(" ")
					.append(path2);
		executeLocal(executeCmd.toString(), -1);
	}
	
/*	public static void main(String[] args) {
		String command = "sudo -u hdfs hdfs haadmin -getServiceState ";
		String[] ips = new String[]{"172.16.1.109", "172.16.1.110", "172.16.1.111", "172.16.2.182"};
		for(String ip : ips) {
			try {
				String result = ShellUtil.executeLocal("ssh -i /tmp/transwarp-id_rsa root@172.16.1.109 " + command + ip);
				System.out.println(result);
				System.out.println();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			String cmd = "netstat -anp | grep 10000";
			String result = ShellUtil.executeLocal("ssh -i /tmp/transwarp-id_rsa root@172.16.1.198 " + cmd);
			System.out.println(result);
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}*/
}
