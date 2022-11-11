package com.zbkj.crmeb.store.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;

/**
 * 商品属性值表-请求类
 * @author: 零风
 * @CreateDate: 2022/3/29 13:46
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="StoreProductAttrValueRequest-商品属性值表-请求类", description="商品属性值表请求类")
public class StoreProductAttrValueRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "ID")
    private Integer id;

    @ApiModelProperty(value = "商品ID", example = "0")
    @Min(value = 0, message = "请选择商品")
    private Integer productId;

    @ApiModelProperty(value = "商品属性sku,用逗号隔开")
    private String suk;
    @ApiModelProperty(value = "商品属性索引值 (attr_value|attr_value[|....])")
    private String suks;

    @ApiModelProperty(value = "属性对应的库存")
    private Integer stock;

    @ApiModelProperty(value = "销量")
    private Integer sales;

    @ApiModelProperty(value = "属性金额")
    private BigDecimal price;

    @ApiModelProperty(value = "图片")
    private String image;

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

    @ApiModelProperty(value = "活动类型 0=商品，1=秒杀，2=砍价，3=拼团")
    private Integer type;

    @ApiModelProperty(value = "活动限购数量")
    private Integer quota;

    @ApiModelProperty(value = "活动限购数量显示")
    private Integer quotaShow;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "attrValue字段，前端传递后用作sku字段")
    private LinkedHashMap<String,String> attrValue;

    @ApiModelProperty(value = "是否选中-秒杀用")
    private Boolean checked;

    @ApiModelProperty(value = "砍价商品最低价")
    private BigDecimal minPrice;

    @ApiModelProperty(value = "区域代理返佣比例%")
    private BigDecimal ugaBrokerage;

    @ApiModelProperty(value = "区域代理返佣金额￥")
    private BigDecimal ugaPrice;

    @ApiModelProperty(value = "囎送积分")
    @NotNull(message = "囎送积分不能为空")
    private BigDecimal integral;

    @ApiModelProperty(value = "返推荐人首单奖励佣金")
    private BigDecimal firstOrderBrokerage;

}
