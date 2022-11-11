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
 * 订单收货之后相关业务处理Task
 * 订单收货之后-处理订单状态日志、佣金、公共积分等相关业务
 * @author: 零风
 * @CreateDate: 2021/12/20 10:20
*/
//@Component
//@Configuration //读取配置
//@EnableScheduling // 2.开启定时任务
public class OrderReceiptTask {
    //日志
    private static final Logger logger = LoggerFactory.getLogger(OrderReceiptTask.class);

    @Autowired
    private OrderTaskService orderTaskService;

    //@Scheduled(fixedDelay = 1000 * 60L) //1分钟同步一次数据
    public void init(){
        logger.info("---订单收货之后相关业务处理Task------1分钟同步一次数据task: OrderReceiptTask.java Time - {}", DateUtil.nowDateTime());
        try {
            orderTaskService.orderReceiving();
        }catch (Exception e){
            e.printStackTrace();
            logger.error("订单收货之后相关业务处理Task.task" + " | msg : " + e.getMessage());
        }

    }
}
