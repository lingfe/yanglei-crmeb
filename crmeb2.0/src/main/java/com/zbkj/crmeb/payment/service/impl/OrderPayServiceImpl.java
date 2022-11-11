package com.zbkj.crmeb.payment.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import com.common.MyRecord;
import com.constants.*;
import com.exception.CrmebException;
import com.utils.DateUtil;
import com.utils.RedisUtil;
import com.zbkj.crmeb.bargain.service.StoreBargainService;
import com.zbkj.crmeb.bargain.service.StoreBargainUserService;
import com.zbkj.crmeb.combination.model.StoreCombination;
import com.zbkj.crmeb.combination.model.StorePink;
import com.zbkj.crmeb.combination.service.StoreCombinationService;
import com.zbkj.crmeb.combination.service.StorePinkService;
import com.zbkj.crmeb.finance.model.InvoiceRecord;
import com.zbkj.crmeb.finance.model.UserExtract;
import com.zbkj.crmeb.finance.service.InvoiceRecordService;
import com.zbkj.crmeb.finance.service.UserExtractService;
import com.zbkj.crmeb.front.request.OrderPayRequest;
import com.zbkj.crmeb.front.response.OrderPayResultResponse;
import com.zbkj.crmeb.front.vo.OrderInfoDetailVo;
import com.zbkj.crmeb.front.vo.WxPayJsResultVo;
import com.zbkj.crmeb.integal.model.PublicIntegalRecord;
import com.zbkj.crmeb.integal.service.PublicIntegalRecordService;
import com.zbkj.crmeb.marketing.model.StoreCouponUser;
import com.zbkj.crmeb.marketing.service.StoreCouponService;
import com.zbkj.crmeb.marketing.service.StoreCouponUserService;
import com.zbkj.crmeb.payment.service.OrderPayService;
import com.zbkj.crmeb.payment.wechat.WeChatPayService;
import com.zbkj.crmeb.regionalAgency.model.RegionalAgency;
import com.zbkj.crmeb.regionalAgency.service.RegionalAgencyService;
import com.zbkj.crmeb.regionalAgency.service.RegionalUserService;
import com.zbkj.crmeb.retailer.service.RetailerService;
import com.zbkj.crmeb.sms.service.SmsService;
import com.zbkj.crmeb.store.model.StoreOrder;
import com.zbkj.crmeb.store.model.StoreProduct;
import com.zbkj.crmeb.store.model.StoreProductAttrValue;
import com.zbkj.crmeb.store.model.StoreProductCoupon;
import com.zbkj.crmeb.store.service.*;
import com.zbkj.crmeb.store.utilService.OrderUtils;
import com.zbkj.crmeb.store.vo.StoreOrderInfoOldVo;
import com.zbkj.crmeb.system.model.SystemAdmin;
import com.zbkj.crmeb.system.service.SystemAdminService;
import com.zbkj.crmeb.system.service.SystemConfigService;
import com.zbkj.crmeb.user.model.*;
import com.zbkj.crmeb.user.service.*;
import com.zbkj.crmeb.wechat.service.TemplateMessageService;
import com.zbkj.crmeb.wechat.service.WeChatService;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static java.math.BigDecimal.ZERO;

/**
 * OrderPayService-实现类
 * @author: 零风
 * @CreateDate: 2022/2/23 15:29
 */
@Data
@Service
public class OrderPayServiceImpl implements OrderPayService {

    private static final Logger logger = LoggerFactory.getLogger(OrderPayServiceImpl.class);

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private StoreOrderStatusService storeOrderStatusService;

    @Autowired
    private StoreOrderInfoService storeOrderInfoService;

    @Lazy
    @Autowired
    private WeChatPayService weChatPayService;

    @Autowired
    private TemplateMessageService templateMessageService;

    @Autowired
    private UserBillService userBillService;

    @Lazy
    @Autowired
    private SmsService smsService;

    @Autowired
    private UserService userService;

    @Autowired
    private StoreProductCouponService storeProductCouponService;

    @Autowired
    private StoreCouponUserService storeCouponUserService;

    @Autowired
    private WeChatService weChatService;

    @Autowired
    private OrderUtils orderUtils;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private StoreProductService storeProductService;

    @Autowired
    private UserLevelService userLevelService;

    @Autowired
    private StoreBargainService storeBargainService;

    @Autowired
    private StoreBargainUserService storeBargainUserService;

    @Autowired
    private StoreCombinationService storeCombinationService;

    @Autowired
    private StorePinkService storePinkService;

    @Autowired
    private UserBrokerageRecordService userBrokerageRecordService;

    @Autowired
    private StoreCouponService storeCouponService;

    @Autowired
    private SystemAdminService systemAdminService;

    @Autowired
    private UserTokenService userTokenService;

    @Autowired
    private UserIntegralRecordService userIntegralRecordService;

    @Autowired
    private StoreProductAttrValueService storeProductAttrValueService;

    @Autowired
    private UserIntegralExchangeRecordService userIntegralExchangeRecordService;

    @Autowired
    private PublicIntegalRecordService publicIntegalRecordService;

    @Autowired
    private RegionalAgencyService regionalAgencyService;

    @Autowired
    private RetailerService retailerService;

    @Autowired
    private RegionalUserService regionalUserService;

    @Autowired
    private InvoiceRecordService invoiceRecordService;

    @Autowired
    private UserExtractService userExtractService;

