/**
 * 海到尽头天作岸 山登绝顶我为峰
 *
 * @Author Administrator
 * @CreateTime 2026-04-24 14:52
 * @Description
 **/
package com.weiyj.jwt.config;

import com.weiyj.jwt.filter.JwtAuthenticationFilter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Log4j2
@Configuration
@EnableWebSecurity
public class MultiSecurityConfig  {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;



    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration){
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    @Order(1)
    public SecurityFilterChain securityFilterChain(HttpSecurity http){
        http
            // 1. 开启跨域支持 (CORS)
            // 注意：需要在 WebMvcConfig 中也配置 CorsRegistry，或者在这里配置 corsConfigurationSource
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // 2. 禁用 CSRF (前后端分离 + JWT 不需要)
            .csrf(csrf -> csrf.disable())

            // 3. 基于 Token，所以不需要 Session (无状态化)
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // 4. 处理异常情况 (未登录/无权限)
            .exceptionHandling(exception -> exception
                    .authenticationEntryPoint(new CustomAuthenticationEntryPoint()) // 401: 未认证
                    .accessDeniedHandler(new CustomAccessDeniedHandler())           // 403: 无权限
            )

            // 5. 配置 URL 权限
            .authorizeHttpRequests(auth -> auth
                    // --- 放行列表 ---
                    // 允许跨域预检请求 (OPTIONS)
                    .requestMatchers(HttpMethod.OPTIONS).permitAll()
                    // 放行登录、注册接口
                    .requestMatchers("/api/auth/login", "/api/user/register").permitAll()
//                    // 放行 Swagger 文档 (如果有的话)
//                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
//                    // 放行静态资源
//                    .requestMatchers("/static/**", "/public/**").permitAll()

                    // --- 权限控制 ---
                    // 只有 ADMIN 角色能访问 /admin/**
                    .requestMatchers("/api/admin/**").authenticated()

                    // --- 兜底策略 ---
                    // 所有未匹配的路径都需放行
                    .anyRequest().permitAll()
            );

            // 6. 将 JWT 过滤器添加到 UsernamePasswordAuthenticationFilter 之前
            // 这样请求到达登录验证之前，会先检查 Token
            http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


}
