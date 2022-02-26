package com.moshui.mall.service;

import com.moshui.mall.controller.vo.IndexCategoryVO;
import com.moshui.mall.controller.vo.SearchPageCategoryVO;
import com.moshui.mall.entity.GoodsCategory;
import com.moshui.mall.util.PageQueryUtil;
import com.moshui.mall.util.PageResult;

import java.util.List;
import java.util.Locale;

public interface MallCategoryService {
    //列表
    PageResult getCategorisPage(PageQueryUtil pageQueryUtil);

    //添加
    String saveCategory(GoodsCategory goodsCategory);

    //修改
    String updateGoodsCategory(GoodsCategory goodsCategory);

    //删除
    boolean delete(Integer[] ids);

    //根据parentId和level获取分类列表
    List<GoodsCategory> selectByLevelAndParentIdsAndNumber(List<Long> parentIds, int categoryLevel);

    //根据id获取
    GoodsCategory getGoodsCategoryById(Long categoryId);

    //获取主页的分类数据
    List<IndexCategoryVO> getCategoriesForIndex();

    //根据分类id获取分类（搜索用）
    SearchPageCategoryVO getCategoriesForSearch(Long categoryId);
}
