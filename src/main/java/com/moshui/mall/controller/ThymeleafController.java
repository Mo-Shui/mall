package com.moshui.mall.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ThymeleafController {

    @GetMapping("/thymeleaf")
    public String thymelaef(HttpServletRequest request,@RequestParam(defaultValue = "haha") String text){
        request.setAttribute("text",text);
        return "thymeleaf";
    }

}
