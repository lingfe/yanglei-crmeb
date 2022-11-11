package com.zbkj.crmeb.cloudAccount.util;

import com.exception.CrmebException;
import com.zbkj.crmeb.cloudAccount.constant.ConfigPath;
import com.zbkj.crmeb.cloudAccount.constant.DataDict;
import com.zbkj.crmeb.cloudAccount.constant.XmlData;
import com.zbkj.crmeb.cloudAccount.request.Request;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * http请求工具类
 */
public class HttpUtil {

    /**
     * post 请求
     * @param request
     * @param url
     * @return
     * @throws Exception
     */
    public static Map<String, Object> post(Request request, String url) {
        try{
            return postMethod(request, url);
        }catch (Exception e){
            throw new CrmebException("post请求异常："+e.getMessage());
        }
    }

    /**
     * post请求
     * @param request
     * @param url
     * @return
     * @throws Exception
     * @Description (TODO这里用一句话描述这个方法的作用)
     */
    private static Map<String, Object> postMethod(Request request, String url) throws Exception {
        //验证-url非空
        if (StringUtils.isEmpty(url)) {
            throw new RuntimeException("HttpUtil error url:" + url);
        }

        //设置-请求配置 https://developer.aliyun.com/ask/221545?spm=a2c6h.13524658
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(DataDict.CONNECTTIMEOUT)
                .setSocketTimeout(DataDict.SOCKETTIMEOUT)
                .build();

        //创建-SSL链接-对象 SSLConnectionSocketFactory 中设置允许所有主机名称就可以忽略主机名称验证
        SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(
                SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build(),
                NoopHostnameVerifier.INSTANCE);

        //创建-可关闭的网络链接-请求对象 https://www.jianshu.com/p/82f30208ae56
        CloseableHttpClient httpClient = HttpClientBuilder.create().setSSLSocketFactory(sslConnectionSocketFactory).setDefaultRequestConfig(config).build();

        //创建-post请求-并设置请求头
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader(HTTP.CONTENT_TYPE, XmlData.CONTENT_TYPE_JSON);
        httpPost.setHeader(XmlData.DEALER_ID, Property.getProperties(ConfigPath.YZH_DEALERID));
        httpPost.setHeader(XmlData.REQUEST_ID, StringUtils.getRequestId());

        //实例化-NameValuePair-集合对象 https://zhidao.baidu.com/question/2122337151053979947.html
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(XmlData.MESS,  request.getMess()));
        params.add(new BasicNameValuePair(XmlData.TIMESTAMP, request.getTimestamp()+""));
        params.add(new BasicNameValuePair(XmlData.SIGN_TYPE, request.getSign_type()));
        params.add(new BasicNameValuePair(XmlData.DATA, request.getData()));
        params.add(new BasicNameValuePair(XmlData.SIGN, request.getSign()));

        //定义-可关闭的网络链接-响应对象
        CloseableHttpResponse response = null;
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            //设置编码
            httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

            //发送请求-并得到响应
            response = httpClient.execute(httpPost);

            //状态码
            int statusCode = response.getStatusLine().getStatusCode();

            //定义-响应结果
            String result = null;

            //验证状态码
            if (statusCode != DataDict.STATUS_200) {
                if (statusCode == DataDict.STATUS_302) {
                    result = response.getHeaders("location")[0].getValue();
                } else {
                    httpPost.abort();
                    throw new RuntimeException("HttpClient,error status code :" + statusCode);
                }
            } else {
                //成功
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    result = EntityUtils.toString(entity, XmlData.CHARSET);
                }
                Header[] headers = response.getAllHeaders();
                EntityUtils.consume(entity);
                map.put(XmlData.HEADER, headers);
            }
            map.put(XmlData.STATUSCODE, statusCode);
            map.put(XmlData.DATA, result);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return map;
    }

    /**
     * GET请求
     * @param request
     * @param url
     * @return
     * @throws Exception
     */
    public static Map<String, Object> get(Request request, String url)  {
       try {
           return get(url + "?" + request.getPlainEncode());
       }catch (Exception e){
           throw new CrmebException("get请求异常："+e.getMessage());
       }
    }

    /**
     * get请求
     *
     * @param url
     * @return
     * @throws Exception
     * @Description (TODO这里用一句话描述这个方法的作用)
     */
    private static Map<String, Object> get(String url) throws Exception {
        if (StringUtils.isEmpty(url)) {
            throw new RuntimeException("HttpUtil error url:" + url);
        }
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(DataDict.CONNECTTIMEOUT)
                .setSocketTimeout(DataDict.SOCKETTIMEOUT)
                .build();
        SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(
                SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build(),
                NoopHostnameVerifier.INSTANCE
        );
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setSSLSocketFactory(sslConnectionSocketFactory).setDefaultRequestConfig(config).build();
        HttpGet httpget = new HttpGet(url);

        httpget.setHeader(HTTP.CONTENT_TYPE, XmlData.CONTENT_TYPE_JSON);
        httpget.setHeader(XmlData.DEALER_ID, Property.getProperties(ConfigPath.YZH_DEALERID));
        httpget.setHeader(XmlData.REQUEST_ID, StringUtils.getRequestId());

        CloseableHttpResponse response = null;
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            // 执行get请求.
            response = httpClient.execute(httpget);
            String result = null;
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != DataDict.STATUS_200) {
                if (statusCode == DataDict.STATUS_302) {
                    result = response.getHeaders("location")[0].getValue();
                } else {
                    httpget.abort();
                    throw new RuntimeException("HttpClient,error status code :" + statusCode);
                }
            } else {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    result = EntityUtils.toString(entity, XmlData.CHARSET);
                }
                Header[] headers = response.getAllHeaders();
                EntityUtils.consume(entity);
                map.put(XmlData.HEADER, headers);
            }
            map.put(XmlData.STATUSCODE, statusCode);
            map.put(XmlData.DATA, result);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return map;
    }

    public static void main(String[] args) {

    }

}
