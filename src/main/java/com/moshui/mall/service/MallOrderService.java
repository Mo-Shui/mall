package com.moshui.mall.service;

import com.moshui.mall.controller.vo.MallOrderDetailVO;
import com.moshui.mall.controller.vo.MallOrderItemVO;
import com.moshui.mall.controller.vo.MallShoppingCartItemVO;
import com.moshui.mall.controller.vo.MallUserVO;
import com.moshui.mall.entity.MallOrder;
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

    //取消订单
    String cancelOrder(String orderNo, Long userId);

    //确认订单
    String finishOrder(String orderNo, Long userId);

    //获取订单详情
    MallOrder getMallOrderByOrderNo(String orderNo);

    //支付成功
    String paySuccess(String orderNo, int payType);

    //后台分页
    PageResult getMallOrdersPage(PageQueryUtil pageUtil);

    //订单信息修改
    String updateOrderInfo(MallOrder mallOrder);

    //获取订单项
    List<MallOrderItemVO> getOrderItems(Long id);

    //配货
    String checkDone(Long[] ids);

    //出库
    String checkOut(Long[] ids);

    //关闭订单
    String closeOrder(Long[] ids);
}
