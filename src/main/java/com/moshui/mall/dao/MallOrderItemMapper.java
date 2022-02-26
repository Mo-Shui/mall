package com.moshui.mall.dao;

import com.moshui.mall.entity.MallOrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MallOrderItemMapper {

    //保存订单项到数据库
    int insertBatch(@Param("orderItems") List<MallOrderItem> mallOrderItems);

    //根据订单id查询其下的订单项
    List<MallOrderItem> selectByOrderId(Long orderId);

    //根据多个订单id获取其下的订单项
    List<MallOrderItem> selectByOrderIds(List<Long> orderIds);
}
