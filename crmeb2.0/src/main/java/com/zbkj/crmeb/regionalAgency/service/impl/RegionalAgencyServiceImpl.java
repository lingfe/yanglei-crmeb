package com.zbkj.crmeb.regionalAgency.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CommonPage;
import com.common.PageParamRequest;
import com.exception.CrmebException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.regionalAgency.dao.RegionalAgencyDao;
import com.zbkj.crmeb.regionalAgency.model.RegionalAgency;
import com.zbkj.crmeb.regionalAgency.request.RegionalAgencySearchRequest;
import com.zbkj.crmeb.regionalAgency.response.RegionalAgencyResponse;
import com.zbkj.crmeb.regionalAgency.service.RegionalAgencyService;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.model.UserAddress;
import com.zbkj.crmeb.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 区域代理表-service层接口-实现类
 * @author: 零风
 * @CreateDate: 2021/11/6 10:30
 */
@Service
public class RegionalAgencyServiceImpl extends ServiceImpl<RegionalAgencyDao, RegionalAgency> implements RegionalAgencyService {

    @Resource
    private RegionalAgencyDao dao;

    @Autowired
    private UserService userService;

    @Override
    public String getRaName(Integer id) {
        RegionalAgency regionalAgency = dao.selectById(id);
        if(regionalAgency == null) return "区域代理已不存在！";
        return regionalAgency.getRaName();
    }

    @Override
    public RegionalAgency getRegionalAgencyException(String id) {
        RegionalAgency regionalAgency=dao.selectById(id);
        if(regionalAgency == null)throw new CrmebException("区域代理信息不存在或ID错误!");
        return regionalAgency;
    }

    @Override
    public boolean save(RegionalAgency entity) {
        //验证-是否存在该区域代理
        UserAddress userAddress=new UserAddress();
        //userAddress.setUid(entity.getUid());
        userAddress.setCity(entity.getCity());
        userAddress.setProvince(entity.getProvince());
        userAddress.setDistrict(entity.getDistrict());
        if(this.getRAUid(userAddress)!=null){
            throw new CrmebException("该区域代理已存在或者该代理用户已有代理区域！");
        }

        //执行保存
        return super.save(entity);
    }

    @Override
    public List<RegionalAgency> getWhereUserID(Integer uid){
        //定义查询对象
        LambdaQueryWrapper<RegionalAgency> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(RegionalAgency::getUid,uid);

        //得到数据
        List<RegionalAgency> regionalAgencyList = dao.selectList(lambdaQueryWrapper);
        if(regionalAgencyList == null || regionalAgencyList.size() == 0){
            return null;
        }

        //返回
        return regionalAgencyList;
    }

    @Override
    public RegionalAgency getRAUid(UserAddress userAddress) {
        //定义查询对象
        LambdaQueryWrapper<RegionalAgency> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        //省、市、区
        lambdaQueryWrapper.eq(RegionalAgency::getCity,userAddress.getCity());
        lambdaQueryWrapper.eq(RegionalAgency::getProvince,userAddress.getProvince());
        String district=userAddress.getDistrict()==null?"":userAddress.getDistrict();
        String[] districtSplit = district.split(",");
        Boolean bl=true;
        for (String str: districtSplit) {
            if(bl){
                lambdaQueryWrapper.like(RegionalAgency::getDistrict, str);
                bl=false;
            }else{
                lambdaQueryWrapper.or().like(RegionalAgency::getDistrict, str);
            }
        }

        //只取一条
        lambdaQueryWrapper.last("LIMIT 0,1");

        //得到数据
        RegionalAgency regionalAgency = dao.selectOne(lambdaQueryWrapper);
        if(regionalAgency == null){
            return null;
        }

        //返回
        return regionalAgency;
    }

    @Override
    public PageInfo<RegionalAgencyResponse> getList(RegionalAgencySearchRequest request, PageParamRequest pageParamRequest) {
        //分页
        Page<RegionalAgency> regionalAgencyPage= PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //定义查询对象
        LambdaQueryWrapper<RegionalAgency> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        //条件-关键字搜索
        if(StringUtils.isNotBlank(request.getKeywords())){
            lambdaQueryWrapper.and(i -> i.like(RegionalAgency::getDistrict, request.getKeywords())
                    .or().like(RegionalAgency::getId, request.getKeywords()));
        }

        //排序
        lambdaQueryWrapper.orderByDesc(RegionalAgency::getId);

        //得到-数据
        List<RegionalAgency> regionalAgencyList= dao.selectList(lambdaQueryWrapper);
        List<RegionalAgencyResponse> regionalAgencyResponseList=new ArrayList<>();
        for (RegionalAgency record:regionalAgencyList) {
            //实例化-区域代理-响应对象
            RegionalAgencyResponse regionalAgencyResponse=new RegionalAgencyResponse();
            BeanUtils.copyProperties(record, regionalAgencyResponse);

            //得到-用户名称
            User user = userService.getById(record.getUid());
            if(user == null) {
                regionalAgencyResponse.setNickname("用户已不存在！");
            }else{
                regionalAgencyResponse.setNickname(user.getNickname());
            }

            //添加到-区域代理list响应集合
            regionalAgencyResponseList.add(regionalAgencyResponse);
        }

        //返回
        return CommonPage.copyPageInfo(regionalAgencyPage, regionalAgencyResponseList);
    }
}
