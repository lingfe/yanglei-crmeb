package com.zbkj.crmeb.payment.service;

import javax.servlet.http.HttpServletRequest;

/**
 * 订单支付回调-service
 * @author: 零风
 * @CreateDate: 2022/1/19 14:59
 */
public interface CallbackService {

    /**
     * 微信支付回调
     * @param xmlInfo 微信回调json
     * @return String
     */
    String weChat(String xmlInfo);

    /**
     * 支付宝支付回调
     * @param request
     * @return
     */
    boolean aliPay(HttpServletRequest request);

    /**
     * 微信退款回调
     * @param request 微信回调json
     * @return String
     */
    String weChatRefund(String request);
}
