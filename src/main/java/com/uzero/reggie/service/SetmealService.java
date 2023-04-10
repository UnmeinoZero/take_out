package com.uzero.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.uzero.reggie.dto.SetmealDto;
import com.uzero.reggie.entity.Employee;
import com.uzero.reggie.entity.Setmeal;

import java.util.List;

/**
 * @author 千叶零
 * @version 1.0
 * create 2023-03-30  16:40:50
 */
public interface SetmealService extends IService<Setmeal> {

    //新增套餐，同时保持套餐和菜品的关联关系
    void saveWithDish(SetmealDto setmealDto);


    //删除套餐，同时删除套餐和菜品的关联关系
    void removeWithDish(List<Long> ids);

    //根据id查询套餐和菜品的关联关系
    SetmealDto getByIdWithFlavor(Long id);

    //修改套餐方法，同时保持套餐和菜品的关联关系
    void updateWithFlavor(SetmealDto setmealDto);
}
