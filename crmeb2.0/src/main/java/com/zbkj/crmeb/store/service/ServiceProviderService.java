package com.zbkj.crmeb.store.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.store.model.ServiceProvider;
import com.zbkj.crmeb.store.model.ServiceProviderTwolevel;
import com.zbkj.crmeb.store.request.ServiceProviderSearchRequest;
import com.zbkj.crmeb.store.request.ServiceProviderTwolevelSearchRequest;
import com.zbkj.crmeb.store.response.ServiceProviderDataResponse;
import com.zbkj.crmeb.user.model.User;

/**
 * 服务商表-service层接口
 * @author: 零风
 * @CreateDate: 2022/5/9 14:42
 */
public interface ServiceProviderService extends IService<ServiceProvider> {

    /**
     * 服务商删除二级商户
     * @param id 二级商户ID标识
     * @Author 零风
     * @Date  2022/5/11 10:06
     * @return 结果
     */
    boolean deleteTwelevelProvider(Integer id);

    /**
     * 服务商保存或更新二级商户
     * @param serviceProviderTwolevel 二级商户信息
     * @Author 零风
     * @Date  2022/5/11 10:01
     * @return 结果
     */
    boolean saveOrUpdateTwolevel(ServiceProviderTwolevel serviceProviderTwolevel);

    /**
     * 服务商二级商户分页列表
     * @param request   搜索参数
     * @param pageParamRequest 分页参数
     * @Author 零风
     * @Date  2022/5/11 9:50
     * @return 数据
     */
    PageInfo<ServiceProviderTwolevel> getPageTwolevelList(ServiceProviderTwolevelSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 服务商统计数据
     * @param id 服务商表ID标识
     * @Author 零风
     * @Date  2022/5/10 10:37
     * @return 数据
     */
    ServiceProviderDataResponse data(Integer id);

    /**
     * 得到服务商统计数据(公共接口)
     * @param idType    ID类型(1=用户id、2=商户ID、3=服务商ID、4=二级商户ID)
     * @param value     值
     * @Author 零风
     * @Date  2022/5/10 15:47
     * @return 数据
     */
    ServiceProviderDataResponse getServiceProviderDataResponse(Integer idType, Integer value);

    /**
     * 所有会员分页列表
     * @param typeId ID标识类型(1=服务商表ID标识、2=二级商户ID)
     * @param value  值
     * @param pageParamRequest 分页参数
     * @Author 零风
     * @Date  2022/5/10 10:36
     * @return 分页数据
     */
    PageInfo<User> getPageUserList(Integer typeId,Integer value,PageParamRequest pageParamRequest);

    /**
     * 分页获取
     * @param request   请求参数
     * @param pageParamRequest  分页参数
     * @Author 零风
     * @Date  2022/5/9
     * @return  分页结果
     */
    PageInfo<ServiceProvider> getPageList(ServiceProviderSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 保存服务商信息
     * -如果服务商表ID标识存在,则执行更新
     * @param serviceProvider 服务商信息
     * @Author 零风
     * @Date  2022/5/9
     * @return 结果
     */
    Boolean saveServiceProvider(ServiceProvider serviceProvider);


}
