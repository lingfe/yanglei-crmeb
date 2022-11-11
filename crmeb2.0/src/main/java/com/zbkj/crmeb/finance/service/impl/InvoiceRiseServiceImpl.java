package com.zbkj.crmeb.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CommonPage;
import com.common.PageParamRequest;
import com.exception.CrmebException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.finance.dao.InvoiceRiseDao;
import com.zbkj.crmeb.finance.model.InvoiceRise;
import com.zbkj.crmeb.finance.request.InvoiceRiseSearchRequest;
import com.zbkj.crmeb.finance.response.InvoiceRiseResponse;
import com.zbkj.crmeb.finance.service.InvoiceRiseService;
import com.zbkj.crmeb.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class InvoiceRiseServiceImpl  extends ServiceImpl<InvoiceRiseDao, InvoiceRise> implements InvoiceRiseService {

    private static final Logger logger = LoggerFactory.getLogger(InvoiceRiseServiceImpl.class);

    @Resource
    private InvoiceRiseDao dao;

    @Autowired
    private UserService userService;

    @Override
    public InvoiceRiseResponse info(Integer id) {
        //得到发票信息
        InvoiceRise invoiceRise = dao.selectById(id);
        if(invoiceRise == null) throw new CrmebException("发票抬头信息不存在！");

        //转换
        InvoiceRiseResponse response=new InvoiceRiseResponse();
        BeanUtils.copyProperties(invoiceRise,response);//转换

        //验证发票抬头类型
        switch (invoiceRise.getRiseType()){
            case 1: response.setRiseTypeStr("个人");break;
            case 2: response.setRiseTypeStr("企业");break;
            default:response.setRiseTypeStr("未知");
        }

        //验证发票类型
        switch (invoiceRise.getInvoiceType()){
            case 1: response.setInvoiceTypeStr("增值税电子普通发票");break;
            case 2: response.setInvoiceTypeStr("增值税电子专用发票"); break;
            default:response.setInvoiceTypeStr("未知");;
        }

        //验证默认
        if(response.getIsDefault()){
            response.setIsDefaultStr("是");
        }else{
            response.setIsDefaultStr("否");
        }

        //返回
        return response;
    }

    @Override
    public boolean sou(InvoiceRise invoiceRise) {
        //得到当前登录用户ID标识
        Integer uid=userService.getUserIdException();

        //验证是否为默认
        if(invoiceRise.getIsDefault()){
            UpdateWrapper<InvoiceRise> invoiceRiseUpdateWrapper=new UpdateWrapper<>();
            invoiceRiseUpdateWrapper.eq("uid",uid);
            dao.update(new InvoiceRise().setIsDefault(Boolean.FALSE),invoiceRiseUpdateWrapper);
        }

        //执行保存,返回结果
        invoiceRise.setUid(uid);
        return this.saveOrUpdate(invoiceRise);
    }

    @Override
    public PageInfo<InvoiceRise> getPageList(InvoiceRiseSearchRequest request, PageParamRequest pageParamRequest) {
        //得到分页对象
        Page<InvoiceRise> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //得到当前登录用户ID标识
        Integer uid=userService.getUserIdException();

        //得到lambda对象，用作筛选条件
        LambdaQueryWrapper<InvoiceRise> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(InvoiceRise::getIsDel,Boolean.FALSE);
        lambdaQueryWrapper.eq(InvoiceRise::getIsShow,Boolean.TRUE);
        lambdaQueryWrapper.eq(InvoiceRise::getUid,uid);

        //条件-关键字
        if(StringUtils.isNotBlank(request.getKeywords())){
            lambdaQueryWrapper.like(InvoiceRise::getAddressInfo, request.getKeywords());
        }

        //排序条件，根据sort以及创建时间排序
        lambdaQueryWrapper.orderByDesc(InvoiceRise::getId);

        //得到数据
        List<InvoiceRise> list = dao.selectList(lambdaQueryWrapper);

        //返回数据
        return CommonPage.copyPageInfo(page, list);
    }


}