    @Override
    public Boolean paySuccess(StoreOrder storeOrder) {
        // 得到下单用户信息
        User user = userService.getById(storeOrder.getUid());

        // 实例化对象
        List<UserBill> billList = CollUtil.newArrayList();
        List<PublicIntegalRecord> publicIntegalRecordList = CollUtil.newArrayList();

        // 订单支付
        UserBill userBill = userBillService.getUserBill(    // 订单支付-账单记录
                storeOrder.getUid(),
                storeOrder.getId().toString(),
                Constants.USER_BILL_PM_0,
                storeOrder.getPayType(),
                Constants.USER_BILL_TYPE_PAY_ORDER,
                storeOrder.getPayPrice(),
                user.getNowMoney(),
                ""
        );
        billList.add(userBill);

        // 经验处理：1.经验添加，2.等级计算
        Integer experience = storeOrder.getPayPrice().setScale(0, BigDecimal.ROUND_DOWN).intValue();
        user.setExperience(user.getExperience() + experience);

        // 订单支付获得经验值
        UserBill experienceBill = userBillService.getUserBill(  // 订单支付获得经验值-账单记录
                storeOrder.getUid(),
                storeOrder.getId().toString(),
                Constants.USER_BILL_PM_1,
                Constants.USER_BILL_CATEGORY_EXPERIENCE,
                Constants.USER_BILL_TYPE_PAY_ORDER,
                new BigDecimal(experience),
                new BigDecimal(user.getExperience()),
                ""
        );
        billList.add(experienceBill);

        // 商品赠送积分
        // 查询订单详情
        // 获取商品额外赠送积分
        List<StoreOrderInfoOldVo> orderInfoList = storeOrderInfoService.getOrderListByOrderId(storeOrder.getId());
        List<OrderInfoDetailVo> productIds = orderInfoList.stream().map(StoreOrderInfoOldVo::getInfo).collect(Collectors.toList());
        if(productIds!=null && productIds.size() > 0){
            //统计积分
            BigDecimal sumIntegral=BigDecimal.ZERO;
            for (OrderInfoDetailVo proInfo:productIds) {
                StoreProductAttrValue storeProductAttrValue = storeProductAttrValueService.getById(proInfo.getAttrValueId());
                if(storeProductAttrValue == null)continue;
                BigDecimal infoIntegral= storeProductAttrValue.getIntegral().multiply(new BigDecimal(proInfo.getPayNum()));
                sumIntegral=sumIntegral.add(infoIntegral);
            }

            //验证积分
            if (sumIntegral.compareTo(BigDecimal.ZERO) == 1) {
                // 生成公共积分记录
                PublicIntegalRecord publicIntegalRecord = publicIntegalRecordService.add( // 支付成功值之后处理
                        user.getUid(),
                        String.valueOf(storeOrder.getId()),
                        sumIntegral,
                        user.getIntegral(),
                        IntegralRecordConstants.PUBLIC_INTEGRAL_RECORD_LINK_TYPE_ORDER_INT,
                        Constants.ADD,
                        IntegralRecordConstants.PUBLIC_INTEGRAL_RECORD_STATUS_CREATE,
                        Boolean.FALSE,
                        storeOrder.getSpreadUid(),"");
                publicIntegalRecordList.add(publicIntegalRecord);
            }
        }

        // 更新用户下单数量
        user.setPayCount(user.getPayCount() + 1);

        //计算佣金
        List<UserBrokerageRecord>  recordList = assignCommission(storeOrder);

        //验证是否开具发票
        if(storeOrder.getRiseId()>0){
            InvoiceRecord invoiceRecord=new InvoiceRecord();
            invoiceRecord.setOrderId(storeOrder.getId());
            invoiceRecord.setRiseId(storeOrder.getRiseId());
            invoiceRecord.setUid(storeOrder.getUid());
            invoiceRecordService.save(invoiceRecord);
        }

        //验证订单推荐人用户是否为联盟商家
        User spUser=userService.getById(storeOrder.getSpreadUid());
        Boolean isStatus=Boolean.FALSE;
        if(spUser!=null){
            String tagid=spUser.getTagId();
            if((Constants.TARGID_LMSJ.equals(tagid)) ||
                    (tagid.indexOf(",10")!=-1&&tagid.indexOf(",1010") == -1)||
                    (tagid.indexOf("10,")!=-1&&tagid.indexOf("1010,") == -1)){
                isStatus=Boolean.TRUE;
            }
        }

        //验证是否减少额度
        UserExtract userExtract=null;
        Boolean integralPayBL=PayConstants.PAY_TYPE_INTEGRAL.equals(storeOrder.getPayType());
        if((integralPayBL&&storeOrder.getQuotaControl() == Constants.ORDER_QUOTA_SUB)||
                (integralPayBL&&storeOrder.getQuotaControl() == Constants.ORDER_QUOTA_ADD_SUB)){
            //增加-提现记录(减少额度)
            userExtract=new UserExtract();
            userExtract.setLinkType(Constants.USER_BALANCE_TIXIN_LINKTYPE_JFDHSUBQUOTA);
            userExtract.setStatus(Constants.USER_EXTRACT_STATUS_SUCCESS);
            userExtract.setUid(user.getUid());
            userExtract.setExtractPrice(storeOrder.getPayPrice());
            userExtract.setServiceFee(ZERO);
            userExtract.setBalance(user.getIntegral());
            userExtract.setIsOk(Boolean.TRUE);
            userExtract.setNickName(user.getNickname());
            userExtract.setRealName(user.getRealName());
            userExtract.setExtractType("other");
            userExtract.setRemark("积分兑换减少额度");
            userExtract.setCreateTime(DateUtil.nowDateTime());
        }

        //执行结果
        Boolean finalIsStatus = isStatus;
        UserExtract finalUserExtract = userExtract;
        Boolean execute = transactionTemplate.execute(e -> {
            //订单日志
            storeOrderStatusService.addLog(storeOrder.getId(), Constants.ORDER_LOG_PAY_SUCCESS, Constants.ORDER_LOG_MESSAGE_PAY_SUCCESS);

            //用户信息变更
            userService.updateById(user);

            //资金变动
            userBillService.saveBatch(billList);

            //公共积分记录
            publicIntegalRecordService.saveBatch(publicIntegalRecordList);

            //经验升级
            userLevelService.upLevel(user);

            //佣金记录
            if (CollUtil.isNotEmpty(recordList)) {
                userBrokerageRecordService.saveBatch(recordList);
            }

            //如果是拼团订单进行拼团后置处理
            if (storeOrder.getCombinationId() > 0) {
                pinkProcessing(storeOrder);
            }

            //验证配送类型-更新订单状态
            if(finalIsStatus){
                //已收货，待评价
                storeOrder.setStatus(Constants.ORDER_STATUS_INT_BARGAIN);
                storeOrderService.updateById(storeOrder);

                //后续操作放入redis
                redisUtil.lPush(Constants.ORDER_TASK_REDIS_KEY_AFTER_TAKE_BY_USER, storeOrder.getId());//订单收货之后处理-联盟商家订单
            }else if(storeOrder.getShippingType().equals(3)){
                storeOrder.setStatus(Constants.ORDER_STATUS_INT_COMPLETE);
                storeOrderService.updateById(storeOrder);
                redisUtil.lPush(Constants.ORDER_TASK_REDIS_KEY_AFTER_COMPLETE_BY_USER, storeOrder.getId());//订单完成之后业务处理-自提核销订单
            }

            //验证减少额度记录
            if(finalUserExtract !=null){
                userExtractService.save(finalUserExtract);
            }

            //执行结果
            return Boolean.TRUE;
        });

        //验证-执行结果
        if (execute) {
            try {
                // 发送短信
                if (StrUtil.isNotBlank(user.getPhone())) {
                    // 支付成功提醒开关
                    String lowerOrderSwitch = systemConfigService.getValueByKey(SmsConstants.SMS_CONFIG_LOWER_ORDER_SWITCH);
                    if (StrUtil.isNotBlank(lowerOrderSwitch) && lowerOrderSwitch.equals("1")) {
                        smsService.sendPaySuccess(user.getPhone(), storeOrder.getOrderId(), storeOrder.getPayPrice());
                    }
                }

                // 发送用户支付成功管理员提醒短信
                String smsSwitch = systemConfigService.getValueByKey(SmsConstants.SMS_CONFIG_ADMIN_PAY_SUCCESS_SWITCH);
                if (StrUtil.isNotBlank(smsSwitch) && smsSwitch.equals("1")) {
                    // 查询可已发送短信的管理员
                    List<SystemAdmin> systemAdminList = systemAdminService.findIsSmsList();
                    if (CollUtil.isNotEmpty(systemAdminList)) {
                        // 发送短信
                        systemAdminList.forEach(admin -> {
                            smsService.sendOrderPaySuccessNotice(admin.getPhone(), storeOrder.getOrderId(), admin.getRealName());
                        });
                    }
                }

                //下发模板通知
                pushMessageOrder(storeOrder, user);

                // 购买成功后根据配置送优惠券
                autoSendCoupons(storeOrder);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("短信、模板通知或优惠券异常");
            }
        }

        //返回-执行结果
        return execute;
    }

