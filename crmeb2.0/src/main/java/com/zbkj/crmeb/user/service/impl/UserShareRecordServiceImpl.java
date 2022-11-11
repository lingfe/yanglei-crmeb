package com.zbkj.crmeb.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.crmeb.user.dao.UserShareRecordDao;
import com.zbkj.crmeb.user.model.UserShareRecord;
import com.zbkj.crmeb.user.service.UserShareRecordService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @program: crmeb
 * @description: 用户分享记录表Service层实现类
 * @author: 零风
 * @create: 2021-06-29 11:10
 **/
@Service
public class UserShareRecordServiceImpl extends ServiceImpl<UserShareRecordDao, UserShareRecord> implements UserShareRecordService {

    @Resource
    private UserShareRecordDao dao;


}
