package com.zbkj.crmeb.store.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.CommonPage;
import com.common.PageParamRequest;
import com.zbkj.crmeb.express.vo.ExpressSheetVo;
import com.zbkj.crmeb.express.vo.LogisticsResultVo;
import com.zbkj.crmeb.store.model.StoreOrder;
import com.zbkj.crmeb.store.model.StoreOrderInfo;
import com.zbkj.crmeb.store.request.*;
import com.zbkj.crmeb.store.response.*;
import com.zbkj.crmeb.system.request.SystemWriteOffOrderSearchRequest;
import com.zbkj.crmeb.system.response.SystemWriteOffOrderResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 订单表-service层接口
 * @author: 零风
 * @CreateDate: 2021/12/30 10:14
 */
public interface StoreOrderService extends IService<StoreOrder> {

    /**
     * 给联盟商家发货
     * @param id    订单表ID标识
     * @Author 零风
     * @Date  2022/6/20 10:55
     * @return 结果
     */
    Boolean isLmsjfahuo(Integer id);

    /**
     * 支付方式字符串(中文)-给前端使用
     * @Author 零风
     * @Date  2022/4/15
     * @return 字符串
     */
    String getOrderPayTypeStr(String payType);

    /**
     * 根据商品ID和商户ID和日期得到-商品订单数据
     * @param productId     商品ID
     * @param merId         商户ID
     * @Author 零风
     * @Date  2021/12/15
     * @return
     */
    ProductOrderDataResponse  getWhereProductIdAndMerId(Integer productId,Integer merId);

    /**
     * 获取订单列表-根据订单详情list
     * @param storeOrderInfoList 订单详情list
     * @param merId              商户ID
     * @Author 零风
     * @Date  2021/12/15
     * @return
     */
    List<StoreOrder> getWhereOrderInfoList(List<StoreOrderInfo> storeOrderInfoList,Integer merId);

