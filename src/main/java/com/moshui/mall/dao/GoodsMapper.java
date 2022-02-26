package com.moshui.mall.dao;

import com.moshui.mall.controller.vo.StockNumDTO;
import com.moshui.mall.entity.Goods;
import com.moshui.mall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

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

    //根据搜索获取列表
    List<Goods> findMallGoodsListBySearch(PageQueryUtil pageQueryUtil);

    //根据搜索获取总数
    int getTotalMallGoodsBySearch(PageQueryUtil pageQueryUtil);

    //修改库存
    int updateStockNum(@Param("stockNumDTOS") List<StockNumDTO> stockNumDTOS);
}
