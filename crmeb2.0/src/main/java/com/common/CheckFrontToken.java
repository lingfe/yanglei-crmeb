package com.common;

import com.constants.Constants;
import com.utils.RedisUtil;
import com.utils.RequestUtil;
import com.utils.ThreadLocalUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
* 前端-用户token、检查、验证、读取等相关类
* @author: 零风
* @Version: 1.0
* @CreateDate: 2021/9/18 14:59
* @return： CheckFrontToken.java
**/
@Component
public class CheckFrontToken {

    @Autowired
    protected RedisUtil redisUtil;

    /**
     * 检测token是否存在于缓存或数据库中，
     * 不存在则判断路由是否通过。
     * @param token
     * @param request
     * @return
     */
    public Boolean check(String token, HttpServletRequest request){
        try {
            //验证token非空
            if(token == null || token.isEmpty()){
                return this.checkRouter(RequestUtil.getUri(request));
            }

            //验证是否存在值
            boolean exists = redisUtil.exists(Constants.USER_TOKEN_REDIS_KEY_PREFIX + token);
            if(exists){
                //存在值，取出值
                Object value = redisUtil.get(Constants.USER_TOKEN_REDIS_KEY_PREFIX + token);

                //更新-过期时间
                Map<String, Object> hashedMap = new HashMap<>();
                hashedMap.put("id", value);
                ThreadLocalUtil.set(hashedMap);
                redisUtil.set(Constants.USER_TOKEN_REDIS_KEY_PREFIX +token, value, Constants.TOKEN_EXPRESS_MINUTES, TimeUnit.MINUTES);
            }else{
                //不存在值
                //判断路由，部分路由不管用户是否登录/token是否过期都可以访问
                exists = this.checkRouter(RequestUtil.getUri(request));
            }
            return exists;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * 验证路由,
     * 路由在此处-则返回true，无论用户是否登录都可以访问
     * @Author lingfe
     * @Date  2021/9/18
     **/
    public boolean checkRouter(String uri) {
        String[] routerList = {
                "api/front/coupons",
                "api/front/index",
                "api/front/index/product",
                "api/front/index/wear",
                "api/front/bargain/index",
                "api/front/bargain/list",
                "api/front/combination/index",
                "api/front/combination/list",
                "api/front/product/detail",
                "api/front/product/good",
                "api/front/brands/getCateIdList",
                "api/front/brands/get/getBrandsPreferredInfo",
                "api/front/userLogin",
                "api/front/login/app/iosAccountLogin",
                "api/front/creator/index/indexData",
                "api/front/creator/index/indexSearch",
                "api/front/creator/index/infoCreatorHome",
                "api/front/creator/index/whereCategoryScreenCreatorWorksList",
                "api/front/creator/index/download",
                "api/front/creator/index/attid",
                "api/front/creator/index/login",
        };
        return ArrayUtils.contains(routerList, uri);
    }

    /**
     * 取出请求中的token
     * @param request
     * @return
     */
    public String getTokenFormRequest(HttpServletRequest request){
        return request.getHeader(Constants.HEADER_AUTHORIZATION_KEY);
    }
}
