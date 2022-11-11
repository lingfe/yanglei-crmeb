package com.zbkj.crmeb.marketing.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *  穿搭-搜索请求类
 * @author: 零风
 * @CreateDate: 2021/10/8 11:07
 */
@Data
public class StoreWearSearchRequest {

    @ApiModelProperty(value = "搜索关键字 商品id或者名称")
    private String keywords;

    @ApiModelProperty(value = "是否显示，-1=全部，1=显示，0=关闭")
    private Integer isShow;

    @ApiModelProperty(value = "是否展示在首页，-1=全部，1=是，0=否")
    private Integer isIndex;

}
