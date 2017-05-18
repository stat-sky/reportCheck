package io.transwarp.report.outputByExcel.sheet;

import java.util.Iterator;
import java.util.Map;

import jxl.write.Label;
import io.transwarp.information.ClusterInformation;
import io.transwarp.report.outputByExcel.ExcelSheetTemplate;

public class VersionSheet extends ExcelSheetTemplate {

	public VersionSheet(String path, String name, int id) throws Exception {
		super(path, name, id);
		sheet.setColumnView(1, cellView);
		sheet.setColumnView(2, cellView);
	}

	@Override
	public void writeToExcel() throws Exception {
		writeTdhVersion();
		writeOSAndJDKVersion();
	}
	
	protected void writeTdhVersion() throws Exception {
		sheet.addCell(new Label(0, 1, "集群版本号", cellFormat));
		sheet.addCell(new Label(1, 1, ClusterInformation.tdh_version, cellFormat));
	}
	
	protected void writeOSAndJDKVersion() throws Exception {
		int row = 3;
		sheet.addCell(new Label(0, row, "hostname", cellFormat));
		sheet.addCell(new Label(1, row, "OSType", cellFormat));
		sheet.addCell(new Label(2, row, "jdk_version", cellFormat));
		for(Iterator<String> hostnames = ClusterInformation.nodeInfoByShells.keySet().iterator(); hostnames.hasNext(); ) {
			row += 1;
			String hostname = hostnames.next();
			Map<String, String> nodeInfos = ClusterInformation.nodeInfoByShells.get(hostname);
			String OSType = nodeInfos.get("OSType");
			String jdkVersion = nodeInfos.get("jdk_version");
			sheet.addCell(new Label(0, row, hostname, cellFormat));
			sheet.addCell(new Label(1, row, OSType.replaceAll("\n", " ").trim(), cellFormat));
			sheet.addCell(new Label(2, row, jdkVersion.replaceAll("\n", " ").trim(), cellFormat));
		}
	}
	

}
