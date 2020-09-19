package com.sz.fts.impl.engineer;

import com.sz.fts.bean.engineer.ZsEngineerManager;
import com.sz.fts.bean.engineer.ZsEngineerOrder;
import com.sz.fts.bean.xmf.KdyyLog;
import com.sz.fts.bean.xmf.XmfResult;
import com.sz.fts.bean.xmf.XmfTcInfo;
import com.sz.fts.bean.xmf.XmfTxInfo;
import com.sz.fts.dao.engineer.ZsEngineerManagerMapper;
import com.sz.fts.dao.engineer.ZsEngineerOrderMapper;
import com.sz.fts.dao.xmf.KdyyLogMapper;
import com.sz.fts.dao.xmf.XmfTcInfoMapper;
import com.sz.fts.dao.xmf.XmfTxInfoMapper;
import com.sz.fts.impl.wxpay.qiyepay.QiyePay;
import com.sz.fts.redis.repository.RedisAction;
import com.sz.fts.service.engineer.ZsEngineerService;
import com.sz.fts.utils.CommonUtil;
import com.sz.fts.utils.DateUtils;
import com.sz.fts.utils.HttpUtil;
import com.sz.fts.utils.sms.MD5;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 征华兴
 * @date 下午 1:58  2018/11/15 0015
 * @Copyright 江苏鸿信系统集成有限公司 All rights reserved
 */
@Service
public class ZsEngineerServiceImpl implements ZsEngineerService {

    @Autowired
    private RedisAction redisAction;
    @Autowired
    private ZsEngineerManagerMapper zsEngineerManagerMapper;

    @Autowired
    private XmfTcInfoMapper xmfTcInfoMapper;
    @Autowired
    private KdyyLogMapper kdyyLogMapper;
    @Autowired
    private ZsEngineerOrderMapper zsEngineerOrderMapper;
    @Autowired
    private XmfTxInfoMapper xmfTxInfoMapper;
    private static final Logger logger = LogManager.getLogger(ZsEngineerServiceImpl.class);

    @Override
    public JSONObject getUserInfo(String json) {
        JSONObject out = this.out("0", "成功");
        String openId = "";
        try {
            JSONObject in = JSONObject.fromObject(json);
            // 获取用户 openid ,用户渠道
            openId = in.getString("openId");
            int source = in.getInt("source");
            // 通过openid  获取用户 头像，昵称
            String url1 = "http://192.168.5.6:39090/itpi/thirdPart/phoneQuery?openid=" + openId;
            String res1 = HttpUtil.postUrl(url1, "wechat");
            JSONObject result1 = JSONObject.fromObject(res1);
            logger.info("***工程师openid获得***" + result1);
            //  跳转绑定页面
            if (result1.getString("phone") == null || "".equals(result1.getString("phone"))) {
                long time = System.currentTimeMillis();
                String sign = MD5.GetMD5Code("10000003qAz4#6RfV1=3yHn8" + time);
                String url = "http://quanzi.118114sz.com/network/bindPhone?contact_id=10000003&openid="
                        + openId + "&sign=" + sign + "&timestamp="
                        + time + "&redirect_uri=http://app1.118114sz.com/active/excuBroad/pages/linker.html";
                out.put("flag", "2");
                out.put("msg", "未绑定号码");
                out.put("url", url);
                return out;
            }
            String mobile = result1.getString("phone");
            ZsEngineerManager zsEngineerManager = null;
            // 通过手机号码，工程师编号 查询工程师的信息
            zsEngineerManager = this.zsEngineerManagerMapper.selectManagerByMobileAndSource(mobile, source);
            if (zsEngineerManager == null) {
                zsEngineerManager = this.zsEngineerManagerMapper.selectManagerBySource(source);
                out.put("status", false);
            } else {
                // 工程师本人
                out.put("status", true);
                String userOpenId = zsEngineerManager.getOpenId();
                if (userOpenId == null || !openId.equals(userOpenId)) {
                    zsEngineerManager.setOpenId(openId);
                    this.zsEngineerManagerMapper.updateByPrimaryKeySelective(zsEngineerManager);
                }
            }
            if (zsEngineerManager == null) {
                // 非法进入
                out.put("flag", "3");
                out.put("msg", "对不起，您没有权限进入");
                return out;
            }
            // 获取用户 提现金额，待结算金额
            int jiesuanMoney = zsEngineerManager.getJiesuanMoney();
            // 提现金额
            int txMoney = zsEngineerManager.getTxMoney();
            out.put("userName", zsEngineerManager.getUserName());
            out.put("mobile", zsEngineerManager.getMobile());
            out.put("starLevel", zsEngineerManager.getStarLevel());
            out.put("jiesuanMoney", jiesuanMoney);
            out.put("txMoney", txMoney);
            out.put("txInfo", zsEngineerManager.getTxInfo());
            //TODO 接单数
            out.put("orderCount", zsEngineerManager.getExtend1());
            redisAction.incr("zsengineerclickcount");
        } catch (Exception e) {
            e.printStackTrace();
            return out("4", "活动火爆，请稍后再试");
        }
        return out;
    }

