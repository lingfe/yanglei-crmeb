package com.zbkj.crmeb.retailer.service.impl;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CommonPage;
import com.common.PageParamRequest;
import com.exception.CrmebException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.retailer.dao.RetailerBillDao;
import com.zbkj.crmeb.retailer.model.Retailer;
import com.zbkj.crmeb.retailer.model.RetailerBill;
import com.zbkj.crmeb.retailer.response.RetailerBillResponse;
import com.zbkj.crmeb.retailer.response.RetailerBillSettlementStatisticsDataResponse;
import com.zbkj.crmeb.retailer.service.RetailerBillService;
import com.zbkj.crmeb.retailer.service.RetailerService;
import com.zbkj.crmeb.store.model.StoreProduct;
import com.zbkj.crmeb.store.service.StoreProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 零售商账单表-service层接口-实现类
 * @author: 零风
 * @CreateDate: 2021/12/15 16:40
 */
@Service
public class RetailerBillServiceImpl extends ServiceImpl<RetailerBillDao, RetailerBill> implements RetailerBillService {

    @Resource
    private RetailerBillDao dao;

    @Autowired
    private RetailerService retailerService;

    @Autowired
    private StoreProductService storeProductService;

    @Override
    public RetailerBillSettlementStatisticsDataResponse settlementStatisticsData(Integer retailerId, Integer productId) {
        //得到-账单列表
        PageParamRequest pageParamRequest=new PageParamRequest();
        pageParamRequest.setLimit(10000);
        List<RetailerBillResponse> responseList=this.getRetailerBillList(retailerId,productId,pageParamRequest).getList();
        if(responseList.size() == 0) return new RetailerBillSettlementStatisticsDataResponse();

        //实例化-零售商账单-结算统计数据响应对象
        RetailerBillSettlementStatisticsDataResponse response=new RetailerBillSettlementStatisticsDataResponse();

        //得到-统计数据
        Long noNum=responseList.stream().filter(a->a.getStatus() == 0).count();
        Long daiNum=responseList.stream().filter(b->b.getStatus() == 1).count();
        Long okNum=responseList.stream().filter(c->c.getStatus() == 2).count();
        BigDecimal totalDaiAmount=responseList.stream().filter(b->b.getStatus() == 1).map(RetailerBillResponse::getBillPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalOkAmount=responseList.stream().filter(c->c.getStatus() == 2).map(RetailerBillResponse::getBillPrice).reduce(BigDecimal.ZERO,BigDecimal::add);
        BigDecimal totalAmount = totalDaiAmount.add(totalOkAmount);

        //赋值
        response.setNoNum(noNum.intValue());
        response.setDaiNum(daiNum.intValue());
        response.setOkNum(okNum.intValue());
        response.setTotalAmount(totalAmount);
        response.setTotalDaiAmount(totalDaiAmount);
        response.setTotalOkAmount(totalOkAmount);

        //返回
        return response;
    }

    @Override
    public Boolean allSettlement(Integer retailerId) {
        //实例化零售商账单对象
        RetailerBill retailerBill=new RetailerBill();
        retailerBill.setStatus(2);
        retailerBill.setUpdateTime(new DateTime());

        //实例化修改对象
        UpdateWrapper<RetailerBill> retailerBillUpdateWrapper = new UpdateWrapper<>();
        retailerBillUpdateWrapper.eq("retailer_id",retailerId);
        retailerBillUpdateWrapper.eq("status",1);
        return dao.update(retailerBill,retailerBillUpdateWrapper) > 0;
    }

    @Override
    public Boolean clickSettlement(Integer retailerBillId) {
        //验证id
        RetailerBill retailerBill=dao.selectById(retailerBillId);
        if(retailerBill == null) throw new CrmebException("零售商账单不存在！");

        //更新结算时间
        retailerBill.setUpdateTime(new DateTime());
        retailerBill.setStatus(2);

        //执行
        return dao.updateById(retailerBill) > 0;
    }

    @Override
    public PageInfo<RetailerBillResponse> getRetailerBillList(Integer retailerId, Integer productId, PageParamRequest pageParamRequest) {
        //定义变量
        int tt=0;
        Retailer retailer=null;
        StoreProduct storeProduct=null;

        //定义查询对象
        LambdaQueryWrapper<RetailerBill> retailerBillLambdaQueryWrapper=new LambdaQueryWrapper<>();

        //条件-零售商id
        if(retailerId !=null && retailerId > 0){
            //得到-零售商信息
            retailer=retailerService.getInfoException(retailerId);
            retailerBillLambdaQueryWrapper.eq(RetailerBill::getRetailerId,retailerId);
            tt++;
        }else{
            throw new CrmebException("零售商id不能为空！");
        }

        //条件-商品表id标识
        if(productId !=null && productId > 0){
            //得到-商品信息
            storeProduct = storeProductService.getById(productId);
            if(storeProduct == null){
                storeProduct = new StoreProduct();
                storeProduct.setStoreName("商品已不存在！");
                storeProduct.setPrice(BigDecimal.ZERO);
            }
            retailerBillLambdaQueryWrapper.eq(RetailerBill::getProductId,productId);
            tt++;
        }

        //排序
        retailerBillLambdaQueryWrapper.orderByDesc(RetailerBill::getCreateTime).orderByDesc(RetailerBill::getId);

        //得到数据
        Page page = PageHelper.startPage(pageParamRequest.getPage(), 2);
        List<RetailerBill> retailerBillList = dao.selectList(retailerBillLambdaQueryWrapper);
        List<RetailerBillResponse> retailerResponseList=new ArrayList<>();
        if(retailerBillList == null) return CommonPage.copyPageInfo(page, retailerResponseList);

        //循环处理
        for (RetailerBill bill:retailerBillList) {
            //实例化-零售商账单响应对象
            RetailerBillResponse response=new RetailerBillResponse();
            BeanUtils.copyProperties(bill, response);

            //验证业务类型
            if(tt == 1 ){
                //得到-商品信息
                storeProduct = storeProductService.getById(bill.getProductId());
                if(storeProduct == null){
                    storeProduct = new StoreProduct();
                    storeProduct.setStoreName("商品已不存在！");
                    storeProduct.setPrice(BigDecimal.ZERO);
                }
                //变化业务类型,只进这个条件一次！
                if(tt > 1) tt = -1;
            }

            //赋值
            response.setReName(retailer.getReName());
            response.setStoreName(storeProduct.getStoreName());
            response.setPrice(storeProduct.getPrice());

            //添加到集合
            retailerResponseList.add(response);
        }

        //返回数据
        return CommonPage.copyPageInfo(page, retailerResponseList);
    }
}
