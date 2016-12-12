package com.icss.test;

import com.google.gson.JsonObject;
import com.icss.action.ActionExecutorImpl;
import com.icss.dao.hive.HiveDao;
import com.icss.dao.hive.HiveUtil;
import com.icss.ws.bean.InfoBean;
import junit.framework.TestCase;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Administrator on 2016/12/12.
 */
public class TestAction extends TestCase {
    @Test
    public void testActionExecutorImplMethodQueryByRowKey() throws Exception {

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

}



