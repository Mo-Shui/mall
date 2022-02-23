package com.moshui.mall.controller.admin;

import com.moshui.mall.common.MallCategoryLevelEnum;
import com.moshui.mall.common.ServiceResultEnum;
import com.moshui.mall.entity.GoodsCategory;
import com.moshui.mall.service.MallCategoryService;
import com.moshui.mall.util.PageQueryUtil;
import com.moshui.mall.util.PageResult;
import com.moshui.mall.util.Result;
import com.moshui.mall.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@RequestMapping("/admin")
public class MallGoodsCategoryController {

    @Resource
    private MallCategoryService mallCategoryService;

    @GetMapping("/categories")
    public String categories(HttpServletRequest request,
                             @RequestParam("categoryLevel") Byte categoryLevel,
                             @RequestParam("parentId") Long parentId,
                             @RequestParam("backParentId") Long backParentId){
        if (categoryLevel == null || categoryLevel < 1 || categoryLevel > 3){
            return "error/error_5xx";
        }

        request.setAttribute("path","mall_category");
        request.setAttribute("categoryLevel",categoryLevel);
        request.setAttribute("parentId",parentId);
        request.setAttribute("backParentId",backParentId);

        return "/admin/mall_category";
    }

    //列表
    @GetMapping("/categories/list")
    @ResponseBody
    public Result list(@RequestParam Map params){
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit")) || StringUtils.isEmpty(params.get("categoryLevel")) || StringUtils.isEmpty(params.get("parentId"))) {
            return ResultGenerator.genFailResult("参数异常！");
        }

        PageQueryUtil pageQueryUtil = new PageQueryUtil(params);
        PageResult pageResult = mallCategoryService.getCategorisPage(pageQueryUtil);
        return ResultGenerator.genSuccessResult(pageResult);
    }

    //添加
    @PostMapping("/categories/save")
    @ResponseBody
    public Result save(@RequestBody GoodsCategory goodsCategory){
        if (Objects.isNull(goodsCategory.getCategoryLevel())
                || StringUtils.isEmpty(goodsCategory.getCategoryName())
                || Objects.isNull(goodsCategory.getParentId())
                || Objects.isNull(goodsCategory.getCategoryRank())) {
            return ResultGenerator.genFailResult("参数异常！");
        }

        String result = mallCategoryService.saveCategory(goodsCategory);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)){
            return ResultGenerator.genSuccessResult();
        }
        return ResultGenerator.genFailResult(result);
    }

    //修改
    @PostMapping("/categories/update")
    @ResponseBody
    public Result update(@RequestBody GoodsCategory goodsCategory){
        if (Objects.isNull(goodsCategory.getCategoryId())
                || Objects.isNull(goodsCategory.getCategoryLevel())
                || StringUtils.isEmpty(goodsCategory.getCategoryName())
                || Objects.isNull(goodsCategory.getParentId())
                || Objects.isNull(goodsCategory.getCategoryRank())) {
            return ResultGenerator.genFailResult("参数异常！");
        }

        String result = mallCategoryService.updateGoodsCategory(goodsCategory);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)){
            return ResultGenerator.genSuccessResult();
        }
        return ResultGenerator.genFailResult(result);
    }

    //删除
    @RequestMapping("/categories/delete")
    @ResponseBody
    public Result delete(@RequestBody Integer[] ids){
        if (ids.length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }

        if (mallCategoryService.delete(ids)){
            return ResultGenerator.genSuccessResult();
        }
        return ResultGenerator.genFailResult("删除失败");
    }

    //三级联动测试
    @GetMapping("/coupling-test")
    public String couplingTest(HttpServletRequest request) {
        request.setAttribute("path","coupling-test");

        //查询所有一级分类
        List<GoodsCategory> first = mallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), MallCategoryLevelEnum.LEVEL_ONE.getLevel());
        if (first != null){
            List<GoodsCategory> second = mallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(first.get(0).getCategoryId()), MallCategoryLevelEnum.LEVEL_TWO.getLevel());
            if (second != null){
                List<GoodsCategory> third = mallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(second.get(0).getCategoryId()), MallCategoryLevelEnum.LEVEL_THREE.getLevel());
                request.setAttribute("firstLevelCategories", first);
                request.setAttribute("secondLevelCategories", second);
                request.setAttribute("thirdLevelCategories", third);
                return "admin/coupling-test";
            }
        }

        return "/error/error_5xx";
    }

    //更改三级联动时调用
    @RequestMapping("/categories/listForSelect")
    @ResponseBody
    public Result listForSelect(@RequestParam("categoryId") Long categoryId){
        if (categoryId == null || categoryId < 1){
            return ResultGenerator.genFailResult("缺少参数");
        }

        GoodsCategory goodsCategory = mallCategoryService.getGoodsCategoryById(categoryId);
        if (goodsCategory == null || goodsCategory.getCategoryLevel() == MallCategoryLevelEnum.LEVEL_THREE.getLevel()){
            return ResultGenerator.genFailResult("参数错误");
        }

        Map categoryResult = new HashMap();
        //如果传进来的分类是一级分类，就要获取二级分类和二级分类的第一个分类下的分类（即三级分类）
        if (goodsCategory.getCategoryLevel() == MallCategoryLevelEnum.LEVEL_ONE.getLevel()){
            List<GoodsCategory> second = mallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(categoryId), MallCategoryLevelEnum.LEVEL_TWO.getLevel());
            if (second != null){
                List<GoodsCategory> third = mallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(second.get(0).getCategoryId()), MallCategoryLevelEnum.LEVEL_THREE.getLevel());
                categoryResult.put("secondLevelCategories", second);
                categoryResult.put("thirdLevelCategories", third);
            }
        }
        //如果传进来的分类是二级分类，就要获取三级分类
        if (goodsCategory.getCategoryLevel() == MallCategoryLevelEnum.LEVEL_TWO.getLevel()){
            List<GoodsCategory> third = mallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(categoryId), MallCategoryLevelEnum.LEVEL_THREE.getLevel());
            categoryResult.put("thirdLevelCategories", third);
        }

        return ResultGenerator.genSuccessResult(categoryResult);
    }

}
