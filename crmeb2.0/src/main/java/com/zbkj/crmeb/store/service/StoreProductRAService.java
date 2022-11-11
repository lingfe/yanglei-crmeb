package com.zbkj.crmeb.store.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.store.model.StoreProductRA;
import com.zbkj.crmeb.store.request.StoreProductRARequest;
import com.zbkj.crmeb.store.response.StoreProductRAResponse;

import java.util.List;

/**
* 产品代理-service接口层
* @author: 零风
* @CreateDate: 2021/11/24 9:54
*/
public interface StoreProductRAService extends IService<StoreProductRA> {

    /**
     * 根据产品ID-查询代理商列表
     * @param productId
     * @return
     */
    List<StoreProductRAResponse> getInfoList(Integer productId);

    /**
     * 添加-产品代理信息
     * @param request
     * @return
     */
    Boolean add(StoreProductRARequest request);

    /**
     * 通过-区域代理ID标识-得到产品代理表信息
     * @param raId 区域代理ID标识
     * @return
     */
    List<StoreProductRA> getWhereRaIDList(Integer raId);


    /**
     * 删除-产品代理信息。
     * @param id 产品代理表ID标识
     * @return
     */
    Boolean delete(Integer id);

    /**
     * 根据零售商id标识得到产品代理list
     * @param retailerId    零售商id标识
     * @Author 零风
     * @Date  2021/12/15
     * @return
     */
    List<StoreProductRA> getWhereRetailerId(Integer retailerId);

}
