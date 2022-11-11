package com.zbkj.crmeb.retailer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.CommonPage;
import com.common.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.retailer.model.RetailerBill;
import com.zbkj.crmeb.retailer.response.RetailerBillResponse;
import com.zbkj.crmeb.retailer.response.RetailerBillSettlementStatisticsDataResponse;

import java.util.List;

/**
* 零售商账单表-service层接口
* @author: 零风
* @CreateDate: 2021/12/15 16:39
*/
public interface RetailerBillService extends IService<RetailerBill> {

    /**
     * 得到-零售商账单列表
     * @param retailerId 零售商表ID标识
     * @param productId  商品表ID标识
     * @param pageParamRequest 分页
     * @Author 零风
     * @Date  2021/12/16
     * @return
     */
    PageInfo<RetailerBillResponse> getRetailerBillList(Integer retailerId, Integer productId, PageParamRequest pageParamRequest);


    /**
     * 点击结算
     * @param retailerBillId 零售商账单表id标识
     * @Author 零风
     * @Date  2021/12/16
     * @return
     */
    Boolean clickSettlement(Integer retailerBillId);

    /**
     * 一键结算/全部结算
     * @param retailerId retailerId
     * @Author 零风
     * @Date  2021/12/16
     * @return
     */
    Boolean allSettlement(Integer retailerId);

    /**
     * 零售商账单-结算统计数据
     * @param retailerId    零售商表id标识
     * @param productId     商品表id标识
     * @Author 零风
     * @Date  2021/12/16
     * @return
     */
     RetailerBillSettlementStatisticsDataResponse settlementStatisticsData(Integer retailerId, Integer productId);

}
