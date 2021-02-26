package com.abners.nettyrpc.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 类Boot.java的实现描述：TODO 类实现描述
 *
 * @author baoxing.peng 2021年02月25日 14:17:21
 */
@SpringBootApplication
@ComponentScan("com.abners")
public class Boot {

    public static void main(String[] args) {
        SpringApplication.run(Boot.class);
    }
}
