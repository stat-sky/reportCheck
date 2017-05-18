package io.transwarp.report.outputByExcel.sheet;

import java.util.Iterator;

import jxl.write.Label;
import io.transwarp.information.ClusterInformation;
import io.transwarp.report.outputByExcel.ExcelSheetTemplate;

public class HdfsInfoSheet extends ExcelSheetTemplate {

	public HdfsInfoSheet(String path, String name, int id) throws Exception {
		super(path, name, id);
	}
	
	@Override
	public void writeToExcel() throws Exception {
		sheet.setColumnView(1, cellView);
		sheet.setColumnView(2, cellView);
		
		int row = 0;
		sheet.mergeCells(0, row, 3, row);
		sheet.addCell(new Label(0, row, "hdfs 文件信息检测:", cellFormat));
		row += 1;
		int column = 1;
		for(Iterator<String> topics = ClusterInformation.hdfsInfos.keySet().iterator(); topics.hasNext();) {
			String topic = topics.next();
			String value = ClusterInformation.hdfsInfos.get(topic);
			String[] lines = value.split("\n");
			for(String line : lines) {
				line = line.replace("\t", " ").trim();
				if(line.startsWith(".") && line.endsWith(".")) {
					continue;
				}
				int splitID = line.indexOf(": ");
				if(splitID == -1) {
					sheet.mergeCells(column, row, column + 1, row);
					sheet.addCell(new Label(column, row, line, cellFormat));
				}else {
					sheet.addCell(new Label(column, row, line.substring(0, splitID), cellFormat));
					sheet.addCell(new Label(column + 1, row, line.substring(splitID + 1), cellFormat));
				}
				row += 1;
			}
			row += 1;
		}
	}
}
