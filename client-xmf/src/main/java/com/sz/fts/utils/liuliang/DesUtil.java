package com.sz.fts.utils.liuliang;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class DesUtil {
	 /**
     * 加签
     * 
     * @param text 明文
     * @param key 密钥
     * @param algorithm 签名算法,目前KMI支持NONEwithRSA, MD2withRSA, MD5withRSA, SHA1withRSA, SHA256withRSA, SHA384withRSA, SHA512withRSA , SHA1withDSA
     * @return 签名
     * @throws Exception
     */
    public static String sign(final String text, final String key, final String algorithm) {
        final byte[] textBytes = text.getBytes();
        final byte[] keyBytes = Base64.decode(key);
        byte[] resultBytes = null;
        try {
            resultBytes = sign(textBytes, keyBytes, algorithm);
            return Base64.encode(resultBytes);
        } catch (GeneralSecurityException e) {
            System.out.println("加签出现异常");
            e.printStackTrace();
            return "";
        }

    }

    /**
     * 加签 (添加参数：编码)
     * 
     * @param text          内容
     * @param key           私钥
     * @param algorithm     签名算法
     * @param charset       编码
     * @return              加签后的内容
     * @throws UnsupportedEncodingException
     */
    public static String sign(final String text, final String key, final String algorithm,
                              final String charset) {
        final byte[] keyBytes = Base64.decode(key);
        byte[] resultBytes = null;
        try {
            final byte[] textBytes = text.getBytes(charset);
            resultBytes = sign(textBytes, keyBytes, algorithm);
            return Base64.encode(resultBytes);
        } catch (GeneralSecurityException e) {
            System.out.println("加签出现异常");
            e.printStackTrace();
            return "";
        } catch (UnsupportedEncodingException e) {
            System.out.println("不支持的编码方式");
            e.printStackTrace();
            return "";
        }

    }

    /**
     * 验签
     * 
     * @param text 明文
     * @param signText 签名
     * @param key 密钥
     * @param algorithm 验签算法,目前KMI支持NONEwithRSA, MD2withRSA, MD5withRSA, SHA1withRSA, SHA256withRSA, SHA384withRSA, SHA512withRSA , SHA1withDSA
     * @return 验签通过返回true，不通过返回false
     * @throws Exception
     */
    public static boolean verify(final String text, final String signText, final String key,
                                 final String algorithm) {
        try {
            return verify(text.getBytes(), Base64.decode(signText),
                Base64.decode(key), algorithm);
        } catch (Exception e) {
            System.out.println("验证签名出现异常");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 验签 （添加了字符编码，为了处理中文问题）
     * 
     * @param text          需要验签的内容
     * @param signText      签名
     * @param key           公钥 
     * @param algorithm     验签算法
     * @param charset       编码
     * @return
     */
    public static boolean verify(final String text, final String signText, final String key,
                                 final String algorithm, final String charset) {
        try {
            return verify(text.getBytes(charset), Base64.decode(signText), Base64.decode(key),
                algorithm);
        } catch (Exception e) {
            System.out.println("验证签名出现异常");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 加密
     * 
     * @param text 明文
     * @param key 密钥
     * @param algorithm 算法
     * @return 密文
     */
    public static String encrypt(String text, String key, String algorithm) {

        byte[] bytes = text.getBytes(); //待加/解密的数据

        byte[] keyData = Base64.decode(key); //密钥数据

        try {
            byte[] cipherBytes = symmtricCrypto(bytes, keyData, algorithm, Cipher.ENCRYPT_MODE);
            return Base64.encode(cipherBytes);
        } catch (GeneralSecurityException e) {
            System.out.println("加密出现异常");
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 解密
     * 
     * @param text 密文
     * @param key 密钥
     * @param algorithm 算法 
     * @return 明文
     */
    public static String decrypt(String text, String key, String algorithm) {

        byte[] bytes = Base64.decode(text); //待加/解密的数据
        byte[] keyData = Base64.decode(key); //密钥数据
        try {
            byte[] cipherBytes = symmtricCrypto(bytes, keyData, algorithm, Cipher.DECRYPT_MODE);
            return new String(cipherBytes);
        } catch (GeneralSecurityException e) {
            System.out.println("解密出现异常");
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 对称加解密(/CBC/PKCS5Padding模式)
     * KMI默认的是/CBC/PKCS5Padding模式
     * 
     * @param text
     *            待加/解密的数据
     * @param keyData
     *            密钥数据
     * @param algorithm
     *            对称加密算法名称。KMI默认使用3DES算法，即“DESede”.
     *            目前KMI接受的参数有: AES, Blowfish, DESede
     * @param mode
     *            加解密标识：加密——Cipher.ENCRYPT_MODE；解密——Cipher.DECRYPT_MODE。
     * @return 密文(加密)/明文（解密）。
     * @throws GeneralSecurityException
     *             当用户输入KMI不接受的参数时,会抛出异常
     *             当密钥数据的长度不符合算法要求时,会抛出异常
     */
    private static byte[] symmtricCrypto(byte[] text, byte[] keyData, String algorithm, int mode)
                                                                                                 throws GeneralSecurityException {
        String fullAlg = algorithm + "/CBC/PKCS5Padding";
        byte[] iv = initIv(fullAlg);
        return doCrypto(text, keyData, iv, fullAlg, "CBC", "PKCS5Padding", mode);
    }

    /**
     * 初始向量的方法
     * 
     * @param fullAlg
     * @return
     * @throws GeneralSecurityException  
     */
    private static byte[] initIv(String fullAlg) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(fullAlg);
        int blockSize = cipher.getBlockSize();
        byte[] iv = new byte[blockSize];
        for (int i = 0; i < blockSize; ++i) {
            iv[i] = 0;
        }
        return iv;
    }

    /**
     * 实现加解密的方法
     * 
     * @param text
     *            待加/解密的数据
     * @param keyData
     *            密钥数据
     * @param iv
     *            初始向量
     * @param fullAlg
     *            对称加密算法全名。eg.DESede/CBC/PKCS5Padding
     * @param padding
     *            填充模式,目前KMI接受的参数有PKCS5Padding和NoPadding.
     * @param mode
     *            加解密标识：加密——Cipher.ENCRYPT_MODE；解密——Cipher.DECRYPT_MODE。
     * @return 密文(加密)/明文（解密）。
     * @throws GeneralSecurityException
     *             当用户输入KMI不接受的参数时,会抛出异常
     *             当密钥数据的长度不符合算法要求时,会抛出异常
     *             在NoPadding填充模式下,当待加密的数据不是相应的算法的块大小的整数倍时,会抛出异常
     */
    private static byte[] doCrypto(byte[] text, byte[] keyData, byte[] iv, String fullAlg,
                                   String workingMode, String padding, int mode)
                                                                                throws GeneralSecurityException {
        if (!"CBC".equals(workingMode) && !"ECB".equals(workingMode)) {
            throw new GeneralSecurityException("错误的工作模式,目前KMI只支持CBC和ECB两种工作模式");
        }

        if (!"PKCS5Padding".equals(padding) && !"NoPadding".equals(padding)) {
            throw new GeneralSecurityException("错误的填充模式,目前KMI只支持PKCS5Padding和NoPadding两种工作模式");
        }

        if (mode != Cipher.ENCRYPT_MODE && mode != Cipher.DECRYPT_MODE) {
            throw new GeneralSecurityException(
                "错误的加解密标识,目前KMI只支持Cipher.ENCRYPT_MODE和Cipher.DECRYPT_MODE");
        }

        Cipher cipher = getCipher(keyData, iv, fullAlg, workingMode, mode);
        return cipher.doFinal(text);
    }

    /**
     * 根据参数初始化cipher的方法
     * 
     * @param keyData
     *            密钥数据
     * @param fullAlg
     *            用来初始化Cipher对象的算法全称(已经加上工作模式和填充模式的)
     * @param workingMode
     *            工作模式,目前KMI接受的参数有CBC和ECB.
     *  padding
     *            填充模式,目前KMI接受的参数有PKCS5Padding和NoPadding.
     * @param mode
     *            加解密标识：加密——Cipher.ENCRYPT_MODE；解密——Cipher.DECRYPT_MODE。
     * @return cipher
     * @throws GeneralSecurityException
     */
    private static Cipher getCipher(byte[] keyData, byte[] iv, String fullAlg, String workingMode,
                                    int mode) throws GeneralSecurityException {

        Cipher cipher = Cipher.getInstance(fullAlg);

        SecretKey secretKey = new SecretKeySpec(keyData, fullAlg.substring(0, fullAlg.indexOf("/")));

        if ("CBC".equals(workingMode)) {
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(mode, secretKey, ivSpec);
        } else {
            cipher.init(mode, secretKey);
        }
        return cipher;
    }

    /**
     * 使用私钥进行签名的方法
     * 
     * @param text 
     *            待签名的数据
     * @param privateKeyData 
     *            私钥数据
     * @param algorithm 
     *            签名算法,目前KMI支持NONEwithRSA, MD2withRSA, MD5withRSA, SHA1withRSA, SHA256withRSA, SHA384withRSA, SHA512withRSA , SHA1withDSA
     * @return 签名后的数据
     * @throws GeneralSecurityException 
     */
    private static byte[] sign(byte[] text, byte[] privateKeyData, String algorithm)
                                                                                    throws GeneralSecurityException {
        PrivateKey privateKey = getPrivateKey(privateKeyData, algorithm);
        Signature signatureChecker = Signature.getInstance(algorithm);
        signatureChecker.initSign(privateKey);
        signatureChecker.update(text);
        return signatureChecker.sign();
    }

    /**
     * 使用公钥进行验签的方法
     * 
     * @param text 
     *            原始数据数据
     * @param signedText 
     *            签名过的数据
     * @param publicKeyData 
     *            公钥数据
     * @param algorithm 
     *            签名算法,目前KMI支持NONEwithRSA, MD2withRSA, MD5withRSA, SHA1withRSA, SHA256withRSA, SHA384withRSA, SHA512withRSA , SHA1withDSA
     * @return 如果验签成功,返回true,验签失败,返回false
     * @throws GeneralSecurityException 
     */
    private static boolean verify(byte[] text, byte[] signedText, byte[] publicKeyData,
                                  String algorithm) throws GeneralSecurityException {
        PublicKey publicKey = getPublicKey(publicKeyData, algorithm);
        Signature signatureChecker = Signature.getInstance(algorithm);
        signatureChecker.initVerify(publicKey);
        signatureChecker.update(text);
        return signatureChecker.verify(signedText);
    }

    /**  
     * 得到公钥  
     * @param keyData 密钥数据  
     * @throws GeneralSecurityException  
     */
    private static PublicKey getPublicKey(byte[] keyData, String algorithm)
                                                                           throws GeneralSecurityException {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyData);
        KeyFactory keyFactory = KeyFactory.getInstance(LLStringUtils
            .substringAfter(algorithm, "with"));
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    /**  
     * 得到私钥  
     * @param keyData 密钥数据  
     * @throws GeneralSecurityException  
     */
    private static PrivateKey getPrivateKey(byte[] keyData, String algorithm)
                                                                             throws GeneralSecurityException {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyData);
        KeyFactory keyFactory = KeyFactory.getInstance(LLStringUtils
            .substringAfter(algorithm, "with"));
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }
}
