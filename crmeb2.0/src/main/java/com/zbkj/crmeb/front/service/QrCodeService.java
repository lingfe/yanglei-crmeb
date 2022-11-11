package com.zbkj.crmeb.front.service;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.Map;

/**
 * 类功能描述
 * @author: 零风
 * @CreateDate: 2022/3/14 16:23
 */
public interface QrCodeService {

    /**
     * 微信小程序二维码
     * @Author 零风
     * @Date  2022/7/21 16:51
     * @return
     */
    Map<String, Object> get(JSONObject data) throws IOException;

    /**
     * 远程图片url转base64格式
     * @param url 图片url
     * @Author 零风
     * @Date  2022/3/14
     * @return 数据
     */
    Map<String, Object> base64(String url);

    /**
     * 生成base64格式二维码
     * @param text 内容
     * @param width 宽
     * @param height 高
     * @Author 零风
     * @Date  2022/3/14
     * @return 数据
     */
    Map<String, Object> base64String(String text,int width, int height);

}
