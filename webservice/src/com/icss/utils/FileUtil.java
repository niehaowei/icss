package com.icss.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;



/**
 * @author swot.liu
 * @date 2011-8-15 下午04:11:16
 * 
 */
public class FileUtil {
	private static Logger logger = Logger.getLogger(FileUtil.class);

	/**
	 * 
	 * @Title： BuffWrite
	 * @param fileList
	 * @param filePath
	 * @return boolean
	 * @throws java.io.IOException
	 * @throws Exception
	 */
	public static boolean BuffWrite(ArrayList<String> fileList, String filePath)
			throws IOException {
		FileOutputStream fw = null;
		BufferedWriter bw = null;
		try {

			fw = new FileOutputStream(filePath);
			bw = new BufferedWriter(new OutputStreamWriter(fw, "iso8859-1"));

			for (int i = 0; i < fileList.size(); i++) {
				bw.write(fileList.get(i) + "");
				bw.write("\n");
			}
			bw.flush();
			bw.close();
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (fw != null) {
				fw.close();
			}
			if (bw != null) {
				bw.close();
			}
		}
		return true;
	}

	/**
	 * 删除单个文件
	 * 
	 * @Title： deleteFile
	 * @param fileName
	 *            要删除的文件的文件名
	 * @return boolean 单个文件删除成功返回true，否则返回false
	 */
	public static boolean deleteFile(String fileName) {
		File file = new File(fileName);
		// 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
		if (file.exists() && file.isFile()) {
			if (file.delete()) {
				logger.debug("删除单个文件" + fileName + "成功！");
				return true;
			} else {
				logger.debug("删除单个文件" + fileName + "失败！");
				return false;
			}
		} else {
			logger.debug("删除单个文件失败：" + fileName + "不存在！");
			return false;
		}
	}

	/**
	 * 复制文件
	 * 
	 * @Title： copyFile
	 * @param from
	 *            代表的是复制的源文件
	 * @param to
	 *            代表目标文件
	 * @return void
	 * @throws Exception
	 */
	public static void copyFile(String from, String to) throws Exception {
		FileInputStream in = null;
		FileOutputStream out = null;
		try {
			File file_from = new File(from);

			in = new FileInputStream(file_from);

			File file_to = new File(to);
			if (!file_to.exists()) {
				String ls_to_parent = file_to.getParent();
				File file_to_parent = new File(ls_to_parent);
				file_to_parent.mkdirs();
			}

			out = new FileOutputStream(file_to);

			byte[] buf = new byte[128 * 1024];
			int c;

			while (true) {
				c = in.read(buf);
				if (c == -1)
					break;
				out.write(buf, 0, c);
			}
			in.close();
			out.close();
			logger.debug("复制单个文件成功：" + file_to);
		} catch (Exception ex) {
			logger.error("文件复制错误!");
			throw new Exception("文件复制错误!");

		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			try {
				if (out != null) {
					out.close();
				}
			} catch (Exception e) {
				// TODO: handle exception
			}

		}
	}

