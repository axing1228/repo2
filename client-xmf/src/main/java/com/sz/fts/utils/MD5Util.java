package com.sz.fts.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class MD5Util {

	/**
	 *
	 * @Title: encrypt
	 * @Description: 加密方式
	 * @author: 杨坚
	 * @param openid
	 * @throws NoSuchAlgorithmException
	 * @return: String
	 */
	public static String encrypt(String openid) throws NoSuchAlgorithmException{
		String flag="wssmall_jspd";//固定的常量
		String str=openid+DateUtils.getCurrentYearMonthDay()+flag;
		String myToken=MD5Util.MD5(str);
		System.out.println("====加密数据====>"+myToken+"=========>"+str);
		return myToken;
	}



	public final static String MD5(String s) throws NoSuchAlgorithmException {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		byte[] strTemp = s.getBytes();
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.update(strTemp);
		byte[] md = md5.digest();
		int j = md.length;
		char str[] = new char[j * 2];
		int k = 0;
		for (int i = 0; i < j; i++) {
			byte byte0 = md[i];
			str[k++] = hexDigits[byte0 >>> 4 & 0xf];
			str[k++] = hexDigits[byte0 & 0xf];
		}
		return new String(str);
	}

	private static final String[] hexDigits = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d",
/* 79 */     "e", "f" };
	/*    */
	/*    */   private static String byteArrayToHexString(byte[] b)
	/*    */   {
		/*  9 */     StringBuffer resultSb = new StringBuffer();
		/* 10 */     for (int i = 0; i < b.length; i++) {
			/* 11 */       resultSb.append(byteToHexString(b[i]));
			/*    */     }
		/* 13 */     return resultSb.toString();
		/*    */   }
	/*    */
	/*    */   private static String byteToHexString(byte b) {
		/* 17 */     int n = b;
		/* 18 */     if (n < 0)
			/* 19 */       n += 256;
		/* 20 */     int d1 = n / 16;
		/* 21 */     int d2 = n % 16;
		/* 22 */     return hexDigits[d1] + hexDigits[d2];
		/*    */   }
	/*    */
	/*    */   public static String MD5RandomEncode(String origin, String charsetname) {
		/* 26 */     String resultString = null;
		/*    */     try {
			/* 28 */       resultString = new String(UUID.randomUUID().toString() + origin);
			/* 29 */       MessageDigest md = MessageDigest.getInstance("MD5");
			/* 30 */       if ((charsetname == null) || ("".equals(charsetname)))
				/* 31 */         resultString = byteArrayToHexString(md.digest(resultString.getBytes()));
				/*    */       else
				/* 33 */         resultString = byteArrayToHexString(md.digest(resultString.getBytes(charsetname)));
			/*    */     } catch (Exception localException) {
			/*    */     }
		/* 36 */     return resultString;
		/*    */   }
	/*    */
	/*    */   public static String MD5Encode(String origin, String charsetname) {
		/* 40 */     String resultString = null;
		/*    */     try {
			/* 42 */       resultString = new String(origin);
			/* 43 */       MessageDigest md = MessageDigest.getInstance("MD5");
			/* 44 */       if ((charsetname == null) || ("".equals(charsetname)))
				/* 45 */         resultString = byteArrayToHexString(md.digest(resultString.getBytes()));
				/*    */       else
				/* 47 */         resultString = byteArrayToHexString(md.digest(resultString.getBytes(charsetname)));
			/*    */     } catch (Exception localException) {
			/*    */     }
		/* 50 */     return resultString;
		/*    */   }
	/*    */
	/*    */   public static String MD5ToSixteenEncode(String origin, String charsetname) {
		/* 54 */     String resultString = null;
		/*    */     try {
			/* 56 */       resultString = new String(origin);
			/* 57 */       MessageDigest md = MessageDigest.getInstance("MD5");
			/* 58 */       if ((charsetname == null) || ("".equals(charsetname)))
				/* 59 */         resultString = byteArrayToHexString(md.digest(resultString.getBytes()));
				/*    */       else
				/* 61 */         resultString = byteArrayToHexString(md.digest(resultString.getBytes(charsetname)));
			/*    */     } catch (Exception localException) {
			/*    */     }
		/* 64 */     return resultString.substring(8, 24);
		/*    */   }
	/*    */
	/*    */   public static String MD5Encode(String origin) {
		/* 68 */     String resultString = null;
		/*    */     try {
			/* 70 */       resultString = new String(origin);
			/* 71 */       MessageDigest md = MessageDigest.getInstance("MD5");
			/* 72 */       resultString = byteArrayToHexString(md.digest(resultString.getBytes()));
			/*    */     } catch (Exception localException) {
			/*    */     }
		/* 75 */     return resultString;
		/*    */   }

}
