package com.moshui.mall.service;

import com.moshui.mall.entity.Goods;
import com.moshui.mall.util.PageQueryUtil;
import com.moshui.mall.util.PageResult;

public interface MallGoodsService {

    //添加商品
    String saveMallGoods(Goods goods);

    //根据id获取商品
    Goods getMallGoodsById(Long goodsId);

    //修改商品
    String updateMallGoods(Goods goods);

    //列表
    PageResult getMallGoodsPage(PageQueryUtil pageQueryUtil);

    //批量修改销售状态
    boolean batchUpdateSellStatus(Long[] ids, int sellStatus);

    //搜索数据列表
    PageResult searchMallGoods(PageQueryUtil pageQueryUtil);
}
