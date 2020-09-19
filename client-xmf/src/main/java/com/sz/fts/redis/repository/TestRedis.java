package com.sz.fts.redis.repository;

import com.sz.fts.redis.keys.RedisKeys;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.stereotype.Component;

/**
 * @author 耿怀志
 * @version [版本号, 2017/6/15]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
@Component
public class TestRedis extends AbstractRepository{

    public void setString(final String key, final String value, int time){
        setValue(key, value, time);
    }


    public String getString(final String key){
        return getValue(key);
    }


    /**
     * 创建用户锁
     * @param terminalId
     * @return
     */
    public boolean setUserLock(final String terminalId)throws Exception{
        return businessRedis.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                String key = RedisKeys.LOCK+terminalId;
                boolean lock = connection.setNX(key.getBytes(),"lock".getBytes());
                if(lock)
                    connection.expire(key.getBytes(),10);
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
     * @param terminalId
     */
    public void delUserLock(final String terminalId)throws Exception{
        businessRedis.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                String key = RedisKeys.LOCK+terminalId;
                connection.del(key.getBytes());
                return null;
            }
        });
    }

    /**
     * 获取游戏编号
     */
    public long getDuobaoPv() {
        return incrby(RedisKeys.DUOBAO_PV , 1);
    }

    public final static String XXMB = "xxmb_send2017_log";
    /**
     * 获取流水编号
     */
    public long getXxmb() {
        return incrby(XXMB , 1);
    }

    public long getIncrbyId(final String key) {
        return incrby(key , 1);
    }



}
