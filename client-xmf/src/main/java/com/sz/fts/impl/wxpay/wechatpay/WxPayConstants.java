package com.sz.fts.impl.wxpay.wechatpay;

/**
 * @author 耿怀志
 * @version [版本号, 2018/4/12]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class WxPayConstants {

    //第三方用户唯一ID
    public static final String APPID = "wx1575a1a993950056";
    //第三方用户唯一凭证密码
    public static final String APP_SECRET = "6304d7a4f4381fe67ad63fd41589ddd2";
    //商户ID
    public static final String MCH_ID = "1323521601";
    //微信商户平台-账户设置-安全设置-api安全,配置32位key
    public static final String KEY = "HX123456WEIXIN65432120160328WEI8";
    //交易类型
    public static final String TRADE_TYPE_JS = "JSAPI";
    //微信支付回调url
    public static final String NOTIFY_URL = "http://www.118114sz.com.cn/xmf/wxPay/reUrl.do";

}
