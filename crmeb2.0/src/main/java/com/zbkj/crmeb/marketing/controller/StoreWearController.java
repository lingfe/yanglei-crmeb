package com.zbkj.crmeb.marketing.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.marketing.model.StoreWear;
import com.zbkj.crmeb.marketing.request.StoreWearRequest;
import com.zbkj.crmeb.marketing.request.StoreWearSearchRequest;
import com.zbkj.crmeb.marketing.response.StoreWearResponse;
import com.zbkj.crmeb.marketing.service.StoreWearService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 *  穿搭表-控制层、表示类
 * @author: 零风
 * @CreateDate: 2021/10/8 10:55
 */
@Slf4j
@RestController
@RequestMapping("api/admin/marketing/wear")
@Api(tags = "营销 -- 穿搭")
public class StoreWearController {

    @Autowired
    private StoreWearService storeWearService;

    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<StoreWear>>  getList(@Validated StoreWearSearchRequest request, @Validated PageParamRequest pageParamRequest){
        CommonPage<StoreWear> storeCouponCommonPage = CommonPage.restPage(storeWearService.getList(request, pageParamRequest));
        return CommonResult.success(storeCouponCommonPage);
    }

    @ApiOperation(value = "添加")
    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    public CommonResult<String> insert(@RequestBody @Validated StoreWearRequest request){
        if(storeWearService.insert(request)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public CommonResult<String> delete(@RequestParam Integer id){
        if(storeWearService.delete(id)){
            return CommonResult.success("删除成功");
        }else{
            return CommonResult.failed("删除失败");
        }
    }

    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated StoreWearRequest request){
        if(storeWearService.update(request)){
            return CommonResult.success("删除成功");
        }else{
            return CommonResult.failed("删除失败");
        }
    }

    @ApiOperation(value = "是否显示")
    @RequestMapping(value = "/isShow", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "id标识"),
            @ApiImplicitParam(name = "isShow",value = "是否显示")
    })
    public CommonResult<String> isShow(
            @RequestParam("id") Integer id,
            @RequestParam("isShow") Boolean isShow){
        if(storeWearService.isShow(id,isShow)){
            return CommonResult.success("操作成功!");
        }else{
            return CommonResult.failed("操作失败!");
        }
    }

    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<StoreWearResponse> isShow(@RequestParam("id") Integer id){
        return CommonResult.success(storeWearService.info(id));
    }

    @ApiOperation(value = "是否展示在首页-用于h5用户端")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "id标识"),
            @ApiImplicitParam(name = "isIndex",value = "是否展示在首页")
    })
    @RequestMapping(value = "/isIndex", method = RequestMethod.GET)
    public CommonResult<Object> isIndex(
            @RequestParam("id") Integer id,
            @RequestParam("isIndex") Boolean isIndex){
        return CommonResult.success(storeWearService.isIndex(id,isIndex));
    }

}
