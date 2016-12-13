package com.icss.dao.hbase;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.*;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.icss.utils.EciConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.*;

import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.ipc.Server.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.icss.utils.FileUtil;
import com.icss.ws.bean.InfoBean;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;

/**
 * @author freedom.xie
 * @version 1.0 HBase数据访问对象封装,以单例模式进行创建
 */
public class HBaseDao implements IHBaseDao {
    private static Logger Log = LoggerFactory.getLogger(HBaseDao.class);
    private final static HBaseDao instance = new HBaseDao();
    private static Configuration config;
    private static Connection connection;
    private static Properties props = new Properties();

    public HBaseDao() {
        this.config = HbaseUtil.getConfig();
    }

    public static HBaseDao getInstance() {
        return instance;
    }


    /**
     * 根据起始rowkey和查询条件，在Hbase表中进行分页查询
     * @author swot.liu
     * @param bean 查询结果bean
     * @param recordNum 每页记录数
     * @param params 条件参数
     * @param tableParam 表参数
     * @return
     * @throws java.io.IOException
     */


    /**
     * 获取ROWKEY的名称与长度映射
     *
     * @param
     * @return
     */
    public Map<String, Integer> rowkeyMap(Properties propers) {
        String[] rowkeyName = ((String) propers.get("hbase.rowkey.name")).split("\\,");
        String[] rowkeyLen = ((String) propers.get("hbase.rowkey.length")).split("\\,");
        Map<String, Integer> rowkeyDict = new LinkedHashMap<String, Integer>();
        for (int i = 0; i < rowkeyName.length; i++) {
            rowkeyDict.put(rowkeyName[i], Integer.parseInt(rowkeyLen[i]));
        }
        return rowkeyDict;
    }

    /**
     * 按位数切分rowKey
     *
     * @param row
     * @param maps
     */
    private void splitRow(String row, Map<String, String> maps, Properties propers) {
        String length[] = propers.getProperty("hbase.rowkey.length").split("\\,");
        String name[] = propers.getProperty("hbase.rowkey.name").split("\\,");
        int index = 0;
        for (int i = 0; i < length.length; i++) {
            int offset = Integer.parseInt(length[i]);
            maps.put(name[i].toLowerCase(), row.substring(index, index + offset));
            index += offset;
        }
    }

    /**
     * 创建表,并预创建Region
     *
     * @param
     */

    private byte[][] getSplits(String startKey, String endKey, int numRegions, String rowKeyLen) {
        if (StringUtils.isEmpty(startKey) || StringUtils.isEmpty(endKey))
            throw new RuntimeException("Split key error...");

        byte[][] splits = new byte[numRegions - 1][];
        int lowestKey = Integer.parseInt(startKey);
        int highestKey = Integer.parseInt(endKey);
        int splitLength = Integer.parseInt(rowKeyLen);

        StringBuilder tempRowKey = new StringBuilder();
        for (int i = 0; i < splitLength; i++) {
            tempRowKey.append("0");
        }

        int range = highestKey - lowestKey;
        int regionIncrement = range / numRegions;
        lowestKey += regionIncrement;
        for (int i = 0; i < numRegions - 1; i++) {
            String key = (lowestKey + (regionIncrement * i)) + "";
            // 如果key位数不够，则补充为定长，加0填充
            if (key.length() < splitLength) {
                key = tempRowKey.substring(0, splitLength - key.length()) + key;
            }
            byte[] b = Bytes.toBytes(key);
            splits[i] = b;
        }
        return splits;
    }

    public byte[][] getHexSplits(String startKey, String endKey, int numRegions) {
        byte[][] splits = new byte[numRegions - 1][];
        BigInteger lowestKey = new BigInteger(startKey, 16);
        BigInteger highestKey = new BigInteger(endKey, 16);
        BigInteger range = highestKey.subtract(lowestKey);
        BigInteger regionIncrement = range.divide(BigInteger.valueOf(numRegions));
        lowestKey = lowestKey.add(regionIncrement);
        for (int i = 0; i < numRegions - 1; i++) {
            BigInteger key = lowestKey.add(regionIncrement.multiply(BigInteger.valueOf(i)));
            byte[] b = String.format("%016x", key).getBytes();
            splits[i] = b;
        }
        return splits;
    }

