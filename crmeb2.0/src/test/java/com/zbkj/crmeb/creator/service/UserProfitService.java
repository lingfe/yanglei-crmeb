package com.zbkj.crmeb.creator.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.creator.model.UserProfit;

import java.math.BigDecimal;

/**
 * 创作者用户收益记录表-service层接口
 * @author: 零风
 * @CreateDate: 2022/7/29 10:53
 */
public interface UserProfitService extends IService<UserProfit> {

    /**
     * 收益统计
     * @Author 零风
     * @Date  2022/7/29 11:34
     * @return
     */
    BigDecimal getProfitStatistics(Integer uid, String date);

}
