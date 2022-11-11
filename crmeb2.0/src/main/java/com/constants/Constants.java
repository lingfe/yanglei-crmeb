package com.constants;

/**
 *  配置类
 */
public class Constants {

    //系统日志相关 start
    public static final String SELECT = "查" ;
    public static final String DELETE = "删";
    public static final String INSERT = "增";
    public static final String UPDATE = "改";
    //END

    public static final String MAA_RIDE_NUM="maa_ride_num";//预约数量乘以..

    //字节相关参数
    public static final String ZIJIE_APl_URL="https://developer.toutiao.com";//字节请求URL
    public static final String ZIJIE_API_GET_ACCESS_TOKEN = "/api/apps/v2/jscode2session";//获取字节小程序access_token

    //额度控制
    public static final int ORDER_QUOTA_NO=0;//不增不减（实际支付不增加额度，兑换不减少额度）
    public static final int ORDER_QUOTA_ADD=1;//要增不减（实际支付增加额度、兑换不减少额度）
    public static final int ORDER_QUOTA_SUB=2;//不增要减（实际支付不增加额度、兑换减少额度）
    public static final int ORDER_QUOTA_ADD_SUB=3;//要增要减（实际支付增加额度、兑换减少额度）

    //用户特殊标签
    public static final String TARGID_LMSJ="10";//联盟商家
    public static final String TARGID_ZSTGZ="11";//专属推广者

    //订单配送方式
    public static final int ORDER_SHIPPING_TYPE_KUAIDI = 1 ;    //快递
    public static final int ORDER_SHIPPING_TYPE_ZITI = 2;       //上门自提
    public static final int ORDER_SHIPPING_TYPE_NO = 3;         //无需配送

    /** 增加-数值 = 1 */
    public static final Integer ADD = 1;
    /** 扣减-数字 = 2 */
    public static final Integer SUB = 2;
    /** 增加-字符串 = add */
    public static final String ADD_STR = "add";
    /** 扣减-字符串 = sub */
    public static final String SUB_STR = "sub";

    //发票相关
    public static final int INVOICE_RECORD_0=0;
    public static final String INVOICE_RECORD_0_STR="未开票";
    public static final int INVOICE_RECORD_1=1;
    public static final String INVOICE_RECORD_1_STR="待处理";
    public static final int INVOICE_RECORD_2=2;
    public static final String INVOICE_RECORD_2_STR="已开票";

    //token相关 start
    /** 管理端账号token前缀：TOKEN:ADMIN: */
    public static final String TOKEN_REDIS = "TOKEN:ADMIN:";
    /** 用户端登录token-redis存储前缀 */
    public static final String USER_TOKEN_REDIS_KEY_PREFIX = "TOKEN_USER:";
    /** token-过期时间 */
    public static final long TOKEN_EXPRESS_MINUTES = (60 * 24); //3小时
    /** 加密后的token-key */
    public static final String TOKEN_KEY = "TOKEN";
    /** 头部-token令牌key=Authori-zation */
    public static final String HEADER_AUTHORIZATION_KEY = "Authori-zation";
    //end

    //分页相关 start
    /** 当前页，默认=1 */
    public static final int DEFAULT_PAGE = 1;
    /** 每页数量，默认=20 */
    public static final int DEFAULT_LIMIT = 20;
    //end

    //云账户 - start
    public static final String C_APP_Key="I5w780bsWnH21Bh2z1JO3Nee1MTE2zly";
    public static final String C_3DES_Key="T7Kc3rfHNXwNN3e65Ncgw1fT";
    /** 云账户公钥 */
    public static final String C_Public_Key="-----BEGIN PUBLIC KEY-----\n" +
            "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDOKCqpyTwwdNVm9ROSY60wL/U3\n" +
            "Mo2yzSbkCtR3Ypjy/am/PBLVBQCXmxwxOeMsf/1gnyiKk4oQOGqRTlKPD175lk7k\n" +
            "DuXY4LI98s4eLTpYduyCo7GD9vvNXbJNYtDIAvE4IvOeXa47h1CJQ16gHXGIZqNf\n" +
            "InIR1bFrNWMgjrGNpQIDAQAB\n" +
            "-----END PUBLIC KEY-----";
    /** 商户ID(dealer_id) */
    public static final String dealer_id="25171525";
    /** 商户名称 */
    public static final String dealer_name="test贵州双尚";
    /** 综合服务主体名称 */
    public static final String broker_name="云账户（天津）共享经济信息咨询有限公司";
    /** 综合服务主体(broker_id) */
    public static final String broker_id="27532644";
    //end

    //数值字段
    public static final int NUM_ZERO = 0;
    public static final int NUM_ONE = 1;
    public static final int NUM_TWO = 2;
    public static final int NUM_THREE = 3;
    public static final int NUM_FIVE = 5;
    public static final int NUM_SEVEN = 7;
    public static final int NUM_TEN = 10;
    public static final int NUM_ONE_HUNDRED = 100;

    //日期格式字符串
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_UTC = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String DATE_FORMAT_DATE = "yyyy-MM-dd";
    public static final String DATE_FORMAT_YEAR = "yyyy";
    public static final String DATE_FORMAT_MONTH_DATE = "MM-dd";
    public static final String DATE_FORMAT_MONTH = "yyyy-MM";
    public static final String DATE_TIME_FORMAT_NUM = "yyyyMMddHHmmss";
    public static final String DATE_FORMAT_NUM = "yyyyMMdd";
    public static final String DATE_FORMAT_START = "yyyy-MM-dd 00:00:00";
    public static final String DATE_FORMAT_END = "yyyy-MM-dd 23:59:59";
    public static final String DATE_FORMAT_MONTH_START = "yyyy-MM-01 00:00:00";
    public static final String DATE_FORMAT_YEAR_START = "yyyy-01-01 00:00:00";
    public static final String DATE_FORMAT_YEAR_END = "yyyy-12-31 23:59:59";
    public static final String DATE_FORMAT_HHMMSS = "HH:mm:ss";
    public static final String DATE_FORMAT_START_PEREND = "00:00:00";
    public static final String DATE_FORMAT_END_PEREND = "23:59:59";
    public static final String DATE_FORMAT_HHMM = "yyyy-MM-dd HH:mm";

