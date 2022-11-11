package com.zbkj.crmeb.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.user.model.UserIntegralExchangeRecord;
import com.zbkj.crmeb.user.request.UserIntegralExchangeRecordSearchRequest;
import com.zbkj.crmeb.user.response.UserIntegralExchangeRecordResponse;
import com.zbkj.crmeb.user.response.UserIntegralRecordResponse;

import java.util.List;

/**
* 用户积分兑换记录表-servicec层接口
* @author: 零风
* @Version: 1.0
* @CreateDate: 2021/7/7 15:14
* @return： UserIntegralExchangeRecordService.java
**/
public interface UserIntegralExchangeRecordService extends IService<UserIntegralExchangeRecord> {

    /**
     * 分页获取积分兑换记录列表
     * @param request
     * @param pageParamRequest
     * @return
     */
    PageInfo<UserIntegralExchangeRecordResponse> findAdminList(UserIntegralExchangeRecordSearchRequest request, PageParamRequest pageParamRequest);

}
