package com.utils;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * 通用工具类
 * @author: 零风
 * @CreateDate: 2022/1/5 14:00
 */
public class CommonUtil {

    /**
     * 随机生成密码
     * @param phone 手机号
     * @return 密码
     * 使用des方式加密
     */
    public static String createPwd(String phone) {
        if("".equals(phone))phone = "123456";
        return CrmebUtil.encryptPassword(phone, phone);
    }

    public static void main(String[] args) {
        String tt= createPwd("13908515688");
        System.out.println(tt);
    }

    /**
     * 随机生成用户昵称
     * @param phone 手机号
     * @return 昵称
     */
    public static String createNickName(String phone) {
        return DigestUtils.md5Hex(phone + DateUtil.getNowTime()).
                subSequence(0, 12).
                toString();
    }

}
