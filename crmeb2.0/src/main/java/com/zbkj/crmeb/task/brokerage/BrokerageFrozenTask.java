package com.zbkj.crmeb.task.brokerage;


import com.utils.DateUtil;
import com.zbkj.crmeb.task.order.OrderReceiptTask;
import com.zbkj.crmeb.user.service.UserBrokerageRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 佣金冻结期解冻task
 * @author: 零风
 * @CreateDate: 2021/12/24 11:01
 */
//@Component
//@Configuration //读取配置
//@EnableScheduling // 2.开启定时任务
public class BrokerageFrozenTask {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(OrderReceiptTask.class);

    @Autowired
    private UserBrokerageRecordService userBrokerageRecordService;

    //@Scheduled(fixedDelay = 1000 * 5L) //1分钟同步一次数据
    //@Scheduled(fixedDelay = 1000 * 60L) //1小时同步一次数据
    public void init(){
        logger.info("---佣金冻结期解冻task，5s同步一次数据。 BrokerageFrozenTask.java Time - {}", DateUtil.nowDateTime());
        try {
            userBrokerageRecordService.brokerageThaw();
        }catch (Exception e){
            e.printStackTrace();
            logger.error("佣金冻结期解冻task.task" + " | msg : " + e.getMessage());
        }
    }
}