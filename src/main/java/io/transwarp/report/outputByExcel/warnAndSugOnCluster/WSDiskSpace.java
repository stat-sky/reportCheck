package io.transwarp.report.outputByExcel.warnAndSugOnCluster;

import io.transwarp.information.CheckInfos;
import io.transwarp.report.outputByExcel.CheckReport;

import java.util.Iterator;

public class WSDiskSpace extends WarnAndSuggestTemplate {

	
	@Override
	public String getWarnAndSuggest() throws Exception {
		StringBuffer warnInfo = new StringBuffer();
		StringBuffer suggestInfo = new StringBuffer();
		if(CheckInfos.diskOfLessMemory.size() > 0) {
			warnInfo.append(CheckReport.warnID).append("、").append("集群磁盘空间不足:\n");
			for(Iterator<String> hostnames = CheckInfos.diskOfLessMemory.keySet().iterator(); hostnames.hasNext(); ) {
				String hostname = hostnames.next();
				warnInfo.append("\t").append(hostname).append(":").append(CheckInfos.diskOfLessMemory.get(hostname)).append("\n");
			}
			suggestInfo.append(CheckReport.warnID).append("、").append("集群扩容\n");
			CheckReport.warnID += 1;
		}
		if(CheckInfos.diskOfLessInodes.size() > 0) {
			warnInfo.append(CheckReport.warnID).append("、").append("inode使用率过高:\n");
			for(Iterator<String> hostnames = CheckInfos.diskOfLessInodes.keySet().iterator(); hostnames.hasNext(); ) {
				String hostname = hostnames.next();
				warnInfo.append("    ").append(hostname).append(":").append(CheckInfos.diskOfLessInodes.get(hostname)).append("\n");
			}
			suggestInfo.append(CheckReport.warnID).append("、").append("清理小文件\n");
			CheckReport.warnID += 1;
		}
		return warnInfo.toString() + "|" + suggestInfo.toString();
	}
}
