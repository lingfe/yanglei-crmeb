package com.zbkj.crmeb.log.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.CommonPage;
import com.common.PageParamRequest;
import com.zbkj.crmeb.log.model.EbVersionLog;
import com.zbkj.crmeb.log.request.EbVersionLogSearchRequest;

/**
 * 版本控制-server层接口
 */
public interface EbVersionLogService extends IService<EbVersionLog> {

    /**
     * 同步日志
     * @return
     */
    Boolean synchronizationLog();

    /**
     * 分页查询-版本日志记录list
     * @param request             请求对象
     * @param pageParamRequest    分页对象
     * @return
     */
    CommonPage<EbVersionLog> getPageList(EbVersionLogSearchRequest request, PageParamRequest pageParamRequest);

}
