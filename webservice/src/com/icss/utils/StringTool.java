package com.icss.utils;

import java.io.UnsupportedEncodingException;

/**
 * 字符串工具类
 * @author 刘永浩
 * @version 1.0
 */
public class StringTool {
	public final static int LEFT = 0;

	public final static int RIGHT = 1;

	public final static int ALL = 2;

	public final static int MAX_OUTPUT_LENGTH = 49152;

	public final static String DEFAULT_ENCODING = EciConstants.G_ENCODING;

	public static String getString(Object o) {
		if (o == null)
			return null;
		if (o instanceof byte[]) {
			String s = null;
			try {
				s = new String((byte[]) o, DEFAULT_ENCODING);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return null;
			}
			return s;
		} else if (o instanceof String) {
			return (String) o;
		} else
			return o.toString();
	}
	
	/**
	 * 以指定方式填充指定字符到字符串中
	 * 
	 * @param sSource
	 *            要填充的字符串(StringBuilderr对象)
	 * @param ch
	 *            填充字符
	 * @param nLen
	 *            填充后长度
	 * @param bLeft
	 *            填充方向：left:左对齐；right:右对齐
	 * @return 填充后字符串
	 * @throws java.io.UnsupportedEncodingException
	 * @since StringTool 1.0
	 */
	public static String fillChar(String sSource, char ch, int nLen, String align) 
			throws UnsupportedEncodingException {
		
		int nSrcLen = sSource.getBytes(EciConstants.G_ENCODING).length;	// 取字符串长度
		StringBuffer buffer = new StringBuffer();
		if (nSrcLen < nLen) {
			if ("right".equals(align)) { // 左填充
				 for (int i = 0; i < (nLen - nSrcLen); i++) {
					buffer.append(ch);
				}
				buffer.append(sSource);
			} else {	 // 右填充
				buffer.append(sSource);
				for (int i = 0; i < (nLen - nSrcLen); i++)
					buffer.append(ch);
			}
			return buffer.toString();
		}else{
			return buffer.append(sSource.substring(0, nLen)).toString();
		}
	}
}
