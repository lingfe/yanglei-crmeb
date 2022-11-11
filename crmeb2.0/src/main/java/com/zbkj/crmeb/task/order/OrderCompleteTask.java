package com.zbkj.crmeb.task.order;

import com.utils.DateUtil;
import com.zbkj.crmeb.store.service.OrderTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 订单完成之后相关业务处理task任务
 * -自动更新订单状态日志
 * -保存可分配积分用户
 * @author: 零风
 * @CreateDate: 2021/12/20 10:19
*/
//@Component
//@Configuration //读取配置
//@EnableScheduling // 2.开启定时任务
public class OrderCompleteTask {
    //日志
    private static final Logger logger = LoggerFactory.getLogger(OrderCompleteTask.class);

    @Autowired
    private OrderTaskService orderTaskService;

    //@Scheduled(fixedDelay = 1000 * 10L) //10秒同步一次数据
    public void init(){
        logger.info("---订单完成之后相关业务处理task任务------10s执行一次task: OrderCompleteTask.java Time - {}", DateUtil.nowDateTime());
        try {
            orderTaskService.complete();
        }catch (Exception e){
            e.printStackTrace();
            logger.error("订单完成之后相关业务处理task任务.task" + " | msg : " + e.getMessage());
        }

    }
}
