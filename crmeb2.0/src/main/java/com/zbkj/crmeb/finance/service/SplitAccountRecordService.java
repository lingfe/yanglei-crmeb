package com.zbkj.crmeb.finance.service;

import com.alipay.api.AlipayClient;
import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.finance.model.SplitAccountRecord;
import com.zbkj.crmeb.finance.request.SplitAccountRecordSearchRequest;
import com.zbkj.crmeb.store.model.StoreOrder;
import com.zbkj.crmeb.user.model.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 分账记录表-service层接口
 * @author: 零风
 * @CreateDate: 2022/1/21 10:36
 */
public interface SplitAccountRecordService extends IService<SplitAccountRecord> {

    /**
     * 分账完成-发起解冻请求
     * @param mapParamOK 参数
     * @param appkey key
     * @param certPath 证书路径
     * @Author 零风
     * @Date  2022/6/30 15:19
     */
    void payProfitsharingOK(Map<String, String> mapParamOK, String appkey, String certPath) throws Exception;

    /**
     * 验证-分账费率是否全部转入提现账户
     * @param user 分账接收方账户
     * @param storeOrder 订单信息
     * @param s 分账参数
     * @Author 零风
     * @Date  2022/6/30 15:41
     * @return 是否
     */
    Boolean getPayProfitsharingIsAccount(User user,StoreOrder storeOrder, Map<String, Object> s);

    /**
     * 分账-转入提现账户
     * @Author 零风
     * @Date  2022/6/27 11:59
     * @return
     */
    Boolean payProfitsharingWithdrawalAccount(User user, Integer orderId, String type, BigDecimal price);

    /**
     * 支付宝分账-统一收单交易结算
     * @Author 零风
     * @Date  2022/2/17
     * @return
     */
    SplitAccountRecord payProfitSharingAlipay(StoreOrder storeOrder);

    /**
     * 得到分账接收方(公共接口)
     * @param storeOrder 订单信息
     * @Author 零风
     * @Date  2022/2/15
     * @return 接收方list
     */
    List<Map<String, Object>> getPayProfitSharingReceiver(StoreOrder storeOrder);

    /**
     * 积分支付订单分账
     * @param storeOrder 订单信息
     * @Author 零风
     * @Date  2022/2/14
     * @return 结果
     */
    SplitAccountRecord payProfitSharingIntegral(StoreOrder storeOrder);

    /**
     * 支付宝支付订单分账-添加接收方
     * @param alipayClient 支付宝公共请求参数
     * @param mapList 接收方参数
     * @Author 零风
     * @Date  2022/2/14
     * @return 结果
     */
    Boolean payProfitSharingAlipayAddReceiver(AlipayClient alipayClient, List<Map<String,Object>> mapList);

    /**
     * 分账(定时任务)
     * -积分支付订单、微信支付订单、支付宝支付订单等分账处理
     * @Author 零风
     * @Date  2022/1/19
     */
    void payProfitsharingTask();

    /**
     * 微信分账-请求单次分账
     * @param orderId 订单ID标识
     * @Author 零风
     * @Date  2022/1/19
     * @return
     */
    SplitAccountRecord payProfitsharingWeiXin(Integer orderId) throws Exception;

    /**
     * 分页-分账信息
     * @param pageParamRequest  分页
     * @param request           搜索请求参数
     * @Author 零风
     * @Date  2022/1/21
     * @return 分页结果
     */
    PageInfo<SplitAccountRecord> getPageList(SplitAccountRecordSearchRequest request, PageParamRequest pageParamRequest);

}
