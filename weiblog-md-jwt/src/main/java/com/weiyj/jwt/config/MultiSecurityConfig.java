/**
 * 海到尽头天作岸 山登绝顶我为峰
 *
 * @Author Administrator
 * @CreateTime 2026-04-24 14:52
 * @Description
 **/
package com.weiyj.jwt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import java.util.logging.Handler;

@Configuration
@EnableWebSecurity
public class MultiSecurityConfig  {
    @Bean
    @Order(1)
    public SecurityFilterChain securityFilterChain(HttpSecurity http){



        return http.build();
    }


}
