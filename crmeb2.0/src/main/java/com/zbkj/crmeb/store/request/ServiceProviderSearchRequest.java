package com.zbkj.crmeb.store.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 服务商表-搜索请求类
 * @author: 零风
 * @CreateDate: 2022/5/9 14:48
 */
@Data
public class ServiceProviderSearchRequest {

    @ApiModelProperty(value = "关键字")
    private String keywords;

}
