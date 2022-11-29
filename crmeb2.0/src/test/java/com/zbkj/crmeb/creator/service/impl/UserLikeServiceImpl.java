package com.zbkj.crmeb.creator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.crmeb.creator.dao.UserLikeDao;
import com.zbkj.crmeb.creator.model.UserLike;
import com.zbkj.crmeb.creator.service.UserLikeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 用户点赞记录表-service层接口实现类
 * @author: 零风
 * @CreateDate: 2022/7/12 10:54
 */
@Service
public class UserLikeServiceImpl extends ServiceImpl<UserLikeDao, UserLike> implements UserLikeService {

    @Resource
    private UserLikeDao dao;

}
