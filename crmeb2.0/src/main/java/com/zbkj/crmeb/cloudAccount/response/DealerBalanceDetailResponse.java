package com.zbkj.crmeb.cloudAccount.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * 云账户-账户信息-余额详情-响应类
 */
@Data
public class DealerBalanceDetailResponse {

    /** 账户余额 */
    @ApiModelProperty(value = "账户余额")
    private String acct_balance;
    /** 支付宝余额 */
    @ApiModelProperty(value = "支付宝余额")
    private String alipay_balance;
    /** 银行卡余额 */
    @ApiModelProperty(value = "银行卡余额")
    private String bank_card_balance;
    /** 代征主体ID */
    @ApiModelProperty(value = "代征主体ID")
    private String broker_id;
    /** 微信余额 */
    @ApiModelProperty(value = "微信余额")
    private String wxpay_balance;
    /** 服务费返点余额 */
    @ApiModelProperty(value = "服务费返点余额")
    private String rebate_fee_balance;

    /** 是否开通-银行卡通道 */
    @ApiModelProperty(value = "是否开通-银行卡通道")
    private boolean is_bank_card;
    /** 是否开通-支付宝通道 */
    @ApiModelProperty(value = "是否开通-支付宝通道")
    private boolean is_alipay;
    /** 是否开通-微信通道 */
    @ApiModelProperty(value = "是否开通-微信通道")
    private boolean is_wxpay;

}
