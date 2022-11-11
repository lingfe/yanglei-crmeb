package com.zbkj.crmeb.data.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.constants.Constants;
import com.utils.RedisUtil;
import com.zbkj.crmeb.data.dao.BusinessTypeDao;
import com.zbkj.crmeb.data.model.BusinessType;
import com.zbkj.crmeb.data.service.BusinessTypeService;
import com.zbkj.crmeb.data.vo.BusinessTypeVo;
import com.zbkj.crmeb.system.model.SystemCity;
import com.zbkj.crmeb.system.vo.SystemCityTreeVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 经营类型表-service层接口-实现类
 * @author: 零风
 * @CreateDate: 2021/12/30 14:57
 */
@Service
public class BusinessTypeServiceImpl extends ServiceImpl<BusinessTypeDao, BusinessType> implements BusinessTypeService {

    @Resource
    private BusinessTypeDao dao;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public Object getListTree() {
        //先-读取缓存
        Object cityList = redisUtil.get(Constants.DATA_BUSINESS_TYPE);
        if(cityList != null){
            return cityList;
        }

        //循环数据，把数据对象变成带list结构的vo
        List<BusinessTypeVo> treeList = new ArrayList<>();

        //定义查询对象
        LambdaQueryWrapper<BusinessType> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select(BusinessType::getId, BusinessType::getPid, BusinessType::getTitle);
        lambdaQueryWrapper.eq(BusinessType::getIsShow, true);

        //得到数据
        List<BusinessType> allTree = dao.selectList(lambdaQueryWrapper);
        if (allTree == null) return null;

        //循环处理
        for (BusinessType businessType : allTree) {
            BusinessTypeVo businessTypeVo = new BusinessTypeVo();
            BeanUtils.copyProperties(businessType, businessTypeVo);
            treeList.add(businessTypeVo);
        }

        //再次处理
        Map<Integer, BusinessTypeVo> map = new HashMap<>();
        //cityId 为 key 存储到map 中
        for (BusinessTypeVo businessTypeVo : treeList) {
            map.put(businessTypeVo.getId(), businessTypeVo);
        }

        //递归
        List<BusinessTypeVo> list = new ArrayList<>();
        for (BusinessTypeVo tree : treeList) {
            //子集ID返回对象，有则添加。
            BusinessTypeVo tree1 = map.get(tree.getPid());
            if (tree1 != null) {
                tree1.getChild().add(tree);
            } else {
                list.add(tree);
            }
        }

        //放入缓存
        if(list == null || list.size() <=0) list = null;
        redisUtil.set(Constants.DATA_BUSINESS_TYPE, list);
        return list;
    }
}
