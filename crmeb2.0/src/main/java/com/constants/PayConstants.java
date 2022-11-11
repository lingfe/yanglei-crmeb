package com.constants;

/**
 * 支付相关常量类
 * @author: 零风
 * @CreateDate: 2022/1/20 17:27
 */
public class PayConstants {

    //支付方式-英文
    /** 支付方式-余额支付 = yue */
    public static final String PAY_TYPE_YUE = "yue"; //余额支付
    /** 支付方式-积分支付 = integral */
    public static final String PAY_TYPE_INTEGRAL = "integral"; //积分支付
    /** 支付方式-线下支付 = offline */
    public static final String PAY_TYPE_OFFLINE = "offline"; //线下支付
    /** 支付方式-支付宝支付 = alipay */
    public static final String PAY_TYPE_ALI_PAY = "alipay"; //支付宝
    /** 支付方式-微信支付 = weixin */
    public static final String PAY_TYPE_WE_CHAT = "weixin"; //微信支付
    /** 支付方式-零元付 = zeroPay */
    public static final String PAY_TYPE_ZERO_PAY = "zeroPay"; // 零元付
    /** 支付方式-银行卡 = bank */
    public static final String PAY_TYPE_BANK ="bank"; //银行卡支付
    /** 支付方式-其他支付 = 其他支付  */
    public static final String PAY_TYPE_OTHER = "other"; //其他支付

    //支付方式-中文
    /** 支付方式-中文字符串 = 余额支付  */
    public static final String PAY_TYPE_STR_YUE = "余额支付"; //余额支付
    /** 支付方式-中文字符串 = 积分支付、酒米兑换  */
    public static final String PAY_TYPE_STR_INTEGAL ="酒米兑换";//"积分支付";
    /** 支付方式-中文字符串 = 微信支付  */
    public static final String PAY_TYPE_STR_WE_CHAT = "微信支付";
    /** 支付方式-中文字符串 = 支付宝支付  */
    public static final String PAY_TYPE_STR_ALI_PAY = "支付宝支付"; //支付宝支付
    /** 支付方式-中文字符串 = 线下支付  */
    public static final String PAY_TYPE_STR_OFFLINE = "线下支付"; //线下支付
    /** 支付方式-中文字符串 = 零元付 */
    public static final String PAY_TYPE_STR_ZERO_PAY = "零元付"; // 零元付
    /** 支付方式-中文字符串 = 银行卡支付 */
    public static final String PAY_TYPE_STR_BANK ="银行卡支付"; //银行卡支付
    /** 支付方式-中文字符串 = 其他支付  */
    public static final String PAY_TYPE_STR_OTHER = "其他支付"; //其他支付

    //微信支付-支付订单号前缀
    /** 微信平台下单订单号前缀-wx = wx */
    public static final String ORDER_NO_PREFIX_WE_CHAT = "wx"; //微信平台下单订单号前缀
    /** 微信平台下单订单号前缀-h5 = h5 */
    public static final String ORDER_NO_PREFIX_H5 = "h5"; //微信平台下单订单号前缀

    //微信支付-支付渠道-字符串
    /** 微信-支付渠道：H5唤起微信支付 = weixinh5 */
    public static final String PAY_CHANNEL_WE_CHAT_H5 = "weixinh5"; //H5唤起微信支付
    /** 微信-支付渠道：公众号支付 = public */
    public static final String PAY_CHANNEL_WE_CHAT_PUBLIC = "public"; //公众号
    /** 微信-支付渠道：小程序支付 = routine */
    public static final String PAY_CHANNEL_WE_CHAT_PROGRAM = "routine"; //小程序
    /** 微信-支付渠道：微信App支付ios = weixinAppIos */
    public static final String PAY_CHANNEL_WE_CHAT_APP_IOS = "weixinAppIos"; //微信App支付ios
    /** 微信-支付渠道：微信App支付android = weixinAppAndroid */
    public static final String PAY_CHANNEL_WE_CHAT_APP_ANDROID = "weixinAppAndroid"; //微信App支付android

