package io.transwarp.report.outputByExcel.sheet;

import java.util.List;

import jxl.write.Label;
import io.transwarp.bean.restapiInfo.ComponentBean;
import io.transwarp.information.ClusterInformation;
import io.transwarp.report.outputByExcel.ExcelSheetTemplate;

public class LicenseSheet extends ExcelSheetTemplate {

	public LicenseSheet(String path, String name, int id) throws Exception {
		super(path, name, id);
	}
	
	@Override
	public void writeToExcel() throws Exception {
		addLicenseInfoByTotal();
		addLicenseInfoByService();
	}
	
	protected void addLicenseInfoByTotal() throws Exception {
		sheet.addCell(new Label(0, 1, "clusterSize", cellFormat));
		sheet.addCell(new Label(1, 1, ClusterInformation.licenseInfo.getClusterSize(), cellFormat));
		sheet.addCell(new Label(0, 2, "supportStartDay", cellFormat));
		sheet.addCell(new Label(1, 2, ClusterInformation.licenseInfo.getSupportStartDay(), cellFormat));
		sheet.addCell(new Label(0, 3, "serverKey", cellFormat));
		sheet.addCell(new Label(1, 3, ClusterInformation.licenseInfo.getServerKey(), cellFormat));
		sheet.addCell(new Label(0, 4, "serialNumber", cellFormat));
		sheet.addCell(new Label(1, 4, ClusterInformation.licenseInfo.getSerialNumber(), cellFormat));
	}
	
	protected void addLicenseInfoByService() throws Exception {
		sheet.setColumnView(0, cellView);
		sheet.addCell(new Label(0, 6, "compType", cellFormat));
		sheet.addCell(new Label(0, 7, "compTypeFriendly", cellFormat));
		sheet.addCell(new Label(0, 8, "compSize", cellFormat));
		sheet.addCell(new Label(0, 9, "licenseType", cellFormat));
		sheet.addCell(new Label(0, 10, "expiredDate", cellFormat));
		sheet.addCell(new Label(0, 11, "supportExpiration", cellFormat));
		
		List<ComponentBean> components = ClusterInformation.licenseInfo.getComponents();
		int column = 1;
		for(ComponentBean component : components) {
			int row = 6;
			sheet.setColumnView(column, cellView);
			sheet.addCell(new Label(column, row++, component.getCompType(), cellFormat));
			sheet.addCell(new Label(column, row++, component.getCompTypeFriendly(), cellFormat));
			sheet.addCell(new Label(column, row++, component.getCompSize(), cellFormat));
			sheet.addCell(new Label(column, row++, component.getLicenseType(), cellFormat));
			sheet.addCell(new Label(column, row++, component.getExpiredDate(), cellFormat));
			sheet.addCell(new Label(column, row++, component.getSupportExpiration(), cellFormat));		
			column += 1;
		}
	}
}
