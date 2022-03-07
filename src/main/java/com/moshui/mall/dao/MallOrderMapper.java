package com.moshui.mall.dao;

import com.moshui.mall.entity.MallOrder;
import com.moshui.mall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

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

    //取消订单
    int closeOrder(@Param("orderIds") List<Long> orderIds, @Param("orderStatus") int orderStatus);

    //确认订单，更新订单
    int updateByPrimaryKeySelective(MallOrder mallOrder);

    //根据id查询
    MallOrder selectByPrimaryKey(Long orderId);

    List<MallOrder> selectByPrimaryKeys(@Param("orderIds") List<Long> orderIds);

    int checkDone(@Param("orderIds") List<Long> asList);

    int checkOut(@Param("orderIds") List<Long> orderIds);
}
