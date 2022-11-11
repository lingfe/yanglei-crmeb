package com.zbkj.crmeb.task.integral;

import com.utils.DateUtil;
import com.zbkj.crmeb.integal.service.PublicIntegalRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 更新用户积分(消费有推广模式)task
 * @author: 零风
 * @CreateDate: 2021/10/19 13:53
 */
//@Component
//@Configuration //读取配置
//@EnableScheduling // 2.开启定时任务
public class UpdateUserIntegralTask {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(UpdateUserIntegralTask.class);

    @Autowired
    private PublicIntegalRecordService publicIntegalRecordService;

    //@Scheduled(fixedDelay = 1000 * 60L) //1分钟同步一次数据
    //@Scheduled(cron = "0 10 3 ? * *") // 每天03：10 分 执行
    public void init(){
        logger.info("---更新用户积分(消费有推广模式)task-每天03：10 分 执行。UpdateUserIntegralTask.java,Time - {}", DateUtil.nowDateTime());
        try {
            publicIntegalRecordService.updateUserIntegal();
        }catch (Exception e){
            e.printStackTrace();
            logger.error("更新用户积分(消费有推广模式).task" + " | msg : " + e.getMessage());
        }
    }

}
