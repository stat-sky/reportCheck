package io.transwarp.report.outputByExcel.sheet;

import java.util.Iterator;

import jxl.write.Label;
import io.transwarp.information.CheckInfos;
import io.transwarp.information.ClusterInformation;
import io.transwarp.report.outputByExcel.ExcelSheetTemplate;
import io.transwarp.util.UtilTool;

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
					String name = line.substring(0, splitID);
					String valueOfItem = line.substring(splitID + 1);
					sheet.addCell(new Label(column, row, name, cellFormat));
					sheet.addCell(new Label(column + 1, row, valueOfItem, cellFormat));
					if(name.startsWith("DFS Used")) {
						Double usedPercent = Double.valueOf(UtilTool.getDouble(valueOfItem));
						CheckInfos.maxHdfsUsed = Math.max(CheckInfos.maxHdfsUsed, usedPercent);
						CheckInfos.minHdfsUsed = Math.min(CheckInfos.minHdfsUsed, usedPercent);
					}
				}
				row += 1;
			}
			row += 1;
		}
	}
}