    public void batchCrtTable() {
        String serviceCode[] = props.getProperty("hbase.service.code").split("\\,");
        String sCode = null;
        Properties propers = null;
        String prestartkey = null;
        String preendkey = null;
        String tableName = null;
        String splitSize = null;
        String family[] = new String[1];
        String compressionType = null;
        String regionNum = null;
        String rowKeyLen = null;
        boolean rebuild = true;
        for (int i = 0; i < serviceCode.length; i++) {
            sCode = serviceCode[i];
            propers = FileUtil.readConfigFile(sCode + ".properties");
            prestartkey = propers.getProperty("hbase.table.prestartkey");
            preendkey = propers.getProperty("hbase.table.preendkey");
            tableName = propers.getProperty("hbase.table.name");
            splitSize = propers.getProperty("hbase.table.split.size");
            family[0] = propers.getProperty("hbase.table.family");
            compressionType = propers.getProperty("hbase.table.compressionType");
            regionNum = propers.getProperty("hbase.table.regionNum");
            rowKeyLen = propers.getProperty("hbase.table.rowKeyLen");
            //	this.createTable(rebuild, tableName, splitSize, family, compressionType, prestartkey, preendkey, regionNum, rowKeyLen);
        }
    }


    public static void QueryAll(String tableName) {
        HTablePool pool = new HTablePool(config, 1000);
        HTable table = (HTable) pool.getTable(tableName);
        try {
            ResultScanner rs = table.getScanner(new Scan());
            for (Result r : rs) {
                System.out.println("获得到rowkey:" + new String(r.getRow()));
                for (KeyValue keyValue : r.raw()) {
                    System.out.println("列：" + new String(keyValue.getFamily())
                            + "====值:" + new String(keyValue.getValue()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static String queryByCondition1(String tableName) {
        InfoBean infoBean = new InfoBean();
        HTablePool pool = new HTablePool(config, 1000);
        HTableInterface table = pool.getTable(tableName);
        List<String> resultList = new ArrayList<String>();
        HashMap<String, HashMap<String, Object>> result = new HashMap<String, HashMap<String, Object>>();
        try {
            Get scan = new Get("111111".getBytes());// 根据rowkey查询
            Result r = table.get(scan);


            StringBuffer stringBuffer = new StringBuffer();
            System.out.println("获得到rowkey:" + new String(r.getRow()));

            HashMap<String, Object> hashMap = new HashMap<String, Object>();

            for (KeyValue keyValue : r.raw()) {

                System.out.println("列族：" + new String(keyValue.getFamily())
                        + "====值:" + new String(keyValue.getValue()));
                stringBuffer.append(new String(keyValue.getFamily()) + ":" + new String(keyValue.getValue()));
                hashMap.put(new String(keyValue.getQualifier()), new String(keyValue.getValue()));
                resultList.add(stringBuffer.toString());
                result.put(new String(keyValue.getFamily()), hashMap);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        Gson gson = new Gson();

        return gson.toJson(result);
    }

    public static String jsonToString(JsonElement jsonelement) {
        if (jsonelement == null) {
            return "";
        }
        String str = String.valueOf(jsonelement).trim();
        if (str.startsWith("\"")) {
            str = str.substring(1, str.length() - 1);
        }
        if (str.endsWith("\"")) {
            str = str.substring(0, str.length() - 2);
        }
        System.out.println("str=" + str);
        return str;
    }

    public String queryByRowKey(String databases, String actionType, JsonObject param, int[] pageParam) {
        System.out.println("rowkey:tablename=" + String.valueOf(param.get("rowkey") + ":" + String.valueOf(param.get("tablename"))));
        InfoBean infoBean = new InfoBean();
        HTablePool pool = new HTablePool(config, 1000);
        String tablename = jsonToString(param.get("tablename"));
        String rowkey = jsonToString(param.get("rowkey"));
        System.out.println("tablename=" + tablename.length());
        System.out.println("rowkey=" + rowkey.length());
        HTableInterface table = pool.getTable(tablename);
        Get scan = new Get(rowkey.getBytes());// 根据rowkey查询
        List<String> resultList = new ArrayList<String>();

        HashMap<String, HashMap<String, Object>> result = new HashMap<String, HashMap<String, Object>>();
        try {


            Result r = table.get(scan);


            StringBuffer stringBuffer = new StringBuffer();
            System.out.println("获得到rowkey:" + new String(r.getRow()));

            HashMap<String, Object> hashMap = new HashMap<String, Object>();

            for (KeyValue keyValue : r.raw()) {

                System.out.println("列族：" + new String(keyValue.getFamily())
                        + "====值:" + new String(keyValue.getValue()));
                stringBuffer.append(new String(keyValue.getFamily()) + ":" + new String(keyValue.getValue()));
                hashMap.put(new String(keyValue.getQualifier()), new String(keyValue.getValue()));
                long timestamp = keyValue.getTimestamp();
                hashMap.put("timestamp", String.valueOf(timestamp));
                hashMap.put("rowkey", new String(keyValue.getRow()));

                resultList.add(stringBuffer.toString());
                result.put(new String(keyValue.getFamily()), hashMap);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        Gson gson = new Gson();

        return gson.toJson(result);
    }


    public static void printResult(Result result) {

        //行键

        byte[] rowKey = result.getRow();
        //返回的是四元组Map<family,   Map<qualifier,    Map<timestamp,    value>>>
        NavigableMap<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> map = result.getMap();

        for (Entry<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> entry : map.entrySet()) {
            //第一个是列族
            byte[] familyName = entry.getKey();
            NavigableMap<byte[], NavigableMap<Long, byte[]>> value2 = entry.getValue();
            for (Entry<byte[], NavigableMap<Long, byte[]>> entry2 : value2.entrySet()) {
                //第二个是列限定符
                byte[] qualifierName = entry2.getKey();
                NavigableMap<Long, byte[]> value3 = entry2.getValue();
                for (Entry<Long, byte[]> entry3 : value3.entrySet()) {
                    //第三个是时间戳
                    Long timestamp = entry3.getKey();
                    //真正的值
                    byte[] realValue = entry3.getValue();

                    String s = String.format("行键%s 列族%s:%s 值%s 时间戳%d", Bytes.toString(rowKey), Bytes.toString(familyName), Bytes.toString(qualifierName), Bytes.toString(realValue), timestamp);
                    System.out.println(s);


                }
            }
        }


    }


    /**
     * 开发人员:nie_hw
     * 创建日期:2016年12月9日
     * 方法(重写)说明:
     * 方法修改记录:
     * @param str
     * @return
     */


    /**
     * 开发人员:nie_hw
     * 创建日期:2016年12月9日
     * 方法(重写)说明:
     * 方法修改记录:
     *
     * @param databases
     * @param actionType
     * @param param
     * @return
     */
    @Override
    public String queryByValue(String databases, String actionType, JsonObject param) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 开发人员:nie_hw
     * 创建日期:2016年12月9日
     * 方法(重写)说明:
     * 方法修改记录:
     *
     * @param databases
     * @param actionType
     * @param param
     * @return
     */
    @Override
    public String ScanTable(String databases, String actionType, JsonObject param) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 开发人员:nie_hw
     * 创建日期:2016年12月9日
     * 方法(重写)说明:
     * 方法修改记录:
     *
     * @param databases
     * @param actionType
     * @param param      参数里面必须包含键rowkey，tablename
     * @return 返回json的String 串
     */
    @Override
    public String queryByRowKey(String databases, String actionType, JsonObject param) {

        Log.info("rowkey:tablename=" + String.valueOf(param.get("rowkey") + ":" + String.valueOf(param.get("tablename"))));
        InfoBean infoBean = new InfoBean();
        HTablePool pool = new HTablePool(config, 1000);
        String tablename = jsonToString(param.get("tablename"));
        String rowkey = jsonToString(param.get("rowkey"));
        HTableInterface table = pool.getTable(tablename);
        Get scan = new Get(rowkey.getBytes());
        List<String> resultList = new ArrayList<String>();

        HashMap<String, HashMap<String, Object>> result = new HashMap<String, HashMap<String, Object>>();
        try {
            Result r = table.get(scan);
            StringBuffer stringBuffer = new StringBuffer();
            System.out.println("获得到rowkey:" + new String(r.getRow()));
            HashMap<String, Object> hashMap = new HashMap<String, Object>();
            for (KeyValue keyValue : r.raw()) {

                Log.info("列族：" + new String(keyValue.getFamily())
                        + "====值:" + new String(keyValue.getValue()));
                stringBuffer.append(new String(keyValue.getFamily()) + ":" + new String(keyValue.getValue()));
                hashMap.put(new String(keyValue.getQualifier()), new String(keyValue.getValue()));
                long timestamp = keyValue.getTimestamp();
                hashMap.put("timestamp", String.valueOf(timestamp));
                hashMap.put("rowkey", new String(keyValue.getRow()));
                resultList.add(stringBuffer.toString());
                result.put(new String(keyValue.getFamily()), hashMap);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return "";
        }
        Gson gson = new Gson();

        return gson.toJson(result);

    }


    /**
     * 根据起始rowkey和查询条件，在Hbase表中进行分页查询
     *
     * @param
     * @param param      必须包含的
     * @param propFileNm
     * @return
     * @throws IOException 返回值{"results":[{"age":"28"},{"age":"29"}],"recordNum":"1","startRowKey":"111112","stopRowKey":"111116","retMsg":"success"}
     */
    public String scanData(String strbean, JsonObject param, String propFileNm) throws IOException {

        ScanBean bean = new ScanBean();
        Gson gson = new Gson();
        bean = gson.fromJson(strbean, ScanBean.class);
        String tablename = jsonToString(param.get("tablename"));
        String cf = jsonToString(param.get("cf"));
        ScanBean scanBean = new ScanBean();
        Map<String, String> params = new HashMap<String, String>();
        params.put("age", "28");
        params.put("name", "niehw");
        HTablePool pool = new HTablePool(config, 1000);
        if (StringUtils.isEmpty(tablename) || StringUtils.isEmpty(cf)) {
            scanBean.setRetMsg("table or cf is null ");
            return gson.toJson(scanBean);
        }
        Properties props = FileUtil.readConfigFile(tablename + ".properties");
        String[] qualifiers = props.getProperty("qualifiers").split(",");
        HTableInterface table = pool.getTable(tablename);

        FilterList filters = new FilterList();
        Scan scan = new Scan();
        /**
         * 添加待查询列
         */
        for (String qualifier : qualifiers) {
            scan.addColumn(Bytes.toBytes(cf), Bytes.toBytes(qualifier));
        }

        /**
         * 添加条件过滤器
         */
        addColumnFilter(params, props, filters, cf);
        /**
         * 将上一次查询的结束rowkey作为本次查询的起始rowkey
         * 本过滤器对于rowkey范围是前闭后开
         */
        scan.setStartRow(Bytes.toBytes(bean.startRowKey));
        scan.setStopRow(Bytes.toBytes(bean.stopRowKey));
        /**
         *  设置分页过滤器
         */
        PageFilter pageFilter = new PageFilter(Integer.parseInt(bean.recordNum) + 1);
        filters.addFilter(pageFilter);
        scan.setFilter(filters);

        /**
         * 设置缓存
         */

        scan.setCacheBlocks(true);
        scan.setCaching(Integer.parseInt(props.getProperty("hbase.caching")));

        /**
         * 解析每页记录结果集
         */


        ResultScanner results = table.getScanner(scan);
        List<Map<String, String>> lists = new ArrayList<Map<String, String>>();
        Map<String, String> maps = null;
        try {
            int count = 0;
            for (Result r : results) {
                maps = new HashMap<String, String>();
                for (Cell cell : r.rawCells()) {
                    maps.put(Bytes.toString(CellUtil.cloneQualifier(cell)), Bytes.toString(CellUtil.cloneValue(cell)));
                }
                lists.add(maps);
                bean.stopRowKey = Bytes.toString(r.getRow());
            }
        } finally {
            results.close();
        }

        bean.results.addAll(lists);
        bean.setRetMsg("success");
        lists.clear();
        System.out.println("---------" + gson.toJson(bean));
        return gson.toJson(bean);
    }

    public void addColumnFilter(Map<String, String> params, Properties props, FilterList filters, String family) throws NumberFormatException, UnsupportedEncodingException {
        Iterator<Entry<String, String>> condIt = params.entrySet().iterator();
        SingleColumnValueFilter filter = null;
        for (int i = 0; condIt.hasNext(); i++) {
            Entry<String, String> condEntry = condIt.next();
            String qualifier = condEntry.getKey();
            String value = condEntry.getValue();
            Log.debug("KEY:" + qualifier + " VALUE:" + value);
            if (!(value == null || value.equals(""))) {
                if ("age".equalsIgnoreCase(qualifier)) {
                    filter = new SingleColumnValueFilter(Bytes.toBytes(family), Bytes.toBytes(qualifier), CompareFilter.CompareOp.GREATER_OR_EQUAL, Bytes.toBytes(value));

                } else if ("name".equalsIgnoreCase(qualifier)) {
                    filter = new SingleColumnValueFilter(Bytes.toBytes(family), Bytes.toBytes(qualifier), CompareFilter.CompareOp.EQUAL, new SubstringComparator(value));

                } else {
                    filter = new SingleColumnValueFilter(Bytes.toBytes(family), Bytes.toBytes(qualifier), CompareFilter.CompareOp.EQUAL, Bytes.toBytes(value));

                }
                filter.setFilterIfMissing(false);
                filters.addFilter(filter);
            }
        }

    }

}
