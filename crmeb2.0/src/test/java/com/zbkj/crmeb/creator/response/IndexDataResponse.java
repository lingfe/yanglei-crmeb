package com.zbkj.crmeb.creator.response;

import com.zbkj.crmeb.system.model.SystemAttachment;
import com.zbkj.crmeb.user.model.User;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * 首页信息-Response
 * @author: 零风
 * @CreateDate: 2022/1/11 11:07
 */
@Data
public class IndexDataResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "首页banner滚动图")
    private List<HashMap<String, Object>> bannerList;

    @ApiModelProperty(value = "首页热门创作者列表")
    private List<User> userList;

    @ApiModelProperty(value = "首页推荐图片列表")
    private List<SystemAttachment> systemAttachmentList;

}
