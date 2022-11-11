package com.zbkj.crmeb.cloudAccount.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 取消待打款订单，公共类
 */
@Getter
@Setter
@ToString
@Builder
public class CancelOrderRequest {

    /**
     * 商户id（必填）
     **/
    private String order_id;

    /**
     * 商户订单号（商户订单号和综合服务平台订单号必须二选一）
     **/
    private String dealer_id;

    /**
     * 综合服务平台订单号（商户订单号和综合服务平台订单号必须二选一）
     **/
    private String broker_id;

    /**
     * 银行卡，支付宝，微信（选填，不填时为银行卡订单查询）
     **/
    private String channel;

}
