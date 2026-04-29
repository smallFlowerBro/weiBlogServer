/**
 * 海到尽头天作岸 山登绝顶我为峰
 *
 * @Author Administrator
 * @CreateTime 2026-04-29 11:08
 * @Description
 **/
package com.weiyj.jwt.service;

import com.weiyj.jwt.config.JwtTokenProvider;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
@Log4j2
public class AuthService {


    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public String auth(String userName,String password){
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                = new UsernamePasswordAuthenticationToken(userName,password);
        // 执行认证 如果认证失败会抛异常
        Authentication authenticate = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        //生成token
        String token = jwtTokenProvider.generateToken(authenticate);


        return token;

    }
}