    // 支持成功拼团后置处理
    private Boolean pinkProcessing(StoreOrder storeOrder) {
        // 判断拼团是否成功
        StorePink storePink = storePinkService.getById(storeOrder.getPinkId());

        if (storePink.getKId() <= 0) {
            return true;
        }

        List<StorePink> pinkList = storePinkService.getListByCidAndKid(storePink.getCid(), storePink.getKId());
        StorePink tempPink = storePinkService.getById(storePink.getKId());
        pinkList.add(tempPink);
        if (pinkList.size() < storePink.getPeople()) {// 还未拼团成功
            return true;
        }
        // 1.修改拼团状态
        // 2.给所有拼团人员发送拼团成功通知
        pinkList.forEach(e -> {
            e.setStatus(2);
        });
        boolean update = storePinkService.updateBatchById(pinkList);
        if (!update) {
            logger.error("拼团订单支付成功后更新拼团状态失败,orderNo = " + storeOrder.getOrderId());
            return false;
        }
        pinkList.forEach(i -> {
            StoreOrder order = storeOrderService.getByOderId(i.getOrderId());
            StoreCombination storeCombination = storeCombinationService.getById(i.getCid());
            User tempUser = userService.getById(i.getUid());
            // 发送微信模板消息
            MyRecord record = new MyRecord();
            record.set("orderNo", order.getOrderId());
            record.set("proName", storeCombination.getTitle());
            record.set("payType", order.getPayType());
            record.set("isChannel", order.getIsChannel());
            pushMessagePink(record, tempUser);
        });
        return true;
    }

    /**
     * 发送拼团成功通知
     * @param record 信息参数
     * @param user 用户
     */
    private void pushMessagePink(MyRecord record, User user) {
        if (!record.getStr("payType").equals(PayConstants.PAY_TYPE_WE_CHAT)) {
            return ;
        }
        if (record.getInt("isChannel").equals(2)) {
            return ;
        }

        UserToken userToken;
        HashMap<String, String> temMap = new HashMap<>();
        // 公众号
        if (record.getInt("isChannel").equals(PayConstants.ORDER_PAY_CHANNEL_PUBLIC)) {
            userToken = userTokenService.getTokenByUserId(user.getUid(), UserConstants.USER_TOKEN_TYPE_WECHAT);
            if (ObjectUtil.isNull(userToken)) {
                return ;
            }
            // 发送微信模板消息
            temMap.put(Constants.WE_CHAT_TEMP_KEY_FIRST, "恭喜您拼团成功！我们将尽快为您发货。");
            temMap.put("keyword1", record.getStr("orderNo"));
            temMap.put("keyword2", record.getStr("proName"));
            temMap.put(Constants.WE_CHAT_TEMP_KEY_END, "感谢你的使用！");
            templateMessageService.pushTemplateMessage(Constants.WE_CHAT_TEMP_KEY_COMBINATION_SUCCESS, temMap, userToken.getToken());
            return;
        }
        // 小程序发送订阅消息
        userToken = userTokenService.getTokenByUserId(user.getUid(), UserConstants.USER_TOKEN_TYPE_ROUTINE);
        if (ObjectUtil.isNull(userToken)) {
            return ;
        }
        // 组装数据
        temMap.put("character_string1",  record.getStr("orderNo"));
        temMap.put("thing2", record.getStr("proName"));
        temMap.put("thing5", "恭喜您拼团成功！我们将尽快为您发货。");
        templateMessageService.pushMiniTemplateMessage(Constants.WE_CHAT_PROGRAM_TEMP_KEY_COMBINATION_SUCCESS, temMap, userToken.getToken());

    }

