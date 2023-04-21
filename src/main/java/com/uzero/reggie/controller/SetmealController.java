package com.uzero.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.uzero.reggie.common.R;
import com.uzero.reggie.dto.SetmealDto;
import com.uzero.reggie.entity.*;
import com.uzero.reggie.service.CategoryService;
import com.uzero.reggie.service.SetmealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 千叶零
 * @version 1.0
 * create 2023-04-04  09:55:16
 * 套餐管理
 */
@Slf4j
@RestController
@RequestMapping("/setmeal")
@Api(tags = "套餐相关接口")
public class SetmealController {

    //套餐
    @Autowired
    private SetmealService setmealService;

    //分类
    @Autowired
    private CategoryService categoryService;

    //redis
    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;


    /**
     * 添加套餐
     *
     * @param setmealDto 套餐
     * @return 返回添加结果
     */
    @PostMapping
    @CacheEvict(value = "setmealCache", allEntries = true) //删除缓存
    @ApiOperation(value = "新增套餐接口")
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        log.info("添加套餐：{}", setmealDto);
        setmealService.saveWithDish(setmealDto);
        return R.success("添加成功");
    }


    /**
     * 套餐分页查询
     *
     * @param page     当前页
     * @param pageSize 页数据数量
     * @param setmeal  套餐名
     * @return 返回分页数据
     */
    @GetMapping("/page")
    @ApiOperation(value = "套餐分页查询接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页记录数", required = true),
            @ApiImplicitParam(name = "setmeal", value = "套餐", required = false),
    })
    public R<Page<SetmealDto>> getByIdPage(@RequestParam(defaultValue = "1") Integer page,
                                           @RequestParam(defaultValue = "10") Integer pageSize,
                                           Setmeal setmeal) {
        String name = setmeal.getName();
        log.info("分页查询：当前页{}，数据数量{}，套餐名{}", page, pageSize, name);

        //构造分页
        Page<Setmeal> setmealPage = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>(page, pageSize);

        //如果name不会空，清除缓存
        if (StringUtils.isNotBlank(name)) {
            redisTemplate.delete("setmealCache");
            log.info("清除套餐缓存...");
        }

        //构造条件
        LambdaQueryWrapper<Setmeal> qw = new LambdaQueryWrapper<>();
        qw.like(StringUtils.isNotBlank(name), Setmeal::getName, name);
        qw.orderByDesc(Setmeal::getUpdateTime);

        //菜品分页查询
        setmealPage = setmealService.page(setmealPage, qw);

        //对象拷贝, 不拷贝records
        BeanUtils.copyProperties(setmealPage, setmealDtoPage, "records");
        List<Setmeal> records = setmealPage.getRecords();

        List<SetmealDto> setmealDtos = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            //将属性复制给dishDto
            BeanUtils.copyProperties(item, setmealDto);

            Long categoryId = item.getCategoryId();  //分类id
            //根据di查询分类信息
            Category category = categoryService.getById(categoryId);
            if (null != category) {
                //获得分类名
                String categoryName = category.getName();
                //设置分类名
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());

        setmealDtoPage = setmealDtoPage.setRecords(setmealDtos);

        return R.success(setmealDtoPage);
    }


    /**
     * 根据id批量删除
     *
     * @param ids id集合
     * @return 删除结果
     */
    @DeleteMapping
    @CacheEvict(value = "setmealCache", allEntries = true) //删除缓存
    @ApiOperation(value = "根据id批量删除接口")
    public R<String> deleteByIds(@RequestParam List<Long> ids) {
        log.info("根据id集合删除：{}", ids);
        setmealService.removeWithDish(ids);
        return R.success("删除成功");
    }


    /**
     * 根据id批量修改状态
     *
     * @param ids id集合
     * @return 修改结果
     */
    @PostMapping("/status/{status}")
    @CacheEvict(value = "setmealCache", allEntries = true) //删除缓存
    @ApiOperation(value = "根据id批量修改状态接口")
    public R<String> updateStatus(@PathVariable int status, String[] ids) {
        log.info("根据id集合修改：{}", (Object) ids);
        //遍历id集合取出id查询数据
        for (String id : ids) {
            Setmeal setmeal = setmealService.getById(id);
            //修改状态
            setmeal.setStatus(status);
            setmealService.updateById(setmeal);
        }
        return R.success("修改成功");
    }


    /**
     * 根据id查询套餐信息
     *
     * @param setmeal 菜品
     * @return DishDto
     */
    @GetMapping("/{id}")
    @Cacheable(value = "setmealCache", key = "#setmeal.categoryId + '_' + #setmeal.status") //查询缓存，有用无加
    @ApiOperation(value = "根据id查询套餐信息接口")
    public R<SetmealDto> getById(Setmeal setmeal) {
        Long id = setmeal.getId();
        log.info("根据id查询：{}", id);
        SetmealDto setmealDto = setmealService.getByIdWithFlavor(id);
        return R.success(setmealDto);
    }


    /**
     * 修改菜品
     *
     * @return 返回修改结果信息
     */
    @PutMapping
    @CacheEvict(value = "setmealCache", allEntries = true) //删除缓存
    @ApiOperation(value = "修改菜品接口")
    public R<String> update(@RequestBody SetmealDto setmealDto) {
        log.info("修改菜品：{}", setmealDto);
        setmealService.updateWithFlavor(setmealDto);
        return R.success("修改成功");
    }


    /**
     * 根据条件查询套餐
     *
     * @param setmeal 套餐
     * @return 套餐
     */
    @GetMapping("/list")
    @Cacheable(value = "setmealCache", key = "#setmeal.categoryId + '_' + #setmeal.status") //查询缓存，有用无加
    @ApiOperation(value = "根据条件查询套餐接口")
    public R<List<Setmeal>> getList(Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> qw = new LambdaQueryWrapper<>();
        qw.eq(null != setmeal.getCategoryId(), Setmeal::getCategoryId, setmeal.getCategoryId());
        qw.eq(null != setmeal.getStatus(), Setmeal::getStatus, setmeal.getStatus());
        qw.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(qw);
        return R.success(list);
    }
}
