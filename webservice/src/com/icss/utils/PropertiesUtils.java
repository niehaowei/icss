package com.icss.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Properties文件工具类
 * 
 * @author 刘永浩
 * @version 1.0
 */
public class PropertiesUtils {

	private static Log logger = LogFactory.getLog(PropertiesUtils.class);

	/**
	 * 得到文件的输入流
	 **/
	private Properties prop = new Properties();

	private String fileName = null;

	public PropertiesUtils(String fileName) {
		this.fileName = fileName;
		this.initProperties();
	}

	private File getConfigFile() {
		String pathName = fileName.replaceAll("[/|//]", File.separator);
		File loadFile = new File(pathName);
		if (!loadFile.exists()) {
			loadFile = new File(Thread.currentThread().getContextClassLoader()
					.getResource("").getPath()
					+ File.separator + pathName);
		}
		if (!loadFile.exists()) {
			loadFile = new File("sources" + File.separator + pathName);
		}
		return loadFile;
	}

	private void initProperties() {
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(this.getConfigFile());
			prop.load(inputStream);
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * @param propertyName
	 *            读取 和写入
	 * @return key
	 */
	public String read(String propertyName) {
		return prop.getProperty(propertyName);
	}

	public void write(String name, String value) {
		prop.setProperty(name, value);
	}

	/**
	 * 将属性信息写入到Properties文件中
	 */
	public void writeFlush() {
		try {
			OutputStream os = new FileOutputStream(this.getConfigFile());
			prop.store(os, null);
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(fileName + "无法正常关闭");
		}
	}

	public String getFileName() {
		return fileName;
	}

	public static void main(String[] args) {
		PropertiesUtils pu = new PropertiesUtils("hbaseconfig.properties");
		System.out.println(pu.read("csv.input.dir"));
		pu.write("table.tmp", "tmp");
		pu.writeFlush();
	}
}
