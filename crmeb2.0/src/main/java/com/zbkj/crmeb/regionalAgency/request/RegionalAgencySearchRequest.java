package com.zbkj.crmeb.regionalAgency.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 区域代理-搜索请求类
 * @author: 零风
 * @CreateDate: 2021/11/6 10:36
 */
@Data
public class RegionalAgencySearchRequest {

    @ApiModelProperty(value = "搜索关键字: 商品id或者名称")
    private String keywords;

}
