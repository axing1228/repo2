package com.sz.fts.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 时间工具类
 *
 * @author qss
 */
public class DateUtils {

    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static SimpleDateFormat sdf14 = new SimpleDateFormat("yyyyMMddHHmmss");

    public static SimpleDateFormat sdf17 = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    public static SimpleDateFormat sdfhuman = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static SimpleDateFormat sdfcn = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");

    public static SimpleDateFormat sdfymd = new SimpleDateFormat("yyyy-MM-dd");

    public static SimpleDateFormat sdfymd2 = new SimpleDateFormat("yyyyMMdd");

    /**
     * 比较2个时间大小 true 第一小 false 第一大
     *
     * @param firstDate 第一个时间
     * @param afterDate 第二个时间
     * @return
     * @throws Exception
     */
    public static boolean getDateComPareTo(String firstDate, String afterDate) throws Exception {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dateTime1 = dateFormat.parse(firstDate);
        Date dateTime2 = dateFormat.parse(afterDate);
        int i = dateTime1.compareTo(dateTime2);
        if (i < 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 比较2个时间大小 第一小 时为1 第一大为2 相等为0
     *
     * @param firstDate 第一个时间
     * @param afterDate 第二个时间
     * @return
     * @throws Exception
     */
    public static int getDateComPareToFans(String firstDate, String afterDate) throws Exception {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dateTime1 = dateFormat.parse(firstDate);
        Date dateTime2 = dateFormat.parse(afterDate);
        int i = dateTime1.compareTo(dateTime2);
        if (i < 0) {
            return 1;
        } else if (i > 0) {
            return 2;
        } else {
            return 0;
        }
    }

    /**
     * 日期增加几天
     *
     * @param datestr 日期
     * @param step    增加的天数，负数为减少的天数
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static String addMonth(String datestr, int step) {
        try {
            SimpleDateFormat sdfhuman = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Date date = sdfhuman.parse(datestr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MONTH, step);
            return sdfhuman.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * 日期增加几天
     *
     * @param datestr 日期
     * @param step    增加的天数，负数为减少调试
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static String addDay(String datestr, int step) {
        try {
            SimpleDateFormat sdfhuman = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Date date = sdfhuman.parse(datestr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DATE, step);
            return sdfhuman.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;

    }


    /**
     * 日期增加几小时
     *
     * @param datestr 日期
     * @param step    增加的天数，负数为减少调试
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static String addHour(String datestr, int step) {
        try {
            SimpleDateFormat sdfhuman = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Date date = sdfhuman.parse(datestr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.HOUR, step);
            return sdfhuman.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;

    }


    /**
     * 日期增加几小时
     *
     * @param datestr 日期
     * @param step    增加的天数，负数为减少调试
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static String addFenZhong(String datestr, int step) {
        try {
            SimpleDateFormat sdfhuman = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = sdfhuman.parse(datestr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MINUTE, step);
            return sdfhuman.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;

    }


    /**
     * 与当时时间相比较，返回小时差
     *
     * @param time
     * @return 小时
     * @throws ParseException
     */
    public static Integer getHoursCompared(String time) {
        if (null == time)
            return 0;
        // 转化为时间
        String timeStr = DateUtils.timestampToTime(time);
        Date date2 = null;
        try {
            date2 = dateFormat.parse(timeStr);
        } catch (ParseException e) {
            throw new RuntimeException((new StringBuilder("string date value[")).append(date2)
                    .append("] parsing using [").append(timeStr).append("] parse error").toString());
        }

        Date date1 = new Date();// 当前时间
        int hours = (int) ((date1.getTime() - date2.getTime()) / (60 * 60 * 1000));// 得到时间差
        return hours;
    }

    public static String getCurrentTimeHuman() {
        SimpleDateFormat sdfhuman = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdfhuman.format(new Date());
    }

    public static String getCurrentTime17() {
        SimpleDateFormat sdf17 = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return sdf17.format(new Date());
    }

    public static String getCurrentDate() {
        return sdfymd2.format(new Date());
    }

    /**
     * 与当前时间比较 ，返回差值 秒数
     *
     * @param time
     * @return
     */
    public static int getSecondsCompared(String time) {
        if (null == time) {
            return 0;
        }

        Long timel = Long.parseLong(time) * 1000L;// 转化为long类型

        int seconds = (int) ((System.currentTimeMillis() - timel) / (1000));// 计算出
        // 分钟差值
        return seconds;
    }

    public static void main(String[] args) throws Exception {
        System.out.println(nowTimeToStamp());
    }

    public static String getCurrentTime14() {
        SimpleDateFormat sdf14 = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf14.format(new Date());
    }

    public static String getCurrentTimeTrade_no() {
        SimpleDateFormat sdf14 = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return sdf14.format(new Date());
    }

    public static String getCurrentRandom() {
        SimpleDateFormat sdf14 = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        long random = Long.parseLong(sdf14.format(new Date()).substring(11));
        return String.valueOf(random);
    }

    /**
     * 时间戳转化为时间
     *
     * @param time
     * @return
     */
    public static String timestampToTime(String time) {

        Long l = Long.parseLong(time) * 1000L;// 转化为long类型

        String date = dateFormat.format(new Date(l));

        return date;
    }

    /**
     * 日期格式字符串转换成时间戳
     * <p>
     * 字符串日期
     *
     * @param format 如：yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String date2TimeStamp(String date_str, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return String.valueOf(sdf.parse(date_str).getTime() / 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 时间字符串转换成时间戳
     * <p>
     * 字符串日期
     *
     * @return
     */
    public static Long timeToStamp(String date_str) {
        try {
            // 时间转日期全格式
            date_str = getCurrentYearMonthDay().concat(" ").concat(date_str).concat(":59");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.parse(date_str).getTime() / 1000;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 当前时间戳
     *
     * @return
     * @throws Exception
     * @author 杨坚
     * @Time 2017年2月22日
     * @version 1.0v
     */
    public static Long nowTimeToStamp() throws Exception {
        // 日期格式
        return System.currentTimeMillis() / 1000;
    }

    /**
     * 时间转化为时间戳
     *
     * @return
     */
    public static Long timeToTimestamp(String time) {

        Date date = null;
        try {
            date = dateFormat.parse(time);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException((new StringBuilder("string date value[")).append(date)
                    .append("] parsing using [").append(time).append("] parse error").toString());
        }
        return date.getTime();
    }

    /**
     * 显示日期格式
     *
     * @param date    日期
     * @param pattern 格式
     * @return
     */
    public static String formsat(Date date, String pattern) {
        return new SimpleDateFormat(pattern).format(date);
    }

    /**
     * @param expression
     * @param pattern
     * @return
     */
    public static Date pase(String expression, String pattern) {
        try {
            return new SimpleDateFormat(pattern).parse(expression);
        } catch (ParseException e) {
            throw new RuntimeException((new StringBuilder("string date value[")).append(expression)
                    .append("] parsing using [").append(pattern).append("] parse error").toString());
        }
    }

    /**
     * 判断是否是工作时间。是工作时间，返回true,不是工作时间返回false
     *
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static boolean isTelecomWorkTime(int one, String two, int three, String four, int five, String six,
                                            int seven, String eight) {
        /*
         * 判断当前时间是否为电信的工作时间 (1) 判断今天是否是工作日 (2) 判断当前时间段是否是工作时间
         */
        if (checkWorkDay() && checkWorkTime(one, two, three, four, five, six, seven, eight)) {
            return true;
        }
        return false;
    }

    public static String getCurrentYearMonthDay() {
        SimpleDateFormat sdfymd = new SimpleDateFormat("yyyy-MM-dd");
        return sdfymd.format(new Date());
    }

    /**
     * 判断今天是否是工作日， 是工作日返还true，否则返回false
     *
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static boolean checkWorkDay() {
        // 获取当前日期
        // String today = getCurrentYearMonthDay();

        // // 特殊假期
        // List<String> specialHolidayList = getSpecialHoliday();
        // if (specialHolidayList.contains(today)) {
        // return false;
        // }
        //
        // // 特殊工作日
        // List<String> specialWorkday = getSpecialWorkday();
        // if (specialWorkday.contains(today)) {
        // return true;
        // }

        // 其他日期周一至周五为工作日，其他时间为非工作日
        Integer week = getDayOfWeek(null);
        if (week != null && week < 6) {
            return true;
        }

        return false;
    }

    /**
     * 获取指定时间为星期几，如果指定时间不存在，使用当前时间。
     * <p>
     * 根据中国的星期来排序：1-7 分别表示周一至周日
     *
     * @param datestr
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static Integer getDayOfWeek(String datestr) {
        Integer week = null;
        try {
            Date date = null;

            // 指定时间不存在，使用当前时间
            if (StringUtils.isNotEmpty(datestr)) {
                date = sdfhuman.parse(datestr);
            } else {
                date = new Date();
            }

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            week = cal.get(Calendar.DAY_OF_WEEK) - 1;
            if (week == 0) {
                week = 7;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return week;
    }

    /**
     * 获取特殊工作日期列表：日期形式为 YYYY-MM-DD
     *
     * @return
     * @see [类、类#方法、类#成员]
     */
    private static List<String> getSpecialWorkday() {
        List<String> holidayList = new ArrayList<String>();
        holidayList.add("2015-01-04");
        holidayList.add("2015-02-15");
        holidayList.add("2015-02-28");
        holidayList.add("2015-10-10");
        holidayList.add("2015-09-06");
        holidayList.add("2015-09-26");
        holidayList.add("2015-09-27");

        return holidayList;
    }

    /**
     * 获取特殊假期日期列表：日期形式为 YYYY-MM-DD
     *
     * @return
     * @see [类、类#方法、类#成员]
     */
    private static List<String> getSpecialHoliday() {
        List<String> holidayList = new ArrayList<String>();
        holidayList.add("2014-12-30");
        holidayList.add("2014-12-31");
        holidayList.add("2015-01-01");
        holidayList.add("2015-01-02");
        holidayList.add("2015-01-03");
        holidayList.add("2015-02-18");
        holidayList.add("2015-02-19");
        holidayList.add("2015-02-20");
        holidayList.add("2015-02-21");
        holidayList.add("2015-02-22");
        holidayList.add("2015-02-23");
        holidayList.add("2015-02-24");
        holidayList.add("2015-04-04");
        holidayList.add("2015-04-05");
        holidayList.add("2015-04-06");
        holidayList.add("2015-05-01");
        holidayList.add("2015-05-02");
        holidayList.add("2015-05-03");
        holidayList.add("2015-06-20");
        holidayList.add("2015-06-21");
        holidayList.add("2015-06-22");
        holidayList.add("2015-10-01");
        holidayList.add("2015-10-02");
        holidayList.add("2015-10-03");
        holidayList.add("2015-10-04");
        holidayList.add("2015-10-05");
        holidayList.add("2015-10-06");
        holidayList.add("2015-10-07");

        return holidayList;
    }

    /**
     * 获得固定格式的系统当前时间的日期
     *
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static String getSystemCurrData() {
        SimpleDateFormat sdfhuman = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date date = new Date();
        return sdfhuman.format(date);
    }

    /**
     * 判断今天是否是工作时间， 是工作时间返还true，否则返回false
     *
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static boolean checkWorkTime(int one, String two, int three, String four, int five, String six, int seven,
                                        String eight) {
        String currentDate = getSystemCurrData();

        // 上午的工作时间
        Date startDateMorning = new Date();
        startDateMorning.setHours(one);
        startDateMorning.setMinutes(Integer.parseInt(two));
        String startDateMorningStr = sdfhuman.format(startDateMorning);

        Date endDateMorning = new Date();
        endDateMorning.setHours(three);
        endDateMorning.setMinutes(Integer.parseInt(four));
        String endDateMorningStr = sdfhuman.format(endDateMorning);

        // 下午的工作时间
        Date startDateAfternoon = new Date();
        startDateAfternoon.setHours(five);
        startDateAfternoon.setMinutes(Integer.parseInt(six));
        String startDateAfternoonStr = sdfhuman.format(startDateAfternoon);

        Date endDateAfternoon = new Date();
        endDateAfternoon.setHours(seven);
        endDateAfternoon.setMinutes(Integer.parseInt(eight));
        String endDateAfternoonStr = sdfhuman.format(endDateAfternoon);

        // 判断时间
        if (compareToData(currentDate, startDateMorningStr) && compareToData(endDateMorningStr, currentDate)
                || compareToData(currentDate, startDateAfternoonStr)
                && compareToData(endDateAfternoonStr, currentDate)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 如果data1大于data2，则返回true，否则返回false
     *
     * @param data1
     * @param data2
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static boolean compareToData(String data1, String data2) {
        try {
            SimpleDateFormat sdfhuman = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date a = sdfhuman.parse(data1);
            Date b = sdfhuman.parse(data2);
            if (a.getTime() - b.getTime() > 0) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取系统当前一天
     *
     * @return
     */
    public static String getNextretreatDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1); // 得到前一天
        Date date = calendar.getTime();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(date);
    }

    /**
     * 获取系统当前二天
     *
     * @return
     */
    public static String getNextretreatTwoDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -2); // 得到前二天
        Date date = calendar.getTime();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(date);
    }

    /**
     * 客服校验时间
     *
     * @param beginTime  08:32:44
     * @param finishTime 18:32:44
     * @return
     * @throws Exception
     * @author 杨坚
     * @Time 2016年12月29日
     * @version 1.0v
     */
    public static boolean checkCustomWorkTime(String beginTimeOne, String finishTimeOne, String beginTimeTwo,
                                              String finishTimeTwo) throws Exception {
        boolean flag = false;
        // 结束时间
        long endTime = Long.parseLong(DateUtils.date2TimeStamp(
                getCurrentYearMonthDay().concat(" ").concat(finishTimeOne).concat(":00"), "yyyy-MM-dd HH:mm:ss"));
        // 开始时间
        long startTime = Long.parseLong(DateUtils.date2TimeStamp(
                getCurrentYearMonthDay().concat(" ").concat(beginTimeOne).concat(":00"), "yyyy-MM-dd HH:mm:ss"));
        // 当前时间
        long nowTime = Long.parseLong(
                DateUtils.date2TimeStamp(DateUtils.getCurrentTimeHuman().concat(":00"), "yyyy-MM-dd HH:mm:ss"));
        System.out.println(endTime + "" + startTime + "" + nowTime);
        // 第一次校验
        if (nowTime >= startTime && nowTime <= endTime) {
            flag = true;
        }
        // 校验为false则标识有问题
        if (!flag) {
            endTime = Long.parseLong(DateUtils.date2TimeStamp(
                    getCurrentYearMonthDay().concat(" ").concat(finishTimeTwo).concat(":00"), "yyyy-MM-dd HH:mm:ss"));
            // 开始时间
            startTime = Long.parseLong(DateUtils.date2TimeStamp(
                    getCurrentYearMonthDay().concat(" ").concat(beginTimeTwo).concat(":00"), "yyyy-MM-dd HH:mm:ss"));
            if (nowTime >= startTime && nowTime <= endTime) {
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 获取系统后一天
     *
     * @return
     */
    public static String getNadvanceDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, +1); // 得到后一天
        Date date = calendar.getTime();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(date);
    }

}
