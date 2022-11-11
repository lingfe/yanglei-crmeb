package com.zbkj.crmeb.marketing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.zbkj.crmeb.marketing.model.StoreWear;
import com.zbkj.crmeb.marketing.request.StoreWearRequest;
import com.zbkj.crmeb.marketing.request.StoreWearSearchRequest;
import com.zbkj.crmeb.marketing.response.StoreWearResponse;

import java.util.List;

/**
 * 穿搭表-service层接口
 * @author: 零风
 * @CreateDate: 2021/10/8 10:51
 */
public interface StoreWearService extends IService<StoreWear> {

    /**
     * 分页查询
     * @param request
     * @param pageParamRequest
     * @author  零风
     * @CreateDate: 2021/10/8 10:51
     * @return
     */
    List<StoreWear> getList(StoreWearSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 添加-穿搭
     * @param storeWearRequest
     * @author 零风
     * @CreateDate: 2021/10/8 10:51
     * @return
     */
    Boolean insert(StoreWearRequest storeWearRequest);

    /**
     * 删除-穿搭
     * @param id
     * @author  零风
     * @CreateDate: 2021/10/8 10:51
     * @return
     */
    Boolean delete(Integer id);

    /**
     * 修改-穿搭
     * @param storeWearRequest  请求修改参数
     * @author  零风
     * @CreateDate: 2021/10/8 10:51
     * @return
     */
    Boolean update(StoreWearRequest storeWearRequest);

    /**
     * 是否显示
     * @param isShow    是否显示： false：否，true：是
     * @author  零风
     * @CreateDate: 2021/10/8 10:51
     * @return
     */
    Boolean isShow(Integer id,Boolean isShow);

    /**
     * 穿搭-详情
     * @param id    穿搭表标识
     * @author  零风
     * @CreateDate: 2021/10/8 10:51
     * @return
     */
    StoreWearResponse info(Integer id);

    /**
     * 获取穿搭-展示在H5or用户端首页
     * @author  零风
     * @CreateDate: 2021/10/9 10:51
     * @return
     */
    StoreWearResponse getWearH5Index();

    /**
     * 是否展示在首页-h5用户端
     * @param id    穿搭表标识
     * @param isIndex    是否展示在首页
     * @author  零风
     * @CreateDate: 2021/10/9 10:51
     * @return
     */
    Boolean isIndex(Integer id,Boolean isIndex);

    /**
     * 获取-穿搭列表-用户h5用户端
     * @param request
     * @param pageParamRequest
     * @return
     */
    List<StoreWear> getWearH5List(StoreWearSearchRequest request, PageParamRequest pageParamRequest);
}
