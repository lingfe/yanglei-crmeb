package com.zbkj.crmeb.store.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: crmeb
 * @description: 商品信息-excel-导入导出-响应类
 * @author: 零风
 * @create: 2021-07-29 14:38
 **/
@Data
public class StoreProductExcelResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "导入-成功数量")
    private Integer okNum=0;

    @ApiModelProperty(value = "导入-失败数量")
    private Integer loseNum=0;

    @ApiModelProperty(value = "导入-重复数量")
    private Integer readerNum=0;
}
