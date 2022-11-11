package com.zbkj.crmeb.front.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.constants.PayConstants;
import com.utils.CrmebUtil;
import com.zbkj.crmeb.front.request.UserRechargeRequest;
import com.zbkj.crmeb.front.response.OrderPayResultResponse;
import com.zbkj.crmeb.front.response.UserRechargeBillRecordResponse;
import com.zbkj.crmeb.front.response.UserRechargeResponse;
import com.zbkj.crmeb.front.service.UserCenterService;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


/**
 * 用户 -- 充值
 * @author: 零风
 * @CreateDate: 2022/3/21 9:56
 */
@Slf4j
@RestController("UserRechargeController")
@RequestMapping("api/front/recharge")
@Api(tags = "用户 -- 充值")
public class UserRechargeController {

    @Autowired
    private UserCenterService userCenterService;

    @Autowired
    private UserService userService;

    /**
     * 充值额度选择
     */
    @ApiOperation(value = "充值额度选择")
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public CommonResult<UserRechargeResponse> getRechargeConfig(){
        return CommonResult.success(userCenterService.getRechargeConfig());
    }

    /**
     * 充值
     */
    @ApiOperation(value = "小程序充值")
    @RequestMapping(value = "/routine", method = RequestMethod.POST)
    public CommonResult<Map<String, Object>> routineRecharge(HttpServletRequest httpServletRequest, @RequestBody @Validated UserRechargeRequest request){
        request.setFromType(PayConstants.PAY_CHANNEL_WE_CHAT_PROGRAM);
        OrderPayResultResponse recharge = userCenterService.recharge(request);
        request.setClientIp(CrmebUtil.getClientIp(httpServletRequest));

        Map<String, Object> map = new HashMap<>();
        map.put("data", recharge);
        map.put("type", request.getFromType());
        return CommonResult.success(map);
    }

    /**
     * 充值
     */
    @ApiOperation(value = "公众号充值")
    @RequestMapping(value = "/wechat", method = RequestMethod.POST)
    public CommonResult<OrderPayResultResponse> weChatRecharge(HttpServletRequest httpServletRequest, @RequestBody @Validated UserRechargeRequest request){
        request.setClientIp(CrmebUtil.getClientIp(httpServletRequest));
        return CommonResult.success(userCenterService.recharge(request));
    }

    /**
     * App充值
     */
    @ApiOperation(value = "App充值")
    @RequestMapping(value = "/wechat/app", method = RequestMethod.POST)
    public CommonResult<OrderPayResultResponse> weChatAppRecharge(HttpServletRequest httpServletRequest, @RequestBody @Validated UserRechargeRequest request){
        request.setClientIp(CrmebUtil.getClientIp(httpServletRequest));
        return CommonResult.success(userCenterService.recharge(request));
    }

    @ApiOperation(value = "佣金转入账户余额")
    @RequestMapping(value = "/transferIn", method = RequestMethod.POST)
    @ApiImplicitParam(name = "price",value = "转入金额")
    public CommonResult<Boolean> transferIn(@RequestParam(name = "price") BigDecimal price){
        User user=userService.getInfoException();
        Map<String,String> map=userService.transferIn(price, Constants.USER_BILL_transferIn_TYPE_1,user,BigDecimal.ZERO);
        if(Boolean.valueOf(map.get("result"))){
            return CommonResult.success(map.get("msg"));
        }else{
            return CommonResult.failed(map.get("msg"));
        }
    }

    /**
     * 用户账单记录
     */
    @ApiOperation(value = "用户账单记录")
    @RequestMapping(value = "/bill/record", method = RequestMethod.GET)
    @ApiImplicitParam(name = "type", value = "记录类型：all-全部，expenditure-支出，income-收入", required = true)
    public CommonResult<CommonPage<UserRechargeBillRecordResponse>> billRecord(@RequestParam(name = "type") String type, @ModelAttribute PageParamRequest pageRequest){
        return CommonResult.success(userCenterService.nowMoneyBillRecord(type, pageRequest));
    }
}



