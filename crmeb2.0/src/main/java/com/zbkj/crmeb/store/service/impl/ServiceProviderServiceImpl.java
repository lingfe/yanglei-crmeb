package com.zbkj.crmeb.store.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CommonPage;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.exception.CrmebException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.store.dao.ServiceProviderDao;
import com.zbkj.crmeb.store.model.ServiceProvider;
import com.zbkj.crmeb.store.model.ServiceProviderTwolevel;
import com.zbkj.crmeb.store.request.ServiceProviderSearchRequest;
import com.zbkj.crmeb.store.request.ServiceProviderTwolevelSearchRequest;
import com.zbkj.crmeb.store.response.ServiceProviderDataResponse;
import com.zbkj.crmeb.store.service.ServiceProviderService;
import com.zbkj.crmeb.store.service.ServiceProviderTwolevelService;
import com.zbkj.crmeb.store.service.StoreOrderService;
import com.zbkj.crmeb.system.model.SystemAdmin;
import com.zbkj.crmeb.system.service.SystemAdminService;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * 服务商表-service层接口-实现类
 * @author: 零风
 * @CreateDate: 2022/5/9 14:42
 */
@Service
public class ServiceProviderServiceImpl extends ServiceImpl<ServiceProviderDao, ServiceProvider> implements ServiceProviderService {

    @Resource
    private ServiceProviderDao dao;

    @Autowired
    private SystemAdminService systemAdminService;

    @Autowired
    private UserService userService;

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private ServiceProviderTwolevelService serviceProviderTwolevelService;

    @Override
    public boolean deleteTwelevelProvider(Integer id) {
        //得到-当前登录管理员信息
        SystemAdmin admin=systemAdminService.getInfo();
        ServiceProvider serviceProvider=dao.selectById(admin.getSpId());
        if(serviceProvider == null )throw new CrmebException("服务商已不存在！");
        ServiceProviderTwolevel serviceProviderTwolevel=serviceProviderTwolevelService.getById(id);
        if(serviceProviderTwolevel == null)throw new CrmebException("二级商户已不存在！");
        if(!serviceProviderTwolevel.getSpId().equals(serviceProvider.getId()))throw new CrmebException("无操作权限！当前二级商户不属于该服务商！");
        return serviceProviderTwolevelService.removeById(id);
    }

    @Override
    public boolean saveOrUpdateTwolevel(ServiceProviderTwolevel serviceProviderTwolevel) {
        //得到-当前登录管理员信息
        SystemAdmin admin=systemAdminService.getInfo();
        serviceProviderTwolevel.setSpId(admin.getSpId());
        return serviceProviderTwolevelService.saveServiceProviderTwolevel(serviceProviderTwolevel);
    }

    @Override
    public PageInfo<ServiceProviderTwolevel> getPageTwolevelList(ServiceProviderTwolevelSearchRequest request,PageParamRequest pageParamRequest) {
        if(request.getSpId()>0){
            return serviceProviderTwolevelService.getPageList(request,pageParamRequest);
        }else{
            //得到-当前登录管理员信息
            SystemAdmin admin=systemAdminService.getInfo();
            request.setSpId(admin.getSpId());
            return serviceProviderTwolevelService.getPageList(request,pageParamRequest);
        }
    }

    @Override
    public ServiceProviderDataResponse data(Integer id) {
        //过渡变量
        Integer idType=3;
        Integer value=id;

        //验证非空
        if(value == null || value.equals(0)){
            //得到-当前登录管理者信息
            SystemAdmin admin = systemAdminService.getInfo();
            value = admin.getSpId();
        }

        //得到-服务商信息
        ServiceProvider serviceProvider = dao.selectById(value);
        if(serviceProvider == null)throw new CrmebException("服务商信息不存在！");

        //得到-用户会员总数
        LambdaQueryWrapper<User> userLambdaQueryWrapper=new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getSpId,value);
        Integer userTotalNum=userService.count(userLambdaQueryWrapper);

