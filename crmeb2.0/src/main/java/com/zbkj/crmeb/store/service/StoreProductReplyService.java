package com.zbkj.crmeb.store.service;

import com.common.MyRecord;
import com.common.PageParamRequest;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.front.response.ProductDetailReplyResponse;
import com.zbkj.crmeb.front.response.ProductReplyResponse;
import com.zbkj.crmeb.store.model.StoreProductReply;
import com.zbkj.crmeb.store.request.StoreProductReplyAddRequest;
import com.zbkj.crmeb.store.request.StoreProductReplySearchRequest;
import com.zbkj.crmeb.store.response.StoreProductReplyResponse;

import java.util.List;

/**
 * StoreProductReplyService接口
 * @author: 零风
 * @CreateDate: 2022/1/17 15:10
 */
public interface StoreProductReplyService extends IService<StoreProductReply> {

    PageInfo<StoreProductReplyResponse> getList(StoreProductReplySearchRequest request, PageParamRequest pageParamRequest);

    Integer getSumStar(Integer productId);

    /**
     * 商品评价
     * @param request 请求参数
     * @Author 零风
     * @Date  2022/1/17
     * @return 结果
     */
    Boolean create(StoreProductReplyAddRequest request);

    /**
     * 添加虚拟评论
     * @param request 评论参数
     * @return 评论结果
     */
    boolean virtualCreate(StoreProductReplyAddRequest request);

    /**
     * 查询是否已经回复
     * @param unique
     * @param replayType
     * @return
     */
    List<StoreProductReply> isReply(String unique,String replayType, Integer orderId);

    /**
     * 查询是否已经回复
     * @param unique skuId
     * @param orderId 订单id
     * @return Boolean
     */
    Boolean isReply(String unique, Integer orderId);

    /**
     * 获取商品评论列表
     * @param productId     商品ID
     * @param type          商品类型
     * @return List<StoreProductReply>
     */
    List<StoreProductReply> getAllByPidAndType(Integer productId, String type);

    /**
     * H5商品评论统计
     * @param productId 商品编号
     * @return MyRecord
     */
    MyRecord getH5Count(Integer productId);

    /**
     * H5商品详情评论信息
     * @param proId 商品编号
     * @return ProductDetailReplyResponse
     */
    ProductDetailReplyResponse getH5ProductReply(Integer proId);

    /**
     * 移动端商品评论列表
     * @param proId 商品编号
     * @param type 评价等级|0=全部,1=好评,2=中评,3=差评
     * @param pageParamRequest 分页参数
     * @return PageInfo<ProductReplyResponse>
     */
    PageInfo<ProductReplyResponse> getH5List(Integer proId, Integer type, PageParamRequest pageParamRequest);

    /**
     * 得到评论对象-(公共)
     * @param uid       评论用户ID
     * @param oid       订单ID
     * @param productId 商品ID
     * @param unique    商品属性id
     * @param replyType 评论商品类型
     * @param productScore  商品分
     * @param serviceScore  服务分
     * @param comment       评论内容
     * @param pics          评论图片
     * @param merchantReplyContent  回复内容
     * @param merchantReplyTime     回复时间
     * @param nickname  评论人名称
     * @param avatar    评论人头像
     * @param sku   商品sku
     * @return
     */
    StoreProductReply getStoreProductReply(Integer uid,Integer oid, Integer productId,
                                           String unique, String replyType,
                                           Integer productScore, Integer serviceScore,
                                           String comment, String pics,
                                           String merchantReplyContent,Integer merchantReplyTime,
                                           String nickname,String avatar,
                                           String sku);
}
