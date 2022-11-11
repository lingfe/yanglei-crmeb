package com.zbkj.crmeb.task.log;

import com.utils.DateUtil;
import com.zbkj.crmeb.log.service.StoreProductLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 商品日志记录task
 * @author: 零风
 * @CreateDate: 2022/2/7 11:16
 */
//@Component
//@Configuration //读取配置
//@EnableScheduling // 2.开启定时任务
public class ProductLogAddTask {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(ProductLogAddTask.class);

    @Autowired
    private StoreProductLogService logService;

    //@Scheduled(fixedDelay = 1000 * 60L) // 一分钟同步一次数据
    public void init(){
        try {
            logger.info("---商品日志记录task------ 一分钟同步一次数据 ProductLogAddTask.java : Execution Time - {}", DateUtil.nowDate());
            logService.addLogTask();
        }catch (Exception e){
            e.printStackTrace();
            logger.error("---商品日志记录task ---" + " 发生错误 | msg : " + e.getMessage());
        }

    }

}
