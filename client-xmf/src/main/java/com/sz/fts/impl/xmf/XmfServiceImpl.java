package com.sz.fts.impl.xmf;


import com.sz.fts.bean.xmf.*;
import com.sz.fts.dao.xmf.*;
import com.sz.fts.impl.wxpay.qiyepay.QiyePay;
import com.sz.fts.redis.repository.RedisAction;
import com.sz.fts.service.xmf.XmfService;
import com.sz.fts.utils.CommonUtil;
import com.sz.fts.utils.DateUtils;
import com.sz.fts.utils.HttpUtil;
import net.sf.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 征华兴
 * @date 上午 11:19  2018/4/11 0011
 * @Copyright 江苏鸿信系统集成有限公司 All rights reserved
 */
@Service
public class XmfServiceImpl implements XmfService {

    private static final Logger logger = LogManager.getLogger(XmfServiceImpl.class);

    @Autowired
    private XmfManagerMapper xmfManagerMapper;

    @Autowired
    private XmfTaskMapper xmfTaskMapper;

    @Autowired
    private XmfTcInfoMapper xmfTcInfoMapper;
    @Autowired
    private XmfOrderMapper xmfOrderMapper;
    @Autowired
    private RedisAction redisAction;
    @Autowired
    private XmfTxInfoMapper xmfTxInfoMapper;
    @Autowired
    private KdyyLogMapper kdyyLogMapper;

    @Autowired
    private YYOrderKSMapper yyOrderKSMapper;


    @Override
    public JSONObject getUserInfo(String param) throws Exception {
        JSONObject out = this.out("0", "成功");
        boolean openIdLock = false;
        String openId = "";
        try {
            JSONObject in = JSONObject.fromObject(param);
            openId = in.getString("openId");
            // 通过openid  获取用户 头像，昵称
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
            //else {
            //TODO 汉宏那边做了强制关注
//            }
            // 获取用户 提现金额，待结算金额
            XmfManager existManager = this.xmfManagerMapper.selectManagerByOpenId(openId);
            String jiesuanMoney = "0";
            String txMoney = "0";
            int shareNum = 0;
            int sharePyq = 0;
            int shareOrder = 0;
            if (existManager == null) {
                XmfManager manager = new XmfManager();
                // 用户不存在 新增
                manager.setOpenId(openId);
                manager.setJiesuanMoney("0");
                manager.setTxMoney("0");
                manager.setNickName(nickName);
                manager.setCreateTime(DateUtils.dateFormat.format(new Date()));
                // 新增一条信息
                this.xmfManagerMapper.insertSelective(manager);
                out.put("jiesuanMoney", jiesuanMoney);
                out.put("txMoney", txMoney);
                out.put("shareNum", shareNum);
                out.put("sharePyq", sharePyq);
                out.put("shareOrder", shareOrder);
                return out;
            } else {
                jiesuanMoney = existManager.getJiesuanMoney(); // 可结算金额
                txMoney = existManager.getTxMoney();   // 提现金额
                if (existManager.getNickName() == null) {
                    existManager.setNickName(nickName);
                    this.xmfManagerMapper.updateByPrimaryKeySelective(existManager);
                }
            }
            // 通过 openId,当前日期 查看任务信息
            XmfTask task = this.xmfTaskMapper.selectTaskByOpenIdAndToday(openId, this.getToday());
            if (task != null) {
                shareNum = task.getShareNum();
                sharePyq = task.getSharePyq();
                shareOrder = task.getShareOrder();
                if (shareNum >= 3 && sharePyq >= 1 && shareOrder >= 1) {
                    XmfOrder order = new XmfOrder();
                    order.setOpenId(openId);
                    order.setStatus(2);  // 审核成功
                    order.setExtend3("1");  // 未转入
                    List<XmfOrder> lists = this.xmfOrderMapper.selectList(order);
                    int totalMoney = 0;   // 合计金额
                    if (lists.size() != 0) {
                        openIdLock = redisAction.setUserLock(openId);
                        if (!openIdLock) {
                            return out("4", "网络繁忙，请稍后再试");
                        }
                        for (XmfOrder xmfOrder : lists) {
                            String yjMoney = xmfOrder.getYjMoney();
                            if (yjMoney != null) {
                                // 可结算 money
                                int kjsMoney = Integer.parseInt(yjMoney);
                                totalMoney = kjsMoney + totalMoney;
                                xmfOrder.setExtend3("2");
                                this.xmfOrderMapper.updateByPrimaryKeySelective(xmfOrder);
                            }
                        }
                        // 完成任务的时候  将结算金额，转到提现金额中
                        int jsMoney = Integer.parseInt(jiesuanMoney);
                        int tixianMoney = Integer.parseInt(txMoney);
                        existManager.setTxMoney(totalMoney + tixianMoney + "");
                        existManager.setJiesuanMoney(jsMoney - totalMoney + "");
                        this.xmfManagerMapper.updateByPrimaryKeySelective(existManager);
                    }
                }
            }
            out.put("jiesuanMoney", jiesuanMoney);
            out.put("txMoney", txMoney);
            out.put("shareNum", shareNum);
            out.put("sharePyq", sharePyq);
            out.put("shareOrder", shareOrder);
        } catch (Exception e) {
            e.printStackTrace();
            return out("4", "活动火爆，请稍后再试");
        } finally {
            if (openIdLock)
                redisAction.delUserLock(openId);
        }
        return out;
    }


