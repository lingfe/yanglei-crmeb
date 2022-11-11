package com.zbkj.crmeb.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.crmeb.user.dao.UserSignSuppDao;
import com.zbkj.crmeb.user.model.UserSignSupp;
import com.zbkj.crmeb.user.service.UserSignSuppService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @program: crmeb
 * @description: 签到补签service接口实现类
 * @author: 零风
 * @create: 2021-06-28 15:22
 **/
@Service
public class UserSignSuppServiceImpl extends ServiceImpl<UserSignSuppDao, UserSignSupp> implements UserSignSuppService {

    @Resource
    private UserSignSuppDao dao;

}
