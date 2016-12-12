package com.icss.ws;

import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.jws.WebService;

/**
 * WebService总调器接口
 * @author freedom.xie
 *
 */
@WebService
public interface IDispatcherWebService {
	/**
	 * @param queryType 查询类型
	 * @param sql 查询SQL
	 * <br>如果是HIVE/IMPALA查询，（sql必须包含ORDER BY）。
	 * <br>如果是HBASE查询，此参数无用，可为NULL。
	 * @param sqlParams 查询参数(字符串数组)
	 * <br>如果是HIVE/IMPALA查询，则依次写入查询条件。
	 * <br>如果是HBASE查询，参数[查询服务码,startRowKey,stopRowKey,rowKey1,rowKey2,rowKey3,rowKey4...]
	 * <br>rowkey的个数必须匹配表中设计的rowkey个数，对应空值传入空字符串:""。
	 * @param pageParams 分页参数，整数数组。
	 * <br>参数[当前页数, 每页条数]
	 * @return (json)
	 */
   //   public String handle(String queryType, String sql, String[] sqlParams, int[] pageParams);
      public String handle(String queryType, String sql, String sqlParams, int[] pageParams);
      
}
