package com.moshui.mall.controller.mall;

import com.moshui.mall.common.Constants;
import com.moshui.mall.common.MallException;
import com.moshui.mall.common.ServiceResultEnum;
import com.moshui.mall.controller.vo.MallOrderDetailVO;
import com.moshui.mall.controller.vo.MallShoppingCartItemVO;
import com.moshui.mall.controller.vo.MallUserVO;
import com.moshui.mall.service.MallOrderService;
import com.moshui.mall.service.MallShoppingCartService;
import com.moshui.mall.util.PageQueryUtil;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
        return "/mall/order-detail";
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

        return "/mall/my-orders";
    }

}
