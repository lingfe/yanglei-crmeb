package com.zbkj.crmeb.creator.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.common.PageParamRequest;
import com.constants.CategoryConstants;
import com.constants.Constants;
import com.constants.SysGroupDataConstants;
import com.exception.CrmebException;
import com.utils.RedisUtil;
import com.utils.RestTemplateUtil;
import com.utils.lingfe.images.ImageHttpGet;
import com.zbkj.crmeb.category.service.CategoryService;
import com.zbkj.crmeb.creator.model.UserLike;
import com.zbkj.crmeb.creator.request.IndexDataRequest;
import com.zbkj.crmeb.creator.request.IndexSearchRequest;
import com.zbkj.crmeb.creator.request.InfoCreatorSearchRequest;
import com.zbkj.crmeb.creator.response.*;
import com.zbkj.crmeb.creator.service.CreatorIndexService;
import com.zbkj.crmeb.creator.service.CreatorUserService;
import com.zbkj.crmeb.creator.service.UserLikeService;
import com.zbkj.crmeb.front.request.PublicUserLoginRequest;
import com.zbkj.crmeb.front.request.UserBindingPhoneUpdateRequest;
import com.zbkj.crmeb.front.response.LoginResponse;
import com.zbkj.crmeb.front.service.LoginService;
import com.zbkj.crmeb.system.model.SystemAttachment;
import com.zbkj.crmeb.system.request.SystemAttachmentSearchRequest;
import com.zbkj.crmeb.system.service.SystemAttachmentService;
import com.zbkj.crmeb.system.service.SystemConfigService;
import com.zbkj.crmeb.system.service.SystemGroupDataService;
import com.zbkj.crmeb.upload.vo.FileResultVo;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 首页相关接口-service接口实现
 * @author: 零风
 * @CreateDate: 2022/1/11 10:59
 */
@Service
public class CreatorIndexServiceImpl implements CreatorIndexService {

    private static final Logger logger = LoggerFactory.getLogger(CreatorIndexServiceImpl.class);

    @Autowired
    private SystemGroupDataService systemGroupDataService;

    @Autowired
    private UserService userService;

    @Autowired
    private SystemAttachmentService systemAttachmentService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RestTemplateUtil restTemplateUtil;

    @Autowired
    private LoginService loginService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private UserLikeService userLikeService;

    @Autowired
    private CreatorUserService userCreatorService;

    @Override
    public IndexDataResponse indexData(IndexDataRequest request) {
        //实例化响应对象
        IndexDataResponse response=new IndexDataResponse();

        //首页banner滚动图
        if(request.getGidIndexLbt() == null||request.getGidIndexLbt()<=0) request.setGidIndexLbt(SysGroupDataConstants.GROUP_DATA_ID_PUBLIC_LBT);//默认
        response.setBannerList(systemGroupDataService.getListMapByGid(request.getGidIndexLbt()));

        //首页热门搜索创作者
        response.setUserList(this.getIndexRemenUserList(null));

        //首页推荐图片列表
        LambdaQueryWrapper<SystemAttachment> lqwSystemAttachment=new LambdaQueryWrapper<>();
        lqwSystemAttachment.eq(SystemAttachment::getPid,999);
        lqwSystemAttachment.orderByDesc(SystemAttachment::getAttId);
        if(request.getTypeId() == null || request.getTypeId()<=0)request.setTypeId(CategoryConstants.CATEGORY_ID_INDEXTUIJIAN);
        lqwSystemAttachment.last("limit 24");
        List<SystemAttachment> systemAttachmentList=systemAttachmentService.list(lqwSystemAttachment);
        response.setSystemAttachmentList(systemAttachmentList);

        //返回响应数据
        return response;
    }

    @Override
    public List<User> getIndexRemenUserList(String searchKey) {
        LambdaQueryWrapper<User> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select(User::getUid,User::getNickname,User::getRealName,User::getAvatar,User::getCode);
        if(StrUtil.isNotBlank(searchKey)&&StringUtils.isNumeric(searchKey)){
            lambdaQueryWrapper.eq(User::getCode,searchKey);
        }else if(StrUtil.isNotBlank(searchKey)){
            lambdaQueryWrapper.eq(User::getNickname,searchKey);
        }
        lambdaQueryWrapper.orderByAsc(User::getUid);
        lambdaQueryWrapper.last("limit 10");
        List<User> userList=userService.list(lambdaQueryWrapper);
        return userList;
    }

