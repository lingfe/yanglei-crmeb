package com.zbkj.crmeb.front.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 首页商品响应类
 * @author: 零风
 * @CreateDate: 2022/6/24 13:56
 */
@Data
@ApiModel(value="IndexProductResponse-IndexProductResponse", description="首页商品响应类")
public class IndexProductResponse {

    @ApiModelProperty(value = "商品id")
    private Integer id;

    @ApiModelProperty(value = "商品图片")
    private String image;

    @ApiModelProperty(value = "商品名称")
    private String storeName;

    @ApiModelProperty(value = "商品价格")
    private BigDecimal price;

    @ApiModelProperty(value = "市场价")
    private BigDecimal otPrice;

    @ApiModelProperty(value = "销量")
    private Integer sales;

    @ApiModelProperty(value = "虚拟销量")
    private Integer ficti;

    @ApiModelProperty(value = "单位名")
    private String unitName;

    @ApiModelProperty(value = "活动显示排序0=默认，1=秒杀，2=砍价，3=拼团")
    private String activity;

    @ApiModelProperty(value = "为移动端特定参数")
    private ProductActivityItemResponse activityH5;

    @ApiModelProperty(value = "商品控制: 0=(不增不减)普通正常、1=支付不增兑换减少额度、2=不返酒米、3=增加额度、4=支付增加额度兑换减少")
    private Integer control;

}
