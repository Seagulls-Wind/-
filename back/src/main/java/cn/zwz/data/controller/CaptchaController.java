package cn.zwz.data.controller;

import cn.zwz.basics.utils.CreateVerifyCode;
import cn.zwz.basics.utils.ResultUtil;
import cn.zwz.basics.baseVo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;

/**
 * 验证码接口（和前端登录页完全匹配）
 * @author 郑为中
 */
@Api(tags = "验证码接口")
@RequestMapping("/zwz/common/captcha")
@RestController
// 移除事务注解，避免干扰Controller注册
public class CaptchaController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 绘制验证码图片（和前端路径参数uuid完全匹配）
     * @param uuid 验证码ID
     * @param response 响应对象
     * @throws IOException IO异常
     */
    @GetMapping("/draw/{uuid}")
    @ApiOperation(value = "根据验证码ID获取图片")
    public void draw(@PathVariable("uuid") String uuid, HttpServletResponse response) throws IOException {
        // 1. 强制设置响应头为图片类型，确保浏览器识别
        response.setContentType("image/png");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Expires", "0");

        // 2. 从Redis获取验证码，为空则兜底（防止Redis未启动/数据过期）
        String codeStr = redisTemplate.opsForValue().get(uuid);
        if (codeStr == null || codeStr.isEmpty()) {
            codeStr = new CreateVerifyCode().randomStr(4); // 生成随机4位验证码
        }

        try {
            // 优先使用项目自带的CreateVerifyCode工具类生成图片
            CreateVerifyCode createVerifyCode = new CreateVerifyCode(116, 36, 4, 10, codeStr);
            createVerifyCode.write(response.getOutputStream());
        } catch (Exception e) {
            // 备用方案：手动绘制验证码图片（兼容CreateVerifyCode工具类异常）
            BufferedImage image = new BufferedImage(116, 36, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();

            // 绘制白色背景
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, 116, 36);

            // 绘制验证码文字（黑色、加粗）
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString(codeStr, 10, 28);

            // 绘制干扰线（增强验证码安全性）
            Random random = new Random();
            for (int i = 0; i < 5; i++) {
                g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
                g.drawLine(random.nextInt(116), random.nextInt(36), random.nextInt(116), random.nextInt(36));
            }
            g.dispose();

            // 输出图片到浏览器
            ImageIO.write(image, "png", response.getOutputStream());
        }
    }

    /**
     * 初始化验证码（生成UUID并存储到Redis）
     * @return 验证码ID（UUID）
     */
    @GetMapping("/init")
    @ApiOperation(value = "初始化验证码")
    public Result<Object> init() {
                                                                                // 生成不带横线的UUID作为验证码ID
        String codeId = UUID.randomUUID().toString().replace("-", "");
                                                                        // 生成4位随机验证码，存入Redis，2分钟过期
        String randomCode = new CreateVerifyCode().randomStr(4);
        redisTemplate.opsForValue().set(codeId, randomCode, 2L, TimeUnit.MINUTES);
        // 返回验证码ID给前端
        return ResultUtil.data(codeId);
    }
}
//package cn.zwz.data.controller;
//
//import cn.zwz.basics.utils.*;
//import cn.zwz.basics.baseVo.Result;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.UUID;
//import java.util.concurrent.TimeUnit;
//
///**
// * @author 郑为中
// */
//@Api(tags = "验证码接口")
//@RequestMapping("/zwz/common/captcha")
//@RestController
//@Transactional
//public class CaptchaController {
//
//    @Autowired
//    private StringRedisTemplate redisTemplate;
//
//    @RequestMapping(value = "/draw/{captchaId}", method = RequestMethod.GET)
//    @ApiOperation(value = "根据验证码ID获取图片")
//    public void draw(@PathVariable("captchaId") String captchaId,HttpServletResponse response) throws IOException {
//        String codeStr = redisTemplate.opsForValue().get(captchaId);
//        CreateVerifyCode createVerifyCode = new CreateVerifyCode(116,36,4,10, codeStr);
//        response.setContentType("image/png");
//        createVerifyCode.write(response.getOutputStream());
//    }
//
//    @RequestMapping(value = "/init", method = RequestMethod.GET)
//    @ApiOperation(value = "初始化验证码")
//    public Result<Object> init() {
//        String codeId = UUID.randomUUID().toString().replace("-","");
//        redisTemplate.opsForValue().set(codeId, new CreateVerifyCode().randomStr(4),2L, TimeUnit.MINUTES);
//        return ResultUtil.data(codeId);
//    }
//}
