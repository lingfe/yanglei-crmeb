package com.zbkj.crmeb.config;

import com.filter.ResponseFilter;
import com.interceptor.AdminAuthInterceptor;
import com.interceptor.AdminTokenInterceptor;
import com.interceptor.FrontTokenInterceptor;
import com.interceptor.SwaggerInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.MappedInterceptor;

/**
 * token验证拦截器
 * @author: 零风
 * @CreateDate: 2022/1/10 10:37
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    // 这里使用一个Bean为的是可以在拦截器中自由注入，也可以在拦截器中使用SpringUtil.getBean 获取
    // 但是觉得这样更优雅
    @Bean
    public HandlerInterceptor adminAuthInterceptor(){ return new AdminAuthInterceptor(); }

    @Bean
    public HandlerInterceptor adminTokenInterceptor(){
        return new AdminTokenInterceptor();
    }

    @Bean
    public HandlerInterceptor frontTokenInterceptor(){
        return new FrontTokenInterceptor();
    }

    @Bean
    public ResponseFilter responseFilter(){ return new ResponseFilter(); }

    @Value("${swagger.basic.username}")
    private String username;
    @Value("${swagger.basic.password}")
    private String password;
    @Value("${swagger.basic.check}")
    private Boolean check;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //添加token拦截器
        //addPathPatterns添加需要拦截的命名空间；
        //excludePathPatterns添加排除拦截命名空间

        //后台token拦截
        registry.addInterceptor(adminTokenInterceptor()).
                addPathPatterns("/api/admin/**").
                excludePathPatterns("/api/admin/validate/**").
                excludePathPatterns("/api/admin/login").
                excludePathPatterns("/api/admin/logout").
                excludePathPatterns("/api/admin/getLoginPic").
                excludePathPatterns("/api/admin/wechat/config").
                excludePathPatterns("/api/admin/authorize/login").
                excludePathPatterns("/api/admin/payment/callback/**").
                excludePathPatterns("/api/admin/system/role/info").
                excludePathPatterns("/swagger-resources/**", "/webjars/**", "/v2/**", "/swagger-ui.html/**");

        //后台权限规则
        registry.addInterceptor(adminAuthInterceptor()).
                addPathPatterns("/api/admin/**").
                excludePathPatterns("/api/admin/validate/**").
                excludePathPatterns("/api/admin/login").
                excludePathPatterns("/api/admin/logout").
                excludePathPatterns("/api/admin/getLoginPic").
                excludePathPatterns("/api/admin/payment/callback/**").
                excludePathPatterns("/swagger-resources/**", "/webjars/**", "/v2/**", "/swagger-ui.html/**");

        //前端用户登录token
        registry.addInterceptor(frontTokenInterceptor()).
                addPathPatterns("/api/front/**").
                excludePathPatterns("/api/front/qrcode/**").
                excludePathPatterns("/api/front/login/mobile").
                excludePathPatterns("/api/front/zijie/login").
                excludePathPatterns("/api/front/login").
                excludePathPatterns("/api/front/sendCode").
                excludePathPatterns("/api/front/wechat/**").
                excludePathPatterns("/api/front/search/keyword").
                excludePathPatterns("/api/front/share").
                excludePathPatterns("/api/front/article/**").
                excludePathPatterns("/api/front/city/**").
                excludePathPatterns("/api/front/product/hot").
                excludePathPatterns("/api/front/products/**").
                excludePathPatterns("/api/front/reply/**").
                excludePathPatterns("/api/front/user/service/**").
                excludePathPatterns("/api/front/logistics").
                excludePathPatterns("/api/front/groom/list/**").
                excludePathPatterns("/api/front/config").
                excludePathPatterns("/api/front/category").
                excludePathPatterns("/api/front/seckill/list/*").
                excludePathPatterns("/api/front/ios/*").
                excludePathPatterns("/api/front/ios/register/binding/phone").
                excludePathPatterns("api/front/combination/index").
                excludePathPatterns("api/front/seckill/index").
                excludePathPatterns("api/front/bargain/index").
                excludePathPatterns("api/front/combination/index").
                excludePathPatterns("api/front/index/product/*").
                excludePathPatterns("api/front/userLogin").
                excludePathPatterns("api/front/login/app/iosAccountLogin").
                excludePathPatterns("api/front/product/good").
                excludePathPatterns("/swagger-resources/**", "/webjars/**", "/v2/**", "/swagger-ui.html/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Bean
    public FilterRegistrationBean filterRegister() {
        //注册过滤器
        FilterRegistrationBean registration = new FilterRegistrationBean(responseFilter());
        registration.addUrlPatterns("/*");
        registration.addInitParameter("notice","noF,*/excel/*");
        return registration;
    }

    /* 必须在此处配置拦截器,要不然拦不到swagger的静态资源 */
    @Bean
    @ConditionalOnProperty(name = "swagger.basic.enable", havingValue = "true")
    public MappedInterceptor getMappedInterceptor() {
        return new MappedInterceptor(new String[]{"/doc.html", "/webjars/**"}, new SwaggerInterceptor(username, password, check));
    }

}
