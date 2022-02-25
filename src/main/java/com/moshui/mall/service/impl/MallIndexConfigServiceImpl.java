package com.moshui.mall.service.impl;

import com.moshui.mall.common.IndexConfigTypeEnum;
import com.moshui.mall.common.ServiceResultEnum;
import com.moshui.mall.dao.GoodsMapper;
import com.moshui.mall.dao.IndexConfigMapper;
import com.moshui.mall.entity.Goods;
import com.moshui.mall.entity.IndexConfig;
import com.moshui.mall.service.MallIndexConfigService;
import com.moshui.mall.util.PageQueryUtil;
import com.moshui.mall.util.PageResult;
import org.springframework.stereotype.Service;
import org.w3c.dom.stylesheets.LinkStyle;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class MallIndexConfigServiceImpl implements MallIndexConfigService {

    @Resource
    private IndexConfigMapper indexConfigMapper;

    @Resource
    private GoodsMapper goodsMapper;

    //列表
    @Override
    public PageResult getConfigsPage(PageQueryUtil pageQueryUtil) {
        List<IndexConfig> list = indexConfigMapper.findIndexConfigList(pageQueryUtil);
        int total = indexConfigMapper.getTotalIndexConfigs(pageQueryUtil);
        return new PageResult(list,total,pageQueryUtil.getLimit(), pageQueryUtil.getPage());
    }

    //添加
    @Override
    public String saveIndexConfig(IndexConfig indexConfig) {
        if (indexConfigMapper.insertSelective(indexConfig) > 0){
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    //修改
    @Override
    public String updateIndexConfig(IndexConfig indexConfig) {
        IndexConfig temp = indexConfigMapper.selectByPrimaryKey(indexConfig.getConfigId());
        if (temp == null){
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }

        if (indexConfigMapper.updateByPrimaryKeySelective(indexConfig) > 0){
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    //删除
    @Override
    public boolean deleteBatch(Long[] ids) {
        if (ids.length < 1) {
            return false;
        }
        return indexConfigMapper.deleteBatch(ids) > 0;
    }

    //返回固定数量的首页配置商品对象(首页调用)
    @Override
    public List<Goods> getConfigGoodsesForIndex(int configType, int number) {
        List<Goods> list = new ArrayList<>();
        List<IndexConfig> indexConfigs = indexConfigMapper.findIndexConfigsByTypeAndNum(configType,number);
        if (indexConfigs != null){
            List<Long> goodsIds = getGoodsIds(indexConfigs);
            list = goodsMapper.selectByPrimaryKeys(goodsIds);
            for (Goods goods : list) {
                String goodsName = goods.getGoodsName();
                String goodsIntro = goods.getGoodsIntro();
                // 字符串过长导致文字超出的问题
                if (goodsName.length() > 30) {
                    goodsName = goodsName.substring(0, 30) + "...";
                    goods.setGoodsName(goodsName);
                }
                if (goodsIntro.length() > 22) {
                    goodsIntro = goodsIntro.substring(0, 22) + "...";
                    goods.setGoodsIntro(goodsIntro);
                }
            }
        }
        return list;
    }

    //获取所有id
    private List<Long> getGoodsIds(List<IndexConfig> indexConfigs) {
        List<Long> list = new ArrayList<>();
        for (IndexConfig indexConfig : indexConfigs) {
            list.add(indexConfig.getGoodsId());
        }
        return list;
    }

}
