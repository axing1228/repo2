package com.sz.fts.utils;

import net.sf.json.JSONObject;

/**
 * @author 耿怀志
 * @version [版本号, 2017/4/5]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class DistanceUtil {

    private static double rad(double d) {
        return d * 3.141592653589793D / 180.0D;
    }

    public static double getDistance(double lng1, double lat1, double lng2, double lat2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2.0D * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2.0D), 2.0D) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2.0D), 2.0D)));
        s *= 6378137.0D;
        s = (double)(Math.round(s * 10000.0D) / 10000L);
        return s;
    }

    private static double x_pi = 3.14159265358979324 * 3000.0 / 180.0;

    //腾讯转百度
    public static JSONObject Convert_GCJ02_To_BD09(double lat, double lng)
    {
        JSONObject out = new JSONObject();
        double x = lng, y = lat;
        double z =Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * x_pi);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * x_pi);
        lng = z * Math.cos(theta) + 0.0065;
        lat = z * Math.sin(theta) + 0.006;
        out.put("lat",lat);
        out.put("lng",lng);
        return out;
    }

    //百度转腾讯
    public static JSONObject Convert_BD09_To_GCJ02(double lat, double lng)
    {
        JSONObject out = new JSONObject();
        double x = lng - 0.0065, y = lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
        lng = z * Math.cos(theta);
        lat = z * Math.sin(theta);
        out.put("lat",lat);
        out.put("lng",lng);
        return out;
    }
}
