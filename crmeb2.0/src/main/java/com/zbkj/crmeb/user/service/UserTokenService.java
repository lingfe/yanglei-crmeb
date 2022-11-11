package com.zbkj.crmeb.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.user.model.UserToken;

import java.util.List;

/**
 * UserTokenService
 * @author: 零风
 * @CreateDate: 2022/5/10 9:51
 */
public interface UserTokenService extends IService<UserToken> {

    /**
     * 检测token是否存在
     * @param token String openId
     * @param type int 类型
     * @author Mr.Zhang
     * @since 2020-05-25
     * @return UserToken
     */
    UserToken getByOpenidAndType(String token, int type);

    void bind(String openId, int type, Integer uid);

    UserToken getTokenByUserId(Integer userId, int type);

    List<UserToken> getList(List<Integer> userIdList);

    UserToken getByUid(Integer uid);
}