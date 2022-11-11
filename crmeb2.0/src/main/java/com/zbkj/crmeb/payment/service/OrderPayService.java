package com.zbkj.crmeb.payment.service;

import com.zbkj.crmeb.front.request.OrderPayRequest;
import com.zbkj.crmeb.front.response.OrderPayResultResponse;
import com.zbkj.crmeb.store.model.StoreOrder;

import javax.servlet.http.HttpServletResponse;

/**
 * 订单支付相关业务-service接口
 * @author: 零风
 * @CreateDate: 2022/1/17 11:39
 */
public interface OrderPayService{

    /**
     * 支付成功值之后处理
     * -更新订单日志、支付记录、经验值、公共积分、零售商订单处理、账单记录、佣金记录等
     * @param storeOrder 订单
     */
    Boolean paySuccess(StoreOrder storeOrder);

    /**
     * 余额支付
     * @param storeOrder 订单
     * @return Boolean
     */
    Boolean yuePay(StoreOrder storeOrder);

    /**
     * 订单支付
     * -1.微信支付拉起微信预支付，返回前端调用微信支付参数，在之后需要调用微信支付查询接口
     * -2.余额支付，更改对应信息后，加入支付成功处理task
     * @param orderPayRequest 支付参数
     * @param ip    ip
     * @Author 零风
     * @Date  2022/2/23
     * @return  订单支付结果
     */
    OrderPayResultResponse payment(OrderPayRequest orderPayRequest, String ip, HttpServletResponse httpServletResponse);
}
