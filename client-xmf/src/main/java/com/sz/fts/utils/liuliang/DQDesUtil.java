package com.sz.fts.utils.liuliang;


import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.SecureRandom;

/**
 * 加密工具类
 * 
 */
public class DQDesUtil {

    /**
     * 采用DES 加/解 密
     */
    private final static String DES     = "DES";

    /**
     * 编码方式
     */
    private final static String CHARSET = "UTF-8";

    /**
     * 加密数据
     * 
     * @param data          待加密的数据
     * @param key           密钥
     * @return              密文
     */
    public static String encrypt(String data, String key) {
        byte[] bt = null;
        try {
            bt = encrypt(data.getBytes(CHARSET), key.getBytes(CHARSET));
            return (null == bt) ? "" : Base64.encode(bt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /*** 加密数据
     * 
     * @param data      待加密数据
     * @param key       密钥
     * @return
     * @throws Exception
     */
    private static byte[] encrypt(byte[] data, byte[] key) {
        SecureRandom sr = new SecureRandom();
        DESKeySpec dks = null;
        Cipher cipher = null;
        try {
            dks = new DESKeySpec(key);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
            SecretKey securekey = keyFactory.generateSecret(dks);
            cipher = Cipher.getInstance(DES);
            cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解密数据
     * 
     * @param data  待解密的数据
     * @param key   密钥
     * @return
     */
    public static String decrypt(String data, String key) {
        if (LLStringUtils.isBlank(data)) {
            return "";
        }
        byte[] buf;
        try {
            buf = Base64.decode(data);
            byte[] bt = decrypt(buf, key.getBytes(CHARSET));
            return new String(bt, CHARSET);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 解密
     * 
     * @param data      待解密数据
     * @param key       密钥
     * @return
     * @throws Exception
     */
    private static byte[] decrypt(byte[] data, byte[] key) throws Exception {
        SecureRandom sr = new SecureRandom();
        DESKeySpec dks = new DESKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance(DES);
        cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
        return cipher.doFinal(data);
    }
}