package com.sz.fts.impl.zjf;

import com.sz.fts.bean.zjf.ActivityOrder;
import com.sz.fts.bean.zjf.AllActivityUserInfo;
import com.sz.fts.dao.zjf.HomeNetworkMapper;
import com.sz.fts.service.zjf.HomeNetworkService;
import com.sz.fts.utils.WechatUpload;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @NAME HomeNetworkServiceImpl
 * @AUTHOR 朱建峰
 * @DATE 2019/5/6 0006 下午 2:52
 * @DESCRIPTION 家庭网秒杀
 */
@Service
public class HomeNetworkServiceImpl implements HomeNetworkService {
    private static final String ACTIVITY_KEY = "zjf008";
    private static final String BASE_IMG_PATH = "/home/hongxin/apache-client-xmf/webapps/xmf/pages/activityImg/";
    private static final String BASE_IMG_HTML = "http://www.118114sz.com.cn/xmf/pages/activityImg/";
    private static final Logger logger = LogManager.getLogger(HomeNetworkServiceImpl.class);

    @Resource
    private HomeNetworkMapper homeNetworkMapper;

    @Override
    public JSONObject displayOrder(MultipartFile[] files, HttpServletRequest request,
                                   HttpServletResponse response) {
        JSONObject out = new JSONObject();
        String id = request.getParameter("id");
        String broadbandNumber = request.getParameter("broadbandNumber");
        String identityCard = request.getParameter("identityCard");
        String evaluationContent = request.getParameter("evaluationContent");
        String result = "";
        if(StringUtils.isEmpty(id)){
            result += "id,";
        }
        if(StringUtils.isEmpty(broadbandNumber)){
            result += "broadbandNumber,";
        }
        if(StringUtils.isEmpty(identityCard)){
            result += "identityCard,";
        }
        if(StringUtils.isEmpty(evaluationContent)){
            result += "evaluationContent,";
        }
        if(StringUtils.isNotEmpty(result)){
            String substring = result.substring(0, result.length() - 1);
            out.put("flag", 9);
            out.put("msg", "未传入参数：" + substring);
            return out;
        }

        ActivityOrder activityOrder = homeNetworkMapper.selectOrderByActivityKeyAndId(ACTIVITY_KEY, id);
        if(activityOrder == null) {
            out.put("flag", 0);
            out.put("msg", "尚未参与秒杀");
            return out;
        }

        AllActivityUserInfo allActivityUserInfo = homeNetworkMapper.selecUsertById(id);
        if(allActivityUserInfo != null){
            if(StringUtils.isNotEmpty(allActivityUserInfo.getExtend3())){
                out.put("flag", 0);
                out.put("msg", "已晒单，请勿重复晒单");
                return out;
            }
        }
        String path = uploadImg(files, ACTIVITY_KEY);
        Map<String, Object> map = new HashMap<>();
        map.put("broadbandNumber", broadbandNumber);
        map.put("identityCard", identityCard);
        map.put("evaluationContent", evaluationContent);
        map.put("imgPath", path);
        map.put("id", id);
        homeNetworkMapper.updateForEvaluation(map);
        allActivityUserInfo = homeNetworkMapper.selecUsertById(id);
        out.put("flag", 1);
        out.put("msg", "评价成功");
        out.put("data", allActivityUserInfo);
        return out;

    }


    public  String uploadImg(MultipartFile[] files, String activityKey){
        long current = System.currentTimeMillis();
        String directory = BASE_IMG_PATH + activityKey + "/" + current;
        String  baseHtml = BASE_IMG_HTML + activityKey + "/" + current + "/";
        StringBuilder path = new StringBuilder();
        File file = new File(BASE_IMG_PATH + activityKey);
        try{
            if(!file.exists()){
                file.mkdirs();
            }
            for(MultipartFile one : files){
                WechatUpload.convertFile(one.getInputStream(), directory, one.getOriginalFilename());
                path.append(baseHtml + one.getOriginalFilename() + ";");
            }
        }catch (IOException e){
            logger.info("上传图片异常：" + e.getLocalizedMessage());
        }
        String result = path.toString();
        return result.substring(0,result.length() - 1);
    }




}
