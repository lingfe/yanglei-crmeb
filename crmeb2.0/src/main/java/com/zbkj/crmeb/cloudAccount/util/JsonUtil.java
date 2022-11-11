package com.zbkj.crmeb.cloudAccount.util;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class JsonUtil {

    private static Gson gson = new Gson();

    public static <T> T fromJson(String json, Class<T> clazz) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        return gson.fromJson(json, clazz);
    }

    public static String toJson(Object object) {
        if (object == null) {
            return "";
        }
        return gson.toJson(object);
    }

    public static <T> T fromData(String message, Class<T> clazz) {
        if (StringUtils.isEmpty(message)) {
            return null;
        }

        Map<String, String> data = new HashMap<String, String>();
        for(String element:message.split("&")){
            String[] eles = element.split("=", 2);
            data.put(eles[0], eles[1]);
        }

        return gson.fromJson(toJson(data), clazz);
    }

}
