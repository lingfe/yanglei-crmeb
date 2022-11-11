package com.zbkj.crmeb.regionalAgency.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 区域代理-响应类
 * @author: 零风
 * @CreateDate: 2021/11/6 10:38
 */
@Data
public class RegionalAgencyResponse {

    @ApiModelProperty(value = "区域代理表ID标识")
    private Integer id;

    @ApiModelProperty(value = "区域代理管理用户id标识")
    private Integer uid;
    @ApiModelProperty(value = "区域代理管理用户昵称")
    private String nickname;

    @ApiModelProperty(value = "省")
    private String province;

    @ApiModelProperty(value = "市")
    private String city;

    @ApiModelProperty(value = "区,多个用逗号隔开")
    private String district;

    @ApiModelProperty(value = "是否删除")
    private Boolean isDel;

    @ApiModelProperty(value = "创建时间")
    private Date updateTime;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "区域代理名称")
    private String raName;

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
