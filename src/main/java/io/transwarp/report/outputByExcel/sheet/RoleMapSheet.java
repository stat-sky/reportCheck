package io.transwarp.report.outputByExcel.sheet;

import java.util.Iterator;

import jxl.write.Label;
import jxl.write.WritableSheet;
import io.transwarp.information.ClusterInformation;
import io.transwarp.report.BuildRoleMap;
import io.transwarp.report.outputByExcel.ExcelSheetTemplate;

public class RoleMapSheet extends ExcelSheetTemplate {
	
	private int row;

	public RoleMapSheet(String path, String name, int id) throws Exception {
		super(path, name, id);
		BuildRoleMap build = new BuildRoleMap();
		build.getBuildRoleMap();
		row = 0;
	}
	
	@Override
	public void writeToExcel() throws Exception {
		writeNodeList();
		writeRoleMap();
	}
	
	private void writeNodeList() throws Exception {
		sheet.mergeCells(0, row, 1, row);
		sheet.addCell(new Label(0, row, "节点列表", cellFormat));
		row += 1;
		for(Iterator<String> nodenames = ClusterInformation.roleMapOfNodes.keySet().iterator(); nodenames.hasNext(); ) {
			String nodename = nodenames.next();
			String value = ClusterInformation.roleMapOfNodes.get(nodename);
			sheet.addCell(new Label(1, row, nodename, cellFormat));
			sheet.addCell(new Label(2, row, value, cellFormat));
			row += 1;
		}
		row += 1;
	}
	
	private void writeRoleMap() throws Exception {
		sheet.mergeCells(0, row, 1, row);
		sheet.addCell(new Label(0, row, "服务角色分布图", cellFormat));
		row += 1;
		int roleMapBeginRow = row;
		int maxColumn = 0;
		for(String[] line : ClusterInformation.roleMapTable) {
			int column = 1;
			for(String item : line) {
				if(item != null || column == 1) {
					Label cent = new Label(column, row, item, cellFormat);
					sheet.addCell(cent);
				}
				column += 1;
			}
			maxColumn = Math.max(maxColumn, column);
			row += 1;
		}
		//设在列自适应宽度
		for(int i = 1; i <= maxColumn; i++) {
			sheet.setColumnView(i, cellView);
		}
		//服务名称列需要合并空白单元格
		mergeLine(sheet, 1, roleMapBeginRow, row - 1);
		//服务角色列需要合并空白单元格
		mergeLine(sheet, 2, roleMapBeginRow, row - 1);
		//机柜行需要合并单元格
		mergeRow(sheet, roleMapBeginRow, 1, maxColumn);
		//节点名称行需要合并单元格
		mergeRow(sheet, roleMapBeginRow + 1, 1, maxColumn);
		//标题行合并单元格
		mergeRow(sheet, roleMapBeginRow + 2, 1, maxColumn);
	}
	
	private void mergeLine(WritableSheet sheet, int column, int beginRow, int endRow) throws Exception {
		for(int i = endRow; i >= beginRow; i--) {
			int temp = i;
			while(temp >= beginRow) {
				String cellValue = sheet.getCell(column, temp).getContents();
				if(cellValue != null && !cellValue.equals("")) {
					break;
				}
				temp -= 1;
			}
			if(temp >= beginRow && temp < i) {
				sheet.mergeCells(column, temp, column, i);
			}
			i = temp;
		}
	}
	
	private void mergeRow(WritableSheet sheet, int row, int beginColumn, int endColumn) throws Exception {
		for(int i = endColumn; i >= beginColumn; i--) {
			int temp = i;
			while(temp >= beginColumn) {
				String cellValue = sheet.getCell(temp, row).getContents();
				if(cellValue != null && !cellValue.equals("")) {
					break;
				}
				temp -= 1;
			}
			if(temp < i) {
				sheet.mergeCells(temp, row, i, row);
			}
			i = temp;
		}		
	}
}
