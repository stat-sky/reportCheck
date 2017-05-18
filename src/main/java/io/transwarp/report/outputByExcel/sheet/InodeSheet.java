package io.transwarp.report.outputByExcel.sheet;

import java.util.Iterator;
import java.util.Map;

import jxl.write.Label;
import io.transwarp.information.ClusterInformation;
import io.transwarp.report.outputByExcel.ExcelSheetTemplate;

public class InodeSheet extends ExcelSheetTemplate {

	public InodeSheet(String path, String name, int id) throws Exception {
		super(path, name, id);
	}
	
	@Override
	public void writeToExcel() throws Exception {
		int row = 1;
		for(Iterator<String> hostnames = ClusterInformation.nodeInfoByShells.keySet().iterator(); hostnames.hasNext(); ) {
			String hostname = hostnames.next();
			row = writeInodeInfo(hostname, row) + 1;
		}
	}
	
	public int writeInodeInfo(String hostname, int beginRow) throws Exception {
		sheet.addCell(new Label(0, beginRow++, hostname, cellFormat));
		Map<String, String> nodeInfos = ClusterInformation.nodeInfoByShells.get(hostname);
		String inode = nodeInfos.get("inode");
		String[] lines = inode.split("\n");
		int row_inode = beginRow;
		int maxColumn = 0;
		for(String line : lines) {
			int column = 1;
			String[] items = line.trim().split("\\s+");
			for(String item : items) {
				sheet.addCell(new Label(column++, row_inode, item, cellFormat));
			}
			row_inode += 1;
			maxColumn = Math.max(maxColumn, column);
		}
		int row_memory = beginRow;
		String memory = nodeInfos.get("memory");
		String[] lines_memory = memory.split("\n");
		for(String line : lines_memory) {
			int column = maxColumn + 1;
			String[] items = line.trim().split("\\s+");
			for(String item : items) {
				sheet.addCell(new Label(column++, row_memory, item, cellFormat));
			}
			row_memory += 1;
		}
		return Math.max(row_memory, row_inode);
	}
}
