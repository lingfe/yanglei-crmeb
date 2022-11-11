package com.filter;

import com.constants.Constants;
import com.utils.SpringUtil;
import com.zbkj.crmeb.system.service.SystemAttachmentService;

/**
 * response-路径处理
 * @author: 零风
 * @CreateDate: 2022/1/10 10:17
 */
public class ResponseRouter {

    /**
     * 响应结果-路径处理
     * @Author 零风
     * @Date  2022/1/10
     * @return 数据
     */
    public String filter(String data, String path){
        //验证-路径是否为空
        boolean result = "".contains(path);
        if(result){
            return data;
        }

        //验证-是否为接口
        if (!path.contains("api/admin/") && !path.contains("api/front/") && !path.contains("api/public/")) {
        //if (!path.contains("api/admin/") && !path.contains("api/front/") ) {
            return data;
        }

        //验证-是否包含图片路径并处理图片路径前缀
        //if(data.contains("image/") && !data.contains("data:image/png;base64")){
        if(data.contains(Constants.filter_response_IMAGE) &&
                !data.contains("data:image/png;base64")){
            data = SpringUtil.getBean(SystemAttachmentService.class).prefixImage(data);
        }

        //验证-是否包含导入商品图片路径并处理前缀
        if(data.contains(Constants.filter_response_product) &&
                !data.contains("data:image/png;base64")&&
                !data.contains(Constants.filter_response_IMAGE)) {
            data = SpringUtil.getBean(SystemAttachmentService.class).prefixImageProduct(data);
        }

        //验证-是否包含文件路径
        if(data.contains(Constants.filter_response_File)||data.contains("file/store")){
            data = SpringUtil.getBean(SystemAttachmentService.class).prefixFile(data);
        }

        //返回data
        return data;
    }
}
