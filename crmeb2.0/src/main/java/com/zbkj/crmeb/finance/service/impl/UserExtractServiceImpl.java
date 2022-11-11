package com.zbkj.crmeb.finance.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CommonPage;
import com.common.PageParamRequest;
import com.constants.BrokerageRecordConstants;
import com.constants.Constants;
import com.constants.ConstantsFromID;
import com.constants.PayConstants;
import com.exception.CrmebException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.utils.DateUtil;
import com.utils.WxPayUtil;
import com.utils.vo.dateLimitUtilVo;
import com.zbkj.crmeb.cloudAccount.constant.*;
import com.zbkj.crmeb.cloudAccount.request.*;
import com.zbkj.crmeb.cloudAccount.response.Response;
import com.zbkj.crmeb.cloudAccount.util.*;
import com.zbkj.crmeb.finance.dao.UserExtractDao;
import com.zbkj.crmeb.finance.model.UserExtract;
import com.zbkj.crmeb.finance.request.UserExtractRequest;
import com.zbkj.crmeb.finance.request.UserExtractSearchRequest;
import com.zbkj.crmeb.finance.response.BalanceResponse;
import com.zbkj.crmeb.finance.response.UserExtractResponse;
import com.zbkj.crmeb.finance.service.UserExtractService;
import com.zbkj.crmeb.front.response.UserExtractRecordResponse;
import com.zbkj.crmeb.system.service.SystemAttachmentService;
import com.zbkj.crmeb.system.service.SystemConfigService;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.model.UserBrokerageRecord;
import com.zbkj.crmeb.user.model.UserToken;
import com.zbkj.crmeb.user.service.UserBillService;
import com.zbkj.crmeb.user.service.UserBrokerageRecordService;
import com.zbkj.crmeb.user.service.UserService;
import com.zbkj.crmeb.user.service.UserTokenService;
import com.zbkj.crmeb.wechat.service.impl.WechatSendMessageForMinService;
import com.zbkj.crmeb.wechat.vo.PayToChangeVo;
import com.zbkj.crmeb.wechat.vo.WechatSendMessageForCash;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static java.math.BigDecimal.ZERO;

/**
 * UserExtractServiceImpl 接口实现
 * @author: 零风
 * @CreateDate: 2022/3/1 10:05
 */
@Service
public class UserExtractServiceImpl extends ServiceImpl<UserExtractDao, UserExtract> implements UserExtractService {

    @Resource
    private UserExtractDao dao;

    @Autowired
    private UserService userService;

    @Autowired
    private UserBillService userBillService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private WechatSendMessageForMinService wechatSendMessageForMinService;

    @Autowired
    private SystemAttachmentService systemAttachmentService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private UserBrokerageRecordService userBrokerageRecordService;

    @Autowired
    private UserTokenService userTokenService;

    public static void main(String[] args) throws Exception {
        String appkey = "137130421c9b43cb9763b39647a33bd9";// 微信商户秘钥, 根据实际情况填写
        String certPath = "D:\\demo\\apiclient_cert.p12";// 微信商户证书路径, 根据实际情况填写
        PayToChangeVo model = new PayToChangeVo();// 微信接口请求参数, 根据实际情况填写
        model.setMch_appid("wx5cc601e2403cd25d"); // 申请商户号的appid或商户号绑定的appid
        model.setMchid("1610646923"); // 商户号
        model.setMch_name("466c61bab9a34df613dd7736b454b6cb"); // 商户名称
        model.setOpenid("o2MXC0mRisuBidrelyTh4DIXiN3Y"); // 商户appid下，某用户的openid
        model.setAmount(1); // 企业付款金额，这里单位为元
        model.setDesc("测试企业付款到零钱");

        // 微信官方API文档 https://pay.weixin.qq.com/wiki/doc/api/tools/mch_pay.php?chapter=14_2
        String iResult = WxPayUtil.doTransfers(certPath, model);
        HashMap<String, Object> map=  WxPayUtil.processResponseXml(iResult);
        Map<String, String> map2= WxPayUtil.xmlToMap(iResult);
        System.out.println(map2);
    }

    @Override
    public HashMap<String, Object> weixinPayToChange(BigDecimal extractPrice,Integer appidType,Integer uid) {
        HashMap<String, Object> resultMap=new HashMap<>();
        resultMap.put("result",false);

        //得到-提现配置
        HashMap<String, String> hashMap = systemConfigService.info(ConstantsFromID.INT_tixian_to_weixin);
        if(hashMap == null){
            resultMap.put("msg","提现到微信零钱配置为空！");
            return resultMap;
        }

        //验证-是否开启
        Boolean isOpen=Boolean.valueOf(hashMap.get("tixian_to_weixin_is_open"));
        if(!isOpen){
            resultMap.put("msg",hashMap.get("tixian_to_weixin_close_msg"));
            return resultMap;
        }

        //设置-请求参数
        PayToChangeVo payToChangeVo = new PayToChangeVo();
        payToChangeVo.setMchid(hashMap.get("tixian_to_weixin_mchid"));
        payToChangeVo.setMch_name(hashMap.get("tixian_to_weixin_mch_name"));
        payToChangeVo.setAppkey(hashMap.get("tixian_to_weixin_appkey"));
        payToChangeVo.setDesc(hashMap.get("tixian_to_weixin_desc"));
        payToChangeVo.setAmount(extractPrice.doubleValue());
        payToChangeVo.setNonce_str(WxPayUtil.getNonceStr());
        payToChangeVo.setPartner_trade_no(OrderUtil.getOrderId(OrderPrefixEnum.WXPAY_ORDER.getValue()));

        //验证提现appid类型设置对应的应用ID
        String appid = "";
        Integer type = 0;
        switch (appidType){
            case 1: // 微信小程序
                appid = hashMap.get("tixian_to_weixin_appid_server");
                type = Constants.THIRD_LOGIN_TOKEN_TYPE_PUBLIC;
                break;
            case 2: // 微信服务号
                appid = hashMap.get("tixian_to_weixin_appid_xcx");
                type = Constants.THIRD_LOGIN_TOKEN_TYPE_PROGRAM;
                break;
            case 3: // 安卓微信
                appid = hashMap.get("tixian_to_weixin_appid_app");
                type = Constants.THIRD_LOGIN_TOKEN_TYPE_ANDROID_WX;
                break;
            case 4: // ios微信
                type = Constants.THIRD_LOGIN_TOKEN_TYPE_IOS_WX;
                appid = hashMap.get("tixian_to_weixin_appid_app");
                break;
            default:
                resultMap.put("msg","appid错误！");
                return resultMap;
        }
        payToChangeVo.setMch_appid(appid);

        //得到-openid
        UserToken userToken = userTokenService.getTokenByUserId(uid,type);
        if(userToken == null){
            resultMap.put("msg","请使用微信小程序登录！或绑定微信！");
            return resultMap;
        }
        payToChangeVo.setOpenid(userToken.getToken());

        try{
            //发起提现请求->得到响应结果
            String certPath=hashMap.get("tixian_to_weixin_zhengshu_path");// 微信商户证书路径, 根据实际情况填写
            String iResult = WxPayUtil.doTransfers(certPath, payToChangeVo);
            HashMap<String, Object> map =  WxPayUtil.processResponseXml(iResult);//将结果转为map对象
            System.out.println("lingfe");
            System.out.println(iResult);
            System.out.println(map);

            //验证非空
            if(map == null){
                resultMap.put("msg","提现失败！map为空！");
            }
            //验证请求结果
            else if("SUCCESS".equals(map.get("result_code"))){
                resultMap.put("msg","提现成功！");
                resultMap.put("result",true);
            }
            //验证返回结果
            else if("SUCCESS".equals(map.get("return_code"))){
                resultMap.put("msg",new StringBuffer().append(map.get("return_msg")).append("||").append(map.get("err_code_des")).toString());
            }else{
                resultMap.put("msg","提现失败！请重试！");
            }

            //返回
            return resultMap;
        }catch (Exception e){
            resultMap.put("msg",e.getMessage());
            return resultMap;
        }
    }

