package com.zbkj.crmeb.task.brokerage;

import com.utils.DateUtil;
import com.zbkj.crmeb.user.service.UserBrokerageRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 佣金分红task
 * @author: 零风
 * @CreateDate: 2022/4/6 10:23
 */
//@Component
//@Configuration //读取配置
//@EnableScheduling // 2.开启定时任务
public class BrokerageABonusTask {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(BrokerageABonusTask.class);

    @Autowired
    private UserBrokerageRecordService userBrokerageRecordService;

    //@Scheduled(cron = "0 0 1 15 * ?") //每个月15号凌晨1点同步一次数据-https://www.cnblogs.com/achengmu/p/12450772.html
    public void init(){
        logger.info("---佣金分红task-每个月15号凌晨1点同步一次数据。 BrokerageABonusTask.java Time - {}", DateUtil.nowDateTime());
        try {
            userBrokerageRecordService.brokerageABonusTask();
        }catch (Exception e){
            logger.error("佣金分红task-每个月15号凌晨1点同步一次数据.task" + " | msg : " + e.getMessage());
        }
    }

}