    //上传类型
    //public static final String UPLOAD_TYPE_IMAGE = "image";
    public static final String UPLOAD_LOCAL_URL="localUploadUrl";
    public static final String UPLOAD_TYPE_IMAGE = "crmebimage";    //上传类型-图片
    public static final String UPLOAD_TYPE_FILE = "file";           //上传类型-文件
    public static final String UPLOAD_MODEL_PATH_EXCEL = "excel";   //上传类型-excel文件类型
    public static final String UPLOAD_ROOT_PATH_CONFIG_KEY = "upload_root_path";//上传地址
    public static final String UPLOAD_IMAGE_EXT_STR_CONFIG_KEY = "image_ext_str";//图片上传
    public static final String UPLOAD_IMAGE_MAX_SIZE_CONFIG_KEY = "image_max_size";//图片最大上传大小
    public static final String UPLOAD_FILE_EXT_STR_CONFIG_KEY = "file_ext_str";//文件上传
    public static final String UPLOAD_FILE_MAX_SIZE_CONFIG_KEY = "file_max_size";//最大上传文件
    public static final int UPLOAD_TYPE_USER = 7; //用户上传

    //公众号支付配置
    public static final String CONFIG_KEY_PAY_WE_CHAT_APP_ID = "pay_weixin_appid"; //公众号appId
    public static final String CONFIG_KEY_PAY_WE_CHAT_MCH_ID = "pay_weixin_mchid"; //公众号配的商户号
    public static final String CONFIG_KEY_PAY_WE_CHAT_APP_SECRET = "pay_weixin_appsecret"; //公众号秘钥
    public static final String CONFIG_KEY_PAY_WE_CHAT_APP_KEY = "pay_weixin_key"; //公众号支付key

    //小程序支付配置
    public static final String CONFIG_KEY_PAY_ROUTINE_APP_ID = "pay_routine_appid"; //小程序appId
    public static final String CONFIG_KEY_PAY_ROUTINE_MCH_ID = "pay_routine_mchid"; //小程序分配的商户号
    public static final String CONFIG_KEY_PAY_ROUTINE_APP_SECRET = "pay_routine_appsecret"; //小程序秘钥
    public static final String CONFIG_KEY_PAY_ROUTINE_APP_KEY = "pay_routine_key"; //小程序支付key

    //微信APP支付配置
    public static final String CONFIG_KEY_PAY_WE_CHAT_APP_APP_ID = "pay_weixin_app_appid"; //appId
    public static final String CONFIG_KEY_PAY_WE_CHAT_APP_MCH_ID = "pay_weixin_app_mchid"; //商户号
    public static final String CONFIG_KEY_PAY_WE_CHAT_APP_APP_KEY = "pay_weixin_app_key"; //支付key
    public static final String CONFIG_KEY_PAY_WE_APP_SECRET = "pay_routine_appsecret"; //秘钥

    //config配置的key
    public static final String CONFIG_SITE_TENG_XUN_MAP_KEY = "tengxun_map_key"; //腾讯地图key

    //分销
    public static final String CONFIG_KEY_STORE_BROKERAGE_LEVEL = "store_brokerage_rate_num"; //返佣比例前缀
    public static final String CONFIG_KEY_STORE_BROKERAGE_RATE_ONE = "store_brokerage_ratio"; //一级返佣比例前缀
    public static final String CONFIG_KEY_STORE_BROKERAGE_RATE_TWO = "store_brokerage_two"; //二级返佣比例前缀
    public static final String CONFIG_KEY_STORE_BROKERAGE_MODEL = "store_brokerage_status"; //分销模式1-指定分销2-人人分销
    public static final String CONFIG_KEY_STORE_BROKERAGE_EXTRACT_TIME = "extract_time"; //佣金冻结时间
    public static final String CONFIG_KEY_STORE_INTEGRAL_EXTRACT_TIME = "freeze_integral_day"; //积分冻结时间(天)
    public static final String CONFIG_KEY_STORE_BROKERAGE_PERSON_PRICE = "store_brokerage_price"; //人人分销满足金额
    public static final String CONFIG_KEY_STORE_BROKERAGE_IS_OPEN = "brokerage_func_status"; //分销启用
    public static final String CONFIG_KEY_DISTRIBUTION_TYPE = "brokerage_bindind";//分销关系绑定方式:0-所有游湖，2-新用户
    /** 分销开启-公共开关：0关闭 */
    public static final String COMMON_SWITCH_CLOSE = "0";
    /** 分销开启-公共开关：1开启 */
    public static final String COMMON_SWITCH_OPEN = "1";

