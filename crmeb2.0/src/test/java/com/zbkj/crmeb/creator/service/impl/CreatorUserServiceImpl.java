package com.zbkj.crmeb.creator.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.exception.CrmebException;
import com.github.pagehelper.PageHelper;
import com.utils.DateUtil;
import com.utils.UploadUtil;
import com.utils.vo.dateLimitUtilVo;
import com.zbkj.crmeb.category.model.Category;
import com.zbkj.crmeb.category.service.CategoryService;
import com.zbkj.crmeb.creator.model.UserProfit;
import com.zbkj.crmeb.creator.request.GetWorksListRequest;
import com.zbkj.crmeb.creator.response.CreatorDataResponse;
import com.zbkj.crmeb.creator.response.CreatorProfitDataResponse;
import com.zbkj.crmeb.creator.response.SystemAttachmentResponse;
import com.zbkj.crmeb.creator.service.UserLikeService;
import com.zbkj.crmeb.creator.service.CreatorUserService;
import com.zbkj.crmeb.creator.service.UserProfitService;
import com.zbkj.crmeb.pub.model.PublicTableField;
import com.zbkj.crmeb.system.model.SystemAttachment;
import com.zbkj.crmeb.system.request.SystemAttachmentSearchRequest;
import com.zbkj.crmeb.system.service.SystemAttachmentService;
import com.zbkj.crmeb.upload.vo.FileResultVo;
import com.zbkj.crmeb.creator.model.UserLike;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * 创作者用户-service接口实现类
 * @author: 零风
 * @CreateDate: 2022/7/15 9:37
 */
@Service
public class CreatorUserServiceImpl implements CreatorUserService {

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SystemAttachmentService systemAttachmentService;

    @Autowired
    private UserLikeService systemAttachmentYwService;

    @Autowired
    private UserProfitService userProfitService;

    @Override
    public List<Map<String, Object>> getWorksType(Boolean isNull) {
        //得到当前登录信息
        Integer uid=userService.getUserIdException();
        return this.getWorksTypeList(uid,isNull);
    }

    @Override
    public List<Map<String,Object>> getWorksTypeList(Integer uid,Boolean isNull) {
        //得到分类信息
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Category::getPid,1089);
        //lqw.eq(Category::getType,2);
        List<Category> categoryList=categoryService.list(lqw);

        //筛选出创作者有作品的分类
        SystemAttachmentSearchRequest systemAttachmentSearchRequest=new SystemAttachmentSearchRequest();
        systemAttachmentSearchRequest.setUid(uid);
        List<Map<String,Object>> mapList=new ArrayList<>();
        Map<String,Object> map=null;
        for (Category category:categoryList) {
            //得到分类,验证是否只查询作品不为空的分类
            if(isNull){
                systemAttachmentSearchRequest.setPid(category.getId());
                map = this.getWorksTypeMap(category, systemAttachmentSearchRequest);
                if(map!=null) mapList.add(map);
            }else{
                map = new HashMap<>();
                map.put("typeName", category.getName()); //分类名称
                map.put("id", category.getId());//分类ID
                mapList.add(map);
            }
        }

