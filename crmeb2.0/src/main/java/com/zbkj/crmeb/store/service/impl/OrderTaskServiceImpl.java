package com.zbkj.crmeb.store.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.constants.Constants;
import com.exception.CrmebException;
import com.utils.DateUtil;
import com.utils.RedisUtil;
import com.zbkj.crmeb.front.service.OrderService;
import com.zbkj.crmeb.payment.service.OrderPayService;
import com.zbkj.crmeb.regionalAgency.model.RegionalAgency;
import com.zbkj.crmeb.regionalAgency.service.RegionalAgencyService;
import com.zbkj.crmeb.store.model.StoreOrder;
import com.zbkj.crmeb.store.model.StoreOrderStatus;
import com.zbkj.crmeb.store.model.StoreProductReply;
import com.zbkj.crmeb.store.model.Supplier;
import com.zbkj.crmeb.store.service.*;
import com.zbkj.crmeb.store.utilService.OrderUtils;
import com.zbkj.crmeb.store.vo.StoreOrderInfoOldVo;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.model.UserBill;
import com.zbkj.crmeb.user.service.UserBillService;
import com.zbkj.crmeb.user.service.UserService;
import com.zbkj.crmeb.wechat.service.impl.WechatSendMessageForMinService;
import com.zbkj.crmeb.wechat.vo.WechatSendMessageForOrderCancel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;
import java.util.List;

@Service
public class OrderTaskServiceImpl implements OrderTaskService {

    private static final Logger logger = LoggerFactory.getLogger(OrderTaskServiceImpl.class);

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private StoreOrderTaskService storeOrderTaskService;

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private WechatSendMessageForMinService wechatSendMessageForMinService;

    @Autowired
    private OrderUtils orderUtils;

    @Autowired
    private StoreOrderStatusService storeOrderStatusService;

    @Autowired
    private StoreOrderInfoService storeOrderInfoService;

    @Autowired
    private UserService userService;

    @Autowired
    private StoreProductReplyService storeProductReplyService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private OrderPayService orderPayService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private RegionalAgencyService regionalAgencyService;

    @Autowired
    private UserBillService userBillService;

    @Autowired
    private SupplierService supplierService;

    public static void main(String[] args) {
        //计算时间
        String datetime = DateUtil.addDay(new Date(),7,Constants.DATE_FORMAT_DATE);
        String datetime2 = DateUtil.addDay("2021-12-10",7,Constants.DATE_FORMAT_DATE);
        String datetime3 = DateUtil.nowDateTime(Constants.DATE_FORMAT_DATE);
        System.out.println(datetime.compareTo(datetime3));
        System.out.println(datetime2.compareTo(datetime3));
        System.out.println(datetime3.compareTo(datetime3));
    }

    @Override
    public void settlementSupplierOrder(StoreOrder order) {
        //得到-供应商信息
        Supplier supplier=supplierService.getById(order.getMerId());
        if(supplier == null){
            logger.error("订单自动完成：供应商结算失败!供应商不存在！orderId= " + order.getId());
            return;
        }

        //得到-供应商管理用户信息
        User supplierUser=userService.getById(supplier.getUid());
        if(supplierUser == null){
            logger.error("订单自动完成：供应商结算失败!供应商管理用户不存在！orderId= " + order.getId());
            return;
        }

        //保存-供应商管理用户余额记录
        UserBill userBill = userBillService.getUserBill(    // 供应商管理用户余额记录
                supplierUser.getUid(),
                order.getId().toString(),
                1,
                Constants.USER_BILL_CATEGORY_MONEY,
                Constants.USER_BILL_TYPE_SUPPLIER_ORDER_settlement,
                order.getPayPrice(),
                supplierUser.getNowMoney(),
                ""
        );
        userBillService.save(userBill);

        //更新-供应商管理用户余额
        userService.operationNowMoney(supplierUser.getUid(), order.getPayPrice(), supplierUser.getNowMoney(), "add");
    }

    @Override
    public void settlementRaOrder(StoreOrder order) {
        //得到-区域代理信息
        RegionalAgency regionalAgency = regionalAgencyService.getById(order.getMerId());
        if(regionalAgency == null){
            logger.error("订单自动完成：区域代理结算失败!区域代理不存在！orderId= " + order.getId());
            return;
        }

        //得到-区域代理管理用户信息
        User raUser=userService.getById(regionalAgency.getUid());
        if(raUser == null){
            logger.error("订单自动完成：区域代理结算失败!区域代理管理用户不存在！orderId= " + order.getId());
            return;
        }

        //保存-代理商用户余额记录
        UserBill userBill = userBillService.getUserBill(    // 区域代理结算订单
                raUser.getUid(),
                order.getId().toString(),
                1,
                Constants.USER_BILL_CATEGORY_MONEY,
                Constants.USER_BILL_TYPE_RA_ORDER_settlement,
                order.getPayPrice(),
                raUser.getNowMoney(),
                ""
        );
        userBillService.save(userBill);

        //更新-区域代理管理用户余额
        userService.operationNowMoney(raUser.getUid(), order.getPayPrice(), raUser.getNowMoney(), "add");
    }

