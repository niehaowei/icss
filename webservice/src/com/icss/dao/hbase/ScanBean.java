package com.icss.dao.hbase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 用户封装查询结果
 */
public class ScanBean {
	
	public List<Map<String, String>> results = new ArrayList<Map<String, String>>();
	/**
	 * 每页的行数
	 */
	public String recordNum;
	/**
	 * 开始rowkey
	 */
	
	public String startRowKey;

	/**
	 * 结束rowkey，也是分页查询的下一页起始rowkey
	 */
	public String stopRowKey;
	
	public String output;
	
	public String fileName;

	/**
	 * 查询返回的提示信息
	 */
	public String retMsg;

	/**
	 * 查询返回的提示码
	 */
	public String retCode;
	
	public String getRetCode() {
		return retCode;
	}
	public void setRetCode(String retCode) {
		this.retCode = retCode;
	}
	public String getOutput() {
		return output;
	}
	public void setOutput(String output) {
		this.output = output;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getRetMsg() {
		return retMsg;
	}
	public void setRetMsg(String retMsg) {
		this.retMsg = retMsg;
	}
	public List<Map<String, String>> getResults() {
		return results;
	}
	public void setResults(List<Map<String, String>> results) {
		this.results = results;
	}
	public String getRecordNum() {
		return recordNum;
	}
	public void setRecordNum(String recordNum) {
		this.recordNum = recordNum;
	}
	public String getStartRowKey() {
		return startRowKey;
	}
	public void setStartRowKey(String startRowKey) {
		this.startRowKey = startRowKey;
	}
	public String getStopRowKey() {
		return stopRowKey;
	}
	public void setStopRowKey(String stopRowKey) {
		this.stopRowKey = stopRowKey;
	}
}
