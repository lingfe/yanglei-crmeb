package com.zbkj.crmeb.cloudAccount.constant;

/**
 * 云账户-配置类
 */
public class ConfigPath {
    /** 云账户请求接口域名 */
    public static String YZH_URL = "yunzhanghu.url";
    /** 云账户请求接口域名-AIC */
    public static String YZH_URL_AIC = "yunzhanghu.url.aic";

    /** 云账户-商户ID */
    public static String YZH_DEALERID = "yunzhanghu.dealerID";
    /** 云账户-主体ID */
    public static String YZH_BROKERID = "yunzhanghu.brokerID";
    /** 3des_key */
    public static String YZH_3DESKEY = "yunzhanghu.3des_key";
    /** appKey */
    public static String YZH_APPKEY = "yunzhanghu.app_key";
    /** 密钥 */
    public static String YZH_RSA_PRIVATE_KEY = "yunzhanghu.rsa.private.key";
    /** 公钥 */
    public static String YZH_RSA_PUBLIC_KEY = "yunzhanghu.rsa.public.key";

    /** 签名加密方式 */
    public static String YZH_SIGN_TYPE = "yunzhanghu.sign_type";

    /** 回调接口 */
    public static String YZH_BACKNOTIFY_URL = "dealer.backNotifyUrl";

    /** api-银行卡-下单打款 */
    public static String YZH_BANK_CARD_REAL_TIME_ORDER = "bank_card_real_time_order";
    /** api-支付宝-下单打款 */
    public static String YZH_ALIPAY_REAL_TIME_ORDER = "alipay_real_time_order";
    /** api-微信-下单打款 */
    public static String YZH_WXPAY_REAL_TIME_ORDER = "wxpay_real_time_order";
    /** api-根据订单号查询订单 */
    public static String YZH_ORDER_QUERY = "order_query";
    /** api-查询-商户账户余额 */
    public static String YZH_DEALER_BALANCE_QUERY = "dealer_balance_query";
    /** api-查询-云账户-日流水记录 */
    public static String YZH_API_QUERY_V1_BILLS="/api/dataservice/v1/bills";

    public static String YZH_RECEIPT_QUERY = "receipt_file_query";
    public static String YZH_CANCEL_ORDER = "cancel_order";
    public static String YZH_DAILY_ORDER_FILE_QUERY = "daily_order_file_query";
    public static String YZH_DAILY_BILL_FILE_QUERY = "daily_bill_file_query";
    public static String YZH_RECHARGE_RECORD_QUERY = "recharge_record_query";
    public static String YZH_FOUR_FACTOR_VERIFY_SEND_MSG = "four_factor_verify_send_msg";
    public static String YZH_FOUT_FACTOR_VERIFY_CONFIRM = "four_factor_verify_confirm";
    public static String YZH_FOUR_FACTOR_BANK_CARD_VERIFY = "four_factor_bank_card_verify";
    public static String YZH_THREE_FACTOR_BANK_CARD_VERIFY = "three_factor_bank_card_verify";
    public static String YZH_REAL_NAME_VERIFY = "real_name_verify";
    public static String YZH_UPLOAD_EXEMOTED_INFO_LIST = "exempted_user_upload";
    public static String YZH_EXEMOTED_INFO_QUERY = "exempted_user_check";
    public static String YZH_CARD_INFO_QUERY = "card_info_query";
    public static String YZH_H5_PRE_SIGN = "h5_presign";
    public static String YZH_H5_SIGN = "h5_sign";
    public static String YZH_H5_SIGN_STATUS = "h5_sign_status";
    public static String YZH_H5_SIGN_CANCLE = "h5_sign_cancle";
    public static String YZH_AIC_REGISTER_INFORMATION = "aic_register_information";
    public static String YZH_AIC_REGISTER = "aic_register_register";
    public static String YZH_AIC_REGISTER_QUERY = "aic_queryregister_status";
    public static String YZH_INVOICE_STAT_QUERY = "invoice_stat_query";
    public static String YZH_INVOICE_AMOUNT_QUERY = "invoice_amount_query";
    public static String YZH_INVOICE_APPLY = "invoice_apply";
    public static String YZH_APPLY_STATUS_QUERY = "invoice_apply_status_query";
    public static String YZH_INVOICE_PDF = "invoice_pdf";


}
