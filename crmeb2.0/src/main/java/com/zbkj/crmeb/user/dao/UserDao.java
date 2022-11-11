package com.zbkj.crmeb.user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zbkj.crmeb.front.response.UserSpreadPeopleItemResponse;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.vo.UserOperateFundsVo;

import java.util.List;
import java.util.Map;


/**
 * 用户表 Mapper 接口
 * @author: 零风
 * @CreateDate: 2022/6/21 13:17
 */
public interface UserDao extends BaseMapper<User> {
    Boolean updateFounds(UserOperateFundsVo userOperateFundsVo);

    List<UserSpreadPeopleItemResponse> getSpreadPeopleList(Map<String, Object> map);

    List<User> findAdminList(Map<String, Object> map);
}
