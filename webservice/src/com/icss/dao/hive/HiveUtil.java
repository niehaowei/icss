package com.icss.dao.hive;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import com.google.gson.Gson;
import com.icss.utils.FileUtil;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.log4j.Logger;

/**
 * Created by Administrator on 2016/12/12.
 */
public class HiveUtil {
    private static String driverName = null;
    private static String url = null;
    private static String user = null;
    private static String password = null;
    private static String sql = null;
    private static ResultSet res;
    private static final Logger log = Logger.getLogger(HiveUtil.class);
    private static Properties props = new Properties();

    /*
    * C3P0对象
    */
    private static ComboPooledDataSource cpds = new ComboPooledDataSource(true);
    /*
    * 连接池中保留的最大连接数
    */
    private static String maxpoolsize = null;
    /*
    * 连接池中保留的最少连接数
    */
    private static String minpoolsize = null;
    /*
    *当连接池中的连接耗尽的时候c3p0一次同时获取的连接数
    */
    private static String acquireincrement = null;
    /*
     *初始化时获取5个连接，取值应在minPoolSize与maxPoolSize之间
     */
    private static String initialpoolsize = null;
    /*
    *最大空闲时间,60秒内未使用则连接被丢弃。若为0则永不丢弃
    */
    private static String maxidletime = null;


    static {
        props = FileUtil.readConfigFile("hiveConfigCSS.properties");
        driverName = props.getProperty("driverName");
        url = props.getProperty("url");
        user = props.getProperty("user");
        password = props.getProperty("password");
        maxpoolsize = props.getProperty("maxpoolsize");
        minpoolsize = props.getProperty("minpoolsize");
        acquireincrement = props.getProperty("acquireincrement");
        initialpoolsize = props.getProperty("initialpoolsize");
        maxidletime = props.getProperty("maxidletime");
    }

    public static Connection getConnection() {
        Connection connection = null;
        Statement stmt = null;
        try {
            Class.forName(driverName);
            connection = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }


    public static Connection getConnectionByC3P0Pool() {
        cpds.setJdbcUrl(url);
        cpds.setUser(user);
        cpds.setPassword(password);
        cpds.setMaxPoolSize(Integer.parseInt(maxpoolsize));
        cpds.setMinPoolSize(Integer.parseInt(minpoolsize));
        cpds.setAcquireIncrement(Integer.parseInt(acquireincrement));
        cpds.setInitialPoolSize(Integer.parseInt(initialpoolsize));
        cpds.setMaxIdleTime(Integer.parseInt(maxidletime));
        try {
            cpds.setDriverClass(driverName);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }

        Connection connection = null;
        try {
            connection = cpds.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;

    }

    public static String parseResult(ResultSet resultSet) {
        String result = "";
        Map resultMap = null;
        List<Map> resultList = new ArrayList<Map>();

        if (resultSet == null) {
            return result;
        }
        try {
            while (resultSet.next()) {
                resultMap = new HashMap();
                resultMap.put("id", resultSet.getString(1));
                resultMap.put("name", resultSet.getString(2));
                resultList.add(resultMap);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        return gson.toJson(resultList);
    }
}
