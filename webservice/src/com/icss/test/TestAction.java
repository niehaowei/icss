package com.icss.test;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.icss.action.ActionExecutorImpl;
import com.icss.dao.hbase.ScanBean;
import com.icss.dao.hive.HiveDao;
import com.icss.ws.bean.InfoBean;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.HashMap;

/**
 * Created by Administrator on 2016/12/12.
 */
public class TestAction extends TestCase {
    @Test
    public void testActionExecutorImplQueryByRowKey() throws Exception {

        System.out.println("" + "sssss");
        ActionExecutorImpl actionExecutorImpl = new ActionExecutorImpl();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("rowkey", "111111");
        jsonObject.addProperty("tablename", "test_demo0");
        int[] num = new int[2];
        InfoBean infoBean = new InfoBean();
        infoBean = actionExecutorImpl.executorActionHBase("", "byrowkey", jsonObject, num);
        System.out.println("00000=" + infoBean.getMessage());
    }

    @Test
    public void testHiveDaoSelectData() {
        String result = HiveDao.selectData("select * from test_demo1");

        System.out.println(result);
    }

    @Test
    public void testHiveDaoLoadData() {
        String result = HiveDao.loadData("test_demo1");

        System.out.println(result);
    }

    @Test
   public void testJsonTOMap(){
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("ss","sd");
        jsonObject.addProperty("sa","sa");
        HashMap<Object, Object> objectObjectHashMap = new HashMap<Object, Object>();
        objectObjectHashMap=gson.fromJson(jsonObject,HashMap.class);
        System.out.println(objectObjectHashMap.get("ss"));

    }

    @Test
    public  void  testHbaseDaoScanData(){
        JsonObject jsonObject = new JsonObject();
        try {
            jsonObject.addProperty("tablename","test_demo0");
            jsonObject.addProperty("cf","ps");
            ScanBean scanBean = new ScanBean();
            scanBean.setRecordNum("2");
            scanBean.setStartRowKey("111112");
            scanBean.setStopRowKey("9999999");
            scanBean.setRecordNum("1");
            Gson gson = new Gson();
            System.out.println(gson.toJson(scanBean));
            com.icss.dao.hbase.HBaseDao.getInstance().scanData(gson.toJson(scanBean), jsonObject, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}



