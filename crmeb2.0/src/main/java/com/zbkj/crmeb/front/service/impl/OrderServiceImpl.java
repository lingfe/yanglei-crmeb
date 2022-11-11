package com.zbkj.crmeb.front.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.common.CommonPage;
import com.common.MyRecord;
import com.common.PageParamRequest;
import com.constants.*;
import com.exception.CrmebException;
import com.utils.CrmebUtil;
import com.utils.DateUtil;
import com.utils.RedisUtil;
import com.zbkj.crmeb.bargain.model.StoreBargain;
import com.zbkj.crmeb.bargain.model.StoreBargainUser;
import com.zbkj.crmeb.bargain.service.StoreBargainService;
import com.zbkj.crmeb.bargain.service.StoreBargainUserService;
import com.zbkj.crmeb.combination.model.StoreCombination;
import com.zbkj.crmeb.combination.service.StoreCombinationService;
import com.zbkj.crmeb.express.model.ShippingTemplates;
import com.zbkj.crmeb.express.model.ShippingTemplatesFree;
import com.zbkj.crmeb.express.model.ShippingTemplatesRegion;
import com.zbkj.crmeb.express.service.*;
import com.zbkj.crmeb.express.vo.LogisticsResultVo;
import com.zbkj.crmeb.front.request.*;
import com.zbkj.crmeb.front.response.*;
import com.zbkj.crmeb.front.service.OrderService;
import com.zbkj.crmeb.front.service.UserCenterService;
import com.zbkj.crmeb.front.vo.OrderInfoDetailVo;
import com.zbkj.crmeb.front.vo.OrderInfoVo;
import com.zbkj.crmeb.marketing.model.StoreCouponUser;
import com.zbkj.crmeb.marketing.model.StoreIntegalShop;
import com.zbkj.crmeb.marketing.service.StoreCouponUserService;
import com.zbkj.crmeb.marketing.service.StoreIntegalShopService;
import com.zbkj.crmeb.regionalAgency.model.RegionalAgency;
import com.zbkj.crmeb.regionalAgency.service.RegionalAgencyService;
import com.zbkj.crmeb.retailer.model.Retailer;
import com.zbkj.crmeb.retailer.service.RetailerService;
import com.zbkj.crmeb.seckill.model.StoreSeckill;
import com.zbkj.crmeb.seckill.model.StoreSeckillManger;
import com.zbkj.crmeb.seckill.service.StoreSeckillMangerService;
import com.zbkj.crmeb.seckill.service.StoreSeckillService;
import com.zbkj.crmeb.sms.service.SmsService;
import com.zbkj.crmeb.store.model.*;
import com.zbkj.crmeb.store.request.StoreOrderInfoSearchRequest;
import com.zbkj.crmeb.store.service.*;
import com.zbkj.crmeb.store.utilService.OrderUtils;
import com.zbkj.crmeb.store.vo.StoreOrderInfoOldVo;
import com.zbkj.crmeb.store.vo.StoreOrderInfoVo;
import com.zbkj.crmeb.system.model.SystemAdmin;
import com.zbkj.crmeb.system.model.SystemStore;
import com.zbkj.crmeb.system.response.SystemGroupDataOrderStatusPicResponse;
import com.zbkj.crmeb.system.service.*;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.model.UserAddress;
import com.zbkj.crmeb.user.service.UserAddressService;
import com.zbkj.crmeb.user.service.UserService;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * H5端订单操作
 * @author: 零风
 * @CreateDate: 2022/1/17 15:05
 */
@Service
public class OrderServiceImpl implements OrderService {

    private final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private UserService userService;

    @Autowired
    private StoreCartService storeCartService;

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private StoreOrderInfoService storeOrderInfoService;

    @Autowired
    private StoreOrderStatusService storeOrderStatusService;

    @Autowired
    private ShippingTemplatesService shippingTemplatesService;

    @Autowired
    private UserAddressService userAddressService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private SystemStoreService systemStoreService;

    @Autowired
    private OrderUtils orderUtils;

    @Autowired
    private SystemAttachmentService systemAttachmentService;

    @Autowired
    private LogisticService logisticsService;

    @Autowired
    private StoreSeckillService storeSeckillService;

    @Autowired
    private StoreCombinationService storeCombinationService;

    @Autowired
    private StoreBargainService storeBargainService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private SystemAdminService systemAdminService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private SystemGroupDataService systemGroupDataService;

    @Autowired
    private StoreProductService storeProductService;

    @Autowired
    private StoreProductAttrValueService attrValueService;

    @Autowired
    private ShippingTemplatesFreeService shippingTemplatesFreeService;

    @Autowired
    private ShippingTemplatesRegionService shippingTemplatesRegionService;

    @Autowired
    private StoreCouponUserService storeCouponUserService;

    @Autowired
    private StoreProductAttrValueService storeProductAttrValueService;

    @Autowired
    private StoreBargainUserService storeBargainUserService;

    @Autowired
    private StoreSeckillMangerService storeSeckillMangerService;

    @Autowired
    private ExpressService expressService;

    @Autowired
    private StoreIntegalShopService storeIntegalShopService;

    @Autowired
    private UserCenterService userCenterService;

    @Autowired
    private RegionalAgencyService regionalAgencyService;

    @Autowired
    private RetailerService retailerService;

    @Autowired
    private SupplierService supplierService;

    @Override
    public OrderProcedureIsPopupResponse orderProcedureIsPopup(Integer uid) {
        //实例化响应对象
        OrderProcedureIsPopupResponse response=new OrderProcedureIsPopupResponse();
        response.setIsPopup(Boolean.FALSE);

        //得到订单推荐人用户信息
        User user=userService.getById(uid);
        if(user == null){
            return response;
        }else{
            response.setAvatar(user.getAvatar());
            response.setNickname(user.getNickname());
            response.setUid(user.getUid());
            response.setPhone(user.getPhone());
        }

        //验证是否为联盟商家
        String tagid=user.getTagId();
        if((Constants.TARGID_LMSJ.equals(tagid)) ||
                (tagid.indexOf(",10")!=-1&&tagid.indexOf(",1010") == -1)||
                (tagid.indexOf("10,")!=-1&&tagid.indexOf("1010,") == -1)){
            response.setIsPopup(Boolean.TRUE);
            response.setIsAllianceMerchants(Boolean.TRUE);
            response.setTitile("温馨提示");
            response.setContent("亲爱的用户您好，欢迎您进入九秒中，" +
                    "扫本商户二维码下单购酒享可受减餐费活动，" +
                    "参与本活动下单成功后不支持退款,另酒米兑换产品或" +
                    "通过ID转米的不享受减餐费活动，请悉知。");
        }

        //返回
        return response;
    }

    /**
     * 发送后台管理员下单提醒通知短信
     * @param orderNo 订单编号
     */
    @Async
    public void sendAdminOrderNotice(String orderNo) {
        // 系统是否开启用户下单管理员提醒开关
        String smsSwitch = systemConfigService.getValueByKey(SmsConstants.SMS_CONFIG_ADMIN_LOWER_ORDER_SWITCH);
        if (StrUtil.isBlank(smsSwitch) || smsSwitch.equals("0")) {
            return ;
        }
        // 查询可已发送短信的管理员
        List<SystemAdmin> systemAdminList = systemAdminService.findIsSmsList();
        if (CollUtil.isEmpty(systemAdminList)) {
            return ;
        }

        // 发送短信
        systemAdminList.forEach(admin -> smsService.sendCreateOrderNotice(admin.getPhone(), orderNo, admin.getRealName()));
    }

    /**
     * 删除已完成订单
     * @param id Integer 订单id
     * @return 删除结果
     */
    @Override
    public Boolean delete(Integer id) {
        StoreOrder storeOrder = storeOrderService.getById(id);
        Integer userId = userService.getUserIdException();
        if (ObjectUtil.isNull(storeOrder) || !userId.equals(storeOrder.getUid())){
            throw new CrmebException("没有找到相关订单信息!");
        }
        if (storeOrder.getIsDel() || storeOrder.getIsSystemDel()) {
            throw new CrmebException("订单已删除!");
        }
        if (storeOrder.getPaid()) {
            if (storeOrder.getRefundStatus() > 0 && !storeOrder.getRefundStatus().equals(2)) {
                throw new CrmebException("订单在退款流程中无法删除!");
            }
            if (storeOrder.getRefundStatus().equals(0) && !storeOrder.getStatus().equals(3)) {
                throw new CrmebException("只能删除已完成订单!");
            }
        } else {
            throw new CrmebException("未支付订单无法删除!");
        }

        //可以删除
        storeOrder.setIsDel(true);
        Boolean execute = transactionTemplate.execute(e -> {
            storeOrderService.updateById(storeOrder);
            //日志
            storeOrderStatusService.createLog(storeOrder.getId(), "remove_order", "删除订单");
            return Boolean.TRUE;
        });
        return execute;
    }

    @Override
    public boolean take(Integer id) {
        try{
            StoreOrder storeOrder =storeOrderService.getById(id);
            if(!storeOrder.getStatus().equals(Constants.ORDER_STATUS_INT_SPIKE)){
                throw new CrmebException("订单状态错误");
            }

            //已收货，待评价
            storeOrder.setStatus(Constants.ORDER_STATUS_INT_BARGAIN);
            boolean result = storeOrderService.updateById(storeOrder);

            //后续操作放入redis
            redisUtil.lPush(Constants.ORDER_TASK_REDIS_KEY_AFTER_TAKE_BY_USER, id);
            return result;
        }catch (Exception e){
            throw new CrmebException("收货失败" + e.getMessage());
        }
    }

    /**
     * 订单取消
     * @param id Integer 订单id
     */
    @Override
    public boolean cancel(Integer id) {
        StoreOrder storeOrder = storeOrderService.getById(id);
        Integer userId = userService.getUserIdException();
        if(null == storeOrder || !userId.equals(storeOrder.getUid())){
            //订单号错误
            throw new CrmebException("没有找到相关订单信息!");
        }

        //已收货，待评价
        storeOrder.setIsDel(true);
        storeOrder.setIsSystemDel(true);
        boolean result = storeOrderService.updateById(storeOrder);

        //后续操作放入redis
        redisUtil.lPush(Constants.ORDER_TASK_REDIS_KEY_AFTER_CANCEL_BY_USER, id);
        return result;
    }

    /**
     * 订单退款申请
     * @param request OrderRefundApplyRequest 退款参数
     */
    @Override
    public boolean refundApply(OrderRefundApplyRequest request) {
        StoreOrder storeOrderPram = new StoreOrder();
        storeOrderPram.setOrderId(request.getUni());
        storeOrderPram.setIsDel(false);
        storeOrderPram.setPaid(true);
        StoreOrder existStoreOrder = storeOrderService.getByEntityOne(storeOrderPram);
        if(null == existStoreOrder) throw new CrmebException("支付订单不存在");
        if(existStoreOrder.getRefundStatus() == 1){
            throw new CrmebException("正在申请退款中");
        }

        if(existStoreOrder.getRefundStatus() == 2){
            throw new CrmebException("订单已退款");
        }

        if(existStoreOrder.getRefundStatus() == 3){
            throw new CrmebException("订单退款中");
        }

        existStoreOrder.setRefundStatus(1);
        existStoreOrder.setRefundReasonTime(DateUtil.nowDateTime());
        existStoreOrder.setRefundReasonWap(request.getText());
        existStoreOrder.setRefundReasonWapExplain(request.getExplain());
        existStoreOrder.setRefundReasonWapImg(systemAttachmentService.clearPrefix(request.getReasonImage()));
        existStoreOrder.setRefundPrice(BigDecimal.ZERO);

        Boolean execute = transactionTemplate.execute(e -> {
            storeOrderService.updateById(existStoreOrder);
            storeOrderStatusService.createLog(existStoreOrder.getId(), Constants.ORDER_LOG_REFUND_APPLY, "用户申请退款原因：" + request.getExplain());
            return Boolean.TRUE;
        });

        if (execute) {
            // 发送用户退款管理员提醒短信
            String smsSwitch = systemConfigService.getValueByKey(SmsConstants.SMS_CONFIG_ADMIN_CONFIRM_TAKE_OVER_SWITCH);
            if (StrUtil.isNotBlank(smsSwitch) && smsSwitch.equals("1")) {
                // 查询可已发送短信的管理员
                List<SystemAdmin> systemAdminList = systemAdminService.findIsSmsList();
                if (CollUtil.isNotEmpty(systemAdminList)) {
                    // 发送短信
                    systemAdminList.forEach(admin -> smsService.sendOrderRefundApplyNotice(admin.getPhone(), existStoreOrder.getOrderId(), admin.getRealName()));
                }
            }
        }
        if(!execute) throw new CrmebException("申请退款失败");
        return execute;
    }

