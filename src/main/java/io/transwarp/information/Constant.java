package io.transwarp.information;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class Constant {
	
	public static final String USER_LOGIN = "用户登录";
	public static final String USER_LOGOUT = "用户登出";
	public static final String FIND_SERVICE = "查询服务";
	public static final String FIND_MORE_SERVICE = "查询多个服务";
	public static final String FIND_MORE_SERVICE_ROLE = "查询多个服务角色";
	public static final String FIND_NODE = "查询节点";
	public static final String FIND_MORE_NODE = "查询多个节点";
	public static final String NODE_METRIC = "节点指标查询";
	/** 一天的毫秒数 */
	public static final int ONEDAYTIME = 24*3600*1000;
	/** 日期格式设置 */
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/** 浮点数小数位设置 */
	public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#0.00");
	/** 编码格式 */
	public static final String ENCODING = "UTF-8";
	/** 日志检测脚本在本地的路径 */
	public static final String[] LOGCHECKPAHT = new String[]{"script/logCheck.jar", "config/logCheck.xml"};
	/** 数据字典查询语句 */
	public static final String QUERY_TABLE_INCEPTOR = "select database_name, table_name, table_type, transactional, table_format, table_location, owner_name from tables_v;";
	/** 单位 */
	public static final String[] UNITS = new String[]{"B", "KB", "MB", "GB", "TB", "PB"};
	/** mysql数据库查询元数据 */
	public static final String QUERY_TABLE_MYSQL = "SELECT tbls.TBL_NAME AS table_name, DBS.NAME AS database_name, tbls.TBL_TYPE AS table_type, tbls.OWNER AS owner_name, (CASE WHEN isnull(temp_transactional_v.param_value) THEN 'false' ELSE temp_transactional_v.param_value END) AS transactional, (CASE WHEN (SDS.INPUT_FORMAT = 'org.apache.hadoop.mapred.TextInputFormat') THEN 'text' WHEN (SDS.INPUT_FORMAT = 'org.apache.hadoop.mapred.SequenceFileInputFormat') THEN 'sequence' WHEN (SDS.INPUT_FORMAT = 'org.apache.hadoop.hive.ql.io.orc.OrcInputFormat') THEN 'orc' WHEN (SDS.INPUT_FORMAT = 'io.transwarp.inceptor.memstore2.MemoryTableInputFormat') THEN 'memory' WHEN (SDS.INPUT_FORMAT = 'io.transwarp.hyperdrive.HyperdriveInputFormat') THEN 'hyperdrive' WHEN (SDS.INPUT_FORMAT = 'org.apache.hadoop.hive.ql.io.parquet.MapredParquetInputFormat') THEN 'parquet' WHEN (SDS.INPUT_FORMAT = 'org.apache.hadoop.hive.ql.io.RCFileInputFormat') THEN 'rcfile' WHEN (SDS.INPUT_FORMAT = 'io.transwarp.hyperbase.HyperbaseInputFormat') THEN 'hyperbase' WHEN (SDS.INPUT_FORMAT = 'org.apache.hadoop.hive.hbase.HiveHBaseTableInputFormat') THEN 'hbase' ELSE SDS.INPUT_FORMAT END) AS table_format, SDS.LOCATION AS table_location FROM((((SELECT TBLS.DB_ID AS DB_ID, TBLS.SD_ID AS SD_ID, TBLS.TBL_ID AS TBL_ID, TBLS.OWNER AS OWNER, TBLS.TBL_NAME AS TBL_NAME, TBLS.TBL_TYPE AS TBL_TYPE FROM TBLS WHERE(TBLS.TBL_TYPE <> 'VIRTUAL_VIEW')) tbls JOIN DBS ON (tbls.DB_ID = DBS.DB_ID)) JOIN SDS ON (tbls.SD_ID = SDS.SD_ID)) LEFT JOIN(SELECT TABLE_PARAMS.TBL_ID AS tbl_id, TABLE_PARAMS.PARAM_VALUE AS param_value FROM TABLE_PARAMS WHERE(TABLE_PARAMS.PARAM_KEY = 'transactional')) temp_transactional_v ON (tbls.TBL_ID = temp_transactional_v.tbl_id))";
}
