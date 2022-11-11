package com.zbkj.crmeb.task.sms;

import com.utils.DateUtil;
import com.zbkj.crmeb.sms.service.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 发送短信
 * @author: 零风
 * @CreateDate: 2022/2/7 11:04
 */
//@Component
//@Configuration //读取配置
//@EnableScheduling // 2.开启定时任务
public class AsyncSmsSend {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(AsyncSmsSend.class);

    @Autowired
    private SmsService smsService;

    //@Scheduled(fixedDelay = 1000 * 5L) //5秒同步一次数据
    public void init(){
        try {
            logger.info("--- 发送短信，5秒同步一次数据。AsyncSmsSend.java,Time - {}", DateUtil.nowDate());
            smsService.consumeTask();
        }catch (Exception e){
            e.printStackTrace();
            logger.error("发送短信 - AsyncSmsSend.task" + " 发生错误 | msg : " + e.getMessage());
        }

    }
}
