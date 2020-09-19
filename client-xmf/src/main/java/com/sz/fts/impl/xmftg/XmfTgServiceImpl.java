package com.sz.fts.impl.xmftg;

import com.sz.fts.bean.xmf.KdyyLog;
import com.sz.fts.bean.xmf.XmfResult;
import com.sz.fts.bean.xmf.XmfTcInfo;
import com.sz.fts.bean.xmf.YYOrderKS;
import com.sz.fts.bean.xmftg.XmfTgInfo;
import com.sz.fts.bean.xmftg.XmfTuanYuanInfo;
import com.sz.fts.dao.xmf.KdyyLogMapper;
import com.sz.fts.dao.xmf.XmfTcInfoMapper;
import com.sz.fts.dao.xmf.YYOrderKSMapper;
import com.sz.fts.dao.xmftg.XmfTgInfoMapper;
import com.sz.fts.dao.xmftg.XmfTuanYuanInfoMapper;
import com.sz.fts.redis.repository.RedisAction;
import com.sz.fts.service.xmftg.XmfTgService;
import com.sz.fts.utils.DateUtils;
import com.sz.fts.utils.HttpUtil;
import net.sf.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author 征华兴
 * @date 下午 2:48  2018/5/29 0029
 * @Copyright 江苏鸿信系统集成有限公司 All rights reserved
 */
@Service
public class


