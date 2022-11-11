package com.zbkj.crmeb.front.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.front.response.UserMerIdOrderDetailsResponse;
import com.zbkj.crmeb.front.response.UserSpreadBannerResponse;
import com.zbkj.crmeb.retailer.model.Retailer;
import com.zbkj.crmeb.retailer.request.RetailerSearchRequest;
import com.zbkj.crmeb.retailer.response.RetailerPraResponse;
import com.zbkj.crmeb.retailer.service.RetailerPraService;
import com.zbkj.crmeb.retailer.service.RetailerService;
import com.zbkj.crmeb.store.request.StoreOrderSearchRequest;
import com.zbkj.crmeb.store.response.StoreOrderDetailResponse;
import com.zbkj.crmeb.store.service.StoreOrderService;
import com.zbkj.crmeb.user.response.UserMerIdDataResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 零售商-(前端)控制类、表示层
 * @author: 零风
 * @CreateDate: 2021/11/22 15:04
 */
@Slf4j
@RestController
@RequestMapping("api/front/retailer")
@Api(tags = "零售商")
public class RetailerFrontController {

    @Autowired
    private RetailerService retailerService;

    @Autowired
    private RetailerPraService retailerPraService;

    @Autowired
    private StoreOrderService storeOrderService;

    @ApiOperation(value = "零售商-推广海报图")
    @RequestMapping(value = "/admin/spreadBanner", method = RequestMethod.GET)
    public CommonResult<List<UserSpreadBannerResponse>>  getSpreadBannerList(){
        return CommonResult.success(retailerService.getSpreadBannerList());
    }

    @ApiOperation(value = "零售商代理产品-是否销售(上下架)")
    @RequestMapping(value = "/admin/isSale", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "零售商产品代理表ID标识"),
            @ApiImplicitParam(name = "isSale",value = "是否销售，false=下架，true=上架")
    })
    public CommonResult<Boolean> isSale(@RequestParam("id")Integer id,
                                        @RequestParam("isSale")Boolean isSale){
        if(retailerPraService.isSale(id,isSale)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    @ApiOperation(value = "零售商代理产品-分页列表(管理)")
    @RequestMapping(value = "/admin/retailerProductList", method = RequestMethod.GET)
    public CommonResult<CommonPage<RetailerPraResponse>> retailerProductList(
            @Validated RetailerSearchRequest request,
            @Validated PageParamRequest pageParamRequest){
        return CommonResult.success(CommonPage.restPage(retailerService.retailerProductList(request,pageParamRequest)));
    }

    @ApiOperation(value = "零售商代理产品-产品列表(用户查看)")
    @RequestMapping(value = "/user/getRetailerProductList", method = RequestMethod.GET)
    @ApiImplicitParam(name = "retailerId",value = "零售商表ID标识")
    public CommonResult<List<RetailerPraResponse>> getRetailerProductList(@RequestParam(name = "retailerId") Integer retailerId){
        return CommonResult.success(retailerPraService.getRetailerProductList(retailerId,1,true));
    }

    @ApiOperation(value = "零售商-相关统计数据")
    @RequestMapping(value = "/getData", method = RequestMethod.GET)
    @ApiImplicitParam(name = "retailerId",value = "零售商表ID标识")
    public CommonResult<UserMerIdDataResponse> getUserMerIdData(@RequestParam("retailerId") Integer retailerId){
        return CommonResult.success(retailerService.getData(retailerId));
    }

    @ApiOperation(value = "零售商-管理用户-得到绑定的零售商列表")
    @RequestMapping(value = "/getWhereUserIDList", method = RequestMethod.GET)
    public CommonResult<List<Retailer>> getWhereUserIDList(){
        return CommonResult.success(retailerService.getWhereUserIDList());
    }

    @ApiOperation(value = "零售商-订单详细统计数据")
    @RequestMapping(value = "/getOrderInfoData", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "retailerId",value = "零售商表ID标识"),
            @ApiImplicitParam(name = "dateType", value = "日期类型(0=今天、1=昨天、2=最近7天、3=本月、4=本年)", required = true)
    })
    public CommonResult<UserMerIdOrderDetailsResponse> getOrderInfoData(
            @RequestParam("retailerId") Integer retailerId,
            @RequestParam(name = "dateType") Integer dateType){
        return CommonResult.success(retailerService.getOrderInfoData(retailerId,dateType));
    }

    @ApiOperation(value = "零售商-订单列表")
    @RequestMapping(value = "/merID/getRetailerOrderList", method = RequestMethod.GET)
    @ApiImplicitParam(name = "retailerId",value = "零售商表ID标识")
    public CommonResult<CommonPage<StoreOrderDetailResponse>> getRetailerOrderList(
            @RequestParam("retailerId") Integer retailerId,
            @Validated StoreOrderSearchRequest request,
            @Validated PageParamRequest pageParamRequest) {
        //验证-零售商信息
        Retailer retailer= retailerService.getById(retailerId);
        if(retailer == null)  return CommonResult.success();

        //设置参数得到订单列表
        request.setMerId(retailer.getId());
        request.setType(3);
        return CommonResult.success(storeOrderService.getAdminList(request, pageParamRequest));
    }

}
