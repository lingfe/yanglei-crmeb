package com.zbkj.crmeb.task.order;

import com.utils.DateUtil;
import com.zbkj.crmeb.store.service.OrderTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
* 订单退款后续处理task
* @author: 零风
* @CreateDate: 2021/12/20 10:20
*/
//@Component
//@Configuration //读取配置
//@EnableScheduling // 2.开启定时任务
public class OrderRefundTask {
    //日志
    private static final Logger logger = LoggerFactory.getLogger(OrderRefundTask.class);

    @Autowired
    private OrderTaskService orderTaskService;

   // @Scheduled(fixedDelay = 1000 * 60L) //1分钟同步一次数据
    public void init(){
        logger.info("---订单退款后续处理task------一分钟执行一次task: OrderRefundTask.java Time - {}", DateUtil.nowDateTime());
        try {
            orderTaskService.refundApply();
        }catch (Exception e){
            e.printStackTrace();
            logger.error("订单退款后续处理task.task" + " | msg : " + e.getMessage());
        }

    }
}
