package com.zbkj.crmeb.marketing.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.PageParamRequest;
import com.exception.CrmebException;
import com.github.pagehelper.PageHelper;
import com.zbkj.crmeb.marketing.dao.StoreWearDao;
import com.zbkj.crmeb.marketing.model.StoreWear;
import com.zbkj.crmeb.marketing.request.StoreWearRequest;
import com.zbkj.crmeb.marketing.request.StoreWearSearchRequest;
import com.zbkj.crmeb.marketing.response.StoreWearResponse;
import com.zbkj.crmeb.marketing.service.StoreWearService;
import com.zbkj.crmeb.store.model.StoreProduct;
import com.zbkj.crmeb.store.service.StoreProductService;
import com.zbkj.crmeb.system.service.SystemAttachmentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 穿搭表-service层接口实现内
 * @author: 零风
 * @CreateDate: 2021/10/8 10:52
 */
@Service
public class StoreWearServiceImpl extends ServiceImpl<StoreWearDao, StoreWear> implements StoreWearService {

    @Resource
    private StoreWearDao dao;

    @Autowired
    private StoreProductService storeProductService;

    @Autowired
    private SystemAttachmentService systemAttachmentService;

    @Override
    public List<StoreWear> getWearH5List(StoreWearSearchRequest request, PageParamRequest pageParamRequest) {
        return this.getList(request,pageParamRequest);
    }

    @Override
    public Boolean isIndex(Integer id, Boolean isIndex) {
        //验证
        StoreWear storeWear = getById(id);
        if (ObjectUtil.isNull(storeWear) || storeWear.getIsDel()) {
            throw new CrmebException("穿搭不存在!");
        }

        //将其他修改为，不展示
        LambdaQueryWrapper<StoreWear> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.ne(StoreWear::getId,String.valueOf(id));
        update(new StoreWear().setIsIndex(Boolean.FALSE),lambdaQueryWrapper);

        //执行
        storeWear=new StoreWear();
        storeWear.setId(id);
        storeWear.setIsIndex(isIndex);
        return dao.updateById(storeWear) > 0;
    }

    @Override
    public StoreWearResponse getWearH5Index() {
        //定义查询条件
        LambdaQueryWrapper<StoreWear> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StoreWear::getIsIndex,Boolean.TRUE);

        //得到-设置展示在首页的穿搭
        List<StoreWear> storeWearList = dao.selectList(lambdaQueryWrapper);
        if(storeWearList == null ||storeWearList.size() <=0) {
            return new StoreWearResponse();
        }

        //实例化-穿搭响应对象
        StoreWear storeWear = storeWearList.get(0);
        StoreWearResponse storeWearResponse=new StoreWearResponse();
        BeanUtils.copyProperties(storeWear, storeWearResponse);

        //查询并验证主商品信息
        LambdaQueryWrapper<StoreProduct> storeProductLambdaQueryWrapper = new LambdaQueryWrapper<>();
        storeProductLambdaQueryWrapper.in(StoreProduct::getId,storeWear.getWearProductIds().split(","));
        List<StoreProduct> storeProductList = storeProductService.list(storeProductLambdaQueryWrapper);
        if(storeProductList == null){
            return new StoreWearResponse();
            //throw new CrmebException("穿搭副商品已不存在!");
        }

        //赋值
        storeWearResponse.setWearProductList(storeProductList);

        //返回
        return storeWearResponse;
    }

    @Override
    public StoreWearResponse info(Integer id) {
        //验证
        StoreWear storeWear = getById(id);
        if (ObjectUtil.isNull(storeWear) || storeWear.getIsDel()) {
            throw new CrmebException("穿搭不存在!");
        }

        //实例化-穿搭响应对象
        StoreWearResponse storeWearResponse=new StoreWearResponse();
        BeanUtils.copyProperties(storeWear, storeWearResponse);

        //查询-主商品信息并验证
        StoreProduct storeProduct = storeProductService.getById(storeWearResponse.getProductId());
        if(storeProduct == null){
            throw new CrmebException("主商品已不存在!");
        }

        //查询-副商品信息
        LambdaQueryWrapper<StoreProduct> storeProductLambdaQueryWrapper = new LambdaQueryWrapper<>();
        storeProductLambdaQueryWrapper.in(StoreProduct::getId,storeWear.getWearProductIds().split(","));
        List<StoreProduct> storeProductList = storeProductService.list(storeProductLambdaQueryWrapper);

        //赋值-其他参数
        storeWearResponse.setStoreProduct(storeProduct);
        storeWearResponse.setWearProductList(storeProductList);

        //返回
        return storeWearResponse;
    }

    @Override
    public Boolean isShow(Integer id,Boolean isShow) {
        //验证
        StoreWear storeWear = getById(id);
        if (ObjectUtil.isNull(storeWear) || storeWear.getIsDel()) {
            throw new CrmebException("穿搭不存在!");
        }

        //执行
        storeWear.setIsShow(isShow);
        return dao.updateById(storeWear) > 0;
    }

    @Override
    public Boolean update(StoreWearRequest storeWearRequest) {
        //验证
        StoreWear storeWear = getById(storeWearRequest.getId());
        if (ObjectUtil.isNull(storeWear) || storeWear.getIsDel()) {
            throw new CrmebException("穿搭不存在!");
        }

        //更新值
        BeanUtils.copyProperties(storeWearRequest, storeWear);
        storeWear.setImg(systemAttachmentService.clearPrefix(storeWear.getImg()));

        //执行修改
        return dao.updateById(storeWear) > 0;
    }

    @Override
    public Boolean delete(Integer id) {
        //验证
        StoreWear storeWear = getById(id);
        if (ObjectUtil.isNull(storeWear) || storeWear.getIsDel()) {
            throw new CrmebException("穿搭不存在!");
        }

        //执行
        storeWear.setIsDel(true);
        return dao.updateById(storeWear) > 0;
    }

    @Override
    public Boolean insert(StoreWearRequest storeWearRequest) {
        //实例化-穿搭对象
        StoreWear storeWear=new StoreWear();
        BeanUtils.copyProperties(storeWearRequest, storeWear);
        storeWear.setImg(systemAttachmentService.clearPrefix(storeWear.getImg()));
        storeWear.setIsShow(Boolean.TRUE);
        storeWear.setIsIndex(Boolean.FALSE);
        storeWear.setIsDel(Boolean.FALSE);

        //执行保存
        return dao.insert(storeWear)>=1;
    }

    @Override
    public List<StoreWear> getList(StoreWearSearchRequest request, PageParamRequest pageParamRequest) {
        //分页
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //定义查询对象
        LambdaQueryWrapper<StoreWear> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StoreWear::getIsDel, false);

        //条件-是否显示
        if( request.getIsShow() !=null && request.getIsShow() >= 0 ){
            lambdaQueryWrapper.eq(StoreWear::getIsShow, request.getIsShow());
        }

        //条件-是否展示首页
        if( request.getIsIndex() !=null && request.getIsIndex() >= 0){
            lambdaQueryWrapper.eq(StoreWear::getIsIndex, request.getIsIndex());
        }

        //条件-关键字搜索
        if(StringUtils.isNotBlank(request.getKeywords())){
            lambdaQueryWrapper.and(i ->
                    i.like(StoreWear::getWearName, request.getKeywords())
                    .or().like(StoreWear::getId, request.getKeywords()));
        }

        //排序
        lambdaQueryWrapper.orderByDesc(StoreWear::getId);

        //返回
        return dao.selectList(lambdaQueryWrapper);
    }

}
