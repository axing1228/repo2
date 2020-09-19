package com.sz.fts.utils;

import java.io.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesUtil{
	// 配置文件的路径
	private String configPath;

	/**
	 * 配置文件对象
	 */
	private Properties props = null;

	public PropertiesUtil() throws IOException {
		this.configPath = this.getClass().getResource("/").getPath() + "global.properties";
		InputStream in = new FileInputStream(configPath);
		props = new Properties();
		props.load(in);
		// 关闭资源
		in.close();
	}

	/**
	 * 根据key值读取配置的值
	 * 
	 * @author 杨坚
	 * @param key
	 *            key值
	 * @return key 键对应的值
	 * @throws IOException
	 */
	public String readValue(String key) throws IOException {
		return props.getProperty(key);
	}

	/**
	 * 读取properties的全部信息
	 * 
	 * @author 杨坚
	 * @throws FileNotFoundException
	 *             配置文件没有找到
	 * @throws IOException
	 * 
	 */
	public Map<String, String> readAllProperties() throws FileNotFoundException, IOException {
		// 保存所有的键值
		Map<String, String> map = new HashMap<String, String>();
		Enumeration en = props.propertyNames();
		while (en.hasMoreElements()) {
			String key = (String) en.nextElement();
			String Property = props.getProperty(key);
			map.put(key, Property);
		}
		return map;
	}

	/**
	 * 设置某个key的值,并保存至文件
	 * 
	 * @author 杨坚
	 * @param key
	 *            key值
	 * @return key 键对应的值
	 * @throws IOException
	 */
	public void setValue(String key, String value) throws IOException {
		Properties prop = new Properties();
		InputStream fis = new FileInputStream(this.configPath.toString());
		// 从输入流中读取属性列表（键和元素对）
		prop.load(fis);
		OutputStream fos = new FileOutputStream(this.configPath.toString());
		prop.setProperty(key, value);
		prop.store(fos, "last update");
		// 关闭文件
		fis.close();
		fos.close();
	}

	public String getConfigPath() {
		return configPath;
	}

	public void setConfigPath(String configPath) {
		this.configPath = configPath;
	}
}