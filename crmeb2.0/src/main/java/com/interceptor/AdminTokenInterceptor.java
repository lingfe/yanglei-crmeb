package com.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.common.CheckAdminToken;
import com.common.CommonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * PC管理端-token验证拦截器
 * 使用前注意需要一个@Bean手动注解，否则注入无效
 * @author: 零风
 * @CreateDate: 2022/1/10 10:34
 */
public class AdminTokenInterceptor implements HandlerInterceptor {

    @Autowired
    private CheckAdminToken checkAdminToken;

    //程序处理之前需要处理的业务
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        response.setCharacterEncoding("UTF-8");
        String token = checkAdminToken.getTokenFormRequest(request);
        if(token == null || token.isEmpty()){
            response.getWriter().write(JSONObject.toJSONString(CommonResult.unauthorized()));
            return false;
        }

        //检测token是否过期
        Boolean result = checkAdminToken.check(token);
        if(!result){
            response.getWriter().write(JSONObject.toJSONString(CommonResult.unauthorized()));
            return false;
        }
        return true;
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) { }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) { }

}