    /**
     * 分配佣金
     * @param storeOrder 订单
     * @return List<UserBrokerageRecord>
     */
    private List<UserBrokerageRecord> assignCommission(StoreOrder storeOrder) {
        // 检测商城是否开启分销功能
        String isOpen = systemConfigService.getValueByKey(Constants.CONFIG_KEY_STORE_BROKERAGE_IS_OPEN);
        if(StrUtil.isBlank(isOpen) || isOpen.equals("0")){
            return CollUtil.newArrayList();
        }

        // 营销产品不参与
        if(storeOrder.getCombinationId() > 0 || storeOrder.getSeckillId() > 0 || storeOrder.getBargainId() > 0){
            return CollUtil.newArrayList();
        }

        // 查找订单所属人信息
        User user = userService.getById(storeOrder.getUid());
        // 当前用户不存在\没有上级\或者当用用户上级时自己  直接返回
        if(user == null || null == user.getSpreadUid() || user.getSpreadUid() < 1 || user.getSpreadUid().equals(storeOrder.getUid())){
            return CollUtil.newArrayList();
        }

        // 获取参与分佣的人（两级）+区域代理佣金
        // 计算区域代理佣金-验证是否为区域代理订单
        List<MyRecord> spreadRecordList=new ArrayList<>();
        if(storeOrder.getType() == Constants.ORDER_TYPE_2){
            //得到区域代理信息
            RegionalAgency regionalAgency=regionalAgencyService.getById(storeOrder.getMerId());
            if(regionalAgency != null){
                //得到区域代理管理用户
                User raUser=userService.getById(regionalAgency.getUid());
                if(raUser != null){
                    //设置级数为区域代理
                    MyRecord firstRecord = new MyRecord();
                    firstRecord.set("index", 0);
                    firstRecord.set("spreadUid", raUser.getUid());
                    spreadRecordList.add(firstRecord);
                }
            }
        }

        // 计算佣金用户推广关系佣金，生成佣金记录,20111227-add:验证用户是否第一次下单
        Integer orderCount = storeOrderService.getOrderCount(1,user.getUid(),null);
        if(orderCount <= 1){
            spreadRecordList = this.getSpreadRecordList(user.getSpreadUid());
        }

        // 计算佣金记录非空
        if (CollUtil.isEmpty(spreadRecordList)) {
            return CollUtil.newArrayList();
        }

        // 获取佣金冻结期
        String fronzenTime = systemConfigService.getValueByKey(Constants.CONFIG_KEY_STORE_BROKERAGE_EXTRACT_TIME);

        // 生成佣金记录
        List<UserBrokerageRecord> brokerageRecordList = spreadRecordList.stream().map(record -> {
            //计算佣金并得到佣金记录对象
            UserBrokerageRecord brokerageRecord =  this.calculateCommission(record, storeOrder.getId(),fronzenTime); // 计算佣金并得到佣金记录对象
            brokerageRecord.setBalance(user.getBrokeragePrice()==null?BigDecimal.ZERO:user.getBrokeragePrice());
            return brokerageRecord;
        }).collect(Collectors.toList());

        //返回佣金记录
        return brokerageRecordList;
    }

    public static void main(String[] args) {
         List<String> A= Arrays.stream("10, 23, 42, 12, 20, 6".split(",")).collect(Collectors.toList());
         Collections.reverse(A);
         for(String a : A){
                 System.out.println(a);
         }
    }

    /**
     * 计算佣金
     * @param record index-分销级数，spreadUid-分销人
     * @param orderId 订单id
     * @param  fronzenTime 冻结时间
     * @return
     */
    private UserBrokerageRecord calculateCommission(MyRecord record, Integer orderId,String fronzenTime) {
        //实例化-用户佣金记录对象
        UserBrokerageRecord brokerageRecord = userBrokerageRecordService.getUserBrokerageRecord(    // 订单佣金记录
                record.getInt("spreadUid"),
                String.valueOf(orderId),
                BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_ORDER,
                BrokerageRecordConstants.BROKERAGE_RECORD_TYPE_1,
                BrokerageRecordConstants.BROKERAGE_RECORD_TYPE_ADD,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                Integer.valueOf(Optional.ofNullable(fronzenTime).orElse("0")),0L
        );

        //定义变量
        String title=BrokerageRecordConstants.BROKERAGE_RECORD_TITLE_ORDER; //自定义-佣金记录标题
        String zodailiMarke="";                             //区域代理-字符串标识
        String mark="%s获得推广佣金，分佣%s";                   //备注
        BigDecimal brokeragePrice = BigDecimal.ZERO;        //佣金-返佣金
        BigDecimal totalBrokerPrice = BigDecimal.ZERO;      //返佣金统计-过渡变量
        Integer index = record.getInt("index");     //查询对应等级的分销比例
        String key = "";                                    //配置key
        BigDecimal rateBigDecimal = BigDecimal.ZERO;        //系统默认配置返佣

        //验证-返佣等级
        switch (index){
            case 0:
                //区域代理
                zodailiMarke="区域代理-";
                key = Constants.CONFIG_KEY_STORE_GACR;
                title = systemConfigService.getValueByKey(Constants.CONFIG_KEY_STORE_GACR_TITLE);
                break;
            case 1:
                //推广1级
                key = Constants.CONFIG_KEY_STORE_BROKERAGE_RATE_ONE;
                break;
            case 2:
                //推广2级
                key = Constants.CONFIG_KEY_STORE_BROKERAGE_RATE_TWO;
                break;
            default:
                return new UserBrokerageRecord();
        }

        //得到-系统默认配置：百分比*%,如果为配置默认设置1%
        String rate = systemConfigService.getValueByKey(key);
        if(StringUtils.isBlank(rate))rate = "1";
        rateBigDecimal = new BigDecimal(rate).divide(BigDecimal.TEN.multiply(BigDecimal.TEN));//佣金比例转数值

        // 查询订单详情
        List<StoreOrderInfoOldVo> orderInfoVoList = storeOrderInfoService.getOrderListByOrderId(orderId);
        if (CollUtil.isEmpty(orderInfoVoList)) {
            //赋值佣金
            brokerageRecord.setTitle(title);
            brokerageRecord.setPrice(brokeragePrice);
            brokerageRecord.setMark(String.format(mark,zodailiMarke,brokeragePrice));
            return brokerageRecord;
        }

        //循环处理-订单详情
        for (StoreOrderInfoOldVo orderInfoVo : orderInfoVoList) {
            //得到-商品属性值-》先看商品是否有固定分佣
            StoreProductAttrValue attrValue = storeProductAttrValueService.getById(orderInfoVo.getInfo().getAttrValueId());

            // 验证是否为区域代理
            if(index == 0){
                //得到商品信息
                StoreProduct storeProduct= storeProductService.getById(attrValue.getProductId());
                if(storeProduct==null){
                    throw new CrmebException("商品不存在！");
                }

                //验证商品-有没有-单独设置区域代理分佣
                switch (storeProduct.getUga()){
                    case 0:
                        //没有设置
                        break;
                    case 1:
                        //比例
                        rate = attrValue.getUgaBrokerage().toString();
                        rateBigDecimal = new BigDecimal(rate).divide(BigDecimal.TEN.multiply(BigDecimal.TEN));
                        brokeragePrice = orderInfoVo.getInfo().getPrice().multiply(rateBigDecimal).setScale(2, BigDecimal.ROUND_DOWN);
                        break;
                    case 2:
                        //金额
                        brokeragePrice=attrValue.getUgaPrice();
                        break;
                    default:
                        throw new CrmebException("单独设置区域代理分佣-值错误！");
                }
            }else  if (orderInfoVo.getInfo().getIsSub()!=null&&orderInfoVo.getInfo().getIsSub()) {// 验证商品-是否-单独设置分销分佣
                //验证-返佣等级
                if(index == 1){
                    brokeragePrice = Optional.ofNullable(attrValue.getBrokerage()).orElse(BigDecimal.ZERO);
                }else if(index == 2){
                    brokeragePrice = Optional.ofNullable(attrValue.getBrokerageTwo()).orElse(BigDecimal.ZERO);
                }
            } else {
                //系统默认分佣
                if(!rateBigDecimal.equals(BigDecimal.ZERO)){
                    // 商品没有分销金额, 并且有设置对应等级的分佣比例
                    // 舍入模式向零舍入。
                    brokeragePrice = orderInfoVo.getInfo().getPrice().multiply(rateBigDecimal).setScale(2, BigDecimal.ROUND_DOWN);
                } else {
                    brokeragePrice = BigDecimal.ZERO;
                }
            }

            // 同规格商品可能有多件
            if (brokeragePrice.compareTo(BigDecimal.ZERO) > 0 && orderInfoVo.getInfo().getPayNum() > 1) {
                brokeragePrice = brokeragePrice.multiply(new BigDecimal(orderInfoVo.getInfo().getPayNum()));
            }

            // 统计返佣金
            totalBrokerPrice = totalBrokerPrice.add(brokeragePrice);
        }

        //赋值佣金
        brokerageRecord.setTitle(title);
        brokerageRecord.setPrice(totalBrokerPrice);
        brokerageRecord.setMark(String.format(mark,zodailiMarke,brokeragePrice));

        //返回
        return brokerageRecord;
    }

