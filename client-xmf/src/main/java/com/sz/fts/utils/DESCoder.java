/**
 * @(#)com.ztesoft.resmaster.sys.code.DESCoder.java
 * 南京中兴软创科技有限责任公司
 * @date 2011-12-9
 */
package com.sz.fts.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.SecureRandom;

/**
 * DES安全编码组件
 * 
 * <pre>
 * 支持 DES、DESede(TripleDES,就是3DES)、AES、Blowfish、RC2、RC4(ARCFOUR)
 * DES          		key size must be equal to 56
 * DESede(TripleDES) 	key size must be equal to 112 or 168
 * AES          		key size must be equal to 128, 192 or 256,but 192 and 256 bits may not be available
 * Blowfish     		key size must be multiple of 8, and can only range from 32 to 448 (inclusive)
 * RC2          		key size must be between 40 and 1024 bits
 * RC4(ARCFOUR) 		key size must be between 40 and 1024 bits
 * 具体内容 需要关注 JDK Document http://.../docs/technotes/guides/security/SunProviders.html
 * </pre>
 * 
 * @author <a href="mailto:xu.wenhui@zte.com.cn">徐文辉</a>
 * @version 1.0
 */

public class DESCoder extends Coder {
	/**
	 * ALGORITHM 算法 <br>
	 * 可替换为以下任意一种算法，同时key值的size相应改变。
	 * 
	 * 
	 * <pre>
	 * DES          		key size must be equal to 56
	 * DESede(TripleDES) 	key size must be equal to 112 or 168
	 * AES          		key size must be equal to 128, 192 or 256,but 192 and 256 bits may not be available
	 * Blowfish     		key size must be multiple of 8, and can only range from 32 to 448 (inclusive)
	 * RC2          		key size must be between 40 and 1024 bits
	 * RC4(ARCFOUR) 		key size must be between 40 and 1024 bits
	 * </pre>
	 * 
	 * 在Key toKey(byte[] key)方法中使用下述代码
	 * 
	 * <code>SecretKey secretKey = new SecretKeySpec(key, ALGORITHM);</code> 替换
	 * <code>
	 * DESKeySpec dks = new DESKeySpec(key);
	 * SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
	 * SecretKey secretKey = keyFactory.generateSecret(dks);
	 * </code>
	 */
	public static final String ALGORITHM = "AES";

	/**
	 * 转换密钥<br>
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	private static Key toKey(byte[] key) throws Exception {
		// DESKeySpec dks = new DESKeySpec(key);
		// SecretKeyFactory keyFactory =
		// SecretKeyFactory.getInstance(ALGORITHM);
		// SecretKey secretKey = keyFactory.generateSecret(dks);

		// 当使用其他对称加密算法时，如AES、Blowfish等算法时，用下述代码替换上述三行代码
		SecretKey secretKey = new SecretKeySpec(key, ALGORITHM);

		return secretKey;
	}

	/**
	 * 加密
	 * 
	 * @param data
	 *            二进制数据
	 * @param key
	 *            密钥
	 * @return
	 * @throws Exception
	 */
	public static byte[] encrypt(byte[] data, String key) throws Exception {
		Key k = toKey(decryptBASE64(key));
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, k);

		return cipher.doFinal(data);
	}

	/**
	 * 解密
	 * 
	 * @param data
	 *            二进制数据
	 * @param key
	 *            密钥
	 * @return
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] data, String key) throws Exception {
		Key k = toKey(decryptBASE64(key));

		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, k);

		return cipher.doFinal(data);
	}

	/**
	 * 加密
	 * 
	 * @param data
	 *            需要加密的字符串
	 * @param key
	 *            密钥
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(String data, String key) throws Exception {
		Key k = toKey(decryptBASE64(key));
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, k);

		byte[] encryptData = cipher.doFinal(data.getBytes());
		return DESCoder.encryptBASE64(encryptData);
	}

	/**
	 * 解密
	 * 
	 * @param data
	 *            加密后的字符串
	 * @param key
	 *            密钥
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(String data, String key) throws Exception {
		Key k = toKey(decryptBASE64(key));

		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, k);

		byte[] decryptData = cipher.doFinal(decryptBASE64(data));
		return new String(decryptData);
	}

	/**
	 * 生成密钥
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String initKey() throws Exception {
		return initKey(null);
	}

	/**
	 * 生成密钥
	 * 
	 * @param seed
	 *            密钥依据字符串
	 * @return
	 * @throws Exception
	 */
	public static String initKey(String seed) throws Exception {
		SecureRandom secureRandom = null;

		if (seed != null) {
			secureRandom = new SecureRandom(decryptBASE64(seed));
		} else {
			secureRandom = new SecureRandom();
		}

		KeyGenerator kg = KeyGenerator.getInstance(ALGORITHM);
		kg.init(secureRandom);

		SecretKey secretKey = kg.generateKey();

		return encryptBASE64(secretKey.getEncoded());
	}

	/*public static void main(String[] args) throws Exception {
		String inputStr = DateUtils.getCurrTimeStr(DateUtils.DB_STORE_DATE) + "&uuid=121212121212132323242323";
		System.err.println("原文:\t" + inputStr);
		
		String key = DESCoder.initKey("江苏电信公司");
		System.err.println("密钥:\t" + key);

		byte[] inputData = inputStr.getBytes();
		inputData = DESCoder.encrypt(inputData, key);

		System.err.println("加密后:\t" + DESCoder.encryptBASE64(inputData));

		byte[] outputData = DESCoder.decrypt(inputData, key);
		String outputStr = new String(outputData);

		System.err.println("解密后:\t" + outputStr);

		String encryptData = DESCoder.encrypt(inputStr, key);
		System.err.println("加密后:\t" + encryptData);

		String decryptData = DESCoder.decrypt(encryptData, key);
		System.err.println("解密后:\t" + decryptData);

	}*/
}
