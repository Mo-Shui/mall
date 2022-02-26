package com.moshui.mall.service;

import com.moshui.mall.controller.vo.MallUserVO;
import com.moshui.mall.entity.MallUser;
import com.moshui.mall.util.PageQueryUtil;
import com.moshui.mall.util.PageResult;

import javax.servlet.http.HttpSession;

public interface MallUserService {

    //登录
    String login(String loginName, String passwordMD5, HttpSession session);

    //注册
    String register(String loginName, String password);

    //用户信息修改并返回最新的用户信息
    MallUserVO updateUserInfo(MallUser mallUser, HttpSession httpSession);

    //后台分页
    PageResult getMallUsersPage(PageQueryUtil pageUtil);

    //用户禁用与解除禁用(0-未锁定 1-已锁定)
    boolean lockUsers(Integer[] ids, int lockStatus);
}
