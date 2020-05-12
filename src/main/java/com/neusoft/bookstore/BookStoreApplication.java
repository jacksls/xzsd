package com.neusoft.bookstore;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Date;


@SpringBootApplication
@EnableSwagger2
@EnableTransactionManagement
@MapperScan("com.neusoft.bookstore.*.mapper")
public class BookStoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookStoreApplication.class, args);
        System.out.println(new Date() +"   Run success！");
    }

}