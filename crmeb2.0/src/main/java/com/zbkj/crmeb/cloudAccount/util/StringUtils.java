package com.zbkj.crmeb.cloudAccount.util;

import java.util.UUID;

public class StringUtils {

    public static String trim(Object object) {
        return object == null ? "" : object.toString().trim();
    }

    public static boolean isEmpty(Object object) {
        if (object == null) {
            return true;
        }

        return "".equals(trim(object)) ? true : false;
    }

    public static String getRequestId(){
        return UUID.randomUUID().toString().replace("-", "");
    }

}
