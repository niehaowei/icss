package com.icss.ws;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


import javax.annotation.Resource;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.icss.action.ActionExecutorImpl;
import com.icss.action.IActionExecutor;
import com.icss.datasource.IDataSource;
import com.icss.ws.bean.InfoBean;
import com.icss.ws.config.ConfigManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.xml.internal.ws.developer.JAXWSProperties;

/**
 * WebService分发器实现类
 * @author freedom.xie
 *
 */
@WebService
public class DispatcherWebService implements IDispatcherWebService {
	private static Logger Log = LoggerFactory.getLogger(DispatcherWebService.class);
	private final static Gson gson = new Gson();
	@Resource
	private WebServiceContext wsContext;
	private IActionExecutor executorAction ;
	public DispatcherWebService(){
		executorAction = new ActionExecutorImpl();
	}
	
	/**
	 * 获取访问的IP地址
	 * @return
	 */
	private String getAccessIP() {
		MessageContext mc = wsContext.getMessageContext();
		HttpExchange exchange = (HttpExchange) mc.get(JAXWSProperties.HTTP_EXCHANGE);
		InetSocketAddress isa = exchange.getRemoteAddress();
	    return isa.getAddress().getHostAddress();
	}

	/**
	 * 检查访问IP是否合法
	 * @return
	 */
	@SuppressWarnings("static-access")
	private boolean ipFilter(){
		String accessIP = this.getAccessIP();
		Log.debug("Access address is:"+accessIP);
		for (String ip : ConfigManager.getInstance().IP_FILTER) {
			if (accessIP.equals(ip)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String handle(String datasource , String queryType, String  sqlParam, int[] pageParams) {
		JsonObject sqlParams =gson.fromJson(sqlParam, JsonObject.class);
		System.out.println(sqlParams);
		Log.debug("webserice command is ["+ queryType +"]，sqlParam is ["+ sqlParams +"]");
		InfoBean info = null;
		switch(datasource) {
		case "HBASE":
			info = (InfoBean) executorAction.executorActionHBase(datasource,queryType, sqlParams,pageParams);
			break;
		case "HIVE":
			info = executorAction.executorActionHive(datasource,queryType, sqlParams,pageParams);
			break;
		case "IMPALA":
			/*预留处理
			info = executorAction.executorActionImpala(datasource,queryType, sqlParams,pageParams);*/
			break;
		case "KYLIN":
			/*预留处理
			info = executorAction.executorActionKylin(datasource,queryType, sqlParams,pageParams);*/
			break;
		
		/*---------------------------------------------------------------*/
		default: 
			info = new InfoBean();
			info.setCode(InfoBean.FAILURE);
			info.setMessage("没有对应的服务！");
			break;
		}
		return gson.toJson(info);
	}

	/**
	 * 开发人员:nie_hw
	 * 创建日期:2016年12月8日
	 * 方法(重写)说明:
	 * 方法修改记录:
	 * @param queryType
	 * @param sql
	 * @param sqlParams
	 * @param pageParams
	 * @return
	 */
	
}
