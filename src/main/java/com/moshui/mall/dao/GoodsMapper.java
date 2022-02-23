package com.moshui.mall.dao;

import com.moshui.mall.entity.Goods;

public interface GoodsMapper {

    //添加商品
    int insertSelective(Goods goods);

}
