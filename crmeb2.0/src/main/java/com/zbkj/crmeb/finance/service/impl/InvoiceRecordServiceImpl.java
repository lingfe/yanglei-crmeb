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
        //??????????????????
        InvoiceRecord record=dao.selectById(id);
        if(record == null )throw new CrmebException("??????????????????");

        //????????????????????????
        record.setInvoiceNumber(invoiceNumber);
        record.setRemark(remark);
        record.setStatus(Constants.INVOICE_RECORD_2);
        return dao.updateById(record)>0;
    }

    @Override
    public InvoiceRecordResponse info(Integer id) {
        //????????????????????????
        InvoiceRecord r=dao.selectById(id);
        if( r == null)return null;

        //?????????????????????????????????
        InvoiceRecordResponse response=new InvoiceRecordResponse();
        response.setId(r.getId());

        //????????????????????????
        response.setInvoiceRiseResponse(invoiceRiseService.info(r.getRiseId()));

        //????????????????????????
        response.setStatus(r.getStatus());
        response.setStatusStr(this.getStatueStr(r.getStatus()));
        response.setRecordTime(r.getCreateTime().toString());
        response.setInvoiceNumber(r.getInvoiceNumber());
        response.setRemark(r.getRemark());

        //??????????????????
        StoreOrder storeOrder=storeOrderService.getById(r.getOrderId());
        if(storeOrder == null) storeOrder=new StoreOrder();
        response.setOrderId(storeOrder.getOrderId());
        response.setMark(storeOrder.getMark());
        response.setPayPrice(storeOrder.getPayPrice());
        response.setPayTime(storeOrder.getPayTime());

        //????????????????????????
        //??????-????????????map
        List<Integer> orderIds=new ArrayList<>();
        orderIds.add(storeOrder.getId());
        HashMap<Integer, List<StoreOrderInfoOldVo>> orderInfoList = storeOrderInfoService.getMapInId(orderIds);
        if(orderInfoList !=null)response.setProductList(orderInfoList.get(storeOrder.getId()));

        //?????????????????????
        response.setOrderStatus(storeOrder.getStatus());
        response.setOrderStatusStr(storeOrderService.getStatus(storeOrder).get("value"));

        //???????????????????????????
        response.setPayType(storeOrderService.getOrderPayTypeStr(storeOrder.getPayType()));

        //????????????
        return response;
    }

    @Override
    public String getStatueStr(Integer statue) {
        switch (statue){
            case Constants.INVOICE_RECORD_0:return Constants.INVOICE_RECORD_0_STR;
            case Constants.INVOICE_RECORD_1:return Constants.INVOICE_RECORD_1_STR;
            case Constants.INVOICE_RECORD_2:return Constants.INVOICE_RECORD_2_STR;
            default:return "??????";
        }
    }

    @Override
    public PageInfo<InvoiceRecordResponse> getPageList(InvoiceRecordSearchRequest request, PageParamRequest pageParamRequest) {
        //??????????????????
        Page<InvoiceRecord> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //????????????????????????id??????
        Integer uid=userService.getUserIdException();

        //??????lambda???????????????????????????
        LambdaQueryWrapper<InvoiceRecord> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(InvoiceRecord::getIsDel,Boolean.FALSE);
        lambdaQueryWrapper.eq(InvoiceRecord::getIsShow,Boolean.TRUE);
        lambdaQueryWrapper.eq(InvoiceRecord::getUid,uid);

        //??????
        if(request.getStatus() !=null && request.getStatus() > 0){
            lambdaQueryWrapper.eq(InvoiceRecord::getStatus,request.getStatus());
        }

        //??????-?????????
        if(StringUtils.isNotBlank(request.getKeywords())){
            lambdaQueryWrapper.like(InvoiceRecord::getOrderId, request.getKeywords());
        }

        //?????????????????????sort????????????????????????
        lambdaQueryWrapper.orderByDesc(InvoiceRecord::getId).orderByDesc(InvoiceRecord::getCreateTime);

        //????????????
        List<InvoiceRecord> list = dao.selectList(lambdaQueryWrapper);
        List<InvoiceRecordResponse> responseList=new ArrayList<>();
        for (InvoiceRecord r:list ) {
            //??????????????????????????????
            InvoiceRecordResponse response=this.info(r.getId());
            if(response == null)continue;
            response.setId(r.getId());

            //?????????list
            responseList.add(response);
        }

        //????????????
        return CommonPage.copyPageInfo(page, responseList);
    }

}
