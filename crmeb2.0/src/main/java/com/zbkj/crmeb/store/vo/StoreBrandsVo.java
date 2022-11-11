package com.zbkj.crmeb.store.vo;

import com.zbkj.crmeb.category.model.Category;
import com.zbkj.crmeb.store.model.StoreBrands;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @program: crmeb
 * @description: Vo详情对象
 * @author: 零风
 * @create: 2021-06-24 11:05
 **/
@Data
public class StoreBrandsVo {

    @ApiModelProperty(value = "品牌信息")
    private StoreBrands sbrandsInfo;

    @ApiModelProperty(value = "品牌对应的分类list")
    private List<Category> cateList;
}
