package com.icss.utils;

import java.util.HashMap;
import java.util.Map;

public class DataUtil {

	public static Map<String, String> getParamsMap(Map<String, Object> inData, String interName){
		String[] conds = ((String)FileUtil.readConfigFile(interName + ".properties").get("hbase.query.condition")).split("\\,");
		Map<String, String> params = new HashMap<String, String>();
		for(String cond : conds){
			if(inData.containsKey(cond)){
				params.put(cond, (String)inData.get(cond));
			}
		}
		return params;
	}
}