    //组合数据 gid
    public static final Integer GROUP_DATA_ID_INDEX_BEST_BANNER = 37; //中部推荐banner图
    public static final Integer GROUP_DATA_ID_INDEX_BANNER = 48; //首页banner滚动图
    /** 首页Banner图片-组合数据gid-精品推荐 = 52 */
    public static final Integer GROUP_DATA_ID_INDEX_RECOMMEND_BANNER = 52;
    public static final Integer GROUP_DATA_ID_ORDER_STATUS_PIC = 53; //订单详情状态图
    public static final Integer GROUP_DATA_ID_USER_CENTER_MENU = 54; //个人中心菜单
    public static final Integer GROUP_DATA_ID_SIGN = 55; //签到配置
    public static final Integer GROUP_DATA_ID_HOT_SEARCH = 56; //热门搜索
    public static final Integer GROUP_DATA_ID_INDEX_HOT_BANNER = 57; //热门榜单推荐Banner图片
    public static final Integer GROUP_DATA_ID_INDEX_NEW_BANNER = 58; //首发新品推荐Banner图片
    public static final Integer GROUP_DATA_ID_INDEX_BENEFIT_BANNER = 59; //首页促销单品推荐Banner图片
    public static final Integer GROUP_DATA_ID_SPREAD_BANNER_LIST = 60; //推广海报图
    public static final Integer GROUP_DATA_ID_RECHARGE_LIST = 62; //充值金额设置
    public static final Integer GROUP_DATA_ID_USER_CENTER_BANNER = 65; //个人中心轮播图
    public static final Integer GROUP_DATA_ID_INDEX_MENU = 67; //导航模块
    public static final Integer GROUP_DATA_ID_INDEX_NEWS_BANNER = 68; //首页滚动新闻
    public static final Integer GROUP_DATA_ID_INDEX_ACTIVITY_BANNER = 69; //首页活动区域图片
    public static final Integer GROUP_DATA_ID_INDEX_EX_BANNER = 70; //首页超值爆款
    public static final Integer GROUP_DATA_ID_INDEX_KEYWORDS = 71; //热门搜索
    public static final Integer GROUP_DATA_ID_ADMIN_LOGIN_BANNER_IMAGE_LIST = 72; //后台登录页面轮播图
    public static final Integer GROUP_DATA_ID_COMBINATION_LIST_BANNNER = 73; //拼团列表banner
    /** 零售商-推广海报图 = 86 */
    public static final Integer GROUP_DATA_ID_RETAILER_BANNER_LIST=86;
    /** 首页活动广告区域- groupID */
    public static final Integer GROUP_DATA_ID_INDEX_GUANGGAO=74;
    /** 积分商城bannEr-ID */
    public static final Integer GROUP_DATA_ID_INTEGAL_BANNER=76;
    /** 积分商城导航菜单-ID */
    public static final Integer GROUP_DATA_ID_INTEGAL_DAOHANG=77;
    /** 首页-品牌优选-banner */
    public static final Integer GROUP_DATA_ID_INDEX_BRANDS_BANNER=78;
    /** 品牌优选-详情-广告banner */
    public static final Integer GROUP_DATA_ID_INDEX_BRANDS_INFO_BANNER=79;
    /** 品牌优选-详情-推荐分类 */
    public static final Integer GROUP_DATA_ID_INDEX_BRANDS_INFO_TYPE=80;
    /** 优品推荐 */
    public static final Integer GROUP_DATA_ID_YOUPINGTUIJIAN=82;

    //签到
    public static final Integer SIGN_TYPE_INTEGRAL = 1; //积分
    public static final Integer SIGN_TYPE_EXPERIENCE = 2; //经验
    public static final String SIGN_TYPE_INTEGRAL_TITLE = "签到积分奖励"; //积分
    public static final String SIGN_TYPE_EXPERIENCE_TITLE = "签到经验奖励"; //经验

    //会员搜索日期类型
    public static final String SEARCH_DATE_DAY = "today"; //今天
    public static final String SEARCH_DATE_YESTERDAY = "yesterday"; //昨天
    public static final String SEARCH_DATE_LATELY_7 = "lately7"; //最近7天
    public static final String SEARCH_DATE_LATELY_30 = "lately30"; //最近30天
    public static final String SEARCH_DATE_WEEK = "week"; //本周
    public static final String SEARCH_DATE_PRE_WEEK = "preWeek"; //上周
    public static final String SEARCH_DATE_MONTH = "month"; //本月
    public static final String SEARCH_DATE_PRE_MONTH = "preMonth"; //上月
    public static final String SEARCH_DATE_YEAR = "year"; //年
    public static final String SEARCH_DATE_PRE_YEAR = "preYear"; //上一年

    //分类服务类型  类型，1 产品分类，2 附件分类，3 文章分类， 4 设置分类， 5 菜单分类， 6 配置分类， 7 秒杀配置
    public static final int CATEGORY_TYPE_PRODUCT = 1; //产品
    public static final int CATEGORY_TYPE_ATTACHMENT = 2; //附件分类
    public static final int CATEGORY_TYPE_ARTICLE = 3; //文章分类
    public static final int CATEGORY_TYPE_SET = 4; //设置分类
    public static final int CATEGORY_TYPE_MENU = 5; //菜单分类
    public static final int CATEGORY_TYPE_CONFIG = 6; //配置分类
    public static final int CATEGORY_TYPE_SKILL = 7; //秒杀配置

    //首页Banner图片-类型 start
    /** 首页Banner图片-类型-精品推荐 = 1 */
    public static final int INDEX_RECOMMEND_BANNER = 1;
    /** 首页Banner图片-类型-热门榜单 = 2 */
    public static final int INDEX_HOT_BANNER = 2;
    /** 首页Banner图片-类型-首发新品 = 3 */
    public static final int INDEX_NEW_BANNER = 3;
    /** 首页Banner图片-类型-促销单品 = 4 */
    public static final int INDEX_BENEFIT_BANNER = 4;
    /** 首页Banner图片-类型-优选推荐= 5 */
    public static final int INDEX_GOOD_BANNER = 5;
    //end

    //首页Banner图片-默认数量 start
    /** 首页Banner图片-默认数量-精品推荐= bastNumber */
    public static final String INDEX_BAST_LIMIT = "bastNumber";
    public static final String INDEX_FIRST_LIMIT = "firstNumber"; //首发新品个数
    public static final String INDEX_SALES_LIMIT = "promotionNumber"; //促销单品个数
    public static final String INDEX_HOT_LIMIT = "hotNumber"; //热门推荐个数
    public static final String INDEX_YOUPING_LIMIT = "youpingNumber"; //优品推荐个数
    //end

    //用户资金
    /** 收支类型-支出 = 0  */
    public static final int USER_BILL_PM_0=0;
    /** 收支类型-获得 = 1  */
    public static final int USER_BILL_PM_1=1;