    /**
     * 订单退款申请Task使用
     * @param applyList OrderRefundApplyRequest 退款参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean refundApplyTask(List<OrderRefundApplyRequest> applyList) {
        if (CollUtil.isEmpty(applyList)) {
            return false;
        }
        String smsSwitch = systemConfigService.getValueByKey(SmsConstants.SMS_CONFIG_ADMIN_CONFIRM_TAKE_OVER_SWITCH);
        List<StoreOrder> orderList = CollUtil.newArrayList();
        for (OrderRefundApplyRequest request : applyList) {
            StoreOrder storeOrder = storeOrderService.getById(request.getId());
            if(ObjectUtil.isNull(storeOrder)){
                //订单号错误
                logger.error("拼团自动处理订单申请退款：没有找到相关订单信息!");
                continue;
            }
            if(storeOrder.getRefundStatus() == 1){
                logger.error("拼团自动处理订单申请退款：正在申请退款中!");
                continue;
            }

            if(storeOrder.getRefundStatus() == 2){
                logger.error("拼团自动处理订单申请退款：订单已退款!");
                continue;
            }

            if(storeOrder.getRefundStatus() == 3){
                logger.error("拼团自动处理订单申请退款：订单退款中!");
                continue;
            }

            storeOrder.setRefundReasonWapImg(systemAttachmentService.clearPrefix(request.getReasonImage()));
            storeOrder.setRefundStatus(1);
            storeOrder.setRefundReasonWapExplain(request.getExplain());
            storeOrder.setRefundReason(request.getText());
            storeOrder.setRefundPrice(BigDecimal.ZERO);
            storeOrder.setRefundReasonTime(DateUtil.nowDateTime());
            orderList.add(storeOrder);

            // 发送用户退款管理员提醒短信
            if (StrUtil.isNotBlank(smsSwitch) || smsSwitch.equals("1")) {
                // 查询可已发送短信的管理员
                List<SystemAdmin> systemAdminList = systemAdminService.findIsSmsList();
                if (CollUtil.isNotEmpty(systemAdminList)) {
                    // 发送短信
                    systemAdminList.forEach(admin -> smsService.sendOrderRefundApplyNotice(admin.getPhone(), storeOrder.getOrderId(), admin.getRealName()));
                }
            }
        }

        return storeOrderService.updateBatchById(orderList, 100);
    }

    /**
     * 订单列表
     * @param status 类型
     * @param pageRequest 分页
     * @return CommonPage<OrderDetailResponse>
     */
    @Override
    public CommonPage<OrderDetailResponse> list(Integer status, PageParamRequest pageRequest) {
        Integer userId = userService.getUserIdException();

        List<StoreOrder> orderList = storeOrderService.getUserOrderList(userId, status, pageRequest);
        CommonPage<StoreOrder> storeOrderCommonPage = CommonPage.restPage(orderList);
        List<OrderDetailResponse> responseList = CollUtil.newArrayList();
        for (StoreOrder storeOrder : orderList) {
            OrderDetailResponse infoResponse = new OrderDetailResponse();
            BeanUtils.copyProperties(storeOrder, infoResponse);
            // 订单状态
            infoResponse.setOrderStatus(getH5OrderStatus(storeOrder));
            // 活动类型
            infoResponse.setActivityType(getOrderActivityType(storeOrder));
            // 订单详情对象列表
            List<StoreOrderInfoOldVo> infoVoList = storeOrderInfoService.getOrderListByOrderId(storeOrder.getId());
            List<OrderInfoResponse> infoResponseList = CollUtil.newArrayList();
            infoVoList.forEach(e -> {
                OrderInfoResponse orderInfoResponse = new OrderInfoResponse();
                orderInfoResponse.setStoreName(e.getInfo().getProductName());
                orderInfoResponse.setImage(e.getInfo().getImage());
                orderInfoResponse.setCartNum(e.getInfo().getPayNum());
                orderInfoResponse.setPrice(e.getInfo().getPrice());
                orderInfoResponse.setProductId(e.getProductId());
                infoResponseList.add(orderInfoResponse);
            });
            infoResponse.setOrderInfoList(infoResponseList);
            responseList.add(infoResponse);
        }
        CommonPage<OrderDetailResponse> detailPage = CommonPage.restPage(responseList);
        BeanUtils.copyProperties(storeOrderCommonPage, detailPage, "list");
        return detailPage;
    }

    /**
     * 获取订单活动类型
     * @param storeOrder 订单都西昂
     * @return 活动类型
     */
    public static String getOrderActivityType(StoreOrder storeOrder) {
        if (storeOrder.getSeckillId() > 0) {
            return "秒杀";
        }
        if (storeOrder.getCombinationId() > 0) {
            return "拼团";
        }
        if (storeOrder.getBargainId() > 0) {
            return "砍价";
        }
        if (storeOrder.getShippingType().equals(2)) {
            return "核销";
        }
        return "普通";
    }

    /**
     * 获取H5订单状态
     * @param storeOrder 订单对象
     */
    public static String getH5OrderStatus(StoreOrder storeOrder) {
        if (!storeOrder.getPaid()) {
            return "待支付";
        }
        if (storeOrder.getRefundStatus().equals(1)) {
            return "申请退款中";
        }
        if (storeOrder.getRefundStatus().equals(2)) {
            return "已退款";
        }
        if (storeOrder.getRefundStatus().equals(3)) {
            return "退款中";
        }
        if (storeOrder.getStatus().equals(0)) {
            return "待发货";
        }
        if (storeOrder.getStatus().equals(1)) {
            return "待收货";
        }
        if (storeOrder.getStatus().equals(2)) {
            return "待评价";
        }
        if (storeOrder.getStatus().equals(3)) {
            return "已完成";
        }
        return "";
    }