XmfTgServiceImpl implements XmfTgService {

    @Autowired
    private XmfTgInfoMapper xmfTgInfoMapper;
    @Autowired
    private XmfTuanYuanInfoMapper xmfTuanYuanInfoMapper;
    @Autowired
    private XmfTcInfoMapper xmfTcInfoMapper;
    @Autowired
    private KdyyLogMapper kdyyLogMapper;
    @Autowired
    private YYOrderKSMapper yyOrderKSMapper;

    @Autowired
    protected RedisTemplate<Serializable, Serializable> businessRedis;
    @Autowired
    private RedisAction redisAction;

    private static final Logger logger = LogManager.getLogger(XmfTgServiceImpl.class);

    @Override
    public JSONObject kaiTuan(String param) {
        JSONObject out = this.out("0", "成功");
        String openId = "";
        boolean tuanGouLock = false;
        try {
            JSONObject in = JSONObject.fromObject(param);
            if (in.get("openId") == null || in.getString("openId").equals("")) {
                return out("4", "参数错误");
            }
            openId = in.getString("openId");
            // 通过openid 查看是否已经开团
            tuanGouLock = redisAction.setUserLock("tuangou" + openId);
            if (!tuanGouLock) {
                return out("4", "活动火爆，请稍后再试");
            }
            // 查询该团的状态
            XmfTgInfo xmfTgInfo = xmfTgInfoMapper.selectTgInfoByOpenIdAndStatus(openId, 1);
            List<XmfTuanYuanInfo> infos = new ArrayList<XmfTuanYuanInfo>();
            List<String> overTimes = null;
            String jiesuTime = "";
            XmfTuanYuanInfo info = new XmfTuanYuanInfo();
            if (null != xmfTgInfo) {
                // 判断 时间 是否过期
                jiesuTime = xmfTgInfo.getJiesuTime();  // 该团结束时间
                boolean resultStatus = this.getTgResultStatus(jiesuTime);
                if (!resultStatus) {
                    // 未过期
                    info.setFlag(xmfTgInfo.getFlag());  // 该团的标识
                    infos = this.xmfTuanYuanInfoMapper.selectList(info); // 查出团的所有成员信息
                    // 遍历 依次获取团员的头像信息
                    for (XmfTuanYuanInfo info2 : infos) {
                        // 获取他们的openid
                        String tyOpenId = info2.getOpenId();
                        String url2 = "http://192.168.5.6:39090/itpi/thirdPart/wxuser?openId=" + tyOpenId;
                        String res2 = HttpUtil.postUrl(url2, "wechat");
                        JSONObject result2 = JSONObject.fromObject(res2);
                        if (result2.getInt("subscribe") == 1) {
                            // 获取最新头像信息
                            String txInfo = result2.getString("headimgurl");
                            info2.setTxUrl(txInfo);
                        }
                    }
                    // 统计有多少参与了团
                    int count = this.xmfTuanYuanInfoMapper.selectCountByFlag(xmfTgInfo.getFlag());
                    overTimes = this.getOverTime(jiesuTime);
                    out.put("count", count);
                    out.put("data", infos);
                    out.put("overTime", overTimes);
                    return out;
                } else {
                    // 已过期
                    xmfTgInfo.setStatus(2); // 已过期
                    this.xmfTgInfoMapper.updateByPrimaryKeySelective(xmfTgInfo);  // 将团标识未失效
                    this.xmfTuanYuanInfoMapper.updateAllByFlag(xmfTgInfo.getFlag());  // 将团员信息中表标识为失效
                }
            }
            //  开团
            XmfTgInfo tgInfo = new XmfTgInfo();
            tgInfo.setOpenId(openId);
            tgInfo.setStatus(1);  // 有效
//            String url2 = "http://192.168.5.6:39090/itpi/thirdPart/wxuser?openId=" + openId;
//            String res2 = HttpUtil.postUrl(url2, "wechat");
//            JSONObject result2 = JSONObject.fromObject(res2);
//            logger.info("***开团团长头像信息***" + result2);
//            if (result2.getInt("subscribe") == 1) {
//                // 获取团长头像信息
//                String txUrl = result2.getString("headimgurl");
//                info.setTxUrl(txUrl);    // 头像信息
//            }
            info.setOpenId(openId);
            String flag = openId + "xmftg" + System.currentTimeMillis();
            info.setFlag(flag);
            info.setStatus(1);   //  1 代表有效，2 代表 无效
            info.setLocation(1); // 第1 个位置
            info.setExtend1(1 + "");  // 1,团长,2,团员
            Date date = new Date();
            String createTime = DateUtils.dateFormat.format(date);
            info.setCreateTime(createTime);
            tgInfo.setCreateTime(createTime);
            tgInfo.setFlag(flag);
            Calendar calendar = Calendar.getInstance();
            // 从缓存中获取 设置了团购设置是多少个小时
            int hour = 72;
            if (businessRedis.opsForValue().get("tuangouTime") != null) {
                hour = Integer.parseInt(businessRedis.opsForValue().get("tuangouTime") + "");
            } else {
                businessRedis.opsForValue().set("tuangouTime", 72 + "");
            }
            calendar.setTime(date);
            calendar.add(Calendar.HOUR_OF_DAY, hour);
            jiesuTime = DateUtils.dateFormat.format(calendar.getTime());
            tgInfo.setJiesuTime(jiesuTime);
            overTimes = this.getOverTime(jiesuTime);
            xmfTgInfoMapper.insertSelective(tgInfo);
            xmfTuanYuanInfoMapper.insertSelective(info);
            //  将团标识放入 缓存中
            redisAction.incrbyTimeOut(flag, hour * 3600).intValue();
            infos.add(info);
            out.put("count", 1);
            out.put("data", infos);
            out.put("overTime", overTimes);
        } catch (Exception e) {
            e.printStackTrace();
            return out("4", "活动火爆，请稍后再试");
        } finally {
            if (tuanGouLock) {
                try {
                    redisAction.delUserLock("tuangou" + openId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return out;
    }

    // 通过团购结束时间 判断团购是否过期
    private boolean getTgResultStatus(String jiesuTime) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d1 = df.parse(jiesuTime);
        return new Date().getTime() - d1.getTime() > 0;  // true 已过期，false 未过期
    }

    private List<String> getOverTime(String jiesuTime) {
        List<String> overTimes = new ArrayList<String>();
        String year = jiesuTime.substring(0, 4);
        String month = jiesuTime.substring(5, 7);
        String day = jiesuTime.substring(8, 10);
        String hour = jiesuTime.substring(11, 13);
        String min = jiesuTime.substring(14, 16);
        overTimes.add(year);
        overTimes.add(month);
        overTimes.add(day);
        overTimes.add(hour);
        overTimes.add(min);
        return overTimes;
    }

    @Override
    public JSONObject canTuan(String param) {
        JSONObject out = this.out("0", "成功");
        //    boolean canTuanLock = false;
        boolean openIdLock = false;
        String flag = "";
        String openId = "";
        try {
            JSONObject in = JSONObject.fromObject(param);
            flag = in.getString("flag");        // 团标识
            openId = in.getString("openId");    // 自己的openid
            openIdLock = redisAction.setUserLock("xmftg" + openId);
            if (!openIdLock) {
                return out("4", "活动火爆，请稍后再试");
            }
            // 查询该团的状态
            XmfTgInfo xmfTgInfo = xmfTgInfoMapper.selectTgInfoByFlag(flag);
            if (1 == xmfTgInfo.getStatus()) {
                // 团有效，查看是否已经过期
                boolean resultStatus = this.getTgResultStatus(xmfTgInfo.getJiesuTime());
                if (resultStatus) {
                    // 团已过期
                    out.put("status", "2");
                    xmfTgInfo.setStatus(2); // 团已失效
                    // 更改团状态，更改团员信息表中的团状态
                    this.xmfTgInfoMapper.updateByPrimaryKeySelective(xmfTgInfo);
                    this.xmfTuanYuanInfoMapper.updateAllByFlag(flag);
                } else {
                    out.put("status", "1"); // 有效
                    // 1,判断该用户是否已经参加过该团
                    XmfTuanYuanInfo existXmfTuan = this.xmfTuanYuanInfoMapper.selectByFlagAndOpenId(flag, openId);
                    if (existXmfTuan != null) {
                        // 已经参加过该团购，不可再次参加
                        out.put("type", "2");
                        return out;
                    } else {
                        // 未参加过该团,
                        // 2,判断该用户是否已经参加过其他团
                        XmfTuanYuanInfo info = new XmfTuanYuanInfo();
                        info.setExtend1("2");  // 团的成员
                        info.setOpenId(openId);
                        List<XmfTuanYuanInfo> xmfTuanYuanInfos = this.xmfTuanYuanInfoMapper.selectList(info);
                        if (xmfTuanYuanInfos.size() >= 1) {
                            //      1,参加过其他团,循环遍历
                            for (XmfTuanYuanInfo info1 : xmfTuanYuanInfos) {
                                if (info1.getStatus() == 1) {
                                    // 团有效
                                    XmfTgInfo xmfTgInfo1 = this.xmfTgInfoMapper.selectTgInfoByFlag(info1.getFlag());  // 查询到 改团的信息
                                    // 判断改团团购时间 是否过期
                                    boolean result = this.getTgResultStatus(xmfTgInfo1.getJiesuTime());
                                    if (result) {
                                        // 如果团已过期
                                        // 更改团状态，更改团员信息表中的团状态
                                        xmfTgInfo1.setStatus(2); // 将团状态改为 失效
                                        this.xmfTgInfoMapper.updateByPrimaryKeySelective(xmfTgInfo1);
                                        this.xmfTuanYuanInfoMapper.updateAllByFlag(info1.getFlag());
                                    } else {
                                        //TODO 参加过其他团 ，不可以再次参加，显示该团的所有信息
                                        out.put("type", "3"); // 已经参加过其他团购活动
                                        return out;
                                    }
                                }
                            }
                        }

                        int joinCount = Integer.parseInt(redisAction.getString(flag));
                        if (joinCount > 4) {
                            // 最多5个人参加
                            out.put("type", "4"); // 人数已满不可参加
                            return out;
                        }
                        logger.info("===========参团人数======+" + flag + "+=======" + joinCount);
                        //    int totalCount = this.xmfTuanYuanInfoMapper.selectCountByFlag(flag);
//                        if (totalCount >= 5) {
//                            out.put("type", "4"); // 人数已满不可参加
//                            return out;
//                        } else {
//                            canTuanLock = redisAction.setUserLock("xmfcantuan" + flag);
//                            if (!canTuanLock) {
//                                return out("4", "活动火爆，请稍后再试");
//                            }
                        // 对团标识进行加锁操作
                        // 控制参加人数
                        //      totalCount = this.xmfTuanYuanInfoMapper.selectCountByFlag(flag);
//                            if (totalCount >= 5) {
//                                // 人数已满，不可参加
//                                out.put("type", "4");
//                                return out;
//                            } else {
                        out.put("type", "1"); // 人数未满，可参加
                        XmfTuanYuanInfo tuanYuanInfo = new XmfTuanYuanInfo();
                        tuanYuanInfo.setStatus(1);  // 团有效
                        tuanYuanInfo.setExtend1("2");  // 团成员
                        tuanYuanInfo.setOpenId(openId);
                        tuanYuanInfo.setFlag(flag);
                        tuanYuanInfo.setLocation(joinCount);  // 位置
                        tuanYuanInfo.setCreateTime(DateUtils.dateFormat.format(new Date()));
                        String url2 = "http://192.168.5.6:39090/itpi/thirdPart/wxuser?openId=" + openId;
                        String res2 = HttpUtil.postUrl(url2, "wechat");
                        JSONObject result2 = JSONObject.fromObject(res2);
                        if (result2.getInt("subscribe") == 1) {
                            // 获取最新头像信息
                            String txInfo = result2.getString("headimgurl");
                            tuanYuanInfo.setTxUrl(txInfo);
                        }
                        joinCount = redisAction.incr(flag).intValue();
                        if (joinCount > 5) {
                            // 第6个人进来时，直接返回
                            out.put("type", "4"); // 人数已满不可参加
                            return out;
                        }
                        this.xmfTuanYuanInfoMapper.insertSelective(tuanYuanInfo);
                    }
                }
                //    }

                //     }
            } else {
                // 团失效
                out.put("status", "2");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return out("4", "活动火爆，稍后再试");
        } finally {
            try {
                if (openIdLock)
                    redisAction.delUserLock("xmftg" + openId);
//                if (canTuanLock)
//                    redisAction.delUserLock("" + flag);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return out;
    }


    @Override
    public JSONObject showAllMembers(String param) {
        JSONObject out = this.out("0", "成功");
        try {
            JSONObject in = JSONObject.fromObject(param);
            String flag = in.getString("flag");        // 团标识

            XmfTgInfo existXmfTgInfo = this.xmfTgInfoMapper.selectTgInfoByFlag(flag);
            boolean resultStatus = this.getTgResultStatus(existXmfTgInfo.getJiesuTime());
            if (resultStatus) {
                // 已过期
                out.put("status", "2"); // 团购已失效
            } else {
                out.put("status", "1"); // 团购有效
                List<String> overTimes = this.getOverTime(existXmfTgInfo.getJiesuTime());
                XmfTuanYuanInfo tuanYuanInfo = new XmfTuanYuanInfo();
                tuanYuanInfo.setFlag(flag);
                // 查询所有的团购成员信息
                List<XmfTuanYuanInfo> infos = this.xmfTuanYuanInfoMapper.selectList(tuanYuanInfo);
                int count = this.xmfTuanYuanInfoMapper.selectCount(tuanYuanInfo);
                out.put("count", count);
                for (XmfTuanYuanInfo tuanYuan : infos) {
                    // 获取他们的openid
                    String tyOpenId = tuanYuan.getOpenId();
                    String url2 = "http://192.168.5.6:39090/itpi/thirdPart/wxuser?openId=" + tyOpenId;
                    String res2 = HttpUtil.postUrl(url2, "wechat");
                    JSONObject result2 = JSONObject.fromObject(res2);
                    logger.info("***团购所有头像信息***" + result2);
                    if (result2.getInt("subscribe") == 1) {
                        // 获取最新头像信息
                        String txInfo = result2.getString("headimgurl");
                        tuanYuan.setTxUrl(txInfo);
                    }
                }
                out.put("overTime", overTimes);
                out.put("data", infos);

            }
        } catch (Exception e) {
            e.printStackTrace();
            return out("4", "活动火爆，稍后再试");
        }
        return out;
    }

    @Override
    public JSONObject queryOrderByOpenId(String json) {
        JSONObject out = this.out("0", "成功");
        JSONObject in = JSONObject.fromObject(json);
        try {
            KdyyLog kdyyLog = new KdyyLog();
            YYOrderKS yyOrderKS = new YYOrderKS();
            String openId = in.getString("openId");
            yyOrderKS.setSource(13); // 嗨卡公共小蜜蜂
            // 渠道 3 公共小蜜蜂
            kdyyLog.setSource(3);
            yyOrderKS.setExtend4(openId);
            kdyyLog.setExtend4(openId);  // openId
            // 订单状态 status 0 全部，1,代处理，2,成功，3,取消 ,4 进行中
            List<XmfResult> results = new ArrayList<XmfResult>();
            List<KdyyLog> orders = this.kdyyLogMapper.selectList(kdyyLog);
            List<YYOrderKS> yyOrders = this.yyOrderKSMapper.selectList(yyOrderKS);
            // 遍历宽带预约
            for (KdyyLog log : orders) {
                XmfResult xmfResult = new XmfResult();
                xmfResult.setUserName(log.getUserName());
                xmfResult.setCreateTime(log.getCreateTime());
                xmfResult.setTaocanName(log.getTaocanName());
                xmfResult.setMobile(log.getMobile());
                if (log.getStatus() == 1 || log.getStatus() == 2) {
                    xmfResult.setStatus("进行中");
                } else if (log.getStatus() == 3) {
                    xmfResult.setStatus("取消");
                } else if (log.getStatus() == 4) {
                    xmfResult.setStatus("已竣工");
                }
                xmfResult.setBeiZhu("");
                results.add(xmfResult);
            }
            //  遍历 嗨卡
            for (YYOrderKS ks : yyOrders) {
                XmfResult xmfResult = new XmfResult();
                xmfResult.setUserName(ks.getUserName());
                xmfResult.setCreateTime(ks.getCreateTime());
                xmfResult.setTaocanName(ks.getTaocanName());
                xmfResult.setMobile(ks.getMobile());
                if (ks.getStatus() == 1) {
                    xmfResult.setStatus("进行中");
                } else if (ks.getStatus() == 3) {
                    xmfResult.setStatus("取消");
                } else if (ks.getStatus() == 2) {
                    xmfResult.setStatus("成功");
                }
                xmfResult.setBeiZhu("");
                results.add(xmfResult);
            }
            out.put("data", results);
            System.out.println("*********团购订单结果********" + results);
        } catch (Exception e) {
            e.printStackTrace();
            return out("4", "活动火爆，稍后再试");
        }
        return out;
    }

    @Override
    public JSONObject holdInfo(String param) {
        JSONObject out = this.out("0", "成功");
        boolean isLock = false;
        String extend4 = "";
        try {
            JSONObject in = JSONObject.fromObject(param);
            // 校验身份证号码
            String personId = in.getString("personId");
            String openId = in.getString("openId"); // 团长openid
            extend4 = in.getString("extend4"); // 自己openId
            String reg = "\\d{14}\\w|\\d{17}\\w";
            if (!personId.matches(reg)) {
                return out("4", "身份证格式不正确");
            }
            String mobile = in.getString("mobile");
            if (!mobile.matches("1\\d{10}")) {
                return out("4", "手机号码格式不正确");
            }
            // 相同openId用户 同时只能一个进入
            isLock = redisAction.setUserLock("xmftg" + extend4);
            if (!isLock) {
                return out("4", "活动火爆，请稍后再试");
            }
            String fixArea = in.getString("fixArea"); // 区域
            KdyyLog log = new KdyyLog();
            // 通过手机号码 非苏州电信用户 不调用翼支付接口
            Integer taocanId = in.getInt("taocanId");
            XmfTcInfo tcInfo = this.xmfTcInfoMapper.selectByPrimaryKey(taocanId);
            log.setTaocanName(tcInfo.getTaocanName()); // 套餐名称
            log.setMsg(tcInfo.getYjMoney());
            log.setUserName(in.getString("userName"));
            log.setPersonId(personId);
            log.setMobile(mobile);
            log.setFixArea(fixArea); // 安装区域
            log.setAddress(in.getString("address"));
            log.setDxMobile(in.getString("dxMobile"));
            log.setExtend1(openId);  // 推荐人openid
            log.setSource(3); // 公共小蜜蜂渠道
            log.setStatus(1);  // 待处理
            log.setExtend4(extend4); //  用户自己的openid
            log.setCreateTime(DateUtils.dateFormat.format(new Date()));
            kdyyLogMapper.insertSelective(log);
            // 不是指定区域
        } catch (Exception e) {
            e.printStackTrace();
            return out("4", "活动火爆，请稍后再试！");
        } finally {
            try {
                if (isLock) {
                    redisAction.delUserLock("xmftg" + extend4);
                }
            } catch (Exception e) {
            }
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
