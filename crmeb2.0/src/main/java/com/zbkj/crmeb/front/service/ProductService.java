package com.zbkj.crmeb.front.service;

import com.common.CommonPage;
import com.common.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.category.vo.CategoryTreeVo;
import com.zbkj.crmeb.front.request.IndexStoreProductSearchRequest;
import com.zbkj.crmeb.front.request.ProductRequest;
import com.zbkj.crmeb.front.response.*;

import java.util.List;

/**
 * IndexService-接口
 * @author: 零风
 * @CreateDate: 2022/1/11 10:56
 */
public interface ProductService {

    /**
     * 首页-得到商品分页列表
     * @param pageParamRequest 分页
     * @param request 请求搜索参数
     * @Author 零风
     * @Date  2022/1/11
     * @return
     */
    CommonPage<ProductResponse> getIndexProduct(IndexStoreProductSearchRequest request, PageParamRequest pageParamRequest);

    List<CategoryTreeVo> getCategory();

    CommonPage<IndexProductResponse> getList(ProductRequest request, PageParamRequest pageParamRequest);

    /**
     * 获取商品详情
     * @param id 商品编号
     * @param type normal-正常，void-视频
     * @return 商品详情信息
     */
    ProductDetailResponse getDetail(Integer id, String type);

    /**
     * 商品评论列表
     * @param proId 商品编号
     * @param type 评价等级|0=全部,1=好评,2=中评,3=差评
     * @param pageParamRequest 分页参数
     * @return PageInfo<ProductReplyResponse>
     */
    PageInfo<ProductReplyResponse> getReplyList(Integer proId, Integer type, PageParamRequest pageParamRequest);

    StoreProductReplayCountResponse getReplyCount(Integer id);

    /**
     * 获取热门推荐商品列表
     * @param pageRequest 分页参数
     * @return CommonPage<IndexProductResponse>
     */
    CommonPage<IndexProductResponse> getHotProductList(PageParamRequest pageRequest);

    /**
     * 商品详情评论
     * @param id 商品id
     * @return ProductDetailReplyResponse
     */
    ProductDetailReplyResponse getProductReply(Integer id);

    /**
     * 优选商品推荐
     * @return CommonPage<IndexProductResponse>
     */
    CommonPage<IndexProductResponse> getGoodProductList();

}
