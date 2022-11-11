package com.zbkj.crmeb.front.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
* 商品表-首页搜索请求类
* @author: 零风
* @CreateDate: 2021/11/8 14:09
*/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class IndexStoreProductSearchRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "类型（1：出售中（已上架），2：仓库中（未上架），3：已售罄，4：警戒库存，5：回收站）")
    @NotNull
    @Min(value = 1, message = "类型不能小于1")
    @Max(value = 5, message = "类型不能大于5")
    private int type;

    @ApiModelProperty(value = "分类ID， 多个逗号分隔")
    private List<Integer> cateId;

    @ApiModelProperty(value = "关键字搜索， 支持(商品名称, 商品简介, 关键字, 商品条码)")
    private String keywords;

    @ApiModelProperty(value = "是否精品")
    private Boolean isBest = null;

    @ApiModelProperty(value = "是否热门")
    private Boolean isHot = null;

    @ApiModelProperty(value = "是否新品")
    private Boolean isNew = null;

    @ApiModelProperty(value = "是否优惠")
    private Boolean isBenefit = null;

    @ApiModelProperty(value = "价格排序", allowableValues = "range[asc,desc]")
    private String priceOrder;

    @ApiModelProperty(value = "销量排序", allowableValues = "range[asc,desc]")
    private String salesOrder;

    @ApiModelProperty(value = "是否优品推荐")
    private Boolean isGood;

}
