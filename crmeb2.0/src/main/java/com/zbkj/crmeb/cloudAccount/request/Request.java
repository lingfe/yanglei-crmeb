package com.zbkj.crmeb.cloudAccount.request;

import com.exception.CrmebException;
import com.google.gson.Gson;
import com.zbkj.crmeb.cloudAccount.constant.ConfigPath;
import com.zbkj.crmeb.cloudAccount.constant.XmlData;
import com.zbkj.crmeb.cloudAccount.util.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.http.protocol.HTTP;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Map;

/**
 * 示例：data=MhjTl1rWjFxHJ5e&mess=12313&timestamp=123457&sign=b6516baa210161df6f34f1efec2a7c484fd7920fed5640e066e970d8a3f01499&sign_type=rsa
 **/
@Getter
@Setter
@ToString
@Builder
public class Request<T> {

    /**
     * 时间戳，精确到秒
     **/
    private int timestamp = Integer.parseInt(String.valueOf(new Date().getTime()/1000));

    /**
     * 签名
     **/
    private String sign;

    /**
     * 签名方式，固定值rsa
     **/
    private String sign_type = Property.getProperties(ConfigPath.YZH_SIGN_TYPE);

    /**
     * 随机数，用于签名
     **/
    private String mess;

    /**
     * 经过加密后的具体数据
     **/
    private String data;

    /**
     * 发送Post请求，得到响应
     * @param data  请求参数
     * @param api   请求地址
     * @return
     */
    public static Map<String, Object> sendRequestResult(Object data, String api, String requestType)  {
        //转换为请求
        Request request = Request.getRequest(data);

        //得到-响应map
        Map<String, Object> result = null;

        //验证-请求方式
        if(RequestMethod.GET.toString().equals(requestType)){
            result = HttpUtil.get(request, api);
        }else{
            result = HttpUtil.post(request, api);
        }

        //验证-非空
        if(result == null  ){
            throw new CrmebException("失败！云账户异常：NULL!");
        }

        //取出data
        Object data2=result.get("data");
        if(data2 == null){
            throw new CrmebException("失败！data：NULL!");
        }

        //验证响应状态
        Map<String,Object> data3= JsonUtil.fromJson(data2.toString(),Map.class);
        Object code=data3.get("code");
        Object msg=data3.get("message");
        if(code == null || !"0000".equals(code)) throw new CrmebException(String.format("失败！%s!",msg));

        //返回
        return result;
    }

    /**
     * 请求拼接
     * @param data 数据
     * @return
     */
    public static Request getRequest(Object data) {
        try{
            return Request.builder()
                    .mess(OrderUtil.getMess())
                    .timestamp(Integer.parseInt(String.valueOf(new Date().getTime()/1000)))
                    .sign_type(Property.getProperties(ConfigPath.YZH_SIGN_TYPE))
                    .build()
                    .encData(data);
        }catch(Exception e){
            throw new CrmebException("请求拼接异常:"+e.getMessage());
        }
    }

    /**
     * 设置数据
     * @param param 请求数据
     * @return
     * @throws Exception
     */
    public Request encData(T param) throws Exception {
        setData(DESUtil.encode(JsonUtil.toJson(param)));
        return this;
    }

    /**
     * 获取签名
     * @return
     */
    public String getSign() {
        //拼接内容
        String plain = new StringBuffer("data=")
                .append(data)
                .append("&mess=")
                .append(mess)
                .append("&timestamp=")
                .append(timestamp)
                .append("&key=")
                .append(Property.getProperties(ConfigPath.YZH_APPKEY))
                .toString();
        //生成签名并返回
        return RSAUtil.sign(plain, Property.getProperties(ConfigPath.YZH_RSA_PRIVATE_KEY));
    }

    /**
     * 获取get请求明文串
     * @return
     * @throws Exception
     */
    public String getPlainEncode() throws Exception {
        return new StringBuffer("data=")
                .append(getEncode(data))
                .append("&mess=")
                .append(getEncode(mess))
                .append("&timestamp=")
                .append(timestamp)
                .append("&sign_type=")
                .append(getEncode(sign_type))
                .append("&sign=")
                .append(getEncode(getSign()))
                .toString();
    }

    private String getEncode(String content) throws Exception {
        return URLEncoder.encode(content, XmlData.CHARSET);
    }
}
