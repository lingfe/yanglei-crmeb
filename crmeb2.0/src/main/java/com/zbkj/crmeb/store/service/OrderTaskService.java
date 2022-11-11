package com.zbkj.crmeb.store.service;


import com.zbkj.crmeb.store.model.StoreOrder;

/**
* 订单任务服务-StoreOrderService接口
* @author: 零风
* @CreateDate: 2021/12/20 14:42
*/
public interface OrderTaskService{

    /**
     * 订单结算-供应商
     * @param order 订单信息
     * @Author 零风
     * @Date  2021/12/31
     */
    void settlementSupplierOrder(StoreOrder order);

    /**
     * 订单结算-区域代理
     * @param order 订单信息
     * @Author 零风
     * @Date  2021/12/31
     */
    void settlementRaOrder(StoreOrder order);

    /**
     * 取消订单之后相关业务逻辑
     * -回滚库存、日志等
     * @Author 零风
     * @Date  2021/12/20
     */
    void cancelByUser();

    /**
     * 订单退款之后相关业务处理
     * - 经验值、积分、佣金等返还和记录
     * @Author 零风
     * @Date  2021/12/20
     */
    void refundApply();

    /**
     * 订单完成之后相关业务处理
     * -自动更新订单状态日志
     * @Author 零风
     * @Date  2021/12/20
     */
    void complete();

    /**
     * 订单支付成功之后相关业务处理
     * -更新订单日志、支付记录、经验值、公共积分、零售商订单处理、账单记录、佣金记录等
     * @Author 零风
     * @Date  2021/12/20
     */
    void orderPaySuccessAfter();

    /**
     * 订单收货之后相关业务处理
     * -处理订单状态日志、佣金、公共积分等相关业务
     * @Author 零风
     * @Date  2021/12/20
     */
    void orderReceiving();

    /**
     * 订单自动完成
     * @Author 零风
     * @Date  2021/12/20
     */
    void autoComplete();

    /**
     * 订单自动收货
     * @Author 零风
     * @Date  2021/12/20
     */
    void autoReceiving();

    /**
     * 自动取消未支付订单
     * @Author 零风
     * @Date  2021/12/20
     */
    void autoCancel();
}
