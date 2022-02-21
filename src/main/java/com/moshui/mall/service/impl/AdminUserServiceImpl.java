package com.moshui.mall.service.impl;

import com.moshui.mall.dao.AdminUserMapper;
import com.moshui.mall.entity.AdminUser;
import com.moshui.mall.service.AdminUserService;
import com.moshui.mall.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.security.provider.MD5;

import javax.annotation.Resource;

@Service
@Transactional
public class AdminUserServiceImpl implements AdminUserService {

    @Resource
    private AdminUserMapper adminUserMapper;

    //登录
    @Override
    public AdminUser login(String username, String password) {
        return adminUserMapper.login(username, MD5Util.MD5Encode(password,"UTF-8"));
    }

    //根据用户id获取用户信息
    @Override
    public AdminUser getUserDetailById(Integer userId) {
        return adminUserMapper.getUserDetailById(userId);
    }

    //根据用户id修改用户名
    @Override
    public boolean updateNameById(Integer userId, String loginUserName, String nickName) {
        AdminUser adminUser = adminUserMapper.getUserDetailById(userId);
        if (adminUser != null){
            adminUser.setLoginUserName(loginUserName);
            adminUser.setNickName(nickName);
            if(adminUserMapper.updateUserDetailByAdminUser(adminUser) > 0){
                return true;
            }
        }
        return false;
    }

    //根据用户id修改密码
    @Override
    public boolean updatePasswordById(Integer userId, String originalPassword, String newPassword) {
        AdminUser adminUser = adminUserMapper.getUserDetailById(userId);
        if (adminUser != null){
            String originalPasswordMD5 = MD5Util.MD5Encode(originalPassword, "UTF-8");
            if (originalPasswordMD5.equals(adminUser.getLoginPassword())){
                adminUser.setLoginPassword(MD5Util.MD5Encode(newPassword,"UTF-8"));
                if(adminUserMapper.updateUserDetailByAdminUser(adminUser) > 0){
                    return true;
                }
            }
        }
        return false;
    }

}
