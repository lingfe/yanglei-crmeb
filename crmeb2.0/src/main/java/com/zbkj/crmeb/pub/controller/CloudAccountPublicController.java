package com.zbkj.crmeb.pub.controller;

import com.zbkj.crmeb.finance.service.UserExtractService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @program: crmeb
 * @description: 云账户-下单打款回调接口-控制类-表示层
 * @author: 零风
 * @create: 2021-08-11 10:14
 **/
@Slf4j
@RestController()
@RequestMapping("api/public/cloudAccount")
@Api(tags = "云账户-下单打款回调接口")
public class CloudAccountPublicController {

    @Autowired
    private UserExtractService userExtractService;

    /**
     * 银行卡-下单打款-回调接口
     * @param request 请求
     * @return
     */
    @ApiOperation(value = "银行卡-下单打款-回调接口")
    @RequestMapping(value = "/applyCallback", method = {RequestMethod.POST,RequestMethod.GET})
    public String recordList(HttpServletRequest request)   {
        return userExtractService.applyCallback(request);
    }

}
