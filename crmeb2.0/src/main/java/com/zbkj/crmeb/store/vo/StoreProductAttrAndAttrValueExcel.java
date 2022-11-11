package com.zbkj.crmeb.store.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: crmeb
 * @description: 属性与属性值关系
 * @author: 零风
 * @create: 2021-07-29 10:57
 **/
@Data
public class StoreProductAttrAndAttrValueExcel implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "序号")
    private Integer id=0;

    @ApiModelProperty(value = "属性值ID")
    private Integer attrValueId=0;

    @ApiModelProperty(value = "属性名")
    private String attrName="选项";

    @ApiModelProperty(value = "属性值")
    private String attrValues="(必填)";

}
