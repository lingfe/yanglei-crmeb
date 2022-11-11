package com.zbkj.crmeb.retailer.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CommonPage;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.constants.SmsConstants;
import com.exception.CrmebException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.utils.RedisUtil;
import com.zbkj.crmeb.front.response.UserMerIdOrderDetailsResponse;
import com.zbkj.crmeb.front.response.UserSpreadBannerResponse;
import com.zbkj.crmeb.regionalAgency.model.RegionalAgency;
import com.zbkj.crmeb.regionalAgency.service.RegionalAgencyService;
import com.zbkj.crmeb.retailer.dao.RetailerDao;
import com.zbkj.crmeb.retailer.model.Retailer;
import com.zbkj.crmeb.retailer.model.RetailerBill;
import com.zbkj.crmeb.retailer.request.RetailerRequest;
import com.zbkj.crmeb.retailer.request.RetailerSearchRequest;
import com.zbkj.crmeb.retailer.response.RetailerBillResponse;
import com.zbkj.crmeb.retailer.response.RetailerPraResponse;
import com.zbkj.crmeb.retailer.response.RetailerResponse;
import com.zbkj.crmeb.retailer.service.RetailerBillService;
import com.zbkj.crmeb.retailer.service.RetailerPraService;
import com.zbkj.crmeb.retailer.service.RetailerService;
import com.zbkj.crmeb.store.model.StoreProductRA;
import com.zbkj.crmeb.store.response.ProductOrderDataResponse;
import com.zbkj.crmeb.store.service.StoreOrderService;
import com.zbkj.crmeb.store.service.StoreProductRAService;
import com.zbkj.crmeb.system.service.SystemAttachmentService;
import com.zbkj.crmeb.system.service.SystemGroupDataService;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.response.UserMerIdDataResponse;
import com.zbkj.crmeb.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 零售商表-service层接口实现类
 * @author: 零风
 * @CreateDate: 2021/11/22 14:47
 */
@Service
public class RetailerServiceImpl extends ServiceImpl<RetailerDao, Retailer> implements RetailerService {

    @Resource
    private RetailerDao dao;

    @Autowired
    private UserService userService;

    @Autowired
    private RegionalAgencyService regionalAgencyService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RetailerPraService retailerPraService;

    @Autowired
    private SystemGroupDataService systemGroupDataService;

    @Autowired
    private SystemAttachmentService systemAttachmentService;

    @Autowired
    private StoreProductRAService storeProductRAService;

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private RetailerBillService retailerBillService;

    @Override
    public String getRetailerName(Integer id) {
        Retailer retailer=dao.selectById(id);
        if(retailer == null )return "零售商不存在或零售商ID错误！";
        return retailer.getReName();
    }

    @Override
    public  PageInfo<RetailerBillResponse> seeRetailerBillList(Integer retailerId) {
        return  retailerBillService.getRetailerBillList(retailerId,null,new PageParamRequest());
    }

    @Override
    public Retailer getInfoException(Integer retailerId) {
        //根据零售商ID标识-得到零售商数据
        Retailer retailer=dao.selectById(retailerId);
        if(retailer == null)throw new CrmebException("零售商信息不存在!");
        return retailer;
    }

    @Override
    public void exeRetailerBillTask() {
        //得到-ALL区域代理
        List<RegionalAgency> regionalAgencyList = regionalAgencyService.list();
        if(regionalAgencyList == null || regionalAgencyList.size() == 0)return;

        //得到-零售商list
        List<Integer> raIds = regionalAgencyList.stream().map(RegionalAgency::getId).collect(Collectors.toList());
        List<RetailerResponse> retailerResponseList=this.getWhereRaIdsList(raIds);
        if(retailerResponseList == null ||retailerResponseList.size() == 0)return;

        //循环处理-生成账单
        for (RetailerResponse resp:retailerResponseList) {
            this.generateRetailerProductBill(resp.getId());
        }
    }

