package com.zbkj.crmeb.cloudAccount.util;

import java.util.Random;

public class OrderUtil {

    private static Random random = new Random(1);

    /**
     * 随机字符
     * @return
     */
    public static String getMess() {
        return random.nextInt(10000000) + "";
    }

    /**
     * 生成订单号
     * @param prefix
     * @return
     */
    public static String getOrderId(String prefix) {
        return prefix + System.currentTimeMillis() + random.nextInt(1000);
    }


}
