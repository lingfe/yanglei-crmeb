package com.zbkj.crmeb.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.zbkj.crmeb.express.vo.ExpressSheetVo;
import com.zbkj.crmeb.system.model.SystemConfig;
import com.zbkj.crmeb.system.request.SystemFormCheckRequest;

import java.util.HashMap;
import java.util.List;

/**
 * SystemConfigService-接口
 * @author: 零风
 * @CreateDate: 2022/1/19 14:11
 */
public interface SystemConfigService extends IService<SystemConfig> {

    List<SystemConfig> getList(PageParamRequest pageParamRequest);

    /**
     * 根据name-获取value(读取配置字段)
     * @Author 零风
     * @Date  2021/10/13
     * @return 值
     */
    String getValueByKey(String key);

    /**
     * 同时获取多个配置
     * @param keys 多个配置key
     * @return 查询到的多个结果
     */
    List<String> getValuesByKes(List<String> keys);

    boolean updateOrSaveValueByName(String name, String value);

    /**
     * 根据字段名称key，取出值
     * @param key   字段名称
     * @return  字段值
     */
    String getValueByKeyException(String key);

    boolean saveForm(SystemFormCheckRequest systemFormCheckRequest);

    HashMap<String, String> info(Integer formId);

    boolean checkName(String name);

    /**
     * 获取面单默认配置信息
     * @return
     */
    ExpressSheetVo getDeliveryInfo();
}
