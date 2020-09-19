package com.sz.fts.controller.xmftg;

import com.sz.fts.service.xmftg.XmfTgService;
import com.sz.fts.utils.BaseRquestAndResponse;
import com.sz.fts.utils.CommonUtil;
import net.sf.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * @author 征华兴
 * @date 下午 2:31  2018/5/29 0029
 * @Copyright 江苏鸿信系统集成有限公司 All rights reserved
 */
@Controller
@RequestMapping(value = "/xmfTg")
public class XmfTgController extends BaseRquestAndResponse {

    @Autowired
    private XmfTgService xmfTgService;

    private static final Logger logger = LogManager.getLogger(XmfTgController.class);

    /**
     * 开团
     *
     * @param json
     * @throws Exception
     */
    @RequestMapping(value = "/kaiTuan.do")
    public void kaiTuan(@RequestBody String json) throws Exception {
        getResponse().setHeader("Access-Control-Allow-Origin", "*");
        logger.info("--------------开团传入参数-----------：" + json);
        JSONObject out = this.xmfTgService.kaiTuan(this.getParam(json));
        CommonUtil.printOutMsg(getRequest(), getResponse(), out);
    }

    /**
     * 参团信息
     *
     * @param json
     * @throws Exception
     */
    @RequestMapping(value = "/canTuan.do")
    public void canTuan(@RequestBody String json) throws Exception {
        getResponse().setHeader("Access-Control-Allow-Origin", "*");
        logger.info("--------------参团传入参数-----------：" + json);
        JSONObject out = this.xmfTgService.canTuan(this.getParam(json));
        CommonUtil.printOutMsg(getRequest(), getResponse(), out);
    }

    /**
     * 显示团所有成员信息
     *
     * @param json
     * @throws Exception
     */
    @RequestMapping(value = "/showAllMember.do")
    public void showAllMembers(@RequestBody String json) throws Exception {
        getResponse().setHeader("Access-Control-Allow-Origin", "*");
        logger.info("--------------显示所有参团成员信息-----------：" + json);
        JSONObject out = this.xmfTgService.showAllMembers(this.getParam(json));
        CommonUtil.printOutMsg(getRequest(), getResponse(), out);
    }

    /**
     * 提交订单
     *
     * @param json
     */
    @RequestMapping(value = "/saveInfo.do")
    public void saveInfo(@RequestBody String json) throws Exception {
        getResponse().setHeader("Access-Control-Allow-Origin", "*");
        logger.info("--------------团购传入参数-----------：" + json);
        JSONObject out = this.xmfTgService.holdInfo(this.getParam(json));
        CommonUtil.printOutMsg(getRequest(), getResponse(), out);
    }

    /**
     * 用户通过openid，查询自己的订单信息
     *
     * @param json
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping("queryOrder.do")
    public void queryOrder(@RequestBody String json) throws Exception {
        getResponse().setHeader("Access-Control-Allow-Origin", "*");
        String param = getParam(json);
        JSONObject out = xmfTgService.queryOrderByOpenId(param);
        CommonUtil.printOutMsg(getRequest(), getResponse(), out);
    }

    /**
     *  解析json数据
     */
    private String getParam(String param) throws UnsupportedEncodingException {
        String out = URLDecoder.decode(param, "UTF-8");
        out = out.replace("=", "\":\"");
        out = out.replace("&", "\",\"");
        out = "{\"" + out + "\"}";
        return out;
    }


}

