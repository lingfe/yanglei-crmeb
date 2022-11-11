package com.zbkj.crmeb.cloudAccount.constant;

public enum ChannelEnum {
    BANKCARD("银行卡"),
    ALIPAY("支付宝"),
    WXPAY("微信");

    private String value;

    ChannelEnum(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
