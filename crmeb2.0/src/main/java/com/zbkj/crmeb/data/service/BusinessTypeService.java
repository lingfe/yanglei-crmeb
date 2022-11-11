package com.zbkj.crmeb.data.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.data.model.BusinessType;

/**
 * 经营类型表-service层接口
 * @author: 零风
 * @CreateDate: 2021/12/30 14:56
 */
public interface BusinessTypeService extends IService<BusinessType> {

    /**
     * 树形结构
     * @Author 零风
     * @Date  2021/12/30
     * @return 数据
     */
    Object getListTree();

}
