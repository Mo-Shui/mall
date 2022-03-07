package com.moshui.mall.controller.mall;

import com.moshui.mall.common.Constants;
import com.moshui.mall.common.MallException;
import com.moshui.mall.common.ServiceResultEnum;
import com.moshui.mall.controller.vo.MallGoodsDetailVO;
import com.moshui.mall.controller.vo.SearchPageCategoryVO;
import com.moshui.mall.entity.Goods;
import com.moshui.mall.service.MallCategoryService;
import com.moshui.mall.service.MallGoodsService;
import com.moshui.mall.util.BeanUtil;
import com.moshui.mall.util.PageQueryUtil;
import com.moshui.mall.util.PageResult;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class GoodsController {

    @Resource
    private MallCategoryService mallCategoryService;

    @Resource
    private MallGoodsService mallGoodsService;

    //搜索
    @RequestMapping({"/search","/search.html"})
    public String search(HttpServletRequest request, @RequestParam Map params){
        if (StringUtils.isEmpty(params.get("page"))){
            params.put("page",1);
        }
        params.put("limit", Constants.GOODS_SEARCH_PAGE_LIMIT);

        //封装分类数据
        if (params.containsKey("goodsCategoryId") && !StringUtils.isEmpty(params.get("goodsCategoryId") + "")){
            Long categoryId = Long.valueOf(params.get("goodsCategoryId").toString());
            SearchPageCategoryVO searchPageCategoryVO =mallCategoryService.getCategoriesForSearch(categoryId);
            if (searchPageCategoryVO != null) {
                request.setAttribute("goodsCategoryId", categoryId);
                request.setAttribute("searchPageCategoryVO", searchPageCategoryVO);
            }
        }

        //封装参数供前端回显
        if (params.containsKey("orderBy") && !StringUtils.isEmpty(params.get("orderBy") + "")) {
            request.setAttribute("orderBy", params.get("orderBy") + "");
        }

        String keyword = "";
        //对keyword做过滤
        if (params.containsKey("keyword") && !StringUtils.isEmpty((params.get("keyword") + "").trim())) {
            keyword = params.get("keyword") + "";
        }
        request.setAttribute("keyword", keyword);
        params.put("keyword", keyword);

        //封装商品数据
        PageQueryUtil pageQueryUtil = new PageQueryUtil(params);
        request.setAttribute("pageResult", mallGoodsService.searchMallGoods(pageQueryUtil));

        return "mall/search";
    }

    //获取商品的详细信息
    @RequestMapping("/goods/detail/{goodsId}")
    public String detail(HttpServletRequest request,@PathVariable("goodsId")Long goodsId){
        if (goodsId < 1) {
            MallException.fail("参数异常");
        }

        Goods goods = mallGoodsService.getMallGoodsById(goodsId);
        if (Constants.SELL_STATUS_UP != goods.getGoodsSellStatus()) {
            MallException.fail(ServiceResultEnum.GOODS_PUT_DOWN.getResult());
        }

        MallGoodsDetailVO goodsDetailVO = new MallGoodsDetailVO();
        BeanUtil.copyProperties(goods, goodsDetailVO);
        goodsDetailVO.setGoodsCarouselList(goods.getGoodsCarousel().split(","));
        request.setAttribute("goodsDetail", goodsDetailVO);
        return "mall/detail";
    }

}
