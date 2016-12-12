package com.icss.ws;
import javax.xml.ws.Endpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.icss.ws.config.ConfigManager;


public class Main {
	private static Logger Log = LoggerFactory.getLogger(Main.class);
	@SuppressWarnings("static-access")
	public static void main(String... args) {
		try {
			Endpoint endpoint = Endpoint.create(new DispatcherWebService());
			
			//暂不指定线程池，使用默认线程池
			//endpoint.setExecutor(Executors.newFixedThreadPool(10));
			endpoint.publish(ConfigManager.getInstance().WS_URL);
			Log.debug("webservice发布成功！"+ConfigManager.getInstance().WS_URL );
         	System.out.println("webservice发布成功！"+ConfigManager.getInstance().WS_URL);
		} catch (Exception e) {
			Log.debug("webservice发布失败！" + e.getMessage());
		System.out.println("webservice发布失败！" + e.getMessage());
		}
	}
}
