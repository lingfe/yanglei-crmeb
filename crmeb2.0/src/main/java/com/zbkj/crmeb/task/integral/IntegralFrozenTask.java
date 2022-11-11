package com.zbkj.crmeb.task.integral;


import com.utils.DateUtil;
import com.zbkj.crmeb.user.service.UserIntegralRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 积分冻结期解冻task
 * -包含用户积分记录解冻、公共积分记录解冻
 * @author: 零风
 * @CreateDate: 2022/2/23 10:55
 */
//@Component
//@Configuration //读取配置
//@EnableScheduling // 2.开启定时任务
public class IntegralFrozenTask {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(IntegralFrozenTask.class);

    @Autowired
    private UserIntegralRecordService userIntegralRecordService;

    //@Scheduled(fixedDelay = 1000 * 60L) //1分钟同步一次数据
    public void init(){
        logger.info("---积分冻结期解冻task-1分钟同步一次数据。IntegralFrozenTask.java,Time - {}", DateUtil.nowDateTime());
        try {
            userIntegralRecordService.integralThawTask();
        }catch (Exception e){
            e.printStackTrace();
            logger.error("IntegralFrozenTask.task" + " | msg : " + e.getMessage());
        }
    }
}
