package com.uzero.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.uzero.reggie.entity.Dish;
import com.uzero.reggie.entity.DishFlavor;
import com.uzero.reggie.mapper.DishFlavorMapper;
import com.uzero.reggie.mapper.DishMapper;
import com.uzero.reggie.service.DishFlavorService;
import com.uzero.reggie.service.DishService;
import org.springframework.stereotype.Service;

/**
 * @author 千叶零
 * @version 1.0
 * create 2023-03-30  16:41:31
 */
@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
