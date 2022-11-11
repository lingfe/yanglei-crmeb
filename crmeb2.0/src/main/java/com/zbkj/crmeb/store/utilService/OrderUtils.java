package com.zbkj.crmeb.store.utilService;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.common.MyRecord;
import com.constants.Constants;
import com.constants.PayConstants;
import com.constants.SysConfigConstants;
import com.exception.CrmebException;
import com.utils.DateUtil;
import com.utils.RedisUtil;
import com.zbkj.crmeb.bargain.model.StoreBargain;
import com.zbkj.crmeb.bargain.service.StoreBargainService;
import com.zbkj.crmeb.bargain.service.StoreBargainUserHelpService;
import com.zbkj.crmeb.combination.model.StoreCombination;
import com.zbkj.crmeb.combination.service.StoreCombinationService;
import com.zbkj.crmeb.combination.service.StorePinkService;
import com.zbkj.crmeb.seckill.model.StoreSeckill;
import com.zbkj.crmeb.seckill.model.StoreSeckillManger;
import com.zbkj.crmeb.seckill.service.StoreSeckillMangerService;
import com.zbkj.crmeb.seckill.service.StoreSeckillService;
import com.zbkj.crmeb.store.model.StoreCart;
import com.zbkj.crmeb.store.model.StoreOrder;
import com.zbkj.crmeb.store.model.StoreProductAttrValue;
import com.zbkj.crmeb.store.service.StoreOrderInfoService;
import com.zbkj.crmeb.store.service.StoreOrderService;
import com.zbkj.crmeb.store.service.StoreProductAttrValueService;
import com.zbkj.crmeb.store.vo.StoreOrderInfoOldVo;
import com.zbkj.crmeb.system.service.SystemConfigService;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.wechat.service.impl.WechatSendMessageForMinService;
import com.zbkj.crmeb.wechat.vo.WechatSendMessageForDistrbution;
import com.zbkj.crmeb.wechat.vo.WechatSendMessageForPackage;
import com.zbkj.crmeb.wechat.vo.WechatSendMessageForPaySuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 订单工具类
 * @author: 零风
 * @CreateDate: 2022/2/7 9:53
 */
@Service
public class OrderUtils {

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private StoreOrderInfoService storeOrderInfoService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private WechatSendMessageForMinService wechatSendMessageForMinService;

    @Autowired
    private StoreProductAttrValueService storeProductAttrValueService;

    @Autowired
    private StoreSeckillService storeSeckillService;

    @Autowired
    private StoreSeckillMangerService storeSeckillMangerService;

    @Autowired
    private StoreBargainService storeBargainService;

    @Autowired
    private StoreCombinationService storeCombinationService;

    @Autowired
    private StoreBargainUserHelpService storeBargainUserHelpService;

    @Autowired
    private StorePinkService storePinkService;


    /**
     * 支付渠道-检测是否可用
     * @param payChannel 支付渠道
     */
    public static boolean checkPayChannel(String payChannel) {
        switch (payChannel){
            case PayConstants.PAY_CHANNEL_WE_CHAT_H5:
            case PayConstants.PAY_CHANNEL_WE_CHAT_PROGRAM:
            case PayConstants.PAY_CHANNEL_WE_CHAT_PUBLIC:
            case PayConstants.PAY_CHANNEL_WE_CHAT_APP_IOS:
            case PayConstants.PAY_CHANNEL_WE_CHAT_APP_ANDROID:
                return Boolean.TRUE;
            default:
                return Boolean.FALSE;
        }
    }

    /**
     * 支付渠道-微信-转数字
     * -默认返回余额支付
     * @param isChannel 支付渠道-字符串
     * @return
     */
    public int getIsChanneLWeiXin(String isChannel) {
        switch (isChannel){
            case PayConstants.PAY_CHANNEL_WE_CHAT_PUBLIC:// 公众号
                return PayConstants.ORDER_PAY_CHANNEL_PUBLIC;
            case PayConstants.PAY_CHANNEL_WE_CHAT_PROGRAM:// 小程序
                return PayConstants.ORDER_PAY_CHANNEL_PROGRAM;
            case PayConstants.PAY_CHANNEL_WE_CHAT_H5:// H5
                return PayConstants.ORDER_PAY_CHANNEL_H5;
            case PayConstants.PAY_CHANNEL_WE_CHAT_APP_IOS:// app ios
                return PayConstants.ORDER_PAY_CHANNEL_APP_IOS;
            case PayConstants.PAY_CHANNEL_WE_CHAT_APP_ANDROID:// app android
                return PayConstants.ORDER_PAY_CHANNEL_APP_ANDROID;
            default:
                return PayConstants.ORDER_PAY_CHANNEL_YUE;//余额
        }
    }

