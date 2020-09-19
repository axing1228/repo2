package com.sz.fts.impl.xjggl;

import com.sz.fts.bean.own.OwnSMS;
import com.sz.fts.bean.xjggl.XjDfsLog;
import com.sz.fts.bean.xjggl.XjDfsWhite;
import com.sz.fts.dao.own.OwnSMSMapper;
import com.sz.fts.dao.xjggl.XjDfsLogMapper;
import com.sz.fts.dao.xjggl.XjDfsWhiteMapper;
import com.sz.fts.redis.repository.RedisAction;
import com.sz.fts.service.xjggl.XjDfsService;
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
 * @date 下午 2:16  2018/11/8 0008
 * @Copyright 江苏鸿信系统集成有限公司 All rights reserved
 */
@Service
public class XjDfsServiceImpl implements XjDfsService {

    @Autowired
    private OwnSMSMapper ownSMSMapper;
    @Autowired
    private RedisAction redisAction;
    @Autowired
    private XjDfsWhiteMapper xjDfsWhiteMapper;
    @Autowired
    private XjDfsLogMapper xjDfsLogMapper;

    /**
     * 保存接口
     *
     * @param json
     * @return
     */
    @Override
    public JSONObject holdLog(JSONObject json) {
        JSONObject out = out("0", "成功");
        String mobile = "";
        try {
            JSONObject input = JSONObject.fromObject(json);
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
            // 判断用户是否是白名单用户
            XjDfsWhite existWhite = xjDfsWhiteMapper.selectByMobile(mobile);
            if (existWhite == null) {
                return out("4", "本活动仅对四星级以上主卡用户开放");
            }
            String startLevel = existWhite.getStarLevel();
            int count = xjDfsLogMapper.selectCountByMobile(mobile);
            if (count < 1) {
                XjDfsLog log = new XjDfsLog();
                log.setMobile(mobile);
                // 礼品等级
                log.setStarLevel(startLevel);
                log.setCreateTime(DateUtils.dateFormat.format(new Date()));
                this.xjDfsLogMapper.insertSelective(log);
            }
            out.put("startLevel", startLevel);
            return out;
        } catch (Exception e) {
            e.printStackTrace();
            return out("4", "活动火爆，请稍后再试");
        }
    }

    @Override
    public void downloadLog(HttpServletRequest request, HttpServletResponse response) {
        try {
            List<XjDfsLog> logs = xjDfsLogMapper.selectAll();
            writeExcel(request, response, "记录", new String[]{"手机号", "等级", "时间"}, logs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeExcel(HttpServletRequest request, HttpServletResponse response, String name, String[] title, List<XjDfsLog> list) {

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
            row1.createCell(1).setCellValue(list.get(j).getStarLevel());
            row1.createCell(2).setCellValue(list.get(j).getCreateTime());
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
