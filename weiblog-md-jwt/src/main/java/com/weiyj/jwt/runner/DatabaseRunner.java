/**
 * 海到尽头天作岸 山登绝顶我为峰
 *
 * @Author Administrator
 * @CreateTime 2026-04-29 12:22
 * @Description
 **/
package com.weiyj.jwt.runner;

import com.weiyj.jwt.db.UserEntity;
import com.weiyj.jwt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DatabaseRunner implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 创建一个初始用户
        if (userRepository.findByUserName("admin") == null) {
            UserEntity user = new UserEntity("admin", passwordEncoder.encode("password"), "ROLE_USER");
            userRepository.save(user);
        }
    }
}
