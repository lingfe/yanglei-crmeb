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
 * ???????????????-service??????????????????
 * @author: ??????
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
        System.out.println("???????????????:" + amount.multiply(new BigDecimal("100")).intValue());
    }

    @Override
    public Boolean payProfitsharingWithdrawalAccount(User user,Integer orderId,String type,BigDecimal price) {
        try {
            //??????-??????????????????
            UserBill userBill = userBillService.getUserBill(    // ??????????????????
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

            //??????-??????????????????
            userService.operationNowMoney(user.getUid(), price, user.getNowMoney(), Constants.ADD_STR);
            return Boolean.TRUE;
        }catch (Exception e){
            e.printStackTrace();
            logger.error("????????????????????????:");
            logger.error(e.getMessage());
            return Boolean.FALSE;
        }
    }

    @Override
    public SplitAccountRecord payProfitSharingAlipay(StoreOrder storeOrder) {
        //??????????????????
        SplitAccountRecord splitAccountRecord=new SplitAccountRecord();
        splitAccountRecord.setOrderid(storeOrder.getId());
        splitAccountRecord.setType(Constants.SplitAccountRecord_TYPE_2);

        //???????????????
        HashMap<String, String> hashMap = systemConfigService.info(ConstantsFromID.INT_CONFIG_FORM_ZFB);
        if(hashMap == null) {
            splitAccountRecord.setDescription("???????????????-??????????????? || ???????????????????????????! ?????????????????????????????????");
            logger.error(splitAccountRecord.getDescription());
            return splitAccountRecord;
        }

        //???????????????
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
        for (Map<String,Object> objMap:mapList) { //???????????????????????????
            //??????-??????????????????????????????????????????
            is = this.getPayProfitsharingIsAccount((User) objMap.get("user"),storeOrder, objMap);
            if (is) continue;

            //????????????
            paramSB.append("{");
            paramSB.append("\"trans_in\":\"").append(objMap.get("accountAlipay")).append("\",");
            paramSB.append("\"amount\":").append(new BigDecimal(String.valueOf(objMap.get("amount"))).doubleValue()).append(",");
            paramSB.append("\"trans_in_type\":\"").append(objMap.get("accountTypeAlipay")).append("\",");
            paramSB.append("\"desc\":\"???????????????\"");
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
            map.put("desc","???????????????");
            map.put("royalty_type","transfer");
            map.put("trans_out","2088241500822091");
            map.put("trans_out_type","userId");
            map.put("amount_percentage","500");
            map.put("royalty_scene","??????");
            paramList.add(map);
        }
        paramSB.append("]").append("}");

        //???????????????????????????????????????
        if(is){
            splitAccountRecord.setStatus(2);
            splitAccountRecord.setDescription("??????????????? || ????????????,??????????????????????????????");
            logger.error(splitAccountRecord.getDescription());
            return splitAccountRecord;
        }

        //????????????
        if(paramList.size() == 0){
            splitAccountRecord.setStatus(2);
            splitAccountRecord.setDescription("??????????????? || ????????????,??????????????????");
            logger.error(splitAccountRecord.getDescription());
            return splitAccountRecord;
        }

        //????????????
        AlipayClient alipayClient = new DefaultAlipayClient(
                "https://openapi.alipay.com/gateway.do",
                hashMap.get("zfb_app_zf_appid"),
                hashMap.get("zfb_app_zf_secret_key"),
                "json",
                "utf-8",
                hashMap.get("zfb_app_zf_public_key"),
                "RSA2");

        //?????????????????????
        Boolean bl = this.payProfitSharingAlipayAddReceiver(alipayClient,paramList);
        if(!bl){
            splitAccountRecord.setStatus(2);
            splitAccountRecord.setDescription("??????????????? || ????????????,????????????????????????");
            logger.error(splitAccountRecord.getDescription());
            return splitAccountRecord;
        }

        //????????????
        AlipayTradeOrderSettleRequest request = new AlipayTradeOrderSettleRequest();
        request.setBizContent(paramSB.toString());

        //??????
        StringBuffer stringBuffer=new StringBuffer("??????????????????????????? || ");
        try {
            AlipayTradeOrderSettleResponse response = alipayClient.execute(request);
            logger.info(response.getSubMsg());
            System.out.println(response);

            if(response.isSuccess()){
                logger.info("??????????????????????????? || ???????????????");
                stringBuffer.append("???????????????");
                splitAccountRecord.setStatus(1);
            } else {
                logger.error("??????????????????????????? || ???????????????");
                stringBuffer.append("???????????????????????????!");
            }
        }catch (Exception e){
            logger.error("??????????????? || ???????????????????????????: "+e.getMessage());
            stringBuffer.append("???????????????????????????: "+e.getMessage());
        }

        //??????????????????
        splitAccountRecord.setDescription(stringBuffer.toString());
        logger.error(splitAccountRecord.getDescription());
        return splitAccountRecord;
    }

    @Override
    public List<Map<String, Object>> getPayProfitSharingReceiver(StoreOrder storeOrder) {
        //??????-?????????????????????
        List<Map<String,Object>> userList=new ArrayList<>();
        Map<String,Object> map=null;
        BigDecimal amount=BigDecimal.ZERO;
        User user=null;
        String name = "";

        //??????-???????????????????????????
        HashMap<String, String> hashMap = systemConfigService.info(ConstantsFromID.INT_CONFIG_FORM_JMZ_ProfitSharing);
        if(hashMap == null) {
            logger.error("??????????????????????????????!");
        }else{
            //??????????????????
            Boolean profitSharingJMZIsOpen=Boolean.getBoolean(String.valueOf(hashMap.get("profitSharingJMZIsOpen")));
            if(profitSharingJMZIsOpen){
                //???????????????????????????
                user=userService.getById(hashMap.get("profitSharingJMZuid"));
                if(user != null ){
                    //??????-????????????
                    String rate = hashMap.get("profitSharingJMZrate");

                    //??????????????????
                    amount = new BigDecimal(rate).divide(new BigDecimal(100)).multiply(storeOrder.getCost());

                    //???????????????
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
                    logger.error("???????????????????????????????????????!");
                }
            }else{
                logger.info("???????????????????????????!");
            }
        }

        //??????????????????-???????????????
        switch (storeOrder.getType()){
            case Constants.ORDER_TYPE_0: break;
            case Constants.ORDER_TYPE_1: break;
            case Constants.ORDER_TYPE_2:
                //??????-??????????????????
                RegionalAgency regionalAgency = regionalAgencyService.getById(storeOrder.getMerId());
                if(regionalAgency == null)break;
                user = userService.getById(regionalAgency.getUid());
                name = regionalAgency.getRaName();

                //??????????????????
                if(regionalAgency.getRate()==null || regionalAgency.getRate().compareTo(BigDecimal.ZERO) < 1)break;
                amount = regionalAgency.getRate().divide(new BigDecimal(100)).multiply(storeOrder.getCost());

                //???????????????
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
                //??????-???????????????
                Retailer retailer=retailerService.getById(storeOrder.getMerId());
                if(retailer == null)break;
                user = userService.getById(retailer.getUid());
                name = retailer.getReName();

                //??????????????????
                if(retailer.getRate()==null || retailer.getRate().compareTo(BigDecimal.ZERO) < 1)break;
                amount = retailer.getRate().divide(new BigDecimal(100)).multiply(storeOrder.getCost());

                //???????????????
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

                //??????-??????????????????
                RegionalAgency ra = regionalAgencyService.getById(retailer.getRaId());
                if(ra == null)break;
                user = userService.getById(ra.getUid());
                name = ra.getRaName();

                //??????????????????
                if(ra.getRate()==null || ra.getRate().compareTo(BigDecimal.ZERO) < 1)break;
                amount = ra.getRate().divide(new BigDecimal(100)).multiply(storeOrder.getCost());

                //???????????????
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
                //??????-???????????????
                Supplier supplier=supplierService.getById(storeOrder.getMerId());
                if(supplier == null)break;

                //??????-???????????????????????????
                user=userService.getById(supplier.getUid());
                if(user == null)break;
                name = supplier.getSuppName();

                //??????????????????
                if(supplier.getRate()==null || supplier.getRate().compareTo(BigDecimal.ZERO) < 1)break;
                amount = supplier.getRate().divide(new BigDecimal(100)).multiply(storeOrder.getCost());

                //???????????????
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
        //??????????????????
        SplitAccountRecord splitAccountRecord=new SplitAccountRecord();
        splitAccountRecord.setOrderid(storeOrder.getId());
        splitAccountRecord.setType(Constants.SplitAccountRecord_TYPE_3);

        //??????-???????????????
        List<Map<String, Object>> userList = this.getPayProfitSharingReceiver(storeOrder);
        if(userList == null || userList.size() <= 0){
            splitAccountRecord.setStatus(2);
            splitAccountRecord.setDescription("???????????????????????? || ?????????????????????! ?????????????????????????????????");
            logger.error(splitAccountRecord.getDescription());
            return splitAccountRecord;
        }

        //???????????????????????????
        Boolean is=Boolean.FALSE;
        for (Map<String,Object> objMap:userList) {
            //????????????
            User user = (User) objMap.get("user");

            //??????-??????????????????????????????????????????
            is = this.getPayProfitsharingIsAccount(user,storeOrder, objMap);
            if (is) continue;

            //????????????????????????
            //????????????
            BigDecimal amount = new BigDecimal(String.valueOf(objMap.get("amount")));
            UserBill userBill = userBillService.getUserBill(    // ????????????????????????-????????????
                    user.getUid(),storeOrder.getId().toString(),
                    Constants.USER_BILL_PM_1,
                    Constants.USER_BILL_CATEGORY_MONEY,
                    Constants.USER_BILL_TYPE_payProfitSharingIntegral,
                    amount,user.getNowMoney(),
                    ""
            );
            userBillService.save(userBill);
            logger.info("???????????????????????? || "+objMap.get("name") +" || ??????"+ amount +"??????");
            userService.operationNowMoney(user.getUid(), amount, user.getNowMoney(), Constants.ADD_STR);//??????-????????????
        }

        //???????????????????????????????????????
        if(is){
            splitAccountRecord.setStatus(2);
            splitAccountRecord.setDescription("???????????????????????? || ????????????,??????????????????????????????");
            logger.error(splitAccountRecord.getDescription());
            return splitAccountRecord;
        }

        //??????????????????
        splitAccountRecord.setStatus(1);
        splitAccountRecord.setDescription("???????????????????????? || ????????????!");
        return splitAccountRecord;
    }

    @Override
    public Boolean payProfitSharingAlipayAddReceiver(AlipayClient alipayClient,List<Map<String,Object>> mapList) {
        //???????????????
        List<Map<String,Object>> paramMapList=new ArrayList<>();
        for (Map<String,Object> objMap:mapList) {
            Map<String,Object> map=new HashMap<>();
            map.put("type",objMap.get("trans_in_type"));
            map.put("account",objMap.get("trans_in"));
            map.put("name",objMap.get("trans_in_name"));
            map.put("memo",objMap.get("desc"));
            paramMapList.add(map);
        }

        //????????????
        AlipayTradeRoyaltyRelationBindRequest request = new AlipayTradeRoyaltyRelationBindRequest();
        String obj=JSONObject.toJSONString(paramMapList);  //???????????????
        request.setBizContent("{" +
                "  \"receiver_list\":" +obj+","+
                "  \"out_request_no\":\""+ DateUtil.nowDateTime(Constants.DATE_TIME_FORMAT_NUM) +"\"" +
                "}");
        try {
            //????????????????????????
            AlipayTradeRoyaltyRelationBindResponse response = alipayClient.execute(request);
            if(response.isSuccess()){
                System.out.println("????????????");
                logger.info("???????????????-??????????????? || ????????????????????????!");
                return Boolean.TRUE;
            } else {
                logger.error("???????????????-??????????????? || ????????????????????????!");
                return Boolean.FALSE;
            }
        }catch (Exception e){
            logger.error("???????????????-??????????????? || ??????????????????????????????: "+e.getMessage());
            return Boolean.FALSE;
        }
    }

    @Override
    public void payProfitsharingTask() {
        //??????redsi-key
        String redisKey = Constants.ORDER_TASK_payProfitSharing; // ??????-????????????redis-key
        //??????????????????-size
        Long size = redisUtil.getListSize(redisKey);
        logger.info("??????-payProfitsharingTask  | size:" + size);
        if(size < 1)return;

        //????????????
        for (int i = 0; i < size; i++) {
            //??????10????????????????????????????????????????????????
            Object data = redisUtil.getRightPop(redisKey, 10L);
            if (null == data) {
                continue;
            }

            try {
                //??????-????????????
                StoreOrder storeOrder = storeOrderService.getById(Integer.valueOf(data.toString()));
                SplitAccountRecord splitAccountRecord=new SplitAccountRecord();
                if (storeOrder == null) {
                    splitAccountRecord.setStatus(2);
                    splitAccountRecord.setDescription("??????-payProfitsharingTask.task  || ???????????????! orderId:" + data);
                    logger.error(splitAccountRecord.getDescription());
                } else {
                    //??????-??????????????????
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
                            splitAccountRecord.setDescription("??????-payProfitsharingTask.task  || ??????????????????????????????! orderId:" + data);
                            logger.error(splitAccountRecord.getDescription());
                            return;
                    }

                    //??????-????????????
                    if (splitAccountRecord.getStatus() == 0) {
                        logger.error("??????-payProfitsharingTask.task || ????????????! ??????:" + splitAccountRecord.getDescription());
                        redisUtil.lPush(redisKey, data);//???????????????-????????????
                    }
                }

                //??????????????????
                dao.insert(splitAccountRecord);
            }catch (Exception e){
                e.printStackTrace();
                logger.error("??????-payProfitsharingTask.task" + " | ???????????? : " + e.getMessage());
                redisUtil.lPush(redisKey, data);//???????????????-????????????
            }
        }
    }

    @Override
    public SplitAccountRecord payProfitsharingWeiXin(Integer orderId) throws Exception {
        //??????????????????
        SplitAccountRecord splitAccountRecord=new SplitAccountRecord();
        splitAccountRecord.setOrderid(orderId);
        splitAccountRecord.setType(Constants.SplitAccountRecord_TYPE_1);
        splitAccountRecord.setStatus(0);

        //??????-????????????
        StoreOrder storeOrder=storeOrderService.getById(orderId);
        if(storeOrder == null) {
            splitAccountRecord.setStatus(2);
            splitAccountRecord.setDescription("????????????-?????????????????? || ???????????????! ");
            logger.error(splitAccountRecord.getDescription());
            return splitAccountRecord;
        }

        //??????????????????
        String appId = "";
        String mchId = "";
        switch (storeOrder.getIsChannel()){
            case PayConstants.ORDER_PAY_CHANNEL_PUBLIC:    //?????????
            case PayConstants.ORDER_PAY_CHANNEL_H5:        //H5??????????????????
                appId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_ID);
                mchId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_MCH_ID);
                break;
            case PayConstants.ORDER_PAY_CHANNEL_PROGRAM:   //?????????
                appId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_ROUTINE_APP_ID);
                mchId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_ROUTINE_MCH_ID);
                break;
            case PayConstants.ORDER_PAY_CHANNEL_APP_IOS:       //??????App-ios
            case PayConstants.ORDER_PAY_CHANNEL_APP_ANDROID:   //??????App-??????
                appId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_APP_ID);
                mchId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_MCH_ID);
                break;
            default:
                splitAccountRecord.setStatus(2);
                splitAccountRecord.setDescription("????????????-?????????????????? || ????????????????????????????????????????????????orderId???"+orderId);
                logger.info(splitAccountRecord.getDescription());
                return splitAccountRecord;
        }

        //????????????
        Map<String,String> mapParam=new HashMap<>();
        mapParam.put("mch_id",mchId);
        mapParam.put("appid",appId);
        mapParam.put("nonce_str", WxPayUtil.getNonceStr());             //???????????????
        mapParam.put("transaction_id",storeOrder.getTransactionId());   //???????????????
        mapParam.put("out_order_no", new StringBuffer("FZ").append(storeOrder.getOrderId()).toString()); //??????????????????
        Map<String,String> mapParamOK=new HashMap<>(); //??????????????????QQ??????
        mapParamOK.putAll(mapParam);
        mapParamOK.put("description","???????????????");

        //??????-?????????
        List<Map<String,Object>> mapList=new ArrayList<>();
        Map<String,Object> mapJSF=null;
        List<Map<String, Object>> userList = this.getPayProfitSharingReceiver(storeOrder);
        if(userList == null || userList.size()<=0){
            splitAccountRecord.setStatus(2);
            splitAccountRecord.setDescription("????????????-?????????????????? || ?????????????????????????????????????????????????????? ");
            logger.error(splitAccountRecord.getDescription());
            return splitAccountRecord;
        }

        //?????????????????????
        Boolean is =Boolean.FALSE;
        for (Map<String, Object> s:userList) {
            //??????-????????????????????????????????????
            is = this.getPayProfitsharingIsAccount((User) s.get("user"),storeOrder, s);
            if (is) continue;

            //?????????????????????
            mapJSF=new HashMap<>();
            mapJSF.put("type",s.get("accountTypeWeixin"));
            mapJSF.put("account",s.get("accountWeixin"));
            mapJSF.put("description","????????????");
            mapJSF.put("name",s.get("accountWeixinRealName"));
            Integer amount = new BigDecimal(s.get("amount").toString()).multiply(new BigDecimal("100")).intValue();
            mapJSF.put("amount",amount);

            //?????????list
            mapList.add(mapJSF);
        }

        //???????????????????????????????????????
        if(is){
            splitAccountRecord.setStatus(2);
            splitAccountRecord.setDescription("????????????-?????????????????? || ????????????,??????????????????????????????");
            logger.error(splitAccountRecord.getDescription());
            return splitAccountRecord;
        }

        //??????json
        String obj=JSONObject.toJSONString(mapList);
        mapParam.put("receivers", obj);

        //???????????????????????????
        HashMap<String, String> hashMap = systemConfigService.info(ConstantsFromID.INT_CONFIG_FORM_WX_XCX);
        if(hashMap == null) {
            splitAccountRecord.setDescription("????????????-?????????????????? || ?????????????????????orderId???"+orderId);
            logger.error(splitAccountRecord.getDescription());
            return splitAccountRecord;
        }

        //????????????
        String appkey = hashMap.get("pay_routine_key");
        String sign1 = WxPayUtil.generateSignSHA256(mapParam,appkey);
        String sign2 = WxPayUtil.getSign(mapParam,appkey);
        mapParam.put("sign",sign1);

        //??????????????????
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

        //??????????????????->??????????????????
        String certPath = hashMap.get("pay_routine_certificate_path");// ????????????????????????, ???????????????????????? "D:\\lingfe\\demo\\apiclient_cert.p12"
        String url = "https://api.mch.weixin.qq.com/secapi/pay/profitsharing";
        String iResult = WxPayUtil.requestWxSplitAccount(url,certPath,reqXmlStr,mchId);
        //???????????????map??????
        HashMap<String, Object> map =  WxPayUtil.processResponseXml(iResult);
        System.out.println(map);
        StringBuffer stringBufferMsg=new StringBuffer("????????????-?????????????????? || ");
        if(map == null){
            stringBufferMsg.append("??????????????????");
        }else if("SUCCESS".equals(map.get("result_code"))&&"SUCCESS".equals(map.get("return_code"))){
            stringBufferMsg.append("???????????????");
            splitAccountRecord.setStatus(1);
            this.payProfitsharingOK(mapParamOK, appkey, certPath);
        }else if("SUCCESS".equals(map.get("return_code"))){
            switch (map.get("err_code").toString()){
                case "AMOUNT_OVERDUE":
                    stringBufferMsg.append(map.get("err_code_des"));
                    break;
                default:
                    stringBufferMsg.append("??????????????????????????????????????????????????????");
                    break;
            }
            splitAccountRecord.setStatus(2);
        }else if("FAIL".equals(map.get("return_code"))){
            stringBufferMsg.append(map.get("return_msg"));
            splitAccountRecord.setStatus(2);
            return splitAccountRecord;
        }

        //??????????????????
        splitAccountRecord.setDescription(stringBufferMsg.toString());
        logger.error(splitAccountRecord.getDescription());
        return splitAccountRecord;
    }

    @Override
    public void payProfitsharingOK(Map<String, String> mapParamOK, String appkey, String certPath) throws Exception {
        //????????????
        String signOK= WxPayUtil.generateSignSHA256(mapParamOK, appkey);

        //??????????????????
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

        //????????????
        String url = "https://api.mch.weixin.qq.com/secapi/pay/profitsharingfinish";
        String iResult = WxPayUtil.requestWxSplitAccount(url, certPath,reqXmlStr, mapParamOK.get("mch_id"));
        HashMap<String, Object> map =  WxPayUtil.processResponseXml(iResult);//???????????????map??????
        System.out.println(map);
        if(map == null){
            logger.info("????????????-??????????????????-?????????????????? || ??????????????????");
        }else if("SUCCESS".equals(map.get("result_code"))&&"SUCCESS".equals(map.get("return_code"))){
            logger.info("????????????-??????????????????-?????????????????? || ?????????");
        }else{
            logger.info("????????????-??????????????????-?????????????????? || ?????????");
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
        //???????????????????????????
        Page<SplitAccountRecord> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<SplitAccountRecord> lqw = new LambdaQueryWrapper<>();

        //??????-?????????
        if (StrUtil.isNotBlank(request.getKeywords())) {
            lqw.eq(SplitAccountRecord::getOrderid,request.getKeywords());
            lqw.or().like(SplitAccountRecord::getType,request.getKeywords());
        }

        //??????
        lqw.orderByDesc(SplitAccountRecord::getUpdateTime);

        //?????????????????????????????????
        List<SplitAccountRecord> recordList = dao.selectList(lqw);
        return CommonPage.copyPageInfo(page, recordList);
    }
}
