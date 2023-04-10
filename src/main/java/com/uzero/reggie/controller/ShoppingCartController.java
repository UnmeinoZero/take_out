package com.uzero.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.uzero.reggie.common.BaseContext;
import com.uzero.reggie.common.R;
import com.uzero.reggie.entity.ShoppingCart;
import com.uzero.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 千叶零
 * @version 1.0
 * create 2023-04-06  15:04:12
 */
@Slf4j
@RestController
@RequestMapping("shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     *
     * @param shoppingCart 购物车
     * @return 返回购物车对象
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        log.info("添加购物车：{}", shoppingCart);

        //设置用户id，指定当前是哪个用户的购物车数据
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);

        //查询当前菜品或套餐是否在购物车
        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> qw = new LambdaQueryWrapper<>();
        qw.eq(ShoppingCart::getUserId, currentId);

        if (null != dishId) {
            //添加到购物车的是菜品
            qw.eq(ShoppingCart::getDishId, dishId);
        } else {
            //添加到购物车的是套餐
            qw.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }

        ShoppingCart cartServiceOne = shoppingCartService.getOne(qw);

        if (null != cartServiceOne) {
            //如果已存在，就在原来的数量上加一
            Integer number = cartServiceOne.getNumber();
            cartServiceOne.setNumber(number + 1);
            shoppingCartService.updateById(cartServiceOne);
        } else {
            //如果不存在，则添加到购物车，数量默认是一
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            cartServiceOne = shoppingCart;
        }
        return R.success(cartServiceOne);
    }


    /**
     * 减少购物车数量
     *
     * @param shoppingCart 购物车对象
     * @return 购物车对象
     */
    @PostMapping("sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart) {
        log.info("减购物车：{}", shoppingCart);

        //设置用户id，指定当前是哪个用户的购物车数据
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);

        //查询当前菜品或套餐是否在购物车
        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> qw = new LambdaQueryWrapper<>();
        qw.eq(ShoppingCart::getUserId, currentId);

        if (null != dishId) {
            //添加到购物车的是菜品
            qw.eq(ShoppingCart::getDishId, dishId);
        } else {
            //添加到购物车的是套餐
            qw.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }

        ShoppingCart cartServiceOne = shoppingCartService.getOne(qw);

        if (null != cartServiceOne) {
            //如果已存在，就在原来的数量上减一
            Integer number = cartServiceOne.getNumber();
            number--;
            if (number < 1) {
                //数量小于1时，删除购物车中的该菜品或套餐
                cartServiceOne.setNumber(0);
                shoppingCartService.removeById(cartServiceOne);
            } else {
                //更新数量
                cartServiceOne.setNumber(number);
                shoppingCartService.updateById(cartServiceOne);
            }
        } else {
            //如果不存在，数量设置为0
            shoppingCart.setNumber(0);
            cartServiceOne = shoppingCart;
        }
        return R.success(cartServiceOne);
    }


    /**
     * 查询当前用户购物车信息
     *
     * @return 返回购物车集合
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        log.info("查看购物车...");
        LambdaQueryWrapper<ShoppingCart> qw = new LambdaQueryWrapper<>();
        qw.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        qw.orderByDesc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(qw);
        return R.success(list);
    }


    /**
     * 清空购物车
     *
     * @return 返回结果信息
     */
    @DeleteMapping("/clean")
    public R<String> clean() {
        log.info("清空购物车...");
        //根据用户id删除对应的购物车信息
        LambdaQueryWrapper<ShoppingCart> qw = new LambdaQueryWrapper<>();
        qw.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        shoppingCartService.remove(qw);
        return R.success("清空购物车成功");
    }
}
