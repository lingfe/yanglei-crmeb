package com.zbkj.crmeb.store.service;

import com.common.PageParamRequest;
import com.zbkj.crmeb.store.model.StoreOrderInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.store.request.StoreOrderInfoSearchRequest;
import com.zbkj.crmeb.store.vo.StoreOrderInfoOldVo;
import com.zbkj.crmeb.store.vo.StoreOrderInfoVo;

import java.util.HashMap;
import java.util.List;

/**
* 订单详情-service层接口
* @author: 零风
* @CreateDate: 2021/12/14 17:12
*/
public interface StoreOrderInfoService extends IService<StoreOrderInfo> {

    List<StoreOrderInfo> getList(StoreOrderInfoSearchRequest request, PageParamRequest pageParamRequest);

    HashMap<Integer, List<StoreOrderInfoOldVo>> getMapInId(List<Integer> orderIdList);

    /**
     * 通过订单ID得到订单购物详情
     * @param orderId 订单ID标识
     * @Author 零风
     * @Date  2022/3/29
     * @return 详情list
     */
    List<StoreOrderInfoOldVo> getOrderListByOrderId(Integer orderId);

    /**
     * 批量添加订单详情
     * @param storeOrderInfos 订单详情集合
     * @return 保存结果
     */
    boolean saveOrderInfos(List<StoreOrderInfo> storeOrderInfos);

    /**
     * 通过订单编号和规格号查询
     * @param uni 规格号
     * @param orderId 订单编号
     * @return StoreOrderInfo
     */
    StoreOrderInfo getByUniAndOrderId(String uni, Integer orderId);

    /**
     * 获取订单详情vo列表
     * @param orderId 订单id
     * @return List<StoreOrderInfoVo>
     */
    List<StoreOrderInfoVo> getVoListByOrderId(Integer orderId);

    /**
     * 获取订单详情list-根据商品ID和日期范围筛选
     * @param productId 商品ID
     * @param date      日期范围
     * @return
     */
    List<StoreOrderInfo> getWhereProductIdAndDate(Integer productId,String date);
}
