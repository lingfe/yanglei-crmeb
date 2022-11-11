package com.zbkj.crmeb.store.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 商品点赞和收藏表-搜索请求类
 * @author: 零风
 * @CreateDate: 2022/3/4 17:13
 */
@Data
public class StoreProductRelationSearchRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "类型(收藏(collect）、点赞(like))")
    private String type;

    @ApiModelProperty(value = "用户ID")
    @NotBlank(message = "用户id不能为空")
    private Integer uid;

}
