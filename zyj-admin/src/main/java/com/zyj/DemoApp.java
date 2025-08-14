package com.zyj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApp {

    public static void main(String[] args) {
        SpringApplication.run(DemoApp.class, args);
        String startLog = """
                +==================================================================================+
                +                                   项目启动成功                                     +
                +  作者： ZhaoYiJun                                                                 +
                +                                                                                  +
                +=================================================================================="
                """;
        System.out.println(startLog);
    }

}
