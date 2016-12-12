package com.icss.datasource;

import com.icss.dao.hbase.IHBaseDao;

/**
 * 开发人员:nie_hw
 * 日期:2016年12月9日 创建
 * 功能说明:
 * 修改记录:
 *
 */
public interface IDataSource {

	IHBaseDao getDataSourceInstance(String string);
}