    @Override
    public List<RetailerResponse> getWhereRaIdsList(List<Integer> raIds) {
        //定义查询对象
        LambdaQueryWrapper<Retailer> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(Retailer::getRaId,raIds);

        //得到数据
        List<Retailer> retailerList = dao.selectList(lambdaQueryWrapper);
        List<RetailerResponse> retailerResponseList=new ArrayList<>();
        for (Retailer r:retailerList) {
            //实例化-零售商响应对象
            RetailerResponse response=new RetailerResponse();
            BeanUtils.copyProperties(r, response);

            //添加到-响应list
            retailerResponseList.add(response);
        }

        //返回数据
        return retailerResponseList;
    }

    @Override
    public void generateRetailerProductBill(Integer retailerId) {
        //得到-零售商-产品订单相关统计数据
        List<ProductOrderDataResponse> retailerPraList = this.getPraOrderData(retailerId);
        List<RetailerBill> retailerBillList=new ArrayList<>();
        for (ProductOrderDataResponse response:retailerPraList) {
            //实例化-零售商账单对象
            RetailerBill retailerBill=new RetailerBill();
            retailerBill.setRetailerId(retailerId);
            retailerBill.setProductId(response.getProductId());
            retailerBill.setBillPrice(response.getYesterdayGmv());

            //验证金额
            if(response.getYesterdayGmv().compareTo(BigDecimal.ZERO) < 1){
                retailerBill.setStatus(0);
            }else{
                retailerBill.setStatus(1);
            }

            //添加到集合
            retailerBillList.add(retailerBill);
        }

        //执行保存
        retailerBillService.saveBatch(retailerBillList);
    }

    @Override
    public List<ProductOrderDataResponse> getPraOrderData(Integer retailerId) {
        //得到-产品代理list
        List<StoreProductRA> storeProductRAList = storeProductRAService.getWhereRetailerId(retailerId);
        if(storeProductRAList.size() == 0) return new ArrayList<>();

        //实例化对象
        List<ProductOrderDataResponse> responseList=new ArrayList<>();
        for (StoreProductRA ra:storeProductRAList) {
            //添加到-产品订单数据响应list集合
            responseList.add(storeOrderService.getWhereProductIdAndMerId(ra.getProductId(),retailerId));
        }

        //返回-产品订单数据响应集合
        return responseList;
    }

    @Override
    public UserMerIdOrderDetailsResponse getOrderInfoData(Integer retailerId,Integer dateType) {
        //验证-零售商信息
        Retailer retailer=dao.selectById(retailerId);
        if(retailer == null) return new UserMerIdOrderDetailsResponse();

        //得到-零售商-订单详细统计数据
        UserMerIdOrderDetailsResponse userMerIdOrderDetailsResponse = userService.getMerIdOrderInfoStatisticsData(retailer.getId(),dateType);
        if(userMerIdOrderDetailsResponse == null) new UserMerIdOrderDetailsResponse();

        //返回
        return userMerIdOrderDetailsResponse;
    }

    @Override
    public List<Retailer> getWhereUserIDList() {
        //得到-当前用户信息
        User user=userService.getInfoException();

        //定义查询-查询零售商信息
        LambdaQueryWrapper<Retailer> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Retailer::getUid,user.getUid());
        List<Retailer> retailerList = dao.selectList(lambdaQueryWrapper);
        if(retailerList == null || retailerList.size() == 0)throw new CrmebException("未绑定零售商信息！");

