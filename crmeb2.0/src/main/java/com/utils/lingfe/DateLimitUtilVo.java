package com.utils.lingfe;

import lombok.Data;

/**
 * 时间Vo类
 * @author: 零风
 * @CreateDate: 2022/7/29 11:48
 */
@Data
public class DateLimitUtilVo {
    public DateLimitUtilVo() {}
    public DateLimitUtilVo(String startTime, String endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    private String startTime; //开始时间

    private String endTime; //结束时间
}
