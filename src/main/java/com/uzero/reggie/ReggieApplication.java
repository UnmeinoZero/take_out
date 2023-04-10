package com.uzero.reggie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author 千叶零
 * @version 1.0
 * create 2023-03-30  15:53:32
 */
@Slf4j
@SpringBootApplication
@EnableTransactionManagement  //开启事务管理
public class ReggieApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReggieApplication.class);
        log.info("项目启动成功...");
    }
}
