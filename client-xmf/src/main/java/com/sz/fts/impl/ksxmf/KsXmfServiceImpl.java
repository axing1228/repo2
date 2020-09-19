package com.sz.fts.impl.ksxmf;

import com.sz.fts.bean.ksxmf.KsXmfLog;
import com.sz.fts.dao.ksxmf.KsXmfLogMapper;
import com.sz.fts.redis.repository.RedisAction;
import com.sz.fts.service.ksxmf.KsXmfService;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * @author 征华兴
 * @date 下午 3:18  2018/8/9 0009
 * @Copyright 江苏鸿信系统集成有限公司 All rights reserved
 */
@Service
public class KsXmfServiceImpl implements KsXmfService {

    @Autowired
    private RedisAction redisAction;
    @Autowired
    private KsXmfLogMapper ksXmfLogMapper;

    private static Pattern phone_pattern = Pattern.compile("1[3|4|5|6|7|8|9]\\d{9}");
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public JSONObject saveInfo(String json) {
        JSONObject out = new JSONObject();
        out.put("flag", "4");
        boolean phoneLock = false;
        JSONObject in = JSONObject.fromObject(json);
        String mobile = "";
        try {
            mobile = in.getString("mobile");
            if (!phone_pattern.matcher(mobile).matches()) {
                out.put("msg", "请核对手机号码");
                return out;
            }
            if (null == in.get("personId") || "".equals(in.getString("personId"))) {
                out.put("msg", "请核对身份证号码");
                return out;
            }
            String userName = in.getString("userName"); // 姓名
            String personId = in.getString("personId"); //身份证号码
            String address = in.getString("address");   // 地址
            phoneLock = redisAction.setUserLock("ksxmf" + mobile);
            if (!phoneLock) {
                out.put("msg", "请稍后再试");
                return out;
            }
            int count = ksXmfLogMapper.selectByMobile(mobile);
            if (count > 0) {
                out.put("msg", "该号码已使用，请重新输入手机号码");
                return out;
            }
            // 创建一个yuyueOrder订单表
            KsXmfLog ksXmfLog = new KsXmfLog();
            ksXmfLog.setUserName(userName);
            ksXmfLog.setPersonId(personId);
            ksXmfLog.setMobile(mobile);
            ksXmfLog.setAddress(address);
            ksXmfLog.setPersonAddress(in.getString("personAddress"));
            ksXmfLog.setMsg(in.getString("msg"));
            ksXmfLog.setCreateTime(dateFormat.format(new Date()));
            ksXmfLog.setTaocanName(in.getString("taocanName"));
            ksXmfLog.setSource(in.getInt("source"));
            ksXmfLog.setStatus(1);
            this.ksXmfLogMapper.insert(ksXmfLog);
            out.put("flag", "0");
            out.put("msg", "成功");
            return out;
        } catch (Exception e) {
            e.printStackTrace();
            out.put("flag", "4");
            out.put("msg", "活动火爆，请稍后再试");
            return out;
        } finally {
            if (phoneLock) {
                try {
                    redisAction.delUserLock("ksxmf" + mobile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
