package com.moshui.mall.service;

import com.moshui.mall.entity.Carousel;
import com.moshui.mall.util.PageQueryUtil;
import com.moshui.mall.util.PageResult;

import java.util.List;

public interface MallCarouselService {

    //查询分页数据
    PageResult findCarouselList(PageQueryUtil pageResult);

    //增加新的轮播图
    String insert(Carousel carousel);

    //修改
    String update(Carousel carousel);

    //根据id查找
    Carousel findById(Integer id);

    //批量删除
    boolean deleteBatch(Integer[] ids);

    //返回固定数量的轮播图对象(首页调用)
    List<Carousel> getCarouselsForIndex(int indexCarouselNumber);
}
