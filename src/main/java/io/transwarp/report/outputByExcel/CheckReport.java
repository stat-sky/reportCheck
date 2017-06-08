package io.transwarp.report.outputByExcel;

import io.transwarp.autoCheck.CheckTemplate;
import io.transwarp.autoCheck.DiskUsedOnNodeCheck;
import io.transwarp.autoCheck.LicenseDateCheck;
import io.transwarp.bean.restapiInfo.ServiceBean;
import io.transwarp.information.CheckInfos;
import io.transwarp.information.ClusterInformation;
import io.transwarp.report.outputByExcel.warnAndSugOnCluster.WSCpuAndMemory;
import io.transwarp.report.outputByExcel.warnAndSugOnCluster.WSDiskSpace;
import io.transwarp.report.outputByExcel.warnAndSugOnCluster.WSHdfsUsed;
import io.transwarp.report.outputByExcel.warnAndSugOnCluster.WSLicense;
import io.transwarp.report.outputByExcel.warnAndSugOnCluster.WSNetwork;
import io.transwarp.report.outputByExcel.warnAndSugOnCluster.WarnAndSuggestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Iterator;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;


public class CheckReport extends ClusterInformation {

	private WritableWorkbook excelFile;
	private WritableSheet sheet;
	private WritableCellFormat titleFormat;
	private WritableCellFormat cellFormat;
	private WritableCellFormat title_background;
	public static int warnID = 1;
	private String[] autoCheckItems = new String[]{"disk", "cpuUsed", "license", "hdfsUsed"};
	
	public CheckReport() {
		checkCluster();
	}
	
