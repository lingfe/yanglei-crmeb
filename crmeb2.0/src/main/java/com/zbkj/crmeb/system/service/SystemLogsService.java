package com.zbkj.crmeb.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.system.model.SystemLogs;
import com.zbkj.crmeb.system.request.SystemLogsSearchRequest;

/**
 * 系统日志-service层接口
 * @author: 零风
 * @CreateDate: 2022/4/13 11:13
 */
public interface SystemLogsService extends IService<SystemLogs> {

    /**
     * 分页系统日志数据
     * @param request
     * @param pageParamRequest
     * @return
     */
    PageInfo<SystemLogs> getPageList(SystemLogsSearchRequest request, PageParamRequest pageParamRequest);



}