    //提现关联类型
    /** 账户余额提现-关联类型-普通 = 1 */
    public static final int USER_BALANCE_TIXIN_LINKTYPE_1=1;
    /** 账户余额提现-关联类型-申请信用卡还款资金 = 2 */
    public static final int USER_BALANCE_TIXIN_LINKTYPE_2=2;
    /** 账户余额提现-关联类型-扫码向联盟商家转米 = 3 */
    public static final int USER_BALANCE_TIXIN_LINKTYPE_3=3;
    /** 账户余额提现-关联类型-积分兑换减少额度 = 4 */
    public static final int USER_BALANCE_TIXIN_LINKTYPE_JFDHSUBQUOTA=4;

    //提现状态
    public static final int USER_EXTRACT_STATUS_NO = -1;//不通过
    public static final int USER_EXTRACT_STATUS_SQZ = 0;//申请中/审核中
    public static final int USER_EXTRACT_STATUS_SUCCESS = 1;//通过/成功/已提现
    public static final int USER_EXTRACT_STATUS_FAIL = 2;//提现失败
    public static final int USER_EXTRACT_STATUS_GD = 3;//挂单
    public static final int USER_EXTRACT_STATUS_TH = 4;//退汇
    public static final int USER_EXTRACT_STATUS_CANCEL = 5;//取消

    //提现账单类型
    /** 提现 = extract */
    public static final String USER_BILL_TYPE_EXTRACT = "extract";
    /** 提现不通过退还余额 = extractNo */
    public static final String USER_BILL_TYPE_EXTRACT_NO = "extractNo";
    /** 提现服务费 = extractFee */
    public static final String USER_BILL_TYPE_EXTRACT_FEE = "extractFee";
    /** 专属推广者的推广酒米转入可提现账户 = zhuanruketixianzhanghu */
    public static final String USER_BILL_TYPE_zhuanruketixianzhanghu = "zhuanruketixianzhanghu";
    /** 联盟商家的推广酒米转入可提现账户 = LMSJDTGJMZRKTXYE */
    public static final String USER_BILL_TYPE_LMSJDTGJMZRKTXYE = "LMSJDTGJMZRKTXYE";

    //提现配置字段
    public static final String USER_ExtractToExaminePrice = "userExtractToExaminePrice";//提现审核金额
    public static final String CONFIG_BANK_LIST = "user_extract_bank"; //可提现银行
    public static final String CONFIG_EXTRACT_FREEZING_TIME = "extract_time"; //提现冻结时间
    public static final String CONFIG_EXTRACT_MIN_PRICE = "user_extract_min_price";//提现最低金额
    public static final String CONFIG_KEY_STORE_BROKERAGE_USER_EXTRACT_MIN_PRICE = "user_extract_min_price"; //提现最低金额
    public static final String CONFIG_KEY_STORE_BROKERAGE_USER_EXTRACT_BANK = "user_extract_bank"; //提现银行卡

    /** 转入账户余额类型-佣金 = 1  */
    public static final int USER_BILL_transferIn_TYPE_1=1;
    /** 转入账户余额类型-积分 = 2  */
    public static final int USER_BILL_transferIn_TYPE_2=2;
    /** 转入账户余额类型-用户扫码转米转入账户余额 = 3  */
    public static final int USER_BILL_transferIn_TYPE_3=3;

    //用户账单相关
    /** 用户余额 = now_money */
    public static final String USER_BILL_CATEGORY_MONEY = "now_money"; //用户余额
    public static final String USER_BILL_CATEGORY_INTEGRAL = "integral"; //积分
    public static final String USER_BILL_CATEGORY_SHARE = "share"; //分享
    /** 经验值 = experience */
    public static final String USER_BILL_CATEGORY_EXPERIENCE = "experience";
    public static final String USER_BILL_CATEGORY_BROKERAGE_PRICE = "brokerage_price"; //佣金金额
    public static final String USER_BILL_CATEGORY_SIGN_NUM = "sign_num"; //签到天数
    public static final String USER_BILL_TYPE_BROKERAGE = "brokerage"; //推广佣金
    public static final String USER_BILL_TYPE_DEDUCTION = "deduction"; //抵扣
    /** 佣金转入余额 = transferIn */
    public static final String USER_BILL_TYPE_TRANSFER_IN = "transferIn";
    /** 联盟商家收到用户扫码转米转入余额 = isAllianceMerchants */
    public static final String USER_BILL_TYPE_isAllianceMerchants = "isAllianceMerchants";
    /** 积分转入余额 = integralTransferIn */
    public static final String USER_BILL_TYPE_integralTransferIn = "integralTransferIn";
    public static final String USER_BILL_TYPE_GAIN = "gain"; //购买商品赠送
    /** 购买 = pay_money */
    public static final String USER_BILL_TYPE_PAY_MONEY = "pay_money";
    public static final String USER_BILL_TYPE_PAY_PRODUCT = "pay_product"; //购买商品
    public static final String USER_BILL_TYPE_PAY_PRODUCT_INTEGRAL_BACK = "pay_product_integral_back"; //商品退积分
    /** 商品退款 = pay_product_refund */
    public static final String USER_BILL_TYPE_PAY_PRODUCT_REFUND = "pay_product_refund";
    public static final String USER_BILL_TYPE_RECHARGE = "recharge"; //佣金转入
    /** 充值 = pay_recharge */
    public static final String USER_BILL_TYPE_PAY_RECHARGE = "pay_recharge"; //充值
    public static final String USER_BILL_TYPE_SHARE = "share"; //用户分享记录
    /** 签到 = sign */
    public static final String USER_BILL_TYPE_SIGN = "sign";
    /** 补签 = supplementary_signature */
    public static final String USER_BILL_TYPE_SUPPLEMENTARY_SIGNATURE = "supplementary_signature";
    public static final String USER_BILL_TYPE_ORDER = "order"; //订单
    /** 订单支付 = pay_order  */
    public static final String USER_BILL_TYPE_PAY_ORDER = "pay_order"; //订单支付
    /** 系统增加 = system_add */
    public static final String USER_BILL_TYPE_SYSTEM_ADD = "system_add";
    /** 系统减少 = system_sub */
    public static final String USER_BILL_TYPE_SYSTEM_SUB = "system_sub";
    public static final String USER_BILL_TYPE_PAY_MEMBER = "pay_member";// 会员支付
    /** 线下支付 = offline_scan  */
    public static final String USER_BILL_TYPE_OFFLINE_SCAN = "offline_scan";// 线下支付
    /** 用户充值退款 = user_recharge_refund */
    public static final String USER_BILL_TYPE_USER_RECHARGE_REFUND = "user_recharge_refund";
    /** 零售商订单处理 = retailer_order */
    public static final String USER_BILL_TYPE_RETAILER_ORDER = "retailer_order";
    /** 区域代理结算订单 = ra_order_settlement */
    public static final String USER_BILL_TYPE_RA_ORDER_settlement ="ra_order_settlement";
    /** 供应商结算订单 = supplier_order_settlement */
    public static final String USER_BILL_TYPE_SUPPLIER_ORDER_settlement ="supplier_order_settlement";
    /** 积分支付订单分账 = payProfitSharingIntegral */
    public static final String USER_BILL_TYPE_payProfitSharingIntegral = "payProfitSharingIntegral";