	private void checkCluster() {
		String[] autoCheckItems = new String[]{"disk", "license"};
		CheckTemplate check;
		for(String item : autoCheckItems) {
			try {
				check = getCheck(item);
				check.check();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private CheckTemplate getCheck(String item) throws Exception {
		if(item.equals("disk")) {
			return new DiskUsedOnNodeCheck();
		}else if(item.equals("license")) {
			return new LicenseDateCheck();
		}else {
			throw new Exception("no this check item");
		}
	}
	
	public void outputCheckReport(String path) {
		try {
			buildExcelFile(path);
			setColWidth();
			getCellFormat();
			int row = 1;
			row = addTitle(row);
			row = addProjectInfo(row);
			row = addServiceStatus(row);
			row = addWarnAndSuggest(row);
			row = addClientSignature(row);
			row = addOther(row);
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {
				close();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void setColWidth() {
		sheet.setColumnView(1, 20);
		sheet.setColumnView(2, 30);
		sheet.setColumnView(3, 15);
		sheet.setColumnView(4, 20);
		sheet.setColumnView(5, 15);
		sheet.setColumnView(6, 20);
	}
	
	private void getCellFormat() throws Exception {
		WritableFont font1 = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
		titleFormat = new WritableCellFormat(font1);
		titleFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
		
		WritableFont font2 = new WritableFont(WritableFont.TIMES, 10);
		cellFormat = new WritableCellFormat(font2);
		cellFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
		cellFormat.setAlignment(Alignment.LEFT);
		cellFormat.setVerticalAlignment(VerticalAlignment.TOP);
		
		WritableFont font3 = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
		title_background = new WritableCellFormat(font3);
		title_background.setBorder(Border.ALL, BorderLineStyle.THIN);
		title_background.setBackground(Colour.GRAY_25);
	}
	
	private void buildExcelFile(String path) throws Exception {
		File file = new File(path);
		excelFile = Workbook.createWorkbook(new FileOutputStream(file));
		sheet = excelFile.createSheet("check report", 1);
	}
	
	private int addTitle(int beginRow) throws Exception {
		sheet.mergeCells(1, beginRow, 6, beginRow);
		sheet.setRowView(beginRow, 600);
		WritableFont font = new WritableFont(WritableFont.ARIAL, 15, WritableFont.BOLD);
		WritableCellFormat title = new WritableCellFormat(font);
		title.setAlignment(jxl.format.Alignment.CENTRE);
		title.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
		title.setBorder(Border.ALL, BorderLineStyle.MEDIUM);
		title.setBackground(Colour.GRAY_25);
		sheet.addCell(new Label(1, beginRow, "平台巡检报告", title));
		beginRow += 1;
		return beginRow;
	}
	
	private int addProjectInfo(int beginRow) throws Exception {

		sheet.addCell(new Label(1, beginRow, "项目编号", titleFormat));
		sheet.mergeCells(2, beginRow, 3, beginRow);
		sheet.addCell(new Label(2, beginRow, "", cellFormat));
		sheet.addCell(new Label(4, beginRow, "项目名称", titleFormat));
		sheet.mergeCells(5, beginRow, 6, beginRow);
		sheet.addCell(new Label(5, beginRow, "", cellFormat));
		sheet.setRowView(beginRow, 350);
		beginRow += 1;
		
		sheet.addCell(new Label(1, beginRow, "合作伙伴", titleFormat));
		sheet.addCell(new Label(2, beginRow, "", cellFormat));
		sheet.addCell(new Label(3, beginRow, "联系人", titleFormat));
		sheet.addCell(new Label(4, beginRow, "", cellFormat));
		sheet.addCell(new Label(5, beginRow, "联系方式", titleFormat));
		sheet.addCell(new Label(6, beginRow, "", cellFormat));
		sheet.setRowView(beginRow, 350);
		beginRow += 1;
		
		sheet.addCell(new Label(1, beginRow, "最终用户", titleFormat));
		sheet.addCell(new Label(2, beginRow, "", cellFormat));
		sheet.addCell(new Label(3, beginRow, "联系人", titleFormat));
		sheet.addCell(new Label(4, beginRow, "", cellFormat));
		sheet.addCell(new Label(5, beginRow, "联系方式", titleFormat));
		sheet.addCell(new Label(6, beginRow, "", cellFormat));
		sheet.setRowView(beginRow, 350);
		beginRow += 1;
		
		sheet.addCell(new Label(1, beginRow, "实施人员", titleFormat));
		sheet.addCell(new Label(2, beginRow, "", cellFormat));
		sheet.addCell(new Label(3, beginRow, "联系方式", titleFormat));
		sheet.addCell(new Label(4, beginRow, "", cellFormat));
		sheet.addCell(new Label(5, beginRow, "星环销售", titleFormat));
		sheet.addCell(new Label(6, beginRow, "", cellFormat));
		sheet.setRowView(beginRow, 350);
		beginRow += 1;
		
		sheet.addCell(new Label(1, beginRow, "实施地点", titleFormat));
		sheet.mergeCells(2, beginRow, 6, beginRow);
		sheet.addCell(new Label(2, beginRow, "", cellFormat));
		sheet.setRowView(beginRow, 350);
		beginRow += 1;
		
		sheet.addCell(new Label(1, beginRow, "巡检开始时间", titleFormat));
		sheet.mergeCells(2, beginRow, 3, beginRow);
		sheet.addCell(new Label(2, beginRow, CheckInfos.beginTime, cellFormat));
		sheet.addCell(new Label(4, beginRow, "巡检结束时间", titleFormat));
		sheet.mergeCells(5, beginRow, 6, beginRow);
		sheet.addCell(new Label(5, beginRow, CheckInfos.endTime, cellFormat));
		sheet.setRowView(beginRow, 350);
		beginRow += 1;
		return beginRow;
	}
	
	private int addServiceStatus(int beginRow) throws Exception {
		
		sheet.mergeCells(1, beginRow, 6, beginRow);
		sheet.addCell(new Label(1, beginRow, "巡检工作内容", title_background));
		sheet.setRowView(beginRow, 350);
		beginRow += 1;
		
		sheet.addCell(new Label(1, beginRow, "序号", cellFormat));
		sheet.addCell(new Label(2, beginRow, "组件", cellFormat));
		sheet.mergeCells(3, beginRow, 4, beginRow);
		sheet.addCell(new Label(3, beginRow, "运行情况", cellFormat));
		sheet.mergeCells(5, beginRow, 6, beginRow);
		sheet.addCell(new Label(5, beginRow, "备注", cellFormat));
		sheet.setRowView(beginRow, 350);
		beginRow += 1;
		
		int id = 1;
		for(Iterator<String> servicenames = ClusterInformation.serviceInfoByRestAPIs.keySet().iterator(); 
				servicenames.hasNext(); ) {
			String servicename = servicenames.next();
			if(servicename.equals("transwarp_license_cluster") || servicename.equals("Transpedia1") || servicename.equals("TranswarpManager")) {
				continue;
			}
			ServiceBean service = ClusterInformation.serviceInfoByRestAPIs.get(servicename);
			sheet.addCell(new Label(1, beginRow, String.valueOf(id), cellFormat));
			sheet.addCell(new Label(2, beginRow, service.getName(), cellFormat));
			sheet.mergeCells(3, beginRow, 4, beginRow);
			sheet.addCell(new Label(3, beginRow, service.getHealth(), cellFormat));
			sheet.mergeCells(5, beginRow, 6, beginRow);
			sheet.addCell(new Label(5, beginRow, "", cellFormat));
			sheet.setRowView(beginRow, 350);
			id += 1;
			beginRow += 1;
		}
		return beginRow;
	}
	
	private int addWarnAndSuggest(int beginRow) throws Exception {
		for(int i = 0; i < 4; i++) {
			sheet.mergeCells(1, beginRow + i, 6, beginRow + i);
		}
		StringBuffer warnOfCluster = new StringBuffer();
		StringBuffer suggestOfCluster = new StringBuffer();
		warnID = 1;
		
		WarnAndSuggestTemplate warnAndSuggest = null;
		for(String autoCheckItem : autoCheckItems) {
			warnAndSuggest = getAutoCheckItem(autoCheckItem);
			try {
				String values = warnAndSuggest.getWarnAndSuggest();
				String[] items = values.split("\\|");
				if(items.length > 0) {
					warnOfCluster.append(items[0]);
					suggestOfCluster.append(items[1]);
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		sheet.addCell(new Label(1, beginRow, "潜在隐患", title_background));
		sheet.setRowView(beginRow, 350);
		sheet.addCell(new Label(1, beginRow + 1, warnOfCluster.toString(), cellFormat));
		String[] lines_warn = warnOfCluster.toString().split("\n");
		int len_warn = Math.max(3, lines_warn.length);
		sheet.setRowView(beginRow + 1, 300 * len_warn, false);
		sheet.addCell(new Label(1, beginRow + 2, "建议", title_background));
		sheet.setRowView(beginRow + 2, 350);
		sheet.addCell(new Label(1, beginRow + 3, suggestOfCluster.toString(), cellFormat));
		String[] lines_suggest = suggestOfCluster.toString().split("\n");
		int len_suggest = Math.max(3,  lines_suggest.length);
		sheet.setRowView(beginRow + 3, 350 * len_suggest, false);
		return beginRow + 4;
	}
	
	private WarnAndSuggestTemplate getAutoCheckItem(String itemName) {
		if(itemName.equals("disk")) {
			return new WSDiskSpace();
		}else if(itemName.equals("cpuUsed")) {
			return new WSCpuAndMemory();
		}else if(itemName.equals("hdfsUsed")) {
			return new WSHdfsUsed();
		}else if(itemName.equals("license")) {
			return new WSLicense();
		}else if(itemName.equals("network")) {
			return new WSNetwork();
		}else {
			throw new RuntimeException("no this check item");
		}
	}
	
	private int addClientSignature(int beginRow) throws Exception {
		sheet.mergeCells(1, beginRow, 6, beginRow);
		sheet.addCell(new Label(1, beginRow, "最终用户/合作伙伴确认意见", title_background));
		sheet.setRowView(beginRow, 350);
		beginRow += 1;
		
		WritableFont font = new WritableFont(WritableFont.TIMES, 10);
		WritableCellFormat cell = new WritableCellFormat(font);
		cell.setBorder(Border.ALL, BorderLineStyle.THIN);
		cell.setAlignment(Alignment.RIGHT);
		cell.setVerticalAlignment(VerticalAlignment.BOTTOM);
		
		sheet.mergeCells(1, beginRow, 6, beginRow);
		sheet.addCell(new Label(1, beginRow, "签名:                          \n日期:                          ", cell));
		sheet.setRowView(beginRow, 1500);
		beginRow += 1;
		return beginRow;
	}
	
	private int addOther(int beginRow) throws Exception {
		sheet.mergeCells(1, beginRow, 6, beginRow);
		sheet.addCell(new Label(1, beginRow, "备注", title_background));
		sheet.setRowView(beginRow, 350);
		beginRow += 1;
		
		sheet.mergeCells(1, beginRow, 6, beginRow);
		sheet.addCell(new Label(1, beginRow, "", cellFormat));
		sheet.setRowView(beginRow, 1000);
		beginRow += 1;
		return beginRow;
	}
	
	private void close() throws Exception {
		if(excelFile != null ) { 
			excelFile.write();
			excelFile.close();
		}
	}
	
}
