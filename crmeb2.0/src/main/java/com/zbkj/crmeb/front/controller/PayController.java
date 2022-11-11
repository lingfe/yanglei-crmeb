package com.zbkj.crmeb.front.controller;

import com.common.CommonResult;
import com.utils.CrmebUtil;
import com.zbkj.crmeb.front.request.OrderPayRequest;
import com.zbkj.crmeb.front.response.OrderPayResultResponse;
import com.zbkj.crmeb.payment.service.OrderPayService;
import com.zbkj.crmeb.payment.wechat.WeChatPayService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 前端支付管理
 * @author: 零风
 * @CreateDate: 2022/1/13 15:08
 */
@Slf4j
@RestController
@RequestMapping("api/front/pay")
@Api(tags = "支付管理")
public class PayController {

    @Autowired
    private WeChatPayService weChatPayService;

    @Autowired
    private OrderPayService orderPayService;

    @ApiOperation(value = "订单支付")
    @RequestMapping(value = "/payment", method = RequestMethod.POST)
    public CommonResult<OrderPayResultResponse> payment(
            @RequestBody @Validated OrderPayRequest orderPayRequest,
            HttpServletRequest request,
            HttpServletResponse httpServletResponse) {
        String ip = CrmebUtil.getClientIp(request);
        if("127.0.0.1".equals(ip)){
            ip="8.140.130.114";
        }
        return CommonResult.success(orderPayService.payment(orderPayRequest, ip,httpServletResponse));
    }

    @ApiOperation(value = "查询支付结果")
    @RequestMapping(value = "/queryPayResult", method = RequestMethod.GET)
    @ApiImplicitParam(name = "orderNo",value = "订单编号|String|必填")
    public CommonResult<Boolean> queryPayResult(@RequestParam String orderNo) {
        return CommonResult.success(weChatPayService.queryPayResult(orderNo));
    }

}
