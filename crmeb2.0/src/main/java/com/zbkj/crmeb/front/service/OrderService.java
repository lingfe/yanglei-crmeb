package com.zbkj.crmeb.front.service;

import com.common.CommonPage;
import com.common.MyRecord;
import com.common.PageParamRequest;
import com.zbkj.crmeb.front.request.*;
import com.zbkj.crmeb.front.response.*;

import java.util.List;

/**
 * H5端订单操作
 * @author: 零风
 * @CreateDate: 2022/4/7 10:33
 */
public interface OrderService {

    /**
     * 订单列表
     * @param type 类型
     * @param pageRequest 分页
     * @return 订单集合
     */
    CommonPage<OrderDetailResponse> list(Integer type, PageParamRequest pageRequest);

    /**
     * 订单详情
     * @param orderId 订单id
     */
    StoreOrderDetailResponse detailOrder(String orderId);

    /**
     * 订单状态数量
     * @return 订单状态数据量
     */
    OrderDataResponse orderData();

    /**
     * 查询退款理由
     * @return 退款理由集合
     */
    List<String> getRefundReason();

    Boolean delete(Integer id);

    /**
     * 订单收货
     * @param id 订单表ID标识
     * @Author 零风
     * @Date  2021/12/20
     * @return 结果
     */
    boolean take(Integer id);

    /**
     * 取消订单
     * @param id 订单表ID标识
     * @Author 零风
     * @Date  2022/2/7
     * @return 结果
     */
    boolean cancel(Integer id);

    boolean refundApply(OrderRefundApplyRequest request);

    /**
     * 订单退款申请Task使用
     * @param applyList 退款List
     * @return Boolean
     */
    Boolean refundApplyTask(List<OrderRefundApplyRequest> applyList);

    /**
     * 订单物流查看
     */
    Object expressOrder(String orderId);

    /**
     * 获取待评价商品信息
     * @param getProductReply 订单详情参数
     * @return 待评价
     */
    OrderProductReplyResponse getReplyProduct(GetProductReply getProductReply);

    /**
     * 获取申请订单退款信息
     * @param orderId 订单编号
     * @return ApplyRefundOrderInfoResponse
     */
    ApplyRefundOrderInfoResponse applyRefundOrderInfo(String orderId);

    /**
     * 订单-预下单
     * @param request 参数
     * @Author 零风
     * @Date  2021/12/30
     * @return 预下单对象
     */
    MyRecord preOrder(PreOrderRequest request);

    /**
     * 加载预下单信息
     * @param preOrderNo 预下单号
     * @return 预下单信息
     */
    PreOrderResponse loadPreOrder(String preOrderNo);

    /**
     * 计算订单价格
     * @param request 计算订单价格请求对象
     * @return ComputedOrderPriceResponse
     */
    ComputedOrderPriceResponse computedOrderPrice(OrderComputedPriceRequest request);

    /**
     * 创建订单
     * @param orderRequest 创建订单请求参数
     * @return MyRecord 订单编号
     */
    MyRecord createOrder(CreateOrderRequest orderRequest);

    /**
     * 订单流程是否需要弹窗
     * @param uid 订单推荐人用户ID标识
     * @Author 零风
     * @Date  2022/6/15 10:38
     * @return
     */
    OrderProcedureIsPopupResponse orderProcedureIsPopup(Integer uid);
}
