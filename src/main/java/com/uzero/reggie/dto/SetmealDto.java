package com.uzero.reggie.dto;

import com.uzero.reggie.entity.Setmeal;
import com.uzero.reggie.entity.SetmealDish;
import lombok.Data;

import java.util.List;

/**
 * @author 千叶零
 * @version 1.0
 * create 2023-04-04  10:39:48
 */
@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
