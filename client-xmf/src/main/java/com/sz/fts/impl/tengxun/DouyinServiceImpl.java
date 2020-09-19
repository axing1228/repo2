package com.sz.fts.impl.tengxun;


import com.sz.fts.bean.own.OwnSMS;
import com.sz.fts.bean.tengxun.DouyinBmLog;
import com.sz.fts.dao.own.OwnSMSMapper;
import com.sz.fts.dao.tengxun.DouyinBmLogMapper;
import com.sz.fts.redis.repository.RedisAction;
import com.sz.fts.service.tengxun.DouyinService;
import com.sz.fts.utils.DateUtils;
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
import java.util.Date;
import java.util.List;

/**
 * @author 征华兴
 * @date 下午 1:33  2018/12/24 0024
 * @Copyright 江苏鸿信系统集成有限公司 All rights reserved
 */
@Service
public class DouyinServiceImpl implements DouyinService {

    @Autowired
    private RedisAction redisAction;
    @Autowired
    private DouyinBmLogMapper douyinBmLogMapper;
    @Autowired
    private OwnSMSMapper ownSMSMapper;

    @Override
    public JSONObject hold(String json) {
        JSONObject out = out("0", "您已成功预约领取视频会员，请及时前往就近校园营业厅领取视频VIP会员卡办理会员领取账号。");
        boolean youlunLock = false;
        String mobile = "";
        try {
            JSONObject input = JSONObject.fromObject(json);
            if (input.get("mobile") == null || !input.getString("mobile").matches("1\\d{10}")) {
                return out("4", "手机号码有误");
            }
            mobile = input.getString("mobile");
            // 校验验证码
            OwnSMS ownSMS = ownSMSMapper.findSMSByTelephone(mobile);
            if (ownSMS != null) {
                Long start = Long.parseLong(DateUtils.sdf14.format(DateUtils.sdfhuman.parse(ownSMS.getSmsCreate())).substring(0, 12));
                Long end = Long.parseLong(DateUtils.getCurrentTime14().substring(0, 12));
                if (end - start > 5) {
                    return out("4", "验证码已过期");
                }
                if (!input.getString("code").equals(ownSMS.getSmsCode())) {
                    ownSMS.setExtend1((Integer.parseInt(ownSMS.getExtend1()) + 1) + "");
                    ownSMSMapper.updateByPrimaryKeySelective(ownSMS);
                    if (Integer.parseInt(ownSMS.getExtend1()) > 3) {
                        ownSMSMapper.deleteSMSByTelephone(input.getString("mobile"));
                        return out("4", "验证码错误超过3次，请重新获取并输入");
                    } else {
                        return out("4", "验证码错误，请重新输入");
                    }
                } else {
                    ownSMSMapper.deleteSMSByTelephone(input.getString("mobile"));
                }
            } else {
                return out("4", "验证码错误，请重新获取并输入");
            }
            youlunLock = redisAction.setUserLock("shuangdan" + mobile);
            if (!youlunLock) {
                return out("4", "预约中...");
            }
            // 判断在该渠道是否已经报过名
            DouyinBmLog existLog = this.douyinBmLogMapper.selectByMobile(mobile);
            if (existLog != null) {
                return out("4", "只能预约一次");
            }
            String tjuser = "";
            if (input.get("tjuser") != null) {
                tjuser = input.getString("tjuser");
            }
            // 保存信息
            DouyinBmLog log = new DouyinBmLog();
            log.setMobile(mobile);
            // 学校名称
            log.setSchool(input.getString("school"));
            // 视频选择
            log.setVideo(input.getString("video"));
            log.setCreateTime(DateUtils.dateFormat.format(new Date()));
            log.setExtend1(tjuser);
            this.douyinBmLogMapper.insertSelective(log);
            return out;

        } catch (Exception e) {
            e.printStackTrace();
            return out("4", "活动火爆，请稍后再试");
        } finally {
            if (youlunLock) {
                try {
                    redisAction.delUserLock("shuangdan" + mobile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void downloadLog(HttpServletRequest request, HttpServletResponse response) {
        try {
            // 查询所有的记录
            List<DouyinBmLog> logs = this.douyinBmLogMapper.selectAll();
            this.writeExcelLog(request, response, "记录", new String[]{"手机号", "学校", "视频", "时间","推荐人信息"}, logs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeExcelLog(HttpServletRequest request, HttpServletResponse response, String name, String[] title, List<DouyinBmLog> list) {

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
            row1.createCell(0).setCellValue(list.get(j).getMobile());
            row1.createCell(1).setCellValue(list.get(j).getSchool());
            row1.createCell(2).setCellValue(list.get(j).getVideo());
            row1.createCell(3).setCellValue(list.get(j).getCreateTime());
            row1.createCell(4).setCellValue(list.get(j).getExtend1());
        }
        try {
            if (wb != null) {
                response.setHeader("Content-disposition",
                        "attachment;filename=" + URLEncoder.encode("记录.xls", "UTF-8"));
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
