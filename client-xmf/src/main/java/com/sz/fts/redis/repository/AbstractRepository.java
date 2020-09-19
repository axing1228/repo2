package com.sz.fts.redis.repository;


import com.sz.fts.redis.base.impl.AbstractBaseRedisManager;
import com.sz.fts.redis.bean.AbstractRedisBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;


public class AbstractRepository extends AbstractBaseRedisManager {

	/**
	 * 日志记录器
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRepository.class);

	/**
	 * 根据key拿到单条记录.
	 *
	 * @param key 主键
	 * @param <T> 泛型类
	 * @return 数据
	 */
	protected <T extends AbstractRedisBean> T getBean(final String key) {
		try {

			byte[] bytes = get(key);
			if (bytes != null) {
				return (T) AbstractRedisBean.fromByteArray(bytes);
			}
		}
		catch (ClassNotFoundException e) {

		}
		catch (IOException e) {

		}
		return null;
	}

	/**
	 * 根据key拿到单条记录.
	 *
	 * @param key   主键
	 * @param field 域
	 * @return 数据
	 */
	protected <T extends AbstractRedisBean> T hgetBean(final String key, final String field) {
		try {
			byte[] bytes = hget(key, field);
			if (bytes != null) {
				return (T) AbstractRedisBean.fromByteArray(bytes);
			}
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 根据key拿到单条记录.
	 *
	 * @param key   主键
	 * @param field 域
	 * @return 数据
	 */
	protected String hgetValue(final String key, final String field) {
		byte[] bytes = hget(key, field);
		if (bytes != null) {
			return new String(bytes);
		}
		return null;
	}

	/**
	 * 根据key拿到单条记录.
	 *
	 * @param key   主键
	 * @param field 域
	 * @return 数据
	 */
	protected String hgetValue(final String key, final String field, final String charSet) {
		byte[] bytes = hget(key, field);
		if (bytes != null) {
			try {
				return new String(bytes, charSet);
			}
			catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 根据key拿到记录.
	 *
	 * @param key 主键
	 * @return 数据
	 */
	protected String getValue(final String key) {
		byte[] bytes = get(key);
		if (bytes != null) {
			return new String(bytes);
		}
		return null;
	}

	/**
	 * 设置 属性.
	 *
	 * @param key   主键
	 * @param value 值
	 * @param time  超时时间
	 */
	protected void setValue(final String key, final String value, int time) {
		set(key, value.getBytes(), time);
	}

	/**
	 * 设置hash 属性.
	 *
	 * @param key   主键
	 * @param field 域
	 * @param value 值
	 */
	protected void hSetValue(final String key, final String field, final String value) {
		hset(key, field, value.getBytes());
	}

	/**
	 * 设置hash 属性.
	 *
	 * @param key   主键
	 * @param field 域
	 * @param value 值
	 */
	protected void hSetValue(final String key, final String field, final String value, final String charSet) {
		try {
			hset(key, field, value.getBytes(charSet));
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * set lock key for redis key to keep field and value set only by one thread.
	 * if some thread using the key ,return false;
	 * forbid dead lock,the lock key must expire time
	 *
	 * @param key        lock key
	 * @param expireTime expire time
	 * @return true: get the key can exchange value;false:can't get the key
	 */
	protected boolean setValueNx(final String key, final long expireTime) {
		if (expireTime <= 0) {
			return setNX(key, "1".getBytes(), 3);
		}
		return setNX(key, "1".getBytes(), expireTime);
	}

	/**
	 * 根据key拿到单条记录.
	 *
	 * @param key 主键
	 * @return 数据
	 */
	protected Map<String, Long> getHashAll(final String key) {
		Map<byte[], byte[]> hgetall = hgetall(key);
		Set<byte[]> keys = hgetall.keySet();
		Map<String, Long> map = new HashMap<String, Long>();
		for (byte[] k : keys) {
			String mKey = null;
			try {
				mKey = new String(k, "UTF-8");
			}
			catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			Long mLong = new Long(String.valueOf(hgetall.get(k)));
			map.put(mKey, mLong);
		}
		return map;
	}

	/**
	 * 把redis 数据转换成 List<Bean>.
	 *
	 * @param key   key
	 * @param <T>   类类型
	 * @param start 开始下标
	 * @param end   结束下标
	 * @return
	 */
	protected <T extends AbstractRedisBean> List<T> getListLrange(final String key, long start, long end) {

		List<byte[]> bytes = lrange(key, start, end);
		List<T> beans = new ArrayList<T>();
		if (CollectionUtils.isEmpty(bytes)) {
			return beans;
		}
		try {
			for (byte[] b : bytes) {
				beans.add((T) AbstractRedisBean.fromByteArray(b));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return beans;
	}

	/**
	 * 把redis 数据转换成 List<Bean>.
	 *
	 * @param key key
	 * @param <T> 类类型
	 * @return
	 */
	protected <T extends AbstractRedisBean> List<T> getListLrange(final String key) {
		return getListLrange(key, 0, -1);
	}

	/**
	 * 插入数据
	 *
	 * @param key 表名.
	 * @param t   参数
	 * @param <T> 泛型
	 * @return 成功与否
	 */
	protected <T extends AbstractRedisBean> boolean setBean(final String key, final T t) {
		// 0 ：不过期
		set(key, t.toByteArray(), 0);
		return exists(key);
	}

	/**
	 * 插入数据
	 *
	 * @param key 表名.
	 * @param t   参数
	 * @param <T> 泛型
	 * @return 成功与否
	 */
	protected <T extends AbstractRedisBean> boolean hsetBean(final String key, final String field, final T t) {
		hset(key, field, t.toByteArray());
		return true;
	}

	/**
	 * 插入数据
	 *
	 * @param key   键名
	 * @param field 域名
	 * @param s     值
	 * @return 是否成功
	 */
	protected boolean hsetBean(final String key, final String field, String s) {
		hset(key, field, s.getBytes());
		return true;
	}

	/**
	 * 插入数据
	 *
	 * @param key 表名.
	 * @param t   参数
	 * @param <T> 泛型
	 * @return 成功与否
	 */
	protected <T extends AbstractRedisBean> boolean setBeanLpushList(final String key, final List<T> t) {
		if (t != null && t.size() > 0) {
			for (T row : t) {
				setBeanLpush(key, row);
			}
			return true;
		}
		return false;
	}

	/**
	 * 插入数据
	 *
	 * @param key 表名.
	 * @param t   参数
	 * @param <T> 泛型
	 * @return 成功与否
	 */
	protected <T extends AbstractRedisBean> boolean setBeanLpush(final String key, final T t) {
		lpush(key, t.toByteArray());
		return true;
	}

	protected void setLockKey(String key) {

	}

	/**
	 * 模糊查询key.
	 *
	 * @param str 查询条件
	 * @return keys
	 */
	public String[] getKeys(String str) {
		Set<byte[]> keys = keys(str);
		String[] a = new String[keys.size()];
		Iterator<byte[]> iterator = keys.iterator();
		for (int i = 0; i < keys.size(); i++) {
			a[i] = new String(iterator.next());
		}
		return a;
	}

}