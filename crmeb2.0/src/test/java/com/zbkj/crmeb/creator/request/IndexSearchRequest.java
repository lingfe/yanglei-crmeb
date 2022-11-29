package com.zbkj.crmeb.creator.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 首页搜索请求类
 * @author: 零风
 * @CreateDate: 2022/7/1 16:09
 */
@Data
public class IndexSearchRequest {

    @ApiModelProperty(value = "关键字搜索：用户口令/名称")
    private String keywords;

}