    @Override
    public void autoReceiving() {
        //得到-待收货的订单
        List<StoreOrder> storeOrderList = storeOrderService.getStayReceivingOrderList();
        if(storeOrderList.size() == 0)return;

        //循环处理
        String thisDate = DateUtil.nowDateTime(Constants.DATE_FORMAT_DATE);//当前日期
        for (StoreOrder storeOrder:storeOrderList) {
            //得到订单发货状态
            StoreOrderStatus orderStatus = storeOrderStatusService.getLastByOrderId(storeOrder.getId());
            if(orderStatus == null)continue;
            if (!Constants.ORDER_LOG_DELIVERY_VI.equals(orderStatus.getChangeType())&&!Constants.ORDER_LOG_EXPRESS.equals(orderStatus.getChangeType())) {
                logger.error("订单自动收货：订单记录最后一条不是发货状态，orderId = " + storeOrder.getId());
                continue ;
            }

            //计算时间，从支付日期开始是否超过七天
            String datetime = DateUtil.addDay(storeOrder.getPayTime(),7,Constants.DATE_FORMAT_DATE);
            if(datetime.compareTo(thisDate) <= 0){
                //进入收货
                orderService.take(storeOrder.getId());
            }
        }
    }

    @Override
    public void cancelByUser() {
        String redisKey = Constants.ORDER_TASK_REDIS_KEY_AFTER_CANCEL_BY_USER;
        Long size = redisUtil.getListSize(redisKey);
        logger.info("取消订单之后相关业务逻辑-OrderTaskServiceImpl.cancelByUser | size:" + size);
        if(size < 1){
            return;
        }

        //循环处理
        for (int i = 0; i < size; i++) {
            //如果10秒钟拿不到一个数据，那么退出循环
            Object data = redisUtil.getRightPop(redisKey, 10L);
            if(null == data){
                continue;
            }

            try{
                //设置参数调用得到结果
                StoreOrder storeOrder = storeOrderService.getById(Integer.valueOf(data.toString()));
                boolean result = storeOrderTaskService.cancelByUser(storeOrder);
                if(!result){
                    logger.info("取消订单之后相关业务逻辑-OrderTaskServiceImpl.cancelByUser | 执行结果:" + result);
                    redisUtil.lPush(redisKey, data);
                }else{
                    WechatSendMessageForOrderCancel orderCancel = new WechatSendMessageForOrderCancel(
                            "暂无",DateUtil.nowDateTimeStr(),"","暂无",orderUtils.getPayTypeStrByOrder(storeOrder),
                            orderUtils.getStoreNameAndCarNumString(storeOrder.getId()),storeOrder.getOrderId(),
                            storeOrder.getStatus()+"",storeOrder.getPayPrice()+"",storeOrder.getCreateTime()+"",
                            "CRMEB","暂无",storeOrder.getOrderId(),"CRMEB","暂无"
                    );
                    wechatSendMessageForMinService.sendOrderCancelMessage(orderCancel,storeOrder.getUid());
                }
            }catch (Exception e){
                logger.info("取消订单之后相关业务逻辑-OrderTaskServiceImpl.cancelByUser | 发生错误:" + e.getMessage());
                redisUtil.lPush(redisKey, data);
            }
        }
    }

    @Override
    public void refundApply() {
        String redisKey = Constants.ORDER_TASK_REDIS_KEY_AFTER_REFUND_BY_USER;
        Long size = redisUtil.getListSize(redisKey);
        logger.info("订单退款之后相关业务处理-OrderTaskServiceImpl.refundApply | size:" + size);
        if(size < 1) return;

        //循环处理
        for (int i = 0; i < size; i++) {
            //如果10秒钟拿不到一个数据，那么退出循环
            Object orderId = redisUtil.getRightPop(redisKey, 10L);
            if(null == orderId) continue;

            try{
                //得到订单
                StoreOrder storeOrder = storeOrderService.getById(Integer.valueOf(orderId.toString()));
                if (ObjectUtil.isNull(storeOrder)) {
                    throw new CrmebException("订单退款之后相关业务处理-订单不存在,orderNo = " + orderId);
                }

                //退款之后处理
                boolean result = storeOrderTaskService.refundOrder(storeOrder);
                if(!result){
                    logger.error("订单退款之后相关业务处理-订单退款错误：result = " + result);
                    redisUtil.lPush(redisKey, orderId);
                }
            }catch (Exception e){
                logger.error("订单退款之后相关业务处理-订单退款错误：" + e.getMessage());
                redisUtil.lPush(redisKey, orderId);
            }
        }
    }