    /**
     * 封装现有支付方式
     * @return 支付方式集合
     */
    public List<String> getPayType(){
        List<String> payType = new ArrayList<>();
        payType.add(PayConstants.PAY_TYPE_WE_CHAT);
        payType.add(PayConstants.PAY_TYPE_YUE);
        payType.add(PayConstants.PAY_TYPE_OFFLINE);
        payType.add(PayConstants.PAY_TYPE_ALI_PAY);
        return payType;
    }

    /**
     * 检查支付类型
     * @param payType 支付类型标识
     * @return 是否支持
     */
    public Boolean checkPayType(String payType){
        boolean result = false;
        payType = payType.toLowerCase();
        switch (payType){
            case PayConstants.PAY_TYPE_WE_CHAT:
                result = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_PAY_WEIXIN_OPEN).equals("1");
                break;
            case PayConstants.PAY_TYPE_YUE:
                result = (systemConfigService.getValueByKey(SysConfigConstants.CONFIG_YUE_PAY_STATUS).equals("1"));
                break;
            case PayConstants.PAY_TYPE_ALI_PAY:
                //result = (systemConfigService.getValueByKey(SysConfigConstants.CONFIG_YUE_PAY_STATUS).equals("1"));
                result = true;
                break;
            case PayConstants.PAY_TYPE_INTEGRAL:
                result = Boolean.TRUE;
                break;
        }
        return result;
    }

    /**
     * 订单数据缓存进redis订单通用
     * @param user 订单缓存key
     * @param list 待缓存的订单数据
     */
    public String setCacheOrderData(User user,Object list){
        Long cacheKey = DateUtil.getTime()+user.getUid();
        String key = Constants.ORDER_CACHE_PER + cacheKey;
        redisUtil.set(key,JSONObject.toJSONString(list), Constants.ORDER_CASH_CONFIRM, TimeUnit.MINUTES);
        return key;
    }

    /**
     * h5 订单查询 where status 封装
     * @param queryWrapper 查询条件
     * @param status 状态
     */
    public void statusApiByWhere(LambdaQueryWrapper<StoreOrder> queryWrapper, Integer status){
        switch (status){
            case Constants.ORDER_STATUS_H5_UNPAID: // 未支付
                queryWrapper.eq(StoreOrder::getPaid, false);
                queryWrapper.eq(StoreOrder::getStatus, 0);
                queryWrapper.eq(StoreOrder::getRefundStatus, 0);
                //queryWrapper.eq(StoreOrder::getType, 0);
                break;
            case Constants.ORDER_STATUS_H5_NOT_SHIPPED: // 待发货
                queryWrapper.eq(StoreOrder::getPaid, true);
                queryWrapper.eq(StoreOrder::getStatus, 0);
                queryWrapper.eq(StoreOrder::getRefundStatus, 0);
//                queryWrapper.eq(StoreOrder::getShippingType, 1);
                break;
            case Constants.ORDER_STATUS_H5_SPIKE: // 待收货
                queryWrapper.eq(StoreOrder::getPaid, true);
                queryWrapper.eq(StoreOrder::getStatus, 1);
                queryWrapper.eq(StoreOrder::getRefundStatus, 0);
                break;
            case Constants.ORDER_STATUS_H5_JUDGE: //  已支付 已收货 待评价
                queryWrapper.eq(StoreOrder::getPaid, true);
                queryWrapper.eq(StoreOrder::getStatus, 2);
                queryWrapper.eq(StoreOrder::getRefundStatus, 0);
                break;
            case Constants.ORDER_STATUS_H5_COMPLETE: // 已完成
                queryWrapper.eq(StoreOrder::getPaid, true);
                queryWrapper.eq(StoreOrder::getStatus, 3);
                queryWrapper.eq(StoreOrder::getRefundStatus, 0);
                break;
            case Constants.ORDER_STATUS_H5_REFUNDING: // 退款中
                queryWrapper.eq(StoreOrder::getPaid, true);
                queryWrapper.in(StoreOrder::getRefundStatus, 1, 3);
                break;
            case Constants.ORDER_STATUS_H5_REFUNDED: // 已退款
                queryWrapper.eq(StoreOrder::getPaid, true);
                queryWrapper.eq(StoreOrder::getRefundStatus, 2);
                break;
            case Constants.ORDER_STATUS_H5_REFUND: // 包含已退款和退款中
                queryWrapper.eq(StoreOrder::getPaid, true);
                queryWrapper.in(StoreOrder::getRefundStatus, 1,2,3);
                break;
        }
        queryWrapper.eq(StoreOrder::getIsDel, false);
        queryWrapper.eq(StoreOrder::getIsSystemDel, false);
    }

    /**
     * 根据订单id获取订单中商品和名称和购买数量字符串
     * @param orderId   订单id
     * @return          商品名称*购买数量
     */
    public String getStoreNameAndCarNumString(int orderId){
        List<StoreOrderInfoOldVo> currentOrderInfo = storeOrderInfoService.getOrderListByOrderId(orderId);
        if(currentOrderInfo.size() > 0) {
            StringBuilder sbOrderProduct = new StringBuilder();
            for (StoreOrderInfoOldVo storeOrderInfoVo : currentOrderInfo) {
                sbOrderProduct.append(storeOrderInfoVo.getInfo().getProductName() + "*"
                        + storeOrderInfoVo.getInfo().getPayNum());
            }
            return sbOrderProduct.toString();
        }
        return null;
    }

    /**
     * 根据订单对象获取订单配送中文
     * @param order 订单对象
     * @return      配送方式中文字符串
     */
    public String getPayTypeStrByOrder(StoreOrder order){
        return order.getShippingType() == 1 ? "快递":"自提";
    }

    /** 这里的方法完全是为了订单中调用而封装 订单支付成功有余额支付和微信支付成功回调中
     * 微信小程序发送付款成功订阅消息
     * @param paySuccess    付款成功订阅消息
     * @param userId        用户Id
     */
    public void sendWeiChatMiniMessageForPaySuccess(WechatSendMessageForPaySuccess paySuccess, int userId){
        wechatSendMessageForMinService.sendPaySuccessMessage(paySuccess,userId);
    }

    /**
     * 微信小程序发送发货快递通知订阅消息
     * @param storeOrder 发送快递
     * @param userId    接收消息的用户id
     */
    public void sendWeiChatMiniMessageForPackageExpress(StoreOrder storeOrder, int userId){
        WechatSendMessageForPackage toPackage = new WechatSendMessageForPackage(
                storeOrderService.getOrderPayTypeStr(storeOrder.getPayType()),storeOrder.getOrderId(),storeOrder.getUserAddress(),
                storeOrder.getUserPhone(),storeOrder.getRealName()
        );
        wechatSendMessageForMinService.sendRePackageMessage(toPackage,userId);
    }

    /**
     * 微信小程序发送 配送通知订阅消息
     * @param storeOrder    配送订单信息
     * @param userId        接收消息的用户id
     */
    public void senWeiChatMiniMessageForDeliver(StoreOrder storeOrder, int userId){
        WechatSendMessageForDistrbution distrbution = new WechatSendMessageForDistrbution(
                storeOrder.getId()+"",storeOrder.getDeliveryName(),storeOrder.getDeliveryId()+"",
                "商家已经开始配送", "暂无",getStoreNameAndCarNumString(storeOrder.getId()),"暂无"
        );
        wechatSendMessageForMinService.sendDistrbutionMessage(distrbution,userId);
    }

    /**
     * 下单前秒杀验证
     * @param storeCartPram 秒杀参数
     * @param currentUser   当前购买人
     */
    public StoreSeckill validSecKill(StoreCart storeCartPram, User currentUser) {
        // 判断秒杀商品是否有效
        StoreSeckill storeProductPram = new StoreSeckill();
        storeProductPram.setId(storeCartPram.getSeckillId());
        storeProductPram.setIsDel(false);
        storeProductPram.setIsShow(true);
        List<StoreSeckill> existSecKills = storeSeckillService.getByEntity(storeProductPram);
        if(null == existSecKills) throw new CrmebException("该商品已下架或者删除");

        // 判断秒杀时间段
        StoreSeckill existSecKill = existSecKills.get(0);
        StoreSeckillManger seckillManger = storeSeckillMangerService.getById(existSecKill.getTimeId());
        // 判断日期是否过期
        String stopTimeStr = DateUtil.dateToStr(existSecKill.getStopTime(),Constants.DATE_FORMAT_DATE);
        Date stopDate = DateUtil.strToDate( stopTimeStr  + " " + seckillManger.getEndTime() +":00:00", Constants.DATE_FORMAT);
        if(DateUtil.getTwoDateDays(DateUtil.nowDateTime(),stopDate) < 0){
            throw new CrmebException("秒杀商品已过期");
        }
        // 判断是否在秒杀时段内（小时）,秒杀开始时间 <= 当前时间 <= 秒杀结束时间
        int hour = cn.hutool.core.date.DateUtil.date().getField(Calendar.HOUR_OF_DAY);// 现在的小时
        if (seckillManger.getStartTime() > hour || seckillManger.getEndTime() < hour) {
            throw new CrmebException("秒杀商品已过期");
        }


        // 判断秒杀商品库存和秒杀限量
        StoreProductAttrValue spavPram = new StoreProductAttrValue()
                .setId(Integer.valueOf(storeCartPram.getProductAttrUnique()))
                .setType(Constants.PRODUCT_TYPE_SECKILL);
        List<StoreProductAttrValue> currentSecKillAttrValues = storeProductAttrValueService.getByEntity(spavPram);
        if(null == currentSecKillAttrValues || currentSecKillAttrValues.size() == 0){
            throw new CrmebException("未找到该商品信息");
        }
        return existSecKill;
    }

    /**
     * 下单前砍价验证
     * @param storeCartPram 砍价参数
     * @param currentUser   当前购买人
     * @return
     */
    public MyRecord validBargain(StoreCart storeCartPram, User currentUser) {
        // 判断砍价商品是否有效
        StoreBargain existBargain = storeBargainService.getByIdException(storeCartPram.getBargainId());
        // 判断购买数量
        if (storeCartPram.getCartNum() > existBargain.getQuota()) {
            throw new CrmebException("砍价商品库存不足");
        }
        if (existBargain.getQuota() <= 0 || existBargain.getStock() <= 0) {// 销量等于限量
            throw new CrmebException("当前拼团商品已售罄");
        }

        // 判断砍价活动时间段
        long timeMillis = System.currentTimeMillis();
        if (timeMillis < existBargain.getStartTime()) {
            throw new CrmebException("砍价商品活动未开始");
        }
        if (timeMillis > existBargain.getStopTime()) {
            throw new CrmebException("砍价商品已过期");
        }

        // 判断用户是否砍价完成
        BigDecimal surplusPrice = storeBargainUserHelpService.getSurplusPrice(storeCartPram.getBargainId(), currentUser.getUid());
        if (surplusPrice.compareTo(BigDecimal.ZERO) > 0) {
            throw new CrmebException("请先完成砍价");
        }

        // 判断砍价商品库存和砍价限量
        StoreProductAttrValue storeProductAttrValue = storeProductAttrValueService.getByIdAndProductIdAndType(Integer.valueOf(storeCartPram.getProductAttrUnique()), storeCartPram.getBargainId(), Constants.PRODUCT_TYPE_BARGAIN);
        if(ObjectUtil.isNull(storeProductAttrValue)){
            throw new CrmebException("未找到该商品规格信息");
        }
        if (storeProductAttrValue.getQuota() <= 0 || storeProductAttrValue.getStock() <= 0){// sku销量等于限量
            throw new CrmebException("当前砍价商品已售罄");
        }

//        // 参与活动次数 -根据用户和秒杀信息查询当天订单判断订单数量
//        StoreOrder soPram = new StoreOrder().setUid(currentUser.getUid()).setBargainId(storeCartPram.getBargainId());
//        List<StoreOrder> userCurrentBargainOrders = storeOrderService.getUserCurrentBargainOrders(soPram);
//
//        // 判断是否有待支付订单
//        List<StoreOrder> unPayOrders = userCurrentBargainOrders.stream().filter(e -> !e.getPaid()).collect(Collectors.toList());
//        if(unPayOrders.size() > 0) throw new CrmebException("您有砍价待支付订单，请支付后再购买");
//
//        // 判断是否达到上限
//        List<StoreOrder> noRefundOrders = userCurrentBargainOrders.stream().filter(i -> i.getRefundStatus() != 2).collect(Collectors.toList());
//        if(CollUtil.isNotEmpty(userCurrentBargainOrders) && noRefundOrders.size() >= existBargain.getNum()){
//            throw new CrmebException("您已经达到当前砍价活动上限");
//        }
        MyRecord record = new MyRecord();
        record.set("product", existBargain);
        record.set("attrInfo", storeProductAttrValue);
        return record;
    }

    /**
     * 下单前拼团验证
     * @param storeCartPram 拼团参数
     * @param currentUser   当前购买人
     * @return
     */
    public MyRecord validCombination(StoreCart storeCartPram, User currentUser) {
        // 判断拼团商品是否有效
        StoreCombination existCombination = storeCombinationService.getByIdException(storeCartPram.getCombinationId());

        // 判断拼团时间段
        long timeMillis = System.currentTimeMillis();
        if (timeMillis < existCombination.getStartTime()) {
            throw new CrmebException("拼团商品活动未开始");
        }
        if (timeMillis >= existCombination.getStopTime()) {
            throw new CrmebException("拼团商品已过期");
        }
        // 判断购买数量
        if (storeCartPram.getCartNum() > existCombination.getOnceNum()) {
            throw new CrmebException("购买数量超过单次拼团购买上限");
        }
        if (storeCartPram.getCartNum() > existCombination.getQuota()) {
            throw new CrmebException("拼团商品库存不足");
        }
        if (existCombination.getQuota() <= 0 || existCombination.getStock() <= 0) {// 销量等于限量
            throw new CrmebException("当前拼团商品已售罄");
        }

        // 如果时参团，判断团队是否已满员
        if (ObjectUtil.isNotNull(storeCartPram.getPinkId()) && storeCartPram.getPinkId() > 0) {
            Integer countPeople = storePinkService.getCountByKid(storeCartPram.getPinkId());
            if (countPeople >= existCombination.getPeople()) {
                throw new CrmebException("当前拼团人数已满");
            }
        }

        // 判断拼团商品规格库存和拼团限量
        // 判断商品对应属性是否有效
        StoreProductAttrValue storeProductAttrValue = storeProductAttrValueService.getByIdAndProductIdAndType(Integer.valueOf(storeCartPram.getProductAttrUnique()), storeCartPram.getCombinationId(), Constants.PRODUCT_TYPE_PINGTUAN);
        if(ObjectUtil.isNull(storeProductAttrValue)){
            throw new CrmebException("未找到该商品信息");
        }
        if (storeProductAttrValue.getQuota() <= 0 || storeProductAttrValue.getStock() <= 0){// sku销量等于限量
            throw new CrmebException("当前拼团商品已售罄");
        }
        if (storeCartPram.getCartNum() > storeProductAttrValue.getQuota()) {
            throw new CrmebException("数量超过拼团商品库存上限");
        }

        // 用户参与活动的次数
        StoreOrder soPram = new StoreOrder().setUid(currentUser.getUid()).setCombinationId(storeCartPram.getCombinationId());
        List<StoreOrder> userCombinationOrders = storeOrderService.getByEntity(soPram);

        if (CollUtil.isNotEmpty(userCombinationOrders)) {
            // 判断是否有待支付订单
            List<StoreOrder> unPayOrders = userCombinationOrders.stream().filter(e -> !e.getPaid()).collect(Collectors.toList());
            if(unPayOrders.size() > 0) throw new CrmebException("您有拼团待支付订单，请支付后再购买");

            List<StoreOrder> noRefundOrders = userCombinationOrders.stream().filter(i -> i.getRefundStatus() != 2).collect(Collectors.toList());
            int payNum = userCombinationOrders.stream().mapToInt(order -> {
                if (order.getRefundStatus() != 2) {
                    return order.getTotalNum();
                }
                return 0;
            }).sum();
            if (CollUtil.isNotEmpty(noRefundOrders) && existCombination.getNum() <= payNum){
                throw new CrmebException("您已达到该商品拼团活动上限");
            }
            if ((payNum + storeCartPram.getCartNum()) > existCombination.getNum()) {
                throw new CrmebException("超过该商品拼团活动您的购买上限");
            }
        }

        MyRecord record = new MyRecord();
        record.set("product", existCombination);
        record.set("attrInfo", storeProductAttrValue);
        return record;
    }
}
