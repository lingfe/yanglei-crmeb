package com.zbkj.crmeb.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.constants.SysConfigConstants;
import com.constants.SysGroupDataConstants;
import com.exception.CrmebException;
import com.github.pagehelper.PageHelper;
import com.utils.CrmebUtil;
import com.utils.ValidateFormUtil;
import com.zbkj.crmeb.authorization.manager.TokenManager;
import com.zbkj.crmeb.authorization.model.TokenModel;
import com.zbkj.crmeb.system.dao.SystemAdminDao;
import com.zbkj.crmeb.system.model.SystemAdmin;
import com.zbkj.crmeb.system.model.SystemRole;
import com.zbkj.crmeb.system.request.SystemAdminAddRequest;
import com.zbkj.crmeb.system.request.SystemAdminLoginNoYzmRequest;
import com.zbkj.crmeb.system.request.SystemAdminRequest;
import com.zbkj.crmeb.system.request.SystemRoleSearchRequest;
import com.zbkj.crmeb.system.response.SystemAdminResponse;
import com.zbkj.crmeb.system.response.SystemGroupDataAdminLoginBannerResponse;
import com.zbkj.crmeb.system.service.SystemAdminService;
import com.zbkj.crmeb.system.service.SystemConfigService;
import com.zbkj.crmeb.system.service.SystemGroupDataService;
import com.zbkj.crmeb.system.service.SystemRoleService;
import com.zbkj.crmeb.user.model.UserToken;
import com.zbkj.crmeb.user.service.UserTokenService;
import com.zbkj.crmeb.validatecode.service.ValidateCodeService;
import com.zbkj.crmeb.wechat.response.WeChatAuthorizeLoginGetOpenIdResponse;
import com.zbkj.crmeb.wechat.service.WeChatService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * SystemAdminServiceImpl ????????????
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB???????????????????????????????????? ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB????????????????????????????????????????????????CRMEB????????????
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Service
public class SystemAdminServiceImpl extends ServiceImpl<SystemAdminDao, SystemAdmin> implements SystemAdminService {

    @Resource
    private SystemAdminDao dao;

    @Autowired
    private SystemRoleService systemRoleService;

    @Resource
    private TokenManager tokenManager;

    @Autowired
    private UserTokenService userTokenService;

    @Autowired
    private WeChatService weChatService;

    @Autowired
    private ValidateCodeService validateCodeService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private SystemGroupDataService systemGroupDataService;


    @Override
    public List<SystemAdminResponse> getList(SystemAdminRequest request, PageParamRequest pageParamRequest){
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //???SystemAdminRequest?????????????????????
        LambdaQueryWrapper<SystemAdmin> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        SystemAdmin systemAdmin = new SystemAdmin();
        BeanUtils.copyProperties(request,systemAdmin);
//        lambdaQueryWrapper.setEntity(systemAdmin);
        if(StringUtils.isNotBlank(systemAdmin.getAccount())){
            lambdaQueryWrapper.eq(SystemAdmin::getAccount, systemAdmin.getAccount());
        }

        if(null != systemAdmin.getId()){
            lambdaQueryWrapper.eq(SystemAdmin::getId, systemAdmin.getId());
        }
        if(null != systemAdmin.getIsDel()){
            lambdaQueryWrapper.eq(SystemAdmin::getIsDel, systemAdmin.getIsDel());
        }
        if(StringUtils.isNotBlank(systemAdmin.getLastIp())){
            lambdaQueryWrapper.eq(SystemAdmin::getLastIp, systemAdmin.getLastIp());
        }

        if(null != systemAdmin.getLevel()){
            lambdaQueryWrapper.eq(SystemAdmin::getLevel, systemAdmin.getLevel());
        }
        if(null != systemAdmin.getLoginCount()){
            lambdaQueryWrapper.eq(SystemAdmin::getLoginCount, systemAdmin.getLoginCount());
        }
        if(StringUtils.isNotBlank(systemAdmin.getRealName())){
            lambdaQueryWrapper.like(SystemAdmin::getRealName, systemAdmin.getRealName());
            lambdaQueryWrapper.or().like(SystemAdmin::getAccount, systemAdmin.getRealName());
        }
        if(StringUtils.isNotBlank(systemAdmin.getRoles())){
            lambdaQueryWrapper.eq(SystemAdmin::getRoles, systemAdmin.getRoles());
        }
        if(null != systemAdmin.getStatus()){
            lambdaQueryWrapper.eq(SystemAdmin::getStatus, systemAdmin.getStatus());
        }
        List<SystemAdmin> systemAdmins = dao.selectList(lambdaQueryWrapper);
        List<SystemAdminResponse> systemAdminResponses = new ArrayList<>();
        PageParamRequest pageRole = new PageParamRequest();
        pageRole.setLimit(999);
        List<SystemRole> roleList = systemRoleService.getList(new SystemRoleSearchRequest(), pageRole);
        for (SystemAdmin admin : systemAdmins) {
            SystemAdminResponse sar = new SystemAdminResponse();
            BeanUtils.copyProperties(admin, sar);

            sar.setLastTime(admin.getUpdateTime());
            if(StringUtils.isBlank(admin.getRoles())) continue;
            List<Integer> roleIds = CrmebUtil.stringToArrayInt(admin.getRoles());
            List<String> roleNames = new ArrayList<>();
            for (Integer roleId : roleIds) {
                List<SystemRole> hasRoles = roleList.stream().filter(e -> e.getId().equals(roleId)).collect(Collectors.toList());
                if(hasRoles.size()> 0){
                    roleNames.add(hasRoles.stream().map(SystemRole::getRoleName).collect(Collectors.joining(",")));
                }
            }
            sar.setRoleNames(StringUtils.join(roleNames,","));
            systemAdminResponses.add(sar);
        }
        return systemAdminResponses;
    }

