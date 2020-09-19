package com.sz.fts.impl.xyxmf;

import com.sz.fts.bean.own.OwnSMS;
import com.sz.fts.bean.xmf.XmfResult;
import com.sz.fts.bean.xmf.XmfTcInfo;
import com.sz.fts.bean.xmf.XmfTxInfo;
import com.sz.fts.bean.xyxmf.*;
import com.sz.fts.dao.own.OwnSMSMapper;
import com.sz.fts.dao.xmf.XmfTcInfoMapper;
import com.sz.fts.dao.xmf.XmfTxInfoMapper;
import com.sz.fts.dao.xyxmf.*;
import com.sz.fts.impl.wxpay.qiyepay.QiyePay;
import com.sz.fts.redis.repository.RedisAction;
import com.sz.fts.service.xyxmf.XyXmfService;
import com.sz.fts.utils.DateUtils;
import com.sz.fts.utils.HttpUtil;
import com.sz.fts.utils.sms.MD5;
import com.sz.fts.utils.sms.SmsUtilService;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 征华兴
 * @date 上午 11:13  2018/5/11 0011
 * @Copyright 江苏鸿信系统集成有限公司 All rights reserved
 */
@Service
public class XyXmfServiceImpl implements XyXmfService {
    private static final Logger logger = LogManager.getLogger(XyXmfServiceImpl.class);

    @Autowired
    private XyXmfManagerMapper xyXmfManagerMapper;

    @Autowired
    private XyXmfOrderMapper xyXmfOrderMapper;
    @Autowired
    private RedisAction redisAction;
    @Autowired
    private XyXmfAdminMapper xyXmfAdminMapper;
    @Autowired
    private XyXmfLogMapper xyXmfLogMapper;
    @Autowired
    private XyXmfWhiteMapper xyXmfWhiteMapper;
    @Autowired
    private XmfTcInfoMapper xmfTcInfoMapper;
    @Autowired
    private XmfTxInfoMapper xmfTxInfoMapper;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public JSONObject getUserInfo(String param) {
        JSONObject out = this.out("0", "成功");
        //    boolean openIdLock = false;
        String openId = "";
        try {
            JSONObject in = JSONObject.fromObject(param);
            // 判断是否有权限进入
            openId = in.getString("openId");
            // 通过openid  获取用户 头像，昵称
            String url1 = "http://192.168.5.6:39090/itpi/thirdPart/phoneQuery?openid=" + openId;
            String res1 = HttpUtil.postUrl(url1, "wechat");
            JSONObject result1 = JSONObject.fromObject(res1);
            System.out.println("******" + result1);
            //  跳转绑定页面
            if (result1.getString("phone") == null || "".equals(result1.getString("phone"))) {
                long time = System.currentTimeMillis();
                String sign = MD5.GetMD5Code("10000003qAz4#6RfV1=3yHn8" + time);
                String url = "http://quanzi.118114sz.com/network/bindPhone?contact_id=10000003&openid="
                        + openId + "&sign=" + sign + "&timestamp="
                        + time + "&redirect_uri=http://app1.118114sz.com/active/shareMfXiaoYuan/pages/linker.html";
                out.put("flag", "2");
                out.put("msg", "未绑定号码");
                out.put("url", url);
                return out;
            }
            String mobile = result1.getString("phone");
            XyXmfAdmin xyXmfAdmin = this.xyXmfAdminMapper.selectAdminByMobile(mobile);
            if (xyXmfAdmin == null) {
                out.put("flag", "3");
                out.put("msg", "对不起，您没有权限进入");
                return out;
            }
            // 获取用户的openid
            String userOpenId = xyXmfAdmin.getCreateTime();
            if (userOpenId == null || !openId.equals(userOpenId)) {
                xyXmfAdmin.setCreateTime(openId);
                this.xyXmfAdminMapper.updateByPrimaryKeySelective(xyXmfAdmin);
            }
            String url2 = "http://192.168.5.6:39090/itpi/thirdPart/wxuser?openId=" + openId;
            String res2 = HttpUtil.postUrl(url2, "wechat");
            JSONObject result2 = JSONObject.fromObject(res2);
            logger.info("***用户关注信息***" + result2);
            String nickName = "";
            if (result2.getInt("subscribe") == 1) {
                // 获取昵称
                nickName = result2.getString("nickname");
                String txInfo = result2.getString("headimgurl");
                out.put("nickName", nickName);
                out.put("txInfo", txInfo);
            }
            //TODO 汉宏那边做了强制关注
            // 获取用户 提现金额，待结算金额
            XyXmfManager existManager = this.xyXmfManagerMapper.selectManagerByOpenId(openId);
            int jiesuanMoney = 0;
            int txMoney = 0;
            if (existManager == null) {
                XyXmfManager manager = new XyXmfManager();
                // 用户不存在 新增
                manager.setOpenId(openId);
                manager.setJiesuanMoney(jiesuanMoney);
                manager.setTxMoney(txMoney);
                manager.setNickName(nickName);
                manager.setCreateTime(dateFormat.format(new Date()));
                // 新增一条信息
                this.xyXmfManagerMapper.insertSelective(manager);
            } else {
                jiesuanMoney = existManager.getJiesuanMoney();
                txMoney = existManager.getTxMoney();
                if (existManager.getNickName() == null) {
                    existManager.setNickName(nickName);
                    this.xyXmfManagerMapper.updateByPrimaryKeySelective(existManager);
                }
            }
            // 通过 openId,当前日期 查看任务信息
            out.put("jiesuanMoney", jiesuanMoney);
            out.put("txMoney", txMoney);
            redisAction.incr("xyxmfclickcount");
        } catch (Exception e) {
            e.printStackTrace();
            return out("4", "活动火爆，请稍后再试");
        }
        return out;
    }


