package com.sz.fts.impl.wxpay.utils;

/**
 * @author 征华兴
 * @date 下午 4:14  2018/12/24 0024
 * @Copyright 江苏鸿信系统集成有限公司 All rights reserved
 */

import java.io.UnsupportedEncodingException;


public class Test {
    public static void main(String args[]) throws UnsupportedEncodingException {
        // 加密生成支付平台支付字符uFFFD?
        UrlEncodeForPay encryptPay = new UrlEncodeForPay();
        String payParam = "征华兴" + "$Pay50";
        // +                        // 2  pay
//                        "$"+        // 3  时间戳
//                        "$1002" +                       // 4 SP
//                        "$100201" +                     // 5 业务代码
//                        "$10020101" +                   // 6  产品标识
//                        "$11" +                         //  7 订单类型  11 充值
//                        "$2" +                          // 参数8    产品类型 2
//                        "$0512" +                       // 参数9 号码归属地
//                        "$18962135505" +                // 10 号码
//                        "$冷亚萍" +                       //  11 联系人
//                        "$1352685947" +                  // 12 联系人电话
//                        "$" +                                 // 13 渠道
//                        "$" +                             //  14 工号
//                        "$01" +                             // 15 人民币
//                        "$100" +                          // 16 100分
//                        "$jsctwap" +                        // 17 订单描述
//                        "$" +                               // 18 应用系统用户ID
//                        "$http://www.qq.com" +  //19 后台返回地址
//                        "$" +                               //20  SP下的子SP身份标识
//                        "$" + DateUtils.getCurrentTime17() +             // 21 订单号
//                        "$http://www.baidu.com" + // 22 前台返回地址
//                        "$wxpay" +                         // 23 支付接口方式
//                        "$0210000002" +                         // 24 支付机构  0210000002  ，商户默认填死
//                        "$CMB" +                                  // 25 支付银行编码
//                        "$" +                                       // 26 分期期数
//                        "$" +                                        // 27 充值级别
//                        "$" +                        //28       用户类型
//                        "$001" +                       //29        购物车ID
//                        "$" +                           //30   翼支付帐号
//                        "$" +                       //31      分账明细
//                        "$58.211.5.59" +                       //32     客户端IP
//                        "$" +                       //33
//                        "$" +                       //34
//                        "$" +                       //35
//                        "$JSAPI" +                       //36  交易类型
//                        "$" + "aabbcc" +                       //37
//                        "$2$";
        System.out.println("原串:" + payParam);

        payParam = new String(payParam.getBytes("utf-8"), "iso8859-1");
        System.out.println("转码:" + payParam);
        String encyPayURL = encryptPay.enCodingForPay(payParam,
                "123456123456123456123456");
        System.out.println("加密:" + encyPayURL);
        // encyPayURL="3310F8E7EAB92A078B58AC5F583A680916B4D09232526A24ACC93992A8E1BA1CA17EA0EAA630C01E9D30703029D8A5355155C8D8CD5627B9091419FB543339C8C88E2243A1482AFFEB39E51E86D766F1E01DE9573B7290D5DD6174AA8EEBBB858949CBDDD02F3C9D45FB957E85B8B8ADAD72937F1B2EADDE9C7ADFBC6B511867E6C105502A101644EDA266C5AF4C369C0330AF026127A4618873446E41B52B15FF17A37026B7EDB73995AA07373D94C0C6801BFC3FB3AE1E101669394AE2EB3B3FC5068DC43EBCD90A11D5441D0BB30168F8B0A86BCF57FF9AB37B187AA6EB4350EE6126880869CEB2429F47EA3F5811BCA1DEA2B8D2AD60B95FBB734A71A0F7645734B02CBF86D99D071B7AAC0D2CFE72000C196535302E48674DF6BB2824B1EDF65CF64253A45E2ABCF3D0C44C863BFF3969724315DDDE6EC8FD974BBF83B38490FA8678D906B87929C226AC8180A19B22B485401653EF";
        String deResValue = encryptPay.deCodingForPay(encyPayURL,
                "123456123456123456123456", "utf-8");
        System.out.println("解密:" + deResValue);
        String mysrc = deResValue.substring(0, deResValue.lastIndexOf("$"));
        System.out.println(mysrc);  // 跟原串一样
//        mysrc = new String(mysrc.getBytes("utf-8"), "iso8859-1");
//        String mysign = encryptPay.enCodingForPay(mysrc,
//                "123456123456123456123456");
//        System.out.println("加密s:" + mysign);

        //ZDU5OTZkZGZlMWJiY2Y0YTA1Mzc4NzFjZmFkMDg1OWIyMzJiZWRkOQ==
        //YTYzYjY5ZDkzZjRhZmQyMGRjZDBmODI1Y2M2MzNmOTU0YzVjZWFmYQ==
        //String ddd = "3310F8E7EAB92A077F074FD80ECF4E5C98DE2366647934E7ACC93992A8E1BA1C87DE294690F363593B02D25C71E642655155C8D8CD5627B9091419FB543339C8C88E2243A1482AFFEB39E51E86D766F1CB7C2C118C0B34F9F809475F25867AE8686E6118D863100DCA5F81095CF5608ED4BCA31443090DD878FFDEAF4D31F20C56BE766D805ABE07B43072AD4D30DF228070F108D623268B4B2E0793956D5160EF3172297A8E19B8910E4A73B8F7713FE66F8831B9EB743687DE294690F3635903C997F694E9336432BE6E82476F8B1FC83D7F80FAF2545CBC35C99EB0096D3B31023E70A4623F22DC819650A1A9A7C20DBE479BEE3CCD44A829A441303EC1B306F67E805C142B0BBF5D49EB1D4F50F685387C11AAAE3DD1FCEA142F6879077DE0606D8154BFE07B04498A0AB401BDCCA4491A048F212BC305EEC67B36FE92D3CCB2840A456592CFB362D38E90E00A58534635D1B2A6C081";
//                     String ddd = "3310F8E7EAB92A077F074FD80ECF4E5C98DE2366647934E7ACC93992A8E1BA1C87DE294690F363593B02D25C71E642655155C8D8CD5627B9091419FB543339C8C88E2243A1482AFFEB39E51E86D766F1CB7C2C118C0B34F92E53F9470107A0200AF523F282270419B8F07A0BA934CEB0326E37F53D92F0F24DDA541CC367172E650A98E646E39B44D0198B0211D06B4AD7F0136A92F15AE808D7BC8FC22C26FFFA090CE82397FB506E3D900785C3BFE3C13EA3D0813746A056FBEE636774AAC25AD5433178A028CFC023C3D87125284E7510678A0533E4CADA7D94B817C7B4BB1C4C4B35C3BF9F438873446E41B52B15FF17A37026B7EDB7CBE0052EB00E8DBE64C0B61C1CC98954E7B843E7397191AEE161FC2AE95E1D6F30958DDC8AB221988816834DA82B22F820A51298AFEA39F7CD3ABB85B941C3215F83FD64ED3CD387AE75427996A6BF16465D191C41CAB76EC1BB91D00ACBE62378B1228A7B54F94C";
//                     String deResValue = encryptPay.deCodingForPay(ddd, "123456123456123456123456", "utf-8");
//        String mysrc = deResValue.substring(0, deResValue.lastIndexOf("$"));
//                     System.out.println(deResValue);
//                     ddd = mysrc;
//                     //ddd = "100220120410111801000005$Pay$20120410111801$1002$100201$10020101$11$2$025$13337800948$zžһ$13337800948$$$01$1$jsctwap$$http://132.228.140.123:9099/wap/bank/bank-pay!result.xhtml$$20120410111801474$http://202.102.111.146:9095/wap/bank/bank-pay!view.xhtml$bestwappay$0210000002$ALIPAY$$";
//                String string = new String(ddd.getBytes("utf-8"), "gbk");
//                System.out.println(string);
//                     String mymac = encryptPay.enCodingForPay(string,
//            "123456123456123456123456");
//                     System.out.println(mymac);
//        try {
//            MessageDigest md = null;
//            String strDes = null;
//            byte bt[] = string.getBytes();
//            try {
//                md = MessageDigest.getInstance("SHA-1");
//            } catch (NoSuchAlgorithmException nosuchalgorithmexception) {}
//            md.update(bt);
//            strDes = encryptPay.bytes2Hex(md.digest());
//            String digst = new String(Base64.encode(strDes.getBytes()));
//            System.out.println(digst);
//        } catch (Exception e) {
//        }
//        String gbk = "我来了";
//        String iso = new String(gbk.getBytes("utf-8"), "ISO-8859-1"); // iso-8859-1 编码
//        System.out.println(new String(gbk.getBytes("utf-8"), "utf-8"));
//        //模拟UTF-8编码的网站显示
//        System.out.println(new String(iso.getBytes("ISO-8859-1"), "utf-8"));
//
//        String utf8 = new String(gbk.getBytes("UTF-8"));
//        for (int i = 0; i < gbk.getBytes("UTF-8").length; i++) {
//            System.out.print(gbk.getBytes("UTF-8")[i]);
//        }
//
//        System.out.println("");
//
//        for (int i = 0; i < utf8.getBytes().length; i++) {
//            System.out.print(utf8.getBytes()[i]);
//        }
//
//        System.out.println("");
//
//        for (int i = 0; i < iso.getBytes("ISO-8859-1").length; i++) {
//            System.out.print(iso.getBytes("ISO-8859-1")[i]);
//        }

    }
}
