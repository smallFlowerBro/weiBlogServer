/**
 * 海到尽头天作岸 山登绝顶我为峰
 *
 * @Author Administrator
 * @CreateTime 2026-04-27 17:26
 * @Description
 **/
package com.weiyj.jwt.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        // 1. 设置响应状态码为 401 (Unauthorized)
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // 2. 设置响应头，确保返回的是 JSON 格式
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // 3. 构建统一的响应体结构
        Map<String, Object> body = new HashMap<>();
        body.put("code", 401);
        body.put("message", "认证失败：请先登录或检查 Token 是否有效");
        body.put("path", request.getRequestURI());
        // 可选：记录异常详情到日志，但不建议直接返回给前端
        // log.warn("Unauthorized access attempt: {}", authException.getMessage());

        // 4. 将对象序列化为 JSON 并写入响应流
        PrintWriter writer = response.getWriter();
        writer.write(objectMapper.writeValueAsString(body));
        writer.flush();
    }
}
