package com.constants;

/**
 * 短信常量类
 * @author: 零风
 * @CreateDate: 2022/6/10 9:23
 */
public class SmsConstants {

    /** 接口异常错误码 */
    public static final Integer SMS_ERROR_CODE = 400;

    /** 短信发送队列key */
    public static final String SMS_SEND_KEY = "sms_send_list";

    /** 手机验证码redis key */
    public static final String SMS_VALIDATE_PHONE = "sms:validate:code:";

    /**
     * ---------------------
     * 短信模版配置开关常量
     * ---------------------
     */
    /** 验证码 */
    public static final String SMS_CONFIG_VERIFICATION_CODE = "verificationCode";
//    public static final Integer SMS_CONFIG_VERIFICATION_CODE_TEMP_ID = 518076;
    /** 验证码模板ID */
    public static final Integer SMS_CONFIG_VERIFICATION_CODE_TEMP_ID = 435250;

    /** 支付成功短信提醒 */
    public static final String SMS_CONFIG_LOWER_ORDER_SWITCH = "lowerOrderSwitch";
    /** 支付成功短信提醒模版ID */
    public static final Integer SMS_CONFIG_LOWER_ORDER_SWITCH_TEMP_ID = 440396;

    /** 发货短信提醒 */
    public static final String SMS_CONFIG_DELIVER_GOODS_SWITCH = "deliverGoodsSwitch";
    /** 发货短信提醒模版ID */
    public static final Integer SMS_CONFIG_DELIVER_GOODS_SWITCH_TEMP_ID = 441596;

    /** 确认收货短信提醒 */
    public static final String SMS_CONFIG_CONFIRM_TAKE_OVER_SWITCH = "confirmTakeOverSwitch";
    /** 确认收货短信提醒模版ID */
    public static final Integer SMS_CONFIG_CONFIRM_TAKE_OVER_SWITCH_TEMP_ID = 520271;

    /** 用户下单管理员短信提醒 */
    public static final String SMS_CONFIG_ADMIN_LOWER_ORDER_SWITCH = "adminLowerOrderSwitch";
    /** 用户下单管理员短信提醒模版ID */
    public static final Integer SMS_CONFIG_ADMIN_LOWER_ORDER_SWITCH_TEMP_ID = 440405;

    /** 支付成功管理员短信提醒 */
    public static final String SMS_CONFIG_ADMIN_PAY_SUCCESS_SWITCH = "adminPaySuccessSwitch";
    /** 支付成功管理员短信提醒模版ID */
    public static final Integer SMS_CONFIG_ADMIN_PAY_SUCCESS_SWITCH_TEMP_ID = 440406;

    /** 用户确认收货管理员短信提醒 */
    public static final String SMS_CONFIG_ADMIN_REFUND_SWITCH = "adminRefundSwitch";
    /** 用户确认收货管理员短信提醒模版ID */
    public static final Integer SMS_CONFIG_ADMIN_REFUND_SWITCH_TEMP_ID = 440408;

    /** 用户发起退款管理员短信提醒 */
    public static final String SMS_CONFIG_ADMIN_CONFIRM_TAKE_OVER_SWITCH = "adminConfirmTakeOverSwitch";
    /** 用户发起退款管理员短信提醒模版ID */
    public static final Integer SMS_CONFIG_ADMIN_CONFIRM_TAKE_OVER_SWITCH_TEMP_ID = 440407;

    /** 改价短信提醒 */
    public static final String SMS_CONFIG_PRICE_REVISION_SWITCH = "priceRevisionSwitch";
    /** 改价短信提醒模版ID */
    public static final Integer SMS_CONFIG_PRICE_REVISION_SWITCH_TEMP_ID = 440410;

    /** 订单未支付 */
    public static final String SMS_CONFIG_ORDER_PAY_FALSE = "orderPayFalse";
    /** 订单未支付模版ID */
    public static final Integer SMS_CONFIG_ORDER_PAY_FALSE_TEMP_ID = 528116;

    /** SMS配置-预约活动通知 */
    public static final String SMS_CONFIG_MAA_TONZHI_SMS = "maaTonzhiSms";
    /** SMS模版ID-预约活动通知（默认） */
    public static final Integer SMS_CONFIG_MAA_TONZHI_SMS_TEMP_ID = 766777;

    /**
     * 短信类型
     * 短信模版配置开关常量
     */
    /** 验证码 */
    public static final int SMS_CONFIG_TYPE_VERIFICATION_CODE = 1;
    /** 支付成功短信提醒 */
    public static final int SMS_CONFIG_TYPE_LOWER_ORDER_SWITCH = 2;
    /** 发货短信提醒 */
    public static final int SMS_CONFIG_TYPE_DELIVER_GOODS_SWITCH = 3;
    /** 确认收货短信提醒 */
    public static final int SMS_CONFIG_TYPE_CONFIRM_TAKE_OVER_SWITCH = 4;
    /** 用户下单管理员短信提醒 */
    public static final int SMS_CONFIG_TYPE_ADMIN_LOWER_ORDER_SWITCH = 5;
    /** 支付成功管理员短信提醒 */
    public static final int SMS_CONFIG_TYPE_ADMIN_PAY_SUCCESS_SWITCH = 6;
    /** 用户确认收货管理员短信提醒 */
    public static final int SMS_CONFIG_TYPE_ADMIN_REFUND_SWITCH = 7;
    /** 用户发起退款管理员短信提醒 */
    public static final int SMS_CONFIG_TYPE_ADMIN_CONFIRM_TAKE_OVER_SWITCH = 8;
    /** 改价短信提醒 */
    public static final int SMS_CONFIG_TYPE_PRICE_REVISION_SWITCH = 9;
    /** 订单未支付 */
    public static final int SMS_CONFIG_TYPE_ORDER_PAY_FALSE = 10;
    /** 预约活动通知 */
    public static final int SMS_CONFIG_TYPE_MAA_SMS = 11;

    /** 发送短信参数模板 */
    public static final String SMS_COMMON_PARAM_FORMAT = "param[{}]";
//    /** 用户token前缀 */
//    public static final String SMS_USER_TOKEN_PREFIX = "Bearer-";
//    /** 支付 */
//    public static final String PAY_DEFAULT_PAY_TYPE = "weixin";
//    /** 用户token的redis前缀 拼接secret */
//    public static final String SMS_USER_TOKEN_REDIS_PREFIX = "sms_user_token_{}";
}
