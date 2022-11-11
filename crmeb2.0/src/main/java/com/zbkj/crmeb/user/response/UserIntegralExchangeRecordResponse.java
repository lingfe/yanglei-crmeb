package com.zbkj.crmeb.user.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户积分兑换记录-响应对象
 */
@Data
public class UserIntegralExchangeRecordResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "id标识")
    private Integer id;

    @ApiModelProperty(value = "用户id")
    private Integer userId;//            `user_id` int(11) NULL DEFAULT NULL COMMENT '用户id',

    @ApiModelProperty(value = "用户昵称")
    private String nickName;

    @ApiModelProperty(value = "兑换订单ID")
    private Integer orderId;//            `order_id` int(11) NULL DEFAULT NULL COMMENT '兑换订单ID',

    @ApiModelProperty(value = "兑换积分")
    private Integer integral;//            `integral` int(11) NULL DEFAULT NULL COMMENT '兑换积分',

    @ApiModelProperty(value = "兑换积分商品ID")
    private Integer integralId;//            `integral_id` int(11) NULL DEFAULT NULL COMMENT '兑换积分商品ID',

    @ApiModelProperty(value = "兑换积分商品名称")
    private String integralProductName;//            `integral_id` int(11) NULL DEFAULT NULL COMMENT '兑换积分商品ID',

    @ApiModelProperty(value = "主商品ID")
    private Integer productId;//            `product_id` int(11) NULL DEFAULT NULL COMMENT '主商品ID',

    @ApiModelProperty(value = "创建时间")
    private Date crtDatetime;//            `crt_datetime` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',

    @ApiModelProperty("兑换积分商品图片")
    private String img;

    @ApiModelProperty("支付金额")
    private BigDecimal price;
}
