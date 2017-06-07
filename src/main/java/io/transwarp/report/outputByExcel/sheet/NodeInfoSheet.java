package io.transwarp.report.outputByExcel.sheet;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;

import jxl.write.Label;
import io.transwarp.bean.restapiInfo.NodeBean;
import io.transwarp.information.ClusterInformation;
import io.transwarp.information.PropertiesInfo;
import io.transwarp.report.outputByExcel.ExcelSheetTemplate;

public class NodeInfoSheet extends ExcelSheetTemplate {

	public NodeInfoSheet(String path, String name, int id) throws Exception {
		super(path, name, id);
	}
	
	@Override
	public void writeToExcel() throws Exception {
		int usedRow = writeNtpInfo(1);
		int row = addNodeInfoTopic(usedRow + 2);
		writeNodeInfo(usedRow + 2);
		writeServiceStatue(row + 2);
	}
	
	protected int addNodeInfoTopic(int row) throws Exception {
		sheet.addCell(new Label(0, row++, "节点基本信息", cellFormat));
		sheet.addCell(new Label(1, row++, "rackName", cellFormat));
		sheet.addCell(new Label(1, row++, "hostname", cellFormat));
		sheet.addCell(new Label(1, row++, "ipAddress", cellFormat));
		sheet.addCell(new Label(1, row++, "isManaged", cellFormat));
		sheet.addCell(new Label(1, row++, "clusterName", cellFormat));
		sheet.addCell(new Label(1, row++, "status", cellFormat));
		sheet.addCell(new Label(1, row++, "numCores", cellFormat));
		sheet.addCell(new Label(1, row++, "totalPhysMemBytes", cellFormat));
		sheet.addCell(new Label(1, row++, "cpu", cellFormat));
		Element config = PropertiesInfo.prop_nodeCheckOfShell.getElementByKeyValue("topic", "OS");
		Element properties = config.element("properties");
		List<?> props = properties.elements();
		for(Object prop : props) {
			Element item = (Element)prop;
			String parameter = item.elementText("parameter");
			sheet.addCell(new Label(1, row++, parameter, cellFormat));
		}
		return row;
	}
	
	protected void writeNodeInfo(int beginRow) throws Exception {
		beginRow += 1;
		int column = 2;
		for(Iterator<String> hostnames = ClusterInformation.nodeInfoByRestAPIs.keySet().iterator(); 
				hostnames.hasNext(); ) {
			String hostname = hostnames.next();
			NodeBean node = ClusterInformation.nodeInfoByRestAPIs.get(hostname);
			Map<String, String> nodeInfos = ClusterInformation.nodeInfoByShells.get(hostname);
			int row = beginRow;
			sheet.addCell(new Label(column, row++, node.getRackName(), cellFormat));
			sheet.addCell(new Label(column, row++, hostname, cellFormat));
			sheet.addCell(new Label(column, row++, node.getIpAddress(), cellFormat));
			sheet.addCell(new Label(column, row++, node.getIsManaged(), cellFormat));
			sheet.addCell(new Label(column, row++, node.getClusterName(), cellFormat));
			sheet.addCell(new Label(column, row++, node.getStatus(), cellFormat));
			sheet.addCell(new Label(column, row++, node.getNumCores(), cellFormat));
			sheet.addCell(new Label(column, row++, node.getTotalPhysMemBytes(), cellFormat));
			sheet.addCell(new Label(column, row++, node.getCpu(), cellFormat));
			Element config = PropertiesInfo.prop_nodeCheckOfShell.getElementByKeyValue("topic", "OS");
			Element properties = config.element("properties");
			List<?> props = properties.elements();
			for(Object prop : props) {
				Element item = (Element)prop;
				String parameter = item.elementText("parameter");
				String value = nodeInfos.get(parameter).trim();
				sheet.addCell(new Label(column, row++, value, cellFormat));
			}
			column += 1;
		}
		for(int i = 0; i < column; i++) {
			sheet.setColumnView(i, cellView);
		}
	}
	
	protected int writeNtpInfo(int row) throws Exception {
		sheet.addCell(new Label(0, row++, "ntp信息", cellFormat));
		boolean isFirst = true;
		for(Iterator<String> hostnames = ClusterInformation.nodeInfoByShells.keySet().iterator(); hostnames.hasNext(); ) {
			int addRow = 0;
			String hostname = hostnames.next();
			Map<String, String> nodeInfos = ClusterInformation.nodeInfoByShells.get(hostname);
			String ntpValue = nodeInfos.get("NTP");
			String[] lines = ntpValue.split("\n");
			for(int i = 0; i < lines.length; i++) {
				lines[i] = lines[i].trim();
				if(lines[i].equals("") || (lines[i].startsWith("=") && lines[i].endsWith("="))) {
					continue;
				}
				if(i == 0) {
					if(isFirst) {
						sheet.addCell(new Label(1, row + addRow, "hostname", cellFormat));
						addNtpValueOfLine(lines[i], row + addRow);
						row += 1;
						isFirst = false;
					}else {
						continue;
					}
				}else {
					addNtpValueOfLine(lines[i], row + addRow);
					addRow += 1;
				}
			}
			if(addRow > 0) {
				sheet.mergeCells(1, row, 1, row + addRow - 1);
			}
			sheet.addCell(new Label(1, row, hostname, cellFormat));
			row = row + addRow;
		}
		return row;
	}
	
	private void addNtpValueOfLine(String line, int row) throws Exception {
		String[] items = line.split("\\s+");
		int column = 2;
		for(String item : items) {
			sheet.setColumnView(column, 15);
			sheet.addCell(new Label(column++, row, item, cellFormat));
		}
	}
	
	protected void writeServiceStatue(int beginRow) throws Exception {
		sheet.addCell(new Label(0, beginRow++, "服务状态", cellFormat));
		
		sheet.addCell(new Label(1, beginRow, "service", cellFormat));
		sheet.addCell(new Label(1, beginRow + 1, "gmetad", cellFormat));
		sheet.addCell(new Label(1, beginRow + 2, "gmond", cellFormat));
		int column = 2;
		for(Iterator<String> hostnames = ClusterInformation.nodeInfoByShells.keySet().iterator(); hostnames.hasNext(); ) {
			String hostname = hostnames.next();
			Map<String, String> nodeInfo = ClusterInformation.nodeInfoByShells.get(hostname);
			String gmetad = nodeInfo.get("gmetad");
			String gmond = nodeInfo.get("gmond");
			sheet.addCell(new Label(column, beginRow, hostname, cellFormat));
			sheet.addCell(new Label(column, beginRow + 1, gmetad.trim(), cellFormat));
			sheet.addCell(new Label(column, beginRow + 2, gmond.trim(), cellFormat));
			column += 1;
		}
	}
}
