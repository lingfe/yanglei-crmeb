package com.zbkj.crmeb.user.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: crmeb
 * @description: 积分兑换记录表
 * @author: 零风
 * @create: 2021-07-07 15:06
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_user_integral_exchange_record")
@ApiModel(value="UserIntegralExchangeRecord-对象", description="用户积分兑换记录表")
public class UserIntegralExchangeRecord implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "用户积分兑换记录表Id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;//    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '积分兑换记录表',

    @ApiModelProperty(value = "用户id")
    private Integer userId;//            `user_id` int(11) NULL DEFAULT NULL COMMENT '用户id',

    @ApiModelProperty(value = "兑换订单ID")
    private Integer orderId;//            `order_id` int(11) NULL DEFAULT NULL COMMENT '兑换订单ID',

    @ApiModelProperty(value = "兑换积分")
    private Integer integral;//            `integral` int(11) NULL DEFAULT NULL COMMENT '兑换积分',

    @ApiModelProperty(value = "兑换积分商品ID")
    private Integer integralId;//            `integral_id` int(11) NULL DEFAULT NULL COMMENT '兑换积分商品ID',

    @ApiModelProperty(value = "主商品ID")
    private Integer productId;//            `product_id` int(11) NULL DEFAULT NULL COMMENT '主商品ID',

    @ApiModelProperty(value = "创建时间")
    private Long crtDatetime;//            `crt_datetime` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',

}
