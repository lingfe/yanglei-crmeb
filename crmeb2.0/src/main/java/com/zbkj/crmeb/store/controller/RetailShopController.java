package com.zbkj.crmeb.store.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.store.request.RetailShopRequest;
import com.zbkj.crmeb.store.request.RetailShopStairUserRequest;
import com.zbkj.crmeb.store.response.RetailShopStatisticsResponse;
import com.zbkj.crmeb.store.response.SpreadOrderResponse;
import com.zbkj.crmeb.store.service.RetailShopService;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.response.SpreadUserResponse;
import com.zbkj.crmeb.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 分销模块
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Slf4j
@RestController
@RequestMapping("api/admin/store/retail")
@Api(tags = "分销")
public class RetailShopController {

    @Autowired
    private RetailShopService retailShopService;

    @Autowired
    private UserService userService;

    /**
     * 分销员列表
     * @param keywords         搜索参数
     * @param dateLimit        时间参数
     * @param pageParamRequest 分页参数
     */
    @ApiOperation(value = "分销员列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keywords", value = "搜索关键字[身份证，手机，昵称，备注等]"),
            @ApiImplicitParam(name = "dateLimit", value = "today,yesterday,lately7,lately30,month,year,/yyyy-MM-dd hh:mm:ss,yyyy-MM-dd hh:mm:ss/")
    })
    public CommonResult<CommonPage<SpreadUserResponse>> getList(@RequestParam(required = false) String keywords,
                                                                @RequestParam(required = false) String dateLimit,
                                                                @ModelAttribute PageParamRequest pageParamRequest) {
        return CommonResult.success(retailShopService.getSpreadPeopleList(keywords, dateLimit, pageParamRequest));
    }

    /**
     * 分销头部信息
     * @param keywords  搜索参数
     * @param dateLimit 时间参数
     */
    @ApiOperation(value = "分销头部数据")
    @RequestMapping(value = "/statistics", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keywords", value = "搜索参数"),
            @ApiImplicitParam(name = "dateLimit", value = "today,yesterday,lately7,lately30,month,year,/yyyy-MM-dd hh:mm:ss,yyyy-MM-dd hh:mm:ss/")
    })
    public CommonResult<RetailShopStatisticsResponse> getStatistics(@RequestParam(required = false) String keywords,
                                                                    @RequestParam(required = false) String dateLimit) {
        return CommonResult.success(retailShopService.getAdminStatistics(keywords, dateLimit));
    }

    /**
     * 添加推广关系
     * @param currentUserId 当前用户id
     * @param spreadUserId  推广人用户id
     * @return 结果
     */
    @ApiOperation(value = "添加推广关系")
    @RequestMapping(value = "/spread/save", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "currentUserId", value = "当前用户id"),
            @ApiImplicitParam(name = "spreadUserId", value = "推广人id")
    })
    public CommonResult<Object> save(Integer currentUserId, Integer spreadUserId) {
        return CommonResult.success(userService.spread(currentUserId, spreadUserId));
    }

    /**
     * 根据用户参数获取推广人列表
     * @param request          查询参数
     * @param pageParamRequest 分页参数
     * @return 查询结果推广人列表
     */
    @ApiOperation(value = "根据条件获取推广人列表")
    @RequestMapping(value = "/spread/userlist", method = RequestMethod.POST)
    public CommonResult<CommonPage<User>> getUserListBySpreadLevel(@RequestBody @Validated RetailShopStairUserRequest request,
                                                                   @ModelAttribute PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(userService.getUserListBySpreadLevel(request, pageParamRequest)));
    }

    /**
     * 根据参数获取推广订单列表
     * @param request          查询参数
     * @param pageParamRequest 分页参数
     * @return 查询结果推广人订单列表
     */
    @ApiOperation(value = "根据条件获取推广人订单")
    @RequestMapping(value = "/spread/orderlist", method = RequestMethod.POST)
    public CommonResult<CommonPage<SpreadOrderResponse>> getOrdersBySpreadLevel(@RequestBody @Validated RetailShopStairUserRequest request,
                                                                                @ModelAttribute PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(userService.getOrderListBySpreadLevel(request, pageParamRequest)));
    }

    /**
     * 清除上级推广人
     * @param id 当前被清理的用户id
     * @return 推广关系清理后的结果
     */
    @ApiOperation(value = "清除上级推广人")
    @RequestMapping(value = "/spread/clean/{id}", method = RequestMethod.GET)
    public CommonResult<Object> clearSpread(@PathVariable Integer id) {
        return CommonResult.success(userService.clearSpread(id));
    }

    /**
     * 分销设置获取
     * @return 保存分销设置
     */
    @ApiOperation(value = "分销配置信息获取")
    @RequestMapping(value = "/spread/manage/get", method = RequestMethod.GET)
    public CommonResult<Object> getSpreadInfo() {
        return CommonResult.success(retailShopService.getManageInfo());
    }

    /**
     * 分销管理信息保存
     * @param retailShopRequest 分销管理对象
     * @return 保存结果
     */
    @ApiOperation(value = "分销管理信息保存")
    @RequestMapping(value = "/spread/manage/set", method = RequestMethod.POST)
    public CommonResult<Object> setSpreadInfo(@RequestBody @Validated RetailShopRequest retailShopRequest) {
        return CommonResult.success(retailShopService.setManageInfo(retailShopRequest));
    }
}
