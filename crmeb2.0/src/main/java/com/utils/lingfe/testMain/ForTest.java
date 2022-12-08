package com.utils.lingfe.testMain;

import org.apache.commons.lang3.StringUtils;

/**
 * @program: crmeb
 * @description: 死循环测试
 * @author: 零风
 * @create: 2021-08-02 15:24
 **/
public class ForTest {

    public static void main(String[] args) {
        String str="123dd";
        String str2="857";

        System.out.println(StringUtils.isNumeric(str));
        System.out.println(StringUtils.isNumeric(str2));
    }

    //public static void main(String[] args) {
    //
    //    //第一种，轮回式
    //    for (int i=0;i<=1;i++){
    //        System.out.println("我愿意为凡凡付出身体，所有妹子看到这个可以来睡我。我绝不反抗 \n");
    //        i--;
    //    }
    //
    //    //第二种，永无止境式
    //    while (true){
    //        System.out.println("我愿意为凡凡付出身体，所有妹子看到这个可以来睡我。我绝不反抗 \n");
    //        if(!Boolean.TRUE)break;
    //    }
    //
    //    //第三种，直来直往式
    //    do {
    //        System.out.println("我愿意为凡凡付出身体，所有妹子看到这个可以来睡我。我绝不反抗 \n");
    //        if(!Boolean.FALSE)break;
    //    }while (true);
    //
    //    //第四种，无穷无尽式
    //    int start = Integer.MAX_VALUE - 1;
    //    for (int i =start; i <=start+1; i++){
    //        System.out.println("我愿意为凡凡付出身体，所有妹子看到这个可以来睡我。我绝不反抗 \n");
    //        if("吴亦凡".equals("强奸犯"))break;
    //    }
    //
    //    //第五种，三路选择式
    //    for (;;){
    //        int i=0;
    //        switch ("吴亦凡"){
    //            case "狗":
    //                i=1;
    //                break;
    //            case "强奸犯":
    //                i=2;
    //                break;
    //            case "牙签":
    //                i=3;
    //                break;
    //            default:
    //                System.out.println("我愿意为凡凡付出身体，所有妹子看到这个可以来睡我。我绝不反抗 \n");
    //                break;
    //        }
    //        if(i>0)break;
    //    }
    //
    //    //第六种，滑稽走位式
    //    byte i=-1;
    //    while( i != 0){
    //        i >>>=1;
    //        System.out.println("我愿意为凡凡付出身体，所有妹子看到这个可以来睡我。我绝不反抗 \n");
    //    }
    //
    //    //第七种，无限套娃式
    //    for( ; true ; ){
    //        System.out.println("我愿意为凡凡付出身体，所有妹子看到这个可以来睡我。我绝不反抗 \n");
    //        if(true)if(true)if(true)if(true)if(!true)break;
    //    }
    //
    //    //第八种，风火轮流式
    //    int tt=0;
    //    String string=new StringBuffer("狗,").append("强奸犯,").append("牙签").toString();
    //    String[]  wyfArr=string.split(",");
    //    List<String> wyfList= Arrays.stream(wyfArr).collect(Collectors.toList());
    //    for (String str: wyfArr) {
    //        System.out.println("我愿意为凡凡付出身体，所有妹子看到这个可以来睡我。我绝不反抗 \n");
    //        tt++;
    //        if(tt==wyfArr.length){
    //            tt=0;
    //            wyfList.addAll(Arrays.stream(wyfArr).collect(Collectors.toList()));
    //        }
    //        if(wyfList.size()==0)break;
    //    }
    //
    //    //第九种，牢底坐穿式
    //    int year=0;
    //    Collection stringList = java.util.Arrays.asList("狗、强奸犯、牙签".split("、"));
    //    for (Iterator itr = stringList.iterator(); itr.hasNext();) {
    //        Object str = itr.next();
    //        System.out.println("我愿意为凡凡付出身体，所有妹子看到这个可以来睡我。我绝不反抗 \n");
    //        if("坐牢年数" == String.valueOf(stringList.size()))break;
    //        year++;
    //        stringList.add(year);
    //    }
    //
    //
    //}

}
