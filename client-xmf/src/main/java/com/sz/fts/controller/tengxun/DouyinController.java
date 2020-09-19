package com.sz.fts.controller.tengxun;


import com.sz.fts.service.tengxun.DouyinService;
import com.sz.fts.utils.BaseRquestAndResponse;
import com.sz.fts.utils.CommonUtil;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.UnsupportedEncodingException;

/**
 * @author 征华兴
 * @date 下午 1:31  2018/12/24 0024
 * @Copyright 江苏鸿信系统集成有限公司 All rights reserved
 */
@Controller
@RequestMapping("douyin")
public class DouyinController extends BaseRquestAndResponse {

    @Autowired
    private DouyinService douyinService;


    @RequestMapping(value = "save.do")
    public void save() throws Exception {
        getResponse().setHeader("Access-Control-Allow-Origin", "*");
        JSONObject jsonObject = CommonUtil.outDecryptMsg(getRequest());
        JSONObject out = douyinService.hold(jsonObject.toString());
        CommonUtil.printEncryptMsg(getRequest(), getResponse(), out);
    }

    /**
     * 导出领取记录
     *
     * @param
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/downloadLog.do")
    public void downloadGift() throws Exception {
        getResponse().setHeader("Access-Control-Allow-Origin", "*");
        this.douyinService.downloadLog(getRequest(), getResponse());
    }

}
