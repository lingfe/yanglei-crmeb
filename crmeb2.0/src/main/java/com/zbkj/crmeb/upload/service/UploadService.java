package com.zbkj.crmeb.upload.service;

import com.zbkj.crmeb.upload.vo.FileResultVo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * UploadService 接口
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
public interface UploadService {

    /**
     * 图片上传
     * @param multipart     文件流
     * @param model         模块= 用户user,商品product,微信wechat,news文章
     * @param pid           分类ID=0编辑器,1商品图片,2拼团图片,3砍价图片,4秒杀图片,5文章图片,6组合数据图,7前台用户,8微信系列
     * @return
     * @throws IOException
     */
    FileResultVo image(MultipartFile multipart, String model, Integer pid) throws IOException;

    /**
     * 文件上传
     * @param multipart     文件流
     * @param model         模块= 用户user,商品product,微信wechat,news文章
     * @param pid           分类ID=0编辑器,1商品图片,2拼团图片,3砍价图片,4秒杀图片,5文章图片,6组合数据图,7前台用户,8微信系列
     * @return
     * @throws IOException
     */
    FileResultVo file(MultipartFile multipart, String model, Integer pid) throws IOException;

}
