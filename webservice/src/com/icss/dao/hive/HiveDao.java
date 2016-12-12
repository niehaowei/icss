package com.icss.dao.hive;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 开发人员:nie_hw
 * 日期:2016年12月9日 创建
 * 功能说明:
 * 修改记录:
 */
public class HiveDao {
    private static Logger Log = LoggerFactory.getLogger(HiveDao.class);
    private static ResultSet res;

    public static String selectData(String sql) {

        Connection conn = null;
        conn = HiveUtil.getConnectionByC3P0Pool();
        Statement statement = null;
        String result = "";
        Log.info("sql:" + sql);
        try {
            statement=conn.createStatement();
            res = statement.executeQuery(sql);
            result = HiveUtil.parseResult(res);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                statement.close();
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("ssssssssssssssssssssssssssss");
        Log.info("\n 运行结果:" + result);
        Log.error("ssssssssss");
        return result;

    }

    public  static String loadData( String tableName) {
        String filepath = "/tmp/test_demo1";
        String   sql = "load data local inpath '" + filepath + "' into table "
                + tableName;
        Connection conn = null;
        conn = HiveUtil.getConnectionByC3P0Pool();
        Statement statement = null;
        try {
            statement=conn.createStatement();
           // res=statement.executeQuery(sql);
            int num=statement.executeUpdate(sql);
            System.out.println(num);
            if(num == 0){
                System.out.println("ok update success");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "ok";
    }

}
