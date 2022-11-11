package com.zbkj.crmeb.cloudAccount.constant;

public enum OrderPrefixEnum {

    /**
     * 银行卡
     */
    BANK_CARD_ORDER("bank"),

    /**
     * 支付宝
     */
    ALIPAY_ORDER("ali"),

    /**
     * 微信
     */
    WXPAY_ORDER("wx");

    private String value;

    OrderPrefixEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

}
