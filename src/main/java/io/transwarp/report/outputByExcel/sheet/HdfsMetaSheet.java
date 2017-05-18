package io.transwarp.report.outputByExcel.sheet;

import java.util.Iterator;
import java.util.List;

import jxl.write.Label;
import io.transwarp.information.ClusterInformation;
import io.transwarp.report.outputByExcel.ExcelSheetTemplate;

public class HdfsMetaSheet extends ExcelSheetTemplate {
	
	private int row;

	public HdfsMetaSheet(String path, String name, int id) throws Exception {
		super(path, name, id);
		row = 0;
		sheet.setColumnView(1, cellView);
		sheet.setColumnView(2, cellView);
	}
	
	@Override
	public void writeToExcel() throws Exception {
		writeFsimage();
		writeEditLog();
	}
	
	protected void writeFsimage() throws Exception {
		sheet.mergeCells(0, row, 3, row);
		sheet.addCell(new Label(0, row, "fsimage检查:", cellFormat));
		row += 1;
		sheet.addCell(new Label(1, row, "path", cellFormat));
		sheet.addCell(new Label(2, row, "value", cellFormat));
		row += 1;
		for(Iterator<String> topics = ClusterInformation.fsimageIDAndTimestamp.keySet().iterator(); topics.hasNext(); ) {
			String topic = topics.next();
			String value = ClusterInformation.fsimageIDAndTimestamp.get(topic);
			String[] lines = value.split(";");
			for(String line : lines) {
				sheet.addCell(new Label(1, row, topic, cellFormat));
				sheet.addCell(new Label(2, row, line.trim(), cellFormat));
				row += 1;
			}
		}
		row += 1;
	}
	
	protected void writeEditLog() throws Exception {
		sheet.mergeCells(0, row, 3, row);
		sheet.addCell(new Label(0, row, "editLog检查:", cellFormat));
		row += 1;
		sheet.addCell(new Label(1, row, "path", cellFormat));
		sheet.addCell(new Label(2, row, "value", cellFormat));
		row += 1;
		for(Iterator<String> topics = ClusterInformation.editLogContinuity.keySet().iterator(); topics.hasNext(); ) {
			String topic = topics.next();
			List<String> values = ClusterInformation.editLogContinuity.get(topic);
			if(values.size() == 0) {
				sheet.addCell(new Label(1, row, topic, cellFormat));
				sheet.addCell(new Label(2, row, "continuity", cellFormat));
			}else {
				StringBuffer result = new StringBuffer("no continuity, lost id is :");
				for(String value : values) {
					result.append(" ").append(value);
				}
				sheet.addCell(new Label(1, row, topic, cellFormat));
				sheet.addCell(new Label(2, row, result.toString(), cellFormat));
			}
			row += 1;
		}
	}
}
