package com.sz.fts.redis.bean;


import com.alibaba.fastjson.JSON;
import com.sz.fts.redis.util.SerializeUtil;

import java.io.IOException;
import java.io.Serializable;


public abstract class AbstractRedisBean implements Serializable {
    private static final long serialVersionUID = -7170522606039843848L;
    /**
     * 反序列化，把byte[]数据恢复成对象
     *
     * @param data
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static AbstractRedisBean fromByteArray(byte[] data) throws IOException, ClassNotFoundException {
        return (AbstractRedisBean) SerializeUtil.unserialize(data);
    }

    /**
     * 序列化，把本地对象序列化为byte[]
     *
     * @return
     */
    public byte[] toByteArray() {
        return SerializeUtil.serialize(this);
    }

    /**
     * 反序列化，把byte[]序列化为本地对象
     *
     * @return
     */
    public Object unserialize(byte[] bytes) {
        try {
            return SerializeUtil.unserialize(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
