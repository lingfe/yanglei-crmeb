package com.zbkj.crmeb.finance.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 发票记录表-搜索请求类
 * @author: 零风
 * @CreateDate: 2022/4/14 15:14
 */
@Data
public class InvoiceRecordSearchRequest {

    @ApiModelProperty(value = "关键字")
    private String keywords;

    @ApiModelProperty(value = "状态: 0=未开票、1=待处理、2=已开票")
    private Integer status;

}
