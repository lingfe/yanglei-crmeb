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
 * StoreOrderServiceImpl 接口实现
 * @author: 零风
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
        if(null == storeOrder )throw new CrmebException("没有找到订单!");
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
        //定义查询对象
        LambdaQueryWrapper<StoreOrder> storeOrderLambdaQueryWrapper=new LambdaQueryWrapper<>();
        storeOrderLambdaQueryWrapper.eq(StoreOrder::getStatus,1);
        storeOrderLambdaQueryWrapper.eq(StoreOrder::getIsDel,Boolean.FALSE);
        storeOrderLambdaQueryWrapper.eq(StoreOrder::getRefundStatus,0);
        storeOrderLambdaQueryWrapper.orderByDesc(StoreOrder::getId).orderByDesc(StoreOrder::getPayTime);

        //得到数据
        List<StoreOrder> list=dao.selectList(storeOrderLambdaQueryWrapper);
        if(list == null ) return new ArrayList<>();
        return list;
    }

    @Override
    public ProductOrderDataResponse getWhereProductIdAndMerId(Integer productId, Integer merId) {
        //定义变量-日期相关
        String date =null;          //日期范围
        String startTime = null;    //开始日期
        String endTime = DateUtil.nowDateTime(Constants.DATE_FORMAT);           //结束日期
        StringBuffer dateSB=new StringBuffer("%s").append(",").append(endTime); //日期字符串拼接
        Date lastMonthEndDay=DateUtil.strToDate(DateUtil.getLastMonthEndDay(),Constants.DATE_FORMAT);//上个月最后一天

        //定义变量-交易相关
        BigDecimal dayGmv = BigDecimal.ZERO;           //今日交易额
        BigDecimal yesterdayGmv = BigDecimal.ZERO;     //昨日交易额
        BigDecimal thisMonthGmv = BigDecimal.ZERO;     //本月交易额
        BigDecimal totalGmv=BigDecimal.ZERO;           //总销售额

        //定义变量-订单统计
        Integer dayOrderNum=0;          //今日订单总数
        Integer yesterdayOrderNum=0;    //昨日订单总数
        Integer thisMonthOrderNum=0;    //本月总订单总数
        Integer totalOrderNum=0;        //总订单数量

        //实例化对象
        ProductOrderDataResponse response=new ProductOrderDataResponse();
        List<StoreOrderInfo>  storeOrderInfoList = null;
        List<StoreOrder> storeOrderList =null;

        //得到-产品信息
        StoreProduct storeProduct=storeProductService.getById(productId);
        if(storeProduct == null)return new ProductOrderDataResponse();
        response.setPrice(storeProduct.getPrice());
        response.setProductId(storeProduct.getId());
        response.setStoreName(storeProduct.getStoreName());

        //得到-今日订单总数、总销售额
        startTime=DateUtil.addDay(DateUtil.nowDateTime(), 0, Constants.DATE_FORMAT_DATE);
        date=String.format(dateSB.toString(),startTime);
        storeOrderInfoList = storeOrderInfoService.getWhereProductIdAndDate(productId,date);
        storeOrderList = this.getWhereOrderInfoList(storeOrderInfoList,merId);
        //赋值-今日订单总数、总销售额
        dayGmv=storeOrderList.stream().map(StoreOrder::getPayPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        dayOrderNum = storeOrderList.size();
        response.setDayGmv(dayGmv);
        response.setDayOrderNum(dayOrderNum);

        //得到-昨日订单总数、总销售额
        startTime = DateUtil.addDay(DateUtil.nowDateTime(), -1, Constants.DATE_FORMAT_DATE);
        date=String.format("%s,%s",startTime,startTime);
        storeOrderInfoList = storeOrderInfoService.getWhereProductIdAndDate(productId,date);
        storeOrderList = this.getWhereOrderInfoList(storeOrderInfoList,merId);
        //赋值-昨日订单总数、总销售额
        yesterdayGmv=storeOrderList.stream().map(StoreOrder::getPayPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        yesterdayOrderNum = storeOrderList.size();
        response.setYesterdayGmv(yesterdayGmv);
        response.setYesterdayOrderNum(yesterdayOrderNum);

        //得到-本月订单总数、总销售额
        startTime = DateUtil.addDay(lastMonthEndDay, 0, Constants.DATE_FORMAT);
        storeOrderInfoList = storeOrderInfoService.getWhereProductIdAndDate(productId,String.format(dateSB.toString(),startTime));
        storeOrderList = this.getWhereOrderInfoList(storeOrderInfoList,merId);
        //赋值-本月订单总数、总销售额
        thisMonthGmv=storeOrderList.stream().map(StoreOrder::getPayPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        thisMonthOrderNum = storeOrderList.size();
        response.setThisMonthGmv(thisMonthGmv);
        response.setThisMonthOrderNum(thisMonthOrderNum);

        //得到-订单总数、总销售额
        storeOrderInfoList = storeOrderInfoService.getWhereProductIdAndDate(productId,null);
        storeOrderList = this.getWhereOrderInfoList(storeOrderInfoList,merId);
        //赋值-订单总数、总销售额
        totalGmv=storeOrderList.stream().map(StoreOrder::getPayPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        totalOrderNum = storeOrderList.size();
        response.setTotalGmv(totalGmv);
        response.setTotalOrderNum(totalOrderNum);

        //返回
        return response;
    }

    @Override
    public List<StoreOrder> getWhereOrderInfoList(List<StoreOrderInfo> storeOrderInfoList,Integer merId) {
        //验证-订单详情非空
        if(storeOrderInfoList == null || storeOrderInfoList.size() == 0 ){
            return new ArrayList<>();
        }

        //读取-订单信息
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
        //得到分页对象
        Page<Object> startPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //定义查询对象
        QueryWrapper<StoreOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "order_id", "uid", "real_name", "pay_price", "pay_type", "create_time", "status", "refund_status"
                , "refund_reason_wap_img", "refund_reason_wap_explain", "refund_reason_wap", "refund_reason", "refund_reason_time"
                , "is_del", "mer_id","combination_id", "pink_id", "seckill_id", "bargain_id", "verify_code", "remark", "paid",
                "is_system_del", "shipping_type", "type","is_lmsjfahuo");

        //条件-订单号
        if (StrUtil.isNotBlank(request.getOrderNo())) {
            queryWrapper.eq("order_id", request.getOrderNo());
            if(StringUtils.isNumeric(request.getOrderNo()))queryWrapper.or().eq("id",request.getOrderNo());
        }

        //条件-时间范围
        this.getRequestTimeWhere(queryWrapper, request);

        //条件-状态
        this.getStatusWhere(queryWrapper, request.getStatus());

        //条件-订单类型
        if (ObjectUtil.isNotNull(request.getType()) && request.getType() > -1 ) {
            queryWrapper.eq("type", request.getType());
        }

        //条件-商户ID
        if (ObjectUtil.isNotNull(request.getMerId()) && request.getMerId() >= 0) {
            queryWrapper.eq("mer_id", request.getMerId());
        }

        //排序
        queryWrapper.orderByDesc("id");

        //得到数据
        List<StoreOrderDetailResponse> detailResponseList = new ArrayList<>();
        List<StoreOrder> orderList = dao.selectList(queryWrapper);
        if(CollUtil.isNotEmpty(orderList)){
            detailResponseList = this.formatOrder1(orderList);
        }

        //返回
        return CommonPage.restPage(CommonPage.copyPageInfo(startPage, detailResponseList));
    }


    /**
     * H5订单列表
     * @param uid 用户uid
     * @param status 评价等级|0=未支付,1=待发货,2=待收货,3=待评价,4=已完成,-3=售后/退款
     * @param pageParamRequest 分页参数
     * @return 订单结果列表
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
     * 创建订单
     * @param storeOrder 订单参数
     * @return 结果标识
     */
    @Override
    public boolean create(StoreOrder storeOrder) {
        return dao.insert(storeOrder) > 0;
    }

    /**
     * 订单基本查询
     * @param storeOrder 订单参数
     * @return 订单查询结果
     */
    @Override
    public List<StoreOrder> getByEntity(StoreOrder storeOrder) {
        LambdaQueryWrapper<StoreOrder> lqw = new LambdaQueryWrapper<>();
        lqw.setEntity(storeOrder);
        return dao.selectList(lqw);
    }

    /**
     * 订单基本查询一条
     * @param storeOrder 参数
     * @return 查询结果
     */
    @Override
    public StoreOrder getByEntityOne(StoreOrder storeOrder) {
        LambdaQueryWrapper<StoreOrder> lqw = new LambdaQueryWrapper<>();
        lqw.setEntity(storeOrder);
        return dao.selectOne(lqw);
    }

    /**
     * 核销列表
     * @param request 请求参数
     * @param pageParamRequest 分页类参数
     * @author Mr.Zhang
     * @since 2020-05-28
     * @return List<StoreOrder>
     */
    @Override
    public SystemWriteOffOrderResponse getWriteOffList(SystemWriteOffOrderSearchRequest request, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<StoreOrder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        String where = " is_del = 0 and shipping_type = 2";
//        String where = " is_del = 0 and paid = 1";
        //时间
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
        systemWriteOffOrderResponse.setOrderTotalPrice(totalPrice);   //订单总金额

        BigDecimal refundPrice = dao.getRefundPrice(where);
        if(refundPrice == null){
            refundPrice = price;
        }
        systemWriteOffOrderResponse.setRefundTotalPrice(refundPrice); //退款总金额
        systemWriteOffOrderResponse.setRefundTotal(dao.getRefundTotal(where));  //退款总单数


        Page<StoreOrder> storeOrderPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        lambdaQueryWrapper.apply(where);
        lambdaQueryWrapper.orderByDesc(StoreOrder::getId);
        List<StoreOrder> storeOrderList = dao.selectList(lambdaQueryWrapper);

        if(storeOrderList.size() < 1){
            systemWriteOffOrderResponse.setList(CommonPage.restPage(new PageInfo<>()));
            return systemWriteOffOrderResponse;
        }

        List<StoreOrderItemResponse> storeOrderItemResponseArrayList = formatOrder(storeOrderList);

        systemWriteOffOrderResponse.setTotal(storeOrderPage.getTotal()); //总单数
        systemWriteOffOrderResponse.setList(CommonPage.restPage(CommonPage.copyPageInfo(storeOrderPage, storeOrderItemResponseArrayList)));

        return systemWriteOffOrderResponse;
    }

    /**
     * 格式化订单信息，对外输出一致
     * @param orderList List<StoreOrder> 订单列表
     * @return List<StoreOrderItemResponse>
     */
    private List<StoreOrderDetailResponse> formatOrder1(List<StoreOrder> orderList) {
        //定义-订单详细-响应list集合
        List<StoreOrderDetailResponse> detailResponseList  = new ArrayList<>();
        if (CollUtil.isEmpty(orderList)) {
            return detailResponseList;
        }

        //取出-订单id标识list集合
        List<Integer> orderIdList = orderList.stream().map(StoreOrder::getId).distinct().collect(Collectors.toList());

        //获取-订单详情map
        HashMap<Integer, List<StoreOrderInfoOldVo>> orderInfoList = storeOrderInfoService.getMapInId(orderIdList);

        //循环处理-订单信息
        for (StoreOrder storeOrder : orderList) {
            //实例化-订单详情-响应对象
            StoreOrderDetailResponse storeOrderItemResponse = new StoreOrderDetailResponse();
            BeanUtils.copyProperties(storeOrder, storeOrderItemResponse);//转换

            //设置-商品信息
            storeOrderItemResponse.setProductList(orderInfoList.get(storeOrder.getId()));

            //设置-订单状态
            storeOrderItemResponse.setStatusStr(this.getStatus(storeOrder));
            storeOrderItemResponse.setStatus(storeOrder.getStatus());

            //设置-支付方式
            String paytypestr=this.getOrderPayTypeStr(storeOrder.getPayType());
            storeOrderItemResponse.setPayTypeStr(paytypestr);

            //设置-订单类型信息
            storeOrderItemResponse.setOrderType(getOrderTypeStr(storeOrder));

            //验证-订单类型
            String shopName="";
            switch (storeOrder.getType()){
                case Constants.ORDER_TYPE_0: shopName = "系统"; break;
                case Constants.ORDER_TYPE_1: shopName = "未知商户"; break;
                case Constants.ORDER_TYPE_2: shopName = regionalAgencyService.getRaName(storeOrder.getMerId()); break;
                case Constants.ORDER_TYPE_3: shopName = retailerService.getRetailerName(storeOrder.getMerId()); break;
                case Constants.ORDER_TYPE_4: shopName = supplierService.getSupplierName(storeOrder.getMerId()); break;
                default: shopName = "订单类型错误"; break;
            }
            storeOrderItemResponse.setShopName(shopName);

            //得到-区域代理信息
            List<RegionalAgency> list = regionalAgencyService.getWhereUserID(storeOrder.getMerId());
            if(list != null && list.size()>=1){
                storeOrderItemResponse.setRegionalAgency(list.get(0));
            }

            //添加到-订单详细-响应list集合
            detailResponseList.add(storeOrderItemResponse);
        }

        //返回-订单详细-响应list集合
        return detailResponseList;
    }

    /**
     * 获取订单类型（前端展示）
     * @param storeOrder 订单
     * @return String
     */
    private String getOrderTypeStr(StoreOrder storeOrder) {
        //定义-订单类型字符串
        String orderTypeFormat = "[{}订单]{}";
        String orderType = StrUtil.format(orderTypeFormat, "普通", "");

        // 核销
        if (StrUtil.isNotBlank(storeOrder.getVerifyCode())) {
            orderType = StrUtil.format(orderTypeFormat, "核销", "");
        }

        // 秒杀
        if (ObjectUtil.isNotNull(storeOrder.getSeckillId()) && storeOrder.getSeckillId() > 0) {
            orderType = StrUtil.format(orderTypeFormat, "秒杀", "");
        }

        // 砍价
        if (ObjectUtil.isNotNull(storeOrder.getBargainId()) && storeOrder.getBargainId() > 0) {
            orderType = StrUtil.format(orderTypeFormat, "砍价", "");
        }

        // 拼团
        if (ObjectUtil.isNotNull(storeOrder.getCombinationId()) && storeOrder.getCombinationId() > 0) {
            StorePink storePink = storePinkService.getById(storeOrder.getPinkId());
            if (ObjectUtil.isNotNull(storePink)) {
                String pinkstatus = "";
                if (storePink.getStatus() == 2) {
                    pinkstatus = "已完成";
                } else if (storePink.getStatus() == 3) {
                    pinkstatus = "未完成";
                } else {
                    pinkstatus = "正在进行中";
                }
                orderType = StrUtil.format(orderTypeFormat, "拼团", pinkstatus);
            }
        }

        // 视频订单
        if (storeOrder.getType().equals(1)) {
            orderType = StrUtil.format(orderTypeFormat, "视频号", "");
        }else if(storeOrder.getType().equals(2)){
            orderType = StrUtil.format(orderTypeFormat, "区域代理", "");
        }else if(storeOrder.getType().equals(3)){
            orderType = StrUtil.format(orderTypeFormat, "零售商", "");
        }

        //返回-订单类型
        return orderType;
    }

    /**
     * 格式化订单信息，对外输出一致
     * @param storeOrderList List<StoreOrder> 订单列表
     * @author Mr.Zhang
     * @since 2020-05-28
     * @return List<StoreOrderItemResponse>
     */
    private List<StoreOrderItemResponse> formatOrder(List<StoreOrder> storeOrderList) {
        List<StoreOrderItemResponse> storeOrderItemResponseArrayList  = new ArrayList<>();
        if(null == storeOrderList || storeOrderList.size() < 1){
            return storeOrderItemResponseArrayList;
        }
        //门店id
        List<Integer> storeIdList = storeOrderList.stream().map(StoreOrder::getStoreId).distinct().collect(Collectors.toList());
        //店员id / 核销员id
        List<Integer> clerkIdList = storeOrderList.stream().map(StoreOrder::getClerkId).distinct().collect(Collectors.toList());

        //订单id集合
        List<Integer> orderIdList = storeOrderList.stream().map(StoreOrder::getId).distinct().collect(Collectors.toList());

        //获取门店map
        HashMap<Integer, SystemStore> systemStoreList = systemStoreService.getMapInId(storeIdList);
        //获取店员map
//        HashMap<Integer, SystemStoreStaff> systemStoreStaffList = systemStoreStaffService.getMapInId(clerkIdList);
        HashMap<Integer, SystemAdmin> systemStoreStaffList = systemAdminService.getMapInId(clerkIdList);
        //获取订单详情map
        HashMap<Integer, List<StoreOrderInfoOldVo>> orderInfoList = storeOrderInfoService.getMapInId(orderIdList);

        //根据用户获取信息
        List<Integer> userIdList = storeOrderList.stream().map(StoreOrder::getUid).distinct().collect(Collectors.toList());
        //订单用户信息
        HashMap<Integer, User> userList = userService.getMapListInUid(userIdList);

        //获取推广人id集合
        List<Integer> spreadPeopleUidList = new ArrayList<>();
        for(Map.Entry<Integer, User> entry : userList.entrySet()){
            spreadPeopleUidList.add(entry.getValue().getSpreadUid());
        }

        //推广信息
        HashMap<Integer, User> mapListInUid = new HashMap<>();
        if(userIdList.size() > 0 && spreadPeopleUidList.size() > 0) {
            //推广人信息
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

            // 添加核销人信息
            String clerkName = "";
            if(systemStoreStaffList.containsKey(storeOrder.getClerkId())){
                clerkName = systemStoreStaffList.get(storeOrder.getClerkId()).getRealName();
            }
            storeOrderItemResponse.setProductList(orderInfoList.get(storeOrder.getId()));
            storeOrderItemResponse.setTotalNum(storeOrder.getTotalNum());

            //订单状态
            storeOrderItemResponse.setStatusStr(this.getStatus(storeOrder));
            storeOrderItemResponse.setStatus(storeOrder.getStatus());
            //支付方式
            String paytypestr=this.getOrderPayTypeStr(storeOrder.getPayType());
            storeOrderItemResponse.setPayTypeStr(paytypestr);

            //推广人信息
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
        //定义查询对象
        QueryWrapper<StoreOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("sum(pay_price) as pay_price").
                ne("pay_type","integral").
                ne("refund_status",2).
                eq("paid", 1).
                eq("is_del", 0);

        //条件-用户ID
        if(null != userId){
            queryWrapper.eq("uid", userId);
        }

        //条件-日期
        if(null != date){
            dateLimitUtilVo dateLimit = DateUtil.getDateLimit(date);
            queryWrapper.between("create_time", dateLimit.getStartTime(), dateLimit.getEndTime());
        }

        //条件-状态
        if(null !=status && status.length > 0){
            queryWrapper.in("status",status);
        }

        //条件-额度控制
        if(isQuotaControl){
            queryWrapper.in("quota_control",
                    Constants.ORDER_QUOTA_ADD,
                    Constants.ORDER_QUOTA_ADD_SUB);
        }

        //得到数据
        StoreOrder storeOrder = dao.selectOne(queryWrapper);
        if(null == storeOrder || null == storeOrder.getPayPrice()){
            return BigDecimal.ZERO;
        }
        return storeOrder.getPayPrice();
    }

    /**
     * 订单列表返回map
     * @param orderIdList Integer 订单id
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
        //定义查询对象
        LambdaQueryWrapper<StoreOrder> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        //条件-支付状态
        lambdaQueryWrapper.eq(StoreOrder::getPaid,1);
        //条件-是否删除
        lambdaQueryWrapper.eq(StoreOrder::getIsDel, 0);

        //条件-ID类型
        this.getTypeId(type, value, lambdaQueryWrapper);

        //条件-时间范围
        if(StringUtils.isNotBlank(date)){
            dateLimitUtilVo dateLimit = DateUtil.getDateLimit(date);
            lambdaQueryWrapper.between(StoreOrder::getCreateTime, dateLimit.getStartTime(), dateLimit.getEndTime());
        }

        //返回统计
        return dao.selectCount(lambdaQueryWrapper);
    }

    /**
     * 按开始结束时间分组订单
     * @param date String 时间范围
     * @param lefTime int 截取创建时间长度
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

    /** 退款
     * @param request StoreOrderRefundRequest 退款参数
     * @return boolean
     * 这里只处理订单状态
     * 余额支付需要把余额给用户加回去
     * 其余处理放入redis中处理
     */
    @Override
    public boolean refund(StoreOrderRefundRequest request) {
        //得到订单信息
        StoreOrder storeOrder = this.getInfoException(request.getOrderNo());

        //验证
        if (!storeOrder.getPaid()) {
            throw new CrmebException("未支付无法退款");
        }else if (storeOrder.getRefundPrice().add(request.getAmount()).compareTo(storeOrder.getPayPrice()) > 0) {
            throw new CrmebException("退款金额大于支付金额，请修改退款金额");
        }else if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            if (storeOrder.getPayPrice().compareTo(BigDecimal.ZERO) != 0) {
                throw new CrmebException("退款金额不能为0，请修改退款金额");
            }
        }else if(storeOrder.getPayType().equals("integral")){
            throw new CrmebException("积分兑换订单不支持退款！");
        }

        //订单ID标识
        request.setOrderId(storeOrder.getId());

        //得到下单用户
        User user = userService.getById(storeOrder.getUid());

        //退款-微信
        Boolean isTask=true;
        if (storeOrder.getPayType().equals(PayConstants.PAY_TYPE_WE_CHAT) && request.getAmount().compareTo(BigDecimal.ZERO) > 0) {
            try {
                isTask=false;
                storeOrderRefundService.refund(request, storeOrder);
            } catch (Exception e) {
                e.printStackTrace();
                throw new CrmebException("微信申请退款失败！");
            }
        }

        //修改订单退款状态
        storeOrder.setRefundStatus(3);
        storeOrder.setRefundPrice(request.getAmount());

        //执行
        Boolean finalIsTask = isTask;
        Boolean execute = transactionTemplate.execute(e -> {
            //修改订单
            this.updateById(storeOrder);

            //新增日志
            request.setOrderId(storeOrder.getId());
            userBillService.saveRefundBill(request, user);

            //验证-余额支付
            if (storeOrder.getPayType().equals(PayConstants.PAY_TYPE_YUE)) {
                // 更新用户金额
                userService.operationNowMoney(user.getUid(), request.getAmount(), user.getNowMoney(), "add");
            }

            // 退款task
            if (finalIsTask){
                redisUtil.lPush(Constants.ORDER_TASK_REDIS_KEY_AFTER_REFUND_BY_USER, storeOrder.getId());
            }
            return Boolean.TRUE;
        });

        if(!execute){
            storeOrderStatusService.saveRefund(storeOrder.getId(), request.getAmount(), "失败");
            throw new CrmebException("订单更新失败");
        }

        // 发送消息通知
        HashMap<String, String> temMap = new HashMap<>();
        temMap.put(Constants.WE_CHAT_TEMP_KEY_FIRST, "您的订单退款申请被通过，钱款将退还至您的支付账户，请耐心等待。");
        temMap.put("keyword1", storeOrder.getOrderId());
        temMap.put("keyword2", storeOrder.getPayPrice().toString());
        temMap.put("keyword3", DateUtil.dateToStr(storeOrder.getCreateTime(), Constants.DATE_FORMAT));
        temMap.put(Constants.WE_CHAT_TEMP_KEY_END, "感谢你的使用。");
        pushMessageRefundOrder(storeOrder, user, temMap);
        return execute;
    }

    /**
     * 发送消息通知
     * 根据用户类型发送
     * 公众号模板消息
     * 小程序订阅消息
     */
    private void pushMessageRefundOrder(StoreOrder storeOrder, User user, HashMap<String, String> temMap) {
        if (user.getUserType().equals(UserConstants.USER_TYPE_H5)) {
            return;
        }
        UserToken userToken;
        // 公众号
        if (user.getUserType().equals(UserConstants.USER_TYPE_WECHAT)) {
            userToken = userTokenService.getTokenByUserId(user.getUid(), UserConstants.USER_TOKEN_TYPE_WECHAT);
            if (ObjectUtil.isNull(userToken)) {
                return ;
            }
            // 发送微信模板消息
            templateMessageService.pushTemplateMessage(Constants.WE_CHAT_TEMP_KEY_ORDER_REFUND, temMap, userToken.getToken());
            return;
        }

        // 小程序发送订阅消息
        String storeNameAndCarNumString = orderUtils.getStoreNameAndCarNumString(storeOrder.getId());
        if(StringUtils.isNotBlank(storeNameAndCarNumString)){
            WechatSendMessageForPaySuccess paySuccess = new WechatSendMessageForPaySuccess(
                    storeOrder.getId()+"",
                    storeOrder.getPayPrice()+"",
                    storeOrder.getPayTime()+"",
                    "暂无",
                    storeOrder.getTotalPrice()+"",
                    storeNameAndCarNumString);
            orderUtils.sendWeiChatMiniMessageForPaySuccess(paySuccess, userService.getById(storeOrder).getUid());
        }
    }

    /**
     * 订单详情（PC）
     * @param orderNo 订单编号
     * @return StoreOrderInfoResponse
     */
    @Override
    public StoreOrderInfoResponse info(String orderNo) {
        StoreOrder storeOrder = getInfoException(orderNo);
        if (storeOrder.getIsSystemDel()) {
            throw new CrmebException("未找到对应订单信息");
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

        //用户信息
        User user = userService.getById(storeOrder.getUid());
        storeOrderInfoResponse.setNikeName(user.getNickname());
        storeOrderInfoResponse.setPhone(user.getPhone());

        // 订单详情-得到关联的佣金记录
        UserBrokerageRecord brokerageRecord = userBrokerageRecordService.getByLinkIdAndLinkType(storeOrder.getId().toString(),    // 订单详情-得到关联的佣金记录
                BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_ORDER);
        if (ObjectUtil.isNotNull(brokerageRecord)) {
            User spread = userService.getById(brokerageRecord.getUid());
            storeOrderInfoResponse.setSpreadName(spread.getNickname());
        }

        //获取订单推荐人
        if(storeOrder.getSpreadUid()!=null && storeOrder.getSpreadUid() > 0){
            User orderSpreadUser=userService.getById(storeOrder.getSpreadUid());
            if(orderSpreadUser!=null){
                storeOrderInfoResponse.setOrderUserName(orderSpreadUser.getNickname());
            }
        }

        storeOrderInfoResponse.setProTotalPrice(storeOrder.getTotalPrice().subtract(storeOrder.getTotalPostage()));
        return storeOrderInfoResponse;
    }

    /** 发送货物
     * @param request StoreOrderSendRequest 发货参数
     * @author lingfe
     * @since 2020-06-10
     * @return boolean
     */
    @Override
    public Boolean send(StoreOrderSendRequest request) {
        //订单信息
        StoreOrder storeOrder = getInfoException(request.getOrderNo());
        if (storeOrder.getIsDel()) throw new CrmebException("订单已删除,不能发货!");
        if (storeOrder.getStatus() > 0) throw new CrmebException("订单已发货请勿重复操作!");
        request.setId(storeOrder.getId());
        switch (request.getType()){
            case "1":// 发货
                express(request, storeOrder);
                break;
            case "2":// 送货
                delivery(request, storeOrder);
                break;
            case "3":// 虚拟
                virtual(request, storeOrder);
                break;
            default:
                throw new CrmebException("类型错误");
        }
        return true;
    }

    /**
     * 订单备注
     * @param orderNo 订单编号
     * @param mark 备注
     * @return Boolean
     */
    @Override
    public Boolean mark(String orderNo, String mark) {
        StoreOrder storeOrder = getInfoException(orderNo);
        storeOrder.setRemark(mark);
        return updateById(storeOrder);
    }

    /**
     * 拒绝退款
     * @param orderNo 订单编号
     * @param reason String 原因
     * @return Boolean
     */
    @Override
    public Boolean refundRefuse(String orderNo, String reason) {
        if (StrUtil.isBlank(reason)) {
            throw new CrmebException("请填写拒绝退款原因");
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
            // 如果是拼团订单要将拼团状态改回去
            if (ObjectUtil.isNotNull(storeOrder) && storeOrder.getPinkId() > 0) {
                StorePink storePink = storePinkService.getById(storeOrder.getPinkId());
                if (storePink.getStatus().equals(3)) {
                    storePink.setStatus(1);
                    storePinkService.updateById(storePink);
                }
            }
            // 发送消息通知
            HashMap<String, String> temMap = new HashMap<>();
            temMap.put(Constants.WE_CHAT_TEMP_KEY_FIRST, "您的订单退款申请被拒绝！");
            temMap.put("keyword1", storeOrder.getOrderId());
            temMap.put("keyword2", storeOrder.getPayPrice().toString());
            temMap.put("keyword3", DateUtil.dateToStr(storeOrder.getCreateTime(), Constants.DATE_FORMAT));
            temMap.put(Constants.WE_CHAT_TEMP_KEY_END, "拒绝原因："+ reason);
            pushMessageRefundOrder(storeOrder, user, temMap);
        }
        return execute;
    }

    /**
     * 查询单条
     * @param storeOrder StoreOrder 订单参数
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
     * 获取订单快递信息
     * @param orderNo 订单编号
     * @return LogisticsResultVo
     */
    @Override
    public LogisticsResultVo getLogisticsInfo(String orderNo) {
        StoreOrder info = getInfoException(orderNo);
        if (info.getType().equals(1)) {// 视频号订单
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
     * 根据用户id集合获取对应订单，分销中使用
     * @param ids 用户id集合
     * @return 对应用户订单集合
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
     * 订单 top 查询参数
     * @param status 状态参数
     * @param userId    用户ID
     * @param merId     商户用户ID
     * @return 订单查询结果
     */
    @Override
    public Integer getTopDataUtil(Integer status, Integer userId,Integer merId) {
        //定义查询对象
        LambdaQueryWrapper<StoreOrder> lqw = new LambdaQueryWrapper<>();

        //条件-验证状态
        orderUtils.statusApiByWhere(lqw, status);

        //条件-用户ID或商户用户ID
        if(userId!=null){
            lqw.eq(StoreOrder::getUid,userId);
        }else if(merId !=null){
            lqw.eq(StoreOrder::getMerId,merId);
        }

        //返回统计数量
        return dao.selectCount(lqw);
    }

    /**
     * 更改订单价格
     *
     * @param request 订单id改价对象
     * @return 更改结果
     */
    @Override
    public boolean editPrice(StoreOrderEditPriceRequest request) {
        String oldPrice;
        StoreOrder existOrder = getByOderId(request.getOrderNo());
        // 订单不存在
        if(null == existOrder) {
            throw new CrmebException(Constants.RESULT_ORDER_NOTFOUND.replace("${orderCode}", request.getOrderNo()));
        }
        // 订单已支付
        if(existOrder.getPaid()) {
            throw new CrmebException(Constants.RESULT_ORDER_PAYED.replace("${orderCode}", request.getOrderNo()));
        }
        // 修改价格和原来价格相同
        if(existOrder.getPayPrice().compareTo(request.getPrice()) ==0) {
            throw new CrmebException(Constants.RESULT_ORDER_EDIT_PRICE_SAME.replace("${oldPrice}",existOrder.getPayPrice()+"")
                    .replace("${editPrice}",request.getPrice()+""));
        }

        oldPrice = existOrder.getPayPrice()+"";

        Boolean execute = transactionTemplate.execute(e -> {
            // 修改订单价格
            orderEditPrice(request.getOrderNo(), request.getPrice());
            // 订单修改状态操作
            storeOrderStatusService.createLog(existOrder.getId(), Constants.ORDER_LOG_EDIT,
                    Constants.RESULT_ORDER_EDIT_PRICE_LOGS.replace("${orderPrice}", oldPrice)
                            .replace("${price}", request.getPrice() + ""));
            return Boolean.TRUE;
        });
        if(!execute) {
            throw new CrmebException(Constants.RESULT_ORDER_EDIT_PRICE_SUCCESS
                    .replace("${orderNo}", existOrder.getOrderId()).replace("${price}", request.getPrice()+""));
        }
        // 发送改价短信提醒
        User user = userService.getById(existOrder.getUid());
        if (StrUtil.isNotBlank(user.getPhone())) {
            // 改价短信开关是否开启
            String smsSwitch = systemConfigService.getValueByKey(SmsConstants.SMS_CONFIG_PRICE_REVISION_SWITCH);
            if (StrUtil.isNotBlank(smsSwitch) && smsSwitch.equals("1")) {
                // 发送改价短信提醒
                smsService.sendOrderEditPriceNotice(user.getPhone(), existOrder.getOrderId(), request.getPrice());
            }
        }

        return execute;
    }

    /**
     * 改价
     * @param orderNo 订单编号
     * @param price 修改后的价格
     */
    private Boolean orderEditPrice(String orderNo, BigDecimal price) {
        LambdaUpdateWrapper<StoreOrder> luw = new LambdaUpdateWrapper<>();
        luw.set(StoreOrder::getPayPrice, price);
        luw.eq(StoreOrder::getOrderId, orderNo);
        luw.eq(StoreOrder::getPaid, false);
        return update(luw);
    }

    /**
     * 根据时间参数统计订单销售额
     *
     * @param dateLimit 时间区间
     * @param type 类型
     * @return 统计订单信息
     */
    @Override
    public StoreOrderStatisticsResponse orderStatisticsByTime(String dateLimit,Integer type) {
        StoreOrderStatisticsResponse response = new StoreOrderStatisticsResponse();
        // 根据开始时间和结束时间获取时间差 再根据时间差获取上一个时间段 查询当前和上一个时间段的数据 进行比较且返回
        dateLimitUtilVo dateRange = DateUtil.getDateLimit(dateLimit);
        String dateStartD = dateRange.getStartTime();
        String dateEndD = dateRange.getEndTime();
        int days = DateUtil.daysBetween(
                DateUtil.strToDate(dateStartD,Constants.DATE_FORMAT_DATE),
                DateUtil.strToDate(dateEndD,Constants.DATE_FORMAT_DATE)
        );
        // 同时间区间的上一个时间起点
        String perDateStart = DateUtil.addDay(
                DateUtil.strToDate(dateStartD,Constants.DATE_FORMAT_DATE), -days, Constants.DATE_FORMAT_START);
        // 当前时间区间
        String dateStart = DateUtil.addDay(
                DateUtil.strToDate(dateStartD,Constants.DATE_FORMAT_DATE),0,Constants.DATE_FORMAT_START);
        String dateEnd = DateUtil.addDay(
                DateUtil.strToDate(dateEndD,Constants.DATE_FORMAT_DATE),0,Constants.DATE_FORMAT_END);

        // 上一个时间段查询
        List<StoreOrder> orderPerList = getOrderPayedByDateLimit(perDateStart,dateStart);

        // 当前时间段
        List<StoreOrder> orderCurrentList = getOrderPayedByDateLimit(dateStart, dateEnd);
        double increasePrice = 0;
        if(type == 1){
            double perSumPrice = orderPerList.stream().mapToDouble(e -> e.getPayPrice().doubleValue()).sum();
            double currentSumPrice = orderCurrentList.stream().mapToDouble(e -> e.getPayPrice().doubleValue()).sum();

            response.setChart(dao.getOrderStatisticsPriceDetail(new StoreDateRangeSqlPram(dateStart,dateEnd)));
            response.setTime(BigDecimal.valueOf(currentSumPrice).setScale(2,BigDecimal.ROUND_HALF_UP));
            // 当前营业额和上一个同比营业额增长区间
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
     * 获取用户当天的秒杀数量
     *
     * @param uid 用户uid
     * @param seckillId 秒杀商品id
     * @return 用户当天的秒杀商品订单数量
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
     * 获取用户当前的砍价订单
     * @param uid    用户uid
     * @return  用户当前的砍价订单
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
     * 获取用户当前的拼团订单
     * @param uid    用户uid
     * @return  用户当前的拼团订单
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
     * 获取面单默认配置信息
     * @return ExpressSheetVo
     */
    @Override
    public ExpressSheetVo getDeliveryInfo() {
        return systemConfigService.getDeliveryInfo();
    }

    /**
     * 根据用户uid集合获取订单号集合
     * @param userIds 用户uid集合
     * @return 订单号集合
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
     * 更新支付结果
     * @param orderNo 订单编号
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
     * 获取推广订单总金额
     * @param orderNoList 订单编号列表
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
     * 获取所有收货订单id集合
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
     * @param userId 用户uid
     * @param pageParamRequest 分页参数
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
     * 订单改价
     * @param request 改价请求对象
     * @return 改价结果
     */
    @Override
    public Boolean updatePrice(StoreOrderUpdatePriceRequest request) {
        String oldPrice;
        StoreOrder existOrder = getInfoException(request.getOrderNo());
        // 订单已支付
        if(existOrder.getPaid()) {
            throw new CrmebException(StrUtil.format("订单号为 {} 的订单已支付", existOrder.getOrderId()));
        }
        // 修改价格和原来价格相同
        if(existOrder.getPayPrice().compareTo(request.getPayPrice()) ==0) {
            throw new CrmebException(StrUtil.format("修改价格不能和支付价格相同 原价 {} 修改价 {}", existOrder.getPayPrice(), request.getPayPrice()));
        }

        oldPrice = existOrder.getPayPrice()+"";

        Boolean execute = transactionTemplate.execute(e -> {
            // 修改订单价格
            orderEditPrice(existOrder.getOrderId(), request.getPayPrice());
            // 订单修改状态操作
            storeOrderStatusService.createLog(existOrder.getId(), Constants.ORDER_LOG_EDIT,
                    Constants.RESULT_ORDER_EDIT_PRICE_LOGS.replace("${orderPrice}", oldPrice)
                            .replace("${price}", request.getPayPrice() + ""));
            return Boolean.TRUE;
        });
        if(!execute) {
            throw new CrmebException(Constants.RESULT_ORDER_EDIT_PRICE_SUCCESS
                    .replace("${orderNo}", existOrder.getOrderId()).replace("${price}", request.getPayPrice()+""));
        }
        // 发送改价短信提醒
        User user = userService.getById(existOrder.getUid());
        if (StrUtil.isNotBlank(user.getPhone())) {
            // 改价短信开关是否开启
            String smsSwitch = systemConfigService.getValueByKey(SmsConstants.SMS_CONFIG_PRICE_REVISION_SWITCH);
            if (StrUtil.isNotBlank(smsSwitch) && smsSwitch.equals("1")) {
                // 发送改价短信提醒
                smsService.sendOrderEditPriceNotice(user.getPhone(), existOrder.getOrderId(), request.getPayPrice());
            }
        }

        return execute;
    }

    @Override
    public Integer getOrderCountByUid(Integer uid,Integer merId) {
        //定义查询对象
        LambdaQueryWrapper<StoreOrder> lqw = Wrappers.lambdaQuery();
        //条件-支付状态
        lqw.eq(StoreOrder::getPaid, true);
        //条件-是否删除
        lqw.eq(StoreOrder::getIsDel, false);

        //条件-用户ID或商户用户ID
        if(uid!=null)lqw.eq(StoreOrder::getUid, uid);
        else if(merId != null)lqw.eq(StoreOrder::getMerId, merId);

        //条件-退款状态：小于2
        lqw.lt(StoreOrder::getRefundStatus, 2);

        //返回统计
        return dao.selectCount(lqw);
    }

    /**
     * 获取用户总消费金额
     * @param userId 用户uid
     * @return BigDecimal
     */
    @Override
    public BigDecimal getSumPayPriceByUid(Integer userId,Integer merId) {
        //定义查询对象
        LambdaQueryWrapper<StoreOrder> lqw = Wrappers.lambdaQuery();
        //查询-字段
        lqw.select(StoreOrder::getPayPrice);
        //条件-支付状态
        lqw.eq(StoreOrder::getPaid, true);
        //条件-是否删除
        lqw.eq(StoreOrder::getIsDel, false);

        //条件-用户ID或商户用户ID
        if(userId!=null)lqw.eq(StoreOrder::getUid, userId);
        else if(merId != null)lqw.eq(StoreOrder::getMerId, merId);

        //条件-退款状态：小于2
        lqw.lt(StoreOrder::getRefundStatus, 2);
        List<StoreOrder> orderList = dao.selectList(lqw);

        //累加后返回
        return orderList.stream().map(StoreOrder::getPayPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 获取订单数量(时间)
     * @param uid 用户uid
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
        //定义查询对象
        LambdaQueryWrapper<StoreOrder> lqw = Wrappers.lambdaQuery();

        //查询-字段
        lqw.select(StoreOrder::getPayPrice);

        //条件-支付状态：已支付
        lqw.eq(StoreOrder::getPaid, true);
        //条件-是否删除：否
        lqw.eq(StoreOrder::getIsDel, false);
        //条件-退款状态：小于2-未退款
        lqw.lt(StoreOrder::getRefundStatus, 2);

        //条件-ID类型
        this.getTypeId(type, value, lqw);

        //条件-时间范围
        if (StrUtil.isNotBlank(date)) {
            dateLimitUtilVo dateLimit = DateUtil.getDateLimit(date);
            lqw.between(StoreOrder::getCreateTime, dateLimit.getStartTime(), dateLimit.getEndTime());
        }

        //得到数据
        List<StoreOrder> orderList = dao.selectList(lqw);

        //统计-并返回
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
     * 获取砍价订单
     * @param bargainId 砍价商品id
     * @param bargainUserId 用户砍价活动id
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
     * 获取订单状态数量
     * @return StoreOrderCountItemResponse
     */
    @Override
    public StoreOrderCountItemResponse getOrderStatusNum(String dateLimit) {
        StoreOrderCountItemResponse response = new StoreOrderCountItemResponse();
        // 全部订单
        response.setAll(getCount(dateLimit, Constants.ORDER_STATUS_ALL));
        // 未支付订单
        response.setUnPaid(getCount(dateLimit, Constants.ORDER_STATUS_UNPAID));
        // 未发货订单
        response.setNotShipped(getCount(dateLimit, Constants.ORDER_STATUS_NOT_SHIPPED));
        // 待收货订单
        response.setSpike(getCount(dateLimit, Constants.ORDER_STATUS_SPIKE));
        // 待评价订单
        response.setBargain(getCount(dateLimit, Constants.ORDER_STATUS_BARGAIN));
        // 交易完成订单
        response.setComplete(getCount(dateLimit, Constants.ORDER_STATUS_COMPLETE));
        // 待核销订单
        response.setToBeWrittenOff(getCount(dateLimit, Constants.ORDER_STATUS_TOBE_WRITTEN_OFF));
        // 退款中订单
        response.setRefunding(getCount(dateLimit, Constants.ORDER_STATUS_REFUNDING));
        // 已退款订单
        response.setRefunded(getCount(dateLimit, Constants.ORDER_STATUS_REFUNDED));
        // 已删除订单
        response.setDeleted(getCount(dateLimit, Constants.ORDER_STATUS_DELETED));
        return response;
    }

    /**
     * 获取订单统计数据
     * @param dateLimit 时间端
     * @return StoreOrderTopItemResponse
     */
    @Override
    public StoreOrderTopItemResponse getOrderData(String dateLimit) {
        StoreOrderTopItemResponse itemResponse = new StoreOrderTopItemResponse();
        // 订单数量
        itemResponse.setCount(getCount(dateLimit, Constants.ORDER_STATUS_ALL));
        // 订单金额
        itemResponse.setAmount(getAmount(dateLimit, ""));
        // 微信支付金额
        itemResponse.setWeChatAmount(getAmount(dateLimit, PayConstants.PAY_TYPE_WE_CHAT));
        // 余额支付金额
        itemResponse.setYueAmount(getAmount(dateLimit, PayConstants.PAY_TYPE_YUE));
        // 酒米兑换订单金额
        itemResponse.setIntegralAmount(this.getAmount(dateLimit, PayConstants.PAY_TYPE_INTEGRAL));
        // 支付宝支付订单金额
        itemResponse.setZfbAmount(this.getAmount(dateLimit, PayConstants.PAY_TYPE_ALI_PAY));
        return itemResponse;
    }

    /**
     * 订单删除
     * @param orderNo 订单编号
     * @return Boolean
     */
    @Override
    public Boolean delete(String orderNo) {
        StoreOrder storeOrder = getInfoException(orderNo);
        if (!storeOrder.getIsDel()) {
            throw new CrmebException("您选择的的订单存在用户未删除的订单，无法删除用户未删除的订单！");
        }
        if (storeOrder.getIsSystemDel()) {
            throw new CrmebException("此订单已经被删除了!");
        }
        storeOrder.setIsSystemDel(true);
        return updateById(storeOrder);
    }


///////////////////////////////////////////////////////////////////////////////////////////////////// 以下为自定义方法

    /**
     * 根据时间参数获取有效订单
     * @return 有效订单列表
     */
    private List<StoreOrder> getOrderPayedByDateLimit(String startTime, String endTime){
        LambdaQueryWrapper<StoreOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(StoreOrder::getIsDel, false).eq(StoreOrder::getPaid, true).eq(StoreOrder::getRefundStatus,0)
                .between(StoreOrder::getCreateTime, startTime, endTime);
     return dao.selectList(lqw);
    }


    /** 发货
     * @param id Integer id
     * @author Mr.Zhang
     * @since 2020-06-10
     * @return Boolean
     */
    public StoreOrder getInfoException(Integer id) {
        StoreOrder info = getById(id);
        if(null == info){
            throw new CrmebException("没有找到订单信息");
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
            throw new CrmebException("没有找到订单信息");
        }
        return storeOrder;
    }

    /** 快递
     * @param request StoreOrderSendRequest 发货参数
     * @param storeOrder StoreOrder 订单信息
     * @author Mr.Zhang
     * @since 2020-06-10
     */
    private void express(StoreOrderSendRequest request, StoreOrder storeOrder) {
        // 校验快递发货参数
        validateExpressSend(request);
        //快递公司信息
        Express express = expressService.getByCode(request.getExpressCode());
        if (request.getExpressRecordType().equals("1")) { // 正常发货
            deliverGoods(request, storeOrder, express);
        }
        if (request.getExpressRecordType().equals("2")) { // 电子面单
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
            //订单记录增加
            storeOrderStatusService.createLog(request.getId(), Constants.ORDER_LOG_EXPRESS, message);
            return Boolean.TRUE;
        });

        if (!execute) throw new CrmebException("快递发货失败！");
        User user = userService.getById(storeOrder.getUid());
        if (StrUtil.isNotBlank(user.getPhone())) {
            // 发货短信提醒
            String smsSwitch = systemConfigService.getValueByKey(SmsConstants.SMS_CONFIG_DELIVER_GOODS_SWITCH);
            if (StrUtil.isNotBlank(smsSwitch) && smsSwitch.equals("1")) {
                String proName = "";
                List<StoreOrderInfoOldVo> voList = storeOrderInfoService.getOrderListByOrderId(storeOrder.getId());
                proName = voList.get(0).getInfo().getProductName();
                if (voList.size() > 1) {
                    proName = proName.concat("等");
                }
                smsService.sendOrderDeliverNotice(user.getPhone(), user.getNickname(), proName, storeOrder.getOrderId());
            }
        }

        // 发送消息通知
        pushMessageOrder(storeOrder, user);
    }

    /**
     * 发送发货消息通知
     * 根据用户类型发送
     * 公众号模板消息
     * 小程序订阅消息
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

        // 公众号
        if (storeOrder.getIsChannel().equals(PayConstants.ORDER_PAY_CHANNEL_PUBLIC)) {
            userToken = userTokenService.getTokenByUserId(user.getUid(), UserConstants.USER_TOKEN_TYPE_WECHAT);
            if (ObjectUtil.isNull(userToken)) {
                return ;
            }
            // 发送微信模板消息
            temMap.put(Constants.WE_CHAT_TEMP_KEY_FIRST, "订单发货提醒");
            temMap.put("keyword1", storeOrder.getOrderId());
            temMap.put("keyword2", storeOrder.getDeliveryName());
            temMap.put("keyword3", storeOrder.getDeliveryId());
            temMap.put(Constants.WE_CHAT_TEMP_KEY_END, "欢迎再次购买！");
            templateMessageService.pushTemplateMessage(Constants.WE_CHAT_TEMP_KEY_EXPRESS, temMap, userToken.getToken());
            return;
        }

        // 小程序发送订阅消息
        userToken = userTokenService.getTokenByUserId(user.getUid(), UserConstants.USER_TOKEN_TYPE_ROUTINE);
        if (ObjectUtil.isNull(userToken)) {
            return ;
        }
        // 组装数据
        temMap.put("character_string1", storeOrder.getOrderId());
        temMap.put("name3", storeOrder.getDeliveryName());
        temMap.put("character_string4", storeOrder.getDeliveryId());
        temMap.put("thing7", "您的订单已发货");
        templateMessageService.pushMiniTemplateMessage(Constants.WE_CHAT_PROGRAM_TEMP_KEY_EXPRESS, temMap, userToken.getToken());
    }

    /**
     * 电子面单
     * @param request
     * @param storeOrder
     * @param express
     */
    private void expressDump(StoreOrderSendRequest request, StoreOrder storeOrder, Express express) {
        String configExportOpen = systemConfigService.getValueByKeyException("config_export_open");
        if (!configExportOpen.equals("1")) {// 电子面单未开启
            throw new CrmebException("请先开启电子面单");
        }
        MyRecord record = new MyRecord();
        record.set("com", express.getCode());// 快递公司编码
        record.set("to_name", storeOrder.getRealName());// 收件人
        record.set("to_tel", storeOrder.getUserPhone());// 收件人电话
        record.set("to_addr", storeOrder.getUserAddress());// 收件人详细地址
        record.set("from_name", request.getToName());// 寄件人
        record.set("from_tel", request.getToTel());// 寄件人电话
        record.set("from_addr", request.getToAddr());// 寄件人详细地址
        record.set("temp_id", request.getExpressTempId());// 电子面单模板ID
        String siid = systemConfigService.getValueByKeyException("config_export_siid");
        record.set("siid", siid);// 云打印机编号
        record.set("count", storeOrder.getTotalNum());// 商品数量

        //获取购买商品名称
        List<Integer> orderIdList = new ArrayList<>();
        orderIdList.add(storeOrder.getId());
        HashMap<Integer, List<StoreOrderInfoOldVo>> orderInfoMap = storeOrderInfoService.getMapInId(orderIdList);
        if(orderInfoMap.isEmpty() || !orderInfoMap.containsKey(storeOrder.getId())){
            throw new CrmebException("没有找到购买的商品信息");
        }
        List<String> productNameList = new ArrayList<>();
        for (StoreOrderInfoOldVo storeOrderInfoVo : orderInfoMap.get(storeOrder.getId())) {
            productNameList.add(storeOrderInfoVo.getInfo().getProductName());
        }

        record.set("cargo", String.join(",", productNameList));// 物品名称
        if (express.getPartnerId()) {
            record.set("partner_id", express.getAccount());// 电子面单月结账号(部分快递公司必选)
        }
        if (express.getPartnerKey()) {
            record.set("partner_key", express.getPassword());// 电子面单密码(部分快递公司必选)
        }
        if (express.getNet()) {
            record.set("net", express.getNetName());// 收件网点名称(部分快递公司必选)
        }

        MyRecord myRecord = onePassService.expressDump(record);
        storeOrder.setDeliveryId(myRecord.getStr("kuaidinum"));
    }

    /**
     * 正常发货
     */
    private void deliverGoods(StoreOrderSendRequest request, StoreOrder storeOrder, Express express) {
        storeOrder.setDeliveryId(request.getExpressNumber());
    }

    /**
     * 校验快递发货参数
     */
    private void validateExpressSend(StoreOrderSendRequest request) {
        if (request.getExpressRecordType().equals("1")) {
            if (StrUtil.isBlank(request.getExpressNumber())) throw new CrmebException("请填写快递单号");
            return;
        }
        if (StrUtil.isBlank(request.getExpressCode())) throw new CrmebException("请选择快递公司");
        if (StrUtil.isBlank(request.getExpressRecordType())) throw new CrmebException("请选择发货记录类型");
        if (StrUtil.isBlank(request.getExpressTempId())) throw new CrmebException("请选择电子面单");
        if (StrUtil.isBlank(request.getToName())) throw new CrmebException("请填写寄件人姓名");
        if (StrUtil.isBlank(request.getToTel())) throw new CrmebException("请填写寄件人电话");
        if (StrUtil.isBlank(request.getToAddr())) throw new CrmebException("请填写寄件人地址");
    }

    /** 送货上门
     * @param request StoreOrderSendRequest 发货参数
     * @param storeOrder StoreOrder 订单信息
     * @author Mr.Zhang
     * @since 2020-06-10
     */
    private void delivery(StoreOrderSendRequest request, StoreOrder storeOrder) {
        if (StrUtil.isBlank(request.getDeliveryName())) throw new CrmebException("请输入送货人姓名");
        if (StrUtil.isBlank(request.getDeliveryTel())) throw new CrmebException("请输入送货人电话号码");
        ValidateFormUtil.isPhone(request.getDeliveryTel(), "送货人联系方式");

        //送货信息
        storeOrder.setDeliveryName(request.getDeliveryName());
        storeOrder.setDeliveryId(request.getDeliveryTel());
        storeOrder.setStatus(1);
        storeOrder.setDeliveryType("send");

        //获取购买商品名称
        List<Integer> orderIdList = new ArrayList<>();
        orderIdList.add(storeOrder.getId());
        HashMap<Integer, List<StoreOrderInfoOldVo>> orderInfoMap = storeOrderInfoService.getMapInId(orderIdList);
        if(orderInfoMap.isEmpty() || !orderInfoMap.containsKey(storeOrder.getId())){
            throw new CrmebException("没有找到购买的商品信息");
        }
        List<String> productNameList = new ArrayList<>();
        for (StoreOrderInfoOldVo storeOrderInfoVo : orderInfoMap.get(storeOrder.getId())) {
            productNameList.add(storeOrderInfoVo.getInfo().getProductName());
        }

        String message = Constants.ORDER_LOG_MESSAGE_DELIVERY.replace("{deliveryName}", request.getDeliveryName()).replace("{deliveryCode}", request.getDeliveryTel());

        Boolean execute = transactionTemplate.execute(i -> {
            // 更新订单
            updateById(storeOrder);
            // 订单记录增加
            storeOrderStatusService.createLog(request.getId(), Constants.ORDER_LOG_DELIVERY, message);
            return Boolean.TRUE;
        });
        if (!execute) throw new CrmebException("订单更新送货失败");

        User user = userService.getById(storeOrder.getUid());
        // 发送消息通知
        pushMessageDeliveryOrder(storeOrder, user, request, productNameList);
    }

    /**
     * 发送消息通知
     * 根据用户类型发送
     * 公众号模板消息
     * 小程序订阅消息
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
        // 公众号
        if (storeOrder.getIsChannel().equals(PayConstants.ORDER_PAY_CHANNEL_PUBLIC)) {
            userToken = userTokenService.getTokenByUserId(user.getUid(), UserConstants.USER_TOKEN_TYPE_WECHAT);
            if (ObjectUtil.isNull(userToken)) {
                return ;
            }
            map.put(Constants.WE_CHAT_TEMP_KEY_FIRST, "订单配送提醒");
            map.put("keyword1", storeOrder.getOrderId());
            map.put("keyword2", DateUtil.dateToStr(storeOrder.getCreateTime(), Constants.DATE_FORMAT));
            map.put("keyword3", storeOrder.getUserAddress());
            map.put("keyword4", request.getDeliveryName());
            map.put("keyword5", request.getDeliveryTel());
            map.put(Constants.WE_CHAT_TEMP_KEY_END, "欢迎再次购买！");
            // 发送微信模板消息
            templateMessageService.pushTemplateMessage(Constants.WE_CHAT_TEMP_KEY_DELIVERY, map, userToken.getToken());
            return;
        }
        // 小程序发送订阅消息
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

    /** 虚拟
     * @param request StoreOrderSendRequest 发货参数
     * @param storeOrder StoreOrder 订单信息
     * @author Mr.Zhang
     * @since 2020-06-10
     */
    private void virtual(StoreOrderSendRequest request, StoreOrder storeOrder) {
        //快递信息
        storeOrder.setDeliveryType("fictitious");
        storeOrder.setStatus(1);

        Boolean execute = transactionTemplate.execute(i -> {
            updateById(storeOrder);
            //订单记录增加
            storeOrderStatusService.createLog(request.getId(), Constants.ORDER_LOG_DELIVERY_VI, "虚拟物品发货");
            return Boolean.TRUE;
        });
        if (!execute) throw new CrmebException("虚拟物品发货失败");
    }

    /**
     * 获取订单总数
     * @param dateLimit 时间端
     * @param status String 状态
     * @return Integer
     */
    private Integer getCount(String dateLimit, String status) {
        //总数只计算时间
        QueryWrapper<StoreOrder> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotBlank(dateLimit)) {
            dateLimitUtilVo dateLimitUtilVo = DateUtil.getDateLimit(dateLimit);
            queryWrapper.between("create_time", dateLimitUtilVo.getStartTime(), dateLimitUtilVo.getEndTime());
        }
        getStatusWhereNew(queryWrapper, status);
        return dao.selectCount(queryWrapper);
    }

    /**
     * 获取订单金额
     * @param dateLimit 时间端
     * @param type  支付类型
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
     * 获取request的where条件
     * @param queryWrapper QueryWrapper<StoreOrder> 表达式
     * @param request StoreOrderSearchRequest 请求参数
     */
    private void getRequestTimeWhere(QueryWrapper<StoreOrder> queryWrapper, StoreOrderSearchRequest request) {
        if (StringUtils.isNotBlank(request.getDateLimit())) {
            dateLimitUtilVo dateLimitUtilVo = DateUtil.getDateLimit(request.getDateLimit());
            queryWrapper.between("create_time", dateLimitUtilVo.getStartTime(), dateLimitUtilVo.getEndTime());
        }
    }

    /**
     * 根据订单状态获取where条件
     * @param queryWrapper QueryWrapper<StoreOrder> 表达式
     * @param status String 类型
     */
    private void getStatusWhereNew(QueryWrapper<StoreOrder> queryWrapper, String status) {
        if(StrUtil.isBlank(status)) {
            return;
        }
        switch (status){
            case Constants.ORDER_STATUS_ALL: //全部
                break;
            case Constants.ORDER_STATUS_UNPAID: //未支付
                queryWrapper.eq("paid", 0);//支付状态
                queryWrapper.eq("status", 0); //订单状态
                queryWrapper.eq("is_del", 0);//删除状态
                break;
            case Constants.ORDER_STATUS_NOT_SHIPPED: //未发货
                queryWrapper.eq("paid", 1);
                queryWrapper.eq("status", 0);
                queryWrapper.eq("refund_status", 0);
                queryWrapper.eq("shipping_type", 1);//配送方式
                queryWrapper.eq("is_del", 0);
                break;
            case Constants.ORDER_STATUS_SPIKE: //待收货
                queryWrapper.eq("paid", 1);
                queryWrapper.eq("status", 1);
                queryWrapper.eq("refund_status", 0);
                queryWrapper.eq("is_del", 0);
                break;
            case Constants.ORDER_STATUS_BARGAIN: //待评价
                queryWrapper.eq("paid", 1);
                queryWrapper.eq("status", 2);
                queryWrapper.eq("refund_status", 0);
                queryWrapper.eq("is_del", 0);
                break;
            case Constants.ORDER_STATUS_COMPLETE: //交易完成
                queryWrapper.eq("paid", 1);
                queryWrapper.eq("status", 3);
                queryWrapper.eq("refund_status", 0);
                queryWrapper.eq("is_del", 0);
                break;
            case Constants.ORDER_STATUS_TOBE_WRITTEN_OFF: //待核销
                queryWrapper.eq("paid", 1);
                queryWrapper.eq("status", 0);
                queryWrapper.eq("refund_status", 0);
                queryWrapper.eq("shipping_type", 2);//配送方式
                queryWrapper.eq("is_del", 0);
                break;
            case Constants.ORDER_STATUS_REFUNDING: //退款中
                queryWrapper.eq("paid", 1);
                queryWrapper.in("refund_status", 1,3);
                queryWrapper.eq("is_del", 0);
                break;
            case Constants.ORDER_STATUS_REFUNDED: //已退款
                queryWrapper.eq("paid", 1);
                queryWrapper.eq("refund_status", 2);
                queryWrapper.eq("is_del", 0);
                break;
            case Constants.ORDER_STATUS_DELETED: //已删除
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
     * 根据订单状态获取where条件
     * @param queryWrapper QueryWrapper<StoreOrder> 表达式
     * @param status String 类型
     */
    private void getStatusWhere(QueryWrapper<StoreOrder> queryWrapper, String status) {
        if (StrUtil.isBlank(status)) {
            return;
        }
        switch (status){
            case Constants.ORDER_STATUS_UNPAID: //未支付
                queryWrapper.eq("paid", 0);//支付状态
                queryWrapper.eq("status", 0); //订单状态
                queryWrapper.eq("is_del", 0);//删除状态
                break;
            case Constants.ORDER_STATUS_NOT_SHIPPED: //未发货
                queryWrapper.eq("paid", 1);
                queryWrapper.eq("status", 0);
                queryWrapper.eq("refund_status", 0);
                queryWrapper.eq("shipping_type", 1);//配送方式
                queryWrapper.eq("is_del", 0);
                break;
            case Constants.ORDER_STATUS_SPIKE: //待收货
                queryWrapper.eq("paid", 1);
                queryWrapper.eq("status", 1);
                queryWrapper.eq("refund_status", 0);
                queryWrapper.eq("is_del", 0);
                break;
            case Constants.ORDER_STATUS_BARGAIN: //待评价
                queryWrapper.eq("paid", 1);
                queryWrapper.eq("status", 2);
                queryWrapper.eq("refund_status", 0);
                queryWrapper.eq("is_del", 0);
                break;
            case Constants.ORDER_STATUS_COMPLETE: //交易完成
                queryWrapper.eq("paid", 1);
                queryWrapper.eq("status", 3);
                queryWrapper.eq("refund_status", 0);
                queryWrapper.eq("is_del", 0);
                break;
            case Constants.ORDER_STATUS_TOBE_WRITTEN_OFF: //待核销
                queryWrapper.eq("paid", 1);
                queryWrapper.eq("status", 0);
                queryWrapper.eq("refund_status", 0);
                queryWrapper.eq("shipping_type", 2);//配送方式
                queryWrapper.eq("is_del", 0);
                break;
            case Constants.ORDER_STATUS_REFUNDING: //退款中
                queryWrapper.eq("paid", 1);
                queryWrapper.in("refund_status", 1,3);
                queryWrapper.eq("is_del", 0);
                break;
            case Constants.ORDER_STATUS_REFUNDED: //已退款
                queryWrapper.eq("paid", 1);
                queryWrapper.eq("refund_status", 2);
                queryWrapper.eq("is_del", 0);
                break;
            case Constants.ORDER_STATUS_DELETED: //已删除
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
        //定义map对象，存储状态值
        Map<String, String> map = new HashMap<>();
        //防止非空，设置默认值
        map.put("key", "");
        map.put("value", "");
        if(null == storeOrder){
            return map;
        }

        // 未支付
        if(!storeOrder.getPaid()
                && storeOrder.getStatus() == 0
                && storeOrder.getRefundStatus() == 0
                && !storeOrder.getIsDel()
                && !storeOrder.getIsSystemDel()){
            map.put("key", Constants.ORDER_STATUS_UNPAID);
            map.put("value", Constants.ORDER_STATUS_STR_UNPAID);
            return map;
        }

        // 未发货
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

        // 待收货
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

        // 待评价
        if(storeOrder.getPaid()
                && storeOrder.getStatus() == 2
                && storeOrder.getRefundStatus() == 0
                && !storeOrder.getIsDel()
                && !storeOrder.getIsSystemDel()){
            map.put("key", Constants.ORDER_STATUS_BARGAIN);
            map.put("value", Constants.ORDER_STATUS_STR_BARGAIN);
            return map;
        }

        // 交易完成
        if(storeOrder.getPaid()
                && storeOrder.getStatus() == 3
                && storeOrder.getRefundStatus() == 0
                && !storeOrder.getIsDel()
                && !storeOrder.getIsSystemDel()){
            map.put("key", Constants.ORDER_STATUS_COMPLETE);
            map.put("value", Constants.ORDER_STATUS_STR_COMPLETE);
            return map;
        }

        // 待核销
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

        //申请退款
        if(storeOrder.getPaid()
                && storeOrder.getRefundStatus() == 1
                && !storeOrder.getIsDel()
                && !storeOrder.getIsSystemDel()){
            map.put("key", Constants.ORDER_STATUS_APPLY_REFUNDING);
            map.put("value", Constants.ORDER_STATUS_STR_APPLY_REFUNDING);
            return map;
        }

        //退款中
        if(storeOrder.getPaid()
                && storeOrder.getRefundStatus() == 3
                && !storeOrder.getIsDel()
                && !storeOrder.getIsSystemDel()){
            map.put("key", Constants.ORDER_STATUS_REFUNDING);
            map.put("value", Constants.ORDER_STATUS_STR_REFUNDING);
            return map;
        }

        //已退款
        if(storeOrder.getPaid()
                && storeOrder.getRefundStatus() == 2
                && !storeOrder.getIsDel()
                && !storeOrder.getIsSystemDel()){
            map.put("key", Constants.ORDER_STATUS_REFUNDED);
            map.put("value", Constants.ORDER_STATUS_STR_REFUNDED);
        }

        //已删除
        if(storeOrder.getIsDel() || storeOrder.getIsSystemDel()){
            map.put("key", Constants.ORDER_STATUS_DELETED);
            map.put("value", Constants.ORDER_STATUS_STR_DELETED);
        }

        //返回
        return map;
    }

}