    @Override
    public Boolean isExtract(Boolean isExtract,Integer id,String backMessage) {
        //根据ID标识读取提现记录
        UserExtract userExtract = dao.selectById(id);
        if(userExtract==null)throw new CrmebException("提现记录为：NULL!");

        //是否
        if(isExtract){
            //yes-通过
            Map<String, Object> result=this.yes(userExtract);
        }else{
            //no-不通过，更新状态退还佣金
            User user=userService.getById(userExtract.getUid());
            return this.tuihuan(user,
                    userExtract,
                    Constants.USER_EXTRACT_STATUS_NO,
                    "原因",
                    BrokerageRecordConstants.BROKERAGE_RECORD_TYPE_3);
        }

        //返回
        return Boolean.TRUE;
    }

    /**
     * 退还佣金
     * @param userExtract   用户提现信息实体对象
     * @param user          用户信息实体对象
     * @param state         提现状态
     * @param backMessage   提现拒绝：原因/理由
     * @param type          明细类型
     */
    public Boolean tuihuan(User user,UserExtract userExtract, int state, String backMessage,Integer type) {
        //验证非空并重新计算佣金余额
        if(user== null) throw new CrmebException("失败！申请用户为：NULL!");

        //佣金提现相关(提现申请拒绝、提现取消)-退还佣金记录
        UserBrokerageRecord brokerageRecord = userBrokerageRecordService.getUserBrokerageRecord(    // 佣金提现相关(提现申请拒绝、提现取消)-退还佣金记录
                user.getUid(),userExtract.getId().toString(),
                BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_EXTRACT, type,
                BrokerageRecordConstants.BROKERAGE_RECORD_TYPE_ADD,
                userExtract.getExtractPrice(),user.getBrokeragePrice().add(userExtract.getExtractPrice()),
                0, 0L
        );

        //执行事务
        Boolean execute = transactionTemplate.execute(e -> {
            //更新-用户佣金
            Boolean isUpdate = userService.operationBrokerage(user.getUid(), brokerageRecord.getPrice(), user.getBrokeragePrice(),
                    BrokerageRecordConstants.BROKERAGE_RECORD_TYPE_ADD_STR);
            System.out.println("更新-用户佣金->"+isUpdate);

            //保存-佣金记录
            boolean saveBrokerageRecord=userBrokerageRecordService.save(brokerageRecord);
            System.out.println("添加-提现申请佣金记录->"+saveBrokerageRecord);

            //更新-用户提现状态
            userExtract.setStatus(state);
            userExtract.setFailMsg(backMessage);
            boolean userExtractUpdateById=dao.updateById(userExtract) > 0;
            System.out.println("更新-提现记录-状态->"+userExtractUpdateById);
            return Boolean.TRUE;
        });

        //验证执行结果
        if(!execute){
            throw new CrmebException("失败!事务异常！");
        }

        //返回
        return Boolean.TRUE;
    }

    /**
     * 通过,根据提现类型-重新发送提现请求
     * @param userExtract
     */
    public Map<String, Object> yes(UserExtract userExtract) {
        //实例化对象
        Object requestObj=null;
        String api=null;

        //验证-提现方式
        switch (userExtract.getExtractType()){
            case PayConstants.PAY_TYPE_BANK:
                //提现-到银行卡
                api=Property.getUrl(ConfigPath.YZH_BANK_CARD_REAL_TIME_ORDER);
                requestObj = BankCardOrderRequestCloudAccount.builder()
                        //商户信息
                        .order_id(OrderUtil.getOrderId(OrderPrefixEnum.BANK_CARD_ORDER.getValue()))
                        .dealer_id(Property.getProperties(ConfigPath.YZH_DEALERID))
                        .broker_id(Property.getProperties(ConfigPath.YZH_BROKERID))
                        .notify_url(Property.getProperties(ConfigPath.YZH_BACKNOTIFY_URL))
                        //收款人信息
                        .real_name(userExtract.getRealName())
                        .card_no(userExtract.getBankCode())
                        .phone_no(userExtract.getPhone())
                        .id_card(userExtract.getIdCard())
                        .pay(userExtract.getExtractPrice().toString())
                        .pay_remark(userExtract.getRemark())
                        .build();
                //发送请求-并得到响应
                return Request.sendRequestResult(requestObj,api, RequestMethod.POST.toString());
            case PayConstants.PAY_TYPE_ALI_PAY:
                //提现-到支付宝
                api=Property.getUrl(ConfigPath.YZH_ALIPAY_REAL_TIME_ORDER);
                requestObj=AlipayOrderRequest.builder()
                        //商户信息
                        .order_id(OrderUtil.getOrderId(OrderPrefixEnum.ALIPAY_ORDER.getValue()))
                        .dealer_id(Property.getProperties(ConfigPath.YZH_DEALERID))
                        .broker_id(Property.getProperties(ConfigPath.YZH_BROKERID))
                        .check_name(CheckNameEnum.NOCHECK.getValue())
                        .notify_url(Property.getProperties(ConfigPath.YZH_BACKNOTIFY_URL))
                        //收款人信息
                        .real_name(userExtract.getRealName())
                        .card_no(userExtract.getAlipayCode())
                        .id_card(userExtract.getIdCard())
                        .phone_no(userExtract.getPhone())
                        .pay(userExtract.getExtractPrice().toString())
                        .pay_remark(userExtract.getRemark())
                        .build();
                //发送请求-并得到响应
                return Request.sendRequestResult(requestObj,api, RequestMethod.POST.toString());
            case PayConstants.PAY_TYPE_WE_CHAT:
                //提现-到微信零钱
                api=Property.getUrl(ConfigPath.YZH_WXPAY_REAL_TIME_ORDER);
                requestObj = WxpayOrderRequest.builder()
                        //商户信息
                        .order_id(OrderUtil.getOrderId(OrderPrefixEnum.WXPAY_ORDER.getValue()))
                        .dealer_id(Property.getProperties(ConfigPath.YZH_DEALERID))
                        .broker_id(Property.getProperties(ConfigPath.YZH_BROKERID))
                        .notify_url(Property.getProperties(ConfigPath.YZH_BACKNOTIFY_URL))
                        //收款人信息
                        .real_name(userExtract.getRealName())
                        .id_card(userExtract.getIdCard())
                        .openid(userExtract.getOpenid())
                        .pay(userExtract.getExtractPrice().toString())
                        .pay_remark(userExtract.getRemark())
                        .wx_app_id("")
                        .wxpay_mode("transfer")
                        .build();
                //发送请求-并得到响应
                return Request.sendRequestResult(requestObj,api, RequestMethod.POST.toString());
            default:
                throw new  CrmebException("提现方式不正确!");
        }
    }