	/**
	 * 移动文件
	 * 
	 * @Title： moveFile
	 * @param from
	 *            代表要移动的源文件
	 * @param to
	 *            代表目标文件
	 * @return boolean 移动成功返回true
	 * @throws Exception
	 */
	public static boolean moveFile(String from, String to) throws Exception {
		File file_from = new File(from);
		FileInputStream in = null;
		FileOutputStream out = null;
		try {
			in = new FileInputStream(file_from);
			File file_to = new File(to);
			if (!file_to.exists()) {
				String ls_to_parent = file_to.getParent();
				File file_to_parent = new File(ls_to_parent);
				file_to_parent.mkdirs();
			}

			out = new FileOutputStream(file_to);

			byte[] buf = new byte[128 * 1024];
			int c;

			while (true) {
				c = in.read(buf);
				if (c == -1)
					break;
				out.write(buf, 0, c);
			}

			in.close();
			out.close();
			if (file_to.length() >= 0) {
				file_from.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			try {
				if (out != null) {
					out.close();
				}
			} catch (Exception e) {
				// TODO: handle exception
			}

		}

		return true;
	}

	/**
	 * @Title: writeFile
	 * @Description: 写文件
	 * @throws Exception
	 */
	public static void writeFile(String filename, String content)
			throws Exception {
		OutputStreamWriter osw = null;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(filename);
			osw = new OutputStreamWriter(fos, "UTF-8");
			osw.write(content);
			osw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				fos.close();
			}

		}

	}

	public static String readXmlPlate(String filename) throws Exception {
		String result = "";
		char[] tempchars = new char[1];
		Reader reader = null;
		File file = new File(filename);
		try {
			if(!file.exists()){
				String filePath = System.getProperty("user.dir") + File.separator + "xmltemplate" + File.separator + filename;
				file = new File(filePath);
			}
			if(!file.exists()){
				file = new File("xmltemplate" + File.separator + filename);
			}
			reader = new InputStreamReader(new FileInputStream(file),
					"utf-8");
			int tempchar;
			while ((tempchar = reader.read()) != -1) {
				char tmp = (char)tempchar;
				if(tmp != '\r' && tmp != '\n'){
					tempchars[0] = tmp;
					result = result.concat(new String(tempchars));
				}
			}
			reader.close();
		} catch (Exception ee) {
			ee.printStackTrace();
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		return result;
	}
	
	/**
	 * 加载配置文件
	 * 
	 * @param fileName
	 * @return
	 */
	public static Properties readConfigFile(String fileName) {
		File file = new File("config" + File.separator + fileName);
		if(!file.exists()){
			String filePath = System.getProperty("user.dir") + File.separator + "config" + File.separator + fileName;
			System.out.println(filePath);
			file = new File(filePath);
		}
		if(!file.exists()){
			file = new File(Thread.currentThread().getContextClassLoader().getResource("").getPath() + File.separator + fileName);
		}
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(file));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return prop;
	}
	
	public static List<String> listFileAndDir(String sourcePath){
		List<String> listFile = new ArrayList<String>();
		File sourceDir = new File(sourcePath);
		if(sourceDir.isDirectory()){
			File[] files = sourceDir.listFiles();
			for(int i = 0; i < files.length; i++){
				listFile.add(files[i].getAbsolutePath());
			}
		}else{
			logger.debug("源路径并非目录！");
		}
		return listFile;
	}
	
	public static List<String> listFiles(String sourcePath){
		List<String> listFile = new ArrayList<String>();
		File sourceDir = new File(sourcePath);
		if(sourceDir.isDirectory()){
			File[] files = sourceDir.listFiles();
			for(int i = 0; i < files.length; i++){
				if(files[i].isFile() && files[i].length() > 0){
					System.out.println("FILEABSPATH:" + files[i].getAbsolutePath());
					listFile.add(files[i].getAbsolutePath());
				}
			}
		}else{
			logger.debug("源路径并非目录！");
		}
		return listFile;
	}
	
	public static void renameFiles(String sourcePath) throws IOException{
		List<String> listFile = new ArrayList<String>();
		File sourceDir = new File(sourcePath);
		boolean flag = false;
		if(sourceDir.isDirectory()){
			File[] files = sourceDir.listFiles();
			File nextFile = null;
			String nextFileName = "";
			for(int i = 0; i < files.length; i++){
//				if(files[i].isFile() && files[i].getName().indexOf(".") < 0 && files[i].length() > 0){
				if(files[i].isFile() && files[i].getName().indexOf(".") < 0){
					if(files[i].getParent().indexOf(".csv") > -1){
						nextFileName = files[i].getParent() + File.separator + i + "_" + files[i].getParent().substring(files[i].getParent().lastIndexOf(File.separator) + 1, files[i].getParent().length());
					}else{
						nextFileName = files[i].getParent() + File.separator + i + "_" + files[i].getParent().substring(files[i].getParent().lastIndexOf(File.separator) + 1, files[i].getParent().length()) + ".csv";
					}
					nextFile = new File(nextFileName);
					if(!nextFile.exists()){
						if(!files[i].renameTo(nextFile)){
							FileUtils.moveFile(files[i], nextFile);
						}
					}
				}
			}
		}else{
			logger.debug("源路径并非目录！");
		}
	}
	
	public static boolean isExistFile(String sourcePath, String fileName, int file_count){
		List<String> listFile = new ArrayList<String>();
		File sourceDir = new File(sourcePath);
		boolean result = false;
		int count = 0;
		if(sourceDir.isDirectory()){
			File[] files = sourceDir.listFiles();
			for(int i = 0; i < files.length; i++){
				if(files[i].isFile()){
					if(("_SUCCESS".equals(files[i].getName()) && files[i].length() == 0)){
						result = true;
						break;
					}
				}else{
					if(files[i].isDirectory() && files[i].getName().indexOf(".hive") > -1){
						result = true;
						break;
					}else{
						File[] subDirs = files[i].listFiles();
						for(int j = 0; j < subDirs.length; j++){
							if(subDirs[j].isDirectory()){
								if((subDirs[j].getName().indexOf(".hive")) > -1){
									count++;
									if(count == file_count){
										result = true;
										break;
									}
								}
							}
						}
					}
				}
			}
		}else{
			logger.debug("源路径并非目录！");
		}
		
//		System.out.println("TMP-RESULT：" + result);
		return result;
	}
	
	public static void ZipCompress(String sourcePath, String targetName) throws IOException{
		FileUtil.renameFiles(sourcePath);
		FileOutputStream fos = new FileOutputStream(targetName);
		CheckedOutputStream cos = new CheckedOutputStream(fos, new Adler32());
		ZipOutputStream zos = new ZipOutputStream(cos);
		BufferedOutputStream bos = new BufferedOutputStream(zos);
		zos.setComment("ZipCompress");
		List<String> sourceNames = FileUtil.listFiles(sourcePath);
		for(int i = 0; i < sourceNames.size(); i++){
			if(!(sourceNames.get(i).indexOf(".crc") > -1)){
				System.out.println("NEXTFILENAME:" + sourceNames.get(i));
				String sourceName = sourceNames.get(i).substring(sourceNames.get(i).lastIndexOf(File.separator) + 1, sourceNames.get(i).length());
				BufferedReader in = new BufferedReader(new FileReader(sourceNames.get(i)));
//				Reader in = new InputStreamReader(new FileInputStream(sourceNames.get(i)), "UTF-8");
				zos.putNextEntry(new ZipEntry(sourceNames.get(i).substring(sourceNames.get(i).lastIndexOf(File.separator) + 1, sourceNames.get(i).length())));
				int cache;
				while((cache = in.read()) != -1){
					bos.write(cache);
				}
				in.close();
			}
		}
		bos.close();
	}
	
	public static void ZipUnCompress(String sourceName, String targetPath) throws Exception{
		FileInputStream fis = null;
		try {
			File sourceFile = new File(sourceName);
			File targetDir = new File(targetPath);
			if(!targetDir.exists()){
				targetDir.mkdir();
			}
					
			fis = new FileInputStream(sourceFile);
			ZipInputStream zis = new ZipInputStream(fis);
			BufferedInputStream bis = new BufferedInputStream(zis);

			String objPath = sourceFile.getAbsolutePath();
			String filePath = objPath.substring(0, objPath.lastIndexOf(File.separator));
			
			ZipEntry ze = null;
			String tempFileName = "";
			int slen = 0;
			byte[] bt = new byte[1024];
			while((ze = zis.getNextEntry()) != null){
				tempFileName = targetDir.getPath() + File.separator + URLDecoder.decode(ze.getName(), "UTF-8");
				boolean check = false;
				FileOutputStream out = new FileOutputStream(tempFileName);
				while((slen = zis.read(bt, 0, bt.length)) != -1){
					check = true;
					out.write(bt, 0, slen);
				}
				if(!check){
					logger.error(tempFileName + "文件解压出错！");
				}
				out.close();
			}
			bis.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception("文件解压出错！");
		} finally{
			if(fis != null){
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String[] args){
		try {
//			String xmlStr = FileUtil.readXmlPlate("hods.01.1.xml");
//			System.out.println(xmlStr);
//			String sourceNames = "D:\\PacteraSource\\PacteraSource\\tmp";
//			String targetName = "D:\\PacteraSource\\PacteraSource\\tmp\\targetFile.zip";
//			FileUtil.ZipCompress(sourceNames, targetName);
//			FileUtil.ZipUnCompress(targetName, "D:\\PacteraSource\\PacteraSource\\tmp\\tmp1");
			String sourcePath = "D:/tmp/BATCHEXPORT_zhazha_20160305104041.csv/SYM_RB_ACCT";
			FileUtil.renameFiles(sourcePath);
//			System.out.printf("RESULT:" + result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
