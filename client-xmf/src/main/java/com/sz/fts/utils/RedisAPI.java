package com.sz.fts.utils;

import net.sf.json.JSONObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.*;

/**
 * redis数据库链接工具
 * 
 * @see:
 * @Company:江苏鸿信系统集成有限公司微信开发组
 * @author 杨坚
 * @Time 2016年11月24日
 * @version 1.0v
 */
public final class RedisAPI {

	// 可用连接实例的最大数目，默认值为8；
	private static int MAX_TOTAL = 0;

	// 控制一个jedisPool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
	private static int MAX_IDLE = 0;

	// 等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
	private static int MAX_WAIT = 0;

	private static int TIMEOUT = 0;
	// 获取连接是否校验可用
	private static boolean TEST_ON_BORROW = false;
	// 主1
	private static JedisPool jedisPoolMasterOne = null;

	/**
	 * 初始化Redis连接池
	 */
	static {
		try {

			TEST_ON_BORROW = Boolean.valueOf(CommonUtil.printPlatformProperties("redis_test_on_borrow"));
			TIMEOUT = Integer.parseInt(CommonUtil.printPlatformProperties("redis_timeout"));
			MAX_WAIT = Integer.parseInt(CommonUtil.printPlatformProperties("redis_max_wait"));
			MAX_IDLE = Integer.parseInt(CommonUtil.printPlatformProperties("redis_max_idle"));
			MAX_TOTAL = Integer.parseInt(CommonUtil.printPlatformProperties("redis_max_total"));
			List<String> redisList = new ArrayList<String>();
			JSONObject redisJson = new JSONObject();
			redisList = new ArrayList<String>();
			redisJson = new JSONObject();
			redisList.add("master_ip");
			redisList.add("master_port");
			redisList.add("master_auth");
			redisList.add("master_data");
			// 获取列表集合
			redisJson = CommonUtil.printPlatformPropertiesList(redisList);
			// redis数据处理
			JedisPoolConfig configMasterOne = new JedisPoolConfig();
			configMasterOne.setMaxTotal(MAX_TOTAL);
			configMasterOne.setMaxIdle(MAX_IDLE);
			configMasterOne.setMaxWaitMillis(MAX_WAIT);
			configMasterOne.setTestOnBorrow(TEST_ON_BORROW);
			jedisPoolMasterOne = new JedisPool(configMasterOne, redisJson.getString("master_ip"),
					redisJson.getInt("master_port"), TIMEOUT, redisJson.getString("master_auth"),
					redisJson.getInt("master_data") == 0 ? 0 : redisJson.getInt("master_data"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 选择主数据库连接池 使用完立即将连接释放到连接池(写操作)
	 * 
	 * @return
	 * @author 杨坚
	 * @Time 2016年11月24日
	 * @version 1.0v
	 */
	public static Jedis masterChooseConnect() {
		return jedisPoolMasterOne.getResource();
	}

	/**
	 * 字符串形式添加
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return
	 * @author 杨坚
	 * @Time 2016年11月17日
	 * @version 1.0v
	 */
	public static String setKeyToValueStr(String key, String value) {
		Jedis jedis = null;
		try {
			jedis = masterChooseConnect();
			return jedis.set(key, value);
		} catch (Exception e) {
			closeJedis(jedis);
			e.printStackTrace();
			return "0";
		} finally {
			closeJedis(jedis);
		}
	}

	/**
	 * 根据appid查询缓存信息
	 * 
	 * @param appid
	 *            公众号
	 * @param ops
	 *            缓存名称
	 * @return
	 * @author 杨坚
	 * @Time 2017年1月18日
	 * @version 1.0v
	 */
	public static String getWechatToken(String appid, String ops) {
		Jedis jedis = null;
		String value = null;
		try {
			jedis = masterChooseConnect();
			value = jedis.get(appid);
		} catch (Exception e) {
			closeJedis(jedis);
			e.printStackTrace();
		} finally {
			closeJedis(jedis);
		}
		return JSONObject.fromObject(value).getString(ops);
	}

	/**
	 * 二进制添加
	 * 
	 * @param key
	 * @param values
	 * @return
	 * @author 杨坚
	 * @Time 2016年11月24日
	 * @version 1.0v
	 */
	public static String setKeyToValueBytes(String key, Object values) {
		Jedis jedis = null;
		try {
			jedis = masterChooseConnect();
			return jedis.set(key.getBytes(), SerializationUtil.serialize(values));
		} catch (Exception e) {
			closeJedis(jedis);
			e.printStackTrace();
			return "0";
		} finally {
			closeJedis(jedis);
		}
	}

	/**
	 * 根据key删除信息
	 * 
	 * @param key
	 *            键
	 * @return
	 * @author 杨坚
	 * @Time 2016年11月17日
	 * @version 1.0v
	 */
	public static Long delKeyStr(String key) {
		Jedis jedis = null;
		try {
			jedis = masterChooseConnect();
			return jedis.del(key.getBytes());
		} catch (Exception e) {
			closeJedis(jedis);
			e.printStackTrace();
			return (long) 0;
		} finally {
			closeJedis(jedis);
		}
	}

	/**
	 * 根据key删除信息
	 * 
	 * @param key
	 *            键
	 * @return
	 * @author 杨坚
	 * @Time 2016年11月17日
	 * @version 1.0v
	 */
	public static Long delKeyBytes(String key) {
		Jedis jedis = null;
		try {
			jedis = masterChooseConnect();
			return jedis.del(key.getBytes());
		} catch (Exception e) {
			closeJedis(jedis);
			e.printStackTrace();
			return (long) 0;
		} finally {
			closeJedis(jedis);
		}
	}

	/**
	 * 判断是否存在key
	 * 
	 * @author 杨坚
	 * @Time 2016年10月2日
	 * @param key
	 *            键
	 * @return
	 */
	public static boolean existsBytes(String key) {
		Jedis jedis = null;
		try {
			jedis = masterChooseConnect();
			return jedis.exists(key.getBytes());
		} catch (Exception e) {
			closeJedis(jedis);
			e.printStackTrace();
			return false;
		} finally {
			closeJedis(jedis);
		}
	}

	/**
	 * 判断是否存在key
	 * 
	 * @author 杨坚
	 * @Time 2016年10月2日
	 * @param key
	 *            键
	 * @return
	 */
	public static Object existsBytesToValue(String key) {
		Jedis jedis = null;
		byte[] value = null;
		try {
			jedis = masterChooseConnect();
			if (jedis.exists(key.getBytes())) {
				value = jedis.get(key.getBytes());
				return SerializationUtil.deserialize(value);
			}
		} catch (Exception e) {
			closeJedis(jedis);
			e.printStackTrace();
			return null;
		} finally {
			closeJedis(jedis);
		}
		return null;
	}

	/**
	 * 判断是否存在key String
	 * 
	 * @author 杨坚
	 * @Time 2016年10月2日
	 * @param key
	 *            键
	 * @return
	 */
	public static boolean existsStr(String key) {
		Jedis jedis = null;
		try {
			jedis = masterChooseConnect();
			return jedis.exists(key);
		} catch (Exception e) {
			closeJedis(jedis);
			e.printStackTrace();
			return false;
		} finally {
			closeJedis(jedis);
		}
	}

	/**
	 * 获取数据信息Object
	 * 
	 * @param key
	 * @return
	 * @author 杨坚
	 * @Time 2016年11月24日
	 * @version 1.0v
	 */
	public static Object getValue(String key) {
		Jedis jedis = null;
		byte[] value = null;
		try {
			jedis = masterChooseConnect();
			value = jedis.get(key.getBytes());
			return SerializationUtil.deserialize(value);
		} catch (Exception e) {
			closeJedis(jedis);
			e.printStackTrace();
		} finally {
			closeJedis(jedis);
		}
		return null;
	}

	/**
	 * 以Map形式添加数据
	 * 
	 * @param key
	 * @param map
	 * @return
	 * @author 杨坚
	 * @Time 2016年11月24日
	 * @version 1.0v
	 */
	public static String setMap(String key, Map<String, String> map) {
		Jedis jedis = null;
		try {
			jedis = masterChooseConnect();
			return jedis.hmset(key, map);
		} catch (Exception e) {
			closeJedis(jedis);
			e.printStackTrace();
			return "0";
		} finally {
			closeJedis(jedis);
		}
	}

	/**
	 * 添加数据并设置时间
	 * 
	 * @param key
	 * @param time
	 *            过期时间
	 * @param value
	 * @return
	 * @author 杨坚
	 * @Time 2016年11月24日
	 * @version 1.0v
	 */
	public static String setStrTime(String key, int time, String value) {
		Jedis jedis = null;
		try {
			jedis = masterChooseConnect();
			return jedis.setex(key, time, value);
		} catch (Exception e) {
			closeJedis(jedis);
			e.printStackTrace();
			return "0";
		} finally {
			closeJedis(jedis);
		}
	}

	/**
	 * 以二进制来添加数据并设置时间
	 * 
	 * @param key
	 * @param time
	 *            过期时间
	 * @param value
	 * @return
	 * @author 杨坚
	 * @Time 2016年11月24日
	 * @version 1.0v
	 */
	public static String setByteTime(byte[] key, int time, byte[] value) {
		Jedis jedis = null;
		try {
			jedis = masterChooseConnect();
			return jedis.setex(key, time, value);
		} catch (Exception e) {
			closeJedis(jedis);
			e.printStackTrace();
			return "0";
		} finally {
			closeJedis(jedis);
		}
	}

	/**
	 * 获取map形式长度
	 * 
	 * @param key
	 * @return
	 * @author 杨坚
	 * @Time 2016年11月24日
	 * @version 1.0v
	 */
	public static long getMapLen(String key) {
		Jedis jedis = null;
		try {
			jedis = masterChooseConnect();
			return jedis.hlen(key);
		} catch (Exception e) {
			closeJedis(jedis);
			e.printStackTrace();
			return 0;
		} finally {
			closeJedis(jedis);
		}
	}

	/**
	 * 获取map形式的键
	 * 
	 * @param key
	 * @return
	 * @author 杨坚
	 * @Time 2016年11月24日
	 * @version 1.0v
	 */
	public static Set<String> getMapKey(String key) {
		Jedis jedis = null;
		try {
			jedis = masterChooseConnect();
			return jedis.hkeys(key);
		} catch (Exception e) {
			closeJedis(jedis);
			e.printStackTrace();
			return null;
		} finally {
			closeJedis(jedis);
		}
	}

	/**
	 * 获取map形式的值
	 * 
	 * @param key
	 * @return
	 * @author 杨坚
	 * @Time 2016年11月24日
	 * @version 1.0v
	 */
	public static List<String> getMapValue(String key) {
		Jedis jedis = null;
		try {
			jedis = masterChooseConnect();
			return jedis.hvals(key);
		} catch (Exception e) {
			closeJedis(jedis);
			e.printStackTrace();
			return null;
		} finally {
			closeJedis(jedis);
		}
	}

	/**
	 * 删除map形式中的键值对
	 * 
	 * @param key
	 *            数据库中的key
	 * @param paramKey
	 *            map中的key
	 * @return
	 * @author 杨坚
	 * @Time 2016年11月24日
	 * @version 1.0v
	 */
	public static Long getMapValue(String key, String paramKey) {
		Jedis jedis = null;
		try {
			jedis = masterChooseConnect();
			return jedis.hdel(key, paramKey);
		} catch (Exception e) {
			closeJedis(jedis);
			e.printStackTrace();
			return null;
		} finally {
			closeJedis(jedis);
		}
	}

	/**
	 * 根据键获取信息
	 * 
	 * @param key
	 * @return
	 * @author 杨坚
	 * @Time 2016年11月17日
	 * @version 1.0v
	 */
	public static String getKeyStr(String key) {
		Jedis jedis = null;
		String value = null;
		try {
			jedis = masterChooseConnect();
			value = jedis.get(key);
		} catch (Exception e) {
			closeJedis(jedis);
			e.printStackTrace();
		} finally {
			closeJedis(jedis);
		}
		return value;
	}

	/**
	 * 
	 * @Title: removeByPattern
	 * @Description: 批量删除某前缀key
	 * @author: 杨坚
	 * @param pattern
	 * @return: void
	 */
	public static void removeByPattern(String pattern) {
		Jedis jedis = null;
		try {
			jedis = masterChooseConnect();
			Set<String> set = jedis.keys(pattern.concat("*"));
			Iterator<String> it = set.iterator();
			while (it.hasNext()) {
				jedis.del(it.next());
			}
		} catch (Exception e) {
			closeJedis(jedis);
			e.printStackTrace();
		} finally {
			closeJedis(jedis);
		}
	}

	/**
	 * 释放jedis资源
	 * 
	 * @param jedis
	 * @author 杨坚
	 * @Time 2016年11月24日
	 * @version 1.0v
	 */
	public static void closeJedis(final Jedis jedis) {
		if (jedis != null) {
			jedis.close();
		}
	}

}