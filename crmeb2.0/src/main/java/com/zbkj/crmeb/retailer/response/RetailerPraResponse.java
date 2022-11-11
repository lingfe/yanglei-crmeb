package com.zbkj.crmeb.retailer.response;

import com.zbkj.crmeb.retailer.model.RetailerPra;
import com.zbkj.crmeb.store.model.StoreProduct;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 零售商产品代理表-响应类
 * @author: 零风
 * @CreateDate: 2021/12/2 15:49
 */
@Data
public class RetailerPraResponse {

    @ApiModelProperty(value = "零售商产品代理表")
    private RetailerPra retailerPra;

    @ApiModelProperty(value = "商品信息表")
    private StoreProduct storeProduct;

}
