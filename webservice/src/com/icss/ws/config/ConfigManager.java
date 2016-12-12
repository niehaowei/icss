package com.icss.ws.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.icss.ws.common.CommonUtils;




/**
 * 配置管理类,根据需求进行添加
 * @author freedom.xie
 *
 */
public class ConfigManager {
	private final static ConfigManager instance = new ConfigManager();
	//监控服务器IP
	public static String WS_URL;
	//监控服务器端口
	public static String[] IP_FILTER;
	//线程池大小
	public static int THREAD_POOL_NUM;
	//灵活查询的数据库
	public static String IMPALA_SCHEMA;
	
	
	public static ConfigManager getInstance(){
		return instance;
	}
	private ConfigManager(){
		Properties config = new Properties();
		try {
			config.load(new FileInputStream(CommonUtils.getResourcesFile("config.properties")));
			WS_URL = config.getProperty("WS_URL");
			IP_FILTER = config.getProperty("IP_FILTER").split("\\;");
			THREAD_POOL_NUM = Integer.parseInt(config.getProperty("THREAD_POOL_NUM"));
			IMPALA_SCHEMA = config.getProperty("IMPALA_SCHEMA");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