    //微信支付-支付渠道-数字
    /** 支付渠道-微信公众号 = 0 */
    public static final int ORDER_PAY_CHANNEL_PUBLIC = 0; //公众号
    /** 支付渠道-微信小程序 = 1 */
    public static final int ORDER_PAY_CHANNEL_PROGRAM = 1; //小程序
    /** 支付渠道-H5 = 2 */
    public static final int ORDER_PAY_CHANNEL_H5 = 2; //H5
    /** 支付渠道-数值-余额 = 3  */
    public static final int ORDER_PAY_CHANNEL_YUE = 3; //余额
    /** 支付渠道-数值-微信（app-ios） = 4  */
    public static final int ORDER_PAY_CHANNEL_APP_IOS = 4; //微信（app-ios）
    /** 支付渠道-数值-微信(app-android) = 5  */
    public static final int ORDER_PAY_CHANNEL_APP_ANDROID = 5; //微信(app-android)

    //微信支付-交易类型(trade_type)
    /** 微信-交易类型：JSAPI支付（或小程序支付) */
    public static final String WX_PAY_TRADE_TYPE_JS = "JSAPI";
    /** 微信-交易类型：H5支付 */
    public static final String WX_PAY_TRADE_TYPE_H5 = "MWEB";
    /** 微信-交易类型: app支付 */
    public static final String WX_PAY_TRADE_TYPE_APP ="APP";
    /** 微信-交易类型: Native支付 */
    public static final String WX_PAY_TRADE_TYPE_NATIVE ="NATIVE";
    /** 微信-交易类型: 付款码支付 */
    public static final String WX_PAY_TRADE_TYPE_MICROPAY ="MICROPAY";

    //微信支付接口
    /** 微信接口-微信支付接口请求地址 */
    public static final String WX_PAY_API_URL = "https://api.mch.weixin.qq.com/";
    /** 微信接口-微信统一预下单 */
    public static final String WX_PAY_API_URI = "pay/unifiedorder";
    /** 微信接口-微信查询订单 */
    public static final String WX_PAY_ORDER_QUERY_API_URI = "pay/orderquery";
    /** 微信接口-公共号退款 */
    public static final String WX_PAY_REFUND_API_URI= "secapi/pay/refund";

    //微信支付回调接口
    /** 回调接口-微信支付回调地址 */
    public static final String WX_PAY_NOTIFY_API_URI = "/api/admin/payment/callback/wechat";
    /** 回调接口-微信退款回调地址 */
    public static final String WX_PAY_REFUND_NOTIFY_API_URI = "/api/admin/payment/callback/wechat/refund";

    //支付宝支付-支付渠道
    /** 支付宝-支付渠道：支付宝App支付android = zfbAppAndroid */
    public static final String PAY_CHANNEL_ZFB_CHAT_APP_ANDROID = "zfbAppAndroid";
    /** 支付宝-支付渠道：支付宝App支付Ios = zfbAppIos */
    public static final String PAY_CHANNEL_ZFB_CHAT_APP_IOS = "zfbAppIos";
    /** 支付宝-支付渠道：支付宝网页支付 = zfbWeb */
    public static final String PAY_CHANNEL_ZFB_CHAT_APP_WEB = "zfbWeb";

    //加密方式
    /** 加密方式-> MD5 */
    public static final String WX_PAY_SIGN_TYPE_MD5 = "MD5";
    /** 加密方式-> HMAC-SHA256 */
    public static final String WX_PAY_SIGN_TYPE_SHA256 = "HMAC-SHA256";

    //签名
    /** 签名字符串 -> sign */
    public static final String FIELD_SIGN = "sign";

    //收款方描述文字
    /** 收款方描述文字 -> Crmeb支付中心-订单支付 */
    public static final String PAY_BODY = "Crmeb支付中心-订单支付";
    /** 收款方描述文字 -> 九秒中-订单支付 */
    public static final String PAY_BODY_QJY = "九秒中-订单支付";
}
