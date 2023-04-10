package com.uzero.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uzero.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 千叶零
 * @version 1.0
 * create 2023-03-30  16:35:55
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
