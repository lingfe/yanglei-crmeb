package com.zbkj.crmeb.store.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CommonPage;
import com.common.MyRecord;
import com.common.PageParamRequest;
import com.constants.*;
import com.exception.CrmebException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.utils.DateUtil;
import com.utils.RedisUtil;
import com.utils.ValidateFormUtil;
import com.utils.vo.dateLimitUtilVo;
import com.zbkj.crmeb.combination.model.StorePink;
import com.zbkj.crmeb.combination.service.StorePinkService;
import com.zbkj.crmeb.express.model.Express;
import com.zbkj.crmeb.express.service.ExpressService;
import com.zbkj.crmeb.express.service.LogisticService;
import com.zbkj.crmeb.express.vo.ExpressSheetVo;
import com.zbkj.crmeb.express.vo.LogisticsResultVo;
import com.zbkj.crmeb.pass.service.OnePassService;
import com.zbkj.crmeb.regionalAgency.model.RegionalAgency;
import com.zbkj.crmeb.regionalAgency.service.RegionalAgencyService;
import com.zbkj.crmeb.retailer.service.RetailerService;
import com.zbkj.crmeb.sms.service.SmsService;
import com.zbkj.crmeb.store.dao.StoreOrderDao;
import com.zbkj.crmeb.store.model.StoreOrder;
import com.zbkj.crmeb.store.model.StoreOrderInfo;
import com.zbkj.crmeb.store.model.StoreProduct;
import com.zbkj.crmeb.store.request.*;
import com.zbkj.crmeb.store.response.*;
import com.zbkj.crmeb.store.service.*;
import com.zbkj.crmeb.store.utilService.OrderUtils;
import com.zbkj.crmeb.store.vo.StoreOrderInfoOldVo;
import com.zbkj.crmeb.system.model.SystemAdmin;
import com.zbkj.crmeb.system.model.SystemStore;
import com.zbkj.crmeb.system.request.SystemWriteOffOrderSearchRequest;
import com.zbkj.crmeb.system.response.StoreOrderItemResponse;
import com.zbkj.crmeb.system.response.SystemWriteOffOrderResponse;
import com.zbkj.crmeb.system.service.SystemAdminService;
import com.zbkj.crmeb.system.service.SystemConfigService;
import com.zbkj.crmeb.system.service.SystemStoreService;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.model.UserBrokerageRecord;
import com.zbkj.crmeb.user.model.UserToken;
import com.zbkj.crmeb.user.service.UserBillService;
import com.zbkj.crmeb.user.service.UserBrokerageRecordService;
import com.zbkj.crmeb.user.service.UserService;
import com.zbkj.crmeb.user.service.UserTokenService;
import com.zbkj.crmeb.wechat.service.TemplateMessageService;
import com.zbkj.crmeb.wechat.vo.WechatSendMessageForPaySuccess;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

/**
 * StoreOrderServiceImpl ????????????
 * @author: ??????
 * @CreateDate: 2022/2/24 10:23
 */
@Service
public class StoreOrderServiceImpl extends ServiceImpl<StoreOrderDao, StoreOrder> implements StoreOrderService {

    @Resource
    private StoreOrderDao dao;

    @Autowired
    private SystemStoreService systemStoreService;

    @Autowired
    private StoreOrderInfoService storeOrderInfoService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserBillService userBillService;

    @Autowired
    private StoreOrderStatusService storeOrderStatusService;

    @Autowired
    private StoreOrderRefundService storeOrderRefundService;

    @Autowired
    private ExpressService expressService;

    @Autowired
    private TemplateMessageService templateMessageService;

    @Autowired
    private LogisticService logisticService;

    @Autowired
    private OrderUtils orderUtils;

    @Autowired
    private SystemAdminService systemAdminService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private StorePinkService storePinkService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private OnePassService onePassService;

    @Autowired
    private UserTokenService userTokenService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private UserBrokerageRecordService userBrokerageRecordService;

    @Autowired
    private RegionalAgencyService regionalAgencyService;

    @Autowired
    private StoreProductService storeProductService;

    @Autowired
    private RetailerService retailerService;

    @Autowired
    private SupplierService supplierService;

    @Override
    public Boolean isLmsjfahuo(Integer id) {
        StoreOrder storeOrder = dao.selectById(id);
        if(null == storeOrder )throw new CrmebException("??????????????????!");
        storeOrder.setIsLmsjfahuo(Boolean.FALSE);
        boolean result = this.updateById(storeOrder);
        return result;
    }

    @Override
    public String getOrderPayTypeStr(String payType){
        switch (payType){
            case PayConstants.PAY_TYPE_WE_CHAT: return PayConstants.PAY_TYPE_STR_WE_CHAT;
            case PayConstants.PAY_TYPE_YUE: return PayConstants.PAY_TYPE_STR_YUE;
            case PayConstants.PAY_TYPE_ALI_PAY: return PayConstants.PAY_TYPE_STR_ALI_PAY;
            case PayConstants.PAY_TYPE_INTEGRAL: return PayConstants.PAY_TYPE_STR_INTEGAL;
            case PayConstants.PAY_TYPE_OFFLINE: return PayConstants.PAY_TYPE_STR_OFFLINE;
            case PayConstants.PAY_TYPE_ZERO_PAY: return PayConstants.PAY_TYPE_STR_ZERO_PAY;
            case PayConstants.PAY_TYPE_BANK: return PayConstants.PAY_TYPE_STR_BANK;
            default: return PayConstants.PAY_TYPE_STR_OTHER;
        }
    }

    @Override
    public List<StoreOrder> getStayReceivingOrderList() {
        //??????????????????
        LambdaQueryWrapper<StoreOrder> storeOrderLambdaQueryWrapper=new LambdaQueryWrapper<>();
        storeOrderLambdaQueryWrapper.eq(StoreOrder::getStatus,1);
        storeOrderLambdaQueryWrapper.eq(StoreOrder::getIsDel,Boolean.FALSE);
        storeOrderLambdaQueryWrapper.eq(StoreOrder::getRefundStatus,0);
        storeOrderLambdaQueryWrapper.orderByDesc(StoreOrder::getId).orderByDesc(StoreOrder::getPayTime);

        //????????????
        List<StoreOrder> list=dao.selectList(storeOrderLambdaQueryWrapper);
        if(list == null ) return new ArrayList<>();
        return list;
    }

