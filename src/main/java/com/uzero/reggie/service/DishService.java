package com.uzero.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.uzero.reggie.dto.DishDto;
import com.uzero.reggie.entity.Dish;

/**
 * @author 千叶零
 * @version 1.0
 * create 2023-03-30  16:40:50
 */
public interface DishService extends IService<Dish> {

    //新增菜品，同时才入菜品对应的口味数据，需要操作两张表，dish，dish_flavor
    boolean saveWithFlavor(DishDto dishDto);

    //根据id查询菜品信息和口味信息
    DishDto getByIdWithFlavor(Long id);

    //修改菜品信息或口味信息
    void updateWithFlavor(DishDto dishDto);


    //删除菜品方法，同时关联口味表和套餐表
    boolean deleteWithFlavorAndSetmeal(String[] id);
}
