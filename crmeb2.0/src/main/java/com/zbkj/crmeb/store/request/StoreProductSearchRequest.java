package com.zbkj.crmeb.store.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 商品表-搜索请求类
 * @author: 零风
 * @CreateDate: 2021/12/30 10:11
 */
@Data
public class StoreProductSearchRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "类型（1：出售中（已上架），2：仓库中（未上架），3：已售罄，4：警戒库存，5：回收站）")
    @NotNull
    @Min(value = 1, message = "类型不能小于1")
    @Max(value = 5, message = "类型不能大于5")
    private int type;

    @ApiModelProperty(value = "分类ID， 多个逗号分隔")
    private String cateId;

    @ApiModelProperty(value = "关键字搜索， 支持(商品名称, 商品简介, 关键字, 商品条码)")
    private String keywords;

    @ApiModelProperty(value = "是否热卖")
    private Boolean isHot;

    @ApiModelProperty(value = "是否优惠")
    private Boolean isBenefit;

    @ApiModelProperty(value = "是否精品")
    private Boolean isBest;

    @ApiModelProperty(value = "是否新品")
    private Boolean isNew;

    @ApiModelProperty(value = "是否推荐")
    private Boolean isGood;

    @ApiModelProperty(value = "是否为区域代理商-批发商品")
    private Boolean isRegionalAgent=Boolean.FALSE;
    @ApiModelProperty(value = "价格排序", allowableValues = "range[asc,desc]")
    private String priceOrder;
    @ApiModelProperty(value = "销量排序", allowableValues = "range[asc,desc]")
    private String salesOrder;

    @ApiModelProperty(value = "区域代理ID标识")
    private Integer raId;

    @ApiModelProperty(value = "供应商ID标识")
    private Integer supplierId;

    @ApiModelProperty(value = "是否已绑定供应商：0=全部、1=已绑定、2=未绑定")
    private Integer isBindSupplier=0;

}