        //返回列表
        return retailerList;
    }

    @Override
    public UserMerIdDataResponse getData(Integer retailerId) {
        //定义变量
        Retailer retailer =null;

        //验证零售商ID标识非空-得到-零售商信息
        if(retailerId == null || retailerId.equals(0)){
            //得到-当前登录用户（查当前用户绑定的零售商信息）
            User user=userService.getInfoException();
            LambdaQueryWrapper<Retailer> retailerLambdaQueryWrapper=new LambdaQueryWrapper<>();
            retailerLambdaQueryWrapper.eq(Retailer::getUid,user.getUid());
            retailerLambdaQueryWrapper.last("LIMIT 1");//只取一条
            retailer = dao.selectOne(retailerLambdaQueryWrapper);
        }else{
            //根据ID标识-得到零售商信息
            retailer = dao.selectById(retailerId);
        }

        //验证-零售商信息
        if(retailer == null) return new UserMerIdDataResponse();

        //得到-零售商相关统计数据
        UserMerIdDataResponse userMerIdDataResponse = userService.getUserMerIdDataResponse(retailer.getId());
        if(userMerIdDataResponse == null) userMerIdDataResponse =new UserMerIdDataResponse();

        //返回数据
        return userMerIdDataResponse;
    }

    @Override
    public List<UserSpreadBannerResponse> getSpreadBannerList() {
        return systemGroupDataService.getListByGid(Constants.GROUP_DATA_ID_RETAILER_BANNER_LIST, UserSpreadBannerResponse.class);
    }

    @Override
    public PageInfo<RetailerPraResponse> retailerProductList(RetailerSearchRequest request, PageParamRequest pageParamRequest) {
        //分页
        Page<RetailerResponse> articlePage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //得到-当前登录用户
        User user=userService.getInfoException();

        //得到-该用户绑定的零售商
        Retailer retailer = this.getWhereUid(user.getUid());
        if(retailer == null )return new PageInfo<>();

        //得到数据
        Boolean isSale=Boolean.FALSE;
        if(request.getIsSale())isSale=Boolean.TRUE;
        List<RetailerPraResponse> storeProductList = retailerPraService.getRetailerProductList(retailer.getId(),0,isSale);

        //返回
        return CommonPage.copyPageInfo(articlePage, storeProductList);
    }

    @Override
    public Retailer getWhereUid(Integer uid) {
        //定义查询对象
        LambdaQueryWrapper<Retailer> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Retailer::getUid,uid);
        lambdaQueryWrapper.last(" limit 1");//取一条
        return dao.selectOne(lambdaQueryWrapper);
    }

    @Override
    public Boolean update(RetailerRequest request) {
        //根据id表示查询
        Retailer retailer = dao.selectById(request.getId());
        if(retailer == null) throw new CrmebException("零售商信息不存在!");

        //转换
        BeanUtils.copyProperties(request, retailer);
        retailer.setStatus(0);
        retailer.setRePhone(null);

        //执行修改并返回结果
        return dao.updateById(retailer)>0;
    }

    @Override
    public RetailerResponse getInfo(Integer id) {
        //得到-数据
        Retailer retailer=dao.selectById(id);
        if(retailer == null)throw new CrmebException("零售商信息不存在!");

        //转换-为响应对象
        RetailerResponse response=new RetailerResponse();
        BeanUtils.copyProperties(retailer, response);

        //得到-区域代理信息
        RegionalAgency regionalAgency = regionalAgencyService.getById(retailer.getRaId());
        if(regionalAgency == null){
            response.setRaName("区域代理信息已不存在！");
        }else{
            response.setRaName(regionalAgency.getRaName());
        }

        //验证-该手机用户是否已经注册
        User user = userService.getByPhone(retailer.getRePhone());
        if (ObjectUtil.isNull(user)) {
            response.setIsRegister(Boolean.FALSE);
        }else{
            response.setIsRegister(Boolean.TRUE);
        }

        //返回
        return response;
    }

    @Override
    public List<RetailerResponse> getInfoList(Integer raId) {
        List<Integer> raids=new ArrayList<>();
        raids.add(raId);
        return this.getWhereRaIdsList(raids);
    }

    @Override
    public Boolean updateState(Integer id, Integer status) {
        //得到-数据
        Retailer retailer = dao.selectById(id);
        if(retailer == null ) throw new CrmebException("零售商不存在");

        //验证参数
        if(status == null || status <1 || status > 2)throw new CrmebException("状态值参数有误！");

        //赋值
        retailer.setStatus(status);

        //验证-状态值
        switch (retailer.getStatus()){
            case 1:
                //审核通过，验证手机号账户是否存在
                User user = userService.getByPhone(retailer.getRePhone());
                if (!ObjectUtil.isNull(user)) {
                    user.setIsRetailer(Boolean.TRUE);//标记为零售商
                    retailer.setUid(user.getUid());//零售商-管理者
                    userService.updateById(user);//更新
                }else{
                    throw new CrmebException("平台不存在该用户！");
                }
                break;
        }

        //执行修改
        return dao.updateById(retailer)>0;
    }

    @Override
    public Retailer add(RetailerRequest request) {
        //检测-手机号验证码
        Object validateCode = redisUtil.get(SmsConstants.SMS_VALIDATE_PHONE + request.getRePhone());
        if (ObjectUtil.isNull(validateCode)) {
            throw new CrmebException("验证码已过期");
        }else if (!validateCode.toString().equals(request.getCode())) {
            throw new CrmebException("验证码错误");
        }else{
            //删除验证码
            redisUtil.remove(SmsConstants.SMS_VALIDATE_PHONE + request.getRePhone());
        }

        //转换
        Retailer retailer=new Retailer();
        BeanUtils.copyProperties(request, retailer);

        //清除图片路径域名部分
        retailer.setReYyzz(systemAttachmentService.clearPrefix(retailer.getReYyzz()));
        retailer.setIdZheng(systemAttachmentService.clearPrefix(retailer.getIdZheng()));
        retailer.setIdFan(systemAttachmentService.clearPrefix(retailer.getIdFan()));

        //执行并验证结果
        if(dao.insert(retailer)<0){
            throw new CrmebException("新增零售商失败!");
        }

        //验证-该手机用户是否已经注册
        User user = userService.getByPhone(retailer.getRePhone());
        if (ObjectUtil.isNull(user)) {
            throw new CrmebException("该手机号还为注册平台账号!");
        }

        //返回
        return retailer;
    }

    @Override
    public PageInfo<RetailerResponse> getList(RetailerSearchRequest request, PageParamRequest pageParamRequest) {
        //分页
        Page<RetailerResponse> articlePage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //定义查询对象
        LambdaQueryWrapper<Retailer> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        //条件-状态
        if(StringUtils.isNotBlank(String.valueOf(request.getStatus())) && request.getStatus() > 0){
            lambdaQueryWrapper.eq(Retailer::getStatus,request.getStatus());
        }

        //条件-关键字搜索
        if(StringUtils.isNotBlank(request.getKeywords())){
            lambdaQueryWrapper.and(i -> i.like(Retailer::getReName, request.getKeywords())
                    .or().like(Retailer::getId, request.getKeywords()));
        }

        //条件-区域代理ID标识
        if(request.getRaId() != null && request.getRaId() > 0){
            lambdaQueryWrapper.eq(Retailer::getRaId,request.getRaId());
        }

        //排序
        lambdaQueryWrapper.orderByDesc(Retailer::getId);

        //得到-数据
        List<Retailer> retailerList= dao.selectList(lambdaQueryWrapper);
        List<RetailerResponse> retailerResponseList=new ArrayList<>();
        for (Retailer record:retailerList) {
            //实例化-响应对象
            RetailerResponse response=new RetailerResponse();
            BeanUtils.copyProperties(record, response);//转换

            //得到-用户名称
            User user = userService.getById(record.getUid());
            if(user == null) {
                response.setNickname("用户已不存在!");
            }else{
                response.setNickname(user.getNickname());
            }

            //添加到-list响应集合
            retailerResponseList.add(response);
        }

        //返回
        return CommonPage.copyPageInfo(articlePage, retailerResponseList);
    }
}
