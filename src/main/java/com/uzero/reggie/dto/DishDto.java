package com.uzero.reggie.dto;

import com.uzero.reggie.entity.Dish;
import com.uzero.reggie.entity.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 千叶零
 * @version 1.0
 * create 2023-04-03  14:30:16
 */
@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}

