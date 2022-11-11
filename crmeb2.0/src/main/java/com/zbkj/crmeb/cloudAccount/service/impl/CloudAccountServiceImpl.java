package com.zbkj.crmeb.cloudAccount.service.impl;

import com.alibaba.fastjson.JSON;
import com.exception.CrmebException;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.zbkj.crmeb.cloudAccount.constant.ConfigPath;
import com.zbkj.crmeb.cloudAccount.constant.XmlData;
import com.zbkj.crmeb.cloudAccount.request.DealerBalanceRequest;
import com.zbkj.crmeb.cloudAccount.request.Request;
import com.zbkj.crmeb.cloudAccount.response.DayStreamDataListResponse;
import com.zbkj.crmeb.cloudAccount.response.DayStreamDataResponse;
import com.zbkj.crmeb.cloudAccount.response.DealerBalanceDetailResponse;
import com.zbkj.crmeb.cloudAccount.response.Response;
import com.zbkj.crmeb.cloudAccount.service.CloudAccountService;
import com.zbkj.crmeb.cloudAccount.util.JsonUtil;
import com.zbkj.crmeb.cloudAccount.util.Property;
import com.zbkj.crmeb.cloudAccount.util.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: crmeb
 * @description:
 * @author: 零风
 * @create: 2021-08-18 11:13
 **/
@Service
public class CloudAccountServiceImpl implements CloudAccountService {

    @Override
    public DayStreamDataListResponse queryDayStream(String dateDay) {
        //实例化-日流水订单list-响应对象
        DayStreamDataListResponse dayStreamDataListResponse = DayStreamDataListResponse.builder().build();

        //验证非空
        if (StringUtils.isEmpty(dateDay)) {
            return dayStreamDataListResponse;
        }

        //设置-请求参数
        Map<String, Object> mapParam = new HashMap<>();
        mapParam.put("bill_date", dateDay);
        mapParam.put("offset", 0);
        mapParam.put("length", 200);

        //发送请求-并得到响应
        String api = Property.getProperties(ConfigPath.YZH_URL) + ConfigPath.YZH_API_QUERY_V1_BILLS;
        Map<String, Object> result = Request.sendRequestResult(mapParam, api, RequestMethod.GET.toString());

        //取出data
        Map<String, Object> data = JsonUtil.fromJson(result.get("data").toString(), Map.class);
        //方式一，逐步转换
        Object objData = data.get("data");
        if (objData == null) throw new CrmebException("失败！data：NULL!");
        Map<String, Object> dataMap = JsonUtil.fromJson(JSON.toJSONString(objData), Map.class);
        Object listObj = dataMap.get("list");
        Object total_numObj = dataMap.get("total_num");
        //转换-对应类型
        List<DayStreamDataResponse> list = JsonUtil.fromJson(JSON.toJSONString(listObj), List.class);
        Number total_num = JsonUtil.fromJson(JSON.toJSONString(total_numObj), Number.class);

        //方式二，强制转换
        //Map<String, Object> objData= (Map<String, Object>) data.get("data");
        //if( objData == null)throw new CrmebException("失败！data：NULL!");
        ////转换-list集合
        //List<DayStreamDataResponse>  objList= (List<DayStreamDataResponse>) objData.get("list");
        //Number total_num= (Number) objData.get("total_num");

        //赋值
        dayStreamDataListResponse.setList(list);
        dayStreamDataListResponse.setTotal_num(total_num.intValue());

        //返回
        return dayStreamDataListResponse;
    }

    @Override
    public DealerBalanceDetailResponse queryAccounts() {
        //实例化-响应对象
        DealerBalanceDetailResponse dealerBalanceDetailResponse = new DealerBalanceDetailResponse();

        //得到-云账户-查询余额-请求对象
        DealerBalanceRequest dealerBalanceRequest = DealerBalanceRequest.builder()
                .dealer_id(Property.getProperties(ConfigPath.YZH_DEALERID))
                .build();

        //发送请求-并得到响应
        String api = Property.getUrl(ConfigPath.YZH_DEALER_BALANCE_QUERY);
        Map<String, Object> result = Request.sendRequestResult(dealerBalanceRequest, api, RequestMethod.GET.toString());

        try {
            //转成-响应对象
            Response response = null;
            if ("200".equals(StringUtils.trim(result.get(XmlData.STATUSCODE)))) {
                response = JsonUtil.fromJson(StringUtils.trim(result.get(XmlData.DATA)), Response.class);
            }
            System.out.println(response);

            //取出-dealer_infos
            Map<String, Object> data = JsonUtil.fromJson(response.getData().toString(), Map.class);
            Object dealer_infos = data.get("dealer_infos");
            if (dealer_infos == null) throw new CrmebException("失败！dealer_infos：NULL!");

            //设置-响应对象
            Gson gson = new Gson();
            Type fooType = new TypeToken<List<DealerBalanceDetailResponse>>() {
            }.getType();
            List<DealerBalanceDetailResponse> dataList = gson.fromJson(dealer_infos.toString(), fooType);
            if (dataList.size() >= 1) {
                dealerBalanceDetailResponse = dataList.get(0);
            }

            //返回
            return dealerBalanceDetailResponse;
        } catch (Exception e) {
            throw new CrmebException("失败！发生错误：" + e.getMessage());
        }
    }
}
