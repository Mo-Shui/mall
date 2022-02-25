package com.moshui.mall.service;

import com.moshui.mall.common.IndexConfigTypeEnum;
import com.moshui.mall.entity.Goods;
import com.moshui.mall.entity.IndexConfig;
import com.moshui.mall.util.PageQueryUtil;
import com.moshui.mall.util.PageResult;

import java.util.List;

public interface MallIndexConfigService {

    //列表
    PageResult getConfigsPage(PageQueryUtil pageQueryUtil);

    //添加
    String saveIndexConfig(IndexConfig indexConfig);

    //修改
    String updateIndexConfig(IndexConfig indexConfig);

    //删除
    boolean deleteBatch(Long[] ids);

    //返回固定数量的首页配置商品对象(首页调用)
    List<Goods> getConfigGoodsesForIndex(int configType, int number);
}