    /**
     * 订单详情
     * @param orderId 订单id
     */
    @Override
    public StoreOrderDetailResponse detailOrder(String orderId) {
        StoreOrderDetailResponse storeOrderDetailResponse = new StoreOrderDetailResponse();

        // 查询订单
        StoreOrder storeOrder = storeOrderService.getByOderId(orderId);
        if (ObjectUtil.isNull(storeOrder) || storeOrder.getIsDel() || storeOrder.getIsSystemDel()) {
            throw new CrmebException("订单不存在");
        }

        BeanUtils.copyProperties(storeOrder, storeOrderDetailResponse);
        MyRecord orderStatusVo = getOrderStatusVo(storeOrder);

        // 订单详情对象列表
        List<StoreOrderInfoOldVo> infoVoList = storeOrderInfoService.getOrderListByOrderId(storeOrder.getId());
        List<OrderInfoResponse> infoResponseList = CollUtil.newArrayList();
        infoVoList.forEach(e -> {
            OrderInfoResponse orderInfoResponse = new OrderInfoResponse();
            orderInfoResponse.setStoreName(e.getInfo().getProductName());
            orderInfoResponse.setImage(e.getInfo().getImage());
            orderInfoResponse.setCartNum(e.getInfo().getPayNum());
            orderInfoResponse.setPrice(e.getInfo().getPrice());
            orderInfoResponse.setProductId(e.getProductId());
            orderInfoResponse.setIsReply(e.getInfo().getIsReply());
            orderInfoResponse.setAttrId(e.getInfo().getAttrValueId());
            orderInfoResponse.setSku(e.getInfo().getSku());
            infoResponseList.add(orderInfoResponse);
        });
        storeOrderDetailResponse.setOrderInfoList(infoResponseList);

        // 系统门店信息
        SystemStore systemStorePram = new SystemStore();
        systemStorePram.setId(storeOrder.getStoreId());
        storeOrderDetailResponse.setSystemStore(systemStoreService.getByCondition(systemStorePram));

        // 腾讯云地图key
        storeOrderDetailResponse.setMapKey(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_SITE_TENG_XUN_MAP_KEY));
        BeanUtils.copyProperties(storeOrder, storeOrderDetailResponse);
        storeOrderDetailResponse.setStatusPic(orderStatusVo.getStr("statusPic"));
        storeOrderDetailResponse.setOrderStatusMsg(orderStatusVo.getStr("msg"));
        storeOrderDetailResponse.setPayTypeStr(orderStatusVo.getStr("payTypeStr"));
        BigDecimal proTotalPrice = storeOrderDetailResponse.getPayPrice().add(storeOrderDetailResponse.getCouponPrice()).add(storeOrderDetailResponse.getDeductionPrice()).subtract(storeOrderDetailResponse.getPayPostage());
        storeOrderDetailResponse.setProTotalPrice(proTotalPrice);
        return storeOrderDetailResponse;
    }

    /**
     * 获取订单状态相关信息
     * @return MyRecord
     */
    private MyRecord getOrderStatusVo(StoreOrder storeOrder) {
        MyRecord record = new MyRecord();
        if (!storeOrder.getPaid()) {
            record.set("type", 0);
            record.set("title", "未支付");
            record.set("msg", "订单未支付");
            // 系统预设取消订单时间段  TODO
            List<String> configKeys = new ArrayList<>();
            configKeys.add("order_cancel_time");
            configKeys.add("order_activity_time");
            configKeys.add("order_bargain_time");
            configKeys.add("order_seckill_time");
            configKeys.add("order_pink_time");
            List<String> configValues = systemConfigService.getValuesByKes(configKeys);
            // 拼团秒杀砍价逻辑处理   TODO
            Date timeSpace;
            timeSpace = DateUtil.addSecond(storeOrder.getCreateTime(),Double.valueOf(configValues.get(0)).intValue() * 3600);
            record.set("msg", "请在" + DateUtil.dateToStr(timeSpace, Constants.DATE_FORMAT) +"前完成支付");
        } else if (storeOrder.getRefundStatus() == 1) {
            record.set("type", -1);
            record.set("title", "申请退款中");
            record.set("msg", "商家审核中,请耐心等待");
        } else if (storeOrder.getRefundStatus() == 2) {
            record.set("type", -2);
            record.set("title", "已退款");
            record.set("msg", "已为您退款,感谢您的支持");
        } else if (storeOrder.getRefundStatus() == 3) {
            record.set("type", -3);
            record.set("title", "退款中");
            record.set("msg", "正在为您退款,感谢您的支持");
        } else if (storeOrder.getStatus() == 0) {
            record.set("type", 1);
            record.set("title", "未发货");
            record.set("msg", "商家未发货,请耐心等待");
        } else if (storeOrder.getStatus() == 1) { // 待收货处理
            // 待收货
            if (null != storeOrder.getDeliveryType() && storeOrder.getDeliveryType().equals(Constants.ORDER_STATUS_STR_SPIKE_KEY)) { // 送货
                StoreOrderStatus storeOrderStatus = new StoreOrderStatus();
                storeOrderStatus.setOid(storeOrder.getId());
                storeOrderStatus.setChangeType(Constants.ORDER_LOG_DELIVERY);
                List<StoreOrderStatus> sOrderStatusResults = storeOrderStatusService.getByEntity(storeOrderStatus);
                if(sOrderStatusResults.size()>0){
                    record.set("type", 2);
                    record.set("title", "待收货");
                    record.set("msg", "商家已送货,请耐心等待");
                }
            } else if (null != storeOrder.getDeliveryType() && storeOrder.getDeliveryType().equals(Constants.ORDER_LOG_EXPRESS)) {
                StoreOrderStatus storeOrderStatus = new StoreOrderStatus();
                storeOrderStatus.setOid(storeOrder.getId());
                storeOrderStatus.setChangeType(Constants.ORDER_LOG_EXPRESS);
                List<StoreOrderStatus> sOrderStatusResults = storeOrderStatusService.getByEntity(storeOrderStatus);
                if(sOrderStatusResults.size()>0){
                    record.set("type", 2);
                    record.set("title", "待收货");
                    record.set("msg", "商家已发货,请耐心等待");
                }
            }else {
                StoreOrderStatus storeOrderStatus = new StoreOrderStatus();
                storeOrderStatus.setOid(storeOrder.getId());
                storeOrderStatus.setChangeType(Constants.ORDER_LOG_DELIVERY_VI);
                List<StoreOrderStatus> sOrderStatusResults = storeOrderStatusService.getByEntity(storeOrderStatus);
                if(sOrderStatusResults.size()>0){
                    record.set("type", 2);
                    record.set("title", "待收货");
                    record.set("msg", "服务商已虚拟发货");
                } else {
                    record.set("type", 2);
                    record.set("title", "待收货");
                    record.set("msg", "退款拒绝订单已发货");
                }
            }
        }else if(storeOrder.getStatus() == 2){
            record.set("type", 3);
            record.set("title", "待评价");
            record.set("msg", "已收货,快去评价一下吧");
        }else if(storeOrder.getStatus() == 3){
            record.set("type", 4);
            record.set("title", "交易完成");
            record.set("msg", "交易完成,感谢您的支持");
        }

        // 支付方式
        String orderPayTypeStr = storeOrderService.getOrderPayTypeStr(storeOrder.getPayType());
        record.set("payTypeStr", orderPayTypeStr);

        // 发货类型
        if(StringUtils.isNotBlank(storeOrder.getDeliveryType())){
            record.set("deliveryType", StringUtils.isNotBlank(storeOrder.getDeliveryType()) ? storeOrder.getDeliveryType():"其他方式");
        }

        // 获取商品状态图片 ignore
        List<SystemGroupDataOrderStatusPicResponse> orderStatusPicList = systemGroupDataService.getListByGid(SysGroupDataConstants.GROUP_DATA_ID_ORDER_STATUS_PIC, SystemGroupDataOrderStatusPicResponse.class);// 53 = group id 在groupData中查询数据
        for (SystemGroupDataOrderStatusPicResponse picList : orderStatusPicList) {
            if(picList.getOrderStatus() == record.getInt("type")){
                record.set("statusPic", picList.getUrl());
                break;
            }
        }
        return record;
    }

    /**
     * 订单tap data
     * @return 订单状态数据量
     */
    @Override
    public OrderDataResponse orderData() {
        Integer userId = userService.getUserIdException();
        OrderDataResponse result = new OrderDataResponse();

        // 订单数量
        Integer orderCount = storeOrderService.getOrderCountByUid(userId,null);
        // 待支付订单数
        Integer unPaidCount = storeOrderService.getTopDataUtil(Constants.ORDER_STATUS_H5_UNPAID, userId,null);

        if (orderCount.equals(0)) {
            result.setOrderCount(0);
            result.setSumPrice(BigDecimal.ZERO);
            result.setUnPaidCount(unPaidCount);
            result.setUnShippedCount(0);
            result.setReceivedCount(0);
            result.setEvaluatedCount(0);
            result.setCompleteCount(0);
            result.setRefundCount(0);
            return result;
        }

        result.setOrderCount(orderCount);
        // 总消费金额
        BigDecimal sumPrice = storeOrderService.getSumPayPriceByUid(userId,null);
        result.setSumPrice(sumPrice);
        // 未支付
        result.setUnPaidCount(unPaidCount);
        // 待发货
        result.setUnShippedCount(storeOrderService.getTopDataUtil(Constants.ORDER_STATUS_H5_NOT_SHIPPED, userId,null));
        // 待收货
        result.setReceivedCount(storeOrderService.getTopDataUtil(Constants.ORDER_STATUS_H5_SPIKE, userId,null));
        // 待核销
        result.setEvaluatedCount(storeOrderService.getTopDataUtil(Constants.ORDER_STATUS_H5_JUDGE, userId,null));
        // 已完成
        result.setCompleteCount(storeOrderService.getTopDataUtil(Constants.ORDER_STATUS_H5_COMPLETE, userId,null));
        // 退款中和已退款（只展示退款中）
        result.setRefundCount(storeOrderService.getTopDataUtil(Constants.ORDER_STATUS_H5_REFUNDING, userId,null));
        return result;
    }

    /**
     * 查询退款理由
     * @return 退款理由集合
     */
    @Override
    public List<String> getRefundReason(){
        String reasonString = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_STOR_REASON);
        reasonString = CrmebUtil.UnicodeToCN(reasonString);
        reasonString = reasonString.replace("rn", "n");
        return Arrays.asList(reasonString.split("\\n"));
    }

    /**
     * 订单物流查看
     * @param orderId 订单id
     */
    @Override
    public Object expressOrder(String orderId) {
        HashMap<String,Object> resultMap = new HashMap<>();
        StoreOrder storeOrderPram = new StoreOrder();
        storeOrderPram.setOrderId(orderId);
        StoreOrder existOrder = storeOrderService.getByEntityOne(storeOrderPram);
        if(null== existOrder) throw new CrmebException("未找到该订单信息");
        if(!existOrder.getDeliveryType().equals(Constants.ORDER_LOG_EXPRESS) || StringUtils.isBlank(existOrder.getDeliveryType()))
            throw new CrmebException("该订单不存在快递订单号");

        LogisticsResultVo expressInfo = logisticsService.info(existOrder.getDeliveryId(), null, Optional.ofNullable(existOrder.getDeliveryCode()).orElse(""), storeOrderPram.getUserPhone());

        PageParamRequest page = new PageParamRequest();
        page.setPage(1); page.setLimit(999);
        StoreOrderInfoSearchRequest storeOrderInfoPram = new StoreOrderInfoSearchRequest();
        storeOrderInfoPram.setOrderId(existOrder.getId());
        List<StoreOrderInfoVo> list = storeOrderInfoService.getVoListByOrderId(existOrder.getId());
        List<HashMap<String, Object>> cartInfos = CollUtil.newArrayList();
        for (StoreOrderInfoVo infoVo : list) {
            HashMap<String, Object> cartInfo = new HashMap<>();
            cartInfo.put("payNum", infoVo.getInfo().getPayNum());
            cartInfo.put("price", infoVo.getInfo().getPrice());
            cartInfo.put("productName", infoVo.getInfo().getProductName());
            cartInfo.put("productImg", infoVo.getInfo().getImage());
            cartInfos.add(cartInfo);
        }
        HashMap<String, Object> orderInfo = new HashMap<>();
        orderInfo.put("deliveryId", existOrder.getDeliveryId());
        orderInfo.put("deliveryName", existOrder.getDeliveryName());
        orderInfo.put("deliveryType", existOrder.getDeliveryType());
        orderInfo.put("info", cartInfos);

        resultMap.put("order", orderInfo);
        resultMap.put("express", expressInfo);
        return resultMap;

    }

    /**
     *获取待评价信息
     */
    @Override
    public OrderProductReplyResponse getReplyProduct(GetProductReply productReply) {
        StoreOrderInfo storeOrderInfo = storeOrderInfoService.getByUniAndOrderId(productReply.getUni(), productReply.getOrderId());
        OrderInfoDetailVo scr = JSONObject.parseObject(storeOrderInfo.getInfo(), OrderInfoDetailVo.class);
        OrderProductReplyResponse response = new OrderProductReplyResponse();
        response.setCartNum(scr.getPayNum());
        response.setTruePrice(scr.getPrice());
        response.setProductId(scr.getProductId());
        response.setImage(scr.getImage());
        response.setSku(scr.getSku());
        response.setStoreName(scr.getProductName());
        return response;
    }

    /**
     * 获取申请订单退款信息
     * @param orderId 订单编号
     * @return ApplyRefundOrderInfoResponse
     */
    @Override
    public ApplyRefundOrderInfoResponse applyRefundOrderInfo(String orderId) {
        StoreOrder storeOrder = getByOrderIdException(orderId);
        ApplyRefundOrderInfoResponse response = new ApplyRefundOrderInfoResponse();
        BeanUtils.copyProperties(storeOrder, response);
        // 订单详情对象列表
        List<StoreOrderInfoOldVo> infoVoList = storeOrderInfoService.getOrderListByOrderId(storeOrder.getId());
        List<OrderInfoResponse> infoResponseList = CollUtil.newArrayList();
        infoVoList.forEach(e -> {
            OrderInfoResponse orderInfoResponse = new OrderInfoResponse();
            orderInfoResponse.setStoreName(e.getInfo().getProductName());
            orderInfoResponse.setImage(e.getInfo().getImage());
            orderInfoResponse.setCartNum(e.getInfo().getPayNum());
            orderInfoResponse.setPrice(e.getInfo().getPrice());
            orderInfoResponse.setProductId(e.getProductId());
            infoResponseList.add(orderInfoResponse);
        });
        response.setOrderInfoList(infoResponseList);
        return response;
    }

    @Override
    public MyRecord preOrder(PreOrderRequest request) {
        // 验证-预下单详情是否为空
        if (CollUtil.isEmpty(request.getOrderDetails())) {
            throw new CrmebException("预下单订单详情列表不能为空");
        }

        // 得到当前用户
        User user = userService.getInfoException();

        // 校验预下单商品信息
        OrderInfoVo orderInfoVo = this.validatePreOrderRequest(request, user);

        // 商品总计金额
        BigDecimal totalPrice = orderInfoVo.getOrderDetailList().stream().map(
                e -> e.getPrice().multiply(new BigDecimal(e.getPayNum())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        orderInfoVo.setProTotalFee(totalPrice);

        // 购买商品总数量
        int orderProNum = orderInfoVo.getOrderDetailList().stream().mapToInt(OrderInfoDetailVo::getPayNum).sum();
        orderInfoVo.setOrderProNum(orderProNum);

        // 获取默认地址
        UserAddress userAddress = userAddressService.getDefaultByUid(user.getUid());
        if (ObjectUtil.isNotNull(userAddress)) {
            // 计算运费
            getFreightFee(orderInfoVo, userAddress);
            orderInfoVo.setAddressId(userAddress.getId());
            orderInfoVo.setRealName(userAddress.getRealName());
            orderInfoVo.setPhone(userAddress.getPhone());
            orderInfoVo.setProvince(userAddress.getProvince());
            orderInfoVo.setCity(userAddress.getCity());
            orderInfoVo.setDistrict(userAddress.getDistrict());
            orderInfoVo.setDetail(userAddress.getDetail());
        } else {
            orderInfoVo.setFreightFee(BigDecimal.ZERO);
        }

        // 实际支付金额
        orderInfoVo.setPayFee(orderInfoVo.getProTotalFee().add(orderInfoVo.getFreightFee()));
        orderInfoVo.setUserBalance(user.getNowMoney());

        // 计算可用积分
        IntegralUserResponse integralUserResponse = userCenterService.getIntegralUser(null);
        orderInfoVo.setUserIntegral(integralUserResponse.getAvailableIntegral());

        // 缓存订单
        String key = user.getUid() + DateUtil.getNowTime().toString()+CrmebUtil.getUuid();
        redisUtil.set("user_order:" + key, JSONObject.toJSONString(orderInfoVo), Constants.ORDER_CASH_CONFIRM, TimeUnit.MINUTES);

        // 返回订单-key
        MyRecord record = new MyRecord();
        record.set("preOrderNo", key);
        return record;
    }

    /**
     * 加载预下单信息
     * @param preOrderNo 预下单号
     * @return 预下单信息
     */
    @Override
    public PreOrderResponse loadPreOrder(String preOrderNo) {
        // 通过缓存获取预下单对象
        String key = "user_order:" + preOrderNo;
        boolean exists = redisUtil.exists(key);

        //验证-预下单是否存在
        if (!exists) {
            throw new CrmebException("预下单订单不存在");
        }

        //得到预定单信息
        String orderVoString = redisUtil.get(key).toString();
        OrderInfoVo orderInfoVo = JSONObject.parseObject(orderVoString, OrderInfoVo.class);

        //将-预下单信息-赋值到-预下单响应对象
        PreOrderResponse preOrderResponse = new PreOrderResponse();
        preOrderResponse.setOrderInfoVo(orderInfoVo);

        //读取-相关配置
        String payWeixinOpen = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_PAY_WEIXIN_OPEN);
        String yuePayStatus = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_YUE_PAY_STATUS);// 1开启 2关闭
        String storeSelfMention = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_STORE_SELF_MENTION);

        //赋值-相关配置
        preOrderResponse.setYuePayStatus(yuePayStatus);
        preOrderResponse.setPayWeixinOpen(payWeixinOpen);
        preOrderResponse.setStoreSelfMention(storeSelfMention);

        //返回-预下单响应对象
        return preOrderResponse;
    }

    /**
     * 计算订单价格
     * @param request 计算订单价格请求对象
     * @return ComputedOrderPriceResponse
     */
    @Override
    public ComputedOrderPriceResponse computedOrderPrice(OrderComputedPriceRequest request) {
        // 通过缓存获取预下单对象
        String key = "user_order:" + request.getPreOrderNo();
        boolean exists = redisUtil.exists(key);
        if (!exists) {
            throw new CrmebException("预下单订单不存在");
        }
        String orderVoString = redisUtil.get(key).toString();
        OrderInfoVo orderInfoVo = JSONObject.parseObject(orderVoString, OrderInfoVo.class);
        User user = userService.getInfoException();
        return computedPrice(request, orderInfoVo, user);
    }

    /**
     * 根据地址得到区域代理ID
     * @param userAddress 用户地址信息
     * @Author 零风
     * @Date  2022/6/15 16:34
     * @return
     */
    public Integer getWhereRAMerId(UserAddress userAddress){
        RegionalAgency regionalAgency=regionalAgencyService.getRAUid(userAddress);// 得到-区域代理ID
        if(regionalAgency != null){
            return regionalAgency.getId(); //区域代理ID标识
        }else{
            return null;
        }
    }

    /**
     * 创建订单
     * @param request 创建订单请求参数
     * @return MyRecord 订单编号
     */
    @Override
    public MyRecord createOrder(CreateOrderRequest request) {
        // 得到-当前登录用户
        User user = userService.getInfoException();

        //验证推荐人是否与下单人相同
        Integer spreadUid=request.getSpreadUid();
        if(user.getUid().equals(spreadUid)){
            throw new CrmebException("哦！这个漏洞都被你发现了~推荐人不能是自己！");
        }

        // 通过缓存获取预下单对象
        String key = "user_order:" + request.getPreOrderNo();
        boolean exists = redisUtil.exists(key);

        //验证-预下单是否存在
        if (!exists) {
            throw new CrmebException("预下单订单不存在");
        }

        //得到-预下单信息
        String orderVoString = redisUtil.get(key).toString();
        OrderInfoVo orderInfoVo = JSONObject.parseObject(orderVoString, OrderInfoVo.class);

        // 检测支付方式
        if(!orderUtils.checkPayType(request.getPayType())) throw new CrmebException("暂不支持该支付方式，请刷新页面或者联系管理员");

        // 检测-微信支付以及支付渠道
        if (request.getPayType().equals(PayConstants.PAY_TYPE_WE_CHAT)) {
            if (StrUtil.isBlank(request.getPayChannel())) throw new CrmebException("支付渠道不能为空!");
            if (!OrderUtils.checkPayChannel(request.getPayChannel())) throw new CrmebException("支付渠道不存在!");
        }

        // 校验商品库存
        List<MyRecord> skuRecordList = validateProductStock(orderInfoVo, user);

        // 定义变量
        String verifyCode = "";     //核销码
        String userAddressStr = ""; //详细地址
        Integer merId = 0;          //默认系统
        Integer orderType = 0;           //默认系统订单
        Integer raMerId = 0;        //区域代理ID
        UserAddress userAddress=null;   //收货地址信息
        Boolean isLmsjfahuo = Boolean.FALSE;

        //验证-快递类型
        if(request.getOrderType()!=null)orderType=request.getOrderType();
        Integer shippingType= request.getShippingType();
        switch (shippingType){
            case Constants.ORDER_SHIPPING_TYPE_KUAIDI: // 快递配送
                //验证-收货地址ID
                if(request.getAddressId() == null)request.setAddressId(0);
                if(request.getAddressId() <= 0) throw new CrmebException("请选择收货地址");

                //得到-收货地址信息
                userAddress = userAddressService.getById(request.getAddressId());

                //验证-收货地址信息是否正确
                if(ObjectUtil.isNull(userAddress) || userAddress.getIsDel()){
                    throw new CrmebException("收货地址有误");
                }

                //赋值-收货人信息
                //拼接-详细地址
                if(orderType == Constants.ORDER_TYPE_5) {// 联盟商家订单
                    request.setRealName(new StringBuffer("联盟商家||用户快递:").append(userAddress.getRealName()).toString());
                }else{
                    request.setRealName(userAddress.getRealName());
                }
                request.setPhone(userAddress.getPhone());
                userAddressStr = new StringBuffer(userAddress.getProvince())
                        .append(userAddress.getCity())
                        .append(userAddress.getDistrict())
                        .append(userAddress.getDetail())
                        .toString();

                //匹配区域代理
                raMerId = this.getWhereRAMerId(userAddress);
                if(raMerId != null){
                    merId = raMerId; //区域代理ID标识
                    orderType = Constants.ORDER_TYPE_2; // 区域代理订单
                }else{
                    merId=orderInfoVo.getMerId();
                    orderType = Constants.ORDER_TYPE_4; // 供应商订单
                }
                break;
            case Constants.ORDER_SHIPPING_TYPE_ZITI: // 到店自提
                //验证-收货人是否为空
                if(StringUtils.isBlank(request.getRealName()) || StringUtils.isBlank(request.getPhone())) {
                    throw new CrmebException("请填写姓名和电话");
                }

                // 自提开关是否打开
                String storeSelfMention = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_STORE_SELF_MENTION);
                if (storeSelfMention.equals("false")) {
                    throw new CrmebException("请先联系管理员开启门店自提");
                }

                //验证-门店是否存在
                SystemStore systemStore = systemStoreService.getById(request.getStoreId());
                if (ObjectUtil.isNull(systemStore) || systemStore.getIsDel() || !systemStore.getIsShow()) {
                    throw new CrmebException("暂无门店无法选择门店自提");
                }

                //生成-核销码
                verifyCode = CrmebUtil.randomCount(1111111111,999999999)+"";

                //取出-详细地址
                userAddressStr = systemStore.getName();
                break;
            case Constants.ORDER_SHIPPING_TYPE_NO://无需配送
                //得到-零售商信息
                if(orderType == Constants.ORDER_TYPE_3){// 零售商订单
                    merId=orderInfoVo.getMerId();
                    Retailer retailer= retailerService.getById(merId); //零售商id标识
                    if(retailer != null){
                        //拼接-详细地址
                        userAddressStr = retailer.getReAddress();
                    }else{
                        //拼接-详细地址
                        userAddressStr = "无需配送！";
                    }
                }else if(orderType == Constants.ORDER_TYPE_5){// 联盟商家订单
                    //得到默认地址
                    isLmsjfahuo = Boolean.TRUE;
                    userAddress = userAddressService.getDefault(spreadUid);
                    if(userAddress !=null){
                        request.setRealName(new StringBuffer("联盟商家:").append(userAddress.getRealName()).toString());
                        request.setPhone(userAddress.getPhone());
                        userAddressStr = new StringBuffer(userAddress.getProvince())
                                .append(userAddress.getCity())
                                .append(userAddress.getDistrict())
                                .append(userAddress.getDetail())
                                .toString();
                    }else{
                        request.setRealName("九秒中");
                        request.setPhone("18585094270");
                        userAddressStr = "没有默认地址！";
                    }

                    //匹配区域代理
                    raMerId = this.getWhereRAMerId(userAddress);
                    if(raMerId != null){
                        merId = raMerId; //区域代理ID标识
                        orderType = Constants.ORDER_TYPE_2; // 区域代理订单
                    }else{
                        merId = orderInfoVo.getMerId(); //没有就取商品供应商
                        orderType = Constants.ORDER_TYPE_4; // 供应商订单
                    }
                }
                break;
            default:
                throw new CrmebException("快递类型错误！");
        }

        //实例化-计算订单价格请求对象
        OrderComputedPriceRequest orderComputedPriceRequest = new OrderComputedPriceRequest();
        orderComputedPriceRequest.setShippingType(shippingType);
        orderComputedPriceRequest.setAddressId(request.getAddressId());
        orderComputedPriceRequest.setCouponId(request.getCouponId());
        orderComputedPriceRequest.setUseIntegral(request.getUseIntegral());

        //计算订单各种价格
        ComputedOrderPriceResponse computedOrderPriceResponse = computedPrice(orderComputedPriceRequest, orderInfoVo, user);

        // 购买赠送的积分
        int gainIntegral = 0;
        List<StoreOrderInfo> storeOrderInfos = new ArrayList<>();
        for (OrderInfoDetailVo detailVo : orderInfoVo.getOrderDetailList()) {
            // 订单详情
            StoreOrderInfo soInfo = new StoreOrderInfo();
            soInfo.setProductId(detailVo.getProductId());
            soInfo.setInfo(JSON.toJSON(detailVo).toString());
            soInfo.setUnique(detailVo.getAttrValueId().toString());
            storeOrderInfos.add(soInfo);
        }

        // 支付渠道 默认：余额支付
        int isChannel = 3;
        if (request.getPayType().equals(PayConstants.PAY_TYPE_WE_CHAT)) {
            isChannel = orderUtils.getIsChanneLWeiXin(request.getPayChannel());
        }else if(request.getPayType().equals(PayConstants.PAY_TYPE_ALI_PAY)){
            switch (request.getPayChannel()){
                case PayConstants.PAY_CHANNEL_ZFB_CHAT_APP_ANDROID:
                    isChannel = 6;
                    break;
                case PayConstants.PAY_CHANNEL_ZFB_CHAT_APP_IOS:
                    isChannel = 7;
                    break;
                case PayConstants.PAY_CHANNEL_ZFB_CHAT_APP_WEB:
                    isChannel = 8;
                    break;
            }
        }else if(request.getPayType().equals(PayConstants.PAY_TYPE_INTEGRAL)){
            isChannel = 9;
        }

        // 生成订单号
        String orderNo = CrmebUtil.getOrderNo("order");

        //订单
        StoreOrder storeOrder = new StoreOrder();
        storeOrder.setUid(user.getUid());
        storeOrder.setSpId(user.getSpId());
        storeOrder.setSptlId(user.getSptlId());
        storeOrder.setOrderId(orderNo);
        storeOrder.setRealName(request.getRealName());
        storeOrder.setUserPhone(request.getPhone());
        storeOrder.setUserAddress(userAddressStr);
        storeOrder.setTotalNum(orderInfoVo.getOrderProNum());
        storeOrder.setCouponId(Optional.ofNullable(request.getCouponId()).orElse(0));
        storeOrder.setMerId(merId); //默认系统/商品绑定的商户ID/区域代理ID/零售商ID/店铺ID/供应商ID。。
        storeOrder.setType(orderType);   //默认普通订单/视频号订单/区域代理订单/零售商订单/供应商订单
        storeOrder.setIsLmsjfahuo(isLmsjfahuo);

        //验证推荐人自己是否消费过
        storeOrder.setSpreadUid(spreadUid);//推广人ID(表示这个订单是这个人推广的)
        //if(request.getSpreadUid()>0){
        //    BigDecimal orderStatusSum = storeOrderService.getSumBigDecimal(user.getUid(), null, null);//用于验证下单时推荐人自己有没有消费过
        //    if(orderStatusSum.compareTo(BigDecimal.ZERO) < 1){//如果小于等于零
        //        storeOrder.setSpreadUid(0);
        //    }
        //}

        //验证是否开具发票
        if(request.getIsInvoice()){
            storeOrder.setRiseId(request.getRiseId());
        }

        // 如果是自提
        if(shippingType == Constants.ORDER_SHIPPING_TYPE_ZITI){
            storeOrder.setVerifyCode(verifyCode);
            storeOrder.setStoreId(request.getStoreId());
        }

        // 订单总价
        BigDecimal totalPrice = computedOrderPriceResponse.getProTotalFee().add(computedOrderPriceResponse.getFreightFee());
        storeOrder.setTotalPrice(totalPrice);
        storeOrder.setTotalPostage(computedOrderPriceResponse.getFreightFee());
        storeOrder.setCouponPrice(computedOrderPriceResponse.getCouponFee());
        storeOrder.setPayPrice(computedOrderPriceResponse.getPayFee());
        storeOrder.setPayPostage(computedOrderPriceResponse.getFreightFee());
        storeOrder.setDeductionPrice(computedOrderPriceResponse.getDeductionPrice());
        storeOrder.setPayType(request.getPayType());
        storeOrder.setUseIntegral(computedOrderPriceResponse.getUsedIntegral());
        storeOrder.setGainIntegral(gainIntegral);
        storeOrder.setMark(StringEscapeUtils.escapeHtml4(request.getMark()));
        storeOrder.setCombinationId(orderInfoVo.getCombinationId());
        storeOrder.setPinkId(orderInfoVo.getPinkId());
        storeOrder.setSeckillId(orderInfoVo.getSeckillId());
        storeOrder.setBargainId(orderInfoVo.getBargainId());
        storeOrder.setBargainUserId(orderInfoVo.getBargainUserId());
        storeOrder.setCreateTime(DateUtil.nowDateTime());
        storeOrder.setShippingType(shippingType);
        storeOrder.setIsChannel(isChannel);
        storeOrder.setPaid(false);
        storeOrder.setCost(orderInfoVo.getCost());
        storeOrder.setFreightPrice(computedOrderPriceResponse.getFreightFee());
        storeOrder.setRefundPrice(BigDecimal.ZERO);
        storeOrder.setIsBrokerageAbonus(orderInfoVo.getIsBrokerageAbonus());
        storeOrder.setQuotaControl(orderInfoVo.getQuotaControl());

        // 优惠券修改
        StoreCouponUser storeCouponUser = new StoreCouponUser();
        if (storeOrder.getCouponId() > 0) {
            storeCouponUser = storeCouponUserService.getById(storeOrder.getCouponId());
            storeCouponUser.setStatus(1);
        }
        StoreCouponUser finalStoreCouponUser = storeCouponUser;

        //执行
        Boolean execute = transactionTemplate.execute(e -> {
            // 扣减库存
            // 需要根据是否活动商品，扣减不同的库存
            if (storeOrder.getSeckillId() > 0) {// 秒杀扣库存
                MyRecord skuRecord = skuRecordList.get(0);
                // 秒杀商品扣库存
                storeSeckillService.operationStock(skuRecord.getInt("activityId"), skuRecord.getInt("num"), "sub");
                // 秒杀商品规格扣库存
                storeProductAttrValueService.operationStock(skuRecord.getInt("activityAttrValueId"), skuRecord.getInt("num"), "sub", Constants.PRODUCT_TYPE_SECKILL);
                // 普通商品口库存
                storeProductService.operationStock(skuRecord.getInt("productId"), skuRecord.getInt("num"), "sub");
                // 普通商品规格扣库存
                storeProductAttrValueService.operationStock(skuRecord.getInt("attrValueId"), skuRecord.getInt("num"), "sub", Constants.PRODUCT_TYPE_NORMAL);
            } else if (storeOrder.getBargainId() > 0) {// 砍价扣库存
                MyRecord skuRecord = skuRecordList.get(0);
                // 砍价商品扣库存
                storeBargainService.operationStock(skuRecord.getInt("activityId"), skuRecord.getInt("num"), "sub");
                // 砍价商品规格扣库存
                storeProductAttrValueService.operationStock(skuRecord.getInt("activityAttrValueId"), skuRecord.getInt("num"), "sub", Constants.PRODUCT_TYPE_BARGAIN);
                // 普通商品口库存
                storeProductService.operationStock(skuRecord.getInt("productId"), skuRecord.getInt("num"), "sub");
                // 普通商品规格扣库存
                storeProductAttrValueService.operationStock(skuRecord.getInt("attrValueId"), skuRecord.getInt("num"), "sub", Constants.PRODUCT_TYPE_NORMAL);
            } else if (storeOrder.getCombinationId() > 0) {// 拼团扣库存
                MyRecord skuRecord = skuRecordList.get(0);
                // 拼团商品扣库存
                Boolean operationStock = storeCombinationService.operationStock(skuRecord.getInt("activityId"), skuRecord.getInt("num"), "sub");
                System.out.println("拼团商品扣库存operationStock " + operationStock);
                // 拼团商品规格扣库存
                storeProductAttrValueService.operationStock(skuRecord.getInt("activityAttrValueId"), skuRecord.getInt("num"), "sub", Constants.PRODUCT_TYPE_PINGTUAN);
                // 普通商品口库存
                storeProductService.operationStock(skuRecord.getInt("productId"), skuRecord.getInt("num"), "sub");
                // 普通商品规格扣库存
                storeProductAttrValueService.operationStock(skuRecord.getInt("attrValueId"), skuRecord.getInt("num"), "sub", Constants.PRODUCT_TYPE_NORMAL);
            } else { // 普通商品
                for (MyRecord skuRecord : skuRecordList) {
                    // 普通商品口库存
                    storeProductService.operationStock(skuRecord.getInt("productId"), skuRecord.getInt("num"), "sub");
                    // 普通商品规格扣库存
                    storeProductAttrValueService.operationStock(skuRecord.getInt("attrValueId"), skuRecord.getInt("num"), "sub", Constants.PRODUCT_TYPE_NORMAL);
                }
            }

            //创建订单
            storeOrderService.create(storeOrder);

            // 优惠券修改
            if (storeOrder.getCouponId() > 0) {
                storeCouponUserService.updateById(finalStoreCouponUser);
            }

            // 保存购物车商品详情
            storeOrderInfos.forEach(info -> info.setOrderId(storeOrder.getId()));
            storeOrderInfoService.saveOrderInfos(storeOrderInfos);

            // 生成订单日志
            storeOrderStatusService.createLog(storeOrder.getId(), Constants.ORDER_STATUS_CACHE_CREATE_ORDER, "订单生成");

            // 清除购物车数据
            if (CollUtil.isNotEmpty(orderInfoVo.getCartIdList())) {
                storeCartService.deleteCartByIds(orderInfoVo.getCartIdList());
            }

            return Boolean.TRUE;
        });

        //验证-执行结果
        if (!execute) {
            throw new CrmebException("订单生成失败");
        }

        // 删除缓存订单
        if (redisUtil.exists(key)) {
            redisUtil.remove(key);
        }

        // 加入自动未支付自动取消队列
        redisUtil.lPush(Constants.ORDER_AUTO_CANCEL_KEY, storeOrder.getOrderId());

        // 发送后台管理员下单提醒通知短信
        sendAdminOrderNotice(storeOrder.getOrderId());

        //返回-订单号
        MyRecord record = new MyRecord();
        record.set("orderNo", storeOrder.getOrderId());
        return record;
    }

    /**
     * 校验商品库存（生成订单）
     * @param orderInfoVo 订单详情Vo
     * @return List<MyRecord> 库存对象
     * ——返回以下字段
     * ——activityId             活动商品id
     * ——activityAttrValueId    活动商品skuId
     * ——productId              普通（主）商品id
     * ——attrValueId            普通（主）商品skuId
     * ——num                    购买数量
     */
    private List<MyRecord> validateProductStock(OrderInfoVo orderInfoVo, User user) {
        //定义-库存对象集合
        List<MyRecord> recordList = CollUtil.newArrayList();

        //定义变量
        Integer huodoShopId=null;           //活动商品ID
        Integer huodoShopSkuId=null;        //活动商品sku属性值ID
        Integer mianShopId=null;            //主商品ID
        Integer mainShopSkuId=null;         //主商品sku属性值ID
        Integer num=0;                      //购买数量

        //商品订单详情，第0个Vo对象
        OrderInfoDetailVo detailVo = orderInfoVo.getOrderDetailList().get(0);

        //判断-秒杀商品
        if (orderInfoVo.getSeckillId() > 0) {
            //得到-秒杀商品
            Integer seckillId = orderInfoVo.getSeckillId();
            StoreSeckill storeSeckill = storeSeckillService.getByIdException(seckillId);

            //根据-商品订单详情-得到秒杀商品sku属性值对象
            StoreProductAttrValue seckillAttrValue = storeProductAttrValueService.getByIdAndProductIdAndType(
                    detailVo.getAttrValueId(),
                    seckillId,
                    Constants.PRODUCT_TYPE_SECKILL);

            //公共校验-秒杀商品和属性值
            MyRecord seckillRecord = commonValidateSeckill(storeSeckill, seckillAttrValue, user, detailVo.getPayNum());
            StoreProductAttrValue productAttrValue = seckillRecord.get("productAttrValue");

            //赋值
            huodoShopId = seckillId;
            huodoShopSkuId = seckillAttrValue.getId();
            mianShopId  = storeSeckill.getProductId();
            mainShopSkuId = productAttrValue.getId();
            num = detailVo.getPayNum();
        }

        //判断-砍价商品
        else if (orderInfoVo.getBargainId() > 0) {
            //得到-砍价商品
            Integer bargainId = orderInfoVo.getBargainId();
            StoreBargain storeBargain = storeBargainService.getByIdException(bargainId);

            //根据-商品订单详情-得到砍价商品sku属性值对象
            StoreProductAttrValue bargainAttrValue = storeProductAttrValueService.getByIdAndProductIdAndType(
                    detailVo.getAttrValueId(),
                    bargainId,
                    Constants.PRODUCT_TYPE_BARGAIN);

            //公共校验-砍价商品
            MyRecord bargainRecord = commonValidateBargain(storeBargain, bargainAttrValue, orderInfoVo.getBargainUserId(), user, detailVo.getPayNum());
            StoreProductAttrValue productAttrValue = bargainRecord.get("productAttrValue");

            //赋值
            huodoShopId = bargainId;
            huodoShopSkuId = bargainAttrValue.getId();
            mianShopId  = storeBargain.getProductId();
            mainShopSkuId = productAttrValue.getId();
            num = detailVo.getPayNum();
        }

        //判断-拼团商品
        else if (orderInfoVo.getCombinationId() > 0) {
            //得到-拼团商品
            Integer combinationId = orderInfoVo.getCombinationId();
            StoreCombination storeCombination = storeCombinationService.getByIdException(combinationId);

            //得到-拼团属性值
            StoreProductAttrValue combinationAttrValue = storeProductAttrValueService.getByIdAndProductIdAndType(detailVo.getAttrValueId(), combinationId, Constants.PRODUCT_TYPE_PINGTUAN);
            if (ObjectUtil.isNull(combinationAttrValue)) {
                throw new CrmebException("拼团商品规格不存在");
            }

            //公共校验-拼团商品
            MyRecord combinationRecord = commonValidateCombination(storeCombination, combinationAttrValue, user, detailVo.getPayNum());
            StoreProductAttrValue productAttrValue = combinationRecord.get("productAttrValue");

            //赋值
            huodoShopId = combinationId;
            huodoShopSkuId = combinationAttrValue.getId();
            mianShopId  = storeCombination.getProductId();
            mainShopSkuId = productAttrValue.getId();
            num = detailVo.getPayNum();
        }

        //普通商品
        else {
            //得到-订单详情
            List<OrderInfoDetailVo> orderDetailList = orderInfoVo.getOrderDetailList();
            orderDetailList.forEach(e -> {
                // 查询商品信息
                StoreProduct storeProduct = storeProductService.getById(e.getProductId());

                // 商品验证
                if (ObjectUtil.isNull(storeProduct)) {
                    throw new CrmebException("购买的商品信息不存在");
                }
                if (storeProduct.getIsDel()) {
                    throw new CrmebException("购买的商品已删除");
                }
                if (!storeProduct.getIsShow()) {
                    throw new CrmebException("购买的商品已下架");
                }
                if (storeProduct.getStock().equals(0) || e.getPayNum() > storeProduct.getStock()) {
                    throw new CrmebException("购买的商品库存不足");
                }

                // 查询商品规格属性值信息
                StoreProductAttrValue attrValue = attrValueService.getByIdAndProductIdAndType(e.getAttrValueId(), e.getProductId(), Constants.PRODUCT_TYPE_NORMAL);

                // 商品属性值验证
                if (ObjectUtil.isNull(attrValue)) {
                    throw new CrmebException("购买的商品规格信息不存在");
                }
                if (attrValue.getStock() < e.getPayNum()) {
                    throw new CrmebException("购买的商品库存不足");
                }

                //生成-订单map
                MyRecord record = new MyRecord();
                record.set("productId", e.getProductId());
                record.set("num", e.getPayNum());
                record.set("attrValueId", e.getAttrValueId());
                recordList.add(record);
            });

            //返回-库存对象集合
            return recordList;
        }

        //定义-商品库存对象
        MyRecord record = new MyRecord();
        record.set("activityId", huodoShopId);
        record.set("activityAttrValueId", huodoShopSkuId);
        record.set("productId", mianShopId);
        record.set("attrValueId", mainShopSkuId);
        record.set("num", num);

        //添加到-库存对象集合
        recordList.add(record);

        //返回-库存对象集合
        return recordList;
    }


    /**
     * 校验预下单商品信息
     * @param request 预下单请求参数
     * @return OrderInfoVo
     */
    private OrderInfoVo validatePreOrderRequest(PreOrderRequest request, User user) {
        //实例化-预下单vo对象
        OrderInfoVo orderInfoVo = new OrderInfoVo();
        //定义-订单详细详细vo集合
        List<OrderInfoDetailVo> detailVoList = CollUtil.newArrayList();

        //验证-购物车购买
        if (request.getPreOrderType().equals("shoppingCart")) {// 购物车购买
            detailVoList = validatePreOrderShopping(request, user);
            List<Long> cartIdList = request.getOrderDetails().stream().map(PreOrderDetailRequest::getShoppingCartId).distinct().collect(Collectors.toList());
            orderInfoVo.setCartIdList(cartIdList);
        }

        //验证-立即购买
        else if (request.getPreOrderType().equals("buyNow")) {// 立即购买
            // 立即购买只会有一条详情
            PreOrderDetailRequest detailRequest = request.getOrderDetails().get(0);

            //验证-商品类型
            if (detailRequest.getSeckillId() > 0) {// 秒杀
                detailVoList.add(validatePreOrderSeckill(detailRequest, user));
                orderInfoVo.setSeckillId(detailRequest.getSeckillId());
            } else if (detailRequest.getBargainId() > 0) {// 砍价
                detailVoList.add(validatePreOrderBargain(detailRequest, user));
                orderInfoVo.setBargainId(detailRequest.getBargainId());
                orderInfoVo.setBargainUserId(detailRequest.getBargainUserId());
            } else if (detailRequest.getCombinationId() > 0) {// 拼团
                detailVoList.add(validatePreOrderCombination(detailRequest, user));
                orderInfoVo.setCombinationId(detailRequest.getCombinationId());
                orderInfoVo.setPinkId(detailRequest.getPinkId());
            } else {  // 普通商品
                //验证-商品订单详情参数
                if (ObjectUtil.isNull(detailRequest.getProductId())) {
                    throw new CrmebException("商品编号不能为空");
                }
                if (ObjectUtil.isNull(detailRequest.getAttrValueId())) {
                    throw new CrmebException("商品规格属性值不能为空");
                }
                if (ObjectUtil.isNull(detailRequest.getProductNum()) || detailRequest.getProductNum() < 0) {
                    throw new CrmebException("购买数量必须大于0");
                }

                // 查询商品信息
                StoreProduct storeProduct = storeProductService.getById(detailRequest.getProductId());
                orderInfoVo.setIsBrokerageAbonus(storeProduct.getIsBrokerageAbonus());
                orderInfoVo.setQuotaControl(storeProduct.getQuotaControl());
                orderInfoVo.setMerId(this.getMerId(storeProduct));

                //验证-商品信息
                if (ObjectUtil.isNull(storeProduct)) {
                    throw new CrmebException("商品信息不存在，请刷新后重新选择");
                }
                if (storeProduct.getIsDel()) {
                    throw new CrmebException("商品已删除，请刷新后重新选择");
                }
                if (!storeProduct.getIsShow()) {
                    throw new CrmebException("商品已下架，请刷新后重新选择");
                }
                if (storeProduct.getStock() < detailRequest.getProductNum()) {
                    throw new CrmebException("商品库存不足，请刷新后重新选择");
                }

                // 查询商品规格属性值信息
                StoreProductAttrValue attrValue = attrValueService.getByIdAndProductIdAndType(detailRequest.getAttrValueId(), detailRequest.getProductId(), Constants.PRODUCT_TYPE_NORMAL);

                // 验证-商品规格参数
                if (ObjectUtil.isNull(attrValue)) {
                    throw new CrmebException("商品规格信息不存在，请刷新后重新选择");
                }else if (attrValue.getStock() < detailRequest.getProductNum()) {
                    throw new CrmebException("商品规格库存不足，请刷新后重新选择");
                }else{
                    orderInfoVo.setCost(attrValue.getCost());
                }

                //实例化-订单详情Vo对象
                OrderInfoDetailVo detailVo = new OrderInfoDetailVo();
                detailVo.setProductId(storeProduct.getId());
                detailVo.setProductName(storeProduct.getStoreName());
                detailVo.setAttrValueId(attrValue.getId());
                detailVo.setSku(attrValue.getSuk());
                detailVo.setPrice(attrValue.getPrice());
                detailVo.setPayNum(detailRequest.getProductNum());
                detailVo.setImage(StrUtil.isNotBlank(attrValue.getImage()) ? attrValue.getImage() : storeProduct.getImage());
                detailVo.setVolume(attrValue.getVolume());
                detailVo.setWeight(attrValue.getWeight());
                detailVo.setTempId(storeProduct.getTempId());
                detailVo.setIsSub(storeProduct.getIsSub());
                detailVo.setGiveIntegral(storeProduct.getGiveIntegral()); //商品赠送积分
                detailVo.setIsBrokerageAbonus(orderInfoVo.getIsBrokerageAbonus());
                detailVo.setQuotaControl(orderInfoVo.getQuotaControl());
                detailVo.setMerId(orderInfoVo.getMerId());
                detailVo.setCost(orderInfoVo.getCost());

                //添加到-订单详细详细vo集合
                detailVoList.add(detailVo);
            }
        }

        //验证-再次购买
        else if (request.getPreOrderType().equals("again")) {// 再次购买
            PreOrderDetailRequest detailRequest = request.getOrderDetails().get(0);
            detailVoList = validatePreOrderAgain(detailRequest);
        }

        //赋值-商户ID
        if(request.getIsRetailer()){
            orderInfoVo.setMerId(request.getRetailerId()); // 赋值-(商户ID/零售商id)
        }

        //赋值-订单详细list
        orderInfoVo.setOrderDetailList(detailVoList);

        //返回-预下单vo对象
        return orderInfoVo;
    }

    private Integer getMerId(StoreProduct storeProduct) {
        //验证是否绑定供应商
        Integer merId = storeProduct.getMerId(); //商户->供应商ID
        Supplier supplier=supplierService.getById(merId);
        if(supplier == null) throw new CrmebException("商品未绑定供应商！无法下单购买！");
        return merId;
    }

    /**
     * 购物车预下单校验
     * @param request 请求参数
     * @param user 用户
     * @return List<OrderInfoDetailVo>
     */
    private List<OrderInfoDetailVo> validatePreOrderShopping(PreOrderRequest request, User user) {
        List<OrderInfoDetailVo> detailVoList = CollUtil.newArrayList();
        request.getOrderDetails().forEach(e -> {
            if (ObjectUtil.isNull(e.getShoppingCartId())) {
                throw new CrmebException("购物车编号不能为空");
            }
            StoreCart storeCart = storeCartService.getByIdAndUid(e.getShoppingCartId(), user.getUid());
            if (ObjectUtil.isNull(storeCart)) {
                throw new CrmebException("未找到对应的购物车信息");
            }
            // 查询商品信息
            StoreProduct storeProduct = storeProductService.getById(storeCart.getProductId());
            if (ObjectUtil.isNull(storeProduct)) {
                throw new CrmebException("商品信息不存在，请刷新后重新选择");
            }
            if (storeProduct.getIsDel()) {
                throw new CrmebException("商品已删除，请刷新后重新选择");
            }
            if (!storeProduct.getIsShow()) {
                throw new CrmebException("商品已下架，请刷新后重新选择");
            }
            if (storeProduct.getStock() < storeCart.getCartNum()) {
                throw new CrmebException("商品库存不足，请刷新后重新选择");
            }
            // 查询商品规格属性值信息
            StoreProductAttrValue attrValue = attrValueService.getByIdAndProductIdAndType(Integer.valueOf(storeCart.getProductAttrUnique()), storeCart.getProductId(), Constants.PRODUCT_TYPE_NORMAL);
            if (ObjectUtil.isNull(attrValue)) {
                throw new CrmebException("商品规格信息不存在，请刷新后重新选择");
            }
            if (attrValue.getStock() < storeCart.getCartNum()) {
                throw new CrmebException("商品规格库存不足，请刷新后重新选择");
            }
            OrderInfoDetailVo detailVo = new OrderInfoDetailVo();
            detailVo.setProductId(storeProduct.getId());
            detailVo.setProductName(storeProduct.getStoreName());
            detailVo.setAttrValueId(attrValue.getId());
            detailVo.setSku(attrValue.getSuk());
            detailVo.setPrice(attrValue.getPrice());
            detailVo.setPayNum(storeCart.getCartNum());
            detailVo.setImage(StrUtil.isNotBlank(attrValue.getImage()) ? attrValue.getImage() : storeProduct.getImage());
            detailVo.setVolume(attrValue.getVolume());
            detailVo.setWeight(attrValue.getWeight());
            detailVo.setTempId(storeProduct.getTempId());
            detailVo.setGiveIntegral(storeProduct.getGiveIntegral());
            detailVo.setIsSub(storeProduct.getIsSub());
            detailVo.setIsBrokerageAbonus(storeProduct.getIsBrokerageAbonus());
            detailVo.setQuotaControl(storeProduct.getQuotaControl());
            detailVo.setMerId(this.getMerId(storeProduct));
            detailVo.setCost(attrValue.getCost());
            detailVoList.add(detailVo);
        });
        return detailVoList;
    }

    /**
     * 秒杀预下单校验
     * @param detailRequest 请求参数
     * @param user 用户
     * @return OrderInfoDetailVo
     */
    private OrderInfoDetailVo validatePreOrderSeckill(PreOrderDetailRequest detailRequest, User user) {
        Integer seckillId = detailRequest.getSeckillId();
        StoreSeckill storeSeckill = storeSeckillService.getByIdException(seckillId);
        StoreProductAttrValue seckillAttrValue = storeProductAttrValueService.getByIdAndProductIdAndType(
                detailRequest.getAttrValueId(),
                seckillId,
                Constants.PRODUCT_TYPE_SECKILL);
        if (ObjectUtil.isNull(seckillAttrValue)) {
            throw new CrmebException("秒杀商品规格不存在");
        }
        commonValidateSeckill(storeSeckill, seckillAttrValue, user, detailRequest.getProductNum());

        OrderInfoDetailVo detailVo = new OrderInfoDetailVo();
        detailVo.setProductId(storeSeckill.getProductId());
        detailVo.setProductName(storeSeckill.getTitle());
        detailVo.setAttrValueId(seckillAttrValue.getId());
        detailVo.setSku(seckillAttrValue.getSuk());
        detailVo.setPrice(seckillAttrValue.getPrice());
        detailVo.setPayNum(detailRequest.getProductNum());
        detailVo.setImage(StrUtil.isNotBlank(seckillAttrValue.getImage()) ? seckillAttrValue.getImage() : storeSeckill.getImage());
        detailVo.setVolume(seckillAttrValue.getVolume());
        detailVo.setWeight(seckillAttrValue.getWeight());
        detailVo.setTempId(storeSeckill.getTempId());
        return detailVo;
    }

    /**
     * 公共校验-秒杀商品和属性值
     * @param storeSeckill 秒杀商品
     * @param seckillAttrValue 秒杀商品规格属性
     * @param user 用户
     * @return 属性值对象
     */
    private MyRecord commonValidateSeckill(StoreSeckill storeSeckill, StoreProductAttrValue seckillAttrValue, User user, Integer productNum) {
        // 秒杀商品判断
        if (storeSeckill.getStock().equals(0) || productNum > storeSeckill.getStock()) {
            throw new CrmebException("秒杀商品库存不足");
        }

        // 判断-秒杀商品规格属性值
        if (ObjectUtil.isNull(seckillAttrValue)) {
            throw new CrmebException("秒杀商品规格不存在");
        }
        if (seckillAttrValue.getStock() <= 0 || seckillAttrValue.getQuota() <= 0 || productNum > seckillAttrValue.getStock()) {
            throw new CrmebException("秒杀商品规格库存不足");
        }

        // 主商品部分判断
        StoreProduct product = storeProductService.getById(storeSeckill.getProductId());
        if (ObjectUtil.isNull(product) || product.getIsDel()) {
            throw new CrmebException("秒杀主商品不存在");
        }
        if (product.getStock().equals(0) || productNum > product.getStock()) {
            throw new CrmebException("秒杀主商品库存不足");
        }

        // 主商品sku判断
        StoreProductAttrValue productAttrValue = storeProductAttrValueService.getByProductIdAndSkuAndType(storeSeckill.getProductId(), seckillAttrValue.getSuk(), Constants.PRODUCT_TYPE_NORMAL);
        if (ObjectUtil.isNull(productAttrValue)) {
            throw new CrmebException("秒杀主商品规格不存在");
        }
        if (productAttrValue.getStock() <= 0 || productNum > productAttrValue.getStock()) {
            throw new CrmebException("秒杀主商品规格库存不足");
        }

        // 判断秒杀是否过期 1:日期是否在范围内 2：时间是否在每天的时间段内
        StoreSeckillManger seckillManger = storeSeckillMangerService.getById(storeSeckill.getTimeId());
        if (ObjectUtil.isNull(seckillManger)) {
            throw new CrmebException("秒杀时段不存在");
        }

        // 判断日期是否过期
        DateTime nowDateTime = cn.hutool.core.date.DateUtil.date();
        String stopTimeStr = DateUtil.dateToStr(storeSeckill.getStopTime(), Constants.DATE_FORMAT_DATE);
        Date stopDate = DateUtil.strToDate( stopTimeStr  + " " + seckillManger.getEndTime() +":00:00", Constants.DATE_FORMAT);
        if (nowDateTime.getTime() - stopDate.getTime() >= 0) {
            throw new CrmebException("秒杀商品已过期");
        }

        // 判断是否在秒杀时段内（小时）,秒杀开始时间 <= 当前时间 <= 秒杀结束时间
        int hour = nowDateTime.getField(Calendar.HOUR_OF_DAY);// 现在的小时
        if (seckillManger.getStartTime() > hour || seckillManger.getEndTime() < hour) {
            throw new CrmebException("秒杀商品已过期");
        }

        // 判断秒杀购买次数
        // 当天参与活动次数 -根据用户和秒杀信息查询当天订单判断订单数量
        List<StoreOrder> userCurrentDaySecKillOrders = storeOrderService.getUserCurrentDaySecKillOrders(user.getUid(), storeSeckill.getId());
        if (CollUtil.isNotEmpty(userCurrentDaySecKillOrders)) {
            // 判断是否有待支付订单
            List<StoreOrder> unPayOrders = userCurrentDaySecKillOrders.stream().filter(e -> !e.getPaid()).collect(Collectors.toList());
            if(unPayOrders.size() > 0) {
                throw new CrmebException("您有秒杀待支付订单，请支付后再购买");
            }

            // 判断是否达到上线
            if(userCurrentDaySecKillOrders.size() >= storeSeckill.getNum()){
                throw new CrmebException("您已经达到当前秒杀活动上限");
            }
        }

        //定义-公共map对象-返回属性值对象
        MyRecord record = new MyRecord();
        record.set("productAttrValue", productAttrValue);
        return record;
    }

    /**
     * 砍价预下单校验
     * @param detailRequest 请求参数
     * @param user 用户
     * @return OrderInfoDetailVo
     */
    private OrderInfoDetailVo validatePreOrderBargain(PreOrderDetailRequest detailRequest, User user) {
        if (detailRequest.getBargainUserId() <= 0) {
            throw new CrmebException("用户砍价活动id必须大于0");
        }
        // 砍价部分判断
        Integer bargainId = detailRequest.getBargainId();
        StoreBargain storeBargain = storeBargainService.getByIdException(bargainId);
        StoreProductAttrValue bargainAttrValue = storeProductAttrValueService.getByIdAndProductIdAndType(detailRequest.getAttrValueId(), bargainId, Constants.PRODUCT_TYPE_BARGAIN);
        if (ObjectUtil.isNull(bargainAttrValue)) {
            throw new CrmebException("砍价商品规格不存在");
        }
        commonValidateBargain(storeBargain, bargainAttrValue, detailRequest.getBargainUserId(), user, detailRequest.getProductNum());


        OrderInfoDetailVo detailVo = new OrderInfoDetailVo();
        detailVo.setProductId(storeBargain.getProductId());
        detailVo.setProductName(storeBargain.getTitle());
        detailVo.setAttrValueId(bargainAttrValue.getId());
        detailVo.setSku(bargainAttrValue.getSuk());
        detailVo.setPrice(storeBargain.getMinPrice());
        detailVo.setPayNum(detailRequest.getProductNum());
        detailVo.setImage(StrUtil.isNotBlank(bargainAttrValue.getImage()) ? bargainAttrValue.getImage() : bargainAttrValue.getImage());
        detailVo.setVolume(bargainAttrValue.getVolume());
        detailVo.setWeight(bargainAttrValue.getWeight());
        detailVo.setTempId(storeBargain.getTempId());
        return detailVo;
    }

    /**
     * 公共校验-砍价商品
     * @param storeBargain 砍价商品
     * @param bargainAttrValue 砍价商品规格属性
     * @param bargainUserId 砍价活动id
     * @param user 用户
     * @param productNum 购买数量
     * @return MyRecord
     */
    private MyRecord commonValidateBargain(StoreBargain storeBargain, StoreProductAttrValue bargainAttrValue, Integer bargainUserId, User user, Integer productNum) {
        //验证-砍价商品
        if (storeBargain.getStock().equals(0) || productNum > storeBargain.getStock()) {
            throw new CrmebException("砍价商品库存不足");
        }
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis > storeBargain.getStopTime()) {
            throw new CrmebException("砍价活动已结束");
        }

        //验证-用户砍价活动是否存在取消
        StoreBargainUser bargainUser = storeBargainUserService.getById(bargainUserId);
        if (ObjectUtil.isNull(bargainUser)) {
            throw new CrmebException("用户砍价活动不存在");
        }
        if (bargainUser.getIsDel()) {
            throw new CrmebException("用户砍价活动已取消");
        }
        if (!bargainUser.getStatus().equals(3)) {
            if (bargainUser.getBargainPriceMin().compareTo(bargainUser.getBargainPrice().subtract(bargainUser.getPrice())) != 0) {
                throw new CrmebException("请先完成砍价活动");
            }
        }

        // 判断是否有订单
        StoreOrder bargainOrder = storeOrderService.getByBargainOrder(bargainUser.getBargainId(), bargainUser.getId());
        if (ObjectUtil.isNotNull(bargainOrder)) {
            if (!bargainOrder.getPaid()) {
                throw new CrmebException("订单已创建，尚未支付");
            }
            throw new CrmebException("该砍价活动已创建了订单");
        }

        // 参与活动次数 -根据用户和秒杀信息查询当天订单判断订单数量
        List<StoreOrder> userCurrentBargainOrders = storeOrderService.getUserCurrentBargainOrders(user.getUid(), storeBargain.getId());
        if (CollUtil.isNotEmpty(userCurrentBargainOrders)) {
            // 判断是否有待支付订单
            List<StoreOrder> unPayOrders = userCurrentBargainOrders.stream().filter(e -> !e.getPaid()).collect(Collectors.toList());
            if (unPayOrders.size() > 0) throw new CrmebException("您有砍价待支付订单，请支付后再购买");

            // 判断是否达到上限
            if (CollUtil.isNotEmpty(userCurrentBargainOrders) && userCurrentBargainOrders.size() >= storeBargain.getNum()){
                throw new CrmebException("您已经达到当前砍价活动上限");
            }
        }

        //验证-砍价商品属性值
        if (ObjectUtil.isNull(bargainAttrValue)) {
            throw new CrmebException("砍价商品规格不存在");
        }
        if (bargainAttrValue.getStock() <= 0 || bargainAttrValue.getQuota() <= 0 || productNum > bargainAttrValue.getStock()) {
            throw new CrmebException("砍价商品规格库存不足");
        }

        // 主商品部分判断
        StoreProduct product = storeProductService.getById(storeBargain.getProductId());
        if (ObjectUtil.isNull(product) || product.getIsDel()) {
            throw new CrmebException("砍价主商品不存在");
        }
        if (product.getStock().equals(0) || productNum > product.getStock()) {
            throw new CrmebException("砍价主商品库存不足");
        }

        // 主商品sku
        StoreProductAttrValue productAttrValue = storeProductAttrValueService.getByProductIdAndSkuAndType(storeBargain.getProductId(), bargainAttrValue.getSuk(), Constants.PRODUCT_TYPE_NORMAL);
        if (ObjectUtil.isNull(productAttrValue)) {
            throw new CrmebException("砍价主商品规格不存在");
        }
        if (productAttrValue.getStock() <= 0 || productNum > productAttrValue.getStock()) {
            throw new CrmebException("砍价主商品规格库存不足");
        }

        //定义-公共map对象-返回属性值对象
        MyRecord record = new MyRecord();
        record.set("productAttrValue", productAttrValue);
        return record;
    }

    /**
     * 拼团预下单校验
     * @param detailRequest 请求参数
     * @param user 用户
     * @return OrderInfoDetailVo
     */
    private OrderInfoDetailVo validatePreOrderCombination(PreOrderDetailRequest detailRequest, User user) {
        // 拼团部分判断
        Integer combinationId = detailRequest.getCombinationId();
        StoreCombination storeCombination = storeCombinationService.getByIdException(combinationId);
        if (storeCombination.getStock().equals(0) || detailRequest.getProductNum() > storeCombination.getStock()) {
            throw new CrmebException("拼团商品库存不足");
        }
        StoreProductAttrValue combinationAttrValue = storeProductAttrValueService.getByIdAndProductIdAndType(detailRequest.getAttrValueId(), combinationId, Constants.PRODUCT_TYPE_PINGTUAN);
        if (ObjectUtil.isNull(combinationAttrValue)) {
            throw new CrmebException("拼团商品规格不存在");
        }
        commonValidateCombination(storeCombination, combinationAttrValue, user, detailRequest.getProductNum());

        OrderInfoDetailVo detailVo = new OrderInfoDetailVo();
        detailVo.setProductId(storeCombination.getProductId());
        detailVo.setProductName(storeCombination.getTitle());
        detailVo.setAttrValueId(combinationAttrValue.getId());
        detailVo.setSku(combinationAttrValue.getSuk());
        detailVo.setPrice(combinationAttrValue.getPrice());
        detailVo.setPayNum(detailRequest.getProductNum());
        detailVo.setImage(StrUtil.isNotBlank(combinationAttrValue.getImage()) ? combinationAttrValue.getImage() : combinationAttrValue.getImage());
        detailVo.setVolume(combinationAttrValue.getVolume());
        detailVo.setWeight(combinationAttrValue.getWeight());
        detailVo.setTempId(storeCombination.getTempId());
        return detailVo;
    }

    /**
     * 公共校验拼团
     * @param storeCombination 砍价商品
     * @param combinationAttrValue 砍价商品规格属性
     * @param user 用户
     * @param productNum 购买数量
     * @return MyRecord
     */
    private MyRecord commonValidateCombination(StoreCombination storeCombination, StoreProductAttrValue combinationAttrValue, User user, Integer productNum) {
        // 判断拼团时间段
        long timeMillis = System.currentTimeMillis();
        if (timeMillis < storeCombination.getStartTime()) {
            throw new CrmebException("拼团商品活动未开始");
        }
        if (timeMillis >= storeCombination.getStopTime()) {
            throw new CrmebException("拼团商品已过期");
        }
        // 判断购买数量
        if (productNum > storeCombination.getOnceNum()) {
            throw new CrmebException("购买数量超过单次拼团购买上限");
        }

        if (combinationAttrValue.getStock() <= 0 || combinationAttrValue.getQuota() <= 0 || productNum > combinationAttrValue.getStock()) {
            throw new CrmebException("拼团商品规格库存不足");
        }
        // 普通商品部分判断
        StoreProduct product = storeProductService.getById(storeCombination.getProductId());
        if (ObjectUtil.isNull(product) || product.getIsDel()) {
            throw new CrmebException("拼团主商品不存在");
        }
        if (product.getStock().equals(0) || productNum > product.getStock()) {
            throw new CrmebException("拼团主商品库存不足");
        }
        // 主商品sku
        StoreProductAttrValue productAttrValue = storeProductAttrValueService.getByProductIdAndSkuAndType(storeCombination.getProductId(), combinationAttrValue.getSuk(), Constants.PRODUCT_TYPE_NORMAL);
        if (ObjectUtil.isNull(productAttrValue)) {
            throw new CrmebException("拼团主商品规格不存在");
        }
        if (productAttrValue.getStock() <= 0 || productNum > productAttrValue.getStock()) {
            throw new CrmebException("拼团主商品规格库存不足");
        }

        // 用户参与活动的次数
        List<StoreOrder> userCombinationOrders = storeOrderService.getUserCurrentCombinationOrders(user.getUid(), storeCombination.getId());
        if (CollUtil.isNotEmpty(userCombinationOrders)) {
            // 判断是否有待支付订单
            List<StoreOrder> unPayOrders = userCombinationOrders.stream().filter(e -> !e.getPaid()).collect(Collectors.toList());
            if(unPayOrders.size() > 0) throw new CrmebException("您有拼团待支付订单，请支付后再购买");
            int payNum = userCombinationOrders.stream().mapToInt(StoreOrder::getTotalNum).sum();
            if (storeCombination.getNum() <= payNum){
                throw new CrmebException("您已达到该商品拼团活动上限");
            }
            if ((payNum + productNum) > storeCombination.getNum()) {
                throw new CrmebException("超过该商品拼团活动您的购买上限");
            }
        }

        MyRecord record = new MyRecord();
        record.set("productAttrValue", productAttrValue);
        return record;
    }

    /**
     * 积分兑换-预下单校验
     * @param detailRequest 请求参数
     * @param user 用户
     * @return OrderInfoDetailVo
     */
    private OrderInfoDetailVo validatePreOrderIntegal(PreOrderDetailRequest detailRequest, User user) {
        //得到积分兑换商品
        Integer integalId = detailRequest.getIntegalId();
        StoreIntegalShop storeIntegalShop = storeIntegalShopService.getByIdException(integalId);

        //得到属性值
        StoreProductAttrValue seckillAttrValue = storeProductAttrValueService.getByIdAndProductIdAndType(
                detailRequest.getAttrValueId(),
                integalId,
                Constants.PRODUCT_TYPE_INTEGAL);

        //验证
        commonValidateIntegal(storeIntegalShop, seckillAttrValue, user, detailRequest.getProductNum());

        //设置订单参数
        OrderInfoDetailVo detailVo = new OrderInfoDetailVo();
        detailVo.setProductId(storeIntegalShop.getProductId());
        detailVo.setProductName(storeIntegalShop.getTitle());
        detailVo.setAttrValueId(seckillAttrValue.getId());
        detailVo.setSku(seckillAttrValue.getSuk());
        detailVo.setPrice(seckillAttrValue.getPrice());
        detailVo.setPayNum(detailRequest.getProductNum());
        detailVo.setImage(StrUtil.isNotBlank(seckillAttrValue.getImage()) ? seckillAttrValue.getImage() : storeIntegalShop.getImage());
        detailVo.setVolume(seckillAttrValue.getVolume());
        detailVo.setWeight(seckillAttrValue.getWeight());
        detailVo.setTempId(storeIntegalShop.getTempId());
        return detailVo;
    }

    /**
     * 公共校验积分兑换商品
     * @param storeIntegalShop      积分兑换商品
     * @param seckillAttrValue      属性值
     * @param user  当前用户
     * @param productNum    购买数量
     * @return
     */
    private MyRecord commonValidateIntegal(StoreIntegalShop storeIntegalShop, StoreProductAttrValue seckillAttrValue, User user, Integer productNum) {
        //校验-主商品-是否存在
        StoreProduct product = storeProductService.getById(storeIntegalShop.getProductId());
        if (ObjectUtil.isNull(product) || product.getIsDel()) {
            throw new CrmebException("积分兑换主商品不存在");
        }

        //校验-主商品-库存
        if (product.getStock().equals(0) || productNum > product.getStock()) {
            throw new CrmebException("积分兑换主商品库存不足");
        }

        //校验-积分商品-库存
        if (storeIntegalShop.getStock().equals(0) || productNum > storeIntegalShop.getStock()) {
            throw new CrmebException("积分兑换商品库存不足");
        }

        //校验-积分商品-是否限购
        if(storeIntegalShop.getIsXiangou()){
            if(storeIntegalShop.getXiangouNum()<=0){
                throw new CrmebException("积分兑换商品限购数量不得小于1");
            }
        }

        //校验-属性值-非空
        if (ObjectUtil.isNull(seckillAttrValue)) {
            throw new CrmebException("积分兑换商品规格不存在");
        }

        //校验-属性值-规格库存
        if (seckillAttrValue.getStock() <= 0 ||  productNum > seckillAttrValue.getStock()) {
            throw new CrmebException("积分兑换商品规格库存不足");
        }

        //校验-主商品-属性-规格
        StoreProductAttrValue productAttrValue = storeProductAttrValueService.getByProductIdAndSkuAndType(
                storeIntegalShop.getProductId(),
                seckillAttrValue.getSuk(),
                Constants.PRODUCT_TYPE_NORMAL);
        if (ObjectUtil.isNull(productAttrValue)) {
            throw new CrmebException("积分兑换主商品规格不存在");
        }

        //校验-主商品-属性-规格-库存
        if (productAttrValue.getStock() <= 0 || productNum > productAttrValue.getStock()) {
            throw new CrmebException("积分兑换主商品规格库存不足");
        }

        //校验-用户积分余额或现金余额
        if(storeIntegalShop.getIntegal() > user.getIntegral().intValue()){
            throw new CrmebException("积分不足，无法兑换商品");
        }

        //得到返回
        MyRecord record = new MyRecord();
        record.set("productAttrValue", productAttrValue);
        return record;
    }

    /**
     * 再次下单预下单校验
     * @param detailRequest 请求参数
     * @return List<OrderInfoDetailVo>
     */
    private List<OrderInfoDetailVo> validatePreOrderAgain(PreOrderDetailRequest detailRequest) {
        List<OrderInfoDetailVo> detailVoList = CollUtil.newArrayList();
        if (StrUtil.isBlank(detailRequest.getOrderNo())) {
            throw new CrmebException("再次购买订单编号不能为空");
        }
        StoreOrder storeOrder = getByOrderIdException(detailRequest.getOrderNo());
        if (storeOrder.getRefundStatus() > 0 || storeOrder.getStatus() != 3) {
            throw new CrmebException("只有已完成状态订单才能再次购买");
        }
        if (storeOrder.getSeckillId() > 0 || storeOrder.getBargainId() > 0 || storeOrder.getCombinationId() > 0) {
            throw new CrmebException("活动商品订单不能再次购买");
        }
        if (storeOrder.getType().equals(1)) {
            throw new CrmebException("视频订单不能再次购买");
        }
        // 获取订单详情
        List<StoreOrderInfoVo> infoVoList = storeOrderInfoService.getVoListByOrderId(storeOrder.getId());
        if (CollUtil.isEmpty(infoVoList)) {
            throw new CrmebException("订单详情未找到");
        }
        infoVoList.forEach(e -> {
            OrderInfoDetailVo detailVo = e.getInfo();
            // 查询商品信息
            StoreProduct storeProduct = storeProductService.getById(detailVo.getProductId());
            if (ObjectUtil.isNull(storeProduct)) {
                throw new CrmebException("商品信息不存在，请刷新后重新选择");
            }
            if (storeProduct.getIsDel()) {
                throw new CrmebException("商品已删除，请刷新后重新选择");
            }
            if (!storeProduct.getIsShow()) {
                throw new CrmebException("商品已下架，请刷新后重新选择");
            }
            if (storeProduct.getStock() < detailVo.getPayNum()) {
                throw new CrmebException("商品库存不足，请刷新后重新选择");
            }
            // 查询商品规格属性值信息
            StoreProductAttrValue attrValue = attrValueService.getByIdAndProductIdAndType(detailVo.getAttrValueId(), detailVo.getProductId(), Constants.PRODUCT_TYPE_NORMAL);
            if (ObjectUtil.isNull(attrValue)) {
                throw new CrmebException("商品规格信息不存在，请刷新后重新选择");
            }
            if (attrValue.getStock() < detailVo.getPayNum()) {
                throw new CrmebException("商品规格库存不足，请刷新后重新选择");
            }
            OrderInfoDetailVo tempDetailVo = new OrderInfoDetailVo();
            tempDetailVo.setProductId(storeProduct.getId());
            tempDetailVo.setProductName(storeProduct.getStoreName());
            tempDetailVo.setAttrValueId(attrValue.getId());
            tempDetailVo.setSku(attrValue.getSuk());
            tempDetailVo.setPrice(attrValue.getPrice());
            tempDetailVo.setPayNum(detailVo.getPayNum());
            tempDetailVo.setImage(StrUtil.isNotBlank(attrValue.getImage()) ? attrValue.getImage() : storeProduct.getImage());
            tempDetailVo.setVolume(attrValue.getVolume());
            tempDetailVo.setWeight(attrValue.getWeight());
            tempDetailVo.setTempId(storeProduct.getTempId());
            tempDetailVo.setGiveIntegral(storeProduct.getGiveIntegral());
            tempDetailVo.setIsSub(storeProduct.getIsSub());
            detailVoList.add(tempDetailVo);
        });
        return detailVoList;
    }

    private StoreOrder getByOrderIdException(String orderId) {
        StoreOrder storeOrder = storeOrderService.getByOderId(orderId);
        if (ObjectUtil.isNull(storeOrder)) {
            throw new CrmebException("订单不存在");
        }
        if (storeOrder.getIsDel() || storeOrder.getIsSystemDel()) {
            throw new CrmebException("订单不存在");
        }
        return storeOrder;
    }

    /**
     * 计算订单运费
     */
    private void getFreightFee(OrderInfoVo orderInfoVo, UserAddress userAddress) {
        // 判断是否满额包邮 type=1按件数 2按重量 3按体积
        // 全场满额包邮开关
        String postageSwitchString = systemConfigService.getValueByKey(SysConfigConstants.STORE_FEE_POSTAGE_SWITCH);
        // 全场满额包邮金额
        String storeFreePostageString = systemConfigService.getValueByKey(SysConfigConstants.STORE_FEE_POSTAGE);
        BigDecimal storePostage = BigDecimal.ZERO;
        if (postageSwitchString.equals("true") &&
                (storeFreePostageString.equals("0") || orderInfoVo.getProTotalFee().compareTo(new BigDecimal(storeFreePostageString)) >= 0)) {
            storePostage = BigDecimal.ZERO;
        } else if(ObjectUtil.isNull(userAddress) || userAddress.getCityId() <= 0) {
            // 用户地址不存在，默认运费为0元
            storePostage = BigDecimal.ZERO;
        } else {
            // 有用户地址的情况下
            // 运费根据商品计算
            Map<Integer, MyRecord> proMap = CollUtil.newHashMap();
            orderInfoVo.getOrderDetailList().forEach(e -> {
                Integer proId = e.getProductId();
                if (proMap.containsKey(proId)) {
                    MyRecord record = proMap.get(proId);
                    record.set("totalPrice", record.getBigDecimal("totalPrice").add(e.getPrice().multiply(BigDecimal.valueOf(e.getPayNum()))));
                    record.set("totalNum", record.getInt("totalNum") + e.getPayNum());
                    BigDecimal weight = e.getWeight().multiply(BigDecimal.valueOf(e.getPayNum()));
                    record.set("weight", record.getBigDecimal("weight").add(weight));
                    BigDecimal volume = e.getVolume().multiply(BigDecimal.valueOf(e.getPayNum()));
                    record.set("volume", record.getBigDecimal("volume").add(volume));
                } else {
                    MyRecord record = new MyRecord();
                    record.set("totalPrice", e.getPrice().multiply(BigDecimal.valueOf(e.getPayNum())));
                    record.set("totalNum", e.getPayNum());
                    record.set("tempId", e.getTempId());
                    record.set("proId", proId);
                    BigDecimal weight = e.getWeight().multiply(BigDecimal.valueOf(e.getPayNum()));
                    record.set("weight", weight);
                    BigDecimal volume = e.getVolume().multiply(BigDecimal.valueOf(e.getPayNum()));
                    record.set("volume", volume);
                    proMap.put(proId, record);
                }
            });

            // 指定包邮（单品运费模板）> 指定区域配送（单品运费模板）
            int cityId = userAddress.getCityId();
            for (Map.Entry<Integer, MyRecord> m : proMap.entrySet()) {
                MyRecord value = m.getValue();
                Integer tempId = value.getInt("tempId");
                ShippingTemplates shippingTemplate = shippingTemplatesService.getById(tempId);
                if (shippingTemplate.getAppoint()) {// 指定包邮
                    // 判断是否在指定包邮区域内
                    // 必须满足件数 + 金额 才能包邮
                    ShippingTemplatesFree shippingTemplatesFree = shippingTemplatesFreeService.getByTempIdAndCityId(tempId, cityId);
                    if (ObjectUtil.isNotNull(shippingTemplatesFree)) { // 在包邮区域内
                        BigDecimal freeNum = shippingTemplatesFree.getNumber();
                        BigDecimal multiply = value.getBigDecimal("totalPrice");
                        if (new BigDecimal(value.getInt("totalNum")).compareTo(freeNum) >= 0 && multiply.compareTo(shippingTemplatesFree.getPrice()) >= 0) {
                            // 满足件数 + 金额 = 包邮
                            continue;
                        }
                    }
                }

                // 不满足指定包邮条件，走指定区域配送
                ShippingTemplatesRegion shippingTemplatesRegion = shippingTemplatesRegionService.getByTempIdAndCityId(tempId, cityId);
                if (ObjectUtil.isNull(shippingTemplatesRegion)) {
                    throw new CrmebException("计算运费时，未找到全国运费配置");
                }

                // 判断计费方式：件数、重量、体积
                switch (shippingTemplate.getType()) {
                    case 1: // 件数
                        // 判断件数是否超过首件
                        Integer num = value.getInt("totalNum");
                        if (num <= shippingTemplatesRegion.getFirst().intValue()) {
                            storePostage = storePostage.add(shippingTemplatesRegion.getFirstPrice());
                        } else {// 超过首件的需要计算续件
                            int renewalNum = num - shippingTemplatesRegion.getFirst().intValue();
                            // 剩余件数/续件 = 需要计算的续件费用的次数
                            BigDecimal divide = new BigDecimal(renewalNum).divide(shippingTemplatesRegion.getRenewal(), 0, BigDecimal.ROUND_UP);
                            BigDecimal renewalPrice = shippingTemplatesRegion.getRenewalPrice().multiply(divide);
                            storePostage = storePostage.add(shippingTemplatesRegion.getFirstPrice()).add(renewalPrice);
                        }
                        break;
                    case 2: // 重量
                        BigDecimal weight = value.getBigDecimal("weight");
                        if (weight.compareTo(shippingTemplatesRegion.getFirst()) <= 0) {
                            storePostage = storePostage.add(shippingTemplatesRegion.getFirstPrice());
                        } else {// 超过首件的需要计算续件
                            BigDecimal renewalNum = weight.subtract(shippingTemplatesRegion.getFirst());
                            // 剩余件数/续件 = 需要计算的续件费用的次数
                            BigDecimal divide = renewalNum.divide(shippingTemplatesRegion.getRenewal(), 0, BigDecimal.ROUND_UP);
                            BigDecimal renewalPrice = shippingTemplatesRegion.getRenewalPrice().multiply(divide);
                            storePostage = storePostage.add(shippingTemplatesRegion.getFirstPrice()).add(renewalPrice);
                        }
                        break;
                    case 3: // 体积
                        BigDecimal volume = value.getBigDecimal("volume");
                        if (volume.compareTo(shippingTemplatesRegion.getFirst()) <= 0) {
                            storePostage = storePostage.add(shippingTemplatesRegion.getFirstPrice());
                        } else {// 超过首件的需要计算续件
                            BigDecimal renewalNum = volume.subtract(shippingTemplatesRegion.getFirst());
                            // 剩余件数/续件 = 需要计算的续件费用的次数
                            BigDecimal divide = renewalNum.divide(shippingTemplatesRegion.getRenewal(), 0, BigDecimal.ROUND_UP);
                            BigDecimal renewalPrice = shippingTemplatesRegion.getRenewalPrice().multiply(divide);
                            storePostage = storePostage.add(shippingTemplatesRegion.getFirstPrice()).add(renewalPrice);
                        }
                        break;
                }
            }
        }
        orderInfoVo.setFreightFee(storePostage);
    }

    /**
     * 计算订单各种价格
     * @param request
     * @param orderInfoVo
     * @param user
     * @return
     */
    private ComputedOrderPriceResponse computedPrice(OrderComputedPriceRequest request, OrderInfoVo orderInfoVo, User user) {
        // 实例化-订单价格响应对象
        ComputedOrderPriceResponse priceResponse = new ComputedOrderPriceResponse();

        // 计算运费
        if (request.getShippingType().equals(2)) {// 到店自提，不计算运费
            priceResponse.setFreightFee(BigDecimal.ZERO);
        } else if (ObjectUtil.isNull(request.getAddressId()) || request.getAddressId() <= 0) {
            // 快递配送，无地址
            priceResponse.setFreightFee(BigDecimal.ZERO);
        } else {// 快递配送，有地址
            UserAddress userAddress = userAddressService.getById(request.getAddressId());
            if (ObjectUtil.isNull(userAddress)) {
                priceResponse.setFreightFee(BigDecimal.ZERO);
            } else {
                getFreightFee(orderInfoVo, userAddress);
                priceResponse.setFreightFee(orderInfoVo.getFreightFee());
            }
        }

        // 计算优惠券金额
        if (ObjectUtil.isNull(request.getCouponId()) || request.getCouponId() <= 0) {
            priceResponse.setCouponFee(BigDecimal.ZERO);
        } else if (orderInfoVo.getSeckillId() > 0 || orderInfoVo.getBargainId() > 0 || orderInfoVo.getCombinationId() > 0) {
            throw new CrmebException("营销活动商品无法使用优惠券");
        } else if (orderInfoVo.getIsVideo()) {
            throw new CrmebException("视频号商品无法使用优惠券");
        } else {
            // 判断优惠券是否可以使用
            StoreCouponUser storeCouponUser = storeCouponUserService.getById(request.getCouponId());
            if (ObjectUtil.isNull(storeCouponUser) || !storeCouponUser.getUid().equals(user.getUid())) {
                throw new CrmebException("优惠券领取记录不存在！");
            }
            if(storeCouponUser.getStatus() == 1){
                throw new CrmebException("此优惠券已使用！");
            }

            if(storeCouponUser.getStatus() == 2){
                throw new CrmebException("此优惠券已失效！");
            }
            //判断是否在使用时间内
            Date date = DateUtil.nowDateTime();
            if(storeCouponUser.getStartTime().compareTo(date) > 0){
                throw new CrmebException("此优惠券还未到达使用时间范围之内！");
            }
            if(date.compareTo(storeCouponUser.getEndTime()) > 0){
                throw new CrmebException("此优惠券已经失效了");
            }
            if(storeCouponUser.getMinPrice().compareTo(orderInfoVo.getProTotalFee()) > 0){
                throw new CrmebException("总金额小于优惠券最小使用金额");
            }
            //检测优惠券信息
            if(storeCouponUser.getUseType() > 1){
                List<Integer> productIdList = orderInfoVo.getOrderDetailList().stream().map(OrderInfoDetailVo::getProductId).collect(Collectors.toList());
                if(productIdList.size() < 1){
                    throw new CrmebException("没有找到商品");
                }

                //设置优惠券所提供的集合
                List<Integer> primaryKeyIdList = CrmebUtil.stringToArray(storeCouponUser.getPrimaryKey());

                //取两个集合的交集，如果是false则证明没有相同的值
                //oldList.retainAll(newList)返回值代表oldList是否保持原样，如果old和new完全相同，那old保持原样并返回false。
                //交集：listA.retainAll(listB) ——listA内容变为listA和listB都存在的对象；listB不变
                if(storeCouponUser.getUseType() == 2){
                    primaryKeyIdList.retainAll(productIdList);
                    if (CollUtil.isEmpty(primaryKeyIdList)) {
                        throw new CrmebException("此优惠券为商品券，请购买相关商品之后再使用！");
                    }
                }

                if(storeCouponUser.getUseType() == 3){
                    //拿出需要使用优惠券的商品分类集合
                    List<Integer> categoryIdList = storeProductService.getSecondaryCategoryByProductId(StringUtils.join(productIdList, ","));

                    primaryKeyIdList.retainAll(categoryIdList);
                    if (CollUtil.isEmpty(primaryKeyIdList)) {
                        throw new CrmebException("此优惠券为分类券，请购买相关分类下的商品之后再使用！");
                    }
                }
            }
            if (orderInfoVo.getProTotalFee().compareTo(storeCouponUser.getMoney()) <= 0) {
                priceResponse.setCouponFee(orderInfoVo.getProTotalFee());
                priceResponse.setDeductionPrice(BigDecimal.ZERO);
                priceResponse.setSurplusIntegral(user.getIntegral().intValue());
                priceResponse.setPayFee(priceResponse.getFreightFee());
                priceResponse.setUsedIntegral(0);
                priceResponse.setUseIntegral(false);
                priceResponse.setProTotalFee(orderInfoVo.getProTotalFee());
                return priceResponse;
            } else {
                priceResponse.setCouponFee(storeCouponUser.getMoney());
            }
        }

        // 积分部分
        BigDecimal payPrice = orderInfoVo.getProTotalFee().add(priceResponse.getFreightFee()).subtract(priceResponse.getCouponFee());
        priceResponse.setUseIntegral(request.getUseIntegral());
        priceResponse.setProTotalFee(orderInfoVo.getProTotalFee());
        priceResponse.setDeductionPrice(BigDecimal.ZERO);
        priceResponse.setSurplusIntegral(user.getIntegral().intValue());
        priceResponse.setPayFee(payPrice);
        priceResponse.setUsedIntegral(0);

        //返回-订单价格响应对象
        return priceResponse;
    }
}
