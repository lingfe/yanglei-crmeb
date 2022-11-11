package com.zbkj.crmeb.cloudAccount.controller;

import com.common.CommonResult;
import com.zbkj.crmeb.cloudAccount.response.DayStreamDataListResponse;
import com.zbkj.crmeb.cloudAccount.response.DealerBalanceDetailResponse;
import com.zbkj.crmeb.cloudAccount.service.CloudAccountService;
import com.zbkj.crmeb.finance.response.BalanceResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: crmeb
 * @description: 云账户-控制类表示层
 * @author: 零风
 * @create: 2021-08-12 11:41
 **/
@Slf4j
@RestController
@RequestMapping("api/admin/cloudAccount")
@Api(tags = "云账户") //配合swagger使用
public class CloudAccountAdminController {

    @Autowired
    private CloudAccountService cloudAccountService;

    /**
     * 云账户余额详细
     * @return
     */
    @ApiOperation(value = "云账户余额详细")
    @RequestMapping(value = "/balance", method = RequestMethod.GET)
    public CommonResult<DealerBalanceDetailResponse> balance(){
        return CommonResult.success(cloudAccountService.queryAccounts());
    }

    /***
     * 查询-云账户-日流水记录
     * @return
     */
    @ApiOperation(value = "查询-云账户-日流水记录")
    @RequestMapping(value = "/queryDayStream", method = RequestMethod.GET)
    @ApiImplicitParam(name = "date", value = "查询日期，格式:yyyy-MM-dd")
    public CommonResult<DayStreamDataListResponse> queryDayStream(@RequestParam(value = "date") String date){
        return CommonResult.success(cloudAccountService.queryDayStream(date));
    }


}
