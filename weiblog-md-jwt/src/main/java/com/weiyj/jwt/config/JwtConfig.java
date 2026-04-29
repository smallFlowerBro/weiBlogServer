/**
 * 海到尽头天作岸 山登绝顶我为峰
 *
 * @Author Administrator
 * @CreateTime 2026-04-28 14:11
 * @Description
 **/
package com.weiyj.jwt.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {
    //请求头名称
    private String header;
    //前缀
    private String prefix;
    //密钥
    private String secret;
    //有效器
    private Long expiration;
    // 是否开启刷新token功能
    private boolean refreshEnable;
}
