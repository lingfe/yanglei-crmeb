package com.zbkj.crmeb.front.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.marketing.model.StoreWear;
import com.zbkj.crmeb.marketing.request.StoreWearSearchRequest;
import com.zbkj.crmeb.marketing.response.StoreWearResponse;
import com.zbkj.crmeb.marketing.service.StoreWearService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 穿搭-用户端控制类
 * @author: 零风
 * @CreateDate: 2021/10/9 14:01
 */
@Slf4j
@RestController("StoreWearFrontController")
@RequestMapping("api/front/wear")
@Api(tags = "营销 -- 穿搭")
public class StoreWearFrontController {

    @Autowired
    private StoreWearService storeWearService;


    @ApiOperation(value = "获取-穿搭列表-用户h5用户端")
    @RequestMapping(value = "/index/wear", method = RequestMethod.GET)
    public CommonResult<CommonPage<StoreWear>>  getList(@Validated StoreWearSearchRequest request, @Validated PageParamRequest pageParamRequest){
        CommonPage<StoreWear> storeCouponCommonPage = CommonPage.restPage(storeWearService.getWearH5List(request, pageParamRequest));
        return CommonResult.success(storeCouponCommonPage);
    }


}
