package com.zbkj.crmeb.regionalAgency.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.crmeb.regionalAgency.dao.RegionalUserDao;
import com.zbkj.crmeb.regionalAgency.model.RegionalUser;
import com.zbkj.crmeb.regionalAgency.service.RegionalUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 区域用户表-service层接口-实现类
 * @author: 零风
 * @CreateDate: 2021/11/9 14:57
 */
@Service
public class RegionalUserServiceImpl extends ServiceImpl<RegionalUserDao, RegionalUser> implements RegionalUserService {

    @Resource
    private RegionalUserDao dao;




}
