package com.zbkj.crmeb.authorization.model;

import com.constants.Constants;
import com.utils.CrmebUtil;
import com.zbkj.crmeb.system.model.SystemAdmin;
import lombok.Data;

import java.io.Serializable;
import java.util.Locale;

/**
 * token实体类
 */
@Data
public class TokenModel implements Serializable {
    private static final long serialVersionUID = 4903514237492573024L;

    // 用户号
    private String userNo;
    private Integer userId;
    private String token;

    // 最后访问时间
    private long lastAccessedTime = System.currentTimeMillis();
    // 过期时间
    private long expirationTime;
    // 客户端类型
    private String clienttype;
    // 客户端语言
    private Locale locale;
    // 客户端ip
    private String host;
    // 当前登录用户信息
    private SystemAdmin systemAdmin;

    public String getAuthorization() throws Exception {
        return CrmebUtil.encryptPassword(userNo+"_"+ token, Constants.TOKEN_KEY);
    }

    public TokenModel(String userno, String token){
        this.userNo = userno;
        this.token = token;
    }

    public TokenModel() {
    }
}
