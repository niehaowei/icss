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
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.*;

import org.apache.hadoop.hbase.io.compress.Compression;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.ipc.Server.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.icss.utils.FileUtil;
import com.icss.ws.bean.InfoBean;

/**
 * @author niehw
 * @version 0.98 HBase数据访问对象封装,以单例模式进行创建
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

    public static  HBaseDao getInstance() {
        return instance;
    }

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
    public void createTable(boolean rebuild, String tableName, String splitSize, String family[], String compressionType, String startKey, String stopKey, String regionNum, String rowKeyLen) {
        Log.debug("create table:" + tableName);
        try {
            HBaseAdmin hBaseAdmin = new HBaseAdmin(config);
            if (hBaseAdmin.tableExists(tableName)) {
                if (rebuild) {
                    hBaseAdmin.disableTable(tableName);
                    hBaseAdmin.deleteTable(tableName);
                    Log.debug(tableName + "will be rebuild ...");
                } else {
                    Log.debug(tableName + " exists ignore...");
                    return;
                }
            }

            HTableDescriptor tableDescriptor = new HTableDescriptor(tableName);
            // 设置region大小
            long maxFileSize = Long.parseLong(splitSize);
            tableDescriptor.setMaxFileSize(maxFileSize * 1000 * 1000);

            // 创建列族
            HColumnDescriptor cf = null;
            for (int i = 0; family.length > i; i++) {
                cf = new HColumnDescriptor(family[i].toLowerCase());
                switch (compressionType) {
                    case "gzip":
                        cf.setCompressionType(Compression.Algorithm.GZ);
                        break;
                    case "lzo":
                        cf.setCompressionType(Compression.Algorithm.LZO);
                        break;
                    case "snappy":
                        cf.setCompressionType(Compression.Algorithm.SNAPPY);
                        break;
                    default:break;

                }
                tableDescriptor.addFamily(cf);
            }
            // 创建表,并预创建Region
            // 创建表,并预创建Region
            hBaseAdmin.createTable(
                    tableDescriptor,
                    getHexSplits(startKey, stopKey, Integer.parseInt(regionNum)));
//			hBaseAdmin.createTable(
//					tableDescriptor,
//					getSplits(startKey, stopKey, Integer.parseInt(regionNum), rowKeyLen));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.debug("create table end...");
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
            this.createTable(rebuild, tableName, splitSize, family, compressionType, prestartkey, preendkey, regionNum, rowKeyLen);
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


    @Override
    public String queryByValue(String databases, String actionType, JsonObject param) {
        // TODO Auto-generated method stub
        return null;
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
    @Override
    public String scanTable(String strbean, String propFileNm,JsonObject param) {
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
        try {
            addColumnFilter(params, props, filters, cf);
        } catch (UnsupportedEncodingException e) {

        }
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


        ResultScanner results = null;
        try {
            results = table.getScanner(scan);
        } catch (IOException e) {

        }
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
