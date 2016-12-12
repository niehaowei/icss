package com.icss.action;

import com.google.gson.JsonObject;
import com.icss.dao.hbase.BaseDaoHBaseImpl;
import com.icss.dao.hbase.BaseDaoHBaseImpl2;
import com.icss.dao.hive.BaseDaoHiveImpl;
import com.icss.ws.bean.InfoBean;

/**
 * 开发人员:nie_hw
 * 日期:2016年12月9日 创建
 * 功能说明:
 * 修改记录:
 *
 */
public class ActionExecutorImpl implements IActionExecutor{
	
	private BaseDaoHBaseImpl  basedaohbaseimpl;
	private BaseDaoHiveImpl   basedaohiveimpl;
	public ActionExecutorImpl () {
		this.basedaohbaseimpl= BaseDaoHBaseImpl.getInstance();

		this.basedaohiveimpl=BaseDaoHiveImpl.getInstance();
	}
	


	/**
	 * 开发人员:nie_hw
	 * 创建日期:2016年12月9日
	 * 方法(重写)说明:
	 * 方法修改记录:
	 * @param datasource
	 * @param actionType
	 * @param sqlParams
	 * @return
	 */
	@Override
	
	public InfoBean executorActionHBase(String datasource, String actionType, JsonObject sqlParams,int[] pageParams) {
		InfoBean infoBean = new InfoBean();
		String result="";
		String code="";
		switch (actionType) {
		case "get":
			break;
		case "byrowkey":
			result=basedaohbaseimpl.hbasedao.queryByRowKey(datasource, actionType, sqlParams);
			break;
		case "scan":
			break;
        case "insert":
			break;
        case "update":
			break;
        case "xy":
			break;
		default:
			code="201";
			result="无符合数据源的查询";
			break;
		}
		if(result.length() == 0){
			result="无符合条件的查询";
		}
		infoBean.setMessage(result);
		return infoBean;
	}

	/**
	 * 开发人员:nie_hw
	 * 创建日期:2016年12月9日
	 * 方法(重写)说明:
	 * 方法修改记录:
	 * @param datasource
	 * @param actionType
	 * @param sqlParams
	 * @param pageParamss
	 * @return
	 */
	@Override
	public InfoBean executorActionHive(String datasource, String actionType, JsonObject sqlParams, int[] pageParamss) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 开发人员:nie_hw
	 * 创建日期:2016年12月9日
	 * 方法(重写)说明:
	 * 方法修改记录:
	 * @param datasource
	 * @param actionType
	 * @param sqlParams
	 * @param pageParamss
	 * @return
	 */
	@Override
	public InfoBean executorActionImpala(String datasource, String actionType, JsonObject sqlParams,
			int[] pageParamss) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 开发人员:nie_hw
	 * 创建日期:2016年12月9日
	 * 方法(重写)说明:
	 * 方法修改记录:
	 * @param datasource
	 * @param actionType
	 * @param sqlParams
	 * @param pageParamss
	 * @return
	 */
	@Override
	public InfoBean executorActionKylin(String datasource, String actionType, JsonObject sqlParams, int[] pageParamss) {
		// TODO Auto-generated method stub
		return null;
	}

}
