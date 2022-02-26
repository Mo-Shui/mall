package com.moshui.mall.service;

import com.moshui.mall.controller.vo.MallShoppingCartItemVO;
import com.moshui.mall.entity.MallShoppingCartItem;

import java.util.List;

public interface MallShoppingCartService {

    //将商品加入购物车
    String saveMallCartItem(MallShoppingCartItem mallShoppingCartItem);

    //根据用户id获取该用户的购物车数据
    List<MallShoppingCartItemVO> getMyShoppingCartItems(Long userId);

    //修改
    String updateMallCartItem(MallShoppingCartItem mallShoppingCartItem);

    //删除
    Boolean deleteById(Long mallShoppingCartItemId);
}
