package io.transwarp.bean;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.sf.json.JSONObject;
import io.transwarp.connTool.HdfsUtil;
import io.transwarp.util.TypeChangeUtil;

public class TableBean {

	private String table_name;			//表名
	private String database_name;		//所属数据库名
	private String table_type;			//内表(MANAGED_TABLE)还是外表(EXTERNAL_TABLE)
	private String transactional;		//是否为事物表
	private String table_format;		//表的存储文件格式
	private String table_location;		//表的物理存放位置
	private String owner_name;			//表的拥有者
	private long maxDirSize;			//最大文件夹大小
	private long minDirSize;			//最小文件夹大小
	private long sumDirSize;			//总文件夹大小
	private Integer countDirNumber;		//最下层文件夹个数
	private long maxFileSize;			//最大文件大小
	private long minFileSize;			//最小文件大小
	private long sumFileSize;			//文件总大小
	private Integer countFileNumber;	//文件总数
	
	public TableBean() {
		super();
		this.maxDirSize = 0;
		this.minDirSize = Long.MAX_VALUE;
		this.sumDirSize = 0;
		this.countDirNumber = 0;
		this.maxFileSize = 0;
		this.minFileSize = Long.MAX_VALUE;
		this.sumFileSize = 0;
		this.countFileNumber = 0;
	}
	
	public String getTableType() {
		String tableType;
		String transaction;
		/* 记录是否为外表 */
		if(table_type.equals("EXTERNAL_TABLE")) {
			tableType = "外表";
		}else {
			tableType = "表";
		}
		/* 记录是否为事物表 */
		if(transactional != null && transactional.equals("true")) {
			transaction = "事务";
		}else {
			transaction = "";
		}
		return table_format + transaction + tableType;
	}
	
	@Override
	public String toString() {
		JSONObject json = new JSONObject();
		Class<?> clazz = this.getClass();
		Field[] fields = clazz.getDeclaredFields();
		for(Field field : fields) {
			String name = field.getName();
			try {
				Method getMethod = clazz.getDeclaredMethod("get" + TypeChangeUtil.changeFirstCharToCapital(name));
				Object value = getMethod.invoke(this, new Object[]{});
				if(value == null) continue;
				json.put(name, value.toString());
			} catch (Exception e) {	}
		}
		return json.toString();
	}

	public String getTable_name() {
		return table_name;
	}
	public void setTable_name(Object table_name) {
		if(table_name == null)
			table_name = "";
		this.table_name = table_name.toString();
	}
	public String getDatabase_name() {
		return database_name;
	}
	public void setDatabase_name(Object database_name) {
		if(database_name == null)
			database_name = "";
		this.database_name = database_name.toString();
	}
	public void setTable_type(Object table_type) {
		if(table_type == null)
			table_type = "";
		this.table_type = table_type.toString();
	}
	public void setTransactional(Object transactional) {
		if(transactional == null)
			transactional = "";
		this.transactional = transactional.toString();
	}
	public void setTable_format(Object table_format) {
		if(table_format == null)
			table_format = "";
		this.table_format = table_format.toString();
	}
	public String getTable_location() {
		return table_location;
	}
	public void setTable_location(Object table_location) {
		if(table_location == null)
			table_location = "";
		this.table_location = HdfsUtil.getHdfsPath(table_location.toString());
	}
	public String getOwner_name() {
		return owner_name;
	}
	public void setOwner_name(Object owner_name) {
		if(owner_name == null)
			owner_name = "";
		this.owner_name = owner_name.toString();
	}
	public long getMaxDirSize() {
		if(countDirNumber == 0)
			return -1;
		return maxDirSize;
	}
	public void setMaxDirSize(long maxDirSize) {
		this.maxDirSize = maxDirSize;
	}
	public long getMinDirSize() {
		if(minDirSize == Long.MAX_VALUE)
			return -1;
		return minDirSize;
	}
	public void setMinDirSize(long minDirSize) {
		this.minDirSize = minDirSize;
	}
	public long getSumDirSize() {
		if(countDirNumber == 0)
			return -1;
		return sumDirSize;
	}
	public void setSumDirSize(long sumDirSize) {
		this.sumDirSize = sumDirSize;
	}
	public Integer getCountDirNumber() {
		return countDirNumber;
	}
	public void setCountDirNumber(Integer countDirNumber) {
		this.countDirNumber = countDirNumber;
	}
	public long getMaxFileSize() {
		if(countFileNumber == 0)
			return -1;
		return maxFileSize;
	}
	public void setMaxFileSize(long maxFileSize) {
		this.maxFileSize = maxFileSize;
	}
	public long getMinFileSize() {
		if(minFileSize == Long.MAX_VALUE)
			return -1;
		return minFileSize;
	}
	public void setMinFileSize(long minFileSize) {
		this.minFileSize = minFileSize;
	}
	public long getSumFileSize() {
		if(countFileNumber == 0)
			return -1;
		return sumFileSize;
	}
	public void setSumFileSize(long sumFileSize) {
		this.sumFileSize = sumFileSize;
	}
	public Integer getCountFileNumber() {
		return countFileNumber;
	}
	public void setCountFileNumber(Integer countFileNumber) {
		this.countFileNumber = countFileNumber;
	}
	public void addOneDirSize(long dirSize) {
		this.sumDirSize += dirSize;
		this.countDirNumber += 1;
		this.maxDirSize = Math.max(maxDirSize, dirSize);
		this.minDirSize = Math.min(minDirSize, dirSize);
	}
	public void addOneFileSize(long fileSize) {
		this.sumFileSize += fileSize;
		this.countFileNumber += 1;
		this.maxFileSize = Math.max(maxFileSize, fileSize);
		this.minFileSize = Math.min(minFileSize, fileSize);
	}
}
