package com.sz.fts.impl.common;

import com.sz.fts.bean.xmf.XmfTxInfo;
import com.sz.fts.dao.xmf.XmfTxInfoMapper;
import com.sz.fts.impl.wxpay.qiyepay.QiyePay;
import com.sz.fts.redis.repository.RedisAction;
import com.sz.fts.service.common.CommonService;
import com.sz.fts.utils.DateUtils;
import com.sz.fts.utils.HttpUtil;
import net.sf.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author 征华兴
 * @date 下午 3:10  2018/7/17 0017
 * @Copyright 江苏鸿信系统集成有限公司 All rights reserved
 */
@Service
public class CommonServiceImpl implements CommonService {
    @Autowired
    private RedisAction redisAction;
    //    @Autowired
//    private OwnSMSMapper ownSMSMapper;
//    @Autowired
//    private SmsUtilService smsUtilService;
    private static Logger logger = LogManager.getLogger(CommonServiceImpl.class);
//    private static final String SMALL_CODE_KEY = "small_code_key";
//    private static final String USER_OPEN_ID = "user_open_id";
//    private static final long TIME_OUT = 60 * 60 * 2;

    //    @Override
//    public JSONObject sendCode(String json, HttpServletRequest request) {
//        try {
//            JSONObject in = JSONObject.fromObject(json);
//            String key1 = in.getString("key");
//            List<byte[]> bytes = redisAction.lrange(SMALL_CODE_KEY, 0, -1);
//            boolean result = this.getResult(key1, bytes);
//            if (!result) {
//                return out("4", "非法访问");
//            }
//            // 根据手机号查询短信验证码
//            OwnSMS smsCode = this.ownSMSMapper.findSMSByTelephone(in.getString("mobile"));
//            String key = SMALL_CODE_KEY + in.getString("key");
//            String msg = "您本次操作的验证码是：";
//            // 验证码
//            String code = com.sz.fts.utils.CommonUtil.createRandom(true, 6);
//            if (smsCode == null) {
//                OwnSMS ownSMS = new OwnSMS(in.getString("mobile"), code, DateUtils.getCurrentTimeHuman());
//                ownSMS.setExtend1("0");
//                // 添加信息
//                this.ownSMSMapper
//                        .insertSMSInfo(ownSMS);
//            } else {
//                smsCode.setSmsCreate(DateUtils.getCurrentTimeHuman());
//                smsCode.setSmsCode(code);
//                // 更新信息
//                this.ownSMSMapper.updateSMSByTelephone(smsCode);
//            }
//            return smsUtilService.sendSms(key, in.getString("mobile"), msg + code + "【苏州电信】", request);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return out("4", "活动火爆，请稍后再试");
//        }
//    }
//
//    @Override
//    public JSONObject getUserInfoByMobile(String param) {
//        JSONObject out = out("0", "成功");
//        try {
//            JSONObject input = JSONObject.fromObject(param);
//            String mobile = input.getString("mobile");
//            String sign = input.getString("sign");
//            String url = "http://221.228.43.76:6688/api-transfer/user/getinfo?ACCESS_NUMBER=" + mobile;
//            String res = HttpUtil.sendUrl(url);
//            JSONObject result = JSONObject.fromObject(res);
//            logger.info("=========0=======" + res);
//            JSONArray array = result.getJSONObject("returnValue").getJSONArray("activitys");
//            String activity_prod_id = "";
//            System.out.println("========1========" + array.size());
//            if (array != null && array.size() >= 1) {
//                for (int i = 0; i < array.size(); i++) {
//                    if (sign.equals(array.getJSONObject(i).getString("policy_id"))) {
//                        activity_prod_id = array.getJSONObject(i).getString("activity_prod_id");
//                        break;
//                    }
//                }
//            }
//            String url1 = "http://221.228.43.76:6688/api-transfer/user/updateactinfo?activity_prod_id=" + activity_prod_id;
//            String res1 = HttpUtil.sendUrl(url1);
//            logger.info("=======2=========" + url1);
//            JSONObject result1 = JSONObject.fromObject(res1);
//            logger.info("=======3=========" + result1);
//            if (result1.get("returnValue") != null && !result1.getString("returnValue").equals("null")) {
//                out.put("returnValue", result1.getBoolean("returnValue"));
//            } else {
//                out.put("returnValue", false);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return out("4", "请稍后再试。");
//        }
//        return out;
//    }
    @Autowired
    private XmfTxInfoMapper xmfTxInfoMapper;

//    @Override
//    public JSONObject payMoney(String wdrOpenid, String uuid, String useOpenid) {
//        JSONObject out = new JSONObject();
//        boolean openIdLock = false;
//        try {
//            String sendUrl = "http://app1.118114sz.com/szfts/rollCharge/withdraw.do?userId=" + uuid;
//            String result1 = HttpUtil.sendUrl(sendUrl);
//            JSONObject jsonRes = JSONObject.fromObject(result1);
//            String yuan = jsonRes.getString("withdrawMoney");
//            out.put("flag", "4");
//            // 用户openid
//            String selectOpenid = jsonRes.getString("openId");
//            out.put("openid", selectOpenid);
//            // 判断如果不是同一用户，返回
//            if (!selectOpenid.equals(useOpenid)) {
//                out.put("flag", "2");
//                return out;
//            }
//            System.out.println("========提现金额========" + yuan + "=====查询=======" + selectOpenid);
//            if (Double.parseDouble(yuan) <= 0) {
//                return out;
//            }
//            BigDecimal v1 = new BigDecimal(yuan);
//            BigDecimal v2 = new BigDecimal("100");
//            int fee = v1.multiply(v2).intValue();
//            openIdLock = redisAction.setUserLock("tixian" + wdrOpenid);
//            if (!openIdLock) {
//                out.put("flag", "5");
//                return out;
//            }
//
//            synchronized (uuid) {
//                String time = DateUtils.dateFormat.format(new Date());
//                String value = redisAction.getString(wdrOpenid);
//                if (value != null) {
//                    out.put("flag", "5");
//                    return out;
//                }
//                JSONObject result = QiyePay.qiYePayTwo(wdrOpenid, fee + "");
//                // 12小时只能提现一次
//                redisAction.setString(wdrOpenid, time, 12 * 3600);
//                String code = result.getString("code");
//                String tradeNo = result.getString("tradeNo");
//                XmfTxInfo txInfo = new XmfTxInfo();
//                txInfo.setLlbOpenId(wdrOpenid);
//                txInfo.setOpenId(selectOpenid);
//                txInfo.setTxMoney(yuan + "");
//                txInfo.setCreateTime(time);
//                txInfo.setTxMonth(getCurrentDay());
//                txInfo.setExtend2("100");  // 翻滚吧话费
//                txInfo.setPaymentNo(tradeNo);
//                if ("0".equals(code)) {
//                    txInfo.setExtend1("成功");
//                    out.put("flag", "0");
//                } else if ("2".equals(code)) {
//                    txInfo.setExtend1("系统异常");
//                    out.put("flag", "0");
//                } else if ("1".equals(code)) {
//                    txInfo.setExtend1("失败");
//                    txInfo.setExtend3(result.getString("errCode"));
//                    // 提现失败，不扣款
//                    out.put("flag", "4");
//                }
//                this.xmfTxInfoMapper.insertSelective(txInfo);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            out.put("flag", "3");
//            return out;
//        } finally {
//            if (openIdLock) {
//                try {
//                    redisAction.delUserLock("tixian" + wdrOpenid);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                logger.info("*******锁删除****tixian****" + wdrOpenid);
//            }
//        }
//        return out;
//    }

