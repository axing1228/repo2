package com.sz.fts.controller.zjf;

import com.sz.fts.bean.zjf.PersonalConstants;
import com.sz.fts.service.zjf.WeichatService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * @NAME WeichatController
 * @AUTHOR 朱建峰
 * @DATE 2019/7/18 0018 下午 1:25
 * @DESCRIPTION 针对微信controller
 */
@Controller
@RequestMapping("weichat")
public class WeichatController {
    private static final Logger logger = LogManager.getLogger(WeichatController.class);

    @Autowired
    private WeichatService weichatService;

    //获取微信票据
    @RequestMapping("getCode.do")
    public void firstRequest(HttpServletResponse response) throws IOException {
        String redirect_uri = URLEncoder.encode("http://app1.118114sz.com/zjfszfts/weichat/displayOpenId.do?www=123&ccc=12444&ddd=2423&eee=342342", "UTF-8");
        String sql = "https://open.weixin.qq.com/connect/oauth2/authorize?" +
                "appid=" + PersonalConstants.APPID +
                "&redirect_uri=" + redirect_uri +
                "&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect";
        response.sendRedirect(sql);
    }


    @ResponseBody
    @RequestMapping("displayOpenId.do")
    public String displayOpenId(HttpServletRequest request) throws IOException {
        return weichatService.getOpenId(request.getParameter("code"));
    }
}