    @Override
    public SystemAdminResponse login(SystemAdminLoginNoYzmRequest systemAdminLoginRequest, String ip) throws Exception {
        // ???????????????
        //ValidateCode validateCode = new ValidateCode(systemAdminLoginRequest.getKey(), systemAdminLoginRequest.getCode());
        //boolean codeCheckResult = validateCodeService.check(validateCode);
        //if(!codeCheckResult) throw new CrmebException("??????????????????");

        //???????????????-????????????
        LambdaQueryWrapper<SystemAdmin> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SystemAdmin::getAccount, systemAdminLoginRequest.getAccount());
        SystemAdmin systemAdmin = dao.selectOne(lambdaQueryWrapper);
        if(null == systemAdmin){
            throw new CrmebException("???????????????");
        }

        // base64????????????????????????
        String encryptPassword = CrmebUtil.encryptPassword(systemAdminLoginRequest.getPwd(), systemAdminLoginRequest.getAccount());
        if(!systemAdmin.getPwd().equals(encryptPassword)){
            throw new CrmebException("???????????????????????????");
        }
        if(!systemAdmin.getStatus()){
            throw new CrmebException("?????????????????????");
        }
        if(systemAdmin.getIsDel()){
            throw new CrmebException("?????????????????????");
        }

        //??????-token
        TokenModel tokenModel = tokenManager.createToken(systemAdmin.getAccount(), systemAdmin.getId().toString(), Constants.TOKEN_REDIS);

        //?????????-???????????????????????????
        SystemAdminResponse systemAdminResponse = new SystemAdminResponse();
        systemAdminResponse.setToken(tokenModel.getToken());
        BeanUtils.copyProperties(systemAdmin, systemAdminResponse);

        //????????????????????????
        systemAdmin.setLoginCount(systemAdmin.getLoginCount() + 1);
        //??????????????????IP
        systemAdmin.setLastIp(ip);

        //??????-??????
        updateById(systemAdmin);

