package com.uzero.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.uzero.reggie.common.CustomException;
import com.uzero.reggie.entity.Category;
import com.uzero.reggie.entity.Dish;
import com.uzero.reggie.entity.Setmeal;
import com.uzero.reggie.mapper.CategoryMapper;
import com.uzero.reggie.service.CategoryService;
import com.uzero.reggie.service.DishService;
import com.uzero.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 千叶零
 * @version 1.0
 * create 2023-03-30  16:41:31
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 根据id删除分类，删除之前需要进行判断
     * @param id id
     */
    @Override
    public boolean remove(Long id) {
        //查询当前分类是否关联了菜品，如果已经关联，抛出一个业务异常
        LambdaQueryWrapper<Dish> dqw = new LambdaQueryWrapper<>();
        //根据id查询
        dqw.eq(Dish::getCategoryId, id);
        long count = dishService.count(dqw);
        if (count > 0){
            //关联了菜品，抛出业务异常
            throw new CustomException("当前分类已关联菜品，删除失败");
        }

        //查询当前分类是否关联了套餐，如果已经关联，抛出一个业务异常
        LambdaQueryWrapper<Setmeal> sqw = new LambdaQueryWrapper<>();
        sqw.eq(Setmeal::getCategoryId, id);
        long count1 = setmealService.count(sqw);
        if (count > 0){
            //关联了套餐，抛出业务异常
            throw new CustomException("当前分类已关联套餐，删除失败");
        }

        //删除分类
        super.removeById(id);
        return true;
    }
}
