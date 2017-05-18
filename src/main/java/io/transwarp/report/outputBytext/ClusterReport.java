package io.transwarp.report.outputBytext;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//import org.apache.log4j.Logger;



import java.util.Map;
import java.util.Vector;

import org.dom4j.Element;

import io.transwarp.bean.TableBean;
import io.transwarp.information.ClusterInformation;
import io.transwarp.information.Constant;
import io.transwarp.information.PropertiesInfo;
import io.transwarp.report.BuildRoleMap;
import io.transwarp.report.ClusterReportTemplate;
import io.transwarp.util.UtilTool;

public class ClusterReport extends ClusterReportTemplate {
	
//	private static final Logger LOG = Logger.getLogger(ClusterReport.class);

	@Override
	public void outputToFile(String path) throws Exception {
		FileWriter writer = new FileWriter(path);

		try {
			writer.write(getTdh_version());
			writer.write("\n\n");
		}catch(Exception e) {
			e.printStackTrace();
		}
		try {
			writer.write(getRoleMap());
			writer.write("\n\n");
		}catch(Exception e) {
			e.printStackTrace();
		}
		try {
			writer.write(getHdfsInfo());
			writer.write("\n\n");
		}catch(Exception e) {
			e.printStackTrace();
		}
		try {
			writer.write(getHdfsSpaceInfo());
			writer.write("\n\n");
		}catch(Exception e) {
			e.printStackTrace();
		}
		try {
			writer.write(getHdfsMetaDataInfo());
			writer.write("\n\n");
		}catch(Exception e) {
			e.printStackTrace();
		}
		try {
			writer.write(getProcessInfo());
			writer.write("\n\n");
		}catch(Exception e) {
			e.printStackTrace();
		}
		try {
			writer.write(getTableInfo());
			writer.write("\n\n");
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		writer.flush();
		writer.close();
	}
	
	protected String getTdh_version() {
		return "集群版本号：" + ClusterInformation.tdh_version;
	}
	
	protected String getRoleMap() {
		BuildRoleMap build = new BuildRoleMap();
		build.getBuildRoleMap();
		StringBuffer buffer = new StringBuffer();
		List<String[]> nodeMaps = new ArrayList<String[]>();
		for(Iterator<String> nodenames = ClusterInformation.roleMapOfNodes.keySet().iterator(); nodenames.hasNext(); ) {
			String nodename = nodenames.next();
			String value = ClusterInformation.roleMapOfNodes.get(nodename);
			nodeMaps.add(new String[]{nodename, value});
		}
		buffer.append(PrintToTable.changeToTable(nodeMaps, 50));
		buffer.append(PrintToTable.changeToTableBySetBorderNum(ClusterInformation.roleMapTable, 25, 2, 2));
		return buffer.toString();
	}
	
	protected String getHdfsInfo() {
		StringBuffer buffer = new StringBuffer("hdfs检测:\n");
		for(Iterator<String> topics = ClusterInformation.hdfsInfos.keySet().iterator(); topics.hasNext(); ) {
			String topic = topics.next();
			String value = ClusterInformation.hdfsInfos.get(topic);
			List<String[]> buildTableBuffer = new ArrayList<String[]>();
			String[] lines = value.split("\n");
			for(String line : lines) {
				line = line.replaceAll("\t", " ").trim();
				if(line.startsWith(".") && line.endsWith(".")) {
					continue;
				}
				int splitID = line.indexOf(": ");
				if(splitID == -1) {
					buildTableBuffer.add(new String[]{line, null});
				}else {
					buildTableBuffer.add(new String[]{line.substring(0, splitID), line.substring(splitID + 1)});
				}
			}
			buffer.append("  ").append(topic).append(":\n")
					.append(PrintToTable.changeToTable(buildTableBuffer, 50));
		}
		return buffer.toString();
	}
	
	protected String getHdfsSpaceInfo() {
		StringBuffer buffer = new StringBuffer("hdfs空间检查:\n");
		List<String[]> maps = new ArrayList<String[]>();
		maps.add(new String[]{"menu", "size"});
		for(String line : ClusterInformation.hdfsSpaceSizeInfos) {
			String[] items = line.split(":");
			if(items.length != 2) {
				continue;
			}
			String sizeOfString = UtilTool.getInteger(items[1]);
			double size = Double.valueOf(sizeOfString);
			items[1] = getSize(size);
			maps.add(items);
		}
		buffer.append(PrintToTable.changeToTable(maps, 40));
		return buffer.toString();
	}
	
	private String getSize(double number) {
		int carry = 0;
		while(number >= 1024) {
			number /= 1024;
			carry += 1;
		}
		return Constant.DECIMAL_FORMAT.format(number) + " " + Constant.UNITS[carry];
	}
	
	protected String getHdfsMetaDataInfo() {
		StringBuffer buffer = new StringBuffer("editLog连续性检查:\n");
		buffer.append(getEditLogContinuity());
		buffer.append("\nfsimage检测:\n");
		buffer.append(getFsimageInfo());
		return buffer.toString();
	}
	
	private String getEditLogContinuity() {
		List<String[]> maps = new ArrayList<String[]>();
		maps.add(new String[]{"path", "value"});
		for(Iterator<String> topics = ClusterInformation.editLogContinuity.keySet().iterator(); topics.hasNext(); ) {
			String topic = topics.next();
			List<String> values = ClusterInformation.editLogContinuity.get(topic);
			if(values.size() == 0) {
				maps.add(new String[]{topic, "continuity"});
			}else {
				StringBuffer result = new StringBuffer("no continuity, lost id is :");
				for(String value : values) {
					result.append(" ").append(value);
				}
				maps.add(new String[]{topic, result.toString()});
			}
		}
		return PrintToTable.changeToTable(maps, 50);
	}
	
	private String getFsimageInfo() {
		List<String[]> maps = new ArrayList<String[]>();
		maps.add(new String[]{"path", "value"});
		for(Iterator<String> topics = ClusterInformation.fsimageIDAndTimestamp.keySet().iterator(); topics.hasNext(); ) {
			String topic = topics.next();
			String value = ClusterInformation.fsimageIDAndTimestamp.get(topic);
			String[] lines = value.split(";");
			for(String line : lines) {
				maps.add(new String[]{topic, line});
			}
		}
		return PrintToTable.changeToTable(maps, 50);
	}
	
	protected String getProcessInfo() throws Exception {
		StringBuffer buffer = new StringBuffer("进程检测:\n");
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
				buffer.append(" 服务: ").append(topics[0])
					.append(", 角色: ").append(topics[1])
					.append(", 检查项: ").append(name).append(":\n");
				String splitKeys = checkItem.elementText("key");
				if(splitKeys == null || splitKeys.equals("")) {  //切分过滤要求为空，则所有内容按表格输出
					String[] lines = result.split("\n");
					if(lines.length == 1) {
						buffer.append("\t").append(result).append("\n\n");
						continue;
					}
					List<String[]> maps = new ArrayList<String[]>();
					for(String line : lines) {
						line = line.replaceAll("\t", " ").trim();
						if(line.equals("") || (line.startsWith("-") && line.endsWith("-"))) {
							continue;
						}
						maps.add(line.split("\\s+"));
					}
					if(maps.size() > 0) {
						buffer.append(PrintToTable.changeToTable(maps, 30)).append("\n");
					}
				}else {
					buffer.append(analysis(result, splitKeys)).append("\n\n");
				}
			}
		}
		return buffer.toString();
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
	
