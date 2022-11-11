package com.zbkj.crmeb.store.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.zbkj.crmeb.store.model.StoreProduct;
import com.zbkj.crmeb.store.model.Supplier;
import com.zbkj.crmeb.store.request.SupplierRequest;
import com.zbkj.crmeb.store.request.SupplierSearchRequest;
import com.zbkj.crmeb.store.service.SupplierService;
import com.zbkj.crmeb.system.service.SystemConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 供应商表-后端管理、控制层表示类
 * @author: 零风
 * @CreateDate: 2021/12/28 11:28
 */
@Slf4j
@RestController
@RequestMapping("api/admin/supplier")
@Api(tags = "供应商") //配合swagger使用
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private SystemConfigService systemConfigService;

    @ApiOperation(value = "供应商-绑定的商品列表")
    @RequestMapping(value = "/getSupplierProductList", method = RequestMethod.POST)
    @ApiImplicitParam(name = "id",value = "供应商表ID标识")
    public CommonResult<CommonPage<StoreProduct>> getSupplierProductList(@RequestParam("id") Integer id, PageParamRequest pageParamRequest){
        return CommonResult.success(CommonPage.restPage(supplierService.getSupplierProductList(id,pageParamRequest)));
    }

    @ApiOperation(value = "供应商-得到银行卡开户行信息")
    @RequestMapping(value = "/getBank", method = RequestMethod.GET)
    public CommonResult<List<String>> getBank(){
        String bankValue =  systemConfigService.getValueByKey(Constants.CONFIG_KEY_STORE_BROKERAGE_USER_EXTRACT_BANK);
        return CommonResult.success(Arrays.stream(bankValue.split("\n")).collect(Collectors.toList()));
    }

    @ApiOperation(value = "供应商-分页列表")
    @RequestMapping(value = "/getPageList", method = RequestMethod.GET)
    public CommonResult<CommonPage<Supplier>> getPageList(
            @Validated SupplierSearchRequest request,
            @Validated PageParamRequest pageParamRequest){
        return CommonResult.success(CommonPage.restPage(supplierService.getPageList(request, pageParamRequest)));
    }

    @ApiOperation(value = "供应商-新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated SupplierRequest request){
        if(supplierService.save(request)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    @ApiOperation(value = "供应商-修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated SupplierRequest request){
        if(supplierService.update(request)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    @ApiOperation(value = "供应商-删除(根据id标识删除)")
    @RequestMapping(value = "/deleteByid", method = RequestMethod.POST)
    @ApiImplicitParam(name = "id",value = "供应商表ID标识")
    public CommonResult<String> deleteByid(@RequestParam("id") Integer id){
        if(supplierService.removeById(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    @ApiOperation(value = "供应商-详情")
    @RequestMapping(value = "/info", method = RequestMethod.POST)
    @ApiImplicitParam(name = "id",value = "供应商表ID标识")
    public CommonResult<Supplier> info(@RequestParam("id") Integer id){
        return CommonResult.success(supplierService.getById(id));
    }

    @ApiOperation(value = "供应商-审核")
    @RequestMapping(value = "/toExamine", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "供应商表ID标识"),
            @ApiImplicitParam(name = "status",value = "状态：1=审核通过，2=审核不通过")
    })
    public CommonResult<String> toExamine(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "status") Integer status){
        if(supplierService.toExamine(id,status)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

}
