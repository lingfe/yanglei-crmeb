package com.zbkj.crmeb.cloudAccount.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 云账户-查询余额-请求类
 */
@Getter
@Setter
@Builder
@ToString
public class DealerBalanceRequest {

    /**
     * 商户代码(必填)
     **/
    private String dealer_id;

}
