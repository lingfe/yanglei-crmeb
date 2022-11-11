package com.zbkj.crmeb.store.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CommonPage;
import com.common.PageParamRequest;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.store.dao.StoreMakeAnAppointmentDao;
import com.zbkj.crmeb.store.model.StoreMakeAnAppointment;
import com.zbkj.crmeb.store.request.StoreMakeAnAppointmentSearchRequest;
import com.zbkj.crmeb.store.service.StoreMakeAnAppointmentService;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class StoreMakeAnAppointmentServiceImpl  extends ServiceImpl<StoreMakeAnAppointmentDao, StoreMakeAnAppointment>
        implements StoreMakeAnAppointmentService {

    @Resource
    private StoreMakeAnAppointmentDao dao;

    @Autowired
    private UserService userService;

    @Override
    public boolean saveMaa(Integer linkId) {
        StoreMakeAnAppointment storeMakeAnAppointment=new StoreMakeAnAppointment();
        storeMakeAnAppointment.setUid(userService.getUserIdException());
        storeMakeAnAppointment.setLinkId(linkId);
        storeMakeAnAppointment.setLinkType(0);
        return dao.insert(storeMakeAnAppointment)>0;
    }

    @Override
    public PageInfo<StoreMakeAnAppointment> getPageList(StoreMakeAnAppointmentSearchRequest request, PageParamRequest pageParamRequest,Boolean isAll) {
        //得到当前登录对象
        User user=userService.getInfoException();

        //得到分页对象
        Page<StoreMakeAnAppointment> sbPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //得到lambda对象，用作筛选条件
        LambdaQueryWrapper<StoreMakeAnAppointment> lambdaQueryWrapper = Wrappers.lambdaQuery();

        //验证是否查询全部
        if(!isAll){
            lambdaQueryWrapper.eq(StoreMakeAnAppointment::getUid,user.getUid());
        }

        //条件-预约结果
        if(request.getIsResult()!=null&&request.getIsResult()){
            lambdaQueryWrapper.eq(StoreMakeAnAppointment::getIsResult,Boolean.TRUE);
        }

        //条件-预约状态
        if(request.getStatus()!=null&&request.getStatus()>-1){
            lambdaQueryWrapper.eq(StoreMakeAnAppointment::getStatus,request.getStatus());
        }

        //排序条件，根据sort以及创建时间排序
        lambdaQueryWrapper.orderByDesc(StoreMakeAnAppointment::getSort).orderByDesc(StoreMakeAnAppointment::getCreateTime);
        List<StoreMakeAnAppointment> sbList = dao.selectList(lambdaQueryWrapper);

        //返回数据
        return CommonPage.copyPageInfo(sbPage, sbList);
    }

    @Override
    public void maaHandleTask() {
        //得到lambda对象，用作筛选条件
        //排序条件，根据sort以及创建时间排序
        LambdaQueryWrapper<StoreMakeAnAppointment> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.in(StoreMakeAnAppointment::getStatus,0,1);
        lambdaQueryWrapper.orderByDesc(StoreMakeAnAppointment::getSort).orderByDesc(StoreMakeAnAppointment::getCreateTime);
        List<StoreMakeAnAppointment> list= dao.selectList(lambdaQueryWrapper);
        for (StoreMakeAnAppointment maa:list) {
            if(maa.getStatus() == 0){
                maa.setStatus(2);
            }else{
                maa.setStatus(3);
            }
            dao.updateById(maa);
        }
    }

}
