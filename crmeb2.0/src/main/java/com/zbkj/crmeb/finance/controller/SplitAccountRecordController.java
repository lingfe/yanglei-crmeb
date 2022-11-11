package com.zbkj.crmeb.finance.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.finance.model.SplitAccountRecord;
import com.zbkj.crmeb.finance.request.SplitAccountRecordSearchRequest;
import com.zbkj.crmeb.finance.service.SplitAccountRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 分账记录表-表示层、控制类
 * @author: 零风
 * @CreateDate: 2022/1/21 10:52
 */
@Slf4j
@RestController
@RequestMapping("api/admin/splitAccount")
@Api(tags = "分账")
public class SplitAccountRecordController {

    @Autowired
    private SplitAccountRecordService splitAccountService;

    @ApiOperation(value = "分账-分页")
    @RequestMapping(value = "/getPageList", method = RequestMethod.GET)
    public CommonResult<CommonPage<SplitAccountRecord>> getPageList(
            SplitAccountRecordSearchRequest request,
            PageParamRequest pageParamRequest){
        CommonPage<SplitAccountRecord> commonPage = CommonPage.restPage(splitAccountService.getPageList(request,pageParamRequest));
        return CommonResult.success(commonPage);
    }


}
