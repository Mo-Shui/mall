package com.moshui.mall.service.impl;

import com.moshui.mall.common.ServiceResultEnum;
import com.moshui.mall.dao.CarouselMapper;
import com.moshui.mall.entity.Carousel;
import com.moshui.mall.service.MallCarouselService;
import com.moshui.mall.util.PageQueryUtil;
import com.moshui.mall.util.PageResult;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class MallCarouselServiceImpl implements MallCarouselService {

    @Resource
    private CarouselMapper carouselMapper;

    //查询分页数据
    @Override
    public PageResult findCarouselList(PageQueryUtil pageResult) {
        List<Carousel> carouselList = carouselMapper.findCarouselList(pageResult);
        int totalCarousels = carouselMapper.getTotalCarousels();
        return new PageResult(carouselList,totalCarousels, pageResult.getLimit(), pageResult.getPage());
    }

    //增加新的轮播图
    @Override
    public String insert(Carousel carousel) {
        if (carouselMapper.insert(carousel) > 0){
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    //修改
    @Override
    public String update(Carousel carousel) {
        //查询是否有这个轮播图
        Carousel temp = carouselMapper.findById(carousel.getCarouselId());
        if (temp == null){
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }

        carousel.setUpdateTime(new Date());
        if (carouselMapper.update(carousel) > 0){
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    //根据id查找
    @Override
    public Carousel findById(Integer id) {
        return carouselMapper.findById(id);
    }

    //批量删除
    @Override
    public boolean deleteBatch(Integer[] ids) {
        if (ids.length < 1){
            return false;
        }
        return carouselMapper.deleteBatch(ids) > 0;
    }

    //返回固定数量的轮播图对象(首页调用)
    @Override
    public List<Carousel> getCarouselsForIndex(int indexCarouselNumber) {
        return carouselMapper.findCarouselListByNumber(indexCarouselNumber);
    }
}
