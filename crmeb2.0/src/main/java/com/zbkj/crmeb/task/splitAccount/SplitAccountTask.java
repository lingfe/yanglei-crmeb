package com.zbkj.crmeb.task.splitAccount;

import com.utils.DateUtil;
import com.zbkj.crmeb.finance.service.SplitAccountRecordService;
import com.zbkj.crmeb.store.service.StoreMakeAnAppointmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 分账
 * @author: 零风
 * @CreateDate: 2022/1/21 14:19
 */
//@Component
//@Configuration //读取配置
//@EnableScheduling // 2.开启定时任务
public class SplitAccountTask {

    private static final Logger logger = LoggerFactory.getLogger(SplitAccountTask.class);

    @Autowired
    private SplitAccountRecordService splitAccountService;

    @Autowired
    private StoreMakeAnAppointmentService storeMakeAnAppointmentService;

    //@Scheduled(fixedDelay = 1000 * 2L) //5s同步一次数据
    //@Scheduled(cron = "0 30 3 ? * *") // 每天03：30 分 执行
    public void init(){
        logger.info("分账-每天凌晨3点半同步一次数据。SplitAccountTask.java Time - {}", DateUtil.nowDateTime());
        splitAccountService.payProfitsharingTask();
        logger.info("商城预约数据处理-每天凌晨3点半处理数据。SplitAccountTask.java Time - {}", DateUtil.nowDateTime());
        storeMakeAnAppointmentService.maaHandleTask();
    }
}
