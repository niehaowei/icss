package com.icss.dao.hbase;


public  class BaseDaoHBaseImpl2 {
	
	public HBaseDao hbasedao;
	public HBaseDao2 hbasedao2;
	
	public static BaseDaoHBaseImpl2 getInstance(){
		return new BaseDaoHBaseImpl2();
	}
	public BaseDaoHBaseImpl2(){
		hbasedao=HBaseDao.getInstance();
		hbasedao2= new HBaseDao2();
	}
	
}
