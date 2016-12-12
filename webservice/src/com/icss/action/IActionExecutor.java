package com.icss.action;

import com.google.gson.JsonObject;
import com.icss.datasource.IDataSource;
import com.icss.ws.bean.InfoBean;


/**
 * 开发人员:nie_hw
 * 日期:2016年12月9日 创建
 * 功能说明:支持的数据源
 * 修改记录:
 *
 */
public  interface IActionExecutor {

	InfoBean executorActionHBase(String datasource, String actionType, JsonObject sqlParams, int[] pageParamss);
	InfoBean executorActionHive(String datasource, String actionType, JsonObject sqlParams, int[] pageParamss);
	InfoBean executorActionImpala(String datasource, String actionType, JsonObject sqlParams, int[] pageParamss);
	InfoBean executorActionKylin(String datasource, String actionType, JsonObject sqlParams, int[] pageParamss);

}
