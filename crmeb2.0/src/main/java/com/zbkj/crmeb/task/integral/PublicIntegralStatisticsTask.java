package com.zbkj.crmeb.task.integral;

import com.utils.DateUtil;
import com.zbkj.crmeb.integal.service.PublicIntegalRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 公共积分记录-统计task
 * @author: 零风
 * @CreateDate: 2021/10/18 15:28
 */
//@Component
//@Configuration //读取配置
//@EnableScheduling // 2.开启定时任务
public class PublicIntegralStatisticsTask {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(PublicIntegralStatisticsTask.class);

    @Autowired
    private PublicIntegalRecordService publicIntegalRecordService;

    //@Scheduled(fixedDelay = 1000 * 60L) //1分钟同步一次数据
    //@Scheduled(cron = "0 10 3 ? * *") // 每天03：10 分 执行
    public void init(){
        logger.info("---公共积分记录统计task-每天03:10分执行-已关闭。PublicIntegralStatisticsTask.java,Time - {}", DateUtil.nowDateTime());
        try {
            //publicIntegalRecordService.publicIntegralStatistics();
            //publicIntegalRecordService.publicIntegralStatisticsRA();
        }catch (Exception e){
            e.printStackTrace();
            logger.error("公共积分记录统计.task" + " | msg : " + e.getMessage());
        }
    }

}
