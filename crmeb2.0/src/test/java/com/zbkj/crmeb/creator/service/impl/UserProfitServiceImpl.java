package com.zbkj.crmeb.creator.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.utils.DateUtil;
import com.utils.vo.dateLimitUtilVo;
import com.zbkj.crmeb.creator.dao.UserProfitDao;
import com.zbkj.crmeb.creator.model.UserProfit;
import com.zbkj.crmeb.creator.service.UserProfitService;
import com.zbkj.crmeb.pub.model.PublicTableField;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * 创作者用户记录表-service层接口实现类
 * @author: 零风
 * @CreateDate: 2022/7/29 10:54
 */
@Service
public class UserProfitServiceImpl extends ServiceImpl<UserProfitDao, UserProfit> implements UserProfitService {

    @Resource
    private UserProfitDao dao;

    @Override
    public BigDecimal getProfitStatistics(Integer uid, String date) {
        LambdaQueryWrapper<UserProfit> lqw=new LambdaQueryWrapper<>();
        lqw.eq(UserProfit::getUid,uid);
        if(null != date){
            dateLimitUtilVo dateLimit = DateUtil.getDateLimit(date);
            lqw.between(PublicTableField::getCreateTime, dateLimit.getStartTime(), dateLimit.getEndTime());
        }
        List<UserProfit> userBills = dao.selectList(lqw);
        if (CollUtil.isEmpty(userBills))  return BigDecimal.ZERO;
        BigDecimal profit=userBills.stream().map(UserProfit::getProfit).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, BigDecimal.ROUND_DOWN);
        return profit;
    }

}
