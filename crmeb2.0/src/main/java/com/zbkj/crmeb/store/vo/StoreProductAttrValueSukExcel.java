package com.zbkj.crmeb.store.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @program: crmeb
 * @description: 属性值-suk
 * @author: 零风
 * @create: 2021-07-29 13:52
 **/
@Data
public class StoreProductAttrValueSukExcel {

    @ApiModelProperty(value = "属性值ID")
    private Integer attrValueId=0;

    @ApiModelProperty(value = "属性名")
    private String attrName="选项";

    @ApiModelProperty(value = "属性值")
    private String attrValue="(必填)";

}
