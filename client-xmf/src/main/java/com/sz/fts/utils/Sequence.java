package com.sz.fts.utils;

/**
 * 序列生成器
 * @author  陈鹏
 * @version  [版本号, 2013-7-2]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public abstract class Sequence
{
    /**
     * 0-999 循环
     */
    private static int num;

    public synchronized static String nextContentId()
    {
        return nextNum();
    }

    public synchronized static String nextProductId()
    {
        return nextNum();
    }

    public synchronized static String nextlinkId()
    {
        return nextNum();
    }

    public synchronized static String nextArticleId()
    {
        return nextNum();
    }

    public synchronized static String nextId()
    {
        return nextNum();
    }

    private static String nextNum()
    {
        num = num + 1;
        if (num >= 1000)
        {
            num = 0;
        }
        return DateUtils.getCurrentTime17() + formatNum(3, String.valueOf(num));
    }

    private static String formatNum(int len, String number)
    {
        String temp = number;
        while (temp.length() < len)
        {
            temp = '0' + temp;
        }

        return temp;
    }
}
