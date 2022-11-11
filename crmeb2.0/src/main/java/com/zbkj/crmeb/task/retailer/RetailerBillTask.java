package com.zbkj.crmeb.task.retailer;

import com.utils.DateUtil;
import com.zbkj.crmeb.retailer.service.RetailerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
* 零售商账单-生成-定时任务
* @author: 零风
* @CreateDate: 2021/12/15 16:48
*/
//@Component
//@Configuration //读取配置
//@EnableScheduling // 2.开启定时任务
public class RetailerBillTask {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(RetailerBillTask.class);

    @Autowired
    private RetailerService retailerService;
    //@Scheduled(fixedDelay = 1000 * 60L) //1分钟同步一次数据
    //@Scheduled(cron = "0 10 3 ? * *") // 每天03：10 分 执行
    //@Scheduled(cron = "0 */1 * * * ?") //每分钟执行一次
    public void init(){
        logger.info("----零售商账单-生成-定时任务-----每天凌晨01:00执行: Execution Time - {}", DateUtil.nowDateTime());
        try {
            retailerService.exeRetailerBillTask();
        }catch (Exception e){
            e.printStackTrace();
            logger.error("零售商账单-生成-定时任务-执行结果" + " | msg : " + e.getMessage());
        }

    }
}
