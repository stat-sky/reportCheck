package io.transwarp.report.outputByExcel.sheet;

import jxl.write.Label;
import io.transwarp.information.ClusterInformation;
import io.transwarp.report.outputByExcel.ExcelSheetTemplate;

public class ErrorSheet extends ExcelSheetTemplate {

	public ErrorSheet(String path, String name, int id) throws Exception {
		super(path, name, id);
	}
	
	@Override
	public void writeToExcel() throws Exception {
		sheet.setColumnView(1, cellView);
		sheet.setColumnView(2, cellView);
		int row = 1;
		for(String errorInfo : ClusterInformation.errorInfos) {
			String[] items = errorInfo.split("\\|");
			int len = items.length;
			for(int i = 1; i <= len; i++) {
				sheet.addCell(new Label(i, row, items[i - 1], cellFormat));
			}
			row += 1;
		}
	}
}
