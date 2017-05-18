package io.transwarp.report.outputByExcel.sheet;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;

import jxl.write.Label;
import io.transwarp.information.ClusterInformation;
import io.transwarp.information.PropertiesInfo;
import io.transwarp.report.outputByExcel.ExcelSheetTemplate;

public class ProcessSheet extends ExcelSheetTemplate {

	public ProcessSheet(String path, String name, int id) throws Exception {
		super(path, name, id);
	}
	
	@Override
	public void writeToExcel() throws Exception {
		int row = 1;
		int maxColumn = 0;
		for(Iterator<String> keys = ClusterInformation.serviceProcessInfos.keySet().iterator(); keys.hasNext(); ) {
			String key = keys.next();
			Map<String, String> processChecks = ClusterInformation.serviceProcessInfos.get(key);
			String[] topics = key.split(":");
			Element config = PropertiesInfo.prop_process.getElementByKeyValue("serviceRoleType", topics[1]);
			Element properties = config.element("properties");
			List<?> props = properties.elements();
			for(Object prop : props) {  //按配置切分过滤结果
				Element checkItem = (Element)prop;
				String name = checkItem.elementText("name");
				String result = processChecks.get(name);
				sheet.mergeCells(0, row, 6, row);
				sheet.addCell(new Label(0, row, "服务:" + topics[0] + ", 角色: " + topics[1] + ", 检查项: " + name, cellFormat));
				row += 1;
				String splitKeys = checkItem.elementText("key");
				if(splitKeys == null || splitKeys.equals("")) {  //切分过滤要求为空，则所有内容按表格输出
					String[] lines = result.split("\n");
					for(String line : lines) {
						int column = 1;
						line = line.replaceAll("\t", " ").trim();
						if(line.equals("") || (line.startsWith("-") && line.endsWith("-"))) {
							continue;
						}
						String[] items = line.split("\\s+");
						for(String item : items) {
							sheet.addCell(new Label(column, row, item, cellFormat));
							column += 1;
						}
						row += 1;
						maxColumn = Math.max(maxColumn, column);
					}
					row += 1;
				}else {
					String values = analysis(result, splitKeys);
					String[] lines = values.split("\n");
					for(String line : lines) {
						sheet.addCell(new Label(1, row, line, cellFormat));
						sheet.mergeCells(1, row, 3, row);
						row += 1;
					}
				}
				row += 2;
			}
		}
		for(int i = 1; i <= maxColumn; i++) {
			sheet.setColumnView(i, cellView);
		}
	}
	
	private String analysis(String result, String key) {
		StringBuffer answer = new StringBuffer();
		String[] lines = result.split("\n");
		String[] items = key.split(";");
		for(String line : lines) {
			/* 取一行开头作为行关键字用于查询 */
			int equalsIndex = line.indexOf("=");
			if(equalsIndex == -1) {
				equalsIndex = line.length();
			}
			String lineKey = line.substring(0, equalsIndex);
			for(String item : items) {
				int index = item.indexOf("(");
				if(index == -1) {
					/* 没有要选取的子项，则直接取关键字对该行进行判断 */
					if(lineKey.indexOf(item) != -1) {
						answer.append(line).append("\n");
					}
				}else {
					/* 有需要选取的子项，所以先对关键字解析 */
					String itemKey = item.substring(0, index);
					if(lineKey.indexOf(itemKey) != -1) {
						answer.append(itemKey).append(":");
						/* 去掉括号并按照逗号分割 */
						String[] props = item.substring(index, item.length() - 1).split(",");
						String[] lineItems = line.split(" ");
						/* 遍历所有子项，判断是否需要 */
						for(String lineItem : lineItems) {
							for(String prop : props) {
								if(lineItem.indexOf(prop) != -1) {
									answer.append(lineItem).append(" ");
								}
							}
						}
					}
				}
				
			}
		}
		return answer.toString();
	}
}
