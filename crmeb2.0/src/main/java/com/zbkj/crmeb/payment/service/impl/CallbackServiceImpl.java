package com.zbkj.crmeb.payment.service.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.internal.util.AlipaySignature;
import com.common.MyRecord;
import com.constants.Constants;
import com.exception.CrmebException;
import com.utils.CrmebUtil;
import com.utils.DateUtil;
import com.utils.RedisUtil;
import com.utils.WxPayUtil;
import com.zbkj.crmeb.combination.model.StoreCombination;
import com.zbkj.crmeb.combination.model.StorePink;
import com.zbkj.crmeb.combination.service.StoreCombinationService;
import com.zbkj.crmeb.combination.service.StorePinkService;
import com.zbkj.crmeb.finance.model.UserRecharge;
import com.zbkj.crmeb.finance.service.UserRechargeService;
import com.zbkj.crmeb.payment.service.CallbackService;
import com.zbkj.crmeb.payment.service.OrderPayService;
import com.zbkj.crmeb.payment.service.RechargePayService;
import com.zbkj.crmeb.payment.vo.wechat.AttachVo;
import com.zbkj.crmeb.payment.vo.wechat.CallbackVo;
import com.zbkj.crmeb.store.model.StoreOrder;
import com.zbkj.crmeb.store.service.StoreOrderService;
import com.zbkj.crmeb.system.service.SystemConfigService;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.service.UserService;
import com.zbkj.crmeb.user.service.UserTokenService;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.util.*;


