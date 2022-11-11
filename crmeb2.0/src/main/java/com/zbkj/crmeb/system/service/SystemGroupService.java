package com.zbkj.crmeb.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.zbkj.crmeb.system.model.SystemGroup;
import com.zbkj.crmeb.system.request.SystemGroupSearchRequest;

import java.util.List;

public interface SystemGroupService extends IService<SystemGroup> {

    /**
     * 分页显示组合数据
     * @param request 请求参数
     * @param pageParamRequest 分页类参数
     * @Author 零风
     * @Date  2022/6/9 10:43
     * @return
     */
    List<SystemGroup> getList(SystemGroupSearchRequest request, PageParamRequest pageParamRequest);
}
