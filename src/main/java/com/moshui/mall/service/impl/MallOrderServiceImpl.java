package com.moshui.mall.service.impl;

import com.moshui.mall.common.*;
import com.moshui.mall.controller.vo.*;
import com.moshui.mall.dao.GoodsMapper;
import com.moshui.mall.dao.MallOrderItemMapper;
import com.moshui.mall.dao.MallOrderMapper;
import com.moshui.mall.dao.MallShoppingCartItemMapper;
import com.moshui.mall.entity.Goods;
import com.moshui.mall.entity.MallOrder;
import com.moshui.mall.entity.MallOrderItem;
import com.moshui.mall.entity.StockNumDTO;
import com.moshui.mall.service.MallOrderService;
import com.moshui.mall.util.BeanUtil;
import com.moshui.mall.util.NumberUtil;
import com.moshui.mall.util.PageQueryUtil;
import com.moshui.mall.util.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;

@Service
@Transactional
public class MallOrderServiceImpl implements MallOrderService {

    @Resource
    private GoodsMapper goodsMapper;

    @Resource
    private MallShoppingCartItemMapper mallShoppingCartItemMapper;

    @Resource
    private MallOrderMapper mallOrderMapper;

    @Resource
    private MallOrderItemMapper mallOrderItemMapper;

