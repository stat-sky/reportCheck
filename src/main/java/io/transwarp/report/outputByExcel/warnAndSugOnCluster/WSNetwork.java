package io.transwarp.report.outputByExcel.warnAndSugOnCluster;

import io.transwarp.information.CheckInfos;
import io.transwarp.report.outputByExcel.CheckReport;

public class WSNetwork extends WarnAndSuggestTemplate {

	@Override
	public String getWarnAndSuggest() throws Exception {
		StringBuffer warnInfo = new StringBuffer();
		StringBuffer suggestInfo = new StringBuffer();
		if(CheckInfos.networkCheck.size() > 0) {
			warnInfo.append(CheckReport.warnID).append("、").append("网络端口不通\n");
			for(String info : CheckInfos.networkCheck) {
				warnInfo.append("    ").append(info).append("\n");
			}
			suggestInfo.append(CheckReport.warnID).append("、").append("检查网络");
		}
		return warnInfo.toString() + "|" + suggestInfo.toString();
	}
}
