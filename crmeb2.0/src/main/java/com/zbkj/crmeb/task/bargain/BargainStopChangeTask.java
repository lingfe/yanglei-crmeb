package com.zbkj.crmeb.task.bargain;

import com.utils.DateUtil;
import com.zbkj.crmeb.bargain.service.StoreBargainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 砍价活动结束状态变化定时任务
 * @author: 零风
 * @CreateDate: 2022/3/23 10:32
 */
//@Component
//@Configuration //读取配置
//@EnableScheduling // 2.开启定时任务
public class BargainStopChangeTask {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(BargainStopChangeTask.class);

    @Autowired
    private StoreBargainService storeBargainService;

    //@Scheduled(cron = "0 0 0 */1 * ?") //每天0点执行
    public void init(){
        logger.info("---BargainStopChangeTask------bargain stop status change task: Execution Time - {}", DateUtil.nowDateTime());
        try {
            storeBargainService.stopAfterChange();
        }catch (Exception e){
            e.printStackTrace();
            logger.error("BargainStopChangeTask" + " | msg : " + e.getMessage());
        }

    }

}
