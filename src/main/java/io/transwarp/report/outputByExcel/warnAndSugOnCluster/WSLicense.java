package io.transwarp.report.outputByExcel.warnAndSugOnCluster;

import io.transwarp.information.CheckInfos;
import io.transwarp.report.outputByExcel.CheckReport;

import java.util.Iterator;

public class WSLicense extends WarnAndSuggestTemplate {

	@Override
	public String getWarnAndSuggest() throws Exception {
		StringBuffer warnInfo = new StringBuffer();
		StringBuffer suggestInfo = new StringBuffer();
		if(CheckInfos.licenseDateofRemaining.size() > 0) {
			warnInfo.append(CheckReport.warnID).append("、").append("license即将过期\n");
			for(Iterator<String> servicenames = CheckInfos.licenseDateofRemaining.keySet().iterator(); servicenames.hasNext(); ) {
				String servicename = servicenames.next();
				warnInfo.append("    ").append(servicename).append(":").append(CheckInfos.licenseDateofRemaining.get(servicename)).append("\n");
			}
			suggestInfo.append(CheckReport.warnID).append("、").append("申请license延期\n");
			CheckReport.warnID += 1;
		}
		return warnInfo.toString() + "|" + suggestInfo.toString();
	}
}
