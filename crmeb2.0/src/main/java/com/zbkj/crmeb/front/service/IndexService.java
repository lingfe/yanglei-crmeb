package com.zbkj.crmeb.front.service;

import com.common.CommonPage;
import com.common.PageParamRequest;
import com.zbkj.crmeb.front.response.IndexInfoResponse;
import com.zbkj.crmeb.front.response.IndexProductBannerResponse;
import com.zbkj.crmeb.front.response.IndexProductResponse;

import java.util.HashMap;
import java.util.List;

/**
 * IndexService-接口
 * @author: 零风
 * @CreateDate: 2022/1/11 10:27
 */
public interface IndexService {

    /**
     * 首页产品的轮播图和产品信息
     * @param pageParamRequest 分页参数
     * @param type 类型
     * @Author 零风
     * @Date  2022/1/11
     * @return 结果
     */
    IndexProductBannerResponse getProductBanner(int type, PageParamRequest pageParamRequest);

    /**
     * 首页信息
     * @return IndexInfoResponse
     */
    IndexInfoResponse getIndexInfo();

    List<HashMap<String, Object>> hotKeywords();

    HashMap<String, String> getShareConfig();

    /**
     * 获取首页商品列表
     *
     * @param type             类型 【1 精品推荐 2 热门榜单 3首发新品 4促销单品】
     * @param pageParamRequest 分页参数
     * @return List
     */
    CommonPage<IndexProductResponse> findIndexProductList(Integer type, PageParamRequest pageParamRequest);
}
