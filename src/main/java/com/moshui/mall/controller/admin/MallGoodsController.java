package com.moshui.mall.controller.admin;

import com.moshui.mall.common.MallCategoryLevelEnum;
import com.moshui.mall.common.ServiceResultEnum;
import com.moshui.mall.entity.Goods;
import com.moshui.mall.entity.GoodsCategory;
import com.moshui.mall.service.MallCategoryService;
import com.moshui.mall.service.MallGoodsService;
import com.moshui.mall.util.Result;
import com.moshui.mall.util.ResultGenerator;
import org.apache.ibatis.ognl.DynamicSubscript;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.apache.ibatis.ognl.DynamicSubscript.first;

@Controller
@RequestMapping("/admin")
public class MallGoodsController {

    @Resource
    private MallCategoryService mallCategoryService;

    @Resource
    private MallGoodsService mallGoodsService;

    @RequestMapping("/goods/edit")
    public String edit(HttpServletRequest request){
        request.setAttribute("path","edit");

        List<GoodsCategory> first = mallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), MallCategoryLevelEnum.LEVEL_ONE.getLevel());
        if (first != null){
            List<GoodsCategory> second = mallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(first.get(0).getCategoryId()), MallCategoryLevelEnum.LEVEL_TWO.getLevel());
            if (second != null){
                List<GoodsCategory> third = mallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(second.get(0).getCategoryId()), MallCategoryLevelEnum.LEVEL_THREE.getLevel());

                request.setAttribute("firstLevelCategories", first);
                request.setAttribute("secondLevelCategories", second);
                request.setAttribute("thirdLevelCategories", third);
                request.setAttribute("path", "goods-edit");
                return "/admin/mall_goods_edit";
            }
        }

        return "error/error_5xx";
    }

    //添加商品
    @RequestMapping("/goods/save")
    @ResponseBody
    private Result save(@RequestBody Goods goods){
        if (StringUtils.isEmpty(goods.getGoodsName())
                || StringUtils.isEmpty(goods.getGoodsIntro())
                || StringUtils.isEmpty(goods.getTag())
                || Objects.isNull(goods.getOriginalPrice())
                || Objects.isNull(goods.getGoodsCategoryId())
                || Objects.isNull(goods.getSellingPrice())
                || Objects.isNull(goods.getStockNum())
                || Objects.isNull(goods.getGoodsSellStatus())
                || StringUtils.isEmpty(goods.getGoodsCoverImg())
                || StringUtils.isEmpty(goods.getGoodsDetailContent())) {
            return ResultGenerator.genFailResult("参数异常！");
        }

        String result = mallGoodsService.saveMallGoods(goods);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)){
            return ResultGenerator.genSuccessResult();
        }
        return ResultGenerator.genFailResult(result);
    }

}