    @Override
    public void complete() {
        String redisKey = Constants.ORDER_TASK_REDIS_KEY_AFTER_COMPLETE_BY_USER;
        Long size = redisUtil.getListSize(redisKey);
        logger.info("订单完成之后相关业务处理-OrderTaskServiceImpl.complete | size:" + size);
        if(size < 1){
            return;
        }

        //循环处理
        for (int i = 0; i < size; i++) {
            //如果10秒钟拿不到一个数据，那么退出循环
            Object data = redisUtil.getRightPop(redisKey, 10L);
            if(null == data){
                continue;
            }

            try{
                //根据订单ID-得到订单信息
                StoreOrder storeOrder = storeOrderService.getById(String.valueOf(data));

                //验证非空
                if (ObjectUtil.isNull(storeOrder)) {
                    logger.error("订单完成之后相关业务处理-OrderTaskServiceImpl.complete | 订单不存在，orderNo: " + data);
                    redisUtil.lPush(redisKey, data);
                    continue;
                }

                //得到结果
                boolean result = storeOrderTaskService.complete(storeOrder);
                if(!result){
                    logger.error("订单完成之后相关业务处理-OrderTaskServiceImpl.complete | result:" + result);
                    redisUtil.lPush(redisKey, data);
                }
            }catch (Exception e){
                logger.error("订单完成之后相关业务处理-OrderTaskServiceImpl.complete | 发生错误:" + e.getMessage());
                redisUtil.lPush(redisKey, data);
            }
        }
    }

    @Override
    public void orderPaySuccessAfter() {
        //得到redsi-key
        String redisKey = Constants.ORDER_TASK_PAY_SUCCESS_AFTER; // task处理

        //从缓存里读取-size
        Long size = redisUtil.getListSize(redisKey);
        logger.info("订单支付成功之后-OrderTaskServiceImpl.orderPaySuccessAfter | size:" + size);
        if(size < 1)return;

        //循环处理
        for (int i = 0; i < size; i++) {
            //如果10秒钟拿不到一个数据，那么退出循环
            Object data = redisUtil.getRightPop(redisKey, 10L);
            if(null == data){
                continue;
            }

            try{
                //根据订单ID-得到订单信息
                StoreOrder storeOrder = storeOrderService.getById(Integer.valueOf(data.toString()));

                //验证非空
                if (ObjectUtil.isNull(storeOrder)) {
                    logger.error("订单支付成功之后-OrderTaskServiceImpl.orderPaySuccessAfter | 订单不存在，orderNo: " + data);
                    continue;
                }

                //执行-订单支付后续处理接口
                boolean result = orderPayService.paySuccess(storeOrder);
                if(!result){
                    //未执行成功-重新放入
                    redisUtil.lPush(redisKey, data);
                }
            }catch (Exception e){
                logger.error("订单支付成功之后-OrderTaskServiceImpl.orderPaySuccessAfter | 发生错误 : " + e.getMessage());
                redisUtil.lPush(redisKey, data);
            }
        }
    }

    @Override
    public void autoCancel() {
        String redisKey = Constants.ORDER_AUTO_CANCEL_KEY;
        Long size = redisUtil.getListSize(redisKey);
        logger.info("自动取消未支付订单-OrderTaskServiceImpl.autoCancel | size:" + size);
        if(size < 1){
            return;
        }

        //循环处理
        for (int i = 0; i < size; i++) {
            //如果10秒钟拿不到一个数据，那么退出循环
            Object data = redisUtil.getRightPop(redisKey, 10L);
            if(null == data){
                continue;
            }

            try{
                //得到订单信息
                StoreOrder storeOrder = storeOrderService.getByOderId(String.valueOf(data));
                if (ObjectUtil.isNull(storeOrder)) {
                    logger.error("自动取消未支付订单-OrderTaskServiceImpl.autoCancel | 订单不存在，orderNo: " + data);
                    throw new CrmebException("订单不存在，orderNo: " + data);
                }

                //结果
                boolean result = storeOrderTaskService.autoCancel(storeOrder);
                if(!result){
                    logger.error("自动取消未支付订单-OrderTaskServiceImpl.autoCancel | 结果 : " + result);
                    redisUtil.lPush(redisKey, data);
                }
            }catch (Exception e){
                logger.error("自动取消未支付订单-OrderTaskServiceImpl.autoCancel | 发生错误: " + e.getMessage());
                redisUtil.lPush(redisKey, data);
            }
        }
    }

