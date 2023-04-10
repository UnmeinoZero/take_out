package com.uzero.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uzero.reggie.entity.OrderDetail;
import com.uzero.reggie.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 千叶零
 * @version 1.0
 * create 2023-04-02  16:31:54
 */
@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {
}