        //返回
        return mapList;
    }

    @Override
    public Map<String,Object> getWorksTypeMap(Category category, SystemAttachmentSearchRequest systemAttachmentSearchRequest) {
        //只筛选有作品的分类
        List<SystemAttachment> defaultWorksList = systemAttachmentService.getList(systemAttachmentSearchRequest,new PageParamRequest());
        if(defaultWorksList !=null && defaultWorksList.size()>0){
            Map<String,Object> map=new HashMap<>();
            map.put("typeName", category.getName()); //分类名称
            map.put("id", category.getId());//分类ID
            return map;
        }else{
            return null;
        }
    }

    @Override
    public List<SystemAttachmentResponse> getWorksList(GetWorksListRequest request, PageParamRequest pageParamRequest) {
        Integer uid=userService.getUserIdException();
        SystemAttachmentSearchRequest systemAttachmentSearchRequest=new SystemAttachmentSearchRequest();
        systemAttachmentSearchRequest.setUid(uid);
        systemAttachmentSearchRequest.setPid(request.getType());
        if(request.getStatus()!=null&&request.getStatus()>-1)systemAttachmentSearchRequest.setStatus(request.getStatus());
        List<SystemAttachment> worksList = systemAttachmentService.getList(systemAttachmentSearchRequest,pageParamRequest);
        List<SystemAttachmentResponse> list=new ArrayList<>();
        for (SystemAttachment att:worksList) {
            SystemAttachmentResponse response=new SystemAttachmentResponse();
            response.setUrl(att.getSattDir());
            response.setAttid(att.getAttId());
            Integer sownloads=this.getAttachmentDownloads(uid,1,att.getAttId(),null);
            response.setDownloads(sownloads);
            list.add(response);
        }
        return list;
    }

    @Override
    public Integer getAttachmentDownloads(Integer uid,Integer ywType,Integer attId,String date) {
        LambdaQueryWrapper<UserLike> lqw=new LambdaQueryWrapper<>();
        lqw.eq(UserLike::getUid,uid);
        lqw.eq(UserLike::getYwType,ywType);
        if(attId!=null)lqw.eq(UserLike::getAttid,attId);
        if(null != date){
            dateLimitUtilVo dateLimit = DateUtil.getDateLimit(date);
            lqw.between(PublicTableField::getCreateTime, dateLimit.getStartTime(), dateLimit.getEndTime());
        }
        Integer sownloads = systemAttachmentYwService.count(lqw);
        return sownloads;
    }

    @Override
    public FileResultVo uploadWorks(MultipartFile multipart, String lingfe, Integer pid) throws IOException {
        //得到当前登录创作者ID标识
        Integer uid=userService.getUserIdException();

        //保存文件
        UploadUtil.setModelPath(lingfe);
        FileResultVo file = UploadUtil.file(multipart);
        file.setUid(uid);

        //文件入库
        file.setType(file.getType().replace("image/", ""));
        systemAttachmentService.create(file, pid);
        return file;
    }

    @Override
    public FileResultVo UploadUrl(Integer pid, String url) {
        FileResultVo fileResultVo=new FileResultVo();
        fileResultVo.setUid(userService.getUserIdException());
        fileResultVo.setUrl(url);
        fileResultVo.setFileSize(0L);
        fileResultVo.setType("2");
        fileResultVo.setFileName("URL上传");
        systemAttachmentService.create(fileResultVo, pid);
        return fileResultVo;
    }

    @Override
    public Boolean setSpreadUid(Integer spreadUid) {
        User user=userService.getInfoException();
        if(user.getSpreadUid()!=null&&user.getSpreadUid()>0){
            throw new CrmebException("已绑定推荐人，请联系客服修改！");
        }else{
            user.setSpreadUid(spreadUid);
            return userService.updateById(user);
        }
    }

    @Override
    public Boolean setAlipay(String realName,String alipay){
        User user=userService.getInfoException();
        user.setRealName(realName);
        user.setAlipay(alipay);
        return userService.updateById(user);
    }

    @Override
    public List<CreatorDataResponse> invitationMy(PageParamRequest pageParamRequest) {
        List<CreatorDataResponse> list=new ArrayList<>();
        User user=userService.getInfoException();
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getSpreadUid, user.getUid());
        lambdaQueryWrapper.orderByDesc(User::getCreateTime);
        List<User> userList = userService.list(lambdaQueryWrapper);
        for (User u:userList) {
            CreatorDataResponse response= this.getCreatorDataResponse(u);
            list.add(response);
        }
        return list;
    }

    @Override
    public CreatorDataResponse getCreatorDataResponse(User user) {
        CreatorDataResponse creatorDataResponse=new CreatorDataResponse();
        creatorDataResponse.setAvatar(user.getAvatar());
        creatorDataResponse.setNickname(user.getNickname());
        creatorDataResponse.setCode(user.getCode());
        creatorDataResponse.setGiveCount(0);
        creatorDataResponse.setWorksCount(0);
        creatorDataResponse.setNowMoney(user.getNowMoney());
        if("9527".equals(user.getTagId())){ //创作者专属标签
            creatorDataResponse.setIsCreator(Boolean.TRUE);
        }else{
            creatorDataResponse.setIsCreator(Boolean.FALSE);
        }
        return creatorDataResponse;
    }

    @Override
    public CreatorDataResponse myInfo() {
        User user=userService.getInfoException();
        CreatorDataResponse response= this.getCreatorDataResponse(user);
        return response;
    }

    @Override
    public void creatorProfitComputeTask() {
        //定义过渡变量
        String startTime = DateUtil.addDay(DateUtil.nowDateTime(), -1, Constants.DATE_FORMAT_DATE);  //开始日期
        String endTime = DateUtil.nowDateTime(Constants.DATE_FORMAT);           //结束日期
        StringBuffer date=new StringBuffer(startTime).append(",").append(endTime); //日期字符串拼接
        List<UserProfit> userProfitList=new ArrayList<>();

        //查询创作者数据
        LambdaQueryWrapper<User> userLambdaQueryWrapper=new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getTagId,9527);//创作者专属标签
        userLambdaQueryWrapper.orderByDesc(User::getCreateTime);
        List<User> userList = userService.list(userLambdaQueryWrapper);

        //循环处理
        for (User user:userList) {
            //实例化收益记录
            UserProfit userProfit=new UserProfit();
            userProfit.setUid(user.getUid());
            userProfit.setDateStr(DateUtil.strToDate(DateUtil.getYesterdayStr(), Constants.DATE_FORMAT_DATE).toString());
            userProfit.setUnitPrice(user.getUnitPrice());
            userProfit.setAdNum(0);

            //得到下载量
            Integer downloadNum = this.getAttachmentDownloads(user.getUid(),1,null,date.toString());
            userProfit.setDownloadNum(downloadNum);

            //计算收益
            BigDecimal profit=new BigDecimal(downloadNum).multiply(user.getUnitPrice());
            userProfit.setProfit(profit);

            //更新余额
            if(profit.compareTo(BigDecimal.ZERO) > -1)userService.operationNowMoney(user.getUid(),profit,user.getNowMoney(),Constants.ADD_STR);
            userProfitList.add(userProfit);
        }

        //保存收益记录
        if(userProfitList.size()>0)userProfitService.saveBatch(userProfitList);
    }

    @Override
    public CreatorProfitDataResponse getCreatorProfitData() {
        //得到用户
        User user=userService.getInfoException();

        //定义变量-订单统计
        BigDecimal yesterdayProfit;    //昨日收益
        BigDecimal thisMonthProfit;    //本月收益
        BigDecimal nowMoney=BigDecimal.ZERO;//可提现余额

        String startTime = null;    //开始日期
        String endTime = DateUtil.nowDateTime(Constants.DATE_FORMAT);           //结束日期
        StringBuffer dateSB=new StringBuffer("%s").append(",").append(endTime); //日期字符串拼接
        String date =null;          //日期范围
        Date lastMonthEndDay=DateUtil.strToDate(DateUtil.getLastMonthEndDay(),Constants.DATE_FORMAT);//上个月最后一天

        //昨日
        startTime = DateUtil.addDay(DateUtil.nowDateTime(), -1, Constants.DATE_FORMAT_DATE);
        date=String.format(dateSB.toString(),startTime);
        yesterdayProfit = userProfitService.getProfitStatistics(user.getUid(),date);

        //本月
        startTime = DateUtil.addDay(lastMonthEndDay, 0, Constants.DATE_FORMAT);
        date=String.format(dateSB.toString(),startTime);
        thisMonthProfit=userProfitService.getProfitStatistics(user.getUid(),date);

        //可提现余额
        nowMoney = user.getNowMoney();

        //得到最近七天收益记录
        dateLimitUtilVo dateLimitUtilVo = DateUtil.getDateLimit(Constants.SEARCH_DATE_LATELY_7);
        LambdaQueryWrapper<UserProfit> lqw=new LambdaQueryWrapper<>();
        lqw.eq(UserProfit::getUid,user.getUid());
        lqw.between(PublicTableField::getCreateTime,dateLimitUtilVo.getStartTime(),dateLimitUtilVo.getEndTime());
        lqw.orderByDesc(PublicTableField::getCreateTime);
        List<UserProfit> userProfitList = userProfitService.list();

        //实例化响应类
        CreatorProfitDataResponse response=new CreatorProfitDataResponse();
        response.setYestProfit(yesterdayProfit);
        response.setThisMonthProfit(thisMonthProfit);
        response.setNowMoney(nowMoney);
        response.setUserProfitList(userProfitList);
        return response;
    }

}