	protected String getTableInfo() {
		StringBuffer buffer = new StringBuffer("table check :\n");
		for(Iterator<String> servicenames = ClusterInformation.tableInfos.keySet().iterator(); servicenames.hasNext(); ) {
			String servicename = servicenames.next();
			Vector<TableBean> tables = ClusterInformation.tableInfos.get(servicename);
			int totalTable = tables.size();
			int fileTotal = 0;
			int dirTotal = 0;
			buffer.append("  " + servicename + " table number is : ").append(totalTable).append("\n");
			List<String[]> maps = new ArrayList<String[]>();
			maps.add(new String[]{"database name", "owner", "table name", "table type", 
					"director size:(max/min/number/avg)", "file size:(max/min/number/avg)"});
			for(TableBean table : tables) {
				String tableDirInfo;
				if(table.getCountDirNumber() == 0) {
					tableDirInfo = "null";
				}else {
					tableDirInfo = getSize(table.getMaxDirSize()) + "/" + 
								   getSize(table.getMinDirSize()) + "/" +
								   table.getCountDirNumber() + "/" +
								   getSize(table.getSumDirSize() / table.getCountDirNumber());
					dirTotal += table.getCountDirNumber();
				}
				String tableFileInfo;
				if(table.getCountFileNumber() == 0) {
					tableFileInfo = "null";
				}else {
					tableFileInfo = getSize(table.getMaxFileSize()) + "/" +
								    getSize(table.getMinFileSize()) + "/" +
								    table.getCountFileNumber() + "/" + 
								    getSize(table.getSumFileSize() / table.getCountFileNumber());
					fileTotal += table.getCountFileNumber();
				}
				maps.add(new String[]{table.getDatabase_name(), table.getOwner_name(), table.getTable_name(), table.getTableType(), 
						tableDirInfo, tableFileInfo});
			}
			buffer.append("    total dir number is : ").append(dirTotal)
			.append(", total file number is : ").append(fileTotal).append("\n");
			buffer.append(PrintToTable.changeToTable(maps, 40)).append("\n");

		}
		return buffer.toString();	
	}
}
