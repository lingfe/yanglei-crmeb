package com.zbkj.crmeb.retailer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.front.response.UserMerIdOrderDetailsResponse;
import com.zbkj.crmeb.front.response.UserSpreadBannerResponse;
import com.zbkj.crmeb.retailer.model.Retailer;
import com.zbkj.crmeb.retailer.request.RetailerRequest;
import com.zbkj.crmeb.retailer.request.RetailerSearchRequest;
import com.zbkj.crmeb.retailer.response.RetailerBillResponse;
import com.zbkj.crmeb.retailer.response.RetailerPraResponse;
import com.zbkj.crmeb.retailer.response.RetailerResponse;
import com.zbkj.crmeb.store.response.ProductOrderDataResponse;
import com.zbkj.crmeb.user.response.UserMerIdDataResponse;

import java.util.List;

/**
 * 零售商表-service层接口
 * @author: 零风
 * @CreateDate: 2021/11/22 14:46
 */
public interface RetailerService extends IService<Retailer> {

    /**
     * 得到零售商名称
     * @param id 零售商id标识
     * @Author 零风
     * @Date  2022/1/5
     * @return 零售商名称
     */
    String getRetailerName(Integer id);

    /**
     * 分页查询
     * @param request
     * @param pageParamRequest
     * @author  零风
     * @Date  2021/10/18
     * @return  记录列表
     */
    PageInfo<RetailerResponse> getList(RetailerSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 添加-零售商
     * @param request
     * @return
     */
    Retailer add(RetailerRequest request);

    /**
     * 修改-状态
     * @param id
     * @param states
     * @return
     */
    Boolean updateState(Integer id,Integer states);

    /**
     * 根据区域代理标识-得到零售商列表
     * @Author 零风
     * @Date  2021/11/29
     * @return 零售商信息响应list
     */
    List<RetailerResponse> getInfoList(Integer raId);

    /**
     * 根据区域代理标识list-得到零售商列表
     * @param raIds 区域代理ID标识集合
     * @Author 零风
     * @Date  2021/12/15
     * @return 零售商信息响应list
     */
    List<RetailerResponse> getWhereRaIdsList(List<Integer> raIds);

    /**
     * 查看详情
     * @param id 零售商表ID标识
     * @return
     */
    RetailerResponse getInfo(Integer id);

    /**
     * 修改零售商信息
     * @Author 零风
     * @Date  2021/12/1
     * @return
     */
    Boolean update(RetailerRequest request);

    /**
     * 根据用户ID查询零售商
     * @param uid
     * @return
     */
    Retailer getWhereUid(Integer uid);

    /**
     * 零售商代理产品-分页列表
     * @param request
     * @param pageParamRequest
     * @return
     */
    PageInfo<RetailerPraResponse> retailerProductList(RetailerSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 获取-零售商推广图
     * @return
     */
    List<UserSpreadBannerResponse> getSpreadBannerList();

    /**
     * 得到-零售商-相关统计数据
     * @param retailerId  零售商表id标识
     * @return
     */
    UserMerIdDataResponse getData(Integer retailerId);

    /**
     * 根据当前登录用户-得到绑定的零售商列表
     * @return
     */
    List<Retailer> getWhereUserIDList();

    /**
     * 得到-零售商-订单详细统计数据
     * @param retailerId  零售商表id标识
     * @param dateType  日期类型(0=今天、1=昨天、2=最近7天、3=本月、4=本年)
     * @return
     */
    UserMerIdOrderDetailsResponse getOrderInfoData(Integer retailerId, Integer dateType);

    /**
     * 零售商-获取-产品订单相关统计数据
     * @param retailerId 零售商id标识
     * @Author 零风
     * @Date  2021/12/15
     * @return
     */
    List<ProductOrderDataResponse> getPraOrderData(Integer retailerId);

    /**
     * 根据零售商id标识-生成零售商产品账单
     * @param retailerId   零售商id标识
     * @Author 零风
     * @Date  2021/12/15
     * @return
     */
    void  generateRetailerProductBill(Integer retailerId);

    /**
     * 执行生成零售商账单task
     * @Author 零风
     * @Date  2021/12/15
     * @return
     */
    void exeRetailerBillTask();

    /**
     * 根据零售商ID标识得到-零售商信息(带异常)
     * @param retailerId 零售商表ID标识
     * @Author 零风
     * @Date  2021/12/16
     * @return
     */
    Retailer getInfoException(Integer retailerId);

    /**
     * 查看零售商账单列表
     * @param retailerId 零售商表id标识。
     * @Author 零风
     * @Date  2021/12/16
     * @return
     */
    PageInfo<RetailerBillResponse> seeRetailerBillList(Integer retailerId);
}
