package com.zbkj.crmeb.front.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
* 预下单-Vo对象
* @author: 零风
* @CreateDate: 2021/10/21 15:00
*/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="OrderInfoVo对象", description="预下单Vo对象")
public class OrderInfoVo {

    @ApiModelProperty(value = "运费金额")
    private BigDecimal freightFee;

    @ApiModelProperty(value = "优惠券编号（选择优惠券时有值")
    private Integer userCouponId;

    @ApiModelProperty(value = "优惠金额")
    private BigDecimal couponFee;

    @ApiModelProperty(value = "商品总计金额")
    private BigDecimal proTotalFee;

    @ApiModelProperty(value = "订单商品数量")
    private Integer orderProNum;

    @ApiModelProperty(value = "实际支付金额")
    private BigDecimal payFee;

    @ApiModelProperty(value = "地址id")
    private Integer addressId;

    @ApiModelProperty(value = "收货人姓名(前端用)")
    private String realName;

    @ApiModelProperty(value = "收货人电话(前端用)")
    private String phone;

    @ApiModelProperty(value = "收货人所在省(前端用)")
    private String province;

    @ApiModelProperty(value = "收货人所在市(前端用)")
    private String city;

    @ApiModelProperty(value = "收货人所在区(前端用)")
    private String district;

    @ApiModelProperty(value = "收货人详细地址(前端用)")
    private String detail;

    @ApiModelProperty(value = "用户剩余积分")
    private BigDecimal userIntegral;

    @ApiModelProperty(value = "用户可用余额")
    private BigDecimal userBalance;

    @ApiModelProperty(value = "订单备注")
    private String remark;

    @ApiModelProperty(value = "订单详情数组")
    private List<OrderInfoDetailVo> orderDetailList;

    @ApiModelProperty(value = "秒杀商品Id")
    private Integer seckillId = 0;

    @ApiModelProperty(value = "砍价商品Id")
    private Integer bargainId = 0;

    @ApiModelProperty(value = "用户砍价活动id")
    private Integer bargainUserId;

    @ApiModelProperty(value = "拼团商品Id")
    private Integer combinationId = 0;

    @ApiModelProperty(value = "拼团团长Id")
    private Integer pinkId = 0;

    @ApiModelProperty(value = "购物车编号列表")
    private List<Long> cartIdList;

    @ApiModelProperty(value = "是否视频号订单")
    private Boolean isVideo = false;

    //用于-商户用户操作
    @ApiModelProperty(value = "商户用户ID")
    private Integer merId;

    @ApiModelProperty(value = "是否参与佣金分红")
    private Boolean isBrokerageAbonus;

    @ApiModelProperty(value = "额度控制->Constants.java")
    private Integer quotaControl;

    @ApiModelProperty(value = "成本价")
    private BigDecimal cost;

}
