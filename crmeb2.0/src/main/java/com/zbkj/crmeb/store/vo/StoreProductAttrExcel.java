package com.zbkj.crmeb.store.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: crmeb
 * @description: 商品-属性-excel导入导出类
 * @author: 零风
 * @create: 2021-07-23 11:34
 **/
@Data
public class StoreProductAttrExcel implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "属性ID(不填)")
    private Integer id=0;

    @ApiModelProperty(value = "商品ID(必填)")
    private Integer productId=0;

    @ApiModelProperty(value = "属性名(必填)")
    private String attrName="选项";

}
