package com.zbkj.crmeb.finance.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 用户提现-请求类(公共)
 * @author: 零风
 * @CreateDate: 2021/12/22 14:23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="UserExtractRequest对象", description="用户提现")
public class UserExtractRequest implements Serializable {

    private static final long serialVersionUID=1L;

    //公共字段
    @ApiModelProperty(value = "提现方式| alipay=支付宝,bank=银行卡,weixin=微信零钱", allowableValues = "range[alipay,weixin,bank]")
    @NotBlank(message = "请选择提现方式：支付宝|微信零钱|银行卡")
    private String extractType;
    @ApiModelProperty(value = "提现金额")
    @JsonProperty(value = "money")
    @DecimalMin(value = "0.01", message = "请输入提现金额")
    private BigDecimal extractPrice;
    @ApiModelProperty(value = "姓名/银行开户姓名/支付宝实名名称/微信实名名称(必填)")
    private String realName;
    @ApiModelProperty(value = "身份证号/银行卡开户身份证号/支付宝实名身份证号/微信实名身份证号(必填)")
    private String idCard;
    @ApiModelProperty(value = "联系人手机号(选填)")
    private String phone;
    @ApiModelProperty(value = "打款备注(选填，最大20个字符，一个汉字占2个字符，不允许特殊字符：' \" & | @ % * ( ) - : # ￥)")
    private String remark;

    @ApiModelProperty(value = "关联类型：1=普通提现、2、申请信用卡还款资金")
    @Min(value = 1, message = "必须大于等于1!")
    private Integer linkType;

    //银行卡-字段
    @ApiModelProperty(value = "提现银行名称")
    private String bankName;
    @ApiModelProperty(value = "银行开户卡号(必填)")
    private String bankCode;
    @ApiModelProperty(value = "开户地址")
    private String bankAddress;

    //支付宝-字段
    @ApiModelProperty(value = "支付宝账号")
    private String alipayCode;

    //微信-字段
    @ApiModelProperty(value = "微信号")
    private String wechat;
    @ApiModelProperty(value = "微信用户的openid")
    private String openid;
    @ApiModelProperty(value = "微信收款码")
    private String qrcodeUrl;
    @ApiModelProperty(value = "应用ID")
    private String appid;
    @ApiModelProperty(value = "应用ID类型: 1=微信服务号、2=微信小程序、3=安卓微信、4=ios微信、5=网页..")
    private Integer appidType;

}
