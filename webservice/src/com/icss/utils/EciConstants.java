package com.icss.utils;

/**
 * 
 * 基础参数类
 * @author 刘永浩
 * @version 1.0
 */
public class EciConstants {

	/** 平台统一使用编码 */
	
	public static final String EXPORTER_SUCCESS = "_SUCCESS";
	
	public static final String EXPORTER_TASK_STATUS[] = new String[]{"0","1","2","3","4","5","6","7"};
	
	public static final String EXPORTER_TASK_ZIPSTATUS[] = new String[]{"0","1","2","3","4","5","6","7"};
	
	public static final String HIVE_CONFIG_PATH = "hive-jdbc.properties";
	
	public static final String IMPALA_CONFIG_PATH = "impala-jdbc.properties";
	
	public static final String MYSQL_CONFIG_PATH = "mysql-jdbc.properties";
	
	public static final String DATA_SOURCE_IMPALA = "IMPALA";
	
	public static final String DATA_SOURCE_HIVE = "HIVE";
	
	public static final String DATA_SOURCE_MYSQL = "MYSQL";
	
	public static final String HBASE_DATE_TRANDATE = "tran_date";
	
	public static final String HBASE_DATE_START = "start_date";
	
	public static final String HBASE_DATE_END = "end_date";
	
	public static final String HBASE_SERVICE_CODE1 = "hods.02.1";
	
	public static final String HBASE_SERVICE_CODE2 = "hods.02.2";
	
	public static final String G_ENCODING = System.getProperty("eci_encoding", "UTF-8");

	public static final String G_ENCODING_GBK = "GBK";

	public static final String G_ENCODING_UTF8 = "UTF-8";
	
	public static final String G_ATTRIBUTE = "attribute";
	
	public static final String G_STRUCT_FLAG = "STRUCT";
	
	public static final String G_ARRAY_FLAG = "ARRAY";
	
	public static final String G_STRUCT_NODE = "struct";
	
	public static final String G_ARRAY_NODE = "array";
	
	public static final String G_DATA_NODE = "data";
	
	public static final String G_FIELD_NODE = "field";
	
	public static final String G_SYS_HEADER_NODE = "sys-header";
	
	public static final String G_APP_HEADER_NODE = "app-header";
	
	public static final String G_LOCAL_HEADER_NODE = "local-header";
	
	public static final String G_BODY_NODE = "body";
	
	public static final String G_ATTR_SEPARATOR = "@";
	
	public static final String G_DATA_AND_NAME = "data@name";
	
	public static final String G_VAR_SEPARATOR = ".";
	
	public static final String G_TRAN_CODE_FLAG = "SYS_HEAD.TRAN_CODE";
	
	public static final String G_START_KEY_FLAG = "START_ROW_KEY";
	
	public static final String G_STOP_KEY_FLAG = "STOP_ROW_KEY";
	
	public static final String G_RECORD_NUM_FLAG = "RECORD_NUM";
	
	public static final String G_EXCHANGE_ARRAY_FLAG = "EXCHANGE_ARRAY";
}
