package com.zbkj.crmeb.wechat.vo;

import lombok.Data;

/**
 * 赠送积分消息通知
 * @author: 零风
 * @CreateDate: 2022/3/11 15:50
 */
@Data
public class WechatSendMessageForIntegral {

    public WechatSendMessageForIntegral(String shuoMing, String dingDanBianHao, String shangPinMingCheng, String zhiFuJinE, String huoDeJiFen, String leiJiJiFen, String jiaoYiShiJian, String beiZhu, String menDian, String daoZhangYuanYin) {
        ShuoMing = shuoMing;
        DingDanBianHao = dingDanBianHao;
        ShangPinMingCheng = shangPinMingCheng;
        ZhiFuJinE = zhiFuJinE;
        HuoDeJiFen = huoDeJiFen;
        LeiJiJiFen = leiJiJiFen;
        JiaoYiShiJian = jiaoYiShiJian;
        BeiZhu = beiZhu;
        MenDian = menDian;
        DaoZhangYuanYin = daoZhangYuanYin;
    }

    private String ShuoMing;
    private String DingDanBianHao;
    private String ShangPinMingCheng;
    private String ZhiFuJinE;
    private String HuoDeJiFen;
    private String LeiJiJiFen;
    private String JiaoYiShiJian;
    private String BeiZhu;
    private String MenDian;
    private String DaoZhangYuanYin;
}