    /**
     * 获取参与分佣人员（两级）
     * @param spreadUid 一级分佣人Uid
     * @return List<MyRecord>
     */
    private List<MyRecord> getSpreadRecordList(Integer spreadUid) {
        List<MyRecord> recordList = CollUtil.newArrayList();

        // 第一级
        User spreadUser = userService.getById(spreadUid);
        if (ObjectUtil.isNull(spreadUser)) {
            return recordList;
        }
        // 判断分销模式
        String model = systemConfigService.getValueByKey(Constants.CONFIG_KEY_STORE_BROKERAGE_MODEL);
        if (StrUtil.isNotBlank(model) && model.equals("1") && !spreadUser.getIsPromoter()) {
            // 指定分销模式下：不是推广员不参与分销
            return recordList;
        }
        MyRecord firstRecord = new MyRecord();
        firstRecord.set("index", 1);
        firstRecord.set("spreadUid", spreadUid);
        recordList.add(firstRecord);

        // 第二级
        User spreadSpreadUser = userService.getById(spreadUser.getSpreadUid());
        if (ObjectUtil.isNull(spreadSpreadUser)) {
            return recordList;
        }
        if (StrUtil.isNotBlank(model) && model.equals("1") && !spreadSpreadUser.getIsPromoter()) {
            // 指定分销模式下：不是推广员不参与分销
            return recordList;
        }
        MyRecord secondRecord = new MyRecord();
        secondRecord.set("index", 2);
        secondRecord.set("spreadUid", spreadSpreadUser.getUid());
        recordList.add(secondRecord);

        //返回分销员信息
        return recordList;
    }

    /**
     * 余额支付
     * @param storeOrder 订单
     * @return Boolean Boolean
     */
    @Override
    public Boolean yuePay(StoreOrder storeOrder) {
        // 用户余额扣除
        User user = userService.getById(storeOrder.getUid());
        if (ObjectUtil.isNull(user)) throw new CrmebException("用户不存在");

        //验证-余额
        if (user.getNowMoney().compareTo(storeOrder.getPayPrice()) < 0) {
            throw new CrmebException("用户余额不足");
        }

        //设置支付状态、支付时间
        storeOrder.setPaid(true);
        storeOrder.setPayTime(DateUtil.nowDateTime());

        //执行结果
        Boolean execute = transactionTemplate.execute(e -> {
            // 订单修改
            storeOrderService.updateById(storeOrder);

            // 这里只扣除金额，账单记录在task中处理
            userService.updateNowMoney(user, storeOrder.getPayPrice(), "sub");

            // 添加支付成功redis队列
            redisUtil.lPush(Constants.ORDER_TASK_PAY_SUCCESS_AFTER, storeOrder.getId()); // 余额支付

            // 处理拼团
            if (storeOrder.getCombinationId() > 0) {
                // 判断拼团团长是否存在
                StorePink headPink = new StorePink();
                Integer pinkId = storeOrder.getPinkId();
                if (pinkId > 0) {
                    headPink = storePinkService.getById(pinkId);
                    if (ObjectUtil.isNull(headPink) || headPink.getIsRefund().equals(true) || headPink.getStatus() == 3) {
                        pinkId = 0;
                    }
                }

                //实例化-拼团对象
                StoreCombination storeCombination = storeCombinationService.getById(storeOrder.getCombinationId());

                // 如果拼团人数已满，重新开团
                if (pinkId > 0) {
                    Integer count = storePinkService.getCountByKid(pinkId);
                    if (count >= storeCombination.getPeople()) {
                        pinkId = 0;
                    }
                }

                // 生成拼团表数据
                StorePink storePink = new StorePink();
                storePink.setUid(user.getUid());
                storePink.setAvatar(user.getAvatar());
                storePink.setNickname(user.getNickname());
                storePink.setOrderId(storeOrder.getOrderId());
                storePink.setOrderIdKey(storeOrder.getId());
                storePink.setTotalNum(storeOrder.getTotalNum());
                storePink.setTotalPrice(storeOrder.getTotalPrice());
                storePink.setCid(storeCombination.getId());
                storePink.setPid(storeCombination.getProductId());
                storePink.setPeople(storeCombination.getPeople());
                storePink.setPrice(storeCombination.getPrice());
                Integer effectiveTime = storeCombination.getEffectiveTime();// 有效小时数
                DateTime dateTime = cn.hutool.core.date.DateUtil.date();
                storePink.setAddTime(dateTime.getTime());

                //设置拼图结束时间
                if (pinkId > 0) {
                    storePink.setStopTime(headPink.getStopTime());
                } else {
                    DateTime hourTime = cn.hutool.core.date.DateUtil.offsetHour(dateTime, effectiveTime);
                    long stopTime =  hourTime.getTime();
                    if (stopTime > storeCombination.getStopTime()) {
                        stopTime = storeCombination.getStopTime();
                    }
                    storePink.setStopTime(stopTime);
                }

                //设置团长ID、是否发送模版信息、状态等
                storePink.setKId(pinkId);
                storePink.setIsTpl(false);
                storePink.setIsRefund(false);
                storePink.setStatus(1);

                //保存-拼团信息
                storePinkService.save(storePink);

                // 如果是开团，需要更新订单数据
                storeOrder.setPinkId(storePink.getId());

                //修改拼团订单
                storeOrderService.updateById(storeOrder);
            }
            return Boolean.TRUE;
        });

        //验证执行结果
        if (!execute) throw new CrmebException("余额支付订单失败");
        return execute;
    }

