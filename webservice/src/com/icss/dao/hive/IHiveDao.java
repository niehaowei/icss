package com.icss.dao.hive;

import com.google.gson.JsonObject;

/**
 * 开发人员:nie_hw
 * 日期:2016年12月9日 创建
 * 功能说明:
 * 修改记录:
 *
 */
interface IHiveDao {

	   String  query(String databases, String actionType, JsonObject param);
	   String  insert(String databases, String actionType, JsonObject param);
	   String  load(String databases, String actionType, JsonObject param);
}
