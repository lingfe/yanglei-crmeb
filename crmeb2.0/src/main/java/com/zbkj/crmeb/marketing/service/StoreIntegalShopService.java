package com.zbkj.crmeb.marketing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.front.response.StoreIntegalShopDetailH5Response;
import com.zbkj.crmeb.marketing.model.StoreIntegalShop;
import com.zbkj.crmeb.marketing.request.StoreIntegalShopRequest;
import com.zbkj.crmeb.marketing.request.StoreIntegalShopSearchRequest;
import com.zbkj.crmeb.marketing.response.StoreIntegalShopResponse;
import com.zbkj.crmeb.seckill.model.StoreSeckill;
import com.zbkj.crmeb.store.response.StoreProductResponse;

import java.util.HashMap;
import java.util.List;

/**
* 商城-营销-积分商品表service接口
* @author: 零风
* @Version: 1.0
* @CreateDate: 2021/7/1 15:26
* @return： com.zbkj.crmeb.marketing.service.StoreIntegalShopService.java
**/
public interface StoreIntegalShopService extends IService<StoreIntegalShop> {

    /**
     * 分页显示积分兑换商品表
     * @param request   搜索条件
     * @param pageParamRequest  分页参数
     */
    PageInfo<StoreIntegalShopResponse> getList(StoreIntegalShopSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 新增积分兑换商品
     * @param storeIntegalShopRequest
     * @return 是否成功
     */
    boolean saveBargain(StoreIntegalShopRequest storeIntegalShopRequest);

    /**
     * 删除积分兑换商品
     */
    Boolean deleteById(Integer id);

    /**
     * 编辑积分兑换商品
     */
    Boolean updateIntegal(StoreIntegalShopRequest request);

    /**
     * 查询积分兑换商品详情
     */
    StoreProductResponse getAdminDetail(Integer id);

    /**
     * 积分兑换商品是否显示
     */
    Boolean updateIsShow(Integer id, Boolean isShow);

    /**
     * 根据ID查询，带异常
     * @param id
     * @return
     */
    StoreIntegalShop getByIdException(Integer id);

    /**
     *  积分兑换商品详情-H5用户端
     * @param id 积分兑换商品id
     * @return 详情
     */
    StoreIntegalShopDetailH5Response getDetailH5(Integer id);

    /**
     *  积分兑换商品列表-H5用户端
     * @param pageParamRequest
     * @return
     */
    List<StoreIntegalShopResponse> getListH5( PageParamRequest pageParamRequest);

    /**
     * 获取积分商品banner图
     * @return
     */
    List<HashMap<String, Object>> getBannerH5();

    /**
     * 获取积分商城导航菜单
     * @return
     */
    List<HashMap<String, Object>> getDaohangMenuH5();

}
