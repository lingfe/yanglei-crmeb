package com.zbkj.crmeb.creator.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 首页数据请求类
 * @author: 零风
 * @CreateDate: 2022/7/1 15:42
 */
@Data
public class IndexDataRequest {

    @ApiModelProperty(value = "首页轮播图组合数据ID")
    private Integer gidIndexLbt;

    @ApiModelProperty(value = "首页推荐图片列表分类ID")
    private Integer typeId;

}
