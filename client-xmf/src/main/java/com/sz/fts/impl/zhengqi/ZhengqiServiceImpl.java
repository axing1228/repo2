package com.sz.fts.impl.zhengqi;

import com.sz.fts.service.zhengqi.ZhengqiService;
import com.sz.fts.utils.HttpUtil;
import com.sz.fts.utils.sms.MD5;
import net.sf.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

/**
 * @author 征华兴
 * @date 上午 9:44  2018/6/9 0009
 * @Copyright 江苏鸿信系统集成有限公司 All rights reserved
 */
@Service
public class ZhengqiServiceImpl implements ZhengqiService {


    private static final Logger logger = LogManager.getLogger(ZhengqiServiceImpl.class);

    @Override
    public JSONObject getUserInfo(String param) {
        JSONObject out = this.out("0", "成功");
        String openId = "";
        try {
            JSONObject in = JSONObject.fromObject(param);
            openId = in.getString("openId");
            String skipUrl = in.getString("url");
            logger.info("跳转链接" + skipUrl);
            // 通过openid  获取手机号码
            String url1 = "http://192.168.5.6:39090/itpi/thirdPart/phoneQuery?openid=" + openId;
            String res1 = HttpUtil.postUrl(url1, "wechat");
            JSONObject result1 = JSONObject.fromObject(res1);
            System.out.println("***获取手机号***" + result1);
            //  跳转绑定页面
            if (result1.getString("phone") == null || "".equals(result1.getString("phone"))) {
                long time = System.currentTimeMillis();
                String sign = MD5.GetMD5Code("10000003qAz4#6RfV1=3yHn8" + time);
                String url = "http://quanzi.118114sz.com/network/bindPhone?contact_id=10000003&openid="
                        + openId + "&sign=" + sign + "&timestamp="
                        + time + "&redirect_uri=" + skipUrl;
                out.put("flag", "2");
                out.put("msg", "未绑定号码");
                out.put("url", url);
                return out;
            }
            String mobile = result1.getString("phone");  // 获取用户的手机号码
            out.put("mobile", mobile);
            return out;
        } catch (Exception e) {
            e.printStackTrace();
            return out("4", "活动火爆，请稍后再试");
        }
    }

    private JSONObject out(String flag, String msg) {
        JSONObject out = new JSONObject();
        out.put("flag", flag);
        out.put("msg", msg);
        return out;
    }
}