    @Override
    public JSONObject queryTaocanList(String json) {
        JSONObject out = this.out("0", "成功");
        try {
            JSONObject in = JSONObject.fromObject(json);
            String area = "0";
            XmfTcInfo tc = new XmfTcInfo();
            if (in.get("area") != null && !in.getString("area").equals("")) {
                // 0 代表公共小蜜蜂 ， 1 校园小蜜蜂
                area = in.getString("area");
            }

            tc.setBigenPage(0);
            tc.setEndPage(50);
            tc.setArea(area);
            // 1 宽带热门套餐
//            tc.setSource("1");   // 热门
//            tc.setExtend3("1");
            List<XmfTcInfo> remenKuandai = new ArrayList<XmfTcInfo>();  // 热门宽带
            List<XmfTcInfo> remenHaika = new ArrayList<XmfTcInfo>();
            List<XmfTcInfo> kuandaiList = new ArrayList<XmfTcInfo>();
            List<XmfTcInfo> haikaList = new ArrayList<XmfTcInfo>();
            List<XmfTcInfo> lists = this.xmfTcInfoMapper.selectList(tc);
            for (XmfTcInfo info : lists) {

                if ("1".equals(info.getExtend3())) {
                    // 宽带套餐
                    KdyyLog log = new KdyyLog();
                    log.setTaocanName(info.getTaocanName());
                    log.setSource(3);
                    int count = this.kdyyLogMapper.selectByTaocanName(log);
                    info.setCount(count);
                    if ("1".equals(info.getSource())) {
                        // 热门套餐
                        remenKuandai.add(info);
                    } else if ("0".equals(info.getSource())) {
                        // 普通套餐
                        kuandaiList.add(info);
                    }
                } else if ("2".equals(info.getExtend3())) {
                    // 嗨卡套餐
                    YYOrderKS order = new YYOrderKS();
                    order.setSource(13);
                    order.setTaocanName(info.getTaocanName());
                    int count = this.yyOrderKSMapper.selectCount(order);
                    order.setCount(count);
                    if ("1".equals(info.getSource())) {
                        remenHaika.add(info);
                    } else if ("0".equals(info.getSource())) {
                        haikaList.add(info);
                    }
                }
            }
            out.put("remenKuandai", remenKuandai);
            out.put("remenHaika", remenHaika);
            out.put("kuandai", kuandaiList);
            out.put("haika", haikaList);
            redisAction.incr("xmfclickcount");
        } catch (Exception e) {
            e.printStackTrace();
            return out("4", "活动火爆，请稍后再试！");
        }
        return out;
    }


