package com.uzero.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.uzero.reggie.common.BaseContext;
import com.uzero.reggie.common.CustomException;
import com.uzero.reggie.entity.*;
import com.uzero.reggie.mapper.OrdersMapper;
import com.uzero.reggie.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author 千叶零
 * @version 1.0
 * create 2023-03-30  16:41:31
 */
@Slf4j
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 用户下单
     *
     * @param orders 订单
     */
    @Override
    @Transactional
    public void submit(Orders orders) {
        //获得当前用户id
        Long userId = BaseContext.getCurrentId();

        //查询当前用户的购物车数据
        LambdaQueryWrapper<ShoppingCart> qw = new LambdaQueryWrapper<>();
        qw.eq(ShoppingCart::getUserId, userId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(qw);

        if (null == shoppingCarts || shoppingCarts.size() == 0) {
            throw new CustomException("购物车为空，不能下单");
        }

        //查询用户数据
        User user = userService.getById(userId);

        //查询地址数据
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
        if (null == addressBook) {
            throw new CustomException("地址为空，不能下单");
        }

        //生成订单号
        long orderId = IdWorker.getId();

        //定义金额
        AtomicInteger amount = new AtomicInteger(0);

        List<OrderDetail> orderDetails = shoppingCarts.stream().map((item) -> {
            //设置订单明细数据
            OrderDetail orderDetail = new OrderDetail();
            //设置订单号
            orderDetail.setOrderId(orderId);
            //设置菜品数量
            orderDetail.setNumber(item.getNumber());
            //设置菜品口味
            orderDetail.setDishFlavor(item.getDishFlavor());
            //设置菜品id
            orderDetail.setDishId(item.getDishId());
            //设置套餐id
            orderDetail.setSetmealId(item.getSetmealId());
            //设置名称
            orderDetail.setName(item.getName());
            //设置图片
            orderDetail.setImage(item.getImage());
            //设置金额
            orderDetail.setAmount(item.getAmount());
            //计算总金额
            // addAndGet(加等) multiply(乘) 封装为BigDecimal再相乘(因为Amount为BigDecimal) intValue(转为int)
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());


        //向订单表插入数据，一条数据
        //设置订单号
        orders.setNumber(String.valueOf(orderId));
        //设置id
        orders.setId(orderId);
        //设置下单时间
        orders.setOrderTime(LocalDateTime.now());
        //设置结账时间
        orders.setCheckoutTime(LocalDateTime.now());
        //设置状态  1待付款，2待派送，3已派送，4已完成，5已取消
        orders.setStatus(2);
        //设置  实收金额
        orders.setAmount(new BigDecimal(amount.get()));
        //设置用户id
        orders.setUserId(userId);
        //设置用户名
        orders.setUserName(user.getName());
        //设置收货人
        orders.setConsignee(addressBook.getConsignee());
        //设置手机号
        orders.setPhone(addressBook.getPhone());
        //设置地址, 省，市，区，详细地址
        orders.setAddress((null == addressBook.getProvinceName() ? "" : addressBook.getProvinceName())
                + (null == addressBook.getCityName() ? "" : addressBook.getCityName())
                + (null == addressBook.getDistrictName() ? "" : addressBook.getDistrictName())
                + (null == addressBook.getDetail() ? "" : addressBook.getDetail()));

        //保存订单
        this.save(orders);

        //向明细表插入数据
        orderDetailService.saveBatch(orderDetails);

        //清空购物车
        shoppingCartService.remove(qw);
    }
}
