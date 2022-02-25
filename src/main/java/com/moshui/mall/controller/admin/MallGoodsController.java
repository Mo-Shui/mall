package com.moshui.mall.controller.admin;

import com.moshui.mall.common.Constants;
import com.moshui.mall.common.MallCategoryLevelEnum;
import com.moshui.mall.common.MallException;
import com.moshui.mall.common.ServiceResultEnum;
import com.moshui.mall.controller.vo.MallGoodsDetailVO;
import com.moshui.mall.entity.Goods;
import com.moshui.mall.entity.GoodsCategory;
import com.moshui.mall.service.MallCategoryService;
import com.moshui.mall.service.MallGoodsService;
import com.moshui.mall.util.BeanUtil;
import com.moshui.mall.util.PageQueryUtil;
import com.moshui.mall.util.Result;
import com.moshui.mall.util.ResultGenerator;
import org.apache.ibatis.ognl.DynamicSubscript;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.apache.ibatis.ognl.DynamicSubscript.first;

@Controller
@RequestMapping("/admin")
public class MallGoodsController {

    @Resource
    private MallCategoryService mallCategoryService;

    @Resource
    private MallGoodsService mallGoodsService;

    //商品管理页面
    @RequestMapping("/goods")
    public String goods(HttpServletRequest request) {
        request.setAttribute("path", "mall_goods");
        return "/admin/mall_goods";
    }

    //编辑商品信息页面
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

    //编辑商品信息页面
    @RequestMapping("/goods/edit/{goodsId}")
    public String edit(HttpServletRequest request, @PathVariable("goodsId") Long goodsId){
        request.setAttribute("path","edit");

        Goods goods = mallGoodsService.getMallGoodsById(goodsId);
        if (goods == null) {
            return "/error/error_400";
        }

        //获取该商品的三级分类信息
        if (goods.getGoodsId() > 0) {
            //说明该商品有分类数据，仅获取该商品的三级分类信息
            //需要获取全部的三级分类，并且还要获取该商品所对应的分类信息，以便前端可以选中该商品

            //此为三级分类信息
            GoodsCategory currentGoodsCategory = mallCategoryService.getGoodsCategoryById(goods.getGoodsCategoryId());
            if (currentGoodsCategory != null && currentGoodsCategory.getCategoryLevel() == MallCategoryLevelEnum.LEVEL_THREE.getLevel()){
                List<GoodsCategory> firstList = mallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), MallCategoryLevelEnum.LEVEL_ONE.getLevel());
                List<GoodsCategory> thirdList = mallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(currentGoodsCategory.getParentId()), MallCategoryLevelEnum.LEVEL_THREE.getLevel());
                GoodsCategory secondGoodsCategory = mallCategoryService.getGoodsCategoryById(currentGoodsCategory.getParentId());
                if (secondGoodsCategory != null){
                    List<GoodsCategory> secondList = mallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondGoodsCategory.getParentId()), MallCategoryLevelEnum.LEVEL_TWO.getLevel());
                    GoodsCategory firstGoodsCategory = mallCategoryService.getGoodsCategoryById(secondGoodsCategory.getParentId());

                    if (firstGoodsCategory != null) {
                        //所有分类数据都得到之后放到request对象中供前端读取
                        request.setAttribute("firstLevelCategories", firstList);
                        request.setAttribute("secondLevelCategories", secondList);
                        request.setAttribute("thirdLevelCategories", thirdList);
                        request.setAttribute("firstLevelCategoryId", firstGoodsCategory.getCategoryId());
                        request.setAttribute("secondLevelCategoryId", secondGoodsCategory.getCategoryId());
                        request.setAttribute("thirdLevelCategoryId", currentGoodsCategory.getCategoryId());
                    }
                }
            }
        }else{
            //说明该商品没有分类数据，获取所有三级分类信息即可
            //查询所有的一级分类
            List<GoodsCategory> firstLevelCategories = mallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), MallCategoryLevelEnum.LEVEL_ONE.getLevel());
            if (firstLevelCategories != null) {
                //查询一级分类列表中第一个实体的所有二级分类
                List<GoodsCategory> secondLevelCategories = mallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(firstLevelCategories.get(0).getCategoryId()), MallCategoryLevelEnum.LEVEL_TWO.getLevel());
                if (secondLevelCategories != null) {
                    //查询二级分类列表中第一个实体的所有三级分类
                    List<GoodsCategory> thirdLevelCategories = mallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondLevelCategories.get(0).getCategoryId()), MallCategoryLevelEnum.LEVEL_THREE.getLevel());
                    request.setAttribute("firstLevelCategories", firstLevelCategories);
                    request.setAttribute("secondLevelCategories", secondLevelCategories);
                    request.setAttribute("thirdLevelCategories", thirdLevelCategories);
                }
            }
        }

        request.setAttribute("goods",goods);
        request.setAttribute("path","goods-edit");
        return "/admin/mall_goods_edit";
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

    //修改商品
    @RequestMapping("/goods/update")
    @ResponseBody
    private Result update(@RequestBody Goods goods){
        if (Objects.isNull(goods.getGoodsId())
                || StringUtils.isEmpty(goods.getGoodsName())
                || StringUtils.isEmpty(goods.getGoodsIntro())
                || StringUtils.isEmpty(goods.getTag())
                || Objects.isNull(goods.getOriginalPrice())
                || Objects.isNull(goods.getSellingPrice())
                || Objects.isNull(goods.getGoodsCategoryId())
                || Objects.isNull(goods.getStockNum())
                || Objects.isNull(goods.getGoodsSellStatus())
                || StringUtils.isEmpty(goods.getGoodsCoverImg())
                || StringUtils.isEmpty(goods.getGoodsDetailContent())) {
            return ResultGenerator.genFailResult("参数异常！");
        }

        String result = mallGoodsService.updateMallGoods(goods);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)){
            return ResultGenerator.genSuccessResult();
        }
        return ResultGenerator.genFailResult(result);
    }

    //列表
    @RequestMapping("/goods/list")
    @ResponseBody
    private Result list(@RequestParam Map params){
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            return ResultGenerator.genFailResult("参数异常！");
        }

        PageQueryUtil pageQueryUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(mallGoodsService.getMallGoodsPage(pageQueryUtil));
    }

    //批量修改销售状态
    @RequestMapping("/goods/status/{sellStatus}")
    @ResponseBody
    private Result delete(@RequestBody Long[] ids,@PathVariable("sellStatus")int sellStatus){
        if (ids.length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        if (sellStatus != Constants.SELL_STATUS_UP && sellStatus != Constants.SELL_STATUS_DOWN) {
            return ResultGenerator.genFailResult("状态异常！");
        }

        if (mallGoodsService.batchUpdateSellStatus(ids,sellStatus)){
            return ResultGenerator.genSuccessResult();
        }
        return ResultGenerator.genFailResult("修改失败");
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
        return "/mall/detail";
    }

}
