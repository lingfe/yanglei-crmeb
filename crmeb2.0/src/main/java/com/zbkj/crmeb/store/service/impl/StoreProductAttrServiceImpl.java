package com.zbkj.crmeb.store.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.PageParamRequest;
import com.github.pagehelper.PageHelper;
import com.zbkj.crmeb.store.dao.StoreProductAttrDao;
import com.zbkj.crmeb.store.model.StoreProductAttr;
import com.zbkj.crmeb.store.request.StoreProductAttrSearchRequest;
import com.zbkj.crmeb.store.service.StoreProductAttrService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * StoreProductAttrServiceImpl 接口实现
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Service
public class StoreProductAttrServiceImpl extends ServiceImpl<StoreProductAttrDao, StoreProductAttr>
        implements StoreProductAttrService {

    @Resource
    private StoreProductAttrDao dao;


    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @author Mr.Zhang
    * @since 2020-05-27
    * @return List<StoreProductAttr>
    */
    @Override
    public List<StoreProductAttr> getList(StoreProductAttrSearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //带 StoreProductAttr 类的多条件查询
        LambdaQueryWrapper<StoreProductAttr> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        StoreProductAttr model = new StoreProductAttr();
        BeanUtils.copyProperties(request, model);
        lambdaQueryWrapper.setEntity(model);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 根据基本属性查询商品属性详情
     *
     * @param storeProductAttr 商品属性
     * @return 查询商品属性集合
     */
    @Override
    public List<StoreProductAttr> getByEntity(StoreProductAttr storeProductAttr) {
        //定义查询
        LambdaQueryWrapper<StoreProductAttr> lqw = Wrappers.lambdaQuery();
        //验证非空
        if(null != storeProductAttr.getId()) lqw.eq(StoreProductAttr::getId,storeProductAttr.getId());

        //验证属性值
        if(StringUtils.isNotBlank(storeProductAttr.getAttrValues()))
            lqw.eq(StoreProductAttr::getAttrValues,storeProductAttr.getAttrValues());

        if(StringUtils.isNotBlank(storeProductAttr.getAttrName()))
            lqw.eq(StoreProductAttr::getAttrName,storeProductAttr.getAttrName());

        if(null != storeProductAttr.getProductId()) lqw.eq(StoreProductAttr::getProductId,storeProductAttr.getProductId());
        if(null != storeProductAttr.getType()) lqw.eq(StoreProductAttr::getType,storeProductAttr.getType());
        return dao.selectList(lqw);
    }

    /**
     * 根据id查询商品属性详情
     * @param productId 商品id
     * @return 查询结果
     */
    @Override
    public List<StoreProductAttr> getByProductId(int productId) {
        LambdaQueryWrapper<StoreProductAttr> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StoreProductAttr::getProductId, productId);
        List<StoreProductAttr> storeProductAttrs = dao.selectList(lambdaQueryWrapper);
    return storeProductAttrs;
    }

    /**
     * 根据id删除商品
     * @param productId 待删除商品id
     * @param type 类型区分是是否添加营销
     */
    @Override
    public void removeByProductId(Integer productId,int type) {
        LambdaQueryWrapper<StoreProductAttr> lambdaQW = Wrappers.lambdaQuery();
        lambdaQW.eq(StoreProductAttr::getProductId, productId).eq(StoreProductAttr::getType,type);
        dao.delete(lambdaQW);
    }
}

