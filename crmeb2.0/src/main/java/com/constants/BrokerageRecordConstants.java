package com.constants;

/**
 * 佣金记录-常量类
 * @author: 零风
 * @CreateDate: 2022/2/25 10:26
 */
public class BrokerageRecordConstants {

    /** 佣金记录类型—增加=1 */
    public static final Integer BROKERAGE_RECORD_TYPE_ADD = 1;
    /** 佣金记录类型—增加=add  */
    public static final String BROKERAGE_RECORD_TYPE_ADD_STR="add";
    /** 佣金记录类型—扣减=2 */
    public static final Integer BROKERAGE_RECORD_TYPE_SUB = 2;
    /** 佣金记录类型—扣减=sub  */
    public static final String BROKERAGE_RECORD_TYPE_SUB_STR="sub";

    /** 佣金记录状态—创建=1 */
    public static final Integer BROKERAGE_RECORD_STATUS_CREATE = 1;
    /** 佣金记录状态—冻结期=2 */
    public static final Integer BROKERAGE_RECORD_STATUS_FROZEN = 2;
    /** 佣金记录状态—完成=3 */
    public static final Integer BROKERAGE_RECORD_STATUS_COMPLETE = 3;
    /** 佣金记录状态—失效（订单退款）=4 */
    public static final Integer BROKERAGE_RECORD_STATUS_INVALIDATION = 4;
    /** 佣金记录状态—提现申请=5 */
    public static final Integer BROKERAGE_RECORD_STATUS_WITHDRAW = 5;

    /** 佣金记录-关联类型—订单 = order */
    public static final String BROKERAGE_RECORD_LINK_TYPE_ORDER = "order";
    /** 佣金记录-关联类型—提现 = extract */
    public static final String BROKERAGE_RECORD_LINK_TYPE_EXTRACT = "extract";
    /** 佣金记录-关联类型—转余额 = yue */
    public static final String BROKERAGE_RECORD_LINK_TYPE_YUE = "yue";
    /** 佣金记录-关联类型—系统 = system */
    public static final String BROKERAGE_RECORD_LINK_TYPE_SYSTEM = "system";
    /** 佣金记录-关联类型—佣金分红 = abonus */
    public static final String BROKERAGE_RECORD_LINK_TYPE_ABONUS= "abonus";

    /** 佣金记录-明细类型-订单佣金 = 1 */
    public static final int BROKERAGE_RECORD_TYPE_1 = 1;
    /** 佣金记录-明细类型-佣金提现申请 = 2 */
    public static final int BROKERAGE_RECORD_TYPE_2 = 2;
    /** 佣金记录-明细类型-佣金提现被拒绝 = 3  */
    public static final int BROKERAGE_RECORD_TYPE_3 = 3;
    /** 佣金记录-明细类型-佣金提现取消 = 4 */
    public static final int BROKERAGE_RECORD_TYPE_4 = 4;
    /** 佣金记录-明细类型-佣金转余额 = 5 */
    public static final int BROKERAGE_RECORD_TYPE_5 = 5;
    /** 佣金记录-明细类型-佣金分红 = 6 */
    public static final int BROKERAGE_RECORD_TYPE_6 = 6;

    /** 佣金记录标题—订单获得佣金=获得推广佣金 */
    public static final String BROKERAGE_RECORD_TITLE_ORDER = "获得推广佣金";
    /** 佣金记录标题-提现申请=提现申请 */
    public static final String BROKERAGE_RECORD_TITLE_WITHDRAW_APPLY = "佣金提现申请";
    /** 佣金记录标题—提现拒绝=提现申请拒绝 */
    public static final String BROKERAGE_RECORD_TITLE_WITHDRAW_FAIL = "佣金提现申请拒绝";
    /** 佣金记录标题—提现拒绝=佣金转余额 */
    public static final String BROKERAGE_RECORD_TITLE_BROKERAGE_YUE = "佣金转余额";

}
