package com.zbkj.crmeb.store.response;

import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.store.model.StoreBrands;
import com.zbkj.crmeb.store.model.StoreProduct;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * @program: crmeb
 * @description: 品牌-优选-响应对象
 * @author: 零风
 * @create: 2021-07-27 14:27
 **/
@Data
public class StoreBrandsPreferredRsponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "品牌优选-详情-广告banner")
    private List<HashMap<String,Object>> infoGuangaoBanner;

    @ApiModelProperty(value = "品牌优选-详情-推荐分类")
    private List<HashMap<String,Object>> infoType;

    @ApiModelProperty(value = "品牌优选-品牌信息-list")
    private List<StoreBrands> brandsPreferredList;

    @ApiModelProperty(value = "品牌优选-推荐-商品列表-list")
    private PageInfo<StoreProduct> pageStoreProductList;
}