/**
 * 订单支付回调 CallbackService 实现类
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Service
public class CallbackServiceImpl implements CallbackService {

    private static final Logger logger = LoggerFactory.getLogger(CallbackServiceImpl.class);

    @Autowired
    private RechargePayService rechargePayService;

    @Lazy
    @Autowired
    private OrderPayService orderPayService;

    @Autowired
    private UserTokenService userTokenService;

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRechargeService userRechargeService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private StoreCombinationService storeCombinationService;

    @Autowired
    private StorePinkService storePinkService;

    /**
     * 微信支付回调
     * @author Mr.Zhang
     * @since 2020-05-06
     */
    @Override
    public String weChat(String xmlInfo) {
        logger.error("***************进入微信支付回调*************");
        StringBuffer sb = new StringBuffer();
        sb.append("<xml>");
        if(StrUtil.isBlank(xmlInfo)){
            sb.append("<return_code><![CDATA[FAIL]]></return_code>");
            sb.append("<return_msg><![CDATA[xmlInfo is blank]]></return_msg>");
            sb.append("</xml>");
            logger.error("wechat callback error : " + sb.toString());
            return sb.toString();
        }

        try{
            //HashMap<String, Object> map = XmlUtil.xmlToMap(xmlInfo);
            HashMap<String, Object> map = WxPayUtil.processResponseXml(xmlInfo);

            // 通信是否成功
            logger.error("***************通信是否成功*************");
            String returnCode = (String) map.get("return_code");
            if (!returnCode.equals(Constants.SUCCESS)) {
                sb.append("<return_code><![CDATA[SUCCESS]]></return_code>");
                sb.append("<return_msg><![CDATA[OK]]></return_msg>");
                sb.append("</xml>");
                logger.error("wechat callback error : wx pay return code is fail returnMsg : " + map.get("return_msg"));
                return sb.toString();
            }

            // 交易是否成功
            logger.error("***************交易是否成功*************");
            String resultCode = (String) map.get("result_code");
            if (!resultCode.equals(Constants.SUCCESS)) {
                sb.append("<return_code><![CDATA[SUCCESS]]></return_code>");
                sb.append("<return_msg><![CDATA[OK]]></return_msg>");
                sb.append("</xml>");
                logger.error("wechat callback error : wx pay result code is fail");
                return sb.toString();
            }

            //解析xml
            logger.error("***************解析xml*************");
            CallbackVo callbackVo = CrmebUtil.mapToObj(map, CallbackVo.class);
            AttachVo attachVo = JSONObject.toJavaObject(JSONObject.parseObject(callbackVo.getAttach()), AttachVo.class);

            //判断openid
            logger.error("***************判断openid*************");
            User user = userService.getById(attachVo.getUserId());
            if (ObjectUtil.isNull(user)) {
                //用户信息错误
                throw new CrmebException("用户信息错误！");
            }

            //根据类型判断是订单或者充值
            logger.error("***************根据类型判断是订单或者充值*************");
            if (!Constants.SERVICE_PAY_TYPE_ORDER.equals(attachVo.getType()) && !Constants.SERVICE_PAY_TYPE_RECHARGE.equals(attachVo.getType())) {
                logger.error("wechat pay err : 未知的支付类型==》" + callbackVo.getOutTradeNo());
                throw new CrmebException("未知的支付类型！");
            }

            // 订单
            logger.error("***************订单*************"+attachVo.getType());
            if (Constants.SERVICE_PAY_TYPE_ORDER.equals(attachVo.getType())) {
                //根据订单ID、用户id得到订单信息
                logger.error("***************根据订单ID、用户id得到订单信息*************");
                StoreOrder orderParam = new StoreOrder();
                orderParam.setOrderId(callbackVo.getOutTradeNo());
                orderParam.setUid(attachVo.getUserId());
                StoreOrder storeOrder = storeOrderService.getInfoByEntity(orderParam);

                //验证-非空
                if (ObjectUtil.isNull(storeOrder)) {
                    logger.error("wechat pay error : 订单信息不存在==》" + callbackVo.getOutTradeNo());
                    throw new CrmebException("wechat pay error : 订单信息不存在==》" + callbackVo.getOutTradeNo());
                }

                //验证-支付状态
                logger.error("***************验证-支付状态*************");
                if (storeOrder.getPaid()) {
                    logger.error("wechat pay error : 订单已处理==》" + callbackVo.getOutTradeNo());
                    sb.append("<return_code><![CDATA[SUCCESS]]></return_code>");
                    sb.append("<return_msg><![CDATA[OK]]></return_msg>");
                    sb.append("</xml>");
                    return sb.toString();
                }

                // 添加支付成功redis队列
                logger.error("***************添加支付成功redis队列*************");
                Boolean execute = transactionTemplate.execute(e -> {
                    //设置支付状态、支付时间，并执行修改订单
                    //storeOrderService.updatePaid(storeOrder.getOrderId());
                    storeOrder.setPaid(true);
                    storeOrder.setPayTime(DateUtil.nowDateTime());
                    storeOrder.setTransactionId(callbackVo.getTransactionId()); //微信支付订单号
                    storeOrderService.updateById(storeOrder);

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
                        // 如果是开团，需要更新订单数据
                        storeOrder.setPinkId(storePink.getId());
                        storeOrderService.updateById(storeOrder);
                    }

                    //返回结果
                    return Boolean.TRUE;
                });

                //验证执行结果
                logger.error("***************验证执行结果*************"+execute);
                if (!execute) {
                    logger.error("wechat pay error : 订单更新失败==》" + callbackVo.getOutTradeNo());
                    sb.append("<return_code><![CDATA[SUCCESS]]></return_code>");
                    sb.append("<return_msg><![CDATA[OK]]></return_msg>");
                    sb.append("</xml>");
                    return sb.toString();
                }
                logger.error("***************微信支付task*************");
                redisUtil.lPush(Constants.ORDER_TASK_PAY_SUCCESS_AFTER, storeOrder.getId()); // 微信支付
            }

            // 充值
            if (Constants.SERVICE_PAY_TYPE_RECHARGE.equals(attachVo.getType())) {
                UserRecharge userRecharge = new UserRecharge();
                userRecharge.setOrderId(callbackVo.getOutTradeNo());
                userRecharge.setUid(attachVo.getUserId());
                userRecharge = userRechargeService.getInfoByEntity(userRecharge);
                if(ObjectUtil.isNull(userRecharge)){
                    throw new CrmebException("没有找到订单信息");
                }
                if(userRecharge.getPaid()){
                    sb.append("<return_code><![CDATA[SUCCESS]]></return_code>");
                    sb.append("<return_msg><![CDATA[OK]]></return_msg>");
                    sb.append("</xml>");
                    return sb.toString();
                }
                // 支付成功处理
                Boolean rechargePayAfter = rechargePayService.paySuccess(userRecharge);
                if (!rechargePayAfter) {
                    logger.error("wechat pay error : 数据保存失败==》" + callbackVo.getOutTradeNo());
                    throw new CrmebException("wechat pay error : 数据保存失败==》" + callbackVo.getOutTradeNo());
                }
            }
            sb.append("<return_code><![CDATA[SUCCESS]]></return_code>");
            sb.append("<return_msg><![CDATA[OK]]></return_msg>");
        }catch (Exception e){
            sb.append("<return_code><![CDATA[FAIL]]></return_code>");
            sb.append("<return_msg><![CDATA[").append(e.getMessage()).append("]]></return_msg>");
            logger.error("wechat pay error : 业务异常==》" + e.getMessage());
        }
        sb.append("</xml>");
        logger.error("wechat callback response : " + sb.toString());
        return sb.toString();
    }

    /**
     * 支付宝支付回调
     * @author Mr.Zhang
     * @since 2020-05-06
     */
    @Override
    public boolean aliPay(HttpServletRequest request)  {
        //获取支付宝POST过来反馈信息
        System.out.println("-------------进入支付宝回调------------");
        logger.info("进入支付宝回调");
        Map< String , String > params = new HashMap < String , String > ();
        Map requestParams = request.getParameterMap();
        System.out.println(requestParams);
        logger.info(requestParams.toString());

        //循环处理
        for(Iterator iter = requestParams.keySet().iterator(); iter.hasNext();){
            String name = (String)iter.next();
            String[] values = (String [])requestParams.get(name);
            String valueStr = "";
            for(int i = 0;i < values.length;i ++ ){
                valueStr =  (i==values.length-1)?valueStr + values [i]:valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用。
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put (name,valueStr);
        }

        System.out.println("-------------支付宝回调-循环处理完成------------");
        System.out.println(params);
        //切记alipaypublickey是支付宝的公钥，请去open.alipay.com对应应用下查看。
        //boolean AlipaySignature.rsaCheckV1(Map<String, String> params, String publicKey, String charset, String sign_type)
        String public_key=systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_ZFB_APP_ZF_PUBLIC_KEY);
        try{
            boolean flag = AlipaySignature.rsaCheckV1(params,public_key, "UTF-8","RSA2");
            if(!flag)System.out.println("支付宝支付回调-flag："+flag);
            logger.info("支付宝回调");

            //得到订单号
            Object out_trade_no=params.get("out_trade_no");
            if(out_trade_no == null){
                return false;
            }

            //得到订单信息
            //根据订单ID、用户id得到订单信息
            StoreOrder orderParam = new StoreOrder();
            orderParam.setOrderId(out_trade_no.toString());
            StoreOrder storeOrder = storeOrderService.getInfoByEntity(orderParam);
            System.out.println(storeOrder);
            System.out.println(params.get("trade_no"));

            //执行修改
            Boolean execute = transactionTemplate.execute(e -> {
                //设置支付状态、支付时间，并执行修改订单
                storeOrder.setPaid(true);
                storeOrder.setPayTime(DateUtil.nowDateTime());
                storeOrder.setTransactionId(String.valueOf(params.get("trade_no")));
                storeOrderService.updateById(storeOrder);
                System.out.println("-------------修改完成------------");
                redisUtil.lPush(Constants.ORDER_TASK_PAY_SUCCESS_AFTER, storeOrder.getId()); // 支付宝支付
                return Boolean.TRUE;
            });
            return execute;
        }catch (Exception e){
            System.out.println("支付宝支付回调-错误！"+e.getMessage());
            e.printStackTrace();
        }

        //根据类型判断是订单或者充值
        return false;
    }

    /**
     * 微信退款回调
     * @param xmlInfo 微信回调json
     * @return MyRecord
     */
    @Override
    public String weChatRefund(String xmlInfo) {
        MyRecord notifyRecord = new MyRecord();
        MyRecord refundRecord = refundNotify(xmlInfo, notifyRecord);
        if (refundRecord.getStr("status").equals("fail")) {
            logger.error("微信退款回调失败==>" + refundRecord.getColumns() + ", rawData==>" + xmlInfo + ", data==>" + notifyRecord);
            return refundRecord.getStr("returnXml");
        }

        if (!refundRecord.getBoolean("isRefund")) {
            logger.error("微信退款回调失败==>" + refundRecord.getColumns() + ", rawData==>" + xmlInfo + ", data==>" + notifyRecord);
            return refundRecord.getStr("returnXml");
        }

        String outRefundNo = notifyRecord.getStr("out_refund_no");
        StoreOrder storeOrder = storeOrderService.getByOderId(outRefundNo);
        if (ObjectUtil.isNull(storeOrder)) {
            logger.error("微信退款订单查询失败==>" + refundRecord.getColumns() + ", rawData==>" + xmlInfo + ", data==>" + notifyRecord);
            return refundRecord.getStr("returnXml");
        }

        if (storeOrder.getRefundStatus() == 2) {
            logger.warn("微信退款订单已确认成功==>" + refundRecord.getColumns() + ", rawData==>" + xmlInfo + ", data==>" + notifyRecord);
            return refundRecord.getStr("returnXml");
        }

        storeOrder.setRefundStatus(2);
        boolean update = storeOrderService.updateById(storeOrder);
        if (update) {
            // 退款task
            redisUtil.lPush(Constants.ORDER_TASK_REDIS_KEY_AFTER_REFUND_BY_USER, storeOrder.getId());
        } else {
            logger.warn("微信退款订单更新失败==>" + refundRecord.getColumns() + ", rawData==>" + xmlInfo + ", data==>" + notifyRecord);
        }
        return refundRecord.getStr("returnXml");
    }

    /**
     * 支付订单回调通知
     * @return MyRecord
     */
    private MyRecord refundNotify(String xmlInfo, MyRecord notifyRecord) {
        MyRecord refundRecord = new MyRecord();
        refundRecord.set("status", "fail");
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>");
        if(StrUtil.isBlank(xmlInfo)){
            sb.append("<return_code><![CDATA[FAIL]]></return_code>");
            sb.append("<return_msg><![CDATA[xmlInfo is blank]]></return_msg>");
            sb.append("</xml>");
            logger.error("wechat refund callback error : " + sb.toString());
            return refundRecord.set("returnXml", sb.toString()).set("errMsg", "xmlInfo is blank");
        }

        Map<String, String> respMap;
        try {
            respMap = WxPayUtil.xmlToMap(xmlInfo);
        } catch (Exception e) {
            sb.append("<return_code><![CDATA[FAIL]]></return_code>");
            sb.append("<return_msg><![CDATA[").append(e.getMessage()).append("]]></return_msg>");
            sb.append("</xml>");
            logger.error("wechat refund callback error : " + e.getMessage());
            return refundRecord.set("returnXml", sb.toString()).set("errMsg", e.getMessage());
        }

        notifyRecord.setColums(_strMap2ObjMap(respMap));
        // 这里的可以应该根据小程序还是公众号区分
        String return_code = respMap.get("return_code");
        if (return_code.equals(Constants.SUCCESS)) {
            String appid = respMap.get("appid");
            String signKey = getSignKey(appid);
            // 解码加密信息
            String reqInfo = respMap.get("req_info");
            System.out.println("encodeReqInfo==>" + reqInfo);
            try {
                String decodeInfo = decryptToStr(reqInfo, signKey);
                Map<String, String> infoMap = WxPayUtil.xmlToMap(decodeInfo);
                notifyRecord.setColums(_strMap2ObjMap(infoMap));

                String refund_status = infoMap.get("refund_status");
                refundRecord.set("isRefund", refund_status.equals(Constants.SUCCESS));
            } catch (Exception e) {
                refundRecord.set("isRefund", false);
                logger.error("微信退款回调异常，e==》" + e.getMessage());
            }
        } else {
            notifyRecord.set("return_msg", respMap.get("return_msg"));
            refundRecord.set("isRefund", false);
        }
        sb.append("<return_code><![CDATA[SUCCESS]]></return_code>");
        sb.append("<return_msg><![CDATA[OK]]></return_msg>");
        sb.append("</xml>");
        return refundRecord.set("returnXml", sb.toString()).set("status", "ok");
    }

    private String getSignKey(String appid) {
        String publicAppid = systemConfigService.getValueByKey(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_ID);
        String miniAppid = systemConfigService.getValueByKey(Constants.CONFIG_KEY_PAY_ROUTINE_APP_ID);
        String signKey = "";
        if (StrUtil.isBlank(publicAppid) && StrUtil.isBlank(miniAppid)) {
            throw new CrmebException("pay_weixin_appid或pay_routine_appid不能都为空");
        }
        if (StrUtil.isNotBlank(publicAppid) && appid.equals(publicAppid)) {
            signKey = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_KEY);
        }
        if (StrUtil.isNotBlank(miniAppid) && appid.equals(miniAppid)) {
            signKey = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_ROUTINE_APP_KEY);
        }
        return signKey;
    }

    /**
     * java自带的是PKCS5Padding填充，不支持PKCS7Padding填充。
     * 通过BouncyCastle组件来让java里面支持PKCS7Padding填充
     * 在加解密之前加上：Security.addProvider(new BouncyCastleProvider())，
     * 并给Cipher.getInstance方法传入参数来指定Java使用这个库里的加/解密算法。
     */
    public static String decryptToStr(String reqInfo, String signKey) throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        byte[] decodeReqInfo = Base64.decode(reqInfo);
        SecretKeySpec key = new SecretKeySpec(SecureUtil.md5(signKey).toLowerCase().getBytes(), "AES");
        Cipher cipher;
        cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(decodeReqInfo), StandardCharsets.UTF_8);
    }

    private static final List<String> list = new ArrayList<>();
    static {
        list.add("total_fee");
        list.add("cash_fee");
        list.add("coupon_fee");
        list.add("coupon_count");
        list.add("refund_fee");
        list.add("settlement_refund_fee");
        list.add("settlement_total_fee");
        list.add("cash_refund_fee");
        list.add("coupon_refund_fee");
        list.add("coupon_refund_count");
    }

    private Map<String, Object> _strMap2ObjMap(Map<String, String> params) {
        Map<String, Object> map = new HashMap<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (list.contains(entry.getKey())) {
                try {
                    map.put(entry.getKey(), Integer.parseInt(entry.getValue()));
                } catch (NumberFormatException e) {
                    map.put(entry.getKey(), 0);
                    logger.error("字段格式错误，key==》" + entry.getKey() + ", value==》" + entry.getValue());
                }
                continue;
            }

            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }
}
