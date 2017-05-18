package io.transwarp.report.outputByExcel.sheet;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import jxl.write.Label;
import io.transwarp.bean.restapiInfo.MetricBean;
import io.transwarp.information.ClusterInformation;
import io.transwarp.information.Constant;
import io.transwarp.report.outputByExcel.ExcelSheetTemplate;

public class MetricSheet extends ExcelSheetTemplate {

	public MetricSheet(String path, String name, int id) throws Exception {
		super(path, name, id);
	}
	
	@Override
	public void writeToExcel() throws Exception {
		int column = 2;
		boolean isFirstNode = true;
		Vector<String> metricList = new Vector<String>();
		for(Iterator<String> hostnames = ClusterInformation.metricByRestAPIs.keySet().iterator(); hostnames.hasNext(); ) {
			String hostname = hostnames.next();
			Map<String, MetricBean> metrics = ClusterInformation.metricByRestAPIs.get(hostname);
			int row = 1;
			if(isFirstNode) {
				for(Iterator<String> metricNames = metrics.keySet().iterator(); metricNames.hasNext();) {
					String metricName = metricNames.next();
					metricList.add(metricName);
					MetricBean metric = metrics.get(metricName);
					sheet.addCell(new Label(0, row++, metric.getMetricName(), cellFormat));
					sheet.addCell(new Label(column - 1, row, "timestamp", cellFormat));
					sheet.addCell(new Label(column, row++, hostname, cellFormat));
					String unit = metric.getUnit();
					List<String> values = metric.getMetricValues();
					for(String value : values) {
						String[] items = value.split(":");
						Date date = new Date(Long.valueOf(items[0]));
						sheet.addCell(new Label(column - 1, row, Constant.DATE_FORMAT.format(date), cellFormat));
						sheet.addCell(new Label(column, row++, items[1] + " " + unit, cellFormat));
					}
					row += 1;
				}
				isFirstNode = false;
			}else {
				for(String metricName : metricList) {
					MetricBean metric = metrics.get(metricName);
					row += 1;
					sheet.addCell(new Label(column, row++, hostname, cellFormat));
					String unit = metric.getUnit();
					List<String> values = metric.getMetricValues();
					for(String value : values) {
						String[] items = value.split(":");
						sheet.addCell(new Label(column, row++, items[1] + " " + unit, cellFormat));
					}
					row += 1;
				}
			}
			column += 1;
		}
		for(int i = 0; i < column; i++) {
			sheet.setColumnView(i, cellView);
		}
	}
}
