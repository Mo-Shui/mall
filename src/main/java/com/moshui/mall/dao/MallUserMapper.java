package com.moshui.mall.dao;

import com.moshui.mall.entity.MallUser;
import com.moshui.mall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MallUserMapper {

    //根据用户名和加密后的密码查询用户
    MallUser selectByLoginNameAndPasswd(String loginName, String passwordMD5);

    //根据用户名查询是否有相同名称的用户
    MallUser selectByLoginName(String loginName);

    //添加用户
    int insertSelective(MallUser mallUser);

    MallUser selectByPrimaryKey(Long userId);

    int updateByPrimaryKeySelective(MallUser mallUser);

    List<MallUser> findMallUserList(PageQueryUtil pageUtil);

    int getTotalMallUsers(PageQueryUtil pageUtil);

    int lockUserBatch(@Param("ids") Integer[] ids, @Param("lockStatus") int lockStatus);
}
