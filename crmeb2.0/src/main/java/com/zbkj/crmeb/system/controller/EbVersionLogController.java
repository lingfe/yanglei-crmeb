package com.zbkj.crmeb.system.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.log.model.EbVersionLog;
import com.zbkj.crmeb.log.request.EbVersionLogSearchRequest;
import com.zbkj.crmeb.log.service.EbVersionLogService;
import com.zbkj.crmeb.system.request.SystemCitySearchRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 版本日志表
 * @author: 零风
 * @CreateDate: 2021/9/27 16:28
 */
@Slf4j
@RestController
@RequestMapping("api/admin/versionLog")
@Api(tags = "设置 -- 版本日志")
public class EbVersionLogController {

    @Autowired
    private EbVersionLogService ebVersionLogService;

    @ApiOperation(value = "同步日志")
    @RequestMapping(value = "/synchronizationLog", method = RequestMethod.GET)
    public CommonResult<Boolean>  synchronizationLog(){
        return CommonResult.success(ebVersionLogService.synchronizationLog());
    }

    @ApiOperation(value = "版本日志-分页列表")
    @RequestMapping(value = "/getPageList", method = RequestMethod.GET)
    public CommonResult<CommonPage<EbVersionLog>> getList(EbVersionLogSearchRequest request, PageParamRequest pageParamRequest){
        return CommonResult.success(ebVersionLogService.getPageList(request,pageParamRequest));
    }

}
