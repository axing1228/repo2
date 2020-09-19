package com.sz.fts.controller.zjf;

import com.sz.fts.service.zjf.HomeNetworkService;
import com.sz.fts.utils.BaseRquestAndResponse;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.PrintWriter;

/**
 * @NAME HomeNetworkController
 * @AUTHOR 朱建峰
 * @DATE 2019/5/6 0006 下午 2:51
 * @DESCRIPTION 家庭网秒杀
 */
@Controller
@RequestMapping("homeNetwork")
public class HomeNetworkController extends BaseRquestAndResponse {
    @Autowired
    private HomeNetworkService homeNetworkService;

    @RequestMapping("displayOrder.do")
    public void displayOrder(@RequestParam MultipartFile[] files) throws Exception{
        JSONObject out = homeNetworkService.displayOrder(files,getRequest(),getResponse());
        PrintWriter pw = getResponse().getWriter();
        pw.write(out.toString());
        pw.flush();
        pw.close();
    }


}
