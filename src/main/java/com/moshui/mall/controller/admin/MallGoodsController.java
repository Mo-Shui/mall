package com.moshui.mall.controller.admin;

import com.moshui.mall.common.MallCategoryLevelEnum;
import com.moshui.mall.entity.GoodsCategory;
import com.moshui.mall.service.MallCategoryService;
import org.apache.ibatis.ognl.DynamicSubscript;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

import static org.apache.ibatis.ognl.DynamicSubscript.first;

@Controller
@RequestMapping("/admin")
public class MallGoodsController {

    @Resource
    private MallCategoryService mallCategoryService;

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

}
