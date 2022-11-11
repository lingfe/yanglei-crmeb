package com.zbkj.crmeb.log.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.constants.Constants;
import com.utils.DateUtil;
import com.utils.RedisUtil;
import com.utils.vo.dateLimitUtilVo;
import com.zbkj.crmeb.log.dao.StoreProductLogDao;
import com.zbkj.crmeb.log.model.StoreProductLog;
import com.zbkj.crmeb.log.response.StoreProductLogResponse;
import com.zbkj.crmeb.log.service.StoreProductLogService;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class StoreProductLogServiceImpl extends ServiceImpl<StoreProductLogDao, StoreProductLog> implements StoreProductLogService {

    @Resource
    private StoreProductLogDao dao;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisUtil redisUtil;

    private static final Logger logger = LoggerFactory.getLogger(StoreProductLogServiceImpl.class);


    @Override
    public List<StoreProductLogResponse> getListWhereProductId(Integer productId) {
        //得到查询对象
        String time=Constants.SEARCH_DATE_LATELY_30;
        String type="visit";
        QueryWrapper<StoreProductLog> lqw = this.getLambdaQueryWrapper(time,type);
        lqw.select("DISTINCT uid,visit_num,cart_num,order_num,pay_num,collect_num ");
        lqw.eq("product_id",productId);

        //得到数据
        List<StoreProductLog> list=dao.selectList(lqw);
        List<StoreProductLogResponse> listResponse=new ArrayList<>();
        for (StoreProductLog storeProductLog:list) {
            //实例化-商品日志-响应对象
            StoreProductLogResponse storeProductLogResponse = new StoreProductLogResponse();
            BeanUtils.copyProperties(storeProductLog, storeProductLogResponse);

            //得到用户名称
            User user = userService.getById(storeProductLog.getUid());
            if(user!=null){
                storeProductLogResponse.setUserName(user.getNickname()==null?"游客":user.getNickname());
                storeProductLogResponse.setAvatar(user.getAvatar());
            }

            //添加到-响应集合
            listResponse.add(storeProductLogResponse);
        }
        return listResponse;
    }

    /**
     * 得到查询条件
     * @param time  日期类型
     * @param type  数据类型
     * @return
     */
    static QueryWrapper<StoreProductLog> getLambdaQueryWrapper(String time, String type){
        QueryWrapper<StoreProductLog> lqw = new QueryWrapper<>();
        lqw.eq("type", type);
        dateLimitUtilVo dateLimit = DateUtil.getDateLimit(time);
        //时间范围
        if(dateLimit.getStartTime() != null && dateLimit.getEndTime() != null){
            Long startTime = DateUtil.dateStr2Timestamp(dateLimit.getStartTime(), Constants.DATE_TIME_TYPE_BEGIN);
            Long endTime = DateUtil.dateStr2Timestamp(dateLimit.getEndTime(), Constants.DATE_TIME_TYPE_END);
            lqw.between("add_time", startTime, endTime);
        }
        return lqw;
    }

    @Override
    public Integer getCountByTimeAndType(String time, String type) {
        return dao.selectCount(this.getLambdaQueryWrapper(time,type));
    }

    @Override
    public void addLogTask() {
        Long size = redisUtil.getListSize(Constants.PRODUCT_LOG_KEY);
        logger.info(" 添加商品日志记录(定时任务) - StoreProductLogServiceImpl.addLogTask | size:" + size);

        if(size > 0){
            for (int i = 0; i < size; i++) {
                //如果10秒钟拿不到一个数据，那么退出循环
                Object data = redisUtil.getRightPop(Constants.PRODUCT_LOG_KEY, 10L);
                if(null == data){
                    continue;
                }

                try{
                    JSONObject jsonObject = JSON.parseObject(data.toString());
                    StoreProductLog proLog = new StoreProductLog();
                    proLog.setProductId(jsonObject.getInteger("product_id"));
                    proLog.setUid(jsonObject.getInteger("uid"));
                    proLog.setType(jsonObject.getString("type"));
                    proLog.setAddTime(jsonObject.getLong("add_time"));
                    dao.insert(proLog);
                }catch (Exception e){
                    logger.info(" 添加商品日志记录(定时任务) - StoreProductLogServiceImpl.addLogTask  发生错误 | msg:" + e.getMessage());
                    redisUtil.lPush(Constants.PRODUCT_LOG_KEY, data);
                }
            }
        }
    }

}

