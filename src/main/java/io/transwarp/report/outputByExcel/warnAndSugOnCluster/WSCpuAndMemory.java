package io.transwarp.report.outputByExcel.warnAndSugOnCluster;

import io.transwarp.information.CheckInfos;
import io.transwarp.report.outputByExcel.CheckReport;

import java.util.Iterator;

public class WSCpuAndMemory extends WarnAndSuggestTemplate {

	@Override
	public String getWarnAndSuggest() throws Exception {
		StringBuffer warnInfo = new StringBuffer();
		StringBuffer suggestInfo = new StringBuffer();
		if(CheckInfos.cpuUsed.size() > 0) {
			warnInfo.append(CheckReport.warnID).append("、").append("cpu使用率过高:\n");
			for(Iterator<String> hostnames = CheckInfos.cpuUsed.keySet().iterator(); hostnames.hasNext(); ) {
				String hostname = hostnames.next();
				warnInfo.append("    ").append(hostname).append(":").append(CheckInfos.cpuUsed.get(hostname)).append("\n");
			}
			suggestInfo.append(CheckReport.warnID).append("、").append("集群扩容，或调整服务占用资源\n");
			CheckReport.warnID += 1;
		}
		if(CheckInfos.memUsed.size() > 0) {
			warnInfo.append(CheckReport.warnID).append("、").append("内存使用率过高:\n");
			for(Iterator<String> hostnames = CheckInfos.memUsed.keySet().iterator(); hostnames.hasNext(); ) {
				String hostname = hostnames.next(); 
				warnInfo.append("    ").append(hostname).append(":").append(CheckInfos.memUsed.get(hostname)).append("\n");
			}
			suggestInfo.append(CheckReport.warnID).append("、").append("集群扩容，或调整服务占用资源\n");
			CheckReport.warnID += 1;
		}
		return warnInfo.toString() + "|" + suggestInfo.toString();
	}
}
