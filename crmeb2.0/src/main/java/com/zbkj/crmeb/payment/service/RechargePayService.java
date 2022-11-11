package com.zbkj.crmeb.payment.service;

import com.zbkj.crmeb.finance.model.UserRecharge;

/**
 * 订单支付
 * @author: 零风
 * @CreateDate: 2022/1/19 15:00
 */
public interface RechargePayService {

    /**
     * 支付成功处理
     * @param userRecharge 充值订单
     */
    Boolean paySuccess(UserRecharge userRecharge);
}
