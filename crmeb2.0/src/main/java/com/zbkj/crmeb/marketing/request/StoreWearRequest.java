package com.zbkj.crmeb.marketing.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 *  穿搭表请求类
 * @author: 零风
 * @CreateDate: 2021/10/8 10:59
 */
@Data
@Builder
public class StoreWearRequest {

    @ApiModelProperty(value = "穿搭表ID标识")
    private Integer id;

    @ApiModelProperty(value = "主商品ID")
    private Integer productId;

    @ApiModelProperty(value = "主图")
    private String img;

    @ApiModelProperty(value = "穿搭名称")
    private String wearName;

    @ApiModelProperty(value = "穿搭商品ID，用英文逗号隔开")
    private String wearProductIds;

    @ApiModelProperty(value = "是否删除")
    private Boolean isDel;

    @ApiModelProperty(value = "是否显示")
    private Boolean isShow;

    @ApiModelProperty(value = "是否展示在首页")
    private Boolean isIndex;

}
