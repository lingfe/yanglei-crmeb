package com.zbkj.crmeb.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.PageParamRequest;
import com.github.pagehelper.PageHelper;
import com.zbkj.crmeb.system.dao.SystemGroupDao;
import com.zbkj.crmeb.system.model.SystemGroup;
import com.zbkj.crmeb.system.request.SystemGroupSearchRequest;
import com.zbkj.crmeb.system.service.SystemGroupService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * SystemGroupServiceImpl接口实现
 * @author: 零风
 * @CreateDate: 2022/6/9 10:43
 */
@Service
public class SystemGroupServiceImpl extends ServiceImpl<SystemGroupDao, SystemGroup> implements SystemGroupService {

    @Resource
    private SystemGroupDao dao;

    public static void main(String[] args) {
        String lingfe="lingfe:可乐表情包";
        System.out.println(lingfe.substring(7));
    }

    @Override
    public List<SystemGroup> getList(SystemGroupSearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //带 SystemGroup 类的多条件查询
        LambdaQueryWrapper<SystemGroup> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SystemGroup::getIsShow,Boolean.TRUE);

        //关键字
        if(!StringUtils.isBlank(request.getKeywords())){
            lambdaQueryWrapper.eq(SystemGroup::getId,request.getKeywords());
            lambdaQueryWrapper.or().like(SystemGroup::getName, request.getKeywords());
        }

        //排序
        lambdaQueryWrapper.orderByDesc(SystemGroup::getId);
        return dao.selectList(lambdaQueryWrapper);
    }

}