    @Override
    public OrderPayResultResponse payment(OrderPayRequest orderPayRequest, String ip, HttpServletResponse httpServletResponse) {
        //得到订单
        StoreOrder storeOrder = storeOrderService.getByOderId(orderPayRequest.getOrderNo());

        //验证订单
        if (ObjectUtil.isNull(storeOrder)) {
            throw new CrmebException("订单不存在");
        }
        if (storeOrder.getIsDel()) {
            throw new CrmebException("订单已被删除");
        }
        if (storeOrder.getPaid()) {
            throw new CrmebException("订单已支付");
        }

        //得到用户
        User user = userService.getById(storeOrder.getUid());
        if (ObjectUtil.isNull(user)) throw new CrmebException("用户不存在");

        // 判断订单是否还是之前的支付类型
        if (!storeOrder.getPayType().equals(orderPayRequest.getPayType())) {
            // 根据支付类型进行校验,更换支付类型
            storeOrder.setPayType(orderPayRequest.getPayType());

            //验证-余额支付
            if (orderPayRequest.getPayType().equals(PayConstants.PAY_TYPE_YUE)) {
                if (user.getNowMoney().compareTo(storeOrder.getPayPrice()) < 0) {
                    throw new CrmebException("用户余额不足");
                }
                storeOrder.setIsChannel(3);
            }

            //验证-微信支付
            if (orderPayRequest.getPayType().equals(PayConstants.PAY_TYPE_WE_CHAT)) {
                //设置-微信支付类型
                switch (orderPayRequest.getPayChannel()){
                    case PayConstants.PAY_CHANNEL_WE_CHAT_H5:// H5
                        storeOrder.setIsChannel(2);
                        break;
                    case PayConstants.PAY_CHANNEL_WE_CHAT_PUBLIC:// 公众号
                        storeOrder.setIsChannel(0);
                        break;
                    case PayConstants.PAY_CHANNEL_WE_CHAT_PROGRAM:// 小程序
                        storeOrder.setIsChannel(1);
                        break;
                    case PayConstants.PAY_CHANNEL_WE_CHAT_APP_IOS:
                        storeOrder.setIsChannel(4);
                        break;
                    case PayConstants.PAY_CHANNEL_WE_CHAT_APP_ANDROID:
                        storeOrder.setIsChannel(5);
                        break;
                }
            }

            // 积分支付-2021/10/20
            if (storeOrder.getPayType().equals(PayConstants.PAY_TYPE_INTEGRAL)){
                //验证-积分
                if (user.getIntegral().compareTo(storeOrder.getPayPrice()) == -1) {
                    throw new CrmebException("用户酒米余额不足!");
                }
                storeOrder.setIsChannel(9);
            }

            //变更订单-支付方式
            boolean changePayType = storeOrderService.updateById(storeOrder);
            if (!changePayType) {
                throw new CrmebException("变更订单支付类型失败!");
            }
        }

        //实例化-订单-支付结果-响应对象
        OrderPayResultResponse response = new OrderPayResultResponse();
        response.setOrderNo(storeOrder.getOrderId());
        response.setPayType(storeOrder.getPayType());

        // 0元付
        if (storeOrder.getPayPrice().compareTo(BigDecimal.ZERO) <= 0) {
            //则，调用余额支付
            Boolean aBoolean = yuePay(storeOrder);
            response.setPayType(PayConstants.PAY_TYPE_ZERO_PAY);
            response.setStatus(aBoolean);
            return response;
        }

        // 微信支付，调用微信预下单，返回拉起微信支付需要的信息
        if (storeOrder.getPayType().equals(PayConstants.PAY_TYPE_WE_CHAT)) {
            //微信预下单
            Map<String, String> unifiedorder = weChatPayService.unifiedorder(storeOrder, ip);
            response.setStatus(true);

            //实例化-微信支付参数对象
            WxPayJsResultVo vo = new WxPayJsResultVo();
            vo.setAppId(unifiedorder.get("appId"));
            vo.setNonceStr(unifiedorder.get("nonceStr"));
            vo.setPackages(unifiedorder.get("package"));
            vo.setSignType(unifiedorder.get("signType"));
            vo.setTimeStamp(unifiedorder.get("timeStamp"));
            vo.setPaySign(unifiedorder.get("paySign"));

            //验证支付渠道，2=H5
            if (storeOrder.getIsChannel() == 2) {
                vo.setMwebUrl(unifiedorder.get("mweb_url"));
                response.setPayType(PayConstants.PAY_CHANNEL_WE_CHAT_H5);
            }

            //验证支付渠道-ios、Android
            if (storeOrder.getIsChannel() == 4|| storeOrder.getIsChannel() == 5) {
                vo.setPartnerid(unifiedorder.get("partnerid"));
            }

            //设置-支付结果-响应对象-微信支付参数
            response.setJsConfig(vo);

            //返回-支付结果-响应对象
            return response;
        }

        // 余额支付
        if (storeOrder.getPayType().equals(PayConstants.PAY_TYPE_YUE)) {
            Boolean yueBoolean = yuePay(storeOrder);
            response.setStatus(yueBoolean);
            return response;
        }

        // 支付宝支付
        if (storeOrder.getPayType().equals(PayConstants.PAY_TYPE_ALI_PAY)) {
            //读取配置
            String appid=systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_ZFB_APP_ZF_APPID);
            String secret_key=systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_ZFB_APP_ZF_SECRET_KEY);
            String public_key=systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_ZFB_APP_ZF_PUBLIC_KEY);

            //创建-支付宝链接对象
            AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do",
                    appid,
                    secret_key,
                    "json",
                    "UTF-8",
                    public_key,
                    "RSA2");

            //验证渠道
            switch (orderPayRequest.getPayChannel()){
                case PayConstants.PAY_CHANNEL_ZFB_CHAT_APP_ANDROID:
                    storeOrder.setIsChannel(6);
                    return this.getZFBAPPResponse(storeOrder,response,alipayClient);
                case PayConstants.PAY_CHANNEL_ZFB_CHAT_APP_IOS:
                    storeOrder.setIsChannel(7);
                    return this.getZFBAPPResponse(storeOrder,response,alipayClient);
                case PayConstants.PAY_CHANNEL_ZFB_CHAT_APP_WEB:
                    storeOrder.setIsChannel(8);
                    return this.getZFBWebResponse(storeOrder, response, alipayClient,httpServletResponse);
                default:
                    throw new CrmebException("支付宝渠道错误！");
            }
        }

        // 积分支付-2021/10/20
        if (storeOrder.getPayType().equals(PayConstants.PAY_TYPE_INTEGRAL)){
            //验证-可用米额度
            BigDecimal keIntegral=userService.getKeyonMiED(user);
            if(keIntegral.compareTo(storeOrder.getPayPrice()) == -1){
                throw new CrmebException("用户可用米不不够!");
            }

            //设置支付状态、支付时间
            storeOrder.setPaid(true);
            storeOrder.setPayTime(DateUtil.nowDateTime());

            //执行结果
            Boolean execute = transactionTemplate.execute(e -> {
                // 订单修改
                storeOrderService.updateById(storeOrder);
                logger.info("积分支付 || 订单修改");

                // 扣除积分-并增加积分记录
                userService.operationIntegral(user.getUid(),storeOrder.getPayPrice(),user.getIntegral(),"sub");
                UserIntegralRecord integralRecord = userIntegralRecordService.getUserIntegralRecord( // 积分支付扣除积分记录
                        user.getUid(),
                        user.getIntegral(),
                        storeOrder.getOrderId(),
                        IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_ORDER,
                        IntegralRecordConstants.INTEGRAL_RECORD_TYPE_SUB,
                        IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE,
                        storeOrder.getPayPrice(),null);
                userIntegralRecordService.save(integralRecord);
                logger.info("积分支付 || 积分支付扣除积分记录");

                // 添加支付成功redis队列
                redisUtil.lPush(Constants.ORDER_TASK_PAY_SUCCESS_AFTER, storeOrder.getId()); // 积分支付
                logger.info("积分支付 || 添加支付成功redis队列");
                return Boolean.TRUE;
            });

            //验证-执行结果
            if(!execute){
                logger.info("积分支付 || 添加支付成功redis队列");
                throw new CrmebException("积分支付失败!");
            }
            logger.info("积分支付 || 结束");

            //返回-支付结果-响应对象
            return response;
        }

        //其他支付-验证
        if (storeOrder.getPayType().equals(PayConstants.PAY_TYPE_OFFLINE)) {
            throw new CrmebException("暂时不支持线下支付");
        }

        //设置-支付状态
        response.setStatus(false);

        //返回-支付结果-响应对象
        return response;
    }

    /**
     * 支付宝-web支付
     * @param storeOrder
     * @param response
     * @param alipayClient
     * @return
     * @throws AlipayApiException
     */
    private OrderPayResultResponse getZFBWebResponse(StoreOrder storeOrder, OrderPayResultResponse response, AlipayClient alipayClient,HttpServletResponse httpServletResponse)  {
        try{
            AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();
            AlipayTradeWapPayModel model = new AlipayTradeWapPayModel();
            model.setOutTradeNo(storeOrder.getOrderId());
            model.setTotalAmount(storeOrder.getPayPrice().toString());
            model.setSubject(PayConstants.PAY_BODY_QJY+"(web支付)");
            model.setProductCode("QUICK_WAP_PAY");
            model.setSellerId("2088241500822091");
            model.setQuitUrl("https://admin.gzsskj.cn/api/admin/payment/callback/alipay");
            request.setBizModel(model);
            AlipayTradeWapPayResponse response2 = alipayClient.pageExecute(request);
            System.out.println(response2.getBody());
            if (!response2.isSuccess()) {
                throw new CrmebException("支付宝web支付,创建订单失败！");
            }

            //设置支付参数
            response.setStatus(true);
            response.setBody(response2.getBody());
            response.setPayType(PayConstants.PAY_TYPE_ALI_PAY);

            //返回-支付结果-响应对象
            return response;
        }catch(Exception e){
            e.printStackTrace();
            throw new CrmebException("支付宝web支付错误！"+e.getMessage());
        }
    }

    /**
     * 支付宝-app支付
     * @param storeOrder
     * @param response
     * @param alipayClient
     * @return
     */
    private OrderPayResultResponse getZFBAPPResponse(StoreOrder storeOrder, OrderPayResultResponse response, AlipayClient alipayClient) {
        //实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();

        //SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        model.setBody("订单附加信息。如果请求时传递了该参数，将在异步通知、对账单中原样返回，同时会在商户和用户的pc账单详情中作为交易描述展示。");
        model.setSubject(PayConstants.PAY_BODY_QJY);//订单标题
        model.setOutTradeNo(storeOrder.getOrderId());//商户订单号
        /**
         * 订单相对超时时间。
         * 该笔订单允许的最晚付款时间，逾期将关闭交易。取值范围：1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。 该参数数值不接受小数点， 如 1.5h，可转换为 90m。
         * 当面付场景默认值为3h；
         * 其它场景默认值为15d;
         */
        model.setTimeoutExpress("30m");//订单超时时间
        model.setTotalAmount(storeOrder.getPayPrice().toString());//实际支付金额
        /**
         * 产品码。
         * 商家和支付宝签约的产品码。 枚举值（点击查看签约情况）：
         * FACE_TO_FACE_PAYMENT：当面付产品；
         * CYCLE_PAY_AUTH：周期扣款产品；
         * GENERAL_WITHHOLDING：代扣产品；
         * PRE_AUTH_ONLINE：支付宝预授权产品；
         * PRE_AUTH：新当面资金授权产品；
         * 默认值为FACE_TO_FACE_PAYMENT。
         * 注意：非当面付产品使用本接口时，本参数必填。请传入对应产品码。
         */
        model.setProductCode("QUICK_MSECURITY_PAY");

        //设置请求参数
        request.setBizModel(model);
        request.setNotifyUrl("https://adminjcx.gzsskj.cn/api/admin/payment/callback/alipay");//回调接口

        try {
            //这里和普通的接口调用不同，使用的是sdkExecute
            AlipayTradeAppPayResponse response2 = alipayClient.sdkExecute(request);
            System.out.println(response2.getBody());//就是orderString 可以直接给客户端请求，无需再做处理。

            //设置支付参数
            response.setStatus(true);
            response.setBody(response2.getBody());
            response.setPayType(PayConstants.PAY_TYPE_ALI_PAY);

            //返回-支付结果-响应对象
            return response;
        } catch (AlipayApiException e) {
            e.printStackTrace();
            throw new CrmebException("暂时不支持支付宝支付");
        }
    }

    /**
     * 积分抵扣记录
     * @param storeOrder
     * @param user
     * @return
     */
    private UserIntegralRecord integralRecordSubInit(StoreOrder storeOrder, User user) {
        UserIntegralRecord integralRecord = new UserIntegralRecord();
        integralRecord.setUid(storeOrder.getUid());
        integralRecord.setLinkId(storeOrder.getOrderId());
        integralRecord.setLinkType(IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_ORDER);
        integralRecord.setType(IntegralRecordConstants.INTEGRAL_RECORD_TYPE_SUB);
        integralRecord.setTitle(IntegralRecordConstants.BROKERAGE_RECORD_TITLE_ORDER);
        integralRecord.setIntegral(new BigDecimal(storeOrder.getUseIntegral()));
        integralRecord.setBalance(user.getIntegral());
        integralRecord.setMark(StrUtil.format("订单支付抵扣{}积分购买商品", storeOrder.getUseIntegral()));
        integralRecord.setStatus(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE);
        return integralRecord;
    }

    /**
     * 积分添加记录
     * @return UserIntegralRecord
     */
    private UserIntegralRecord integralRecordInit(StoreOrder storeOrder, BigDecimal balance, BigDecimal integral, String type) {
        UserIntegralRecord integralRecord = new UserIntegralRecord();
        integralRecord.setUid(storeOrder.getUid());
        integralRecord.setLinkId(storeOrder.getOrderId());
        integralRecord.setLinkType(IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_ORDER);
        integralRecord.setType(IntegralRecordConstants.INTEGRAL_RECORD_TYPE_ADD);
        integralRecord.setTitle(IntegralRecordConstants.BROKERAGE_RECORD_TITLE_ORDER);
        integralRecord.setIntegral(integral);
        integralRecord.setBalance(balance);
        if (type.equals("order")){
            integralRecord.setMark(StrUtil.format("用户付款成功,订单增加{}积分", integral));
        }
        if (type.equals("product")) {
            integralRecord.setMark(StrUtil.format("用户付款成功,商品增加{}积分", integral));
        }
        integralRecord.setStatus(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_CREATE);

        // 获取积分冻结期
        String fronzenTime = systemConfigService.getValueByKey(Constants.CONFIG_KEY_STORE_INTEGRAL_EXTRACT_TIME);
        integralRecord.setFrozenTime(Integer.valueOf(Optional.ofNullable(fronzenTime).orElse("0")));
        integralRecord.setCreateTime(DateUtil.nowDateTime());
        return integralRecord;
    }

    /**
     * 发送消息通知
     * 根据用户类型发送
     * 公众号模板消息
     * 小程序订阅消息
     */
    private void pushMessageOrder(StoreOrder storeOrder, User user) {
        if (storeOrder.getIsChannel().equals(2)) {// H5
            return;
        }
        UserToken userToken;
        HashMap<String, String> temMap = new HashMap<>();
        if (!storeOrder.getPayType().equals(PayConstants.PAY_TYPE_WE_CHAT)) {
            return;
        }
        // 公众号
        if (storeOrder.getIsChannel().equals(PayConstants.ORDER_PAY_CHANNEL_PUBLIC)) {
            userToken = userTokenService.getTokenByUserId(user.getUid(), UserConstants.USER_TOKEN_TYPE_WECHAT);
            if (ObjectUtil.isNull(userToken)) {
                return ;
            }
            // 发送微信模板消息
            temMap.put(Constants.WE_CHAT_TEMP_KEY_FIRST, "您的订单已支付成功！");
            temMap.put("keyword1", storeOrder.getOrderId());
            temMap.put("keyword2", storeOrder.getPayPrice().toString());
            temMap.put(Constants.WE_CHAT_TEMP_KEY_END, "欢迎下次再来！");
            templateMessageService.pushTemplateMessage(Constants.WE_CHAT_TEMP_KEY_ORDER_PAY, temMap, userToken.getToken());
            return;
        }
        if (storeOrder.getIsChannel().equals(PayConstants.ORDER_PAY_CHANNEL_PROGRAM)) {
            // 小程序发送订阅消息
            userToken = userTokenService.getTokenByUserId(user.getUid(), UserConstants.USER_TOKEN_TYPE_ROUTINE);
            if (ObjectUtil.isNull(userToken)) {
                return ;
            }
            // 组装数据
            temMap.put("character_string1", storeOrder.getOrderId());
            temMap.put("amount2", storeOrder.getPayPrice().toString() + "元");
            temMap.put("thing7", "您的订单已支付成功");
            templateMessageService.pushMiniTemplateMessage(Constants.WE_CHAT_PROGRAM_TEMP_KEY_ORDER_PAY, temMap, userToken.getToken());
        }
    }

    /**
     * 商品购买后根据配置送券
     */
    private void autoSendCoupons(StoreOrder storeOrder){
        // 根据订单详情获取商品信息
        List<StoreOrderInfoOldVo> orders = storeOrderInfoService.getOrderListByOrderId(storeOrder.getId());
        if(null == orders){
            return;
        }
        List<StoreCouponUser> couponUserList = CollUtil.newArrayList();
        Map<Integer, Boolean> couponMap = CollUtil.newHashMap();
        for (StoreOrderInfoOldVo order : orders) {
            List<StoreProductCoupon> couponsForGiveUser = storeProductCouponService.getListByProductId(order.getProductId());
            for (int i = 0; i < couponsForGiveUser.size();) {
                StoreProductCoupon storeProductCoupon = couponsForGiveUser.get(i);
                MyRecord record = storeCouponUserService.paySuccessGiveAway(storeProductCoupon.getIssueCouponId(), storeOrder.getUid());
                if (record.getStr("status").equals("fail")) {
                    logger.error(StrUtil.format("支付成功领取优惠券失败，失败原因：{}", record.getStr("errMsg")));
                    couponsForGiveUser.remove(i);
                    continue;
                }

                StoreCouponUser storeCouponUser = record.get("storeCouponUser");
                couponUserList.add(storeCouponUser);
                couponMap.put(storeCouponUser.getCouponId(), record.getBoolean("isLimited"));
                i++;
            }
        }

        Boolean execute = transactionTemplate.execute(e -> {
            if (CollUtil.isNotEmpty(couponUserList)) {
                storeCouponUserService.saveBatch(couponUserList);
                couponUserList.forEach(i -> storeCouponService.deduction(i.getCouponId(), 1, couponMap.get(i.getCouponId())));
            }
            return Boolean.TRUE;
        });
        if (!execute) {
            logger.error(StrUtil.format("支付成功领取优惠券，更新数据库失败，订单编号：{}", storeOrder.getOrderId()));
        }
    }
}
