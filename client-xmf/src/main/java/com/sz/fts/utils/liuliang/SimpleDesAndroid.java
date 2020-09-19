package com.sz.fts.utils.liuliang;

import javax.crypto.Cipher;
import java.security.Key;

/**
 * 加密、解密辅助类
 * <p>
 * 说明：
 * </p>
 * <p>
 * 1、DES加、解密时的Key已经内置, 这样做的好处有两点:A. 避免维护服务器与手机端Key的同步;B.给客户的调用提供便利;
 * </p>
 * <p>
 * 2、为了避免在数据传输的过程中产生乱码, 只对外提供了String类型数据的加、解密;
 * </p>
 * 
 * @author wangbing
 * 
 */
public class SimpleDesAndroid {
	public static final String UTF8 = "UTF-8";
	public static final String GBK = "GBK";

	public static String strDefaultKey = "t2b4h6y4l5";
	public String charset = UTF8;
	public Cipher encryptCipher = null;
	public Cipher decryptCipher = null;

	/**
	 * 将byte数组转换为表示16进制值的字符串， 如：byte[]{8,18}转换为：0813， 和public static byte[]
	 * hexStr2ByteArr(String strIn) 互为可逆的转换过程
	 * 
	 * @param bytes
	 *            需要转换的byte数组
	 * @return 转换后的字符串
	 * @throws Exception
	 *             本方法不处理任何异常，所有异常全部抛出
	 */
	public static String byteArr2HexStr(byte[] bytes) throws Exception {
		int iLen = bytes.length;
		// 每个byte用两个字符才能表示，所以字符串的长度是数组长度的两倍
		StringBuffer sb = new StringBuffer(iLen * 2);
		for (int i = 0; i < iLen; i++) {
			int intTmp = bytes[i];
			// 把负数转换为正数
			while (intTmp < 0) {
				intTmp = intTmp + 256;
			}
			// 小于0F的数需要在前面补0
			if (intTmp < 16) {
				sb.append("0");
			}
			sb.append(Integer.toString(intTmp, 16));
		}
		return sb.toString();
	}

	/**
	 * 将表示16进制值的字符串转换为byte数组， 和public static String byteArr2HexStr(byte[] arrB)
	 * 互为可逆的转换过程
	 * 
	 * @param text
	 *            需要转换的字符串
	 * @return 转换后的byte数组
	 * @throws Exception
	 *             本方法不处理任何异常，所有异常全部抛出
	 * @author
	 */
	public byte[] hexStr2ByteArr(String text) throws Exception {
		byte[] arrB = text.getBytes(charset);
		int iLen = arrB.length;

		// 两个字符表示一个字节，所以字节数组长度是字符串长度除以2
		byte[] arrOut = new byte[iLen / 2];
		for (int i = 0; i < iLen; i = i + 2) {
			String strTmp = new String(arrB, i, 2);
			arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
		}
		return arrOut;
	}

	/**
	 * 默认构造方法，使用默认密钥
	 * 
	 * @throws Exception
	 */
	public SimpleDesAndroid() throws Exception {
		this(strDefaultKey);
	}

	/**
	 * 指定密钥构造方法
	 * 
	 * @param key
	 *            指定的密钥
	 * @throws Exception
	 */
	public SimpleDesAndroid(String key) throws Exception {
		Key keyTmp = getKey(key.getBytes(charset));

		encryptCipher = Cipher.getInstance("DES");
		encryptCipher.init(Cipher.ENCRYPT_MODE, keyTmp);

		decryptCipher = Cipher.getInstance("DES");
		decryptCipher.init(Cipher.DECRYPT_MODE, keyTmp);
	}

	/**
	 * 加密字节数组
	 * 
	 * @param bytes
	 *            需加密的字节数组
	 * @return 加密后的字节数组
	 * @throws Exception
	 */
	public byte[] encrypt(byte[] bytes) throws Exception {
		return encryptCipher.doFinal(bytes);
	}

	/**
	 * 解密字节数组
	 * 
	 * @param bytes
	 *            需解密的字节数组
	 * @return 解密后的字节数组
	 * @throws Exception
	 */
	public byte[] decrypt(byte[] bytes) throws Exception {
		return decryptCipher.doFinal(bytes);
	}

	/**
	 * 加密字符串
	 * 
	 * @param text
	 *            需加密的字符串(明文)
	 * @return 加密后的字符串(密文)
	 * @throws Exception
	 *             加密时, direction的值只能是DataDirection.TO_MOBILE 或
	 *             DataDirection.TO_SERVER, 否则会抛出LawlessDataDirectionException
	 */
	public static String encrypt(String text, DataDirection direction)
			throws Exception {
		if (direction == DataDirection.FROM_MOBILE
				|| direction == DataDirection.FROM_SERVER)
			throw new LawlessDataDirectionException(
					"direction can't be FROM_MOBILE or FROM_SERVER when encrypt!!");
		if (text == null) {
			text = "";
		}
		// return byteArr2HexStr(encrypt(strIn.getBytes()));
		SimpleDesAndroid des = new SimpleDesAndroid();
		if (direction == DataDirection.TO_MOBILE) {
			des.setCharset(UTF8);
		} else if (direction == DataDirection.TO_SERVER) {
			des.setCharset(GBK);
		}

		return byteArr2HexStr(des.encrypt(text.getBytes(des.getCharset())));
	}

	/**
	 * 解密字符串
	 * 
	 * @param text
	 *            需解密的字符串(密文)
	 * @return 解密后的字符串(明文)
	 * @throws Exception
	 *             解密时, direction的值只能是DataDirection.FROM_MOBILE 或
	 *             DataDirection.FROM_SERVER, 否则会抛出LawlessDataDirectionException
	 */
	public static String decrypt(String text, DataDirection direction)
			throws Exception {
		if (direction == DataDirection.TO_MOBILE
				|| direction == DataDirection.TO_SERVER)
			throw new LawlessDataDirectionException(
					"direction can't be TO_MOBILE or TO_SERVER when decrypt!!");
		// return new String(decrypt(hexStr2ByteArr(strIn)));
		SimpleDesAndroid des = new SimpleDesAndroid();
		if (direction == DataDirection.FROM_MOBILE) {
			des.setCharset(GBK);
		} else if (direction == DataDirection.FROM_SERVER) {
			des.setCharset(UTF8);
		}
		byte[] bytes =null;
		try {
			bytes = des.decrypt(des.hexStr2ByteArr(text));
		} catch (Exception e) {
			e.printStackTrace();
		} 
		if(null!=bytes)
		{
			return new String(bytes);	
		}
		else
		{
			return text;
		}
	}

	/**
	 * 从指定字符串生成密钥，密钥所需的字节数组长度为8位 不足8位时后面补0，超出8位只取前8位
	 * 
	 * @param bytes
	 *            构成该字符串的字节数组
	 * @return 生成的密钥
	 * @throws Exception
	 */
	public Key getKey(byte[] bytes) throws Exception {
		// 创建一个空的8位字节数组（默认值为0）
		byte[] arrB = new byte[8];

		// 将原始字节数组转换为8位
		for (int i = 0; i < bytes.length && i < arrB.length; i++) {
			arrB[i] = bytes[i];
		}

		// 生成密钥
		Key key = new javax.crypto.spec.SecretKeySpec(arrB, "DES");

		return key;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public String getCharset() {
		return charset;
	}
}