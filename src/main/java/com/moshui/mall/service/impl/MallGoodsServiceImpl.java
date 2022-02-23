package com.moshui.mall.service.impl;

import com.moshui.mall.common.ServiceResultEnum;
import com.moshui.mall.dao.GoodsMapper;
import com.moshui.mall.entity.Goods;
import com.moshui.mall.service.MallGoodsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class MallGoodsServiceImpl implements MallGoodsService {

    @Resource
    private GoodsMapper goodsMapper;

    //添加商品
    @Override
    public String saveMallGoods(Goods goods) {
        if (goodsMapper.insertSelective(goods) > 0){
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

}