    //订单相关-start
    //订单类型：0-普通订单、1-视频号订单、2-区域代理订单、3-零售商订单、4-供应商订单、5=联盟商家订单
    /** 普通订单 = 0 */
    public static final int ORDER_TYPE_0=0;
    /** 视频号订单 = 1 */
    public static final int ORDER_TYPE_1=1;
    /** 区域代理订单 = 2 */
    public static final int ORDER_TYPE_2=2;
    /** 零售商订单 = 3 */
    public static final int ORDER_TYPE_3=3;
    /** 供应商订单 = 4 */
    public static final int ORDER_TYPE_4=4;
    /** 联盟商家订单 = 5 */
    public static final int ORDER_TYPE_5=5;

    //订单状态-英文
    public static final String ORDER_STATUS_ALL = "all"; //所有
    public static final String ORDER_STATUS_UNPAID = "unPaid"; //未支付
    public static final String ORDER_STATUS_NOT_SHIPPED = "notShipped"; //未发货
    public static final String ORDER_STATUS_SPIKE = "spike"; //待收货
    public static final String ORDER_STATUS_BARGAIN = "bargain"; //已收货待评价
    public static final String ORDER_STATUS_COMPLETE = "complete"; //交易完成
    public static final String ORDER_STATUS_TOBE_WRITTEN_OFF = "toBeWrittenOff"; //待核销
    public static final String ORDER_STATUS_APPLY_REFUNDING = "applyRefund"; //申请退款
    public static final String ORDER_STATUS_REFUNDING = "refunding"; //退款中
    public static final String ORDER_STATUS_REFUNDED = "refunded"; //已退款
    public static final String ORDER_STATUS_DELETED = "deleted"; //已删除
    //订单状态-中文
    public static final String ORDER_STATUS_STR_UNPAID = "未支付"; //未支付
    public static final String ORDER_STATUS_STR_NOT_SHIPPED = "未发货"; //未发货
    public static final String ORDER_STATUS_STR_SPIKE = "待收货"; //待收货
    public static final String ORDER_STATUS_STR_BARGAIN = "待评价"; //已收货待评价
    public static final String ORDER_STATUS_STR_TAKE = "用户已收货"; //用户已收货
    public static final String ORDER_STATUS_STR_COMPLETE = "交易完成"; //交易完成
    public static final String ORDER_STATUS_STR_TOBE_WRITTEN_OFF = "待核销"; //待核销
    public static final String ORDER_STATUS_STR_APPLY_REFUNDING = "申请退款"; //申请退款
    public static final String ORDER_STATUS_STR_REFUNDING = "退款中"; //退款中
    public static final String ORDER_STATUS_STR_REFUNDED = "已退款";
    public static final String ORDER_STATUS_STR_DELETED = "已删除"; //已删除
    //订单状态-H5端数值
    public static final int ORDER_STATUS_H5_UNPAID = 0; // 未支付
    public static final int ORDER_STATUS_H5_NOT_SHIPPED = 1; // 待发货
    public static final int ORDER_STATUS_H5_SPIKE = 2; // 待收货
    public static final int ORDER_STATUS_H5_JUDGE = 3; // 待评价
    public static final int ORDER_STATUS_H5_COMPLETE = 4; // 已完成
    public static final int ORDER_STATUS_H5_VERIFICATION = 5; // 待核销
    public static final int ORDER_STATUS_H5_REFUNDING = -1; // 退款中
    public static final int ORDER_STATUS_H5_REFUNDED = -2; // 已退款
    public static final int ORDER_STATUS_H5_REFUND = -3; // 退款
    //订单状态-数值
    public static final int ORDER_STATUS_INT_PAID = 0; //已支付,待发货
    public static final int ORDER_STATUS_INT_SPIKE = 1; //待收货
    public static final int ORDER_STATUS_INT_BARGAIN = 2; //已收货，待评价
    public static final int ORDER_STATUS_INT_COMPLETE = 3;//已完成

