package com.zbkj.crmeb.payment.wechat.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.common.MyRecord;
import com.constants.Constants;
import com.constants.PayConstants;
import com.constants.SysConfigConstants;
import com.constants.WeChatConstants;
import com.exception.CrmebException;
import com.utils.*;
import com.zbkj.crmeb.combination.model.StoreCombination;
import com.zbkj.crmeb.combination.model.StorePink;
import com.zbkj.crmeb.combination.service.StoreCombinationService;
import com.zbkj.crmeb.combination.service.StorePinkService;
import com.zbkj.crmeb.finance.model.UserRecharge;
import com.zbkj.crmeb.finance.service.UserRechargeService;
import com.zbkj.crmeb.payment.service.RechargePayService;
import com.zbkj.crmeb.payment.vo.wechat.*;
import com.zbkj.crmeb.payment.wechat.WeChatPayService;
import com.zbkj.crmeb.store.model.StoreOrder;
import com.zbkj.crmeb.store.service.StoreOrderInfoService;
import com.zbkj.crmeb.store.service.StoreOrderService;
import com.zbkj.crmeb.system.service.SystemConfigService;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.model.UserToken;
import com.zbkj.crmeb.user.service.UserService;
import com.zbkj.crmeb.user.service.UserTokenService;
import com.zbkj.crmeb.wechat.service.WeChatService;
import lombok.Data;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


/**
 * ????????????
 * @author: ??????
 * @CreateDate: 2022/1/19 15:00
 */
@Data
@Service
public class WeChatPayServiceImpl implements WeChatPayService {

    private static final Logger logger = LoggerFactory.getLogger(WeChatPayServiceImpl.class);

    @Autowired
    private RestTemplateUtil restTemplateUtil;

    @Autowired
    private WeChatService weChatService;

    @Autowired
    private UserTokenService userTokenService;

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private StoreOrderInfoService storeOrderInfoService;

    @Autowired
    private SystemConfigService systemConfigService;

    private String signKey;

    private PayParamsVo payParamsVo;

    private CreateOrderRequestVo createOrderRequestVo;

    private CreateOrderResponseVo createOrderResponseVo = null;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRechargeService userRechargeService;

    @Autowired
    private RechargePayService rechargePayService;

    @Autowired
    private StoreCombinationService storeCombinationService;

    @Autowired
    private StorePinkService storePinkService;

    @Override
    public CreateOrderResponseVo create(PayParamsVo payParamsVo){
        try {
            setPayParamsVo(payParamsVo);
            switch (payParamsVo.getFromType()){
                case PayConstants.PAY_CHANNEL_WE_CHAT_H5: //h5
                    setH5PayConfig();
                    break;
                case PayConstants.PAY_CHANNEL_WE_CHAT_PUBLIC: //?????????
                    setPublicPayConfig();
                    break;
                case PayConstants.PAY_CHANNEL_WE_CHAT_PROGRAM: //?????????
                    setProgramPayConfig();
                    break;
                default:
                    throw new CrmebException("????????????????????????????????????");
            }
            createOrderWeChatData();
            response();
            return getCreateOrderResponseVo();
        }catch (Exception e){
            e.printStackTrace();
            throw new CrmebException(e.getMessage());
        }
    }

    /**
     * ?????????????????????????????????
     * @author Mr.Zhang
     * @since 2020-06-22
     */
    private void response(){
        try{
            String url = WeChatConstants.PAY_API_URL + WeChatConstants.PAY_API_URI;
            String request = XmlUtil.objectToXml(getCreateOrderRequestVo());
            String xml = restTemplateUtil.postXml(url, request);
            HashMap<String, Object> map = XmlUtil.xmlToMap(xml);
            if(null == map){
                throw new CrmebException("?????????????????????");
            }
            CreateOrderResponseVo responseVo = CrmebUtil.mapToObj(map, CreateOrderResponseVo.class);
            if(responseVo.getReturnCode().toUpperCase().equals("FAIL")){
                throw new CrmebException("??????????????????1???" +  responseVo.getReturnMsg());
            }

            if(responseVo.getResultCode().toUpperCase().equals("FAIL")){
                throw new CrmebException("??????????????????2???" + responseVo.getErrCodeDes());
            }
            responseVo.setExtra(getCreateOrderRequestVo().getScene_info());
            setCreateOrderResponseVo(responseVo);
        }catch (Exception e){
            e.printStackTrace();
            throw new CrmebException(e.getMessage());
        }
    }


