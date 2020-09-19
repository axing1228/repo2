package com.sz.fts.service.zjf;

import java.io.IOException;

/**
 * @NAME WeichatService
 * @AUTHOR 朱建峰
 * @DATE 2019/7/18 0018 下午 1:30
 * @DESCRIPTION 微信服务接口
 */
public interface WeichatService {

    String getOpenId(String code) throws IOException;
}
