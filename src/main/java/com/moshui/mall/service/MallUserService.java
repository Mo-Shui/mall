package com.moshui.mall.service;

import com.moshui.mall.controller.vo.MallUserVO;
import com.moshui.mall.entity.MallUser;

import javax.servlet.http.HttpSession;

public interface MallUserService {

    //登录
    String login(String loginName, String passwordMD5, HttpSession session);

    //注册
    String register(String loginName, String password);

    //用户信息修改并返回最新的用户信息
    MallUserVO updateUserInfo(MallUser mallUser, HttpSession httpSession);

}
