package com.zbkj.crmeb.store.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @program: crmeb
 * @description: 商品-详情-Excel导入导出类
 * @author: 零风
 * @create: 2021-08-02 16:31
 **/
@Data
public class StoreProductInfoExcel {

    @ApiModelProperty(value = "商品ID")
    private Integer productId=0;

    @ApiModelProperty(value = "图片路径")
    private String imgPath="https://bing.ioliu.cn/v1/rand";

}
