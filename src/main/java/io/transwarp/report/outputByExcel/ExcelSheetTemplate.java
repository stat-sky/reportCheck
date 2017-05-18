package io.transwarp.report.outputByExcel;

import java.io.File;
import java.io.FileOutputStream;

import jxl.CellView;
import jxl.Workbook;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import io.transwarp.information.ClusterInformation;

public abstract class ExcelSheetTemplate extends ClusterInformation {

	protected static WritableFont font;
	protected static WritableCellFormat cellFormat;
	protected static CellView cellView;
	protected WritableWorkbook excelFile;
	protected WritableSheet sheet;
	
	static {
		try {
			font = new WritableFont(WritableFont.TIMES);
			cellFormat = new WritableCellFormat(font);
			cellFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
			cellFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
			cellView = new CellView();
			cellView.setAutosize(true);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public ExcelSheetTemplate(String path, String name, int id) throws Exception {
		excelFile = buildExcelFile(path);
		sheet = excelFile.createSheet(name, id);

	}
	
	protected WritableWorkbook buildExcelFile(String path) throws Exception {
		WritableWorkbook excelFile = null;
		File file = new File(path);
		if(!file.exists()) {
			excelFile = Workbook.createWorkbook(new FileOutputStream(file));
		}else {
			excelFile = Workbook.createWorkbook(file, Workbook.getWorkbook(file));
		}
		return excelFile;
	}
	
	public abstract void writeToExcel() throws Exception;
	
	public void close() throws Exception {
		if(excelFile != null) {
			excelFile.write();
			excelFile.close();
		}
	}
}
