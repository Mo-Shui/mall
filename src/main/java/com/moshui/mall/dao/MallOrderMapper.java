package com.moshui.mall.dao;

import com.moshui.mall.entity.MallOrder;
import com.moshui.mall.util.PageQueryUtil;

import java.util.List;

public interface MallOrderMapper {

    //保存订单
    int insertSelective(MallOrder mallOrder);

    //根据订单号查询订单
    MallOrder selectByOrderNo(String orderNo);

    //获取订单总数
    int getTotalMallOrders(PageQueryUtil pageQueryUtil);

    //获取订单数据
    List<MallOrder> findMallOrderList(PageQueryUtil pageQueryUtil);
}
