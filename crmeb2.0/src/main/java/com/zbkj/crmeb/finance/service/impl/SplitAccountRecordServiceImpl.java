package com.zbkj.crmeb.finance.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeOrderSettleRequest;
import com.alipay.api.request.AlipayTradeRoyaltyRelationBindRequest;
import com.alipay.api.response.AlipayTradeOrderSettleResponse;
import com.alipay.api.response.AlipayTradeRoyaltyRelationBindResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CommonPage;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.constants.ConstantsFromID;
import com.constants.PayConstants;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.utils.DateUtil;
import com.utils.RedisUtil;
import com.utils.WxPayUtil;
import com.zbkj.crmeb.finance.dao.SplitAccountRecordDao;
import com.zbkj.crmeb.finance.model.SplitAccountRecord;
import com.zbkj.crmeb.finance.request.SplitAccountRecordSearchRequest;
import com.zbkj.crmeb.finance.service.SplitAccountRecordService;
import com.zbkj.crmeb.regionalAgency.model.RegionalAgency;
import com.zbkj.crmeb.regionalAgency.service.RegionalAgencyService;
import com.zbkj.crmeb.retailer.model.Retailer;
import com.zbkj.crmeb.retailer.service.RetailerService;
import com.zbkj.crmeb.store.model.StoreOrder;
import com.zbkj.crmeb.store.model.Supplier;
import com.zbkj.crmeb.store.service.StoreOrderService;
import com.zbkj.crmeb.store.service.SupplierService;
import com.zbkj.crmeb.system.service.SystemConfigService;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.model.UserBill;
import com.zbkj.crmeb.user.service.UserBillService;
import com.zbkj.crmeb.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分账记录表-service层接口实现类
 * @author: 零风
 * @CreateDate: 2022/1/21 10:37
 */
@Service
public class SplitAccountRecordServiceImpl extends ServiceImpl<SplitAccountRecordDao, SplitAccountRecord> implements SplitAccountRecordService {

    private static final Logger logger = LoggerFactory.getLogger(SplitAccountRecordServiceImpl.class);

    @Resource
    private SplitAccountRecordDao dao;

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RegionalAgencyService regionalAgencyService;

    @Autowired
    private RetailerService retailerService;

    @Autowired
    private UserBillService userBillService;

    @Autowired
    private UserService userService;

    @Autowired
    private SupplierService supplierService;

    public static void main(String[] args) {
        Boolean profitSharingJMZIsOpen=Boolean.getBoolean(String.valueOf(""));
        BigDecimal amount =BigDecimal.ZERO;
        BigDecimal pay=new BigDecimal(5.00);
        amount=amount.divide(pay);
        BigDecimal pay2=new BigDecimal("5.00");
        amount= new BigDecimal("10").divide(new BigDecimal(100)).multiply(pay);
        System.out.println(amount);
        System.out.println(amount.doubleValue());
        amount= new BigDecimal("10").divide(new BigDecimal(100)).multiply(pay2);
        System.out.println(amount);
        System.out.println(amount.doubleValue());
        System.out.println("分账金额为:" + amount.multiply(new BigDecimal("100")).intValue());
    }

    @Override
    public Boolean payProfitsharingWithdrawalAccount(User user,Integer orderId,String type,BigDecimal price) {
        try {
            //保存-分账结算记录
            UserBill userBill = userBillService.getUserBill(    // 分账结算记录
                    user.getUid(),
                    String.valueOf(orderId),
                    Constants.USER_BILL_PM_1,
                    Constants.USER_BILL_CATEGORY_MONEY,
                    type,
                    price,
                    user.getNowMoney(),
                    ""
            );
            userBillService.save(userBill);

            //更新-管理用户余额
            userService.operationNowMoney(user.getUid(), price, user.getNowMoney(), Constants.ADD_STR);
            return Boolean.TRUE;
        }catch (Exception e){
            e.printStackTrace();
            logger.error("分账结算发生错误:");
            logger.error(e.getMessage());
            return Boolean.FALSE;
        }
    }

