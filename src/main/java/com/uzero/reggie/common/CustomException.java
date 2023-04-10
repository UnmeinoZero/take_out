package com.uzero.reggie.common;

/**
 * @author 千叶零
 * @version 1.0
 * create 2023-04-02  18:23:01
 * 自定义业务异常
 */
public class CustomException extends RuntimeException{
    public CustomException(String msg){
        super(msg);
    }
}
