package com.sz.fts.utils;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串表达式
 *
 * @author 杨坚
 * @version [版本号, 2016-3-19]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public abstract class StringUtils {
	/**
	 * 格式化字符串
	 * 
	 * @param source
	 *            源字符串
	 * @param vars
	 *            参数
	 * @return 结果
	 * @see [类、类#方法、类#成员]
	 */
	public static String formatStr(String source, Map<String, String> vars) {
		if (null == source || vars == null) {
			return source;
		}

		String result = null;
		for (Iterator<Entry<String, String>> it = vars.entrySet().iterator(); it.hasNext();) {
			Entry<String, String> var = it.next();
			result = source.replace('$' + var.getKey(), var.getValue());
		}

		return result;
	}

	public static void main(String[] args) {

	}

	public static boolean isNotEmpty(String str) {
		return null != str && 0 != str.length() && !("undefined".equals(str)) && !("null".equals(str)) && !("".equals(str.trim()));
	}

	public static String replaceBlank(String str) {
		String dest = "";
		if (str != null) {
			Pattern p = Pattern.compile("\r\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("\\\\n");
		}
		return dest;
	}

	public static String YreplaceBlank(String str) {
		String dest = "";
		if (str != null) {
			Pattern p = Pattern.compile("\\\\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("\r\n");
		}
		return dest;
	}

	public static boolean isEmpty(String str) {
		return null == str || 0 == str.length() || "undefined".equals(str);
	}

	/**
	 * 字符串是否为空
	 * 
	 * @param
	 * @param
	 * @return
	 * @throws
	 */
	public static boolean isNull(String[] strs) {
		boolean flag = true;
		for (int i = 0; i < strs.length; i++) {
			if (strs[i] == null || "".equals(strs[i]) || "undefined".equals(strs[i])) {
				flag = false;
			}
		}
		return flag;

	}

	public static String foramtName(String name, String userId) {
		StringBuilder sb = new StringBuilder(256);
		sb.append("apps").append('/').append(userId).append('/').append(DateUtils.getCurrentTime14())
				.append(UUID.randomUUID()).append(name.substring(name.lastIndexOf('.')));
		return sb.toString();
	}

	public static String ecodeISO8859(String str) {
		if (StringUtils.isEmpty(str)) {
			return "";
		}

		String result = "";
		try {
			result = new String(str.getBytes("UTF-8"), "ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String ecodeGBK(String str) {
		String result = "";
		try {
			result = new String(str.getBytes("ISO-8859-1"), "GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String ecodeUTF8(String str) {
		String result = "";
		try {
			result = new String(str.getBytes("ISO-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}
}
