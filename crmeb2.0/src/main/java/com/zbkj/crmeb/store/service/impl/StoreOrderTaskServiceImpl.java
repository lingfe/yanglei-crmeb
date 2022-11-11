package com.zbkj.crmeb.store.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.constants.*;
import com.exception.CrmebException;
import com.utils.DateUtil;
import com.utils.RedisUtil;
import com.zbkj.crmeb.bargain.service.StoreBargainService;
import com.zbkj.crmeb.combination.model.StorePink;
import com.zbkj.crmeb.combination.service.StoreCombinationService;
import com.zbkj.crmeb.combination.service.StorePinkService;
import com.zbkj.crmeb.finance.model.InvoiceRecord;
import com.zbkj.crmeb.finance.service.InvoiceRecordService;
import com.zbkj.crmeb.integal.model.PublicIntegalRecord;
import com.zbkj.crmeb.integal.service.PublicIntegalRecordService;
import com.zbkj.crmeb.marketing.model.StoreCouponUser;
import com.zbkj.crmeb.marketing.service.StoreCouponUserService;
import com.zbkj.crmeb.regionalAgency.model.RegionalUser;
import com.zbkj.crmeb.regionalAgency.service.RegionalUserService;
import com.zbkj.crmeb.seckill.service.StoreSeckillService;
import com.zbkj.crmeb.sms.service.SmsService;
import com.zbkj.crmeb.store.model.StoreOrder;
import com.zbkj.crmeb.store.request.StoreProductStockRequest;
import com.zbkj.crmeb.store.service.*;
import com.zbkj.crmeb.store.utilService.OrderUtils;
import com.zbkj.crmeb.store.vo.StoreOrderInfoOldVo;
import com.zbkj.crmeb.system.model.SystemAdmin;
import com.zbkj.crmeb.system.service.SystemAdminService;
import com.zbkj.crmeb.system.service.SystemConfigService;
import com.zbkj.crmeb.user.model.*;
import com.zbkj.crmeb.user.service.*;
import com.zbkj.crmeb.wechat.service.TemplateMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class StoreOrderTaskServiceImpl implements StoreOrderTaskService {
    //日志
    private static final Logger logger = LoggerFactory.getLogger(StoreOrderTaskServiceImpl.class);

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private StoreOrderInfoService storeOrderInfoService;

    @Autowired
    private StoreProductService storeProductService;

    @Autowired
    private StoreOrderStatusService storeOrderStatusService;

    @Autowired
    private UserService userService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private StoreSeckillService storeSeckillService;

    @Autowired
    private StoreBargainService storeBargainService;

    @Autowired
    private StoreCombinationService storeCombinationService;

    @Autowired
    private UserBillService userBillService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private UserBrokerageRecordService userBrokerageRecordService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private SystemAdminService systemAdminService;

    @Autowired
    private UserTokenService userTokenService;

    @Autowired
    private TemplateMessageService templateMessageService;

    @Autowired
    private OrderUtils orderUtils;

    @Autowired
    private StorePinkService storePinkService;

    @Autowired
    private UserIntegralRecordService userIntegralRecordService;

    @Autowired
    private UserLevelService userLevelService;

    @Autowired
    private StoreCouponUserService couponUserService;

    @Autowired
    private PublicIntegalRecordService publicIntegalRecordService;

    @Autowired
    private RegionalUserService regionalUserService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private InvoiceRecordService invoiceRecordService;

    public static void main(String[] args) {
        // 计算解冻时间
        Integer tt=Integer.valueOf(Optional.ofNullable("0").orElse("0"));
        Long thawTime = DateUtil.calculationThawDatetime(0);
        Long thawTime2 = System.currentTimeMillis();
        List<PublicIntegalRecord> publicIntegalRecordList = new ArrayList<>();
        publicIntegalRecordList.stream().forEach(e->e.setStatus(IntegralRecordConstants.PUBLIC_INTEGRAL_RECORD_STATUS_INVALIDATION));
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Error.class, CrmebException.class})
    public Boolean cancelByUser(StoreOrder storeOrder) {
        try{
            /*
            * 1、修改订单状态 （用户操作的时候已处理）
            * 2、写订单日志
            * 3、回滚库存
            * 4、回滚优惠券
            * 5、回滚积分
            * */
            //写订单日志
            storeOrderStatusService.createLog(storeOrder.getId(), "cancel_order", "取消订单");
            return rollbackStock(storeOrder);
        }catch (Exception e){
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Error.class, CrmebException.class})
    public Boolean complete(StoreOrder storeOrder) {
        /*
         * 1、修改订单状态 （用户操作的时候已处理）
         * 2、写订单日志
         * 3、保存有资格参与随机分配积分的用户
         * */
        try{
            // 处理发票
            if(storeOrder.getRiseId() > 0){
                //得到发票记录信息
                LambdaQueryWrapper<InvoiceRecord> lambdaQueryWrapper=new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(InvoiceRecord::getOrderId,storeOrder.getId());
                lambdaQueryWrapper.eq(InvoiceRecord::getRiseId,storeOrder.getRiseId());
                lambdaQueryWrapper.eq(InvoiceRecord::getUid,storeOrder.getUid());
                lambdaQueryWrapper.last(" limit 1 ");
                InvoiceRecord invoiceRecord = invoiceRecordService.getOne(lambdaQueryWrapper);
                if(invoiceRecord!=null){
                    //更新状态
                    invoiceRecord.setStatus(Constants.INVOICE_RECORD_1);
                    invoiceRecordService.updateById(invoiceRecord);
                }
            }

            // 分账-订单完成之后处理
            redisUtil.lPush(Constants.ORDER_TASK_payProfitSharing, storeOrder.getId()); // 分账-订单完成之后处理

            // 保存-订单状态日志
            storeOrderStatusService.createLog(storeOrder.getId(), "check_order_over", "用户评价");

            // 保存-可分配积分用户
            LambdaQueryWrapper<RegionalUser> regionalUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
            regionalUserLambdaQueryWrapper.eq(RegionalUser::getUid,storeOrder.getUid());
            regionalUserLambdaQueryWrapper.eq(RegionalUser::getRaId,0);//系统
            List<RegionalUser> regionalUserList = regionalUserService.list(regionalUserLambdaQueryWrapper);//先匹配是否已经存在
            if(regionalUserList == null|| regionalUserList.size() == 0){
                //不存在-创建
                RegionalUser regionalUser=new RegionalUser();
                regionalUser.setRaId(0);//系统
                regionalUser.setUid(storeOrder.getUid());
                regionalUserService.save(regionalUser);
            }else{
                //存在-则修改日期
                RegionalUser regionalUser=regionalUserList.get(0);
                regionalUser.setUpdateTime(new DateTime());
                regionalUserService.updateById(regionalUser);
            }
            return true;
        }catch (Exception e){
            logger.error("订单完成之后相关业务处理-发生错误："+e.getMessage());
            return false;
        }
    }

    /**
     * 回滚库存
     * @param storeOrder 订单信息
     */
    private Boolean rollbackStock(StoreOrder storeOrder) {
        try{
            // 查找出商品详情
            List<StoreOrderInfoOldVo> orderInfoVoList = storeOrderInfoService.getOrderListByOrderId(storeOrder.getId());
            if(null == orderInfoVoList || orderInfoVoList.size() < 1){
                return true;
            }

            // 兼容处理秒杀数据退款
            // 秒杀商品回滚库存和销量
            if(null != storeOrder.getSeckillId() && storeOrder.getSeckillId() > 0){
                for (StoreOrderInfoOldVo orderInfoVo : orderInfoVoList) {
                    StoreProductStockRequest stockRequest = new StoreProductStockRequest();
                    stockRequest.setSeckillId(storeOrder.getSeckillId());
                    stockRequest.setProductId(orderInfoVo.getProductId());
                    stockRequest.setAttrId(orderInfoVo.getInfo().getAttrValueId());
                    stockRequest.setOperationType("add");
                    stockRequest.setType(1);
                    stockRequest.setSuk(orderInfoVo.getInfo().getSku());
                    if(orderInfoVo.getInfo().getPayNum() < 1){
                        //如果取不到值，则跳过
                        continue;
                    }
                    stockRequest.setNum(orderInfoVo.getInfo().getPayNum());
                    storeSeckillService.stockAddRedis(stockRequest);
                }
            } else if (ObjectUtil.isNotNull(storeOrder.getBargainId()) && storeOrder.getBargainId() > 0) { // 砍价商品回滚销量库存
                for (StoreOrderInfoOldVo orderInfoVo : orderInfoVoList) {
                    StoreProductStockRequest stockRequest = new StoreProductStockRequest();
                    stockRequest.setBargainId(storeOrder.getBargainId());
                    stockRequest.setProductId(orderInfoVo.getProductId());
                    stockRequest.setAttrId(orderInfoVo.getInfo().getAttrValueId());
                    stockRequest.setOperationType("add");
                    stockRequest.setType(2);
                    stockRequest.setSuk(orderInfoVo.getInfo().getSku());
                    if(orderInfoVo.getInfo().getPayNum() < 1){
                        //如果取不到值，则跳过
                        continue;
                    }
                    stockRequest.setNum(orderInfoVo.getInfo().getPayNum());
                    storeBargainService.stockAddRedis(stockRequest);
                }
            } else if (ObjectUtil.isNotNull(storeOrder.getCombinationId()) && storeOrder.getCombinationId() > 0) { // 拼团商品回滚销量库存
                for (StoreOrderInfoOldVo orderInfoVo : orderInfoVoList) {
                    StoreProductStockRequest stockRequest = new StoreProductStockRequest();
                    stockRequest.setCombinationId(storeOrder.getCombinationId());
                    stockRequest.setProductId(orderInfoVo.getProductId());
                    stockRequest.setAttrId(orderInfoVo.getInfo().getAttrValueId());
                    stockRequest.setOperationType("add");
                    stockRequest.setType(3);
                    stockRequest.setSuk(orderInfoVo.getInfo().getSku());
                    if(orderInfoVo.getInfo().getPayNum() < 1){
                        //如果取不到值，则跳过
                        continue;
                    }
                    stockRequest.setNum(orderInfoVo.getInfo().getPayNum());
                    storeCombinationService.stockAddRedis(stockRequest);
                }
            } else { // 正常商品回滚销量库存
                for (StoreOrderInfoOldVo orderInfoVo : orderInfoVoList) {
                    StoreProductStockRequest stockRequest = new StoreProductStockRequest();
                    stockRequest.setProductId(orderInfoVo.getProductId());
                    stockRequest.setAttrId(orderInfoVo.getInfo().getAttrValueId());
                    stockRequest.setOperationType("add");
                    stockRequest.setType(0);
                    stockRequest.setSuk(orderInfoVo.getInfo().getSku());
                    if(orderInfoVo.getInfo().getPayNum() < 1){
                        //如果取不到值，则跳过
                        continue;
                    }
                    stockRequest.setNum(orderInfoVo.getInfo().getPayNum());
                    storeProductService.stockAddRedis(stockRequest);
                }
            }
        }catch (Exception e){
            throw new CrmebException(e.getMessage());
        }

        return true;
    }

    /**
     * 订单退款处理
     * 退款得时候根据userBill 来进行回滚
     */
    @Override
    public Boolean refundOrder(StoreOrder storeOrder) {
        /**
         * 1、写订单日志
         * 2、回滚消耗积分
         * 3、回滚获得积分
         * 4、回滚冻结期佣金
         * 5、回滚经验
         * 6、回滚库存
         * 7、发送通知
         * 实际上2-5就是user数据的处理+userBill的记录
         */
        // 获取下单用户对象
        User user = userService.getById(storeOrder.getUid());
        if (ObjectUtil.isNull(user)) {
            logger.error("订单退款处理，对应的用户不存在,storeOrder===>" + storeOrder);
            return Boolean.FALSE;
        }

        // 定义账单list集合
        List<UserBill> userBillList = CollUtil.newArrayList();

        // 回滚积分-消耗和获得
        // 通过userBill记录获取订单之前处理的记录
        List<UserBill> userBills = userBillService.findListByOrderIdAndUid(storeOrder.getId(), storeOrder.getUid());

        // 回滚经验
        List<UserBill> experienceList = userBills.stream()
                .filter(e -> e.getCategory().equals(Constants.USER_BILL_CATEGORY_EXPERIENCE))
                .collect(Collectors.toList());
        experienceList.forEach(bill -> {
            user.setExperience(user.getExperience() - bill.getNumber().intValue());

            //账单记录
            UserBill userBill = userBillService.getUserBill(    // 商品退款退还经验值
                    bill.getUid(),
                    bill.getLinkId(),
                    0,
                    bill.getCategory(),
                    Constants.USER_BILL_TYPE_PAY_PRODUCT_REFUND,
                    bill.getNumber(),
                    new BigDecimal(user.getExperience()).setScale(2),
                    ""
            );
            userBillList.add(userBill);
        });

        // 回滚积分
        List<UserIntegralRecord> integralRecordList = userIntegralRecordService.findListByOrderIdAndUid(storeOrder.getOrderId(), storeOrder.getUid());
        integralRecordList.forEach(record -> {
            if (record.getType().equals(IntegralRecordConstants.INTEGRAL_RECORD_TYPE_SUB)) {// 订单抵扣部分
                user.setIntegral(user.getIntegral().add(record.getIntegral()));
                record.setId(null);
                record.setTitle(IntegralRecordConstants.BROKERAGE_RECORD_TITLE_REFUND);
                record.setType(IntegralRecordConstants.INTEGRAL_RECORD_TYPE_ADD);
                record.setBalance(user.getIntegral());
                record.setCreateTime(DateUtil.nowDateTime());
                record.setUpdateTime(DateUtil.nowDateTime());
                record.setMark(StrUtil.format("订单退款，返还支付扣除得{}积分", record.getIntegral()));
                record.setStatus(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE);
            }else if (record.getType().equals(IntegralRecordConstants.INTEGRAL_RECORD_TYPE_ADD)) {// 冻结积分部分
                record.setStatus(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_INVALIDATION);
            }
        });

        // add and update
        List<UserIntegralRecord> addIntegralList = integralRecordList.stream().filter(e -> ObjectUtil.isNull(e.getId())).collect(Collectors.toList());
        List<UserIntegralRecord> updateIntegralList = integralRecordList.stream().filter(e -> ObjectUtil.isNotNull(e.getId())).collect(Collectors.toList());

        // 佣金处理：只处理冻结期佣金
        // 查询佣金记录
        List<UserBrokerageRecord> brokerageRecordList = CollUtil.newArrayList();
        List<UserBrokerageRecord> recordList = userBrokerageRecordService.findListByLinkIdAndLinkType(
                storeOrder.getId().toString(),
                BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_ORDER);
        if (CollUtil.isNotEmpty(recordList)) {
            recordList.forEach(r -> {
                //创建、冻结期佣金置为失效状态
                if (r.getStatus() < BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_COMPLETE) {
                    r.setStatus(BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_INVALIDATION);
                    r.setPm(BrokerageRecordConstants.BROKERAGE_RECORD_TYPE_SUB);
                    r.setTitle(new StringBuffer(r.getTitle()).append("(已退款)").toString());
                    brokerageRecordList.add(r);
                }
            });
        }

        // 公共积分库
        LambdaQueryWrapper<PublicIntegalRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(PublicIntegalRecord::getUid, user.getUid());
        lqw.eq(PublicIntegalRecord::getLinkId, String.valueOf(storeOrder.getId()));
        lqw.in(PublicIntegalRecord::getStatus,
                IntegralRecordConstants.PUBLIC_INTEGRAL_RECORD_STATUS_CREATE,
                IntegralRecordConstants.PUBLIC_INTEGRAL_RECORD_STATUS_FROZEN);
        List<PublicIntegalRecord> publicIntegalRecordList = publicIntegalRecordService.list(lqw);

        // 公共积分库-相关业务
        List<UserIntegralRecord> userIntegralRecordList = null;
        if(publicIntegalRecordList !=null && publicIntegalRecordList.size() > 0) {
            // 设置状态、描述
            publicIntegalRecordList.stream().forEach(e->{
                e.setStatus(IntegralRecordConstants.PUBLIC_INTEGRAL_RECORD_STATUS_INVALIDATION)
                        .setTitle(new StringBuffer(e.getTitle()).append("(已退款)").toString());
            });

            // 处理返推荐人待结算积分记录
            LambdaQueryWrapper<UserIntegralRecord> lqw2 = Wrappers.lambdaQuery();
            lqw2.eq(UserIntegralRecord::getLinkType,IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_PUBLIC);
            lqw2.eq(UserIntegralRecord::getType,IntegralRecordConstants.INTEGRAL_RECORD_TYPE_ADD);
            lqw2.eq(UserIntegralRecord::getStatus, IntegralRecordConstants.INTEGRAL_RECORD_STATUS_DJS);
            List<Integer> prIds = publicIntegalRecordList.stream().map(PublicIntegalRecord::getId).collect(Collectors.toList());
            lqw2.in(UserIntegralRecord::getLinkId, prIds);
            userIntegralRecordList = userIntegralRecordService.list(lqw2);
            userIntegralRecordList.stream().forEach(e->{
                e.setStatus(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_INVALIDATION).setTitle(new StringBuffer(e.getTitle()).append("(已退款)").toString());
                e.setType(IntegralRecordConstants.INTEGRAL_RECORD_TYPE_SUB);
            });
        }

        // 执行
        List<UserIntegralRecord> finalUserIntegralRecordList = userIntegralRecordList;
        Boolean execute = transactionTemplate.execute(e -> {
            //写订单日志
            storeOrderStatusService.saveRefund(storeOrder.getId(), storeOrder.getRefundPrice(), "成功");

            // 更新用户数据
            userService.updateById(user);

            // 用户账单记录处理
            userBillService.saveBatch(userBillList);

            // 积分部分
            if (CollUtil.isNotEmpty(addIntegralList)) {
                userIntegralRecordService.saveBatch(addIntegralList);
            }
            if (CollUtil.isNotEmpty(updateIntegralList)) {
                userIntegralRecordService.updateBatchById(updateIntegralList);
            }

            // 公共积分库
            if(CollUtil.isNotEmpty(publicIntegalRecordList)){
                publicIntegalRecordService.updateBatchById(publicIntegalRecordList);
            }
            if(CollUtil.isNotEmpty(finalUserIntegralRecordList)){
                userIntegralRecordService.updateBatchById(finalUserIntegralRecordList);
            }

            // 佣金处理
            if (CollUtil.isNotEmpty(brokerageRecordList)) {
                userBrokerageRecordService.updateBatchById(brokerageRecordList);
            }

            // 经验处理
            userLevelService.upLevel(user);

            // 回滚库存
            rollbackStock(storeOrder);

            // 更新订单退款状态
            StoreOrder tempOrder = new StoreOrder();
            tempOrder.setId(storeOrder.getId());
            tempOrder.setRefundStatus(2);
            storeOrderService.updateById(tempOrder);

            // 拼团状态处理
            if (storeOrder.getCombinationId() > 0) {
                StorePink storePink = storePinkService.getByOrderId(storeOrder.getOrderId());
                storePink.setStatus(3);
                storePink.setIsRefund(true);
                storePinkService.updateById(storePink);
            }

            // 退优惠券
            if (storeOrder.getCouponId() > 0 ) {
                StoreCouponUser couponUser = couponUserService.getById(storeOrder.getCouponId());
                couponUser.setStatus(CouponConstants.STORE_COUPON_USER_STATUS_USABLE);
                couponUserService.updateById(couponUser);
            }
            return Boolean.TRUE;
        });
        return execute;
    }

    @Override
    public Boolean autoCancel(StoreOrder storeOrder) {
        // 判断订单是否支付
        if (storeOrder.getPaid()) {
            return Boolean.TRUE;
        }

        // 订单是否删除
        if (storeOrder.getIsDel() || storeOrder.getIsSystemDel()) {
            return Boolean.TRUE;
        }

        // 获取过期时间
        String cancelStr;
        DateTime cancelTime;
        if (storeOrder.getBargainId() > 0 || storeOrder.getSeckillId() > 0 || storeOrder.getCombinationId() > 0) {
            cancelStr = systemConfigService.getValueByKey("order_activity_time");
        } else {
            cancelStr = systemConfigService.getValueByKey("order_cancel_time");
        }

        // 如果为空默认=1
        if (StrUtil.isBlank(cancelStr)) {
            cancelStr = "1";
        }

        //计算超时时间
        cancelTime = cn.hutool.core.date.DateUtil.offset(storeOrder.getCreateTime(), DateField.HOUR_OF_DAY, Integer.parseInt(cancelStr));
        long between = cn.hutool.core.date.DateUtil.between(cancelTime, cn.hutool.core.date.DateUtil.date(), DateUnit.SECOND, false);
        if (between < 0) {// 未到过期时间继续循环
            return Boolean.FALSE;
        }

        // 删除订单
        storeOrder.setIsDel(true).setIsSystemDel(true);

        //执行
        Boolean execute = transactionTemplate.execute(e -> {
            storeOrderService.updateById(storeOrder);
            //写订单日志
            storeOrderStatusService.createLog(storeOrder.getId(), "cancel", "到期未支付系统自动取消");
            return Boolean.TRUE;
        });

        //执行结果
        if (execute) {
            // 回滚库存
            rollbackStock(storeOrder);
        }

        //返回结果
        return execute;
    }

    @Override
    public Boolean orderReceiving(Integer orderId) {
        //得到订单信息
        StoreOrder storeOrder = storeOrderService.getById(orderId);
        if (ObjectUtil.isNull(storeOrder)) {
            logger.error(StrUtil.format("订单收货task处理，未找到订单，id={}", orderId));
        }

        //得到下单用户
        User user = userService.getById(storeOrder.getUid());

        // 获取佣金记录
        List<UserBrokerageRecord> recordList = userBrokerageRecordService.findListByLinkIdAndLinkType(storeOrder.getId().toString(), BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_ORDER);
        logger.info("收货处理佣金条数：" + recordList.size());
        for (UserBrokerageRecord record : recordList) {
            //验证-佣金是否为创建状态
            if (!record.getStatus().equals(BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_CREATE)) {
                logger.error(StrUtil.format("订单收货task处理，订单佣金记录不是创建状态，id={}", orderId));
                continue;
            }

            // 佣金进入冻结期
            record.setStatus(BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_FROZEN);

            // 计算解冻时间
            Long thawTime = DateUtil.calculationThawDatetime(record.getFrozenTime());

            //设置解冻时间
            record.setThawTime(thawTime);
        }

        // 获取公共积分记录
        List<PublicIntegalRecord> publicIntegalRecordList = publicIntegalRecordService.findListByOrderIdAndUid(String.valueOf(storeOrder.getId()), storeOrder.getUid());
        //publicIntegalRecordList.stream().forEach(e->{e.setStatus(IntegralRecordConstants.PUBLIC_INTEGRAL_RECORD_STATUS_COMPLETE); });
        //publicIntegalRecordList.forEach(p -> p.setStatus(IntegralRecordConstants.PUBLIC_INTEGRAL_RECORD_STATUS_COMPLETE));
        //list = list.stream().map(p -> p.setName("17_"+p.getName()) ).collect(Collectors.toList());
        logger.info("收货处理-公共积分条数：" + publicIntegalRecordList.size());
        for (PublicIntegalRecord record : publicIntegalRecordList) {
            // 公共积分直接完成
            record.setStatus(IntegralRecordConstants.PUBLIC_INTEGRAL_RECORD_STATUS_COMPLETE);
        }

        //执行
        Boolean execute = transactionTemplate.execute(e -> {
            // 日志
            storeOrderStatusService.createLog(storeOrder.getId(), Constants.ORDER_LOG_USER_TAKE_DELIVERY, Constants.ORDER_STATUS_STR_TAKE);

            // 分佣-佣金进入冻结期
            if (CollUtil.isNotEmpty(recordList)) {
                userBrokerageRecordService.updateBatchById(recordList);
            }

            // 更新公共积分
            if (CollUtil.isNotEmpty(publicIntegalRecordList)) {
                publicIntegalRecordService.updateBatchById(publicIntegalRecordList);
            }

            return Boolean.TRUE;
        });

        if (execute) {
            // 发送用户确认收货管理员提醒短信
            String smsSwitch = systemConfigService.getValueByKey(SmsConstants.SMS_CONFIG_ADMIN_REFUND_SWITCH);
            if (StrUtil.isNotBlank(smsSwitch) && smsSwitch.equals("1")) {
                // 查询可已发送短信的管理员
                List<SystemAdmin> systemAdminList = systemAdminService.findIsSmsList();
                if (CollUtil.isNotEmpty(systemAdminList)) {
                    // 发送短信
                    systemAdminList.forEach(admin -> {
                        smsService.sendOrderReceiptNotice(admin.getPhone(), storeOrder.getOrderId(), admin.getRealName());
                    });
                }
            }

            // 发送消息通知
            pushMessageOrder(storeOrder, user);
        }

        return execute;
    }

    /**
     * 发送消息通知
     * 根据用户类型发送
     * 公众号模板消息
     * 小程序订阅消息
     */
    private void pushMessageOrder(StoreOrder storeOrder, User user) {
        if (storeOrder.getIsChannel().equals(2)) {
            return;
        }
        if (!storeOrder.getPayType().equals(PayConstants.PAY_TYPE_WE_CHAT)) {
            return;
        }
        UserToken userToken;
        HashMap<String, String> temMap = new HashMap<>();
        // 公众号
        if (storeOrder.getIsChannel().equals(PayConstants.ORDER_PAY_CHANNEL_PUBLIC)) {
            userToken = userTokenService.getTokenByUserId(user.getUid(), UserConstants.USER_TOKEN_TYPE_WECHAT);
            if (ObjectUtil.isNull(userToken)) {
                return ;
            }
            // 发送微信模板消息
            temMap.put(Constants.WE_CHAT_TEMP_KEY_FIRST, "您购买的商品已确认收货！");
            temMap.put("keyword1", storeOrder.getOrderId());
            temMap.put("keyword2", "已收货");
            temMap.put("keyword3", DateUtil.nowDateTimeStr());
            temMap.put("keyword4", "详情请进入订单查看");
            temMap.put(Constants.WE_CHAT_TEMP_KEY_END, "感谢你的使用。");
            templateMessageService.pushTemplateMessage(Constants.WE_CHAT_TEMP_KEY_ORDER_RECEIVING, temMap, userToken.getToken());
            return;
        }
        // 小程序发送订阅消息
        userToken = userTokenService.getTokenByUserId(user.getUid(), UserConstants.USER_TOKEN_TYPE_ROUTINE);
        if (ObjectUtil.isNull(userToken)) {
            return ;
        }
        // 组装数据
        // 获取商品名称
        String storeNameAndCarNumString = orderUtils.getStoreNameAndCarNumString(storeOrder.getId());
        if (StrUtil.isBlank(storeNameAndCarNumString)) {
            return ;
        }
        if (storeNameAndCarNumString.length() > 20) {
            storeNameAndCarNumString = storeNameAndCarNumString.substring(0, 15) + "***";
        }
        temMap.put("character_string6", storeOrder.getOrderId());
        temMap.put("phrase4", "已收货");
        temMap.put("time7", DateUtil.nowDateTimeStr());
        temMap.put("thing1", storeNameAndCarNumString);
        temMap.put("thing5", "您购买的商品已确认收货！");
        templateMessageService.pushMiniTemplateMessage(Constants.WE_CHAT_PROGRAM_TEMP_KEY_ORDER_RECEIVING, temMap, userToken.getToken());
    }
}
