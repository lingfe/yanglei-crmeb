package com.zbkj.crmeb.regionalAgency.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.regionalAgency.model.RegionalAgency;
import com.zbkj.crmeb.regionalAgency.request.RegionalAgencySearchRequest;
import com.zbkj.crmeb.regionalAgency.response.RegionalAgencyResponse;
import com.zbkj.crmeb.regionalAgency.service.RegionalAgencyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 区域代理表-控制类、表示层
 * @author: 零风
 * @CreateDate: 2021/11/6 10:32
 */
@Slf4j
@RestController
@RequestMapping("api/admin/regionalAgency")
@Api(tags = "区域代理 ")
public class RegionalAgencyController {

    @Autowired
    private RegionalAgencyService regionalAgencyService;

    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<RegionalAgencyResponse>> getList(@Validated RegionalAgencySearchRequest request, @Validated PageParamRequest pageParamRequest){
        return CommonResult.success(CommonPage.restPage(regionalAgencyService.getList(request, pageParamRequest)));
    }

    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated RegionalAgency regionalAgency){
        regionalAgency.setDistributableIntegral(BigDecimal.ZERO);
        if(regionalAgencyService.save(regionalAgency)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    @ApiOperation(value = "详情")
    @RequestMapping(value = "/getInfoId/{id}", method = RequestMethod.GET)
    @ApiImplicitParam(name = "id", value="区域代理表ID标识")
    public CommonResult<Object> getInfoId(@Validated @PathVariable(name = "id") Integer id){
       return CommonResult.success(regionalAgencyService.getById(id));
    }

    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated RegionalAgency regionalAgency){
        if(regionalAgencyService.updateById(regionalAgency)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    @ApiImplicitParam(name = "id", value="区域代理表ID标识")
    public CommonResult<String> delete(@PathVariable Integer id){
        if(regionalAgencyService.removeById(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

}
