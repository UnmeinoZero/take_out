package com.uzero.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.uzero.reggie.common.BaseContext;
import com.uzero.reggie.common.R;
import com.uzero.reggie.entity.AddressBook;
import com.uzero.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 千叶零
 * @version 1.0
 * create 2023-04-06  11:45:44
 * 地址簿管理
 */
@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    /**
     * 新增
     * @return 返回地址簿对象
     */
    @PostMapping
    public R<AddressBook> save(@RequestBody AddressBook addressBook){
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("地址簿：{}", addressBook);
        addressBookService.save(addressBook);
        return R.success(null);
    }


    /**
     * 设置默认地址
     * @return  返回地址簿对象
     */
    @PutMapping("/default")
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook){
        log.info("地址簿：{}", addressBook);
        LambdaUpdateWrapper<AddressBook> qw = new LambdaUpdateWrapper<>();
        qw.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        //将所有的地址设置为0（非默认）
        qw.set(AddressBook::getIsDefault, 0);
        addressBookService.update(qw);

        //设置默认地址
        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);
        return R.success(addressBook);
    }


    /**
     *  根据id查询
     * @param id id
     * @return addressBook
     */
    @GetMapping("/{id}")
    public R<AddressBook> getById(@PathVariable Long id){
        log.info("根据id查询地址：{}", id);
        AddressBook addressBook = addressBookService.getById(id);
        if (addressBook != null){
            return R.success(addressBook);
        }
        return R.error("没有找到该对象");
    }


    /**
     * 获得默认地址
     * @return  地址簿
     */
    @GetMapping("/default")
    public R<AddressBook> getDefault(){
        log.info("查询默认地址");
        LambdaQueryWrapper<AddressBook> qw = new LambdaQueryWrapper<>();
        qw.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        qw.eq(AddressBook::getIsDefault, 1);
        AddressBook addressBook = addressBookService.getOne(qw);
        if (addressBook == null){
            return R.error("没有找到该对象");
        }
        return R.success(addressBook);
    }


    /**
     * 查询指定用户的全部地址
     * @return 地址簿对象
     */
    @GetMapping("/list")
    public R<List<AddressBook>> gatList(AddressBook addressBook){
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("地址簿：{}", addressBook);

        //构造条件
        LambdaQueryWrapper<AddressBook> qw = new LambdaQueryWrapper<>();
        qw.eq(null != addressBook.getUserId(), AddressBook::getUserId, addressBook.getUserId());
        //根据更新时间排序
        qw.orderByDesc(AddressBook::getUpdateTime);

        return R.success(addressBookService.list(qw));
    }
}
