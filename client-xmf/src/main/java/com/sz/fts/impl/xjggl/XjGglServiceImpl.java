package com.sz.fts.impl.xjggl;

import com.sz.fts.bean.xjggl.XjGglLog;
import com.sz.fts.dao.dongwu.SzMobileUserMapper;
import com.sz.fts.dao.xjggl.XjGglLogMapper;
import com.sz.fts.redis.repository.TestRedis;
import com.sz.fts.service.xjggl.XjGglService;
import com.sz.fts.utils.HttpUtil;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
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
import java.util.Date;
import java.util.List;

/**
 * @author 征华兴
 * @date 上午 9:06  2018/7/17 0017
 * @Copyright 江苏鸿信系统集成有限公司 All rights reserved
 */
@Service
public class XjGglServiceImpl implements XjGglService {

    @Autowired
    private XjGglLogMapper xjGglLogMapper;
    @Autowired
    private SzMobileUserMapper szMobileUserMapper;

    @Autowired
    private TestRedis testRedis;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String PRIZE_NAME_NO = "谢谢参与";
    private static final String PRIZE_NAME_YES = "饿了么10元翼支付代金券";

    @Override
    public JSONObject getGgl(JSONObject input) {
        JSONObject out = out("0", "成功");
        try {
            String mobile = input.getString("mobile");
            String regex = "1\\d{10}";
            if (!mobile.matches(regex)) {
                return out("4", "请输入正确的手机号");
            }
            XjGglLog existLog = this.xjGglLogMapper.selectByMobile(mobile);
            if (existLog != null) {
                //  已领取过
                out.put("sign", "2");
                out.put("prizeName", existLog.getPrize());
                return out;
            }
            // 未领取
            out.put("sign", "1");
            out.put("prizeName", "");
            return out;
        } catch (Exception e) {
            e.printStackTrace();
            return out("4", "活动火爆");
        }
    }

    @Override
    public JSONObject holdLog(JSONObject in) {
        JSONObject out = out("0", "成功");
        boolean mobileLock = false;
        String mobile = "";
        try {
            if (StringUtils.isEmpty(in.getString("mobile"))) {
                return out("4", "参数有误");
            }
            String openId = in.getString("openId");
            mobile = in.getString("mobile");
            String regex = "1\\d{10}";
            if (!mobile.matches(regex)) {
                return out("4", "请输入正确的手机号");
            }
            mobileLock = testRedis.setUserLock("xjggl" + mobile);
            if (!mobileLock) {
                return out("4", "活动火爆，请稍后再试");
            }
            // 判断用户是否已经领取过
            XjGglLog log = xjGglLogMapper.selectByMobile(mobile);
            if (log != null) {
                // 已经中奖
                return out("4", "您已经参加过了");
            }
            XjGglLog xjGglLog = new XjGglLog();
            xjGglLog.setMobile(mobile);
            xjGglLog.setOpenId(openId);
            xjGglLog.setCreateTime(dateFormat.format(new Date()));
            if ("1".equals(openId)) {
                // 中奖
                String url = "http://221.228.43.76:6688/api-transfer/user/getlevel?ACCESS_NUMBER=" + mobile;
                String res = HttpUtil.sendUrl(url);
                JSONObject result = JSONObject.fromObject(res);
                JSONObject result1 = result.getJSONObject("returnValue");
                System.out.println("================" + result1);
                if (result1.get("userlevel") == null || "null".equals(result1.getString("userlevel"))) {
                    xjGglLog.setPrize(PRIZE_NAME_NO);
                    out.put("prizeName", PRIZE_NAME_NO);
                    this.xjGglLogMapper.insert(xjGglLog);
                    return out;
                }
                int count = this.xjGglLogMapper.selectByPrizeName(PRIZE_NAME_YES);
                if (count < 2985) {
                    xjGglLog.setPrize(PRIZE_NAME_YES);
                    out.put("prizeName", PRIZE_NAME_YES);
                    this.xjGglLogMapper.insert(xjGglLog);
                    return out;
                }
            }
            xjGglLog.setPrize(PRIZE_NAME_NO);
            out.put("prizeName", PRIZE_NAME_NO);
            this.xjGglLogMapper.insert(xjGglLog);
            return out;
        } catch (Exception e) {
            e.printStackTrace();
            return out("4", "活动火爆，请稍后再试");
        } finally {
            if (mobileLock) {
                try {
                    testRedis.delUserLock("xjggl" + mobile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void downloadLog(HttpServletRequest request, HttpServletResponse response) {
        try {
            List<XjGglLog> logs = xjGglLogMapper.selectAll();
            writeExcel(request, response, "刮奖记录", new String[]{"手机号", "奖品", "刮奖时间"}, logs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeExcel(HttpServletRequest request, HttpServletResponse response, String name, String[] title, List<XjGglLog> list) {
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
            row1.createCell(0).setCellValue(list.get(j).getMobile());
            row1.createCell(1).setCellValue(list.get(j).getPrize());
            row1.createCell(2).setCellValue(list.get(j).getCreateTime());
        }
        try {
            if (wb != null) {
                response.setHeader("Content-disposition",
                        "attachment;filename=" + URLEncoder.encode("刮奖记录.xls", "UTF-8"));
                response.setContentType("application/msexcel;charset=UTF-8");
                OutputStream out = response.getOutputStream();
                wb.write(out);
                out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JSONObject out(String flag, String msg) {
        JSONObject out = new JSONObject();
        out.put("flag", flag);
        out.put("msg", msg);
        return out;
    }
}
