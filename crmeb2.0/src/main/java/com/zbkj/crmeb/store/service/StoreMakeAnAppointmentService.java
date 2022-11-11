package com.zbkj.crmeb.store.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.store.model.StoreMakeAnAppointment;
import com.zbkj.crmeb.store.request.StoreMakeAnAppointmentSearchRequest;

/**
 * 商城预约表-service层接口
 * @author: 零风
 * @CreateDate: 2022/7/21 15:01
 */
public interface StoreMakeAnAppointmentService extends IService<StoreMakeAnAppointment> {

    /**
     * 保存预约信息
     * @Author 零风
     * @Date  2022/7/21 15:04
     * @return
     */
    boolean saveMaa(Integer linkId);

    /**
     * 分页预约记录
     * @param request   搜索参数
     * @param pageParamRequest 分页参数
     * @param isAll     是否查询全部
     * @Author 零风
     * @Date  2022/5/11 9:50
     * @return 数据
     */
    PageInfo<StoreMakeAnAppointment> getPageList(StoreMakeAnAppointmentSearchRequest request, PageParamRequest pageParamRequest,Boolean isAll);

    /**
     * 商城预约处理定时任务
     * @Author 零风
     * @Date  2022/7/21 15:28
     * @return
     */
    void maaHandleTask();

}
