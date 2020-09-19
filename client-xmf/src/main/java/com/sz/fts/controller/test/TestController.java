package com.sz.fts.controller.test;

import com.sz.fts.service.yuyuekunsan.YuYueLHService;
import com.sz.fts.utils.BaseRquestAndResponse;
import com.sz.fts.utils.CommonUtil;
import com.sz.fts.utils.HttpUtil;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Random;

/**
 * @author 征华兴
 * @date 下午 3:51  2019/3/27 0027
 * @Copyright 江苏鸿信系统集成有限公司 All rights reserved
 */
@Controller
@RequestMapping("test")
public class TestController extends BaseRquestAndResponse {
    private static Random random = new Random();
    @Autowired
    private YuYueLHService yuYueLHService;
    //  066f30ff6ad1b4148e4a6f8b8262ca26     5e01223cc793593a9111a274b2ea8a26
    private static final String SECRET = "066f30ff6ad1b4148e4a6f8b8262ca26";
    private static final String APPID = "wxe5930e19f587de32";



    @RequestMapping("getCode2")
    public ModelAndView getCode() {
        getResponse().setHeader("Access-Control-Allow-Origin", "*");
        try {
            String URL = "http://www.118114sz.com.cn/xmf/test/test1";
            String jumpURL = "redirect:https://open.weixin.qq.com/connect/oauth2/authorize?appid=wxe5930e19f587de32&redirect_uri=" + URLEncoder.encode(URL) + "&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect";
            //跳转路径
            System.out.println("===========" + jumpURL);
            return new ModelAndView(jumpURL);
        } catch (Exception e) {
            e.printStackTrace();
            return new ModelAndView("http://www.baidu.com");
        }
    }
    @RequestMapping("test1")
    public void Test1() {
        getResponse().setHeader("Access-Control-Allow-Origin", "*");
        try {
            System.out.println("----------111111111111111-------");
            String code = getRequest().getParameter("code");
            System.out.println("-----------------"+code);
            String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + APPID + "&secret=" + SECRET + "&code=" + code + "&grant_type=authorization_code";
            String result = HttpUtil.postUrl(url, "wechat");
            System.out.println("-------------------------result-------------------" + result);
            JSONObject out = JSONObject.fromObject(result);
            CommonUtil.printOutMsg(getRequest(), getResponse(), out);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("-------------openid--------------" + e);
        }


    }

    @RequestMapping("testPayMoney")
    public void testPayMoney() {
        getResponse().setHeader("Access-Control-Allow-Origin", "*");
        try {
            Enumeration<String> parameterNames = getRequest().getParameterNames();
            while (parameterNames.hasMoreElements()) {
                String s = parameterNames.nextElement();
                System.out.println(s + ":" + getRequest().getParameter(s));
            }
            JSONObject out = new JSONObject();
            out.put("result", "0");
            out.put("resultMsg", "success");

            CommonUtil.printOutMsg(getRequest(), getResponse(), out);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("-------------openid--------------" + e);
        }


    }

