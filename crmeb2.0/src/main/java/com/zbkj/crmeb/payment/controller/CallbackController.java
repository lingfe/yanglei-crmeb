package com.zbkj.crmeb.payment.controller;

import com.zbkj.crmeb.payment.service.CallbackService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


/**
 * 支付回调
 * @author: 零风
 * @CreateDate: 2022/1/19 14:58
 */
@Slf4j
@RestController
@RequestMapping("api/admin/payment/callback")
@Api(tags = "支付回调")
public class CallbackController {

    @Autowired
    private CallbackService callbackService;

    /**
     * 微信支付回调
     * @author Mr.Zhang
     * @since 2020-05-06
     * @return
     */
    @ApiOperation(value = "微信支付回调")
    @RequestMapping(value = "/wechat", method = RequestMethod.POST)
    public String weChat(@RequestBody String  request) {
        System.out.println("微信支付回调 request ===> " + request);
        String response = callbackService.weChat(request);
        System.out.println("微信支付回调 response ===> " + response);
        return response;
    }

    /**
     * 支付宝支付回调
     * @author Mr.Zhang
     * @since 2020-05-06
     */
    @ApiOperation(value = "支付宝支付回调 ")
    @RequestMapping(value = "/alipay", method = RequestMethod.POST)
    public void aliPay(HttpServletRequest request){
        //支付宝支付回调
        callbackService.aliPay(request);
    }

    /**
     * 微信退款回调
     */
    @ApiOperation(value = "微信退款回调")
    @RequestMapping(value = "/wechat/refund", method = RequestMethod.POST)
    public String weChatRefund(@RequestBody String request) {
        System.out.println("微信退款回调 request ===> " + request);
        String response = callbackService.weChatRefund(request);
        System.out.println("微信退款回调 response ===> " + response);
        return response;
    }
}



