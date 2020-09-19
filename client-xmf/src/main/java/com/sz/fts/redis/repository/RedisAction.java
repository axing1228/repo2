package com.sz.fts.redis.repository;

import com.sz.fts.redis.keys.RedisKeys;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.stereotype.Component;

/**
 * @author 耿怀志
 * @version [版本号, 2017/12/15]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
@Component
public class RedisAction extends AbstractRepository {

    public void setString(final String key, final String value, int time) {
        setValue(key, value, time);
    }


    public String getString(final String key) {
        return getValue(key);
    }


    /**
     * 将名称为key的value 自增，并设置超时时间
     *
     * @param key
     * @param
     * @return
     */
    public Long incrbyTimeOut(final String key, final long expireTime) {
        return (Long) businessRedis.execute(new RedisCallback() {
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                long count = connection.incr(key.getBytes());
                connection.expire(key.getBytes(), expireTime);
                return count;
            }
        });
    }

    /**
     * 创建用户锁
     *
     * @param terminalId
     * @return
     */
    public boolean setUserLock(final String terminalId)  {
        return businessRedis.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                String key = RedisKeys.LOCK + terminalId;
                boolean lock = connection.setNX(key.getBytes(), "lock".getBytes());
                if (lock)
                    connection.expire(key.getBytes(), 30);
                return lock;
            }
        });
    }

    /**
     * 创建用户短信锁
     *
     * @param terminalId
     * @return
     */
    public boolean setUserSmsLock(final String terminalId) throws Exception {
        return businessRedis.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                String key = RedisKeys.LOCK + terminalId;
                boolean lock = connection.setNX(key.getBytes(), "lock".getBytes());
                if (lock)
                    connection.expire(key.getBytes(), 50);
                return lock;
            }
        });
    }

    /**
     * 删除用户锁
     *
     * @param terminalId
     */
    public void delUserLock(final String terminalId) throws Exception {
        businessRedis.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                String key = RedisKeys.LOCK + terminalId;
                connection.del(key.getBytes());
                return null;
            }
        });
    }

    /**
     * 获取自增字段
     */
    public long getIds(final String key) {
        return incrby(key, 1);
    }
}
