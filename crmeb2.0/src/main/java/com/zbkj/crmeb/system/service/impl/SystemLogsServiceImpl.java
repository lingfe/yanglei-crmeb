package com.zbkj.crmeb.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CommonPage;
import com.common.PageParamRequest;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.system.dao.SystemLogsDao;
import com.zbkj.crmeb.system.model.SystemLogs;
import com.zbkj.crmeb.system.request.SystemLogsSearchRequest;
import com.zbkj.crmeb.system.service.SystemLogsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 系统日志-service层接口实现类
 * @author: 零风
 * @CreateDate: 2022/4/13 11:15
 */
@Service
public class SystemLogsServiceImpl extends ServiceImpl<SystemLogsDao, SystemLogs> implements SystemLogsService {

    @Resource
    private SystemLogsDao dao;

    @Override
    public PageInfo<SystemLogs> getPageList(SystemLogsSearchRequest request, PageParamRequest pageParamRequest) {
        //得到分页对象
        Page<SystemLogs> sbPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //得到lambda对象，用作筛选条件
        LambdaQueryWrapper<SystemLogs> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.select(SystemLogs::getId,SystemLogs::getCreateTime,
                SystemLogs::getUid,SystemLogs::getLogType,
                SystemLogs::getOperationType,SystemLogs::getIp,
                SystemLogs::getAdminId,SystemLogs::getDeviceName,SystemLogs::getUrl,SystemLogs::getUserAgent);

        //条件-日志类型
        if(request.getLogType() > 0){
            lambdaQueryWrapper.eq(SystemLogs::getLogType, request.getLogType());
        }

        //条件-操作类型
        if(StringUtils.isNotBlank(request.getOperationType())){
            lambdaQueryWrapper.eq(SystemLogs::getOperationType, request.getOperationType());
        }

        //条件-关键字
        if(StringUtils.isNotBlank(request.getKeywords())){
            lambdaQueryWrapper.like(SystemLogs::getUrl, request.getKeywords());
        }

        //排序条件，根据sort以及创建时间排序
        lambdaQueryWrapper.orderByDesc(SystemLogs::getId);
        List<SystemLogs> sbList = dao.selectList(lambdaQueryWrapper);

        //返回数据
        return CommonPage.copyPageInfo(sbPage, sbList);
    }
}
