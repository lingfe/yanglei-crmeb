package com.zbkj.crmeb.store.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.exception.CrmebException;
import com.github.pagehelper.PageHelper;
import com.utils.DateUtil;
import com.zbkj.crmeb.cloudAccount.util.StringUtils;
import com.zbkj.crmeb.front.response.UserMerIdOrderDetailsResponse;
import com.zbkj.crmeb.store.dao.SupplierDao;
import com.zbkj.crmeb.store.model.StoreProduct;
import com.zbkj.crmeb.store.model.Supplier;
import com.zbkj.crmeb.store.request.SupplierRequest;
import com.zbkj.crmeb.store.request.SupplierSearchRequest;
import com.zbkj.crmeb.store.service.StoreProductService;
import com.zbkj.crmeb.store.service.SupplierService;
import com.zbkj.crmeb.system.service.SystemAttachmentService;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.response.UserMerIdDataResponse;
import com.zbkj.crmeb.user.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 供应商表-service层接口实现类
 * @author: 零风
 * @CreateDate: 2021/12/28 11:24
 */
@Service
public class SupplierServiceImpl extends ServiceImpl<SupplierDao, Supplier> implements SupplierService {

    @Resource
    private SupplierDao dao;

    @Autowired
    private UserService userService;

    @Autowired
    private SystemAttachmentService systemAttachmentService;

    @Autowired
    private StoreProductService storeProductService;

