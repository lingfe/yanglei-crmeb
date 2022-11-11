package com.zbkj.crmeb.log.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CommonPage;
import com.common.PageParamRequest;
import com.constants.ConstantsFromID;
import com.exception.CrmebException;
import com.github.pagehelper.PageHelper;
import com.zbkj.crmeb.log.dao.EbVersionLogDao;
import com.zbkj.crmeb.log.model.EbVersionLog;
import com.zbkj.crmeb.log.request.EbVersionLogSearchRequest;
import com.zbkj.crmeb.log.service.EbVersionLogService;
import com.zbkj.crmeb.system.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 版本控制-service接口实现类
 * @author: 零风
 * @CreateDate: 2021/9/27 15:34
 */
@Service
public class EbVersionLogServiceImpl extends ServiceImpl<EbVersionLogDao, EbVersionLog> implements EbVersionLogService {

    @Resource
    private EbVersionLogDao dao;

    @Autowired
    private SystemConfigService systemConfigService;

    @Override
    public Boolean synchronizationLog() {
        //得到表单数据-并验证非空
        Map<String,String> mapSysForm = systemConfigService.info(ConstantsFromID.INT_VERSION_LOG);
        if(mapSysForm == null){
            throw new CrmebException("获取表单数据失败! 未对应表单id:"+ConstantsFromID.INT_VERSION_LOG);
        }

        //根据版本号验证-是否已同步
        EbVersionLogSearchRequest eb = new EbVersionLogSearchRequest();
        eb.setVersion(eb.getVersion());
        eb.setPtype(eb.getPtype());
        CommonPage<EbVersionLog>  pageList =  this.getPageList(eb,new PageParamRequest());
        if(pageList!=null && pageList.getTotal() > 0){
            throw new CrmebException("已同步！");
        }

        //实例化-版本日志对象
        EbVersionLog ebVersionLog=EbVersionLog.builder()
                .ptype(1)
                .version(mapSysForm.get("version"))
                .updateContent(mapSysForm.get("updateContent"))
                .createTime(new Date())
                .build();

        //执行保存-同步
        return this.save(ebVersionLog);
    }

    @Override
    public CommonPage<EbVersionLog> getPageList(EbVersionLogSearchRequest request, PageParamRequest pageParamRequest) {
        //分页和查询对象
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<EbVersionLog> lambdaQueryWrapper = Wrappers.lambdaQuery();

        //验证条件-非空
        if(request == null)request= new EbVersionLogSearchRequest();

        //条件-类型
        if(request.getPtype() !=null){
            lambdaQueryWrapper.eq(EbVersionLog::getPtype,request.getPtype());
        }

        //条件-版本号
        if(StringUtils.isNotBlank(request.getVersion())){
           lambdaQueryWrapper.eq(EbVersionLog::getVersion,request.getVersion());
        }

        //得到数据
        List<EbVersionLog> list=dao.selectList(lambdaQueryWrapper);

        //返回
        return CommonPage.restPage(list);
    }

}
