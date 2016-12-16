package com.icss.dao.hbase;

/*
组合类。
如果需要重写HBaseDao，建议以修改组合类的组成，达到扩展的目的。
 */
public  class BaseDaoHBaseImpl {
	/**
	 * Hbase 的查询dao
	 */
	public HBaseDao hbasedao;
	/**
	 * Hbase 的扩展dao ,目前未使用
	 */
	public HBaseDao2 hbasedao2;
	
	public static BaseDaoHBaseImpl getInstance(){
		return new BaseDaoHBaseImpl();
	}
	public BaseDaoHBaseImpl(){
		hbasedao=HBaseDao.getInstance();


	}
	
}
