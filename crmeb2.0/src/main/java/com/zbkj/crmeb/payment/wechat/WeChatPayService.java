package com.zbkj.crmeb.payment.wechat;

import com.zbkj.crmeb.finance.model.UserRecharge;
import com.zbkj.crmeb.payment.vo.wechat.CreateOrderResponseVo;
import com.zbkj.crmeb.payment.vo.wechat.PayParamsVo;
import com.zbkj.crmeb.store.model.StoreOrder;

import java.util.Map;

/**
 * 微信支付-service接口
 * @author: 零风
 * @CreateDate: 2022/1/17 11:36
 */
public interface WeChatPayService {

    /**
     * 统一下单
     * @param payParamsVo PayParamsVo 支付参数
     * @author Mr.Zhang
     * @since 2020-06-22
     * @return PayResponseVo
     */
    CreateOrderResponseVo create(PayParamsVo payParamsVo);

    /**
     * 微信预下单接口
     * @param storeOrder 订单
     * @param ip      ip
     * @return 获取wechat.requestPayment()参数
     */
    Map<String, String> unifiedorder(StoreOrder storeOrder, String ip);

    /**
     * 查询支付结果
     * @param orderNo 订单编号
     * @return
     */
    Boolean queryPayResult(String orderNo);

    /**
     * 微信充值预下单接口
     * @param userRecharge 充值订单
     * @param clientIp      ip
     * @return 获取wechat.requestPayment()参数
     */
    Map<String, String> unifiedRecharge(UserRecharge userRecharge, String clientIp);
}
