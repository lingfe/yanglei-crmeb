package com.zbkj.crmeb.front.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.exception.CrmebException;
import com.utils.CrmebUtil;
import com.utils.QRCodeUtil;
import com.utils.RestTemplateUtil;
import com.zbkj.crmeb.front.service.QrCodeService;
import com.zbkj.crmeb.wechat.service.WeChatService;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class QrCodeServiceImpl implements QrCodeService {

    @Autowired
    private WeChatService weChatService;

    @Autowired
    private RestTemplateUtil restTemplateUtil;

    @Override
    public Map<String, Object> get(JSONObject data) {
        Map<String, Object> map = new HashMap<>();
        StringBuilder scene = new StringBuilder();
        String page = "";
        try{
            if(null != data){
                Map<Object, Object> dataMap = JSONObject.toJavaObject(data, Map.class);
                for (Map.Entry<Object, Object> m : dataMap.entrySet()) {
                    if(m.getKey().equals("path")){
                        //前端路由， 不需要拼参数
                        page = m.getValue().toString();
                        continue;
                    }
                    if (scene.length() > 0) {
                        scene.append(",");
                    }
                    scene.append(m.getKey()).append(":").append(m.getValue());
                }
            }
        }catch (Exception e){
            throw new CrmebException("url参数错误 " + e.getMessage());
        }
        map.put("code", weChatService.qrCode(page, scene.length() > 0 ? scene.toString() : ""));
        return map;
    }

    @Override
    public Map<String, Object> base64(String url) {
        byte[] bytes = restTemplateUtil.getBuffer(url);
        String base64Image = CrmebUtil.getBase64Image(Base64.encodeBase64String(bytes));
        Map<String, Object> map = new HashMap<>();
        map.put("code", base64Image);
        return map;
    }

    /**
     * 将字符串转为QRcode
     * @param text 待转换字符串
     * @return QRcode base64格式
     */
    @Override
    public Map<String, Object> base64String(String text,int width, int height) {
        String base64Image = null;
        try {
            base64Image = QRCodeUtil.crateQRCode(text,width,height);
        }catch (Exception e){
            throw new CrmebException("生成二维码异常");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("code", base64Image);
        return map;
    }
}

