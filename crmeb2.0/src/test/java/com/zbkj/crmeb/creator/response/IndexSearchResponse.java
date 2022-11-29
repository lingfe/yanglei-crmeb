package com.zbkj.crmeb.creator.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 首页搜索-响应类
 * @author: 零风
 * @CreateDate: 2022/7/1 16:09
 */
@Data
public class IndexSearchResponse {

    @ApiModelProperty(value = "是否搜索成功")
    private Boolean isSearchOk=Boolean.FALSE;

    @ApiModelProperty(value = "搜索出来的创作者和他的作品信息")
    private List<RemenUserResponse> data;

}
