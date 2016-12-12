package com.icss.dao.hbase;


public  class BaseDaoHBaseImpl {
	
	public HBaseDao hbasedao;
	public HBaseDao2 hbasedao2;
	
	public static BaseDaoHBaseImpl getInstance(){
		return new BaseDaoHBaseImpl();
	}
	public BaseDaoHBaseImpl(){
		hbasedao=HBaseDao.getInstance();
		hbasedao2= new HBaseDao2();
	}
	
}
