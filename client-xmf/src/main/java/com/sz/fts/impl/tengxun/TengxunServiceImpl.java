package com.sz.fts.impl.tengxun;

import com.sz.fts.bean.own.OwnSMS;
import com.sz.fts.bean.tengxun.TengxunOrder;
import com.sz.fts.bean.tengxun.TengxunWhite;
import com.sz.fts.dao.own.OwnSMSMapper;
import com.sz.fts.dao.tengxun.TengxunOrderMapper;
import com.sz.fts.dao.tengxun.TengxunWhiteMapper;
import com.sz.fts.redis.repository.RedisAction;
import com.sz.fts.service.tengxun.TengxunService;
import com.sz.fts.utils.DateUtils;
import com.sz.fts.utils.HttpUtil;
import com.sz.fts.utils.sms.SmsUtil;
import net.sf.json.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
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
import java.util.*;

/**
 * @author 征华兴
 * @date 上午 10:30  2018/11/7 0007
 * @Copyright 江苏鸿信系统集成有限公司 All rights reserved
 */
@Service
public class TengxunServiceImpl implements TengxunService {

    private static final Logger logger = LogManager.getLogger(TengxunServiceImpl.class);

    private static final String appid = "1450409239926402";
    private static final String key = "#F*2AP2ayx+W=9O21yqfETFZnxZVoI%J";

    @Autowired
    private RedisAction redisAction;
    @Autowired
    private OwnSMSMapper ownSMSMapper;
    @Autowired
    private TengxunWhiteMapper tengxunWhiteMapper;
    @Autowired
    private TengxunOrderMapper tengxunOrderMapper;

