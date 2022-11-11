package com.zbkj.crmeb.sms.service;

import com.common.MyRecord;
import com.common.PageParamRequest;
import com.zbkj.crmeb.sms.request.SendSmsVo;
import com.zbkj.crmeb.sms.request.SmsApplyTempRequest;
import com.zbkj.crmeb.sms.request.SmsModifySignRequest;

import java.math.BigDecimal;
import java.util.HashMap;

/**
 * SmsService-接口
 * @author: 零风
 * @CreateDate: 2022/2/7 11:01
 */
public interface SmsService {

    void push(String phone, String tempKey, Integer msgTempId, HashMap<String, Object> mapPram);

    /**
     * 发送短信(定时任务)
     * @Author 零风
     * @Date  2022/2/7
     */
    void consumeTask();

    /**
     * 发送短信(公共)
     * @Author 零风
     * @Date  2022/7/28 15:32
     * @return
     */
    Boolean sendCode(SendSmsVo sendSmsVo);

    /**
     * 修改签名
     */
    Boolean modifySign(SmsModifySignRequest request);

    /**
     * 短信模板
     */
    MyRecord temps(PageParamRequest pageParamRequest);

    /**
     * 申请模板消息
     */
    Boolean applyTempMessage(SmsApplyTempRequest request);

    /**
     * 模板申请记录
     *
     * @param type (1=验证码 2=通知 3=推广)
     */
    MyRecord applys(Integer type, PageParamRequest pageParamRequest);

    /**
     * 发送公共验证码
     *
     * @param phone 手机号
     */
    Boolean sendCommonCode(String phone);

    /**
     * 发送支付成功短信
     * @param phone 手机号
     * @param orderNo 订单编号
     * @param payPrice 支付金额
     * @return Boolean
     */
    Boolean sendPaySuccess(String phone, String orderNo, BigDecimal payPrice);

    /**
     * 发送管理员下单短信提醒短信
     * @param phone 手机号
     * @param orderNo 订单编号
     * @param realName 管理员名称
     */
    Boolean sendCreateOrderNotice(String phone, String orderNo, String realName);

    /**
     * 发送订单支付成功管理员提醒短信
     * @param phone 手机号
     * @param orderNo 订单编号
     * @param realName 管理员名称
     */
    Boolean sendOrderPaySuccessNotice(String phone, String orderNo, String realName);

    /**
     * 发送用户退款管理员提醒短信
     * @param phone 手机号
     * @param orderNo 订单编号
     * @param realName 管理员名称
     */
    Boolean sendOrderRefundApplyNotice(String phone, String orderNo, String realName);

    /**
     * 发送用户确认收货管理员提醒短信
     * @param phone 手机号
     * @param orderNo 订单编号
     * @param realName 管理员名称
     */
    Boolean sendOrderReceiptNotice(String phone, String orderNo, String realName);

    /**
     * 发送订单改价提醒短信
     * @param phone 手机号
     * @param orderNo 订单编号
     * @param price 修改后的支付金额
     */
    Boolean sendOrderEditPriceNotice(String phone, String orderNo, BigDecimal price);

    /**
     * 发送订单发货提醒短信
     * @param phone 手机号
     * @param nickName 用户昵称
     * @param storeName 商品名称
     * @param orderNo 订单编号
     */
    Boolean sendOrderDeliverNotice(String phone, String nickName, String storeName, String orderNo);

    /**
     * 发送预约通知短信
     * @Author 零风
     * @Date  2022/7/28 14:56
     * @return
     */
    Boolean sendMaaTonzhiSms(String phone);

}
