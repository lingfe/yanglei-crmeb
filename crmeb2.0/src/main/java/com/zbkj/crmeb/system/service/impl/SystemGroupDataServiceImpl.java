package com.zbkj.crmeb.system.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.PageParamRequest;
import com.constants.SysGroupDataConstants;
import com.github.pagehelper.PageHelper;
import com.utils.CrmebUtil;
import com.zbkj.crmeb.system.dao.SystemGroupDataDao;
import com.zbkj.crmeb.system.model.SystemGroupData;
import com.zbkj.crmeb.system.request.SystemFormItemCheckRequest;
import com.zbkj.crmeb.system.request.SystemGroupDataRequest;
import com.zbkj.crmeb.system.request.SystemGroupDataSearchRequest;
import com.zbkj.crmeb.system.service.SystemAttachmentService;
import com.zbkj.crmeb.system.service.SystemFormTempService;
import com.zbkj.crmeb.system.service.SystemGroupDataService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * SystemGroupDataServiceImpl 接口实现
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Service
public class SystemGroupDataServiceImpl extends ServiceImpl<SystemGroupDataDao, SystemGroupData> implements SystemGroupDataService {

    @Resource
    private SystemGroupDataDao dao;

    @Autowired
    private SystemFormTempService systemFormTempService;

    @Autowired
    private SystemAttachmentService systemAttachmentService;

    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @author Mr.Zhang
    * @since 2020-05-15
    * @return List<SystemGroupData>
    */
    @Override
    public List<SystemGroupData> getList(SystemGroupDataSearchRequest request, PageParamRequest pageParamRequest) {
        //分页对象
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //带 SystemGroupData 类的多条件查询
        LambdaQueryWrapper<SystemGroupData> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        //实例化-组合数据-对象
        SystemGroupData model = new SystemGroupData();
        BeanUtils.copyProperties(request, model);

        //设置查询条件
        lambdaQueryWrapper.setEntity(model);
        lambdaQueryWrapper.orderByAsc(SystemGroupData::getSort).orderByAsc(SystemGroupData::getId);

        //返回数据
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 保存数据
     * @param systemGroupDataRequest SystemGroupDataRequest 数据保存
     * @author Mr.Zhang
     * @since 2020-04-13
     * @return bool
     */
    @Override
    public boolean create(SystemGroupDataRequest systemGroupDataRequest) {
        //检测form表单，并且返回需要添加的数据
        systemFormTempService.checkForm(systemGroupDataRequest.getForm());

        SystemGroupData systemGroupData = new SystemGroupData();
        systemGroupData.setGid(systemGroupDataRequest.getGid());

        String value = JSONObject.toJSONString(systemGroupDataRequest.getForm());
        value = systemAttachmentService.clearPrefix(value);

        systemGroupData.setValue(value);

        systemGroupData.setSort(systemGroupDataRequest.getForm().getSort());
        systemGroupData.setStatus(systemGroupDataRequest.getForm().getStatus());
        return save(systemGroupData);
    }

    /**
     * 修改组合数据详情表
     * @param id integer id
     * @param request 修改参数
     * @author Mr.Zhang
     * @since 2020-05-15
     * @return bool
     */
    @Override
    public boolean update(Integer id, SystemGroupDataRequest request) {
        //检测form表单，并且返回需要添加的数据
        systemFormTempService.checkForm(request.getForm());

        SystemGroupData systemGroupData = new SystemGroupData();
        systemGroupData.setId(id);
        systemGroupData.setGid(request.getGid());

        String value = JSONObject.toJSONString(request.getForm());
        value = systemAttachmentService.clearPrefix(value);
        systemGroupData.setValue(value);

        systemGroupData.setSort(request.getForm().getSort());
        systemGroupData.setStatus(request.getForm().getStatus());
        return updateById(systemGroupData);
    }

    /**
     * 通过gid获取列表 推荐二开使用
     * @param gid Integer group id
     * @author Mr.Zhang
     * @since 2020-05-15
     * @return List<T>
     */
    @Override
    public <T> List<T> getListByGid(Integer gid, Class<T> cls) {
        SystemGroupDataSearchRequest systemGroupDataSearchRequest = new SystemGroupDataSearchRequest();
        systemGroupDataSearchRequest.setGid(gid);
        systemGroupDataSearchRequest.setStatus(true);
        List<SystemGroupData> list = getList(systemGroupDataSearchRequest, new PageParamRequest());

        List<T> arrayList = new ArrayList<>();
        if(list.size() < 1){
            return null;
        }

        for (SystemGroupData systemGroupData : list) {
            JSONObject jsonObject = JSONObject.parseObject(systemGroupData.getValue());
            List<SystemFormItemCheckRequest> systemFormItemCheckRequestList = CrmebUtil.jsonToListClass(jsonObject.getString("fields"), SystemFormItemCheckRequest.class);
            if(systemFormItemCheckRequestList.size() < 1){
                continue;
            }
            HashMap<String, Object> map = new HashMap<>();
            T t;
            for (SystemFormItemCheckRequest systemFormItemCheckRequest : systemFormItemCheckRequestList) {
                map.put(systemFormItemCheckRequest.getName(), systemFormItemCheckRequest.getValue());
            }
            map.put("id", systemGroupData.getId());
            t = CrmebUtil.mapToObj(map, cls);
            arrayList.add(t);
        }

        return arrayList;
    }

    @Override
    public List<HashMap<String, Object>> getListMapByGid(Integer gid) {
        //实例化-组合数据-请求对象
        SystemGroupDataSearchRequest systemGroupDataSearchRequest = new SystemGroupDataSearchRequest();

        //赋值
        systemGroupDataSearchRequest.setGid(gid);
        systemGroupDataSearchRequest.setStatus(true);

        //得到数据
        List<SystemGroupData> list = getList(systemGroupDataSearchRequest, new PageParamRequest());

        //创建list集合map类型的对象-用于存放处理组合数据list之后的数据
        List<HashMap<String, Object>> arrayList = new ArrayList<>();

        //验证非空
        if(list==null || list.size() < 1){
            return null;
        }

        //循环处理
        for (SystemGroupData systemGroupData : list) {
            //得到组合数据值json对象
            JSONObject jsonObject = JSONObject.parseObject(systemGroupData.getValue());
            //转成list
            List<SystemFormItemCheckRequest> systemFormItemCheckRequestList = CrmebUtil.jsonToListClass(jsonObject.getString("fields"), SystemFormItemCheckRequest.class);
            //验证非空
            if(systemFormItemCheckRequestList==null||systemFormItemCheckRequestList.size() < 1){
                continue;
            }

            //定义map-用于存放配置表表单字段明细
            HashMap<String, Object> map = new HashMap<>();
            for (SystemFormItemCheckRequest systemFormItemCheckRequest : systemFormItemCheckRequestList) {
                map.put(systemFormItemCheckRequest.getName(), systemFormItemCheckRequest.getValue());
            }

            //放入groupid
            map.put("id", systemGroupData.getId());

            //添加到list
            arrayList.add(map);
        }

        //返回数据
        return arrayList;
    }

    /**
     * 通过gid获取列表
     * @param groupDataId Integer group id
     * @author Mr.Zhang
     * @since 2020-05-15
     * @return <T>>
     */
    public <T> T getNormalInfo(Integer groupDataId, Class<T> cls){
        SystemGroupData systemGroupData = getById(groupDataId);
        if(null == systemGroupData || !systemGroupData.getStatus()){
            return null;
        }

        JSONObject jsonObject = JSONObject.parseObject(systemGroupData.getValue());
        List<SystemFormItemCheckRequest> systemFormItemCheckRequestList = CrmebUtil.jsonToListClass(jsonObject.getString("fields"), SystemFormItemCheckRequest.class);
        if(systemFormItemCheckRequestList.size() < 1){
            return null;
        }
        HashMap<String, Object> map = new HashMap<>();
        T t;
        for (SystemFormItemCheckRequest systemFormItemCheckRequest : systemFormItemCheckRequestList) {
            map.put(systemFormItemCheckRequest.getName(), systemFormItemCheckRequest.getValue());
        }
        map.put("id", systemGroupData.getId());
        t = CrmebUtil.mapToObj(map, cls);

        return t;
    }

    /**
     * 获取个人中心菜单
     * @return HashMap<String, Object>
     */
    @Override
    public HashMap<String, Object> getMenuUser() {
        //实例化-map-对象
        HashMap<String, Object> map = new HashMap<>();
        //得到-个人中心菜单-数据
        List<HashMap<String, Object>> list=getListMapByGid(SysGroupDataConstants.GROUP_DATA_ID_USER_CENTER_MENU);

        //存放到map
        map.put("routine_my_menus", list);
        map.put("routine_my_banner", getListMapByGid(SysGroupDataConstants.GROUP_DATA_ID_USER_CENTER_BANNER));
        map.put("from_menu",getListMapByGid(SysGroupDataConstants.GROUP_DATA_ID_FROM_MENU));
        return map;
    }

}