    @Override
    public SplitAccountRecord payProfitSharingAlipay(StoreOrder storeOrder) {
        //分账默认信息
        SplitAccountRecord splitAccountRecord=new SplitAccountRecord();
        splitAccountRecord.setOrderid(storeOrder.getId());
        splitAccountRecord.setType(Constants.SplitAccountRecord_TYPE_2);

        //得到配置项
        HashMap<String, String> hashMap = systemConfigService.info(ConstantsFromID.INT_CONFIG_FORM_ZFB);
        if(hashMap == null) {
            splitAccountRecord.setDescription("支付宝分账-添加接收方 || 支付宝支付配置为空! 可能是因为没有设置费率");
            logger.error(splitAccountRecord.getDescription());
            return splitAccountRecord;
        }

        //得到接收方
        List<Map<String,Object>> paramList=new ArrayList<>();
        List<Map<String,Object>> mapList=this.getPayProfitSharingReceiver(storeOrder);
        String outRequestNo=DateUtil.nowDateTime(Constants.DATE_TIME_FORMAT_NUM);
        String tradeNo= storeOrder.getTransactionId();
        StringBuffer paramSB=new StringBuffer();
        paramSB.append("{");
        paramSB.append("\"out_request_no\":\"").append(outRequestNo).append("\",");
        paramSB.append("\"trade_no\":\"").append(tradeNo).append("\",");
        paramSB.append("\"royalty_parameters\":[");
        int tt=0;
        Boolean is=Boolean.FALSE;
        for (Map<String,Object> objMap:mapList) { //循环处理接收方信息
            //验证-分账费率是否全部转入提现账户
            is = this.getPayProfitsharingIsAccount((User) objMap.get("user"),storeOrder, objMap);
            if (is) continue;

            //拼接参数
            paramSB.append("{");
            paramSB.append("\"trans_in\":\"").append(objMap.get("accountAlipay")).append("\",");
            paramSB.append("\"amount\":").append(new BigDecimal(String.valueOf(objMap.get("amount"))).doubleValue()).append(",");
            paramSB.append("\"trans_in_type\":\"").append(objMap.get("accountTypeAlipay")).append("\",");
            paramSB.append("\"desc\":\"支付宝分账\"");
            tt++;
            if(tt == mapList.size()) {
                paramSB.append("}");
            }else{
                paramSB.append("},");
            }
            //"[{\"trans_in\":\"18585094270\",\"amount\":0.5," + "\"trans_in_type\":\"loginName\"}]}");

            Map<String,Object> map=new HashMap<>();
            map.put("trans_in",objMap.get("accountAlipay"));
            map.put("trans_in_type",objMap.get("accountTypeAlipay"));
            map.put("amount",objMap.get("amount"));
            map.put("trans_in_name",objMap.get("accountAlipayRealName"));
            map.put("desc","支付宝分账");
            map.put("royalty_type","transfer");
            map.put("trans_out","2088241500822091");
            map.put("trans_out_type","userId");
            map.put("amount_percentage","500");
            map.put("royalty_scene","其他");
            paramList.add(map);
        }
        paramSB.append("]").append("}");

        //验证是否已通过绑定账户结算
        if(is){
            splitAccountRecord.setStatus(2);
            splitAccountRecord.setDescription("支付宝分账 || 分账转移,已通过绑定账户结算！");
            logger.error(splitAccountRecord.getDescription());
            return splitAccountRecord;
        }

        //验证非空
        if(paramList.size() == 0){
            splitAccountRecord.setStatus(2);
            splitAccountRecord.setDescription("支付宝分账 || 分账失败,没有接收方！");
            logger.error(splitAccountRecord.getDescription());
            return splitAccountRecord;
        }

        //公共参数
        AlipayClient alipayClient = new DefaultAlipayClient(
                "https://openapi.alipay.com/gateway.do",
                hashMap.get("zfb_app_zf_appid"),
                hashMap.get("zfb_app_zf_secret_key"),
                "json",
                "utf-8",
                hashMap.get("zfb_app_zf_public_key"),
                "RSA2");

        //动态添加接收方
        Boolean bl = this.payProfitSharingAlipayAddReceiver(alipayClient,paramList);
        if(!bl){
            splitAccountRecord.setStatus(2);
            splitAccountRecord.setDescription("支付宝分账 || 分账失败,添加接收方失败！");
            logger.error(splitAccountRecord.getDescription());
            return splitAccountRecord;
        }

        //请求参数
        AlipayTradeOrderSettleRequest request = new AlipayTradeOrderSettleRequest();
        request.setBizContent(paramSB.toString());

        //结果
        StringBuffer stringBuffer=new StringBuffer("支付宝支付订单分账 || ");
        try {
            AlipayTradeOrderSettleResponse response = alipayClient.execute(request);
            logger.info(response.getSubMsg());
            System.out.println(response);

            if(response.isSuccess()){
                logger.info("支付宝支付订单分账 || 分账成功！");
                stringBuffer.append("分账成功！");
                splitAccountRecord.setStatus(1);
            } else {
                logger.error("支付宝支付订单分账 || 分账失败！");
                stringBuffer.append("分账失败！未知原因!");
            }
        }catch (Exception e){
            logger.error("支付宝分账 || 分账失败，发生错误: "+e.getMessage());
            stringBuffer.append("分账失败！发生错误: "+e.getMessage());
        }

        //返回分账信息
        splitAccountRecord.setDescription(stringBuffer.toString());
        logger.error(splitAccountRecord.getDescription());
        return splitAccountRecord;
    }

