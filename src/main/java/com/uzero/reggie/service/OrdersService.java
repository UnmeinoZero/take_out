package com.uzero.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.uzero.reggie.entity.Orders;
import com.uzero.reggie.entity.User;

/**
 * @author 千叶零
 * @version 1.0
 * create 2023-03-30  16:40:50
 */
public interface OrdersService extends IService<Orders> {

    void submit(Orders orders);
}
