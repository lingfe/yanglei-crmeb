package com.zbkj.crmeb.store.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.store.model.StoreProductCoupon;

import java.util.List;

/**
 * StoreProductCouponService 接口
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
public interface StoreProductCouponService extends IService<StoreProductCoupon> {
    /**
     * 根据产品id删除 优惠券关联信息
     * @param productId 产品id
     */
    boolean deleteByProductId(Integer productId);

    /**
     * 批量保存商品优惠券关联信息
     * @param storeProductCoupons 优惠券关联信息
     * @return 保存结果
     */
    void saveCoupons(List<StoreProductCoupon> storeProductCoupons);

    /**
     * 根据商品id获取已关联优惠券信息
     * @param productId 商品id
     * @return 已关联优惠券
     */
    List<StoreProductCoupon> getListByProductId(Integer productId);
}
