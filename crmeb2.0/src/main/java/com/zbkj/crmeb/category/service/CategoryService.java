package com.zbkj.crmeb.category.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.zbkj.crmeb.category.model.Category;
import com.zbkj.crmeb.category.request.CategoryRequest;
import com.zbkj.crmeb.category.request.CategorySearchRequest;
import com.zbkj.crmeb.category.vo.CategoryTreeVo;

import java.util.HashMap;
import java.util.List;

/**
*   CategoryService 接口
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
public interface CategoryService extends IService<Category> {
    List<Category> getList(CategorySearchRequest request, PageParamRequest pageParamRequest);

    int delete(Integer id);

    String getPathByPId(Integer pid);

    List<CategoryTreeVo> getListTree(Integer type, Integer status, String name);
    List<CategoryTreeVo> getListTree(Integer type, Integer status, List<Integer> categoryIdList);

    List<Category> getByIds(List<Integer> ids);

    HashMap<Integer, String> getListInId(List<Integer> cateIdList);

    Boolean checkAuth(List<Integer> pathIdList, String uri);

    boolean update(CategoryRequest request, Integer id);

    List<Category> getChildVoListByPid(Integer pid);

    /**
     * 检测-分类是否存在
     * @param name  分类名称
     * @param type  分类类型
     * @param pid   父级ID
     * @author 零风
     * @date 2021-09-17
     * @return
     */
    int checkName(String name, Integer type,Integer pid);

    boolean checkUrl(String uri);

    boolean updateStatus(Integer id);

    /**
     * 新增分类
     */
    Boolean create(CategoryRequest categoryRequest);

    /**
     * 获取文章分类列表
     * @return List<Category>
     */
    List<Category> findArticleCategoryList();
}
