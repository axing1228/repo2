package com.sz.fts.utils.liuliang;

import com.alibaba.fastjson.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author 耿怀志
 * @version [版本号, 2017/5/3]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class LXCommonUtil {

    private static Logger logger = LogManager.getLogger(LXCommonUtil.class);

    public static void printOutMsg(HttpServletRequest request, HttpServletResponse response, JSONObject msg)
            throws Exception {
        try {
            PrintWriter pw = response.getWriter();
            pw.write(msg.toString());
            pw.flush();
            pw.close();
        } catch (IOException e) {
            logger.error("response wirter fail!", e);
        }
    }
}
