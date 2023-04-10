package com.uzero.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uzero.reggie.entity.Category;
import com.uzero.reggie.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 千叶零
 * @version 1.0
 * create 2023-04-02  16:31:54
 */
@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