    @Override
    public List<Map<String, Object>> getPayProfitSharingReceiver(StoreOrder storeOrder) {
        //得到-分账接收方信息
        List<Map<String,Object>> userList=new ArrayList<>();
        Map<String,Object> map=null;
        BigDecimal amount=BigDecimal.ZERO;
        User user=null;
        String name = "";

        //得到-分账固定账户配置项
        HashMap<String, String> hashMap = systemConfigService.info(ConstantsFromID.INT_CONFIG_FORM_JMZ_ProfitSharing);
        if(hashMap == null) {
            logger.error("分账固定账户配置为空!");
        }else{
            //验证是否开启
            Boolean profitSharingJMZIsOpen=Boolean.getBoolean(String.valueOf(hashMap.get("profitSharingJMZIsOpen")));
            if(profitSharingJMZIsOpen){
                //得到绑定的平台用户
                user=userService.getById(hashMap.get("profitSharingJMZuid"));
                if(user != null ){
                    //得到-分账比例
                    String rate = hashMap.get("profitSharingJMZrate");

                    //计算分账金额
                    amount = new BigDecimal(rate).divide(new BigDecimal(100)).multiply(storeOrder.getCost());

                    //设置接收方
                    map=new HashMap<>();
                    map.put("user",user);
                    map.put("name",user.getRealName());
                    map.put("amount",amount);
                    map.put("rate",rate);
                    map.put("accountTypeWeixin",hashMap.get("profitSharingJMZaccountTypeWeixin"));
                    map.put("accountWeixin",hashMap.get("profitSharingJMZaccountWeixin"));
                    map.put("accountWeixinRealName",hashMap.get("profitSharingJMZaccountWeixinRealName"));
                    map.put("accountTypeAlipay",hashMap.get("profitSharingJMZaccountTypeAlipay"));
                    map.put("accountAlipay",hashMap.get("profitSharingJMZaccountAlipay"));
                    map.put("accountAlipayRealName",hashMap.get("profitSharingJMZaccountAlipayRealName"));
                    map.put("type",Constants.USER_BILL_TYPE_SUPPLIER_ORDER_settlement);
                    map.put("price",storeOrder.getPayPrice());
                    map.put("cost",storeOrder.getCost());
                    userList.add(map);
                }else{
                    logger.error("分账固定账户未绑定平台账户!");
                }
            }else{
                logger.info("分账固定账户未开启!");
            }
        }

        //验证订单类型-区别接收方
        switch (storeOrder.getType()){
            case Constants.ORDER_TYPE_0: break;
            case Constants.ORDER_TYPE_1: break;
            case Constants.ORDER_TYPE_2:
                //得到-区域代理信息
                RegionalAgency regionalAgency = regionalAgencyService.getById(storeOrder.getMerId());
                if(regionalAgency == null)break;
                user = userService.getById(regionalAgency.getUid());
                name = regionalAgency.getRaName();

                //计算分账金额
                if(regionalAgency.getRate()==null || regionalAgency.getRate().compareTo(BigDecimal.ZERO) < 1)break;
                amount = regionalAgency.getRate().divide(new BigDecimal(100)).multiply(storeOrder.getCost());

                //设置接收方
                map=new HashMap<>();
                map.put("user",user);
                map.put("amount",amount);
                map.put("name",name);
                map.put("rate",regionalAgency.getRate());
                map.put("accountTypeWeixin",regionalAgency.getAccountTypeWeixin());
                map.put("accountWeixin",regionalAgency.getAccountWeixin());
                map.put("accountWeixinRealName",regionalAgency.getAccountWeixinRealName());
                map.put("accountTypeAlipay",regionalAgency.getAccountTypeAlipay());
                map.put("accountAlipay",regionalAgency.getAccountAlipay());
                map.put("accountAlipayRealName",regionalAgency.getAccountAlipayRealName());
                map.put("type",Constants.USER_BILL_TYPE_RA_ORDER_settlement);
                map.put("price",storeOrder.getPayPrice());
                map.put("cost",storeOrder.getCost());
                userList.add(map);
                break;
            case Constants.ORDER_TYPE_3:
                //得到-零售商信息
                Retailer retailer=retailerService.getById(storeOrder.getMerId());
                if(retailer == null)break;
                user = userService.getById(retailer.getUid());
                name = retailer.getReName();

                //计算分账金额
                if(retailer.getRate()==null || retailer.getRate().compareTo(BigDecimal.ZERO) < 1)break;
                amount = retailer.getRate().divide(new BigDecimal(100)).multiply(storeOrder.getCost());

                //设置接收方
                map=new HashMap<>();
                map.put("user",user);
                map.put("amount",amount);
                map.put("name",name);
                map.put("rate",retailer.getRate());
                map.put("accountTypeWeixin",retailer.getAccountTypeWeixin());
                map.put("accountWeixin",retailer.getAccountWeixin());
                map.put("accountWeixinRealName",retailer.getAccountWeixinRealName());
                map.put("accountTypeAlipay",retailer.getAccountTypeAlipay());
                map.put("accountAlipay",retailer.getAccountAlipay());
                map.put("accountAlipayRealName",retailer.getAccountAlipayRealName());
                map.put("type",Constants.USER_BILL_TYPE_RETAILER_ORDER);
                map.put("price",storeOrder.getPayPrice());
                map.put("cost",storeOrder.getCost());
                userList.add(map);

                //得到-区域代理信息
                RegionalAgency ra = regionalAgencyService.getById(retailer.getRaId());
                if(ra == null)break;
                user = userService.getById(ra.getUid());
                name = ra.getRaName();

                //计算分账金额
                if(ra.getRate()==null || ra.getRate().compareTo(BigDecimal.ZERO) < 1)break;
                amount = ra.getRate().divide(new BigDecimal(100)).multiply(storeOrder.getCost());

                //设置接收方
                map=new HashMap<>();
                map.put("user",user);
                map.put("amount",amount);
                map.put("name",name);
                map.put("rate",ra.getRate());
                map.put("accountTypeWeixin",ra.getAccountTypeWeixin());
                map.put("accountWeixin",ra.getAccountWeixin());
                map.put("accountWeixinRealName",ra.getAccountWeixinRealName());
                map.put("accountTypeAlipay",ra.getAccountTypeAlipay());
                map.put("accountAlipay",ra.getAccountAlipay());
                map.put("accountAlipayRealName",ra.getAccountAlipayRealName());
                map.put("type",Constants.USER_BILL_TYPE_RA_ORDER_settlement);
                map.put("price",storeOrder.getPayPrice());
                map.put("cost",storeOrder.getCost());
                userList.add(map);
                break;
            case Constants.ORDER_TYPE_4:
                //得到-供应商信息
                Supplier supplier=supplierService.getById(storeOrder.getMerId());
                if(supplier == null)break;

                //得到-供应商管理用户信息
                user=userService.getById(supplier.getUid());
                if(user == null)break;
                name = supplier.getSuppName();

                //计算分账金额
                if(supplier.getRate()==null || supplier.getRate().compareTo(BigDecimal.ZERO) < 1)break;
                amount = supplier.getRate().divide(new BigDecimal(100)).multiply(storeOrder.getCost());

                //设置接收方
                map=new HashMap<>();
                map.put("user",user);
                map.put("amount",amount);
                map.put("name",name);
                map.put("rate",supplier.getRate());
                map.put("accountTypeWeixin",supplier.getAccountTypeWeixin());
                map.put("accountWeixin",supplier.getAccountWeixin());
                map.put("accountWeixinRealName",supplier.getAccountWeixinRealName());
                map.put("accountTypeAlipay",supplier.getAccountTypeAlipay());
                map.put("accountAlipay",supplier.getAccountAlipay());
                map.put("accountAlipayRealName",supplier.getAccountAlipayRealName());
                map.put("type",Constants.USER_BILL_TYPE_SUPPLIER_ORDER_settlement);
                map.put("price",storeOrder.getPayPrice());
                map.put("cost",storeOrder.getCost());
                userList.add(map);
                break;
            default: break;
        }
        return userList;
    }