    @Override
    public JSONObject openAndBindTengxun(String json) {

        JSONObject out = new JSONObject();
        boolean phoneLock = false;
        String phone = "";
        try {
            JSONObject in = JSONObject.fromObject(json);
            if (in.get("phone") == null || !in.getString("phone").matches("1\\d{10}")) {
                return out("4", "请输入正确的手机号");
            }
            // 用户QQ号
            if (in.get("userQQ") == null || !in.getString("userQQ").matches("[1-9][0-9]{4,11}")) {
                return out("4", "请输入正确的QQ号码");
            }
            if (in.get("type") == null || "".equals(in.getString("type"))) {
                return out("4", "链接参数有误");
            }
            phone = in.getString("phone");
            // 校验验证码
            OwnSMS ownSMS = ownSMSMapper.findSMSByTelephone(phone);
            if (ownSMS != null) {
                Long start = Long.parseLong(DateUtils.sdf14.format(DateUtils.sdfhuman.parse(ownSMS.getSmsCreate())).substring(0, 12));
                Long end = Long.parseLong(DateUtils.getCurrentTime14().substring(0, 12));
                if (end - start > 5) {
                    return out("4", "验证码已过期");
                }
                if (!in.getString("code").equals(ownSMS.getSmsCode())) {
                    ownSMS.setExtend1((Integer.parseInt(ownSMS.getExtend1()) + 1) + "");
                    ownSMSMapper.updateByPrimaryKeySelective(ownSMS);
                    if (Integer.parseInt(ownSMS.getExtend1()) > 3) {
                        ownSMSMapper.deleteSMSByTelephone(phone);
                        return out("4", "验证码错误超过3次，请重新获取并输入");
                    } else {
                        return out("4", "验证码错误，请重新输入");
                    }
                } else {
                    ownSMSMapper.deleteSMSByTelephone(phone);
                }
            } else {
                return out("4", "验证码错误，请重新获取并输入");
            }
            // 判断是否是白名单
            int count = tengxunWhiteMapper.selectByMobile(phone);
            if (count < 1) {
                return out("4", "对不起，该手机号码暂时不能开通腾讯会员");
            }
            // 参数  month 开通的月份
            String type = in.getString("type");
            String source = "";
            if (type.equals("1")) {
                source = "短信";
            } else if (type.equals("2")) {
                source = "微信";
            } else {
                source = "其他";
            }
            String month = "1";
            String user_id = in.getString("userQQ");
            phoneLock = redisAction.setUserLock("tengxun" + phone);
            if (!phoneLock) {
                return out("4", "请稍后再试");
            }
            // 判断是否已经参加过
            TengxunOrder order = tengxunOrderMapper.selectByMobile(phone);
            if (order != null) {
                return out("4", "该号码已经参加，谢谢");
            }
            // 开通并绑定
            String base = "http://api.3gfly.net/telcom_vqq/open_and_bind";
            long timestamp = System.currentTimeMillis() / 1000;
            //  订单号 唯一不可重复
            String order_no = phone + "_" + timestamp + "_" + new Random().nextInt(100);
            String stringA = "appid=" + appid + "&month=" + month + "&order_no=" + order_no + "&phone=" + phone + "&timestamp=" + timestamp + "&user_id=" + user_id;
            String sign = DigestUtils.md5Hex(stringA + "&key=" + key).toUpperCase();
            Map<String, String> map = new HashMap<String, String>();
            map.put("appid", appid);
            map.put("timestamp", timestamp + "");
            map.put("sign", sign);
            map.put("order_no", order_no);
            map.put("phone", phone);
            map.put("month", month);
            map.put("user_id", user_id);
            String out2 = HttpUtil.post(base, map);
            JSONObject result1 = JSONObject.fromObject(out2);
            logger.info("----------开通并绑定结果-------" + result1);
            if ("0".equals(result1.getString("error_code"))) {
                // 成功
                out.put("flag", "0");
                out.put("msg", "成功");
                TengxunOrder tengxunOrder = new TengxunOrder();
                tengxunOrder.setMonth(month);
                tengxunOrder.setUserMobile(phone);
                tengxunOrder.setUserOrderNo(order_no);
                tengxunOrder.setUserQq(user_id);
                tengxunOrder.setCreateTime(DateUtils.dateFormat.format(new Date()));
                tengxunOrder.setExtend1(source);
                tengxunOrderMapper.insertSelective(tengxunOrder);
            } else {
                // 失败
                String msg = result1.getString("error_msg");
                out.put("flag", "4"); // 失败
                out.put("msg", msg);
            }
            return out;
        } catch (Exception e) {
            e.printStackTrace();
            return out("4", "参数错误");
        } finally {
            if (phoneLock) {
                try {
                    redisAction.delUserLock("tengxun" + phone);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public JSONObject closeTengxun(String json) {
        JSONObject out = new JSONObject();
        try {
            JSONObject in = JSONObject.fromObject(json);
            String order_no = in.getString("order_no");
            String phone = in.getString("phone");
            // 关闭
            String base = "http://api.3gfly.net/telcom_vqq/close";
            // 字符串A
            long timestamp = System.currentTimeMillis() / 1000;
            String stringA = "appid=" + appid + "&order_no=" + order_no + "&phone=" + phone + "&timestamp=" + timestamp;
            String sign = DigestUtils.md5Hex(stringA + "&key=" + key).toUpperCase();
            Map<String, String> map = new HashMap<String, String>();
            map.put("appid", appid);
            map.put("timestamp", timestamp + "");
            map.put("sign", sign);
            map.put("order_no", order_no);
            map.put("phone", phone);
            String out2 = HttpUtil.post(base, map);
            JSONObject result1 = JSONObject.fromObject(out2);
            logger.info("----------关闭接口-------" + result1);
            if ("0".equals(result1.getString("error_code"))) {
                // 成功
                out.put("flag", "0");
                out.put("msg", "成功");
            } else {
                // 失败
                String msg = result1.getString("error_msg");
                out.put("flag", "4"); // 失败
                out.put("msg", msg);
            }
            return out;
        } catch (Exception e) {
            e.printStackTrace();
            return out("4", "参数错误");
        }
    }

    @Override
    public void download(HttpServletRequest request, HttpServletResponse response) {
        try {
            List<TengxunOrder> orders = tengxunOrderMapper.selectAll();
            writeExcel(request, response, "参与记录", new String[]{"手机号", "QQ号", "开通月份", "时间", "渠道"}, orders);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeExcel(HttpServletRequest request, HttpServletResponse response, String name, String[] title, List<TengxunOrder> list) {
        //创建一个EXCEL
        Workbook wb = new HSSFWorkbook();
        //创建一个SHEET
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
            //下面是填充数据 "手机号", "QQ", "中奖时间"
            row1.createCell(0).setCellValue(list.get(j).getUserMobile());
            row1.createCell(1).setCellValue(list.get(j).getUserQq());
            row1.createCell(2).setCellValue(list.get(j).getMonth());
            row1.createCell(3).setCellValue(list.get(j).getCreateTime());
            row1.createCell(4).setCellValue(list.get(j).getExtend1());

        }
        try {
            if (wb != null) {
                response.setHeader("Content-disposition",
                        "attachment;filename=" + URLEncoder.encode("腾讯会员参与记录.xls", "UTF-8"));
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
    public JSONObject sendCodeAll() {
        JSONObject out = out("0", "成功");
        try {
            List<TengxunWhite> phones = tengxunWhiteMapper.selectByExtend4("0");
//            String msg = "请打开网址：http://app1.118114sz.com/active/dyCardInput/index.html" + " 领取您的腾讯会员！";
            String msg = "【苏州电信】感谢您办理中国电信抖音爽卡，您的腾讯会员已经发放，请在3个工作日之内点此链接 https://dwz.cn/Fs8jklwh " + "直接领取";
            int count = 0;
            for (TengxunWhite s : phones) {
                // 获取所有手机号码
                SmsUtil.sendSms(s.getUserMobile(), msg);
                count++;
                s.setExtend4("1");
                s.setExtend2(DateUtils.dateFormat.format(new Date()));
                this.tengxunWhiteMapper.updateByPrimaryKeySelective(s);
            }
            out.put("count", count);
        } catch (Exception e) {
            e.printStackTrace();
            return out("4", "活动火爆");
        }
        return out;
    }


    private JSONObject out(String flag, String msg) {
        JSONObject out = new JSONObject();
        out.put("flag", flag);
        out.put("msg", msg);
        return out;
    }
}
