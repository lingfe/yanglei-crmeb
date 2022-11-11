package com.zbkj.crmeb.front.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSONObject;
import com.constants.Constants;
import com.constants.SmsConstants;
import com.exception.CrmebException;
import com.utils.*;
import com.zbkj.crmeb.front.request.LoginMobileRequest;
import com.zbkj.crmeb.front.request.LoginRequest;
import com.zbkj.crmeb.front.request.PublicUserLoginRequest;
import com.zbkj.crmeb.front.response.LoginResponse;
import com.zbkj.crmeb.front.service.LoginService;
import com.zbkj.crmeb.store.model.ServiceProviderTwolevel;
import com.zbkj.crmeb.store.service.ServiceProviderTwolevelService;
import com.zbkj.crmeb.system.service.SystemConfigService;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.model.UserToken;
import com.zbkj.crmeb.user.request.RegisterThirdUserRequest;
import com.zbkj.crmeb.user.service.UserService;
import com.zbkj.crmeb.user.service.UserTokenService;
import com.zbkj.crmeb.wechat.response.WeChatAuthorizeLoginUserInfoResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 移动端登录服务类
 *
 * 2021.07.15
 * 1、合并了登录响应对象，合并方法: getLoginResponse
 * @author ；零风
 */
@Service
public class LoginServiceImpl implements LoginService {

    private static final Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private UserTokenService userTokenService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private RestTemplateUtil restTemplateUtil;

    @Autowired
    private ServiceProviderTwolevelService serviceProviderTwolevelService;

    public static void main(String[] args) {
        System.out.println(Integer.parseInt(null));
    }

