package com.zbkj.crmeb.finance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.finance.model.InvoiceRecord;
import com.zbkj.crmeb.finance.request.InvoiceRecordSearchRequest;
import com.zbkj.crmeb.finance.response.InvoiceRecordResponse;

/**
 * 发票记录表-service层接口
 * @author: 零风
 * @CreateDate: 2022/4/14 15:06
 */
public interface InvoiceRecordService extends IService<InvoiceRecord> {

    /**
     * 处理发票
     * @param id 发票记录表ID标识
     * @param invoiceNumber 发票号码
     * @param remark 发票备注
     * @Author 零风
     * @Date  2022/4/15
     * @return
     */
    Boolean chuliInvoice(Integer id,String invoiceNumber,String remark);

    /**
     * 发票记录详细信息
     * @param id 发票记录表ID标识
     * @Author 零风
     * @Date  2022/4/15
     * @return 数据
     */
    InvoiceRecordResponse info(Integer id);

    /**
     * 得到发票记录状态字符串(中文)
     * @Author 零风
     * @Date  2022/4/15
     * @return 字符串
     */
    String getStatueStr(Integer statue);

    /**
     * 分页列表
     * @param request    请求参数
     * @param pageParamRequest  分页参数
     * @return
     */
    PageInfo<InvoiceRecordResponse> getPageList(InvoiceRecordSearchRequest request, PageParamRequest pageParamRequest);

}
