package com.zbkj.crmeb.creator.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 创作者详细主页-搜索QQ类
 * @author: 零风
 * @CreateDate: 2022/7/1 16:09
 */
@Data
public class InfoCreatorSearchRequest {

    @ApiModelProperty(value = "创作者用户ID标识")
    private Integer uid;

    @ApiModelProperty(value = "标签表ID")
    private Integer tagId;

    @ApiModelProperty(value = "分类表ID标识")
    private Integer pid;

}
