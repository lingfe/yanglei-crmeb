package com.zbkj.crmeb.store.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.store.model.ServiceProviderTwolevel;
import com.zbkj.crmeb.store.request.ServiceProviderTwolevelSearchRequest;
import com.zbkj.crmeb.store.response.ServiceProviderDataResponse;

/**
 * 服务商二级商户表-service层接口-实现类
 * @author: 零风
 * @CreateDate: 2022/5/9 14:42
 */
public interface ServiceProviderTwolevelService extends IService<ServiceProviderTwolevel> {

    /**
     * 服务商二级商户统计数据
     * -如果ID不为空，则取ID的统计数据
     * -如果ID为空，则取当前管理者绑定的二级商户
     * @param id 服务商二级商户表ID标识
     * @Author 零风
     * @Date  2022/5/10 10:37
     * @return 数据
     */
    ServiceProviderDataResponse data(Integer id);

    /**
     * 分页获取
     * @param request   请求参数
     * @param pageParamRequest  分页参数
     * @Author 零风
     * @Date  2022/5/9
     * @return  分页结果
     */
    PageInfo<ServiceProviderTwolevel> getPageList(ServiceProviderTwolevelSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 保存服务商二级商户信息
     * -如果服务商二级商户表ID标识存在,则执行更新
     * @param serviceProviderTwolevel 二级商户信息
     * @Author 零风
     * @Date  2022/5/9
     * @return 结果
     */
    Boolean saveServiceProviderTwolevel(ServiceProviderTwolevel serviceProviderTwolevel);

}
