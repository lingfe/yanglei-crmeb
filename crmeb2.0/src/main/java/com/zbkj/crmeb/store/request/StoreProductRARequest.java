package com.zbkj.crmeb.store.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 产品代理表-请求类
 * @author: 零风
 * @CreateDate: 2021/11/24 10:45
 */
@Data
public class StoreProductRARequest {

    @ApiModelProperty(value = "商品ID标识,多个用逗号隔开")
    private String productIds;

    @ApiModelProperty(value = "区域代理ID标识，多个用逗号隔开")
    private String raIds;


}