        //得到-统计数据
        ServiceProviderDataResponse response=this.getServiceProviderDataResponse(idType, value);
        response.setUserTotalNum(userTotalNum);
        response.setServiceProvider(serviceProvider);
        return response;
    }

    @Override
    public ServiceProviderDataResponse getServiceProviderDataResponse(Integer type, Integer id) {
        //得到-订单数据
        Integer dayOrderNum = storeOrderService.getOrderCount(type, id,Constants.SEARCH_DATE_DAY);
        Integer yesterdayOrderNum = storeOrderService.getOrderCount(type, id,Constants.SEARCH_DATE_YESTERDAY);
        Integer thisMonthOrderNum = storeOrderService.getOrderCount(type, id,Constants.SEARCH_DATE_MONTH);
        Integer orderTotalNum = storeOrderService.getOrderCount(type, id,null);

        //得到-交易额
        BigDecimal  dayGmv=storeOrderService.getSumPayPriceByUidAndDate(type, Constants.SEARCH_DATE_DAY, id);
        BigDecimal  yesterdayGmv=storeOrderService.getSumPayPriceByUidAndDate(type, Constants.SEARCH_DATE_YESTERDAY, id);
        BigDecimal  thisMonthGmv=storeOrderService.getSumPayPriceByUidAndDate(type, Constants.SEARCH_DATE_MONTH, id);
        BigDecimal  totalGmv=storeOrderService.getSumPayPriceByUidAndDate(type, null, id);

        //实例化响应对象
        ServiceProviderDataResponse response=new ServiceProviderDataResponse();

        //赋值-订单相关
        response.setDayOrderNum(dayOrderNum);
        response.setYesterdayOrderNum(yesterdayOrderNum);
        response.setThisMonthOrderNum(thisMonthOrderNum);
        response.setOrderTotalNum(orderTotalNum);

        //赋值-交易相关
        response.setDayGmv(dayGmv);
        response.setYesterdayGmv(yesterdayGmv);
        response.setThisMonthGmv(thisMonthGmv);
        response.setTotalGmv(totalGmv);

        //返回
        return response;
    }

    @Override
    public PageInfo<User> getPageUserList(Integer typeId,Integer value,PageParamRequest pageParamRequest) {
        //验证非空
        if(value == null || value.equals(0)){
            //验证-ID标识类型
            SystemAdmin admin=systemAdminService.getInfo();
            if(admin == null) throw new CrmebException("管理员账户不存在！");
            switch (typeId){
                case 1:value=admin.getSpId();break;
                case 2:value=admin.getSptlId();break;
                default:break;
            }

            //验证非空
            if(value == null || value.equals(0)){
                return new PageInfo<>();
            }
        }

        //得到分页对象
        Page<User> sbPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //得到lambda对象，用作筛选条件
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getSpId,value);

        //排序条件，根据sort以及创建时间排序
        lambdaQueryWrapper.orderByDesc(User::getUid).orderByDesc(User::getCreateTime);
        List<User> sbList = userService.list(lambdaQueryWrapper);

        //返回数据
        return CommonPage.copyPageInfo(sbPage, sbList);
    }

    @Override
    public PageInfo<ServiceProvider> getPageList(ServiceProviderSearchRequest request, PageParamRequest pageParamRequest) {
        //得到分页对象
        Page<ServiceProvider> sbPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //得到lambda对象，用作筛选条件
        LambdaQueryWrapper<ServiceProvider> lambdaQueryWrapper = Wrappers.lambdaQuery();

        //条件-关键字
        if(StringUtils.isNotBlank(request.getKeywords())){
            lambdaQueryWrapper.like(ServiceProvider::getServiceName, request.getKeywords());
        }

        //排序条件，根据sort以及创建时间排序
        lambdaQueryWrapper.orderByDesc(ServiceProvider::getSort).orderByDesc(ServiceProvider::getCreateTime);
        List<ServiceProvider> sbList = dao.selectList(lambdaQueryWrapper);

        //返回数据
        return CommonPage.copyPageInfo(sbPage, sbList);
    }

    @Override
    public Boolean saveServiceProvider(ServiceProvider serviceProvider) {
        //验证-服务商名称
        LambdaQueryWrapper<ServiceProvider> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ServiceProvider::getServiceName,serviceProvider.getServiceName());

        //验证ID
        if(serviceProvider.getId()>0){
            if(dao.selectList(queryWrapper).size()>1)throw new CrmebException("服务商名称已被使用！换一个吧！");
        }else{
            if(dao.selectList(queryWrapper).size()>0)throw new CrmebException("服务商名称已被使用！");
        }

        //执行保存
        return this.saveOrUpdate(serviceProvider);
    }
}
