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
* 用户取消订单task任务
* @author: 零风
* @CreateDate: 2021/12/20 10:18
*/
//@Component
//@Configuration //读取配置
//@EnableScheduling // 2.开启定时任务
public class OrderCancelTask {
    //日志
    private static final Logger logger = LoggerFactory.getLogger(OrderCancelTask.class);

    @Autowired
    private OrderTaskService orderTaskService;

    //@Scheduled(fixedDelay = 1000 * 60L) //1分钟同步一次数据
    public void init(){
        logger.info("---用户取消订单task任务------一分钟执行一次task: Execution Time - {}", DateUtil.nowDateTime());
        try {
            orderTaskService.cancelByUser();
        }catch (Exception e){
            e.printStackTrace();
            logger.error("用户取消订单task任务.task" + " | msg : " + e.getMessage());
        }

    }
}
