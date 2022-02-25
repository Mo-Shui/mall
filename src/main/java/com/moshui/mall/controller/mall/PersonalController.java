package com.moshui.mall.controller.mall;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PersonalController {

    //登录页面跳转
    @GetMapping({"/login", "login.html"})
    public String login() {
        return "/mall/login";
    }

    //注册页面跳转
    @GetMapping({"/register", "register.html"})
    public String register() {
        return "/mall/register";
    }
}