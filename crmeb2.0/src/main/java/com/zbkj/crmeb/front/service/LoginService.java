package com.zbkj.crmeb.front.service;

import com.zbkj.crmeb.front.request.LoginMobileRequest;
import com.zbkj.crmeb.front.request.LoginRequest;
import com.zbkj.crmeb.front.request.PublicUserLoginRequest;
import com.zbkj.crmeb.front.response.LoginResponse;
import com.zbkj.crmeb.user.model.User;

/**
 * 移动端登录服务-service接口层
 * @author: 零风
 * @CreateDate: 2022/1/6 16:30
 */
public interface LoginService {

    /**
     * 用户登录(公共登录接口)
     * @Author 零风
     * @Date  2022/4/18
     * @return
     */
    LoginResponse publicUserLogin(PublicUserLoginRequest data);

    /**
     * 苹果账号授权APP登录
     * @param data  参数
     * @author 零风
     * @updateTime 2019-9-14 10:00:51
     * @return
     */
    LoginResponse iosAccountLogin(String data);

    /**
     * 账号密码登录
     * @return LoginResponse
     */
    LoginResponse login(LoginRequest loginRequest) throws Exception;

    /**
     * 手机号验证码登录
     */
    LoginResponse phoneLogin(LoginMobileRequest loginRequest) throws Exception;

    /**
     * 老绑定分销关系
     * @param user User 用户user类
     * @param spreadUid Integer 推广人id
     * @return Boolean
     */
    Boolean bindSpread(User user, Integer spreadUid);


    /**
     * 字节-小程序授权登录
     * @Author 零风
     * @Date  2022/6/28 14:50
     * @return
     */
    LoginResponse zijieAuthorizeProgramLogin(String code, PublicUserLoginRequest request);

}
