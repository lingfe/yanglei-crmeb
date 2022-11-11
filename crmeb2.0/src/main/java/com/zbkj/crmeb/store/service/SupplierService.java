package com.zbkj.crmeb.store.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.zbkj.crmeb.front.response.UserMerIdOrderDetailsResponse;
import com.zbkj.crmeb.store.model.StoreProduct;
import com.zbkj.crmeb.store.model.Supplier;
import com.zbkj.crmeb.store.request.SupplierRequest;
import com.zbkj.crmeb.store.request.SupplierSearchRequest;
import com.zbkj.crmeb.user.response.UserMerIdDataResponse;

import java.util.List;

/**
 * 供应商表-service层接口
 * @author: 零风
 * @CreateDate: 2021/12/28 11:22
 */
public interface SupplierService extends IService<Supplier> {

    /**
     * 得到供应商绑定的商品列表
     * @param supplierId 供应商表ID标识
     * @param pageParamRequest 分页参数
     * @Author 零风
     * @Date  2022/3/1
     * @return
     */
    List<StoreProduct> getSupplierProductList(Integer supplierId,PageParamRequest pageParamRequest);

    /**
     * 得到供应商名称
     * @param id 供应商ID标识
     * @Author 零风
     * @Date  2022/1/5
     * @return 供应商名称
     */
    String getSupplierName(Integer id);

    /**
     * 供应商-清除图片字段前缀
     * @param supplier 供应商实体对象
     * @Author 零风
     * @Date  2021/12/29
     */
    void clearPrefix(Supplier supplier);

    /**
     * 供应商-订单详细统计数据
     * @param id        供应商ID标识
     * @param dateType  日期类型(0=今天、1=昨天、2=最近7天、3=本月、4=本年)
     * @Author 零风
     * @Date  2021/12/29
     * @return 数据
     */
    UserMerIdOrderDetailsResponse getSupplierOrderInfoStatisticsData(Integer id, Integer dateType);

    /**
     * 供应商-相关数据统计
     * @param id 供应商ID标识
     * @Author 零风
     * @Date  2021/12/29
     * @return 数据
     */
    UserMerIdDataResponse getSupplierData(Integer id);
    /**
     * 获取-用户绑定的供应商列表
     * @Author 零风
     * @Date  2021/12/29
     * @return 列表
     */
    List<Supplier> getUidSupplierList();

    /**
     * 获取-供应商信息-带异常验证
     * @param id 供应商id标识
     * @Author 零风
     * @Date  2021/12/28
     * @return 对象
     */
    Supplier getSupplierException(Integer id);

    /**
     * 供应商-审核
     * @param id    供应商id标识
     * @param status 状态值：1=通过、2=不通过
     * @Author 零风
     * @Date  2021/12/28
     * @return 结果
     */
    Boolean toExamine(Integer id, Integer status);

    /**
     * 供应商-修改
     * @param request 参数
     * @Author 零风
     * @Date  2021/12/28
     * @return 结果
     */
    Boolean update(SupplierRequest request);

    /**
     * 供应商-新增
     * @param request 参数
     * @Author 零风
     * @Date  2021/12/28
     * @return 结果
     */
    Boolean save(SupplierRequest request);

    /**
     * 供应商-分页列表
     * @param request
     * @param pageParamRequest
     * @Author 零风
     * @Date  2021/12/28
     * @return 列表
     */
    List<Supplier> getPageList(SupplierSearchRequest request, PageParamRequest pageParamRequest);

}
