package com.zbkj.crmeb.store.service;


import com.zbkj.crmeb.store.model.StoreOrder;

/**
 * 订单任务服务-service接口
 * @author: 零风
 * @CreateDate: 2022/1/17 9:49
 */
public interface StoreOrderTaskService {

    /**
     * 取消订单之后相关业务逻辑
     * -回滚库存、日志等
     * @Author 零风
     * @Date  2021/12/20
     */
    Boolean cancelByUser(StoreOrder storeOrder);

    /**
     * 订单完成之后相关业务处理
     * -自动更新订单状态日志
     * -保存有资格参与随机分配积分的用户
     * @Author 零风
     * @Date  2021/12/20
     */
    Boolean complete(StoreOrder storeOrder);

    /**
     * 订单退款后续处理
     * @Author 零风
     * @Date  2022/1/17
     * @return
     */
    Boolean refundOrder(StoreOrder storeOrder);

    /**
     * 超时未支付系统自动取消
     * @param storeOrder 订单信息
     * @Author 零风
     * @Date  2021/12/20
     * @return
     */
    Boolean autoCancel(StoreOrder storeOrder);

    /**
     * 订单收货task处理
     * @param orderId 订单id
     * @return Boolean
     * 1.写订单日志
     * 2.分佣-佣金进入冻结期
     */
    Boolean orderReceiving(Integer orderId);
}
