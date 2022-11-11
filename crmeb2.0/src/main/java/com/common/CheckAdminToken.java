package com.common;

import com.constants.Constants;
import com.utils.RedisUtil;
import com.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
* 后端(管理端)-用户token、检查、验证、读取等相关类
* @author: 零风
* @CreateDate: 2021/9/18 15:38
*/
@Component
public class CheckAdminToken {

    @Autowired
    protected RedisUtil redisUtil;

    /**
     * 检测-后端账号-token是否过期
     * @param token
     * @return
     */
    public Boolean check(String token){
        try {
            //验证-redis中是否存在-该token
            boolean exists = redisUtil.exists(Constants.TOKEN_REDIS + token);
            if(exists){
                //是-取出token
                Object value = redisUtil.get(Constants.TOKEN_REDIS + token);
                Map<String, Object> hashedMap = new HashMap<>();
                hashedMap.put("id", value);
                ThreadLocalUtil.set(hashedMap);

                //查询设置-更新过期时间
                redisUtil.set(Constants.TOKEN_REDIS +token, value, Constants.TOKEN_EXPRESS_MINUTES, TimeUnit.MINUTES);
            }
            return exists;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * 取出-请求或参数中的token
     * @param request   请求
     * @return  token字符串
     */
    public String getTokenFormRequest(HttpServletRequest request){
        String pathToken =request.getParameter(Constants.HEADER_AUTHORIZATION_KEY);
        if(null != pathToken){
            return pathToken;
        }
        return request.getHeader(Constants.HEADER_AUTHORIZATION_KEY);
    }

    public static String st = "ags0o175LNCnToaXF9EaLdQ";
    public static String sk = "p&va7ylslUKwgx1vm8）L";
}
