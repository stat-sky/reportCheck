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
	public static final String QUERY_TABLE_MYSQL = "select tbls.TBL_NAME AS table_name, DBS.NAME AS database_name, tbls.TBL_TYPE AS table_type, tbls.OWNER AS owner_name, (case when isnull(temp_transactional_v.param_value) then 'false' else temp_transactional_v.param_value end) AS transactional, (case when (SDS.INPUT_FORMAT = 'org.apache.hadoop.mapred.TextInputFormat') then 'text' when (SDS.INPUT_FORMAT = 'org.apache.hadoop.mapred.SequenceFileInputFormat') then 'sequence' when (SDS.INPUT_FORMAT = 'org.apache.hadoop.hive.ql.io.orc.OrcInputFormat') then 'orc' when (SDS.INPUT_FORMAT = 'io.transwarp.inceptor.memstore2.MemoryTableInputFormat') then 'memory' when (SDS.INPUT_FORMAT = 'io.transwarp.hyperdrive.HyperdriveInputFormat') then 'hyperdrive' when (SDS.INPUT_FORMAT = 'org.apache.hadoop.hive.ql.io.parquet.MapredParquetInputFormat') then 'parquet' when (SDS.INPUT_FORMAT = 'org.apache.hadoop.hive.ql.io.RCFileInputFormat') then 'rcfile' when (SDS.INPUT_FORMAT = 'io.transwarp.hyperbase.HyperbaseInputFormat') then 'hyperbase' when (SDS.INPUT_FORMAT = 'org.apache.hadoop.hive.hbase.HiveHBaseTableInputFormat') then 'hbase' else SDS.INPUT_FORMAT end) AS table_format, SDS.LOCATION AS table_location from (((((((((select TBLS.TBL_ID AS TBL_ID, TBLS.CREATE_TIME AS CREATE_TIME, TBLS.DB_ID AS DB_ID, TBLS.LAST_ACCESS_TIME AS LAST_ACCESS_TIME, TBLS.OWNER AS OWNER, TBLS.RETENTION AS RETENTION, TBLS.SD_ID AS SD_ID, TBLS.TBL_NAME AS TBL_NAME, TBLS.TBL_TYPE AS TBL_TYPE, TBLS.VIEW_EXPANDED_TEXT AS VIEW_EXPANDED_TEXT, TBLS.VIEW_ORIGINAL_TEXT AS VIEW_ORIGINAL_TEXT, TBLS.IS_RANGE_PARTITIONED AS IS_RANGE_PARTITIONED, TBLS.RLS_ENABLED AS RLS_ENABLED, TBLS.ROW_PERMISSION AS ROW_PERMISSION, TBLS.CLS_ENABLED AS CLS_ENABLED, TBLS.COLUMN_PERMISSION AS COLUMN_PERMISSION, TBLS.LINK_TARGET_ID AS LINK_TARGET_ID from TBLS where (TBLS.TBL_TYPE <> 'VIRTUAL_VIEW')) tbls join DBS on(tbls.DB_ID = DBS.DB_ID)) join SDS on(tbls.SD_ID = SDS.SD_ID)) left join (select TABLE_PARAMS.TBL_ID AS tbl_id,TABLE_PARAMS.PARAM_VALUE AS param_value from TABLE_PARAMS where (TABLE_PARAMS.PARAM_KEY = 'comment')) temp_comment_v on(tbls.TBL_ID = temp_comment_v.tbl_id)) left join (select TABLE_PARAMS.TBL_ID AS tbl_id,TABLE_PARAMS.PARAM_VALUE AS param_value from TABLE_PARAMS where (TABLE_PARAMS.PARAM_KEY = 'transactional')) temp_transactional_v on(tbls.TBL_ID = temp_transactional_v.tbl_id)) left join (select TABLE_PARAMS.TBL_ID AS tbl_id,TABLE_PARAMS.PARAM_VALUE AS param_value from TABLE_PARAMS where (TABLE_PARAMS.PARAM_KEY = 'hbase.table.name')) temp_hbase_v on(tbls.TBL_ID = temp_hbase_v.tbl_id)) left join (select SERDE_PARAMS.SERDE_ID AS serde_id,SERDE_PARAMS.PARAM_VALUE AS param_value from SERDE_PARAMS where (SERDE_PARAMS.PARAM_KEY = 'field.delim')) temp_field_delim_v on(SDS.SERDE_ID = temp_field_delim_v.serde_id)) left join (select SERDE_PARAMS.SERDE_ID AS serde_id,SERDE_PARAMS.PARAM_VALUE AS param_value from SERDE_PARAMS where (SERDE_PARAMS.PARAM_KEY = 'line.delim')) temp_line_delim_v on(SDS.SERDE_ID = temp_line_delim_v.serde_id)) left join (select SERDE_PARAMS.SERDE_ID AS serde_id,SERDE_PARAMS.PARAM_VALUE AS param_value from SERDE_PARAMS where (SERDE_PARAMS.PARAM_KEY = 'collection.delim')) temp_collection_delim_v on(SDS.SERDE_ID = temp_collection_delim_v.serde_id))";
}
