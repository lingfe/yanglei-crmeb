package com.filter;


import com.utils.RequestUtil;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;


/**
 * 返回值输出过滤器
 * @author: 零风
 * @CreateDate: 2022/1/10 10:16
 */
@Component
public class ResponseFilter implements Filter {

    private String[] excludedUris;

    @Override
    public void init(FilterConfig config) throws ServletException {
        excludedUris = config.getInitParameter("notice").split(",");
    }

    /**
     * 不过滤指定包含路径
     * @Author 零风
     * @Date  2022/3/3
     * @return 是否
     */
    private boolean isExcludedUri(String uri) {
        if (excludedUris == null || excludedUris.length <= 0) {
            return false;
        }
        for (String ex : excludedUris) {
            uri = uri.trim();
            ex = ex.trim();
            if (uri.indexOf(ex)!=-1) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        //验证-是否需要拦截
        HttpServletRequest  test=(HttpServletRequest)request;
        //String url=test.getRequestURI();
        String path= test.getServletPath();
        if(isExcludedUri(path)){
            filterChain.doFilter(request, response);
        } else {
            //将-response-转换成代理类
            ResponseWrapper wrapperResponse = new ResponseWrapper((HttpServletResponse) response);
            // 这里只拦截返回，直接让请求过去，如果在请求前有处理，可以在这里处理
            filterChain.doFilter(request, wrapperResponse);
            byte[] content =  wrapperResponse.getContent();//获取返回值

            //判断是否有值
            if (content.length > 0) {
                String str = new String(content, StandardCharsets.UTF_8);
                // String str = new String(content,StandardCharsets.ISO_8859_1);
                try {
                    HttpServletRequest req = (HttpServletRequest) request;
                    str = new ResponseRouter().filter(str, RequestUtil.getUri(req));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //把返回值输出到客户端
                ServletOutputStream outputStream = response.getOutputStream();
                if (str.length() > 0) {
                    outputStream.write(str.getBytes());
                    outputStream.flush();
                    outputStream.close();
                    //最后添加这一句，输出到客户端
                    response.flushBuffer();
                }
            }
        }
    }
}
