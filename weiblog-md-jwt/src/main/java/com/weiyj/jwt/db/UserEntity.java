/**
 * 海到尽头天作岸 山登绝顶我为峰
 *
 * @Author Administrator
 * @CreateTime 2026-04-27 15:43
 * @Description
 **/
package com.weiyj.jwt.db;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String userName;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role; // e.g., ROLE_USER, ROLE_ADMIN

    public UserEntity(String userName, String password, String role) {
        this.userName = userName;
        this.password = password;
        this.role = role;
    }
}
