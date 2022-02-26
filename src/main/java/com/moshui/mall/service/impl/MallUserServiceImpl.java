package com.moshui.mall.service.impl;

import com.moshui.mall.common.Constants;
import com.moshui.mall.common.ServiceResultEnum;
import com.moshui.mall.controller.vo.MallUserVO;
import com.moshui.mall.dao.MallUserMapper;
import com.moshui.mall.entity.MallUser;
import com.moshui.mall.service.MallUserService;
import com.moshui.mall.util.BeanUtil;
import com.moshui.mall.util.MD5Util;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@Service
public class MallUserServiceImpl implements MallUserService {

    @Resource
    private MallUserMapper mallUserMapper;

    //登录
    @Override
    public String login(String loginName, String passwordMD5, HttpSession session) {
        MallUser mallUser = mallUserMapper.selectByLoginNameAndPasswd(loginName,passwordMD5);
        if (mallUser != null && session != null){
            //该用户是否已锁定
            if (mallUser.getLockedFlag() == 1){
                return ServiceResultEnum.LOGIN_USER_LOCKED.getResult();
            }

            //昵称太长 影响页面展示
            if (mallUser.getNickName() != null && mallUser.getNickName().length() > 7) {
                String tempNickName = mallUser.getNickName().substring(0, 7) + "..";
                mallUser.setNickName(tempNickName);
            }

            MallUserVO mallUserVO = new MallUserVO();
            BeanUtil.copyProperties(mallUser, mallUserVO);
            session.setAttribute(Constants.MALL_USER_SESSION_KEY,mallUserVO);

            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.LOGIN_ERROR.getResult();
    }

    //注册
    @Override
    public String register(String loginName, String password) {
        if (mallUserMapper.selectByLoginName(loginName) != null){
            return ServiceResultEnum.SAME_LOGIN_NAME_EXIST.getResult();
        }

        MallUser mallUser = new MallUser();
        mallUser.setLoginName(loginName);
        mallUser.setNickName(loginName);
        mallUser.setPasswordMd5(MD5Util.MD5Encode(password,"UTF-8"));
        if (mallUserMapper.insertSelective(mallUser) > 0){
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

}
