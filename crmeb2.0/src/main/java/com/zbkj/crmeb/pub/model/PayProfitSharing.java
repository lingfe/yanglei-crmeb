package com.zbkj.crmeb.pub.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 分账-公共字段
 * @author: 零风
 * @CreateDate: 2022/2/24 15:07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class PayProfitSharing {

    @ApiModelProperty(value = "分账费率比例(%)")
    private BigDecimal rate;
    @ApiModelProperty(value = "微信账号类型: MERCHANT_ID(商户号)、PERSONAL_OPENID(个人用户openid)")
    private String accountTypeWeixin;
    @ApiModelProperty(value = "微信帐号")
    private String accountWeixin;
    @ApiModelProperty(value = "微信账号实名")
    private String accountWeixinRealName;
    @ApiModelProperty(value = "支付宝账号类型: userId(唯一用户号)、cardAliasNo(支付宝绑定的卡编号)、loginName")
    private String accountTypeAlipay;
    @ApiModelProperty(value = "支付宝帐号")
    private String accountAlipay;
    @ApiModelProperty(value = "支付宝账号实名")
    private String accountAlipayRealName;

}