    @Override
    public JSONObject shareInfo(String json) {
        JSONObject out = this.out("0", "成功");
        try {
            JSONObject in = JSONObject.fromObject(json);
            String openId = in.getString("openId");
            String type = "1";
            //  获取分享类型
            if (in.get("type") != null && !in.getString("type").equals("")) {
                type = in.getString("type");
            }
            // 查询当天，有无任务记录
            XmfTask existTask = this.xmfTaskMapper.selectTaskByOpenIdAndToday(openId, this.getToday());
            // 任务不存在
            if (existTask == null) {
                XmfTask xmfTask = new XmfTask();  // 新建一个任务信息
                xmfTask.setOpenId(openId);
                xmfTask.setCreateTime(this.getToday());
                if ("1".equals(type)) {
                    // 名片分享
                    xmfTask.setShareNum(1);
                } else if ("2".equals(type)) {
                    // 朋友圈分享
                    xmfTask.setSharePyq(1);
                }
                XmfManager manager = this.xmfManagerMapper.selectManagerByOpenId(openId);
                if (manager != null) {
                    xmfTask.setNickName(manager.getNickName());
                }
                this.xmfTaskMapper.insertSelective(xmfTask);
            } else {
                if ("1".equals(type)) {
                    // 名片分享
                    existTask.setShareNum(existTask.getShareNum() + 1);
                } else if ("2".equals(type)) {
                    // 朋友圈分享
                    existTask.setSharePyq(existTask.getSharePyq() + 1);
                }
                this.xmfTaskMapper.updateByPrimaryKey(existTask);
            }
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
            XmfOrder xmfOrder = new XmfOrder();
            if (in.get("openId") == null || in.getString("openId").equals("")) {
                return out("4", "参数有误");
            }
            xmfOrder.setOpenId(in.getString("openId"));
            // 订单状态 status 0 全部，1,代处理，2,成功，3,取消
            if (in.get("status") != null && in.getInt("status") > 0) {
                xmfOrder.setStatus(in.getInt("status"));
            }
            List<XmfOrder> orders = this.xmfOrderMapper.selectList(xmfOrder);
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
            if (in.get("openId") == null || in.getString("openId").equals("")) {
                return out("4", "参数错误");
            }
            if (in.get("llbOpenId") == null || in.getString("llbOpenId").equals("")) {
                return out("4", "参数错误");
            }
            openId = in.getString("openId");
            String llbOpenId = in.getString("llbOpenId");
            // 判断任务是否完成
            XmfTask existTask = this.xmfTaskMapper.selectTaskByOpenIdAndToday(openId, this.getToday());
            if (existTask == null) {
                return out("4", "请先完成任务");
            }
            openIdLock = redisAction.setUserLock(openId);
            if (!openIdLock) {
                return out("4", "服务器忙，请稍后再试");
            }
//             通过用户openId,查看用户表中的数据
            XmfManager manager = this.xmfManagerMapper.selectManagerByOpenId(openId);
            if (manager == null) {
                return out("4", "您不可提现");
            } else {
                //  int oldMoney = Integer.parseInt(manager.getTxMoney());
                // if (oldMoney >= money) {
                //  可提现
                //  int newMoney = oldMoney - money;
                //    manager.setTxMoney(newMoney + "");  // 最新的价格
                /*<=================================修改用户提现=====================================>*/
                // 获取用户可结算 金额
                String txMoney = manager.getTxMoney();
                if (txMoney == null) {
                    return out("4", "可提余额不足");
                }
                int money = Integer.parseInt(txMoney);
                if (money < 1) {
                    return out("4", "可提余额不足");
                }
                com.alibaba.fastjson.JSONObject result = QiyePay.qiYePay(llbOpenId, money * 100 + "");
                String code = result.getString("code");
                String tradeNo = result.getString("tradeNo");
                XmfTxInfo txInfo = new XmfTxInfo();
                txInfo.setLlbOpenId(llbOpenId);
                txInfo.setOpenId(openId);
                txInfo.setTxMoney(money + "");  // 提现金额
                txInfo.setCreateTime(DateUtils.dateFormat.format(new Date()));  // 提现日期
                txInfo.setTxMonth(getCurrentDay());   // 提现日期 2018年04月25日
                txInfo.setPaymentNo(tradeNo);   // 订单号
                String nickName = manager.getNickName();
                txInfo.setNickName(nickName);
                txInfo.setExtend2("1");
                manager.setTxMoney("0");
                if ("0".equals(code)) {
                    txInfo.setExtend1("成功");
                    out.put("code", "0");  // 成功
                } else if ("2".equals(code)) {
                    txInfo.setExtend1("系统异常");
                    out.put("code", "0");  // 成功
                } else if ("1".equals(code)) {
                    txInfo.setExtend1("失败");
                    manager.setTxMoney(txMoney);  // 提现失败，不扣款
                    out.put("code", "1");  // 失败
                }
                this.xmfManagerMapper.updateByPrimaryKey(manager);
                this.xmfTxInfoMapper.insertSelective(txInfo);
            }
//                else {
//                    return out("4", "可提余额不足");
//                }

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
            String openId = in.getString("openId");
            info.setOpenId(openId);
            info.setExtend1("成功");
            info.setExtend2("1"); // 公共小蜜蜂
            List<XmfTxInfo> data = this.xmfTxInfoMapper.selectList(info);
            out.put("data", data);
            return out;
        } catch (Exception e) {
            e.printStackTrace();
            return out("4", "服务器忙");
        }
    }

