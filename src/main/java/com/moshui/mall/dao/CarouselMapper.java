package com.moshui.mall.dao;

import com.moshui.mall.entity.Carousel;
import com.moshui.mall.util.PageQueryUtil;

import java.util.List;

public interface CarouselMapper {

    //根据轮播图id删除
    int deleteById(Integer id);

    //增加新的轮播图
    int insert(Carousel carousel);

    //根据id查找
    Carousel findById(Integer id);

    //修改
    int update(Carousel carousel);

    //查询分页数据
    List<Carousel> findCarouselList(PageQueryUtil pageResult);

    //查询总数
    int getTotalCarousels();

    //批量删除
    int deleteBatch(Integer[] ids);

    //查询固定数量的记录
    List<Carousel> findCarouselListByNumber(Integer number);

}
