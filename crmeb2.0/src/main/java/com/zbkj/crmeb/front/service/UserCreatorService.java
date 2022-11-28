package com.zbkj.crmeb.front.service;

import com.common.PageParamRequest;
import com.zbkj.crmeb.category.model.Category;
import com.zbkj.crmeb.front.request.GetWorksListRequest;
import com.zbkj.crmeb.front.response.CreatorDataResponse;
import com.zbkj.crmeb.front.response.CreatorProfitDataResponse;
import com.zbkj.crmeb.system.request.SystemAttachmentSearchRequest;
import com.zbkj.crmeb.system.response.SystemAttachmentResponse;
import com.zbkj.crmeb.upload.vo.FileResultVo;
import com.zbkj.crmeb.user.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 创作者用户-service层接口层
 * @author: 零风
 * @CreateDate: 2022/7/15 9:36
 */
public interface UserCreatorService {

    /**
     * 得到作品分类
     * @param isNull 是否只查询作品不为空的分类
     * @Author 零风
     * @Date  2022/7/15 9:51
     * @return
     */
    List<Map<String,Object>> getWorksType(Boolean isNull);

    /**
     * 得到作品分类列表
     * @param uid 创作者用户ID标识
     * @param isNull 是否只查询作品不为空的分类
     * @Author 零风
     * @Date  2022/7/15 9:51
     * @return
     */
    List<Map<String,Object>> getWorksTypeList(Integer uid,Boolean isNull);

    /**
     * 得到单个作品分类信息
     * @Author 零风
     * @Date  2022/7/15 10:05
     * @return
     */
    Map<String,Object> getWorksTypeMap(Category category, SystemAttachmentSearchRequest systemAttachmentSearchRequest);

    /**
     * 得到作品列表
     * @Author 零风
     * @Date  2022/7/15 9:39
     * @return
     */
    List<SystemAttachmentResponse> getWorksList(GetWorksListRequest request, PageParamRequest pageParamRequest);

    /**
     * 得到附件下载量
     * @Author 零风
     * @Date  2022/7/19 15:04
     * @return
     */
    Integer getAttachmentDownloads(Integer uid,Integer ywType,Integer attId,String date);

    /**
     * 上传作品
     * @Author 零风
     * @Date  2022/7/19 15:26
     * @return
     */
    FileResultVo uploadWorks(MultipartFile multipart, String lingfe, Integer pid) throws IOException;

    /**
     * 通过图片URL路径保存
     * @Author 零风
     * @Date  2022/7/19 15:43
     * @return
     */
    FileResultVo UploadUrl(Integer pid, String url);

    /**
     * 设置推荐人
     * @param spreadUid 推荐人用户ID标识
     * @Author 零风
     * @Date  2022/7/27 9:48
     * @return
     */
    Boolean setSpreadUid(Integer spreadUid);

    /**
     * 设置支付宝账号
     * @param alipay   支付宝账号
     * @param realName 真实姓名
     * @Author 零风
     * @Date  2022/7/27 9:45
     * @return
     */
    Boolean setAlipay(String realName,String alipay);

    /**
     * 我的邀请
     * @Author 零风
     * @Date  2022/7/29 9:20
     * @return
     */
    List<CreatorDataResponse> invitationMy(PageParamRequest pageParamRequest);

    /**
     * 得到创作者数据响应对象
     * @param user 用户信息
     * @Author 零风
     * @Date  2022/7/8 10:36
     * @return 创作者数据响应对象
     */
    CreatorDataResponse getCreatorDataResponse(User user);

    /**
     * 我的资料
     * @Author 零风
     * @Date  2022/7/29 9:46
     * @return
     */
    CreatorDataResponse myInfo();

    /**
     * 创作者收益计算Task(定时任务)
     * -每天凌晨01:00计算
     * @Author 零风
     * @Date  2022/7/29 10:39
     * @return
     */
    void creatorProfitComputeTask();

    /**
     * 获取收益数据
     * @Author 零风
     * @Date  2022/7/29 10:38
     * @return
     */
    CreatorProfitDataResponse getCreatorProfitData();

}