    @Override
    public JSONObject querySuccessOrderList(String param) {
        JSONObject out = this.out("0", "成功");
        JSONObject in = JSONObject.fromObject(param);
        try {
            XyXmfOrder xmfOrder = new XyXmfOrder();
            if (StringUtils.isEmpty(in.getString("openId"))) {
                return out("4", "参数有误");
            }
            xmfOrder.setOpenId(in.getString("openId"));
            // 订单状态 status 0 全部，1,代处理，2,成功，3,取消
            if (in.get("status") != null && in.getInt("status") > 0) {
                xmfOrder.setStatus(in.getInt("status"));
            }
            List<XyXmfOrder> orders = this.xyXmfOrderMapper.selectList(xmfOrder);
            out.put("orders", orders);
        } catch (Exception e) {
            e.printStackTrace();
            return out("4", "活动火爆，稍后再试");
        }
        return out;
    }

    @Override
    public JSONObject payMoney(String param) throws Exception {
        JSONObject out = this.out("0", "成功");
        boolean openIdLock = false;
        String openId = "";
        try {
            JSONObject in = JSONObject.fromObject(param);
            if (StringUtils.isEmpty(in.getString("openId"))) {
                return out("4", "参数错误");
            }
            if (StringUtils.isEmpty(in.getString("llbOpenId"))) {
                return out("4", "参数错误");
            }
            openId = in.getString("openId");
            String llbOpenId = in.getString("llbOpenId");
            openIdLock = redisAction.setUserLock(openId);
            if (!openIdLock) {
                return out("4", "服务器忙，请稍后再试");
            }
//            // 判断是否是当月 15 日
            String date = DateUtils.getCurrentDate().substring(6);
            if ("15".equals(date) || "16".equals(date) || "17".equals(date)) {
//             通过用户openId,查看用户表中的数据
                XyXmfManager manager = this.xyXmfManagerMapper.selectManagerByOpenId(openId);
                if (manager == null) {
                    return out("4", "您不可提现");
                } else {
                    // 获取用户可结算 金额
                    int money = manager.getTxMoney();
                    if (money < 1) {
                        return out("4", "可提余额不足");
                    }
                    com.alibaba.fastjson.JSONObject result = QiyePay.qiYePay(llbOpenId, money * 100 + "");
                    String code = result.getString("code");
                    String tradeNo = result.getString("tradeNo");
                    XmfTxInfo txInfo = new XmfTxInfo();
                    txInfo.setLlbOpenId(llbOpenId);
                    txInfo.setOpenId(openId);
                    txInfo.setTxMoney(money + "");
                    txInfo.setCreateTime(dateFormat.format(new Date()));
                    txInfo.setTxMonth(getCurrentDay());
                    txInfo.setExtend2("2");
                    txInfo.setPaymentNo(tradeNo);
                    String nickName = manager.getNickName();
                    txInfo.setNickName(nickName);
                    manager.setTxMoney(0);
                    if ("0".equals(code)) {
                        txInfo.setExtend1("成功");
                        out.put("code", "0");
                    } else if ("2".equals(code)) {
                        txInfo.setExtend1("系统异常");
                        out.put("code", "0");
                    } else if ("1".equals(code)) {
                        txInfo.setExtend1("失败");
                        // 提现失败，不扣款
                        manager.setTxMoney(money);
                        out.put("code", "1");
                    }
                    this.xyXmfManagerMapper.updateByPrimaryKey(manager);
                    this.xmfTxInfoMapper.insertSelective(txInfo);
                }
            } else {
                return out("4", "只能在15,16,17号提现");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return out("4", "服务器忙,请稍后再试");
        } finally {
            if (openIdLock) {
                redisAction.delUserLock(openId);
                logger.info("*******锁删除********" + openId);
            }
        }
        return out;
    }


    /**
     * 查看提现记录
     *
     * @param param
     * @return
     */
    @Override
    public JSONObject queryTxInfo(String param) {
        JSONObject out = this.out("0", "成功");
        try {
            JSONObject in = JSONObject.fromObject(param);
            XmfTxInfo info = new XmfTxInfo();
            info.setExtend2("2");
            String openId = in.getString("openId");
            info.setOpenId(openId);
            info.setExtend1("成功");
            List<XmfTxInfo> data = this.xmfTxInfoMapper.selectList(info);
            out.put("data", data);
            return out;
        } catch (Exception e) {
            e.printStackTrace();
            return out("4", "服务器忙");
        }
    }

    @Override
    public JSONObject queryTaocanByMobile(String param) {
        JSONObject out = this.out("0", "成功");
        JSONObject in = JSONObject.fromObject(param);
        try {
//            Long endTime = Long.parseLong("201806301200");
//            Long current = Long.parseLong(DateUtils.getCurrentTime14().substring(0, 12));
//            if (current - endTime > 0) {
//                return out("4", "该活动已结束,请关注下期活动");
//            }
            String mobile = in.getString("mobile");
            String type = "";
            //TODO 通过手机号码查询白名单用户获得结果
            List<XyXmfWhite> lists = this.xyXmfWhiteMapper.selectByMobile(mobile);
            XmfTcInfo tcInfo = new XmfTcInfo();
            tcInfo.setArea("1");  // 校园小蜜蜂
            List<XmfTcInfo> infos = new ArrayList<XmfTcInfo>();
            int size = lists.size();
            if (size == 1) {
                out.put("type", "1");   // 白名单用户
                XyXmfWhite white = lists.get(0);
                type = white.getType();
                tcInfo.setExtend2(type);  //  嗨无限用户，或者是非嗨无限套餐信息
                infos = this.xmfTcInfoMapper.selectList(tcInfo);
            } else if (size == 2) {
                out.put("type", "1");
                infos = this.xmfTcInfoMapper.selectList(tcInfo);
            } else if (size == 0) {
                out.put("type", "0"); // 非白名单用户
                return out;
            }
            XyXmfLog log = new XyXmfLog();
            log.setMobile(mobile);
            List<XyXmfLog> xyXmfLogs = this.xyXmfLogMapper.selectList(log);
            if (xyXmfLogs.size() >= 1) {
                out.put("status", "1");
            } else {
                out.put("status", "0");
            }
            out.put("data", infos);
        } catch (Exception e) {
            return out("4", "活动火爆，稍后再试");
        }
        return out;
    }

    @Autowired
    private OwnSMSMapper ownSMSMapper;

    @Autowired
    private SmsUtilService smsUtilService;

    @Override
    public JSONObject sendCode(String param, HttpServletRequest request) {
        try {
            JSONObject in = JSONObject.fromObject(param);
            // 根据手机号查询短信验证码
            OwnSMS smsCode = this.ownSMSMapper.findSMSByTelephone(in.getString("userMobile"));
            String key = "xyxmf";
            String msg = "您正在登录苏州电信，验证码：";
//            // 验证码
            String code = com.sz.fts.utils.CommonUtil.createRandom(true, 6);
            if (smsCode == null) {
                // 添加信息
                this.ownSMSMapper
                        .insertSMSInfo(new OwnSMS(in.getString("userMobile"), code, DateUtils.getCurrentTimeHuman()));
            } else {
                smsCode.setSmsCreate(DateUtils.getCurrentTimeHuman());
                smsCode.setSmsCode(code);
                // 更新信息
                this.ownSMSMapper.updateSMSByTelephone(smsCode);
            }
            return smsUtilService.sendSms(key, in.getString("userMobile"), msg + code + "【苏州电信】", request);
        } catch (Exception e) {
            e.printStackTrace();
            return out("4", "活动火爆，请稍后再试");
        }

    }


    @Override
    public JSONObject queryOrderByMobile(String param) {
        JSONObject out = this.out("0", "成功");
        JSONObject in = JSONObject.fromObject(param);
        try {
            String mobile = in.getString("mobile");
            XyXmfLog log = new XyXmfLog();
            log.setMobile(mobile);
            List<XyXmfLog> infos = this.xyXmfLogMapper.selectList(log);
            List<XmfResult> results = new ArrayList<XmfResult>();
            if (infos.size() > 0) {
                for (XyXmfLog xyXmfLog : infos) {
                    XmfResult xmfResult = new XmfResult();
                    xmfResult.setUserName(xyXmfLog.getUserName());
                    xmfResult.setCreateTime(xyXmfLog.getCreateTime());
                    xmfResult.setTaocanName(xyXmfLog.getTaocanName());
                    if (xyXmfLog.getStatus() == 1) {
                        xmfResult.setStatus("进行中");
                    } else if (xyXmfLog.getStatus() == 3) {
                        xmfResult.setStatus("取消");
                    } else if (xyXmfLog.getStatus() == 2) {
                        xmfResult.setStatus("完成");
                    }
                    results.add(xmfResult);
                }
            }
            out.put("data", results);
        } catch (Exception e) {
            e.printStackTrace();
            return out("4", "活动火爆，请稍后再试");
        }
        return out;
    }

    @Override
    public JSONObject holdInfoByTaocan(String param) {
        JSONObject out = this.out("0", "成功");
        boolean isLock = false;
        String mobile = "";
        try {
            JSONObject in = JSONObject.fromObject(param);
            String personId = in.getString("personId");
            mobile = in.getString("mobile");
            String openId = in.getString("openId");
            if (!mobile.matches("1\\d{10}")) {
                return out("4", "手机号码格式不正确");
            }
            OwnSMS ownSMS = ownSMSMapper.findSMSByTelephone(mobile);
            if (ownSMS != null) {
                long start = Long.parseLong(DateUtils.sdf14.format(dateFormat.parse(ownSMS.getSmsCreate())).substring(0, 12));
                long end = Long.parseLong(DateUtils.getCurrentTime14().substring(0, 12));
                if (end - start > 5) {
                    return out("4", "验证码已过期");
                }
                if (!in.getString("code").equals(ownSMS.getSmsCode())) {
                    ownSMSMapper.deleteSMSByTelephone(mobile);
                    return out("4", "验证码错误，请重新获取并输入");
                } else {
                    ownSMSMapper.deleteSMSByTelephone(mobile);
                }
            } else {
                ownSMSMapper.deleteSMSByTelephone(mobile);
                return out("4", "验证码错误，请重新获取并输入");
            }

            // 相同手机号码用户 同时只能一个进入
            isLock = redisAction.setUserLock("xyxmf" + mobile);
            if (!isLock) {
                return out("4", "活动火爆，请稍后再试");
            }
            String fixArea = in.getString("fixArea");
            XyXmfLog log = new XyXmfLog();
            Integer taocanId = in.getInt("taocanId");
            XmfTcInfo tcInfo = this.xmfTcInfoMapper.selectByPrimaryKey(taocanId);
            log.setTaocanName(tcInfo.getTaocanName());
            log.setMsg(tcInfo.getYjMoney());
            log.setUserName(in.getString("userName"));
            log.setPersonId(personId);
            log.setMobile(mobile);
            log.setFixArea(fixArea);
            log.setAddress(in.getString("address"));
            log.setSource(in.getInt("source"));
            log.setExtend2(in.getString("xyName"));
            log.setExtend1(openId);
            XyXmfAdmin admin = this.xyXmfAdminMapper.selectAdminByOpenId(openId);
            if (admin != null) {
                log.setExtend3(admin.getExtend1());
                log.setExtend4(admin.getExtend2());
                log.setExtend5(admin.getExtend3());
                log.setExtend6(admin.getExtend4());
                log.setExtend7(admin.getMobile());
            }
            log.setStatus(1);
            log.setCreateTime(dateFormat.format(new Date()));
            xyXmfLogMapper.insert(log);
            // 不是指定区域
        } catch (Exception e) {
            e.printStackTrace();
            return out("4", "活动火爆，请稍后再试！");
        } finally {
            try {
                if (isLock) {
                    redisAction.delUserLock("xyxmf" + mobile);
                }
            } catch (Exception e) {
            }
        }
        return out;
    }

    @Override
    public JSONObject taocanInfo(String param) {
        JSONObject out = this.out("0", "成功");
        try {
            JSONObject input = JSONObject.fromObject(param);
            int taocanId = input.getInt("taocanId");
            XmfTcInfo tcInfo = this.xmfTcInfoMapper.selectByPrimaryKey(taocanId);
            out.put("data", tcInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return out("4", "活动火爆，请稍后再试！");
        }
        return out;
    }


    @Override
    public JSONObject getProcessOrder(String json) {
        JSONObject out = this.out("0", "成功");
        JSONObject in = JSONObject.fromObject(json);
        try {
            XyXmfLog xmXmfLog = new XyXmfLog();
            XyXmfOrder xmfOrder = new XyXmfOrder();
            String openId = in.getString("openId");
            xmXmfLog.setExtend1(openId);  // openId
            xmfOrder.setOpenId(openId);
            // 订单状态 status 0 全部，1,代处理，2,成功，3,取消 ,4 进行中
            int status = in.getInt("status");
            List<XyXmfLog> orders = new ArrayList<XyXmfLog>();
            List<XyXmfOrder> xmfList = new ArrayList<XyXmfOrder>();
            if (status == 1) {
                //   待处理状态  1，2
                xmXmfLog.setStatus(1);
                orders = this.xyXmfLogMapper.selectList(xmXmfLog);
                // 订单审核表 待审核  1
                xmfOrder.setStatus(1);
                xmfList = this.xyXmfOrderMapper.selectList(xmfOrder);
            } else if (status == 0) {
                // 查看全部
                xmXmfLog.setStatus(1);
                orders = this.xyXmfLogMapper.selectList(xmXmfLog);
                xmXmfLog.setStatus(3);
                orders.addAll(this.xyXmfLogMapper.selectList(xmXmfLog));
                // xmf 审核表
                xmfList = this.xyXmfOrderMapper.selectList(xmfOrder);
            }
            List<XmfResult> results = new ArrayList<XmfResult>();
            // 遍历宽带预约
            for (XyXmfLog log : orders) {
                XmfResult xmfResult = new XmfResult();
                xmfResult.setUserName(log.getUserName());
                xmfResult.setCreateTime(log.getCreateTime());
                xmfResult.setTaocanName(log.getTaocanName());
                xmfResult.setYjMoney(log.getMsg());
                xmfResult.setMobile(log.getMobile());
                if (log.getStatus() == 1) {
                    xmfResult.setStatus("进行中");
                } else if (log.getStatus() == 3) {
                    xmfResult.setStatus("取消");
                }
                xmfResult.setBeiZhu("");
                results.add(xmfResult);
            }
            for (XyXmfOrder order : xmfList) {
                XmfResult xmfResult = new XmfResult();
                xmfResult.setUserName(order.getUserName());
                xmfResult.setCreateTime(order.getCreateTime());
                xmfResult.setTaocanName(order.getTaocanName());
                xmfResult.setMobile(order.getUserMobile());
                if (order.getStatus() == 1) {
                    xmfResult.setStatus("审核中");
                } else if (order.getStatus() == 2) {
                    xmfResult.setStatus("已完成");
                } else if (order.getStatus() == 3) {
                    xmfResult.setStatus("审核未通过");
                }
                xmfResult.setYjMoney(order.getYjMoney());
                xmfResult.setBeiZhu(order.getExtend2());
                results.add(xmfResult);
            }
            out.put("data", results);
            System.out.println("*********查询订单结果********" + results);
        } catch (Exception e) {
            e.printStackTrace();
            return out("4", "活动火爆，稍后再试");
        }
        return out;
    }


    private String getCurrentDay() {
        return DateUtils.sdfcn.format(new Date()).substring(0, 11);
    }


    private JSONObject out(String flag, String msg) {
        JSONObject out = new JSONObject();
        out.put("flag", flag);
        out.put("msg", msg);
        return out;
    }
}
