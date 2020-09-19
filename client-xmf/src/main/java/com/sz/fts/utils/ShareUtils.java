/*     */ package com.sz.fts.utils;
/*     */ 
/*     */

/*     */

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.InputSource;

import javax.servlet.http.HttpServletRequest;
import java.io.StringReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.Map.Entry;

/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */

/*     */
/*     */ public class ShareUtils
/*     */ {
/*  32 */   private static final String[] hexDigits = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", 
/*  33 */     "e", "f" };
/*     */ 
/*     */   public static String create_nonce_str()
/*     */   {
/*  36 */     return UUID.randomUUID().toString();
/*     */   }
/*     */ 
/*     */   public static String getSign(Map<String, Object> map, String key) {
/*  40 */     ArrayList list = new ArrayList();
/*  41 */     for (Entry entry : map.entrySet()) {
/*  42 */       if (entry.getValue() != "") {
/*  43 */         list.add((String)entry.getKey() + "=" + entry.getValue() + "&");
/*     */       }
/*     */     }
/*  46 */     int size = list.size();
/*  47 */     String[] arrayToSort = (String[])list.toArray(new String[size]);
/*  48 */     Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
/*  49 */     StringBuilder sb = new StringBuilder();
/*  50 */     for (int i = 0; i < size; i++) {
/*  51 */       sb.append(arrayToSort[i]);
/*     */     }
/*  53 */     String result = sb.toString();
/*  54 */     result = result + "key=" + key;
/*  55 */     System.out.println(result);
/*     */ 
/*  57 */     result = MD5Encode(result).toUpperCase();
/*     */ 
/*  59 */     return result;
/*     */   }
/*     */ 
/*     */   public static boolean getPlatformSign(Map<String, Object> map, String sign, String authorizeCode)
/*     */     throws NoSuchAlgorithmException
/*     */   {
/*  78 */     ArrayList list = new ArrayList();
/*  79 */     for (Entry entry : map.entrySet()) {
/*  80 */       if ((entry.getValue() != "") && 
/*  81 */         (!((String)entry.getKey()).equals("sign")) && (!((String)entry.getKey()).equals("authorizeCode")) && 
/*  82 */         (!((String)entry.getKey()).equals("bigenPage")) && (!((String)entry.getKey()).equals("endPage"))) {
/*  83 */         list.add((String)entry.getKey() + "=" + entry.getValue() + "&");
/*     */       }
/*     */     }
/*     */ 
/*  87 */     int size = list.size();
/*  88 */     String[] arrayToSort = (String[])list.toArray(new String[size]);
/*  89 */     Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
/*  90 */     StringBuilder sb = new StringBuilder();
/*  91 */     for (int i = 0; i < size; i++) {
/*  92 */       sb.append(arrayToSort[i]);
/*     */     }
/*  94 */     String result = sb.toString();
/*  95 */     result = result + "authorizeCode=" + authorizeCode;
/*  96 */     System.out.println(result);
/*  97 */     result = MD5Encode(result).toUpperCase();
/*  98 */     if (result.equalsIgnoreCase(sign)) {
/*  99 */       return true;
/*     */     }
/* 101 */     return false;
/*     */   }
/*     */ 
/*     */   public static String getTimeStamp() {
/* 105 */     return String.valueOf(System.currentTimeMillis() / 1000L);
/*     */   }
/*     */ 
/*     */   public static String MD5Encode(String origin)
/*     */   {
/* 116 */     String resultString = null;
/*     */     try {
/* 118 */       resultString = origin;
/* 119 */       MessageDigest md = MessageDigest.getInstance("MD5");
/* 120 */       md.update(resultString.getBytes("UTF-8"));
/* 121 */       resultString = byteArrayToHexString(md.digest());
/*     */     } catch (Exception e) {
/* 123 */       e.printStackTrace();
/*     */     }
/* 125 */     return resultString;
/*     */   }
/*     */ 
/*     */   public static String createSign(SortedMap<String, String> packageParams, String key)
/*     */   {
/* 132 */     StringBuffer sb = new StringBuffer();
/* 133 */     Set es = packageParams.entrySet();
/* 134 */     Iterator it = es.iterator();
/* 135 */     while (it.hasNext()) {
/* 136 */       Entry entry = (Entry)it.next();
/* 137 */       String k = (String)entry.getKey();
/* 138 */       String v = (String)entry.getValue();
/* 139 */       if ((v != null) && (!"".equals(v)) && (!"sign".equals(k)) && (!"key".equals(k))) {
/* 140 */         sb.append(k + "=" + v + "&");
/*     */       }
/*     */     }
/* 143 */     sb.append("key=" + key);
/* 144 */     String sign = MD5Util.MD5Encode(sb.toString(), "UTF-8").toUpperCase();
/* 145 */     return sign;
/*     */   }
/*     */ 
/*     */   public static String byteArrayToHexString(byte[] b)
/*     */   {
/* 157 */     StringBuilder resultSb = new StringBuilder();
/* 158 */     byte[] arrayOfByte = b; int j = b.length; for (int i = 0; i < j; i++) { byte aB = arrayOfByte[i];
/* 159 */       resultSb.append(byteToHexString(aB));
/*     */     }
/* 161 */     return resultSb.toString();
/*     */   }
/*     */ 
/*     */   private static String byteToHexString(byte b)
/*     */   {
/* 172 */     int n = b;
/* 173 */     if (n < 0) {
/* 174 */       n += 256;
/*     */     }
/* 176 */     int d1 = n / 16;
/* 177 */     int d2 = n % 16;
/* 178 */     return hexDigits[d1] + hexDigits[d2];
/*     */   }
/*     */ 
/*     */   public static byte[] callMapToXML(Map map)
/*     */   {
/* 189 */     StringBuffer sb = new StringBuffer();
/* 190 */     sb.append("<xml>");
/* 191 */     mapToXMLTest2(map, sb);
/* 192 */     sb.append("</xml>");
/* 193 */     System.out.println(sb.toString());
/*     */     try {
/* 195 */       return sb.toString().getBytes("UTF-8");
/*     */     }
/*     */     catch (Exception localException) {
/*     */     }
/* 199 */     return null;
/*     */   }
/*     */ 
/*     */   private static void mapToXMLTest2(Map map, StringBuffer sb) {
/* 203 */     Set set = map.keySet();
/* 204 */     for (Iterator it = set.iterator(); it.hasNext(); ) {
/* 205 */       String key = (String)it.next();
/* 206 */       Object value = map.get(key);
/* 207 */       if (value == null)
/* 208 */         value = "";
/* 209 */       if (value.getClass().getName().equals("java.util.ArrayList")) {
/* 210 */         ArrayList list = (ArrayList)map.get(key);
/* 211 */         sb.append("<" + key + ">");
/* 212 */         for (int i = 0; i < list.size(); i++) {
/* 213 */           HashMap hm = (HashMap)list.get(i);
/* 214 */           mapToXMLTest2(hm, sb);
/*     */         }
/* 216 */         sb.append("</" + key + ">");
/*     */       }
/* 219 */       else if ((value instanceof HashMap)) {
/* 220 */         sb.append("<" + key + ">");
/* 221 */         mapToXMLTest2((HashMap)value, sb);
/* 222 */         sb.append("</" + key + ">");
/*     */       } else {
/* 224 */         sb.append("<" + key + ">" + value + "</" + key + ">");
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static Map parseXmlToList2(String xml)
/*     */   {
/* 233 */     Map retMap = new HashMap();
/*     */     try {
/* 235 */       StringReader read = new StringReader(xml);
/*     */ 
/* 237 */       InputSource source = new InputSource(read);
/*     */ 
/* 239 */       SAXBuilder sb = new SAXBuilder();
/*     */ 
/* 241 */       Document doc = sb.build(source);
/* 242 */       Element root = doc.getRootElement();
/* 243 */       List<Element> es = root.getChildren();
/* 244 */       if ((es != null) && (es.size() != 0))
/* 245 */         for (Element element : es)
/* 246 */           retMap.put(element.getName(), element.getValue());
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 250 */       e.printStackTrace();
/*     */     }
/* 252 */     return retMap;
/*     */   }
/*     */ 
/*     */   public static String getIpAddr(HttpServletRequest request)
/*     */   {
/* 262 */     String ip = request.getHeader("x-forwarded-for");
/* 263 */     if ((ip == null) || (ip.length() == 0) || ("unknown".equalsIgnoreCase(ip))) {
/* 264 */       ip = request.getHeader("Proxy-Client-IP");
/*     */     }
/* 266 */     if ((ip == null) || (ip.length() == 0) || ("unknown".equalsIgnoreCase(ip))) {
/* 267 */       ip = request.getHeader("WL-Proxy-Client-IP");
/*     */     }
/* 269 */     if ((ip == null) || (ip.length() == 0) || ("unknown".equalsIgnoreCase(ip))) {
/* 270 */       ip = request.getRemoteAddr();
/*     */     }
/* 272 */     return ip;
/*     */   }
/*     */ 
/*     */   public static Map<String, String> getPostPara(String content)
/*     */   {
/* 282 */     Map paras = new HashMap();
/*     */     try {
/* 284 */       if ((content != null) && (!"".equals(content))) {
/* 285 */         if (content.indexOf(";") != -1) {
/* 286 */           String[] rs = content.split(";");
/* 287 */           for (int i = 0; i < rs.length; i++)
/* 288 */             if (rs[i].indexOf("=") != -1)
/*     */             {
/* 291 */               String[] temp = rs[i].split("=");
/* 292 */               if (temp.length > 1) {
/* 293 */                 String tempStr = temp[1];
/* 294 */                 int len = temp.length;
/* 295 */                 if (len >= 2) {
/* 296 */                   tempStr = "";
/* 297 */                   for (int m = 1; m < len; m++) {
/* 298 */                     tempStr = tempStr + temp[m];
/*     */                   }
/*     */                 }
/* 301 */                 paras.put(temp[0], tempStr);
/*     */               }
/*     */             }
/*     */         }
/* 305 */         else if (content.indexOf("=") != -1) {
/* 306 */           String[] temp = content.split("=");
/* 307 */           if (temp.length > 1) {
/* 308 */             paras.put(temp[0], temp[1]);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 315 */       e.printStackTrace();
/*     */     }
/* 317 */     return paras;
/*     */   }
/*     */ }

/* Location:           E:\workfile\苏州电信\01源代码\泛渠道速通卡受理系统\com\acceptp\su\acceptp\0.0.1\acceptp-0.0.1.jar
 * Qualified Name:     com.accept.stc.utils.ShareUtils
 * JD-Core Version:    0.6.2
 */