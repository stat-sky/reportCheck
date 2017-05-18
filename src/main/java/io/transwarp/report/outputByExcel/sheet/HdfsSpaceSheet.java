package io.transwarp.report.outputByExcel.sheet;

import jxl.write.Label;
import io.transwarp.information.ClusterInformation;
import io.transwarp.report.outputByExcel.ExcelSheetTemplate;
import io.transwarp.util.UtilTool;

public class HdfsSpaceSheet extends ExcelSheetTemplate {

	public HdfsSpaceSheet(String name, String path, int id) throws Exception {
		super(name, path, id);
	}
	
	@Override
	public void writeToExcel() throws Exception {
		sheet.setColumnView(1, cellView);
		sheet.setColumnView(2, cellView);
		
		sheet.mergeCells(0, 0, 3, 0);
		sheet.addCell(new Label(0, 0, "hdfs文件空间大小检测:", cellFormat));
		sheet.addCell(new Label(1, 1, "menu", cellFormat));
		sheet.addCell(new Label(2, 1, "size", cellFormat));
		
		int row = 2;
		for(String line : ClusterInformation.hdfsSpaceSizeInfos) {
			String[] items = line.split(":");
			if(items.length != 2) {
				continue;
			}
			String sizeOfString = UtilTool.getInteger(items[1]);
			double size = Double.valueOf(sizeOfString);
			sheet.addCell(new Label(1, row, items[0], cellFormat));
			sheet.addCell(new Label(2, row, UtilTool.getSize(size), cellFormat));
			row += 1;
		}
	}
}
