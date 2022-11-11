package com.zbkj.crmeb.front.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单详情响应对象
 * @author: 零风
 * @CreateDate: 2022/3/18 13:55
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="OrderInfoResponse对象", description="订单详情响应对象")
public class OrderInfoResponse implements Serializable {

    private static final long serialVersionUID=1L;

//    @ApiModelProperty(value = "订单id")
//    private Integer orderId;
    @ApiModelProperty(value = "attrId")
    private Integer attrId;

    @ApiModelProperty(value = "商品ID")
    private Integer productId;

//    @ApiModelProperty(value = "购买东西的详细信息")
//    private StoreCartResponse info;

    @ApiModelProperty(value = "商品数量")
    private Integer cartNum;

//    @ApiModelProperty(value = "唯一id")
//    @TableField(value = "`unique`")
//    private String unique;

    @ApiModelProperty(value = "商品图片")
    private String image;

    @ApiModelProperty(value = "商品名称")
    private String storeName;

    @ApiModelProperty(value = "商品价格")
    private BigDecimal price;

    @ApiModelProperty(value = "是否评价")
    private Integer isReply;

    @ApiModelProperty(value = "规格属性值")
    private String sku;
}
