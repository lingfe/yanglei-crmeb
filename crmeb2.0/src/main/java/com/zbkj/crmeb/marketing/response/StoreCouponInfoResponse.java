package com.zbkj.crmeb.marketing.response;

import com.zbkj.crmeb.category.model.Category;
import com.zbkj.crmeb.marketing.request.StoreCouponRequest;
import com.zbkj.crmeb.store.model.StoreProduct;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 优惠券详情-响应类
 * @author: 零风
 * @CreateDate: 2022/3/4 17:00
 */
@Data
public class StoreCouponInfoResponse implements Serializable {

    private static final long serialVersionUID=1L;

    public StoreCouponInfoResponse(StoreCouponRequest coupon, List<StoreProduct> product, List<Category> category) {
        this.coupon = coupon;
        this.product = product;
        this.category = category;
    }

    @ApiModelProperty(value = "优惠券信息")
    private StoreCouponRequest coupon;

    @ApiModelProperty(value = "商品信息")
    private List<StoreProduct> product;

    @ApiModelProperty(value = "分类信息")
    private List<Category> category;

}
