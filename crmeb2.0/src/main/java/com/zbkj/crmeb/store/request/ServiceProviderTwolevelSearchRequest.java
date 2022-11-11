package com.zbkj.crmeb.store.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 服务商二级商户表-搜索请求类
 * @author: 零风
 * @CreateDate: 2022/5/9 15:11
 */
@Data
public class ServiceProviderTwolevelSearchRequest {

    @ApiModelProperty(value = "关键字")
    private String keywords;

    @ApiModelProperty(value = "服务商表ID标识")
    private Integer spId;

}
