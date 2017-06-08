package io.transwarp.report.outputByExcel;

import org.apache.log4j.Logger;

import io.transwarp.information.ClusterInformation;
import io.transwarp.information.PropertiesInfo;
import io.transwarp.report.ClusterReportTemplate;
import io.transwarp.report.outputByExcel.sheet.ErrorSheet;
import io.transwarp.report.outputByExcel.sheet.HdfsInfoSheet;
import io.transwarp.report.outputByExcel.sheet.HdfsMetaSheet;
import io.transwarp.report.outputByExcel.sheet.HdfsSpaceSheet;
import io.transwarp.report.outputByExcel.sheet.InodeSheet;
import io.transwarp.report.outputByExcel.sheet.LicenseSheet;
import io.transwarp.report.outputByExcel.sheet.MetricSheet;
import io.transwarp.report.outputByExcel.sheet.NodeInfoSheet;
import io.transwarp.report.outputByExcel.sheet.PortSheet;
import io.transwarp.report.outputByExcel.sheet.ProcessSheet;
import io.transwarp.report.outputByExcel.sheet.RoleMapSheet;
import io.transwarp.report.outputByExcel.sheet.TableSheet;
import io.transwarp.report.outputByExcel.sheet.VersionSheet;

public class ClusterReport extends ClusterReportTemplate {
	
	private static final Logger LOG = Logger.getLogger(ClusterReport.class);
	
	@Override
	public void outputToFile(String path) throws Exception {
		int id = 0;
		if(ClusterInformation.errorInfos.size() > 0) {
			ExcelSheetTemplate sheet = null;
			try {
				sheet = new ErrorSheet(path, "error", id);
				sheet.writeToExcel();
				id += 1;
			}catch(Exception e) {
				e.printStackTrace();
			}finally {
				if(sheet != null) {
					sheet.close();
				}
			}
		}
		String[] outputItems = PropertiesInfo.checkItems.getOutputSheet();
		for(String item : outputItems) {
			ExcelSheetTemplate sheet = null;
			try {
				LOG.info("output check item : " + item);
				sheet = getSheet(path, item, id);
				sheet.writeToExcel();
				id += 1;
			}catch(Exception e) {
				e.printStackTrace();
			}finally {
				if(sheet != null) {
					sheet.close();
				}
			}
		}
	}
	
	public ExcelSheetTemplate getSheet(String path, String name, int id) throws Exception{
		if(name.equalsIgnoreCase("version")) {
			return new VersionSheet(path, name, id);
		}else if(name.equalsIgnoreCase("roleMap")) {
			return new RoleMapSheet(path, name, id);
		}else if(name.equalsIgnoreCase("hdfsInfo")) {
			return new HdfsInfoSheet(path, name, id);
		}else if(name.equalsIgnoreCase("hdfsSpace")) {
			return new HdfsSpaceSheet(path, name, id);
		}else if(name.equalsIgnoreCase("hdfsMeta")) {
			return new HdfsMetaSheet(path, name, id);
		}else if(name.equalsIgnoreCase("process")) {
			return new ProcessSheet(path, name, id);
		}else if(name.equalsIgnoreCase("tableInfo")) {
			return new TableSheet(path, name, id);
		}else if(name.equalsIgnoreCase("license")) {
			return new LicenseSheet(path, name, id);
		}else if(name.equalsIgnoreCase("nodeInfo")){
			return new NodeInfoSheet(path, name, id);
		}else if(name.equalsIgnoreCase("portInfo")) {
			return new PortSheet(path, name, id);
		}else if(name.equalsIgnoreCase("inode")) {
			return new InodeSheet(path, name, id);
		}else if(name.equalsIgnoreCase("metric")) {
			return new MetricSheet(path, name, id);
		}else {
			throw new RuntimeException("there is no this item " + name);
		}
	}
}
