package com.sz.fts.impl.yuyuekunsan;


import com.sz.fts.bean.yuyuekunsan.YYOrderKS;
import com.sz.fts.bean.yuyuekunsan.YYPhoneKS;
import com.sz.fts.dao.yuyuekunsan.YYOrderKSMapperTwo;
import com.sz.fts.dao.yuyuekunsan.YYPhoneKSMapper;
import com.sz.fts.redis.repository.RedisAction;
import com.sz.fts.service.yuyuekunsan.YuYueLHService;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 靓号0元抢
 * @author 耿怀志
 * @version [版本号, 2019/3/20]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
@Service
public class YuYueLHServiceImpl implements YuYueLHService {

    @Autowired
    private RedisAction redisAction;

    @Autowired
    private YYPhoneKSMapper yyPhoneKSMapper;

    @Autowired
    private YYOrderKSMapperTwo yyOrderKSMapper;



    @Override
    public JSONObject xiuOrder(JSONObject json) throws Exception {
        JSONObject out = out("0","成功");
        try {
            YYOrderKS yyOrderLog = this.yyOrderKSMapper.selectByJuBen(json.getString("orderId"));
            yyOrderLog.setExtend1(json.getString("status"));
            if(json.getString("status").equals("0")){
                yyOrderLog.setStatus(3);
                // 获取手机号码对象
                YYPhoneKS phone = this.yyPhoneKSMapper.selectByMobile(yyOrderLog.getPhoneNumber());
                // 取消订单  将号码变为可选
                phone.setStatus(0);
                phone.setExtend1("");
                this.yyPhoneKSMapper.updateByPrimaryKeySelective(phone);
            }
            this.yyOrderKSMapper.updateByPrimaryKeySelective(yyOrderLog);
        } catch (Exception e) {
            e.printStackTrace();
            return out("4","请稍后再试。");
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
