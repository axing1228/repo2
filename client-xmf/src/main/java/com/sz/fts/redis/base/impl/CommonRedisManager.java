package com.sz.fts.redis.base.impl;



import com.sz.fts.redis.base.BaseRedisManager;
import com.sz.fts.redis.keys.RedisKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Component
public class CommonRedisManager implements BaseRedisManager {

    @Autowired
    protected RedisTemplate<Serializable, Serializable> commonRedis;

    /**
     * 是否是黑名单用户
     * @param mobile
     * @return
     */
    public boolean isBlackListUser(final String mobile){
        return commonRedis.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException{
                return connection.sIsMember(RedisKeys.CACHE_COMMON_BLACK_LIST.getBytes(), mobile.getBytes());
            }
        });
    }


    @Override
    public void del(String... keys){

    }

    @Override
    public void set(String key, byte[] value, long expireTime){

    }

    @Override
    public void hset(String key, String field, byte[] value){

    }

    @Override
    public void hMSet(String key, Map<byte[], byte[]> map){

    }

    @Override
    public void mset(Map<byte[], byte[]> map){

    }

    @Override
    public byte[] get(String key){
        return new byte[0];
    }

    @Override
    public byte[] hget(String key, String field){
        return new byte[0];
    }

    @Override
    public List<byte[]> hvals(String key){
        return null;
    }

    @Override
    public void lpush(String key, byte[]... bytes){

    }

    @Override
    public void rpush(String key, byte[]... bytes){

    }

    @Override
    public List<byte[]> lrange(String key, long start, long end){
        return null;
    }

    @Override
    public boolean exists(String key){
        return false;
    }

    @Override
    public boolean hexists(String key, String field){
        return false;
    }

    @Override
    public Set<byte[]> keys(String pattern){
        return null;
    }

    @Override
    public Long hincrby(String key, String field, long l){
        return null;
    }

    @Override
    public Map<byte[], byte[]> hgetall(String key){
        return null;
    }
}
