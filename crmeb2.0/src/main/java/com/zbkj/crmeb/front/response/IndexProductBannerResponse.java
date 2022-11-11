package com.zbkj.crmeb.front.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * 顶部轮播图-Response
 * @author: 零风
 * @CreateDate: 2022/1/11 10:28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class IndexProductBannerResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "顶部轮播图")
    private List<HashMap<String, Object>> banner;

    @ApiModelProperty(value = "商品")
    private List<ProductResponse> list;
}
