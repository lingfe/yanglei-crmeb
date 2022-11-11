package com.zbkj.crmeb.finance.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 发票抬头表-响应类
 * @author: 零风
 * @CreateDate: 2022/4/14 16:31
 */
@Data
public class InvoiceRiseResponse {

    @ApiModelProperty(value = "用户表ID标识")
    private Integer uid;

    @ApiModelProperty(value = "抬头类型:1=个人，2=企业")
    private Integer riseType;
    @ApiModelProperty(value = "抬头类型字符串")
    private String riseTypeStr;

    @ApiModelProperty(value = "发票类型:1=增值税电子普通发票,2=增值税电子专用发票")
    private Integer invoiceType;
    @ApiModelProperty(value = "发票类型字符串")
    private String invoiceTypeStr;

    @ApiModelProperty(value = "发票抬头")
    private String rise;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "邮箱号")
    private String mailbox;

    @ApiModelProperty(value = "是否默认：0=否、1=是")
    private Boolean isDefault;
    @ApiModelProperty(value = "是否默认")
    private String isDefaultStr;

    @ApiModelProperty(value = "税号")
    private String dutyParagraph;

    @ApiModelProperty(value = "开户银行")
    private String yhkKaihuhang;

    @ApiModelProperty(value = "银行卡号")
    private String yhkNumber;

    @ApiModelProperty(value = "详细地址")
    private String addressInfo;

    @ApiModelProperty(value = "企业电话")
    private String telephone;

}
