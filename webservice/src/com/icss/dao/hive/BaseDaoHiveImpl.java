package com.icss.dao.hive;

/**
 * 开发人员:nie_hw
 * 日期:2016年12月9日 创建
 * 功能说明:
 * 修改记录:
 *
 */
public class BaseDaoHiveImpl {

	public HiveDao hivedao;
	public HiveDao2 hivedao2;
	
	public static BaseDaoHiveImpl getInstance(){
		return new BaseDaoHiveImpl();
	}
	public BaseDaoHiveImpl(){
		hivedao=new HiveDao();
		hivedao2= new HiveDao2();
	}
}
