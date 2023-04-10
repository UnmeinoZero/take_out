package com.uzero.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.uzero.reggie.common.CustomException;
import com.uzero.reggie.dto.DishDto;
import com.uzero.reggie.dto.SetmealDto;
import com.uzero.reggie.entity.*;
import com.uzero.reggie.mapper.CategoryMapper;
import com.uzero.reggie.mapper.SetmealMapper;
import com.uzero.reggie.service.CategoryService;
import com.uzero.reggie.service.SetmealDishService;
import com.uzero.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 千叶零
 * @version 1.0
 * create 2023-03-30  16:41:31
 */
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {


    @Autowired
    private SetmealDishService setmealDishService;


    /**
     * 新增套餐，同时保持套餐和菜品的关联关系
     *
     * @param setmealDto setmealDto
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐基础信息，操作setmeal
        this.save(setmealDto);

        //保存套餐和菜品的关联信息，操作sermeal_dish
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);

    }

    /**
     * 删除套餐，同时删除套餐和菜品的关联关系
     *
     * @param ids
     */
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {

        //判断是否有不符合条件的套餐
        LambdaQueryWrapper<Setmeal> qw = new LambdaQueryWrapper<>();
        qw.in(Setmeal::getId, ids);
        qw.eq(Setmeal::getStatus, 1);
        long count = this.count(qw);
        //如果有启售中的套餐，删除失败，抛出异常
        if (count > 0) {
            throw new CustomException("套装启售中，删除失败");
        }
        this.removeByIds(ids);

        //删除套餐与菜品关联
        LambdaQueryWrapper<SetmealDish> qw1 = new LambdaQueryWrapper<>();
        qw1.in(SetmealDish::getSetmealId, ids);
        setmealDishService.remove(qw1);
    }


    /**
     * 根据id查询套餐和菜品的关联关系
     *
     * @param id 套餐id
     * @return setmealDto
     */
    @Override
    public SetmealDto getByIdWithFlavor(Long id) {
        //查询套餐
        Setmeal setmeal = this.getById(id);

        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal, setmealDto);

        //查询菜品信息
        LambdaQueryWrapper<SetmealDish> qw = new LambdaQueryWrapper<>();
        qw.eq(id != null, SetmealDish::getSetmealId, id);
        List<SetmealDish> dishes = setmealDishService.list(qw);
        setmealDto.setSetmealDishes(dishes);
        return setmealDto;
    }

    /**
     * 修改套餐方法，同时保持套餐和菜品的关联关系
     *
     * @param setmealDto 修改信息
     */
    @Override
    @Transactional
    public void updateWithFlavor(SetmealDto setmealDto) {
        //更新dish表基本信息
        this.updateById(setmealDto);

        //清理当前套餐对应菜品数据---setmeal_dish表的delete操作
        LambdaQueryWrapper<SetmealDish> qw = new LambdaQueryWrapper<>();
        qw.eq(SetmealDish::getSetmealId, setmealDto.getId());
        setmealDishService.remove(qw);

        //添加当前提交过来的菜品信息
        List<SetmealDish> dishes = setmealDto.getSetmealDishes();
        dishes = dishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(dishes);

    }
}
