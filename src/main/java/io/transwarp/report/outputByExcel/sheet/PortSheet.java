package io.transwarp.report.outputByExcel.sheet;

import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import jxl.write.Label;
import io.transwarp.information.ClusterInformation;
import io.transwarp.report.outputByExcel.ExcelSheetTemplate;

public class PortSheet extends ExcelSheetTemplate {

	public PortSheet(String path, String name, int id) throws Exception {
		super(path, name, id);
	}
	
	@Override
	public void writeToExcel() throws Exception {
		boolean isFirstNode = true;
		int column = 2;
		Vector<String> checkItems = new Vector<String>();
		for(Iterator<String> hostnames = ClusterInformation.portInfos.keySet().iterator(); hostnames.hasNext(); ) {
			String hostname = hostnames.next();
			int row = 1;
			sheet.addCell(new Label(column, row++, hostname, cellFormat));
			sheet.setColumnView(column, cellView);
			Map<String, String> portInfo = ClusterInformation.portInfos.get(hostname);
			if(isFirstNode) {
				sheet.setColumnView(column - 1, cellView);
				for(Iterator<String> keys = portInfo.keySet().iterator(); keys.hasNext(); ) {
					String key = keys.next();
					checkItems.add(key);
					String value = portInfo.get(key);
					sheet.addCell(new Label(column - 1, row, key, cellFormat));
					sheet.addCell(new Label(column, row, value.trim(), cellFormat));
					row += 1;
				}
				column += 1;
				isFirstNode = false;
			}else {
				for(String checkItem : checkItems) {
					String value = portInfo.get(checkItem);
					sheet.addCell(new Label(column, row, value.trim(), cellFormat));
					row += 1;
				}
				column += 1;
			}
		}
	}
	
}
