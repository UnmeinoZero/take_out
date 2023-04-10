package com.uzero.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.uzero.reggie.common.R;
import com.uzero.reggie.entity.Category;
import com.uzero.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 千叶零
 * @version 1.0
 * create 2023-03-30  16:46:57
 */
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     *
     * @param category 分类信息
     * @return 返回结果信息
     */
    @PostMapping
    public R<String> save(@RequestBody Category category) {
        log.info("新增分类：{}", category);
        boolean b = categoryService.save(category);
        return b ? R.success("添加成功") : R.error("添加失败");
    }


    /**
     * 查询全部-分页
     *
     * @return 返回分页信息
     */
    @GetMapping("/page")
    public R<Page<Category>> getByIdPage(@RequestParam(defaultValue = "1") Integer page,
                                         @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("分页查询：当前页{}，数据数量{}", page, pageSize);
        //构造条件, 根据sort排序
        LambdaQueryWrapper<Category> qw = new LambdaQueryWrapper<>();
        qw.orderByAsc(Category::getSort);
        //构造分页
        Page<Category> pageInfo = new Page<>(page, pageSize);
        categoryService.page(pageInfo, qw);
        return R.success(pageInfo);
    }


    /**
     * 根据id删除分类
     *
     * @param ids id
     * @return 返回结果信息
     */
    @DeleteMapping
    public R<String> deleteById(Long ids) {
        log.info("根据id删除分类：{}", ids);
        //判断是否关联菜品
        boolean b = categoryService.remove(ids);
        return b ? R.success("删除成功") : R.error("删除失败");
    }


    /**
     * 根据id修改分类信息
     *
     * @param category 分类信息
     * @return 返回修改结果
     */
    @PutMapping
    public R<String> update(@RequestBody Category category) {
        log.info("修改员工信息：{}", category);
        Long id = Thread.currentThread().getId();
        log.info("当前线程id：{}", id);
        boolean b = categoryService.updateById(category);
        return b ? R.success("修改成功") : R.error("修改失败");
    }


    /**
     * 查询菜品分类
     *
     * @param category 分类
     * @return 返回菜品分类
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category) {
        log.info("根据条件查询分类：{}", category);
        LambdaQueryWrapper<Category> qw = new LambdaQueryWrapper<>();
        //添加条件
        qw.eq(category.getType() != null, Category::getType, category.getType());
        //排序
        qw.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(qw);
        return R.success(list);
    }

//
//    /**
//     * 根据id查询
//     * @param id 员工id
//     * @return 员工信息
//     */
//    @GetMapping("/{id}")
//    public R<Employee> getById(@PathVariable Long id){
//        log.info("根据id查询：{}", id);
//        Employee employee = employeeService.getById(id);
//        return null != employee ? R.success(employee) : R.error("查询失败");
//    }
}
