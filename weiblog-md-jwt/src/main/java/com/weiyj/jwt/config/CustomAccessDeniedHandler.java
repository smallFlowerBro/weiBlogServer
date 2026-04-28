/**
 * 海到尽头天作岸 山登绝顶我为峰
 *
 * @Author Administrator
 * @CreateTime 2026-04-27 17:27
 * @Description
 **/
package com.weiyj.jwt.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
@Log4j2
public class CustomAccessDeniedHandler implements AccessDeniedHandler {


    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // 1. 设置响应状态码为 403
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        // 2. 设置响应内容类型为 JSON
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // 3. 构建统一的响应体
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("code", HttpStatus.FORBIDDEN.value());
        responseBody.put("msg", "权限不足，无法访问该资源");
        // 可选：记录详细的异常信息到日志，但不返回给前端
        log.warn("Access denied for user: {}, resource: {}", request.getUserPrincipal(), request.getRequestURI(), accessDeniedException);

        // 4. 将响应体对象序列化为 JSON 字符串并写入响应流
        String json = objectMapper.writeValueAsString(responseBody);
        response.getWriter().write(json);
    }
}
