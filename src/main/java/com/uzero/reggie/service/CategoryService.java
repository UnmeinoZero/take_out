package com.uzero.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.uzero.reggie.entity.Category;
import com.uzero.reggie.entity.Employee;

/**
 * @author 千叶零
 * @version 1.0
 * create 2023-03-30  16:40:50
 */
public interface CategoryService extends IService<Category> {

    boolean remove(Long id);
}
