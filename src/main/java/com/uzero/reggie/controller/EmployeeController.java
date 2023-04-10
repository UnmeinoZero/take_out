package com.uzero.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.uzero.reggie.common.R;
import com.uzero.reggie.entity.Employee;
import com.uzero.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * @author 千叶零
 * @version 1.0
 * create 2023-03-30  16:46:57
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;


    /**
     * 登录
     *
     * @param request 请求
     * @param employee 员工信息
     * @return 返回员工信息
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {

        //将页面提交的密码password进行md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //根据用户名查询数据库
        LambdaQueryWrapper<Employee> qw = new LambdaQueryWrapper<>();
        qw.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(qw);

        //判断查询结果
        if (null == emp || !password.equals(emp.getPassword())) {
            return R.error("用户名或密码错误");
        }

        //查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if (emp.getStatus() == 0) {
            return R.error("账户已禁用");
        }

        //登录成功,将id储存到session
        request.getSession().setAttribute("employee", emp.getId());

        return R.success(emp);
    }


    /**
     * 员工账户退出
     *
     * @param request 请求
     * @return 返回退出信息
     */
    @PostMapping("/logout")
    public R<String> loginOut(HttpServletRequest request) {
        //清理session中的id信息
        request.getSession().removeAttribute("employee");
        return R.success("账户已退出");
    }

    /**
     * 新增员工方法
     *
     * @param employee 员工信息
     * @return 返回结果信息
     */
    @PostMapping
    public R<String> save(@RequestBody Employee employee) {
        log.info("新增员工：{}", employee);
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        boolean b = employeeService.save(employee);
        return b ? R.success("添加成功") : R.error("添加失败");
    }


    /**
     * 查询全部-分页
     *
     * @return  返回分页信息
     */
    @GetMapping("/page")
    public R<Page<Employee>> getByIdPage(@RequestParam(defaultValue = "1") Integer page,
                                   @RequestParam(defaultValue = "10") Integer pageSize,
                                   String name) {
        log.info("分页查询：当前页{}，数据数量{}，姓名{}", page, pageSize, name);
        //构造条件
        LambdaQueryWrapper<Employee> qw = new LambdaQueryWrapper<>();
        qw.like( StringUtils.isNotBlank(name), Employee::getName, name);
        qw.orderByDesc(Employee::getUpdateTime);

        //构造分页
        Page<Employee> pageInfo = new Page<>(page, pageSize);

        pageInfo = employeeService.page(pageInfo, qw);

        return R.success(pageInfo);
    }


    /**
     * 根据id修改员工信息
     * @param employee 员工信息
     * @return  返回修改结果
     */
    @PutMapping
    public R<String> update(@RequestBody Employee employee){
        log.info("修改员工信息：{}", employee);
        Long id = Thread.currentThread().getId();
        log.info("当前线程id：{}", id);
        boolean b = employeeService.updateById(employee);
        return b ? R.success("修改成功") : R.error("修改失败");
    }

    /**
     * 根据id查询
     * @param id 员工id
     * @return 员工信息
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据id查询：{}", id);
        Employee employee = employeeService.getById(id);
        return null != employee ? R.success(employee) : R.error("查询失败");
    }
}
