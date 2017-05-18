package io.transwarp.report.outputByExcel.sheet;

import java.util.Iterator;
import java.util.Vector;

import jxl.write.Label;
import io.transwarp.bean.TableBean;
import io.transwarp.information.ClusterInformation;
import io.transwarp.report.outputByExcel.ExcelSheetTemplate;
import io.transwarp.util.UtilTool;

public class TableSheet extends ExcelSheetTemplate {
	
	private int row;

	public TableSheet(String path, String name, int id) throws Exception {
		super(path, name, id);
		row = 1;
		for(int i = 1; i <= 6; i++) {
			sheet.setColumnView(i, cellView);
		}
	}
	
	@Override
	public void writeToExcel() throws Exception {
		writeTableInfo();
	}
	
	protected void writeTableInfo() throws Exception {
		System.out.println("inceptor service number is " + ClusterInformation.tableInfos.size());
		for(Iterator<String> servicenames = ClusterInformation.tableInfos.keySet().iterator(); servicenames.hasNext(); ) {
			String servicename = servicenames.next();
			Vector<TableBean> tables = ClusterInformation.tableInfos.get(servicename);
			System.out.println("table number is : " + tables.size());
			int totalTable = tables.size();
			int fileTotal = 0;
			int dirTotal = 0;
			sheet.mergeCells(0, row, 6, row);
			sheet.addCell(new Label(0, row, servicename + " table number is : " + totalTable, cellFormat));
			row += 1;
			sheet.addCell(new Label(1, row, "database name", cellFormat));
			sheet.addCell(new Label(2, row, "owner", cellFormat));
			sheet.addCell(new Label(3, row, "table name", cellFormat));
			sheet.addCell(new Label(4, row, "table type", cellFormat));
			sheet.addCell(new Label(5, row, "directory size:(max/min/number/avg)", cellFormat));
			sheet.addCell(new Label(6, row, "file size:(max/min/number/avg)", cellFormat));
			row += 1;
			for(TableBean table : tables) {
				String tableDirInfo;
				if(table.getCountDirNumber() == 0) {
					tableDirInfo = "null";
				}else {
					tableDirInfo = UtilTool.getSize(table.getMaxDirSize()) + "/" + 
								   UtilTool.getSize(table.getMinDirSize()) + "/" +
								   table.getCountDirNumber() + "/" +
								   UtilTool.getSize(table.getSumDirSize() / table.getCountDirNumber());
					dirTotal += table.getCountDirNumber();
				}
				String tableFileInfo;
				if(table.getCountFileNumber() == 0) {
					tableFileInfo = "null";
				}else {
					tableFileInfo = UtilTool.getSize(table.getMaxFileSize()) + "/" +
								    UtilTool.getSize(table.getMinFileSize()) + "/" +
								    table.getCountFileNumber() + "/" + 
								    UtilTool.getSize(table.getSumFileSize() / table.getCountFileNumber());
					fileTotal += table.getCountFileNumber();
				}
				sheet.addCell(new Label(1, row, table.getDatabase_name(), cellFormat));
				sheet.addCell(new Label(2, row, table.getOwner_name(), cellFormat));
				sheet.addCell(new Label(3, row, table.getTable_name(), cellFormat));
				sheet.addCell(new Label(4, row, table.getTableType(), cellFormat));
				sheet.addCell(new Label(5, row, tableDirInfo, cellFormat));
				sheet.addCell(new Label(6, row, tableFileInfo, cellFormat));
				row += 1;
			}
			sheet.mergeCells(4, row, 6, row);
			sheet.addCell(new Label(4, row, "total dir number is : " + dirTotal, cellFormat));
			row += 1;
			sheet.mergeCells(4, row, 6, row);
			sheet.addCell(new Label(4, row, "total file number is : " + fileTotal, cellFormat));
			row += 2;
			
		}
	}
}
