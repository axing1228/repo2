package com.sz.fts.impl.wxpay.utils;

/**
 * @author 征华兴
 * @date 下午 4:13  2018/12/24 0024
 * @Copyright 江苏鸿信系统集成有限公司 All rights reserved
 */

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;

public class ThreeDES
{

    public ThreeDES()
    {
    }

    public static byte[] encryptMode(byte keybyte[], byte src[])
    {
        try
        {
            javax.crypto.SecretKey deskey = new SecretKeySpec(keybyte, "DESede");
            Cipher c1 = Cipher.getInstance("DESede");
            c1.init(1, deskey);
            return c1.doFinal(src);
        }
        catch(NoSuchAlgorithmException e1)
        {
            e1.printStackTrace();
        }
        catch(NoSuchPaddingException e2)
        {
            e2.printStackTrace();
        }
        catch(Exception e3)
        {
            e3.printStackTrace();
        }
        return null;
    }

    public static byte[] decryptMode(byte keybyte[], byte src[])
    {
        try
        {
            javax.crypto.SecretKey deskey = new SecretKeySpec(keybyte, "DESede");
            Cipher c1 = Cipher.getInstance("DESede");
            c1.init(2, deskey);
            return c1.doFinal(src);
        }
        catch(NoSuchAlgorithmException e1)
        {
            e1.printStackTrace();
        }
        catch(NoSuchPaddingException e2)
        {
            e2.printStackTrace();
        }
        catch(Exception e3)
        {
            e3.printStackTrace();
        }
        return null;
    }

    public static String byte2hex(byte b[])
    {
        String hs = "";
        String stmp = "";
        for(int n = 0; n < b.length; n++)
        {
            stmp = Integer.toHexString(b[n] & 0xff);
            if(stmp.length() == 1)
                hs = hs + "0" + stmp;
            else
                hs = hs + stmp;
            if(n < b.length - 1)
                hs = hs + ":";
        }

        return hs.toUpperCase();
    }

    private static final String Algorithm = "DESede";
}