    @Override
    public SplitAccountRecord payProfitSharingIntegral(StoreOrder storeOrder) {
        //分账默认信息
        SplitAccountRecord splitAccountRecord=new SplitAccountRecord();
        splitAccountRecord.setOrderid(storeOrder.getId());
        splitAccountRecord.setType(Constants.SplitAccountRecord_TYPE_3);

        //得到-分账接收方
        List<Map<String, Object>> userList = this.getPayProfitSharingReceiver(storeOrder);
        if(userList == null || userList.size() <= 0){
            splitAccountRecord.setStatus(2);
            splitAccountRecord.setDescription("积分支付订单分账 || 没有分账接收方! 可能是因为没有设置费率");
            logger.error(splitAccountRecord.getDescription());
            return splitAccountRecord;
        }

        //循环处理接收方信息
        Boolean is=Boolean.FALSE;
        for (Map<String,Object> objMap:userList) {
            //分账对象
            User user = (User) objMap.get("user");

            //验证-分账费率是否全部转入提现账户
            is = this.getPayProfitsharingIsAccount(user,storeOrder, objMap);
            if (is) continue;

            //积分支付订单分账
            //分账金额
            BigDecimal amount = new BigDecimal(String.valueOf(objMap.get("amount")));
            UserBill userBill = userBillService.getUserBill(    // 积分支付订单分账-账单记录
                    user.getUid(),storeOrder.getId().toString(),
                    Constants.USER_BILL_PM_1,
                    Constants.USER_BILL_CATEGORY_MONEY,
                    Constants.USER_BILL_TYPE_payProfitSharingIntegral,
                    amount,user.getNowMoney(),
                    ""
            );
            userBillService.save(userBill);
            logger.info("积分支付订单分账 || "+objMap.get("name") +" || 分到"+ amount +"元！");
            userService.operationNowMoney(user.getUid(), amount, user.getNowMoney(), Constants.ADD_STR);//更新-账户余额
        }

        //验证是否已通过绑定账户结算
        if(is){
            splitAccountRecord.setStatus(2);
            splitAccountRecord.setDescription("积分支付订单分账 || 分账转移,已通过绑定账户结算！");
            logger.error(splitAccountRecord.getDescription());
            return splitAccountRecord;
        }

        //返回分账信息
        splitAccountRecord.setStatus(1);
        splitAccountRecord.setDescription("积分支付订单分账 || 分账成功!");
        return splitAccountRecord;
    }

