package com.icss.dao.hbase;

import com.google.gson.JsonObject;

/**
 * 开发人员:nie_hw
 * 日期:2016年12月9日 创建
 * 功能说明:
 * 修改记录:
 *
 */
public class HBaseDao2 implements IHBaseDao{

	/**
	 * 开发人员:nie_hw
	 * 创建日期:2016年12月9日
	 * 方法(重写)说明:
	 * 方法修改记录:
	 * @param databases
	 * @param actionType
	 * @param param
	 * @return
	 */
	@Override
	public String queryByRowKey(String databases, String actionType, JsonObject param) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 开发人员:nie_hw
	 * 创建日期:2016年12月9日
	 * 方法(重写)说明:
	 * 方法修改记录:
	 * @param databases
	 * @param actionType
	 * @param param
	 * @return
	 */
	@Override
	public String queryByValue(String databases, String actionType, JsonObject param) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 开发人员:nie_hw
	 * 创建日期:2016年12月9日
	 * 方法(重写)说明:
	 * 方法修改记录:
	 * @param databases
	 * @param actionType
	 * @param param
	 * @return
	 */
	@Override
	public String ScanTable(String databases, String actionType, JsonObject param) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String queryVerify(String databases, String actionType, JsonObject param){
		return "";
		
	}

}
