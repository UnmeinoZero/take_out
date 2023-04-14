package com.uzero.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.uzero.reggie.common.R;
import com.uzero.reggie.dto.DishDto;
import com.uzero.reggie.entity.Category;
import com.uzero.reggie.entity.Dish;
import com.uzero.reggie.entity.DishFlavor;
import com.uzero.reggie.service.CategoryService;
import com.uzero.reggie.service.DishFlavorService;
import com.uzero.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author 千叶零
 * @version 1.0
 * create 2023-04-03  11:39:12
 * 菜品管理
 */
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    //菜品
    @Autowired
    private DishService dishService;

    //菜品口味
    @Autowired
    private DishFlavorService dishFlavorService;

    //菜品分类
    @Autowired
    private CategoryService categoryService;

    //redis
    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        log.info("新增菜品：{}", dishDto);
        boolean save = dishService.saveWithFlavor(dishDto);

        //精确清理某个分类下的菜品缓存数据
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);
        return save ? R.success("添加成功") : R.error("添加失败");
    }


    /**
     * 菜品分页查询
     *
     * @param page     当前页
     * @param pageSize 页数据数量
     * @param name     菜名
     * @return 返回分页数据
     */
    @GetMapping("/page")
    public R<Page<DishDto>> getByIdPage(@RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "10") Integer pageSize,
                                        String name) {
        log.info("分页查询：当前页{}，数据数量{}，菜名{}", page, pageSize, name);
        //构造条件
        LambdaQueryWrapper<Dish> qw = new LambdaQueryWrapper<>();
        qw.like(StringUtils.isNotBlank(name), Dish::getName, name);
        qw.orderByDesc(Dish::getUpdateTime);

        //构造分页
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>(page, pageSize);

        //菜品分页查询
        pageInfo = dishService.page(pageInfo, qw);

        //对象拷贝, 不拷贝records
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");
        List<Dish> records = pageInfo.getRecords();

        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            //将属性复制给dishDto
            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = item.getCategoryId();  //分类id
            //根据di查询分类信息
            Category category = categoryService.getById(categoryId);
            if (null != category) {
                //获得分类名
                String categoryName = category.getName();
                //设置分类名
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage = dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }


    /**
     * 根据id查询菜品信息和对应的口味信息
     *
     * @param id 菜品id
     * @return DishDto
     */
    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable Long id) {
        log.info("根据id查询：{}", id);
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }


    /**
     * 修改菜品
     *
     * @return 返回修改结果信息
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        log.info("修改菜品：{}", dishDto);
        dishService.updateWithFlavor(dishDto);

        //修改菜品后，清理redis中的缓存数据

        //清理所有缓存数据
        //Set keys = redisTemplate.keys("dish_");
        //redisTemplate.delete(keys);

        //精确清理某个分类下的菜品缓存数据
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);


        return R.success("修改成功");
    }


    /**
     * 根据id批量修改状态
     *
     * @param ids id集合
     * @return 修改结果
     */
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable int status, String[] ids) {
        log.info("根据id集合修改：{}", (Object) ids);
        //遍历id集合取出id查询数据
        for (String id : ids) {
            Dish dish = dishService.getById(id);
            //修改状态
            dish.setStatus(status);
            dishService.updateById(dish);
        }

        return R.success("修改成功");
    }

    /**
     * 根据id批量删除
     *
     * @param ids id集合
     * @return 删除结果
     */
    @DeleteMapping
    public R<String> deleteById(String[] ids) {
        log.info("根据id集合删除：{}", (Object) ids);
        for (String id : ids) {
            dishService.removeById(id);
        }
        return R.success("删除成功");
    }


    /**
     * 根据条件查询菜品
     *
     * @return 返回菜品集合
     */
//    @GetMapping("/list")
//    public R<List<Dish>> getByCategory(Dish dish) {
//        log.info("根据条件查询菜品：{}", dish);
//        LambdaQueryWrapper<Dish> qw = new LambdaQueryWrapper<>();
//        //构造条件
//        qw.eq(Dish::getStatus, 1);
//        qw.eq(null != dish.getCategoryId(), Dish::getCategoryId, dish.getCategoryId());
//        //添加排序(根据设置的排序或者更新时间排序)
//        qw.orderByDesc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//        List<Dish> list = dishService.list(qw);
//        return R.success(list);
//    }
    @GetMapping("/list")
    public R<List<DishDto>> getByCategory(Dish dish) {
        List<DishDto> dishDtoList = null;
        log.info("根据条件查询菜品：{}", dish);

        //构造redis key
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();

        //从redis中获取数据
        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);

        //如果存在，无需返回直接返回
        if (null != dishDtoList){
            log.info("使用菜品缓存...");
            return R.success(dishDtoList);
        }

        //不存在是，需要查询数据库，将查询到的菜品数据缓存到redis中

        LambdaQueryWrapper<Dish> qw = new LambdaQueryWrapper<>();
        //构造条件
        qw.eq(Dish::getStatus, 1);
        qw.eq(null != dish.getCategoryId(), Dish::getCategoryId, dish.getCategoryId());
        //添加排序(根据设置的排序或者更新时间排序)
        qw.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(qw);

        dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            //将属性复制给dishDto
            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = item.getCategoryId();  //分类id
            //根据di查询分类信息
            Category category = categoryService.getById(categoryId);
            if (null != category) {
                //获得分类名
                String categoryName = category.getName();
                //设置分类名
                dishDto.setCategoryName(categoryName);
            }

            //当前菜品id
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> qw2 = new LambdaQueryWrapper<>();
            qw2.eq(DishFlavor::getDishId, dishId);
            //查询当前菜品口味数据
            List<DishFlavor> dishFlavorList = dishFlavorService.list(qw2);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());

        //将菜品数据缓存到redis，时效60分钟
        redisTemplate.opsForValue().set(key, dishDtoList, 60, TimeUnit.MINUTES);

        return R.success(dishDtoList);
    }
}
