package com.zbkj.crmeb.log.response;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @program: crmeb
 * @description: 商品日志-响应类
 * @author: 零风
 * @create: 2021-07-30 12:01
 **/
@Data
public class StoreProductLogResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "ID标识")
    private Integer id;

    @ApiModelProperty(value = "类型visit,cart,order,pay,collect,refund")
    private String type;

    @ApiModelProperty(value = "商品ID")
    private Integer productId;

    @ApiModelProperty(value = "用户ID")
    private Integer uid;
    @ApiModelProperty(value = "用户名称")
    private String userName;
    @ApiModelProperty(value = "用户头像")
    private String avatar;

    @ApiModelProperty(value = "是否浏览")
    private Boolean visitNum;

    @ApiModelProperty(value = "加入购物车数量")
    private Integer cartNum;

    @ApiModelProperty(value = "下单数量")
    private Integer orderNum;

    @ApiModelProperty(value = "支付数量")
    private Integer payNum;

    @ApiModelProperty(value = "支付金额")
    private BigDecimal payPrice;

    @ApiModelProperty(value = "商品成本价")
    private BigDecimal costPrice;

    @ApiModelProperty(value = "支付用户ID")
    private Integer payUid;

    @ApiModelProperty(value = "退款数量")
    private Integer refundNum;

    @ApiModelProperty(value = "退款金额")
    private BigDecimal refundPrice;

    @ApiModelProperty(value = "收藏")
    private Boolean collectNum;

    @ApiModelProperty(value = "添加时间")
    private Long addTime;

}
