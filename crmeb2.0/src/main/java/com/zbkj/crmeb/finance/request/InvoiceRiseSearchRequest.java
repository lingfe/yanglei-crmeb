package com.zbkj.crmeb.finance.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 发票抬头表-搜索请求类
 * @author: 零风
 * @CreateDate: 2022/4/14 15:14
 */
@Data
public class InvoiceRiseSearchRequest {

    @ApiModelProperty(value = "关键字")
    private String keywords;

}
