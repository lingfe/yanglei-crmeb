package com.zbkj.crmeb.front.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
* 创建订单请求对象
* @author: 零风
* @CreateDate: 2021/10/21 14:57
*/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="CreateOrderRequest对象", description="创建订单请求对象")
public class CreateOrderRequest implements Serializable {

    private static final long serialVersionUID = -6133994384185333872L;

    @ApiModelProperty(value = "预下单订单号")
    @NotBlank(message = "预下单订单号不能为空")
    private String preOrderNo;

    @ApiModelProperty(value = "配送方式: 1=快递、2=门店自提、3=无需配送")
    @NotNull(message = "快递类型不能为空")
    private Integer shippingType;

    @ApiModelProperty(value = "收货地址id")
    private Integer addressId;

    @ApiModelProperty(value = "优惠券编号")
    private Integer couponId;

    @ApiModelProperty(value = "支付类型:weixin-微信支付，yue-余额支付、alipay-支付宝app支付、integral-积分支付")
    @NotBlank(message = "支付类型不能为空")
    private String payType;

    @ApiModelProperty(value = "支付渠道:weixinh5-微信H5支付，public-公众号支付，routine-小程序支付，weixinAppIos-微信appios支付，weixinAppAndroid-微信app安卓支付、" +
            "zfbAppAndroid-支付宝app支付(安卓端)、zfbAppIos-支付宝app支付(苹果端)、zfbWeb-支付宝网页支付(h5网页端)")
    @NotBlank(message = "支付渠道不能为空")
    private String payChannel;

    @ApiModelProperty(value = "是否使用积分")
    @NotNull(message = "是否使用积分不能为空")
    private Boolean useIntegral;

    @ApiModelProperty(value = "订单备注")
    private String mark;

    // 以下为到店自提参数
    @ApiModelProperty(value = "自提点id")
    private Integer storeId;
    @ApiModelProperty(value = "真实名称")
    private String realName;
    @ApiModelProperty(value = "手机号码")
    private String phone;

    @ApiModelProperty(value = "订单类型: 0=系统、1=视频号订单、2=区域代理订单、3=零售商订单、4=供应商订单、5=联盟商家订单")
    private Integer orderType;
    @ApiModelProperty(value = "是否开具发票")
    private Boolean isInvoice=Boolean.FALSE;
    @ApiModelProperty(value = "发票抬头表ID标识")
    private Integer riseId;
    @ApiModelProperty(value = "推广人id(表示这个订单是这个人推广的)")
    private Integer spreadUid;

}