    @Override
    public IndexSearchResponse indexSearch(IndexSearchRequest request) {
        //实例化响应对象
        IndexSearchResponse indexSearchResponse=new IndexSearchResponse();
        List<RemenUserResponse> responseList=new ArrayList<>();

        //热门创作者
        List<User> userList = this.getIndexRemenUserList(request.getKeywords());
        if(userList == null || userList.size()<=0){
            userList = this.getIndexRemenUserList(null);
        }else{
            indexSearchResponse.setIsSearchOk(Boolean.TRUE);
        }

        //循环处理
        SystemAttachmentSearchRequest systemAttachmentSearchRequest=new SystemAttachmentSearchRequest();
        PageParamRequest pageParamRequest=new PageParamRequest();
        pageParamRequest.setLimit(3);
        for (User user: userList) {
            RemenUserResponse response=new RemenUserResponse();
            CreatorDataResponse creatorDataResponse = userCreatorService.getCreatorDataResponse(user);
            response.setCreatorData(creatorDataResponse);

            //得到默认作品信息
            systemAttachmentSearchRequest.setUid(user.getUid());
            List<SystemAttachment> defaultWorksList = systemAttachmentService.getList(systemAttachmentSearchRequest,pageParamRequest);
            response.setWorksList(defaultWorksList);

            //添加到集合
            responseList.add(response);
        }

        //返回List
        indexSearchResponse.setData(responseList);
        return indexSearchResponse;
    }

    @Override
    public InfoCreatorHomeResponse infoCreatorHome(InfoCreatorSearchRequest request) {
        //实例化响应对象
        InfoCreatorHomeResponse response=new InfoCreatorHomeResponse();

        //得到创作者用户信息
        User user=userService.getUserException(request.getUid());
        CreatorDataResponse creatorDataResponse = userCreatorService.getCreatorDataResponse(user);
        response.setCreatorData(creatorDataResponse);

        //得到作品分类信息
        List<Map<String,Object>> mapList=userCreatorService.getWorksTypeList(user.getUid(),Boolean.FALSE);
        response.setTypeList(mapList);

        //得到默认作品信息
        SystemAttachmentSearchRequest systemAttachmentSearchRequest=new SystemAttachmentSearchRequest();
        systemAttachmentSearchRequest.setUid(user.getUid());
        List<SystemAttachment> defaultWorksList = systemAttachmentService.getList(systemAttachmentSearchRequest,new PageParamRequest());
        response.setDefaultWorksList(defaultWorksList);

        //返回
        return response;
    }

    @Override
    public List<SystemAttachment> whereCategoryScreenCreatorWorksList(InfoCreatorSearchRequest request, PageParamRequest pageParamRequest) {
        SystemAttachmentSearchRequest systemAttachmentSearchRequest=new SystemAttachmentSearchRequest();
        systemAttachmentSearchRequest.setUid(request.getUid());
        systemAttachmentSearchRequest.setPid(request.getPid());
        List<SystemAttachment> defaultWorksList = systemAttachmentService.getList(systemAttachmentSearchRequest,pageParamRequest);
        return defaultWorksList;
    }

    @Override
    public FileResultVo imgUrlSaveFiles(String imgUrl,String folder,Integer pid) {
        //得到上传图片地址
        String uploadPath = systemConfigService.getValueByKey(Constants.UPLOAD_ROOT_PATH_CONFIG_KEY);
        //得到图片前缀
        String imgPrefix = "https://adminjcx.gzsskj.cn";

        //验证非空
        if(imgUrl==null||imgUrl.length()<=0){
            throw new CrmebException("图片url不能为空！");
        }

        //验证是否是本地url
        //处理图片路径-并上传
        FileResultVo fileResultVo=null;
        if(imgPrefix.indexOf(imgUrl)!=-1){
            fileResultVo=new FileResultVo();
            fileResultVo.setPrefix(imgPrefix);
            fileResultVo.setPath(uploadPath);
            fileResultVo.setUrl(imgUrl);
            fileResultVo.setFileName("最新下载");
            fileResultVo.setServerPath(imgPrefix);
        }else{
            //得到-文件详情对象
            fileResultVo= ImageHttpGet.getImages(imgUrl,folder, uploadPath,imgPrefix);
        }

        //执行保存文件记录并返回
        systemAttachmentService.create(fileResultVo, pid);
        return fileResultVo;
    }

