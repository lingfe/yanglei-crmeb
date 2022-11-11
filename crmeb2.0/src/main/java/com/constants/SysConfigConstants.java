package com.constants;

/**
* 系统设置-常量类
* @author: 零风
* @CreateDate: 2021/10/13 9:40
*/
public class SysConfigConstants {

    //基本设置->站点设置
    public static final String CONFIG_KEY_SITE_NAME = "site_name"; //网站名称
    public static final String CONFIG_KEY_SITE_URL = "site_url"; //网站网址、域名
    public static final String CONFIG_KEY_API_URL = "api_url"; //Api接口地址
    public static final String CONFIG_KEY_ADMIN_LOGIN_LOGO_LOGIN = "site_logo_login"; //登录页logo[后台]
    public static final String CONFIG_KEY_ADMIN_LOGIN_BACKGROUND_IMAGE = "admin_login_bg_pic"; //登录页背景图[后台]
    public static final String CONFIG_KEY_ADMIN_LOGIN_LOGO_LEFT_TOP = "site_logo_lefttop";//主页左上角logo[后台]
    public static final String CONFIG_KEY_ADMIN_SITE_LOGO_SQUARE = "site_logo_square";//主页左上角缩回菜单logo[后台]
    public static final String CONFIG_KEY_MOBILE_LOGIN_LOGO = "mobile_login_logo"; //[移动端]登录页logo
    public static final String CONFIG_KEY_SITE_LOGO = "mobile_top_logo"; //[移动端]顶部logo地址
    public static final String CONFIG_KEY_NEWS_SLIDES_LIMIT = "news_slides_limit";//[移动端]banner图最大数量-配置数据最小3最大10
    public static final String CONFIG_KEY_SEO_TITLE = "seo_title";//SEO标题

    //商城配置->商城基础配置
    /** 警戒库存 = store_stock */
    public static final String CONFIG_FORM_KEY_store_stock="store_stock";
    /** 退款理由 = stor_reason */
    public static final String CONFIG_KEY_STOR_REASON = "stor_reason";

    /** 微信分享图片（公众号） */
    public static final String CONFIG_KEY_ADMIN_WECHAT_SHARE_IMAGE = "wechat_share_img";
    /** 微信分享标题（公众号） */
    public static final String CONFIG_KEY_ADMIN_WECHAT_SHARE_TITLE = "wechat_share_title";
    /** 微信分享简介（公众号） */
    public static final String CONFIG_KEY_ADMIN_WECHAT_SHARE_SYNOSIS = "wechat_share_synopsis";

    /** 是否启用分销 */
    public static final String CONFIG_KEY_BROKERAGE_FUNC_STATUS = "brokerage_func_status";
    /** 分销模式 :1-指定分销，2-人人分销 */
    public static final String CONFIG_KEY_STORE_BROKERAGE_STATUS = "store_brokerage_status";
    /** 分销模式 :1-指定分销 */
    public static final String STORE_BROKERAGE_STATUS_APPOINT = "1";
    /** 分销模式 :2-人人分销 */
    public static final String STORE_BROKERAGE_STATUS_PEOPLE = "2";
    /** 一级返佣比例 */
    public static final String CONFIG_KEY_STORE_BROKERAGE_RATIO = "store_brokerage_ratio";
    /** 二级返佣比例 */
    public static final String CONFIG_KEY_STORE_BROKERAGE_TWO = "store_brokerage_two";

    /** 是否开启会员功能 */
    public static final String CONFIG_KEY_VIP_OPEN = "vip_open";
    /** 是否开启充值功能 */
    public static final String CONFIG_KEY_RECHARGE_SWITCH = "recharge_switch";
    /** 是否开启门店自提 */
    public static final String CONFIG_KEY_STORE_SELF_MENTION = "store_self_mention";
    /** 腾讯地图key */
    public static final String CONFIG_SITE_TENG_XUN_MAP_KEY = "tengxun_map_key";
    /** 提现最低金额 */
    public static final String CONFIG_EXTRACT_MIN_PRICE = "user_extract_min_price";
    /** 提现冻结时间 */
    public static final String CONFIG_EXTRACT_FREEZING_TIME = "extract_time";

    /** 全场满额包邮开关 */
    public static final String STORE_FEE_POSTAGE_SWITCH = "store_free_postage_switch";
    /** 全场满额包邮金额 */
    public static final String STORE_FEE_POSTAGE = "store_free_postage";
    /** 积分抵用比例(1积分抵多少金额) */
    public static final String CONFIG_KEY_INTEGRAL_RATE = "integral_ratio";
    /** 下单支付金额按比例赠送积分（实际支付1元赠送多少积分) */
    public static final String CONFIG_KEY_INTEGRAL_RATE_ORDER_GIVE = "order_give_integral";

    /** 微信支付开关 */
    public static final String CONFIG_PAY_WEIXIN_OPEN  = "pay_weixin_open";
    /** 余额支付状态 */
    public static final String CONFIG_YUE_PAY_STATUS  = "yue_pay_status";

}
