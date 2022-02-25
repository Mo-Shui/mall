package com.moshui.mall.dao;

import com.moshui.mall.entity.Goods;
import com.moshui.mall.util.PageQueryUtil;

import java.util.List;

public interface GoodsMapper {

    //添加商品
    int insertSelective(Goods goods);

    //根据id获取商品
    Goods selectByPrimaryKey(Long goodsId);

    //修改商品
    int updateByPrimaryKeySelective(Goods goods);

    //列表
    List<Goods> findMallGoodsList(PageQueryUtil pageQueryUtil);

    //获取商品总数
    int getTotalMallGoods(PageQueryUtil pageQueryUtil);

    //批量修改销售状态
    int batchUpdateSellStatus(Long[] ids, int sellStatus);

    //根据多个id获取商品
    List<Goods> selectByPrimaryKeys(List<Long> goodsIds);
}