    @Override
    public List<StoreProduct> getSupplierProductList(Integer supplierId,PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<StoreProduct> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StoreProduct::getMerId,supplierId);
        return storeProductService.list(lambdaQueryWrapper);
    }

    @Override
    public String getSupplierName(Integer id) {
        if(id == 0)return "系统";
        Supplier supplier=dao.selectById(id);
        if(supplier == null) return "供应商不存在或ID错误！";
        return supplier.getSuppName();
    }

    @Override
    public void clearPrefix(Supplier supplier) {
        //清除图片路径域名部分
        supplier.setKhxkzImg(systemAttachmentService.clearPrefix(supplier.getKhxkzImg()));
        supplier.setYyzzImg(systemAttachmentService.clearPrefix(supplier.getYyzzImg()));
        supplier.setZhuzhiImg(systemAttachmentService.clearPrefix(supplier.getZhuzhiImg()));
        supplier.setIdFmImg(systemAttachmentService.clearPrefix(supplier.getIdFmImg()));
        supplier.setIdZmImg(systemAttachmentService.clearPrefix(supplier.getIdZmImg()));
        supplier.setSettlementShouquanhanImg(systemAttachmentService.clearPrefix(supplier.getSettlementShouquanhanImg()));
        supplier.setSettlementSfzFmImg(systemAttachmentService.clearPrefix(supplier.getSettlementSfzFmImg()));
        supplier.setSettlementSfzZmImg(systemAttachmentService.clearPrefix(supplier.getSettlementSfzZmImg()));
        supplier.setShopMentouImg(systemAttachmentService.clearPrefix(supplier.getShopMentouImg()));
        supplier.setShopNeijingImg(systemAttachmentService.clearPrefix(supplier.getShopNeijingImg()));
        supplier.setTaxDengjizhengImg(systemAttachmentService.clearPrefix(supplier.getTaxDengjizhengImg()));
        supplier.setYhkFmImg(systemAttachmentService.clearPrefix(supplier.getYhkFmImg()));
        supplier.setYhkZmImg(systemAttachmentService.clearPrefix(supplier.getYhkZmImg()));
    }

    @Override
    public UserMerIdOrderDetailsResponse getSupplierOrderInfoStatisticsData(Integer id, Integer dateType) {
        Supplier supplier=this.getSupplierException(id);
        if(supplier == null) return  new UserMerIdOrderDetailsResponse();
        return userService.getMerIdOrderInfoStatisticsData(supplier.getId(),dateType);
    }

    @Override
    public UserMerIdDataResponse getSupplierData(Integer id) {
        //定义变量
        Supplier supplier =null;

        //验证id非空
        if(id == null || id <= 0){
            //得到-当前登录用户（查当前用户绑定的-供应商）
            User thisLoginUser=userService.getInfoException();
            LambdaQueryWrapper<Supplier> supplierLambdaQueryWrapper=new LambdaQueryWrapper<>();
            supplierLambdaQueryWrapper.eq(Supplier::getUid,thisLoginUser.getUid());
            supplierLambdaQueryWrapper.last("LIMIT 1");//只取一条
            supplier = dao.selectOne(supplierLambdaQueryWrapper);
        }else{
            //根据ID标识-得到
            supplier = dao.selectById(id);
        }

        //验证
        if(supplier == null) return new UserMerIdDataResponse();

        //得到数据-并返回
        return userService.getUserMerIdDataResponse(supplier.getId());
    }

    @Override
    public List<Supplier> getUidSupplierList() {
        //得到用户
        User user=userService.getInfoException();

        //定义查询对象
        LambdaQueryWrapper<Supplier> supplierLambdaQueryWrapper=new LambdaQueryWrapper<>();
        supplierLambdaQueryWrapper.eq(Supplier::getUid,user.getUid());
        return dao.selectList(supplierLambdaQueryWrapper);
    }

    @Override
    public Supplier getSupplierException(Integer id) {
        Supplier supplier = dao.selectById(id);
        if(supplier == null ) throw new CrmebException("供应商不存在!");
        return supplier;
    }

    @Override
    public Boolean toExamine(Integer id, Integer status) {
        //得到-数据
        Supplier supplier = this.getSupplierException(id);

        //验证参数
        if(status == null || status < 1 || status > 2)throw new CrmebException("供应商状态值参数有误！");

        //验证-状态值
        switch (supplier.getStatus()){
            case 1:
                //审核通过，验证手机号账户是否存在
                User user = userService.getById(supplier.getUid());
                if (!ObjectUtil.isNull(user)) {
                    user.setIsSupplier(Boolean.TRUE);   //标记为供应商
                    supplier.setUid(user.getUid());     //设置为供应商-管理者
                    userService.updateById(user);       //更新
                }else{
                    throw new CrmebException("平台不存在该用户！");
                }
                break;
        }

        //执行修改
        supplier.setStatus(status);
        return dao.updateById(supplier)>0;
    }

    @Override
    public Boolean update(SupplierRequest request) {
        //验证ID
        Supplier supplier = this.getSupplierException(request.getId());
        BeanUtils.copyProperties(request, supplier);//转换

        //处理-图片路径
        this.clearPrefix(supplier);

        //处理-身份证日期
        if(request.getIdStartTime() == null || request.getIdStopTime() ==null)throw new CrmebException("身份证起始日期不能空！");
        supplier.setIdStartTime(DateUtil.strToDate(request.getIdStartTime(), Constants.DATE_FORMAT_DATE));
        supplier.setIdStopTime(DateUtil.strToDate(request.getIdStopTime(), Constants.DATE_FORMAT_DATE));

        //返回
        return dao.updateById(supplier) > 0;
    }

    @Override
    public Boolean save(SupplierRequest request) {
        //验证参数
        if(StringUtils.isEmpty(request.getUid())||request.getUid() <= 0){
            throw new CrmebException("未绑定管理者用户！");
        }

        //转换
        Supplier supplier = new Supplier();
        BeanUtils.copyProperties(request, supplier);
        supplier.setStatus(0);

        //处理-图片路径
        this.clearPrefix(supplier);

        //处理-身份证日期
        supplier.setIdStartTime(DateUtil.strToDate(request.getIdStartTime(), Constants.DATE_FORMAT_DATE));
        supplier.setIdStopTime(DateUtil.strToDate(request.getIdStopTime(), Constants.DATE_FORMAT_DATE));

        //执行保存
        return this.save(supplier);
    }

    @Override
    public List<Supplier> getPageList(SupplierSearchRequest request, PageParamRequest pageParamRequest) {
        //设置-分页参数
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //定义查询条件
        LambdaQueryWrapper<Supplier> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        //条件-关键字
        if(null != request.getKeywords()){
            lambdaQueryWrapper.like(Supplier::getSuppName, request.getKeywords());
            lambdaQueryWrapper.or().like(Supplier::getFarenName, request.getKeywords());
        }

        //排序
        lambdaQueryWrapper.orderByDesc(Supplier::getId);

        //返回数据
        return dao.selectList(lambdaQueryWrapper);
    }
}