    @Override
    public Boolean applyWithdrawalToWeixin(UserExtractRequest userExtractRequest) {
        //验证-打款金额
        BigDecimal pay=this.verificationPay(userExtractRequest.getExtractPrice());

        //得到-微信-下单打款-请求实体
        WxpayOrderRequest wxpayOrderRequest = WxpayOrderRequest.builder()
                //商户信息
                .order_id(OrderUtil.getOrderId(OrderPrefixEnum.WXPAY_ORDER.getValue()))
                .dealer_id(Property.getProperties(ConfigPath.YZH_DEALERID))
                .broker_id(Property.getProperties(ConfigPath.YZH_BROKERID))
                .notify_url(Property.getProperties(ConfigPath.YZH_BACKNOTIFY_URL))
                //收款人信息
                .real_name(userExtractRequest.getRealName())
                .id_card(userExtractRequest.getIdCard())
                .openid(userExtractRequest.getOpenid())
                .pay(userExtractRequest.getExtractPrice().toString())
                .pay_remark(userExtractRequest.getRemark())
                .wx_app_id("")
                .wxpay_mode("transfer")
//                .real_name("李杰")
//                .id_card("522228199705281319")
//                .openid("wxpayOrder")
//                .pay("100000.00")
//                .notes("备注")
//                .pay_remark("测试数据")
                .build();

        //提现记录-基础参数-设置
        UserExtract userExtract = new UserExtract();    // 申请打款-至微信零钱(云账户)->提现记录
        userExtract.setOrderId(wxpayOrderRequest.getOrder_id());
        userExtract.setExtractType(PayConstants.PAY_TYPE_WE_CHAT);
        userExtract.setRealName(wxpayOrderRequest.getReal_name());
        userExtract.setWechat(wxpayOrderRequest.getOpenid());
        userExtract.setRemark(wxpayOrderRequest.getPay_remark());
        userExtract.setIdCard(wxpayOrderRequest.getId_card());
        userExtract.setOpenid(wxpayOrderRequest.getOpenid());

        //验证-是否需要审核提现金额
        Boolean isToExaminePrice=this.isToExaminePrice(pay);
        if(isToExaminePrice){
            userExtract.setStatus(Constants.USER_EXTRACT_STATUS_SQZ);//审核中
        }else{
            //发送请求-并得到响应
            String api=Property.getUrl(ConfigPath.YZH_WXPAY_REAL_TIME_ORDER);
            Map<String, Object> result=Request.sendRequestResult(wxpayOrderRequest,api, RequestMethod.POST.toString());
            if(result!=null){
                userExtract.setStatus(Constants.USER_EXTRACT_STATUS_SUCCESS);
            }else{
                userExtract.setStatus(Constants.USER_EXTRACT_STATUS_FAIL);
            }
        }

        //执行-添加佣金记录、提现记录，更新用户佣金等逻辑
        return this.zhixin(userExtract,pay);
    }

    @Override
    public Boolean applyWithdrawalToAlipay(UserExtractRequest userExtractRequest) {
        //验证打款金额
        BigDecimal pay=this.verificationPay(userExtractRequest.getExtractPrice());
        //BigDecimal pay=new BigDecimal("1.00");

        //得到-支付宝下单打款-请求实体
        AlipayOrderRequest alipayOrderRequest=AlipayOrderRequest.builder()
                //商户信息
                .order_id(OrderUtil.getOrderId(OrderPrefixEnum.ALIPAY_ORDER.getValue()))
                .dealer_id(Property.getProperties(ConfigPath.YZH_DEALERID))
                .broker_id(Property.getProperties(ConfigPath.YZH_BROKERID))
                .check_name(CheckNameEnum.NOCHECK.getValue())
                .notify_url(Property.getProperties(ConfigPath.YZH_BACKNOTIFY_URL))
                //收款人信息
                .real_name(userExtractRequest.getRealName())
                .card_no(userExtractRequest.getAlipayCode())
                .id_card(userExtractRequest.getIdCard())
                .phone_no(userExtractRequest.getPhone())
                .pay(userExtractRequest.getExtractPrice().toString())
                .pay_remark(userExtractRequest.getRemark())
//                .real_name("李杰")
//                .card_no("18585094270")
//                .phone_no("18585094270")
//                .id_card("522228199705281319")
//                .pay(pay.toString())
//                .pay_remark("测试数据")
                .build();

        //提现记录-基础参数-设置
        UserExtract userExtract = new UserExtract();    // 申请打款-到支付宝(支付宝提现)(云账户)->提现记录
        userExtract.setOrderId(alipayOrderRequest.getOrder_id());
        userExtract.setExtractType(PayConstants.PAY_TYPE_ALI_PAY);
        userExtract.setRealName(alipayOrderRequest.getReal_name());
        userExtract.setAlipayCode(alipayOrderRequest.getCard_no());
        userExtract.setRemark(alipayOrderRequest.getPay_remark());
        userExtract.setStatus(Constants.USER_EXTRACT_STATUS_SQZ);
        userExtract.setIdCard(alipayOrderRequest.getId_card());
        userExtract.setPhone(alipayOrderRequest.getPhone_no());

        //验证-是否需要审核提现金额
        Boolean isToExaminePrice=this.isToExaminePrice(pay);
        if(isToExaminePrice){
            userExtract.setStatus(Constants.USER_EXTRACT_STATUS_SQZ);//审核中
        }else{
            //发送请求-并得到响应
            String api=Property.getUrl(ConfigPath.YZH_ALIPAY_REAL_TIME_ORDER);
            Map<String, Object> result=Request.sendRequestResult(alipayOrderRequest,api,RequestMethod.POST.toString());
        }

        //执行-添加佣金记录、提现记录，更新用户佣金等逻辑
        return this.zhixin(userExtract,pay);
    }