    @Override
    public ProductOrderDataResponse getWhereProductIdAndMerId(Integer productId, Integer merId) {
        //????????????-????????????
        String date =null;          //????????????
        String startTime = null;    //????????????
        String endTime = DateUtil.nowDateTime(Constants.DATE_FORMAT);           //????????????
        StringBuffer dateSB=new StringBuffer("%s").append(",").append(endTime); //?????????????????????
        Date lastMonthEndDay=DateUtil.strToDate(DateUtil.getLastMonthEndDay(),Constants.DATE_FORMAT);//?????????????????????

        //????????????-????????????
        BigDecimal dayGmv = BigDecimal.ZERO;           //???????????????
        BigDecimal yesterdayGmv = BigDecimal.ZERO;     //???????????????
        BigDecimal thisMonthGmv = BigDecimal.ZERO;     //???????????????
        BigDecimal totalGmv=BigDecimal.ZERO;           //????????????

        //????????????-????????????
        Integer dayOrderNum=0;          //??????????????????
        Integer yesterdayOrderNum=0;    //??????????????????
        Integer thisMonthOrderNum=0;    //?????????????????????
        Integer totalOrderNum=0;        //???????????????

        //???????????????
        ProductOrderDataResponse response=new ProductOrderDataResponse();
        List<StoreOrderInfo>  storeOrderInfoList = null;
        List<StoreOrder> storeOrderList =null;

        //??????-????????????
        StoreProduct storeProduct=storeProductService.getById(productId);
        if(storeProduct == null)return new ProductOrderDataResponse();
        response.setPrice(storeProduct.getPrice());
        response.setProductId(storeProduct.getId());
        response.setStoreName(storeProduct.getStoreName());

        //??????-?????????????????????????????????
        startTime=DateUtil.addDay(DateUtil.nowDateTime(), 0, Constants.DATE_FORMAT_DATE);
        date=String.format(dateSB.toString(),startTime);
        storeOrderInfoList = storeOrderInfoService.getWhereProductIdAndDate(productId,date);
        storeOrderList = this.getWhereOrderInfoList(storeOrderInfoList,merId);
        //??????-?????????????????????????????????
        dayGmv=storeOrderList.stream().map(StoreOrder::getPayPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        dayOrderNum = storeOrderList.size();
        response.setDayGmv(dayGmv);
        response.setDayOrderNum(dayOrderNum);

        //??????-?????????????????????????????????
        startTime = DateUtil.addDay(DateUtil.nowDateTime(), -1, Constants.DATE_FORMAT_DATE);
        date=String.format("%s,%s",startTime,startTime);
        storeOrderInfoList = storeOrderInfoService.getWhereProductIdAndDate(productId,date);
        storeOrderList = this.getWhereOrderInfoList(storeOrderInfoList,merId);
        //??????-?????????????????????????????????
        yesterdayGmv=storeOrderList.stream().map(StoreOrder::getPayPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        yesterdayOrderNum = storeOrderList.size();
        response.setYesterdayGmv(yesterdayGmv);
        response.setYesterdayOrderNum(yesterdayOrderNum);

        //??????-?????????????????????????????????
        startTime = DateUtil.addDay(lastMonthEndDay, 0, Constants.DATE_FORMAT);
        storeOrderInfoList = storeOrderInfoService.getWhereProductIdAndDate(productId,String.format(dateSB.toString(),startTime));
        storeOrderList = this.getWhereOrderInfoList(storeOrderInfoList,merId);
        //??????-?????????????????????????????????
        thisMonthGmv=storeOrderList.stream().map(StoreOrder::getPayPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        thisMonthOrderNum = storeOrderList.size();
        response.setThisMonthGmv(thisMonthGmv);
        response.setThisMonthOrderNum(thisMonthOrderNum);

        //??????-???????????????????????????
        storeOrderInfoList = storeOrderInfoService.getWhereProductIdAndDate(productId,null);
        storeOrderList = this.getWhereOrderInfoList(storeOrderInfoList,merId);
        //??????-???????????????????????????
        totalGmv=storeOrderList.stream().map(StoreOrder::getPayPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        totalOrderNum = storeOrderList.size();
        response.setTotalGmv(totalGmv);
        response.setTotalOrderNum(totalOrderNum);

        //??????
        return response;
    }

    @Override
    public List<StoreOrder> getWhereOrderInfoList(List<StoreOrderInfo> storeOrderInfoList,Integer merId) {
        //??????-??????????????????
        if(storeOrderInfoList == null || storeOrderInfoList.size() == 0 ){
            return new ArrayList<>();
        }

        //??????-????????????
        List<Integer> orderIdList = storeOrderInfoList.stream().map(StoreOrderInfo::getOrderId).collect(Collectors.toList());
        LambdaQueryWrapper<StoreOrder> storeOrderLambdaQueryWrapper=new LambdaQueryWrapper<>();
        storeOrderLambdaQueryWrapper.in(StoreOrder::getId,orderIdList);
        storeOrderLambdaQueryWrapper.eq(StoreOrder::getMerId,merId);
        List<StoreOrder>  storeOrderList = dao.selectList(storeOrderLambdaQueryWrapper);
        if(storeOrderList == null )return new ArrayList<>();
        return storeOrderList;
    }

    @Override
    public CommonPage<StoreOrderDetailResponse> getAdminList(StoreOrderSearchRequest request, PageParamRequest pageParamRequest) {
        //??????????????????
        Page<Object> startPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //??????????????????
        QueryWrapper<StoreOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "order_id", "uid", "real_name", "pay_price", "pay_type", "create_time", "status", "refund_status"
                , "refund_reason_wap_img", "refund_reason_wap_explain", "refund_reason_wap", "refund_reason", "refund_reason_time"
                , "is_del", "mer_id","combination_id", "pink_id", "seckill_id", "bargain_id", "verify_code", "remark", "paid",
                "is_system_del", "shipping_type", "type","is_lmsjfahuo");

        //??????-?????????
        if (StrUtil.isNotBlank(request.getOrderNo())) {
            queryWrapper.eq("order_id", request.getOrderNo());
            if(StringUtils.isNumeric(request.getOrderNo()))queryWrapper.or().eq("id",request.getOrderNo());
        }

        //??????-????????????
        this.getRequestTimeWhere(queryWrapper, request);

        //??????-??????
        this.getStatusWhere(queryWrapper, request.getStatus());

        //??????-????????????
        if (ObjectUtil.isNotNull(request.getType()) && request.getType() > -1 ) {
            queryWrapper.eq("type", request.getType());
        }

        //??????-??????ID
        if (ObjectUtil.isNotNull(request.getMerId()) && request.getMerId() >= 0) {
            queryWrapper.eq("mer_id", request.getMerId());
        }

        //??????
        queryWrapper.orderByDesc("id");

        //????????????
        List<StoreOrderDetailResponse> detailResponseList = new ArrayList<>();
        List<StoreOrder> orderList = dao.selectList(queryWrapper);
        if(CollUtil.isNotEmpty(orderList)){
            detailResponseList = this.formatOrder1(orderList);
        }

        //??????
        return CommonPage.restPage(CommonPage.copyPageInfo(startPage, detailResponseList));
    }


    /**
     * H5????????????
     * @param uid ??????uid
     * @param status ????????????|0=?????????,1=?????????,2=?????????,3=?????????,4=?????????,-3=??????/??????
     * @param pageParamRequest ????????????
     * @return ??????????????????
     */
    @Override
    public List<StoreOrder> getUserOrderList(Integer uid, Integer status, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<StoreOrder> lqw = new LambdaQueryWrapper<>();
        orderUtils.statusApiByWhere(lqw, status);
        lqw.eq(StoreOrder::getUid, uid);
        lqw.orderByDesc(StoreOrder::getId);
        return dao.selectList(lqw);
    }

    /**
     * ????????????
     * @param storeOrder ????????????
     * @return ????????????
     */
    @Override
    public boolean create(StoreOrder storeOrder) {
        return dao.insert(storeOrder) > 0;
    }

    /**
     * ??????????????????
     * @param storeOrder ????????????
     * @return ??????????????????
     */
    @Override
    public List<StoreOrder> getByEntity(StoreOrder storeOrder) {
        LambdaQueryWrapper<StoreOrder> lqw = new LambdaQueryWrapper<>();
        lqw.setEntity(storeOrder);
        return dao.selectList(lqw);
    }

    /**
     * ????????????????????????
     * @param storeOrder ??????
     * @return ????????????
     */
    @Override
    public StoreOrder getByEntityOne(StoreOrder storeOrder) {
        LambdaQueryWrapper<StoreOrder> lqw = new LambdaQueryWrapper<>();
        lqw.setEntity(storeOrder);
        return dao.selectOne(lqw);
    }

    /**
     * ????????????
     * @param request ????????????
     * @param pageParamRequest ???????????????
     * @author Mr.Zhang
     * @since 2020-05-28
     * @return List<StoreOrder>
     */
    @Override
    public SystemWriteOffOrderResponse getWriteOffList(SystemWriteOffOrderSearchRequest request, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<StoreOrder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        String where = " is_del = 0 and shipping_type = 2";
//        String where = " is_del = 0 and paid = 1";
        //??????
        if(!StringUtils.isBlank(request.getDateLimit())){
            dateLimitUtilVo dateLimit = DateUtil.getDateLimit(request.getDateLimit());
            where += " and (create_time between '" + dateLimit.getStartTime() + "' and '" + dateLimit.getEndTime() + "' )";
        }

        if(!StringUtils.isBlank(request.getKeywords())){
            where += " and (real_name like '%"+ request.getKeywords() +"%' or user_phone = '"+ request.getKeywords() +"' or order_id = '" + request.getKeywords() + "' or id = '" + request.getKeywords() + "' )";
        }

        if(request.getStoreId() != null && request.getStoreId() > 0){
            where += " and store_id = " + request.getStoreId();
        }

        SystemWriteOffOrderResponse systemWriteOffOrderResponse = new SystemWriteOffOrderResponse();
        BigDecimal totalPrice = dao.getTotalPrice(where);
        BigDecimal price = new BigDecimal(BigInteger.ZERO);
        if(totalPrice == null){
            totalPrice = price;
        }
        systemWriteOffOrderResponse.setOrderTotalPrice(totalPrice);   //???????????????

        BigDecimal refundPrice = dao.getRefundPrice(where);
        if(refundPrice == null){
            refundPrice = price;
        }
        systemWriteOffOrderResponse.setRefundTotalPrice(refundPrice); //???????????????
        systemWriteOffOrderResponse.setRefundTotal(dao.getRefundTotal(where));  //???????????????


        Page<StoreOrder> storeOrderPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        lambdaQueryWrapper.apply(where);
        lambdaQueryWrapper.orderByDesc(StoreOrder::getId);
        List<StoreOrder> storeOrderList = dao.selectList(lambdaQueryWrapper);

        if(storeOrderList.size() < 1){
            systemWriteOffOrderResponse.setList(CommonPage.restPage(new PageInfo<>()));
            return systemWriteOffOrderResponse;
        }

        List<StoreOrderItemResponse> storeOrderItemResponseArrayList = formatOrder(storeOrderList);

        systemWriteOffOrderResponse.setTotal(storeOrderPage.getTotal()); //?????????
        systemWriteOffOrderResponse.setList(CommonPage.restPage(CommonPage.copyPageInfo(storeOrderPage, storeOrderItemResponseArrayList)));

        return systemWriteOffOrderResponse;
    }

    /**
     * ??????????????????????????????????????????
     * @param orderList List<StoreOrder> ????????????
     * @return List<StoreOrderItemResponse>
     */
    private List<StoreOrderDetailResponse> formatOrder1(List<StoreOrder> orderList) {
        //??????-????????????-??????list??????
        List<StoreOrderDetailResponse> detailResponseList  = new ArrayList<>();
        if (CollUtil.isEmpty(orderList)) {
            return detailResponseList;
        }

        //??????-??????id??????list??????
        List<Integer> orderIdList = orderList.stream().map(StoreOrder::getId).distinct().collect(Collectors.toList());

        //??????-????????????map
        HashMap<Integer, List<StoreOrderInfoOldVo>> orderInfoList = storeOrderInfoService.getMapInId(orderIdList);

        //????????????-????????????
        for (StoreOrder storeOrder : orderList) {
            //?????????-????????????-????????????
            StoreOrderDetailResponse storeOrderItemResponse = new StoreOrderDetailResponse();
            BeanUtils.copyProperties(storeOrder, storeOrderItemResponse);//??????

            //??????-????????????
            storeOrderItemResponse.setProductList(orderInfoList.get(storeOrder.getId()));

            //??????-????????????
            storeOrderItemResponse.setStatusStr(this.getStatus(storeOrder));
            storeOrderItemResponse.setStatus(storeOrder.getStatus());

            //??????-????????????
            String paytypestr=this.getOrderPayTypeStr(storeOrder.getPayType());
            storeOrderItemResponse.setPayTypeStr(paytypestr);

            //??????-??????????????????
            storeOrderItemResponse.setOrderType(getOrderTypeStr(storeOrder));

            //??????-????????????
            String shopName="";
            switch (storeOrder.getType()){
                case Constants.ORDER_TYPE_0: shopName = "??????"; break;
                case Constants.ORDER_TYPE_1: shopName = "????????????"; break;
                case Constants.ORDER_TYPE_2: shopName = regionalAgencyService.getRaName(storeOrder.getMerId()); break;
                case Constants.ORDER_TYPE_3: shopName = retailerService.getRetailerName(storeOrder.getMerId()); break;
                case Constants.ORDER_TYPE_4: shopName = supplierService.getSupplierName(storeOrder.getMerId()); break;
                default: shopName = "??????????????????"; break;
            }
            storeOrderItemResponse.setShopName(shopName);

            //??????-??????????????????
            List<RegionalAgency> list = regionalAgencyService.getWhereUserID(storeOrder.getMerId());
            if(list != null && list.size()>=1){
                storeOrderItemResponse.setRegionalAgency(list.get(0));
            }

            //?????????-????????????-??????list??????
            detailResponseList.add(storeOrderItemResponse);
        }

        //??????-????????????-??????list??????
        return detailResponseList;
    }

    /**
     * ????????????????????????????????????
     * @param storeOrder ??????
     * @return String
     */
    private String getOrderTypeStr(StoreOrder storeOrder) {
        //??????-?????????????????????
        String orderTypeFormat = "[{}??????]{}";
        String orderType = StrUtil.format(orderTypeFormat, "??????", "");

        // ??????
        if (StrUtil.isNotBlank(storeOrder.getVerifyCode())) {
            orderType = StrUtil.format(orderTypeFormat, "??????", "");
        }

        // ??????
        if (ObjectUtil.isNotNull(storeOrder.getSeckillId()) && storeOrder.getSeckillId() > 0) {
            orderType = StrUtil.format(orderTypeFormat, "??????", "");
        }

        // ??????
        if (ObjectUtil.isNotNull(storeOrder.getBargainId()) && storeOrder.getBargainId() > 0) {
            orderType = StrUtil.format(orderTypeFormat, "??????", "");
        }

        // ??????
        if (ObjectUtil.isNotNull(storeOrder.getCombinationId()) && storeOrder.getCombinationId() > 0) {
            StorePink storePink = storePinkService.getById(storeOrder.getPinkId());
            if (ObjectUtil.isNotNull(storePink)) {
                String pinkstatus = "";
                if (storePink.getStatus() == 2) {
                    pinkstatus = "?????????";
                } else if (storePink.getStatus() == 3) {
                    pinkstatus = "?????????";
                } else {
                    pinkstatus = "???????????????";
                }
                orderType = StrUtil.format(orderTypeFormat, "??????", pinkstatus);
            }
        }

        // ????????????
        if (storeOrder.getType().equals(1)) {
            orderType = StrUtil.format(orderTypeFormat, "?????????", "");
        }else if(storeOrder.getType().equals(2)){
            orderType = StrUtil.format(orderTypeFormat, "????????????", "");
        }else if(storeOrder.getType().equals(3)){
            orderType = StrUtil.format(orderTypeFormat, "?????????", "");
        }

        //??????-????????????
        return orderType;
    }

    /**
     * ??????????????????????????????????????????
     * @param storeOrderList List<StoreOrder> ????????????
     * @author Mr.Zhang
     * @since 2020-05-28
     * @return List<StoreOrderItemResponse>
     */
    private List<StoreOrderItemResponse> formatOrder(List<StoreOrder> storeOrderList) {
        List<StoreOrderItemResponse> storeOrderItemResponseArrayList  = new ArrayList<>();
        if(null == storeOrderList || storeOrderList.size() < 1){
            return storeOrderItemResponseArrayList;
        }
        //??????id
        List<Integer> storeIdList = storeOrderList.stream().map(StoreOrder::getStoreId).distinct().collect(Collectors.toList());
        //??????id / ?????????id
        List<Integer> clerkIdList = storeOrderList.stream().map(StoreOrder::getClerkId).distinct().collect(Collectors.toList());

        //??????id??????
        List<Integer> orderIdList = storeOrderList.stream().map(StoreOrder::getId).distinct().collect(Collectors.toList());

        //????????????map
        HashMap<Integer, SystemStore> systemStoreList = systemStoreService.getMapInId(storeIdList);
        //????????????map
//        HashMap<Integer, SystemStoreStaff> systemStoreStaffList = systemStoreStaffService.getMapInId(clerkIdList);
        HashMap<Integer, SystemAdmin> systemStoreStaffList = systemAdminService.getMapInId(clerkIdList);
        //??????????????????map
        HashMap<Integer, List<StoreOrderInfoOldVo>> orderInfoList = storeOrderInfoService.getMapInId(orderIdList);

        //????????????????????????
        List<Integer> userIdList = storeOrderList.stream().map(StoreOrder::getUid).distinct().collect(Collectors.toList());
        //??????????????????
        HashMap<Integer, User> userList = userService.getMapListInUid(userIdList);

        //???????????????id??????
        List<Integer> spreadPeopleUidList = new ArrayList<>();
        for(Map.Entry<Integer, User> entry : userList.entrySet()){
            spreadPeopleUidList.add(entry.getValue().getSpreadUid());
        }

        //????????????
        HashMap<Integer, User> mapListInUid = new HashMap<>();
        if(userIdList.size() > 0 && spreadPeopleUidList.size() > 0) {
            //???????????????
            mapListInUid = userService.getMapListInUid(spreadPeopleUidList);
        }

        for (StoreOrder storeOrder : storeOrderList) {
            StoreOrderItemResponse storeOrderItemResponse = new StoreOrderItemResponse();
            BeanUtils.copyProperties(storeOrder, storeOrderItemResponse);
            String storeName = "";
            if(systemStoreList.containsKey(storeOrder.getStoreId())){
                storeName = systemStoreList.get(storeOrder.getStoreId()).getName();
            }
            storeOrderItemResponse.setStoreName(storeName);

            // ?????????????????????
            String clerkName = "";
            if(systemStoreStaffList.containsKey(storeOrder.getClerkId())){
                clerkName = systemStoreStaffList.get(storeOrder.getClerkId()).getRealName();
            }
            storeOrderItemResponse.setProductList(orderInfoList.get(storeOrder.getId()));
            storeOrderItemResponse.setTotalNum(storeOrder.getTotalNum());

            //????????????
            storeOrderItemResponse.setStatusStr(this.getStatus(storeOrder));
            storeOrderItemResponse.setStatus(storeOrder.getStatus());
            //????????????
            String paytypestr=this.getOrderPayTypeStr(storeOrder.getPayType());
            storeOrderItemResponse.setPayTypeStr(paytypestr);

            //???????????????
            if(!userList.isEmpty()  && null != userList.get(storeOrder.getUid()) && mapListInUid.containsKey(userList.get(storeOrder.getUid()).getSpreadUid())){
                storeOrderItemResponse.getSpreadInfo().setId(mapListInUid.get(userList.get(storeOrder.getUid()).getSpreadUid()).getUid());
                storeOrderItemResponse.getSpreadInfo().setName(mapListInUid.get(userList.get(storeOrder.getUid()).getSpreadUid()).getNickname());
            }
            storeOrderItemResponse.setRefundStatus(storeOrder.getRefundStatus());
            storeOrderItemResponse.setClerkName(clerkName);
            storeOrderItemResponse.setOrderType(getOrderTypeStr(storeOrder));
            storeOrderItemResponseArrayList.add(storeOrderItemResponse);
        }
        return storeOrderItemResponseArrayList;
    }

    @Override
    public BigDecimal getSumBigDecimal(Integer userId, String date,Integer[] status,Boolean isQuotaControl) {
        //??????????????????
        QueryWrapper<StoreOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("sum(pay_price) as pay_price").
                ne("pay_type","integral").
                ne("refund_status",2).
                eq("paid", 1).
                eq("is_del", 0);

        //??????-??????ID
        if(null != userId){
            queryWrapper.eq("uid", userId);
        }

        //??????-??????
        if(null != date){
            dateLimitUtilVo dateLimit = DateUtil.getDateLimit(date);
            queryWrapper.between("create_time", dateLimit.getStartTime(), dateLimit.getEndTime());
        }

        //??????-??????
        if(null !=status && status.length > 0){
            queryWrapper.in("status",status);
        }

        //??????-????????????
        if(isQuotaControl){
            queryWrapper.in("quota_control",
                    Constants.ORDER_QUOTA_ADD,
                    Constants.ORDER_QUOTA_ADD_SUB);
        }

        //????????????
        StoreOrder storeOrder = dao.selectOne(queryWrapper);
        if(null == storeOrder || null == storeOrder.getPayPrice()){
            return BigDecimal.ZERO;
        }
        return storeOrder.getPayPrice();
    }

    /**
     * ??????????????????map
     * @param orderIdList Integer ??????id
     * @author Mr.Zhang
     * @since 2020-06-10
     * @return UserBalanceResponse
     */
    @Override
    public Map<Integer, StoreOrder> getMapInId(List<Integer> orderIdList) {
        Map<Integer, StoreOrder> map = new HashMap<>();
        if (null == orderIdList || orderIdList.size() < 1) {
            return map;
        }
        LambdaQueryWrapper<StoreOrder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(StoreOrder::getId, orderIdList);
        List<StoreOrder> orderList = dao.selectList(lambdaQueryWrapper);

        if (null == orderList || orderList.size() < 1) {
            return map;
        }

        for (StoreOrder storeOrder : orderList) {
            map.put(storeOrder.getId(), storeOrder);
        }
        return map;
    }

    @Override
    public int getOrderCount(Integer type, Integer value,String date) {
        //??????????????????
        LambdaQueryWrapper<StoreOrder> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        //??????-????????????
        lambdaQueryWrapper.eq(StoreOrder::getPaid,1);
        //??????-????????????
        lambdaQueryWrapper.eq(StoreOrder::getIsDel, 0);

        //??????-ID??????
        this.getTypeId(type, value, lambdaQueryWrapper);

        //??????-????????????
        if(StringUtils.isNotBlank(date)){
            dateLimitUtilVo dateLimit = DateUtil.getDateLimit(date);
            lambdaQueryWrapper.between(StoreOrder::getCreateTime, dateLimit.getStartTime(), dateLimit.getEndTime());
        }

        //????????????
        return dao.selectCount(lambdaQueryWrapper);
    }

    /**
     * ?????????????????????????????????
     * @param date String ????????????
     * @param lefTime int ????????????????????????
     * @author Mr.Zhang
     * @since 2020-05-16
     * @return HashMap<String, Object>
     */
    public List<StoreOrder> getOrderGroupByDate(String date, int lefTime) {
        QueryWrapper<StoreOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("sum(pay_price) as pay_price", "left(create_time, "+lefTime+") as orderId", "count(id) as id");
        if(StringUtils.isNotBlank(date)){
            dateLimitUtilVo dateLimit = DateUtil.getDateLimit(date);
            queryWrapper.between("create_time", dateLimit.getStartTime(), dateLimit.getEndTime());
        }
        queryWrapper.groupBy("orderId").orderByAsc("orderId");
        return dao.selectList(queryWrapper);
    }

    /** ??????
     * @param request StoreOrderRefundRequest ????????????
     * @return boolean
     * ???????????????????????????
     * ?????????????????????????????????????????????
     * ??????????????????redis?????????
     */
    @Override
    public boolean refund(StoreOrderRefundRequest request) {
        //??????????????????
        StoreOrder storeOrder = this.getInfoException(request.getOrderNo());

        //??????
        if (!storeOrder.getPaid()) {
            throw new CrmebException("?????????????????????");
        }else if (storeOrder.getRefundPrice().add(request.getAmount()).compareTo(storeOrder.getPayPrice()) > 0) {
            throw new CrmebException("??????????????????????????????????????????????????????");
        }else if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            if (storeOrder.getPayPrice().compareTo(BigDecimal.ZERO) != 0) {
                throw new CrmebException("?????????????????????0????????????????????????");
            }
        }else if(storeOrder.getPayType().equals("integral")){
            throw new CrmebException("????????????????????????????????????");
        }

        //??????ID??????
        request.setOrderId(storeOrder.getId());

        //??????????????????
        User user = userService.getById(storeOrder.getUid());

        //??????-??????
        Boolean isTask=true;
        if (storeOrder.getPayType().equals(PayConstants.PAY_TYPE_WE_CHAT) && request.getAmount().compareTo(BigDecimal.ZERO) > 0) {
            try {
                isTask=false;
                storeOrderRefundService.refund(request, storeOrder);
            } catch (Exception e) {
                e.printStackTrace();
                throw new CrmebException("???????????????????????????");
            }
        }

        //????????????????????????
        storeOrder.setRefundStatus(3);
        storeOrder.setRefundPrice(request.getAmount());

        //??????
        Boolean finalIsTask = isTask;
        Boolean execute = transactionTemplate.execute(e -> {
            //????????????
            this.updateById(storeOrder);

            //????????????
            request.setOrderId(storeOrder.getId());
            userBillService.saveRefundBill(request, user);

            //??????-????????????
            if (storeOrder.getPayType().equals(PayConstants.PAY_TYPE_YUE)) {
                // ??????????????????
                userService.operationNowMoney(user.getUid(), request.getAmount(), user.getNowMoney(), "add");
            }

            // ??????task
            if (finalIsTask){
                redisUtil.lPush(Constants.ORDER_TASK_REDIS_KEY_AFTER_REFUND_BY_USER, storeOrder.getId());
            }
            return Boolean.TRUE;
        });

        if(!execute){
            storeOrderStatusService.saveRefund(storeOrder.getId(), request.getAmount(), "??????");
            throw new CrmebException("??????????????????");
        }

        // ??????????????????
        HashMap<String, String> temMap = new HashMap<>();
        temMap.put(Constants.WE_CHAT_TEMP_KEY_FIRST, "?????????????????????????????????????????????????????????????????????????????????????????????");
        temMap.put("keyword1", storeOrder.getOrderId());
        temMap.put("keyword2", storeOrder.getPayPrice().toString());
        temMap.put("keyword3", DateUtil.dateToStr(storeOrder.getCreateTime(), Constants.DATE_FORMAT));
        temMap.put(Constants.WE_CHAT_TEMP_KEY_END, "?????????????????????");
        pushMessageRefundOrder(storeOrder, user, temMap);
        return execute;
    }

    /**
     * ??????????????????
     * ????????????????????????
     * ?????????????????????
     * ?????????????????????
     */
    private void pushMessageRefundOrder(StoreOrder storeOrder, User user, HashMap<String, String> temMap) {
        if (user.getUserType().equals(UserConstants.USER_TYPE_H5)) {
            return;
        }
        UserToken userToken;
        // ?????????
        if (user.getUserType().equals(UserConstants.USER_TYPE_WECHAT)) {
            userToken = userTokenService.getTokenByUserId(user.getUid(), UserConstants.USER_TOKEN_TYPE_WECHAT);
            if (ObjectUtil.isNull(userToken)) {
                return ;
            }
            // ????????????????????????
            templateMessageService.pushTemplateMessage(Constants.WE_CHAT_TEMP_KEY_ORDER_REFUND, temMap, userToken.getToken());
            return;
        }

        // ???????????????????????????
        String storeNameAndCarNumString = orderUtils.getStoreNameAndCarNumString(storeOrder.getId());
        if(StringUtils.isNotBlank(storeNameAndCarNumString)){
            WechatSendMessageForPaySuccess paySuccess = new WechatSendMessageForPaySuccess(
                    storeOrder.getId()+"",
                    storeOrder.getPayPrice()+"",
                    storeOrder.getPayTime()+"",
                    "??????",
                    storeOrder.getTotalPrice()+"",
                    storeNameAndCarNumString);
            orderUtils.sendWeiChatMiniMessageForPaySuccess(paySuccess, userService.getById(storeOrder).getUid());
        }
    }

    /**
     * ???????????????PC???
     * @param orderNo ????????????
     * @return StoreOrderInfoResponse
     */
    @Override
    public StoreOrderInfoResponse info(String orderNo) {
        StoreOrder storeOrder = getInfoException(orderNo);
        if (storeOrder.getIsSystemDel()) {
            throw new CrmebException("???????????????????????????");
        }
        StoreOrderInfoResponse storeOrderInfoResponse = new StoreOrderInfoResponse();
        BeanUtils.copyProperties(storeOrder, storeOrderInfoResponse);
        List<StoreOrderInfoOldVo> orderInfos = storeOrderInfoService.getOrderListByOrderId(storeOrder.getId());
        storeOrderInfoResponse.setOrderInfo(orderInfos);
        String paytypestr=this.getOrderPayTypeStr(storeOrder.getPayType());
        storeOrderInfoResponse.setPayTypeStr(paytypestr);
        storeOrderInfoResponse.setStatusStr(this.getStatus(storeOrder));
        if (ObjectUtil.isNotNull(storeOrder.getStoreId()) && storeOrder.getStoreId() > 0) {
            SystemStore systemStorePram = new SystemStore();
            systemStorePram.setId(storeOrder.getStoreId());
            storeOrderInfoResponse.setSystemStore(systemStoreService.getByCondition(systemStorePram));
        }

        //????????????
        User user = userService.getById(storeOrder.getUid());
        storeOrderInfoResponse.setNikeName(user.getNickname());
        storeOrderInfoResponse.setPhone(user.getPhone());

        // ????????????-???????????????????????????
        UserBrokerageRecord brokerageRecord = userBrokerageRecordService.getByLinkIdAndLinkType(storeOrder.getId().toString(),    // ????????????-???????????????????????????
                BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_ORDER);
        if (ObjectUtil.isNotNull(brokerageRecord)) {
            User spread = userService.getById(brokerageRecord.getUid());
            storeOrderInfoResponse.setSpreadName(spread.getNickname());
        }

        //?????????????????????
        if(storeOrder.getSpreadUid()!=null && storeOrder.getSpreadUid() > 0){
            User orderSpreadUser=userService.getById(storeOrder.getSpreadUid());
            if(orderSpreadUser!=null){
                storeOrderInfoResponse.setOrderUserName(orderSpreadUser.getNickname());
            }
        }

        storeOrderInfoResponse.setProTotalPrice(storeOrder.getTotalPrice().subtract(storeOrder.getTotalPostage()));
        return storeOrderInfoResponse;
    }

    /** ????????????
     * @param request StoreOrderSendRequest ????????????
     * @author lingfe
     * @since 2020-06-10
     * @return boolean
     */
    @Override
    public Boolean send(StoreOrderSendRequest request) {
        //????????????
        StoreOrder storeOrder = getInfoException(request.getOrderNo());
        if (storeOrder.getIsDel()) throw new CrmebException("???????????????,????????????!");
        if (storeOrder.getStatus() > 0) throw new CrmebException("?????????????????????????????????!");
        request.setId(storeOrder.getId());
        switch (request.getType()){
            case "1":// ??????
                express(request, storeOrder);
                break;
            case "2":// ??????
                delivery(request, storeOrder);
                break;
            case "3":// ??????
                virtual(request, storeOrder);
                break;
            default:
                throw new CrmebException("????????????");
        }
        return true;
    }

    /**
     * ????????????
     * @param orderNo ????????????
     * @param mark ??????
     * @return Boolean
     */
    @Override
    public Boolean mark(String orderNo, String mark) {
        StoreOrder storeOrder = getInfoException(orderNo);
        storeOrder.setRemark(mark);
        return updateById(storeOrder);
    }

    /**
     * ????????????
     * @param orderNo ????????????
     * @param reason String ??????
     * @return Boolean
     */
    @Override
    public Boolean refundRefuse(String orderNo, String reason) {
        if (StrUtil.isBlank(reason)) {
            throw new CrmebException("???????????????????????????");
        }
        StoreOrder storeOrder = getInfoException(orderNo);
        storeOrder.setRefundReason(reason);
        storeOrder.setRefundStatus(0);

        User user = userService.getById(storeOrder.getUid());

        Boolean execute = transactionTemplate.execute(e -> {
            updateById(storeOrder);
            storeOrderStatusService.createLog(storeOrder.getId(), Constants.ORDER_LOG_REFUND_REFUSE, Constants.ORDER_LOG_MESSAGE_REFUND_REFUSE.replace("{reason}", reason));
            return Boolean.TRUE;
        });
        if (execute) {
            // ????????????????????????????????????????????????
            if (ObjectUtil.isNotNull(storeOrder) && storeOrder.getPinkId() > 0) {
                StorePink storePink = storePinkService.getById(storeOrder.getPinkId());
                if (storePink.getStatus().equals(3)) {
                    storePink.setStatus(1);
                    storePinkService.updateById(storePink);
                }
            }
            // ??????????????????
            HashMap<String, String> temMap = new HashMap<>();
            temMap.put(Constants.WE_CHAT_TEMP_KEY_FIRST, "????????????????????????????????????");
            temMap.put("keyword1", storeOrder.getOrderId());
            temMap.put("keyword2", storeOrder.getPayPrice().toString());
            temMap.put("keyword3", DateUtil.dateToStr(storeOrder.getCreateTime(), Constants.DATE_FORMAT));
            temMap.put(Constants.WE_CHAT_TEMP_KEY_END, "???????????????"+ reason);
            pushMessageRefundOrder(storeOrder, user, temMap);
        }
        return execute;
    }

    /**
     * ????????????
     * @param storeOrder StoreOrder ????????????
     * @author Mr.Zhang
     * @since 2020-05-28
     * @return StoreOrder
     */
    @Override
    public StoreOrder getInfoByEntity(StoreOrder storeOrder) {
        LambdaQueryWrapper<StoreOrder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.setEntity(storeOrder);
        return dao.selectOne(lambdaQueryWrapper);
    }

    /**
     * ????????????????????????
     * @param orderNo ????????????
     * @return LogisticsResultVo
     */
    @Override
    public LogisticsResultVo getLogisticsInfo(String orderNo) {
        StoreOrder info = getInfoException(orderNo);
        if (info.getType().equals(1)) {// ???????????????
            Express express = expressService.getByName(info.getDeliveryName());
            if (ObjectUtil.isNotNull(express)) {
                info.setDeliveryCode(express.getCode());
            } else {
                info.setDeliveryCode("");
            }
        }
        return logisticService.info(info.getDeliveryId(), null, Optional.ofNullable(info.getDeliveryCode()).orElse(""), info.getUserPhone());
    }

    /**
     * ????????????id??????????????????????????????????????????
     * @param ids ??????id??????
     * @return ????????????????????????
     */
    @Override
    public List<StoreOrder> getOrderByUserIdsForRetailShop(List<Integer> ids) {
        LambdaQueryWrapper<StoreOrder> lqw = new LambdaQueryWrapper<>();
        lqw.in(StoreOrder::getUid,ids);
        lqw.eq(StoreOrder::getPaid, 1);
        lqw.eq(StoreOrder::getRefundStatus, 0);
        lqw.eq(StoreOrder::getIsDel, false);
        return dao.selectList(lqw);
    }

    /**
     * ?????? top ????????????
     * @param status ????????????
     * @param userId    ??????ID
     * @param merId     ????????????ID
     * @return ??????????????????
     */
    @Override
    public Integer getTopDataUtil(Integer status, Integer userId,Integer merId) {
        //??????????????????
        LambdaQueryWrapper<StoreOrder> lqw = new LambdaQueryWrapper<>();

        //??????-????????????
        orderUtils.statusApiByWhere(lqw, status);

        //??????-??????ID???????????????ID
        if(userId!=null){
            lqw.eq(StoreOrder::getUid,userId);
        }else if(merId !=null){
            lqw.eq(StoreOrder::getMerId,merId);
        }

        //??????????????????
        return dao.selectCount(lqw);
    }

    /**
     * ??????????????????
     *
     * @param request ??????id????????????
     * @return ????????????
     */
    @Override
    public boolean editPrice(StoreOrderEditPriceRequest request) {
        String oldPrice;
        StoreOrder existOrder = getByOderId(request.getOrderNo());
        // ???????????????
        if(null == existOrder) {
            throw new CrmebException(Constants.RESULT_ORDER_NOTFOUND.replace("${orderCode}", request.getOrderNo()));
        }
        // ???????????????
        if(existOrder.getPaid()) {
            throw new CrmebException(Constants.RESULT_ORDER_PAYED.replace("${orderCode}", request.getOrderNo()));
        }
        // ?????????????????????????????????
        if(existOrder.getPayPrice().compareTo(request.getPrice()) ==0) {
            throw new CrmebException(Constants.RESULT_ORDER_EDIT_PRICE_SAME.replace("${oldPrice}",existOrder.getPayPrice()+"")
                    .replace("${editPrice}",request.getPrice()+""));
        }

        oldPrice = existOrder.getPayPrice()+"";

        Boolean execute = transactionTemplate.execute(e -> {
            // ??????????????????
            orderEditPrice(request.getOrderNo(), request.getPrice());
            // ????????????????????????
            storeOrderStatusService.createLog(existOrder.getId(), Constants.ORDER_LOG_EDIT,
                    Constants.RESULT_ORDER_EDIT_PRICE_LOGS.replace("${orderPrice}", oldPrice)
                            .replace("${price}", request.getPrice() + ""));
            return Boolean.TRUE;
        });
        if(!execute) {
            throw new CrmebException(Constants.RESULT_ORDER_EDIT_PRICE_SUCCESS
                    .replace("${orderNo}", existOrder.getOrderId()).replace("${price}", request.getPrice()+""));
        }
        // ????????????????????????
        User user = userService.getById(existOrder.getUid());
        if (StrUtil.isNotBlank(user.getPhone())) {
            // ??????????????????????????????
            String smsSwitch = systemConfigService.getValueByKey(SmsConstants.SMS_CONFIG_PRICE_REVISION_SWITCH);
            if (StrUtil.isNotBlank(smsSwitch) && smsSwitch.equals("1")) {
                // ????????????????????????
                smsService.sendOrderEditPriceNotice(user.getPhone(), existOrder.getOrderId(), request.getPrice());
            }
        }

        return execute;
    }

    /**
     * ??????
     * @param orderNo ????????????
     * @param price ??????????????????
     */
    private Boolean orderEditPrice(String orderNo, BigDecimal price) {
        LambdaUpdateWrapper<StoreOrder> luw = new LambdaUpdateWrapper<>();
        luw.set(StoreOrder::getPayPrice, price);
        luw.eq(StoreOrder::getOrderId, orderNo);
        luw.eq(StoreOrder::getPaid, false);
        return update(luw);
    }

    /**
     * ???????????????????????????????????????
     *
     * @param dateLimit ????????????
     * @param type ??????
     * @return ??????????????????
     */
    @Override
    public StoreOrderStatisticsResponse orderStatisticsByTime(String dateLimit,Integer type) {
        StoreOrderStatisticsResponse response = new StoreOrderStatisticsResponse();
        // ???????????????????????????????????????????????? ?????????????????????????????????????????? ?????????????????????????????????????????? ?????????????????????
        dateLimitUtilVo dateRange = DateUtil.getDateLimit(dateLimit);
        String dateStartD = dateRange.getStartTime();
        String dateEndD = dateRange.getEndTime();
        int days = DateUtil.daysBetween(
                DateUtil.strToDate(dateStartD,Constants.DATE_FORMAT_DATE),
                DateUtil.strToDate(dateEndD,Constants.DATE_FORMAT_DATE)
        );
        // ???????????????????????????????????????
        String perDateStart = DateUtil.addDay(
                DateUtil.strToDate(dateStartD,Constants.DATE_FORMAT_DATE), -days, Constants.DATE_FORMAT_START);
        // ??????????????????
        String dateStart = DateUtil.addDay(
                DateUtil.strToDate(dateStartD,Constants.DATE_FORMAT_DATE),0,Constants.DATE_FORMAT_START);
        String dateEnd = DateUtil.addDay(
                DateUtil.strToDate(dateEndD,Constants.DATE_FORMAT_DATE),0,Constants.DATE_FORMAT_END);

        // ????????????????????????
        List<StoreOrder> orderPerList = getOrderPayedByDateLimit(perDateStart,dateStart);

        // ???????????????
        List<StoreOrder> orderCurrentList = getOrderPayedByDateLimit(dateStart, dateEnd);
        double increasePrice = 0;
        if(type == 1){
            double perSumPrice = orderPerList.stream().mapToDouble(e -> e.getPayPrice().doubleValue()).sum();
            double currentSumPrice = orderCurrentList.stream().mapToDouble(e -> e.getPayPrice().doubleValue()).sum();

            response.setChart(dao.getOrderStatisticsPriceDetail(new StoreDateRangeSqlPram(dateStart,dateEnd)));
            response.setTime(BigDecimal.valueOf(currentSumPrice).setScale(2,BigDecimal.ROUND_HALF_UP));
            // ??????????????????????????????????????????????????????
            increasePrice = currentSumPrice - perSumPrice;
            if(increasePrice <= 0) response.setGrowthRate(0);
            else if(perSumPrice == 0) response.setGrowthRate((int) increasePrice * 100);
            else response.setGrowthRate((int)((increasePrice * perSumPrice) * 100));
        }else if(type ==2){
            response.setChart(dao.getOrderStatisticsOrderCountDetail(new StoreDateRangeSqlPram(dateStart,dateEnd)));
            response.setTime(BigDecimal.valueOf(orderCurrentList.size()));
            increasePrice = orderCurrentList.size() - orderPerList.size();
            if(increasePrice <= 0) response.setGrowthRate(0);
            else if(orderPerList.size() == 0) response.setGrowthRate((int) increasePrice);
            else response.setGrowthRate((int)((increasePrice / orderPerList.size()) * 100));
        }
        response.setIncreaseTime(increasePrice+"");
        response.setIncreaseTimeStatus(increasePrice >= 0 ? 1:2);
        return response;
    }

    /**
     * ?????????????????????????????????
     *
     * @param uid ??????uid
     * @param seckillId ????????????id
     * @return ???????????????????????????????????????
     */
    @Override
    public List<StoreOrder> getUserCurrentDaySecKillOrders(Integer uid, Integer seckillId) {
        String dayStart = DateUtil.nowDateTime(Constants.DATE_FORMAT_START);
        String dayEnd = DateUtil.nowDateTime(Constants.DATE_FORMAT_END);
        LambdaQueryWrapper<StoreOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(StoreOrder::getUid, uid);
        lqw.eq(StoreOrder::getSeckillId, seckillId);
        lqw.between(StoreOrder::getCreateTime, dayStart, dayEnd);
        lqw.eq(StoreOrder::getIsDel, false);
        return dao.selectList(lqw);
    }

    /**
     * ?????????????????????????????????
     * @param uid    ??????uid
     * @return  ???????????????????????????
     */
    @Override
    public List<StoreOrder> getUserCurrentBargainOrders(Integer uid, Integer bargainId) {
        LambdaQueryWrapper<StoreOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(StoreOrder::getUid, uid);
        lqw.eq(StoreOrder::getBargainId, bargainId);
        lqw.eq(StoreOrder::getIsDel, false);
        return dao.selectList(lqw);
    }

    /**
     * ?????????????????????????????????
     * @param uid    ??????uid
     * @return  ???????????????????????????
     */
    @Override
    public List<StoreOrder> getUserCurrentCombinationOrders(Integer uid, Integer combinationId) {
        LambdaQueryWrapper<StoreOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(StoreOrder::getUid, uid);
        lqw.eq(StoreOrder::getCombinationId, combinationId);
        lqw.eq(StoreOrder::getIsDel, false);
        return dao.selectList(lqw);
    }

    @Override
    public StoreOrder getByOderId(String orderId) {
        LambdaQueryWrapper<StoreOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(StoreOrder::getOrderId, orderId);
        return dao.selectOne(lqw);
    }

    /**
     * ??????????????????????????????
     * @return ExpressSheetVo
     */
    @Override
    public ExpressSheetVo getDeliveryInfo() {
        return systemConfigService.getDeliveryInfo();
    }

    /**
     * ????????????uid???????????????????????????
     * @param userIds ??????uid??????
     * @return ???????????????
     */
    @Override
    public List<StoreOrder> getOrderListStrByUids(List<Integer> userIds, RetailShopStairUserRequest request) {
        LambdaQueryWrapper<StoreOrder> lqw = new LambdaQueryWrapper<>();
        lqw.in(StoreOrder::getUid, userIds);
        lqw.ge(StoreOrder::getPaid, 1);
//        lqw.ge(StoreOrder::getRefundStatus, 0);
        if(StrUtil.isNotBlank(request.getDateLimit())){
            dateLimitUtilVo dateLimit = DateUtil.getDateLimit(request.getDateLimit());
            lqw.between(StoreOrder::getCreateTime, dateLimit.getStartTime(), dateLimit.getEndTime());
        }
        if(StrUtil.isNotBlank(request.getNickName())){
            lqw.eq(StoreOrder::getOrderId, request.getNickName());
        }
        lqw.orderByDesc(StoreOrder::getId);
        return dao.selectList(lqw);
    }

    /**
     * ??????????????????
     * @param orderNo ????????????
     * @return Boolean
     */
    @Override
    public Boolean updatePaid(String orderNo) {
        LambdaUpdateWrapper<StoreOrder> lqw = new LambdaUpdateWrapper<>();
        lqw.set(StoreOrder::getPaid, true);
        lqw.set(StoreOrder::getPayTime, DateUtil.nowDateTime());
        lqw.eq(StoreOrder::getOrderId, orderNo);
        lqw.eq(StoreOrder::getPaid,false);
        return update(lqw);
    }

    @Override
    public Map<String, StoreOrder> getMapInOrderNo(List<String> orderIDList) {
        Map<String, StoreOrder> map = CollUtil.newHashMap();
        LambdaUpdateWrapper<StoreOrder> lqw = new LambdaUpdateWrapper<>();
        lqw.in(StoreOrder::getId, orderIDList);
        List<StoreOrder> orderList = dao.selectList(lqw);
        orderList.forEach(order -> {
            map.put(order.getId().toString(), order);
        });
        return map;
    }

    /**
     * ???????????????????????????
     * @param orderNoList ??????????????????
     * @return BigDecimal
     */
    @Override
    public BigDecimal getSpreadOrderTotalPriceByOrderList(List<String> orderNoList) {
        LambdaQueryWrapper<StoreOrder> lqw = new LambdaQueryWrapper<>();
        lqw.select(StoreOrder::getPayPrice);
        lqw.in(StoreOrder::getOrderId, orderNoList);
        List<StoreOrder> orderList = dao.selectList(lqw);
        return orderList.stream().map(StoreOrder::getPayPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * ????????????????????????id??????
     * @return List<StoreOrder>
     */
    @Override
    public List<StoreOrder> findIdAndUidListByReceipt() {
        LambdaQueryWrapper<StoreOrder> lqw = new LambdaQueryWrapper<>();
        lqw.select(StoreOrder::getId, StoreOrder::getUid,StoreOrder::getMerId,StoreOrder::getType,StoreOrder::getPayPrice);
        lqw.eq(StoreOrder::getStatus, 2);
        lqw.eq(StoreOrder::getRefundStatus, 0);
        lqw.eq(StoreOrder::getIsDel, false);
        List<StoreOrder> orderList = dao.selectList(lqw);
        if (CollUtil.isEmpty(orderList)) {
            return CollUtil.newArrayList();
        }
        return orderList;
    }

    /**
     *
     * @param userId ??????uid
     * @param pageParamRequest ????????????
     * @return List
     */
    @Override
    public List<StoreOrder> findPaidListByUid(Integer userId, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<StoreOrder> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StoreOrder::getUid, userId);
        lqw.eq(StoreOrder::getPaid, true);
        lqw.eq(StoreOrder::getIsDel, false);
        lqw.orderByDesc(StoreOrder::getId);
        return dao.selectList(lqw);
    }

    /**
     * ????????????
     * @param request ??????????????????
     * @return ????????????
     */
    @Override
    public Boolean updatePrice(StoreOrderUpdatePriceRequest request) {
        String oldPrice;
        StoreOrder existOrder = getInfoException(request.getOrderNo());
        // ???????????????
        if(existOrder.getPaid()) {
            throw new CrmebException(StrUtil.format("???????????? {} ??????????????????", existOrder.getOrderId()));
        }
        // ?????????????????????????????????
        if(existOrder.getPayPrice().compareTo(request.getPayPrice()) ==0) {
            throw new CrmebException(StrUtil.format("??????????????????????????????????????? ?????? {} ????????? {}", existOrder.getPayPrice(), request.getPayPrice()));
        }

        oldPrice = existOrder.getPayPrice()+"";

        Boolean execute = transactionTemplate.execute(e -> {
            // ??????????????????
            orderEditPrice(existOrder.getOrderId(), request.getPayPrice());
            // ????????????????????????
            storeOrderStatusService.createLog(existOrder.getId(), Constants.ORDER_LOG_EDIT,
                    Constants.RESULT_ORDER_EDIT_PRICE_LOGS.replace("${orderPrice}", oldPrice)
                            .replace("${price}", request.getPayPrice() + ""));
            return Boolean.TRUE;
        });
        if(!execute) {
            throw new CrmebException(Constants.RESULT_ORDER_EDIT_PRICE_SUCCESS
                    .replace("${orderNo}", existOrder.getOrderId()).replace("${price}", request.getPayPrice()+""));
        }
        // ????????????????????????
        User user = userService.getById(existOrder.getUid());
        if (StrUtil.isNotBlank(user.getPhone())) {
            // ??????????????????????????????
            String smsSwitch = systemConfigService.getValueByKey(SmsConstants.SMS_CONFIG_PRICE_REVISION_SWITCH);
            if (StrUtil.isNotBlank(smsSwitch) && smsSwitch.equals("1")) {
                // ????????????????????????
                smsService.sendOrderEditPriceNotice(user.getPhone(), existOrder.getOrderId(), request.getPayPrice());
            }
        }

        return execute;
    }

    @Override
    public Integer getOrderCountByUid(Integer uid,Integer merId) {
        //??????????????????
        LambdaQueryWrapper<StoreOrder> lqw = Wrappers.lambdaQuery();
        //??????-????????????
        lqw.eq(StoreOrder::getPaid, true);
        //??????-????????????
        lqw.eq(StoreOrder::getIsDel, false);

        //??????-??????ID???????????????ID
        if(uid!=null)lqw.eq(StoreOrder::getUid, uid);
        else if(merId != null)lqw.eq(StoreOrder::getMerId, merId);

        //??????-?????????????????????2
        lqw.lt(StoreOrder::getRefundStatus, 2);

        //????????????
        return dao.selectCount(lqw);
    }

    /**
     * ???????????????????????????
     * @param userId ??????uid
     * @return BigDecimal
     */
    @Override
    public BigDecimal getSumPayPriceByUid(Integer userId,Integer merId) {
        //??????????????????
        LambdaQueryWrapper<StoreOrder> lqw = Wrappers.lambdaQuery();
        //??????-??????
        lqw.select(StoreOrder::getPayPrice);
        //??????-????????????
        lqw.eq(StoreOrder::getPaid, true);
        //??????-????????????
        lqw.eq(StoreOrder::getIsDel, false);

        //??????-??????ID???????????????ID
        if(userId!=null)lqw.eq(StoreOrder::getUid, userId);
        else if(merId != null)lqw.eq(StoreOrder::getMerId, merId);

        //??????-?????????????????????2
        lqw.lt(StoreOrder::getRefundStatus, 2);
        List<StoreOrder> orderList = dao.selectList(lqw);

        //???????????????
        return orderList.stream().map(StoreOrder::getPayPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * ??????????????????(??????)
     * @param uid ??????uid
     * @return Integer
     */
    @Override
    public Integer getOrderCountByUidAndDate(Integer uid, String date) {
        LambdaQueryWrapper<StoreOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(StoreOrder::getPaid, true);
        lqw.eq(StoreOrder::getIsDel, false);
        lqw.eq(StoreOrder::getUid, uid);
        lqw.lt(StoreOrder::getRefundStatus, 2);
        if (StrUtil.isNotBlank(date)) {
            dateLimitUtilVo dateLimit = DateUtil.getDateLimit(date);
            lqw.between(StoreOrder::getCreateTime, dateLimit.getStartTime(), dateLimit.getEndTime());
        }
        return dao.selectCount(lqw);
    }

    @Override
    public BigDecimal getSumPayPriceByUidAndDate(Integer type, String date,Integer value) {
        //??????????????????
        LambdaQueryWrapper<StoreOrder> lqw = Wrappers.lambdaQuery();

        //??????-??????
        lqw.select(StoreOrder::getPayPrice);

        //??????-????????????????????????
        lqw.eq(StoreOrder::getPaid, true);
        //??????-??????????????????
        lqw.eq(StoreOrder::getIsDel, false);
        //??????-?????????????????????2-?????????
        lqw.lt(StoreOrder::getRefundStatus, 2);

        //??????-ID??????
        this.getTypeId(type, value, lqw);

        //??????-????????????
        if (StrUtil.isNotBlank(date)) {
            dateLimitUtilVo dateLimit = DateUtil.getDateLimit(date);
            lqw.between(StoreOrder::getCreateTime, dateLimit.getStartTime(), dateLimit.getEndTime());
        }

        //????????????
        List<StoreOrder> orderList = dao.selectList(lqw);

        //??????-?????????
        return orderList.stream().map(StoreOrder::getPayPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void getTypeId(Integer type, Integer value, LambdaQueryWrapper<StoreOrder> lqw) {
        if(type == null)type=0;
        switch (type){
            case 1:lqw.eq(StoreOrder::getUid, value);break;
            case 2:lqw.eq(StoreOrder::getMerId, value);break;
            case 3:lqw.eq(StoreOrder::getSpId, value);break;
            case 4:lqw.eq(StoreOrder::getSptlId, value);break;
            default:break;
        }
    }

    /**
     * ??????????????????
     * @param bargainId ????????????id
     * @param bargainUserId ??????????????????id
     * @return StoreOrder
     */
    @Override
    public StoreOrder getByBargainOrder(Integer bargainId, Integer bargainUserId) {
        LambdaQueryWrapper<StoreOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(StoreOrder::getBargainId, bargainId);
        lqw.eq(StoreOrder::getBargainUserId, bargainUserId);
        lqw.orderByDesc(StoreOrder::getId);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    /**
     * ????????????????????????
     * @return StoreOrderCountItemResponse
     */
    @Override
    public StoreOrderCountItemResponse getOrderStatusNum(String dateLimit) {
        StoreOrderCountItemResponse response = new StoreOrderCountItemResponse();
        // ????????????
        response.setAll(getCount(dateLimit, Constants.ORDER_STATUS_ALL));
        // ???????????????
        response.setUnPaid(getCount(dateLimit, Constants.ORDER_STATUS_UNPAID));
        // ???????????????
        response.setNotShipped(getCount(dateLimit, Constants.ORDER_STATUS_NOT_SHIPPED));
        // ???????????????
        response.setSpike(getCount(dateLimit, Constants.ORDER_STATUS_SPIKE));
        // ???????????????
        response.setBargain(getCount(dateLimit, Constants.ORDER_STATUS_BARGAIN));
        // ??????????????????
        response.setComplete(getCount(dateLimit, Constants.ORDER_STATUS_COMPLETE));
        // ???????????????
        response.setToBeWrittenOff(getCount(dateLimit, Constants.ORDER_STATUS_TOBE_WRITTEN_OFF));
        // ???????????????
        response.setRefunding(getCount(dateLimit, Constants.ORDER_STATUS_REFUNDING));
        // ???????????????
        response.setRefunded(getCount(dateLimit, Constants.ORDER_STATUS_REFUNDED));
        // ???????????????
        response.setDeleted(getCount(dateLimit, Constants.ORDER_STATUS_DELETED));
        return response;
    }

    /**
     * ????????????????????????
     * @param dateLimit ?????????
     * @return StoreOrderTopItemResponse
     */
    @Override
    public StoreOrderTopItemResponse getOrderData(String dateLimit) {
        StoreOrderTopItemResponse itemResponse = new StoreOrderTopItemResponse();
        // ????????????
        itemResponse.setCount(getCount(dateLimit, Constants.ORDER_STATUS_ALL));
        // ????????????
        itemResponse.setAmount(getAmount(dateLimit, ""));
        // ??????????????????
        itemResponse.setWeChatAmount(getAmount(dateLimit, PayConstants.PAY_TYPE_WE_CHAT));
        // ??????????????????
        itemResponse.setYueAmount(getAmount(dateLimit, PayConstants.PAY_TYPE_YUE));
        // ????????????????????????
        itemResponse.setIntegralAmount(this.getAmount(dateLimit, PayConstants.PAY_TYPE_INTEGRAL));
        // ???????????????????????????
        itemResponse.setZfbAmount(this.getAmount(dateLimit, PayConstants.PAY_TYPE_ALI_PAY));
        return itemResponse;
    }

    /**
     * ????????????
     * @param orderNo ????????????
     * @return Boolean
     */
    @Override
    public Boolean delete(String orderNo) {
        StoreOrder storeOrder = getInfoException(orderNo);
        if (!storeOrder.getIsDel()) {
            throw new CrmebException("?????????????????????????????????????????????????????????????????????????????????????????????");
        }
        if (storeOrder.getIsSystemDel()) {
            throw new CrmebException("???????????????????????????!");
        }
        storeOrder.setIsSystemDel(true);
        return updateById(storeOrder);
    }


///////////////////////////////////////////////////////////////////////////////////////////////////// ????????????????????????

    /**
     * ????????????????????????????????????
     * @return ??????????????????
     */
    private List<StoreOrder> getOrderPayedByDateLimit(String startTime, String endTime){
        LambdaQueryWrapper<StoreOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(StoreOrder::getIsDel, false).eq(StoreOrder::getPaid, true).eq(StoreOrder::getRefundStatus,0)
                .between(StoreOrder::getCreateTime, startTime, endTime);
     return dao.selectList(lqw);
    }


    /** ??????
     * @param id Integer id
     * @author Mr.Zhang
     * @since 2020-06-10
     * @return Boolean
     */
    public StoreOrder getInfoException(Integer id) {
        StoreOrder info = getById(id);
        if(null == info){
            throw new CrmebException("????????????????????????");
        }
        return info;
    }

    public StoreOrder getInfo(String orderNo) {
        LambdaQueryWrapper<StoreOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(StoreOrder::getOrderId, orderNo);
        return dao.selectOne(lqw);
    }

    public StoreOrder getInfoException(String orderNo) {
        LambdaQueryWrapper<StoreOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(StoreOrder::getOrderId, orderNo);
        StoreOrder storeOrder = dao.selectOne(lqw);
        if (ObjectUtil.isNull(storeOrder)) {
            throw new CrmebException("????????????????????????");
        }
        return storeOrder;
    }

    /** ??????
     * @param request StoreOrderSendRequest ????????????
     * @param storeOrder StoreOrder ????????????
     * @author Mr.Zhang
     * @since 2020-06-10
     */
    private void express(StoreOrderSendRequest request, StoreOrder storeOrder) {
        // ????????????????????????
        validateExpressSend(request);
        //??????????????????
        Express express = expressService.getByCode(request.getExpressCode());
        if (request.getExpressRecordType().equals("1")) { // ????????????
            deliverGoods(request, storeOrder, express);
        }
        if (request.getExpressRecordType().equals("2")) { // ????????????
            request.setExpressName(express.getName());
            expressDump(request, storeOrder, express);
        }

        storeOrder.setDeliveryCode(express.getCode());
        storeOrder.setDeliveryName(express.getName());
        storeOrder.setStatus(1);
        storeOrder.setDeliveryType("express");

        String message = Constants.ORDER_LOG_MESSAGE_EXPRESS.replace("{deliveryName}", express.getName()).replace("{deliveryCode}", storeOrder.getDeliveryId());

        Boolean execute = transactionTemplate.execute(i -> {
            updateById(storeOrder);
            //??????????????????
            storeOrderStatusService.createLog(request.getId(), Constants.ORDER_LOG_EXPRESS, message);
            return Boolean.TRUE;
        });

        if (!execute) throw new CrmebException("?????????????????????");
        User user = userService.getById(storeOrder.getUid());
        if (StrUtil.isNotBlank(user.getPhone())) {
            // ??????????????????
            String smsSwitch = systemConfigService.getValueByKey(SmsConstants.SMS_CONFIG_DELIVER_GOODS_SWITCH);
            if (StrUtil.isNotBlank(smsSwitch) && smsSwitch.equals("1")) {
                String proName = "";
                List<StoreOrderInfoOldVo> voList = storeOrderInfoService.getOrderListByOrderId(storeOrder.getId());
                proName = voList.get(0).getInfo().getProductName();
                if (voList.size() > 1) {
                    proName = proName.concat("???");
                }
                smsService.sendOrderDeliverNotice(user.getPhone(), user.getNickname(), proName, storeOrder.getOrderId());
            }
        }

        // ??????????????????
        pushMessageOrder(storeOrder, user);
    }

    /**
     * ????????????????????????
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
            temMap.put(Constants.WE_CHAT_TEMP_KEY_FIRST, "??????????????????");
            temMap.put("keyword1", storeOrder.getOrderId());
            temMap.put("keyword2", storeOrder.getDeliveryName());
            temMap.put("keyword3", storeOrder.getDeliveryId());
            temMap.put(Constants.WE_CHAT_TEMP_KEY_END, "?????????????????????");
            templateMessageService.pushTemplateMessage(Constants.WE_CHAT_TEMP_KEY_EXPRESS, temMap, userToken.getToken());
            return;
        }

        // ???????????????????????????
        userToken = userTokenService.getTokenByUserId(user.getUid(), UserConstants.USER_TOKEN_TYPE_ROUTINE);
        if (ObjectUtil.isNull(userToken)) {
            return ;
        }
        // ????????????
        temMap.put("character_string1", storeOrder.getOrderId());
        temMap.put("name3", storeOrder.getDeliveryName());
        temMap.put("character_string4", storeOrder.getDeliveryId());
        temMap.put("thing7", "?????????????????????");
        templateMessageService.pushMiniTemplateMessage(Constants.WE_CHAT_PROGRAM_TEMP_KEY_EXPRESS, temMap, userToken.getToken());
    }

    /**
     * ????????????
     * @param request
     * @param storeOrder
     * @param express
     */
    private void expressDump(StoreOrderSendRequest request, StoreOrder storeOrder, Express express) {
        String configExportOpen = systemConfigService.getValueByKeyException("config_export_open");
        if (!configExportOpen.equals("1")) {// ?????????????????????
            throw new CrmebException("????????????????????????");
        }
        MyRecord record = new MyRecord();
        record.set("com", express.getCode());// ??????????????????
        record.set("to_name", storeOrder.getRealName());// ?????????
        record.set("to_tel", storeOrder.getUserPhone());// ???????????????
        record.set("to_addr", storeOrder.getUserAddress());// ?????????????????????
        record.set("from_name", request.getToName());// ?????????
        record.set("from_tel", request.getToTel());// ???????????????
        record.set("from_addr", request.getToAddr());// ?????????????????????
        record.set("temp_id", request.getExpressTempId());// ??????????????????ID
        String siid = systemConfigService.getValueByKeyException("config_export_siid");
        record.set("siid", siid);// ??????????????????
        record.set("count", storeOrder.getTotalNum());// ????????????

        //????????????????????????
        List<Integer> orderIdList = new ArrayList<>();
        orderIdList.add(storeOrder.getId());
        HashMap<Integer, List<StoreOrderInfoOldVo>> orderInfoMap = storeOrderInfoService.getMapInId(orderIdList);
        if(orderInfoMap.isEmpty() || !orderInfoMap.containsKey(storeOrder.getId())){
            throw new CrmebException("?????????????????????????????????");
        }
        List<String> productNameList = new ArrayList<>();
        for (StoreOrderInfoOldVo storeOrderInfoVo : orderInfoMap.get(storeOrder.getId())) {
            productNameList.add(storeOrderInfoVo.getInfo().getProductName());
        }

        record.set("cargo", String.join(",", productNameList));// ????????????
        if (express.getPartnerId()) {
            record.set("partner_id", express.getAccount());// ????????????????????????(????????????????????????)
        }
        if (express.getPartnerKey()) {
            record.set("partner_key", express.getPassword());// ??????????????????(????????????????????????)
        }
        if (express.getNet()) {
            record.set("net", express.getNetName());// ??????????????????(????????????????????????)
        }

        MyRecord myRecord = onePassService.expressDump(record);
        storeOrder.setDeliveryId(myRecord.getStr("kuaidinum"));
    }

    /**
     * ????????????
     */
    private void deliverGoods(StoreOrderSendRequest request, StoreOrder storeOrder, Express express) {
        storeOrder.setDeliveryId(request.getExpressNumber());
    }

    /**
     * ????????????????????????
     */
    private void validateExpressSend(StoreOrderSendRequest request) {
        if (request.getExpressRecordType().equals("1")) {
            if (StrUtil.isBlank(request.getExpressNumber())) throw new CrmebException("?????????????????????");
            return;
        }
        if (StrUtil.isBlank(request.getExpressCode())) throw new CrmebException("?????????????????????");
        if (StrUtil.isBlank(request.getExpressRecordType())) throw new CrmebException("???????????????????????????");
        if (StrUtil.isBlank(request.getExpressTempId())) throw new CrmebException("?????????????????????");
        if (StrUtil.isBlank(request.getToName())) throw new CrmebException("????????????????????????");
        if (StrUtil.isBlank(request.getToTel())) throw new CrmebException("????????????????????????");
        if (StrUtil.isBlank(request.getToAddr())) throw new CrmebException("????????????????????????");
    }

    /** ????????????
     * @param request StoreOrderSendRequest ????????????
     * @param storeOrder StoreOrder ????????????
     * @author Mr.Zhang
     * @since 2020-06-10
     */
    private void delivery(StoreOrderSendRequest request, StoreOrder storeOrder) {
        if (StrUtil.isBlank(request.getDeliveryName())) throw new CrmebException("????????????????????????");
        if (StrUtil.isBlank(request.getDeliveryTel())) throw new CrmebException("??????????????????????????????");
        ValidateFormUtil.isPhone(request.getDeliveryTel(), "?????????????????????");

        //????????????
        storeOrder.setDeliveryName(request.getDeliveryName());
        storeOrder.setDeliveryId(request.getDeliveryTel());
        storeOrder.setStatus(1);
        storeOrder.setDeliveryType("send");

        //????????????????????????
        List<Integer> orderIdList = new ArrayList<>();
        orderIdList.add(storeOrder.getId());
        HashMap<Integer, List<StoreOrderInfoOldVo>> orderInfoMap = storeOrderInfoService.getMapInId(orderIdList);
        if(orderInfoMap.isEmpty() || !orderInfoMap.containsKey(storeOrder.getId())){
            throw new CrmebException("?????????????????????????????????");
        }
        List<String> productNameList = new ArrayList<>();
        for (StoreOrderInfoOldVo storeOrderInfoVo : orderInfoMap.get(storeOrder.getId())) {
            productNameList.add(storeOrderInfoVo.getInfo().getProductName());
        }

        String message = Constants.ORDER_LOG_MESSAGE_DELIVERY.replace("{deliveryName}", request.getDeliveryName()).replace("{deliveryCode}", request.getDeliveryTel());

        Boolean execute = transactionTemplate.execute(i -> {
            // ????????????
            updateById(storeOrder);
            // ??????????????????
            storeOrderStatusService.createLog(request.getId(), Constants.ORDER_LOG_DELIVERY, message);
            return Boolean.TRUE;
        });
        if (!execute) throw new CrmebException("????????????????????????");

        User user = userService.getById(storeOrder.getUid());
        // ??????????????????
        pushMessageDeliveryOrder(storeOrder, user, request, productNameList);
    }

    /**
     * ??????????????????
     * ????????????????????????
     * ?????????????????????
     * ?????????????????????
     */
    private void pushMessageDeliveryOrder(StoreOrder storeOrder, User user, StoreOrderSendRequest request, List<String> productNameList) {
        if (storeOrder.getIsChannel().equals(2)) {
            return;
        }
        if (!storeOrder.getPayType().equals(PayConstants.PAY_TYPE_WE_CHAT)) {
            return;
        }
        UserToken userToken;
        HashMap<String, String> map = new HashMap<>();
        String proName = "";
        if (CollUtil.isNotEmpty(productNameList)) {
            proName = StringUtils.join(productNameList, "|");
        }
        // ?????????
        if (storeOrder.getIsChannel().equals(PayConstants.ORDER_PAY_CHANNEL_PUBLIC)) {
            userToken = userTokenService.getTokenByUserId(user.getUid(), UserConstants.USER_TOKEN_TYPE_WECHAT);
            if (ObjectUtil.isNull(userToken)) {
                return ;
            }
            map.put(Constants.WE_CHAT_TEMP_KEY_FIRST, "??????????????????");
            map.put("keyword1", storeOrder.getOrderId());
            map.put("keyword2", DateUtil.dateToStr(storeOrder.getCreateTime(), Constants.DATE_FORMAT));
            map.put("keyword3", storeOrder.getUserAddress());
            map.put("keyword4", request.getDeliveryName());
            map.put("keyword5", request.getDeliveryTel());
            map.put(Constants.WE_CHAT_TEMP_KEY_END, "?????????????????????");
            // ????????????????????????
            templateMessageService.pushTemplateMessage(Constants.WE_CHAT_TEMP_KEY_DELIVERY, map, userToken.getToken());
            return;
        }
        // ???????????????????????????
        userToken = userTokenService.getTokenByUserId(user.getUid(), UserConstants.USER_TOKEN_TYPE_ROUTINE);
        if (ObjectUtil.isNull(userToken)) {
            return ;
        }

        if (proName.length() > 20) {
            proName = proName.substring(0, 15) + "***";
        }
        map.put("thing8", proName);
        map.put("character_string1", storeOrder.getOrderId());
        map.put("name4", request.getDeliveryName());
        map.put("phone_number10", request.getDeliveryTel());
        templateMessageService.pushMiniTemplateMessage(Constants.WE_CHAT_PROGRAM_TEMP_KEY_DELIVERY, map, userToken.getToken());
    }

    /** ??????
     * @param request StoreOrderSendRequest ????????????
     * @param storeOrder StoreOrder ????????????
     * @author Mr.Zhang
     * @since 2020-06-10
     */
    private void virtual(StoreOrderSendRequest request, StoreOrder storeOrder) {
        //????????????
        storeOrder.setDeliveryType("fictitious");
        storeOrder.setStatus(1);

        Boolean execute = transactionTemplate.execute(i -> {
            updateById(storeOrder);
            //??????????????????
            storeOrderStatusService.createLog(request.getId(), Constants.ORDER_LOG_DELIVERY_VI, "??????????????????");
            return Boolean.TRUE;
        });
        if (!execute) throw new CrmebException("????????????????????????");
    }

    /**
     * ??????????????????
     * @param dateLimit ?????????
     * @param status String ??????
     * @return Integer
     */
    private Integer getCount(String dateLimit, String status) {
        //?????????????????????
        QueryWrapper<StoreOrder> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotBlank(dateLimit)) {
            dateLimitUtilVo dateLimitUtilVo = DateUtil.getDateLimit(dateLimit);
            queryWrapper.between("create_time", dateLimitUtilVo.getStartTime(), dateLimitUtilVo.getEndTime());
        }
        getStatusWhereNew(queryWrapper, status);
        return dao.selectCount(queryWrapper);
    }

    /**
     * ??????????????????
     * @param dateLimit ?????????
     * @param type  ????????????
     * @return Integer
     */
    private BigDecimal getAmount(String dateLimit, String type) {
        QueryWrapper<StoreOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("sum(pay_price) as pay_price");
        if (StringUtils.isNotBlank(type)) {
            queryWrapper.eq("pay_type", type);
        }
        queryWrapper.isNotNull("pay_time");
        queryWrapper.eq("paid", 1);
        if (StringUtils.isNotBlank(dateLimit)) {
            dateLimitUtilVo dateLimitUtilVo = DateUtil.getDateLimit(dateLimit);
            queryWrapper.between("create_time", dateLimitUtilVo.getStartTime(), dateLimitUtilVo.getEndTime());
        }
        StoreOrder storeOrder = dao.selectOne(queryWrapper);
        if (ObjectUtil.isNull(storeOrder)) {
            return BigDecimal.ZERO;
        }
        return storeOrder.getPayPrice();
    }

    /**
     * ??????request???where??????
     * @param queryWrapper QueryWrapper<StoreOrder> ?????????
     * @param request StoreOrderSearchRequest ????????????
     */
    private void getRequestTimeWhere(QueryWrapper<StoreOrder> queryWrapper, StoreOrderSearchRequest request) {
        if (StringUtils.isNotBlank(request.getDateLimit())) {
            dateLimitUtilVo dateLimitUtilVo = DateUtil.getDateLimit(request.getDateLimit());
            queryWrapper.between("create_time", dateLimitUtilVo.getStartTime(), dateLimitUtilVo.getEndTime());
        }
    }

    /**
     * ????????????????????????where??????
     * @param queryWrapper QueryWrapper<StoreOrder> ?????????
     * @param status String ??????
     */
    private void getStatusWhereNew(QueryWrapper<StoreOrder> queryWrapper, String status) {
        if(StrUtil.isBlank(status)) {
            return;
        }
        switch (status){
            case Constants.ORDER_STATUS_ALL: //??????
                break;
            case Constants.ORDER_STATUS_UNPAID: //?????????
                queryWrapper.eq("paid", 0);//????????????
                queryWrapper.eq("status", 0); //????????????
                queryWrapper.eq("is_del", 0);//????????????
                break;
            case Constants.ORDER_STATUS_NOT_SHIPPED: //?????????
                queryWrapper.eq("paid", 1);
                queryWrapper.eq("status", 0);
                queryWrapper.eq("refund_status", 0);
                queryWrapper.eq("shipping_type", 1);//????????????
                queryWrapper.eq("is_del", 0);
                break;
            case Constants.ORDER_STATUS_SPIKE: //?????????
                queryWrapper.eq("paid", 1);
                queryWrapper.eq("status", 1);
                queryWrapper.eq("refund_status", 0);
                queryWrapper.eq("is_del", 0);
                break;
            case Constants.ORDER_STATUS_BARGAIN: //?????????
                queryWrapper.eq("paid", 1);
                queryWrapper.eq("status", 2);
                queryWrapper.eq("refund_status", 0);
                queryWrapper.eq("is_del", 0);
                break;
            case Constants.ORDER_STATUS_COMPLETE: //????????????
                queryWrapper.eq("paid", 1);
                queryWrapper.eq("status", 3);
                queryWrapper.eq("refund_status", 0);
                queryWrapper.eq("is_del", 0);
                break;
            case Constants.ORDER_STATUS_TOBE_WRITTEN_OFF: //?????????
                queryWrapper.eq("paid", 1);
                queryWrapper.eq("status", 0);
                queryWrapper.eq("refund_status", 0);
                queryWrapper.eq("shipping_type", 2);//????????????
                queryWrapper.eq("is_del", 0);
                break;
            case Constants.ORDER_STATUS_REFUNDING: //?????????
                queryWrapper.eq("paid", 1);
                queryWrapper.in("refund_status", 1,3);
                queryWrapper.eq("is_del", 0);
                break;
            case Constants.ORDER_STATUS_REFUNDED: //?????????
                queryWrapper.eq("paid", 1);
                queryWrapper.eq("refund_status", 2);
                queryWrapper.eq("is_del", 0);
                break;
            case Constants.ORDER_STATUS_DELETED: //?????????
                queryWrapper.eq("is_del", 1);
                break;
            default:
                queryWrapper.eq("paid", 1);
                queryWrapper.ne("refund_status", 2);
                break;
        }
        queryWrapper.eq("is_system_del", 0);
    }

    /**
     * ????????????????????????where??????
     * @param queryWrapper QueryWrapper<StoreOrder> ?????????
     * @param status String ??????
     */
    private void getStatusWhere(QueryWrapper<StoreOrder> queryWrapper, String status) {
        if (StrUtil.isBlank(status)) {
            return;
        }
        switch (status){
            case Constants.ORDER_STATUS_UNPAID: //?????????
                queryWrapper.eq("paid", 0);//????????????
                queryWrapper.eq("status", 0); //????????????
                queryWrapper.eq("is_del", 0);//????????????
                break;
            case Constants.ORDER_STATUS_NOT_SHIPPED: //?????????
                queryWrapper.eq("paid", 1);
                queryWrapper.eq("status", 0);
                queryWrapper.eq("refund_status", 0);
                queryWrapper.eq("shipping_type", 1);//????????????
                queryWrapper.eq("is_del", 0);
                break;
            case Constants.ORDER_STATUS_SPIKE: //?????????
                queryWrapper.eq("paid", 1);
                queryWrapper.eq("status", 1);
                queryWrapper.eq("refund_status", 0);
                queryWrapper.eq("is_del", 0);
                break;
            case Constants.ORDER_STATUS_BARGAIN: //?????????
                queryWrapper.eq("paid", 1);
                queryWrapper.eq("status", 2);
                queryWrapper.eq("refund_status", 0);
                queryWrapper.eq("is_del", 0);
                break;
            case Constants.ORDER_STATUS_COMPLETE: //????????????
                queryWrapper.eq("paid", 1);
                queryWrapper.eq("status", 3);
                queryWrapper.eq("refund_status", 0);
                queryWrapper.eq("is_del", 0);
                break;
            case Constants.ORDER_STATUS_TOBE_WRITTEN_OFF: //?????????
                queryWrapper.eq("paid", 1);
                queryWrapper.eq("status", 0);
                queryWrapper.eq("refund_status", 0);
                queryWrapper.eq("shipping_type", 2);//????????????
                queryWrapper.eq("is_del", 0);
                break;
            case Constants.ORDER_STATUS_REFUNDING: //?????????
                queryWrapper.eq("paid", 1);
                queryWrapper.in("refund_status", 1,3);
                queryWrapper.eq("is_del", 0);
                break;
            case Constants.ORDER_STATUS_REFUNDED: //?????????
                queryWrapper.eq("paid", 1);
                queryWrapper.eq("refund_status", 2);
                queryWrapper.eq("is_del", 0);
                break;
            case Constants.ORDER_STATUS_DELETED: //?????????
                queryWrapper.eq("is_del", 1);
                break;
            default:
                queryWrapper.eq("paid", 1);
                queryWrapper.ne("refund_status", 2);
                break;
        }
        queryWrapper.eq("is_system_del", 0);
    }

    @Override
    public Map<String, String> getStatus(StoreOrder storeOrder) {
        //??????map????????????????????????
        Map<String, String> map = new HashMap<>();
        //??????????????????????????????
        map.put("key", "");
        map.put("value", "");
        if(null == storeOrder){
            return map;
        }

        // ?????????
        if(!storeOrder.getPaid()
                && storeOrder.getStatus() == 0
                && storeOrder.getRefundStatus() == 0
                && !storeOrder.getIsDel()
                && !storeOrder.getIsSystemDel()){
            map.put("key", Constants.ORDER_STATUS_UNPAID);
            map.put("value", Constants.ORDER_STATUS_STR_UNPAID);
            return map;
        }

        // ?????????
        if(storeOrder.getPaid()
                && storeOrder.getStatus() == 0
                && storeOrder.getRefundStatus() == 0
                && storeOrder.getShippingType() == 1
                && !storeOrder.getIsDel()
                && !storeOrder.getIsSystemDel()){
            map.put("key", Constants.ORDER_STATUS_NOT_SHIPPED);
            map.put("value", Constants.ORDER_STATUS_STR_NOT_SHIPPED);
            return map;
        }

        // ?????????
        if(storeOrder.getPaid()
                && storeOrder.getStatus() == 1
                && storeOrder.getRefundStatus() == 0
                && storeOrder.getShippingType() == 1
                && !storeOrder.getIsDel()
                && !storeOrder.getIsSystemDel()){
            map.put("key", Constants.ORDER_STATUS_SPIKE);
            map.put("value", Constants.ORDER_STATUS_STR_SPIKE);
            return map;
        }

        // ?????????
        if(storeOrder.getPaid()
                && storeOrder.getStatus() == 2
                && storeOrder.getRefundStatus() == 0
                && !storeOrder.getIsDel()
                && !storeOrder.getIsSystemDel()){
            map.put("key", Constants.ORDER_STATUS_BARGAIN);
            map.put("value", Constants.ORDER_STATUS_STR_BARGAIN);
            return map;
        }

        // ????????????
        if(storeOrder.getPaid()
                && storeOrder.getStatus() == 3
                && storeOrder.getRefundStatus() == 0
                && !storeOrder.getIsDel()
                && !storeOrder.getIsSystemDel()){
            map.put("key", Constants.ORDER_STATUS_COMPLETE);
            map.put("value", Constants.ORDER_STATUS_STR_COMPLETE);
            return map;
        }

        // ?????????
        if(storeOrder.getPaid()
                && storeOrder.getStatus() == 0
                && storeOrder.getRefundStatus() == 0
                && storeOrder.getShippingType() == 2
                && !storeOrder.getIsDel()
                && !storeOrder.getIsSystemDel()){
            map.put("key", Constants.ORDER_STATUS_TOBE_WRITTEN_OFF);
            map.put("value", Constants.ORDER_STATUS_STR_TOBE_WRITTEN_OFF);
            return map;
        }

        //????????????
        if(storeOrder.getPaid()
                && storeOrder.getRefundStatus() == 1
                && !storeOrder.getIsDel()
                && !storeOrder.getIsSystemDel()){
            map.put("key", Constants.ORDER_STATUS_APPLY_REFUNDING);
            map.put("value", Constants.ORDER_STATUS_STR_APPLY_REFUNDING);
            return map;
        }

        //?????????
        if(storeOrder.getPaid()
                && storeOrder.getRefundStatus() == 3
                && !storeOrder.getIsDel()
                && !storeOrder.getIsSystemDel()){
            map.put("key", Constants.ORDER_STATUS_REFUNDING);
            map.put("value", Constants.ORDER_STATUS_STR_REFUNDING);
            return map;
        }

        //?????????
        if(storeOrder.getPaid()
                && storeOrder.getRefundStatus() == 2
                && !storeOrder.getIsDel()
                && !storeOrder.getIsSystemDel()){
            map.put("key", Constants.ORDER_STATUS_REFUNDED);
            map.put("value", Constants.ORDER_STATUS_STR_REFUNDED);
        }

        //?????????
        if(storeOrder.getIsDel() || storeOrder.getIsSystemDel()){
            map.put("key", Constants.ORDER_STATUS_DELETED);
            map.put("value", Constants.ORDER_STATUS_STR_DELETED);
        }

        //??????
        return map;
    }

}

