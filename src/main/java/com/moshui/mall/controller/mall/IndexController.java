package com.moshui.mall.controller.mall;

import com.moshui.mall.common.Constants;
import com.moshui.mall.common.IndexConfigTypeEnum;
import com.moshui.mall.controller.vo.IndexCategoryVO;
import com.moshui.mall.entity.Carousel;
import com.moshui.mall.entity.Goods;
import com.moshui.mall.entity.GoodsCategory;
import com.moshui.mall.service.MallCarouselService;
import com.moshui.mall.service.MallCategoryService;
import com.moshui.mall.service.MallIndexConfigService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class IndexController {

    @Resource
    private MallCarouselService mallCarouselService;

    @Resource
    private MallCategoryService mallCategoryService;

    @Resource
    private MallIndexConfigService mallIndexConfigService;

    //商城主页
    @RequestMapping({"/","/index","index.html"})
    public String index(HttpServletRequest request){
        List<Carousel> carousels = mallCarouselService.getCarouselsForIndex(Constants.INDEX_CAROUSEL_NUMBER);
        List<IndexCategoryVO> categories = mallCategoryService.getCategoriesForIndex();
        request.setAttribute("carousels",carousels);
        request.setAttribute("categories",categories);

        List<Goods> hotGoodses = mallIndexConfigService.getConfigGoodsesForIndex(IndexConfigTypeEnum.INDEX_GOODS_HOT.getType(), Constants.INDEX_GOODS_HOT_NUMBER);
        List<Goods> newGoodses = mallIndexConfigService.getConfigGoodsesForIndex(IndexConfigTypeEnum.INDEX_GOODS_NEW.getType(), Constants.INDEX_GOODS_NEW_NUMBER);
        List<Goods> recommendGoodses = mallIndexConfigService.getConfigGoodsesForIndex(IndexConfigTypeEnum.INDEX_GOODS_RECOMMOND.getType(), Constants.INDEX_GOODS_RECOMMOND_NUMBER);
        request.setAttribute("hotGoodses", hotGoodses);//热销商品
        request.setAttribute("newGoodses", newGoodses);//新品
        request.setAttribute("recommendGoodses", recommendGoodses);//推荐商品

        return "mall/index";
    }

}