    /**
     * ??????H5????????????
     * @author Mr.Zhang
     * @since 2020-06-22
     */
    private void setH5PayConfig() {
        setCreateOrderRequestVo(new CreateOrderRequestVo());
        String appId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_ID);
        String mchId = systemConfigService.getValueByKey(Constants.CONFIG_KEY_PAY_WE_CHAT_MCH_ID);
        setSignKey(systemConfigService.getValueByKey(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_KEY));
        getCreateOrderRequestVo().setAppid(appId);
        getCreateOrderRequestVo().setMch_id(mchId);
    }

    /**
     * ??????H5????????????
     * @author Mr.Zhang
     * @since 2020-06-22
     */
    private void setPublicPayConfig() {
        setCreateOrderRequestVo(new CreateOrderRequestVo());
        String appId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_ID);
        String mchId = systemConfigService.getValueByKey(Constants.CONFIG_KEY_PAY_WE_CHAT_MCH_ID);
        setSignKey(systemConfigService.getValueByKey(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_KEY));
        getCreateOrderRequestVo().setAppid(appId);
        getCreateOrderRequestVo().setMch_id(mchId);
        getCreateOrderRequestVo().setTrade_type(WeChatConstants.PAY_TYPE_JS);
        getCreateOrderRequestVo().setOpenid(getOpenId(Constants.THIRD_LOGIN_TOKEN_TYPE_PUBLIC));
    }

    /**
     * ??????JS????????????
     * @author Mr.Zhang
     * @since 2020-06-22
     */
    private void setProgramPayConfig() {
        setCreateOrderRequestVo(new CreateOrderRequestVo());
        String appId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_ROUTINE_APP_ID);
        String mchId = systemConfigService.getValueByKey(Constants.CONFIG_KEY_PAY_ROUTINE_MCH_ID);
        setSignKey(systemConfigService.getValueByKey(Constants.CONFIG_KEY_PAY_ROUTINE_APP_KEY));
        getCreateOrderRequestVo().setAppid(appId);
        getCreateOrderRequestVo().setMch_id(mchId);
        getCreateOrderRequestVo().setTrade_type(WeChatConstants.PAY_TYPE_JS);
        getCreateOrderRequestVo().setOpenid(getOpenId(Constants.THIRD_LOGIN_TOKEN_TYPE_PROGRAM));
    }

    /**
     * ????????????????????????
     * @author Mr.Zhang
     * @since 2020-06-22
     */
    private void createOrderWeChatData(){
        //????????????
        String domain = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_KEY_SITE_URL);
        String apiDomain = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_KEY_API_URL);
        //??????????????????
        getCreateOrderRequestVo().setNotify_url(apiDomain + WeChatConstants.PAY_NOTIFY_API_URI_WECHAT);
        getCreateOrderRequestVo().setNonce_str(DigestUtils.md5Hex(CrmebUtil.getUuid() + CrmebUtil.randomCount(111111, 666666)));
        getCreateOrderRequestVo().setBody(getPayParamsVo().getTitle());
        getCreateOrderRequestVo().setOut_trade_no(getPayParamsVo().getOrderNo());

        //????????????
        getCreateOrderRequestVo().setAttach(JSONObject.toJSONString(getPayParamsVo().getAttach()));

        getCreateOrderRequestVo().setTotal_fee(getWeChatPrice());
        getCreateOrderRequestVo().setSpbill_create_ip(getPayParamsVo().getClientIp());
        CreateOrderH5SceneInfoVo createOrderH5SceneInfoVo = new CreateOrderH5SceneInfoVo(
                new CreateOrderH5SceneInfoDetailVo(
                        domain,
                        systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_KEY_SITE_NAME)
                )
        );
        getCreateOrderRequestVo().setScene_info(JSONObject.toJSONString(createOrderH5SceneInfoVo));
        getCreateOrderRequestVo().setSign(CrmebUtil.getSign(CrmebUtil.objectToMap(getCreateOrderRequestVo()), getSignKey()));
    }

    /**
     * ????????????????????????????????????????????????????????????100
     * @author Mr.Zhang
     * @since 2020-06-22
     * @return String
     */
    private Integer getWeChatPrice(){
        return getPayParamsVo().getPrice().multiply(BigDecimal.TEN).multiply(BigDecimal.TEN).intValue();
    }

    /**
     * ????????????????????????
     * @author Mr.Zhang
     * @since 2020-06-22
     * @return PayCreateOrderSceneInfoVo
     */
    private String getOpenId(int type) {
        UserToken userToken = userTokenService.getTokenByUserId(getPayParamsVo().getUserId(), type);
        return userToken.getToken();
    }

    /**
     * ???????????????
     * @param storeOrder ??????
     * @param ip      ip
     * @return
     */
    @Override
    public Map<String, String> unifiedorder(StoreOrder storeOrder, String ip) {
        //????????????
        if (ObjectUtil.isNull(storeOrder)) {
            throw new CrmebException("???????????????");
        }
        if (storeOrder.getIsDel()) {
            throw new CrmebException("??????????????????");
        }
        if (storeOrder.getPaid()) {
            throw new CrmebException("???????????????");
        }
        if (!storeOrder.getPayType().equals(PayConstants.PAY_TYPE_WE_CHAT)) {
            throw new CrmebException("????????????????????????????????????????????????????????????");
        }

        // ????????????openId
        // ????????????????????????????????????????????????openId???????????????openId
        UserToken userToken = new UserToken();
        if (storeOrder.getIsChannel() == 0) {// ?????????
            userToken = userTokenService.getTokenByUserId(storeOrder.getUid(), 1);
        }
        if (storeOrder.getIsChannel() == 1) {// ?????????
            userToken = userTokenService.getTokenByUserId(storeOrder.getUid(), 2);
        }
        if (storeOrder.getIsChannel() == 2 ||
                storeOrder.getIsChannel() == 4 ||
                storeOrder.getIsChannel() == 5) {// H5?????????AppIos?????????App??????
            userToken.setToken("");
        }

        //??????-openid
        if (ObjectUtil.isNull(userToken)) {
            throw new CrmebException("???????????????openId");
        }

        // ??????appid???mch_id
        // ????????????key
        String appId = "";
        String mchId = "";
        String signKey = "";
        if (storeOrder.getIsChannel() == 0) {// ?????????
            appId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_ID);
            mchId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_MCH_ID);
            signKey = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_KEY);
        }
        if (storeOrder.getIsChannel() == 1) {// ?????????
            appId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_ROUTINE_APP_ID);
            mchId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_ROUTINE_MCH_ID);
            signKey = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_ROUTINE_APP_KEY);
        }
        if (storeOrder.getIsChannel() == 2) {// H5,??????????????????
            appId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_ID);
            mchId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_MCH_ID);
            signKey = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_KEY);
        }
        if (storeOrder.getIsChannel() == 4 || storeOrder.getIsChannel() == 5) {// ??????AppIo ??? ??????App??????
            appId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_APP_ID);
            mchId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_MCH_ID);
            signKey = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_APP_KEY);
        }

        // ???????????????????????????
        CreateOrderRequestVo unifiedorderVo = getUnifiedorderVo(storeOrder, userToken.getToken(), ip, appId, mchId, signKey);
        // ?????????
        CreateOrderResponseVo responseVo = unifiedOrder(unifiedorderVo); // ???????????????

        // ???????????????????????????
        Map<String, String> map = new HashMap<>();
        map.put("appId", unifiedorderVo.getAppid());
        map.put("nonceStr", unifiedorderVo.getAppid());
        map.put("package", "prepay_id=".concat(responseVo.getPrepayId()));
        map.put("signType", unifiedorderVo.getSign_type());
        Long currentTimestamp = WxPayUtil.getCurrentTimestamp();
        map.put("timeStamp", Long.toString(currentTimestamp));
        String paySign = WxPayUtil.getSign(map, signKey);
        map.put("paySign", paySign);
        map.put("prepayId", responseVo.getPrepayId());
        map.put("prepayTime", DateUtil.nowDateTimeStr());

        //??????-H5????????????
        if (storeOrder.getIsChannel() == 2) {
            map.put("mweb_url", responseVo.getMWebUrl());
        }

        //??????-app????????????
        if (storeOrder.getIsChannel() == 4 || storeOrder.getIsChannel() == 5) {// App
            map.put("partnerid", mchId);
            map.put("package", responseVo.getPrepayId());
            Map<String, Object> appMap = new HashMap<>();
            appMap.put("appid", unifiedorderVo.getAppid());
            appMap.put("partnerid", mchId);
            appMap.put("prepayid", responseVo.getPrepayId());
            appMap.put("package", "Sign=WXPay");
            appMap.put("noncestr", unifiedorderVo.getAppid());
            appMap.put("timestamp", currentTimestamp);
            logger.info("================================================app???????????????map = " + appMap);
            String sign = WxPayUtil.getSignObject(appMap, signKey);
            logger.info("================================================app???????????????sign = " + sign);
            map.put("paySign", sign);
        }
        return map;
    }

    public static void main(String[] args) {
        String signKey = "cd94c0b5fe5ab2d9940bee9cae8391f0";
        Map<String, String> appMap = new HashMap<>();
        appMap.put("appid", "wxa83d6fab40cab13f");
        appMap.put("partnerid", "1519485721");
        appMap.put("prepayid", "wx23155011418859d4aa5802ca703bd80000");
        appMap.put("package", "Sign=WXPay");
        appMap.put("noncestr", "wxa83d6fab40cab13f");
        appMap.put("timestamp", "1616485811");
        logger.info("================================================app???????????????map = " + appMap);
        String sign = WxPayUtil.getSign(appMap, signKey);
        logger.info("================================================app???????????????sign = " + sign);
    }

    /**
     * ??????????????????
     * @param orderNo ????????????
     * @return
     */
    @Override
    public Boolean queryPayResult(String orderNo) {
        logger.info("*********??????????????????********");

        //??????-?????????
        if (StrUtil.isBlank(orderNo)) {
            throw new CrmebException("????????????????????????");
        }

        // ?????????????????????????????????????????????????????????
        String pre = StrUtil.subPre(orderNo, 5);
        logger.info("*********???????????????????????????????????????********"+pre);
        if (pre.equals("order")) {// ????????????
            //??????-????????????
            StoreOrder storeOrder = storeOrderService.getByOderId(orderNo);

            //??????-??????
            if (ObjectUtil.isNull(storeOrder)) {
                throw new CrmebException("???????????????");
            }

            //??????-????????????
            if (storeOrder.getIsDel()) {
                throw new CrmebException("??????????????????");
            }

            //??????-????????????
            if (storeOrder.getPaid()) {
                return Boolean.TRUE;
            }

            //??????????????????
            logger.info("*********??????????????????********"+storeOrder.getPayType());
            switch (storeOrder.getPayType()){
                case PayConstants.PAY_TYPE_WE_CHAT:
                    // ??????appid???mch_id
                    // ????????????key
                    String appId = "";
                    String mchId = "";
                    String signKey = "";
                    if (storeOrder.getIsChannel() == 0) {// ?????????
                        appId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_ID);
                        mchId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_MCH_ID);
                        signKey = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_KEY);
                    }
                    if (storeOrder.getIsChannel() == 1) {// ?????????
                        logger.info("*********?????????********"+storeOrder.getPayType());
                        appId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_ROUTINE_APP_ID);
                        mchId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_ROUTINE_MCH_ID);
                        signKey = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_ROUTINE_APP_KEY);
                    }
                    if (storeOrder.getIsChannel() == 2) {// H5
                        appId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_ID);
                        mchId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_MCH_ID);
                        signKey = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_KEY);
                    }

                    // ????????????????????????
                    Map<String, String> payVo = getWxChantQueryPayVo(orderNo, appId, mchId, signKey);
                    logger.info("*********????????????????????????********");
                    System.out.println(payVo);
                    // ??????????????????
                    MyRecord record = orderPayQuery(payVo);
                    logger.info("*********??????????????????********");
                    System.out.println(record);
                    break;
                case PayConstants.PAY_TYPE_ALI_PAY:
                    //????????????
                    String appid = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_ZFB_APP_ZF_APPID);
                    String skey = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_ZFB_APP_ZF_SECRET_KEY);
                    String publicKey = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_ZFB_APP_ZF_PUBLIC_KEY);

                    try{
                        //??????-?????????????????????
                        AlipayConfig alipayConfig = new AlipayConfig();
                        alipayConfig.setServerUrl("https://openapi.alipay.com/gateway.do");
                        alipayConfig.setAppId(appid);
                        alipayConfig.setPrivateKey(skey);
                        alipayConfig.setFormat("json");
                        alipayConfig.setAlipayPublicKey(publicKey);
                        alipayConfig.setCharset("UTF8");
                        alipayConfig.setSignType("RSA2");
                        AlipayClient alipayClient = new DefaultAlipayClient(alipayConfig);
                        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
                        AlipayTradeQueryModel model = new AlipayTradeQueryModel();
                        model.setTradeNo(null);//??????????????????
                        model.setOutTradeNo(storeOrder.getOrderId());//???????????????
                        request.setBizModel(model);

                        //????????????
                        AlipayTradeQueryResponse response = alipayClient.execute(request);
                        if(response.isSuccess()){
                            System.out.println("????????????");
                            //??????-????????????
                            if(!"TRADE_SUCCESS".equals(response.getTradeStatus())){
                                throw new CrmebException("??????????????????");
                            }
                        } else {
                            System.out.println("????????????");
                            throw new CrmebException(response.getSubMsg());
                        }
                    }catch (Exception e){
                        throw new CrmebException("????????????????????????????????????"+e.getMessage());
                    }
                    break;
                default:
                    throw new CrmebException("????????????????????????????????????????????????????????????????????????");
            }

            //??????-??????????????????
            logger.info("*********??????-??????????????????********");
            User user = userService.getById(storeOrder.getUid());
            if (ObjectUtil.isNull(user)) throw new CrmebException("???????????????");

            //????????????
            logger.info("*********????????????********");
            Boolean updatePaid = transactionTemplate.execute(e -> {
                //??????-?????????????????????true
                storeOrderService.updatePaid(orderNo);

                //??????????????????-??????????????????
                if(storeOrder.getShippingType().equals(3)){
                    storeOrder.setStatus(3);
                    storeOrderService.updateById(storeOrder);
                }

                // ????????????
                if (storeOrder.getCombinationId() > 0) {
                    // ??????????????????????????????
                    StorePink headPink = new StorePink();
                    Integer pinkId = storeOrder.getPinkId();
                    if (pinkId > 0) {
                        headPink = storePinkService.getById(pinkId);
                        if (ObjectUtil.isNull(headPink) || headPink.getIsRefund().equals(true) || headPink.getStatus() == 3) {
                            pinkId = 0;
                        }
                    }
                    StoreCombination storeCombination = storeCombinationService.getById(storeOrder.getCombinationId());
                    // ???????????????????????????????????????
                    if (pinkId > 0) {
                        Integer count = storePinkService.getCountByKid(pinkId);
                        if (count >= storeCombination.getPeople()) {
                            pinkId = 0;
                        }
                    }
                    // ?????????????????????
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
                    Integer effectiveTime = storeCombination.getEffectiveTime();// ???????????????
                    DateTime dateTime = cn.hutool.core.date.DateUtil.date();
                    storePink.setAddTime(dateTime.getTime());
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
                    storePink.setKId(pinkId);
                    storePink.setIsTpl(false);
                    storePink.setIsRefund(false);
                    storePink.setStatus(1);
                    storePinkService.save(storePink);

                    // ??????????????????????????????????????????
                    storeOrder.setPinkId(storePink.getId());
                    storeOrderService.updateById(storeOrder);
                }
                return Boolean.TRUE;
            });

            //????????????????????????
            logger.info("*********????????????????????????********"+updatePaid);
            if (!updatePaid) {
                throw new CrmebException("??????????????????????????????");
            }

            // ??????????????????task
            logger.info("*********??????????????????task********"+updatePaid);
            redisUtil.lPush(Constants.ORDER_TASK_PAY_SUCCESS_AFTER, storeOrder.getId()); // ??????????????????
            return Boolean.TRUE;
        }else{
            // ????????????
            UserRecharge userRecharge = new UserRecharge();
            userRecharge.setOrderId(orderNo);
            userRecharge = userRechargeService.getInfoByEntity(userRecharge);
            if(ObjectUtil.isNull(userRecharge)){
                throw new CrmebException("????????????????????????");
            }
            if(userRecharge.getPaid()){
                return Boolean.TRUE;
            }

            // ????????????
            // ??????appid???mch_id
            // ????????????key
            String appId = "";
            String mchId = "";
            String signKey = "";
            if (userRecharge.getRechargeType().equals("public")) {// ?????????
                appId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_ID);
                mchId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_MCH_ID);
                signKey = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_KEY);
            }
            if (userRecharge.getRechargeType().equals("routine")) {// ?????????
                appId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_ROUTINE_APP_ID);
                mchId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_ROUTINE_MCH_ID);
                signKey = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_ROUTINE_APP_KEY);
            }
            // ????????????????????????
            Map<String, String> payVo = getWxChantQueryPayVo(orderNo, appId, mchId, signKey);
            // ??????????????????
            MyRecord record = orderPayQuery(payVo);
            // ??????????????????
            Boolean rechargePayAfter = rechargePayService.paySuccess(userRecharge);
            if (!rechargePayAfter) {
                throw new CrmebException("wechat pay error : ??????????????????==???" + orderNo);
            }
            return rechargePayAfter;
        }
    }

    /**
     * ???????????????????????????
     * @param userRecharge ????????????
     * @param clientIp      ip
     * @return
     */
    @Override
    public Map<String, String> unifiedRecharge(UserRecharge userRecharge, String clientIp) {
        if (ObjectUtil.isNull(userRecharge)) {
            throw new CrmebException("???????????????");
        }
        // ????????????openId
        // ????????????????????????????????????????????????openId???????????????openId
        UserToken userToken = new UserToken();
        if (userRecharge.getRechargeType().equals(PayConstants.PAY_CHANNEL_WE_CHAT_PUBLIC)) {// ?????????
            userToken = userTokenService.getTokenByUserId(userRecharge.getUid(), 1);
        }
        if (userRecharge.getRechargeType().equals(PayConstants.PAY_CHANNEL_WE_CHAT_PROGRAM)) {// ?????????
            userToken = userTokenService.getTokenByUserId(userRecharge.getUid(), 2);
        }
        if (userRecharge.getRechargeType().equals(PayConstants.PAY_CHANNEL_WE_CHAT_H5)) {// H5
            userToken.setToken("");
        }
        if (userRecharge.getRechargeType().equals(PayConstants.PAY_CHANNEL_WE_CHAT_APP_IOS)) {// app ios
            userToken = userTokenService.getTokenByUserId(userRecharge.getUid(), 5);
        }
        if (userRecharge.getRechargeType().equals(PayConstants.PAY_CHANNEL_WE_CHAT_APP_ANDROID)) {// app android
            userToken = userTokenService.getTokenByUserId(userRecharge.getUid(), 6);
        }

        if (ObjectUtil.isNull(userToken)) {
            throw new CrmebException("???????????????openId");
        }

        // ??????appid???mch_id
        // ????????????key
        String appId = "";
        String mchId = "";
        String signKey = "";
        if (userRecharge.getRechargeType().equals(PayConstants.PAY_CHANNEL_WE_CHAT_PUBLIC)) {// ?????????
            appId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_ID);
            mchId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_MCH_ID);
            signKey = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_KEY);
        }
        if (userRecharge.getRechargeType().equals(PayConstants.PAY_CHANNEL_WE_CHAT_PROGRAM)) {// ?????????
            appId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_ROUTINE_APP_ID);
            mchId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_ROUTINE_MCH_ID);
            signKey = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_ROUTINE_APP_KEY);
        }
        if (userRecharge.getRechargeType().equals(PayConstants.PAY_CHANNEL_WE_CHAT_H5)) {// H5,??????????????????
            appId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_ID);
            mchId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_MCH_ID);
            signKey = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_KEY);
        }
        if (userRecharge.getRechargeType().equals(PayConstants.PAY_CHANNEL_WE_CHAT_APP_IOS) || userRecharge.getRechargeType().equals(PayConstants.PAY_CHANNEL_WE_CHAT_APP_ANDROID)) {// H5,??????????????????
            appId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_APP_ID);
            mchId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_MCH_ID);
            signKey = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_APP_KEY);
        }

        // ???????????????????????????
        CreateOrderRequestVo unifiedorderVo = getUnifiedorderVo(userRecharge, userToken.getToken(), clientIp, appId, mchId, signKey);
        // ?????????
        CreateOrderResponseVo responseVo = unifiedOrder(unifiedorderVo);

        // ???????????????????????????
        Map<String, String> map = new HashMap<>();
        map.put("appId", unifiedorderVo.getAppid());
        map.put("nonceStr", unifiedorderVo.getNonce_str());
        map.put("package", "prepay_id=".concat(responseVo.getPrepayId()));
        map.put("signType", unifiedorderVo.getSign_type());
        Long currentTimestamp = WxPayUtil.getCurrentTimestamp();
        map.put("timeStamp", Long.toString(currentTimestamp));
        String paySign = WxPayUtil.getSign(map, signKey);
        map.put("paySign", paySign);
        if (userRecharge.getRechargeType().equals(PayConstants.PAY_CHANNEL_WE_CHAT_H5)) {
            map.put("mweb_url", responseVo.getMWebUrl());
        }
        if (userRecharge.getRechargeType().equals(PayConstants.PAY_CHANNEL_WE_CHAT_APP_IOS) || userRecharge.getRechargeType().equals(PayConstants.PAY_CHANNEL_WE_CHAT_APP_ANDROID)) {// H5,??????????????????
            map.put("partnerid", mchId);
            map.put("package", responseVo.getPrepayId());
            Map<String, Object> appMap = new HashMap<>();
            appMap.put("appid", unifiedorderVo.getAppid());
            appMap.put("partnerid", mchId);
            appMap.put("prepayid", responseVo.getPrepayId());
            appMap.put("package", "Sign=WXPay");
            appMap.put("noncestr", unifiedorderVo.getNonce_str());
            appMap.put("timestamp", currentTimestamp);
            logger.info("================================================app???????????????map = " + appMap);
            String sign = WxPayUtil.getSignObject(appMap, signKey);
            logger.info("================================================app???????????????sign = " + sign);
            map.put("paySign", sign);
        }
        return map;
    }

    private MyRecord orderPayQuery(Map<String, String> payVo) {
        String url = PayConstants.WX_PAY_API_URL + PayConstants.WX_PAY_ORDER_QUERY_API_URI;
        try {
            String request = XmlUtil.mapToXml(payVo);
            String xml = restTemplateUtil.postXml(url, request);
            HashMap<String, Object> map = XmlUtil.xmlToMap(xml);
            MyRecord record = new MyRecord();
            if(null == map){
                throw new CrmebException("???????????????????????????");
            }
            record.setColums(map);
            if (record.getStr("return_code").toUpperCase().equals("FAIL")){
                throw new CrmebException("????????????????????????1???" +  record.getStr("return_msg"));
            }

            if (record.getStr("result_code").toUpperCase().equals("FAIL")){
                throw new CrmebException("????????????????????????2???" + record.getStr("err_code") + record.getStr("err_code_des"));
            }
            if (!record.getStr("trade_state").toUpperCase().equals("SUCCESS")){
                throw new CrmebException("???????????????????????????" + record.getStr("trade_state"));
            }
            return record;
        } catch (Exception e) {
            e.printStackTrace();
            throw new CrmebException("??????????????????mapToXml??????===???" + e.getMessage());
        }
    }

    /**
     * ??????????????????????????????
     * @return
     */
    private Map<String, String> getWxChantQueryPayVo(String orderNo, String appId, String mchId, String signKey) {
        Map<String, String> map = CollUtil.newHashMap();
        map.put("appid", appId);
        map.put("mch_id", mchId);
        map.put("out_trade_no", orderNo);
        map.put("nonce_str", WxPayUtil.getNonceStr());
        map.put("sign_type", PayConstants.WX_PAY_SIGN_TYPE_MD5);
        map.put("sign", WxPayUtil.getSign(map, signKey));
        return map;
    }

    /**
     * ???????????????????????????
     * @return
     */
    private CreateOrderRequestVo getUnifiedorderVo(StoreOrder storeOrder, String openid, String ip, String appId, String mchId, String signKey) {
        // ????????????
        String domain = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_KEY_SITE_URL);
        String apiDomain = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_KEY_API_URL);

        AttachVo attachVo = new AttachVo(Constants.SERVICE_PAY_TYPE_ORDER, storeOrder.getUid());
        CreateOrderRequestVo vo = new CreateOrderRequestVo();
        vo.setAppid(appId);
        vo.setMch_id(mchId);
        //vo.setNonce_str(WxPayUtil.getNonceStr());
        StringBuffer noneceStr=new StringBuffer()
                .append(storeOrder.getId())
                .append(storeOrder.getUid())
                .append(DateUtil.dateToStr(storeOrder.getCreateTime(),Constants.DATE_TIME_FORMAT_NUM));
        logger.info("noneceStr="+noneceStr);
        vo.setNonce_str(noneceStr.toString());
        vo.setSign_type(PayConstants.WX_PAY_SIGN_TYPE_MD5);
        //vo.setBody(PayConstants.PAY_BODY);
        vo.setBody(PayConstants.PAY_BODY_QJY);
        vo.setAttach(JSONObject.toJSONString(attachVo));
        vo.setOut_trade_no(storeOrder.getOrderId());
        // ?????????????????????BigDecimal,???????????????Integer??????
        vo.setTotal_fee(storeOrder.getPayPrice().multiply(BigDecimal.TEN).multiply(BigDecimal.TEN).intValue());
        vo.setSpbill_create_ip(ip);
        vo.setNotify_url(apiDomain + PayConstants.WX_PAY_NOTIFY_API_URI);
        vo.setTrade_type(PayConstants.WX_PAY_TRADE_TYPE_JS);
        vo.setOpenid(openid);

        //??????????????????
        if(storeOrder.getType() != Constants.ORDER_TYPE_2){
            vo.setProfit_sharing("Y");
        }else{
            vo.setProfit_sharing("N");
        }

        //??????????????????-H5??????
        if (storeOrder.getIsChannel() == 2){// H5
            vo.setTrade_type(PayConstants.WX_PAY_TRADE_TYPE_H5);
            vo.setOpenid(null);
        }

        //??????????????????-ios???Android
        if (storeOrder.getIsChannel() == 4|| storeOrder.getIsChannel() == 5) {
            vo.setTrade_type(PayConstants.WX_PAY_TRADE_TYPE_APP);
            vo.setOpenid(null);
        }

        //????????????
        CreateOrderH5SceneInfoVo createOrderH5SceneInfoVo = new CreateOrderH5SceneInfoVo(
                new CreateOrderH5SceneInfoDetailVo(
                        domain,
                        systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_KEY_SITE_NAME)
                )
        );
        vo.setScene_info(JSONObject.toJSONString(createOrderH5SceneInfoVo));

        //????????????
        String sign = WxPayUtil.getSign(vo, signKey);
        vo.setSign(sign);
        return vo;
    }

    /**
     * ???????????????????????????
     * @return
     */
    private CreateOrderRequestVo getUnifiedorderVo(UserRecharge userRecharge, String openid, String ip, String appId, String mchId, String signKey) {
        // ????????????
        String domain = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_KEY_SITE_URL);
        String apiDomain = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_KEY_API_URL);

        AttachVo attachVo = new AttachVo(Constants.SERVICE_PAY_TYPE_RECHARGE, userRecharge.getUid());
        CreateOrderRequestVo vo = new CreateOrderRequestVo();

        vo.setAppid(appId);
        vo.setMch_id(mchId);
        vo.setNonce_str(WxPayUtil.getNonceStr());
        vo.setSign_type(PayConstants.WX_PAY_SIGN_TYPE_MD5);
        //vo.setBody(PayConstants.PAY_BODY);
        vo.setBody(PayConstants.PAY_BODY_QJY);
        vo.setAttach(JSONObject.toJSONString(attachVo));
        vo.setOut_trade_no(userRecharge.getOrderId());

        // ?????????????????????BigDecimal,???????????????Integer??????
        vo.setTotal_fee(userRecharge.getPrice().multiply(BigDecimal.TEN).multiply(BigDecimal.TEN).intValue());
        vo.setSpbill_create_ip(ip);
        vo.setNotify_url(apiDomain + PayConstants.WX_PAY_NOTIFY_API_URI);
        vo.setTrade_type(PayConstants.WX_PAY_TRADE_TYPE_JS);
        vo.setOpenid(openid);

        //??????????????????-H5??????
        if (userRecharge.getRechargeType().equals(PayConstants.PAY_CHANNEL_WE_CHAT_H5)){// H5
            vo.setTrade_type(PayConstants.WX_PAY_TRADE_TYPE_H5);
            vo.setOpenid(null);
        }

        //??????????????????-ios???Android
        if (userRecharge.getRechargeType().equals(PayConstants.PAY_CHANNEL_WE_CHAT_APP_IOS) || userRecharge.getRechargeType().equals(PayConstants.PAY_CHANNEL_WE_CHAT_APP_ANDROID)) {
            vo.setTrade_type(PayConstants.WX_PAY_TRADE_TYPE_APP);
            vo.setOpenid(null);
        }

        //???????????????????????????
        CreateOrderH5SceneInfoVo createOrderH5SceneInfoVo = new CreateOrderH5SceneInfoVo(
                new CreateOrderH5SceneInfoDetailVo(
                        domain,
                        systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_KEY_SITE_NAME)
                )
        );
        vo.setScene_info(JSONObject.toJSONString(createOrderH5SceneInfoVo));

        //????????????
        String sign = WxPayUtil.getSign(vo, signKey);
        vo.setSign(sign);
        return vo;
    }

    /**
     * ?????????????????????<br>
     * ??????????????????????????????????????????APP??????
     * @param vo ???wxpay post???????????????
     * @return API????????????
     */
    private CreateOrderResponseVo unifiedOrder(CreateOrderRequestVo vo) {
        try {
            String url = PayConstants.WX_PAY_API_URL + PayConstants.WX_PAY_API_URI;
            String request = XmlUtil.objectToXml(vo);
            String xml = restTemplateUtil.postXml(url, request);
            HashMap<String, Object> map = XmlUtil.xmlToMap(xml);
            if(null == map){
                throw new CrmebException("?????????????????????");
            }
            CreateOrderResponseVo responseVo = CrmebUtil.mapToObj(map, CreateOrderResponseVo.class);
            if(responseVo.getReturnCode().toUpperCase().equals("FAIL")){
                throw new CrmebException("??????????????????1???" +  responseVo.getReturnMsg());
            }

            if(responseVo.getResultCode().toUpperCase().equals("FAIL")){
                throw new CrmebException("??????????????????2???" + responseVo.getErrCodeDes());
            }

            responseVo.setExtra(vo.getScene_info());
            return responseVo;
        } catch (Exception e) {
            e.printStackTrace();
            throw new CrmebException(e.getMessage());
        }
    }

}
