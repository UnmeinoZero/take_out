package com.uzero.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.uzero.reggie.dto.DishDto;
import com.uzero.reggie.entity.Dish;
import com.uzero.reggie.entity.DishFlavor;
import com.uzero.reggie.entity.SetmealDish;
import com.uzero.reggie.mapper.DishMapper;
import com.uzero.reggie.service.DishFlavorService;
import com.uzero.reggie.service.DishService;
import com.uzero.reggie.service.SetmealDishService;
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
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增菜品，同时保存对应的口味数据
     *
     * @param dishDto 菜品信息
     * @return
     */
    @Override
    @Transactional
    public boolean saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到菜品表
        this.save(dishDto);

        //菜品id
        Long dishId = dishDto.getId();
        //菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        //保存菜品口味到菜品口味表dish_flavor
        dishFlavorService.saveBatch(flavors);
        return true;
    }


    /**
     * 根据id查询菜品信息和口味信息
     * @param id  菜品id
     * @return DishDto
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品信息
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);

        //查询口味信息
        LambdaQueryWrapper<DishFlavor> qw = new LambdaQueryWrapper<>();
        qw.eq(id != null, DishFlavor::getDishId, id);
        List<DishFlavor> flavors = dishFlavorService.list(qw);
        dishDto.setFlavors(flavors);
        return dishDto;
    }


    /**
     * 修改菜品和口味信息
     * @param dishDto  菜品和口味信息
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表基本信息
        this.updateById(dishDto);

        //清理当前菜品对应口味数据---dish_flavor表的delete操作
        LambdaQueryWrapper<DishFlavor> qw = new LambdaQueryWrapper<>();
        qw.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(qw);

        //添加当前提交过来的口味数据
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }


    /**
     * 根据id删除菜品，同时关联口味表和套餐表
     * @param ids
     */
    public boolean deleteWithFlavorAndSetmeal(String[] ids){
        //查询要删除的菜品中是否归属于某个套餐中
        LambdaQueryWrapper<SetmealDish> qw = new LambdaQueryWrapper<>();
        qw.in(SetmealDish::getDishId, ids);
        long count = setmealDishService.count(qw);
        //如果有菜品以归属于某个套餐中，则删除失败
        if (count > 0){
            return false;
        }

        //执行删除
        //删除口味表
        LambdaQueryWrapper<DishFlavor> qw2 = new LambdaQueryWrapper<>();
        qw2.in(DishFlavor::getDishId, ids);
        dishFlavorService.remove(qw2);

        //删除菜品
        LambdaQueryWrapper<Dish> qw3 = new LambdaQueryWrapper<>();
        qw3.in(Dish::getId,  ids);
        this.remove(qw3);
        return true;
    }
}
