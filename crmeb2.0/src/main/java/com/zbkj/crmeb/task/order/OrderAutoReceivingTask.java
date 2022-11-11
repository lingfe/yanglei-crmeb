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
 * 订单自动收货task
 * @author: 零风
 * @CreateDate: 2021/12/20 14:19
 */
//@Component
//@Configuration //读取配置
//@EnableScheduling // 2.开启定时任务
public class OrderAutoReceivingTask {
    //日志
    private static final Logger logger = LoggerFactory.getLogger(OrderAutoReceivingTask.class);

    @Autowired
    private OrderTaskService orderTaskService;

    //@Scheduled(fixedDelay = 1000 * 60L) //1分钟同步一次数据
    //@Scheduled(cron = "0 0 1 ? * *") // 每天01:00分执行
    public void init(){
        logger.info("---订单自动收货task-每天01:00分执行。OrderAutoReceivingTask.java,Time - {}", DateUtil.nowDateTime());
        try {
            orderTaskService.autoReceiving();
        }catch (Exception e){
            e.printStackTrace();
            logger.error("订单自动收货task.task" + " | msg : " + e.getMessage());
        }
    }
}
