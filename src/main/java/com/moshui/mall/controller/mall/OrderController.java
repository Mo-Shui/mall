package com.moshui.mall.controller.mall;

import com.moshui.mall.common.Constants;
import com.moshui.mall.common.MallException;
import com.moshui.mall.common.MallOrderStatusEnum;
import com.moshui.mall.common.ServiceResultEnum;
import com.moshui.mall.controller.vo.MallOrderDetailVO;
import com.moshui.mall.controller.vo.MallShoppingCartItemVO;
import com.moshui.mall.controller.vo.MallUserVO;
import com.moshui.mall.entity.MallOrder;
import com.moshui.mall.service.MallOrderService;
import com.moshui.mall.service.MallShoppingCartService;
import com.moshui.mall.util.PageQueryUtil;
import com.moshui.mall.util.Result;
import com.moshui.mall.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
public class OrderController {

    @Resource
    private MallShoppingCartService mallShoppingCartService;

    @Resource
    private MallOrderService mallOrderService;

    //保存订单
    @GetMapping("/saveOrder")
    public String saveOrder(HttpSession session){
        MallUserVO mallUserVO = (MallUserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        List<MallShoppingCartItemVO> myShoppingCartItems = mallShoppingCartService.getMyShoppingCartItems(mallUserVO.getUserId());
        if (StringUtils.isEmpty(mallUserVO.getAddress().trim())){
            MallException.fail(ServiceResultEnum.NULL_ADDRESS_ERROR.getResult());
        }
        if (CollectionUtils.isEmpty(myShoppingCartItems)){
            MallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
        }

        //返回订单号
        String result = mallOrderService.saveOrder(mallUserVO,myShoppingCartItems);

        return "redirect:/orders/" + result;
    }

    //订单详情跳转
    @GetMapping("/orders/{orderNo}")
    public String orderDetail(HttpServletRequest request, HttpSession session,
                              @PathVariable("orderNo") String orderNo){
        MallUserVO mallUserVO = (MallUserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        MallOrderDetailVO mallOrderDetailVO = mallOrderService.getOrderDetailByOrderNo(orderNo,mallUserVO.getUserId());
        if (mallOrderDetailVO == null) {
            return "error/error_5xx";
        }
        request.setAttribute("orderDetailVO", mallOrderDetailVO);
        return "mall/order-detail";
    }

    //个人中心订单列表
    @GetMapping("/orders")
    public String orderList(HttpServletRequest request, HttpSession session,
                            @RequestParam Map params){
        MallUserVO mallUserVO = (MallUserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        params.put("userId",mallUserVO.getUserId());
        if (StringUtils.isEmpty(params.get("page"))) {
            params.put("page", 1);
        }
        params.put("limit", Constants.ORDER_SEARCH_PAGE_LIMIT);

        //封装我的订单数据
        PageQueryUtil pageQueryUtil = new PageQueryUtil(params);
        request.setAttribute("path","orders");
        request.setAttribute("orderPageResult",mallOrderService.getMyOrders(pageQueryUtil));

        return "mall/my-orders";
    }

    //取消订单
    @PutMapping("/orders/{orderNo}/cancel")
    @ResponseBody
    public Result cancelOrder(@PathVariable("orderNo") String orderNo, HttpSession httpSession) {
        MallUserVO user = (MallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        String cancelOrderResult = mallOrderService.cancelOrder(orderNo, user.getUserId());
        if (ServiceResultEnum.SUCCESS.getResult().equals(cancelOrderResult)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(cancelOrderResult);
        }
    }

    //确认收货
    @PutMapping("/orders/{orderNo}/finish")
    @ResponseBody
    public Result finishOrder(@PathVariable("orderNo") String orderNo, HttpSession httpSession) {
        MallUserVO user = (MallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        String finishOrderResult = mallOrderService.finishOrder(orderNo, user.getUserId());
        if (ServiceResultEnum.SUCCESS.getResult().equals(finishOrderResult)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(finishOrderResult);
        }
    }

    //获取订单详情
    @GetMapping("/selectPayType")
    public String selectPayType(HttpServletRequest request, @RequestParam("orderNo") String orderNo, HttpSession httpSession) {
        MallUserVO user = (MallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        MallOrder mallOrder = mallOrderService.getMallOrderByOrderNo(orderNo);
        //判断订单userId
        if (!user.getUserId().equals(mallOrder.getUserId())) {
            MallException.fail(ServiceResultEnum.NO_PERMISSION_ERROR.getResult());
        }
        //判断订单状态
        if (mallOrder.getOrderStatus().intValue() != MallOrderStatusEnum.ORDER_PRE_PAY.getOrderStatus()) {
            MallException.fail(ServiceResultEnum.ORDER_STATUS_ERROR.getResult());
        }
        request.setAttribute("orderNo", orderNo);
        request.setAttribute("totalPrice", mallOrder.getTotalPrice());
        return "mall/pay-select";
    }

    //获取订单详情
    @GetMapping("/payPage")
    public String payOrder(HttpServletRequest request, @RequestParam("orderNo") String orderNo, HttpSession httpSession, @RequestParam("payType") int payType) {
        MallUserVO user = (MallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        MallOrder mallOrder = mallOrderService.getMallOrderByOrderNo(orderNo);
        //判断订单userId
        if (!user.getUserId().equals(mallOrder.getUserId())) {
            MallException.fail(ServiceResultEnum.NO_PERMISSION_ERROR.getResult());
        }
        //判断订单状态
        if (mallOrder.getOrderStatus().intValue() != MallOrderStatusEnum.ORDER_PRE_PAY.getOrderStatus()) {
            MallException.fail(ServiceResultEnum.ORDER_STATUS_ERROR.getResult());
        }
        request.setAttribute("orderNo", orderNo);
        request.setAttribute("totalPrice", mallOrder.getTotalPrice());
        if (payType == 1) {
            return "mall/alipay";
        } else {
            return "mall/wxpay";
        }
    }

    //支付成功
    @GetMapping("/paySuccess")
    @ResponseBody
    public Result paySuccess(@RequestParam("orderNo") String orderNo, @RequestParam("payType") int payType) {
        String payResult = mallOrderService.paySuccess(orderNo, payType);
        if (ServiceResultEnum.SUCCESS.getResult().equals(payResult)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(payResult);
        }
    }

}
