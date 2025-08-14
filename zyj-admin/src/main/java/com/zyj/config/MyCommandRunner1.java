package com.zyj.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class MyCommandRunner1 implements CommandLineRunner {

    @Override
    public void run(String... args) {
        System.out.println("--------------- CommandLineRunner 执行 ---------------");
    }

}