    /**
     * 验证-是否需要审核提现金额
     * @param pay 提现金额
     * @return 是否
     */
    public Boolean isToExaminePrice(BigDecimal pay){
        //得到-表单字段值
        String value = systemConfigService.getValueByKeyException(Constants.USER_ExtractToExaminePrice);
        BigDecimal ten = new BigDecimal(value);
        if(ten.compareTo(pay) < 1){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 验证打款金额
     * @param  pay    提现金额
     * @return 验证后的提现金额
     */
    public BigDecimal verificationPay(BigDecimal pay){
        //得到-当前登录用户信息
        User user = userService.getInfoException();
        //得到-佣金余额
        BigDecimal brokeragePrice= user.getBrokeragePrice();

        //验证-提现金额
        if(pay == null || pay == ZERO) throw new CrmebException("提现金额不能小于或等于0！");
        pay=pay.setScale(2, BigDecimal.ROUND_DOWN); //小数位直接舍去

        //验证-最低提现金额
        String value = systemConfigService.getValueByKeyException(Constants.CONFIG_EXTRACT_MIN_PRICE);
        BigDecimal ten = new BigDecimal(value);
        if (pay.compareTo(ten) < 0) {
            throw new CrmebException(StrUtil.format("最低提现金额{}元", ten));
        }

        //验证-佣金余额-是否充足
        if(brokeragePrice == null || brokeragePrice == ZERO){
            throw new CrmebException("余额不足！");
        }

        //验证-用户信息非空
        if (ObjectUtil.isNull(user)) {
            throw new CrmebException("提现用户信息异常:null!");
        }

        //验证-只能提现的金额
        if(brokeragePrice.compareTo(pay) < 0){
            throw new CrmebException("你当前最多可提现：" + brokeragePrice + "元");
        }

        //返回提现金额
        return pay;
    }

    /**
     * 执行-添加佣金记录、提现记录，更新用户佣金等逻辑
     * @param pay  提现金额
     * @return  执行结果
     */
    public Boolean zhixin(UserExtract userExtract, BigDecimal pay){
        //得到-当前登录用户信息
        User user = userService.getInfoException();

        //提现记录-补充全局或默认参数
        userExtract.setUid(user.getUid());
        userExtract.setBalance(user.getBrokeragePrice().subtract(pay));
        userExtract.setExtractPrice(pay);
        userExtract.setCreateTime(DateUtil.nowDateTime());

        //提现申请扣除佣金-佣金记录
        UserBrokerageRecord userBrokerageRecord = userBrokerageRecordService.getUserBrokerageRecord(    // 提现申请扣除佣金(云账户)-佣金记录
                user.getUid(),userExtract.getId().toString(),
                BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_EXTRACT,
                BrokerageRecordConstants.BROKERAGE_RECORD_TYPE_2,
                BrokerageRecordConstants.BROKERAGE_RECORD_TYPE_SUB,
                pay,userExtract.getBalance(),
                0,0L
        );

        //执行操作(事务)
        BigDecimal finalPay = pay;
        BigDecimal finalBrokeragePrice = user.getBrokeragePrice();
        Boolean execute = transactionTemplate.execute(e -> {
            //保存-提现记录
            Boolean saveUserExtractIs = this.save(userExtract);
            System.out.println("保存-提现记录->"+saveUserExtractIs);

            //保存-佣金记录
            Boolean isSaveBrokerageRecord = userBrokerageRecordService.save(userBrokerageRecord);
            System.out.println("保存-佣金记录->"+isSaveBrokerageRecord);

            //更新-用户佣金
            Boolean isUpdate = userService.operationBrokerage(user.getUid(), finalPay, finalBrokeragePrice, BrokerageRecordConstants.BROKERAGE_RECORD_TYPE_SUB_STR);
            System.out.println("更新-用户佣金->"+isUpdate);
            return Boolean.TRUE;
        });

        //验证操作
        if(!execute){
            throw new CrmebException("申请提现失败！事务异常!");
        }

        //返回
        return Boolean.TRUE;
    }

    @Override
    public Boolean applyWithdrawalToBankCard(UserExtractRequest userExtractRequest)  {
        //验证打款金额
        BigDecimal pay=this.verificationPay(userExtractRequest.getExtractPrice());

        //提现-到银行卡
        BankCardOrderRequest bankCardOrderRequest=BankCardOrderRequest.builder().build();
        BeanUtils.copyProperties(userExtractRequest, bankCardOrderRequest);
        bankCardOrderRequest.setPay(userExtractRequest.getExtractPrice().toString());
        bankCardOrderRequest.setCardNo(userExtractRequest.getBankCode());

        //得到-请求云账户-实体对象
        BankCardOrderRequestCloudAccount bankCardOrderCloudRequest = BankCardOrderRequestCloudAccount.builder()
                //商户信息
                .order_id(OrderUtil.getOrderId(OrderPrefixEnum.BANK_CARD_ORDER.getValue()))
                .dealer_id(Property.getProperties(ConfigPath.YZH_DEALERID))
                .broker_id(Property.getProperties(ConfigPath.YZH_BROKERID))
                .notify_url(Property.getProperties(ConfigPath.YZH_BACKNOTIFY_URL))
                //收款人信息
                .real_name(bankCardOrderRequest.getRealName())
                .card_no(bankCardOrderRequest.getCardNo())
                .phone_no(bankCardOrderRequest.getPhoneNo())
                .id_card(bankCardOrderRequest.getIdCard())
                .pay(bankCardOrderRequest.getPay())
                .pay_remark(bankCardOrderRequest.getPayRemark())
//                .real_name("李杰")
//                .card_no("6212262408007508180")
//                .phone_no("18585094270")
//                .id_card("522228199705281319")
//                .pay("0.01")
//                .pay_remark("测试数据")
                .build();

        //提现记录-基础参数-设置
        UserExtract userExtract = new UserExtract();    //  申请打款-到银行卡(银行卡提现)(云账户)->提现记录
        userExtract.setOrderId(bankCardOrderCloudRequest.getOrder_id());
        userExtract.setExtractType(PayConstants.PAY_TYPE_BANK);
        userExtract.setBankName(bankCardOrderRequest.getBankName());
        userExtract.setBankCode(bankCardOrderCloudRequest.getCard_no());
        userExtract.setRemark(bankCardOrderCloudRequest.getPay_remark());
        userExtract.setStatus(Constants.USER_EXTRACT_STATUS_SQZ);
        userExtract.setIdCard(bankCardOrderCloudRequest.getId_card());
        userExtract.setPhone(bankCardOrderCloudRequest.getPhone_no());

        //验证-是否需要审核提现金额
        Boolean isToExaminePrice=this.isToExaminePrice(pay);
        if(isToExaminePrice){
            userExtract.setStatus(Constants.USER_EXTRACT_STATUS_SQZ);//审核中
        }else{
            //发送请求-并得到响应
            String api=Property.getUrl(ConfigPath.YZH_BANK_CARD_REAL_TIME_ORDER);
            Map<String, Object> result=Request.sendRequestResult(bankCardOrderCloudRequest,api,RequestMethod.POST.toString());

            //验证响应结果
            //if(!"0000".equals(result.get("code")));
        }

        //执行-添加佣金记录、提现记录，更新用户佣金等逻辑
        return this.zhixin(userExtract,pay);
    }

    @Override
    public Boolean cancel(Integer userExtract_id) {
        //根据订单ID查询
        UserExtract userExtract=dao.selectById(userExtract_id);
        if(userExtract == null)throw new CrmebException("提现记录不存在！");

        //验证状态
        if(userExtract.getStatus() != Constants.USER_EXTRACT_STATUS_FAIL)throw new CrmebException("状态不为失败，取消提现失败！");

        //得到-待打款订单-实体对象
        CancelOrderRequest cancelOrderRequest = CancelOrderRequest.builder()
                .dealer_id(Property.getProperties(ConfigPath.YZH_DEALERID))
                .broker_id(Property.getProperties(ConfigPath.YZH_BROKERID))
                .channel(ChannelEnum.BANKCARD.getValue())
                .order_id(userExtract.getOrderId())
                .build();

        //得到响应
        String api = Property.getUrl(ConfigPath.YZH_CANCEL_ORDER);
        Map<String, Object> map = Request.sendRequestResult(cancelOrderRequest,api,RequestMethod.POST.toString());
        if(map.get("code").equals("0000")){
            //更新状态退还佣金
            User user=userService.getInfoException();
            return this.tuihuan( user,userExtract,5,"原因",BrokerageRecordConstants.BROKERAGE_RECORD_TYPE_4);
        }else{
            throw new CrmebException("取消失败！");
        }
    }

    @Override
    public Map<String, Object> retry(Integer userExtract_id)  {
        //根据订单ID查询
        UserExtract userExtract=dao.selectById(userExtract_id);
        if(userExtract == null)throw new CrmebException("提现记录不存在！");

        //验证状态
        if(userExtract.getStatus() != Constants.USER_EXTRACT_STATUS_FAIL)throw new CrmebException("状态不为失败，重新提交申请失败！");

        //复制转换
        BankCardOrderRequestCloudAccount bankCardOrderCloudRequest=BankCardOrderRequestCloudAccount.builder().build();
        BeanUtils.copyProperties(userExtract,bankCardOrderCloudRequest);

        //得到-响应map
        String api=Property.getUrl(ConfigPath.YZH_BANK_CARD_REAL_TIME_ORDER);
        Map<String, Object> result = Request.sendRequestResult(bankCardOrderCloudRequest,api,RequestMethod.POST.toString());
        if(result.get("code").equals("0000")){
            userExtract.setStatus(Constants.USER_EXTRACT_STATUS_SQZ);//设置为-申请中
        }else{
            throw new CrmebException("重试申请失败！");
        }

        //更新状态,并验证
        if(dao.updateById(userExtract)!=1){
            throw new CrmebException("重试申请失败！");
        }

        //返回响应
        return result;
    }

    @Override
    public String applyCallback(HttpServletRequest request)   {
        //全部输出参数
        System.out.println("applyCallback");
        Enumeration<String> eName=request.getParameterNames();
        while (eName.hasMoreElements()){
            String name=eName.nextElement();
            System.out.print(name);
            System.out.print("=");
            System.out.print(request.getParameter(name));
            System.out.println();
        }

        //读取参数
        Object data=request.getParameter("data");
        System.out.println(data);
        Map<String,Object> map=null;
        try {
            String deskey = "T7Kc3rfHNXwNN3e65Ncgw1fT";
            map = JsonUtil.fromJson(DESUtil.decode(data.toString(),deskey), Map.class);
            System.out.println(map);
        }catch (Exception e){
            e.printStackTrace();
            return "fail";
        }

        //验证-非空
        if(map == null){
            System.out.println("fail！");
            return "fail";
        }
        System.out.println("fail！data：");
        System.out.print(map);

        //状态-验证
        Object stateObj=map.get("status");
        if(stateObj==null) return "fail";

        //状态-处理,状态: 0=申请中，1=成功/已提现，2=失败，3=挂单,4=退汇,5=取消,6=审核中,-1=未通过
        Integer status;
        switch (stateObj.toString()){
            case "1":
                //成功
                status=Constants.USER_EXTRACT_STATUS_SUCCESS;
                System.out.println("成功！");
                break;
            case "2":
                //失败
                status=Constants.USER_EXTRACT_STATUS_FAIL;
                System.out.println("失败！");
                break;
            case "4":
                //挂单
                status=Constants.USER_EXTRACT_STATUS_GD;
                System.out.println("挂单！");
                break;
            case "9":
                //退汇
                status=Constants.USER_EXTRACT_STATUS_TH;
                System.out.println("退汇！");
                break;
            case "15":
                //取消
                status=Constants.USER_EXTRACT_STATUS_CANCEL;
                System.out.println("取消！");
                break;
            default:
                return "fail";
        }

        //得到order_id
        Object order_id=map.get("order_id");
        LambdaQueryWrapper<UserExtract> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(UserExtract::getOrderId,order_id);

        //得到数据
        UserExtract userExtract = dao.selectOne(queryWrapper);
        userExtract.setStatus(status);//设置状态
        dao.updateById(userExtract); //更新
        System.out.println(userExtract);

        //响应成功
        System.out.println("success！");
        return "success";
    }

    @Deprecated
    @Override
    public List<Object> getList(PageParamRequest pageParamRequest) throws Exception {
        //实例化集合
        List<Object> list=new ArrayList<>();

        //得到当前登录用户id
        Integer userId = userService.getUserIdException();
        //得到-分页对象
        Page<Object> startPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        //得到-查询对象
        LambdaQueryWrapper<UserExtract> lqw = Wrappers.lambdaQuery();

        //条件-用户id
        lqw.eq(UserExtract::getUid, userId);

        //得到数据
        List<UserExtract> bankCardOrderList = dao.selectList(lqw);
        if (CollUtil.isEmpty(bankCardOrderList)) {
            return new ArrayList<>();
        }

        //循环处理
        for (UserExtract bank:bankCardOrderList) {
            //请求查询-银行卡下单打款信息
            Map<String, Object> map=new HashMap<>();
            map.put("order_id",bank.getOrderId());
            Request request = Request.builder()
                    .mess(OrderUtil.getMess())
                    .timestamp(Integer.parseInt(String.valueOf(new Date().getTime()/1000)))
                    .sign_type(Property.getProperties(ConfigPath.YZH_SIGN_TYPE))
                    .build()
                    .encData(map);
            String api=Property.getUrl(ConfigPath.YZH_ORDER_QUERY);
            Map<String, Object> result = HttpUtil.get(request, api);
            try {
                Response response = null;
                if("200".equals(com.zbkj.crmeb.cloudAccount.util.StringUtils.trim(result.get(XmlData.STATUSCODE)))){
                    response = JsonUtil.fromJson(com.zbkj.crmeb.cloudAccount.util.StringUtils.trim(result.get(XmlData.DATA)), Response.class);
                    Object obj= response.getData();
                    list.add(obj);
                }
                System.out.println(response);
            } catch (Exception e) {
                e.printStackTrace();
                continue;//跳过
            }
        }

        //返回
        return list;
    }

    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @author Mr.Zhang
    * @since 2020-05-11
    * @return List<UserExtract>
    */
    @Override
    public List<UserExtract> getList(UserExtractSearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //带 UserExtract 类的多条件查询
        LambdaQueryWrapper<UserExtract> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(!StringUtils.isBlank(request.getKeywords())){
            lambdaQueryWrapper.and(i -> i.
                    or().like(UserExtract::getWechat, request.getKeywords()).   //微信号
                    or().like(UserExtract::getRealName, request.getKeywords()). //名称
                    or().like(UserExtract::getBankCode, request.getKeywords()). //银行卡
                    or().like(UserExtract::getBankAddress, request.getKeywords()). //开户行
                    or().like(UserExtract::getAlipayCode, request.getKeywords()). //支付宝
                    or().like(UserExtract::getFailMsg, request.getKeywords()) //失败原因
            );
        }

        //用户ID
        if(request.getUid()!=null && request.getUid() > 0 ){
            lambdaQueryWrapper.eq(UserExtract::getUid,request.getUid());
        }

        //关联类型
        if(request.getLinkType() > 0){
            lambdaQueryWrapper.eq(UserExtract::getLinkType, request.getLinkType());
        }

        //提现状态
        if(request.getStatus() != null){
            lambdaQueryWrapper.eq(UserExtract::getStatus, request.getStatus());
        }

        //提现方式
        if(!StringUtils.isBlank(request.getExtractType())){
            lambdaQueryWrapper.eq(UserExtract::getExtractType, request.getExtractType());
        }

        //时间范围
        if(StringUtils.isNotBlank(request.getDateLimit())){
            dateLimitUtilVo dateLimit = DateUtil.getDateLimit(request.getDateLimit());
            lambdaQueryWrapper.between(UserExtract::getCreateTime, dateLimit.getStartTime(), dateLimit.getEndTime());
        }

        //按创建时间降序排列
        lambdaQueryWrapper.orderByDesc(UserExtract::getCreateTime, UserExtract::getId);

        List<UserExtract> extractList = dao.selectList(lambdaQueryWrapper);
        if (CollUtil.isEmpty(extractList)) {
            return extractList;
        }

        List<Integer> uidList = extractList.stream().map(o -> o.getUid()).distinct().collect(Collectors.toList());
        HashMap<Integer, User> userMap = userService.getMapListInUid(uidList);
        for (UserExtract userExtract : extractList) {
            userExtract.setNickName(Optional.ofNullable(userMap.get(userExtract.getUid()).getNickname()).orElse(""));
        }
        return extractList;
    }

    /**
     * 提现总金额
     * 总佣金 = 已提现佣金 + 未提现佣金
     * 已提现佣金 = 用户成功提现的金额
     * 未提现佣金 = 用户未提现的佣金 = 可提现佣金 + 冻结佣金 = 用户佣金
     * 可提现佣金 = 包括解冻佣金、提现未通过的佣金 = 用户佣金 - 冻结期佣金
     * 待提现佣金 = 待审核状态的佣金
     * 冻结佣金 = 用户在冻结期的佣金，不包括退回佣金
     * 退回佣金 = 因退款导致的冻结佣金退回
     */
    @Override
    public BalanceResponse getBalance(String dateLimit) {
        String startTime = "";
        String endTime = "";
        if(StringUtils.isNotBlank(dateLimit)){
            dateLimitUtilVo dateRage = DateUtil.getDateLimit(dateLimit);
            startTime = dateRage.getStartTime();
            endTime = dateRage.getEndTime();
        }

        // 已提现
        BigDecimal withdrawn = getWithdrawn(startTime, endTime);
        // 待提现(审核中)
        BigDecimal toBeWithdrawn = getWithdrawning(startTime, endTime);

        // 佣金总金额（单位时间）
        BigDecimal commissionTotal = userBrokerageRecordService.getTotalSpreadPriceBydateLimit(dateLimit,BrokerageRecordConstants.BROKERAGE_RECORD_TYPE_ADD);
        // 单位时间消耗的佣金
        BigDecimal subWithdarw = userBrokerageRecordService.getTotalSpreadPriceBydateLimit(dateLimit,BrokerageRecordConstants.BROKERAGE_RECORD_TYPE_SUB);
        // 未提现
        BigDecimal unDrawn = commissionTotal.subtract(subWithdarw);
        return new BalanceResponse(withdrawn, unDrawn, commissionTotal, toBeWithdrawn);
    }


    /**
     * 提现总金额
     * @author Mr.Zhang
     * @since 2020-05-11
     * @return BalanceResponse
     */
    @Override
    public BigDecimal getWithdrawn(String startTime, String endTime) {
        return getSum(null, 1, startTime, endTime);
    }

    /**
     * 审核中总金额
     * @author Mr.Zhang
     * @since 2020-05-11
     * @return BalanceResponse
     */
    @Override
    public BigDecimal getWithdrawning(String startTime, String endTime) {
        return getSum(null, 0, startTime, endTime);
    }

    @Override
    public Boolean create(UserExtractRequest request, Integer userId) {
        //添加判断，提现金额不能小于10元
        BigDecimal ten = new BigDecimal(10);
        if (request.getExtractPrice().compareTo(ten) < 0) {
            throw new CrmebException("最低提现金额10元");
        }

        //看是否有足够的金额可提现
        User user = userService.getById(userId);
        BigDecimal toBeWithdrawn = user.getBrokeragePrice();//提现总金额
        BigDecimal freeze = getFreeze(userId); //冻结的佣金
        BigDecimal money = toBeWithdrawn.subtract(freeze); //可提现总金额
        if(money.compareTo(ZERO) < 1){
            throw new CrmebException("您当前没有金额可以提现");
        }

        // 验证提现金额
        int result = money.compareTo(request.getExtractPrice());
        if(result < 0){
            throw new CrmebException("你当前最多可提现 " + toBeWithdrawn + "元");
        }

        // 提现记录
        UserExtract userExtract = new UserExtract();    // 佣金提现申请->提现记录
        userExtract.setUid(userId);
        BeanUtils.copyProperties(request, userExtract);
        userExtract.setBalance(toBeWithdrawn.subtract(request.getExtractPrice()));

        //存入银行名称
        //userExtract.setBankName(request.getBankName());
        if (StrUtil.isNotBlank(userExtract.getBankName())) {
            userExtract.setBankName(systemAttachmentService.clearPrefix(userExtract.getBankName()));
        }

        // 微信小程序订阅提现通知
        WechatSendMessageForCash cash = new WechatSendMessageForCash(
                "提现申请成功",request.getExtractPrice()+"",request.getBankName()+request.getBankCode(),
                DateUtil.nowDateTimeStr(),"暂无",request.getRealName(),"0",request.getExtractType(),"提现",
                "暂无",request.getExtractType(),"暂无",request.getRealName()
        );
        wechatSendMessageForMinService.sendCashMessage(cash,userId);

        // 保存提现记录
        this.save(userExtract);

        // 扣除用户总金额
        return userService.updateBrokeragePrice(user, toBeWithdrawn.subtract(request.getExtractPrice()));
    }

    /**
     * 冻结的佣金
     * @author Mr.Zhang
     * @since 2020-06-08
     * @return Boolean
     */
    @Override
    public BigDecimal getFreeze(Integer userId) {
        String time = systemConfigService.getValueByKey(Constants.CONFIG_KEY_STORE_BROKERAGE_EXTRACT_TIME);
        if (StrUtil.isBlank(time)) {
            return BigDecimal.ZERO;
        }
        String endTime = DateUtil.nowDateTime(Constants.DATE_FORMAT);
        String startTime = DateUtil.addDay(DateUtil.nowDateTime(), -Integer.parseInt(time), Constants.DATE_FORMAT);
        String date = startTime + "," + endTime;
        //在冻结期的资金
        BigDecimal getSum = userBillService.getSumBigDecimal(1, userId, Constants.USER_BILL_CATEGORY_BROKERAGE_PRICE, date, null);
        return getSum;
    }

    /**
     * 根据状态获取总额
     * @return BigDecimal
     */
    private BigDecimal getSum(Integer userId, int status, String startTime, String endTime) {
        LambdaQueryWrapper<UserExtract> lqw = Wrappers.lambdaQuery();
        if(null != userId) {
            lqw.eq(UserExtract::getUid,userId);
        }
        lqw.eq(UserExtract::getStatus,status);
        if(StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)){
            lqw.between(UserExtract::getCreateTime, startTime, endTime);
        }
        List<UserExtract> userExtracts = dao.selectList(lqw);
        BigDecimal sum = ZERO;
        if(CollUtil.isNotEmpty(userExtracts)) {
            sum = userExtracts.stream().map(UserExtract::getExtractPrice).reduce(ZERO, BigDecimal::add);
        }
        return sum;
    }

    /**
     * 获取用户对应的提现数据
     * @param userId 用户id
     * @return 提现数据
     */
    @Override
    public UserExtractResponse getUserExtractByUserId(Integer userId) {
        QueryWrapper<UserExtract> qw = new QueryWrapper<>();
        qw.select("SUM(extract_price) as extract_price,count(id) as id, uid");
        qw.ge("status", 1);
        qw.eq("uid",userId);
        qw.groupBy("uid");
        UserExtract ux = dao.selectOne(qw);
        UserExtractResponse uexr = new UserExtractResponse();
//        uexr.setEuid(ux.getUid());
        if(null != ux){
            uexr.setExtractCountNum(ux.getId()); // 这里的id其实是数量，借变量传递
            uexr.setExtractCountPrice(ux.getExtractPrice());
        }else{
            uexr.setExtractCountNum(0); // 这里的id其实是数量，借变量传递
            uexr.setExtractCountPrice(ZERO);
        }

        return uexr;
    }

    /**
     * 根据用户id集合获取对应提现用户集合
     * @param userIds 用户id集合
     * @return 提现用户集合
     */
    @Override
    public List<UserExtract> getListByUserIds(List<Integer> userIds) {
        LambdaQueryWrapper<UserExtract> lqw = new LambdaQueryWrapper<>();
        lqw.in(UserExtract::getUid, userIds);
        return dao.selectList(lqw);
    }

    @Override
    public Boolean updateStatus(Integer id, Integer status, String backMessage) {
        //验证状态类型
        if(status == -1 && StringUtils.isBlank(backMessage)) throw new CrmebException("驳回时请填写驳回原因");

        //得到-用户提现记录
        UserExtract userExtract = getById(id);
        if (ObjectUtil.isNull(userExtract)) {
            throw new CrmebException("提现申请记录不存在");
        }
        if (userExtract.getStatus() != Constants.USER_EXTRACT_STATUS_SQZ) {
            throw new CrmebException("提现申请已处理过");
        }

        // 得到-提现用户信息
        User user = userService.getById(userExtract.getUid());
        if (ObjectUtil.isNull(user)) {
            throw new CrmebException("提现用户数据异常");
        }

        //定义变量-执行结果
        Boolean execute = false;

        //参数赋值
        userExtract.setStatus(status);
        userExtract.setFailMsg(backMessage);
        userExtract.setUpdateTime(cn.hutool.core.date.DateUtil.date());

        //验证提现状态
        if (status == -1) {
            //提现审核不通过退还佣金记录
            UserBrokerageRecord brokerageRecord = userBrokerageRecordService.getUserBrokerageRecord(    // 提现审核(旧)-不通过退还佣金记录
                    user.getUid(),userExtract.getId().toString(),
                    BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_EXTRACT,
                    BrokerageRecordConstants.BROKERAGE_RECORD_TYPE_3,
                    BrokerageRecordConstants.BROKERAGE_RECORD_TYPE_ADD,
                    userExtract.getExtractPrice(),user.getBrokeragePrice().add(userExtract.getExtractPrice()),
                    0,0L
            );

            //执行
            execute = transactionTemplate.execute(e -> {
                this.updateById(userExtract);
                userBrokerageRecordService.save(brokerageRecord);
                userService.operationBrokerage(userExtract.getUid(), userExtract.getExtractPrice(), user.getBrokeragePrice(), "add");
                return Boolean.TRUE;
            });
        }else if (status == 1) {
            // 审核通过-得到佣金提现申请记录-并更新状态
            UserBrokerageRecord brokerageRecord = userBrokerageRecordService.getByLinkIdAndLinkType(    // 审核通过-得到佣金提现申请记录-并更新状态
                    userExtract.getId().toString(),
                    BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_EXTRACT);

            //验证非空
            if (ObjectUtil.isNull(brokerageRecord)) {
                throw new CrmebException("对应的佣金记录不存在");
            }else{
                brokerageRecord.setStatus(BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_COMPLETE);
            }

            //执行
            execute = transactionTemplate.execute(e -> {
                this.updateById(userExtract);
                userBrokerageRecordService.updateById(brokerageRecord);
                return Boolean.TRUE;
            });
        }
        return execute;
    }

    @Override
    public PageInfo<UserExtractRecordResponse> getPageInfo(UserExtractSearchRequest request,Integer userId, PageParamRequest pageParamRequest){
        //得到用户ID
        Integer uid=userId;
        if(uid == null){
            uid=userService.getUserIdException();
        }

        //得到-提现记录实体-分页对象
        Page<UserExtract> userExtractPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //实例化-查询对象
        QueryWrapper<UserExtract> queryWrapper = new QueryWrapper<>();

        //条件-用户id
        queryWrapper.eq("uid", uid);

        //条件-关联类型
        switch (request.getLinkType()){
            case Constants.USER_BALANCE_TIXIN_LINKTYPE_2:
                queryWrapper.eq("link_type",request.getLinkType());
                break;
        }

        //条件-状态
        if(request.getStatus() !=null){
            queryWrapper.eq("status",request.getStatus());
        }

        //排序-最近七天
        queryWrapper.groupBy("left(create_time, 7)");
        queryWrapper.orderByDesc("left(create_time, 7)");

        //得到-提现记录list
        List<UserExtract> list = dao.selectList(queryWrapper);

        //验证-非空
        if(CollUtil.isEmpty(list)){
            return new PageInfo<>();
        }

        //实例化-用户提现记录-响应list集合
        ArrayList<UserExtractRecordResponse> userExtractRecordResponseList = CollectionUtil.newArrayList();

        //循环处理-用户提现记录-响应list集合
        for (UserExtract userExtract : list) {
            //将-创建时间-转换为指定格式字符串:-月
            String date = DateUtil.dateToStr(userExtract.getCreateTime(), Constants.DATE_FORMAT_MONTH);

            //根据用户id、日期字符串-得到提现记录list集合
            List<UserExtract> userExtractList=this.getListByMonth(uid, date);

            //设置-用户提现记录实体参数
            UserExtractRecordResponse userExtractRecordResponse = new UserExtractRecordResponse();
            userExtractRecordResponse.setDate(date);
            userExtractRecordResponse.setList(userExtractList);

            //添加到-用户提现记录-响应list集合
            userExtractRecordResponseList.add(userExtractRecordResponse);
        }

        //返回-用户提现记录-响应list集合
        return CommonPage.copyPageInfo(userExtractPage, userExtractRecordResponseList);
    }

    /**
     * 根据用户id、日期字符串-得到提现记录list集合
     * @param userId    用户id
     * @param date      日期字符串
     * @return  提现记录list集合
     */
    private List<UserExtract> getListByMonth(Integer userId, String date) {
        QueryWrapper<UserExtract> queryWrapper = new QueryWrapper<>();
        //queryWrapper.select("id", "extract_price", "status", "create_time", "update_time");
        queryWrapper.select(" * ");
        queryWrapper.eq("uid", userId);
        queryWrapper.apply(StrUtil.format(" left(create_time, 7) = '{}'", date));
        queryWrapper.orderByDesc("create_time");
        return dao.selectList(queryWrapper);
    }

    /**
     * 获取用户提现总金额
     * @param userId
     * @return
     */
    @Override
    public BigDecimal getExtractTotalMoney(Integer userId){
        return getSum(userId, 1, null, null);
    }

    @Override
    public Boolean extractApply(UserExtractRequest request) {
        //添加判断，提现金额不能后台配置金额
        String value = systemConfigService.getValueByKeyException(Constants.CONFIG_EXTRACT_MIN_PRICE);
        BigDecimal ten = new BigDecimal(value);
        if (request.getExtractPrice().compareTo(ten) < 0) {
            throw new CrmebException(StrUtil.format("最低提现金额{}元", ten));
        }

        //得到用户信息
        User user = userService.getInfo();
        if (ObjectUtil.isNull(user)) {
            throw new CrmebException("提现用户信息异常");
        }

        //可提现总金额
        BigDecimal money = user.getBrokeragePrice();
        if(money.compareTo(ZERO) < 1){
            throw new CrmebException("您当前没有金额可以提现");
        }

        //只能提现金额
        if(money.compareTo(request.getExtractPrice()) < 0){
            throw new CrmebException("你当前最多可提现 " + money + "元");
        }

        //转换-提现表
        UserExtract userExtract = new UserExtract();    // 提现申请公共接口(旧方式-手动打款)->提现记录
        BeanUtils.copyProperties(request, userExtract);
        userExtract.setUid(user.getUid());
        userExtract.setBalance(money.subtract(request.getExtractPrice()));

        //存入银行名称
        if (StrUtil.isNotBlank(userExtract.getBankName())) {
            userExtract.setBankName(systemAttachmentService.clearPrefix(userExtract.getBankName()));
        }

        //申请提现-佣金余额提现(旧方式(手动打款))-佣金记录
        UserBrokerageRecord brokerageRecord = userBrokerageRecordService.getUserBrokerageRecord(    // 申请提现-佣金余额提现(旧方式(手动打款))
                user.getUid(),userExtract.getId().toString(),
                BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_EXTRACT,
                BrokerageRecordConstants.BROKERAGE_RECORD_TYPE_2,
                BrokerageRecordConstants.BROKERAGE_RECORD_TYPE_SUB,
                userExtract.getExtractPrice(),money.add(userExtract.getExtractPrice()),
                0,0L
        );

        //执行
        Boolean execute = transactionTemplate.execute(e -> {
            // 保存提现记录
            this.save(userExtract);
            // 修改用户佣金
            userService.operationBrokerage(user.getUid(), userExtract.getExtractPrice(), money, "sub");
            // 添加佣金记录
            userBrokerageRecordService.save(brokerageRecord);
            return Boolean.TRUE;
        });

        //todo 提现申请通知
        // 微信小程序订阅提现通知
        WechatSendMessageForCash cash = new WechatSendMessageForCash(
                "提现申请成功",request.getExtractPrice()+"",request.getBankName()+request.getBankCode(),
                DateUtil.nowDateTimeStr(),"暂无",request.getRealName(),"0",request.getExtractType(),"提现",
                "暂无",request.getExtractType(),"暂无",request.getRealName()
        );
        wechatSendMessageForMinService.sendCashMessage(cash,user.getUid());

        //返回结果
        return execute;
    }
}

