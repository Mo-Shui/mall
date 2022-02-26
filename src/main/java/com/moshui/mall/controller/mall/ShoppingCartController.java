package com.moshui.mall.controller.mall;

import com.moshui.mall.common.Constants;
import com.moshui.mall.common.ServiceResultEnum;
import com.moshui.mall.controller.vo.MallShoppingCartItemVO;
import com.moshui.mall.controller.vo.MallUserVO;
import com.moshui.mall.entity.MallShoppingCartItem;
import com.moshui.mall.entity.MallUser;
import com.moshui.mall.service.MallShoppingCartService;
import com.moshui.mall.util.Result;
import com.moshui.mall.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class ShoppingCartController {

    @Resource
    private MallShoppingCartService mallShoppingCartService;

    //将商品加入购物车
    @PostMapping("/shop-cart")
    @ResponseBody
    public Result saveMallShoppingCartItem(@RequestBody MallShoppingCartItem mallShoppingCartItem, HttpSession session){
        MallUserVO mallUserVO = (MallUserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        mallShoppingCartItem.setUserId(mallUserVO.getUserId());

        String saveResult = mallShoppingCartService.saveMallCartItem(mallShoppingCartItem);
        if (ServiceResultEnum.SUCCESS.getResult().equals(saveResult)) {
            return ResultGenerator.genSuccessResult();
        }
        return ResultGenerator.genFailResult(saveResult);
    }

    //跳转到购物车页面
    @GetMapping("/shop-cart")
    public String cartListPage(HttpServletRequest request,HttpSession session){
        MallUserVO mallUserVO = (MallUserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        int itemsTotal = 0;
        int priceTotal = 0;
        List<MallShoppingCartItemVO> mallShoppingCartItemVOS = mallShoppingCartService.getMyShoppingCartItems(mallUserVO.getUserId());

        if (mallShoppingCartItemVOS != null && mallShoppingCartItemVOS.size() > 0){
            //统计购物项总数
            for (MallShoppingCartItemVO mallShoppingCartItemVO : mallShoppingCartItemVOS) {
                itemsTotal += mallShoppingCartItemVO.getGoodsCount();
            }
            if (itemsTotal < 1){
                return "/error/error_5xx";
            }

            //统计总价
            for (MallShoppingCartItemVO mallShoppingCartItemVO : mallShoppingCartItemVOS) {
                priceTotal += mallShoppingCartItemVO.getGoodsCount() * mallShoppingCartItemVO.getSellingPrice();
            }
            if (priceTotal < 1){
                return "/error/error_5xx";
            }
        }

        request.setAttribute("itemsTotal", itemsTotal);
        request.setAttribute("priceTotal", priceTotal);
        request.setAttribute("myShoppingCartItems", mallShoppingCartItemVOS);
        return "/mall/cart";
    }

    //修改
    @PutMapping("/shop-cart")
    @ResponseBody
    public Result updateMallShoppingCartItem(@RequestBody MallShoppingCartItem mallShoppingCartItem,HttpSession session){
        MallUserVO userVO = (MallUserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        mallShoppingCartItem.setUserId(userVO.getUserId());

        String result = mallShoppingCartService.updateMallCartItem(mallShoppingCartItem);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)){
            return ResultGenerator.genSuccessResult();
        }
        return ResultGenerator.genFailResult(result);
    }

    //删除
    @DeleteMapping("/shop-cart/{newBeeMallShoppingCartItemId}")
    @ResponseBody
    public Result updateMallShoppingCartItem(@PathVariable("newBeeMallShoppingCartItemId") Long mallShoppingCartItemId,
                                             HttpSession httpSession){
        Boolean deleteResult = mallShoppingCartService.deleteById(mallShoppingCartItemId);
        //删除成功
        if (deleteResult) {
            return ResultGenerator.genSuccessResult();
        }
        //删除失败
        return ResultGenerator.genFailResult(ServiceResultEnum.OPERATE_ERROR.getResult());
    }

    //结算页面
    @GetMapping("/shop-cart/settle")
    public String settle(HttpServletRequest request,HttpSession session){
        int priceTotal = 0;
        MallUserVO user = (MallUserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);

        List<MallShoppingCartItemVO> myShoppingCartItems = mallShoppingCartService.getMyShoppingCartItems(user.getUserId());
        if (myShoppingCartItems == null || myShoppingCartItems.size() <= 0){
            return "/shop-cart";
        }else{
            for (MallShoppingCartItemVO myShoppingCartItem : myShoppingCartItems) {
                priceTotal += myShoppingCartItem.getGoodsCount() * myShoppingCartItem.getSellingPrice();
            }
            if (priceTotal < 1){
                return "/error/error_5xx";
            }
        }

        request.setAttribute("priceTotal", priceTotal);
        request.setAttribute("myShoppingCartItems", myShoppingCartItems);
        return "/mall/order-settle";
    }

}