    @Override
    public JSONObject payMoney(JSONObject json) {
        JSONObject out = new JSONObject();
        boolean openIdLock = false;
        String wdrOpenid = "";
        String urlResult = "";
        String uuid = "";
        try {
            String useOpenid = json.getString("openid");
            wdrOpenid = redisAction.getString("huafei" + useOpenid);
            uuid = json.getString("uuid");
            logger.info("============uuid=======" + uuid + ",===szdxopenid==" + useOpenid + ",============wdropenid=======" + wdrOpenid);
            if ("".equals(useOpenid) || "null".equals(wdrOpenid) || "".equals(wdrOpenid) || "null".equals(uuid)) {
                out.put("flag", "2");
                out.put("msg", "非法访问");
                return out;
            }
            openIdLock = redisAction.setUserLock("tixian" + wdrOpenid);
            if (!openIdLock) {
                out.put("flag", "5");
                out.put("msg", "请稍后再试");
                return out;
            }
            // 提现中
            String sendUrlProcess = "http://app1.118114sz.com/szfts/rollCharge/withdraw.do?userId=" + uuid + "&withdrawFlag=2";
            String result1 = HttpUtil.sendUrl(sendUrlProcess);
            JSONObject jsonRes = JSONObject.fromObject(result1);
            String yuan = jsonRes.getString("withdrawMoney");
            // 用户openid
            String selectOpenid = jsonRes.getString("openId");
            // 提现失败
            urlResult = "http://app1.118114sz.com/szfts/rollCharge/withdraw.do?userId=" + uuid + "&withdrawFlag=0";
            // 判断如果不是同一用户，返回
            if (!selectOpenid.equals(useOpenid)) {
                HttpUtil.sendUrl(urlResult);
                out.put("flag", "2");
                out.put("msg", "非法访问");
                return out;
            }
            System.out.println("========提现金额========" + yuan + "=====查询=======" + selectOpenid);
            if (Double.parseDouble(yuan) <= 0) {
                HttpUtil.sendUrl(urlResult);
                out.put("flag", "4");
                out.put("msg", "金额不足");
                return out;
            }
            BigDecimal v1 = new BigDecimal(yuan);
            BigDecimal v2 = new BigDecimal("100");
            int fee = v1.multiply(v2).intValue();
            synchronized (uuid) {
                String time = DateUtils.dateFormat.format(new Date());
                String value = redisAction.getString(wdrOpenid);
                if (value != null) {
                    HttpUtil.sendUrl(urlResult);
                    out.put("flag", "5");
                    out.put("msg", "请稍后再试");
                    return out;
                }
                JSONObject result = QiyePay.qiYePayTwo(wdrOpenid, fee + "");
                // 12小时只能提现一次
                redisAction.setString(wdrOpenid, time, 12 * 3600);
                String code = result.getString("code");
                String tradeNo = result.getString("tradeNo");
                XmfTxInfo txInfo = new XmfTxInfo();
                txInfo.setLlbOpenId(wdrOpenid);
                txInfo.setOpenId(selectOpenid);
                txInfo.setTxMoney(yuan + "");
                txInfo.setCreateTime(time);
                txInfo.setTxMonth(getCurrentDay());
                txInfo.setExtend2("100");  // 翻滚吧话费
                txInfo.setPaymentNo(tradeNo);
                if ("0".equals(code)) {
                    txInfo.setExtend1("成功");
                    out.put("flag", "0");
                    out.put("msg", "成功");
                    urlResult = "http://app1.118114sz.com/szfts/rollCharge/withdraw.do?userId=" + uuid + "&withdrawFlag=1";
                } else if ("2".equals(code)) {
                    txInfo.setExtend1("系统异常");
                    out.put("flag", "0");
                    out.put("msg", "成功");
                    urlResult = "http://app1.118114sz.com/szfts/rollCharge/withdraw.do?userId=" + uuid + "&withdrawFlag=1";
                } else if ("1".equals(code)) {
                    urlResult = "http://app1.118114sz.com/szfts/rollCharge/withdraw.do?userId=" + uuid + "&withdrawFlag=0";
                    txInfo.setExtend1("失败");
                    txInfo.setExtend3(result.getString("errCode"));
                    // 提现失败，不扣款
                    out.put("flag", "4");
                    out.put("msg", result.getString("errCode"));
                }
                HttpUtil.sendUrl(urlResult);
                this.xmfTxInfoMapper.insertSelective(txInfo);
            }

        } catch (Exception e) {
            e.printStackTrace();
            urlResult = "http://app1.118114sz.com/szfts/rollCharge/withdraw.do?userId=" + uuid + "&withdrawFlag=0";
            HttpUtil.sendUrl(urlResult);
            out.put("flag", "4");
            out.put("msg", "活动火爆，稍后再试");
            return out;
        } finally {
            if (openIdLock) {
                try {
                    redisAction.delUserLock("tixian" + wdrOpenid);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return out;
    }


    private boolean getResult(String key, List<byte[]> bytes) throws UnsupportedEncodingException {
        for (byte[] by : bytes) {
            String value = new String(by, "utf-8");
            if (key.equals(value)) {
                return true;
            }
            continue;
        }
        return false;
    }

    private JSONObject out(String flag, String msg) {
        JSONObject out = new JSONObject();
        out.put("flag", flag);
        out.put("msg", msg);
        return out;
    }

    private String getCurrentDay() {
        return DateUtils.sdfcn.format(new Date()).substring(0, 11);
    }
}
