package com.constants;

/**
 * 积分记录-常量类
 * @author: 零风
 * @CreateDate: 2021/12/27 10:14
 */
public class IntegralRecordConstants {

    /** 积分记录类型—增加 */
    public static final Integer INTEGRAL_RECORD_TYPE_ADD = 1;
    /** 积分记录类型—扣减 */
    public static final Integer INTEGRAL_RECORD_TYPE_SUB = 2;

    /** 积分记录是否已读-未读 */
    public static final Integer INTEGRAL_RECORD_ISREAD_0 = 0;
    /** 积分记录是否已读-已读 */
    public static final Integer INTEGRAL_RECORD_ISREAD_1 = 1;

    //公共积分状态
    /** 公共积分状态—创建 */
    public static final Integer PUBLIC_INTEGRAL_RECORD_STATUS_CREATE = 1;
    /** 公共积分状态—冻结期 */
    public static final Integer PUBLIC_INTEGRAL_RECORD_STATUS_FROZEN = 2;
    /** 公共积分状态—完成 = 3 */
    public static final Integer PUBLIC_INTEGRAL_RECORD_STATUS_COMPLETE = 3;
    /** 公共积分状态—失效（订单退款）= 4 */
    public static final Integer PUBLIC_INTEGRAL_RECORD_STATUS_INVALIDATION = 4;
    /** 公共积分状态-待结算 = 5 */
    public static final Integer PUBLIC_INTEGRAL_RECORD_STATUS_DJS = 5;
    /** 公共积分状态—已分配 = 6 */
    public static final Integer PUBLIC_INTEGRAL_RECORD_STATUS_distribu = 6;
    /** 公共积分状态—已存放(已放入公共积分库) = 7 */
    public static final int PUBLIC_INTEGRAL_RECORD_STATUS_YCF = 7;

    // 公共积分关联类型 - 数值
    /** 公共积分关联类型—订单 = 1 */
    public static final int PUBLIC_INTEGRAL_RECORD_LINK_TYPE_ORDER_INT = 1 ;
    /** 公共积分关联类型—系统随机奖励 = 2 */
    public static final int PUBLIC_INTEGRAL_RECORD_LINK_TYPE_RANDOM = 2;
    /** 公共积分关联类型—推广奖励 = 3 */
    public static final int PUBLIC_INTEGRAL_RECORD_LINK_TYPE_REWARD = 3;

    //用户积分状态
    /** 用户积分状态—创建 */
    public static final Integer INTEGRAL_RECORD_STATUS_CREATE = 1;
    /** 用户积分状态—冻结期 */
    public static final Integer INTEGRAL_RECORD_STATUS_FROZEN = 2;
    /** 用户积分状态—完成 = 3 */
    public static final Integer INTEGRAL_RECORD_STATUS_COMPLETE = 3;
    /** 用户积分状态—失效（订单退款）=4 */
    public static final Integer INTEGRAL_RECORD_STATUS_INVALIDATION = 4;
    /** 用户积分状态-待结算 = 5 */
    public static final Integer INTEGRAL_RECORD_STATUS_DJS = 5;
    /** 用户积分状态—已分配 = 6 */
    public static final Integer INTEGRAL_RECORD_STATUS_distribu = 6;


    // 用户积分关联类型 - 数值
    /** 用户积分关联类型—订单 = 1 */
    public static final Integer INTEGRAL_RECORD_LINK_TYPE_ORDER_INT = 1;
    /** 用户积分关联类型—签到 = 2 */
    public static final Integer INTEGRAL_RECORD_LINK_TYPE_SIGN_INT = 2;
    /** 用户积分关联类型—系统后台 = 3 */
    public static final int INTEGRAL_RECORD_LINK_TYPE_SYSTEM_INT = 3;

    // 关联类型 - 字符
    /** 积分记录关联类型—订单 = order */
    public static final String INTEGRAL_RECORD_LINK_TYPE_ORDER = "order";
    /** 积分记录关联类型—签到 = sign */
    public static final String INTEGRAL_RECORD_LINK_TYPE_SIGN = "sign";
    /** 积分记录关联类型—系统后台 = system */
    public static final String INTEGRAL_RECORD_LINK_TYPE_SYSTEM = "system";
    /** 积分记录关联类型-零售商返代理积分 = retailerRa */
    public static final String INTEGRAL_RECORD_LINK_TYPE_RETAILER_RA = "retailerRa";
    /** 积分记录关联类型-公共积分转待结算积分 = public_zhuan_dai */
    public static final String INTEGRAL_RECORD_LINK_TYPE_PUBLIC = "public_zhuan_dai";
    /** 积分记录关联类型-专属推广者的推广酒米转入可提现账户 = public_zhuan_dai_fee */
    public static final String INTEGRAL_RECORD_LINK_TYPE_PUBLIC_FEE = "public_zhuan_dai_fee";
    /** 积分记录关联类型-联盟商家的推广酒米转入可提现账户 = LMSJDTGJMZRKTXYE */
    public static final String INTEGRAL_RECORD_LINK_TYPE_LMSJDTGJMZRKTXYE = "LMSJDTGJMZRKTXYE";
    /** 积分记录关联类型-积分收款(账户ID收款) = collection */
    public static final String INTEGRAL_RECORD_LINK_TYPE_Collection = "collection";
    /** 积分记录关联类型-积分二维码收款 = collectionCode */
    public static final String INTEGRAL_RECORD_LINK_TYPE_Collection_CODE = "collectionCode";
    /** 积分记录关联类型-积分转账(账户ID转账) = transfer */
    public static final String INTEGRAL_RECORD_LINK_TYPE_transfer  = "transfer";
    /** 积分记录关联类型-积分二维码转账 = collection */
    public static final String INTEGRAL_RECORD_LINK_TYPE_transfer_CODE  = "transferCode";
    /** 积分记录关联类型-积分转入账户余额 = integralTransferIn */
    public static final String INTEGRAL_RECORD_LINK_TYPE_integralTransferIn  = "integralTransferIn";
    /** 积分记录关联类型-扫码向联盟商家转米 = isAllianceMerchants */
    public static final String INTEGRAL_RECORD_LINK_TYPE_isAllianceMerchants  = "isAllianceMerchants";
    /** 积分记录关联类型-服务费 = serviceFee */
    public static final String INTEGRAL_RECORD_LINK_TYPE_SERVICE_FEE  = "serviceFee";
    /** 积分记录关联类型-提现退还 = tixintuihuan */
    public static final String INTEGRAL_RECORD_LINK_TYPE_tixintuihuan = "tixintuihuan";

    // 标题 - 描述
    /** 积分记录标题—用户订单付款成功 */
    public static final String BROKERAGE_RECORD_TITLE_ORDER = "用户订单付款成功";
    /** 积分记录标题—签到经验奖励 */
    public static final String BROKERAGE_RECORD_TITLE_SIGN = "签到积分奖励";
    /** 积分记录标题—后台积分操作 */
    public static final String BROKERAGE_RECORD_TITLE_SYSTEM = "后台积分操作";
    /** 积分记录标题—订单退款 */
    public static final String BROKERAGE_RECORD_TITLE_REFUND = "积分订单退款";

}
