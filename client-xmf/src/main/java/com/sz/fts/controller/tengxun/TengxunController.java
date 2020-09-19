package com.sz.fts.controller.tengxun;


import com.sz.fts.service.tengxun.TengxunService;
import com.sz.fts.utils.BaseRquestAndResponse;
import com.sz.fts.utils.CommonUtil;
import net.sf.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author 征华兴
 * @date 下午 1:56  2018/11/6 0006
 * @Copyright 江苏鸿信系统集成有限公司 All rights reserved
 */
@Controller
@RequestMapping("tengxun")
public class TengxunController extends BaseRquestAndResponse {

    private static final Logger logger = LogManager.getLogger(TengxunController.class);


    @Autowired
    private TengxunService tengxunService;

    /**
     * 群发短信
     */
    @RequestMapping(value = "sendCodeAllTest.do",method = RequestMethod.POST)
    public void sendCodeAll() throws Exception {
        getResponse().setHeader("Access-Control-Allow-Origin", "*");
        JSONObject out = tengxunService.sendCodeAll();
        CommonUtil.printEncryptMsg(getRequest(), getResponse(), out);
    }

    /**
     * 开通
     */
    @RequestMapping("open.do")
    public void openAndBindTengxun() throws Exception {

        getResponse().setHeader("Access-Control-Allow-Origin", "*");
        JSONObject json = CommonUtil.outDecryptMsg(getRequest());
        logger.info("-腾讯开通参数-" + json);
        JSONObject out = tengxunService.openAndBindTengxun(json.toString());
        CommonUtil.printEncryptMsg(getRequest(), getResponse(), out);

    }

    /**
     * 关闭
     */
    @RequestMapping("close.do")
    public void closeTengxun(@RequestBody String json) throws Exception {
        getResponse().setHeader("Access-Control-Allow-Origin", "*");
        logger.info("-腾讯关闭参数-" + json);
        JSONObject out = tengxunService.closeTengxun(json);
        CommonUtil.printEncryptMsg(getRequest(), getResponse(), out);
    }

    /**
     * 导出记录
     */
    @RequestMapping("download.do")
    public void download() throws Exception {
        getResponse().setHeader("Access-Control-Allow-Origin", "*");
        this.tengxunService.download(getRequest(), getResponse());
    }

}
