package com.moshui.mall.service;

import com.moshui.mall.controller.vo.MallOrderDetailVO;
import com.moshui.mall.controller.vo.MallShoppingCartItemVO;
import com.moshui.mall.controller.vo.MallUserVO;
import com.moshui.mall.util.PageQueryUtil;
import com.moshui.mall.util.PageResult;

import java.util.List;

public interface MallOrderService {

    //保存订单，返回订单号
    String saveOrder(MallUserVO mallUserVO, List<MallShoppingCartItemVO> myShoppingCartItems);

    //根据订单号和用户id查询
    MallOrderDetailVO getOrderDetailByOrderNo(String orderNo, Long userId);

    //获取我的订单数据
    PageResult getMyOrders(PageQueryUtil pageQueryUtil);
}
