package com.zbkj.crmeb.front.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.front.response.UserMerIdOrderDetailsResponse;
import com.zbkj.crmeb.store.model.Supplier;
import com.zbkj.crmeb.store.request.StoreOrderSearchRequest;
import com.zbkj.crmeb.store.request.StoreOrderSendRequest;
import com.zbkj.crmeb.store.request.StoreProductSearchRequest;
import com.zbkj.crmeb.store.response.StoreOrderDetailResponse;
import com.zbkj.crmeb.store.response.StoreProductResponse;
import com.zbkj.crmeb.store.service.StoreCartService;
import com.zbkj.crmeb.store.service.StoreOrderService;
import com.zbkj.crmeb.store.service.StoreProductService;
import com.zbkj.crmeb.store.service.SupplierService;
import com.zbkj.crmeb.user.response.UserMerIdDataResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 供应商表-前端H5控制类
 * @author: 零风
 * @CreateDate: 2021/12/29 9:57
 */
@Slf4j
@RestController
@RequestMapping("api/front/supplier")
@Api(tags = "供应商")
public class SupplierFrontController {

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private StoreProductService storeProductService;

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private StoreCartService storeCartService;

    @ApiOperation(value = "供应商管理用户-得到绑定的供应商列表")
    @RequestMapping(value = "/merID/getUidSupplierList", method = RequestMethod.GET)
    public CommonResult<List<Supplier>> getUidSupplierList(){
        return CommonResult.success(supplierService.getUidSupplierList());
    }

    @ApiOperation(value = "供应商-绑定de商品列表(商户用户操作)")
    @RequestMapping(value = "/merID/getRegionalAgentProductList", method = RequestMethod.GET)
    public CommonResult<CommonPage<StoreProductResponse>> getRegionalAgentProductList(
            @Validated StoreProductSearchRequest request,
            @Validated PageParamRequest pageParamRequest) {
        if(request.getSupplierId()<=0) return CommonResult.success();
        return CommonResult.success(CommonPage.restPage(storeProductService.getList(request, pageParamRequest, Boolean.FALSE)));
    }

    @ApiOperation(value = "供应商-商品上架")
    @RequestMapping(value = "/merID/putOnShell/{id}", method = RequestMethod.GET)
    @ApiImplicitParam(name = "id",value = "商品表ID标识")
    public CommonResult<String> putOn(@PathVariable Integer id) {
        if (storeProductService.putOnShelf(id)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    @ApiOperation(value = "供应商-商品下架")
    @RequestMapping(value = "/merID/offShell/{id}", method = RequestMethod.GET)
    @ApiImplicitParam(name = "id",value = "商品表ID标识")
    public CommonResult<String> offShell(@PathVariable Integer id) {
        if (storeProductService.offShelf(id)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    @ApiOperation(value = "供应商-商品删除")
    @RequestMapping(value = "/merID/delete/{id}", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "商品ID标识"),
            @ApiImplicitParam(name = "type",value = "类型：recycle——回收站,delete——彻底删除"),
    })
    public CommonResult<String> delete(@RequestBody @PathVariable Integer id,
                                       @RequestParam(value = "type", required = false, defaultValue = "recycle") String type) {
        if (storeProductService.deleteProduct(id, type)) {
            if (type.equals("recycle")) {
                storeCartService.productStatusNotEnable(id);
            } else {
                storeCartService.productDelete(id);
            }
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    @ApiOperation(value = "供应商-相关统计数据")
    @RequestMapping(value = "/merID/getSupplierData", method = RequestMethod.GET)
    @ApiImplicitParam(name = "id",value = "供应商表ID标识")
    public CommonResult<UserMerIdDataResponse> getSupplierData(@RequestParam("id") Integer id){
        return CommonResult.success(supplierService.getSupplierData(id));
    }

    @ApiOperation(value = "供应商-订单详细统计数据")
    @RequestMapping(value = "/merID/getSupplierOrderInfoStatisticsData", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "供应商表ID标识"),
            @ApiImplicitParam(name = "dateType", value = "日期类型(0=今天、1=昨天、2=最近7天、3=本月、4=本年)", required = true) })
    public CommonResult<UserMerIdOrderDetailsResponse> getSupplierOrderInfoStatisticsData(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "dateType") Integer dateType){
        return CommonResult.success(supplierService.getSupplierOrderInfoStatisticsData(id,dateType));
    }

    @ApiOperation(value = "供应商-订单列表(商户用户操作)")
    @RequestMapping(value = "/merID/getMerIdUserOrderList", method = RequestMethod.GET)
    public CommonResult<CommonPage<StoreOrderDetailResponse>> getMerIdUserOrderList(
            @Validated StoreOrderSearchRequest request,
            @Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(storeOrderService.getAdminList(request, pageParamRequest));
    }

    @ApiOperation(value = "供应商-订单发货(商户用户操作)")
    @RequestMapping(value = "/merID/send", method = RequestMethod.POST)
    public CommonResult<Boolean> send(@RequestBody @Validated StoreOrderSendRequest request) {
        if (storeOrderService.send(request)) {
            return CommonResult.success(Boolean.TRUE);
        }
        return CommonResult.failed();
    }

}
