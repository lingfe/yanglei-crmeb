package com.zbkj.crmeb.task.cloud;

import com.utils.DateUtil;
import com.zbkj.crmeb.system.service.SystemAttachmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 云服务同步任务
 * @author: 零风
 * @CreateDate: 2022/5/30 18:09
 */
//@Component
//@Configuration //读取配置
//@EnableScheduling // 2.开启定时任务
public class AsyncCloud {
    //日志
    private static final Logger logger = LoggerFactory.getLogger(AsyncCloud.class);

    @Autowired
    private SystemAttachmentService systemAttachmentService;

    //@Scheduled(fixedDelay = 1000 * 5L) //5秒钟同步一次数据
    public void init(){
        logger.info("---AsyncCloud task------produce Data with fixed rate task: Execution Time - {}", DateUtil.nowDateTime());
        try {
            systemAttachmentService.async();
        }catch (Exception e){
            e.printStackTrace();
            logger.error("AsyncCloud.task" + " | msg : " + e.getMessage());
        }

    }
}
