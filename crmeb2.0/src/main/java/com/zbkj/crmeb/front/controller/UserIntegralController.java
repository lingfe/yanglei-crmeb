package com.zbkj.crmeb.front.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.request.AdminIntegralSearchRequest;
import com.zbkj.crmeb.user.request.UserIntegralExchangeRecordSearchRequest;
import com.zbkj.crmeb.user.response.UserIntegralExchangeRecordResponse;
import com.zbkj.crmeb.user.response.UserIntegralRecordResponse;
import com.zbkj.crmeb.user.service.UserIntegralExchangeRecordService;
import com.zbkj.crmeb.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: crmeb
 * @description: 用户积分相关-h5用户端使用-控制类、表示层
 * @author: 零风
 * @create: 2021-07-07 15:59
 **/
@Slf4j
@RestController("UserIntegralController_front")
@RequestMapping("api/front/user/integral")
@Api(tags = "用户 -- 积分相关 ")
public class UserIntegralController {

    @Autowired
    private UserIntegralExchangeRecordService userIntegralExchangeRecordService;

    @Autowired
    private UserService userService;

    /**
     * 用户积分兑换记录-h5用户端使用
     * @param pageParamRequest
     * @return
     */
    @ApiOperation(value = "用户积分兑换记录-h5用户端使用")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public CommonResult<CommonPage<UserIntegralExchangeRecordResponse>> list(
            @Validated PageParamRequest pageParamRequest){
        //实例化对象
        UserIntegralExchangeRecordSearchRequest request=new UserIntegralExchangeRecordSearchRequest();

        //得到当前用户
        User user = userService.getInfoException();
        request.setUserId(user.getUid());

        //得到数据
        CommonPage<UserIntegralExchangeRecordResponse> restPage = CommonPage.restPage(userIntegralExchangeRecordService.findAdminList(request, pageParamRequest));
        return CommonResult.success(restPage);
    }

}
