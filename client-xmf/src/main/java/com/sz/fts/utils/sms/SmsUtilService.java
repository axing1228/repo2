package com.sz.fts.utils.sms;

import com.sz.fts.redis.repository.RedisAction;
import com.sz.fts.utils.DateUtils;
import com.sz.fts.utils.IpFindConstant;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;


@Service
public class SmsUtilService {

    @Autowired
    private RedisAction redisAction;

    public JSONObject sendSms(String key, String mobile, String msg, HttpServletRequest request) throws Exception {

        boolean userLock = redisAction.setUserSmsLock(mobile + key + "_2018");
        if (!userLock) {
            return out("4", "一分钟内不能再次发送验证码。");
        }
        String ip = IpFindConstant.getIpAddress(request);
        boolean userLockIp = redisAction.setUserLock(ip + key + "_2018");
        if (!userLockIp) {
            return out("4", "请稍后再试。");
        }
        String num = redisAction.getString("IP2018_COUNT_" + ip);
        if (num != null && !num.equals("")) {
            if (Integer.parseInt(num) > 18) {
                return out("4", "此Ip今日发送验证码已达上限");
            }
            redisAction.setString("IP2018_COUNT_" + ip, (Integer.parseInt(num) + 1) + "", 24 * 3600);
        } else {
            redisAction.setString("IP2018_COUNT_" + ip, "1", 24 * 3600);
        }

        String value = redisAction.getString(mobile + DateUtils.getCurrentTime14().substring(0, 8) + key);
        if (value != null && Integer.parseInt(value) > 3) {
            return out("4", "验证码发送次数太多，请次日再进行验证");
        } else {
            if (value == null) {
                redisAction.setString(mobile + DateUtils.getCurrentTime14().substring(0, 8) + key, 1 + "", 3600 * 24);
            } else {
                redisAction.setString(mobile + DateUtils.getCurrentTime14().substring(0, 8) + key, Integer.parseInt(value) + 1 + "", 3600 * 24);
            }
        }
        SmsUtil.sendSms(mobile, msg);
        return out("0", "发送成功");
    }

    private JSONObject out(String flag, String msg) {
        JSONObject out = new JSONObject();
        out.put("flag", flag);
        out.put("msg", msg);
        return out;
    }
}
