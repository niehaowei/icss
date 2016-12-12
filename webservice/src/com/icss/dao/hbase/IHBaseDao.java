package com.icss.dao.hbase;

import com.google.gson.JsonObject;

/**
 * 开发人员:nie_hw
 * 日期:2016年12月9日 创建
 * 功能说明:
 * 修改记录:
 *
 */
public interface IHBaseDao {

	   String  queryByRowKey(String databases, String actionType, JsonObject param);
	   String  queryByValue(String databases, String actionType, JsonObject param);
	   String  ScanTable(String databases, String actionType, JsonObject param);

}
