package com.moshui.mall.dao;

import com.moshui.mall.entity.IndexConfig;
import com.moshui.mall.util.PageQueryUtil;

import java.util.List;

public interface IndexConfigMapper {

    //列表
    List<IndexConfig> findIndexConfigList(PageQueryUtil pageQueryUtil);

    //获取总数
    int getTotalIndexConfigs(PageQueryUtil pageQueryUtil);

    //添加
    int insertSelective(IndexConfig indexConfig);

    //根据id查找
    IndexConfig selectByPrimaryKey(Long configId);

    //修改
    int updateByPrimaryKeySelective(IndexConfig indexConfig);

    //删除
    int deleteBatch(Long[] ids);

    //根据商品类型和商品获取商品
    List<IndexConfig> findIndexConfigsByTypeAndNum(int configType, int number);
}
