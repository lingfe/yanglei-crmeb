package com.zbkj.crmeb.front.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.front.response.SecKillResponse;
import com.zbkj.crmeb.front.response.SeckillIndexResponse;
import com.zbkj.crmeb.front.response.StoreIntegalShopDetailH5Response;
import com.zbkj.crmeb.front.response.StoreSecKillH5Response;
import com.zbkj.crmeb.marketing.response.StoreIntegalShopResponse;
import com.zbkj.crmeb.marketing.service.StoreIntegalShopService;
import com.zbkj.crmeb.seckill.response.StoreSeckillDetailResponse;
import com.zbkj.crmeb.seckill.service.StoreSeckillService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* 积分商城-用户端-控制类、表示层
* @author: 零风
* @Version: 1.0
* @CreateDate: 2021/7/5 10:58
* @return： IntegalShopController.java
**/
@Slf4j
@RestController
@RequestMapping("api/front/integal/shop")
@Api(tags = "积分 -- 商品")
public class IntegalShopController {

    @Autowired
    StoreIntegalShopService storeIntegalShopService;

    /**
     * 积分兑换商品-详情
     * @param id    商品ID
     * @return
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
    @ApiImplicitParam(name = "id", value = "积分兑换商品ID",dataType = "int", required = true)
    public CommonResult<StoreIntegalShopDetailH5Response> getDetailH5(@PathVariable(value = "id") Integer id){
        StoreIntegalShopDetailH5Response storeIntegalShopDetailH5Response = storeIntegalShopService.getDetailH5(id);
        return CommonResult.success(storeIntegalShopDetailH5Response);
    }

    /**
     * 积分兑换商品-列表
     * @param pageParamRequest 分页参数
     * @return
     */
    @ApiOperation(value = "列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<StoreIntegalShopResponse>> getListH5(@ModelAttribute PageParamRequest pageParamRequest){
        return CommonResult.success(CommonPage.restPage(storeIntegalShopService.getListH5(pageParamRequest)));
    }

    /**
     * 积分商城banner
     * @return
     */
    @ApiOperation(value = "积分商城banner")
    @RequestMapping(value = "/banner", method = RequestMethod.GET)
    public CommonResult<List<HashMap<String, Object>>> getBannerH5(){
        return CommonResult.success(storeIntegalShopService.getBannerH5());
    }

    /**
     * 积分商城导航菜单
     * @return
     */
    @ApiOperation(value = "积分商城导航菜单")
    @RequestMapping(value = "/daohang", method = RequestMethod.GET)
    public CommonResult<List<HashMap<String, Object>>> getDaohangMenuH5(){
        return CommonResult.success(storeIntegalShopService.getDaohangMenuH5());
    }
}