    @Override
    public Boolean payProfitSharingAlipayAddReceiver(AlipayClient alipayClient,List<Map<String,Object>> mapList) {
        //分账接收方
        List<Map<String,Object>> paramMapList=new ArrayList<>();
        for (Map<String,Object> objMap:mapList) {
            Map<String,Object> map=new HashMap<>();
            map.put("type",objMap.get("trans_in_type"));
            map.put("account",objMap.get("trans_in"));
            map.put("name",objMap.get("trans_in_name"));
            map.put("memo",objMap.get("desc"));
            paramMapList.add(map);
        }

        //请求参数
        AlipayTradeRoyaltyRelationBindRequest request = new AlipayTradeRoyaltyRelationBindRequest();
        String obj=JSONObject.toJSONString(paramMapList);  //接收方参数
        request.setBizContent("{" +
                "  \"receiver_list\":" +obj+","+
                "  \"out_request_no\":\""+ DateUtil.nowDateTime(Constants.DATE_TIME_FORMAT_NUM) +"\"" +
                "}");
        try {
            //发送请求得到结果
            AlipayTradeRoyaltyRelationBindResponse response = alipayClient.execute(request);
            if(response.isSuccess()){
                System.out.println("调用成功");
                logger.info("支付宝分账-添加接收方 || 分账关系绑定成功!");
                return Boolean.TRUE;
            } else {
                logger.error("支付宝分账-添加接收方 || 分账关系绑定失败!");
                return Boolean.FALSE;
            }
        }catch (Exception e){
            logger.error("支付宝分账-添加接收方 || 分账关系绑定发生错误: "+e.getMessage());
            return Boolean.FALSE;
        }
    }

