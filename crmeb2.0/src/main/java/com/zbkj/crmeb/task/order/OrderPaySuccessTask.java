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
 * 订单支付成功之后相关业务处理task任务
 * -更新订单日志、支付记录、经验值、公共积分、零售商订单处理、账单记录、佣金记录等
 * @author: 零风
 * @CreateDate: 2021/12/20 10:19
*/
//@Component
//@Configuration //读取配置
//@EnableScheduling // 2.开启定时任务
public class OrderPaySuccessTask {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(OrderPaySuccessTask.class);

    @Autowired
    private OrderTaskService orderTaskService;

    //@Scheduled(fixedDelay = 1000 * 5L) // 5秒同步一次数据
    public void init(){
        logger.info("---订单支付成功之后相关业务处理task任务------5秒同步一次数据: OrderPaySuccessTask.java Time - {}", DateUtil.nowDateTime());
        try {
            orderTaskService.orderPaySuccessAfter();
        }catch (Exception e){
            e.printStackTrace();
            logger.error("订单支付成功之后相关业务处理task任务.task" + " | msg : " + e.getMessage());
        }
    }
}
