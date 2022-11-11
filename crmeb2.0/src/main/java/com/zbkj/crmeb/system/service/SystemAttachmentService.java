package com.zbkj.crmeb.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.zbkj.crmeb.system.model.SystemAttachment;
import com.zbkj.crmeb.system.request.SystemAttachmentSearchRequest;
import com.zbkj.crmeb.upload.vo.FileResultVo;

import java.util.List;

/**
 * SystemAttachmentService-接口
 * @author: 零风
 * @CreateDate: 2022/1/19 14:12
 */
public interface SystemAttachmentService extends IService<SystemAttachment> {
    void create(FileResultVo file, Integer pid);

    void async();

    void updateCloudType(Integer attId, int type);

    /**
     * 图片列表分页
     * @param request   搜索请求参数
     * @param pageParamRequest 分页参数
     * @Author 零风
     * @Date  2022/2/23
     * @return 分页列表
     */
    List<SystemAttachment> getList(SystemAttachmentSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 给图片加前缀
     * @param path
     * @return
     */
    String prefixImage(String path);

    /**
     * 给图片加前缀-默认商品导入路径
     * @param path
     * @return
     */
    String prefixImageProduct(String path);

    /**
     * 给文件加前缀
     * @param str
     * @return
     */
    String prefixFile(String str);

    /**
     * 清除 cdn url， 在保存数据的时候使用
     * @param attribute
     * @return
     */
    String clearPrefix(String attribute);

    /**
     * 附件基本查询
     * @param systemAttachment 附件参数
     * @return 附件
     */
    List<SystemAttachment> getByEntity(SystemAttachment systemAttachment);
}
