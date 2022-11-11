package com.zbkj.crmeb.retailer.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.retailer.model.Retailer;
import com.zbkj.crmeb.retailer.request.RetailerRequest;
import com.zbkj.crmeb.retailer.request.RetailerSearchRequest;
import com.zbkj.crmeb.retailer.response.RetailerResponse;
import com.zbkj.crmeb.retailer.service.RetailerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 零售商-控制类、表示层
 * @author: 零风
 * @CreateDate: 2021/11/22 15:04
 */
@Slf4j
@RestController
@RequestMapping("api/admin/retailer")
@Api(tags = "零售商")
public class RetailerController {

    @Autowired
    private RetailerService retailerService;

    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<RetailerResponse>> getList(@Validated RetailerSearchRequest request, @Validated PageParamRequest pageParamRequest){
        return CommonResult.success(CommonPage.restPage(retailerService.getList(request, pageParamRequest)));
    }

    @ApiOperation(value = "审核-零售商")
    @RequestMapping(value = "/merID/retailer/add", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "零售商表ID标识"),
            @ApiImplicitParam(name = "status",value = "状态，1=审核通过，2=审核不通过")
    })
    public CommonResult<String> retailerAdd(@RequestParam(name = "id") Integer id,
                                            @RequestParam(name = "status") Integer status){
        if(retailerService.updateState(id,status)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    @ApiOperation(value = "查看详情")
    @RequestMapping(value = "/getInfo", method = RequestMethod.GET)
    @ApiImplicitParam(name = "id",value = "零售商表ID标识")
    public CommonResult<RetailerResponse> getInfo(@RequestParam(name = "id") Integer id){
        return CommonResult.success(retailerService.getInfo(id));
    }

    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<Boolean> update(@RequestBody @Validated RetailerRequest request){
        return CommonResult.success(retailerService.update(request));
    }

    @ApiOperation(value = "新增")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public CommonResult<Retailer> add(@RequestBody @Validated RetailerRequest request){
        return CommonResult.success(retailerService.add(request));
    }

}
