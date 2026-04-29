/**
 * 海到尽头天作岸 山登绝顶我为峰
 *
 * @Author Administrator
 * @CreateTime 2026-04-27 14:15
 * @Description
 **/
package com.weiyj.jwt.service;

import com.weiyj.jwt.db.UserEntity;
import com.weiyj.jwt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
public class CustomUserDetailService implements UserDetailsService {

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Autowired
    public UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUserName(username);
        if(user==null){
            throw  new UsernameNotFoundException("User not fund:"+username);
        }
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(user.getRole());

        Collection<? extends GrantedAuthority> authorityList = Collections.singletonList(simpleGrantedAuthority);

        return new User(user.getUserName(),user.getPassword(),authorityList);
    }
}
