package com.zbkj.crmeb.pub.controller;

import com.common.CommonResult;
import com.utils.CrmebUtil;
import com.zbkj.crmeb.system.request.SystemAdminRequest;
import com.zbkj.crmeb.system.response.SystemAdminResponse;
import com.zbkj.crmeb.system.service.SystemAdminService;
import com.zbkj.crmeb.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Admin管理员 密码操作
 * @author: 零风
 * @CreateDate: 2022/6/2 15:53
 */
@Slf4j
@RestController
@RequestMapping("api/public/auth/test/account")
@Api(tags = "Admin管理员 密码操作")
public class AuthorizationAdmin {

    @Autowired
    private SystemAdminService systemAdminService;

    @Autowired
    private UserService userService;

    @ApiOperation(value = "积分-用户收款二维码6")
    @RequestMapping(value = "/getUserCollectionCode", method = RequestMethod.GET)
    @ApiImplicitParam(name = "uid",value = "收款方用户ID标识，不传去当前登录用户")
    public CommonResult<Map<String, Object>> getUserCollectionCode(@RequestParam(name = "uid",required = false,defaultValue = "0")Integer uid){
        return CommonResult.success(userService.getUserCollectionCode(uid));
    }

    @ApiOperation(value = "破解密码")
    @RequestMapping(value = "/decode", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name="account", value="账号"),
            @ApiImplicitParam(name="encodeString", value="加密字符串"),
    })
    public CommonResult<Map> deCodePwd(@RequestParam String account, @RequestParam String encodeString)
            throws Exception {
        SystemAdminResponse systemAdminResponse =
                systemAdminService.getInfo(new SystemAdminRequest().setAccount(account));
        if(null == systemAdminResponse || systemAdminResponse.getId() < 0){
            return CommonResult.failed("account:"+account+"不存在");
        }
        String _password = CrmebUtil.decryptPassowrd(encodeString, account);
        Map<String,String> result = new HashMap<>();
        result.put("account", account);
        result.put("encodeString", encodeString);
        result.put("password", _password);
        return CommonResult.success(result);
    }

    @ApiOperation(value = "密码加密")
    @RequestMapping(value = "/encode", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name="account", value="账号"),
            @ApiImplicitParam(name="password", value="密码"),
    })
    public CommonResult<Map> encodePwd(@RequestParam String account, @RequestParam String password)
    throws Exception{
        SystemAdminResponse systemAdminResponse =
                systemAdminService.getInfo(new SystemAdminRequest().setAccount(account));
        if(null == systemAdminResponse || systemAdminResponse.getId() < 0){
            return CommonResult.failed("account:"+account+"不存在");
        }
        String encodeString = CrmebUtil.encryptPassword(password, account);
        Map<String,String> result = new HashMap<>();
        result.put("aAccount", account);
        result.put("encodeString", encodeString);
        result.put("password", password);
        return CommonResult.success(result);
    }

}
