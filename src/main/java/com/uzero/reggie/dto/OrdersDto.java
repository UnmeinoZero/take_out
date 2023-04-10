package com.uzero.reggie.dto;

import com.uzero.reggie.entity.OrderDetail;
import com.uzero.reggie.entity.Orders;
import lombok.Data;

import java.util.*;

/**
 * @author 千叶零
 * @version 1.0
 * create 2023-04-07  14:24:09
 */
@Data
public class OrdersDto extends Orders {
    private List<OrderDetail> orderDetails = new ArrayList<>();
}
