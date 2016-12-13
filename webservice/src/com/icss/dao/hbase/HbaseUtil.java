package com.icss.dao.hbase;

import java.io.IOException;
import java.util.Properties;

import com.icss.utils.FileUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;

/**
 * 开发人员:nie_hw
 * 日期:2016年12月9日 创建
 * 功能说明:
 * 修改记录:
 *
 */
public class HbaseUtil {
	private static Logger Log = LoggerFactory.getLogger(HBaseDao.class);
	private static Configuration config;
	private static Connection connection;
	private static Properties props = new Properties();
	private HbaseUtil(){}
    
	public static Configuration getConfig(){
	
	config = HBaseConfiguration.create();
		props = FileUtil.readConfigFile("hbaseConfigCSS.properties");
	try {
		if (props.getProperty("hbase.zookeeper.quorum") != null && props.getProperty("hbase.zookeeper.quorum").trim().length() > 0) {
			config.set("hbase.zookeeper.quorum",props.getProperty("hbase.zookeeper.quorum"));
			config.set("hbase.zookeeper.property.clientPort", props.getProperty("hbase.zookeeper.property.clientPort"));
			config.set("hbase.rpc.timeout", props.getProperty("hbase.connection.timeout"));
			config.set("hbase.regionserver.lease.period", props.getProperty("hbase.lease.period"));
			config.set("hbase.master", props.getProperty("hbase.master"));
			config.set("zookeeper.znode.parent", props.getProperty("zookeeper.znode.parent"));
		}
		
	} catch (Exception e) {
		e.printStackTrace();
	} 
	return config;
		
	}


public static Connection getConnection(Configuration config){
	try {
		connection = ConnectionFactory.createConnection(config);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return connection;
}
}
