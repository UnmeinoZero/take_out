package com.uzero.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.uzero.reggie.entity.ShoppingCart;
import com.uzero.reggie.entity.User;
import com.uzero.reggie.mapper.ShoppingCartMapper;
import com.uzero.reggie.mapper.UserMapper;
import com.uzero.reggie.service.ShoppingCartService;
import com.uzero.reggie.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author 千叶零
 * @version 1.0
 * create 2023-03-30  16:41:31
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

}
