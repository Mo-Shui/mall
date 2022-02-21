package com.moshui.mall.controller;

import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class KaptchaController {

    @GetMapping("/kaptcha")
    public void defaultKaptcha(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        httpServletResponse.setHeader("Cache-Control", "no-store");
        httpServletResponse.setHeader("Pragma", "no-cache");
        httpServletResponse.setDateHeader("Expires", 0);
        httpServletResponse.setContentType("image/gif");

        // 三个参数分别为宽、高、位数
        SpecCaptcha captcha = new SpecCaptcha(75, 30,4);

        // 设置类型为数字和字母混合
        captcha.setCharType(Captcha.TYPE_DEFAULT);

        // 设置字体
        captcha.setCharType(Captcha.FONT_9);

        // 验证码存入session
        httpServletRequest.getSession().setAttribute("verifyCode", captcha.text().toLowerCase());

        // 输出图片流
        captcha.out(httpServletResponse.getOutputStream());
    }

    @GetMapping("/verify")
    @ResponseBody
    public String verify(String code,HttpSession session){
        if (code == null || code.equals("")){
            return "验证码不能为空";
        }
        if (!session.getAttribute("verifyCode").equals(code.toLowerCase())){
            return "验证码错误";
        }
        return "验证码正确";
    }

}
