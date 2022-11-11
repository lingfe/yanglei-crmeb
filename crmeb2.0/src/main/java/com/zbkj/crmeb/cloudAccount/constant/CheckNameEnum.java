package com.zbkj.crmeb.cloudAccount.constant;

/**
 * 校验支付宝账户姓名
 * **/
public enum CheckNameEnum {

    /** 不校验 */
    NOCHECK("NoCheck"),
    /** 校验 */
    CHECK("Check");

    private String value;

    CheckNameEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
