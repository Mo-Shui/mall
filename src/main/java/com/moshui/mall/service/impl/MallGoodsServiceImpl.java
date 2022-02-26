package com.moshui.mall.service.impl;

import com.moshui.mall.common.ServiceResultEnum;
import com.moshui.mall.dao.GoodsMapper;
import com.moshui.mall.entity.Goods;
import com.moshui.mall.service.MallGoodsService;
import com.moshui.mall.util.PageQueryUtil;
import com.moshui.mall.util.PageResult;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class MallGoodsServiceImpl implements MallGoodsService {

    @Resource
    private GoodsMapper goodsMapper;

    //添加商品
    @Override
    public String saveMallGoods(Goods goods) {
        if (goodsMapper.insertSelective(goods) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    //根据id获取商品
    @Override
    public Goods getMallGoodsById(Long goodsId) {
        return goodsMapper.selectByPrimaryKey(goodsId);
    }

    //修改商品
    @Override
    public String updateMallGoods(Goods goods) {
        Goods temp = goodsMapper.selectByPrimaryKey(goods.getGoodsId());
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }

        goods.setUpdateTime(new Date());
        if (goodsMapper.updateByPrimaryKeySelective(goods) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    //列表
    @Override
    public PageResult getMallGoodsPage(PageQueryUtil pageQueryUtil) {
        List<Goods> goods = goodsMapper.findMallGoodsList(pageQueryUtil);
        int total = goodsMapper.getTotalMallGoods(pageQueryUtil);
        PageResult pageResult = new PageResult(goods, total, pageQueryUtil.getLimit(), pageQueryUtil.getPage());
        return pageResult;
    }

    //批量修改销售状态
    @Override
    public boolean batchUpdateSellStatus(Long[] ids, int sellStatus) {
        return goodsMapper.batchUpdateSellStatus(ids,sellStatus) > 0;
    }

    //搜索数据列表
    @Override
    public PageResult searchMallGoods(PageQueryUtil pageQueryUtil) {
        List<Goods> mallGoodsList = goodsMapper.findMallGoodsListBySearch(pageQueryUtil);
        int totalMallGoods = goodsMapper.getTotalMallGoodsBySearch(pageQueryUtil);

        if (mallGoodsList != null){
            for (Goods goods : mallGoodsList) {
                String goodsName = goods.getGoodsName();
                String goodsIntro = goods.getGoodsIntro();
                // 字符串过长导致文字超出的问题
                if (goodsName.length() > 28) {
                    goodsName = goodsName.substring(0, 28) + "...";
                    goods.setGoodsName(goodsName);
                }
                if (goodsIntro.length() > 30) {
                    goodsIntro = goodsIntro.substring(0, 30) + "...";
                    goods.setGoodsIntro(goodsIntro);
                }
            }
        }

        PageResult pageResult = new PageResult(mallGoodsList,totalMallGoods, pageQueryUtil.getLimit(), pageQueryUtil.getPage());
        return pageResult;
    }

}
