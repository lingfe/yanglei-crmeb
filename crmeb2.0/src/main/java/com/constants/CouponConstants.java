package com.constants;

/**
 * 优惠券-常量类
 * @author: 零风
 * @CreateDate: 2022/2/25 10:24
 */
public class CouponConstants {

    /**
     * ---------------------------------------
     * --------优惠券常量----------------------
     * ---------------------------------------
     */

    /** 优惠券类型—手动领取=1 */
    public static final Integer COUPON_TYPE_RECEIVE = 1;

    /** 优惠券类型—新人券=2 */
    public static final Integer COUPON_TYPE_NEW_PEOPLE = 2;

    /** 优惠券类型—赠送券=3 */
    public static final Integer COUPON_TYPE_GIVE_AWAY = 3;

    /** 优惠券使用类型-通用=1 */
    public static final Integer COUPON_USE_TYPE_COMMON = 1;

    /** 优惠券使用类型-商品=2 */
    public static final Integer COUPON_USE_TYPE_PRODUCT = 2;

    /** 优惠券使用类型-品类=3 */
    public static final Integer COUPON_USE_TYPE_CATEGORY = 3;


    /**
     * ---------------------------------------
     * --------用户优惠券常量-------------------
     * ---------------------------------------
     */

    /** 用户优惠券领取类型—用户注册 */
    public static final String STORE_COUPON_USER_TYPE_REGISTER = "new";

    /** 用户优惠券领取类型—用户领取 */
    public static final String STORE_COUPON_USER_TYPE_GET = "receive";

    /** 用户优惠券领取类型—后台发放 */
    public static final String STORE_COUPON_USER_TYPE_SEND = "send";

    /** 用户优惠券领取类型—买赠送 */
    public static final String STORE_COUPON_USER_TYPE_BUY = "buy";

    /** 用户优惠券状态—未使用 */
    public static final Integer STORE_COUPON_USER_STATUS_USABLE = 0;

    /** 用户优惠券状态—已使用 */
    public static final Integer STORE_COUPON_USER_STATUS_USED = 1;

    /** 用户优惠券状态—已失效 */
    public static final Integer STORE_COUPON_USER_STATUS_LAPSED = 2;



}
