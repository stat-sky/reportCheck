package io.transwarp.report.outputBytext;

import java.util.List;

public class PrintToTable {
	
	public static String changeToTable(List<String[]> form, int centLength) {
		return changeToTableBySetBorderNum(form, centLength, 0, 0);
	}

	public static String changeToTableBySetBorderNum(List<String[]> form, int centLength, int hasBorderRow, int hasBorderCol) {
		String format = "%-" + (centLength - 2) + "s"; 
		String lineBorder = getLineBorder(centLength - 1);
		StringBuffer resultOfTable = new StringBuffer();
		for(int row = 0; row < form.size(); row++) {
			StringBuffer lineValueBuffer = new StringBuffer();
			StringBuffer borderBuffer = new StringBuffer();
			String[] items = form.get(row);
			for(int column = 0; column < items.length; column++) {
				String centValue = items[column];
				if(centValue != null) {
					lineValueBuffer.append("| ").append(String.format(format, centValue));
					borderBuffer.append("+").append(lineBorder);
				}else {
					//添加单元格信息
					if(column <= hasBorderCol) {
						lineValueBuffer.append("| ").append(String.format(format, ""));
					}else {
						lineValueBuffer.append("  ").append(String.format(format, ""));
					}
					//添加边框信息
					if(row <= hasBorderRow) {
						borderBuffer.append("+").append(lineBorder);
					}else if(column <= hasBorderCol) {
						borderBuffer.append("| ").append(String.format(format, ""));
					}else {
						borderBuffer.append("  ").append(String.format(format, ""));
					}
				}
				
			}
			lineValueBuffer.append("|\n");
			borderBuffer.append("+\n");
			resultOfTable.append(borderBuffer.toString()).append(lineValueBuffer.toString());
			if(row == form.size() - 1) {
				resultOfTable.append("+").append(getLineBorder(centLength * items.length - 1)).append("+\n\n");
			}
		}
		return resultOfTable.toString();
	}
	
	protected static String getLineBorder(int centLength) {
		StringBuffer border = new StringBuffer();
		for(int i = 0; i < centLength; i++) {
			border.append("-");
		}
		return border.toString();
	}
}
