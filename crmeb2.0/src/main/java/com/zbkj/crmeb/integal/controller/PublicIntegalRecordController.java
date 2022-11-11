package com.zbkj.crmeb.integal.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.integal.request.PublicIntegalRecordSearchRequest;
import com.zbkj.crmeb.integal.response.PublicIntegalRecordResponse;
import com.zbkj.crmeb.integal.response.PublicIntegralLibraryResponse;
import com.zbkj.crmeb.integal.service.PublicIntegalRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 公共积分记录-控制类，表示层
 * @author: 零风
 * @CreateDate: 2021/10/18 13:33
 */
@Slf4j
@RestController
@RequestMapping("api/admin/integal")
@Api(tags = "积分 -- 公共积分记录 ")
public class PublicIntegalRecordController {

    @Autowired
    private PublicIntegalRecordService publicIntegalRecordService;

    @ApiOperation(value = "获取-公共积分库")
    @RequestMapping(value = "/getPublicIntegralLibrary", method = RequestMethod.GET)
    @ApiImplicitParam(name = "raId",value = "区域代理表ID标识，为空默认全部",required = false)
    public CommonResult<PublicIntegralLibraryResponse> getPublicIntegralLibrary(@RequestParam("raId") Integer raId){
        return CommonResult.success(publicIntegalRecordService.getPublicIntegralLibrary(0));
    }

    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<PublicIntegalRecordResponse>> getList(@Validated PublicIntegalRecordSearchRequest request, @Validated PageParamRequest pageParamRequest){
        return CommonResult.success(CommonPage.restPage(publicIntegalRecordService.getList(request, pageParamRequest)));
    }

}
