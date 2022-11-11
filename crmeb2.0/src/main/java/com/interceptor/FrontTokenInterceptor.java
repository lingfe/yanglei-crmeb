package com.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.common.CheckFrontToken;
import com.common.CommonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
* 移动端-token验证拦截器
* 使用前注意需要一个@Bean手动注解，否则注入无效
* @author: 零风
* @CreateDate: 2021/9/18 15:33
*/
public class FrontTokenInterceptor implements HandlerInterceptor {

    @Autowired
    private CheckFrontToken checkFrontToken;

    /**
     * 程序处理之前-需要处理的业务
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //设置响应编码格式
        response.setCharacterEncoding("UTF-8");
        //取出token
        String token = checkFrontToken.getTokenFormRequest(request);

        //检测token-是否可以访问
        Boolean result = checkFrontToken.check(token, request);
        if(!result){
            //否，不能访问，返回提示:未登录或token过期
            response.getWriter().write(JSONObject.toJSONString(CommonResult.unauthorized()));
            return false;
        }else{
            return true;
        }
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) { }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) { }

}
