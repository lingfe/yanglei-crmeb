package com.zbkj.crmeb.front.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 个人中心响应对象
 * @author: 零风
 * @CreateDate: 2022/3/15 15:01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="UserCenterResponse对象", description="个人中心响应对象")
public class UserCenterResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "用户id")
    private Integer uid;

    @ApiModelProperty(value = "用户昵称")
    private String nickname;

    @ApiModelProperty(value = "用户头像")
    private String avatar;

    @ApiModelProperty(value = "手机号码")
    private String phone;

    @ApiModelProperty(value = "用户余额")
    private BigDecimal nowMoney;

    @ApiModelProperty(value = "用户剩余积分")
    private BigDecimal integral;

    @ApiModelProperty(value = "用户剩余经验")
    private Integer experience;

    @ApiModelProperty(value = "佣金金额")
    private BigDecimal brokeragePrice;

    @ApiModelProperty(value = "等级")
    private Integer level;

    @ApiModelProperty(value = "是否为推广员")
    private Boolean isPromoter;

    @ApiModelProperty(value = "用户优惠券数量")
    private Integer couponCount;

    @ApiModelProperty(value = "是否会员")
    private boolean vip;

    @ApiModelProperty(value = "会员图标")
    private String vipIcon;

    @ApiModelProperty(value = "会员名称")
    private String vipName;

    @ApiModelProperty(value = "小程序充值开关")
    private Boolean rechargeSwitch;

    @ApiModelProperty(value = "用户收藏数量")
    private Integer collectCount;

    @ApiModelProperty(value = "是否为区域代理用户")
    private Boolean isGeneralAgency;

    @ApiModelProperty(value = "是否为零售商管理用户")
    private Boolean isRetailer;

    @ApiModelProperty(value = "是否为联盟商家管理用户")
    private Boolean isAllianceMerchants=Boolean.FALSE;
}
