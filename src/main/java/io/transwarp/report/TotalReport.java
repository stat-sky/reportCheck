package io.transwarp.report;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.transwarp.information.PropertiesInfo;
import io.transwarp.report.outputByExcel.CheckReport;
import io.transwarp.report.outputByExcel.ClusterReport;
import io.transwarp.servlet.ClusterCheck;
import io.transwarp.util.UtilTool;

public class TotalReport {

	public void getReport() {
		ClusterCheck clusterCheck = new ClusterCheck();
		clusterCheck.checkClusterInfo();
		String path = PropertiesInfo.prop_env.getProperty("goalPath", "/home/report/");
		try {
			UtilTool.checkAndBuildDir(path);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Date date = new Date();
		SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd");
		String timestamp = simpleFormat.format(date);
//		ClusterReport clusterReport = new ClusterReport();
		ClusterReportTemplate clusterReport = new ClusterReport();
		CheckReport checkReport = new CheckReport();
//		NodeReport nodeReport = new NodeReport();
		try {
			String fileName = path + "REPORT-" + timestamp + ".xls";
			clusterReport.outputToFile(fileName);
			checkReport.outputCheckReport(path + "checkReport.xls");
//			nodeReport.outputToFile(path + "nodeCheck/");
//			OutputByJson.output(path + "json/");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
