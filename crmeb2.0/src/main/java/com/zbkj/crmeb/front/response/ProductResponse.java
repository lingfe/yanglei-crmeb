package com.zbkj.crmeb.front.response;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 商品表-响应类
 * @author: 零风
 * @CreateDate: 2022/1/11 11:03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ProductResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "商品id")
    @TableId(value = "id")
    private Integer id;

    @ApiModelProperty(value = "商品图片")
    private String image;

    @ApiModelProperty(value = "商品名称")
    private String storeName;

    @ApiModelProperty(value = "分类id")
    private List<Integer> cateId;

    @ApiModelProperty(value = "商品价格")
    private BigDecimal price;

    @ApiModelProperty(value = "市场价")
    private BigDecimal otPrice;

    @ApiModelProperty(value = "单位名")
    private String unitName;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "销量")
    private Integer sales;

    @ApiModelProperty(value = "虚拟销量")
    private Integer ficti;

    @ApiModelProperty(value = "库存")
    private Integer stock;

    @ApiModelProperty(value = "活动显示排序0=默认，1=秒杀，2=砍价，3=拼团")
    private String activity;

    @ApiModelProperty(value = "为移动端特定参数")
    private ProductActivityItemResponse activityH5;

    @ApiModelProperty(value = "商品控制: 0=(不增不减)普通正常、1=支付不增兑换减少额度、2=不返酒米、3=增加额度、4=支付增加额度兑换减少")
    private Integer control;
}
