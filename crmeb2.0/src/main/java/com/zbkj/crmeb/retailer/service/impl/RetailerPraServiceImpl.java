package com.zbkj.crmeb.retailer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.exception.CrmebException;
import com.zbkj.crmeb.front.response.UserSpreadBannerResponse;
import com.zbkj.crmeb.retailer.dao.RetailerDao;
import com.zbkj.crmeb.retailer.dao.RetailerPraDao;
import com.zbkj.crmeb.retailer.model.Retailer;
import com.zbkj.crmeb.retailer.model.RetailerPra;
import com.zbkj.crmeb.retailer.response.RetailerPraResponse;
import com.zbkj.crmeb.retailer.response.RetailerResponse;
import com.zbkj.crmeb.retailer.service.RetailerPraService;
import com.zbkj.crmeb.retailer.service.RetailerService;
import com.zbkj.crmeb.store.model.StoreProduct;
import com.zbkj.crmeb.store.model.StoreProductRA;
import com.zbkj.crmeb.store.service.StoreProductRAService;
import com.zbkj.crmeb.store.service.StoreProductService;
import com.zbkj.crmeb.system.service.SystemGroupDataService;
import com.zbkj.crmeb.user.response.UserMerIdDataResponse;
import com.zbkj.crmeb.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * 零售商产品代理表-service层接口实现类
 * @author: 零风
 * @CreateDate: 2021/11/29 16:01
 */
@Service
public class RetailerPraServiceImpl extends ServiceImpl<RetailerPraDao, RetailerPra> implements RetailerPraService {

    @Resource
    private RetailerPraDao dao;

    @Autowired
    private StoreProductRAService storeProductRAService;

    @Autowired
    private StoreProductService storeProductService;

    @Autowired
    private RetailerService retailerService;

    @Override
    public List<RetailerPra> getWhereRetailerId(Integer retailerId) {
        //根据零售商ID标识-得到零售商数据
        Retailer retailer=retailerService.getInfoException(retailerId);

        //读取-零售商产品代理信息
        LambdaQueryWrapper<RetailerPra> retailerPraLambdaQueryWrapper=new LambdaQueryWrapper<>();
        retailerPraLambdaQueryWrapper.eq(RetailerPra::getRetailerId,retailer.getId());
        List<RetailerPra> retailerPraList = dao.selectList(retailerPraLambdaQueryWrapper);
        if(retailerPraList == null )return new ArrayList<>();
        return retailerPraList;
    }

    @Override
    public Boolean isSale(Integer id, Boolean isSale) {
        //根据ID得到数据
        RetailerPra retailerPra=dao.selectById(id);
        if(retailerPra == null)throw new CrmebException("零售商代理产品信息不存在!");

        //赋值
        retailerPra.setIsSale(isSale);

        //执行并返回
        return dao.updateById(retailerPra)>0;
    }

    @Override
    public List<RetailerPraResponse> getRetailerProductList(Integer retailerId,Integer type,Boolean isSale) {
        //定义查询对象-零售商产品代理信息
        LambdaQueryWrapper<RetailerPra> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(RetailerPra::getRetailerId,retailerId);
        lambdaQueryWrapper.eq(RetailerPra::getIsDel,false);

        //验证-查询类型
        switch (type){
            case 0:
                lambdaQueryWrapper.eq(RetailerPra::getIsSale,isSale);
                break;
            case 1:
                lambdaQueryWrapper.eq(RetailerPra::getIsSale,true);
                break;
            case 2:
                break;
        }

        //得到-零售商产品代理表
        List<RetailerPra> retailerPraList =  dao.selectList(lambdaQueryWrapper);
        if(retailerPraList == null || retailerPraList.size()<=0)return new ArrayList<>();

        //查询-产品代理表
        LambdaQueryWrapper<StoreProductRA> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.in(StoreProductRA::getId,retailerPraList.stream().map(RetailerPra::getPraId).collect(Collectors.toList()));
        List<StoreProductRA> storeProductRAList=storeProductRAService.list(queryWrapper);
        if(storeProductRAList == null || storeProductRAList.size()<=0)return new ArrayList<>();

        //查询-产品信息表
        LambdaQueryWrapper<StoreProduct> storeProductLambdaQueryWrapper=new LambdaQueryWrapper<>();
        storeProductLambdaQueryWrapper.in(StoreProduct::getId,storeProductRAList.stream().map(StoreProductRA::getProductId).collect(Collectors.toList()));
        List<StoreProduct> storeProductList=storeProductService.list(storeProductLambdaQueryWrapper);
        if(storeProductList == null )return new ArrayList<>();

        //循环处理-转成响应对象
        List<RetailerPraResponse> retailerPraResponseList=new ArrayList<>();

        try{
            //零售商产品代理表
            for (RetailerPra pra:retailerPraList ) {
                //产品代理表
                CompletableFuture<Integer> completableFutureProductId=CompletableFuture.supplyAsync(()->{
                    Integer productId=0;
                    for (StoreProductRA ra:storeProductRAList ) {
                        if(pra.getPraId().equals(ra.getId())){
                            productId = ra.getProductId();
                            break;
                        }
                    }
                    return productId;
                });

                //产品信息表
                int finalProductId = completableFutureProductId.get();
                CompletableFuture<StoreProduct> completableFuture=CompletableFuture.supplyAsync(()->{
                    StoreProduct storeProduct=null;
                    for (StoreProduct s:storeProductList) {
                        if(s.getId().equals(finalProductId)){
                            storeProduct=s;
                            break;
                        }
                    }
                    return storeProduct;
                });

                //实例化-零售商产品代理表-响应对象
                RetailerPraResponse response=new RetailerPraResponse();
                response.setStoreProduct(completableFuture.get());
                response.setRetailerPra(pra);

                //添加到-list集合
                retailerPraResponseList.add(response);
            }
        }catch (Exception e){
            throw new CrmebException("线程发生错误!"+e.getMessage());
        }

        //验证非空
        if(retailerPraResponseList == null )return new ArrayList<>();

        //返回
        return retailerPraResponseList;
    }
}
