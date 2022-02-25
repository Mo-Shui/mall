package com.moshui.mall.dao;

import com.moshui.mall.entity.MallUser;

public interface MallUserMapper {

    //根据用户名和加密后的密码查询用户
    MallUser selectByLoginNameAndPasswd(String loginName, String passwordMD5);

    //根据用户名查询是否有相同名称的用户
    MallUser selectByLoginName(String loginName);

    //添加用户
    int insertSelective(MallUser mallUser);

}