    @Override
    public JSONObject queryTaocanList(String s) {
        JSONObject out = this.out("0", "成功");
        try {
            JSONObject in = JSONObject.fromObject(s);
            String area = "10";
            XmfTcInfo tc = new XmfTcInfo();
            if (in.get("area") != null && !"".equals(in.getString("area"))) {
                // 10 代表公共工程师
                area = in.getString("area");
            }
            tc.setBigenPage(0);
            tc.setEndPage(50);
            tc.setArea(area);
            List<XmfTcInfo> lists = this.xmfTcInfoMapper.selectList(tc);
            out.put("data", lists);
        } catch (Exception e) {
            e.printStackTrace();
            return out("4", "活动火爆，请稍后再试！");
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
            isLock = redisAction.setUserLock("engineer" + mobile);
            if (!isLock) {
                return out("4", "活动火爆，请稍后再试");
            }
            String fixArea = in.getString("fixArea"); // 区域
            KdyyLog log = new KdyyLog();
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
            ZsEngineerManager manager = this.zsEngineerManagerMapper.selectManagerBySource(in.getInt("source"));
            log.setExtend2(manager.getUserName());
            log.setExtend3(manager.getMobile());
            // 工程师source
            log.setExtend4(in.getInt("source") + "");
            // 下单数 加一条
            manager.setExtend1(Integer.parseInt(manager.getExtend1()) + 1 + "");
            log.setSource(Integer.parseInt(manager.getExtend4()));
            log.setStatus(1);  // 待处理
            log.setCreateTime(DateUtils.dateFormat.format(new Date()));
            kdyyLogMapper.insertSelective(log);
            zsEngineerManagerMapper.updateByPrimaryKeySelective(manager);
            // 不是指定区域
        } catch (Exception e) {
            e.printStackTrace();
            return out("4", "活动火爆，请稍后再试！");
        } finally {
            try {
                if (isLock) {
                    redisAction.delUserLock("engineer" + mobile);
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
            ZsEngineerOrder zsEngineerOrder = new ZsEngineerOrder();
            //  String openId = in.getString("openId");
            // 获取当前工程师的信息
            ZsEngineerManager manager = zsEngineerManagerMapper.selectManagerBySource(in.getInt("source"));
            // 宽带预约 渠道
            kdyyLog.setSource(Integer.parseInt(manager.getExtend4()));
            //  kdyyLog.setExtend1(openId);
            //  zsEngineerOrder.setOpenId(openId);
            kdyyLog.setExtend4(in.getInt("source") + "");
            zsEngineerOrder.setQudao(in.getInt("source"));
            // 订单渠道
            zsEngineerOrder.setExtend4(manager.getExtend4());
            // 订单状态 status 0 全部，1,代处理，2,成功，3,取消
            int status = in.getInt("status");
            List<KdyyLog> orders = new ArrayList<KdyyLog>();
            List<ZsEngineerOrder> xmfList = new ArrayList<ZsEngineerOrder>();
            if (status == 1) {
                //   待处理状态  1
                kdyyLog.setStatus(1);
                orders = this.kdyyLogMapper.selectList(kdyyLog);
                // 订单审核表 待审核  1
                zsEngineerOrder.setStatus(1);
                xmfList = this.zsEngineerOrderMapper.selectList(zsEngineerOrder);
            } else if (status == 0) {
                // 查看全部
                kdyyLog.setStatus(1);
                orders = this.kdyyLogMapper.selectList(kdyyLog);
                kdyyLog.setStatus(3);
                orders.addAll(this.kdyyLogMapper.selectList(kdyyLog));
                // xmf 审核表
                xmfList = this.zsEngineerOrderMapper.selectList(zsEngineerOrder);
            } else if (status == 2) {
                zsEngineerOrder.setStatus(2);
                xmfList = this.zsEngineerOrderMapper.selectList(zsEngineerOrder);
            }
            List<XmfResult> results = new ArrayList<XmfResult>();
            // 遍历宽带预约
            for (KdyyLog log : orders) {
                XmfResult xmfResult = new XmfResult();
                xmfResult.setUserName(log.getUserName());
                xmfResult.setCreateTime(log.getCreateTime());
                xmfResult.setTaocanName(log.getTaocanName());
                xmfResult.setYjMoney(log.getMsg());
                if (log.getStatus() == 1 || log.getStatus() == 2) {
                    xmfResult.setStatus("进行中");
                } else if (log.getStatus() == 3) {
                    xmfResult.setStatus("取消");
                }
                xmfResult.setBeiZhu("");
                results.add(xmfResult);
            }
            for (ZsEngineerOrder order : xmfList) {
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
            out.put("result", results);
            System.out.println("*********查询订单结果********" + results);
        } catch (Exception e) {
            e.printStackTrace();
            return out("4", "活动火爆，稍后再试");
        }
        return out;
    }

//    @Override
//    public JSONObject querySuccessOrderList(String param) {
//        JSONObject out = this.out("0", "成功");
//
//        JSONObject in = JSONObject.fromObject(param);
//        try {
//            ZsEngineerOrder zsEngineerOrder = new ZsEngineerOrder();
//            if (in.get("openId") == null || in.getString("openId").equals("")) {
//                return out("4", "参数有误");
//            }
//            zsEngineerOrder.setOpenId(in.getString("openId"));
//            zsEngineerOrder.setQudao(in.getInt("source"));
//            // 订单状态 status 0 全部，1,代处理，2,成功，3,取消
//            if (in.get("status") != null && in.getInt("status") > 0) {
//                zsEngineerOrder.setStatus(in.getInt("status"));
//            }
//            List<ZsEngineerOrder> orders = this.zsEngineerOrderMapper.selectList(zsEngineerOrder);
//            out.put("result", orders);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return out("4", "活动火爆，稍后再试");
//        }
//        return out;
//    }

    @Override
    public JSONObject payMoney(String json) throws Exception {
        JSONObject out = this.out("0", "成功");
        boolean openIdLock = false;
        String openId = "";
        try {
            JSONObject in = JSONObject.fromObject(json);
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
//            if ("15".equals(date) || "16".equals(date) || "17".equals(date)) {
//             通过用户openId,查看用户表中的数据
            ZsEngineerManager manager = this.zsEngineerManagerMapper.selectManagerByOpenId(openId);
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
                txInfo.setCreateTime(DateUtils.dateFormat.format(new Date()));
                txInfo.setTxMonth(getCurrentDay());
                txInfo.setExtend2(manager.getExtend4());
                txInfo.setPaymentNo(tradeNo);
                String nickName = manager.getUserName();
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
                this.zsEngineerManagerMapper.updateByPrimaryKey(manager);
                this.xmfTxInfoMapper.insertSelective(txInfo);
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

    /****************************************************管理后台************************************************************/
    @Override
    public JSONObject managerList(String s) throws Exception {
        JSONObject out = CommonUtil.printPlatform(0);
        try {
            JSONObject in = JSONObject.fromObject(s);
            ZsEngineerManager xyNewManager = new ZsEngineerManager();
            String source = in.getString("extend9").split("=")[1];
            // 渠道
            xyNewManager.setExtend4(source);
            xyNewManager.setBigenPage(in.getInt("bigenPage"));
            xyNewManager.setEndPage(in.getInt("endPage"));
            if (in.get("userName") != null && !"".equals(in.getString("userName"))) {
                xyNewManager.setUserName(in.getString("userName"));
            }
            if (in.get("openId") != null && !"".equals(in.getString("openId"))) {
                xyNewManager.setOpenId(in.getString("openId"));
            }
            if (in.get("mobile") != null && !"".equals(in.getString("mobile"))) {
                xyNewManager.setMobile(in.getString("mobile"));
            }
            if (in.get("startTime") != null && !"".equals(in.getString("startTime"))) {
                xyNewManager.setStartTime(in.getString("startTime"));
                xyNewManager.setEndTime(in.getString("endTime"));
            }
            List<ZsEngineerManager> list = zsEngineerManagerMapper.selectList(xyNewManager);
            out.put("items", list);
            out.put("totalCount", zsEngineerManagerMapper.selectCount(xyNewManager));
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
            ZsEngineerOrder xmfOrder = new ZsEngineerOrder();
            String source = in.getString("extend9").split("=")[1];
            xmfOrder.setExtend4(source);
            if (in.get("bigenPage") != null) {
                xmfOrder.setBigenPage(in.getInt("bigenPage"));
                xmfOrder.setEndPage(in.getInt("endPage"));
            }
            if (in.get("openId") != null && !"".equals(in.getString("openId"))) {
                xmfOrder.setOpenId(in.getString("openId"));
            }
            if (in.get("userMobile") != null && !"".equals(in.getString("userMobile"))) {
                xmfOrder.setUserMobile(in.getString("userMobile"));
            }
            if (in.get("status") != null && in.getInt("status") > 0) {
                xmfOrder.setStatus(in.getInt("status"));
            }
            if (in.get("startTime") != null && !"".equals(in.getString("startTime"))) {
                xmfOrder.setStartTime(in.getString("startTime"));
                xmfOrder.setEndTime(in.getString("endTime"));
            }
            // 获取到 yuyueOrder集合对象
            List<ZsEngineerOrder> orders = this.zsEngineerOrderMapper.selectList(xmfOrder);
            int count = zsEngineerOrderMapper.selectCount(xmfOrder);
            out.put("items", orders);
            out.put("totalCount", count);
            out.put("currentPage", in.getInt("currentPage"));
        } catch (Exception e) {
            e.printStackTrace();
            return CommonUtil.printPlatform(1);
        }
        return out;
    }

    @Override
    public JSONObject saveOrder(String s) throws Exception {
        JSONObject out = CommonUtil.printPlatform(0);
        boolean isLock = false;
        String openId = "";
        try {
            JSONObject in = JSONObject.fromObject(s);
            int orderId = in.getInt("orderId");
            ZsEngineerOrder order = this.zsEngineerOrderMapper.selectByPrimaryKey(orderId);
            if (order.getStatus() == 2) {
                out.put("result", "1");
                out.put("msg", "订单已审核成功，不可再次审核");
                return out;
            }
            openId = order.getOpenId();
            isLock = redisAction.setUserLock("xyxmf" + openId);
            if (!isLock) {
                out.put("result", "1");
                out.put("msg", "不可同时处理");
                return out;
            }
            // 订单状态  1 ：待处理  2 ： 成功  3： 取消
            order.setStatus(2);
            // 佣金 ，openid
            String yjMoney = order.getYjMoney();
            // 通过工程师编号 查询客户经理表中的数据
            ZsEngineerManager existManager = this.zsEngineerManagerMapper.selectManagerBySource(order.getQudao());
            int txMoney = existManager.getTxMoney();
            int jiesuanMoney = existManager.getJiesuanMoney();
            existManager.setTxMoney(txMoney + Integer.parseInt(yjMoney));
            existManager.setJiesuanMoney(jiesuanMoney - Integer.parseInt(yjMoney));
            // 审核成功
            this.zsEngineerOrderMapper.updateByPrimaryKeySelective(order);
            this.zsEngineerManagerMapper.updateByPrimaryKey(existManager);
            return out;
        } finally {
            if (isLock) {
                redisAction.delUserLock(openId);
            }
        }
    }

    @Override
    public JSONObject updateOrder(String s) throws Exception {
        JSONObject out = CommonUtil.printPlatform(0);
        try {
            JSONObject in = JSONObject.fromObject(s);
            ZsEngineerOrder yyOrderLog = new ZsEngineerOrder();
            yyOrderLog.setOrderId(in.getInt("orderId"));
            // 管理员备注
            yyOrderLog.setUserName(in.getString("userName"));
            yyOrderLog.setExtend2(in.getString("extend2"));
            this.zsEngineerOrderMapper.updateByPrimaryKeySelective(yyOrderLog);
        } catch (Exception e) {
            e.printStackTrace();
            return CommonUtil.printPlatform(1);
        }
        return out;
    }

    @Override
    public JSONObject delOrder(String s) throws Exception {
        JSONObject out = CommonUtil.printPlatform(0);
        try {
            JSONObject in = JSONObject.fromObject(s);
            //ZsEngineerOrder xmfOrder = new ZsEngineerOrder();
            // 订单状态  1 ：待处理  2 ： 成功  3： 取消
            //   xmfOrder.setOrderId(in.getInt("orderId"));
            //   xmfOrder.setStatus(3);
            // 通过订单id 查询到原订单
            ZsEngineerOrder order = this.zsEngineerOrderMapper.selectByPrimaryKey(in.getInt("orderId"));
            if (order.getStatus() != 1) {
                out.put("result", "1");
                out.put("msg", "已处理订单不可再次处理");
                return out;
            }
            order.setStatus(3);
            this.zsEngineerOrderMapper.updateByPrimaryKeySelective(order);
        } catch (Exception e) {
            e.printStackTrace();
            return CommonUtil.printPlatform(1);
        }
        return out;
    }

    @Override
    public JSONObject addManager(String s) throws Exception {
        JSONObject out = CommonUtil.printPlatform(0);
        try {
            JSONObject in = JSONObject.fromObject(s);
            ZsEngineerManager zsEngineerManager = new ZsEngineerManager();
            zsEngineerManager.setMobile(in.getString("mobile"));
            String extend4 = in.getString("extend9").split("=")[1];
            // 通过工程师编号查看是否存在
            ZsEngineerManager manager = zsEngineerManagerMapper.selectManagerBySource(in.getInt("qudao"));
            if (manager != null) {
                out.put("result", "1");
                out.put("msg", "该区域已有工程师存在");
                return out;
            }
            // 通过手机号码查看是否存在
            ZsEngineerManager existXyXmf = this.zsEngineerManagerMapper.selectManagerByMobile(in.getString("mobile"));
            if (existXyXmf != null) {
                out.put("result", "1");
                out.put("msg", "该号码已有工程师使用");
                return out;
            }
            zsEngineerManager.setUserName(in.getString("userName"));
            // 用户星级
            zsEngineerManager.setStarLevel(in.getString("starLevel"));
            // 接单数
            zsEngineerManager.setExtend1(in.getString("extend1"));
            zsEngineerManager.setCreateTime(DateUtils.dateFormat.format(new Date()));
            if (in.get("txInfo") != null && !in.getString("txInfo").equals("")) {
                zsEngineerManager.setTxInfo(in.getString("txInfo"));
            }
            // 非默认
            zsEngineerManager.setQudao(in.getInt("qudao"));
            // 渠道
            zsEngineerManager.setExtend4(extend4);
            this.zsEngineerManagerMapper.insertSelective(zsEngineerManager);
        } catch (Exception e) {
            e.printStackTrace();
            return CommonUtil.printPlatform(1);
        }
        return out;
    }

    @Override
    public JSONObject updateManager(String s) throws Exception {
        JSONObject out = CommonUtil.printPlatform(0);
        try {
            JSONObject jsonObject = JSONObject.fromObject(s);
            ZsEngineerManager tcInfo = new ZsEngineerManager();
            if (jsonObject.get("userName") != null && !"".equals(jsonObject.getString("userName"))) {
                tcInfo.setUserName(jsonObject.getString("userName"));
            }
            if (jsonObject.get("mobile") != null && !"".equals(jsonObject.getString("mobile"))) {
                tcInfo.setMobile(jsonObject.getString("mobile"));
            }
            if (jsonObject.get("starLevel") != null && !"".equals(jsonObject.getString("starLevel"))) {
                tcInfo.setStarLevel(jsonObject.getString("starLevel"));
            }
            if (jsonObject.get("txInfo") != null && !"".equals(jsonObject.getString("txInfo"))) {
                tcInfo.setTxInfo(jsonObject.getString("txInfo"));
            }
            tcInfo.setExtend1(jsonObject.getString("extend1"));
            tcInfo.setManagerId(jsonObject.getInt("managerId"));
            this.zsEngineerManagerMapper.updateByPrimaryKeySelective(tcInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return CommonUtil.printPlatform(1);
        }
        return out;
    }

    @Override
    public JSONObject queryTxInfo(String s) {
        JSONObject out = this.out("0", "成功");
        try {
            JSONObject in = JSONObject.fromObject(s);
            XmfTxInfo info = new XmfTxInfo();
            String openId = in.getString("openId");
            ZsEngineerManager manager = this.zsEngineerManagerMapper.selectManagerByOpenId(openId);
            info.setOpenId(openId);
            info.setExtend1("成功");
            info.setExtend2(manager.getExtend4());
            List<XmfTxInfo> data = this.xmfTxInfoMapper.selectList(info);
            out.put("data", data);
            return out;
        } catch (Exception e) {
            e.printStackTrace();
            return out("4", "服务器忙");
        }
    }

    @Override
    public JSONObject downloadOrder(String extend9, HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject out = CommonUtil.printPlatform(0);
        try {
            ZsEngineerOrder xmfOrder = new ZsEngineerOrder();
            xmfOrder.setBigenPage(0);
            xmfOrder.setEndPage(9999999);
            xmfOrder.setExtend4(extend9);
            // 获取到 yuyueOrder集合对象
            List<ZsEngineerOrder> orders = this.zsEngineerOrderMapper.selectList(xmfOrder);
            this.writeExcelOrder(request, response, "审核记录", new String[]{"用户姓名", "用户号码", "套餐名称", "佣金",
                    "工程师编号", "备注", "下单时间", "状态"}, orders);
        } catch (Exception e) {
            e.printStackTrace();
            return CommonUtil.printPlatform(1);
        }
        return out;
    }

    private void writeExcelOrder(HttpServletRequest request, HttpServletResponse response, String name, String[] title, List<ZsEngineerOrder> list) {

        // 创建一个EXCEL
        Workbook wb = new HSSFWorkbook();
        // 创建一个sheet
        Sheet sheet1 = wb.createSheet(name);
        int i = 0;
        //创建一行
        Row row = sheet1.createRow((short) 0);
        //填充标题
        for (String s : title) {
            Cell cell = row.createCell(i);
            cell.setCellValue(s);
            i++;
        }
        for (int j = 0; j < list.size(); j++) {
            Row row1 = sheet1.createRow(j + 1);
            row1.createCell(0).setCellValue(list.get(j).getUserName());
            row1.createCell(1).setCellValue(list.get(j).getUserMobile());
            row1.createCell(2).setCellValue(list.get(j).getTaocanName());
            row1.createCell(3).setCellValue(list.get(j).getYjMoney());
            row1.createCell(4).setCellValue(list.get(j).getQudao());
            row1.createCell(5).setCellValue(list.get(j).getExtend2());
            row1.createCell(6).setCellValue(list.get(j).getCreateTime());
            row1.createCell(7).setCellValue(list.get(j).getStatus() == 1 ? "待处理" : list.get(j).getStatus() == 2 ? "已审核" : "已取消");
        }
        try {
            if (wb != null) {
                response.setHeader("Content-disposition",
                        "attachment;filename=" + URLEncoder.encode("审核记录.xls", "UTF-8"));
                response.setContentType("application/msexcel;charset=UTF-8");
                OutputStream out = response.getOutputStream();
                wb.write(out);
                out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

//    @Override
//    public JSONObject updateManager(String s) {
//        return null;
//    }

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
