package com.zbkj.crmeb.finance.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.zbkj.crmeb.finance.request.InvoiceRecordSearchRequest;
import com.zbkj.crmeb.finance.response.InvoiceRecordResponse;
import com.zbkj.crmeb.finance.service.InvoiceRecordService;
import com.zbkj.crmeb.finance.service.InvoiceRiseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 发票管理
 * @author: 零风
 * @CreateDate: 2022/4/14 15:21
 */
@Slf4j
@RestController
@RequestMapping("api/admin/finance/invoice")
@Api(tags = "财务 -- 发票")
public class InvoiceController {

    @Autowired
    private InvoiceRiseService invoiceRiseService;

    @Autowired
    private InvoiceRecordService invoiceRecordService;

    @ApiOperation(value = "分页发票记录列表",notes = Constants.SELECT) //配合swagger使用
    @RequestMapping(value = "/getRecordPageList", method = RequestMethod.GET)
    public CommonResult<CommonPage<InvoiceRecordResponse>> getRecordList(
            @Validated InvoiceRecordSearchRequest request,
            @Validated PageParamRequest pageParamRequest){
        return CommonResult.success(CommonPage.restPage(invoiceRecordService.getPageList(request, pageParamRequest)));
    }

    @ApiOperation(value = "处理发票",notes = Constants.SELECT)
    @RequestMapping(value = "/invoice/invoiceRecordInfo", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "发票记录表id标识",required = true),
            @ApiImplicitParam(name = "invoiceNumber",value = "发票号码",required = true),
            @ApiImplicitParam(name = "remark",value = "备注")
    })
    public CommonResult<Boolean> invoiceRecordInfo(
            @RequestParam("id")Integer id,
            @RequestParam("invoiceNumber")String invoiceNumber,
            @RequestParam("remark")String remark){
        return CommonResult.success(invoiceRecordService.chuliInvoice(id,invoiceNumber,remark));
    }

    @ApiOperation(value = "发票-发票记录详细信息显示",notes = Constants.SELECT)
    @RequestMapping(value = "/invoiceRecordInfo", method = RequestMethod.GET)
    @ApiImplicitParam(name = "id",value = "发票记录表id标识",required = true)
    public CommonResult<InvoiceRecordResponse> invoiceRecordInfo(@RequestParam("id")Integer id){
        return CommonResult.success(invoiceRecordService.info(id));
    }

}
