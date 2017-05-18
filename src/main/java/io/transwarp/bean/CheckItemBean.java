package io.transwarp.bean;

public class CheckItemBean {

	private String[] outputSheet;

	private boolean serviceConfig = false;
	private boolean hdfsInfo = false;
	private boolean hdfsSpace = false;
	private boolean hdfsMeta = false;
	private boolean nodeCheck = false;
	private boolean process = false;
	private boolean excetor = false;
	private boolean table = false;
	private boolean namenodeHA = false;
	
	public CheckItemBean(String[] outputSheet) {
		this.outputSheet = outputSheet;
		for(String sheet : outputSheet) {
			if(sheet.equalsIgnoreCase("roleMap")) {
				serviceConfig = true;
				excetor = true;
			}else if(sheet.equalsIgnoreCase("nodeInfo")) {
				nodeCheck = true;
			}else if(sheet.equalsIgnoreCase("inode")) {
				nodeCheck = true;
			}else if(sheet.equalsIgnoreCase("portInfo")) {
				nodeCheck = true;
			}else if(sheet.equalsIgnoreCase("hdfsInfo")) {
				serviceConfig = true;
				hdfsInfo = true;
				namenodeHA = true;
			}else if(sheet.equalsIgnoreCase("hdfsSpace")) {
				serviceConfig = true;
				hdfsSpace = true;
				namenodeHA = true;
			}else if(sheet.equalsIgnoreCase("hdfsMeta")) {
				hdfsMeta = true;
			}else if(sheet.equalsIgnoreCase("process")) {
				process = true;
			}else if(sheet.equalsIgnoreCase("tableInfo")) {
				serviceConfig = true;
				table = true;
				namenodeHA = true;
			}
		}
	}

	public String[] getOutputSheet() {
		return outputSheet;
	}

	public boolean isServiceConfig() {
		return serviceConfig;
	}

	public boolean isHdfsInfo() {
		return hdfsInfo;
	}

	public boolean isHdfsSpace() {
		return hdfsSpace;
	}

	public boolean isHdfsMeta() {
		return hdfsMeta;
	}

	public boolean isNodeCheck() {
		return nodeCheck;
	}

	public boolean isProcess() {
		return process;
	}

	public boolean isExcetor() {
		return excetor;
	}

	public boolean isTable() {
		return table;
	}
	
	public boolean isNamenodeHA() {
		return namenodeHA;
	}
}
