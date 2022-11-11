package com.zbkj.crmeb.store.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.store.model.StoreBrands;
import com.zbkj.crmeb.store.request.StoreBrandsSearchRequest;
import com.zbkj.crmeb.store.response.StoreBrandsPreferredRsponse;
import com.zbkj.crmeb.store.vo.StoreBrandsVo;

import java.util.List;


/**
 * @program: crmeb
 * @description:  品牌表接口
 * @author: 零风
 * @create: 2021-06-23 11:12
 **/
public interface StoreBrandsService extends IService<StoreBrands> {


    /**
     * 根据分类ID查询品牌list-（H5-用户端使用）
     * @param request 请求参数
     * @return
     */
    List<StoreBrands> getCateIdList(StoreBrandsSearchRequest request);

    /**
     * 根据ID获取品牌详细信息
     * @param id
     * @return
     */
    StoreBrandsVo getInfoId(Integer id);

    /**
     * 修改是否显示
     * @param id
     * @return
     */
    boolean updateIsDisplay(Integer id);

    /**
     * 分页获取品牌数据
     * @param request
     * @param pageParamRequest
     * @return
     */
    PageInfo<StoreBrands> getAdminList(StoreBrandsSearchRequest request, PageParamRequest pageParamRequest);


    /**
     * 获取-品牌优选-详情
     * @param pageParamRequest  分页参数
     * @return
     */
    StoreBrandsPreferredRsponse getBrandsPreferredInfo(StoreBrandsSearchRequest request,PageParamRequest pageParamRequest);

}
