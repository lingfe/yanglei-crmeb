package com.interceptor;

import org.apache.commons.codec.binary.Base64;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Swagger-文档-拦截器
 * @author: 零风
 * @CreateDate: 2022/1/10 10:40
 */
public class SwaggerInterceptor extends HandlerInterceptorAdapter {
    private String username;
    private String password;
    private Boolean check;

    public SwaggerInterceptor(String username, String password, Boolean check) {
        this.username = username;
        this.password = password;
        this.check = check;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authorization = request.getHeader("Authorization");
        boolean isAuthSuccess = httpBasicAuth(authorization);
        if (!isAuthSuccess) {
            response.setCharacterEncoding("utf-8");
            response.setStatus(401);
//            response.setStatus(401,"Unauthorized");
            response.setHeader("WWW-authenticate", "Basic realm=\"Realm\"");
            try (PrintWriter writer = response.getWriter()) {
                writer.print("Forbidden, unauthorized user");
            }
        }
        return isAuthSuccess;
    }

    public boolean httpBasicAuth(String authorization) throws IOException {
        if(check){
            if (authorization != null && authorization.split(" ").length == 2) {
                /**
                 * 2021-6-25
                 * https://blog.csdn.net/cool__7/article/details/80972972
                 * https://blog.csdn.net/u014594604/article/details/86499213
                 * /D:/lingfe/gitee/shop_sys/crmeb/crmeb_java/crmeb/src/main/java/com/interceptor/SwaggerInterceptor.java:[9,16] sun.misc.BASE64Decoder是内部专用 API, 可能会在未来发行版中删除
                 * /D:/lingfe/gitee/shop_sys/crmeb/crmeb_java/crmeb/src/main/java/com/interceptor/SwaggerInterceptor.java:[55,53] sun.misc.BASE64Decoder是内部专用 API, 可能会在未来发行版中删除
                 */
                //String userAndPass = new String(new BASE64Decoder().decodeBuffer(authorization.split(" ")[1]));
                String userAndPass = new String(new Base64().decode(authorization.split(" ")[1]));
                String username = userAndPass.split(":").length == 2 ? userAndPass.split(":")[0] : null;
                String password = userAndPass.split(":").length == 2 ? userAndPass.split(":")[1] : null;
                return this.username.equals(username) && this.password.equals(password);
            }
            return false;
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String uri = request.getRequestURI();
        AntPathMatcher pathMatcher = new AntPathMatcher();
        if (!pathMatcher.match("/swagger-ui.html", uri) && !pathMatcher.match("/webjars/**", uri)) {
            response.setStatus(404);
            return;
        }
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:/META-INF/resources" + uri);
        if (resources.length > 0) {
            FileCopyUtils.copy(resources[0].getInputStream(), response.getOutputStream());
        } else {
            response.setStatus(404);
        }
    }
}
