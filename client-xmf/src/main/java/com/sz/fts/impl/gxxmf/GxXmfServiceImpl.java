package com.sz.fts.impl.gxxmf;

import com.sz.fts.bean.gxxmf.XyLinkLog;
import com.sz.fts.bean.gxxmf.XyNewAdmin;
import com.sz.fts.bean.gxxmf.XyNewManager;
import com.sz.fts.bean.gxxmf.XyNewOrder;
import com.sz.fts.bean.xmf.XmfResult;
import com.sz.fts.bean.xmf.XmfTcInfo;
import com.sz.fts.bean.xmf.XmfTxInfo;
import com.sz.fts.dao.gxxmf.XyLinkLogMapper;
import com.sz.fts.dao.gxxmf.XyNewAdminMapper;
import com.sz.fts.dao.gxxmf.XyNewManagerMapper;
import com.sz.fts.dao.gxxmf.XyNewOrderMapper;
import com.sz.fts.dao.xmf.XmfTcInfoMapper;
import com.sz.fts.dao.xmf.XmfTxInfoMapper;
import com.sz.fts.impl.wxpay.qiyepay.QiyePay;
import com.sz.fts.redis.repository.RedisAction;
import com.sz.fts.service.gxxmf.GxXmfService;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * @author 征华兴
 * @date 下午 1:52  2018/6/20 0020
 * @Copyright 江苏鸿信系统集成有限公司 All rights reserved
 */
@Service
public class GxXmfServiceImpl implements GxXmfService {

    @Autowired
    private XyNewAdminMapper xyNewAdminMapper;
    @Autowired
    private XyNewManagerMapper xyNewManagerMapper;
    @Autowired
    private XyNewOrderMapper xyNewOrderMapper;
    @Autowired
    private RedisAction redisAction;
    @Autowired
    private XmfTcInfoMapper xmfTcInfoMapper;
    @Autowired
    private XmfTxInfoMapper xmfTxInfoMapper;


    private static Logger logger = LogManager.getLogger(GxXmfServiceImpl.class);


