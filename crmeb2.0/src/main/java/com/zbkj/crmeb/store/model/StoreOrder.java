package com.zbkj.crmeb.store.model;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
* 订单表
* @author: 零风
* @CreateDate: 2021/12/2 14:54
*/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_store_order")
@ApiModel(value="StoreOrder对象", description="订单表")
public class StoreOrder implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "订单ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "订单号")
    private String orderId;

    @ApiModelProperty(value = "用户id")
    private Integer uid;

    @ApiModelProperty(value = "用户姓名")
    private String realName;

    @ApiModelProperty(value = "用户电话")
    private String userPhone;

    @ApiModelProperty(value = "详细地址")
    private String userAddress;

    @ApiModelProperty(value = "运费金额")
    private BigDecimal freightPrice;

    @ApiModelProperty(value = "订单商品总数")
    private Integer totalNum;

    @ApiModelProperty(value = "订单总价")
    private BigDecimal totalPrice;

    @ApiModelProperty(value = "邮费")
    private BigDecimal totalPostage;

    @ApiModelProperty(value = "实际支付金额")
    private BigDecimal payPrice;

    @ApiModelProperty(value = "支付邮费")
    private BigDecimal payPostage;

    @ApiModelProperty(value = "抵扣金额")
    private BigDecimal deductionPrice;

    @ApiModelProperty(value = "优惠券id")
    private Integer couponId;

    @ApiModelProperty(value = "优惠券金额")
    private BigDecimal couponPrice;

    @ApiModelProperty(value = "支付状态")
    private Boolean paid;

    @ApiModelProperty(value = "支付时间")
    private Date payTime;

    @ApiModelProperty(value = "支付方式(yue-余额支付、offline-线下支付、alipay-支付宝支付、bank-银行卡支付、integral-积分支付、weixin-微信支付、zeroPay-零元付)")
    private String payType;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "订单状态（0：待发货；1：待收货；2：已收货，待评价；3：已完成；）")
    private Integer status;

    @ApiModelProperty(value = "0=未退款， 1=申请中， 2=已退款， 3=退款中")
    private Integer refundStatus;

    @ApiModelProperty(value = "退款图片")
    private String refundReasonWapImg;

    @ApiModelProperty(value = "退款用户说明")
    private String refundReasonWapExplain;

    @ApiModelProperty(value = "前台退款原因")
    private String refundReasonWap;

    @ApiModelProperty(value = "不退款的理由")
    private String refundReason;

    @ApiModelProperty(value = "退款时间")
    private Date refundReasonTime;

    @ApiModelProperty(value = "退款金额")
    private BigDecimal refundPrice;

    @ApiModelProperty(value = "快递名称/送货人姓名")
    private String deliveryName;

    @ApiModelProperty(value = "发货类型")
    private String deliveryType;

    @ApiModelProperty(value = "快递单号/手机号")
    private String deliveryId;

    @ApiModelProperty(value = "消费赚取积分")
    private Integer gainIntegral;

    @ApiModelProperty(value = "使用积分")
    private Integer useIntegral;

    @ApiModelProperty(value = "给用户退了多少积分")
    private Integer backIntegral;

    @ApiModelProperty(value = "备注")
    private String mark;

    @TableLogic
    @ApiModelProperty(value = "是否删除")
    private Boolean isDel;

    @ApiModelProperty(value = "管理员备注")
    private String remark;

    @ApiModelProperty(value = "商户ID")
    private Integer merId;

    private Integer isMerCheck;

    @ApiModelProperty(value = "拼团商品id0一般商品")
    private Integer combinationId;

    @ApiModelProperty(value = "拼团id 0没有拼团")
    private Integer pinkId;

    @ApiModelProperty(value = "成本价")
    private BigDecimal cost;

    @ApiModelProperty(value = "秒杀商品ID")
    private Integer seckillId;

    @ApiModelProperty(value = "砍价id")
    private Integer bargainId;

    @ApiModelProperty(value = "用户砍价活动id")
    private Integer bargainUserId;

    @ApiModelProperty(value = "核销码")
    private String verifyCode;

    @ApiModelProperty(value = "门店id")
    private Integer storeId;

    @ApiModelProperty(value = "配送方式: 1=快递、2=门店自提、3=无需配送")
    private Integer shippingType;

    @ApiModelProperty(value = "店员id")
    private Integer clerkId;

    @ApiModelProperty(value = "支付渠道(0-微信公众号,1-微信小程序,2-H5,3-余额,4-微信AppIos,5-微信App安卓," +
            "6-支付宝app支付(安卓端)、7-支付宝app支付(苹果端)、8-支付宝网页支付(h5网页端)、9-积分支付")
    private Integer isChannel;

    @ApiModelProperty(value = "消息提醒")
    private Boolean isRemind;

    @ApiModelProperty(value = "后台是否删除")
    private Boolean isSystemDel;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "快递公司简称")
    private String deliveryCode;

    @ApiModelProperty(value = "订单类型: 0-普通订单、1-视频号订单、2-区域代理订单、3-零售商订单、4-供应商订单")
    private Integer type;

    //积分兑换使用
    @ApiModelProperty(value = "积分兑换商品id")
    private Integer integalId;

    @ApiModelProperty(value = "兑换积分")
    private Integer integal;

    @ApiModelProperty(value = "微信支付订单号/支付宝支付订单号")
    private String transactionId;

    @ApiModelProperty(value = "是否参与佣金分红")
    private Boolean isBrokerageAbonus;

    @ApiModelProperty(value = "发票抬头表ID标识")
    private Integer riseId;

    @ApiModelProperty(value = "推广人id(表示这个订单是这个人推广的)")
    private Integer spreadUid;

    @ApiModelProperty(value = "服务商表ID标识")
    private Integer spId;

    @ApiModelProperty(value = "服务商二级商户表ID标识")
    private Integer sptlId;

    @ApiModelProperty(value = "是否需要给联盟商家发货")
    private Boolean isLmsjfahuo;

    @ApiModelProperty(value = "额度控制-Constants.java")
    private Integer quotaControl;

}