    //保持订单，返回订单号
    @Override
    public String saveOrder(MallUserVO mallUserVO, List<MallShoppingCartItemVO> myShoppingCartItems) {
        List<Long> cartItemIds = new ArrayList<>();
        List<Long> goodsIds = new ArrayList<>();
        for (MallShoppingCartItemVO myShoppingCartItem : myShoppingCartItems) {
            cartItemIds.add(myShoppingCartItem.getCartItemId());
            goodsIds.add(myShoppingCartItem.getGoodsId());
        }
        List<Goods> goods = goodsMapper.selectByPrimaryKeys(goodsIds);

        //检查是否包含已下架商品
        List<Goods> goodsListNotSelling = new ArrayList<>();
        for (Goods good : goods) {
            if (good.getGoodsSellStatus() != Constants.SELL_STATUS_UP) {
                goodsListNotSelling.add(good);
            }
        }
        if (!CollectionUtils.isEmpty(goodsListNotSelling)) {
            MallException.fail(goodsListNotSelling.get(0).getGoodsName() + " 已下架，无法生成订单");
        }

        Map<Long, Goods> goodsMap = new HashMap<>();
        for (Goods good : goods) {
            goodsMap.put(good.getGoodsId(), good);
        }

        //判断商品库存
        for (MallShoppingCartItemVO myShoppingCartItem : myShoppingCartItems) {
            //查出的商品中不存在购物车中的这条关联商品数据，直接返回错误提醒
            if (!goodsMap.containsKey(myShoppingCartItem.getGoodsId())) {
                MallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
            }
            //存在数量大于库存的情况，直接返回错误提醒
            if (myShoppingCartItem.getGoodsCount() > goodsMap.get(myShoppingCartItem.getGoodsId()).getStockNum()) {
                MallException.fail(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
            }
        }

        //删除购物项
        if (!CollectionUtils.isEmpty(cartItemIds) && !CollectionUtils.isEmpty(goodsIds) && !CollectionUtils.isEmpty(goods)) {
            if (mallShoppingCartItemMapper.deleteBatch(cartItemIds) > 0) {
                //更新库存
                List<StockNumDTO> stockNumDTOS = BeanUtil.copyList(myShoppingCartItems, StockNumDTO.class);
                int updateStockNumResult = goodsMapper.updateStockNum(stockNumDTOS);
                if (updateStockNumResult < 1) {
                    MallException.fail(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
                }

                //生成订单号，保存订单
                String orderNo = NumberUtil.genOrderNo();
                MallOrder mallOrder = new MallOrder();
                mallOrder.setOrderNo(orderNo);
                mallOrder.setUserId(mallUserVO.getUserId());
                mallOrder.setUserAddress(mallUserVO.getAddress());

                //总价
                int priceTotal = 0;
                for (MallShoppingCartItemVO myShoppingCartItem : myShoppingCartItems) {
                    priceTotal += myShoppingCartItem.getGoodsCount() * myShoppingCartItem.getSellingPrice();
                }
                if (priceTotal < 1) {
                    MallException.fail(ServiceResultEnum.ORDER_PRICE_ERROR.getResult());
                }
                mallOrder.setTotalPrice(priceTotal);
                String extraInfo = "";
                mallOrder.setExtraInfo(extraInfo);

                //生成订单项并保存订单项记录
                if (mallOrderMapper.insertSelective(mallOrder) > 0) {
                    //生成所有订单项快照，并保存到数据库
                    List<MallOrderItem> mallOrderItems = new ArrayList<>();
                    for (MallShoppingCartItemVO myShoppingCartItem : myShoppingCartItems) {
                        MallOrderItem mallOrderItem = new MallOrderItem();

                        BeanUtil.copyProperties(myShoppingCartItem, mallOrderItem);
                        //MallOrderMapper文件insert()方法中使用了useGeneratedKeys因此orderId可以获取到
                        mallOrderItem.setOrderId(mallOrder.getOrderId());

                        mallOrderItems.add(mallOrderItem);
                    }
                    //保存订单项到数据库
                    if (mallOrderItemMapper.insertBatch(mallOrderItems) > 0) {
                        //所有操作成功后，将订单号返回，以供Controller方法跳转到订单详情
                        return orderNo;
                    }
                    MallException.fail(ServiceResultEnum.ORDER_PRICE_ERROR.getResult());
                }
                MallException.fail(ServiceResultEnum.DB_ERROR.getResult());
            }
            MallException.fail(ServiceResultEnum.DB_ERROR.getResult());
        }
        MallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
        return ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult();
    }

    //根据订单号和用户id查询
    @Override
    public MallOrderDetailVO getOrderDetailByOrderNo(String orderNo, Long userId) {
        MallOrder mallOrder = mallOrderMapper.selectByOrderNo(orderNo);
        if (mallOrder == null) {
            MallException.fail(ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult());
        }
        //验证是否是当前userId下的订单，否则报错
        if (!userId.equals(mallOrder.getUserId())) {
            MallException.fail(ServiceResultEnum.NO_PERMISSION_ERROR.getResult());
        }

        //获取订单项数据
        List<MallOrderItem> mallOrderItems = mallOrderItemMapper.selectByOrderId(mallOrder.getOrderId());
        if (CollectionUtils.isEmpty(mallOrderItems)) {
            MallException.fail(ServiceResultEnum.ORDER_ITEM_NOT_EXIST_ERROR.getResult());
        }
        List<MallOrderItemVO> mallOrderItemVOS = BeanUtil.copyList(mallOrderItems, MallOrderItemVO.class);
        MallOrderDetailVO mallOrderDetailVO = new MallOrderDetailVO();
        BeanUtil.copyProperties(mallOrder,mallOrderDetailVO);

        mallOrderDetailVO.setOrderStatusString(MallOrderStatusEnum.getMallOrderStatusEnumByStatus(mallOrderDetailVO.getOrderStatus()).getName());
        mallOrderDetailVO.setPayTypeString(PayTypeEnum.getPayTypeEnumByType(mallOrderDetailVO.getPayType()).getName());
        mallOrderDetailVO.setMallOrderItemVOS(mallOrderItemVOS);
        return mallOrderDetailVO;
    }

    //获取我的订单数据
    @Override
    public PageResult getMyOrders(PageQueryUtil pageQueryUtil) {
        int total = mallOrderMapper.getTotalMallOrders(pageQueryUtil);
        List<MallOrder> mallOrders = mallOrderMapper.findMallOrderList(pageQueryUtil);
        List<MallOrderListVO> mallOrderListVOS = new ArrayList<>();

        if (total > 0){
            mallOrderListVOS = BeanUtil.copyList(mallOrders,MallOrderListVO.class);

            //设置订单状态中文显示值
            for (MallOrderListVO mallOrderListVO : mallOrderListVOS) {
                mallOrderListVO.setOrderStatusString(MallOrderStatusEnum.getMallOrderStatusEnumByStatus(mallOrderListVO.getOrderStatus()).getName());
            }

            List<Long> orderIds = new ArrayList<>();
            for (MallOrderListVO mallOrderListVO : mallOrderListVOS) {
                orderIds.add(mallOrderListVO.getOrderId());
            }
            if (!CollectionUtils.isEmpty(orderIds)){
                List<MallOrderItem> mallOrderItems = mallOrderItemMapper.selectByOrderIds(orderIds);
                Map<Long,List<MallOrderItem>> orderItemsMap = new HashMap<>();
                for (MallOrderItem mallOrderItem : mallOrderItems) {
                    List<MallOrderItem> list = new ArrayList<>();
                    Long orderId = mallOrderItem.getOrderId();
                    for (MallOrderItem orderItem : mallOrderItems) {
                        if (orderId == orderItem.getOrderId()){
                            list.add(orderItem);
                        }
                    }
                    orderItemsMap.put(orderId,list);
                }

                for (MallOrderListVO mallOrderListVO : mallOrderListVOS) {
                    //封装每个订单列表对象的订单项数据
                    if (orderItemsMap.containsKey(mallOrderListVO.getOrderId())){
                        List<MallOrderItem> orderItems = orderItemsMap.get(mallOrderListVO.getOrderId());
                        List<MallOrderItemVO> mallOrderItemVOS = BeanUtil.copyList(orderItems,MallOrderItemVO.class);
                        mallOrderListVO.setMallOrderItemVOS(mallOrderItemVOS);
                    }
                }
            }
        }

        PageResult pageResult = new PageResult(mallOrderListVOS,total, pageQueryUtil.getLimit(), pageQueryUtil.getPage());
        return pageResult;
    }

    //取消订单
    @Override
    public String cancelOrder(String orderNo, Long userId) {
        MallOrder mallOrder = mallOrderMapper.selectByOrderNo(orderNo);
        if (mallOrder != null) {
            //验证是否是当前userId下的订单，否则报错
            if (!userId.equals(mallOrder.getUserId())) {
                MallException.fail(ServiceResultEnum.NO_PERMISSION_ERROR.getResult());
            }
            //订单状态判断
            if (mallOrder.getOrderStatus().intValue() == MallOrderStatusEnum.ORDER_SUCCESS.getOrderStatus()
                    || mallOrder.getOrderStatus().intValue() == MallOrderStatusEnum.ORDER_CLOSED_BY_MALLUSER.getOrderStatus()
                    || mallOrder.getOrderStatus().intValue() == MallOrderStatusEnum.ORDER_CLOSED_BY_EXPIRED.getOrderStatus()
                    || mallOrder.getOrderStatus().intValue() == MallOrderStatusEnum.ORDER_CLOSED_BY_JUDGE.getOrderStatus()) {
                return ServiceResultEnum.ORDER_STATUS_ERROR.getResult();
            }
            if (mallOrderMapper.closeOrder(Collections.singletonList(mallOrder.getOrderId()), MallOrderStatusEnum.ORDER_CLOSED_BY_MALLUSER.getOrderStatus()) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    //确认订单
    @Override
    public String finishOrder(String orderNo, Long userId) {
        MallOrder mallOrder = mallOrderMapper.selectByOrderNo(orderNo);
        if (mallOrder != null) {
            //验证是否是当前userId下的订单，否则报错
            if (!userId.equals(mallOrder.getUserId())) {
                return ServiceResultEnum.NO_PERMISSION_ERROR.getResult();
            }
            //订单状态判断 非出库状态下不进行修改操作
            if (mallOrder.getOrderStatus().intValue() != MallOrderStatusEnum.ORDER_EXPRESS.getOrderStatus()) {
                return ServiceResultEnum.ORDER_STATUS_ERROR.getResult();
            }
            mallOrder.setOrderStatus((byte) MallOrderStatusEnum.ORDER_SUCCESS.getOrderStatus());
            mallOrder.setUpdateTime(new Date());
            if (mallOrderMapper.updateByPrimaryKeySelective(mallOrder) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    //获取订单详情
    @Override
    public MallOrder getMallOrderByOrderNo(String orderNo) {
        return mallOrderMapper.selectByOrderNo(orderNo);
    }

    //支付成功
    @Override
    public String paySuccess(String orderNo, int payType) {
        MallOrder mallOrder = mallOrderMapper.selectByOrderNo(orderNo);
        if (mallOrder != null) {
            //订单状态判断 非待支付状态下不进行修改操作
            if (mallOrder.getOrderStatus().intValue() != MallOrderStatusEnum.ORDER_PRE_PAY.getOrderStatus()) {
                return ServiceResultEnum.ORDER_STATUS_ERROR.getResult();
            }
            mallOrder.setOrderStatus((byte) MallOrderStatusEnum.ORDER_PAID.getOrderStatus());
            mallOrder.setPayType((byte) payType);
            mallOrder.setPayStatus((byte) PayStatusEnum.PAY_SUCCESS.getPayStatus());
            mallOrder.setPayTime(new Date());
            mallOrder.setUpdateTime(new Date());
            if (mallOrderMapper.updateByPrimaryKeySelective(mallOrder) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    //后台分页
    @Override
    public PageResult getMallOrdersPage(PageQueryUtil pageUtil) {
        List<MallOrder> mallOrders = mallOrderMapper.findMallOrderList(pageUtil);
        int total = mallOrderMapper.getTotalMallOrders(pageUtil);
        PageResult pageResult = new PageResult(mallOrders, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    //订单信息修改
    @Override
    public String updateOrderInfo(MallOrder mallOrder) {
        MallOrder temp = mallOrderMapper.selectByPrimaryKey(mallOrder.getOrderId());
        //不为空且orderStatus>=0且状态为出库之前可以修改部分信息
        if (temp != null && temp.getOrderStatus() >= 0 && temp.getOrderStatus() < 3) {
            temp.setTotalPrice(mallOrder.getTotalPrice());
            temp.setUserAddress(mallOrder.getUserAddress());
            temp.setUpdateTime(new Date());
            if (mallOrderMapper.updateByPrimaryKeySelective(temp) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            }
            return ServiceResultEnum.DB_ERROR.getResult();
        }
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    //获取订单项
    @Override
    public List<MallOrderItemVO> getOrderItems(Long id) {
        MallOrder mallOrder = mallOrderMapper.selectByPrimaryKey(id);
        if (mallOrder != null) {
            List<MallOrderItem> orderItems = mallOrderItemMapper.selectByOrderId(mallOrder.getOrderId());
            //获取订单项数据
            if (!CollectionUtils.isEmpty(orderItems)) {
                List<MallOrderItemVO> mallOrderItemVOS = BeanUtil.copyList(orderItems, MallOrderItemVO.class);
                return mallOrderItemVOS;
            }
        }
        return null;
    }

    @Override
    public String checkDone(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<MallOrder> orders = mallOrderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (MallOrder mallOrder : orders) {
                if (mallOrder.getIsDeleted() == 1) {
                    errorOrderNos += mallOrder.getOrderNo() + " ";
                    continue;
                }
                if (mallOrder.getOrderStatus() != 1) {
                    errorOrderNos += mallOrder.getOrderNo() + " ";
                }
            }
            if (StringUtils.isEmpty(errorOrderNos)) {
                //订单状态正常 可以执行配货完成操作 修改订单状态和更新时间
                if (mallOrderMapper.checkDone(Arrays.asList(ids)) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行出库操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单的状态不是支付成功无法执行出库操作";
                } else {
                    return "你选择了太多状态不是支付成功的订单，无法执行配货完成操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    public String checkOut(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<MallOrder> orders = mallOrderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (MallOrder mallOrder : orders) {
                if (mallOrder.getIsDeleted() == 1) {
                    errorOrderNos += mallOrder.getOrderNo() + " ";
                    continue;
                }
                if (mallOrder.getOrderStatus() != 1 && mallOrder.getOrderStatus() != 2) {
                    errorOrderNos += mallOrder.getOrderNo() + " ";
                }
            }
            if (StringUtils.isEmpty(errorOrderNos)) {
                //订单状态正常 可以执行出库操作 修改订单状态和更新时间
                if (mallOrderMapper.checkOut(Arrays.asList(ids)) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行出库操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单的状态不是支付成功或配货完成无法执行出库操作";
                } else {
                    return "你选择了太多状态不是支付成功或配货完成的订单，无法执行出库操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    public String closeOrder(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<MallOrder> orders = mallOrderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (MallOrder mallOrder : orders) {
                // isDeleted=1 一定为已关闭订单
                if (mallOrder.getIsDeleted() == 1) {
                    errorOrderNos += mallOrder.getOrderNo() + " ";
                    continue;
                }
                //已关闭或者已完成无法关闭订单
                if (mallOrder.getOrderStatus() == 4 || mallOrder.getOrderStatus() < 0) {
                    errorOrderNos += mallOrder.getOrderNo() + " ";
                }
            }
            if (StringUtils.isEmpty(errorOrderNos)) {
                //订单状态正常 可以执行关闭操作 修改订单状态和更新时间
                if (mallOrderMapper.closeOrder(Arrays.asList(ids), MallOrderStatusEnum.ORDER_CLOSED_BY_JUDGE.getOrderStatus()) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行关闭操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单不能执行关闭操作";
                } else {
                    return "你选择的订单不能执行关闭操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

}
