package com.zbkj.crmeb.cloudAccount.util;


import com.aliyun.oss.common.utils.StringUtils;
import com.zbkj.crmeb.cloudAccount.constant.ConfigPath;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * 装载属性类
 */
public class Property {

    private static Properties properties = new Properties();

    static {
        try {
            InputStream in = new BufferedInputStream(new FileInputStream("target/classes/diff.properties"));
            properties.load(in);
            in.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * 根据名称-读取属性
     * @param key
     * @return
     */
    public static String getProperties(String key) {
        return StringUtils.trim(properties.getProperty(key));
    }

    /**
     * 云服务-url拼接
     * @param url
     * @return
     */
    public static String getUrl(String url) {
        return StringUtils.trim(properties.getProperty(ConfigPath.YZH_URL))
                + StringUtils.trim(properties.getProperty(url));
    }

    /**
     * 云服务-url拼接(-Aic)
     * @param url
     * @return
     */
    public static String getUrlAic(String url) {
        return StringUtils.trim(properties.getProperty(ConfigPath.YZH_URL_AIC))
                + StringUtils.trim(properties.getProperty(url));
    }

}
