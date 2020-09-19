package com.sz.fts.redis.base;

import java.util.List;
import java.util.Map;
import java.util.Set;


public interface BaseRedisManager {

    /**
     * 删除keys
     *
     * @param keys
     */
    public void del(final String... keys);

    /**
     * 存储二进制key、value
     *
     * @param key
     * @param value
     * @param expireTime
     */
    public void set(final String key, final byte[] value, final long expireTime);

    /**
     * 存储二进制key、value
     *
     * @param key
     * @param field
     * @param value
     */
    public void hset(final String key, final String field, final byte[] value);

    /**
     * 批量存储二进制key、value
     *
     * @param key
     * @param map
     */
    public void hMSet(final String key, final Map<byte[], byte[]> map);

    /**
     * 批量存储二进制key、value
     *
     * @param map
     */
    public void mset(final Map<byte[], byte[]> map);

    /**
     * 获取二进制存储数据
     *
     * @param key
     * @return
     */
    public byte[] get(final String key);

    /**
     * 获取二进制存储数据
     *
     * @param key
     * @param field
     * @return
     */
    public byte[] hget(final String key, final String field);

    /**
     * 返回名称为key的hash中所有键对应的value
     *
     * @param key
     * @return
     */
    public List<byte[]> hvals(final String key);

    /**
     * 在名称为key的list头添加一个值为value的 元素
     *
     * @param key
     * @return
     */
    public void lpush(final String key, final byte[]... bytes);

    /**
     * 在名称为key的list尾添加一个值为value的元素
     *
     * @param key
     * @return
     */
    public void rpush(final String key, final byte[]... bytes);

    /**
     * 返回名称为key的list中start至end之间的元素（end为 -1 ，返回所有）
     *
     * @param key
     * @return
     */
    public List<byte[]> lrange(final String key, final long start, final long end);

    /**
     * 判断key是否存在
     *
     * @param key
     * @return true:存在，false:不存在
     */
    public boolean exists(final String key);

    /**
     * 判断hashes key是否存在
     *
     * @param key
     * @return true:存在，false:不存在
     */
    public boolean hexists(final String key, final String field);

    /**
     * 根据正则表达式匹配符合条件的key
     *
     * @param pattern
     * @return
     */
    public Set<byte[]> keys(final String pattern);

    /**
     * 将名称为key的hash中field的value增加Long
     *
     * @param key
     * @param field
     * @param l
     * @return
     */
    public Long hincrby(final String key, final String field, final long l);

    /**
     * 返回名称为key的hash中所有的键（field）及其对应的value
     *
     * @param key
     * @return
     */
    public Map<byte[], byte[]> hgetall(final String key);
}
