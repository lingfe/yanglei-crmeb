package com.zbkj.crmeb.store.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exception.CrmebException;
import com.zbkj.crmeb.regionalAgency.model.RegionalAgency;
import com.zbkj.crmeb.regionalAgency.service.RegionalAgencyService;
import com.zbkj.crmeb.retailer.model.RetailerPra;
import com.zbkj.crmeb.retailer.service.RetailerPraService;
import com.zbkj.crmeb.store.dao.StoreProductRADao;
import com.zbkj.crmeb.store.model.StoreProductRA;
import com.zbkj.crmeb.store.request.StoreProductRARequest;
import com.zbkj.crmeb.store.response.StoreProductRAResponse;
import com.zbkj.crmeb.store.service.StoreProductRAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 产品代理-service层接口-实现类
 * @author: 零风
 * @CreateDate: 2021/11/24 9:55
 */
@Service
public class StoreProductRAServiceImpl extends ServiceImpl<StoreProductRADao, StoreProductRA> implements StoreProductRAService {

    @Resource
    private StoreProductRADao dao;

    @Autowired
    private RegionalAgencyService regionalAgencyService;

    @Autowired
    private RetailerPraService retailerPraService;

    @Override
    public List<StoreProductRA> getWhereRetailerId(Integer retailerId) {
        //读取-零售商产品代理信息
        List<RetailerPra> retailerPraList = retailerPraService.getWhereRetailerId(retailerId);
        if(retailerPraList.size() == 0 )return new ArrayList<>();

        //读取-产品代理表
        List<Integer> praIdList = retailerPraList.stream().map(RetailerPra::getPraId).collect(Collectors.toList());
        List<StoreProductRA> storeProductRAList=dao.selectBatchIds(praIdList);
        if(storeProductRAList == null )return new ArrayList<>();
        return storeProductRAList;
    }

    @Override
    public Boolean delete(Integer id) {
        //查询-是否存在
        StoreProductRA spra=dao.selectById(id);
        if(spra == null) throw new CrmebException("ID有误或产品代理信息已不存在！");

        //删除-零售商关联的产品代理信息
        LambdaQueryWrapper<RetailerPra> retailerPraLambdaQueryWrapper =  new LambdaQueryWrapper<>();
        retailerPraLambdaQueryWrapper.eq(RetailerPra::getPraId,spra.getId());
        retailerPraService.remove(retailerPraLambdaQueryWrapper);

        //删除-产品代理信息
        return dao.deleteById(id)>0;
    }

    @Override
    public List<StoreProductRAResponse> getInfoList(Integer productId) {
        //实例化查询对象
        LambdaQueryWrapper<StoreProductRA> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StoreProductRA::getProductId,productId);
        lambdaQueryWrapper.eq(StoreProductRA::getIsDel,Boolean.FALSE);

        //得到-数据
        List<StoreProductRA> storeProductRAList = dao.selectList(lambdaQueryWrapper);
        List<StoreProductRAResponse> storeProductRAResponseList=new ArrayList<>();
        for (StoreProductRA spra:storeProductRAList ) {
            //得到-区域代理信息
            StoreProductRAResponse response=new StoreProductRAResponse();
            response.setId(spra.getId());

            //得到-区域代理信息
            RegionalAgency regionalAgency=regionalAgencyService.getById(spra.getRaId());
            if(regionalAgency == null)response.setRaName("区域代理信息为空");
            else {
                response.setRaId(regionalAgency.getId());
                response.setRaName(regionalAgency.getRaName());
            }

            //添加到-响应list
            storeProductRAResponseList.add(response);
        }

        //执行查询
        return storeProductRAResponseList;
    }

    @Override
    public Boolean add(StoreProductRARequest request) {
        //定义变量
        List<Integer> proIdList=new ArrayList<>();//商品ID标识集合
        List<Integer> raIdList=new ArrayList<>();//区域代理ID标识
        List<StoreProductRA> storeProductRAList=new ArrayList<>();

        //验证-商品IDs非空
        if(StrUtil.isNotBlank(request.getProductIds())){
            String[] strings= request.getProductIds().split(",");
            proIdList = Arrays.stream(strings).mapToInt(Integer::valueOf).boxed().collect(Collectors.toList());
        }

        //验证-区域代理IDs非空
        if(StrUtil.isNotBlank(request.getRaIds())){
            String[] strings= request.getRaIds().split(",");
            raIdList = Arrays.stream(strings).mapToInt(Integer::valueOf).boxed().collect(Collectors.toList());
        }

        //处理-待添加的产品代理信息
        for (Integer proid:proIdList) {
            for (Integer raid:raIdList) {
                //实例化对象
                StoreProductRA storeProductRA=new StoreProductRA();
                storeProductRA.setProductId(proid);
                storeProductRA.setRaId(raid);

                //查询验证-产品代理信息是否已存在
                LambdaQueryWrapper<StoreProductRA> lambdaQueryWrapper=new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(StoreProductRA::getRaId,storeProductRA.getRaId());
                lambdaQueryWrapper.eq(StoreProductRA::getProductId,storeProductRA.getProductId());
                List<StoreProductRA> sList = dao.selectList(lambdaQueryWrapper);
                if(sList == null || sList.size() <= 0){
                    //添加到list
                    storeProductRAList.add(storeProductRA);
                }else{
                    continue;//跳过
                }
            }
        }

        //验证list
        if(storeProductRAList == null || storeProductRAList.size() <= 0)throw new CrmebException("已存在或参数不能为空！");

        //执行并返回结果
        return this.saveBatch(storeProductRAList);
    }

    @Override
    public List<StoreProductRA> getWhereRaIDList(Integer raId) {
        //实例化查询对象
        LambdaQueryWrapper<StoreProductRA> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StoreProductRA::getRaId,raId);

        //执行查询
        return dao.selectList(lambdaQueryWrapper);
    }
}
