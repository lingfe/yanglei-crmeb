package com.zbkj.crmeb.retailer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.front.response.UserSpreadBannerResponse;
import com.zbkj.crmeb.retailer.model.RetailerPra;
import com.zbkj.crmeb.retailer.response.RetailerPraResponse;
import com.zbkj.crmeb.retailer.response.RetailerResponse;
import com.zbkj.crmeb.store.model.StoreProduct;
import com.zbkj.crmeb.user.response.UserMerIdDataResponse;
import org.apache.commons.math3.stat.descriptive.summary.Product;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
* 零售商产品代理表-service层接口
* @author: 零风
* @CreateDate: 2021/11/29 16:00
*/
public interface RetailerPraService extends IService<RetailerPra> {

    /**
     * 获取-零售商产品代理列表
     * @param retailerId    零售商表ID标识
     * @param type          类型，0=零售商管理查看、1=用户查看、2=区域代理查看
     * @param isSale        是否销售(是否上架中)
     * @return
     */
    List<RetailerPraResponse> getRetailerProductList(Integer retailerId, Integer type,Boolean isSale);

    /**
     * 是否销售(上下架)
     * @param id        零售商产品代理ID标识
     * @param isSale    是否销售
     * @return
     */
    Boolean isSale(Integer id,Boolean isSale);

    /**
     * 更加零售商id标识得到-零售商代理产品信息
     * @param retailerId 零售商ID标识
     * @Author 零风
     * @Date  2021/12/15
     * @return
     */
    List<RetailerPra> getWhereRetailerId(Integer retailerId);


}
