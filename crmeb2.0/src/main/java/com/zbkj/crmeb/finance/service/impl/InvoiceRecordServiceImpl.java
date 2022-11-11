package com.zbkj.crmeb.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CommonPage;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.exception.CrmebException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.finance.dao.InvoiceRecordDao;
import com.zbkj.crmeb.finance.model.InvoiceRecord;
import com.zbkj.crmeb.finance.request.InvoiceRecordSearchRequest;
import com.zbkj.crmeb.finance.response.InvoiceRecordResponse;
import com.zbkj.crmeb.finance.service.InvoiceRecordService;
import com.zbkj.crmeb.finance.service.InvoiceRiseService;
import com.zbkj.crmeb.store.model.StoreOrder;
import com.zbkj.crmeb.store.service.StoreOrderInfoService;
import com.zbkj.crmeb.store.service.StoreOrderService;
import com.zbkj.crmeb.store.vo.StoreOrderInfoOldVo;
import com.zbkj.crmeb.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class InvoiceRecordServiceImpl  extends ServiceImpl<InvoiceRecordDao, InvoiceRecord> implements InvoiceRecordService {

    private static final Logger logger = LoggerFactory.getLogger(InvoiceRecordServiceImpl.class);

    @Resource
    private InvoiceRecordDao dao;

    @Autowired
    private InvoiceRiseService invoiceRiseService;

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private StoreOrderInfoService storeOrderInfoService;

    @Autowired
    private UserService userService;

    @Override
    public Boolean chuliInvoice(Integer id, String invoiceNumber, String remark) {
        //得到发票记录
        InvoiceRecord record=dao.selectById(id);
        if(record == null )throw new CrmebException("发票记录为空");

        //更新参数执行更新
        record.setInvoiceNumber(invoiceNumber);
        record.setRemark(remark);
        record.setStatus(Constants.INVOICE_RECORD_2);
        return dao.updateById(record)>0;
    }

    @Override
    public InvoiceRecordResponse info(Integer id) {
        //得到发票记录信息
        InvoiceRecord r=dao.selectById(id);
        if( r == null)return null;

        //实例化发票记录响应对象
        InvoiceRecordResponse response=new InvoiceRecordResponse();
        response.setId(r.getId());

        //得到发票抬头信息
        response.setInvoiceRiseResponse(invoiceRiseService.info(r.getRiseId()));

        //得到发票记录状态
        response.setStatus(r.getStatus());
        response.setStatusStr(this.getStatueStr(r.getStatus()));
        response.setRecordTime(r.getCreateTime().toString());
        response.setInvoiceNumber(r.getInvoiceNumber());
        response.setRemark(r.getRemark());

        //得到订单信息
        StoreOrder storeOrder=storeOrderService.getById(r.getOrderId());
        if(storeOrder == null) storeOrder=new StoreOrder();
        response.setOrderId(storeOrder.getOrderId());
        response.setMark(storeOrder.getMark());
        response.setPayPrice(storeOrder.getPayPrice());
        response.setPayTime(storeOrder.getPayTime());

        //得到订单商品信息
        //获取-订单详情map
        List<Integer> orderIds=new ArrayList<>();
        orderIds.add(storeOrder.getId());
        HashMap<Integer, List<StoreOrderInfoOldVo>> orderInfoList = storeOrderInfoService.getMapInId(orderIds);
        if(orderInfoList !=null)response.setProductList(orderInfoList.get(storeOrder.getId()));

        //订单状态字符串
        response.setOrderStatus(storeOrder.getStatus());
        response.setOrderStatusStr(storeOrderService.getStatus(storeOrder).get("value"));

        //订单支付方式字符串
        response.setPayType(storeOrderService.getOrderPayTypeStr(storeOrder.getPayType()));

        //返回详情
        return response;
    }

    @Override
    public String getStatueStr(Integer statue) {
        switch (statue){
            case Constants.INVOICE_RECORD_0:return Constants.INVOICE_RECORD_0_STR;
            case Constants.INVOICE_RECORD_1:return Constants.INVOICE_RECORD_1_STR;
            case Constants.INVOICE_RECORD_2:return Constants.INVOICE_RECORD_2_STR;
            default:return "未知";
        }
    }

    @Override
    public PageInfo<InvoiceRecordResponse> getPageList(InvoiceRecordSearchRequest request, PageParamRequest pageParamRequest) {
        //得到分页对象
        Page<InvoiceRecord> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //得到当前登录用户id标识
        Integer uid=userService.getUserIdException();

        //得到lambda对象，用作筛选条件
        LambdaQueryWrapper<InvoiceRecord> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(InvoiceRecord::getIsDel,Boolean.FALSE);
        lambdaQueryWrapper.eq(InvoiceRecord::getIsShow,Boolean.TRUE);
        lambdaQueryWrapper.eq(InvoiceRecord::getUid,uid);

        //状态
        if(request.getStatus() !=null && request.getStatus() > 0){
            lambdaQueryWrapper.eq(InvoiceRecord::getStatus,request.getStatus());
        }

        //条件-关键字
        if(StringUtils.isNotBlank(request.getKeywords())){
            lambdaQueryWrapper.like(InvoiceRecord::getOrderId, request.getKeywords());
        }

        //排序条件，根据sort以及创建时间排序
        lambdaQueryWrapper.orderByDesc(InvoiceRecord::getId).orderByDesc(InvoiceRecord::getCreateTime);

        //得到数据
        List<InvoiceRecord> list = dao.selectList(lambdaQueryWrapper);
        List<InvoiceRecordResponse> responseList=new ArrayList<>();
        for (InvoiceRecord r:list ) {
            //得到发票记录响应对象
            InvoiceRecordResponse response=this.info(r.getId());
            if(response == null)continue;
            response.setId(r.getId());

            //添加到list
            responseList.add(response);
        }

        //返回数据
        return CommonPage.copyPageInfo(page, responseList);
    }

}