    //订单操作redis队列
    public static final String ORDER_TASK_REDIS_KEY_AFTER_DELETE_BY_USER = "alterOrderDeleteByUser"; // 用户删除订单后续操作
    /** 订单完成之后相关业务处理task-redis队列Key = alterOrderCompleteByUser */
    public static final String ORDER_TASK_REDIS_KEY_AFTER_COMPLETE_BY_USER = "alterOrderCompleteByUser";
    public static final String ORDER_TASK_REDIS_KEY_AFTER_CANCEL_BY_USER = "alterOrderCancelByUser"; // 用户取消订单后续操作
    /** 订单退款后续处理task-redis队列key = alterOrderRefundByUser */
    public static final String ORDER_TASK_REDIS_KEY_AFTER_REFUND_BY_USER = "alterOrderRefundByUser";
    /** 订单收货之后相关业务处理task-redis队列key = alterOrderTakeByUser  */
    public static final String ORDER_TASK_REDIS_KEY_AFTER_TAKE_BY_USER = "alterOrderTakeByUser";
    /** 订单支付成功之后相关业务处理task-redis队列key = orderPaySuccessTask */
    public static final String ORDER_TASK_PAY_SUCCESS_AFTER = "orderPaySuccessTask";
    /** 订单分账task-redis队列key = orderTaskPayProfitSharing */
    public static final String ORDER_TASK_payProfitSharing = "orderTaskPayProfitSharing";
    /** 订单取消task-redis队列key = order_auto_cancel_key */
    public static final String ORDER_AUTO_CANCEL_KEY = "orderAutoCancelKey";

    //订单操作类型
    public static final String ORDER_STATUS_STR_SPIKE_KEY = "send"; //待收货 KEY
    public static final String ORDER_LOG_REFUND_PRICE = "refund_price"; //退款
    public static final String ORDER_LOG_EXPRESS = "express"; //快递
    public static final String ORDER_LOG_DELIVERY = "delivery"; //送货
    public static final String ORDER_LOG_DELIVERY_GOODS = "delivery_goods"; //送货
    public static final String ORDER_LOG_REFUND_REFUSE = "refund_refuse"; //不退款
    public static final String ORDER_LOG_REFUND_APPLY = "apply_refund"; //申请退款
    public static final String ORDER_LOG_PAY_SUCCESS = "pay_success"; //支付成功
    public static final String ORDER_LOG_DELIVERY_VI = "delivery_fictitious"; //虚拟发货
    public static final String ORDER_LOG_EDIT = "order_edit"; //编辑订单
    public static final String ORDER_LOG_PAY_OFFLINE = "offline"; //线下付款订单
    public static final String ORDER_LOG_USER_TAKE_DELIVERY ="user_take_delivery";//用户已收货

    // 订单缓存
    public static final long ORDER_CASH_CONFIRM = (60);
    public static final String ORDER_CACHE_PER = "ORDER_CACHE:"; // redis缓存订单前缀

    //订单操作类型 -> 消息
    public static final String ORDER_LOG_MESSAGE_REFUND_PRICE = "退款给用户{amount}元"; //退款
    public static final String ORDER_LOG_MESSAGE_EXPRESS = "已发货 快递公司：{deliveryName}, 快递单号：{deliveryCode}"; //快递
    public static final String ORDER_LOG_MESSAGE_DELIVERY = "已配送 发货人：{deliveryName}, 发货人电话：{deliveryCode}"; //送货
    public static final String ORDER_LOG_MESSAGE_DELIVERY_FICTITIOUS = "已虚拟发货"; //已虚拟发货
    public static final String ORDER_LOG_MESSAGE_REFUND_REFUSE = "不退款款因：{reason}"; //不退款款因
    public static final String ORDER_LOG_MESSAGE_PAY_SUCCESS = "用户付款成功";

    //订单基本操作字样
    public static String RESULT_ORDER_PAY_OFFLINE = "订单号 ${orderNo} 现在付款 ${price} 成功";
    public static String RESULT_ORDER_NOTFOUND = "订单号 ${orderCode} 未找到";
    public static String RESULT_ORDER_NOTFOUND_IN_ID = "订单id ${orderId} 未找到";
    public static String RESULT_ORDER_PAYED = "订单号 ${orderCode} 已支付";
    public static String RESULT_ORDER_EDIT_PRICE_SAME = "修改价格不能和支付价格相同 原价 ${oldPrice} 修改价 ${editPrice}";
    public static String RESULT_ORDER_EDIT_PRICE_SUCCESS = "订单号 ${orderNo} 修改价格 ${price} 成功";
    public static String RESULT_ORDER_EDIT_PRICE_LOGS = "订单价格 ${orderPrice} 修改实际支付金额为 ${price} 元";

    //订单核销-返回字样 Order response text info
    public static String RESULT_VERIFICATION_ORDER_NOT_FUND = "核销码 ${vCode} 的订单未找到";
    public static String RESULT_VERIFICATION_ORDER_VED = "核销码 ${vCode} 的订单已核销";
    public static String RESULT_VERIFICATION_NOTAUTH = "没有核销权限";
    public static String RESULT_VERIFICATION_USER_EXIST = "当前用户已经是核销员";
    //end

    //区域代理-start
    /** 区域代理(全局)设置消费返佣金比例 = generalAgencyCommissionRate */
    public static final String CONFIG_KEY_STORE_GACR="generalAgencyCommissionRate";
    /** 区域代理(全局)自定义佣金记录标题 */
    public static final String CONFIG_KEY_STORE_GACR_TITLE="generalAgencyCommissionRateTitle";
    //end

    //第三方登录token类型-start
    /** 登录类型： 公众号=1  */
    public static final int THIRD_LOGIN_TOKEN_TYPE_PUBLIC  = 1; //公众号
    /** 登录类型： 小程序=2  */
    public static final int THIRD_LOGIN_TOKEN_TYPE_PROGRAM  = 2; //小程序
    /** 登录类型： unionid=3  */
    public static final int THIRD_LOGIN_TOKEN_TYPE_UNION_ID  = 3; //unionid
    /** 登录类型： 后台登录公众号=4  */
    public static final int THIRD_ADMIN_LOGIN_TOKEN_TYPE_PUBLIC  = 4; //后台登录公众号
    /** 登录类型: ios微信=5 */
    public static final int THIRD_LOGIN_TOKEN_TYPE_IOS_WX  = 5; //ios微信
    /** 登录类型: android微信 = 6 */
    public static final int THIRD_LOGIN_TOKEN_TYPE_ANDROID_WX = 6; //android微信
    /** 登录类型: ios账户(苹果账号)=7 */
    public static final int THIRD_LOGIN_TOKEN_TYPE_IOS  = 7; //ios账户(苹果账号)
    /** 登录类型: 卡卡乐账号 =8 */
    public static final int THIRD_LOGIN_TOKEN_TYPE_KAKALE  = 8; //卡卡乐账号
    //end

