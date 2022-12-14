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
    //??????
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
        // ??????????????????
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
            * 1????????????????????? ????????????????????????????????????
            * 2??????????????????
            * 3???????????????
            * 4??????????????????
            * 5???????????????
            * */
            //???????????????
            storeOrderStatusService.createLog(storeOrder.getId(), "cancel_order", "????????????");
            return rollbackStock(storeOrder);
        }catch (Exception e){
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Error.class, CrmebException.class})
    public Boolean complete(StoreOrder storeOrder) {
        /*
         * 1????????????????????? ????????????????????????????????????
         * 2??????????????????
         * 3???????????????????????????????????????????????????
         * */
        try{
            // ????????????
            if(storeOrder.getRiseId() > 0){
                //????????????????????????
                LambdaQueryWrapper<InvoiceRecord> lambdaQueryWrapper=new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(InvoiceRecord::getOrderId,storeOrder.getId());
                lambdaQueryWrapper.eq(InvoiceRecord::getRiseId,storeOrder.getRiseId());
                lambdaQueryWrapper.eq(InvoiceRecord::getUid,storeOrder.getUid());
                lambdaQueryWrapper.last(" limit 1 ");
                InvoiceRecord invoiceRecord = invoiceRecordService.getOne(lambdaQueryWrapper);
                if(invoiceRecord!=null){
                    //????????????
                    invoiceRecord.setStatus(Constants.INVOICE_RECORD_1);
                    invoiceRecordService.updateById(invoiceRecord);
                }
            }

            // ??????-????????????????????????
            redisUtil.lPush(Constants.ORDER_TASK_payProfitSharing, storeOrder.getId()); // ??????-????????????????????????

            // ??????-??????????????????
            storeOrderStatusService.createLog(storeOrder.getId(), "check_order_over", "????????????");

            // ??????-?????????????????????
            LambdaQueryWrapper<RegionalUser> regionalUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
            regionalUserLambdaQueryWrapper.eq(RegionalUser::getUid,storeOrder.getUid());
            regionalUserLambdaQueryWrapper.eq(RegionalUser::getRaId,0);//??????
            List<RegionalUser> regionalUserList = regionalUserService.list(regionalUserLambdaQueryWrapper);//???????????????????????????
            if(regionalUserList == null|| regionalUserList.size() == 0){
                //?????????-??????
                RegionalUser regionalUser=new RegionalUser();
                regionalUser.setRaId(0);//??????
                regionalUser.setUid(storeOrder.getUid());
                regionalUserService.save(regionalUser);
            }else{
                //??????-???????????????
                RegionalUser regionalUser=regionalUserList.get(0);
                regionalUser.setUpdateTime(new DateTime());
                regionalUserService.updateById(regionalUser);
            }
            return true;
        }catch (Exception e){
            logger.error("????????????????????????????????????-???????????????"+e.getMessage());
            return false;
        }
    }

    /**
     * ????????????
     * @param storeOrder ????????????
     */
    private Boolean rollbackStock(StoreOrder storeOrder) {
        try{
            // ?????????????????????
            List<StoreOrderInfoOldVo> orderInfoVoList = storeOrderInfoService.getOrderListByOrderId(storeOrder.getId());
            if(null == orderInfoVoList || orderInfoVoList.size() < 1){
                return true;
            }

            // ??????????????????????????????
            // ?????????????????????????????????
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
                        //??????????????????????????????
                        continue;
                    }
                    stockRequest.setNum(orderInfoVo.getInfo().getPayNum());
                    storeSeckillService.stockAddRedis(stockRequest);
                }
            } else if (ObjectUtil.isNotNull(storeOrder.getBargainId()) && storeOrder.getBargainId() > 0) { // ??????????????????????????????
                for (StoreOrderInfoOldVo orderInfoVo : orderInfoVoList) {
                    StoreProductStockRequest stockRequest = new StoreProductStockRequest();
                    stockRequest.setBargainId(storeOrder.getBargainId());
                    stockRequest.setProductId(orderInfoVo.getProductId());
                    stockRequest.setAttrId(orderInfoVo.getInfo().getAttrValueId());
                    stockRequest.setOperationType("add");
                    stockRequest.setType(2);
                    stockRequest.setSuk(orderInfoVo.getInfo().getSku());
                    if(orderInfoVo.getInfo().getPayNum() < 1){
                        //??????????????????????????????
                        continue;
                    }
                    stockRequest.setNum(orderInfoVo.getInfo().getPayNum());
                    storeBargainService.stockAddRedis(stockRequest);
                }
            } else if (ObjectUtil.isNotNull(storeOrder.getCombinationId()) && storeOrder.getCombinationId() > 0) { // ??????????????????????????????
                for (StoreOrderInfoOldVo orderInfoVo : orderInfoVoList) {
                    StoreProductStockRequest stockRequest = new StoreProductStockRequest();
                    stockRequest.setCombinationId(storeOrder.getCombinationId());
                    stockRequest.setProductId(orderInfoVo.getProductId());
                    stockRequest.setAttrId(orderInfoVo.getInfo().getAttrValueId());
                    stockRequest.setOperationType("add");
                    stockRequest.setType(3);
                    stockRequest.setSuk(orderInfoVo.getInfo().getSku());
                    if(orderInfoVo.getInfo().getPayNum() < 1){
                        //??????????????????????????????
                        continue;
                    }
                    stockRequest.setNum(orderInfoVo.getInfo().getPayNum());
                    storeCombinationService.stockAddRedis(stockRequest);
                }
            } else { // ??????????????????????????????
                for (StoreOrderInfoOldVo orderInfoVo : orderInfoVoList) {
                    StoreProductStockRequest stockRequest = new StoreProductStockRequest();
                    stockRequest.setProductId(orderInfoVo.getProductId());
                    stockRequest.setAttrId(orderInfoVo.getInfo().getAttrValueId());
                    stockRequest.setOperationType("add");
                    stockRequest.setType(0);
                    stockRequest.setSuk(orderInfoVo.getInfo().getSku());
                    if(orderInfoVo.getInfo().getPayNum() < 1){
                        //??????????????????????????????
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
     * ??????????????????
     * ?????????????????????userBill ???????????????
     */
    @Override
    public Boolean refundOrder(StoreOrder storeOrder) {
        /**
         * 1??????????????????
         * 2?????????????????????
         * 3?????????????????????
         * 4????????????????????????
         * 5???????????????
         * 6???????????????
         * 7???????????????
         * ?????????2-5??????user???????????????+userBill?????????
         */
        // ????????????????????????
        User user = userService.getById(storeOrder.getUid());
        if (ObjectUtil.isNull(user)) {
            logger.error("?????????????????????????????????????????????,storeOrder===>" + storeOrder);
            return Boolean.FALSE;
        }

        // ????????????list??????
        List<UserBill> userBillList = CollUtil.newArrayList();

        // ????????????-???????????????
        // ??????userBill???????????????????????????????????????
        List<UserBill> userBills = userBillService.findListByOrderIdAndUid(storeOrder.getId(), storeOrder.getUid());

        // ????????????
        List<UserBill> experienceList = userBills.stream()
                .filter(e -> e.getCategory().equals(Constants.USER_BILL_CATEGORY_EXPERIENCE))
                .collect(Collectors.toList());
        experienceList.forEach(bill -> {
            user.setExperience(user.getExperience() - bill.getNumber().intValue());

            //????????????
            UserBill userBill = userBillService.getUserBill(    // ???????????????????????????
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

        // ????????????
        List<UserIntegralRecord> integralRecordList = userIntegralRecordService.findListByOrderIdAndUid(storeOrder.getOrderId(), storeOrder.getUid());
        integralRecordList.forEach(record -> {
            if (record.getType().equals(IntegralRecordConstants.INTEGRAL_RECORD_TYPE_SUB)) {// ??????????????????
                user.setIntegral(user.getIntegral().add(record.getIntegral()));
                record.setId(null);
                record.setTitle(IntegralRecordConstants.BROKERAGE_RECORD_TITLE_REFUND);
                record.setType(IntegralRecordConstants.INTEGRAL_RECORD_TYPE_ADD);
                record.setBalance(user.getIntegral());
                record.setCreateTime(DateUtil.nowDateTime());
                record.setUpdateTime(DateUtil.nowDateTime());
                record.setMark(StrUtil.format("????????????????????????????????????{}??????", record.getIntegral()));
                record.setStatus(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE);
            }else if (record.getType().equals(IntegralRecordConstants.INTEGRAL_RECORD_TYPE_ADD)) {// ??????????????????
                record.setStatus(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_INVALIDATION);
            }
        });

        // add and update
        List<UserIntegralRecord> addIntegralList = integralRecordList.stream().filter(e -> ObjectUtil.isNull(e.getId())).collect(Collectors.toList());
        List<UserIntegralRecord> updateIntegralList = integralRecordList.stream().filter(e -> ObjectUtil.isNotNull(e.getId())).collect(Collectors.toList());

        // ???????????????????????????????????????
        // ??????????????????
        List<UserBrokerageRecord> brokerageRecordList = CollUtil.newArrayList();
        List<UserBrokerageRecord> recordList = userBrokerageRecordService.findListByLinkIdAndLinkType(
                storeOrder.getId().toString(),
                BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_ORDER);
        if (CollUtil.isNotEmpty(recordList)) {
            recordList.forEach(r -> {
                //??????????????????????????????????????????
                if (r.getStatus() < BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_COMPLETE) {
                    r.setStatus(BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_INVALIDATION);
                    r.setPm(BrokerageRecordConstants.BROKERAGE_RECORD_TYPE_SUB);
                    r.setTitle(new StringBuffer(r.getTitle()).append("(?????????)").toString());
                    brokerageRecordList.add(r);
                }
            });
        }

        // ???????????????
        LambdaQueryWrapper<PublicIntegalRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(PublicIntegalRecord::getUid, user.getUid());
        lqw.eq(PublicIntegalRecord::getLinkId, String.valueOf(storeOrder.getId()));
        lqw.in(PublicIntegalRecord::getStatus,
                IntegralRecordConstants.PUBLIC_INTEGRAL_RECORD_STATUS_CREATE,
                IntegralRecordConstants.PUBLIC_INTEGRAL_RECORD_STATUS_FROZEN);
        List<PublicIntegalRecord> publicIntegalRecordList = publicIntegalRecordService.list(lqw);

        // ???????????????-????????????
        List<UserIntegralRecord> userIntegralRecordList = null;
        if(publicIntegalRecordList !=null && publicIntegalRecordList.size() > 0) {
            // ?????????????????????
            publicIntegalRecordList.stream().forEach(e->{
                e.setStatus(IntegralRecordConstants.PUBLIC_INTEGRAL_RECORD_STATUS_INVALIDATION)
                        .setTitle(new StringBuffer(e.getTitle()).append("(?????????)").toString());
            });

            // ???????????????????????????????????????
            LambdaQueryWrapper<UserIntegralRecord> lqw2 = Wrappers.lambdaQuery();
            lqw2.eq(UserIntegralRecord::getLinkType,IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_PUBLIC);
            lqw2.eq(UserIntegralRecord::getType,IntegralRecordConstants.INTEGRAL_RECORD_TYPE_ADD);
            lqw2.eq(UserIntegralRecord::getStatus, IntegralRecordConstants.INTEGRAL_RECORD_STATUS_DJS);
            List<Integer> prIds = publicIntegalRecordList.stream().map(PublicIntegalRecord::getId).collect(Collectors.toList());
            lqw2.in(UserIntegralRecord::getLinkId, prIds);
            userIntegralRecordList = userIntegralRecordService.list(lqw2);
            userIntegralRecordList.stream().forEach(e->{
                e.setStatus(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_INVALIDATION).setTitle(new StringBuffer(e.getTitle()).append("(?????????)").toString());
                e.setType(IntegralRecordConstants.INTEGRAL_RECORD_TYPE_SUB);
            });
        }

        // ??????
        List<UserIntegralRecord> finalUserIntegralRecordList = userIntegralRecordList;
        Boolean execute = transactionTemplate.execute(e -> {
            //???????????????
            storeOrderStatusService.saveRefund(storeOrder.getId(), storeOrder.getRefundPrice(), "??????");

            // ??????????????????
            userService.updateById(user);

            // ????????????????????????
            userBillService.saveBatch(userBillList);

            // ????????????
            if (CollUtil.isNotEmpty(addIntegralList)) {
                userIntegralRecordService.saveBatch(addIntegralList);
            }
            if (CollUtil.isNotEmpty(updateIntegralList)) {
                userIntegralRecordService.updateBatchById(updateIntegralList);
            }

            // ???????????????
            if(CollUtil.isNotEmpty(publicIntegalRecordList)){
                publicIntegalRecordService.updateBatchById(publicIntegalRecordList);
            }
            if(CollUtil.isNotEmpty(finalUserIntegralRecordList)){
                userIntegralRecordService.updateBatchById(finalUserIntegralRecordList);
            }

            // ????????????
            if (CollUtil.isNotEmpty(brokerageRecordList)) {
                userBrokerageRecordService.updateBatchById(brokerageRecordList);
            }

            // ????????????
            userLevelService.upLevel(user);

            // ????????????
            rollbackStock(storeOrder);

            // ????????????????????????
            StoreOrder tempOrder = new StoreOrder();
            tempOrder.setId(storeOrder.getId());
            tempOrder.setRefundStatus(2);
            storeOrderService.updateById(tempOrder);

            // ??????????????????
            if (storeOrder.getCombinationId() > 0) {
                StorePink storePink = storePinkService.getByOrderId(storeOrder.getOrderId());
                storePink.setStatus(3);
                storePink.setIsRefund(true);
                storePinkService.updateById(storePink);
            }

            // ????????????
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
        // ????????????????????????
        if (storeOrder.getPaid()) {
            return Boolean.TRUE;
        }

        // ??????????????????
        if (storeOrder.getIsDel() || storeOrder.getIsSystemDel()) {
            return Boolean.TRUE;
        }

        // ??????????????????
        String cancelStr;
        DateTime cancelTime;
        if (storeOrder.getBargainId() > 0 || storeOrder.getSeckillId() > 0 || storeOrder.getCombinationId() > 0) {
            cancelStr = systemConfigService.getValueByKey("order_activity_time");
        } else {
            cancelStr = systemConfigService.getValueByKey("order_cancel_time");
        }

        // ??????????????????=1
        if (StrUtil.isBlank(cancelStr)) {
            cancelStr = "1";
        }

        //??????????????????
        cancelTime = cn.hutool.core.date.DateUtil.offset(storeOrder.getCreateTime(), DateField.HOUR_OF_DAY, Integer.parseInt(cancelStr));
        long between = cn.hutool.core.date.DateUtil.between(cancelTime, cn.hutool.core.date.DateUtil.date(), DateUnit.SECOND, false);
        if (between < 0) {// ??????????????????????????????
            return Boolean.FALSE;
        }

        // ????????????
        storeOrder.setIsDel(true).setIsSystemDel(true);

        //??????
        Boolean execute = transactionTemplate.execute(e -> {
            storeOrderService.updateById(storeOrder);
            //???????????????
            storeOrderStatusService.createLog(storeOrder.getId(), "cancel", "?????????????????????????????????");
            return Boolean.TRUE;
        });

        //????????????
        if (execute) {
            // ????????????
            rollbackStock(storeOrder);
        }

        //????????????
        return execute;
    }

    @Override
    public Boolean orderReceiving(Integer orderId) {
        //??????????????????
        StoreOrder storeOrder = storeOrderService.getById(orderId);
        if (ObjectUtil.isNull(storeOrder)) {
            logger.error(StrUtil.format("????????????task???????????????????????????id={}", orderId));
        }

        //??????????????????
        User user = userService.getById(storeOrder.getUid());

        // ??????????????????
        List<UserBrokerageRecord> recordList = userBrokerageRecordService.findListByLinkIdAndLinkType(storeOrder.getId().toString(), BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_ORDER);
        logger.info("???????????????????????????" + recordList.size());
        for (UserBrokerageRecord record : recordList) {
            //??????-???????????????????????????
            if (!record.getStatus().equals(BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_CREATE)) {
                logger.error(StrUtil.format("????????????task????????????????????????????????????????????????id={}", orderId));
                continue;
            }

            // ?????????????????????
            record.setStatus(BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_FROZEN);

            // ??????????????????
            Long thawTime = DateUtil.calculationThawDatetime(record.getFrozenTime());

            //??????????????????
            record.setThawTime(thawTime);
        }

        // ????????????????????????
        List<PublicIntegalRecord> publicIntegalRecordList = publicIntegalRecordService.findListByOrderIdAndUid(String.valueOf(storeOrder.getId()), storeOrder.getUid());
        //publicIntegalRecordList.stream().forEach(e->{e.setStatus(IntegralRecordConstants.PUBLIC_INTEGRAL_RECORD_STATUS_COMPLETE); });
        //publicIntegalRecordList.forEach(p -> p.setStatus(IntegralRecordConstants.PUBLIC_INTEGRAL_RECORD_STATUS_COMPLETE));
        //list = list.stream().map(p -> p.setName("17_"+p.getName()) ).collect(Collectors.toList());
        logger.info("????????????-?????????????????????" + publicIntegalRecordList.size());
        for (PublicIntegalRecord record : publicIntegalRecordList) {
            // ????????????????????????
            record.setStatus(IntegralRecordConstants.PUBLIC_INTEGRAL_RECORD_STATUS_COMPLETE);
        }

        //??????
        Boolean execute = transactionTemplate.execute(e -> {
            // ??????
            storeOrderStatusService.createLog(storeOrder.getId(), Constants.ORDER_LOG_USER_TAKE_DELIVERY, Constants.ORDER_STATUS_STR_TAKE);

            // ??????-?????????????????????
            if (CollUtil.isNotEmpty(recordList)) {
                userBrokerageRecordService.updateBatchById(recordList);
            }

            // ??????????????????
            if (CollUtil.isNotEmpty(publicIntegalRecordList)) {
                publicIntegalRecordService.updateBatchById(publicIntegalRecordList);
            }

            return Boolean.TRUE;
        });

        if (execute) {
            // ?????????????????????????????????????????????
            String smsSwitch = systemConfigService.getValueByKey(SmsConstants.SMS_CONFIG_ADMIN_REFUND_SWITCH);
            if (StrUtil.isNotBlank(smsSwitch) && smsSwitch.equals("1")) {
                // ????????????????????????????????????
                List<SystemAdmin> systemAdminList = systemAdminService.findIsSmsList();
                if (CollUtil.isNotEmpty(systemAdminList)) {
                    // ????????????
                    systemAdminList.forEach(admin -> {
                        smsService.sendOrderReceiptNotice(admin.getPhone(), storeOrder.getOrderId(), admin.getRealName());
                    });
                }
            }

            // ??????????????????
            pushMessageOrder(storeOrder, user);
        }

        return execute;
    }

    /**
     * ??????????????????
     * ????????????????????????
     * ?????????????????????
     * ?????????????????????
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
        // ?????????
        if (storeOrder.getIsChannel().equals(PayConstants.ORDER_PAY_CHANNEL_PUBLIC)) {
            userToken = userTokenService.getTokenByUserId(user.getUid(), UserConstants.USER_TOKEN_TYPE_WECHAT);
            if (ObjectUtil.isNull(userToken)) {
                return ;
            }
            // ????????????????????????
            temMap.put(Constants.WE_CHAT_TEMP_KEY_FIRST, "????????????????????????????????????");
            temMap.put("keyword1", storeOrder.getOrderId());
            temMap.put("keyword2", "?????????");
            temMap.put("keyword3", DateUtil.nowDateTimeStr());
            temMap.put("keyword4", "???????????????????????????");
            temMap.put(Constants.WE_CHAT_TEMP_KEY_END, "?????????????????????");
            templateMessageService.pushTemplateMessage(Constants.WE_CHAT_TEMP_KEY_ORDER_RECEIVING, temMap, userToken.getToken());
            return;
        }
        // ???????????????????????????
        userToken = userTokenService.getTokenByUserId(user.getUid(), UserConstants.USER_TOKEN_TYPE_ROUTINE);
        if (ObjectUtil.isNull(userToken)) {
            return ;
        }
        // ????????????
        // ??????????????????
        String storeNameAndCarNumString = orderUtils.getStoreNameAndCarNumString(storeOrder.getId());
        if (StrUtil.isBlank(storeNameAndCarNumString)) {
            return ;
        }
        if (storeNameAndCarNumString.length() > 20) {
            storeNameAndCarNumString = storeNameAndCarNumString.substring(0, 15) + "***";
        }
        temMap.put("character_string6", storeOrder.getOrderId());
        temMap.put("phrase4", "?????????");
        temMap.put("time7", DateUtil.nowDateTimeStr());
        temMap.put("thing1", storeNameAndCarNumString);
        temMap.put("thing5", "????????????????????????????????????");
        templateMessageService.pushMiniTemplateMessage(Constants.WE_CHAT_PROGRAM_TEMP_KEY_ORDER_RECEIVING, temMap, userToken.getToken());
    }
}
