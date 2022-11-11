package com.zbkj.crmeb.user.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.user.request.AdminIntegralSearchRequest;
import com.zbkj.crmeb.user.request.UserIntegralExchangeRecordSearchRequest;
import com.zbkj.crmeb.user.response.UserIntegralExchangeRecordResponse;
import com.zbkj.crmeb.user.response.UserIntegralRecordResponse;
import com.zbkj.crmeb.user.service.UserIntegralExchangeRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: crmeb
 * @description: 用户积分兑换记录-controller控制、表示层
 * @author: 零风
 * @create: 2021-07-07 15:20
 **/
@Slf4j
@RestController
@RequestMapping("api/admin/user/integral/exchange/record")
@Api(tags = "用户 -- 积分兑换记录")
public class UserIntegralExchangeRecordController {

    @Autowired
    private UserIntegralExchangeRecordService userIntegralExchangeRecordService;

    /**
     * 积分兑换记录分页列表
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     */
    @ApiOperation(value = "积分兑换记录分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public CommonResult<CommonPage<UserIntegralExchangeRecordResponse>> findAdminList(
            @RequestBody @Validated UserIntegralExchangeRecordSearchRequest request,
            @Validated PageParamRequest pageParamRequest){
        CommonPage<UserIntegralExchangeRecordResponse> restPage = CommonPage.restPage(userIntegralExchangeRecordService.findAdminList(request, pageParamRequest));
        return CommonResult.success(restPage);
    }

}
