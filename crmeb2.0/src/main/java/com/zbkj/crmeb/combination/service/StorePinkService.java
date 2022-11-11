package com.zbkj.crmeb.combination.service;

import com.common.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.combination.model.StorePink;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.combination.request.StorePinkSearchRequest;
import com.zbkj.crmeb.combination.response.StorePinkAdminListResponse;
import com.zbkj.crmeb.combination.response.StorePinkDetailResponse;

import java.util.List;

/**
 * StorePinkService
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
public interface StorePinkService extends IService<StorePink> {

    /**
     * 获取拼团列表
     * @param request
     * @param pageParamRequest
     * @return
     */
    PageInfo<StorePinkAdminListResponse> getList(StorePinkSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 获取拼团列表Cid
     * @param cid 拼团商品id
     * @return
     */
    List<StorePink> getListByCid(Integer cid);

    /**
     * 实体查询
     * @param storePink
     * @return
     */
    List<StorePink> getByEntity(StorePink storePink);

    /**
     * PC拼团详情列表
     * @param pinkId 团长pinkId
     * @return
     */
    List<StorePinkDetailResponse> getAdminList(Integer pinkId);

    /**
     * 查询拼团列表
     * @param cid
     * @param kid
     */
    List<StorePink> getListByCidAndKid(Integer cid, Integer kid);

    /**
     * 根据团长拼团id获取拼团人数
     * @param pinkId
     * @return
     */
    Integer getCountByKid(Integer pinkId);

    /**
     * 检查状态，更新数据
     */
    void detectionStatus();

    /**
     * 拼团成功
     * @param kid
     * @return
     */
    boolean pinkSuccess(Integer kid);

    /**
     * 根据订单编号获取
     * @param orderId
     * @return
     */
    StorePink getByOrderId(String orderId);

    /**
     * 获取最后3个拼团信息（不同用户）
     * @return List
     */
    List<StorePink> findSizePink(Integer size);

    /**
     * 获取拼团参与总人数
     * @return Integer
     */
    Integer getTotalPeople();

    /**
     * 获取拼团详情
     * @return StorePink
     */
    StorePink getByUidAndKid(Integer uid, Integer kid);
}