    @Override
    public JSONObject holdInfoByTaocan(String param) {
        JSONObject out = this.out("0", "成功");
        boolean isLock = false;
        String mobile = "";
        try {
            JSONObject in = JSONObject.fromObject(param);
            // 校验身份证号码
            String personId = in.getString("personId");
            String reg = "\\d{14}\\w|\\d{17}\\w";
            if (!personId.matches(reg)) {
                return out("4", "身份证格式不正确");
            }
            mobile = in.getString("mobile");
            if (!mobile.matches("1\\d{10}")) {
                return out("4", "手机号码格式不正确");
            }
            if (in.getString("address") == null || in.getString("address").equals("")) {
                return out("4", "请输入安装地址信息");
            }
            // 相同手机号码用户 同时只能一个进入
            isLock = redisAction.setUserLock("xmf" + mobile);
            if (!isLock) {
                return out("4", "活动火爆，请稍后再试");
            }
            String fixArea = in.getString("fixArea"); // 区域
            KdyyLog log = new KdyyLog();
            // 通过手机号码 非苏州电信用户 不调用翼支付接口
            /**********************************调用翼支付接口，请求发放优惠劵*****************************************/
//            String taocanName = in.getString("taocanName");
            Integer taocanId = in.getInt("taocanId");
            XmfTcInfo tcInfo = this.xmfTcInfoMapper.selectByPrimaryKey(taocanId);
            log.setTaocanName(tcInfo.getTaocanName()); // 套餐名称
            log.setMsg(tcInfo.getYjMoney());
            log.setUserName(in.getString("userName"));
            log.setPersonId(personId);
            log.setMobile(mobile);
            log.setFixArea(fixArea); // 安装区域
            log.setAddress(in.getString("address"));
            log.setDxMobile(in.getString("dxMobile"));
            if (in.get("openId") != null && !in.getString("openId").equals("")) {
                log.setExtend1(in.getString("openId"));
            } else {
                log.setExtend1("");
            }
//            if (in.get("source") != null) {
//                log.setSource(in.getInt("source"));   // 渠道
//            }
            log.setSource(3); // 公共小蜜蜂渠道
            log.setStatus(1);  // 待处理
            log.setCreateTime(DateUtils.dateFormat.format(new Date()));
            kdyyLogMapper.insertSelective(log);
            // 不是指定区域
        } catch (Exception e) {
            e.printStackTrace();
            return out("4", "活动火爆，请稍后再试！");
        } finally {
            try {
                if (isLock) {
                    redisAction.delUserLock("xmf" + mobile);
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
            KdyyLog kdyyLog = new KdyyLog();
            XmfOrder xmfOrder = new XmfOrder();
            YYOrderKS yyOrderKS = new YYOrderKS();
            String openId = in.getString("openId");
            yyOrderKS.setSource(13); // 嗨卡公共小蜜蜂
            // 渠道 3 公共小蜜蜂
            kdyyLog.setSource(3);
            yyOrderKS.setExtend3(openId);
            kdyyLog.setExtend1(openId);  // openId
            xmfOrder.setOpenId(openId);
            // 订单状态 status 0 全部，1,代处理，2,成功，3,取消 ,4 进行中
            int status = in.getInt("status");
            List<KdyyLog> orders = new ArrayList<KdyyLog>();
            List<XmfOrder> xmfList = new ArrayList<XmfOrder>();
            List<YYOrderKS> yyOrders = new ArrayList<YYOrderKS>();
            if (status == 1) {
                //  嗨卡  待处理状态  1
                yyOrderKS.setStatus(1);
                yyOrders = this.yyOrderKSMapper.selectList(yyOrderKS);
                //  宽带  待处理状态  1，2
                kdyyLog.setStatus(1);  // 待处理
                orders = this.kdyyLogMapper.selectList(kdyyLog);
                kdyyLog.setStatus(2);  // 成功的，也是属于进行中
                orders.addAll(this.kdyyLogMapper.selectList(kdyyLog));
                // 订单审核表 待审核  1
                xmfOrder.setStatus(1);   // 待审核
                xmfList = this.xmfOrderMapper.selectList(xmfOrder);
            } else if (status == 0) {
                // 查看全部
                yyOrderKS.setStatus(1);
                yyOrders = this.yyOrderKSMapper.selectList(yyOrderKS);  // 待处理
                yyOrderKS.setStatus(3);
                yyOrders.addAll(this.yyOrderKSMapper.selectList(yyOrderKS));
                kdyyLog.setStatus(1);  // 待处理
                orders = this.kdyyLogMapper.selectList(kdyyLog);
                kdyyLog.setStatus(2);  // 成功的，也是属于进行中
                orders.addAll(this.kdyyLogMapper.selectList(kdyyLog));
                kdyyLog.setStatus(3);  // 取消的
                orders.addAll(this.kdyyLogMapper.selectList(kdyyLog));
                // xmf 审核表
                xmfList = this.xmfOrderMapper.selectList(xmfOrder);
            }
            List<XmfResult> results = new ArrayList<XmfResult>();
            // 遍历宽带预约
            for (KdyyLog log : orders) {
                XmfResult xmfResult = new XmfResult();
                xmfResult.setUserName(log.getUserName());
                xmfResult.setCreateTime(log.getCreateTime());
                xmfResult.setTaocanName(log.getTaocanName());
                xmfResult.setYjMoney(log.getMsg());
//                xmfResult.setMobile(log.getMobile());
                if (log.getStatus() == 1 || log.getStatus() == 2) {
                    xmfResult.setStatus("进行中");
                } else if (log.getStatus() == 3) {
                    xmfResult.setStatus("取消");
                }
                xmfResult.setBeiZhu("");
                results.add(xmfResult);
            }
            //  遍历 嗨卡
            for (YYOrderKS ks : yyOrders) {
                XmfResult xmfResult = new XmfResult();
                xmfResult.setUserName(ks.getUserName());
                xmfResult.setCreateTime(ks.getCreateTime());
                xmfResult.setTaocanName(ks.getTaocanName());
                xmfResult.setYjMoney(ks.getExtend2());
//                xmfResult.setMobile(ks.getMobile());
                if (ks.getStatus() == 1) {
                    xmfResult.setStatus("进行中");
                } else if (ks.getStatus() == 3) {
                    xmfResult.setStatus("取消");
                }
                xmfResult.setBeiZhu("");
                results.add(xmfResult);
            }
            for (XmfOrder order : xmfList) {
                XmfResult xmfResult = new XmfResult();
                xmfResult.setUserName(order.getUserName());
                xmfResult.setCreateTime(order.getCreateTime());
                xmfResult.setTaocanName(order.getTaocanName());
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
            out.put("kdyy", results);
            System.out.println("*********查询订单结果********" + results);
        } catch (Exception e) {
            e.printStackTrace();
            return out("4", "活动火爆，稍后再试");
        }
        return out;
    }

    public static void main(String[] args) throws Exception {
//        String urlHk = "http://58.211.5.57:22011/szfts/kdyy/queryOrder.do";
//        JSONObject request = new JSONObject();
//        request.put("openId", "oFYchs94SbGKkkZ33T7XIR3a-Jsw");
//        request.put("status", "0");
//
//        String json2 = HttpUtil.postUrl(urlHk, request.toString());
        String json2 = DateUtils.sdfcn.format(new Date()).substring(0, 11);
        System.out.println(json2);
    }

    @Override
    public JSONObject taoCanList(String s) throws Exception {
        JSONObject out = CommonUtil.printPlatform(0);
        try {
            JSONObject in = JSONObject.fromObject(s);
            XmfTcInfo tcInfo = new XmfTcInfo();
            tcInfo.setBigenPage(in.getInt("bigenPage"));
            tcInfo.setEndPage(in.getInt("endPage"));
            if (in.get("taocanName") != null && !in.getString("taocanName").equals("")) {
                tcInfo.setTaocanName(in.getString("taocanName"));
            }
            // 渠道
            //  yuyueTaocan.setSource(in.getString("source").split("=")[1]);
            List<XmfTcInfo> list = xmfTcInfoMapper.selectList(tcInfo);
            out.put("items", list);
            out.put("totalCount", xmfTcInfoMapper.selectCount(tcInfo));
            out.put("currentPage", in.getInt("currentPage"));
        } catch (Exception e) {
            e.printStackTrace();
            return CommonUtil.printPlatform(1);
        }
        return out;
    }

    @Override
    public JSONObject updateTaoCan(String s) throws Exception {
        JSONObject out = CommonUtil.printPlatform(0);
        try {
            JSONObject jsonObject = JSONObject.fromObject(s);
            XmfTcInfo tcInfo = new XmfTcInfo();
            if (jsonObject.get("taocanName") != null && !"".equals(jsonObject.getString("taocanName"))) {
                tcInfo.setTaocanName(jsonObject.getString("taocanName"));
            }
            if (jsonObject.get("description") != null && !"".equals(jsonObject.getString("description"))) {
                tcInfo.setDescription(jsonObject.getString("description"));
            }
            if (jsonObject.get("tcUrl") != null && !"".equals(jsonObject.getString("tcUrl"))) {
                tcInfo.setTcUrl(jsonObject.getString("tcUrl"));
            }
            tcInfo.setTaocanId(jsonObject.getInt("taocanId"));
            this.xmfTcInfoMapper.updateByPrimaryKeySelective(tcInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return CommonUtil.printPlatform(1);
        }
        return out;
    }

    @Override
    public JSONObject managerList(String s) throws Exception {
        JSONObject out = CommonUtil.printPlatform(0);
        try {
            JSONObject in = JSONObject.fromObject(s);
            XmfManager manager = new XmfManager();
            manager.setBigenPage(in.getInt("bigenPage"));
            manager.setEndPage(in.getInt("endPage"));
            if (in.get("mobile") != null && !"".equals(in.getString("mobile"))) {
                manager.setMobile(in.getString("mobile"));
            }
            if (in.get("startTime") != null && !"".equals(in.getString("startTime"))) {
                manager.setStartTime(in.getString("startTime"));
                manager.setEndTime(in.getString("endTime"));
            }
            // 渠道
            //  yuyueTaocan.setSource(in.getString("source").split("=")[1]);
            List<XmfManager> list = xmfManagerMapper.selectList(manager);
            out.put("items", list);
            out.put("totalCount", xmfManagerMapper.selectCount(manager));
            out.put("currentPage", in.getInt("currentPage"));
        } catch (Exception e) {
            e.printStackTrace();
            return CommonUtil.printPlatform(1);
        }
        return out;
    }

    @Override
    public JSONObject orderList(String s) throws Exception {
        JSONObject out = CommonUtil.printPlatform(0);
        try {
            JSONObject in = JSONObject.fromObject(s);
            XmfOrder xmfOrder = new XmfOrder();
            //  int source = Integer.parseInt(in.getString("extend1").split("=")[1]);
            //   xmfOrder.setSource(source);
            if (in.get("bigenPage") != null) {
                xmfOrder.setBigenPage(in.getInt("bigenPage"));
                xmfOrder.setEndPage(in.getInt("endPage"));
            }
            if (in.get("openId") != null && !in.getString("openId").equals("")) {
                xmfOrder.setOpenId(in.getString("openId"));
            }
           /* if (in.get("mobile") != null && !in.getString("mobile").equals("")) {
                xmfOrder.setUserMobile(in.getString("mobile"));
            }*/
            if (in.get("status") != null && in.getInt("status") > 0) {
                xmfOrder.setStatus(in.getInt("status"));
            }
            if (in.get("startTime") != null && !"".equals(in.getString("startTime"))) {
                xmfOrder.setStartTime(in.getString("startTime"));
                xmfOrder.setEndTime(in.getString("endTime"));
            }
            // 获取到 yuyueOrder集合对象
            List<XmfOrder> orders = this.xmfOrderMapper.selectList(xmfOrder);
            int count = xmfOrderMapper.selectCount(xmfOrder);
            out.put("items", orders);
            out.put("totalCount", count);
            out.put("currentPage", in.getInt("currentPage"));
        } catch (Exception e) {
            e.printStackTrace();
            return com.sz.fts.utils.CommonUtil.printPlatform(1);
        }
        return out;
    }

    @Override
    public JSONObject successOrder(String s) throws Exception {
        JSONObject out = com.sz.fts.utils.CommonUtil.printPlatform(0);
        try {
            JSONObject in = JSONObject.fromObject(s);
            XmfOrder order = this.xmfOrderMapper.selectByPrimaryKey(in.getInt("orderId"));
            if (order.getStatus() != 1) {
                out.put("result", "1");
                out.put("msg", "已处理订单不可再次处理");
                return out;
            }
            // 订单状态  1 ：待处理  2 ： 成功  3： 取消
            order.setStatus(2);
            // 佣金 ，openid
            String yjMoney = order.getYjMoney();
            String openId = order.getOpenId();
            // 通过openid 查询客户经理表中的数据
            XmfManager existManager = this.xmfManagerMapper.selectManagerByOpenId(openId);
            // 更新 数据
            if (existManager == null) {
                // 如果不存在 改用户信息  新增
                XmfManager manager = new XmfManager();
                manager.setOpenId(openId);
                manager.setTxMoney(yjMoney);  // 审核成功将这个佣金，算到提现上
                manager.setCreateTime(DateUtils.dateFormat.format(new Date()));
                // 审核成功
                this.xmfOrderMapper.updateByPrimaryKeySelective(order);
                this.xmfManagerMapper.insertSelective(manager);
            } else {
                int jiesuneMoney = Integer.parseInt(existManager.getJiesuanMoney());
                int txMoney = Integer.parseInt(existManager.getTxMoney());
                existManager.setJiesuanMoney(jiesuneMoney - Integer.parseInt(yjMoney) + "");
                existManager.setTxMoney(txMoney + Integer.parseInt(yjMoney) + "");
                // 审核成功
                this.xmfOrderMapper.updateByPrimaryKeySelective(order);
                this.xmfManagerMapper.updateByPrimaryKey(existManager);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return com.sz.fts.utils.CommonUtil.printPlatform(1);
        }
        return out;
    }

    @Override
    public JSONObject delOrder(String s) throws Exception {
        JSONObject out = com.sz.fts.utils.CommonUtil.printPlatform(0);
        try {
            JSONObject in = JSONObject.fromObject(s);
            XmfOrder xmfOrder = new XmfOrder();
            // 订单状态  1 ：待处理  2 ： 成功  3： 取消
            xmfOrder.setStatus(3);
            // 通过订单id 查询到原订单
            XmfOrder order = this.xmfOrderMapper.selectByPrimaryKey(in.getInt("orderId"));
            if (order.getStatus() != 1) {
                out.put("result", "1");
                out.put("msg", "已处理订单不可再次处理");
                return out;
            }
            this.xmfOrderMapper.updateByPrimaryKeySelective(xmfOrder);
        } catch (Exception e) {
            e.printStackTrace();
            return com.sz.fts.utils.CommonUtil.printPlatform(1);
        }
        return out;
    }

    private String getCurrentDay() {
        return DateUtils.sdfcn.format(new Date()).substring(0, 11);
    }


    private String getToday() {
        return DateUtils.sdfymd.format(new Date());
    }

    private JSONObject out(String flag, String msg) {
        JSONObject out = new JSONObject();
        out.put("flag", flag);
        out.put("msg", msg);
        return out;
    }
}
