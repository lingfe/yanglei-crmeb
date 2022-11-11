package com.zbkj.crmeb.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.front.response.SpreadCommissionDetailResponse;
import com.zbkj.crmeb.user.model.UserBrokerageRecord;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 用户佣金记录-service层接口
 * @author: 零风
 * @CreateDate: 2021/12/23 10:41
 */
public interface UserBrokerageRecordService extends IService<UserBrokerageRecord> {

    /**
     * 佣金分红task(定时任务)
     * @Author 零风
     * @Date  2022/4/6
     * @return
     */
    void brokerageABonusTask();

    /**
     * 得到-用户佣金记录实体对象(公共)
     * @param uid       用户ID标识
     * @param linkId    关联ID标识
     * @param linkType  关联类型
     * @param type      明细类型
     * @param pm        收支类型
     * @param price     金额
     * @param balance   余额
     * @param frozenTime    冻结时间
     * @param thawTime      解冻时间
     * @Author 零风
     * @Date  2021/12/23
     * @return 用户佣金记录实体对象
     */
    UserBrokerageRecord getUserBrokerageRecord(Integer uid,String linkId,
                                               String linkType,Integer type,Integer pm,
                                               BigDecimal price,BigDecimal balance,
                                               Integer frozenTime,Long thawTime);

    /**
     * 获取-佣金记录列表
     * @param linkId    关联id
     * @param linkType  关联类型
     * @Author 零风
     * @Date  2021/12/24
     * @return 佣金记录列表
     */
    List<UserBrokerageRecord> findListByLinkIdAndLinkType(String linkId, String linkType);

    /**
     * 获取-佣金记录
     * @param linkId    关联id
     * @param linkType  关联类型
     * @Author 零风
     * @Date  2021/12/24
     * @return 佣金记录
     */
    UserBrokerageRecord getByLinkIdAndLinkType(String linkId, String linkType);

    /**
     * 佣金解冻
     * @Author 零风
     * @Date  2021/12/24
     */
    void brokerageThaw();

    /**
     * 统计-昨天得佣金
     * @param uid   用户ID标识
     * @Author 零风
     * @Date  2021/12/24
     * @return
     */
    BigDecimal getYesterdayIncomes(Integer uid);

    /**
     * 获取-用户佣金明细列表
     * @param uid 用户ID标识
     * @param pageParamRequest 分页参数
     * @Author 零风
     * @Date  2021/12/24
     * @return 分页数据
     */
    PageInfo<SpreadCommissionDetailResponse> findDetailListByUid(Integer uid, PageParamRequest pageParamRequest);

    /**
     * 获取-用户佣金记录条数
     * @param uid 用户uid标识
     * @Author 零风
     * @Date  2021/12/24
     * @return 数量
     */
    Integer getSpreadCountByUid(Integer uid);

    /**
     * 获取-用户佣金记录列表
     * @param uid 用户uid
     * @param pageParamRequest 分页参数
     * @Author 零风
     * @Date  2021/12/24
     * @return 记录列表
     */
    List<UserBrokerageRecord> findSpreadListByUid(Integer uid, PageParamRequest pageParamRequest);

    /**
     * 获取-用户月份对应的推广订单佣金数量
     * @param uid       用户uid标识
     * @param monthList 月份列表
     * @Author 零风
     * @Date  2021/12/24
     * @return Map月份数据
     */
    Map<String, Integer> getSpreadCountByUidAndMonth(Integer uid, List<String> monthList);

    /**
     * 获取-佣金排行榜（根据周、月排名）
     * @param type week-周、month-月
     * @param pageParamRequest 分页参数
     * @Author 零风
     * @Date  2021/12/24
     * @return 记录列表
     */
    List<UserBrokerageRecord> getBrokerageTopByDate(String type, PageParamRequest pageParamRequest);

    /**
     * 获取-用户佣金记录列表(All)
     * @param uid   用户uid标识
     * @Author 零风
     * @Date  2021/12/24
     * @return 记录列表
     */
    List<UserBrokerageRecord> getSpreadListByUid(Integer uid);

    /**
     * 佣金总金额（单位时间）
     * 获取-日期范围内增加或减少的佣金总金额（单位时间）
     * @param dateLimit 时间参数
     * @param pm        收支类型
     * @Author 零风
     * @Date  2021/12/24
     * @return 佣金总金额
     */
    BigDecimal getTotalSpreadPriceBydateLimit(String dateLimit,Integer pm);

    /**
     * 获取-用户某个时间范围内佣金记录详情
     * @param uid 用户ID标识
     * @param dateLimit 日期范围
     * @param pageParamRequest 分页参数
     * @Author 零风
     * @Date  2021/12/24
     * @return  分页数据
     */
    PageInfo<UserBrokerageRecord> getFundsMonitorDetail(Integer uid, String dateLimit, PageParamRequest pageParamRequest);

    /**
     * 获取-用户(冻结期/待结算）的总佣金
     * @param uid 用户ID标识
     * @Author 零风
     * @Date  2021/12/24
     * @return  总佣金
     */
    BigDecimal getFreezePrice(Integer uid);

    /**
     * 获取-用户佣金记录列表
     * @param linkIds   关联id集合
     * @param linkType  关联类型
     * @param uid       用户ID标识
     * @param pageParamRequest 分页参数
     * @Author 零风
     * @Date  2021/12/24
     * @return 分页数据
     */
    PageInfo<UserBrokerageRecord> findListByLinkIdsAndLinkTypeAndUid(List<String> linkIds, String linkType, Integer uid, PageParamRequest pageParamRequest);
}