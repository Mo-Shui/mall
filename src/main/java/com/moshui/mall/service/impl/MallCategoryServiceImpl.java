package com.moshui.mall.service.impl;

import com.moshui.mall.common.Constants;
import com.moshui.mall.common.MallCategoryLevelEnum;
import com.moshui.mall.common.ServiceResultEnum;
import com.moshui.mall.controller.vo.IndexCategoryVO;
import com.moshui.mall.controller.vo.SecondLevelCategoryVO;
import com.moshui.mall.controller.vo.ThirdLevelCategoryVO;
import com.moshui.mall.dao.GoodsCategoryMapper;
import com.moshui.mall.entity.GoodsCategory;
import com.moshui.mall.service.MallCategoryService;
import com.moshui.mall.util.BeanUtil;
import com.moshui.mall.util.PageQueryUtil;
import com.moshui.mall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

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

    //获取主页的分类数据
    @Override
    public List<IndexCategoryVO> getCategoriesForIndex() {
        List<IndexCategoryVO> indexCategoryVOS = new ArrayList<>();
        //获取一级分类数据
        List<GoodsCategory> firstLevelCategories = goodsCategoryMapper.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), MallCategoryLevelEnum.LEVEL_ONE.getLevel(), Constants.INDEX_CATEGORY_NUMBER);
        if (firstLevelCategories != null){
            //获取一级分类id
            List<Long> firstLevelCategoryIds = getCategoryIds(firstLevelCategories);
            //获取二级分类数据
            List<GoodsCategory> secondLevelCategories = goodsCategoryMapper.selectByLevelAndParentIdsAndNumber(firstLevelCategoryIds, MallCategoryLevelEnum.LEVEL_TWO.getLevel(), 0);
            if (secondLevelCategories != null){
                //获取二级分类id
                List<Long> secondLevelCategoryIds = getCategoryIds(secondLevelCategories);
                //获取三级分类数据
                List<GoodsCategory> thirdLevelCategories = goodsCategoryMapper.selectByLevelAndParentIdsAndNumber(secondLevelCategoryIds, MallCategoryLevelEnum.LEVEL_THREE.getLevel(), 0);
                if (thirdLevelCategories != null){
                    Map<Long,List<GoodsCategory>> thirdLevelCategoryMap = getGoodsCategoryMap(thirdLevelCategories);

                    //处理二级分类
                    List<SecondLevelCategoryVO> secondLevelCategoryVOS = new ArrayList<>();
                    for (GoodsCategory secondLevelCategory : secondLevelCategories) {
                        SecondLevelCategoryVO secondLevelCategoryVO = new SecondLevelCategoryVO();
                        BeanUtil.copyProperties(secondLevelCategory,secondLevelCategoryVO);
                        if (thirdLevelCategoryMap.containsKey(secondLevelCategory.getCategoryId())){
                            List<GoodsCategory> list = thirdLevelCategoryMap.get(secondLevelCategory.getCategoryId());
                            secondLevelCategoryVO.setThirdLevelCategoryVOS(BeanUtil.copyList(list, ThirdLevelCategoryVO.class));
                            secondLevelCategoryVOS.add(secondLevelCategoryVO);
                        }
                    }

                    //处理一级分类
                    if (secondLevelCategoryVOS != null){
                        Map<Long, List<SecondLevelCategoryVO>> secondLevelCategoryVOMap = getGoodsCategoryMap2(secondLevelCategoryVOS);
                        for (GoodsCategory firstLevelCategory : firstLevelCategories) {
                            IndexCategoryVO indexCategoryVO = new IndexCategoryVO();
                            BeanUtil.copyProperties(firstLevelCategory,indexCategoryVO);
                            if (secondLevelCategoryVOMap.containsKey(firstLevelCategory.getCategoryId())){
                                List<SecondLevelCategoryVO> secondLevelCategoryVOS1 = secondLevelCategoryVOMap.get(firstLevelCategory.getCategoryId());
                                indexCategoryVO.setSecondLevelCategoryVOS(secondLevelCategoryVOS1);
                                indexCategoryVOS.add(indexCategoryVO);
                            }
                        }
                    }
                }
            }

            return indexCategoryVOS;
        }else{
            return null;
        }
    }

    //根据parentId将categories分组
    private Map<Long, List<SecondLevelCategoryVO>> getGoodsCategoryMap2(List<SecondLevelCategoryVO> categories) {
        Map<Long, List<SecondLevelCategoryVO>> map = new HashMap<>();
        for (SecondLevelCategoryVO category : categories) {
            List<SecondLevelCategoryVO> list = new ArrayList<>();
            Long parentId = category.getParentId();
            for (SecondLevelCategoryVO goodsCategory : categories) {
                if (parentId == goodsCategory.getParentId()){
                    list.add(goodsCategory);
                }
            }
            map.put(parentId,list);
        }

        return map;
    }

    //根据parentId将categories分组
    private Map<Long, List<GoodsCategory>> getGoodsCategoryMap(List<GoodsCategory> categories) {
        Map<Long, List<GoodsCategory>> map = new HashMap<>();
        for (GoodsCategory category : categories) {
            List<GoodsCategory> list = new ArrayList<>();
            Long parentId = category.getParentId();
            for (GoodsCategory goodsCategory : categories) {
                if (parentId == goodsCategory.getParentId()){
                    list.add(goodsCategory);
                }
            }
            map.put(parentId,list);
        }

        return map;
    }

    //获取分类数据的id
    private List<Long> getCategoryIds(List<GoodsCategory> categories) {
        List<Long> ids = new ArrayList<>();
        for (GoodsCategory category : categories) {
            ids.add(category.getCategoryId());
        }
        return ids;
    }

}
