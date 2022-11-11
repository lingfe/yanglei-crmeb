package com.utils;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSONObject;
import com.constants.Constants;
import com.constants.PayConstants;
import com.exception.CrmebException;
import com.zbkj.crmeb.cloudAccount.constant.OrderPrefixEnum;
import com.zbkj.crmeb.cloudAccount.util.OrderUtil;
import com.zbkj.crmeb.payment.vo.wechat.CreateOrderRequestVo;
import com.zbkj.crmeb.payment.vo.wechat.WxRefundVo;
import com.zbkj.crmeb.wechat.vo.PayToChangeVo;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLContext;
import javax.xml.parsers.DocumentBuilder;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.*;

/**
 * 微信支付工具类
 * @author: 零风
 * @CreateDate: 2021/12/22 10:08
 */
public class WxPayUtil {

    private static final Log LOG = LogFactory.getLog(WxPayUtil.class);

    /**
     * 微信分账请求相关QQ
     * @param certPath  证书路径
     * @param reqXmlStr 封装好的请求参数
     * @return 结果
     */
    public static String requestWxSplitAccount(String url,String certPath, StringBuilder reqXmlStr,String mchid) throws Exception {
        String result = null;
        HttpPost httpPost = null;

        // 得指明使用UTF-8编码，否则到API服务器XML的中文不能被成功识别
        httpPost = new HttpPost(url);
        StringEntity postEntity = new StringEntity(reqXmlStr.toString(), "UTF-8");
        httpPost.addHeader("Content-Type", "text/xml");
        httpPost.setEntity(postEntity);

        // 设置请求器的配置，根据默认超时限制初始化requestConfig
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(30000).build();
        httpPost.setConfig(requestConfig);

        // 拼接证书的路径
        KeyStore keyStore = KeyStore.getInstance("PKCS12");

        // 加载本地的证书进行https加密传输
        FileInputStream instream = new FileInputStream(certPath);
        keyStore.load(instream,mchid.toCharArray()); // 加载证书密码，默认为商户ID

        // SSL
        SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore,mchid.toCharArray()).build();
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[] {"TLSv1.2"}, null,
                SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);

        // 创建链接对象->得到响应对象->转换编码返回
        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
        //httpClient= HttpClients.custom().build();
        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity entity = response.getEntity();
        result = EntityUtils.toString(entity, "UTF-8");
        return result;
    }

    /**
     * 提现到微信零钱(封装：参数转换、请求、响应)
     * @param model 微信接口请求参数DTO对象
     * @return  请求提现结果
     */
    public static String doTransfers(String certPath, PayToChangeVo model) {
        //定义对象
        String url = "https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers";
        String result = null;
        HttpPost httpPost = null;
        try{
            // 1.计算参数签名
            String sign = getSign(model.map(),model.getAppkey());

            // 2.封装请求参数
            StringBuilder reqXmlStr = new StringBuilder();
            reqXmlStr.append("<xml>");
            reqXmlStr.append("<amount>" + model.getAmount() + "</amount>");
            reqXmlStr.append("<check_name>" + model.getCheck_name() + "</check_name>");
            reqXmlStr.append("<desc>" + model.getDesc() + "</desc>");
            reqXmlStr.append("<mch_appid>" + model.getMch_appid() + "</mch_appid>");
            reqXmlStr.append("<mchid>" + model.getMchid() + "</mchid>");
            reqXmlStr.append("<nonce_str>" + model.getNonce_str() + "</nonce_str>");
            reqXmlStr.append("<openid>" + model.getOpenid() + "</openid>");
            reqXmlStr.append("<partner_trade_no>" + model.getPartner_trade_no() + "</partner_trade_no>");
            reqXmlStr.append("<spbill_create_ip>" + model.getSpbill_create_ip() + "</spbill_create_ip>");
            reqXmlStr.append("<sign>" + sign + "</sign>");
            reqXmlStr.append("</xml>");
            LOG.info("request-xml= " + reqXmlStr);

            // 得指明使用UTF-8编码，否则到API服务器XML的中文不能被成功识别
            httpPost = new HttpPost(url);
            StringEntity postEntity = new StringEntity(reqXmlStr.toString(), "UTF-8");
            httpPost.addHeader("Content-Type", "text/xml");
            httpPost.setEntity(postEntity);

            // 设置请求器的配置，根据默认超时限制初始化requestConfig
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(30000).build();
            httpPost.setConfig(requestConfig);

            // 拼接证书的路径
            KeyStore keyStore = KeyStore.getInstance("PKCS12");

            // 加载本地的证书进行https加密传输
            FileInputStream instream = new FileInputStream(certPath);
            keyStore.load(instream, model.getMchid().toCharArray()); // 加载证书密码，默认为商户ID

            // SSL
            SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, model.getMchid().toCharArray()).build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[] {"TLSv1.2"}, null,
                    SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);

            // 创建链接对象->得到响应对象->转换编码返回
            CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
            //httpClient= HttpClients.custom().build();
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity, "UTF-8");
        }catch (Exception e){
            e.printStackTrace();
            throw new CrmebException(e.getMessage());
        }finally {
            httpPost.abort();
        }
        return result;
    }


    /**
     * 提现到微信零钱-签名
     * @param model
     * @param appkey
     * @return
     */
    private static String createSignParam(PayToChangeVo model,String appkey) {
        // 微信签名规则 https://pay.weixin.qq.com/wiki/doc/api/tools/mch_pay.php?chapter=4_3
        Map<String, Object> paramMap = new HashMap<String, Object>();

        // 订单号默认用商户号+时间戳+4位随机数+可以根据自己的规则进行调整
        model.setAppkey(appkey);
        model.setNonce_str(WxPayUtil.getNonceStr());
        //model.setPartner_trade_no(model.getMchid() + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + (int)((Math.random() * 9 + 1) * 1000));
        model.setPartner_trade_no(OrderUtil.getOrderId(OrderPrefixEnum.WXPAY_ORDER.getValue()));
        paramMap.put("mch_appid", model.getMch_appid());
        paramMap.put("mchid", model.getMchid());
        paramMap.put("openid", model.getOpenid());
        paramMap.put("amount", model.getAmount());
        paramMap.put("check_name", model.getCheck_name());
        paramMap.put("desc", model.getDesc());
        paramMap.put("partner_trade_no", model.getPartner_trade_no());
        paramMap.put("nonce_str", model.getNonce_str());
        paramMap.put("spbill_create_ip", model.getSpbill_create_ip());

        //排序
        List<String> keys = new ArrayList(paramMap.keySet());
        Collections.sort(keys);

        //拼接成字符串并返回
        String prestr = "";
        for (int i = 0; i < keys.size(); i++ ) {
            String key = keys.get(i);
            Object value = (Object)paramMap.get(key);
            if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
                prestr = prestr + key + "=" + value;
            } else {
                prestr = prestr + key + "=" + value + "&";
            }
        }
        LOG.info("createSignParam = " + prestr);
        return prestr;
    }

    /**
     * 处理 HTTPS API返回数据，转换成Map对象。return_code为SUCCESS时，验证签名。
     * @param xmlStr API返回的XML格式数据
     * @return Map类型数据
     * @throws Exception
     */
    public static HashMap<String, Object> processResponseXml(String xmlStr) throws Exception {
        String RETURN_CODE = "return_code";
        String return_code;
        HashMap<String, Object> respData = XmlUtil.xmlToMap(xmlStr);
        if (respData.containsKey(RETURN_CODE)) {
            return_code = (String) respData.get(RETURN_CODE);
        } else {
            throw new CrmebException(String.format("No `return_code` in XML: %s", xmlStr));
        }

        if (return_code.equals(Constants.FAIL)) {
            return respData;
        } else if (return_code.equals(Constants.SUCCESS)) {
            return respData;
        } else {
            throw new CrmebException(String.format("return_code value %s is invalid in XML: %s", return_code, xmlStr));
        }
    }

    /**
     * 获取随机字符串，长度要求在32位以内。
     */
    public static String getNonceStr() {
        return DigestUtils.md5Hex(CrmebUtil.getUuid() + CrmebUtil.randomCount(111111, 666666));
    }

    /**
     * 获取sign
     * @param vo      微信公共下单对象
     * @param signKey 微信签名key
     * @return String
     */
    public static String getSign(CreateOrderRequestVo vo, String signKey) {
        // 对象转map
        Map<String, Object> map = JSONObject.parseObject(JSONObject.toJSONString(vo), Map.class);
        // map排序
        Set<String> keySet = map.keySet();
        String[] keyArray = keySet.toArray(new String[keySet.size()]);
        Arrays.sort(keyArray);
        StringBuilder sb = new StringBuilder();
        for (String k : keyArray) {
            if (k.equals(PayConstants.FIELD_SIGN)) {
                continue;
            }
            if (ObjectUtil.isNotNull(map.get(k))) // 参数值为空，则不参与签名
                sb.append(k).append("=").append(map.get(k)).append("&");
        }
        sb.append("key=").append(signKey);
        String sign = SecureUtil.md5(sb.toString()).toUpperCase();
        System.out.println("sign ========== " + sign);
        return sign;
    }

    /**
     * 获取sign
     * @param wxRefundVo  微信退款对象
     * @param signKey 微信签名key
     * @return String
     */
    public static String getSign(WxRefundVo wxRefundVo, String signKey) {
        // 对象转map
        Map<String, Object> map = JSONObject.parseObject(JSONObject.toJSONString(wxRefundVo), Map.class);
        // map排序
        Set<String> keySet = map.keySet();
        String[] keyArray = keySet.toArray(new String[keySet.size()]);
        Arrays.sort(keyArray);
        StringBuilder sb = new StringBuilder();
        for (String k : keyArray) {
            if (k.equals(PayConstants.FIELD_SIGN)) {
                continue;
            }
            if (ObjectUtil.isNotNull(map.get(k))) // 参数值为空，则不参与签名
                sb.append(k).append("=").append(map.get(k)).append("&");
        }
        sb.append("key=").append(signKey);
        String sign = SecureUtil.md5(sb.toString()).toUpperCase();
        System.out.println("sign ========== " + sign);
        return sign;
    }

    private static String byteArrayToHexString(byte b[]) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++)
            resultSb.append(byteToHexString(b[i]));
        return resultSb.toString();
    }

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0)
            n += 256;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }
    private static final String hexDigits[] = { "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
    /**
     * 生成 HMACSHA256
     * @param data 待处理数据
     * @param key 密钥
     * @return 加密结果
     * @throws Exception
     */
    public static String HMACSHA256(String data, String key) throws Exception {
        String hash = "";
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] bytes = sha256_HMAC.doFinal(data.getBytes());
            hash = byteArrayToHexString(bytes);
        } catch (Exception e) {
            System.out.println("Error HmacSHA256 ===========" + e.getMessage());
        }
        return hash.toUpperCase();
    }

    public static String generateSignSHA256(Map<String, String> map, String signKey) throws Exception{
        // map排序
        Set<String> keySet = map.keySet();
        String[] keyArray = keySet.toArray(new String[keySet.size()]);
        Arrays.sort(keyArray);
        StringBuilder sb = new StringBuilder();
        for (String k : keyArray) {
            if (k.equals(PayConstants.FIELD_SIGN)) {
                continue;
            }
            if (StrUtil.isNotBlank(map.get(k)) && map.get(k).trim().length() > 0) // 参数值为空，则不参与签名
                sb.append(k).append("=").append(map.get(k).trim()).append("&");
        }

        // 拼接key
        sb.append("key=").append(signKey);
        // MD5加密
        String sign = HMACSHA256(sb.toString(), signKey).toUpperCase();
        return sign;
    }

    /**
     * 获取sign
     * @param map      待签名数据
     * @param signKey  微信签名key
     * @return String
     */
    public static String getSign(Map<String, String> map, String signKey) {
        // map排序
        Set<String> keySet = map.keySet();
        String[] keyArray = keySet.toArray(new String[keySet.size()]);
        Arrays.sort(keyArray);
        StringBuilder sb = new StringBuilder();
        for (String k : keyArray) {
            if (k.equals(PayConstants.FIELD_SIGN)) {
                continue;
            }
            if (StrUtil.isNotBlank(map.get(k)) && map.get(k).trim().length() > 0) // 参数值为空，则不参与签名
                sb.append(k).append("=").append(map.get(k).trim()).append("&");
        }
        sb.append("key=").append(signKey);
        System.out.println(sb);
        String sign = SecureUtil.md5(sb.toString()).toUpperCase();
        String sign2 = DigestUtils.md5Hex(sb.toString()).toUpperCase();
        System.out.println("sign ========== " + sign);
        return sign;
    }

    /**
     * 获取sign
     * @param map      待签名数据
     * @param signKey 微信签名key
     * @return String
     */
    public static String getSignObject(Map<String, Object> map, String signKey) {
        // map排序
        Set<String> keySet = map.keySet();
        String[] keyArray = keySet.toArray(new String[keySet.size()]);
        Arrays.sort(keyArray);
        StringBuilder sb = new StringBuilder();
        for (String k : keyArray) {
            if (k.equals(PayConstants.FIELD_SIGN)) {
                continue;
            }
            if (ObjectUtil.isNotNull(map.get(k))) // 参数值为空，则不参与签名
                sb.append(k).append("=").append(map.get(k)).append("&");
        }
        sb.append("key=").append(signKey);
        System.out.println("sb ========== " + sb);
        String sign = SecureUtil.md5(sb.toString()).toUpperCase();
        System.out.println("sign ========== " + sign);
        return sign;
    }

    /**
     * 获取当前时间戳，单位秒
     * @return  Long
     */
    public static Long getCurrentTimestamp() {
        return System.currentTimeMillis()/1000;
    }

    /**
     * 获取当前时间戳，单位毫秒
     * @return  Long
     */
    public static Long getCurrentTimestampMs() {
        return System.currentTimeMillis();
    }

    /**
     * XML格式字符串转换为Map
     * @param strXML XML字符串
     * @return XML数据转换后的Map
     * @throws Exception
     */
    public static Map<String, String> xmlToMap(String strXML) throws Exception {
        try {
            Map<String, String> data = new HashMap<String, String>();
            DocumentBuilder documentBuilder = WXPayXmlUtil.newDocumentBuilder();
            InputStream stream = new ByteArrayInputStream(strXML.getBytes("UTF-8"));
            org.w3c.dom.Document doc = documentBuilder.parse(stream);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getDocumentElement().getChildNodes();
            for (int idx = 0; idx < nodeList.getLength(); ++idx) {
                Node node = nodeList.item(idx);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    org.w3c.dom.Element element = (org.w3c.dom.Element) node;
                    data.put(element.getNodeName(), element.getTextContent());
                }
            }
            try {
                stream.close();
            } catch (Exception ex) {
                // do nothing
            }
            return data;
        } catch (Exception ex) {
            System.out.println(StrUtil.format("Invalid XML, can not convert to map. Error message: {}. XML content: {}", ex.getMessage(), strXML));
            throw ex;
        }

    }
}