    //商品相关-start
    //商品类型 活动类型 0=商品，1=秒杀，2=砍价，3=拼团 attrResult表用到
    /** 普通商品=0 */
    public static final Integer PRODUCT_TYPE_NORMAL = 0;
    public static final String PRODUCT_TYPE_NORMAL_STR = "默认";
    /** 秒杀商品 = 1 */
    public static final Integer PRODUCT_TYPE_SECKILL = 1;
    public static final String PRODUCT_TYPE_SECKILL_STR = "秒杀";
    /** 砍价商品 = 1 */
    public static final Integer PRODUCT_TYPE_BARGAIN = 2;
    public static final String PRODUCT_TYPE_BARGAIN_STR = "砍价";
    /** 拼团商品 = 3 */
    public static final Integer PRODUCT_TYPE_PINGTUAN= 3;
    public static final String PRODUCT_TYPE_PINGTUAN_STR= "拼团";
    /** 积分兑换商品=4 */
    public static final Integer PRODUCT_TYPE_INTEGAL= 4;
    public static final String PRODUCT_TYPE_INTEGAL_STR= "积分兑换商品";

    //商品库存变动队列key
    public static final String PRODUCT_STOCK_UPDATE = "product_stock_update"; // 普通商品库存变动队列key
    public static final String PRODUCT_SECKILL_STOCK_UPDATE = "product_seckill_stock_update";// 商品秒杀库存队列Key
    public static final String PRODUCT_BARGAIN_STOCK_UPDATE = "product_bargain_stock_update";// 商品砍价库存队列Key
    public static final String PRODUCT_COMBINATION_STOCK_UPDATE = "product_combination_stock_update";// 商品拼团库存队列Key
    public static final String PRODUCT_STOCK_LIST = "product_stock_list";// 商品库存redis-key
    public static String PRODUCT_LOG_KEY = "visit_log_key";//商品日志记录task-redis队列Key

    //商品评论类型
    /** 普通商品 = product */
    public static String STORE_REPLY_TYPE_PRODUCT = "product";
    /** 秒杀商品 = seckill */
    public static String STORE_REPLY_TYPE_SECKILL = "seckill";
    /** 拼团商品 = pintuan */
    public static String STORE_REPLY_TYPE_PINTUAN = "pintuan";
    /** 砍价商品 = bargain */
    public static String STORE_REPLY_TYPE_BARGAIN = "bargain";
    //end

    // 公众号模板消息
    public static final String WE_CHAT_TEMP_KEY_COMBINATION_SUCCESS = "OPENTM407456411";// 拼团成功
    public static final String WE_CHAT_TEMP_KEY_BARGAIN_SUCCESS = "OPENTM410292733";// 砍价成功
    public static final String WE_CHAT_TEMP_KEY_EXPRESS = "OPENTM200565259";// 订单发货提醒
    public static final String WE_CHAT_TEMP_KEY_DELIVERY = "OPENTM207707249";// 订单配送通知
    public static final String WE_CHAT_TEMP_KEY_ORDER_PAY = "OPENTM207791277";// 订单支付成功通知
    public static final String WE_CHAT_TEMP_KEY_ORDER_RECEIVING = "OPENTM413386489";// 订单收货通知
    public static final String WE_CHAT_TEMP_KEY_ORDER_REFUND = "OPENTM207791277";// 退款进度通知
    public static final String WE_CHAT_PUBLIC_TEMP_KEY_RECHARGE = "OPENTM200565260";// 充值成功

    // 小程序服务通知
    public static final String WE_CHAT_PROGRAM_TEMP_KEY_COMBINATION_SUCCESS = "5164";// 拼团成功
    public static final String WE_CHAT_PROGRAM_TEMP_KEY_BARGAIN_SUCCESS = "2920";// 砍价成功
    public static final String WE_CHAT_PROGRAM_TEMP_KEY_EXPRESS = "467";// 订单发货提醒
    public static final String WE_CHAT_PROGRAM_TEMP_KEY_DELIVERY = "14198";// 订单配送通知
    public static final String WE_CHAT_PROGRAM_TEMP_KEY_ORDER_PAY = "516";// 订单支付成功通知
    public static final String WE_CHAT_PROGRAM_TEMP_KEY_ORDER_RECEIVING = "9283";// 订单收货通知
    public static final String WE_CHAT_PROGRAM_TEMP_KEY_RECHARGE = "OPENTM200565260";
    public static final String WE_CHAT_PROGRAM_TEMP_KEY_INTEGAL_TONZHI = "4520"; //积分到账通知

    //消息模板队列key
    public static final String WE_CHAT_MESSAGE_KEY_PUBLIC = "we_chat_public_message_list";
    public static final String WE_CHAT_MESSAGE_KEY_PROGRAM = "we_chat_program_message_list";
    public static final String WE_CHAT_MESSAGE_INDUSTRY_KEY = "we_chat_message_industry";

    //支付宝app支付配置-start
    /** 读取：支付宝app-支付-appid */
    public static final String CONFIG_KEY_PAY_ZFB_APP_ZF_APPID="zfb_app_zf_appid";
    /** 读取：支付宝app-支付-密钥 */
    public static final String CONFIG_KEY_PAY_ZFB_APP_ZF_SECRET_KEY="zfb_app_zf_secret_key";
    /** 读取：支付宝app-支付-公钥 */
    public static final String CONFIG_KEY_PAY_ZFB_APP_ZF_PUBLIC_KEY="zfb_app_zf_public_key";
    //end

