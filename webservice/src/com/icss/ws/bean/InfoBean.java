package com.icss.ws.bean;

import java.util.Map;

import spark.page.easyui.Page;

public class InfoBean {
	/**成功的状态标示位*/
	public static final String SUCCESS = "1";
	/**失败的状态标示位*/
	public static final String FAILURE = "2";
	/**警告的状态标示位*/
	public static final String WARN = "3";	
	/**代码值*/
	private String code = SUCCESS;
	/**返回消息内容*/
	private String message;
	/**分页查询结果*/
	private Page page;
	/**返回的分页信息*/
	private Map<String , String> pageInfo;

	public Map<String, String> getPageInfo() {
		return pageInfo;
	}

	public void setPageInfo(Map<String, String> pageInfo) {
		this.pageInfo = pageInfo;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}
}
