package com.zbkj.crmeb.regionalAgency.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.regionalAgency.model.RegionalAgency;
import com.zbkj.crmeb.regionalAgency.request.RegionalAgencySearchRequest;
import com.zbkj.crmeb.regionalAgency.response.RegionalAgencyResponse;
import com.zbkj.crmeb.user.model.UserAddress;

import java.util.List;

/**
 * 区域代理表-service层接口
 * @author: 零风
 * @CreateDate: 2021/11/6 10:28
 */
public interface RegionalAgencyService extends IService<RegionalAgency> {

    /**
     * 得到区域代理名称
     * @param id   区域代理ID标识
     * @Author 零风
     * @Date  2022/1/5
     * @return 区域代理名称
     */
    String getRaName(Integer id);

    /**
     * 得到区域代理信息
     * @param id   区域代理ID标识
     * @Author 零风
     * @Date  2022/1/5
     * @return 区域代理信息
     */
    RegionalAgency getRegionalAgencyException(String id);

    /**
     * 分页查询
     * @param request
     * @param pageParamRequest
     * @author  零风
     * @Date  2021/11/6
     * @return  分页对象
     */
    PageInfo<RegionalAgencyResponse> getList(RegionalAgencySearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 得到区域代理用户Id-根据用户收货地址筛选
     * @Author 零风
     * @Date  2021/11/6
     * @return 区域代理信息
     */
    RegionalAgency getRAUid(UserAddress userAddress);

    /**
     * 根据用户ID-得到区域代理信息
     * @param uid  用户ID
     * @return
     */
    List<RegionalAgency> getWhereUserID(Integer uid);


}