    @Override
    public void payProfitsharingTask() {
        //得到redsi-key
        String redisKey = Constants.ORDER_TASK_payProfitSharing; // 分账-分账订单redis-key
        //从缓存里读取-size
        Long size = redisUtil.getListSize(redisKey);
        logger.info("分账-payProfitsharingTask  | size:" + size);
        if(size < 1)return;

        //循环处理
        for (int i = 0; i < size; i++) {
            //如果10秒钟拿不到一个数据，那么退出循环
            Object data = redisUtil.getRightPop(redisKey, 10L);
            if (null == data) {
                continue;
            }

            try {
                //得到-订单信息
                StoreOrder storeOrder = storeOrderService.getById(Integer.valueOf(data.toString()));
                SplitAccountRecord splitAccountRecord=new SplitAccountRecord();
                if (storeOrder == null) {
                    splitAccountRecord.setStatus(2);
                    splitAccountRecord.setDescription("分账-payProfitsharingTask.task  || 订单不存在! orderId:" + data);
                    logger.error(splitAccountRecord.getDescription());
                } else {
                    //验证-订单支付类型
                    switch (storeOrder.getPayType()) {
                        case PayConstants.PAY_TYPE_WE_CHAT:
                            splitAccountRecord = this.payProfitsharingWeiXin(Integer.valueOf(data.toString()));
                            break;
                        case PayConstants.PAY_TYPE_ALI_PAY:
                            splitAccountRecord = this.payProfitSharingAlipay(storeOrder);
                            break;
                        case PayConstants.PAY_TYPE_YUE:
                        case PayConstants.PAY_TYPE_INTEGRAL:
                            splitAccountRecord = this.payProfitSharingIntegral(storeOrder);
                            break;
                        default:
                            splitAccountRecord.setStatus(2);
                            splitAccountRecord.setDescription("分账-payProfitsharingTask.task  || 该支付类型不支持分账! orderId:" + data);
                            logger.error(splitAccountRecord.getDescription());
                            return;
                    }

                    //验证-分账结果
                    if (splitAccountRecord.getStatus() == 0) {
                        logger.error("分账-payProfitsharingTask.task || 分账失败! 原因:" + splitAccountRecord.getDescription());
                        redisUtil.lPush(redisKey, data);//未执行成功-重新放入
                    }
                }

                //保存分账记录
                dao.insert(splitAccountRecord);
            }catch (Exception e){
                e.printStackTrace();
                logger.error("分账-payProfitsharingTask.task" + " | 发生错误 : " + e.getMessage());
                redisUtil.lPush(redisKey, data);//未执行成功-重新放入
            }
        }
    }

