package com.zbkj.crmeb.marketing.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.combination.response.StoreCombinationResponse;
import com.zbkj.crmeb.marketing.request.StoreIntegalShopRequest;
import com.zbkj.crmeb.marketing.request.StoreIntegalShopSearchRequest;
import com.zbkj.crmeb.marketing.response.StoreIntegalShopResponse;
import com.zbkj.crmeb.marketing.service.StoreIntegalShopService;
import com.zbkj.crmeb.store.response.StoreProductResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @program: crmeb
 * @description: 商城-营销-积分商品表controller控制层、表示类
 * @author: 零风
 * @create: 2021-07-01 15:30
 **/
@Slf4j
@RestController
@RequestMapping("api/admin/marketing/integal/shop")
@Api(tags = "营销 -- 积分兑换商品")
public class StoreIntegalShopController {

    @Autowired
    private StoreIntegalShopService storeIntegalShopService;

    /**
     * 分页显示积分兑换商品表
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     * @author 零风
     * @since 2021-07-02
     * @return
     */
    @ApiOperation(value = "分页显示积分兑换商品表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<StoreIntegalShopResponse>> getList(@Validated StoreIntegalShopSearchRequest request, @Validated PageParamRequest pageParamRequest){
        CommonPage<StoreIntegalShopResponse> commonPage = CommonPage.restPage(storeIntegalShopService.getList(request, pageParamRequest));
        return CommonResult.success(commonPage);
    }

    /**
     * 新增积分兑换商品
     * @param  storeIntegalShopRequest 新增参数
     * @author 零风
     * @since 2021-07-01
     */
    @ApiOperation(value = "新增积分兑换商品")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated StoreIntegalShopRequest storeIntegalShopRequest){
        if(storeIntegalShopService.saveBargain(storeIntegalShopRequest)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 删除积分兑换商品
     * @param id Integer
     * @author 零风
     * @since 2021-07-02
     */
    @ApiOperation(value = "删除积分兑换商品")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id){
        if(storeIntegalShopService.deleteById(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 修改积分兑换商品表
     * @param id integer id
     * @param StoreIntegalShopRequest 修改参数
     * @author 零风
     * @since 2021-07-02
     */
    @ApiOperation(value = "修改积分兑换商品")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestParam Integer id, @RequestBody @Validated StoreIntegalShopRequest StoreIntegalShopRequest){
        StoreIntegalShopRequest.setId(id);
        if(storeIntegalShopService.updateIntegal(StoreIntegalShopRequest)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 查询积分兑换商品信息
     * @param id Integer
     * @author 零风
     * @since 2021-07-02
     * @return
     */
    @ApiOperation(value = "积分兑换商品详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<StoreProductResponse> info(@RequestParam(value = "id") Integer id){
        StoreProductResponse detail = storeIntegalShopService.getAdminDetail(id);
        return CommonResult.success(detail);
    }

    /**
     * 是否显示
     * @param id    积分兑换商品ID
     * @param isShow    是否显示
     * @return
     */
    @ApiOperation(value = "是否显示")
    @RequestMapping(value = "/update/status", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "积分兑换商品ID",dataType = "int", required = true),
            @ApiImplicitParam(name = "isShow", value = "是否显示",dataType = "boolean", required = true)
    })
    public CommonResult<Object> updateIsShow(@RequestParam(value = "id") Integer id,
                                             @RequestParam(value = "isShow") boolean isShow){
        if(storeIntegalShopService.updateIsShow(id, isShow)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }
}
