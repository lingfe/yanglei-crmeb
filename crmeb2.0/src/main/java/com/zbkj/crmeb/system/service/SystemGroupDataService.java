package com.zbkj.crmeb.system.service;

import com.common.PageParamRequest;
import com.zbkj.crmeb.system.model.SystemGroupData;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.system.request.SystemGroupDataRequest;
import com.zbkj.crmeb.system.request.SystemGroupDataSearchRequest;

import java.util.HashMap;
import java.util.List;

/**
 * SystemGroupDataService-接口
 * @author: 零风
 * @CreateDate: 2022/1/11 10:56
 */
public interface SystemGroupDataService extends IService<SystemGroupData> {

    List<SystemGroupData> getList(SystemGroupDataSearchRequest request, PageParamRequest pageParamRequest);

    boolean create(SystemGroupDataRequest systemGroupDataRequest);

    boolean update(Integer id, SystemGroupDataRequest request);

    <T> List<T> getListByGid(Integer gid, Class<T> cls);

    /**
     * 根据组合gid得到数据列表
     * @param gid 组合数据ID
     * @Author 零风
     * @Date  2022/1/11
     * @return 数据列表
     */
    List<HashMap<String, Object>> getListMapByGid(Integer gid);

    <T> T getNormalInfo(Integer groupDataId, Class<T> cls);

    /**
     * 获取个人中心菜单
     * @return HashMap<String, Object>
     */
    HashMap<String, Object> getMenuUser();
}