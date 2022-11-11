package com.zbkj.crmeb.marketing.response;

import com.zbkj.crmeb.store.model.StoreProduct;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 *  穿搭详情-响应类
 * @author: 零风
 * @CreateDate: 2021/10/8 15:29
 */
@Data
public class StoreWearResponse {

    @ApiModelProperty(value = "穿搭表ID标识")
    private Integer id;

    @ApiModelProperty(value = "主商品ID")
    private Integer productId;
    @ApiModelProperty(value = "主商品信息")
    private StoreProduct storeProduct;

    @ApiModelProperty(value = "主图")
    private String img;

    @ApiModelProperty(value = "穿搭名称")
    private String wearName;

    @ApiModelProperty(value = "穿搭商品ID，用英文逗号隔开")
    private String wearProductIds;
    @ApiModelProperty(value = "穿搭商品list")
    private List<StoreProduct> wearProductList;

    @ApiModelProperty(value = "是否显示： false：否，true：是")
    private Boolean isShow;

    @ApiModelProperty(value = "是否展示在首页： false：否，true：是")
    private Boolean isIndex;
}
