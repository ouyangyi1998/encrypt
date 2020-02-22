package com.supereal.bigfile;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan(basePackages = {"com.supereal.bigfile.repository"})
public class BigfileApplication {

    public static void main(String[] args) {
        SpringApplication.run(BigfileApplication.class, args);
    }

}

