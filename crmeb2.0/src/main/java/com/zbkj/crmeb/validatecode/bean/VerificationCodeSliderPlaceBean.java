package com.zbkj.crmeb.validatecode.bean;

/**
 * 滑块验证码bean
 * @author: 零风
 * @CreateDate: 2022/1/10 10:53
 */
public class VerificationCodeSliderPlaceBean {
    //大图名称
    private String backName;
    private String backNameBass64;
    //小图名称
    private String markName;
    private String markNameBass64;
    //小图x坐标
    private int xLocation;
    //小图y坐标
    private int yLocation;

    /**
     * 构造函数-无参
     */
    public VerificationCodeSliderPlaceBean(){}

    /**
     * 构造函数
     * @param backName 大图名称
     * @param markName 小图名称
     * @param xLocation 小图x
     * @param yLocation 小图y
     */
    public VerificationCodeSliderPlaceBean(String backName, String markName, int xLocation, int yLocation){
        this.backName = backName;
        this.markName = markName;
        this.xLocation = xLocation;
        this.yLocation = yLocation;
    }

    public String getBackName() {
        return backName;
    }

    public void setBackName(String backName) {
        this.backName = backName;
    }

    public String getMarkName() {
        return markName;
    }

    public void setMarkName(String markName) {
        this.markName = markName;
    }

    public int getxLocation() {
        return xLocation;
    }

    public void setxLocation(int xLocation) {
        this.xLocation = xLocation;
    }

    public int getyLocation() {
        return yLocation;
    }

    public void setyLocation(int yLocation) {
        this.yLocation = yLocation;
    }

    public String getBackNameBass64() {
        return backNameBass64;
    }

    public void setBackNameBass64(String backNameBass64) {
        this.backNameBass64 = backNameBass64;
    }

    public String getMarkNameBass64() {
        return markNameBass64;
    }

    public void setMarkNameBass64(String markNameBass64) {
        this.markNameBass64 = markNameBass64;
    }
}
