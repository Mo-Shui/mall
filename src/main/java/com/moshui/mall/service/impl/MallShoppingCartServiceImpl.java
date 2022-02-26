package com.moshui.mall.service.impl;

import com.moshui.mall.common.Constants;
import com.moshui.mall.common.ServiceResultEnum;
import com.moshui.mall.controller.vo.MallShoppingCartItemVO;
import com.moshui.mall.dao.GoodsMapper;
import com.moshui.mall.dao.MallShoppingCartItemMapper;
import com.moshui.mall.entity.Goods;
import com.moshui.mall.entity.MallShoppingCartItem;
import com.moshui.mall.service.MallShoppingCartService;
import com.moshui.mall.util.BeanUtil;
import com.moshui.mall.util.ResultGenerator;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.*;

@Service
public class MallShoppingCartServiceImpl implements MallShoppingCartService {

    @Resource
    private MallShoppingCartItemMapper mallShoppingCartItemMapper;

    @Resource
    private GoodsMapper goodsMapper;

    //将商品加入购物车
    @Override
    public String saveMallCartItem(MallShoppingCartItem mallShoppingCartItem) {
        MallShoppingCartItem item = mallShoppingCartItemMapper.selectByUserIdAndGoodsId(mallShoppingCartItem.getUserId(),mallShoppingCartItem.getGoodsId());
        if (item != null){
            return "购物车中已存在";
        }
        Goods goods = goodsMapper.selectByPrimaryKey(mallShoppingCartItem.getGoodsId());
        if (goods == null){
            return ServiceResultEnum.GOODS_NOT_EXIST.getResult();
        }

        //超出单个商品的最大数量
        if (mallShoppingCartItem.getGoodsCount() > Constants.SHOPPING_CART_ITEM_LIMIT_NUMBER){
            return ServiceResultEnum.SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR.getResult();
        }

        //超出购物车最大数量
        int total = mallShoppingCartItemMapper.selectCountByUserId(mallShoppingCartItem.getUserId()) + 1;
        if (total > Constants.SHOPPING_CART_ITEM_TOTAL_NUMBER){
            return ServiceResultEnum.SHOPPING_CART_ITEM_TOTAL_NUMBER_ERROR.getResult();
        }

        if (mallShoppingCartItemMapper.insertSelective(mallShoppingCartItem) > 0){
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    //根据用户id获取该用户的购物车数据
    @Override
    public List<MallShoppingCartItemVO> getMyShoppingCartItems(Long userId) {
        List<MallShoppingCartItemVO> mallShoppingCartItemVOS = new ArrayList<>();

        List<MallShoppingCartItem> mallShoppingCartItems = mallShoppingCartItemMapper.selectByUserId(userId,Constants.SHOPPING_CART_ITEM_TOTAL_NUMBER);
        if (mallShoppingCartItems != null && mallShoppingCartItems.size() > 0){
            List<Long> goodsIds = new ArrayList<>();
            for (MallShoppingCartItem mallShoppingCartItem : mallShoppingCartItems) {
                goodsIds.add(mallShoppingCartItem.getGoodsId());
            }
            List<Goods> goods = goodsMapper.selectByPrimaryKeys(goodsIds);
            Map<Long,Goods> goodsMap = new HashMap<>();
            if (goods != null){
                for (Goods good : goods) {
                    goodsMap.put(good.getGoodsId(),good);
                }
            }
            for (MallShoppingCartItem mallShoppingCartItem : mallShoppingCartItems) {
                MallShoppingCartItemVO mallShoppingCartItemVO = new MallShoppingCartItemVO();
                BeanUtil.copyProperties(mallShoppingCartItem,mallShoppingCartItemVO);

                if (goodsMap.containsKey(mallShoppingCartItem.getGoodsId())){
                    Goods mallGoodsTemp = goodsMap.get(mallShoppingCartItem.getGoodsId());

                    mallShoppingCartItemVO.setGoodsCoverImg(mallGoodsTemp.getGoodsCoverImg());
                    mallShoppingCartItemVO.setSellingPrice(mallGoodsTemp.getSellingPrice());

                    String goodsName = mallGoodsTemp.getGoodsName();
                    // 字符串过长导致文字超出的问题
                    if (goodsName.length() > 28) {
                        goodsName = goodsName.substring(0, 28) + "...";
                    }
                    mallShoppingCartItemVO.setGoodsName(goodsName);

                    mallShoppingCartItemVOS.add(mallShoppingCartItemVO);
                }
            }
        }

        return mallShoppingCartItemVOS;
    }

    //修改
    @Override
    public String updateMallCartItem(MallShoppingCartItem mallShoppingCartItem) {
        MallShoppingCartItem shoppingCartItem = mallShoppingCartItemMapper.selectByPrimaryKey(mallShoppingCartItem.getCartItemId());
        if (shoppingCartItem == null){
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }

        //超出单个商品的最大数量
        if (mallShoppingCartItem.getGoodsCount() > Constants.SHOPPING_CART_ITEM_LIMIT_NUMBER){
            return ServiceResultEnum.SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR.getResult();
        }
        shoppingCartItem.setGoodsCount(mallShoppingCartItem.getGoodsCount());
        shoppingCartItem.setUpdateTime(new Date());

        if (mallShoppingCartItemMapper.updateByPrimaryKeySelective(shoppingCartItem) > 0){
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    //删除
    @Override
    public Boolean deleteById(Long mallShoppingCartItemId) {
        return mallShoppingCartItemMapper.deleteByPrimaryKey(mallShoppingCartItemId) > 0;
    }

}
