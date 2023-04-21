package com.uzero.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.uzero.reggie.common.BaseContext;
import com.uzero.reggie.common.R;
import com.uzero.reggie.dto.OrdersDto;
import com.uzero.reggie.entity.OrderDetail;
import com.uzero.reggie.entity.Orders;
import com.uzero.reggie.service.OrderDetailService;
import com.uzero.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 千叶零
 * @version 1.0
 * create 2023-04-06  20:22:56
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private OrderDetailService orderDetailService;


    /**
     * 用户下单
     *
     * @param orders 订单
     * @return 返回结果信息
     */
    @PostMapping("/submit")
    @CacheEvict(value = "orderCache", allEntries = true) //删除缓存
    public R<String> submit(@RequestBody Orders orders) {
        log.info("订单数据：{}", orders);
        ordersService.submit(orders);
        return R.success("下单成功");
    }


    /**
     * 查看当前用户最新订单，历史订单
     *
     * @return
     */
    @GetMapping("/userPage")
    @Cacheable(value = "orderCache") //查询缓存，有用无加
    public R<Page<OrdersDto>> getUserPage(Integer page, Integer pageSize) {
        //获得当前用户id
        Long userId = BaseContext.getCurrentId();

        log.info("查看订单：{}, {}, {}", userId, page, pageSize);

        //构造条件
        LambdaQueryWrapper<Orders> qw = new LambdaQueryWrapper<>();
        qw.eq(null != userId, Orders::getUserId, userId);
        qw.orderByDesc(Orders::getOrderTime);

        //构造分页
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        Page<OrdersDto> ordersDtoPage = new Page<>(page, pageSize);

        //分页查询
        ordersPage = ordersService.page(ordersPage, qw);

        //复制对象
        BeanUtils.copyProperties(ordersPage, ordersDtoPage, "records");

        List<Orders> records = ordersPage.getRecords();

        List<OrdersDto> ordersDtoList = records.stream().map((item) -> {
            OrdersDto ordersDto = new OrdersDto();
            //将属性复制给ordersDto
            BeanUtils.copyProperties(item, ordersDto);

            //根据订单id查询订单详细
            LambdaQueryWrapper<OrderDetail> qw2 = new LambdaQueryWrapper<>();
            qw2.eq(OrderDetail::getOrderId, item.getId());
            List<OrderDetail> orderDetailList = orderDetailService.list(qw2);

            ordersDto.setOrderDetails(orderDetailList);

            return ordersDto;
        }).collect(Collectors.toList());

        ordersDtoPage = ordersDtoPage.setRecords(ordersDtoList);

        return R.success(ordersDtoPage);
    }


    /**
     * 员工订单分页条件查询
     *
     * @param page      当前页
     * @param pageSize  当前页数据大小
     * @param number    订单号
     * @param beginTime 开始时间
     * @param endTime   结束时间
     * @return 返回分页对象集合
     */
    @GetMapping("/page")
    @CacheEvict(value = "orderCache", allEntries = true) //删除缓存
    public R<Page<Orders>> getByPage(Integer page, Integer pageSize, Integer number,
                                     @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate beginTime,
                                     @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endTime) {

        log.info("订单分页查询：当前页{}，数据数量{}，订单号{}，日期范围{}~{}", page, pageSize, number, beginTime, endTime);

        //构造条件
        LambdaQueryWrapper<Orders> qw = new LambdaQueryWrapper<>();
        qw.eq(null != number, Orders::getNumber, number);
        qw.ge(null != beginTime, Orders::getOrderTime, beginTime);
        qw.le(null != endTime, Orders::getOrderTime, endTime);

        //构造分页
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        ordersPage = ordersService.page(ordersPage, qw);

        return R.success(ordersPage);
    }


    /**
     * 修改订单派送状态
     *
     * @param orders 订单
     * @return 返回结果信息
     */
    @PutMapping
    @CacheEvict(value = "orderCache", allEntries = true) //删除缓存
    public R<String> updateStatus(@RequestBody Orders orders) {
        log.info("修改订单派送状态：{}", orders);
        orders.setStatus(4);
        ordersService.updateById(orders);
        return R.success("修改成功");
    }
}
