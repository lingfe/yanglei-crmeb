package com.zbkj.crmeb.front.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.exception.CrmebException;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.front.response.UserMerIdOrderDetailsResponse;
import com.zbkj.crmeb.integal.response.PublicIntegralLibraryResponse;
import com.zbkj.crmeb.integal.service.PublicIntegalRecordService;
import com.zbkj.crmeb.regionalAgency.model.RegionalAgency;
import com.zbkj.crmeb.regionalAgency.response.RegionalAgencyPIntegalResponse;
import com.zbkj.crmeb.regionalAgency.service.RegionalAgencyService;
import com.zbkj.crmeb.retailer.model.Retailer;
import com.zbkj.crmeb.retailer.model.RetailerPra;
import com.zbkj.crmeb.retailer.request.RetailerRequest;
import com.zbkj.crmeb.retailer.response.RetailerBillResponse;
import com.zbkj.crmeb.retailer.response.RetailerBillSettlementStatisticsDataResponse;
import com.zbkj.crmeb.retailer.response.RetailerPraResponse;
import com.zbkj.crmeb.retailer.response.RetailerResponse;
import com.zbkj.crmeb.retailer.service.RetailerBillService;
import com.zbkj.crmeb.retailer.service.RetailerPraService;
import com.zbkj.crmeb.retailer.service.RetailerService;
import com.zbkj.crmeb.store.model.StoreProduct;
import com.zbkj.crmeb.store.model.StoreProductRA;
import com.zbkj.crmeb.store.request.StoreOrderRefundRequest;
import com.zbkj.crmeb.store.request.StoreOrderSearchRequest;
import com.zbkj.crmeb.store.request.StoreOrderSendRequest;
import com.zbkj.crmeb.store.request.StoreProductSearchRequest;
import com.zbkj.crmeb.store.response.ProductOrderDataResponse;
import com.zbkj.crmeb.store.response.StoreOrderDetailResponse;
import com.zbkj.crmeb.store.response.StoreProductRAResponse;
import com.zbkj.crmeb.store.response.StoreProductResponse;
import com.zbkj.crmeb.store.service.StoreOrderService;
import com.zbkj.crmeb.store.service.StoreProductRAService;
import com.zbkj.crmeb.store.service.StoreProductService;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.response.UserMerIdDataResponse;
import com.zbkj.crmeb.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户-(商户用户/代理商用户/区域代理用户）-控制类、表示层
 * @author: 零风
 * @create: 2021-09-01 14:23
 **/
@Slf4j
@RestController("FrontUserMerIdController")
@RequestMapping("api/front/userMerId")
@Api(tags = "用户 -- 商户用户 ")
public class UserMerIdController {

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private UserService userService;

    @Autowired
    private StoreProductService storeProductService;

    @Autowired
    private PublicIntegalRecordService publicIntegalRecordService;

    @Autowired
    private RegionalAgencyService regionalAgencyService;

    @Autowired
    private RetailerService retailerService;

    @Autowired
    private StoreProductRAService storeProductRAService;

    @Autowired
    private RetailerPraService retailerPraService;

    @Autowired
    private RetailerBillService retailerBillService;

    @ApiOperation(value = "区域代理-查看零售商账单-结算统计数据")
    @RequestMapping(value = "/merID/settlementStatisticsData", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "retailerId",value = "零售商表ID标识"),
            @ApiImplicitParam(name = "productId",value = "商品表ID标识,如果不传就根据零售商id查询")
    })
    public CommonResult<RetailerBillSettlementStatisticsDataResponse> settlementStatisticsData(
            @RequestParam(name = "retailerId") Integer retailerId,
            @RequestParam(name = "productId") Integer productId){
        return CommonResult.success(retailerBillService.settlementStatisticsData(retailerId,productId));
    }

    @ApiOperation(value = "区域代理-查看零售商账单-一键结算")
    @RequestMapping(value = "/merID/allSettlement", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "retailerId",value = "零售商表ID标识"),
            @ApiImplicitParam(name = "productId",value = "商品表ID标识,如果不传就根据零售商id查询")
    })
    public CommonResult<Boolean> allSettlement(@RequestParam(name = "retailerId") Integer retailerId){
        return CommonResult.success(retailerBillService.allSettlement(retailerId));
    }

    @ApiOperation(value = "区域代理-查看零售商账单-点击结算")
    @RequestMapping(value = "/merID/clickSettlement", method = RequestMethod.GET)
    @ApiImplicitParam(name = "retailerBillId",value = "零售商账单表ID标识")
    public CommonResult<Boolean> clickSettlement(@RequestParam(name = "retailerBillId") Integer retailerBillId){
        return CommonResult.success(retailerBillService.clickSettlement(retailerBillId));
    }

    @ApiOperation(value = "区域代理-获取-零售商账单列表")
    @RequestMapping(value = "/merID/retailer/seeRetailerBillList", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "retailerId",value = "零售商表ID标识"),
            @ApiImplicitParam(name = "productId",value = "商品表ID标识,如果不传就根据零售商id查询",required = false)
    })
    public CommonResult<PageInfo<RetailerBillResponse>> seeRetailerBillList(
            @RequestParam(name = "retailerId") Integer retailerId,
            @RequestParam(name = "productId") Integer productId,
            PageParamRequest pageParamRequest){
        return CommonResult.success(retailerBillService.getRetailerBillList(retailerId,productId,pageParamRequest));
    }

    @ApiOperation(value = "区域代理-获取-零售商代理产品订单数据统计")
    @RequestMapping(value = "/merID/getPraOrderData", method = RequestMethod.GET)
    @ApiImplicitParam(name = "retailerId",value = "零售商表ID标识")
    public CommonResult<List<ProductOrderDataResponse>> getPraOrderData(@RequestParam(name = "retailerId") Integer retailerId){
        return CommonResult.success(retailerService.getPraOrderData(retailerId));
    }

    @ApiOperation(value = "区域代理-获取-零售商代理产品列表")
    @RequestMapping(value = "/merID/getRetailerProductList", method = RequestMethod.GET)
    @ApiImplicitParam(name = "retailerId",value = "零售商表ID标识")
    public CommonResult<List<RetailerPraResponse>> getRetailerProductList(@RequestParam(name = "retailerId") Integer retailerId){
        return CommonResult.success(retailerPraService.getRetailerProductList(retailerId,2,null));
    }

    @ApiOperation(value = "区域代理-修改-零售商信息")
    @RequestMapping(value = "/merID/updateRetailer", method = RequestMethod.GET)
    public CommonResult<Boolean> updateRetailer(@RequestBody @Validated RetailerRequest request){
        return CommonResult.success(retailerService.update(request));
    }

    @ApiOperation(value = "区域代理-获取-零售商详情")
    @RequestMapping(value = "/merID/getInfo", method = RequestMethod.GET)
    @ApiImplicitParam(name = "id",value = "零售商表ID标识")
    public CommonResult<RetailerResponse> getInfo(@RequestParam(name = "id") Integer id){
        return CommonResult.success(retailerService.getInfo(id));
    }

    @ApiOperation(value = "区域代理-删除-零售商产品代理信息")
    @RequestMapping(value = "/ra/deleteWhereId", method = RequestMethod.GET)
    @ApiImplicitParam(name = "id",value = "零售商产品代理表ID标识")
    public CommonResult<String> raDeleteWhereId(@RequestParam("id") Integer id) {
        if (retailerPraService.removeById(id)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    @ApiOperation(value = "区域代理-获取-零售商列表")
    @RequestMapping(value = "/merID/getInfoList", method = RequestMethod.GET)
    @ApiImplicitParam(name = "raId",value = "区域代理id标识")
    public CommonResult<List<RetailerResponse>> getInfoList(@RequestParam(name = "raId") Integer raId){
        return CommonResult.success(retailerService.getInfoList(raId));
    }

    @ApiOperation(value = "区域代理-添加-零售商产品代理信息")
    @RequestMapping(value = "/merID/addRetailerPra", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "retailerId",value = "零售商表id标识"),
            @ApiImplicitParam(name = "praIds",value = "产品代理表ID标识,多个用逗号隔开")
    })
    public CommonResult<Boolean> addRetailerPra(@RequestParam("retailerId") Integer retailerId,
                                                @RequestParam("praIds") String praIds){
        //验证-区域代理IDs非空
        List<Integer> praIdList=new ArrayList<>();
        if(StrUtil.isNotBlank(praIds)){
            String[] strings= praIds.split(",");
            praIdList = Arrays.stream(strings).mapToInt(Integer::valueOf).boxed().collect(Collectors.toList());
        }

        //循环处理
        List<RetailerPra> retailerPraList=new ArrayList<>();
        for (Integer id:praIdList) {
            //实例化对象
            RetailerPra retailerPra=new RetailerPra();
            retailerPra.setPraId(id);
            retailerPra.setRetailerId(retailerId);
            retailerPra.setIsSale(Boolean.TRUE);

            //验证重复
            LambdaQueryWrapper<RetailerPra> lambdaQueryWrapper=new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(RetailerPra::getPraId,id);
            lambdaQueryWrapper.eq(RetailerPra::getRetailerId,retailerId);
            List<RetailerPra> list = retailerPraService.list(lambdaQueryWrapper);
            if(list == null || list.size() <= 0){
                //添加到-list集合
                retailerPraList.add(retailerPra);
            }else{
                continue;//跳过
            }
        }

        //验证list
        if(retailerPraList == null || retailerPraList.size() <= 0)throw new CrmebException("已存在或参数不能为空！");

        //执行并返回结果
        return CommonResult.success(retailerPraService.saveBatch(retailerPraList));
    }

    @ApiOperation(value = "区域代理-获取-区域代理代理的产品列表")
    @RequestMapping(value = "/merID/getRaIdProductList", method = RequestMethod.GET)
    @ApiImplicitParam(name = "raId",value = "区域代理ID标识")
    public CommonResult<List<StoreProductRAResponse>> getRaIdProductList(@RequestParam(name = "raId")Integer raId){
        //得到-产品代理表信息
        List<StoreProductRA> storeProductRAList = storeProductRAService.getWhereRaIDList(raId);
        if(storeProductRAList == null || storeProductRAList.size()<=0)CommonResult.success();

        //循环处理
        List<StoreProductRAResponse> storeProductRAResponseList=new ArrayList<>();
        for (StoreProductRA spra:storeProductRAList) {
            //实例化-产品代理表响应类对象
            StoreProductRAResponse response=new StoreProductRAResponse();
            response.setId(spra.getId());
            response.setRaId(spra.getRaId());

            //得到产品信息
            StoreProduct storeProduct = storeProductService.getById(spra.getProductId());
            if(storeProduct == null)continue;
            response.setStoreProduct(storeProduct);

            //添加到-list集合
            storeProductRAResponseList.add(response);
        }

        //返回
        return CommonResult.success(storeProductRAResponseList);
    }

    @ApiOperation(value = "区域代理-获取-区域代理各个区域以及(公共积分库)")
    @RequestMapping(value = "/merID/getPublicIntegralLibraryList", method = RequestMethod.GET)
    public CommonResult<List<RegionalAgencyPIntegalResponse>> getPublicIntegralLibraryList(){
        //得到-当前登录用户
        User user=userService.getInfoException();
        return CommonResult.success(this.getRAPIR(user.getUid()));
    }

    @ApiOperation(value = "区域代理-新增-零售商")
    @RequestMapping(value = "/merID/retailer/add", method = RequestMethod.POST)
    public CommonResult<Retailer> retailerAdd(@RequestBody @Validated RetailerRequest request){
        return CommonResult.success(retailerService.add(request));
    }

    @ApiOperation(value = "区域代理管理用户-绑定的区域代理列表")
    @RequestMapping(value = "/merID/getRegionalAgencyList", method = RequestMethod.GET)
    public CommonResult<List<RegionalAgency>> getRegionalAgencyList(){
        //得到-当前登录用户
        User user=userService.getInfoException();
        List<RegionalAgency> regionalAgencyList = regionalAgencyService.getWhereUserID(user.getUid());
        return CommonResult.success(regionalAgencyList);
    }

    /**
     * 得到-区域代理信息以及该区域的公共积分库
     * @param uid
     * @return
     */
    public List<RegionalAgencyPIntegalResponse> getRAPIR(Integer uid){
        //得到-区域代理信息
        List<RegionalAgency> regionalAgency = regionalAgencyService.getWhereUserID(uid);
        if(regionalAgency == null)return null;

        //循环处理
        List<RegionalAgencyPIntegalResponse> list=new ArrayList<>();
        for (RegionalAgency r:regionalAgency) {
            RegionalAgencyPIntegalResponse response=new RegionalAgencyPIntegalResponse();
            PublicIntegralLibraryResponse pResponse = publicIntegalRecordService.getPublicIntegralLibrary(r.getId());
            response.setRegionalAgency(r);
            response.setPilr(pResponse);

            //添加到list
            list.add(response);
        }

        //返回
        return list;
    }

    @ApiOperation(value = "区域代理-获取-公共积分库(区域代理)")
    @RequestMapping(value = "/merID/getPublicIntegralLibrary", method = RequestMethod.GET)
    public CommonResult<PublicIntegralLibraryResponse> getPublicIntegralLibrary(){
        //得到-当前登录用户
        User user=userService.getInfoException();
        if(user == null){
            return CommonResult.success(PublicIntegralLibraryResponse.builder().build());
        }

        //得到数据
        List<RegionalAgencyPIntegalResponse> list=this.getRAPIR(user.getUid());
        if(list == null) return CommonResult.success(PublicIntegralLibraryResponse.builder().build());

        //实例化-响应对象
        PublicIntegralLibraryResponse response=PublicIntegralLibraryResponse.builder()
                .otherIntegral(BigDecimal.ZERO)
                .alreadyDistributionIntegral(BigDecimal.ZERO)
                .distributableIntegral(BigDecimal.ZERO)
                .freezingIntegral(BigDecimal.ZERO)
                .totalIntegral(BigDecimal.ZERO)
                .waitDistributionIntegral(BigDecimal.ZERO)
                .build();

        //循环处理
        BigDecimal t=BigDecimal.ZERO;
        for (RegionalAgencyPIntegalResponse r:list) {
            //累计-已分配积分
            t=r.getPilr().getAlreadyDistributionIntegral();
            t=t.add(response.getAlreadyDistributionIntegral());
            response.setAlreadyDistributionIntegral(t);

            //累计-可分配积分
            t=r.getPilr().getDistributableIntegral();
            t=t.add(response.getDistributableIntegral());
            response.setDistributableIntegral(t);

            //累计-冻结积分
            t=r.getPilr().getFreezingIntegral();;
            t=t.add(response.getFreezingIntegral());
            response.setFreezingIntegral(t);

            //累计-其他积分
            t=r.getPilr().getOtherIntegral();
            t=t.add(response.getOtherIntegral());
            response.setOtherIntegral(t);

            //累计-总积分
            t=r.getPilr().getTotalIntegral();
            t=t.add(response.getTotalIntegral());
            response.setTotalIntegral(t);

        }

        //返回-该区域代理公共积分库
        return CommonResult.success(response);
    }

    @ApiOperation(value = "区域代理-得到-区域代理批发商品列表(商户用户操作)")
    @RequestMapping(value = "/merID/getRegionalAgentProductList", method = RequestMethod.GET)
    public CommonResult<CommonPage<StoreProductResponse>> getRegionalAgentProductList(
            @Validated StoreProductSearchRequest request,
            @Validated PageParamRequest pageParamRequest) {
        request.setIsRegionalAgent(Boolean.TRUE);
        request.setType(1);
        return CommonResult.success(CommonPage.restPage(storeProductService.getList(request, pageParamRequest, Boolean.FALSE)));
    }

    @ApiOperation(value = "区域代理-得到-订单列表(商户用户操作)")
    @RequestMapping(value = "/merID/getMerIdUserOrderList", method = RequestMethod.GET)
    public CommonResult<CommonPage<StoreOrderDetailResponse>> getMerIdUserOrderList(
            @Validated StoreOrderSearchRequest request,
            @Validated PageParamRequest pageParamRequest) {
        //验证条件-商户ID
        if (request.getMerId() == null || request.getMerId() <= 0) throw new CrmebException("请选择一个区域代理！");
        return CommonResult.success(storeOrderService.getAdminList(request, pageParamRequest));
    }

    @ApiOperation(value = "区域代理-发货(商户用户操作)")
    @RequestMapping(value = "/merID/send", method = RequestMethod.POST)
    public CommonResult<Boolean> send(@RequestBody @Validated StoreOrderSendRequest request) {
        if (storeOrderService.send(request)) {
            return CommonResult.success(Boolean.TRUE);
        }
        return CommonResult.failed();
    }

    @ApiOperation(value = "区域代理-得到-相关统计数据")
    @RequestMapping(value = "/getUserMerIdData", method = RequestMethod.GET)
    @ApiImplicitParam(name = "raId",value = "区域代理表ID标识")
    public CommonResult<UserMerIdDataResponse> getUserMerIdData(@RequestParam("raId") Integer raId){
        return CommonResult.success(userService.getUserMerIdData(raId));
    }

    @ApiOperation(value = "区域代理-得到-订单详细统计数据")
    @RequestMapping(value = "/getUserMerIdOrderInfoStatisticsData", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "raId",value = "区域代理表ID标识"),
            @ApiImplicitParam(name = "dateType", value = "日期类型(0=今天、1=昨天、2=最近7天、3=本月、4=本年)", required = true) })
    public CommonResult<UserMerIdOrderDetailsResponse> getUserMerIdOrderInfoStatisticsData(
            @RequestParam(name = "raId") Integer raId,
            @RequestParam(name = "dateType") Integer dateType){
        return CommonResult.success(userService.getUserMerIdOrderInfoStatisticsData(raId,dateType));
    }

    @ApiOperation(value = "商户用户-退款")
    @RequestMapping(value = "/refund", method = RequestMethod.GET)
    public CommonResult<Boolean> send(@Validated StoreOrderRefundRequest request) {
        return CommonResult.success(storeOrderService.refund(request));
    }

}
