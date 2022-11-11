package com.zbkj.crmeb.store.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.zbkj.crmeb.store.model.ServiceProvider;
import com.zbkj.crmeb.store.model.ServiceProviderTwolevel;
import com.zbkj.crmeb.store.request.ServiceProviderSearchRequest;
import com.zbkj.crmeb.store.request.ServiceProviderTwolevelSearchRequest;
import com.zbkj.crmeb.store.response.ServiceProviderDataResponse;
import com.zbkj.crmeb.store.service.ServiceProviderService;
import com.zbkj.crmeb.store.service.ServiceProviderTwolevelService;
import com.zbkj.crmeb.user.model.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 服务商表-控制类
 * @author: 零风
 * @CreateDate: 2022/5/9 14:52
 */
@Slf4j
@RestController
@RequestMapping("api/admin/service")
@Api(tags = "服务商")
public class ServiceProviderController {

    @Autowired
    private ServiceProviderService serviceProviderService;

    @Autowired
    private ServiceProviderTwolevelService serviceProviderTwolevelService;

    @ApiOperation(value = "二级商户-所有会员分页列表")
    @RequestMapping(value = "/twelevel/getPageUserListTwelevel", method = RequestMethod.GET)
    @ApiImplicitParam(name = "sptlId",value = "服务商二级商户表ID标识")
    public CommonResult<CommonPage<User>> getPageUserListTwelevel(
            @RequestParam(name = "sptlId",required = false)Integer sptlId,
            @Validated PageParamRequest pageParamRequest){
        return CommonResult.success(CommonPage.restPage(serviceProviderService.getPageUserList(2,sptlId,pageParamRequest)));
    }

    @ApiOperation(value = "二级商户-统计数据")
    @RequestMapping(value = "/twelevel/data", method = RequestMethod.GET)
    @ApiImplicitParam(name = "id",value = "服务商二级商户表ID标识")
    public CommonResult<ServiceProviderDataResponse> dataTwelevel(@RequestParam("id") Integer id){
        return CommonResult.success(serviceProviderTwolevelService.data(id));
    }

    @ApiOperation(value = "二级商户-删除",notes = Constants.DELETE)
    @RequestMapping(value = "/twelevel/{id}", method = RequestMethod.GET)
    public CommonResult<String> deleteTwelevel(@PathVariable Integer id){
        if(serviceProviderTwolevelService.removeById(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    @ApiOperation(value = "二级商户-详细信息")
    @RequestMapping(value = "/twelevel/info", method = RequestMethod.GET)
    @ApiImplicitParam(name = "id",value = "服务商二级商户表ID标识",required = true)
    public CommonResult<ServiceProviderTwolevel> infoTwelevel(@RequestParam Integer id){
        return CommonResult.success(serviceProviderTwolevelService.getById(id));
    }

    @ApiOperation(value = "二级商户-分页列表")
    @RequestMapping(value = "/twelevel/getPageList", method = RequestMethod.GET)
    public CommonResult<CommonPage<ServiceProviderTwolevel>> getPageListTwelevel(
            @Validated ServiceProviderTwolevelSearchRequest request,
            @Validated PageParamRequest pageParamRequest){
        return CommonResult.success(CommonPage.restPage(serviceProviderTwolevelService.getPageList(request, pageParamRequest)));
    }

    @ApiOperation(value = "二级商户-保存或更新",notes = Constants.UPDATE)
    @RequestMapping(value = "/twelevel/save", method = RequestMethod.POST)
    public CommonResult<String> saveTwelevel(@RequestBody @Validated ServiceProviderTwolevel serviceProviderTwolevel){
        if(serviceProviderTwolevelService.saveServiceProviderTwolevel(serviceProviderTwolevel)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    @ApiOperation(value = "服务商-删除二级商户",notes = Constants.DELETE)
    @RequestMapping(value = "/provider/deleteTwelevelProvider/{id}", method = RequestMethod.GET)
    public CommonResult<String> deleteTwelevelProvider(@PathVariable Integer id){
        if(serviceProviderService.deleteTwelevelProvider(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    @ApiOperation(value = "服务商-保存或更新二级商户",notes = Constants.UPDATE)
    @RequestMapping(value = "/provider/saveOrUpdateTwolevel", method = RequestMethod.POST)
    public CommonResult<String> saveOrUpdateTwolevel(@RequestBody @Validated ServiceProviderTwolevel serviceProviderTwolevel){
        if(serviceProviderService.saveOrUpdateTwolevel(serviceProviderTwolevel)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    @ApiOperation(value = "服务商-的二级商户分页列表")
    @RequestMapping(value = "/provider/getPageTwolevelList", method = RequestMethod.GET)
    public CommonResult<CommonPage<ServiceProviderTwolevel>> getPageTwolevelList(
            @Validated ServiceProviderTwolevelSearchRequest request,
            @Validated PageParamRequest pageParamRequest){
        return CommonResult.success(CommonPage.restPage(serviceProviderService.getPageTwolevelList(request, pageParamRequest)));
    }

    @ApiOperation(value = "服务商-统计数据")
    @RequestMapping(value = "/provider/data", method = RequestMethod.GET)
    @ApiImplicitParam(name = "id",value = "服务商表ID标识")
    public CommonResult<ServiceProviderDataResponse> data(@RequestParam(name = "id",required = false) Integer id){
        return CommonResult.success(serviceProviderService.data(id));
    }

    @ApiOperation(value = "服务商-所有会员分页列表")
    @RequestMapping(value = "/provider/getPageUserList", method = RequestMethod.GET)
    @ApiImplicitParam(name = "spId",value = "服务商表ID标识")
    public CommonResult<CommonPage<User>> getPageUserList(
            @RequestParam(name = "spId",required = false)Integer spId,
            @Validated PageParamRequest pageParamRequest){
        return CommonResult.success(CommonPage.restPage(serviceProviderService.getPageUserList(1,spId,pageParamRequest)));
    }

    @ApiOperation(value = "服务商-删除",notes = Constants.DELETE)
    @RequestMapping(value = "/provider/delete/{id}", method = RequestMethod.GET)
    public CommonResult<String> delete(@PathVariable Integer id){
        if(serviceProviderService.removeById(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    @ApiOperation(value = "服务商-详细信息")
    @RequestMapping(value = "/provider/info", method = RequestMethod.GET)
    @ApiImplicitParam(name = "id",value = "服务商表ID标识",required = true)
    public CommonResult<ServiceProvider> info(@RequestParam("id") Integer id){
        return CommonResult.success(serviceProviderService.getById(id));
    }

    @ApiOperation(value = "服务商-分页列表")
    @RequestMapping(value = "/provider/getPageList", method = RequestMethod.GET)
    public CommonResult<CommonPage<ServiceProvider>> getPageList(
            @Validated ServiceProviderSearchRequest request,
            @Validated PageParamRequest pageParamRequest){
        return CommonResult.success(CommonPage.restPage(serviceProviderService.getPageList(request, pageParamRequest)));
    }

    @ApiOperation(value = "服务商-保存或更新",notes = Constants.UPDATE)
    @RequestMapping(value = "/provider/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated ServiceProvider serviceProvider){
        if(serviceProviderService.saveServiceProvider(serviceProvider)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }
}
