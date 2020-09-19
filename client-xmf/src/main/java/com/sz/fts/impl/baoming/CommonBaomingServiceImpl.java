package com.sz.fts.impl.baoming;

import com.sz.fts.bean.baoming.CommonBaomingLog;
import com.sz.fts.dao.baoming.CommonBaomingLogMapper;
import com.sz.fts.redis.repository.TestRedis;
import com.sz.fts.service.baoming.CommonBaomingService;
import com.sz.fts.utils.CommonUtil;
import net.sf.json.JSONObject;
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
import java.util.regex.Pattern;

/**
 * @author 征华兴
 * @date 下午 3:55  2018/8/6 0006
 * @Copyright 江苏鸿信系统集成有限公司 All rights reserved
 */
@Service
public class CommonBaomingServiceImpl implements CommonBaomingService {

    @Autowired
    private CommonBaomingLogMapper commonBaomingLogMapper;
    @Autowired
    private TestRedis testRedis;

    // private SimpleDateFormat sdf8 = new SimpleDateFormat("yyyyMMdd");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static Pattern phone_pattern = Pattern.compile("1[3|4|5|6|7|8|9]\\d{9}");

    @Override
    public JSONObject hold(String s) {
        JSONObject out = out("0", "成功");
        boolean youlunLock = false;
        String mobile = "";
        try {
            JSONObject input = JSONObject.fromObject(s);
            mobile = input.getString("mobile");
            // 校验手机号码
            if (!phone_pattern.matcher(mobile).matches()) {
                return out("4", "请输入正确的手机号码");
            }
            youlunLock = testRedis.setUserLock("commonBaoming" + mobile);
            if (!youlunLock) {
                return out("4", "活动火爆，请稍后再试");
            }
            // 判断手机号码是否存在
            CommonBaomingLog existLog = this.commonBaomingLogMapper.selectByMobile(mobile);
            if (existLog != null) {
                return out("4", "该号码已参加，请重新输入号码");
            }
            int count = testRedis.incrby("commonBaoming", 1).intValue();
            if (count > 30) {
                return out("4", "报名人数已满，请关注下期活动");
            }
            // 保存信息
            CommonBaomingLog log = new CommonBaomingLog();
            log.setUserName(input.getString("userName"));
            log.setMobile(mobile);
            log.setWechatNum(input.getString("wechatNum"));
            log.setCreateTime(dateFormat.format(new Date()));
            this.commonBaomingLogMapper.insert(log);
            return out;

        } catch (Exception e) {
            e.printStackTrace();
            return out("4", "活动火爆，请稍后再试");
        } finally {
            if (youlunLock) {
                try {
                    testRedis.delUserLock("commonBaoming" + mobile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public JSONObject downloadLog(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject out = CommonUtil.printPlatform(0);
        try {
            // 查询所有的记录
            List<CommonBaomingLog> logs = this.commonBaomingLogMapper.selectAll();
            this.writeExcelLog(request, response, "报名记录", new String[]{"姓名", "手机号", "微信号", "报名时间"}, logs);
        } catch (Exception e) {
            e.printStackTrace();
            return CommonUtil.printPlatform(1);
        }
        return out;
    }

    private void writeExcelLog(HttpServletRequest request, HttpServletResponse response, String name, String[] title, List<CommonBaomingLog> list) {

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
            row1.createCell(1).setCellValue(list.get(j).getMobile());
            row1.createCell(2).setCellValue(list.get(j).getWechatNum());
            row1.createCell(3).setCellValue(list.get(j).getCreateTime());
        }
        try {
            if (wb != null) {
                response.setHeader("Content-disposition",
                        "attachment;filename=" + URLEncoder.encode("报名记录.xls", "UTF-8"));
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
