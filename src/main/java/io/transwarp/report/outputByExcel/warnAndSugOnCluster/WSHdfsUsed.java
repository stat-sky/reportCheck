package io.transwarp.report.outputByExcel.warnAndSugOnCluster;

import io.transwarp.information.CheckInfos;
import io.transwarp.report.outputByExcel.CheckReport;

public class WSHdfsUsed extends WarnAndSuggestTemplate {

	@Override
	public String getWarnAndSuggest() throws Exception {
		StringBuffer warnInfo = new StringBuffer();
		StringBuffer suggestInfo = new StringBuffer();
		double differ = CheckInfos.maxHdfsUsed - CheckInfos.minHdfsUsed;
		if(differ > 20) {
			warnInfo.append(CheckReport.warnID).append("、").append("hdfs分布不均衡\n");
			suggestInfo.append(CheckReport.warnID).append("、").append("做balance\n");
			CheckReport.warnID += 1;
		}
		return warnInfo.toString() + "|" + suggestInfo.toString();
	}
}
