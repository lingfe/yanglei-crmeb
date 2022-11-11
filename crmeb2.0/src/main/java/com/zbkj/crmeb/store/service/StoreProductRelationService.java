package com.zbkj.crmeb.store.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.zbkj.crmeb.front.request.UserCollectAllRequest;
import com.zbkj.crmeb.front.response.UserRelationResponse;
import com.zbkj.crmeb.store.model.StoreProduct;
import com.zbkj.crmeb.store.model.StoreProductRelation;
import com.zbkj.crmeb.store.request.StoreProductRelationSearchRequest;

import java.util.List;

/**
 * 商品点赞和收藏表-service层接口
 * @author: 零风
 * @CreateDate: 2022/3/4 17:11
 */
public interface StoreProductRelationService extends IService<StoreProductRelation> {

    /**
     * 得到-某个用户收藏点赞的商品列表
     * @param request           请求参数
     * @param pageParamRequest  分页
     * @Author 零风
     * @Date  2022/3/4
     * @return 商品列表
     */
    List<StoreProduct> getList(StoreProductRelationSearchRequest request, PageParamRequest pageParamRequest);

    List<StoreProductRelation> getList(Integer productId, String type);

    /**
     * 取消收藏
     * @param requestJson 收藏idsJson
     * @return Boolean
     */
    Boolean delete(String requestJson);

    boolean all(UserCollectAllRequest request);

    List<StoreProductRelation> getLikeOrCollectByUser(Integer userId, Integer productId,boolean isLike);

    /**
     * 获取用户收藏列表
     * @param pageParamRequest 分页参数
     * @return List<UserRelationResponse>
     */
    List<UserRelationResponse> getUserList(PageParamRequest pageParamRequest);

    /**
     * 获取用户的收藏数量
     * @param uid 用户uid
     * @return 收藏数量
     */
    Integer getCollectCountByUid(Integer uid);

    /**
     * 根据商品Id取消收藏
     * @param proId 商品Id
     * @return Boolean
     */
    Boolean deleteByProId(Integer proId);
}
