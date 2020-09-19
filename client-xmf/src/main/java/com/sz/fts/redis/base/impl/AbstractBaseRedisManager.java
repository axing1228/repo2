package com.sz.fts.redis.base.impl;



import com.sz.fts.redis.base.BaseRedisManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Component
public abstract class AbstractBaseRedisManager implements BaseRedisManager {

    @Autowired
    protected RedisTemplate<Serializable, Serializable> businessRedis;

    /**
     * 一天所需的秒数
     */
    protected static long ONE_DAY_SEC = 60 * 60 * 24;

    /**
     * 一天所需的秒数
     */
    protected static long ONE_HOUR_SEC = 60 * 60;
    /**
     * 默认数据失效时间
     */
    protected final static int DEFAULT_EXPIRE_DAY = 90;

    protected int expireDay = DEFAULT_EXPIRE_DAY;

    /**
     * 获取 RedisSerializer <br>
     * ------------------------------<br>
     */
    protected RedisSerializer<String> getRedisSerializer(){
        return businessRedis.getStringSerializer();
    }


    /**
     * 删除keys
     *
     * @param keys
     */
    public void del(final String... keys) {
        businessRedis.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                for (String key : keys) {
                    connection.del(key.getBytes());
                }
                return null;
            }
        });
    }

    /**
     * delete the lock key.
     *
     * @param key key
     * @return delete key number;
     */
    public long delLockKey(final String key) {
        return businessRedis.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.del(key.getBytes());
            }
        });
    }

    /**
     * 存储二进制key、value
     *
     * @param key
     * @param value
     * @param expireTime
     */
    public void set(final String key, final byte[] value, final long expireTime) {
        businessRedis.execute(new RedisCallback() {
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                connection.set(key.getBytes(), value);
                if (expireTime > 0) {
                    connection.expire(key.getBytes(), expireTime);
                }
                return 1L;
            }
        });
    }

    /**
     * 存储二进制key、value nx
     *
     * @param key        lock key.
     * @param value      1
     * @param expireTime Require =true
     */
    protected Boolean setNX(final String key, final byte[] value, final long expireTime) {

        //设置锁键
        Boolean execute = businessRedis.execute(new RedisCallback<Boolean>() {
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.setNX(key.getBytes(), value);
            }
        });

        //对锁键设置过期时间 防止程序出错造成死锁
        if (execute && expireTime > 0) {
            //设置过期时间
            businessRedis.execute(new RedisCallback<Boolean>() {
                public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                    return connection.expire(key.getBytes(), expireTime);
                }
            });
        }
        return execute;
    }

    protected Boolean setNX(String key, long seconds) {
        return setNX(key, "true".getBytes(), seconds);
    }

    /**
     * 存储二进制key、value nx
     *
     * @param key   key.
     * @param field field
     * @param value 1
     */
    protected Boolean hsetNX(final String key, final String field, final byte[] value) {

        //设置锁键
        Boolean execute = businessRedis.execute(new RedisCallback<Boolean>() {
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.hSetNX(key.getBytes(), field.getBytes(), value);
            }
        });
        return execute;
    }

    /**
     * 存储二进制key、value
     *
     * @param key
     * @param field
     * @param value
     */
    public void hset(final String key, final String field, final byte[] value) {
        businessRedis.execute(new RedisCallback() {
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                connection.hSet(key.getBytes(), field.getBytes(), value);
                return null;
            }
        });
    }

    /**
     * 批量存储二进制key、value
     *
     * @param key
     * @param map
     */
    public void hMSet(final String key, final Map<byte[], byte[]> map) {
        businessRedis.execute(new RedisCallback() {
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                connection.hMSet(key.getBytes(), map);
                return null;
            }
        });
    }

    /**
     * 批量存储二进制key、value
     *
     * @param map
     */
    public void mset(final Map<byte[], byte[]> map) {
        businessRedis.execute(new RedisCallback() {
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                connection.mSet(map);
                return null;
            }
        });
    }


    /**
     * 获取二进制存储数据
     *
     * @param key
     * @return
     */
    public byte[] get(final String key) {
        return (byte[]) businessRedis.execute(new RedisCallback() {
            public byte[] doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.get(key.getBytes());
            }
        });
    }

    /**
     * 获取二进制存储数据
     *
     * @param key
     * @param field
     * @return
     */
    public byte[] hget(final String key, final String field) {
        return (byte[]) businessRedis.execute(new RedisCallback() {
            public byte[] doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.hGet(key.getBytes(), field.getBytes());
            }
        });
    }

    /**
     * 返回名称为key的hash中所有键对应的value
     *
     * @param key
     * @return
     */
    public List<byte[]> hvals(final String key) {
        return (List<byte[]>) businessRedis.execute(new RedisCallback() {
            public List<byte[]> doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.hVals(key.getBytes());
            }
        });
    }

    /**
     * 在名称为key的list头添加一个值为value的 元素
     *
     * @param key
     * @return
     */
    public void lpush(final String key, final byte[]... bytes) {
        businessRedis.execute(new RedisCallback() {
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.lPush(key.getBytes(), bytes);
            }
        });
    }

    /**
     * 在名称为key的list尾添加一个值为value的元素
     *
     * @param key
     * @return
     */
    public void rpush(final String key, final byte[]... bytes) {
        businessRedis.execute(new RedisCallback() {
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.rPush(key.getBytes(), bytes);
            }
        });
    }

    /**
     * 返回名称为key的list中start至end之间的元素（end为 -1 ，返回所有）
     *
     * @param key
     * @return
     */
    public List<byte[]> lrange(final String key, final long start, final long end) {
        return (List<byte[]>) businessRedis.execute(new RedisCallback() {
            public List<byte[]> doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.lRange(key.getBytes(), start, end);
            }
        });
    }

    /**
     * 判断key是否存在
     *
     * @param key
     * @return true:存在，false:不存在
     */
    public boolean exists(final String key) {
        return (Boolean) businessRedis.execute(new RedisCallback() {
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.exists(key.getBytes());
            }
        });
    }

    /**
     * 判断hashes key是否存在
     *
     * @param key
     * @return true:存在，false:不存在
     */
    public boolean hexists(final String key, final String field) {
        return (Boolean) businessRedis.execute(new RedisCallback() {
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.hExists(key.getBytes(), field.getBytes());
            }
        });
    }

    /**
     * 根据正则表达式匹配符合条件的key
     *
     * @param pattern
     * @return
     */
    public Set<byte[]> keys(final String pattern) {
        return (Set<byte[]>) businessRedis.execute(new RedisCallback() {
            public Set<byte[]> doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.keys(pattern.getBytes());
            }
        });
    }

    /**
     * 将名称为key的hash中field的value增加Long
     *
     * @param key
     * @param field
     * @param l
     * @return
     */
    public Long hincrby(final String key, final String field, final long l) {
        return (Long) businessRedis.execute(new RedisCallback() {
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.hIncrBy(key.getBytes(), field.getBytes(), l);
            }
        });
    }

    /**
     * 将指定 key 的值 +1
     * @param key
     * @return
     */
    public Long incr(final String key){
        return businessRedis.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException{
                return connection.incr(key.getBytes());
            }
        });
    }

    /**
     * 将名称为key的value增加Long
     *
     * @param key
     * @param l
     * @return
     */
    public Long incrby(final String key, final long l) {
        return (Long) businessRedis.execute(new RedisCallback() {
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.incrBy(key.getBytes(), l);
            }
        });
    }

    /**
     * 返回名称为key的hash中所有的键（field）及其对应的value
     *
     * @param key
     * @return
     */
    public Map<byte[], byte[]> hgetall(final String key) {
        return (Map<byte[], byte[]>) businessRedis.execute(new RedisCallback() {
            public Map<byte[], byte[]> doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.hGetAll(key.getBytes());
            }
        });
    }

    /**
     * 删除Hash Field
     *
     * @param key   主键
     * @param field 域
     * @return
     */
    public Long hdel(final String key, final String field) {
        return (Long) businessRedis.execute(new RedisCallback() {
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.hDel(key.getBytes(), field.getBytes());
            }
        });
    }

}