    @RequestMapping("pic")
    public void pic(@RequestParam String method) {
        try {
            switch (method) {
                case "capatch":
                    System.out.println("222222222222");
                    this.getcaptcha(getRequest(), getResponse());
                    break;
                default:
                    System.out.println("1111111111111");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @RequestMapping("test10086")
//    public ModelAndView Test3() {
//
//        Comparator<Integer> comparator = Integer::compare;
//        int value = comparator.compare(100, 200);
//        System.out.println(value);
//        if (value < 0) {
//            return new ModelAndView("redirect:http://www.baidu.com");
//        } else {
//            return new ModelAndView("redirect:http://www.qq.com");
//        }
//
//
//    }

    private void getcaptcha(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //JSONObject outcome = new JSONObject();
        System.out.println("========123===========");
        int codeLength = 6;
        int mixTimes = 50;
        Color bgColor = getRandColor(200, 250);
        Color bfColor = new Color(0, 0, 0);
        boolean ifRandomColor = true;
        boolean ifMixColor = false;

        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0L);

        int width = 13 * codeLength + 6;
        int height = 24;
        BufferedImage image = new BufferedImage(width, height, 1);
        Graphics g = image.getGraphics();
        g.setColor(bgColor);
        g.fillRect(0, 0, width, height);
        g.setFont(new Font("Times New Roman", 1, 17));
        g.setColor(new Color(33, 66, 99));
        g.drawRect(0, 0, width - 1, height - 1);
        System.out.println("========123456===========");
        g.setColor(getRandColor(160, 200));
        for (int i = 0; i < mixTimes * codeLength / 10; ++i) {
            if (ifMixColor) {
                g.setColor(getRandColor(160, 200));
            }
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int xl = random.nextInt(12);
            int yl = random.nextInt(12);
            g.drawLine(x, y, x + xl, y + yl);
        }
        //将验证码从纯数字修改为数字加字母 2018-9-19
        String sRand = "";
		/*for (int i = 0; i < codeLength; ++i) {
			String rand = String.valueOf(random.nextInt(10));
			sRand = sRand + rand;

			if (ifRandomColor)
				g.setColor(getRandColor(20, 110, 0));
			else {
				g.setColor(bfColor);
			}
			g.drawString(rand, 13 * i + 6, 16);
		}*/
        //表示生成几位随机数
        int ischar = 0;
        int isnum = 0;
        for (int i = 0; i < codeLength; i++) {
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            if (ischar >= 2) {
                charOrNum = "num";
            }
            if (isnum >= 4) {
                charOrNum = "char";
            }
            //输出字母还是数字
            String rand = "";
            if ("char".equalsIgnoreCase(charOrNum)) {
                //随机输出是大写字母还是小写字母
                int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
                rand += (char) (random.nextInt(26) + temp);
                ischar++;
            } else if ("num".equalsIgnoreCase(charOrNum)) {
                rand += String.valueOf(random.nextInt(10));
                isnum++;
            }
            sRand = sRand + rand;
            if (ifRandomColor) {
                g.setColor(getRandColor(20, 110, 0));
            } else {
                g.setColor(bfColor);
            }
            g.drawString(rand, 13 * i + 6, 16);
        }
        System.out.println("========123456===========");
        request.getSession().setAttribute("rand", sRand);
        g.dispose();
        String code = (String) request.getSession().getAttribute("rand");
        System.out.println("========123456===========" + code);
        ImageIO.write(image, "PNG", response.getOutputStream());
    }

    @RequestMapping("test2")
    public void Test2(@RequestBody String json) {
        JSONObject in = JSONObject.fromObject(json);
        System.out.println("--------------------------22-------------------");
        JSONObject out = new JSONObject();
        out.put("orderNumber", in.getString("orderNumber"));
        String url = "http://app1.118114sz.com/zjfszfts/homeNetwork/completePay.do";
        try {
            String result1 = HttpUtil.postUrl(url, out.toString());
            System.out.println("--------------------------更新状态-------------------" + result1);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("-------------22异常了--------------" + e);
        }
    }

    @RequestMapping("test3")
    public void Test3(@RequestBody String json) throws Exception {
        JSONObject in = JSONObject.fromObject(json);
        JSONObject out = new JSONObject();
        out.put("orderId", in.getString("orderId"));
        out.put("status", "1");
        String url = "http://app1.118114sz.com/szfts/lhZero/xiuOrder.do";
        try {
            String result1 = HttpUtil.postUrl(url, out.toString());
            System.out.println("--------------------------更新状态-------------------" + result1);

        } catch (Exception e) {
            System.out.println("-------------33异常了--------------" + e);
        }

    }

    @RequestMapping("test4")
    public void Test4(@RequestBody String json) {
        JSONObject in = JSONObject.fromObject(json);
        System.out.println("--------------------------44-------------------");
        JSONObject out = new JSONObject();
        out.put("orderId", in.getString("orderId"));
        out.put("status", "1");
        String url = "http://127.0.0.1:16677/szfts/lhZero/xiuOrder.do";
        try {

            String result1 = HttpUtil.postUrl(url, out.toString());
            System.out.println("--------------------------更新状态-------------------" + result1);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("-------------44异常了--------------" + e);
        }
    }

    @RequestMapping("test5")
    public void Test5() {
        //  JSONObject in =  JSONObject.fromObject();
        System.out.println("--------------------------55-------------------");
        JSONObject out = new JSONObject();
        String url = "http://127.0.0.1:16677/szfts/yinyeting/queryCount.do";
        try {
            String result1 = HttpUtil.postUrl(url, out.toString());
            System.out.println("--------------------------更新状态-------------------" + result1);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("-------------55异常了--------------" + e);
        }
    }

    @RequestMapping("test6")
    public void Test6(@RequestBody String json) {
        JSONObject in = JSONObject.fromObject(json);
        System.out.println("--------------------------66-------------------");
        JSONObject out = new JSONObject();
        out.put("orderId", in.getString("orderId"));
        out.put("status", "1");
        String url = "http://127.0.0.1:8168/xmf/test/xiu";
        try {
            String result1 = HttpUtil.postUrl(url, out.toString());
            System.out.println("--------------------------更新状态-------------------" + result1);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("-------------66异常了--------------" + e);
        }
    }

    @RequestMapping("test8")
    public void Test8(@RequestBody String json) {
        JSONObject in = JSONObject.fromObject(json);
        System.out.println("--------------------------66-------------------");
//        JSONObject out = new JSONObject();
//        out.put("orderId", in.getString("orderId"));
//        out.put("status", "1");
        String url = "http://127.0.0.1:8168/xmf/test/xiu";
        String base = "{\"orderId\":\"" + in.getString("orderId") + "\",\"status\":\"" + 1 + "\"}";
        try {
            String result1 = HttpUtil.postUrl(url, base);
            System.out.println("--------------------------更新状态-------------------" + result1);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("-------------66异常了--------------" + e);
        }
    }

    @RequestMapping("test7")
    public void Test7() {
        System.out.println("--------------------------77-------------------");
        JSONObject out = new JSONObject();
        out.put("orderId", "123");
        out.put("status", "1");
        String url = "http://127.0.0.1:8168/xmf/test/xiu";
        try {
            String result1 = HttpUtil.postUrl(url, out.toString());
            System.out.println("--------------------------更新状态-------------------" + result1);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("-------------77异常了--------------" + e);
        }
    }

    private Color getRandColor(int fc, int bc) {
        return getRandColor(fc, bc, fc);
    }

    private Color getRandColor(int fc, int bc, int interval) {
        if (fc > 255) {
            fc = 255;
        }
        if (bc > 255) {
            bc = 255;
        }
        int r = fc + random.nextInt(bc - interval);
        int g = fc + random.nextInt(bc - interval);
        int b = fc + random.nextInt(bc - interval);
        return new Color(r, g, b);
    }


    @RequestMapping("xiu")
    public void Xiu(@RequestBody String json) {
        getResponse().setHeader("Access-Control-Allow-Origin", "*");
        try {
            System.out.println("--------------------------xiuxiuxiu-------------------" + json);
            json = URLDecoder.decode(json, "UTF-8");
            json = json.split("=")[0];
            System.out.println("------解码后-----------" + json);
            JSONObject out = this.yuYueLHService.xiuOrder(JSONObject.fromObject(json));
            CommonUtil.printOutMsg(getRequest(), getResponse(), out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("xiuTwo")
    public void XiuTwo(@RequestBody String json) {
        getResponse().setHeader("Access-Control-Allow-Origin", "*");
        try {
            System.out.println("--------------------------xiuxiuxiu-------------------" + json);
            json = URLDecoder.decode(json, "UTF-8");
            //   json = json.split("=")[0];
            System.out.println("------解码后-----------" + json);
            JSONObject out = this.yuYueLHService.xiuOrder(JSONObject.fromObject(json));
            CommonUtil.printOutMsg(getRequest(), getResponse(), out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