    //静态数据redis-key
    public static final String CITY_LIST = "city_list";//城市数据redis-key
    public static final String CITY_LIST_TREE = "city_list_tree";//城市数据tree树结构-redis-key
    public static final String CITY_LIST_LEVEL_1 = "city_list_level_1";//城市数据tree树结构-redis-key-带等级
    public static final String DATA_BUSINESS_TYPE = "data_business_type";//经营类型数据redis-key
    /** redis中-系统config配置表-字段名称 */
    public static final String CONFIG_LIST = "config_list"; //配置列表

    //前端用户登录方式
    public static final String USER_LOGIN_TYPE_H5 = "h5";//用户登录方式-h5
    public static final String USER_LOGIN_TYPE_PUBLIC = "wechat"; //用户登录方式-公众号
    public static final String USER_LOGIN_TYPE_PROGRAM = "routine";//用户登录方式-小程序
    public static final String USER_LOGIN_TYPE_IOS_WX = "iosWx"; //用户登录方式-App
    public static final String USER_LOGIN_TYPE_ANDROID_WX = "androidWx";//用户登录方式-androidWx
    public static final String USER_LOGIN_TYPE_IOS = "ios";//用户登录方式-ios

    //分账记录类型
    public static final int SplitAccountRecord_TYPE_1=1;//微信支付订单分账记录
    public static final int SplitAccountRecord_TYPE_2=2;//支付宝支付订单分账记录
    public static final int SplitAccountRecord_TYPE_3=3;//积分支付订单分账记录

    //排序方式
    public static final String SORT_ASC = "asc";//升序排序
    public static final String SORT_DESC = "desc";//降序排序

    //过滤响应数据中包含的几种路径
    public static final String filter_response_IMAGE="crmebimage/"; //图片路径
    public static final String filter_response_File="file/operation/";        //文件路径
    public static final String filter_response_product="product/";  //批量导入的商品的图片的路径

    //响应
    public static final String FAIL     = "FAIL";
    public static final String SUCCESS  = "SUCCESS";
    public static final int HTTPSTATUS_CODE_SUCCESS = 200; //响应-成功状态码 = 200

    //微信消息模板 tempKey
    public static final String WE_CHAT_TEMP_KEY_FIRST = "first";
    public static final String WE_CHAT_TEMP_KEY_END = "remark";

    //砍价计算比例
    public static String BARGAIN_TATIO_DOWN = "0.2";//下行
    public static String BARGAIN_TATIO_UP = "0.8";//上行

    //时间类型
    public static String DATE_TIME_TYPE_BEGIN = "begin";//开始时间
    public static String DATE_TIME_TYPE_END = "end"; //结束时间

    //需要支付的业务类型
    public static final String SERVICE_PAY_TYPE_ORDER = "order"; //订单
    public static final String SERVICE_PAY_TYPE_RECHARGE = "recharge"; //充值

    //积分
    public static final String CONFIG_KEY_INTEGRAL_RATE = "integral_ratio"; //积分抵用比例(1积分抵多少金额
    public static final String CONFIG_KEY_INTEGRAL_RATE_ORDER_GIVE = "order_give_integral"; //下单支付金额按比例赠送积分（实际支付1元赠送多少积分

    //快递信息缓存
    public static final String LOGISTICS_KEY = "logistics_";
    public static final String CONFIG_KEY_LOGISTICS_APP_CODE = "system_express_app_code"; //快递查询密钥
    //物流  https://market.aliyun.com/products/56928004/cmapi021863.html#sku=yuncode15863000015
    /** 快递查询接口 */
    public static String LOGISTICS_API_URL = "https://wuliu.market.alicloudapi.com/kdi";

    public static final String CONFIG_RECHARGE_ATTENTION = "recharge_attention"; //充值注意事项
    public static final String CONFIG_KEY_RECHARGE_MIN_AMOUNT = "store_user_min_recharge"; //最小充值金额

    //用户等级升级
    public static final String USER_LEVEL_OPERATE_LOG_MARK = "尊敬的用户 【{$userName}】, 在{$date}赠送会员等级成为{$levelName}会员";
    public static final String USER_LEVEL_UP_LOG_MARK = "尊敬的用户 【{$userName}】, 在{$date}您升级为为{$levelName}会员";


    /** 后台登录方式 - 公众号  */
    public static final String ADMIN_LOGIN_TYPE_WE_CHAT_FROM_PUBLIC = "admin_public"; //公众号
    /** 后台管理员操作资金mark */
    public static final String USER_BILL_OPERATE_LOG_TITLE = "{$title}{$operate}了{$value}{$founds}";
    /** 用户登录密码加密混淆字符串 */
    public static final String USER_LOGIN_PASSWORD_MD5_KEYWORDS = "crmeb";
    /** 用户默认头像 */
    public static final String USER_DEFAULT_AVATAR_CONFIG_KEY = "h5_avatar";
    /** 用户默认推广人id */
    public static final Integer USER_DEFAULT_SPREAD_ID = 0;
    /** 导出最大数值 */
    public static final Integer EXPORT_MAX_LIMIT = 99999;
    /** 商品最多选择的分类数量 */
    public static final Integer PRODUCT_SELECT_CATEGORY_NUM_MAX = 10;
    /** 云智服-小程序插件 */
    public static final String CONFIG_KEY_YZF_H5_URL = "yzf_h5_url"; //云智服H5 url
    /** 验证码过期时间 */
    public static final String CONFIG_KEY_SMS_CODE_EXPIRE = "sms_code_expire";
    /** 订单状态日志-cache_key_create_order = 生成订单 */
    public static final String ORDER_STATUS_CACHE_CREATE_ORDER = "cache_key_create_order";
    /** CND-URL测试用 */
    public static String CND_URL = "https://wuht-1300909283.cos.ap-chengdu.myqcloud.com";
    /** 生成二维码图片-错误提示 */
    public static String RESULT_QRCODE_PRAMERROR = "生成二维码参数不合法";
    /** 验证码redis-key前缀=validate_code_ */
    public static final String VALIDATE_REDIS_KEY_PREFIX = "validate_code_";

}