        //??????-???????????????????????????
        return systemAdminResponse;
    }

    @Override
    public Boolean logout(String token) throws Exception {
        tokenManager.deleteToken(token, Constants.TOKEN_REDIS);
        return true;
    }

    /**
     * ???????????????
     * @param request
     * @return
     * @throws Exception
     */
    @Override
    public SystemAdminResponse getInfo(SystemAdminRequest request) throws Exception {
        //??????-????????????
        QueryWrapper queryWrapper = new QueryWrapper();

        //??????-???????????????id
        queryWrapper.eq("id", request.getId());

        //?????????????????????
        SystemAdmin systemAdmin = dao.selectOne(queryWrapper);

        //??????-??????
        if(null == systemAdmin || systemAdmin.getId() < 0){
            return null;
        }

        //?????????-???????????????????????????
        SystemAdminResponse systemAdminResponse = new SystemAdminResponse();
        BeanUtils.copyProperties(systemAdmin, systemAdminResponse);
        return systemAdminResponse;
    }

    /**
     * ???????????????
     * @param systemAdminAddRequest
     * @return
     */
    @Override
    public SystemAdminResponse saveAdmin(SystemAdminAddRequest systemAdminAddRequest) {
        try {
            // ???????????????????????????
            Integer result = checkAccount(systemAdminAddRequest.getAccount());
            if (result > 0) {
                throw new CrmebException("??????????????????");
            }

            // ????????????????????????????????????
            if (StrUtil.isNotBlank(systemAdminAddRequest.getPhone())) {
                ValidateFormUtil.isPhoneException(systemAdminAddRequest.getPhone());
            }

            //??????
            SystemAdmin systemAdmin = new SystemAdmin();
            BeanUtils.copyProperties(systemAdminAddRequest, systemAdmin);

            // ???????????????????????????
            String pwd = CrmebUtil.encryptPassword(systemAdmin.getPwd(), systemAdmin.getAccount());
            systemAdminAddRequest.setPwd(pwd); // ???????????????????????????
            systemAdmin.setPwd(pwd);
            SystemAdminResponse systemAdminResponse = new SystemAdminResponse();
            BeanUtils.copyProperties(systemAdminAddRequest, systemAdminResponse);
            if(dao.insert(systemAdmin) <= 0){
                throw new CrmebException("?????????????????????");
            }
            return systemAdminResponse;
        }catch (Exception e){
            throw new CrmebException("????????????????????? " + e.getMessage());
        }

    }

    private Integer checkAccount(String account) {
        LambdaQueryWrapper<SystemAdmin> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SystemAdmin::getAccount, account);
        return dao.selectCount(lambdaQueryWrapper);

    }

    @Override
    public SystemAdminResponse updateAdmin(SystemAdminRequest systemAdminRequest) throws Exception {
        SystemAdminResponse systemAdminResponseExsit = getInfo(systemAdminRequest);
        if(null == systemAdminResponseExsit){
            throw new CrmebException("??????????????????");
        }

        // ????????????????????????????????????
        if (StrUtil.isNotBlank(systemAdminRequest.getPhone())) {
            ValidateFormUtil.isPhoneException(systemAdminRequest.getPhone());
        }

        SystemAdmin systemAdmin = new SystemAdmin();
        BeanUtils.copyProperties(systemAdminRequest, systemAdmin);
        if(null != systemAdminRequest.getPwd()){
            String pwd = CrmebUtil.encryptPassword(systemAdminRequest.getPwd(), systemAdminRequest.getAccount());
            systemAdmin.setPwd(pwd);
        }
        if(dao.updateById(systemAdmin) > 0){
            SystemAdminResponse systemAdminResponse = new SystemAdminResponse();
            BeanUtils.copyProperties(systemAdmin, systemAdminResponse);
            return systemAdminResponse;
        }
        throw new CrmebException("?????????????????????");
    }

    @Override
    public SystemAdminResponse getInfoByToken(String token) throws Exception{
        //??????-token????????????
        Boolean tokenExsit = tokenManager.checkToken(token, Constants.TOKEN_REDIS);
        if(!tokenExsit){
            throw new CrmebException("??????token??????");
        }

        //??????-token???
        TokenModel tokenModel = tokenManager.getToken(token, Constants.TOKEN_REDIS);
        SystemAdminRequest systemAdminRequest = new SystemAdminRequest();
        systemAdminRequest.setId(tokenModel.getUserId());

        //?????????????????????????????????
        return getInfo(systemAdminRequest);
    }

    /**
     * ??????????????????id
     * @author Mr.Zhang
     * @since 2020-04-28
     * @return Integer
     */
    @Override
    public Integer getAdminId() {
        return Integer.parseInt(tokenManager.getLocalInfoException("id"));
    }

    /**
     * ????????????????????????
     * @author Mr.Zhang
     * @since 2020-04-28
     * @return SystemAdmin
     */
    @Override
    public SystemAdmin getInfo() {
        return getInfo(getAdminId());
    }

    /**
     * ??????????????????
     * @author Mr.Zhang
     * @since 2020-04-28
     * @return SystemAdmin
     */
    @Override
    public SystemAdmin getInfo(Integer adminId) {
        return getById(adminId);
    }

    /**
     * ????????????
     * @author Mr.Zhang
     * @since 2020-04-28
     * @return SystemAdmin
     */
    public void bind(String code, Integer adminId) {
        try{
            //??????code??????????????????
            WeChatAuthorizeLoginGetOpenIdResponse response = weChatService.authorizeLogin(code);
            UserToken userToken = userTokenService.getByOpenidAndType(response.getOpenId(), Constants.THIRD_ADMIN_LOGIN_TOKEN_TYPE_PUBLIC);
            if(null == userToken){
                userTokenService.bind(response.getOpenId(), Constants.THIRD_ADMIN_LOGIN_TOKEN_TYPE_PUBLIC, adminId);
            }
        }catch (Exception e){
            throw new CrmebException("???????????????" + e.getMessage());
        }
    }

    /**
     * ???????????????????????????
     * @param id
     * @param status
     * @return
     */
    @Override
    public Boolean updateStatus(Integer id, Boolean status) {
        SystemAdmin systemAdmin = getById(id);
        if (ObjectUtil.isNull(systemAdmin)) {
            throw new CrmebException("???????????????");
        }
        if (systemAdmin.getStatus().equals(status)) {
            return true;
        }
        systemAdmin.setStatus(status);
        return updateById(systemAdmin);
    }

    @Override
    public HashMap<Integer, SystemAdmin> getMapInId(List<Integer> adminIdList) {
        HashMap<Integer, SystemAdmin> map = new HashMap<>();
        if(adminIdList.size() < 1){
            return map;
        }
        LambdaQueryWrapper<SystemAdmin> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.in(SystemAdmin::getId, adminIdList);
        List<SystemAdmin> systemAdminList = dao.selectList(lambdaQueryWrapper);
        if(systemAdminList.size() < 1){
            return map;
        }
        for (SystemAdmin systemAdmin : systemAdminList) {
            map.put(systemAdmin.getId(), systemAdmin);
        }
        return map;
    }

    /**
     * ???????????????????????????????????????
     * @param id ?????????id
     * @return Boolean
     */
    @Override
    public Boolean updateIsSms(Integer id) {
        SystemAdmin systemAdmin = getById(id);
        if (ObjectUtil.isNull(systemAdmin)) {
            throw new CrmebException("????????????????????????!");
        }
        if (StrUtil.isBlank(systemAdmin.getPhone())) {
            throw new CrmebException("?????????????????????????????????!");
        }

        systemAdmin.setIsSms(!systemAdmin.getIsSms());
        return updateById(systemAdmin);
    }

    /**
     * ????????????????????????????????????
     * @return List
     */
    @Override
    public List<SystemAdmin> findIsSmsList() {
        LambdaQueryWrapper<SystemAdmin> lqw = Wrappers.lambdaQuery();
        lqw.eq(SystemAdmin::getStatus, true);
        lqw.eq(SystemAdmin::getIsDel, false);
        lqw.eq(SystemAdmin::getIsSms, true);
        List<SystemAdmin> list = dao.selectList(lqw);
        if (CollUtil.isEmpty(list)) {
            return list;
        }
        return list.stream().filter(i -> StrUtil.isNotBlank(i.getPhone())).collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getLoginPic() {
        //?????????-map??????
        Map<String, Object> map = new HashMap<>();

        //?????????
        map.put("backgroundImage", systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_ADMIN_LOGIN_BACKGROUND_IMAGE));

        //logo
        map.put("logo", systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_ADMIN_LOGIN_LOGO_LEFT_TOP));
        map.put("loginLogo", systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_ADMIN_LOGIN_LOGO_LOGIN));

        //?????????
        List<SystemGroupDataAdminLoginBannerResponse> bannerList = systemGroupDataService.getListByGid(
                SysGroupDataConstants.GROUP_DATA_ID_ADMIN_LOGIN_BANNER_IMAGE_LIST,
                SystemGroupDataAdminLoginBannerResponse.class);
        map.put("banner", bannerList);

        //??????-?????????????????????
        return map;
    }

}