    /**
     * 列表（PC）
     * @param request 请求参数
     * @param pageParamRequest 分页类参数
     * @return CommonPage<StoreOrderDetailResponse>
     */
    CommonPage<StoreOrderDetailResponse> getAdminList(StoreOrderSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 核销列表
     * @param request 请求参数
     * @param pageParamRequest 分页类参数
     * @author Mr.Zhang
     * @since 2020-05-28
     * @return List<StoreOrder>
     */
    SystemWriteOffOrderResponse getWriteOffList(SystemWriteOffOrderSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 统计订单金额
     * @param userId 用户ID
     * @param date   日期范围
     * @param status 订单状态
     * @param isQuotaControl 是否只统计增加额度的订单
     * @Author 零风
     * @Date  2022/1/17
     * @return 统计金额
     */
    BigDecimal getSumBigDecimal(Integer userId, String date,Integer[] status,Boolean isQuotaControl);

    /**
     * 订单列表返回map
     * @param orderIdList Integer 订单id
     * @author Mr.Zhang
     * @since 2020-06-10
     * @return Map
     */
    Map<Integer, StoreOrder> getMapInId(List<Integer> orderIdList);

    /**
     * H5订单列表
     * @param uid 用户uid
     * @param status 评价等级|0=未支付,1=待发货,2=待收货,3=待评价,4=已完成,-3=售后/退款
     * @param pageParamRequest 分页参数
     * @return 订单结果列表
     */
    List<StoreOrder> getUserOrderList(Integer uid, Integer status, PageParamRequest pageParamRequest);

    /**
     * 创建订单
     * @param storeOrder 订单参数
     * @return 创建结果
     */
    boolean create(StoreOrder storeOrder);

    /**
     * 订单基本查询
     * @param storeOrder 订单参数
     * @return 订单查询结果
     */
    List<StoreOrder> getByEntity(StoreOrder storeOrder);

    /**
     * 根据属性仅仅获取一条
     * @param storeOrder 参数
     * @return 当前查询结果
     */
    StoreOrder getByEntityOne(StoreOrder storeOrder);

    /**
     * 根据状态查询订单数量
     * @param status 状态参数
     * @return 查询到的订单结果
     */
    Integer getTopDataUtil(Integer status, Integer userId,Integer merId);

    /**
     * 获取订单数量（根据用户ID，或者商户用户ID等）
     * @param type      ID类型(1=用户id、2=商户ID、3=服务商ID、4=二级商户ID)
     * @param value     值
     * @param date      字符串月份 moth
     * @Author 零风
     * @Date  2021/12/27
     * @return  对应时间段的订单数量
     */
    int getOrderCount(Integer type, Integer value,String date);

    List<StoreOrder> getOrderGroupByDate(String dateLimit, int lefTime);

    /**
     * 订单退款
     * @param request 请求参数
     * @Author 零风
     * @Date  2022/1/17
     * @return 结果
     */
    boolean refund(StoreOrderRefundRequest request);

    /**
     * 订单详情（PC）
     * @param orderNo 订单编号
     * @return StoreOrderInfoResponse
     */
    StoreOrderInfoResponse info(String orderNo);

    /**
     * 发货
     * @Author 零风
     * @Date  2021/12/20
     * @return
     */
    Boolean send(StoreOrderSendRequest request);

    /**
     * 订单备注
     * @param orderNo 订单编号
     * @param mark 备注
     * @return Boolean
     */
    Boolean mark(String orderNo, String mark);

    /**
     * 拒绝退款
     * @param orderNo 订单编号
     * @param reason String 原因
     * @return Boolean
     */
    Boolean refundRefuse(String orderNo, String reason);

    List<StoreOrder> getOrderByUserIdsForRetailShop(List<Integer> ids);

    StoreOrder getInfoByEntity(StoreOrder storeOrder);

    /**
     * 获取订单快递信息
     * @param orderNo 订单编号
     * @return LogisticsResultVo
     */
    LogisticsResultVo getLogisticsInfo(String orderNo);

    /**
     * 获取-订单状态字符串
     * @Author 零风
     * @Date  2021/12/20
     * @return 数据
     */
    Map<String, String> getStatus(StoreOrder storeOrder);

    /**
     * 更改订单价格
     * @param request 订单改价对象
     * @return 更改结果
     */
    boolean editPrice(StoreOrderEditPriceRequest request);

    /**
     * 根据时间参数统计订单价格
     * @param dateLimit 时间区间
     * @param type 1=price 2=订单量
     * @return 统计订单信息
     */
    StoreOrderStatisticsResponse orderStatisticsByTime(String dateLimit,Integer type);

    /**
     * 获取用户当天的秒杀数量
     * @param uid 用户uid
     * @param seckillId 秒杀商品id
     * @return 用户当天的秒杀商品订单数量
     */
    List<StoreOrder> getUserCurrentDaySecKillOrders(Integer uid, Integer seckillId);

    /**
     * 获取用户当前的砍价订单
     * @param uid    用户uid
     * @return  用户当前的砍价订单
     */
    List<StoreOrder> getUserCurrentBargainOrders(Integer uid, Integer bargainId);

    /**
     * 获取用户当前的拼团订单
     * @param uid    用户uid
     * @return  用户当前的拼团订单
     */
    List<StoreOrder> getUserCurrentCombinationOrders(Integer uid, Integer combinationId);

    /**
     * 根据订单号得到订单
     * @param orderId
     * @return
     */
    StoreOrder getByOderId(String orderId);

    /**
     * 获取面单默认配置信息
     * @return ExpressSheetVo
     */
    ExpressSheetVo getDeliveryInfo();

    /**
     * 根据用户uid集合获取订单号集合
     * @param userIds 用户uid集合
     * @return 订单号集合
     */
    List<StoreOrder> getOrderListStrByUids(List<Integer> userIds, RetailShopStairUserRequest request);

    /**
     * 更新支付结果
     * @param orderNo 订单编号
     */
    Boolean updatePaid(String orderNo);

    /**
     * 跟据订单号获取-订单列表Map
     * -订单ID标识key-订单信息value
     * @param orderIDList   订单号ID标识
     * @Author 零风
     * @Date  2022/1/20
     * @return
     */
    Map<String, StoreOrder> getMapInOrderNo(List<String> orderIDList);

    /**
     * 获取推广订单总金额
     * @param orderNoList 订单编号列表
     * @return BigDecimal
     */
    BigDecimal getSpreadOrderTotalPriceByOrderList(List<String> orderNoList);

    /**
     * 获取所有收货订单id集合
     * @return List<StoreOrder>
     */
    List<StoreOrder> findIdAndUidListByReceipt();

    /**
     * 根据用户uid查询所有已支付订单
     * @param userId 用户uid
     * @param pageParamRequest 分页参数
     * @return List<StoreOrder>
     */
    List<StoreOrder> findPaidListByUid(Integer userId, PageParamRequest pageParamRequest);

    /**
     * 订单改价
     * @param request 改价请求对象
     * @return 改价是否成功
     */
    Boolean updatePrice(StoreOrderUpdatePriceRequest request);

    /**
     * 获取订单总数量
     * @param uid 用户uid
     * @param merId    商户用户ID
     * @return Integer
     */
    Integer getOrderCountByUid(Integer uid,Integer merId);

    /**
     * 获取用户总消费金额
     * @param userId 用户uid
     * @param merId
     * @return BigDecimal
     */
    BigDecimal getSumPayPriceByUid(Integer userId,Integer merId);

    /**
     * 获取订单数量(时间)
     * @param uid 用户uid
     * @return Integer
     */
    Integer getOrderCountByUidAndDate(Integer uid, String date);

    /**
     * 获取用户消费金额(可以根据某个时间范围内统计)
     * @param type      ID类型(1=用户id、2=商户ID、3=服务商ID、4=二级商户ID)
     * @param date      日期
     * @param value     值
     * @Author 零风
     * @Date  2022/5/10 15:31
     * @return 数据
     */
    BigDecimal getSumPayPriceByUidAndDate(Integer type, String date,Integer value);

    /**
     * 获取砍价订单
     * @param bargainId 砍价商品id
     * @param bargainUserId 用户砍价活动id
     * @return StoreOrder
     */
    StoreOrder getByBargainOrder(Integer bargainId, Integer bargainUserId);

    /**
     * 获取订单状态数量
     * @param dateLimit 时间端
     * @return StoreOrderCountItemResponse
     */
    StoreOrderCountItemResponse getOrderStatusNum(String dateLimit);

    /**
     * 获取订单统计数据
     * @param dateLimit 时间端
     * @return StoreOrderTopItemResponse
     */
    StoreOrderTopItemResponse getOrderData(String dateLimit);

    /**
     * 订单删除
     * @param orderNo 订单编号
     * @return Boolean
     */
    Boolean delete(String orderNo);

    /**
     * 获取待收货-订单列表
     * @Author 零风
     * @Date  2021/12/20
     * @return 订单列表
     */
    List<StoreOrder> getStayReceivingOrderList();

}
