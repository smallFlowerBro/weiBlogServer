package com.weiyj.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.weiyj.**")
public class WeiblogMdWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeiblogMdWebApplication.class, args);
    }

}