    @Override
    public SplitAccountRecord payProfitsharingWeiXin(Integer orderId) throws Exception {
        //分账默认信息
        SplitAccountRecord splitAccountRecord=new SplitAccountRecord();
        splitAccountRecord.setOrderid(orderId);
        splitAccountRecord.setType(Constants.SplitAccountRecord_TYPE_1);
        splitAccountRecord.setStatus(0);

        //得到-订单信息
        StoreOrder storeOrder=storeOrderService.getById(orderId);
        if(storeOrder == null) {
            splitAccountRecord.setStatus(2);
            splitAccountRecord.setDescription("微信分账-请求单次分账 || 订单不存在! ");
            logger.error(splitAccountRecord.getDescription());
            return splitAccountRecord;
        }

        //验证支付渠道
        String appId = "";
        String mchId = "";
        switch (storeOrder.getIsChannel()){
            case PayConstants.ORDER_PAY_CHANNEL_PUBLIC:    //公众号
            case PayConstants.ORDER_PAY_CHANNEL_H5:        //H5唤起微信支付
                appId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_ID);
                mchId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_MCH_ID);
                break;
            case PayConstants.ORDER_PAY_CHANNEL_PROGRAM:   //小程序
                appId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_ROUTINE_APP_ID);
                mchId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_ROUTINE_MCH_ID);
                break;
            case PayConstants.ORDER_PAY_CHANNEL_APP_IOS:       //微信App-ios
            case PayConstants.ORDER_PAY_CHANNEL_APP_ANDROID:   //微信App-安卓
                appId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_APP_ID);
                mchId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_MCH_ID);
                break;
            default:
                splitAccountRecord.setStatus(2);
                splitAccountRecord.setDescription("微信分账-请求单次分账 || 该支付渠道或方式不支持微信分账！orderId："+orderId);
                logger.info(splitAccountRecord.getDescription());
                return splitAccountRecord;
        }

        //基本参数
        Map<String,String> mapParam=new HashMap<>();
        mapParam.put("mch_id",mchId);
        mapParam.put("appid",appId);
        mapParam.put("nonce_str", WxPayUtil.getNonceStr());             //随机字符串
        mapParam.put("transaction_id",storeOrder.getTransactionId());   //微信支付号
        mapParam.put("out_order_no", new StringBuffer("FZ").append(storeOrder.getOrderId()).toString()); //商户分账单号
        Map<String,String> mapParamOK=new HashMap<>(); //用于分账完成QQ参数
        mapParamOK.putAll(mapParam);
        mapParamOK.put("description","分账成功！");

        //得到-接收方
        List<Map<String,Object>> mapList=new ArrayList<>();
        Map<String,Object> mapJSF=null;
        List<Map<String, Object>> userList = this.getPayProfitSharingReceiver(storeOrder);
        if(userList == null || userList.size()<=0){
            splitAccountRecord.setStatus(2);
            splitAccountRecord.setDescription("微信分账-请求单次分账 || 没有接收方！可能是因为没有设置费率！ ");
            logger.error(splitAccountRecord.getDescription());
            return splitAccountRecord;
        }

        //循环处理接收方
        Boolean is =Boolean.FALSE;
        for (Map<String, Object> s:userList) {
            //验证-分账费率是否转入提现账户
            is = this.getPayProfitsharingIsAccount((User) s.get("user"),storeOrder, s);
            if (is) continue;

            //设置收款方参数
            mapJSF=new HashMap<>();
            mapJSF.put("type",s.get("accountTypeWeixin"));
            mapJSF.put("account",s.get("accountWeixin"));
            mapJSF.put("description","微信分账");
            mapJSF.put("name",s.get("accountWeixinRealName"));
            Integer amount = new BigDecimal(s.get("amount").toString()).multiply(new BigDecimal("100")).intValue();
            mapJSF.put("amount",amount);

            //添加到list
            mapList.add(mapJSF);
        }

        //验证是否已通过绑定账户结算
        if(is){
            splitAccountRecord.setStatus(2);
            splitAccountRecord.setDescription("微信分账-请求单次分账 || 分账转移,已通过绑定账户结算！");
            logger.error(splitAccountRecord.getDescription());
            return splitAccountRecord;
        }

        //转成json
        String obj=JSONObject.toJSONString(mapList);
        mapParam.put("receivers", obj);

        //得到微信支付配置项
        HashMap<String, String> hashMap = systemConfigService.info(ConstantsFromID.INT_CONFIG_FORM_WX_XCX);
        if(hashMap == null) {
            splitAccountRecord.setDescription("微信分账-请求单次分账 || 证书路径为空！orderId："+orderId);
            logger.error(splitAccountRecord.getDescription());
            return splitAccountRecord;
        }

        //得到签名
        String appkey = hashMap.get("pay_routine_key");
        String sign1 = WxPayUtil.generateSignSHA256(mapParam,appkey);
        String sign2 = WxPayUtil.getSign(mapParam,appkey);
        mapParam.put("sign",sign1);

        //封装请求参数
        StringBuilder reqXmlStr = new StringBuilder();
        reqXmlStr.append("<xml>");
        reqXmlStr.append("<appid>" + mapParam.get("appid") + "</appid>");
        reqXmlStr.append("<mch_id>" + mapParam.get("mch_id") + "</mch_id>");
        reqXmlStr.append("<nonce_str>" + mapParam.get("nonce_str") + "</nonce_str>");
        reqXmlStr.append("<out_order_no>" + mapParam.get("out_order_no") + "</out_order_no>");
        reqXmlStr.append("<transaction_id>" + mapParam.get("transaction_id") + "</transaction_id>");
        reqXmlStr.append("<sign>" + mapParam.get("sign") + "</sign>");
        reqXmlStr.append("<receivers>" + mapParam.get("receivers") + "</receivers>");
        reqXmlStr.append("</xml>");
        logger.info("request-xml= " + reqXmlStr);

        //发起提现请求->得到响应结果
        String certPath = hashMap.get("pay_routine_certificate_path");// 微信商户证书路径, 根据实际情况填写 "D:\\lingfe\\demo\\apiclient_cert.p12"
        String url = "https://api.mch.weixin.qq.com/secapi/pay/profitsharing";
        String iResult = WxPayUtil.requestWxSplitAccount(url,certPath,reqXmlStr,mchId);
        //将结果转为map对象
        HashMap<String, Object> map =  WxPayUtil.processResponseXml(iResult);
        System.out.println(map);
        StringBuffer stringBufferMsg=new StringBuffer("微信分账-请求单次分账 || ");
        if(map == null){
            stringBufferMsg.append("响应结果为空");
        }else if("SUCCESS".equals(map.get("result_code"))&&"SUCCESS".equals(map.get("return_code"))){
            stringBufferMsg.append("分账成功！");
            splitAccountRecord.setStatus(1);
            this.payProfitsharingOK(mapParamOK, appkey, certPath);
        }else if("SUCCESS".equals(map.get("return_code"))){
            switch (map.get("err_code").toString()){
                case "AMOUNT_OVERDUE":
                    stringBufferMsg.append(map.get("err_code_des"));
                    break;
                default:
                    stringBufferMsg.append("请求成功但是未知原因导致未分账成功！");
                    break;
            }
            splitAccountRecord.setStatus(2);
        }else if("FAIL".equals(map.get("return_code"))){
            stringBufferMsg.append(map.get("return_msg"));
            splitAccountRecord.setStatus(2);
            return splitAccountRecord;
        }

        //返回分账记录
        splitAccountRecord.setDescription(stringBufferMsg.toString());
        logger.error(splitAccountRecord.getDescription());
        return splitAccountRecord;
    }

    @Override
    public void payProfitsharingOK(Map<String, String> mapParamOK, String appkey, String certPath) throws Exception {
        //得到签名
        String signOK= WxPayUtil.generateSignSHA256(mapParamOK, appkey);

        //封装请求参数
        StringBuilder reqXmlStr = new StringBuilder();
        reqXmlStr.append("<xml>");
        reqXmlStr.append("<appid>" + mapParamOK.get("appid") + "</appid>");
        reqXmlStr.append("<mch_id>" + mapParamOK.get("mch_id") + "</mch_id>");
        reqXmlStr.append("<nonce_str>" + mapParamOK.get("nonce_str") + "</nonce_str>");
        reqXmlStr.append("<out_order_no>" + mapParamOK.get("out_order_no") + "</out_order_no>");
        reqXmlStr.append("<transaction_id>" + mapParamOK.get("transaction_id") + "</transaction_id>");
        reqXmlStr.append("<sign>" + signOK + "</sign>");
        reqXmlStr.append("<description>" + mapParamOK.get("description") + "</description>");
        reqXmlStr.append("</xml>");
        logger.info("request-xml-OK= " + reqXmlStr);

        //得到结果
        String url = "https://api.mch.weixin.qq.com/secapi/pay/profitsharingfinish";
        String iResult = WxPayUtil.requestWxSplitAccount(url, certPath,reqXmlStr, mapParamOK.get("mch_id"));
        HashMap<String, Object> map =  WxPayUtil.processResponseXml(iResult);//将结果转为map对象
        System.out.println(map);
        if(map == null){
            logger.info("微信分账-请求单次分账-分账完成请求 || 响应结果为空");
        }else if("SUCCESS".equals(map.get("result_code"))&&"SUCCESS".equals(map.get("return_code"))){
            logger.info("微信分账-请求单次分账-分账完成请求 || 成功！");
        }else{
            logger.info("微信分账-请求单次分账-分账完成请求 || 未知！");
        }
    }

    @Override
    public Boolean getPayProfitsharingIsAccount(User user,StoreOrder storeOrder, Map<String, Object> s) {
        BigDecimal rate=new BigDecimal(String.valueOf(s.get("rate")));
        if(rate.compareTo(new BigDecimal("90.00")) >-1 ){
            Boolean is=this.payProfitsharingWithdrawalAccount(
                    user,
                    storeOrder.getId(),
                    String.valueOf(s.get("type")),
                    new BigDecimal(String.valueOf(s.get("cost"))));
            return is;
        }else{
            return Boolean.FALSE;
        }
    }

    @Override
    public PageInfo<SplitAccountRecord> getPageList(SplitAccountRecordSearchRequest request, PageParamRequest pageParamRequest) {
        //定义分页和查询对象
        Page<SplitAccountRecord> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<SplitAccountRecord> lqw = new LambdaQueryWrapper<>();

        //条件-关键字
        if (StrUtil.isNotBlank(request.getKeywords())) {
            lqw.eq(SplitAccountRecord::getOrderid,request.getKeywords());
            lqw.or().like(SplitAccountRecord::getType,request.getKeywords());
        }

        //排序
        lqw.orderByDesc(SplitAccountRecord::getUpdateTime);

        //得到数据并转成分页对象
        List<SplitAccountRecord> recordList = dao.selectList(lqw);
        return CommonPage.copyPageInfo(page, recordList);
    }
}
