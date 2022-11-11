package com.zbkj.crmeb.finance.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


/**
 * 分账表-搜索请求类
 * @author: 零风
 * @CreateDate: 2022/1/21 10:46
 */
@Data
public class SplitAccountRecordSearchRequest implements Serializable {

    @ApiModelProperty(value = "搜索关键字")
    private String keywords;

}
