package com.uzero.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.uzero.reggie.entity.OrderDetail;
import com.uzero.reggie.entity.Orders;
import com.uzero.reggie.mapper.OrderDetailMapper;
import com.uzero.reggie.mapper.OrdersMapper;
import com.uzero.reggie.service.OrderDetailService;
import com.uzero.reggie.service.OrdersService;
import org.springframework.stereotype.Service;

/**
 * @author 千叶零
 * @version 1.0
 * create 2023-03-30  16:41:31
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {

}
