package com.moshui.mall.service;

import javax.servlet.http.HttpSession;

public interface MallUserService {

    //登录
    String login(String loginName, String passwordMD5, HttpSession session);

    //注册
    String register(String loginName, String password);

}
