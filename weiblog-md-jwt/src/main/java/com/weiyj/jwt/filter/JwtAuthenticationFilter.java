/**
 * 海到尽头天作岸 山登绝顶我为峰
 *
 * @Author Administrator
 * @CreateTime 2026-04-27 17:28
 * @Description
 **/
package com.weiyj.jwt.filter;

import com.weiyj.jwt.config.JwtConfig;
import com.weiyj.jwt.config.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@Log4j2
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider jwtTokenProvider; // 负责解析和验证 Token 的工具类

    @Autowired
    private UserDetailsService userDetailsService; // 负责从数据库加载用户信息

    @Autowired
    private JwtConfig jwtProperties; // 读取 yml 配置（如 Header 名称）

    public final List<String> EXCLUDE_PATH =  Arrays.asList("/api/auth/login");



    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        if(EXCLUDE_PATH.stream().anyMatch(p->new AntPathMatcher().match(p,path))){
            filterChain.doFilter(request,response);
            return;
        }

        try {

            // 1. 从请求头中获取 JWT Token
            String jwt = getJwtFromRequest(request);

            // 2. 验证 Token 是否有效 且 当前上下文没有认证信息
            // (如果 SecurityContext 已有认证，说明已经登录过，无需重复验证)
            if (StringUtils.hasText(jwt)
                    && jwtTokenProvider.validateToken(jwt)
                    && SecurityContextHolder.getContext().getAuthentication() == null) {

                // 3. 从 Token 中解析用户名
                String username = jwtTokenProvider.getUsernameFromToken(jwt);

                // 4. 根据用户名加载用户详情（包含权限信息）
                // 注意：这里会查询数据库，如果为了性能，可以在 Token 中缓存权限信息
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // 5. 创建认证令牌
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                // 6. 设置请求详情（可选，用于记录 IP 等信息）
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 7. 将认证信息存入 SecurityContext
                // 这样在后续的 Controller 中，你就可以通过 SecurityContextHolder 获取当前用户了
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            // 记录日志，但不要抛出异常中断请求
            // 异常会被 AuthenticationEntryPoint 捕获并返回 401
            logger.error("无法设置用户认证信息", ex);
        }

        filterChain.doFilter(request,response);
    }


    /**
     * 从请求头中提取 Token
     * 格式通常为：Authorization: Bearer <token>
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(jwtProperties.getHeader());

        // 检查 Token 是否存在且以 "Bearer " 开头
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(jwtProperties.getPrefix())) {
            // 截取 "Bearer " 后面的部分
            return bearerToken.substring(jwtProperties.getPrefix().length());
        }
        return null;
    }
}
