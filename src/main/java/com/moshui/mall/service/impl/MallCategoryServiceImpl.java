package com.moshui.mall.service.impl;

import com.moshui.mall.common.ServiceResultEnum;
import com.moshui.mall.dao.GoodsCategoryMapper;
import com.moshui.mall.entity.GoodsCategory;
import com.moshui.mall.service.MallCategoryService;
import com.moshui.mall.util.PageQueryUtil;
import com.moshui.mall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class MallCategoryServiceImpl implements MallCategoryService {

    @Resource
    private GoodsCategoryMapper goodsCategoryMapper;

    //列表
    @Override
    public PageResult getCategorisPage(PageQueryUtil pageQueryUtil) {
        List<GoodsCategory> goodsCategoryList = goodsCategoryMapper.findGoodsCategoryList(pageQueryUtil);
        int total = goodsCategoryMapper.getTotalGoodsCategories(pageQueryUtil);
        return new PageResult(goodsCategoryList,total, pageQueryUtil.getLimit(), pageQueryUtil.getPage());
    }

    //添加
    @Override
    public String saveCategory(GoodsCategory goodsCategory) {
        GoodsCategory temp = goodsCategoryMapper.selectByLevelAndName(goodsCategory.getCategoryLevel(),goodsCategory.getCategoryName());
        if (temp != null){
            return ServiceResultEnum.SAME_CATEGORY_EXIST.getResult();
        }

        if (goodsCategoryMapper.insert(goodsCategory) > 0){
            return ServiceResultEnum.SUCCESS.getResult();
        }

        return ServiceResultEnum.DB_ERROR.getResult();
    }

    //修改
    @Override
    public String updateGoodsCategory(GoodsCategory goodsCategory) {
        GoodsCategory temp = goodsCategoryMapper.selectByPrimaryKey(goodsCategory.getCategoryId());
        if (temp == null){
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }

        GoodsCategory temp2 = goodsCategoryMapper.selectByLevelAndName(goodsCategory.getCategoryLevel(),goodsCategory.getCategoryName());
        if (temp2 != null && !temp2.getCategoryId().equals(goodsCategory.getCategoryId())){
            return ServiceResultEnum.SAME_CATEGORY_EXIST.getResult();
        }

        goodsCategory.setUpdateTime(new Date());

        if (goodsCategoryMapper.update(goodsCategory) > 0){
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    //删除
    @Override
    public boolean delete(Integer[] ids) {
        if (ids.length < 1) {
            return false;
        }
        return goodsCategoryMapper.delete(ids) > 0;
    }

    //根据parentId和level获取分类列表
    @Override
    public List<GoodsCategory> selectByLevelAndParentIdsAndNumber(List<Long> parentIds, int categoryLevel) {
        //0代表查询所有
        return goodsCategoryMapper.selectByLevelAndParentIdsAndNumber(parentIds,categoryLevel,0);
    }

    //根据id获取
    @Override
    public GoodsCategory getGoodsCategoryById(Long categoryId) {
        return goodsCategoryMapper.selectByPrimaryKey(categoryId);
    }

}