    @Override
    public void orderReceiving() {
        String redisKey = Constants.ORDER_TASK_REDIS_KEY_AFTER_TAKE_BY_USER;
        Long size = redisUtil.getListSize(redisKey);
        logger.info("订单收货之后相关业务处理-OrderTaskServiceImpl.orderReceiving | size:" + size);
        if(size < 1){
            return;
        }

        //循环处理
        for (int i = 0; i < size; i++) {
            //如果10秒钟拿不到一个数据，那么退出循环
            Object id = redisUtil.getRightPop(redisKey, 10L);
            if(null == id){
                continue;
            }

            try{
                //结果
                Boolean result = storeOrderTaskService.orderReceiving(Integer.valueOf(id.toString()));
                if(!result){
                    logger.error("订单收货之后相关业务处理-OrderTaskServiceImpl.orderReceiving | 结果:" + result);
                    redisUtil.lPush(redisKey, id);
                }
            }catch (Exception e){
                logger.error("订单收货之后相关业务处理-OrderTaskServiceImpl.orderReceiving | 发生错误:" + e.getMessage());
                redisUtil.lPush(redisKey, id);
            }
        }
    }

    @Override
    public void autoComplete() {
        // 查找所有收货状态订单
        List<StoreOrder> orderList = storeOrderService.findIdAndUidListByReceipt();
        if (CollUtil.isEmpty(orderList)) {
            return ;
        }

        // 根据订单状态表判断订单是否可以自动完成
        logger.info("订单自动完成-OrderTaskServiceImpl.autoComplete | size:"+orderList.size());
        for (StoreOrder order : orderList) {
            // 得到订单状态
            StoreOrderStatus orderStatus = storeOrderStatusService.getLastByOrderId(order.getId());
            if (!Constants.ORDER_LOG_USER_TAKE_DELIVERY.equals(orderStatus.getChangeType())) {
                logger.error("订单自动完成：订单记录最后一条不是收货状态，orderId = " + order.getId());
                continue ;
            }

            // 判断是否到自动完成时间（收货时间向后偏移7天）
            String comTime = DateUtil.addDay(orderStatus.getCreateTime(), 7, Constants.DATE_FORMAT);
            int compareDate = DateUtil.compareDate(comTime, DateUtil.nowDateTime(Constants.DATE_FORMAT), Constants.DATE_FORMAT);
            if (compareDate > 0) {
                continue ;
            }

            /**
             * ---------------
             * 自动好评转完成
             * ---------------
             */
            // 获取订单详情
            List<StoreOrderInfoOldVo> orderInfoVoList = storeOrderInfoService.getOrderListByOrderId(order.getId());
            if (CollUtil.isEmpty(orderInfoVoList)) {
                logger.error("订单自动完成：无订单详情数据，orderId = " + order.getId());
                continue;
            }
            List<StoreProductReply> replyList = CollUtil.newArrayList();
            User user = userService.getById(order.getUid());

            // 生成评论
            for (StoreOrderInfoOldVo orderInfo : orderInfoVoList) {
                // 判断是否已评论
                if (orderInfo.getInfo().getIsReply().equals(1)) {
                    continue;
                }

                // 得到-评论对象
                StoreProductReply reply = storeProductReplyService.getStoreProductReply(    // 系统默认好评
                        order.getUid(),order.getId(),orderInfo.getProductId(),
                        orderInfo.getUnique(),Constants.STORE_REPLY_TYPE_PRODUCT,
                        5,5,
                        "系统默认好评！","",
                        null,null,
                        user.getNickname(),user.getAvatar(),
                        orderInfo.getInfo().getSku()
                );
                replyList.add(reply);
            }

            // 订单状态
            order.setStatus(Constants.ORDER_STATUS_INT_COMPLETE);

            //执行
            Boolean execute = transactionTemplate.execute(e -> {
                storeOrderService.updateById(order);
                storeProductReplyService.saveBatch(replyList);
                return Boolean.TRUE;
            });

            //执行结果
            if (execute) {
                redisUtil.lPush(Constants.ORDER_TASK_REDIS_KEY_AFTER_COMPLETE_BY_USER, order.getId());//订单完成之后业务处理
            } else {
                logger.error("订单自动完成：更新数据库失败，orderId = " + order.getId());
            }
        }
    }
}
