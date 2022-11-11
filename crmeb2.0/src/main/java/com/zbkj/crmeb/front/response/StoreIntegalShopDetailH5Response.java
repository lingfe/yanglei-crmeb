package com.zbkj.crmeb.front.response;

import com.zbkj.crmeb.marketing.response.StoreIntegalShopResponse;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
* 积分兑换商品-详情-H5用户端-响应参数
* @author: 零风
* @Version: 1.0
* @CreateDate: 2021/7/5 10:04
* @return： StoreIntegalShopDetailH5Response.java
**/
@Data
public class StoreIntegalShopDetailH5Response implements Serializable {

    private static final long serialVersionUID = -885733985825623484L;

    @ApiModelProperty(value = "产品属性")
    private List<ProductAttrResponse> productAttr;

    @ApiModelProperty(value = "商品属性详情")
    private HashMap<String,Object> productValue;

    @ApiModelProperty(value = "收藏标识")
    private Boolean userCollect;

    @ApiModelProperty(value = "积分兑换商品表信息")
    private StoreIntegalShopResponse sisDetalH5Response;
}
