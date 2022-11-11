package com.zbkj.crmeb.retailer.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *  零售商表-搜索请求类
 * @author: 零风
 * @CreateDate: 2021/10/18 14:06
 */
@Data
public class RetailerSearchRequest {

    @ApiModelProperty(value = "搜索关键字")
    private String keywords;

    @ApiModelProperty(value = "状态，0-审核中，1-审核通过，2-不通过，-1=全部")
    private Integer status;

    @ApiModelProperty(value = "区域代理表ID标识,为空或0=默认全部")
    private Integer raId;

    @ApiModelProperty(value = "是否销售，false=下架，true=上架")
    private Boolean isSale=Boolean.FALSE;
}
