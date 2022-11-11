package com.zbkj.crmeb.front.controller;


import com.common.CheckAdminToken;
import com.common.CommonResult;
import com.utils.ValidateFormUtil;
import com.zbkj.crmeb.front.request.LoginMobileRequest;
import com.zbkj.crmeb.front.request.LoginRequest;
import com.zbkj.crmeb.front.request.PublicUserLoginRequest;
import com.zbkj.crmeb.front.response.LoginResponse;
import com.zbkj.crmeb.front.service.LoginService;
import com.zbkj.crmeb.sms.service.SmsService;
import com.zbkj.crmeb.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户登陆-前端控制器
 * @author: 零风
 * @CreateDate: 2022/1/6 10:08
 */
@Slf4j
@RestController("FrontLoginController")
@RequestMapping("api/front")
@Api(tags = "用户 -- 登录注册")
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    CheckAdminToken checkAdminToken;

    @Autowired
    private SmsService smsService;

    @Autowired
    private LoginService loginService;

    @ApiOperation(value = "用户登录(公共接口)")
    @RequestMapping(value = "/userLogin",method = RequestMethod.POST)
    protected CommonResult<LoginResponse> userLogin(@RequestBody @Validated PublicUserLoginRequest data) {
        return CommonResult.success(loginService.publicUserLogin(data));
    }

    @ApiOperation(value = "字节-小程序授权登录")
    @RequestMapping(value = "/zijie/login", method = RequestMethod.POST)
    public CommonResult<LoginResponse> programLogin(@RequestParam String code, @RequestBody @Validated PublicUserLoginRequest request){
        return CommonResult.success(loginService.zijieAuthorizeProgramLogin(code, request));
    }

    /**
     * @description 苹果账号授权APP登录
     * @author 零风
     * @updateTime 2019-9-14 10:00:51
     */
    @ApiOperation(value = "苹果账号授权APP登录")
    @RequestMapping(value = "/login/app/iosAccountLogin",method = RequestMethod.GET)
    protected CommonResult<LoginResponse> iosAccountLogin(@RequestParam("data") String data) {
        return CommonResult.success(loginService.iosAccountLogin(data));
    }

    /**
     * 手机号登录接口
     */
    @ApiOperation(value = "手机号登录接口")
    @RequestMapping(value = "/login/mobile", method = RequestMethod.POST)
    public CommonResult<LoginResponse> phoneLogin(@RequestBody @Validated LoginMobileRequest loginRequest) throws Exception {
        return CommonResult.success(loginService.phoneLogin(loginRequest));
    }

    /**
     * 账号密码登录
     */
    @ApiOperation(value = "账号密码登录")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public CommonResult<LoginResponse> login(@RequestBody @Validated LoginRequest loginRequest) throws Exception {
        return CommonResult.success(loginService.login(loginRequest));
    }

    /**
     * 退出登录
     */
    @ApiOperation(value = "退出")
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public CommonResult<String> loginOut(HttpServletRequest request){
        userService.loginOut(checkAdminToken.getTokenFormRequest(request));
        return CommonResult.success();
    }

    /**
     * 发送短信登录验证码
     * @param phone 手机号码
     * @return 发送是否成功
     */
    @ApiOperation(value = "发送短信登录验证码")
    @RequestMapping(value = "/sendCode", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name="phone", value="手机号码", required = true)
    })
    public CommonResult<Object> sendCode(@RequestParam String phone){
        ValidateFormUtil.isPhone(phone,"手机号码错误");
        if(smsService.sendCommonCode(phone)){
            return CommonResult.success("发送成功");
        }else{
            return CommonResult.failed("发送失败");
        }
    }
}



