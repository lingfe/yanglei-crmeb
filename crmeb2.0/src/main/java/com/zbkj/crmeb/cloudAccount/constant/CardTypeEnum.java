package com.zbkj.crmeb.cloudAccount.constant;

public enum CardTypeEnum {
    /**
     * 护照
     * **/
    PASSPORT("passport"),
    /**
     * 港澳居民来往内地通行证
     * **/
    MTPHKM("mtphkm"),
    /**
     * 台湾居民来往大陆通行证（台胞证）
     * **/
    MTPT("mtpt"),
    /**
     * 中华人民共和国港澳居民居住证
     * **/
    RPHKM("rphkm"),
    /**
     * 中华人民共和国台湾居民居住证
     * **/
    RPT("rpt"),
    /**
     * 外国人永久居留身份证
     * **/
    FPR("fpr"),
    /**
     * 中华人民共和国外国人就业许可证书
     * **/
    FFWP("ffwp");

    private String value;

    CardTypeEnum(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
