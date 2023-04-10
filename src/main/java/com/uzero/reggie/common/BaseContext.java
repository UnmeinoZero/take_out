package com.uzero.reggie.common;

/**
 * @author 千叶零
 * @version 1.0
 * create 2023-03-31  16:30:26
 * 基于ThreadLocal封装工具类，用户保存和获取当前登录用户id
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long ids){
        threadLocal.set(ids);
    }

    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
