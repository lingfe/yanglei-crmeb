package com.zbkj.crmeb.express.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.zbkj.crmeb.express.model.ShippingTemplatesFree;
import com.zbkj.crmeb.express.request.ShippingTemplatesFreeRequest;

import java.util.List;

/**
* ShippingTemplatesFreeService 接口
*  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
*/
public interface ShippingTemplatesFreeService extends IService<ShippingTemplatesFree> {

    List<ShippingTemplatesFree> getList(PageParamRequest pageParamRequest);

    void saveAll(List<ShippingTemplatesFreeRequest> shippingTemplatesFreeRequestList, Integer type, Integer id);

    List<ShippingTemplatesFreeRequest> getListGroup(Integer tempId);

    void delete(Integer id);

    /**
     * 根据模板编号、城市ID查询
     * @param tempId 模板编号
     * @param cityId 城市ID
     * @return 运费模板
     */
    ShippingTemplatesFree getByTempIdAndCityId(Integer tempId, Integer cityId);
}