    @Override
    public LoginResponse zijieAuthorizeProgramLogin(String code, PublicUserLoginRequest request) {
        try {
            //通过code获取access_token
            String programAppId = systemConfigService.getValueByKey("zhijie_appid");
            if(StringUtils.isBlank(programAppId)){
                throw new CrmebException("字节小程序appId未设置");
            }

            //验证zhijie_appsecret
            String programAppSecret = systemConfigService.getValueByKey("zhijie_appsecret");
            if(StringUtils.isBlank(programAppSecret)){
                throw new CrmebException("字节小程序secret未设置");
            }

            //拼接请求https://developer.toutiao.com/api/apps/v2/jscode2session
            String url = new StringBuffer(Constants.ZIJIE_APl_URL).append(Constants.ZIJIE_API_GET_ACCESS_TOKEN).toString();
            Map<String,Object> map=new HashMap<>();
            map.put("appid",programAppId);
            map.put("secret",programAppSecret);
            map.put("code",code);
            String result = restTemplateUtil.postMapData(url,map);
            JSONObject jsonObject=JSONObject.parseObject(result);
            JSONObject data=jsonObject.getJSONObject("data");

            //检测token是否存在
            String openid=data.get("openid").toString();
            request.setOpenid(openid);
            request.setTokenType(9);
            return loginService.publicUserLogin(request);
        }catch (Exception e){
            e.printStackTrace();
            throw new CrmebException(e.getMessage());
        }
    }

    @Override
    public boolean bindPhone(UserBindingPhoneUpdateRequest request) {
        //检测验证码
        checkValidateCode(request.getPhone(), request.getCaptcha());

        //删除验证码
        redisUtil.remove(userService.getValidateCodeRedisKey(request.getPhone()));

        //检测当前手机号是否已经是账号
        User user = userService.getUserByAccount(request.getPhone());
        if (null != user) {
            throw new CrmebException("此手机号码已被注册");
        }

        //查询手机号信息
        User bindUser = userService.getInfoException();
        bindUser.setAccount(request.getPhone());
        bindUser.setPhone(request.getPhone());
        return userService.updateById(bindUser);
    }

    /**
     * 检测手机验证码
     * @author Mr.Zhang
     * @since 2020-04-29
     */
    private void checkValidateCode(String phone, String value) {
        Object validateCode = redisUtil.get(userService.getValidateCodeRedisKey(phone));
        if (validateCode == null) {
            throw new CrmebException("验证码已过期");
        }

        if (!validateCode.toString().equals(value)) {
            throw new CrmebException("验证码错误");
        }
    }

    @Override
    public Object attInfo(Integer attid) {
        Map<String,Object> map=new HashMap<>();
        SystemAttachment systemAttachment = systemAttachmentService.getById(attid);
        if(systemAttachment == null)throw new CrmebException("内容不存在!");
        map.put("attid",systemAttachment.getAttId());
        map.put("pid",systemAttachment.getPid());
        map.put("url",systemAttachment.getSattDir());
        map.put("isTop",systemAttachment.getIsTop());
        Integer uid = userService.getUserId();
        if(uid<=0){
            map.put("isLike",false);
        }else{
            UserLike userLike = this.getUserLike(attid, uid);
            if(userLike == null)userLike=new UserLike();
            map.put("isLike",userLike.getIsLike());
        }
        return map;
    }

    @Override
    public boolean download(Integer attid) {
        UserLike systemAttachmentYw=new UserLike();
        systemAttachmentYw.setYwType(1);
        systemAttachmentYw.setAttid(attid);
        systemAttachmentYw.setUid(userService.getUserId());
        return userLikeService.save(systemAttachmentYw);
    }

    @Override
    public boolean like(Integer attid) {
        Integer uid = userService.getUserIdException();
        UserLike userLike = this.getUserLike(attid, uid);
        if(userLike!=null){
            if(userLike.getIsLike())userLike.setIsLike(Boolean.FALSE);
            else userLike.setIsLike(Boolean.TRUE);
            userLikeService.updateById(userLike);
        }else{
            userLike=new UserLike();
            userLike.setYwType(0);
            userLike.setAttid(attid);
            userLike.setUid(uid);
            userLike.setIsLike(Boolean.TRUE);
            userLikeService.save(userLike);
        }

        //返回
        return true;
    }

    //得到用户点赞的信息
    private UserLike getUserLike(Integer attid, Integer uid) {
        LambdaQueryWrapper<UserLike> lqw=new LambdaQueryWrapper<>();
        lqw.eq(UserLike::getYwType,0);
        lqw.eq(UserLike::getUid, uid);
        lqw.eq(UserLike::getAttid, attid);
        lqw.last("limit 1");
        UserLike userLike = userLikeService.getOne(lqw);
        return userLike;
    }

    @Override
    public List<SystemAttachment> likeMy() {
        Integer uid = userService.getUserIdException();
        LambdaQueryWrapper<UserLike> lqw=new LambdaQueryWrapper<>();
        lqw.eq(UserLike::getYwType,0);
        lqw.eq(UserLike::getUid, uid);
        List<UserLike> userLikeList = userLikeService.list(lqw);
        List<Integer> attidList = userLikeList.stream().map(UserLike::getAttid).collect(Collectors.toList());
        List<SystemAttachment> systemAttachmentList = systemAttachmentService.listByIds(attidList);
        return systemAttachmentList;
    }
}