    @Override
    public LoginResponse zijieAuthorizeProgramLogin(String code, PublicUserLoginRequest request) {
        try {
            //通过code获取access_token
            String programAppId = systemConfigService.getValueByKey("zhijie_appid");
            if(StringUtils.isBlank(programAppId)){
                throw new CrmebException("字节小程序appId未设置");
            }

            //验证zhijie_appsecret
            String programAppSecret = systemConfigService.getValueByKey("zhijie_appsecret");
            if(StringUtils.isBlank(programAppSecret)){
                throw new CrmebException("字节小程序secret未设置");
            }

            //拼接请求https://developer.toutiao.com/api/apps/v2/jscode2session
            String url = new StringBuffer(Constants.ZIJIE_APl_URL).append(Constants.ZIJIE_API_GET_ACCESS_TOKEN).toString();
            Map<String,Object> map=new HashMap<>();
            map.put("appid",programAppId);
            map.put("secret",programAppSecret);
            map.put("code",code);
            String result = restTemplateUtil.postMapData(url,map);
            JSONObject jsonObject=JSONObject.parseObject(result);
            JSONObject data=jsonObject.getJSONObject("data");

            //验证渠道二级服务商是否存在
            ServiceProviderTwolevel serviceProviderTwolevel = serviceProviderTwolevelService.getById(request.getTokenType());
            if(serviceProviderTwolevel == null){
                throw new CrmebException("token类型错误!");
            }

            //检测token是否存在
            String openid=data.get("openid").toString();
            UserToken userToken = userTokenService.getByOpenidAndType(openid, request.getTokenType());
            LoginResponse loginResponse = new LoginResponse();
            if(ObjectUtil.isNotNull(userToken)) {// 已存在，正常登录
                User user = userService.getById(userToken.getUid());
                if (!user.getStatus()) {
                    throw new CrmebException("当前账户已禁用，请联系管理员！");
                }

                // 记录最后一次登录时间
                user.setLastLoginTime(DateUtil.nowDateTime());
                Boolean execute = transactionTemplate.execute(e -> {
                    // 分销绑定
                    if (userService.checkBingSpread(user, request.getSpreadPid(), "old")) {
                        user.setSpreadUid(request.getSpreadPid());
                        user.setSpreadTime(DateUtil.nowDateTime());
                        // 处理新旧推广人数据
                        userService.updateSpreadCountByUid(request.getSpreadPid(), "add");
                    }
                    userService.updateById(user);
                    return Boolean.TRUE;
                });

                //执行结果
                if (!execute) {
                    logger.error(StrUtil.format("小程序登录绑定分销关系失败，uid={},spreadUid={}", user.getUid(), request.getSpreadPid()));
                }

                //生成token
                try {
                    String token = userService.token(user);
                    loginResponse.setToken(token);
                } catch (Exception e) {
                    logger.error(StrUtil.format("小程序登录生成token失败，uid={}", user.getUid()));
                    e.printStackTrace();
                }

                //登录成功，放给前端token
                loginResponse.setType("login");
                loginResponse.setUid(user.getUid());
                loginResponse.setNikeName(user.getNickname());
                loginResponse.setPhone(user.getPhone());
                return loginResponse;
            }else{
                //验证参数非空
                if (StrUtil.isBlank(request.getNickName()) && StrUtil.isBlank(request.getAvatarUrl())) {
                    //参数不正确，去走注册起始页重新吊起登录
                    loginResponse.setType("start");
                    return loginResponse;
                }else{
                    //更新值返回给前端去下一步注册
                    request.setOpenid(openid);
                    String key = SecureUtil.md5(openid);
                    redisUtil.set(key, JSONObject.toJSONString(request), (long) (60 * 2), TimeUnit.MINUTES);
                    loginResponse.setType("register");
                    loginResponse.setKey(key);
                    return loginResponse;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new CrmebException(e.getMessage());
        }
    }

    public String getToken(User user) throws Exception {
        //验证-状态
        if (!user.getStatus()) {
            throw new CrmebException("当前账户已禁用，请联系管理员！");
        }

        //更新用户信息
        //记录最后一次登录时间
        user.setLastLoginTime(DateUtil.nowDateTime());
        userService.updateById(user);

        //得到token
        String token = userService.token(user);
        return token;
    }

    /**
     * 添加用户
     * @Author 零风
     * @Date 9:52 2022/5/10
     * @return 用户信息
     */
    public User addUser(PublicUserLoginRequest data) throws Exception {
        //验证手机号是否存在
        User phoneUser = userService.getByPhone(data.getPhone());
        User user=null;
        if(phoneUser == null){
            //不存在，创建用户
            user = new User();
            user.setUserType(data.getTokenType().toString());
            user.setSex(data.getSex() == null ?0:data.getSex());
            user.setAvatar(data.getAvatarUrl());
            user.setSpId(data.getSpId());
            user.setSptlId(data.getSptlId());

            //用户地址
            user.setAddres(new StringBuffer()
                    .append(data.getCountry()).append(",")
                    .append(data.getProvince()).append(",")
                    .append(data.getCity()).toString());

            //设置用户名称
            StringBuffer nickName = new StringBuffer();
            nickName.append(data.getTokenType()).append(":");
            nickName.append(data.getNickName()).append(":");
            nickName.append(data.getAccount());
            user.setNickname(nickName.toString());

            //验证token
            switch (data.getTokenType()){
                case 8:data.setSpreadPid(null);break;
            }

            //验证推荐人用户ID
            Integer preadUid = data.getSpreadPid();
            if (preadUid == null || preadUid <= 0) preadUid = 0;
            user.setSpreadUid(preadUid);
            user.setSpreadTime(DateUtil.nowDateTime());

            //验证手机号
            String phone = data.getPhone();
            if (data.getPhone() == null) phone = "";
            user.setPhone(phone);
            user.setAccount(CommonUtil.createNickName(phone));
            user.setPwd(CommonUtil.createPwd(phone));

            //保存用户信息
            userService.save(user);
        }else{
            user=phoneUser;
        }

        //重新验证token是否存在
        UserToken userToken = userTokenService.getByOpenidAndType(data.getOpenid(), data.getTokenType());
        if (!ObjectUtil.isNotNull(userToken)) {
            // 根据用户类型-绑定token
            userTokenService.bind(data.getOpenid(), data.getTokenType(), user.getUid());
        }
        return user;
    }

    @Override
    public LoginResponse publicUserLogin(PublicUserLoginRequest data) {
        //实例化-登录响应对象
        LoginResponse loginResponse = new LoginResponse();
        String type="login";
        String token=null;
        User user=null;

        try {
            //检测-token是否存在
            UserToken userToken = userTokenService.getByOpenidAndType(data.getOpenid(), data.getTokenType());
            if (ObjectUtil.isNotNull(userToken)) {
                // 已存在，正常登录
                user = userService.getById(userToken.getUid());
                token = this.getToken(user);
            } else {
                // 保存用户信息
                user=this.addUser(data);
                //生成token
                token = userService.token(user);
            }

            // 验证推荐人
            this.isSprUser(data, user);

            //赋值-并返回
            loginResponse.setToken(token);
            loginResponse.setType(type);
            loginResponse.setUid(user.getUid());
            loginResponse.setNikeName(user.getNickname());
            loginResponse.setPhone(user.getPhone());
            return loginResponse;
        }catch (Exception e) {
            logger.error(StrUtil.format("用户登录失败，user={}", user));
            throw new CrmebException(e.getMessage());
        }
    }

    /**
     * 验证推荐人
     * -如果不为空，则继续验证是否存在
     * -如果不存在，则保存添加推荐人信息并绑定推荐关系
     * @Author 零风
     * @Date  2022/5/10 9:54
     * @return
     */
    private void isSprUser(PublicUserLoginRequest data, User user) throws Exception {
        //验证推荐人信息
        if(data.getSprRequest() != null){
            User userSpr=userService.getById(user.getSpreadUid());
            if(userSpr == null){
                //保存推荐人信息
                userSpr = this.addUser(data.getSprRequest());

                // 分销绑定
                if (userService.checkBingSpread(user, userSpr.getUid(), "old")) {
                    user.setSpreadUid(userSpr.getUid());
                    user.setSpreadTime(DateUtil.nowDateTime());

                    // 处理新旧推广人数据
                    userService.updateSpreadCountByUid(userSpr.getUid(), "add");
                }

                //更新用户信息
                userService.updateById(user);
            }
        }
    }

    @Override
    public LoginResponse iosAccountLogin(String data) {
        //实例化-登录响应对象
        LoginResponse loginResponse = new LoginResponse();
        try {
            //解析参数
            JSONObject userInfo = JSONObject.parseObject(data);

            //验证-关键参数非空
            if (userInfo == null || userInfo.get("openId") == null) {
                throw new CrmebException("ios账户授权登录失败！data数据为空！");
            }

            //取出openid
            String openid =userInfo.get("openId").toString();

            //验证-分享人id
            Object spreadUidObject=userInfo.get("spreadUid");
            Integer spreadUid=0;
            if(spreadUidObject == null && "".equals(spreadUidObject))spreadUid=Integer.valueOf(spreadUidObject.toString());

            //检测-token是否存在
            UserToken userToken = userTokenService.getByOpenidAndType(openid, Constants.THIRD_LOGIN_TOKEN_TYPE_IOS);
            if (ObjectUtil.isNotNull(userToken)) {// 已存在，正常登录
                User user = userService.getById(userToken.getUid());

                //验证-状态
                if (!user.getStatus()) {
                    throw new CrmebException("当前账户已禁用，请联系管理员！");
                }

                //记录最后一次登录时间
                user.setLastLoginTime(DateUtil.nowDateTime());

                //执行操作
                Integer finalSpreadUid = spreadUid;
                Boolean execute = transactionTemplate.execute(e -> {
                    // 分销绑定
                    if (userService.checkBingSpread(user, finalSpreadUid, "old")) {
                        user.setSpreadUid(finalSpreadUid);
                        user.setSpreadTime(DateUtil.nowDateTime());

                        // 处理新旧推广人数据
                        userService.updateSpreadCountByUid(finalSpreadUid, "add");
                    }

                    //更新用户信息
                    userService.updateById(user);
                    return Boolean.TRUE;
                });

                //验证-执行结果
                if (!execute) {
                    logger.error(StrUtil.format("ios账户授权登录绑定分销关系失败，uid={},spreadUid={}", user.getUid(), spreadUid));
                }

                //生成token
                try {
                    String token = userService.token(user);
                    loginResponse.setToken(token);
                } catch (Exception e) {
                    logger.error(StrUtil.format("ios账户授权登录生成token失败，uid={}", user.getUid()));
                    e.printStackTrace();
                }

                //赋值-并返回
                loginResponse.setType("login");
                loginResponse.setUid(user.getUid());
                loginResponse.setNikeName(user.getNickname());
                loginResponse.setPhone(user.getPhone());
                return loginResponse;
            }else{
                //没有用户-走创建用户流程
                WeChatAuthorizeLoginUserInfoResponse wcaluir = JSONObject.parseObject(userInfo.toJSONString(), WeChatAuthorizeLoginUserInfoResponse.class);
                RegisterThirdUserRequest registerThirdUserRequest = new RegisterThirdUserRequest();
                BeanUtils.copyProperties(wcaluir, registerThirdUserRequest);

                // 其他参数赋值
                registerThirdUserRequest.setSex(String.valueOf(userInfo.get("sex")==null?0:userInfo.get("sex")));
                registerThirdUserRequest.setType(String.valueOf(userInfo.get("type")));
                registerThirdUserRequest.setHeadimgurl(String.valueOf(userInfo.get("avatarUrl")));
                registerThirdUserRequest.setSpreadPid(spreadUid);
                registerThirdUserRequest.setOpenId(openid);
                registerThirdUserRequest.setNickName("九秒中用户:"+DateUtil.nowDateTime(Constants.DATE_TIME_FORMAT_NUM));

                // 不存在，通过用户信息注册
                User user = userService.registerByThird(registerThirdUserRequest);
                user.setPhone("123456");
                user.setAccount(CommonUtil.createNickName(""));
                user.setSpreadUid(0);
                user.setPwd(CommonUtil.createPwd("123456"));

                // 执行
                Boolean execute = transactionTemplate.execute(e -> {
                    // 保存用户信息
                    userService.save(user);

                    // 根据用户类型-绑定token
                    userTokenService.bind(registerThirdUserRequest.getOpenId(), Constants.THIRD_LOGIN_TOKEN_TYPE_IOS, user.getUid());

                    //返回执行结果
                    return Boolean.TRUE;
                });

                //验证-执行结果
                if (!execute) {
                    logger.error("ios用户注册生成失败，nickName = " + registerThirdUserRequest.getNickName());
                }

                //生成token
                String token = userService.token(user);
                loginResponse.setToken(token);

                //赋值其他参数
                loginResponse.setType("login");
                loginResponse.setUid(user.getUid());
                loginResponse.setNikeName(user.getNickname());
                loginResponse.setPhone(user.getPhone());

                //返回
                return loginResponse;
            }
        } catch (Exception e) {
            throw new  CrmebException("ios账户授权登录失败！"+e.getMessage());
        }
    }

    /**
     * 账号密码登录
     * @return LoginResponse
     */
    @Override
    public LoginResponse login(LoginRequest loginRequest) throws Exception {
        //根据手机号码得到用户
        User user = userService.getByPhone(loginRequest.getPhone());
        //验证
        if (ObjectUtil.isNull(user)) {
            throw new CrmebException("此账号未注册");
        }
        if (!user.getStatus()) {
            throw new CrmebException("此账号被禁用");
        }

        // 校验密码
        String password = CrmebUtil.encryptPassword(loginRequest.getPassword(), loginRequest.getPhone());
        if (!user.getPwd().equals(password)) {
            throw new CrmebException("密码错误");
        }

        //绑定推广关系
        if (loginRequest.getSpreadPid() > 0) {
            bindSpread(user, loginRequest.getSpreadPid());
        }

        // 记录最后一次登录时间
        user.setLastLoginTime(DateUtil.nowDateTime());
        userService.updateById(user);

        //返回响应对象
        return getLoginResponse(user);
    }

    /**
     * 得到登录响应对象
     * @param user  用户信息
     * @return
     */
    public LoginResponse getLoginResponse(User user) throws Exception {
        //实例化登录响应对象
        LoginResponse loginResponse = new LoginResponse();
        //设置参数
        String token = userService.token(user);
        loginResponse.setToken(token);
        loginResponse.setUid(user.getUid());
        loginResponse.setNikeName(user.getNickname());
        loginResponse.setPhone(user.getPhone());

        //返回
        return loginResponse;
    }

    /**
     * 手机号验证码登录
     * @param loginRequest 登录请求信息
     * @return LoginResponse
     */
    @Override
    public LoginResponse phoneLogin(LoginMobileRequest loginRequest) throws Exception {
        //检测验证码
        checkValidateCode(loginRequest.getPhone(), loginRequest.getCaptcha());
        Integer spreadPid = Optional.ofNullable(loginRequest.getSpreadPid()).orElse(0);

        //查询手机号信息
        User user = userService.getByPhone(loginRequest.getPhone());
        if (ObjectUtil.isNull(user)) {
            // 此用户不存在，走新用户注册流程
            user = userService.registerPhone(loginRequest.getPhone(), spreadPid);
        } else {
            //验证状态
            if (!user.getStatus()) {
                throw new CrmebException("当前账户已禁用，请联系管理员！");
            }

            //验证关系
            if (user.getSpreadUid().equals(0) && spreadPid > 0) {
                // 绑定推广关系
                bindSpread(user, spreadPid);
            }

            // 记录最后一次登录时间
            user.setLastLoginTime(DateUtil.nowDateTime());
            boolean b = userService.updateById(user);
            if (!b) {
                logger.error("用户登录时，记录最后一次登录时间出错,uid = " + user.getUid());
            }
        }

        //返回响应对象
        return getLoginResponse(user);
    }

    /**
     * 检测手机验证码
     * @param phone 手机号
     * @param code  验证码
     */
    private void checkValidateCode(String phone, String code) {
        Object validateCode = redisUtil.get(SmsConstants.SMS_VALIDATE_PHONE + phone);
        if (ObjectUtil.isNull(validateCode)) {
            throw new CrmebException("验证码已过期");
        }
        if (!validateCode.toString().equals(code)) {
            throw new CrmebException("验证码错误");
        }
        //删除验证码
        redisUtil.remove(SmsConstants.SMS_VALIDATE_PHONE + phone);
    }

    /**
     * 绑定分销关系
     * @param user      User 用户user类
     * @param spreadUid Integer 推广人id
     * @return Boolean
     * 1.判断分销功能是否启用
     * 2.判断分销模式
     * 3.根据不同的分销模式校验
     * 4.指定分销，只有分销员才可以分销，需要spreadUid是推广员才可以绑定
     * 5.满额分销，同上
     * 6.人人分销，可以直接绑定
     */
    @Override
    public Boolean bindSpread(User user, Integer spreadUid) {
        //检测是否可以绑定
        Boolean checkBingSpread = userService.checkBingSpread(user, spreadUid, "old");
        if (!checkBingSpread) return false;

        //是，赋值参数
        user.setSpreadUid(spreadUid);
        user.setSpreadTime(DateUtil.nowDateTime());
        //2021.7.15-新增path-推广等级路径
        User spUser=userService.getById(spreadUid);//设置path
        user.setPath(new StringBuffer(spUser.getPath()).append(spreadUid).append("/").toString());

        //执行结果
        Boolean execute = transactionTemplate.execute(e -> {
            userService.updateById(user);
            userService.updateSpreadCountByUid(spreadUid, "add");
            return Boolean.TRUE;
        });

        //验证执行结果并返回
        if (!execute) {
            logger.error(StrUtil.format("绑定推广人时出错，userUid = {}, spreadUid = {}", user.getUid(), spreadUid));
        }
        return execute;
    }
}
