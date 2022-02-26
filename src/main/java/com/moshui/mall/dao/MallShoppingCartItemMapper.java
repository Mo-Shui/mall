package com.moshui.mall.dao;

import com.moshui.mall.entity.MallShoppingCartItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MallShoppingCartItemMapper {

    //根据用户id和商品id查询购物车项
    MallShoppingCartItem selectByUserIdAndGoodsId(@Param("newBeeMallUserId") Long newBeeMallUserId, @Param("goodsId") Long goodsId);

    //根据用户id查询该用户的购物车项数量
    int selectCountByUserId(Long userId);

    //添加
    int insertSelective(MallShoppingCartItem mallShoppingCartItem);

    //根据userId和number字段获取固定数量的购物项列表数据
    List<MallShoppingCartItem> selectByUserId(@Param("newBeeMallUserId") Long userId, int number);

    //根据购物项id查询购物项
    MallShoppingCartItem selectByPrimaryKey(Long cartItemId);

    //修改
    int updateByPrimaryKeySelective(MallShoppingCartItem shoppingCartItem);

    //删除
    int deleteByPrimaryKey(Long mallShoppingCartItemId);

    //根据购物项id删除购物项
    int deleteBatch(List<Long> cartItemIds);
}
