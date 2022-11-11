package com.zbkj.crmeb.finance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.finance.model.InvoiceRise;
import com.zbkj.crmeb.finance.request.InvoiceRiseSearchRequest;
import com.zbkj.crmeb.finance.response.InvoiceRiseResponse;

/**
 * 发票抬头表-service层接口
 * @author: 零风
 * @CreateDate: 2022/4/14 14:49
 */
public interface InvoiceRiseService extends IService<InvoiceRise> {

    /**
     * 分页列表
     * @param request    请求参数
     * @param pageParamRequest  分页参数
     * @return
     */
    PageInfo<InvoiceRise> getPageList(InvoiceRiseSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 添加或修改发票抬头
     * @param invoiceRise 发票抬头信息
     * @Author 零风
     * @Date  2022/4/14
     * @return 结果
     */
    boolean sou(InvoiceRise invoiceRise);

    /**
     * 详情
     * @param id 发票抬头表ID标识
     * @Author 零风
     * @Date  2022/4/14
     * @return 发票抬头信息
     */
    InvoiceRiseResponse info(Integer id);

}
