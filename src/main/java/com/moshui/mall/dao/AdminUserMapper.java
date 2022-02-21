package com.moshui.mall.dao;

import com.moshui.mall.entity.AdminUser;

public interface AdminUserMapper {

    //登录
    AdminUser login(String username,String password);

    //根据用户id获取用户信息
    AdminUser getUserDetailById(Integer userId);

    //修改用户信息
    Integer updateUserDetailByAdminUser(AdminUser adminUser);
}
