package com.moshui.mall.dao;

import com.moshui.mall.entity.GoodsCategory;
import com.moshui.mall.util.PageQueryUtil;

import java.util.List;

public interface GoodsCategoryMapper {

    //列表
    List<GoodsCategory> findGoodsCategoryList(PageQueryUtil pageQueryUtil);

    //获取总条数
    int getTotalGoodsCategories(PageQueryUtil pageQueryUtil);

    //根据分类级别和分类名称查询
    GoodsCategory selectByLevelAndName(Byte categoryLevel, String categoryName);

    //添加
    int insert(GoodsCategory goodsCategory);

    //修改
    int update(GoodsCategory goodsCategory);

    //根据id查询
    GoodsCategory selectByPrimaryKey(Long categoryId);

    //删除
    int delete(Integer[] ids);

    //根据parentId和level获取分类列表，number为0时表示查询所有
    List<GoodsCategory> selectByLevelAndParentIdsAndNumber(List<Long> parentIds, int categoryLevel, int number);
}