    @Override
    public JSONObject queryTxInfo(String param) {
        JSONObject out = this.out("0", "成功");
        try {
            JSONObject in = JSONObject.fromObject(param);
            XmfTxInfo info = new XmfTxInfo();
            info.setExtend2("4");
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

    private static final Pattern tele = Pattern.compile("^1\\d{10}$");
    @Autowired
    private XyLinkLogMapper xyLinkLogMapper;

    @Override
    public JSONObject save(String param) {
        JSONObject out = this.out("0", "成功");
        boolean isLock = false;
        String mobile = "";
        try {
            JSONObject in = JSONObject.fromObject(param);
            String personId = in.getString("personId");
            mobile = in.getString("mobile");
            String openId = in.getString("openId");
            if (!tele.matcher(mobile).matches()) {
                return out("4", "手机号码格式不正确");
            }
            // 相同手机号码用户 同时只能一个进入
            isLock = redisAction.setUserLock("xylink" + mobile);
            if (!isLock) {
                return out("4", "活动火爆，请稍后再试");
            }
            String fixArea = in.getString("fixArea");
            XyLinkLog log = new XyLinkLog();
//            Integer taocanId = in.getInt("taocanId");
//            XmfTcInfo tcInfo = this.xmfTcInfoMapper.selectByPrimaryKey(taocanId);
            log.setTaocanName(in.getString("taocanName"));
            log.setMsg("20");
            log.setUserName(in.getString("userName"));
            log.setPersonId(personId);
            log.setMobile(mobile);
            log.setFixArea(fixArea);
            log.setAddress(in.getString("address"));
            log.setSource(in.getInt("source"));
            log.setExtend2(in.getString("xyName"));
            log.setExtend1(openId);
            String uuid = UUID.randomUUID().toString().replace("-", "");
            log.setYhCode(uuid);
            XyNewAdmin admin = this.xyNewAdminMapper.selectAdminByOpenIdAndType(openId,in.getInt("source"));
            if (admin != null) {
                // 姓名
                log.setExtend3(admin.getExtend3());
                // 手机号码
                log.setExtend4(admin.getMobile());
//                log.setExtend5(admin.getExtend3());
//                log.setExtend6(admin.getExtend4());
//                log.setExtend7(admin.getMobile());
            }
            log.setStatus(1);
            log.setCreateTime(DateUtils.dateFormat.format(new Date()));
            xyLinkLogMapper.insert(log);
            redisAction.incr("gateway-Join-1D6EC972CF27A5237768B96CAB792E86");

            out.put("uuid", uuid);
            // 不是指定区域
        } catch (Exception e) {
            e.printStackTrace();
            return out("4", "活动火爆，请稍后再试！");
        } finally {
            try {
                if (isLock) {
                    redisAction.delUserLock("xylink" + mobile);
                }
            } catch (Exception e) {
            }
        }

        return out;
    }

    @Override
    public JSONObject payMoney(String param) {
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
            logger.info("---------linker提现：openIdLock-------为" + openIdLock);
//            // 判断是否是当月 15 日
            String date = DateUtils.getCurrentDate().substring(6);
//            if ("15".equals(date) || "16".equals(date)) {
//             通过用户openId,查看用户表中的数据
            synchronized (llbOpenId) {
                XyNewManager manager = this.xyNewManagerMapper.selectManagerByOpenIdAndSource(openId, 2);
                if (manager == null) {
                    return out("4", "您不可提现");
                } else {
                    // 获取用户可结算 金额
                    int money = manager.getTxMoney();
                    if (money < 1) {
                        return out("4", "可提余额不足");
                    }
                    String time = DateUtils.dateFormat.format(new Date());
                    String value = redisAction.getString(llbOpenId);
                    if (value != null) {
                        return out("4", "请稍后再试");
                    }
                    com.alibaba.fastjson.JSONObject result = QiyePay.qiYePay(llbOpenId, money * 100 + "");
                    redisAction.setString(llbOpenId, time, 3600);
                    String code = result.getString("code");
                    String tradeNo = result.getString("tradeNo");
                    XmfTxInfo txInfo = new XmfTxInfo();
                    txInfo.setLlbOpenId(llbOpenId);
                    txInfo.setOpenId(openId);
                    txInfo.setTxMoney(money + "");
                    txInfo.setCreateTime(time);
                    txInfo.setTxMonth(getCurrentDay());
                    txInfo.setExtend2("4");  // 校园link
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
                        txInfo.setExtend3(result.getString("errCode"));
                        // 提现失败，不扣款
                        manager.setTxMoney(money);
                        out.put("code", "1");
                    }
                    this.xyNewManagerMapper.updateByPrimaryKey(manager);
                    this.xmfTxInfoMapper.insertSelective(txInfo);
                }
            }
//            } else {
//                return out("4", "只能在15,16号提现");
//            }

        } catch (
                Exception e)

        {
            e.printStackTrace();
            return out("4", "服务器忙,请稍后再试");
        } finally

        {
            if (openIdLock) {
                try {
                    redisAction.delUserLock(openId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return out;
    }

    @Override
    public JSONObject saveQuanyi(String s) {
        JSONObject out = out("0", "成功");
        try {
            JSONObject in = JSONObject.fromObject(s);
            String uuid = in.getString("uuid");
            String quanyi = in.getString("quanyi");
            XyLinkLog log = this.xyLinkLogMapper.selectByYhCode(uuid);
            if (log == null) {
                return out("4", "不可参加");
            }
            if (log.getExtend8() != null) {
                return out("4", "只能领取一次");
            }
            log.setExtend8(quanyi);
            this.xyLinkLogMapper.updateByPrimaryKey(log);
        } catch (Exception e) {
            e.printStackTrace();
            return out("4", "活动火爆，稍后再试");
        }
        return out;
    }

    @Override
    public JSONObject logList(String s) throws Exception {
        JSONObject out = CommonUtil.printPlatform(0);
        try {
            JSONObject in = JSONObject.fromObject(s);
            XyLinkLog xyXmfLog = new XyLinkLog();
            int source = Integer.parseInt(in.getString("extend9").split("=")[1]);
            xyXmfLog.setSource(source);
            if (in.get("bigenPage") != null) {
                xyXmfLog.setBigenPage(in.getInt("bigenPage"));
                xyXmfLog.setEndPage(in.getInt("endPage"));
            }
            if (in.get("personId") != null && !"".equals(in.getString("personId"))) {
                xyXmfLog.setPersonId(in.getString("personId"));
            }
            if (in.get("mobile") != null && !"".equals(in.getString("mobile"))) {
                xyXmfLog.setMobile(in.getString("mobile"));
            }
            if (in.get("yhCode") != null && !"".equals(in.getString("yhCode"))) {
                xyXmfLog.setYhCode(in.getString("yhCode"));
            }
            if (in.get("status") != null && in.getInt("status") > 0) {
                xyXmfLog.setStatus(in.getInt("status"));
            }
            if (in.get("startTime") != null && !"".equals(in.getString("startTime"))) {
                xyXmfLog.setStartTime(in.getString("startTime"));
                xyXmfLog.setEndTime(in.getString("endTime"));
            }
            // 获取到 yuyueOrder集合对象
            List<XyLinkLog> orders = null;
            int count = 0;
            orders = this.xyLinkLogMapper.selectList(xyXmfLog);
            count = xyLinkLogMapper.selectCount(xyXmfLog);
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
    public JSONObject saveLog(String s) throws Exception {
        JSONObject out = CommonUtil.printPlatform(0);
        JSONObject in = JSONObject.fromObject(s);
        boolean isLock = false;
        int orderId = 0;
        try {
            XyLinkLog xyLinkLog = new XyLinkLog();
            orderId = in.getInt("orderId");
            XyLinkLog order = this.xyLinkLogMapper.selectByPrimaryKey(orderId);
            xyLinkLog.setOrderId(orderId);
            int status = order.getStatus();
            if (status != 1) {
                out.put("result", "1");
                out.put("msg", "已处理订单不可再次处理");
                return out;
            }
            isLock = redisAction.setUserLock("xylink" + orderId);
            if (!isLock) {
                out.put("result", "1");
                out.put("msg", "不可同时处理");
                return out;
            }
            // 订单状态  1 ：待处理  2 ： 成功  3： 取消
            String yjMoney = "0";
            xyLinkLog.setStatus(2);
            XyNewOrder xmfOrder = new XyNewOrder();
            xmfOrder.setOpenId(order.getExtend1());
            xmfOrder.setUserName(order.getUserName());
            xmfOrder.setUserMobile(order.getMobile());
            xmfOrder.setTaocanName(order.getTaocanName());
            // 订单佣金
            if (order.getMsg() != null && !"".equals(order.getMsg())) {
                yjMoney = order.getMsg();
            }
            xmfOrder.setYjMoney(yjMoney);
            xmfOrder.setCreateTime(DateUtils.dateFormat.format(new Date()));
            xmfOrder.setStatus(1);
            xmfOrder.setQudao(order.getSource());
            this.xyNewOrderMapper.insertSelective(xmfOrder);
            XyNewManager existManager = this.xyNewManagerMapper.selectManagerByOpenIdAndSource(order.getExtend1(), order.getSource());
            // 更新 数据
            if (existManager == null) {
                // 如果不存在 改用户信息  新增
                XyNewManager manager = new XyNewManager();
                manager.setOpenId(order.getExtend1());
                manager.setJiesuanMoney(Integer.parseInt(yjMoney));
                manager.setTxMoney(0);
                manager.setQudao(order.getSource());
                manager.setCreateTime(DateUtils.dateFormat.format(new Date()));
                // 审核成功
                this.xyNewManagerMapper.insertSelective(manager);
            } else {
                int money = existManager.getJiesuanMoney();
                existManager.setJiesuanMoney(money + Integer.parseInt(yjMoney));
                // 审核成功
                this.xyNewManagerMapper.updateByPrimaryKey(existManager);
            }
            this.xyLinkLogMapper.updateByPrimaryKeySelective(xyLinkLog);
            return out;
        } finally {
            if (isLock) {
                redisAction.delUserLock("xylink" + orderId);
            }
        }
    }

    @Override
    public JSONObject updateLog(String s) throws Exception {
        JSONObject out = CommonUtil.printPlatform(0);
        try {
            JSONObject in = JSONObject.fromObject(s);
            XyLinkLog yyOrderLog = new XyLinkLog();
            yyOrderLog.setOrderId(in.getInt("orderId"));
            yyOrderLog.setUserName(in.getString("userName"));
            yyOrderLog.setMobile(in.getString("mobile"));
            yyOrderLog.setFixArea(in.getString("fixArea"));
            yyOrderLog.setAddress(in.getString("address"));
            yyOrderLog.setPersonId(in.getString("personId"));
            if (in.get("yhCode") != null) {
                yyOrderLog.setYhCode(in.getString("yhCode"));
            }
            yyOrderLog.setTaocanName(in.getString("taocanName"));
            this.xyLinkLogMapper.updateByPrimaryKeySelective(yyOrderLog);
        } catch (Exception e) {
            e.printStackTrace();
            return CommonUtil.printPlatform(1);
        }
        return out;
    }

    @Override
    public JSONObject delLog(String s) throws Exception {
        JSONObject out = CommonUtil.printPlatform(0);
        try {
            JSONObject in = JSONObject.fromObject(s);
            XyLinkLog yuyueOrder = new XyLinkLog();
            yuyueOrder.setOrderId(in.getInt("orderId"));
            // 订单状态  1 ：待处理  2 ： 成功  3： 取消
            yuyueOrder.setStatus(3);
            // 通过订单id 查询到原订单
            XyLinkLog order = this.xyLinkLogMapper.selectByPrimaryKey(in.getInt("orderId"));
            if (order.getStatus() != 1) {
                out.put("result", "1");
                out.put("msg", "已处理订单不可再次处理");
                return out;
            }
            this.xyLinkLogMapper.updateByPrimaryKeySelective(yuyueOrder);
        } catch (Exception e) {
            e.printStackTrace();
            return CommonUtil.printPlatform(1);
        }
        return out;
    }

    @Override
    public JSONObject downloadLog(String extend1, HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject out = CommonUtil.printPlatform(0);
        try {
            XyLinkLog xmfOrder = new XyLinkLog();
            xmfOrder.setBigenPage(0);
            xmfOrder.setEndPage(9999999);
            xmfOrder.setSource(Integer.parseInt(extend1));
            // 获取到 yuyueOrder集合对象
            List<XyLinkLog> orders = this.xyLinkLogMapper.selectList(xmfOrder);
            this.writeExcelLog(request, response, "下单记录", new String[]{"推荐人姓名","推荐人号码","用户姓名", "身份证号", "手机号码", "区域（身份证地址）",
                    "安装(寄送)地址", "学校", "套餐名称", "用户权益", "下单时间", "状态"}, orders);
        } catch (Exception e) {
            e.printStackTrace();
            return CommonUtil.printPlatform(1);
        }
        return out;
    }

    private void writeExcelLog(HttpServletRequest request, HttpServletResponse response, String name, String[] title, List<XyLinkLog> list) {
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
            //下面是填充数据
            /*
               "用户姓名", "身份证号", "手机号码","安装区域",
                    "安装地址","学校",  "套餐名称",  "下单时间", "状态"
             */
            row1.createCell(0).setCellValue(list.get(j).getExtend3());
            row1.createCell(1).setCellValue(list.get(j).getExtend4());
            row1.createCell(2).setCellValue(list.get(j).getUserName());
            row1.createCell(3).setCellValue(list.get(j).getPersonId());
            row1.createCell(4).setCellValue(list.get(j).getMobile());
            row1.createCell(5).setCellValue(list.get(j).getFixArea());
            row1.createCell(6).setCellValue(list.get(j).getAddress());
            row1.createCell(7).setCellValue(list.get(j).getExtend2());
            row1.createCell(8).setCellValue(list.get(j).getTaocanName());
            row1.createCell(9).setCellValue(list.get(j).getExtend8());
            row1.createCell(10).setCellValue(list.get(j).getCreateTime());
            row1.createCell(11).setCellValue(list.get(j).getStatus() == 3 ? "取消" : list.get(j).getStatus() == 2 ? "成功" : "待处理");
        }
        try {
            if (wb != null) {
                response.setHeader("Content-disposition",
                        "attachment;filename=" + URLEncoder.encode("下单记录.xls", "UTF-8"));
                response.setContentType("application/msexcel;charset=UTF-8");
                OutputStream out = response.getOutputStream();
                wb.write(out);
                out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public JSONObject getProcessOrder(String json) {
        JSONObject out = this.out("0", "成功");
        JSONObject in = JSONObject.fromObject(json);
        try {
            XyNewOrder xmfOrder = new XyNewOrder();
            XyLinkLog xmXmfLog = new XyLinkLog();
            String openId = in.getString("openId");
            int source = in.getInt("source");
            xmfOrder.setOpenId(openId);
            xmfOrder.setQudao(source);
            xmXmfLog.setExtend1(openId);  // openId
            // 订单状态 status 0 全部，1,代处理，2,成功，3,取消
            int status = in.getInt("status");
            List<XyLinkLog> orders = new ArrayList<XyLinkLog>();
            List<XyNewOrder> xmfList = new ArrayList<XyNewOrder>();
            if (status == 1) {
                xmXmfLog.setStatus(1);
                orders = this.xyLinkLogMapper.selectList(xmXmfLog);
                xmfOrder.setStatus(1);
                xmfList = this.xyNewOrderMapper.selectList(xmfOrder);
            } else if (status == 0) {
                xmXmfLog.setStatus(1);
                orders = this.xyLinkLogMapper.selectList(xmXmfLog);
                xmXmfLog.setStatus(3);
                orders.addAll(this.xyLinkLogMapper.selectList(xmXmfLog));
                xmfList = this.xyNewOrderMapper.selectList(xmfOrder);
            }
            List<XmfResult> results = new ArrayList<XmfResult>();
            for (XyLinkLog log : orders) {
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

            for (XyNewOrder order : xmfList) {
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
            out.put("data", results);
            System.out.println("*********查询订单结果********" + results);
        } catch (Exception e) {
            e.printStackTrace();
            return out("4", "活动火爆，稍后再试");
        }
        return out;
    }

    @Override
    public JSONObject querySuccessOrderList(String param) {
        JSONObject out = this.out("0", "成功");
        JSONObject in = JSONObject.fromObject(param);
        try {
            XyNewOrder xmfOrder = new XyNewOrder();
            if (StringUtils.isEmpty(in.getString("openId"))) {
                return out("4", "参数有误");
            }
            int source = in.getInt("source");
            xmfOrder.setOpenId(in.getString("openId"));
            xmfOrder.setQudao(source);
            // 订单状态 status 0 全部，1,代处理，2,成功，3,取消
            if (in.get("status") != null && in.getInt("status") > 0) {
                xmfOrder.setStatus(in.getInt("status"));
            }
            List<XyNewOrder> orders = this.xyNewOrderMapper.selectList(xmfOrder);
            out.put("orders", orders);
        } catch (Exception e) {
            e.printStackTrace();
            return out("4", "活动火爆，稍后再试");
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
            if (in.get("area") != null && !"".equals(in.getString("area"))) {
                // 0 代表公共小蜜蜂 ， 1 校园小蜜蜂，3 高校小蜜蜂
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
    public JSONObject getUserInfo(String param) {
        JSONObject out = this.out("0", "成功");
        String openId = "";
        try {
            JSONObject in = JSONObject.fromObject(param);
            // 获取用户 openid ,用户渠道
            openId = in.getString("openId");
            int source = in.getInt("source");
            String url2 = "http://192.168.5.6:39090/itpi/thirdPart/wxuser?openId=" + openId;
            String res2 = HttpUtil.postUrl(url2, "wechat");
            JSONObject result2 = JSONObject.fromObject(res2);
            logger.info("***校园link活动***" + result2);
            if (result2.get("subscribe") == null || result2.getInt("subscribe") != 1) {
                return out("1", "未关注苏州电信");
            }
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
                        + time + "&redirect_uri=http://app1.118114sz.com/active/schoolLink/pages/goxylink.html";
                out.put("flag", "2");
                out.put("msg", "未绑定号码");
                out.put("url", url);
                return out;
            }
            String mobile = result1.getString("phone");
            XyNewAdmin xyXmfAdmin = this.xyNewAdminMapper.selectAdminByMobileAndType(mobile, source);
            if (xyXmfAdmin == null) {
                out.put("flag", "3");
                out.put("msg", "对不起，您没有权限进入");
                return out;
            }
            // 获取用户的openid
            String userOpenId = xyXmfAdmin.getOpenId();
            if (userOpenId == null || !openId.equals(userOpenId)) {
                xyXmfAdmin.setOpenId(openId);
                this.xyNewAdminMapper.updateByPrimaryKeySelective(xyXmfAdmin);
            }
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
            XyNewManager existManager = this.xyNewManagerMapper.selectManagerByOpenIdAndSource(openId, source);
            int jiesuanMoney = 0;
            int txMoney = 0;
            if (existManager == null) {
                XyNewManager manager = new XyNewManager();
                // 用户不存在 新增
                manager.setOpenId(openId);
                manager.setJiesuanMoney(jiesuanMoney);
                manager.setTxMoney(txMoney);
                manager.setNickName(nickName);
                manager.setQudao(source);
                manager.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                // 新增一条信息
                this.xyNewManagerMapper.insertSelective(manager);
            } else {
                // 可结算金额
                jiesuanMoney = existManager.getJiesuanMoney();
                // 提现金额
                txMoney = existManager.getTxMoney();
                if (existManager.getNickName() == null) {
                    existManager.setNickName(nickName);
                    this.xyNewManagerMapper.updateByPrimaryKeySelective(existManager);
                }
            }
            out.put("jiesuanMoney", jiesuanMoney);
            out.put("txMoney", txMoney);
            redisAction.incr("gxxmfclickcount");
        } catch (Exception e) {
            e.printStackTrace();
            return out("4", "活动火爆，请稍后再试");
        }
        return out;
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

    public static void main(String[] args) {
        for (int i = 0; i < 30; i++) {
            String uuid = UUID.randomUUID().toString().replace("-", "");
            System.out.println(uuid);
        }
    }
}
