package com.zbkj.crmeb.validatecode.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zbkj.crmeb.validatecode.bean.VerificationCodeSliderPlaceBean;
import com.zbkj.crmeb.validatecode.util.VerificationCodeSliderUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * 验证码服务-图片滑块验证
 * @author: 零风
 * @CreateDate: 2022/1/10 10:51
 */
@Slf4j
@Controller
@RequestMapping("api/admin/validate/code/slider")
@Api(tags = "验证码服务-图片滑块验证")
public class ValidateCodeSliderController {

    //存储路径
    @Value("${afterImage.location}")
    private String imgLocation;

    @Value("${afterImage.imgsPath}")
    private String imgsPath;

    @ApiOperation(value="跳转到某个页面")
    @GetMapping("/index")
    public String index(){
        return "index.html";
    }

    /**
     * 随机获取背景和拼图，返回json
     * @return 滑块图片信息json
     */
    @ApiOperation(value="获取背景和拼图")
    @GetMapping("/getImgInfo")
    @ResponseBody
    public String imgInfo(){
        VerificationCodeSliderPlaceBean vcPlace = VerificationCodeSliderUtils.getRandomVerificationCodePlace(imgLocation,imgsPath);
        ObjectMapper om = new ObjectMapper();
        String jsonResult = "";
        try {
            jsonResult = om.writeValueAsString(vcPlace);
            return jsonResult;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return jsonResult;
    }

    /**
     * 删除生成的验证码图片
     * @return
     */
    @ApiOperation(value="删除生成的验证码图片")
    @RequestMapping(value = "/deleteImg", method = RequestMethod.DELETE)
    @ResponseBody
    public String deleteImg(){
        return VerificationCodeSliderUtils.deleteAfterImage(imgLocation);
    }
}
