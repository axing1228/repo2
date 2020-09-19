package com.sz.fts.service.zjf;

import net.sf.json.JSONObject;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @NAME HomeNetworkService
 * @AUTHOR 朱建峰
 * @DATE 2019/5/6 0006 下午 2:52
 * @DESCRIPTION 家庭网秒杀
 */
public interface HomeNetworkService {
    JSONObject displayOrder(MultipartFile[] files, HttpServletRequest request,
                            HttpServletResponse response);
}
