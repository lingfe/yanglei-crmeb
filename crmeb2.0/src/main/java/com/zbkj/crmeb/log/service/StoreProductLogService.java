package com.zbkj.crmeb.log.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.log.model.StoreProductLog;
import com.zbkj.crmeb.log.response.StoreProductLogResponse;

import java.util.List;

/**
 * 商品日志表-service层接口
 * @author: 零风
 * @CreateDate: 2022/6/10 9:21
 */
public interface StoreProductLogService extends IService<StoreProductLog> {

    /**
     * 根据商品id-查询商品日志
     * @param productId
     * @return
     */
    List<StoreProductLogResponse> getListWhereProductId(Integer productId);

    Integer getCountByTimeAndType(String time, String type);

    /**
     * 添加商品日志记录(定时任务)
     * @Author 零风
     * @Date  2022/2/7
     */
    void addLogTask();

}