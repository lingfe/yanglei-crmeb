package com.zbkj.crmeb.cloudAccount.service;

import com.zbkj.crmeb.cloudAccount.response.DayStreamDataListResponse;
import com.zbkj.crmeb.cloudAccount.response.DealerBalanceDetailResponse;

import java.util.List;

/**
 * @program: crmeb
 * @description:   云账户-server层
 * @author: 零风
 * @create: 2021-08-18 11:10
 **/
public interface CloudAccountService {

    /**
     * 查询-云账户-日流水记录
     * @param dateDay  日期
     * @return
     */
    DayStreamDataListResponse queryDayStream(String dateDay);

    /**
     * 查询-云账户余额详细
     * @return
     */
    DealerBalanceDetailResponse queryAccounts();

}
