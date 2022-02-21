package com.moshui.mall.service;

import com.moshui.mall.entity.AdminUser;

public interface AdminUserService {

    //登录
    AdminUser login(String username, String password);

    //根据用户id获取用户信息
    AdminUser getUserDetailById(Integer userId);

    //根据用户id修改用户名
    boolean updateNameById(Integer userId, String loginUserName, String nickName);

    //根据用户id修改密码
    boolean updatePasswordById(Integer userId, String originalPassword, String newPassword);
}
