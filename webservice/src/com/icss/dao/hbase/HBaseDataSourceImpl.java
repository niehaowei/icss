package com.icss.dao.hbase;

import com.icss.datasource.IDataSource;

/**
 * 开发人员:nie_hw
 * 日期:2016年12月9日 创建
 * 功能说明:
 * 修改记录:
 *
 */
public class HBaseDataSourceImpl implements IDataSource{

	/**
	 * 开发人员:nie_hw
	 * 创建日期:2016年12月9日
	 * 方法(重写)说明:
	 * 方法修改记录:
	 * @return
	 * 
	 */
	IHBaseDao ibasedao=null;
	@Override
	public IHBaseDao getDataSourceInstance(String string) {
		if (string.equals("Hbase")){
			return new HBaseDao();
		}else{
			return null;
		}
		 
		
	}

}
