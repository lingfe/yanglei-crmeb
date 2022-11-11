package com.zbkj.crmeb.store.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
* 商品属性值表
* @author: 零风
* @CreateDate: 2021/10/21 15:36
*/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_store_product_attr_value")
@ApiModel(value="StoreProductAttrValue对象", description="商品属性值表")
public class StoreProductAttrValue implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "attrId")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "商品ID")
    private Integer productId;

    @ApiModelProperty(value = "商品属性sku,用逗号隔开")
    private String suk;

    @ApiModelProperty(value = "属性对应的库存")
    private Integer stock;

    @ApiModelProperty(value = "销量")
    private Integer sales;

    @ApiModelProperty(value = "属性金额")
    private BigDecimal price;

    @ApiModelProperty(value = "图片")
    private String image;

    @TableField(value = "`unique`")
    @ApiModelProperty(value = "唯一值")
    private String unique;

    @ApiModelProperty(value = "成本价")
    private BigDecimal cost;

    @ApiModelProperty(value = "商品条码")
    private String barCode;

    @ApiModelProperty(value = "原价")
    private BigDecimal otPrice;

    @ApiModelProperty(value = "重量")
    private BigDecimal weight;

    @ApiModelProperty(value = "体积")
    private BigDecimal volume;

    @ApiModelProperty(value = "一级返佣")
    private BigDecimal brokerage;

    @ApiModelProperty(value = "二级返佣")
    private BigDecimal brokerageTwo;

    @ApiModelProperty(value = "活动类型 0=商品，1=秒杀，2=砍价，3=拼团，4=积分兑换商品")
    private Integer type;

    @ApiModelProperty(value = "活动限购数量")
    private Integer quota;

    @ApiModelProperty(value = "活动限购数量显示")
    private Integer quotaShow;

    @ApiModelProperty(value = "产品属性值和属性名对应关系")
    private String attrValue;

    @ApiModelProperty(value = "区域代理返佣比例%")
    private BigDecimal ugaBrokerage;

    @ApiModelProperty(value = "区域代理返佣金额￥")
    private BigDecimal ugaPrice;

    @ApiModelProperty(value = "囎送积分")
    private BigDecimal integral;

    @ApiModelProperty(value = "返推荐人首单奖励佣金")
    private BigDecimal firstOrderBrokerage;

}
