package com.zbkj.crmeb.creator.service;

import com.common.PageParamRequest;
import com.zbkj.crmeb.creator.request.IndexDataRequest;
import com.zbkj.crmeb.creator.request.IndexSearchRequest;
import com.zbkj.crmeb.creator.request.InfoCreatorSearchRequest;
import com.zbkj.crmeb.creator.response.IndexDataResponse;
import com.zbkj.crmeb.creator.response.IndexSearchResponse;
import com.zbkj.crmeb.creator.response.InfoCreatorHomeResponse;
import com.zbkj.crmeb.front.request.PublicUserLoginRequest;
import com.zbkj.crmeb.front.request.UserBindingPhoneUpdateRequest;
import com.zbkj.crmeb.front.response.LoginResponse;
import com.zbkj.crmeb.system.model.SystemAttachment;
import com.zbkj.crmeb.upload.vo.FileResultVo;
import com.zbkj.crmeb.user.model.User;

import java.util.List;

/**
 * 首页相关-service接口层
 * @author: 零风
 * @CreateDate: 2022/1/11 10:27
 */
public interface CreatorIndexService {

    /**
     * 首页数据
     * @param request 请求参数
     * @Author 零风
     * @Date  2022/7/1 15:35
     * @return 数据
     */
    IndexDataResponse indexData(IndexDataRequest request);

    /**
     * 获取热门创造者用户列表
     * @Author 零风
     * @Date  2022/7/7 14:47
     * @return
     */
    List<User> getIndexRemenUserList(String searchKey);

    /**
     * 首页搜索
     * @param request 搜索请求对象
     * @Author 零风
     * @Date  2022/7/1 16:08
     * @return 数据
     */
    IndexSearchResponse indexSearch(IndexSearchRequest request);

    /**
     * 创作者详细主页
     * @param request 搜索请求对象
     * @Author 零风
     * @Date  2022/7/1 16:08
     * @return 数据
     */
    InfoCreatorHomeResponse infoCreatorHome(InfoCreatorSearchRequest request);

    /**
     * 根据标签筛选创作者作品
     * @Author 零风
     * @Date  2022/7/7 14:58
     * @return
     */
    List<SystemAttachment> whereCategoryScreenCreatorWorksList(InfoCreatorSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 通过图片url保存到服务器
     * @Author 零风
     * @Date  2022/7/7 15:14
     * @return
     */
    FileResultVo imgUrlSaveFiles(String imgUrl,String folder,Integer pid);

    /**
     * 字节-小程序授权登录
     * @Author 零风
     * @Date  2022/6/28 14:50
     * @return
     */
    LoginResponse zijieAuthorizeProgramLogin(String code, PublicUserLoginRequest request);

    /**
     * 绑定手机号
     * @Author 零风
     * @Date  2022/7/8 11:01
     * @return
     */
    boolean bindPhone(UserBindingPhoneUpdateRequest request);

    /**
     * 附件详细信息
     * @Author 零风
     * @Date  2022/7/12 11:12
     * @return
     */
    Object attInfo(Integer attid);

    /**
     * 点赞
     * @Author 零风
     * @Date  2022/7/8 11:01
     * @return
     */
    boolean like(Integer attid);

    /**
     * 我的点赞
     * @Author 零风
     * @Date  2022/7/12 11:22
     * @return
     */
    List<SystemAttachment> likeMy();

    /**
     * 下载
     * @Author 零风
     * @Date  2022/7/19 14:56
     * @return
     */
    boolean download(Integer attid);
}
