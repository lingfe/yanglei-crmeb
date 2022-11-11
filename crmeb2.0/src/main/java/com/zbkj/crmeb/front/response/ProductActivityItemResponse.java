package com.zbkj.crmeb.front.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 商品所参与的活动类型-响应类
 * @author: 零风
 * @CreateDate: 2022/6/24 13:57
 */
@Data
public class ProductActivityItemResponse {

    @ApiModelProperty(value = "参与活动id")
    private Integer id;

    @ApiModelProperty(value = "秒杀结束时间")
    private Integer time;

    @ApiModelProperty(value = "活动参与类型:1=秒杀，2=砍价，3=拼团")
    private String type;
}
