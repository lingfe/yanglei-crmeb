package com.zbkj.crmeb.store.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CommonPage;
import com.common.PageParamRequest;
import com.exception.CrmebException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.store.dao.ServiceProviderTwolevelDao;
import com.zbkj.crmeb.store.model.ServiceProviderTwolevel;
import com.zbkj.crmeb.store.request.ServiceProviderTwolevelSearchRequest;
import com.zbkj.crmeb.store.response.ServiceProviderDataResponse;
import com.zbkj.crmeb.store.service.ServiceProviderService;
import com.zbkj.crmeb.store.service.ServiceProviderTwolevelService;
import com.zbkj.crmeb.system.model.SystemAdmin;
import com.zbkj.crmeb.system.service.SystemAdminService;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 服务商二级商户表-service层接口-实现类
 * @author: 零风
 * @CreateDate: 2022/5/9 14:42
 */
@Service
public class ServiceProviderTwolevelServiceImpl extends ServiceImpl<ServiceProviderTwolevelDao, ServiceProviderTwolevel> implements ServiceProviderTwolevelService {

    @Resource
    private ServiceProviderTwolevelDao dao;

    @Autowired
    private SystemAdminService systemAdminService;

    @Autowired
    private UserService userService;

    @Autowired
    private ServiceProviderService serviceProviderService;

    @Override
    public ServiceProviderDataResponse data(Integer id) {
        //过渡变量
        Integer idType=4;
        Integer value=id;

        //验证非空
        if(value == null || value.equals(0)){
            //得到-当前登录管理者信息
            SystemAdmin admin = systemAdminService.getInfo();
            value = admin.getSptlId();
        }

        //得到-服务商信息
        ServiceProviderTwolevel serviceProviderTwolevel = dao.selectById(value);
        if(serviceProviderTwolevel == null)throw new CrmebException("二级商户信息不存在！");

        //得到-用户会员总数
        LambdaQueryWrapper<User> userLambdaQueryWrapper=new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getSpId,value);
        Integer userTotalNum=userService.count(userLambdaQueryWrapper);

        //得到-统计数据
        ServiceProviderDataResponse response=serviceProviderService.getServiceProviderDataResponse(idType, value);
        response.setUserTotalNum(userTotalNum);
        response.setServiceProviderTwolevel(serviceProviderTwolevel);
        return response;
    }

    @Override
    public PageInfo<ServiceProviderTwolevel> getPageList(ServiceProviderTwolevelSearchRequest request, PageParamRequest pageParamRequest) {
        //得到分页对象
        Page<ServiceProviderTwolevel> sbPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //得到lambda对象，用作筛选条件
        LambdaQueryWrapper<ServiceProviderTwolevel> lambdaQueryWrapper = Wrappers.lambdaQuery();

        //条件-服务商ID
        if(request.getSpId() > 0){
            lambdaQueryWrapper.eq(ServiceProviderTwolevel::getSpId,request.getSpId());
        }

        //条件-关键字
        if(StringUtils.isNotBlank(request.getKeywords())){
            lambdaQueryWrapper.like(ServiceProviderTwolevel::getSptlName, request.getKeywords());
        }

        //排序条件，根据sort以及创建时间排序
        lambdaQueryWrapper.orderByDesc(ServiceProviderTwolevel::getSort).orderByDesc(ServiceProviderTwolevel::getCreateTime);
        List<ServiceProviderTwolevel> sbList = dao.selectList(lambdaQueryWrapper);

        //返回数据
        return CommonPage.copyPageInfo(sbPage, sbList);
    }

    @Override
    public Boolean saveServiceProviderTwolevel(ServiceProviderTwolevel serviceProviderTwolevel) {
        //验证-名称
        LambdaQueryWrapper<ServiceProviderTwolevel> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ServiceProviderTwolevel::getSptlName,serviceProviderTwolevel.getSptlName());
        if(dao.selectList(queryWrapper).size()>0)throw new CrmebException("二级商户名称已被使用！");

        //执行保存
        return this.saveOrUpdate(serviceProviderTwolevel);
    }
}
